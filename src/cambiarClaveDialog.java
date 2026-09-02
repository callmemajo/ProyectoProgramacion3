package reservas.vista;

import reservas.controlador.controladorCambiarClave;
import reservas.modelo.usuario;

import javax.swing.*;
import java.awt.*;

public class cambiarClaveDialog extends JDialog {

    private final controladorCambiarClave controlador = new controladorCambiarClave();

    private final usuario usuarioConocido;
    private JTextField campoId;
    private final JPasswordField campoClaveActual = new JPasswordField(16);
    private final JPasswordField campoClaveNueva = new JPasswordField(16);
    private final JPasswordField campoClaveConfirmar = new JPasswordField(16);
    private final JLabel etiquetaError = new JLabel(" ");

    public cambiarClaveDialog(Window propietaria, usuario usuarioConocido) {
        super(propietaria, "Cambiar clave", ModalityType.APPLICATION_MODAL);
        this.usuarioConocido = usuarioConocido;
        construirInterfaz();
        pack();
        setLocationRelativeTo(propietaria);
        setResizable(false);
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(estilos.FONDO_VENTANA);
        setContentPane(raiz);

        panelGradiente cabecera = new panelGradiente(estilos.MORADO, estilos.AZUL_OSCURO, true);
        cabecera.setLayout(new BorderLayout());
        cabecera.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        JLabel tituloCabecera = new JLabel("Cambiar clave");
        tituloCabecera.setForeground(Color.WHITE);
        tituloCabecera.setFont(tituloCabecera.getFont().deriveFont(Font.BOLD, 17f));
        cabecera.add(tituloCabecera, BorderLayout.WEST);
        raiz.add(cabecera, BorderLayout.NORTH);

        panelTarjeta panel = new panelTarjeta(null);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setBackground(estilos.FONDO_VENTANA);
        envoltorio.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        envoltorio.add(panel, BorderLayout.CENTER);
        raiz.add(envoltorio, BorderLayout.CENTER);

        if (usuarioConocido != null) {
            JLabel quien = new JLabel("Usuario: " + usuarioConocido.getId()
                    + " (" + usuarioConocido.getNombreMostrar() + ")");
            quien.setFont(quien.getFont().deriveFont(Font.BOLD, 13f));
            quien.setForeground(estilos.TEXTO_TITULO);
            quien.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(quien);
            panel.add(Box.createVerticalStrut(14));
        } else {
            campoId = new JTextField(16);
            panel.add(filaCampo("Id", campoId));
            panel.add(Box.createVerticalStrut(10));
        }

        panel.add(filaCampo("Clave actual", campoClaveActual));
        panel.add(Box.createVerticalStrut(10));
        panel.add(filaCampo("Clave nueva", campoClaveNueva));
        panel.add(Box.createVerticalStrut(10));
        panel.add(filaCampo("Confirmar clave nueva", campoClaveConfirmar));

        JLabel pista = new JLabel("Minimo 6 caracteres.");
        pista.setForeground(estilos.TEXTO_SECUNDARIO);
        pista.setFont(pista.getFont().deriveFont(11f));
        pista.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(pista);

        etiquetaError.setForeground(estilos.ROJO);
        etiquetaError.setFont(etiquetaError.getFont().deriveFont(Font.BOLD, 12f));
        etiquetaError.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(4));
        panel.add(etiquetaError);

        JButton botonGuardar = estilos.botonExito("Guardar");
        JButton botonCancelar = estilos.botonSecundario("Cancelar");
        botonGuardar.addActionListener(e -> alGuardar());
        botonCancelar.addActionListener(e -> dispose());

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaBotones.setOpaque(false);
        filaBotones.add(botonGuardar);
        filaBotones.add(botonCancelar);
        filaBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(14));
        panel.add(filaBotones);
    }

    private JPanel filaCampo(String etiqueta, JTextField campo) {
        JPanel fila = new JPanel();
        fila.setOpaque(false);
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel jLabel = estilos.etiquetaTitulo(etiqueta);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setMaximumSize(new Dimension(280, 34));
        campo.setPreferredSize(new Dimension(280, 34));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(estilos.BORDE, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        fila.add(jLabel);
        fila.add(Box.createVerticalStrut(4));
        fila.add(campo);
        return fila;
    }

    private void alGuardar() {
        usuario usuario = usuarioConocido;
        if (usuario == null) {
            String id = campoId.getText().trim();
            if (id.isEmpty()) {
                mostrarError("Ingresa el id del usuario.");
                return;
            }
            usuario = controlador.buscarUsuario(id);
            if (usuario == null) {
                mostrarError("No existe un usuario con ese id.");
                return;
            }
        }

        String claveActual = new String(campoClaveActual.getPassword());
        String claveNueva = new String(campoClaveNueva.getPassword());
        String claveConfirmar = new String(campoClaveConfirmar.getPassword());

        try {
            controlador.cambiarClave(usuario, claveActual, claveNueva, claveConfirmar);
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Clave actualizada correctamente.",
                "Listo", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void mostrarError(String mensaje) {
        etiquetaError.setText(mensaje);
        pack();
    }
}