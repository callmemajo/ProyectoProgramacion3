package reservas.controller;

import reservas.almacenDatos;
import reservas.persistenciaXml;
import reservas.modelo.categoria;
import reservas.modelo.recurso;
import reservas.modelo.reserva;

import java.util.ArrayList;
import java.util.List;

public class controladorRecursos {

    public List<categoria> listarCategorias() {
        return almacenDatos.categorias;
    }

    public List<recurso> listarRecursos() {
        List<recurso> resultado = new ArrayList<>(almacenDatos.recursos);
        resultado.sort((a, b) -> a.getId().compareToIgnoreCase(b.getId()));
        return resultado;
    }

    public void crearRecurso(String id, categoria categoria, String descripcion) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El Id es obligatorio.");
        }
        if (buscarRecurso(id) != null) {
            throw new IllegalArgumentException("Ya existe un recurso con ese Id.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("Selecciona una categoria.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion es obligatoria.");
        }
        almacenDatos.recursos.add(new recurso(id, categoria, descripcion));
        persistenciaXml.guardar();
    }

    public void editarRecurso(recurso recurso, categoria categoria, String descripcion) {
        if (categoria == null) {
            throw new IllegalArgumentException("Selecciona una categoria.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion es obligatoria.");
        }
        recurso.setCategoria(categoria);
        recurso.setDescripcion(descripcion);
        persistenciaXml.guardar();
    }

    public void eliminarRecurso(recurso recurso) {
        for (reserva reserva : almacenDatos.reservas) {
            if (reserva.getRecursosAsignados().contains(recurso)) {
                throw new IllegalStateException("No se puede eliminar: el recurso esta asignado en alguna reserva.");
            }
        }
        almacenDatos.recursos.remove(recurso);
        persistenciaXml.guardar();
    }

    private recurso buscarRecurso(String id) {
        for (recurso recurso : almacenDatos.recursos) {
            if (recurso.getId().equalsIgnoreCase(id)) {
                return recurso;
            }
        }
        return null;
    }
}