package reservas;

import reservas.modelo.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class pruebaLogica {

    private static int pasaron = 0;
    private static int fallaron = 0;

    public static void main(String[] args) {
        probarLogin();
        probarCambiarClave();
        probarReservaExitosa();
        probarReservaSinDisponibilidad();
        probarCancelarReserva();
        probarNoCancelarReservaPasada();

        System.out.println();
        System.out.println("Resultado: " + pasaron + " pasaron, " + fallaron + " fallaron.");
        if (fallaron > 0) {
            System.exit(1);
        }
    }

    private static void probarLogin() {
        usuario admin = almacenDatos.buscarUsuario("admin");
        verificar("login admin/admin autentica", admin != null && admin.autenticar("admin"));
        verificar("login admin con clave incorrecta falla", admin != null && !admin.autenticar("otraClave"));

        usuario juan = almacenDatos.buscarUsuario("111");
        verificar("login 111/111 (Juan Perez) autentica", juan != null && juan.autenticar("111"));
        verificar("usuario inexistente no aparece", almacenDatos.buscarUsuario("999") == null);
    }

    private static void probarCambiarClave() {
        usuario maria = almacenDatos.buscarUsuario("222");
        try {
            maria.cambiarClave("222", "nuevaClave123");
            verificar("cambiar clave con clave actual correcta funciona", maria.autenticar("nuevaClave123"));
        } catch (IllegalArgumentException ex) {
            verificar("cambiar clave con clave actual correcta funciona", false);
        }

        boolean lanzoExcepcionPorClaveActualMala = false;
        try {
            maria.cambiarClave("claveIncorrecta", "otraClaveNueva");
        } catch (IllegalArgumentException ex) {
            lanzoExcepcionPorClaveActualMala = true;
        }
        verificar("cambiar clave con clave actual incorrecta se rechaza", lanzoExcepcionPorClaveActualMala);

        boolean lanzoExcepcionPorClaveCorta = false;
        try {
            maria.cambiarClave("nuevaClave123", "abc");
        } catch (IllegalArgumentException ex) {
            lanzoExcepcionPorClaveCorta = true;
        }
        verificar("cambiar clave con clave nueva muy corta se rechaza", lanzoExcepcionPorClaveCorta);

        maria.cambiarClave("nuevaClave123", "clave222");
    }

    private static void probarReservaExitosa() {
        funcionario juan = (funcionario) almacenDatos.buscarUsuario("111");
        int cantidadAntes = almacenDatos.reservasDe(juan).size();

        categoria catSalaJuntas = almacenDatos.categorias.get(2);
        LocalDate fecha = LocalDate.now().plusDays(10);
        LocalTime inicio = LocalTime.of(14, 0);
        LocalTime fin = LocalTime.of(15, 0);

        recurso recurso = almacenDatos.buscarRecursoDisponible(catSalaJuntas, fecha, inicio, fin, null);
        verificar("hay un recurso disponible en Sala de Juntas para una fecha libre", recurso != null);

        reserva nueva = new reserva(almacenDatos.generarIdReserva(), "Prueba automatica", fecha, inicio, fin,
                estadoReserva.ACTIVA, juan, List.of(recurso));
        almacenDatos.reservas.add(nueva);

        int cantidadDespues = almacenDatos.reservasDe(juan).size();
        verificar("la reserva nueva aparece en 'mis reservas' de Juan", cantidadDespues == cantidadAntes + 1);

        recurso mismoRecurso = almacenDatos.buscarRecursoDisponible(catSalaJuntas, fecha, inicio, fin, null);
        verificar("el recurso recien reservado ya no aparece disponible en el mismo horario", mismoRecurso == null);
    }

    private static void probarReservaSinDisponibilidad() {
        categoria catSalaJuntas = almacenDatos.categorias.get(2);
        LocalDate fecha = LocalDate.now().plusDays(10);
        recurso recurso = almacenDatos.buscarRecursoDisponible(catSalaJuntas, fecha, LocalTime.of(14, 0), LocalTime.of(15, 0), null);
        verificar("categoria sin recursos libres devuelve null (no revienta)", recurso == null);

        recurso recursoOtraHora = almacenDatos.buscarRecursoDisponible(catSalaJuntas, fecha, LocalTime.of(9, 0), LocalTime.of(10, 0), null);
        verificar("la misma categoria si esta libre en un horario que no se traslapa", recursoOtraHora != null);
    }

    private static void probarCancelarReserva() {
        funcionario juan = (funcionario) almacenDatos.buscarUsuario("111");
        categoria catSalaJuntas = almacenDatos.categorias.get(2);
        LocalDate fecha = LocalDate.now().plusDays(20);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        recurso recurso = almacenDatos.buscarRecursoDisponible(catSalaJuntas, fecha, inicio, fin, null);
        reserva reserva = new reserva(almacenDatos.generarIdReserva(), "Reserva a cancelar", fecha, inicio, fin,
                estadoReserva.ACTIVA, juan, List.of(recurso));
        almacenDatos.reservas.add(reserva);

        verificar("la reserva futura se puede cancelar (esFutura)", reserva.esFutura());
        reserva.cancelar();
        verificar("despues de cancelar, el estado es CANCELADA", reserva.getEstado() == estadoReserva.CANCELADA);

        recurso recursoLibreOtraVez = almacenDatos.buscarRecursoDisponible(catSalaJuntas, fecha, inicio, fin, null);
        verificar("al cancelar, el recurso se libera para ese mismo horario", recursoLibreOtraVez != null);
    }

    private static void probarNoCancelarReservaPasada() {
        funcionario juan = (funcionario) almacenDatos.buscarUsuario("111");
        reserva reservaPasada = new reserva("RES-TEST-PASADA", "Reunion vieja",
                LocalDate.now().minusDays(5), LocalTime.of(9, 0), LocalTime.of(10, 0),
                estadoReserva.ACTIVA, juan, List.of());
        verificar("una reserva con fecha pasada ya no se considera 'futura'", !reservaPasada.esFutura());
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            System.out.println("PASA  - " + descripcion);
            pasaron++;
        } else {
            System.out.println("FALLA - " + descripcion);
            fallaron++;
        }
    }
}