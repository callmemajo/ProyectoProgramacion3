package reservas;

import reservas.controller.controladorRecursos;
import reservas.modelo.categoria;
import reservas.modelo.recurso;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class pruebaRecursosTest {

    private static recurso buscarRecursoPorId(String id) {
        for (recurso r : almacenDatos.recursos) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    @Test
    @Order(1)
    void crearRecursoAgregaALaLista() {
        controladorRecursos controlador = new controladorRecursos();
        int cantidadAntes = controlador.listarRecursos().size();
        categoria catLaptop = almacenDatos.categorias.get(1);

        controlador.crearRecurso("REC-TEST", catLaptop, "Recurso de prueba");

        int cantidadDespues = controlador.listarRecursos().size();
        assertEquals(cantidadAntes + 1, cantidadDespues, "crear recurso aumenta la lista de recursos");
    }

    @Test
    @Order(2)
    void noSePuedeCrearRecursoConIdRepetido() {
        controladorRecursos controlador = new controladorRecursos();
        categoria catLaptop = almacenDatos.categorias.get(1);
        assertThrows(IllegalArgumentException.class,
                () -> controlador.crearRecurso("REC-TEST", catLaptop, "Otra descripcion"),
                "no se puede crear un recurso con un Id que ya existe");
    }

    @Test
    @Order(3)
    void editarRecursoActualizaCategoriaYDescripcion() {
        controladorRecursos controlador = new controladorRecursos();
        recurso recursoPrueba = buscarRecursoPorId("REC-TEST");
        categoria catSalaJuntas = almacenDatos.categorias.get(2);

        controlador.editarRecurso(recursoPrueba, catSalaJuntas, "Descripcion editada");

        assertSame(catSalaJuntas, recursoPrueba.getCategoria(), "editar recurso actualiza la categoria");
        assertEquals("Descripcion editada", recursoPrueba.getDescripcion(), "editar recurso actualiza la descripcion");
    }

    @Test
    @Order(4)
    void noSePuedeEliminarRecursoConReservas() {
        controladorRecursos controlador = new controladorRecursos();
        recurso recursoOcupado = almacenDatos.recursos.get(0);

        assertThrows(IllegalStateException.class,
                () -> controlador.eliminarRecurso(recursoOcupado),
                "no se puede eliminar un recurso que esta asignado en una reserva");
    }

    @Test
    @Order(5)
    void eliminarRecursoSinReservasLoQuita() {
        controladorRecursos controlador = new controladorRecursos();
        recurso recursoPrueba = buscarRecursoPorId("REC-TEST");

        controlador.eliminarRecurso(recursoPrueba);

        assertNull(buscarRecursoPorId("REC-TEST"), "eliminar un recurso sin reservas lo quita de la lista");
    }
}