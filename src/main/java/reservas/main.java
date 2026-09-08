package reservas;

import reservas.vista.loginFrame;

import javax.swing.*;

public class main {
    public static void main(String[] args) {
        boolean seCargaronDatosGuardados = persistenciaXml.cargar();
        if (!seCargaronDatosGuardados) {
            persistenciaXml.guardar();
        }
        SwingUtilities.invokeLater(() -> new loginFrame().setVisible(true));
    }
}