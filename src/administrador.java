package reservas.modelo;

public class administrador extends usuario {

    public administrador(String id, String clave) {
        super(id, clave, rolUsuario.ADMINISTRADOR);
    }

    @Override
    public String getNombreMostrar() {
        return "Administrador";
    }
}