package reservas.vista;

import reservas.modelo.funcionario;
import reservas.modelo.usuario;

import javax.swing.*;
import java.awt.*;

public class ventanaPrincipalFrame extends JFrame {

    private final usuario usuario;

    public ventanaPrincipalFrame(usuario usuario) {
        super("Sistema de Reservas - " + usuario.getId() + " (" + usuario.getRol() + ")");
        this.usuario = usuario;
        construirInterfaz();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 660);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(estilos.FONDO_VENTANA);

        add(construirBarraSuperior(), BorderLayout.NORTH);
        add(construirPestanas(), BorderLayout.CENTER);
    }

    private JPanel construirBarraSuperior() {
        panelGradiente barra = new panelGradiente(estilos.GRADIENTE_INICIO, estilos.GRADIENTE_FIN, true);
        barra.setLayout(new BorderLayout());
        barra.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izquierda.setOpaque(false);
        String inicial = usuario.getNombreMostrar().substring(0, 1).toUpperCase();
        avatarCirculo avatar = new avatarCirculo(inicial, new Color(255, 255, 255, 60), 34);
        JLabel etiquetaUsuario = new JLabel(usuario.getId() + "  -  " + usuario.getRol());
        etiquetaUsuario.setForeground(Color.WHITE);
        etiquetaUsuario.setFont(etiquetaUsuario.getFont().deriveFont(Font.BOLD, 13f));
        izquierda.add(avatar);
        izquierda.add(etiquetaUsuario);
        barra.add(izquierda, BorderLayout.WEST);

        JButton botonCambiarClave = estilos.botonBarra("Cambiar clave");
        botonCambiarClave.addActionListener(e -> new cambiarClaveDialog(this, usuario).setVisible(true));

        JButton botonCerrarSesion = estilos.botonBarra("Cerrar sesion");
        botonCerrarSesion.addActionListener(e -> cerrarSesion());

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);
        derecha.add(botonCambiarClave);
        derecha.add(botonCerrarSesion);
        barra.add(derecha, BorderLayout.EAST);

        return barra;
    }

    private JTabbedPane construirPestanas() {
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(pestanas.getFont().deriveFont(Font.BOLD, 13f));
        pestanas.setBackground(Color.WHITE);
        if (usuario instanceof funcionario funcionario) {
            pestanas.addTab("Reservas", new reservasPanel(funcionario));
            pestanas.addTab("Calendarizacion", new calendarizacionRecursosPanel());
            pestanas.addTab("Actividades", new actividadesPanel());
            pestanas.addTab("Estadisticas", new estadisticasPanel());
        } else {
            pestanas.addTab("Funcionarios", pantallaPendiente("Lista de funcionarios"));
            pestanas.addTab("Categorias", pantallaPendiente("Lista de categorias de recursos"));
            pestanas.addTab("Recursos", pantallaPendiente("Lista de recursos"));
            pestanas.addTab("Calendarizacion", new calendarizacionRecursosPanel());
            pestanas.addTab("Actividades", new actividadesPanel());
            pestanas.addTab("Estadisticas", new estadisticasPanel());
        }
        return pestanas;
    }

    private JPanel pantallaPendiente(String nombrePantalla) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        JLabel etiqueta = new JLabel("\"" + nombrePantalla + "\" se implementara en una siguiente iteracion.");
        etiqueta.setForeground(estilos.TEXTO_SECUNDARIO);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.ITALIC, 13f));
        panel.add(etiqueta);
        return panel;
    }

    private void cerrarSesion() {
        dispose();
        loginFrame login = new loginFrame();
        login.setVisible(true);
    }
}