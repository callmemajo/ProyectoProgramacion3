package reservas.controller;

import reservas.almacenDatos;
import reservas.modelo.usuario;

public class controladorLogin {

    public usuario autenticar(String id, String clave) {
        usuario usuario = almacenDatos.buscarUsuario(id);
        if (usuario == null || !usuario.autenticar(clave)) {
            return null;
        }
        return usuario;
    }
}