package reservas.vista;

import reservas.controller.controladorCalendarizacion;
import reservas.modelo.categoria;
import reservas.modelo.recurso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class calendarizacionRecursosPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final controladorCalendarizacion controlador = new controladorCalendarizacion();

    private final JTextField campoFecha = new JTextField();
    private final JComboBox<categoria> comboCategoria =
            new JComboBox<>(controlador.listarCategorias().toArray(new categoria[0]));
    private final DefaultTableModel modeloTabla = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    public calendarizacionRecursosPanel() {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setBackground(estilos.FONDO_VENTANA);

        add(construirTarjetaFiltros(), BorderLayout.NORTH);
        add(construirTarjetaTabla(), BorderLayout.CENTER);

        campoFecha.setText(LocalDate.now().format(FORMATO_FECHA));
        cargarMatriz();
    }

    private JPanel construirTarjetaFiltros() {
        panelTarjeta tarjeta = new panelTarjeta(estilos.MORADO);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Filtros");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(14));

        JPanel filaCampos = new JPanel(new GridLayout(1, 4, 10, 0));
        filaCampos.setOpaque(false);
        filaCampos.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaCampos.add(campoConEtiqueta("Fecha (dd/mm/aaaa)", campoFecha));
        filaCampos.add(campoConEtiqueta("Categoria", comboCategoria));
        tarjeta.add(filaCampos);
        tarjeta.add(Box.createVerticalStrut(14));

        JButton botonCargar = estilos.botonExito("Cargar");
        JButton botonImprimir = estilos.botonSecundario("Imprimir reporte");
        botonCargar.addActionListener(e -> cargarMatriz());
        botonImprimir.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "La generacion de reporte en PDF queda pendiente para una siguiente iteracion.",
                "Pendiente", JOptionPane.INFORMATION_MESSAGE));
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaBotones.setOpaque(false);
        filaBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaBotones.add(botonCargar);
        filaBotones.add(botonImprimir);
        tarjeta.add(filaBotones);

        return tarjeta;
    }

    private JPanel construirTarjetaTabla() {
        panelTarjeta tarjeta = new panelTarjeta(estilos.MORADO);
        tarjeta.setLayout(new BorderLayout(0, 10));

        JLabel titulo = new JLabel("Calendarizacion de recursos");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        tarjeta.add(titulo, BorderLayout.NORTH);

        tabla.setRowHeight(46);
        tabla.setGridColor(estilos.BORDE);
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        tabla.getTableHeader().setBackground(new Color(0xf1, 0xf5, 0xf9));
        tabla.setDefaultRenderer(Object.class, new rendererCeldaMatriz());
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(estilos.BORDE, 1, true));
        tarjeta.add(scroll, BorderLayout.CENTER);

        return tarjeta;
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

    private static List<LocalTime> horasDelDia() {
        List<LocalTime> horas = new ArrayList<>();
        for (int h = 6; h <= 20; h++) {
            horas.add(LocalTime.of(h, 0));
        }
        return horas;
    }

    private void cargarMatriz() {
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(campoFecha.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "La fecha no es valida. Usa el formato dd/mm/aaaa.",
                    "Revisa el formulario", JOptionPane.WARNING_MESSAGE);
            return;
        }
        categoria categoriaSeleccionada = (categoria) comboCategoria.getSelectedItem();
        if (categoriaSeleccionada == null) {
            return;
        }

        List<recurso> recursosDeCategoria = controlador.recursosDeCategoria(categoriaSeleccionada);

        String[] columnas = new String[recursosDeCategoria.size() + 1];
        columnas[0] = "Hora";
        for (int i = 0; i < recursosDeCategoria.size(); i++) {
            columnas[i + 1] = recursosDeCategoria.get(i).getDescripcion();
        }
        modeloTabla.setColumnIdentifiers(columnas);
        if (tabla.getColumnModel().getColumnCount() > 0) {
            tabla.getColumnModel().getColumn(0).setPreferredWidth(56);
            tabla.getColumnModel().getColumn(0).setMaxWidth(56);
        }
        modeloTabla.setRowCount(0);

        for (LocalTime hora : horasDelDia()) {
            Object[] fila = new Object[recursosDeCategoria.size() + 1];
            fila[0] = hora.toString();
            for (int i = 0; i < recursosDeCategoria.size(); i++) {
                fila[i + 1] = controlador.textoCelda(recursosDeCategoria.get(i), fecha, hora);
            }
            modeloTabla.addRow(fila);
        }
    }
}