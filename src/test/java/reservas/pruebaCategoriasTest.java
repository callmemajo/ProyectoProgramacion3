package reservas;

import reservas.controller.controladorCategorias;
import reservas.modelo.categoria;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class pruebaCategoriasTest {

    private static categoria buscarCategoriaPorId(String id) {
        for (categoria c : almacenDatos.categorias) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    @Test
    @Order(1)
    void crearCategoriaAgregaALaLista() {
        controladorCategorias controlador = new controladorCategorias();
        int cantidadAntes = controlador.listarCategorias().size();

        controlador.crearCategoria("CAT-TEST", "Categoria de prueba");

        int cantidadDespues = controlador.listarCategorias().size();
        assertEquals(cantidadAntes + 1, cantidadDespues, "crear categoria aumenta la lista de categorias");
    }

    @Test
    @Order(2)
    void noSePuedeCrearCategoriaConIdRepetido() {
        controladorCategorias controlador = new controladorCategorias();
        assertThrows(IllegalArgumentException.class,
                () -> controlador.crearCategoria("CAT-TEST", "Otra descripcion"),
                "no se puede crear una categoria con un Id que ya existe");
    }

    @Test
    @Order(3)
    void editarCategoriaActualizaLaDescripcion() {
        controladorCategorias controlador = new controladorCategorias();
        categoria catPrueba = buscarCategoriaPorId("CAT-TEST");

        controlador.editarCategoria(catPrueba, "Descripcion editada");

        assertEquals("Descripcion editada", catPrueba.getDescripcion(), "editar categoria actualiza la descripcion");
    }

    @Test
    @Order(4)
    void noSePuedeEliminarCategoriaConRecursos() {
        controladorCategorias controlador = new controladorCategorias();
        categoria catSalaJuntas = almacenDatos.categorias.get(2);

        assertThrows(IllegalStateException.class,
                () -> controlador.eliminarCategoria(catSalaJuntas),
                "no se puede eliminar una categoria que tiene recursos");
    }

    @Test
    @Order(5)
    void eliminarCategoriaSinRecursosLaQuita() {
        controladorCategorias controlador = new controladorCategorias();
        categoria catPrueba = buscarCategoriaPorId("CAT-TEST");

        controlador.eliminarCategoria(catPrueba);

        assertNull(buscarCategoriaPorId("CAT-TEST"), "eliminar una categoria sin recursos la quita de la lista");
    }
}