package reservas;

import reservas.controller.controladorFuncionarios;
import reservas.modelo.funcionario;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class pruebaFuncionariosTest {

    @Test
    @Order(1)
    void crearFuncionarioAgregaALaLista() {
        controladorFuncionarios controlador = new controladorFuncionarios();
        int cantidadAntes = controlador.listarFuncionarios().size();

        controlador.crearFuncionario("333", "clave333", "Pedro Solano", "8888-8888");

        int cantidadDespues = controlador.listarFuncionarios().size();
        assertEquals(cantidadAntes + 1, cantidadDespues, "crear funcionario aumenta la lista de funcionarios");
        assertTrue(almacenDatos.buscarUsuario("333").autenticar("clave333"),
                "el funcionario nuevo se puede autenticar");
    }

    @Test
    @Order(2)
    void noSePuedeCrearFuncionarioConIdRepetido() {
        controladorFuncionarios controlador = new controladorFuncionarios();
        assertThrows(IllegalArgumentException.class,
                () -> controlador.crearFuncionario("333", "otraClave123", "Otro Nombre", "1111-1111"),
                "no se puede crear un funcionario con un Id que ya existe");
    }

    @Test
    @Order(3)
    void editarFuncionarioActualizaSusDatos() {
        controladorFuncionarios controlador = new controladorFuncionarios();
        funcionario pedro = (funcionario) almacenDatos.buscarUsuario("333");

        controlador.editarFuncionario(pedro, "Pedro Solano Mora", "7777-7777");

        assertEquals("Pedro Solano Mora", pedro.getNombre(), "editar funcionario actualiza el nombre");
        assertEquals("7777-7777", pedro.getTelefono(), "editar funcionario actualiza el telefono");
    }

    @Test
    @Order(4)
    void noSePuedeEliminarFuncionarioConReservas() {
        controladorFuncionarios controlador = new controladorFuncionarios();
        funcionario juan = (funcionario) almacenDatos.buscarUsuario("111");

        assertThrows(IllegalStateException.class,
                () -> controlador.eliminarFuncionario(juan),
                "no se puede eliminar un funcionario que tiene reservas");
        assertNotNull(almacenDatos.buscarUsuario("111"), "el funcionario con reservas sigue en la lista");
    }

    @Test
    @Order(5)
    void eliminarFuncionarioSinReservasLoQuita() {
        controladorFuncionarios controlador = new controladorFuncionarios();
        funcionario pedro = (funcionario) almacenDatos.buscarUsuario("333");

        controlador.eliminarFuncionario(pedro);

        assertNull(almacenDatos.buscarUsuario("333"), "eliminar un funcionario sin reservas lo quita de la lista");
    }
}