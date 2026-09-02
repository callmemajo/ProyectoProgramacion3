package reservas.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class graficoBarras extends JPanel {

    private List<String> etiquetas = new ArrayList<>();
    private List<Integer> valores = new ArrayList<>();
    private final Color colorBarra;

    public graficoBarras(Color colorBarra) {
        this.colorBarra = colorBarra;
        setOpaque(false);
        setPreferredSize(new Dimension(400, 220));
    }

    public void setDatos(List<String> etiquetasNuevas, List<Integer> valoresNuevos) {
        this.etiquetas = etiquetasNuevas;
        this.valores = valoresNuevos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        if (etiquetas.isEmpty()) {
            g2.setColor(estilos.TEXTO_SECUNDARIO);
            g2.setFont(getFont().deriveFont(Font.ITALIC, 13f));
            String mensaje = "No hay datos para el rango seleccionado.";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(mensaje, (ancho - fm.stringWidth(mensaje)) / 2, alto / 2);
            g2.dispose();
            return;
        }

        int margenIzquierdo = 44;
        int margenDerecho = 20;
        int margenSuperior = 24;
        int margenInferior = etiquetas.size() > 6 ? 66 : 54;

        int anchoGrafico = Math.max(ancho - margenIzquierdo - margenDerecho, 10);
        int altoGrafico = Math.max(alto - margenSuperior - margenInferior, 10);
        int baseY = margenSuperior + altoGrafico;

        int maxValor = 1;
        for (int v : valores) {
            maxValor = Math.max(maxValor, v);
        }
        int techo = Math.max(maxValor, 1);
        int numeroLineasPorAlto = Math.max(1, altoGrafico / 34);
        int numeroLineas = Math.min(techo, Math.min(5, numeroLineasPorAlto));

        g2.setFont(getFont().deriveFont(10f));
        FontMetrics fmEje = g2.getFontMetrics();
        for (int i = 0; i <= numeroLineas; i++) {
            int y = baseY - (int) ((double) i / numeroLineas * altoGrafico);
            g2.setColor(estilos.BORDE);
            g2.drawLine(margenIzquierdo, y, margenIzquierdo + anchoGrafico, y);
            int valorLinea = (int) Math.round((double) techo * i / numeroLineas);
            String texto = String.valueOf(valorLinea);
            g2.setColor(estilos.TEXTO_SECUNDARIO);
            g2.drawString(texto, margenIzquierdo - fmEje.stringWidth(texto) - 6, y + 4);
        }

        g2.setColor(estilos.TEXTO_SECUNDARIO);
        g2.drawLine(margenIzquierdo, margenSuperior, margenIzquierdo, baseY);
        g2.drawLine(margenIzquierdo, baseY, margenIzquierdo + anchoGrafico, baseY);

        int cantidadBarras = etiquetas.size();
        double anchoSlot = (double) anchoGrafico / cantidadBarras;
        double anchoBarra = Math.min(anchoSlot * 0.55, 64);
        Font fontEtiqueta = getFont().deriveFont(10.5f);

        for (int i = 0; i < cantidadBarras; i++) {
            int valor = valores.get(i);
            double centroX = margenIzquierdo + anchoSlot * i + anchoSlot / 2;
            int alturaBarra = (int) Math.round((double) valor / techo * altoGrafico);
            int x = (int) Math.round(centroX - anchoBarra / 2);
            int y = baseY - alturaBarra;

            GradientPaint degradado = new GradientPaint(0, y, aclarar(colorBarra, 0.35f), 0, baseY, colorBarra);
            g2.setPaint(degradado);
            g2.fillRoundRect(x, y, (int) anchoBarra, Math.max(alturaBarra, 2), 10, 10);

            g2.setPaint(colorBarra.darker());
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x, y, (int) anchoBarra, Math.max(alturaBarra, 2), 10, 10);

            g2.setColor(estilos.TEXTO_TITULO);
            g2.setFont(getFont().deriveFont(Font.BOLD, 11f));
            String textoValor = String.valueOf(valor);
            FontMetrics fmv = g2.getFontMetrics();
            g2.drawString(textoValor, (int) (centroX - fmv.stringWidth(textoValor) / 2.0), y - 6);

            g2.setColor(estilos.TEXTO_SECUNDARIO);
            g2.setFont(fontEtiqueta);
            FontMetrics fmEtiqueta = g2.getFontMetrics();
            String textoEtiqueta = etiquetas.get(i);

            if (fmEtiqueta.stringWidth(textoEtiqueta) <= anchoSlot - 4) {
                g2.drawString(textoEtiqueta, (int) (centroX - fmEtiqueta.stringWidth(textoEtiqueta) / 2.0), baseY + 16);
            } else if (cantidadBarras <= 6) {
                List<String> lineas = envolverTexto(textoEtiqueta, fmEtiqueta, (int) anchoSlot);
                int yEtiqueta = baseY + 16;
                for (String linea : lineas) {
                    g2.drawString(linea, (int) (centroX - fmEtiqueta.stringWidth(linea) / 2.0), yEtiqueta);
                    yEtiqueta += fmEtiqueta.getHeight();
                }
            } else {
                g2.setFont(getFont().deriveFont(9.5f));
                AffineTransform transformacionAnterior = g2.getTransform();
                g2.translate(centroX + 4, baseY + 14);
                g2.rotate(Math.toRadians(-40));
                g2.drawString(textoEtiqueta, 0, 0);
                g2.setTransform(transformacionAnterior);
            }
        }

        g2.dispose();
    }

    private static Color aclarar(Color base, float proporcion) {
        int r = (int) (base.getRed() + (255 - base.getRed()) * proporcion);
        int g = (int) (base.getGreen() + (255 - base.getGreen()) * proporcion);
        int b = (int) (base.getBlue() + (255 - base.getBlue()) * proporcion);
        return new Color(r, g, b);
    }

    private static List<String> envolverTexto(String texto, FontMetrics fm, int anchoMax) {
        List<String> lineas = new ArrayList<>();
        String[] palabras = texto.split(" ");
        StringBuilder actual = new StringBuilder();
        for (String palabra : palabras) {
            String prueba = actual.isEmpty() ? palabra : actual + " " + palabra;
            if (fm.stringWidth(prueba) > anchoMax && !actual.isEmpty()) {
                lineas.add(actual.toString());
                actual = new StringBuilder(palabra);
            } else {
                actual = new StringBuilder(prueba);
            }
        }
        if (!actual.isEmpty()) {
            lineas.add(actual.toString());
        }
        if (lineas.size() > 2) {
            lineas = lineas.subList(0, 2);
        }
        return lineas;
    }
}