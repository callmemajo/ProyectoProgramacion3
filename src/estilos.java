package reservas.ui;

import javax.swing.*;
import java.awt.*;

public final class estilos {

    public static final Color AZUL = new Color(0x25, 0x63, 0xeb);
    public static final Color AZUL_OSCURO = new Color(0x1d, 0x4e, 0xd8);
    public static final Color VERDE = new Color(0x16, 0xa3, 0x4a);
    public static final Color ROJO = new Color(0xdc, 0x26, 0x26);
    public static final Color MORADO = new Color(0x7c, 0x3a, 0xed);
    public static final Color TEAL = new Color(0x0d, 0x94, 0x88);
    public static final Color NARANJA = new Color(0xea, 0x58, 0x0c);
    public static final Color ROSA = new Color(0xdb, 0x27, 0x77);

    public static final Color TEXTO_SECUNDARIO = new Color(0x52, 0x51, 0x4e);
    public static final Color TEXTO_TITULO = new Color(0x1f, 0x20, 0x37);
    public static final Color BORDE = new Color(0xe1, 0xe1, 0xe8);
    public static final Color FONDO_VENTANA = new Color(0xf1, 0xf3, 0xf9);

    public static final Color GRADIENTE_INICIO = new Color(0x1e, 0x3a, 0x8a);
    public static final Color GRADIENTE_FIN = new Color(0x6d, 0x28, 0xd9);

    public static final Color ACTIVA_FONDO = new Color(0xdc, 0xfc, 0xe7);
    public static final Color ACTIVA_TEXTO = new Color(0x15, 0x80, 0x3d);
    public static final Color CANCELADA_FONDO = new Color(0xe5, 0xe7, 0xeb);
    public static final Color CANCELADA_TEXTO = new Color(0x52, 0x51, 0x4e);
    public static final Color OCUPADO_FONDO = new Color(0xfe, 0xf3, 0xc7);
    public static final Color OCUPADO_TEXTO = new Color(0x7c, 0x4a, 0x03);

    private estilos() {
    }

    public static JButton botonPrimario(String texto) {
        return new botonRedondeado(texto, AZUL, Color.WHITE, null, true);
    }

    public static JButton botonExito(String texto) {
        return new botonRedondeado(texto, VERDE, Color.WHITE, null, true);
    }

    public static JButton botonPeligro(String texto) {
        return new botonRedondeado(texto, Color.WHITE, ROJO, ROJO, false);
    }

    public static JButton botonSecundario(String texto) {
        return new botonRedondeado(texto, Color.WHITE, TEXTO_SECUNDARIO, BORDE, false);
    }

    public static JButton botonBarra(String texto) {
        return new botonRedondeado(texto, new Color(255, 255, 255, 35), Color.WHITE, new Color(255, 255, 255, 110), false);
    }

    public static JLabel etiquetaTitulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 13f));
        etiqueta.setForeground(TEXTO_TITULO);
        return etiqueta;
    }
}