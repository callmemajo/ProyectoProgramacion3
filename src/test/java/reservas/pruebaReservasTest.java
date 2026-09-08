package reservas;

import reservas.modelo.categoria;
import reservas.modelo.estadoReserva;
import reservas.modelo.funcionario;
import reservas.modelo.recurso;
import reservas.modelo.reserva;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class pruebaReservasTest {

    @Test
    @Order(1)
    void crearReservaConDisponibilidadFunciona() {
        funcionario juan = (funcionario) almacenDatos.buscarUsuario("111");
        int cantidadAntes = almacenDatos.reservasDe(juan).size();

        categoria catSalaJuntas = almacenDatos.categorias.get(2);
        LocalDate fecha = LocalDate.now().plusDays(10);
        LocalTime inicio = LocalTime.of(14, 0);
        LocalTime fin = LocalTime.of(15, 0);

        recurso recurso = almacenDatos.buscarRecursoDisponible(catSalaJuntas, fecha, inicio, fin, null);
        assertNotNull(recurso, "hay un recurso disponible en Sala de Juntas para una fecha libre");

        reserva nueva = new reserva(almacenDatos.generarIdReserva(), "Prueba automatica", fecha, inicio, fin,
                estadoReserva.ACTIVA, juan, List.of(recurso));
        almacenDatos.reservas.add(nueva);

        int cantidadDespues = almacenDatos.reservasDe(juan).size();
        assertEquals(cantidadAntes + 1, cantidadDespues, "la reserva nueva aparece en 'mis reservas' de Juan");

        recurso mismoRecurso = almacenDatos.buscarRecursoDisponible(catSalaJuntas, fecha, inicio, fin, null);
        assertNull(mismoRecurso, "el recurso recien reservado ya no aparece disponible en el mismo horario");
    }

    @Test
    @Order(2)
    void reservaSinDisponibilidadNoRevienta() {
        categoria catSalaJuntas = almacenDatos.categorias.get(2);
        LocalDate fecha = LocalDate.now().plusDays(10);

        recurso recursoMismaHora = almacenDatos.buscarRecursoDisponible(catSalaJuntas, fecha,
                LocalTime.of(14, 0), LocalTime.of(15, 0), null);
        assertNull(recursoMismaHora, "categoria sin recursos libres en ese horario devuelve null (no revienta)");

        recurso recursoOtraHora = almacenDatos.buscarRecursoDisponible(catSalaJuntas, fecha,
                LocalTime.of(9, 0), LocalTime.of(10, 0), null);
        assertNotNull(recursoOtraHora, "la misma categoria si esta libre en un horario que no se traslapa");
    }

    @Test
    @Order(3)
    void cancelarReservaFuturaLiberaElRecurso() {
        funcionario juan = (funcionario) almacenDatos.buscarUsuario("111");
        categoria catSalaJuntas = almacenDatos.categorias.get(2);
        LocalDate fecha = LocalDate.now().plusDays(20);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        recurso recurso = almacenDatos.buscarRecursoDisponible(catSalaJuntas, fecha, inicio, fin, null);
        reserva reserva = new reserva(almacenDatos.generarIdReserva(), "Reserva a cancelar", fecha, inicio, fin,
                estadoReserva.ACTIVA, juan, List.of(recurso));
        almacenDatos.reservas.add(reserva);

        assertTrue(reserva.esFutura(), "la reserva futura se puede cancelar (esFutura)");
        reserva.cancelar();
        assertEquals(estadoReserva.CANCELADA, reserva.getEstado(), "despues de cancelar, el estado es CANCELADA");

        recurso recursoLibreOtraVez = almacenDatos.buscarRecursoDisponible(catSalaJuntas, fecha, inicio, fin, null);
        assertNotNull(recursoLibreOtraVez, "al cancelar, el recurso se libera para ese mismo horario");
    }

    @Test
    @Order(4)
    void reservaConFechaPasadaNoEsFutura() {
        funcionario juan = (funcionario) almacenDatos.buscarUsuario("111");
        reserva reservaPasada = new reserva("RES-TEST-PASADA", "Reunion vieja",
                LocalDate.now().minusDays(5), LocalTime.of(9, 0), LocalTime.of(10, 0),
                estadoReserva.ACTIVA, juan, List.of());
        assertFalse(reservaPasada.esFutura(), "una reserva con fecha pasada ya no se considera 'futura'");
    }
}