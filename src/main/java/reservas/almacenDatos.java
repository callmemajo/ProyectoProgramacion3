package reservas;

import reservas.modelo.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class almacenDatos {

    public static final List<usuario> usuarios = new ArrayList<>();
    public static final List<categoria> categorias = new ArrayList<>();
    public static final List<recurso> recursos = new ArrayList<>();
    public static final List<reserva> reservas = new ArrayList<>();

    private static int siguienteNumeroReserva = 6;

    private almacenDatos() {
    }

    static {
        cargarDatosDeEjemplo();
    }

    private static void cargarDatosDeEjemplo() {
        administrador admin = new administrador("admin", "admin");
        funcionario juan = new funcionario("111", "111", "Juan Perez", "3323");
        funcionario maria = new funcionario("222", "222", "Maria Perez", "222222");
        usuarios.add(admin);
        usuarios.add(juan);
        usuarios.add(maria);

        categoria catSala10 = new categoria("CAT-000001", "Sala para 10 personas");
        categoria catLaptop = new categoria("CAT-000002", "Laptop windows");
        categoria catSalaJuntas = new categoria("CAT-000003", "Sala de Juntas");
        categorias.add(catSala10);
        categorias.add(catLaptop);
        categorias.add(catSalaJuntas);

        recurso laptop238715 = new recurso("238715", catLaptop, "Laptop #238715");
        recurso sala34343 = new recurso("34343", catSala10, "Sala 1 primer piso");
        recurso laptop45238 = new recurso("45238", catLaptop, "Laptop #45238");
        recurso salaJuntas51010 = new recurso("51010", catSalaJuntas, "Sala de Juntas 3er piso");
        recursos.add(laptop238715);
        recursos.add(sala34343);
        recursos.add(laptop45238);
        recursos.add(salaJuntas51010);

        reservas.add(new reserva("RES-000001", "Reunion clientes",
                LocalDate.of(2026, 7, 31), LocalTime.of(8, 0), LocalTime.of(10, 0),
                estadoReserva.ACTIVA, juan, List.of(laptop238715, sala34343)));

        reservas.add(new reserva("RES-000003", "Sesion de Junta Directiva",
                LocalDate.of(2026, 8, 5), LocalTime.of(9, 0), LocalTime.of(11, 0),
                estadoReserva.ACTIVA, juan, List.of(laptop238715, laptop45238)));

        reservas.add(new reserva("RES-000005", "Reunion de Control",
                LocalDate.of(2026, 8, 12), LocalTime.of(8, 0), LocalTime.of(9, 0),
                estadoReserva.CANCELADA, juan, List.of(laptop45238)));
    }

    public static usuario buscarUsuario(String id) {
        for (usuario u : usuarios) {
            if (u.getId().equalsIgnoreCase(id)) {
                return u;
            }
        }
        return null;
    }

    public static List<reserva> reservasDe(funcionario funcionario) {
        List<reserva> resultado = new ArrayList<>();
        for (reserva r : reservas) {
            if (r.getFuncionario() == funcionario) {
                resultado.add(r);
            }
        }
        resultado.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));
        return resultado;
    }

    public static String generarIdReserva() {
        String id = String.format("RES-%06d", siguienteNumeroReserva);
        siguienteNumeroReserva++;
        return id;
    }

    public static int obtenerSiguienteNumeroReserva() {
        return siguienteNumeroReserva;
    }

    public static void establecerSiguienteNumeroReserva(int valor) {
        siguienteNumeroReserva = valor;
    }

    public static recurso buscarRecursoDisponible(categoria categoria, LocalDate fecha,
                                                  LocalTime horaInicio, LocalTime horaFin,
                                                  String idReservaAExcluir) {
        for (recurso recurso : recursos) {
            if (recurso.getCategoria() != categoria) {
                continue;
            }
            if (estaLibre(recurso, fecha, horaInicio, horaFin, idReservaAExcluir)) {
                return recurso;
            }
        }
        return null;
    }

    private static boolean estaLibre(recurso recurso, LocalDate fecha, LocalTime horaInicio,
                                     LocalTime horaFin, String idReservaAExcluir) {
        for (reserva r : reservas) {
            if (!r.estaActiva()) {
                continue;
            }
            if (idReservaAExcluir != null && r.getId().equals(idReservaAExcluir)) {
                continue;
            }
            if (!r.getFecha().equals(fecha)) {
                continue;
            }
            if (!r.getRecursosAsignados().contains(recurso)) {
                continue;
            }
            boolean seSolapan = horaInicio.isBefore(r.getHoraFin()) && r.getHoraInicio().isBefore(horaFin);
            if (seSolapan) {
                return false;
            }
        }
        return true;
    }
}