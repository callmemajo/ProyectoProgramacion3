package reservas.vista;

import reservas.controller.controladorFuncionarios;
import reservas.modelo.funcionario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class funcionariosPanel extends JPanel {

    private final controladorFuncionarios controlador = new controladorFuncionarios();

    private final JTextField campoId = new JTextField();
    private final JTextField campoClave = new JTextField();
    private final JTextField campoNombre = new JTextField();
    private final JTextField campoTelefono = new JTextField();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Id", "Nombre", "Telefono"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tablaFuncionarios = new JTable(modeloTabla);
    private List<funcionario> funcionariosMostrados;

    public funcionariosPanel() {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setBackground(estilos.FONDO_VENTANA);

        add(construirTarjetaFormulario(), BorderLayout.NORTH);
        add(construirTarjetaTabla(), BorderLayout.CENTER);

        tablaFuncionarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });

        cargarTabla();
    }

    private JPanel construirTarjetaFormulario() {
        panelTarjeta tarjeta = new panelTarjeta(estilos.AZUL);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Funcionario");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(14));

        JPanel filaCampos = new JPanel(new GridLayout(1, 4, 10, 0));
        filaCampos.setOpaque(false);
        filaCampos.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaCampos.add(campoConEtiqueta("Id", campoId));
        filaCampos.add(campoConEtiqueta("Clave (solo para crear)", campoClave));
        filaCampos.add(campoConEtiqueta("Nombre", campoNombre));
        filaCampos.add(campoConEtiqueta("Telefono", campoTelefono));
        tarjeta.add(filaCampos);
        tarjeta.add(Box.createVerticalStrut(4));

        JLabel pista = new JLabel("Selecciona una fila de la tabla para editarla o eliminarla. La clave solo se usa al crear.");
        pista.setForeground(estilos.TEXTO_SECUNDARIO);
        pista.setFont(pista.getFont().deriveFont(11f));
        pista.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(pista);
        tarjeta.add(Box.createVerticalStrut(14));

        JButton botonCrear = estilos.botonPrimario("Crear funcionario");
        JButton botonGuardar = estilos.botonExito("Guardar cambios");
        JButton botonEliminar = estilos.botonPeligro("Eliminar seleccionado");
        JButton botonLimpiar = estilos.botonSecundario("Limpiar");
        botonCrear.addActionListener(e -> accionCrear());
        botonGuardar.addActionListener(e -> accionGuardar());
        botonEliminar.addActionListener(e -> accionEliminar());
        botonLimpiar.addActionListener(e -> limpiarFormulario());

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaBotones.setOpaque(false);
        filaBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaBotones.add(botonCrear);
        filaBotones.add(botonGuardar);
        filaBotones.add(botonEliminar);
        filaBotones.add(botonLimpiar);
        tarjeta.add(filaBotones);

        return tarjeta;
    }

    private JPanel construirTarjetaTabla() {
        panelTarjeta tarjeta = new panelTarjeta(estilos.TEAL);
        tarjeta.setLayout(new BorderLayout(0, 10));

        JLabel titulo = new JLabel("Funcionarios registrados");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        tarjeta.add(titulo, BorderLayout.NORTH);

        tablaFuncionarios.setRowHeight(30);
        tablaFuncionarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaFuncionarios.setSelectionBackground(new Color(0xdb, 0xea, 0xfe));
        tablaFuncionarios.setSelectionForeground(estilos.TEXTO_TITULO);
        tablaFuncionarios.setGridColor(estilos.BORDE);
        tablaFuncionarios.setShowVerticalLines(false);
        tablaFuncionarios.getTableHeader().setFont(tablaFuncionarios.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        tablaFuncionarios.getTableHeader().setBackground(new Color(0xf1, 0xf5, 0xf9));
        JScrollPane scrollTabla = new JScrollPane(tablaFuncionarios);
        scrollTabla.setBorder(BorderFactory.createLineBorder(estilos.BORDE, 1, true));
        tarjeta.add(scrollTabla, BorderLayout.CENTER);

        JButton botonImprimir = estilos.botonSecundario("Imprimir reporte");
        botonImprimir.addActionListener(e -> accionImprimirReporte());
        JPanel filaAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filaAcciones.setOpaque(false);
        filaAcciones.add(botonImprimir);
        tarjeta.add(filaAcciones, BorderLayout.SOUTH);

        return tarjeta;
    }

    private void accionImprimirReporte() {
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File("reporte_funcionarios.pdf"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".pdf");
        }
        try {
            generadorPdf.generarReporte(archivo.toPath(), "Reporte de Funcionarios", tablaFuncionarios);
            JOptionPane.showMessageDialog(this, "Reporte generado en " + archivo.getAbsolutePath() + ".",
                    "Reporte generado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el reporte: " + ex.getMessage(),
                    "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JPanel campoConEtiqueta(String etiqueta, JComponent campo) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.add(estilos.etiquetaTitulo(etiqueta), BorderLayout.NORTH);
        if (campo instanceof JTextField campoTexto) {
            campoTexto.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(estilos.BORDE, 1, true),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        }
        panel.add(campo, BorderLayout.CENTER);
        return panel;
    }

    private void accionCrear() {
        String id = campoId.getText().trim();
        String clave = campoClave.getText().trim();
        String nombre = campoNombre.getText().trim();
        String telefono = campoTelefono.getText().trim();

        try {
            controlador.crearFuncionario(id, clave, nombre, telefono);
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Funcionario " + id + " creado con exito.",
                "Funcionario creado", JOptionPane.INFORMATION_MESSAGE);
        limpiarFormulario();
        cargarTabla();
    }

    private void accionGuardar() {
        funcionario seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            mostrarError("Selecciona un funcionario de la tabla para editarlo.");
            return;
        }

        String nombre = campoNombre.getText().trim();
        String telefono = campoTelefono.getText().trim();

        try {
            controlador.editarFuncionario(seleccionado, nombre, telefono);
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Cambios guardados para " + seleccionado.getId() + ".",
                "Funcionario actualizado", JOptionPane.INFORMATION_MESSAGE);
        limpiarFormulario();
        cargarTabla();
    }

    private void accionEliminar() {
        funcionario seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            mostrarError("Selecciona un funcionario de la tabla para eliminarlo.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "Eliminar al funcionario " + seleccionado.getId() + "?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controlador.eliminarFuncionario(seleccionado);
        } catch (IllegalStateException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        limpiarFormulario();
        cargarTabla();
    }

    private funcionario obtenerSeleccionado() {
        int fila = tablaFuncionarios.getSelectedRow();
        if (fila == -1 || funcionariosMostrados == null || fila >= funcionariosMostrados.size()) {
            return null;
        }
        return funcionariosMostrados.get(fila);
    }

    private void cargarSeleccionEnFormulario() {
        funcionario seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            return;
        }
        campoId.setText(seleccionado.getId());
        campoClave.setText("");
        campoNombre.setText(seleccionado.getNombre());
        campoTelefono.setText(seleccionado.getTelefono());
        campoId.setEditable(false);
        campoClave.setEditable(false);
    }

    private void limpiarFormulario() {
        tablaFuncionarios.clearSelection();
        campoId.setText("");
        campoClave.setText("");
        campoNombre.setText("");
        campoTelefono.setText("");
        campoId.setEditable(true);
        campoClave.setEditable(true);
    }

    private void mostrarError(String mensaje) {
        if (!mensaje.isBlank()) {
            JOptionPane.showMessageDialog(this, mensaje, "Revisa el formulario", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        funcionariosMostrados = controlador.listarFuncionarios();
        for (funcionario f : funcionariosMostrados) {
            modeloTabla.addRow(new Object[]{f.getId(), f.getNombre(), f.getTelefono()});
        }
    }
}