package reservas.vista;

import reservas.controller.controladorReservas;
import reservas.modelo.categoria;
import reservas.modelo.funcionario;
import reservas.modelo.recurso;
import reservas.modelo.reserva;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class reservasPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final controladorReservas controlador = new controladorReservas();
    private final funcionario funcionario;

    private final JTextField campoActividad = new JTextField();
    private final JTextField campoFecha = new JTextField();
    private final JComboBox<LocalTime> comboHoraInicio = new JComboBox<>(horasDelDia());
    private final JComboBox<LocalTime> comboHoraFin = new JComboBox<>(horasDelDia());
    private final JList<categoria> listaCategorias =
            new JList<>(controlador.listarCategorias().toArray(new categoria[0]));

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Id", "Actividad", "Fecha", "Horario", "Recursos", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tablaReservas = new JTable(modeloTabla);
    private List<reserva> reservasMostradas = new ArrayList<>();

    public reservasPanel(funcionario funcionario) {
        this.funcionario = funcionario;
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setBackground(estilos.FONDO_VENTANA);

        add(construirTarjetaFormulario(), BorderLayout.NORTH);
        add(construirTarjetaTabla(), BorderLayout.CENTER);

        cargarTabla();
    }

    private static LocalTime[] horasDelDia() {
        List<LocalTime> horas = new ArrayList<>();
        for (int h = 6; h <= 20; h++) {
            horas.add(LocalTime.of(h, 0));
        }
        return horas.toArray(new LocalTime[0]);
    }

    private JPanel construirTarjetaFormulario() {
        panelTarjeta tarjeta = new panelTarjeta(estilos.AZUL);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Nueva reserva");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(14));

        campoFecha.setText(LocalDate.now().format(FORMATO_FECHA));

        JPanel filaCampos = new JPanel(new GridLayout(1, 4, 10, 0));
        filaCampos.setOpaque(false);
        filaCampos.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaCampos.add(campoConEtiqueta("Actividad", campoActividad));
        filaCampos.add(campoConEtiqueta("Fecha (dd/mm/aaaa)", campoFecha));
        filaCampos.add(campoConEtiqueta("Hora inicio", comboHoraInicio));
        filaCampos.add(campoConEtiqueta("Hora fin", comboHoraFin));
        tarjeta.add(filaCampos);
        tarjeta.add(Box.createVerticalStrut(14));

        JLabel etiquetaCategorias = estilos.etiquetaTitulo("Categorias requeridas (Ctrl/Cmd + clic para elegir varias)");
        etiquetaCategorias.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(etiquetaCategorias);
        tarjeta.add(Box.createVerticalStrut(4));

        listaCategorias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaCategorias.setVisibleRowCount(3);
        listaCategorias.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        listaCategorias.setSelectionBackground(estilos.AZUL);
        listaCategorias.setSelectionForeground(Color.WHITE);
        JScrollPane scrollCategorias = new JScrollPane(listaCategorias);
        scrollCategorias.setBorder(BorderFactory.createLineBorder(estilos.BORDE, 1, true));
        scrollCategorias.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollCategorias.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        tarjeta.add(scrollCategorias);
        tarjeta.add(Box.createVerticalStrut(4));

        JLabel pista = new JLabel("Si alguna categoria no tiene disponibilidad, el sistema te lo va a indicar para que ajustes la reserva.");
        pista.setForeground(estilos.TEXTO_SECUNDARIO);
        pista.setFont(pista.getFont().deriveFont(11f));
        pista.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(pista);
        tarjeta.add(Box.createVerticalStrut(14));

        JButton botonReservar = estilos.botonPrimario("Reservar");
        JButton botonCancelarReserva = estilos.botonPeligro("Cancelar reserva seleccionada");
        JButton botonLimpiar = estilos.botonSecundario("Limpiar");
        botonReservar.addActionListener(e -> accionReservar());
        botonCancelarReserva.addActionListener(e -> accionCancelar());
        botonLimpiar.addActionListener(e -> limpiarFormulario());

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaBotones.setOpaque(false);
        filaBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaBotones.add(botonReservar);
        filaBotones.add(botonCancelarReserva);
        filaBotones.add(botonLimpiar);
        tarjeta.add(filaBotones);

        return tarjeta;
    }

    private JPanel construirTarjetaTabla() {
        panelTarjeta tarjeta = new panelTarjeta(estilos.TEAL);
        tarjeta.setLayout(new BorderLayout(0, 10));

        JLabel titulo = new JLabel("Mis reservas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        tarjeta.add(titulo, BorderLayout.NORTH);

        tablaReservas.setRowHeight(30);
        tablaReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaReservas.setSelectionBackground(new Color(0xdb, 0xea, 0xfe));
        tablaReservas.setSelectionForeground(estilos.TEXTO_TITULO);
        tablaReservas.setGridColor(estilos.BORDE);
        tablaReservas.setShowVerticalLines(false);
        tablaReservas.getTableHeader().setFont(tablaReservas.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        tablaReservas.getTableHeader().setBackground(new Color(0xf1, 0xf5, 0xf9));
        tablaReservas.getColumnModel().getColumn(5).setCellRenderer(new rendererEstado());
        JScrollPane scrollTabla = new JScrollPane(tablaReservas);
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
        selector.setSelectedFile(new File("reporte_reservas.pdf"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".pdf");
        }
        try {
            generadorPdf.generarReporte(archivo.toPath(), "Reporte de Reservas - " + funcionario.getId(), tablaReservas);
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
        JLabel jLabel = estilos.etiquetaTitulo(etiqueta);
        panel.add(jLabel, BorderLayout.NORTH);
        if (campo instanceof JTextField campoTexto) {
            campoTexto.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(estilos.BORDE, 1, true),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        }
        panel.add(campo, BorderLayout.CENTER);
        return panel;
    }

    private void accionReservar() {
        String actividad = campoActividad.getText().trim();
        if (actividad.isEmpty()) {
            mostrarError("Ingresa la actividad.");
            return;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(campoFecha.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            mostrarError("La fecha no es valida. Usa el formato dd/mm/aaaa.");
            return;
        }
        if (fecha.isBefore(LocalDate.now())) {
            mostrarError("La fecha debe ser hoy o una fecha futura.");
            return;
        }

        LocalTime horaInicio = (LocalTime) comboHoraInicio.getSelectedItem();
        LocalTime horaFin = (LocalTime) comboHoraFin.getSelectedItem();
        if (horaInicio == null || horaFin == null || !horaInicio.isBefore(horaFin)) {
            mostrarError("La hora de inicio debe ser antes que la hora fin.");
            return;
        }

        List<categoria> categoriasSeleccionadas = listaCategorias.getSelectedValuesList();
        if (categoriasSeleccionadas.isEmpty()) {
            mostrarError("Selecciona al menos una categoria de recurso.");
            return;
        }

        controladorReservas.resultadoReserva resultado = controlador.crearReserva(
                funcionario, actividad, fecha, horaInicio, horaFin, categoriasSeleccionadas);

        if (!resultado.categoriasSinDisponibilidad.isEmpty()) {
            String nombres = resultado.categoriasSinDisponibilidad.stream()
                    .map(categoria::getDescripcion)
                    .collect(Collectors.joining(", "));
            JOptionPane.showMessageDialog(this,
                    "No hay disponibilidad para: " + nombres + ".\n"
                            + "Ajusta la fecha, el horario o las categorias e intenta de nuevo.",
                    "Sin disponibilidad", JOptionPane.WARNING_MESSAGE);
            return;
        }

        reserva nueva = resultado.reservaCreada;
        String idsRecursos = nueva.getRecursosAsignados().stream().map(recurso::getId).collect(Collectors.joining(", "));
        JOptionPane.showMessageDialog(this,
                "Reserva " + nueva.getId() + " creada con exito.\nRecursos asignados: " + idsRecursos,
                "Reserva creada", JOptionPane.INFORMATION_MESSAGE);

        limpiarFormulario();
        cargarTabla();
    }

    private void accionCancelar() {
        int fila = tablaReservas.getSelectedRow();
        if (fila == -1) {
            mostrarError("Selecciona una reserva de la tabla para cancelarla.");
            return;
        }
        reserva seleccionada = reservasMostradas.get(fila);

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "Cancelar la reserva " + seleccionada.getId() + "? Se liberaran sus recursos.",
                "Confirmar cancelacion", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controlador.cancelarReserva(seleccionada);
        } catch (IllegalStateException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        cargarTabla();
    }

    private void limpiarFormulario() {
        campoActividad.setText("");
        campoFecha.setText(LocalDate.now().format(FORMATO_FECHA));
        comboHoraInicio.setSelectedIndex(0);
        comboHoraFin.setSelectedIndex(0);
        listaCategorias.clearSelection();
        mostrarError(" ");
    }

    private void mostrarError(String mensaje) {
        if (!mensaje.isBlank()) {
            JOptionPane.showMessageDialog(this, mensaje, "Revisa el formulario", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        reservasMostradas = controlador.listarReservasDe(funcionario);
        for (reserva r : reservasMostradas) {
            String horario = r.getHoraInicio() + " - " + r.getHoraFin();
            String recursosTexto = r.getRecursosAsignados().stream()
                    .map(recurso::getId)
                    .collect(Collectors.joining(", "));
            modeloTabla.addRow(new Object[]{
                    r.getId(), r.getActividad(), r.getFecha().format(FORMATO_FECHA),
                    horario, recursosTexto, r.getEstado()
            });
        }
    }

    private static class etiquetaPill extends JLabel {
        etiquetaPill(String texto, Color fondo, Color colorTexto) {
            super(texto, SwingConstants.CENTER);
            setOpaque(false);
            setForeground(colorTexto);
            setFont(getFont().deriveFont(Font.BOLD, 11f));
            setBackground(fondo);
            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            int alto = Math.max(getHeight(), 1);
            g2.fillRoundRect(0, 0, Math.max(getWidth(), 1), alto, alto, alto);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class rendererEstado extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado,
                                                       boolean conFoco, int fila, int columna) {
            boolean activa = "ACTIVA".equals(String.valueOf(valor));
            JPanel envoltorio = new JPanel(new GridBagLayout());
            envoltorio.setOpaque(seleccionado);
            if (seleccionado) {
                envoltorio.setBackground(tabla.getSelectionBackground());
            }
            etiquetaPill etiqueta = new etiquetaPill(String.valueOf(valor),
                    activa ? estilos.ACTIVA_FONDO : estilos.CANCELADA_FONDO,
                    activa ? estilos.ACTIVA_TEXTO : estilos.CANCELADA_TEXTO);
            envoltorio.add(etiqueta);
            return envoltorio;
        }
    }
}