package reservas;

import reservas.modelo.reserva;
import reservas.modelo.usuario;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class pruebaPersistenciaXmlIT {

    private static final Path ARCHIVO_DATOS = Path.of("datos.xml");
    private static final Path RESPALDO = Path.of("datos.xml.respaldo-it");

    @BeforeEach
    void respaldarDatosXmlSiExiste() throws IOException {
        if (Files.exists(ARCHIVO_DATOS)) {
            Files.copy(ARCHIVO_DATOS, RESPALDO, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @AfterEach
    void restaurarDatosXmlOriginal() throws IOException {
        if (Files.exists(RESPALDO)) {
            Files.copy(RESPALDO, ARCHIVO_DATOS, StandardCopyOption.REPLACE_EXISTING);
            Files.delete(RESPALDO);
        }
    }

    @Test
    void guardarYCargarPreservaLosDatos() {
        int usuariosAntes = almacenDatos.usuarios.size();
        int categoriasAntes = almacenDatos.categorias.size();
        int recursosAntes = almacenDatos.recursos.size();
        int reservasAntes = almacenDatos.reservas.size();

        persistenciaXml.guardar();

        File archivoXml = new File("datos.xml");
        assertTrue(archivoXml.exists() && archivoXml.length() > 0, "guardar crea el archivo datos.xml");

        boolean cargadoDesdeXml = persistenciaXml.cargar();
        assertTrue(cargadoDesdeXml, "cargar lee el archivo datos.xml exitosamente");
        assertEquals(usuariosAntes, almacenDatos.usuarios.size(), "cargar recupera la misma cantidad de usuarios");
        assertEquals(categoriasAntes, almacenDatos.categorias.size(), "cargar recupera la misma cantidad de categorias");
        assertEquals(recursosAntes, almacenDatos.recursos.size(), "cargar recupera la misma cantidad de recursos");
        assertEquals(reservasAntes, almacenDatos.reservas.size(), "cargar recupera la misma cantidad de reservas");

        usuario adminRecuperado = almacenDatos.buscarUsuario("admin");
        assertNotNull(adminRecuperado, "el admin recuperado del XML existe");
        assertTrue(adminRecuperado.autenticar("admin"), "el admin recuperado del XML se puede autenticar");

        reserva primeraReservaRecuperada = almacenDatos.reservas.get(0);
        assertFalse(primeraReservaRecuperada.getRecursosAsignados().isEmpty(),
                "la reserva recuperada del XML conserva sus recursos asignados");
    }
}