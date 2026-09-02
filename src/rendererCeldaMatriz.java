package reservas.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class rendererCeldaMatriz extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado,
                                                   boolean conFoco, int fila, int columna) {
        JLabel etiqueta = (JLabel) super.getTableCellRendererComponent(tabla, valor, seleccionado, conFoco, fila, columna);
        etiqueta.setOpaque(true);
        etiqueta.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        etiqueta.setHorizontalAlignment(columna == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
        etiqueta.setFont(etiqueta.getFont().deriveFont(columna == 0 ? Font.BOLD : Font.PLAIN, 11f));
        etiqueta.setVerticalAlignment(SwingConstants.TOP);

        String texto = valor == null ? "" : String.valueOf(valor);
        boolean ocupado = columna > 0 && !texto.isBlank();
        etiqueta.setBackground(ocupado ? estilos.OCUPADO_FONDO : Color.WHITE);
        etiqueta.setForeground(ocupado ? estilos.OCUPADO_TEXTO : estilos.TEXTO_TITULO);

        if (ocupado) {
            String textoHtml = texto.replace(" - ", "<br>- ").replace(" (", "<br>(").replace(" / ", "<br>/ ");
            etiqueta.setText("<html><div style='width:100px'>" + textoHtml + "</div></html>");
        }
        return etiqueta;
    }
}