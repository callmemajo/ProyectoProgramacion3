import reservas.controller.controladorCategorias;
import reservas.controller.controladorFuncionarios;
import reservas.controller.controladorRecursos;
import reservas.almacenDatos;
import reservas.persistenciaXml;
import reservas.modelo.*;
import reservas.vista.generadorPdf;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
        probarCrearFuncionario();
        probarNoCrearFuncionarioConIdRepetido();
        probarEditarFuncionario();
        probarNoEliminarFuncionarioConReservas();
        probarEliminarFuncionarioSinReservas();
        probarCrearCategoria();
        probarNoCrearCategoriaConIdRepetido();
        probarEditarCategoria();
        probarNoEliminarCategoriaConRecursos();
        probarEliminarCategoriaSinRecursos();
        probarCrearRecurso();
        probarNoCrearRecursoConIdRepetido();
        probarEditarRecurso();
        probarNoEliminarRecursoConReservas();
        probarEliminarRecursoSinReservas();
        probarGenerarReportePdf();
        probarGenerarReportePdfConTablaVacia();
        probarPersistenciaXml();

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

    private static void probarCrearFuncionario() {
        controladorFuncionarios controlador = new controladorFuncionarios();
        int cantidadAntes = controlador.listarFuncionarios().size();

        controlador.crearFuncionario("333", "clave333", "Pedro Solano", "8888-8888");

        int cantidadDespues = controlador.listarFuncionarios().size();
        verificar("crear funcionario aumenta la lista de funcionarios", cantidadDespues == cantidadAntes + 1);
        verificar("el funcionario nuevo se puede autenticar", almacenDatos.buscarUsuario("333").autenticar("clave333"));
    }

    private static void probarNoCrearFuncionarioConIdRepetido() {
        controladorFuncionarios controlador = new controladorFuncionarios();
        boolean lanzoExcepcion = false;
        try {
            controlador.crearFuncionario("333", "otraClave123", "Otro Nombre", "1111-1111");
        } catch (IllegalArgumentException ex) {
            lanzoExcepcion = true;
        }
        verificar("no se puede crear un funcionario con un Id que ya existe", lanzoExcepcion);
    }

    private static void probarEditarFuncionario() {
        controladorFuncionarios controlador = new controladorFuncionarios();
        funcionario pedro = (funcionario) almacenDatos.buscarUsuario("333");

        controlador.editarFuncionario(pedro, "Pedro Solano Mora", "7777-7777");

        verificar("editar funcionario actualiza el nombre", pedro.getNombre().equals("Pedro Solano Mora"));
        verificar("editar funcionario actualiza el telefono", pedro.getTelefono().equals("7777-7777"));
    }

    private static void probarNoEliminarFuncionarioConReservas() {
        controladorFuncionarios controlador = new controladorFuncionarios();
        funcionario juan = (funcionario) almacenDatos.buscarUsuario("111");

        boolean lanzoExcepcion = false;
        try {
            controlador.eliminarFuncionario(juan);
        } catch (IllegalStateException ex) {
            lanzoExcepcion = true;
        }
        verificar("no se puede eliminar un funcionario que tiene reservas", lanzoExcepcion);
        verificar("el funcionario con reservas sigue en la lista", almacenDatos.buscarUsuario("111") != null);
    }

    private static void probarEliminarFuncionarioSinReservas() {
        controladorFuncionarios controlador = new controladorFuncionarios();
        funcionario pedro = (funcionario) almacenDatos.buscarUsuario("333");

        controlador.eliminarFuncionario(pedro);

        verificar("eliminar un funcionario sin reservas lo quita de la lista", almacenDatos.buscarUsuario("333") == null);
    }

    private static void probarCrearCategoria() {
        controladorCategorias controlador = new controladorCategorias();
        int cantidadAntes = controlador.listarCategorias().size();

        controlador.crearCategoria("CAT-TEST", "Categoria de prueba");

        int cantidadDespues = controlador.listarCategorias().size();
        verificar("crear categoria aumenta la lista de categorias", cantidadDespues == cantidadAntes + 1);
    }

    private static void probarNoCrearCategoriaConIdRepetido() {
        controladorCategorias controlador = new controladorCategorias();
        boolean lanzoExcepcion = false;
        try {
            controlador.crearCategoria("CAT-TEST", "Otra descripcion");
        } catch (IllegalArgumentException ex) {
            lanzoExcepcion = true;
        }
        verificar("no se puede crear una categoria con un Id que ya existe", lanzoExcepcion);
    }

    private static void probarEditarCategoria() {
        controladorCategorias controlador = new controladorCategorias();
        categoria catPrueba = buscarCategoriaPorId("CAT-TEST");

        controlador.editarCategoria(catPrueba, "Descripcion editada");

        verificar("editar categoria actualiza la descripcion", catPrueba.getDescripcion().equals("Descripcion editada"));
    }

    private static void probarNoEliminarCategoriaConRecursos() {
        controladorCategorias controlador = new controladorCategorias();
        categoria catSalaJuntas = almacenDatos.categorias.get(2);

        boolean lanzoExcepcion = false;
        try {
            controlador.eliminarCategoria(catSalaJuntas);
        } catch (IllegalStateException ex) {
            lanzoExcepcion = true;
        }
        verificar("no se puede eliminar una categoria que tiene recursos", lanzoExcepcion);
    }

    private static void probarEliminarCategoriaSinRecursos() {
        controladorCategorias controlador = new controladorCategorias();
        categoria catPrueba = buscarCategoriaPorId("CAT-TEST");

        controlador.eliminarCategoria(catPrueba);

        verificar("eliminar una categoria sin recursos la quita de la lista", buscarCategoriaPorId("CAT-TEST") == null);
    }

    private static categoria buscarCategoriaPorId(String id) {
        for (categoria c : almacenDatos.categorias) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    private static void probarCrearRecurso() {
        controladorRecursos controlador = new controladorRecursos();
        int cantidadAntes = controlador.listarRecursos().size();
        categoria catLaptop = almacenDatos.categorias.get(1);

        controlador.crearRecurso("REC-TEST", catLaptop, "Recurso de prueba");

        int cantidadDespues = controlador.listarRecursos().size();
        verificar("crear recurso aumenta la lista de recursos", cantidadDespues == cantidadAntes + 1);
    }

    private static void probarNoCrearRecursoConIdRepetido() {
        controladorRecursos controlador = new controladorRecursos();
        categoria catLaptop = almacenDatos.categorias.get(1);
        boolean lanzoExcepcion = false;
        try {
            controlador.crearRecurso("REC-TEST", catLaptop, "Otra descripcion");
        } catch (IllegalArgumentException ex) {
            lanzoExcepcion = true;
        }
        verificar("no se puede crear un recurso con un Id que ya existe", lanzoExcepcion);
    }

    private static void probarEditarRecurso() {
        controladorRecursos controlador = new controladorRecursos();
        recurso recursoPrueba = buscarRecursoPorId("REC-TEST");
        categoria catSalaJuntas = almacenDatos.categorias.get(2);

        controlador.editarRecurso(recursoPrueba, catSalaJuntas, "Descripcion editada");

        verificar("editar recurso actualiza la categoria", recursoPrueba.getCategoria() == catSalaJuntas);
        verificar("editar recurso actualiza la descripcion", recursoPrueba.getDescripcion().equals("Descripcion editada"));
    }

    private static void probarNoEliminarRecursoConReservas() {
        controladorRecursos controlador = new controladorRecursos();
        recurso recursoOcupado = almacenDatos.recursos.get(0);

        boolean lanzoExcepcion = false;
        try {
            controlador.eliminarRecurso(recursoOcupado);
        } catch (IllegalStateException ex) {
            lanzoExcepcion = true;
        }
        verificar("no se puede eliminar un recurso que esta asignado en una reserva", lanzoExcepcion);
    }

    private static void probarEliminarRecursoSinReservas() {
        controladorRecursos controlador = new controladorRecursos();
        recurso recursoPrueba = buscarRecursoPorId("REC-TEST");

        controlador.eliminarRecurso(recursoPrueba);

        verificar("eliminar un recurso sin reservas lo quita de la lista", buscarRecursoPorId("REC-TEST") == null);
    }

    private static void probarGenerarReportePdf() {
        try {
            String[] encabezados = {"Id", "Descripcion"};
            List<String[]> filas = new ArrayList<>();
            filas.add(new String[]{"CAT-001", "Categoria de prueba"});
            filas.add(new String[]{"CAT-002", "Otra categoria de prueba"});

            Path archivoTemporal = Files.createTempFile("reporte_prueba", ".pdf");
            generadorPdf.generarReporte(archivoTemporal, "Reporte de prueba", encabezados, filas);

            byte[] contenido = Files.readAllBytes(archivoTemporal);
            String encabezadoPdf = new String(contenido, 0, Math.min(5, contenido.length));
            verificar("generarReporte produce un archivo con encabezado %PDF-", encabezadoPdf.equals("%PDF-"));
            verificar("generarReporte produce un archivo con contenido", contenido.length > 100);

            Files.deleteIfExists(archivoTemporal);
        } catch (Exception ex) {
            verificar("generarReporte no lanza excepciones con datos normales", false);
        }
    }

    private static void probarGenerarReportePdfConTablaVacia() {
        try {
            String[] encabezados = {"Id", "Descripcion"};
            Path archivoTemporal = Files.createTempFile("reporte_prueba_vacio", ".pdf");
            generadorPdf.generarReporte(archivoTemporal, "Reporte sin registros", encabezados, new ArrayList<>());

            byte[] contenido = Files.readAllBytes(archivoTemporal);
            verificar("generarReporte con tabla vacia igual produce un PDF valido", contenido.length > 50);

            Files.deleteIfExists(archivoTemporal);
        } catch (Exception ex) {
            verificar("generarReporte no lanza excepciones con tabla vacia", false);
        }
    }

    private static void probarPersistenciaXml() {
        int usuariosAntes = almacenDatos.usuarios.size();
        int categoriasAntes = almacenDatos.categorias.size();
        int recursosAntes = almacenDatos.recursos.size();
        int reservasAntes = almacenDatos.reservas.size();

        persistenciaXml.guardar();

        java.io.File archivoXml = new java.io.File("datos.xml");
        verificar("guardar crea el archivo datos.xml", archivoXml.exists() && archivoXml.length() > 0);

        boolean cargadoDesdeXml = persistenciaXml.cargar();
        verificar("cargar lee el archivo datos.xml exitosamente", cargadoDesdeXml);
        verificar("cargar recupera la misma cantidad de usuarios", almacenDatos.usuarios.size() == usuariosAntes);
        verificar("cargar recupera la misma cantidad de categorias", almacenDatos.categorias.size() == categoriasAntes);
        verificar("cargar recupera la misma cantidad de recursos", almacenDatos.recursos.size() == recursosAntes);
        verificar("cargar recupera la misma cantidad de reservas", almacenDatos.reservas.size() == reservasAntes);

        usuario adminRecuperado = almacenDatos.buscarUsuario("admin");
        verificar("el admin recuperado del XML se puede autenticar", adminRecuperado != null && adminRecuperado.autenticar("admin"));

        reserva primeraReservaRecuperada = almacenDatos.reservas.get(0);
        verificar("la reserva recuperada del XML conserva sus recursos asignados",
                !primeraReservaRecuperada.getRecursosAsignados().isEmpty());
    }

    private static recurso buscarRecursoPorId(String id) {
        for (recurso r : almacenDatos.recursos) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
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