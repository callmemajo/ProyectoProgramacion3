package reservas.vista;

import javax.swing.*;
import java.awt.*;

public class panelGradiente extends JPanel {

    private final Color colorInicio;
    private final Color colorFin;
    private final boolean horizontal;

    public panelGradiente(Color colorInicio, Color colorFin, boolean horizontal) {
        this.colorInicio = colorInicio;
        this.colorFin = colorFin;
        this.horizontal = horizontal;
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        GradientPaint gradiente = horizontal
                ? new GradientPaint(0, 0, colorInicio, getWidth(), 0, colorFin)
                : new GradientPaint(0, 0, colorInicio, getWidth(), getHeight(), colorFin);
        g2.setPaint(gradiente);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}