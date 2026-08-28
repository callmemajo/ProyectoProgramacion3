package reservas.ui;

import reservas.datos.almacenDatos;
import reservas.modelo.reserva;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class actividadesPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_CORTA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String[] ABREVIATURAS_DIA = {"lun", "mar", "mie", "jue", "vie"};
    private static final int DIAS_SEMANA = ABREVIATURAS_DIA.length;

    private final JTextField campoFechaReferencia = new JTextField();
    private final DefaultTableModel modeloTabla = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    public actividadesPanel() {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setBackground(estilos.FONDO_VENTANA);

        add(construirTarjetaFiltros(), BorderLayout.NORTH);
        add(construirTarjetaTabla(), BorderLayout.CENTER);

        campoFechaReferencia.setText(LocalDate.now().format(FORMATO_FECHA));
        cargarMatriz();
    }

    private JPanel construirTarjetaFiltros() {
        panelTarjeta tarjeta = new panelTarjeta(estilos.NARANJA);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Semana");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(14));

        JPanel filaCampos = new JPanel(new GridLayout(1, 4, 10, 0));
        filaCampos.setOpaque(false);
        filaCampos.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaCampos.add(campoConEtiqueta("Fecha de referencia (dd/mm/aaaa)", campoFechaReferencia));
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
        panelTarjeta tarjeta = new panelTarjeta(estilos.NARANJA);
        tarjeta.setLayout(new BorderLayout(0, 10));

        JLabel titulo = new JLabel("Actividades semanales");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setForeground(estilos.TEXTO_TITULO);
        tarjeta.add(titulo, BorderLayout.NORTH);

        tabla.setRowHeight(54);
        tabla.setGridColor(estilos.BORDE);
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD, 11f));
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
        LocalDate fechaReferencia;
        try {
            fechaReferencia = LocalDate.parse(campoFechaReferencia.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "La fecha no es valida. Usa el formato dd/mm/aaaa.",
                    "Revisa el formulario", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate lunes = fechaReferencia.minusDays(fechaReferencia.getDayOfWeek().getValue() - 1);
        List<LocalDate> diasSemana = new ArrayList<>();
        for (int i = 0; i < DIAS_SEMANA; i++) {
            diasSemana.add(lunes.plusDays(i));
        }

        String[] columnas = new String[DIAS_SEMANA + 1];
        columnas[0] = "Hora";
        for (int i = 0; i < DIAS_SEMANA; i++) {
            LocalDate dia = diasSemana.get(i);
            columnas[i + 1] = ABREVIATURAS_DIA[i] + " " + dia.format(FORMATO_FECHA_CORTA);
        }
        modeloTabla.setColumnIdentifiers(columnas);
        if (tabla.getColumnModel().getColumnCount() > 0) {
            tabla.getColumnModel().getColumn(0).setPreferredWidth(56);
            tabla.getColumnModel().getColumn(0).setMaxWidth(56);
        }
        modeloTabla.setRowCount(0);

        for (LocalTime hora : horasDelDia()) {
            Object[] fila = new Object[DIAS_SEMANA + 1];
            fila[0] = hora.toString();
            for (int i = 0; i < DIAS_SEMANA; i++) {
                fila[i + 1] = textoCelda(diasSemana.get(i), hora);
            }
            modeloTabla.addRow(fila);
        }
    }

    private String textoCelda(LocalDate fecha, LocalTime hora) {
        List<String> coincidencias = new ArrayList<>();
        for (reserva r : almacenDatos.reservas) {
            if (!r.estaActiva()) {
                continue;
            }
            if (!r.getFecha().equals(fecha)) {
                continue;
            }
            boolean dentroDelRango = !hora.isBefore(r.getHoraInicio()) && hora.isBefore(r.getHoraFin());
            if (dentroDelRango) {
                coincidencias.add(r.getActividad() + " (" + r.getFuncionario().getNombreMostrar() + ")");
            }
        }
        return String.join(" / ", coincidencias);
    }
}