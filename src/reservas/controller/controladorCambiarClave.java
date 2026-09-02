package reservas.controller;

import reservas.almacenDatos;
import reservas.modelo.usuario;

public class controladorCambiarClave {

    public usuario buscarUsuario(String id) {
        return almacenDatos.buscarUsuario(id);
    }

    public void cambiarClave(usuario usuario, String claveActual, String claveNueva, String claveConfirmar) {
        if (!claveNueva.equals(claveConfirmar)) {
            throw new IllegalArgumentException("La clave nueva y su confirmacion no coinciden.");
        }
        usuario.cambiarClave(claveActual, claveNueva);
    }
}