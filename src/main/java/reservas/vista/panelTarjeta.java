package reservas.vista;

import javax.swing.*;
import java.awt.*;

public class panelTarjeta extends JPanel {

    private final Color colorAcento;

    public panelTarjeta(Color colorAcento) {
        this.colorAcento = colorAcento;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(22, 22, 20, 22));
    }

    public panelTarjeta() {
        this(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int ancho = Math.max(getWidth() - 4, 1);
        int alto = Math.max(getHeight() - 6, 1);
        g2.setColor(new Color(0x1c, 0x1b, 0x33, 24));
        g2.fillRoundRect(2, 5, ancho, alto, 22, 22);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, ancho, alto, 22, 22);
        if (colorAcento != null) {
            g2.setColor(colorAcento);
            g2.fillRoundRect(0, 0, ancho, 8, 22, 22);
            g2.fillRect(0, 4, ancho, 4);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}