package reservas.modelo;

public class funcionario extends usuario {

    private String nombre;
    private String telefono;

    public funcionario(String id, String clave, String nombre, String telefono) {
        super(id, clave, rolUsuario.FUNCIONARIO);
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String getNombreMostrar() {
        return nombre;
    }
}