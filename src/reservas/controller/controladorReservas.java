package reservas.controller;

import reservas.almacenDatos;
import reservas.API.clienteGemini;
import reservas.persistenciaXml;
import reservas.modelo.categoria;
import reservas.modelo.estadoReserva;
import reservas.modelo.funcionario;
import reservas.modelo.recurso;
import reservas.modelo.reserva;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class controladorReservas {

    public static class resultadoReserva {
        public final reserva reservaCreada;
        public final List<categoria> categoriasSinDisponibilidad;

        public resultadoReserva(reserva reservaCreada, List<categoria> categoriasSinDisponibilidad) {
            this.reservaCreada = reservaCreada;
            this.categoriasSinDisponibilidad = categoriasSinDisponibilidad;
        }
    }

    public static class resultadoInterpretacionIA {
        public final String actividad;
        public final LocalDate fecha;
        public final LocalTime horaInicio;
        public final LocalTime horaFin;
        public final List<categoria> categoriasReconocidas;
        public final List<String> categoriasNoReconocidas;

        public resultadoInterpretacionIA(String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                         List<categoria> categoriasReconocidas, List<String> categoriasNoReconocidas) {
            this.actividad = actividad;
            this.fecha = fecha;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
            this.categoriasReconocidas = categoriasReconocidas;
            this.categoriasNoReconocidas = categoriasNoReconocidas;
        }
    }

    public List<categoria> listarCategorias() {
        return almacenDatos.categorias;
    }

    public List<reserva> listarReservasDe(funcionario funcionario) {
        return almacenDatos.reservasDe(funcionario);
    }

    public resultadoReserva crearReserva(funcionario funcionario, String actividad, LocalDate fecha,
                                         LocalTime horaInicio, LocalTime horaFin,
                                         List<categoria> categoriasSeleccionadas) {
        List<recurso> recursosAsignados = new ArrayList<>();
        List<categoria> categoriasSinDisponibilidad = new ArrayList<>();
        for (categoria categoria : categoriasSeleccionadas) {
            recurso recurso = almacenDatos.buscarRecursoDisponible(categoria, fecha, horaInicio, horaFin, null);
            if (recurso != null) {
                recursosAsignados.add(recurso);
            } else {
                categoriasSinDisponibilidad.add(categoria);
            }
        }

        if (!categoriasSinDisponibilidad.isEmpty()) {
            return new resultadoReserva(null, categoriasSinDisponibilidad);
        }

        reserva nueva = new reserva(almacenDatos.generarIdReserva(), actividad, fecha,
                horaInicio, horaFin, estadoReserva.ACTIVA, funcionario, recursosAsignados);
        almacenDatos.reservas.add(nueva);
        persistenciaXml.guardar();
        return new resultadoReserva(nueva, categoriasSinDisponibilidad);
    }

    public resultadoInterpretacionIA interpretarConIA(String frase) {
        clienteGemini.resultadoInterpretacionIA crudo;
        try {
            crudo = clienteGemini.interpretar(frase, almacenDatos.categorias);
        } catch (IllegalStateException excepcionEstado) {
            throw excepcionEstado;
        } catch (IOException | InterruptedException excepcionRed) {
            throw new IllegalStateException("No se pudo conectar con el servicio de IA: "
                    + excepcionRed.getMessage());
        }

        List<categoria> reconocidas = new ArrayList<>();
        List<String> noReconocidas = new ArrayList<>();
        for (String nombreCategoria : crudo.categoriasSugeridas) {
            categoria encontrada = null;
            for (categoria categoria : almacenDatos.categorias) {
                if (categoria.getDescripcion().equalsIgnoreCase(nombreCategoria)) {
                    encontrada = categoria;
                    break;
                }
            }
            if (encontrada != null) {
                reconocidas.add(encontrada);
            } else {
                noReconocidas.add(nombreCategoria);
            }
        }

        return new resultadoInterpretacionIA(crudo.actividad, crudo.fecha, crudo.horaInicio, crudo.horaFin,
                reconocidas, noReconocidas);
    }

    public void cancelarReserva(reserva reserva) {
        if (!reserva.estaActiva()) {
            throw new IllegalStateException("Esa reserva ya esta cancelada.");
        }
        if (!reserva.esFutura()) {
            throw new IllegalStateException("Solo se pueden cancelar reservas futuras.");
        }
        reserva.cancelar();
        persistenciaXml.guardar();
    }
}