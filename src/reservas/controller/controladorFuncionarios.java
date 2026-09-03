package reservas.controller;

import reservas.almacenDatos;
import reservas.modelo.funcionario;
import reservas.modelo.reserva;
import reservas.modelo.usuario;

import java.util.ArrayList;
import java.util.List;

public class controladorFuncionarios {

    public List<funcionario> listarFuncionarios() {
        List<funcionario> resultado = new ArrayList<>();
        for (usuario usuario : almacenDatos.usuarios) {
            if (usuario instanceof funcionario funcionario) {
                resultado.add(funcionario);
            }
        }
        resultado.sort((a, b) -> a.getId().compareToIgnoreCase(b.getId()));
        return resultado;
    }

    public void crearFuncionario(String id, String clave, String nombre, String telefono) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El Id es obligatorio.");
        }
        if (almacenDatos.buscarUsuario(id) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese Id.");
        }
        if (clave == null || clave.length() < 6) {
            throw new IllegalArgumentException("La clave debe tener al menos 6 caracteres.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        funcionario nuevo = new funcionario(id, clave, nombre, telefono);
        almacenDatos.usuarios.add(nuevo);
    }

    public void editarFuncionario(funcionario funcionario, String nombre, String telefono) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        funcionario.setNombre(nombre);
        funcionario.setTelefono(telefono);
    }

    public void eliminarFuncionario(funcionario funcionario) {
        for (reserva reserva : almacenDatos.reservas) {
            if (reserva.getFuncionario() == funcionario) {
                throw new IllegalStateException("No se puede eliminar: el funcionario tiene reservas registradas.");
            }
        }
        almacenDatos.usuarios.remove(funcionario);
    }
}