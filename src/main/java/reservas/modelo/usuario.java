package reservas.modelo;

public abstract class usuario {

    private final String id;
    private String clave;
    private final rolUsuario rol;

    protected usuario(String id, String clave, rolUsuario rol) {
        this.id = id;
        this.clave = clave;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public rolUsuario getRol() {
        return rol;
    }

    public String getClave() {
        return clave;
    }

    public boolean autenticar(String claveIngresada) {
        return clave != null && clave.equals(claveIngresada);
    }

    public void cambiarClave(String claveActual, String claveNueva) {
        if (!autenticar(claveActual)) {
            throw new IllegalArgumentException("La clave actual no es correcta.");
        }
        if (claveNueva == null || claveNueva.length() < 6) {
            throw new IllegalArgumentException("La clave nueva debe tener al menos 6 caracteres.");
        }
        this.clave = claveNueva;
    }

    public abstract String getNombreMostrar();
}