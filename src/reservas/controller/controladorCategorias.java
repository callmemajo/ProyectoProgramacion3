package reservas.controller;

import reservas.almacenDatos;
import reservas.modelo.categoria;
import reservas.modelo.recurso;

import java.util.ArrayList;
import java.util.List;

public class controladorCategorias {

    public List<categoria> listarCategorias() {
        List<categoria> resultado = new ArrayList<>(almacenDatos.categorias);
        resultado.sort((a, b) -> a.getId().compareToIgnoreCase(b.getId()));
        return resultado;
    }

    public void crearCategoria(String id, String descripcion) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El Id es obligatorio.");
        }
        if (buscarCategoria(id) != null) {
            throw new IllegalArgumentException("Ya existe una categoria con ese Id.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion es obligatoria.");
        }
        almacenDatos.categorias.add(new categoria(id, descripcion));
    }

    public void editarCategoria(categoria categoria, String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion es obligatoria.");
        }
        categoria.setDescripcion(descripcion);
    }

    public void eliminarCategoria(categoria categoria) {
        for (recurso recurso : almacenDatos.recursos) {
            if (recurso.getCategoria() == categoria) {
                throw new IllegalStateException("No se puede eliminar: hay recursos que usan esta categoria.");
            }
        }
        almacenDatos.categorias.remove(categoria);
    }

    private categoria buscarCategoria(String id) {
        for (categoria categoria : almacenDatos.categorias) {
            if (categoria.getId().equalsIgnoreCase(id)) {
                return categoria;
            }
        }
        return null;
    }
}