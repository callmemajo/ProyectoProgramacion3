package reservas.controller;

import reservas.almacenDatos;
import reservas.modelo.categoria;
import reservas.modelo.recurso;
import reservas.modelo.reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class controladorCalendarizacion {

    public List<categoria> listarCategorias() {
        return almacenDatos.categorias;
    }

    public List<recurso> recursosDeCategoria(categoria categoria) {
        List<recurso> resultado = new ArrayList<>();
        for (recurso r : almacenDatos.recursos) {
            if (r.getCategoria() == categoria) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    public String textoCelda(recurso recurso, LocalDate fecha, LocalTime hora) {
        for (reserva r : almacenDatos.reservas) {
            if (!r.estaActiva()) {
                continue;
            }
            if (!r.getFecha().equals(fecha)) {
                continue;
            }
            if (!r.getRecursosAsignados().contains(recurso)) {
                continue;
            }
            boolean dentroDelRango = !hora.isBefore(r.getHoraInicio()) && hora.isBefore(r.getHoraFin());
            if (dentroDelRango) {
                return r.getActividad() + " - " + r.getFuncionario().getNombreMostrar();
            }
        }
        return "";
    }
}