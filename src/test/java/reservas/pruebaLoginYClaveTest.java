package reservas;

import reservas.modelo.usuario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class pruebaLoginYClaveTest {

    @Test
    void loginFunciona() {
        usuario admin = almacenDatos.buscarUsuario("admin");
        assertNotNull(admin, "el usuario admin existe");
        assertTrue(admin.autenticar("admin"), "login admin/admin autentica");
        assertFalse(admin.autenticar("otraClave"), "login admin con clave incorrecta falla");

        usuario juan = almacenDatos.buscarUsuario("111");
        assertNotNull(juan, "el usuario 111 existe");
        assertTrue(juan.autenticar("111"), "login 111/111 (Juan Perez) autentica");

        assertNull(almacenDatos.buscarUsuario("999"), "usuario inexistente no aparece");
    }

    @Test
    void cambiarClaveFunciona() {
        usuario maria = almacenDatos.buscarUsuario("222");
        assertNotNull(maria, "el usuario 222 existe");

        maria.cambiarClave("222", "nuevaClave123");
        assertTrue(maria.autenticar("nuevaClave123"), "cambiar clave con clave actual correcta funciona");

        assertThrows(IllegalArgumentException.class,
                () -> maria.cambiarClave("claveIncorrecta", "otraClaveNueva"),
                "cambiar clave con clave actual incorrecta se rechaza");

        assertThrows(IllegalArgumentException.class,
                () -> maria.cambiarClave("nuevaClave123", "abc"),
                "cambiar clave con clave nueva muy corta se rechaza");

        maria.cambiarClave("nuevaClave123", "clave222");
    }
}