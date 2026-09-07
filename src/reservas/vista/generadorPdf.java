package reservas.vista;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class generadorPdf {

    private static final Charset CODIFICACION = Charset.forName("windows-1252");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final float MARGEN = 40f;
    private static final float ALTO_BLOQUE_TITULO = 60f;
    private static final float MINIMO_INFERIOR = 40f;
    private static final float TAMANO_TITULO = 16f;
    private static final float TAMANO_FECHA = 9f;
    private static final float ANCHO_CARACTER_COURIER = 0.6f;

    public static void generarReporte(Path archivo, String titulo, JTable tabla) throws IOException {
        TableModel modelo = tabla.getModel();
        int totalColumnas = modelo.getColumnCount();
        String[] encabezados = new String[totalColumnas];
        for (int c = 0; c < totalColumnas; c++) {
            encabezados[c] = String.valueOf(modelo.getColumnName(c));
        }
        List<String[]> filas = new ArrayList<>();
        for (int f = 0; f < modelo.getRowCount(); f++) {
            String[] fila = new String[totalColumnas];
            for (int c = 0; c < totalColumnas; c++) {
                Object valor = modelo.getValueAt(f, c);
                fila[c] = valor == null ? "" : valor.toString();
            }
            filas.add(fila);
        }
        generarReporte(archivo, titulo, encabezados, filas);
    }

    public static void generarReporte(Path archivo, String titulo, String[] encabezados, List<String[]> filas) throws IOException {
        int totalColumnas = encabezados.length;
        boolean horizontal = totalColumnas > 5;
        float anchoPagina = horizontal ? 792f : 612f;
        float altoPagina = horizontal ? 612f : 792f;

        float tamanoTabla;
        if (totalColumnas <= 4) {
            tamanoTabla = 10f;
        } else if (totalColumnas <= 6) {
            tamanoTabla = 9f;
        } else if (totalColumnas <= 9) {
            tamanoTabla = 8f;
        } else {
            tamanoTabla = 7f;
        }

        int[] anchoColumnas = calcularAnchoColumnas(encabezados, filas, totalColumnas, anchoPagina, tamanoTabla);

        float interlineado = tamanoTabla * 1.5f;
        float yInicioTabla = altoPagina - MARGEN - ALTO_BLOQUE_TITULO;
        float espacioDisponible = yInicioTabla - MINIMO_INFERIOR;
        int lineasDisponibles = (int) (espacioDisponible / interlineado);
        int filasPorPagina = Math.max(1, lineasDisponibles - 2);

        List<List<String[]>> paginas = dividirEnPaginas(filas, filasPorPagina);

        List<byte[]> streamsContenido = new ArrayList<>();
        for (int i = 0; i < paginas.size(); i++) {
            String contenido = construirContenidoPagina(titulo, encabezados, anchoColumnas, paginas.get(i),
                    i, paginas.size(), altoPagina, tamanoTabla, interlineado);
            streamsContenido.add(contenido.getBytes(CODIFICACION));
        }

        escribirPdf(archivo, anchoPagina, altoPagina, streamsContenido);
    }

    private static int[] calcularAnchoColumnas(String[] encabezados, List<String[]> filas, int totalColumnas,
                                               float anchoPagina, float tamanoTabla) {
        int[] anchos = new int[totalColumnas];
        for (int c = 0; c < totalColumnas; c++) {
            int maximo = encabezados[c] == null ? 0 : encabezados[c].length();
            for (String[] fila : filas) {
                String valor = c < fila.length && fila[c] != null ? fila[c] : "";
                maximo = Math.max(maximo, valor.length());
            }
            anchos[c] = Math.max(4, Math.min(maximo + 2, 28));
        }

        int disponibles = (int) ((anchoPagina - 2 * MARGEN) / (tamanoTabla * ANCHO_CARACTER_COURIER));
        int suma = 0;
        for (int ancho : anchos) {
            suma += ancho;
        }
        if (suma > disponibles && disponibles > 0 && suma > 0) {
            for (int c = 0; c < totalColumnas; c++) {
                anchos[c] = Math.max(4, anchos[c] * disponibles / suma);
            }
        }
        return anchos;
    }

    private static List<List<String[]>> dividirEnPaginas(List<String[]> filas, int filasPorPagina) {
        List<List<String[]>> paginas = new ArrayList<>();
        if (filas.isEmpty()) {
            paginas.add(new ArrayList<>());
            return paginas;
        }
        for (int i = 0; i < filas.size(); i += filasPorPagina) {
            paginas.add(new ArrayList<>(filas.subList(i, Math.min(i + filasPorPagina, filas.size()))));
        }
        return paginas;
    }

    private static String construirContenidoPagina(String titulo, String[] encabezados, int[] anchos,
                                                   List<String[]> filasPagina, int indicePagina, int totalPaginas, float altoPagina,
                                                   float tamanoTabla, float interlineado) {

        StringBuilder contenido = new StringBuilder();

        float yTitulo = altoPagina - MARGEN - TAMANO_TITULO;
        contenido.append("BT\n/F1 ").append(fmt(TAMANO_TITULO)).append(" Tf\n")
                .append(fmt(MARGEN)).append(" ").append(fmt(yTitulo)).append(" Td\n")
                .append("(").append(escaparTexto(titulo)).append(") Tj\nET\n");

        float yFecha = yTitulo - 20f;
        String fechaTexto = "Generado: " + LocalDateTime.now().format(FORMATO_FECHA_HORA);
        if (totalPaginas > 1) {
            fechaTexto = fechaTexto + "    Pagina " + (indicePagina + 1) + " de " + totalPaginas;
        }
        contenido.append("BT\n/F3 ").append(fmt(TAMANO_FECHA)).append(" Tf\n")
                .append(fmt(MARGEN)).append(" ").append(fmt(yFecha)).append(" Td\n")
                .append("(").append(escaparTexto(fechaTexto)).append(") Tj\nET\n");

        float yTabla = yFecha - 24f;
        contenido.append("BT\n/F2 ").append(fmt(tamanoTabla)).append(" Tf\n")
                .append(fmt(MARGEN)).append(" ").append(fmt(yTabla)).append(" Td\n");

        String lineaEncabezado = construirLinea(encabezados, anchos);
        contenido.append("(").append(escaparTexto(lineaEncabezado)).append(") Tj\n");
        contenido.append("0 ").append(fmt(-interlineado)).append(" Td\n");

        String separador = construirSeparador(anchos);
        contenido.append("(").append(escaparTexto(separador)).append(") Tj\n");
        contenido.append("0 ").append(fmt(-interlineado)).append(" Td\n");

        if (filasPagina.isEmpty()) {
            contenido.append("(Sin registros para mostrar.) Tj\n");
        } else {
            boolean primeraFila = true;
            for (String[] fila : filasPagina) {
                if (!primeraFila) {
                    contenido.append("0 ").append(fmt(-interlineado)).append(" Td\n");
                }
                primeraFila = false;
                String linea = construirLinea(fila, anchos);
                contenido.append("(").append(escaparTexto(linea)).append(") Tj\n");
            }
        }

        contenido.append("ET\n");
        return contenido.toString();
    }

    private static String construirLinea(String[] valores, int[] anchos) {
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < anchos.length; c++) {
            String valor = c < valores.length && valores[c] != null ? valores[c] : "";
            if (valor.length() > anchos[c]) {
                valor = anchos[c] <= 1 ? valor.substring(0, anchos[c]) : valor.substring(0, anchos[c] - 1) + ".";
            }
            sb.append(padDerecha(valor, anchos[c]));
            if (c < anchos.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private static String construirSeparador(int[] anchos) {
        int totalAncho = 0;
        for (int ancho : anchos) {
            totalAncho += ancho + 1;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < totalAncho; i++) {
            sb.append('-');
        }
        return sb.toString();
    }

    private static String padDerecha(String texto, int ancho) {
        StringBuilder sb = new StringBuilder(texto);
        while (sb.length() < ancho) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static String escaparTexto(String texto) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '\\' || c == '(' || c == ')') {
                sb.append('\\');
                sb.append(c);
            } else if (c == '\n' || c == '\r') {
                sb.append(' ');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String fmt(float valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    private static String padCeros(int valor) {
        String texto = Integer.toString(valor);
        StringBuilder sb = new StringBuilder();
        for (int i = texto.length(); i < 10; i++) {
            sb.append('0');
        }
        sb.append(texto);
        return sb.toString();
    }

    private static void escribirTexto(ByteArrayOutputStream buffer, String texto) throws IOException {
        buffer.write(texto.getBytes(CODIFICACION));
    }

    private static void escribirPdf(Path archivo, float anchoPagina, float altoPagina,
                                    List<byte[]> streamsContenido) throws IOException {
        int numPaginas = streamsContenido.size();
        int objetoCatalogo = 1;
        int objetoPaginas = 2;
        int objetoFuenteTitulo = 3;
        int objetoFuenteTabla = 4;
        int objetoFuenteTexto = 5;
        int primerObjetoPagina = 6;
        int totalObjetos = 5 + numPaginas * 2;

        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < numPaginas; i++) {
            int objetoPagina = primerObjetoPagina + i * 2;
            kids.append(objetoPagina).append(" 0 R ");
        }

        List<String> definiciones = new ArrayList<>();
        definiciones.add(null);
        definiciones.add("<< /Type /Catalog /Pages " + objetoPaginas + " 0 R >>");
        definiciones.add("<< /Type /Pages /Kids [" + kids.toString().trim() + "] /Count " + numPaginas + " >>");
        definiciones.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>");
        definiciones.add("<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>");
        definiciones.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);

        escribirTexto(buffer, "%PDF-1.4\n");
        buffer.write('%');
        buffer.write(0xE2);
        buffer.write(0xE3);
        buffer.write(0xCF);
        buffer.write(0xD3);
        buffer.write('\n');

        for (int n = 1; n <= 5; n++) {
            offsets.add(buffer.size());
            escribirTexto(buffer, n + " 0 obj\n" + definiciones.get(n) + "\nendobj\n");
        }

        for (int i = 0; i < numPaginas; i++) {
            int objetoPagina = primerObjetoPagina + i * 2;
            int objetoContenido = objetoPagina + 1;
            byte[] contenido = streamsContenido.get(i);

            offsets.add(buffer.size());
            String dictPagina = "<< /Type /Page /Parent " + objetoPaginas + " 0 R /MediaBox [0 0 "
                    + fmt(anchoPagina) + " " + fmt(altoPagina) + "] /Resources << /Font << /F1 "
                    + objetoFuenteTitulo + " 0 R /F2 " + objetoFuenteTabla + " 0 R /F3 "
                    + objetoFuenteTexto + " 0 R >> >> /Contents " + objetoContenido + " 0 R >>";
            escribirTexto(buffer, objetoPagina + " 0 obj\n" + dictPagina + "\nendobj\n");

            offsets.add(buffer.size());
            escribirTexto(buffer, objetoContenido + " 0 obj\n<< /Length " + contenido.length + " >>\nstream\n");
            buffer.write(contenido);
            escribirTexto(buffer, "\nendstream\nendobj\n");
        }

        int posicionXref = buffer.size();
        escribirTexto(buffer, "xref\n0 " + (totalObjetos + 1) + "\n");
        escribirTexto(buffer, "0000000000 65535 f \n");
        for (int n = 1; n <= totalObjetos; n++) {
            escribirTexto(buffer, padCeros(offsets.get(n)) + " 00000 n \n");
        }

        escribirTexto(buffer, "trailer\n<< /Size " + (totalObjetos + 1) + " /Root " + objetoCatalogo + " 0 R >>\n");
        escribirTexto(buffer, "startxref\n" + posicionXref + "\n%%EOF");

        Files.write(archivo, buffer.toByteArray());
    }
}