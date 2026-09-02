package reservas.vista;

import reservas.controller.controladorEstadisticas;
import reservas.modelo.categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class estadisticasPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_SEMANA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final controladorEstadisticas controlador = new controladorEstadisticas();

    private final JTextField campoDesdeRecursos = new JTextField();
    private final JTextField campoHastaRecursos = new JTextField();
    private final DefaultTableModel modeloTablaRecursos = new DefaultTableModel(new Object[]{"Categoria", "Cantidad"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tablaRecursos = new JTable(modeloTablaRecursos);
    private final graficoBarras graficoRecursos = new graficoBarras(estilos.AZUL);

    private final JTextField campoDesdeActividades = new JTextField();
    private final JTextField campoHastaActividades = new JTextField();
    private final DefaultTableModel modeloTablaActividades = new DefaultTableModel(new Object[]{"Semana", "Cantidad"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tablaActividades = new JTable(modeloTablaActividades);
    private final graficoBarras graficoActividades = new graficoBarras(estilos.ROSA);

    public estadisticasPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setBackground(estilos.FONDO_VENTANA);

        JPanel columnas = new JPanel(new GridLayout(1, 2, 16, 0));
        columnas.setOpaque(false);
        columnas.add(construirSeccionRecursos());
        columnas.add(construirSeccionActividades());
        add(columnas, BorderLayout.CENTER);

        campoDesdeRecursos.setText("01/07/2026");
        campoHastaRecursos.setText("31/08/2026");
        campoDesdeActividades.setText("01/07/2026");
        campoHastaActividades.setText("31/08/2026");

        cargarRecursos();
        cargarActividades();
    }

    private JPanel construirSeccionRecursos() {
        panelTarjeta tarjeta = new panelTarjeta(estilos.AZUL);
        tarjeta.setLayout(new BorderLayout(0, 10));

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Recursos");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        superior.add(titulo);
        superior.add(Box.createVerticalStrut(10));

        JPanel filaFechas = new JPanel(new GridLayout(1, 2, 10, 0));
        filaFechas.setOpaque(false);
        filaFechas.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaFechas.add(campoConEtiqueta("Fecha desde", campoDesdeRecursos));
        filaFechas.add(campoConEtiqueta("Fecha hasta", campoHastaRecursos));
        superior.add(filaFechas);
        superior.add(Box.createVerticalStrut(10));

        JButton botonCargar = estilos.botonExito("Cargar");
        JButton botonImprimir = estilos.botonSecundario("Imprimir reporte");
        botonCargar.addActionListener(e -> cargarRecursos());
        botonImprimir.addActionListener(e -> mostrarPendientePdf());
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaBotones.setOpaque(false);
        filaBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaBotones.add(botonCargar);
        filaBotones.add(botonImprimir);
        superior.add(filaBotones);
        superior.add(Box.createVerticalStrut(10));

        tablaRecursos.setRowHeight(26);
        tablaRecursos.setGridColor(estilos.BORDE);
        tablaRecursos.getTableHeader().setFont(tablaRecursos.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        tablaRecursos.getTableHeader().setBackground(new Color(0xf1, 0xf5, 0xf9));
        JScrollPane scrollTabla = new JScrollPane(tablaRecursos);
        scrollTabla.setBorder(BorderFactory.createLineBorder(estilos.BORDE, 1, true));
        scrollTabla.setPreferredSize(new Dimension(10, 100));
        scrollTabla.setAlignmentX(Component.LEFT_ALIGNMENT);
        superior.add(scrollTabla);

        tarjeta.add(superior, BorderLayout.NORTH);
        tarjeta.add(construirBloqueGrafico("Recursos Usados", "Recurso", estilos.AZUL, graficoRecursos), BorderLayout.CENTER);

        return tarjeta;
    }

    private JPanel construirSeccionActividades() {
        panelTarjeta tarjeta = new panelTarjeta(estilos.ROSA);
        tarjeta.setLayout(new BorderLayout(0, 10));

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Actividades");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        superior.add(titulo);
        superior.add(Box.createVerticalStrut(10));

        JPanel filaFechas = new JPanel(new GridLayout(1, 2, 10, 0));
        filaFechas.setOpaque(false);
        filaFechas.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaFechas.add(campoConEtiqueta("Fecha desde", campoDesdeActividades));
        filaFechas.add(campoConEtiqueta("Fecha hasta", campoHastaActividades));
        superior.add(filaFechas);
        superior.add(Box.createVerticalStrut(10));

        JButton botonCargar = estilos.botonExito("Cargar");
        JButton botonImprimir = estilos.botonSecundario("Imprimir reporte");
        botonCargar.addActionListener(e -> cargarActividades());
        botonImprimir.addActionListener(e -> mostrarPendientePdf());
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaBotones.setOpaque(false);
        filaBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaBotones.add(botonCargar);
        filaBotones.add(botonImprimir);
        superior.add(filaBotones);
        superior.add(Box.createVerticalStrut(10));

        tablaActividades.setRowHeight(26);
        tablaActividades.setGridColor(estilos.BORDE);
        tablaActividades.getTableHeader().setFont(tablaActividades.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        tablaActividades.getTableHeader().setBackground(new Color(0xf1, 0xf5, 0xf9));
        JScrollPane scrollTabla = new JScrollPane(tablaActividades);
        scrollTabla.setBorder(BorderFactory.createLineBorder(estilos.BORDE, 1, true));
        scrollTabla.setPreferredSize(new Dimension(10, 100));
        scrollTabla.setAlignmentX(Component.LEFT_ALIGNMENT);
        superior.add(scrollTabla);

        tarjeta.add(superior, BorderLayout.NORTH);
        tarjeta.add(construirBloqueGrafico("Actividades Realizadas", "Semana", estilos.ROSA, graficoActividades), BorderLayout.CENTER);

        return tarjeta;
    }

    private JPanel construirBloqueGrafico(String tituloTexto, String etiquetaLeyenda, Color color, graficoBarras grafico) {
        JPanel bloque = new JPanel(new BorderLayout(0, 4));
        bloque.setOpaque(false);

        JLabel titulo = new JLabel(tituloTexto, SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        bloque.add(titulo, BorderLayout.NORTH);

        bloque.add(grafico, BorderLayout.CENTER);
        bloque.add(construirLeyenda(etiquetaLeyenda, color), BorderLayout.SOUTH);

        return bloque;
    }

    private JLabel construirLeyenda(String texto, Color color) {
        JLabel etiqueta = new JLabel("■ " + texto, SwingConstants.CENTER);
        etiqueta.setForeground(color);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 12f));
        return etiqueta;
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

    private void mostrarPendientePdf() {
        JOptionPane.showMessageDialog(this,
                "La generacion de reporte en PDF queda pendiente para una siguiente iteracion.",
                "Pendiente", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarRecursos() {
        LocalDate desde;
        LocalDate hasta;
        try {
            desde = LocalDate.parse(campoDesdeRecursos.getText().trim(), FORMATO_FECHA);
            hasta = LocalDate.parse(campoHastaRecursos.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Alguna fecha de Recursos no es valida. Usa el formato dd/mm/aaaa.",
                    "Revisa el formulario", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (hasta.isBefore(desde)) {
            JOptionPane.showMessageDialog(this, "La fecha hasta no puede ser anterior a la fecha desde.",
                    "Revisa el formulario", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<categoria, Integer> conteo = controlador.reservasPorCategoria(desde, hasta);

        modeloTablaRecursos.setRowCount(0);
        List<String> etiquetas = new ArrayList<>();
        List<Integer> valores = new ArrayList<>();
        for (Map.Entry<categoria, Integer> entrada : conteo.entrySet()) {
            modeloTablaRecursos.addRow(new Object[]{entrada.getKey().getDescripcion(), entrada.getValue()});
            etiquetas.add(entrada.getKey().getDescripcion());
            valores.add(entrada.getValue());
        }
        graficoRecursos.setDatos(etiquetas, valores);
    }

    private void cargarActividades() {
        LocalDate desde;
        LocalDate hasta;
        try {
            desde = LocalDate.parse(campoDesdeActividades.getText().trim(), FORMATO_FECHA);
            hasta = LocalDate.parse(campoHastaActividades.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Alguna fecha de Actividades no es valida. Usa el formato dd/mm/aaaa.",
                    "Revisa el formulario", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (hasta.isBefore(desde)) {
            JOptionPane.showMessageDialog(this, "La fecha hasta no puede ser anterior a la fecha desde.",
                    "Revisa el formulario", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<LocalDate, Integer> conteo = controlador.actividadesPorSemana(desde, hasta);

        modeloTablaActividades.setRowCount(0);
        List<String> etiquetas = new ArrayList<>();
        List<Integer> valores = new ArrayList<>();
        for (Map.Entry<LocalDate, Integer> entrada : conteo.entrySet()) {
            String etiquetaSemana = entrada.getKey().format(FORMATO_SEMANA);
            modeloTablaActividades.addRow(new Object[]{etiquetaSemana, entrada.getValue()});
            etiquetas.add(etiquetaSemana);
            valores.add(entrada.getValue());
        }
        graficoActividades.setDatos(etiquetas, valores);
    }
}