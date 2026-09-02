package reservas;

import reservas.vista.loginFrame;

import javax.swing.*;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new loginFrame().setVisible(true));
    }
}