package reservas.API;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class analizadorJson {

    private final String texto;
    private int posicion;

    private analizadorJson(String texto) {
        this.texto = texto;
        this.posicion = 0;
    }

    public static Object analizar(String texto) {
        analizadorJson analizador = new analizadorJson(texto);
        return analizador.leerValor();
    }

    public static String obtenerCadena(Map<?, ?> mapa, String clave) {
        Object valor = mapa.get(clave);
        return valor == null ? null : String.valueOf(valor);
    }

    public static List<Object> obtenerLista(Map<?, ?> mapa, String clave) {
        Object valor = mapa.get(clave);
        if (valor instanceof List<?> lista) {
            return new ArrayList<>(lista);
        }
        return new ArrayList<>();
    }

    public static String aCadenaJson(String valor) {
        StringBuilder constructor = new StringBuilder();
        constructor.append('"');
        for (int i = 0; i < valor.length(); i++) {
            char actual = valor.charAt(i);
            switch (actual) {
                case '"':
                    constructor.append("\\\"");
                    break;
                case '\\':
                    constructor.append("\\\\");
                    break;
                case '\n':
                    constructor.append("\\n");
                    break;
                case '\r':
                    constructor.append("\\r");
                    break;
                case '\t':
                    constructor.append("\\t");
                    break;
                default:
                    if (actual < 0x20) {
                        constructor.append(String.format("\\u%04x", (int) actual));
                    } else {
                        constructor.append(actual);
                    }
            }
        }
        constructor.append('"');
        return constructor.toString();
    }

    private Object leerValor() {
        saltarEspacios();
        char actual = texto.charAt(posicion);
        if (actual == '{') {
            return leerObjeto();
        }
        if (actual == '[') {
            return leerArreglo();
        }
        if (actual == '"') {
            return leerCadena();
        }
        if (actual == 't' || actual == 'f') {
            return leerBooleano();
        }
        if (actual == 'n') {
            posicion += 4;
            return null;
        }
        return leerNumero();
    }

    private Map<String, Object> leerObjeto() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        posicion++;
        saltarEspacios();
        if (texto.charAt(posicion) == '}') {
            posicion++;
            return mapa;
        }
        while (true) {
            saltarEspacios();
            String clave = leerCadena();
            saltarEspacios();
            posicion++;
            Object valor = leerValor();
            mapa.put(clave, valor);
            saltarEspacios();
            char separador = texto.charAt(posicion);
            posicion++;
            if (separador == '}') {
                break;
            }
        }
        return mapa;
    }

    private List<Object> leerArreglo() {
        List<Object> lista = new ArrayList<>();
        posicion++;
        saltarEspacios();
        if (texto.charAt(posicion) == ']') {
            posicion++;
            return lista;
        }
        while (true) {
            Object valor = leerValor();
            lista.add(valor);
            saltarEspacios();
            char separador = texto.charAt(posicion);
            posicion++;
            if (separador == ']') {
                break;
            }
        }
        return lista;
    }

    private String leerCadena() {
        StringBuilder constructor = new StringBuilder();
        posicion++;
        while (texto.charAt(posicion) != '"') {
            char actual = texto.charAt(posicion);
            if (actual == '\\') {
                posicion++;
                char escapado = texto.charAt(posicion);
                switch (escapado) {
                    case 'n':
                        constructor.append('\n');
                        break;
                    case 't':
                        constructor.append('\t');
                        break;
                    case 'r':
                        constructor.append('\r');
                        break;
                    case '"':
                        constructor.append('"');
                        break;
                    case '\\':
                        constructor.append('\\');
                        break;
                    case '/':
                        constructor.append('/');
                        break;
                    case 'u':
                        String codigo = texto.substring(posicion + 1, posicion + 5);
                        constructor.append((char) Integer.parseInt(codigo, 16));
                        posicion += 4;
                        break;
                    default:
                        constructor.append(escapado);
                }
            } else {
                constructor.append(actual);
            }
            posicion++;
        }
        posicion++;
        return constructor.toString();
    }

    private Boolean leerBooleano() {
        if (texto.startsWith("true", posicion)) {
            posicion += 4;
            return Boolean.TRUE;
        }
        posicion += 5;
        return Boolean.FALSE;
    }

    private Double leerNumero() {
        int inicio = posicion;
        while (posicion < texto.length() && "-+.0123456789eE".indexOf(texto.charAt(posicion)) >= 0) {
            posicion++;
        }
        return Double.parseDouble(texto.substring(inicio, posicion));
    }

    private void saltarEspacios() {
        while (posicion < texto.length() && Character.isWhitespace(texto.charAt(posicion))) {
            posicion++;
        }
    }
}