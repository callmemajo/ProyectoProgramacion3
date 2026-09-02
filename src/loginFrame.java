package reservas.vista;

import reservas.controlador.controladorLogin;
import reservas.modelo.usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class loginFrame extends JFrame {

    private final controladorLogin controlador = new controladorLogin();

    private final JTextField campoId = new JTextField(16);
    private final JPasswordField campoClave = new JPasswordField(16);
    private final JLabel etiquetaError = new JLabel(" ");

    public loginFrame() {
        super("Sistema de Reservas");
        construirInterfaz();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 480);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void construirInterfaz() {
        panelGradiente contenedor = new panelGradiente(estilos.GRADIENTE_INICIO, estilos.GRADIENTE_FIN, false);
        contenedor.setLayout(new GridBagLayout());
        setContentPane(contenedor);

        panelTarjeta tarjeta = new panelTarjeta(null);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPreferredSize(new Dimension(320, 400));

        avatarCirculo avatar = new avatarCirculo("R", estilos.MORADO, 56);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Iniciar sesion");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 19f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Sistema de Reserva de Recursos");
        subtitulo.setFont(subtitulo.getFont().deriveFont(12f));
        subtitulo.setForeground(estilos.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        tarjeta.add(avatar);
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(2));
        tarjeta.add(subtitulo);
        tarjeta.add(Box.createVerticalStrut(22));
        tarjeta.add(filaCampo("Id", campoId));
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(filaCampo("Clave", campoClave));

        etiquetaError.setForeground(estilos.ROJO);
        etiquetaError.setAlignmentX(Component.CENTER_ALIGNMENT);
        etiquetaError.setFont(etiquetaError.getFont().deriveFont(Font.BOLD, 12f));
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(etiquetaError);

        JButton botonIngresar = estilos.botonPrimario("Ingresar");
        JButton botonCancelar = estilos.botonSecundario("Cancelar");
        botonIngresar.addActionListener(this::alIngresar);
        botonCancelar.addActionListener(e -> limpiarFormulario());

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        filaBotones.setOpaque(false);
        filaBotones.add(botonIngresar);
        filaBotones.add(botonCancelar);
        filaBotones.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(Box.createVerticalStrut(18));
        tarjeta.add(filaBotones);

        JButton enlaceCambiarClave = new JButton("Cambiar clave?");
        enlaceCambiarClave.setBorderPainted(false);
        enlaceCambiarClave.setContentAreaFilled(false);
        enlaceCambiarClave.setForeground(estilos.AZUL_OSCURO);
        enlaceCambiarClave.setFont(enlaceCambiarClave.getFont().deriveFont(Font.BOLD, 12f));
        enlaceCambiarClave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        enlaceCambiarClave.setAlignmentX(Component.CENTER_ALIGNMENT);
        enlaceCambiarClave.addActionListener(e -> new cambiarClaveDialog(this, null).setVisible(true));
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(enlaceCambiarClave);

        contenedor.add(tarjeta);

        campoClave.addActionListener(this::alIngresar);
        campoId.addActionListener(this::alIngresar);
    }

    private JPanel filaCampo(String etiqueta, JTextField campo) {
        JPanel fila = new JPanel();
        fila.setOpaque(false);
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jLabel = estilos.etiquetaTitulo(etiqueta);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setMaximumSize(new Dimension(260, 36));
        campo.setPreferredSize(new Dimension(260, 36));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(estilos.BORDE, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        fila.add(jLabel);
        fila.add(Box.createVerticalStrut(4));
        fila.add(campo);
        return fila;
    }

    private void alIngresar(ActionEvent evento) {
        String id = campoId.getText().trim();
        String clave = new String(campoClave.getPassword());

        if (id.isEmpty() || clave.isEmpty()) {
            mostrarError("Ingresa el id y la clave.");
            return;
        }

        usuario usuario = controlador.autenticar(id, clave);
        if (usuario == null) {
            mostrarError("Id o clave incorrectos.");
            return;
        }

        mostrarError(" ");
        ventanaPrincipalFrame ventanaPrincipal = new ventanaPrincipalFrame(usuario);
        ventanaPrincipal.setVisible(true);
        dispose();
    }

    private void mostrarError(String mensaje) {
        etiquetaError.setText(mensaje);
    }

    private void limpiarFormulario() {
        campoId.setText("");
        campoClave.setText("");
        mostrarError(" ");
    }
}