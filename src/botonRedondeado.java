package reservas.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class botonRedondeado extends JButton {

    private final Color colorFondoNormal;
    private final Color colorFondoHover;
    private final Color colorBorde;
    private final boolean relleno;

    public botonRedondeado(String texto, Color colorFondo, Color colorTexto, Color colorBorde, boolean relleno) {
        super(texto);
        this.colorFondoNormal = colorFondo;
        this.colorBorde = colorBorde;
        this.relleno = relleno;
        if (relleno) {
            this.colorFondoHover = ajustar(colorFondo, -22);
        } else if (colorFondo.getAlpha() < 255) {
            this.colorFondoHover = new Color(255, 255, 255, Math.min(255, colorFondo.getAlpha() + 55));
        } else {
            this.colorFondoHover = mezclarConBlanco(colorBorde != null ? colorBorde : colorTexto, 0.16);
        }
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(colorTexto);
        setFont(getFont().deriveFont(Font.BOLD, 13f));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        setBackground(colorFondo);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(colorFondoHover);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(colorFondoNormal);
                repaint();
            }
        });
    }

    private static Color ajustar(Color color, int cantidad) {
        return new Color(limitar(color.getRed() + cantidad), limitar(color.getGreen() + cantidad), limitar(color.getBlue() + cantidad));
    }

    private static Color mezclarConBlanco(Color color, double proporcion) {
        int r = (int) (255 - (255 - color.getRed()) * proporcion);
        int g = (int) (255 - (255 - color.getGreen()) * proporcion);
        int b = (int) (255 - (255 - color.getBlue()) * proporcion);
        return new Color(limitar(r), limitar(g), limitar(b));
    }

    private static int limitar(int valor) {
        return Math.max(0, Math.min(255, valor));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, Math.max(getWidth() - 1, 1), Math.max(getHeight() - 1, 1), 16, 16);
        if (!relleno && colorBorde != null) {
            g2.setColor(colorBorde);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(1, 1, Math.max(getWidth() - 3, 1), Math.max(getHeight() - 3, 1), 16, 16);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}