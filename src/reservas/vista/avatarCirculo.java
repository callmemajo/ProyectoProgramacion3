package reservas.vista;

import javax.swing.*;
import java.awt.*;

public class avatarCirculo extends JComponent {

    private final String letra;
    private final Color colorFondo;
    private final int diametro;

    public avatarCirculo(String letra, Color colorFondo, int diametro) {
        this.letra = letra;
        this.colorFondo = colorFondo;
        this.diametro = diametro;
        setOpaque(false);
        setPreferredSize(new Dimension(diametro, diametro));
        setMaximumSize(new Dimension(diametro, diametro));
        setMinimumSize(new Dimension(diametro, diametro));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(colorFondo);
        g2.fillOval(0, 0, diametro, diametro);
        g2.setColor(Color.WHITE);
        g2.setFont(getFont().deriveFont(Font.BOLD, diametro * 0.42f));
        FontMetrics fm = g2.getFontMetrics();
        int x = (diametro - fm.stringWidth(letra)) / 2;
        int y = (diametro - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(letra, x, y);
        g2.dispose();
    }
}