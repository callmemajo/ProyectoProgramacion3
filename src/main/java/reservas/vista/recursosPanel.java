package reservas.vista;

import reservas.controller.controladorRecursos;
import reservas.modelo.categoria;
import reservas.modelo.recurso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class recursosPanel extends JPanel {

    private final controladorRecursos controlador = new controladorRecursos();

    private final JTextField campoId = new JTextField();
    private final JComboBox<categoria> comboCategoria =
            new JComboBox<>(controlador.listarCategorias().toArray(new categoria[0]));
    private final JTextField campoDescripcion = new JTextField();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Id", "Categoria", "Descripcion"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tablaRecursos = new JTable(modeloTabla);
    private List<recurso> recursosMostrados;

    public recursosPanel() {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setBackground(estilos.FONDO_VENTANA);

        add(construirTarjetaFormulario(), BorderLayout.NORTH);
        add(construirTarjetaTabla(), BorderLayout.CENTER);

        tablaRecursos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });

        cargarTabla();
    }

    private JPanel construirTarjetaFormulario() {
        panelTarjeta tarjeta = new panelTarjeta(estilos.AZUL);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Recurso");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(14));

        JPanel filaCampos = new JPanel(new GridLayout(1, 3, 10, 0));
        filaCampos.setOpaque(false);
        filaCampos.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaCampos.add(campoConEtiqueta("Id", campoId));
        filaCampos.add(campoConEtiqueta("Categoria", comboCategoria));
        filaCampos.add(campoConEtiqueta("Descripcion", campoDescripcion));
        tarjeta.add(filaCampos);
        tarjeta.add(Box.createVerticalStrut(4));

        JLabel pista = new JLabel("Selecciona una fila de la tabla para editarla o eliminarla.");
        pista.setForeground(estilos.TEXTO_SECUNDARIO);
        pista.setFont(pista.getFont().deriveFont(11f));
        pista.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(pista);
        tarjeta.add(Box.createVerticalStrut(14));

        JButton botonCrear = estilos.botonPrimario("Crear recurso");
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

        JLabel titulo = new JLabel("Recursos registrados");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        tarjeta.add(titulo, BorderLayout.NORTH);

        tablaRecursos.setRowHeight(30);
        tablaRecursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaRecursos.setSelectionBackground(new Color(0xdb, 0xea, 0xfe));
        tablaRecursos.setSelectionForeground(estilos.TEXTO_TITULO);
        tablaRecursos.setGridColor(estilos.BORDE);
        tablaRecursos.setShowVerticalLines(false);
        tablaRecursos.getTableHeader().setFont(tablaRecursos.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        tablaRecursos.getTableHeader().setBackground(new Color(0xf1, 0xf5, 0xf9));
        JScrollPane scrollTabla = new JScrollPane(tablaRecursos);
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
        selector.setSelectedFile(new File("reporte_recursos.pdf"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".pdf");
        }
        try {
            generadorPdf.generarReporte(archivo.toPath(), "Reporte de Recursos", tablaRecursos);
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
        categoria categoriaSeleccionada = (categoria) comboCategoria.getSelectedItem();
        String descripcion = campoDescripcion.getText().trim();

        try {
            controlador.crearRecurso(id, categoriaSeleccionada, descripcion);
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Recurso " + id + " creado con exito.",
                "Recurso creado", JOptionPane.INFORMATION_MESSAGE);
        limpiarFormulario();
        cargarTabla();
    }

    private void accionGuardar() {
        recurso seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            mostrarError("Selecciona un recurso de la tabla para editarlo.");
            return;
        }

        categoria categoriaSeleccionada = (categoria) comboCategoria.getSelectedItem();
        String descripcion = campoDescripcion.getText().trim();

        try {
            controlador.editarRecurso(seleccionado, categoriaSeleccionada, descripcion);
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Cambios guardados para " + seleccionado.getId() + ".",
                "Recurso actualizado", JOptionPane.INFORMATION_MESSAGE);
        limpiarFormulario();
        cargarTabla();
    }

    private void accionEliminar() {
        recurso seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            mostrarError("Selecciona un recurso de la tabla para eliminarlo.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "Eliminar el recurso " + seleccionado.getId() + "?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controlador.eliminarRecurso(seleccionado);
        } catch (IllegalStateException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        limpiarFormulario();
        cargarTabla();
    }

    private recurso obtenerSeleccionado() {
        int fila = tablaRecursos.getSelectedRow();
        if (fila == -1 || recursosMostrados == null || fila >= recursosMostrados.size()) {
            return null;
        }
        return recursosMostrados.get(fila);
    }

    private void cargarSeleccionEnFormulario() {
        recurso seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            return;
        }
        campoId.setText(seleccionado.getId());
        comboCategoria.setSelectedItem(seleccionado.getCategoria());
        campoDescripcion.setText(seleccionado.getDescripcion());
        campoId.setEditable(false);
    }

    private void limpiarFormulario() {
        tablaRecursos.clearSelection();
        campoId.setText("");
        if (comboCategoria.getItemCount() > 0) {
            comboCategoria.setSelectedIndex(0);
        }
        campoDescripcion.setText("");
        campoId.setEditable(true);
    }

    private void mostrarError(String mensaje) {
        if (!mensaje.isBlank()) {
            JOptionPane.showMessageDialog(this, mensaje, "Revisa el formulario", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        recursosMostrados = controlador.listarRecursos();
        for (recurso r : recursosMostrados) {
            modeloTabla.addRow(new Object[]{r.getId(), r.getCategoria().getDescripcion(), r.getDescripcion()});
        }
    }
}