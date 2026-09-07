package reservas.API;

import reservas.modelo.categoria;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class clienteGemini {

    private static final String ARCHIVO_CONFIG = "gemini.properties";
    private static final String MODELO = "gemini-3.5-flash-lite";

    private clienteGemini() {
    }

    public static class resultadoInterpretacionIA {
        public final String actividad;
        public final LocalDate fecha;
        public final LocalTime horaInicio;
        public final LocalTime horaFin;
        public final List<String> categoriasSugeridas;

        public resultadoInterpretacionIA(String actividad, LocalDate fecha, LocalTime horaInicio,
                                         LocalTime horaFin, List<String> categoriasSugeridas) {
            this.actividad = actividad;
            this.fecha = fecha;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
            this.categoriasSugeridas = categoriasSugeridas;
        }
    }

    public static String obtenerApiKey() {
        Path archivo = Path.of(ARCHIVO_CONFIG);
        if (!Files.exists(archivo)) {
            try {
                Files.writeString(archivo, "apiKey=" + System.lineSeparator());
            } catch (IOException excepcionEscritura) {
                return null;
            }
            return null;
        }
        try (var entrada = Files.newInputStream(archivo)) {
            Properties propiedades = new Properties();
            propiedades.load(entrada);
            String clave = propiedades.getProperty("apiKey", "").trim();
            return clave.isEmpty() ? null : clave;
        } catch (IOException excepcionLectura) {
            return null;
        }
    }

    public static resultadoInterpretacionIA interpretar(String frase, List<categoria> categoriasDisponibles)
            throws IOException, InterruptedException {
        String apiKey = obtenerApiKey();
        if (apiKey == null) {
            throw new IllegalStateException(
                    "Falta configurar la clave de Gemini. Abre el archivo " + ARCHIVO_CONFIG
                            + " (en la carpeta del proyecto) y completa apiKey=TU_CLAVE. "
                            + "Consigue una clave gratis en aistudio.google.com/apikey.");
        }

        StringBuilder listaCategorias = new StringBuilder();
        for (categoria categoria : categoriasDisponibles) {
            if (listaCategorias.length() > 0) {
                listaCategorias.append(", ");
            }
            listaCategorias.append(categoria.getDescripcion());
        }

        String prompt = construirPrompt(frase, listaCategorias.toString());
        String cuerpoSolicitud = "{\"contents\":[{\"parts\":[{\"text\":" + analizadorJson.aCadenaJson(prompt)
                + "}]}],\"generationConfig\":{\"temperature\":0}}";

        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + MODELO
                        + ":generateContent"))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(cuerpoSolicitud, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> respuesta = enviarConReintentos(cliente, solicitud);
        if (respuesta.statusCode() != 200) {
            String detalle = respuesta.body();
            String detalleCorto = detalle == null ? "" : detalle.substring(0, Math.min(detalle.length(), 200));
            throw new IllegalStateException("El servicio de IA respondio con un error ("
                    + respuesta.statusCode() + "). Revisa tu clave o intenta mas tarde. Detalle: " + detalleCorto);
        }

        String textoExtraido = extraerTextoDeRespuesta(respuesta.body());
        return interpretarJsonExtraido(textoExtraido);
    }

    private static HttpResponse<String> enviarConReintentos(HttpClient cliente, HttpRequest solicitud)
            throws IOException, InterruptedException {
        int intentosMaximos = 4;
        HttpResponse<String> respuesta = null;
        for (int intento = 1; intento <= intentosMaximos; intento++) {
            respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
            boolean esErrorTemporal = respuesta.statusCode() == 503 || respuesta.statusCode() == 429;
            if (!esErrorTemporal || intento == intentosMaximos) {
                return respuesta;
            }
            Thread.sleep(1500L * intento);
        }
        return respuesta;
    }

    private static String construirPrompt(String frase, String categoriasDisponibles) {
        return "Sos un asistente que extrae datos de una reserva de recursos a partir de una frase en "
                + "lenguaje natural en espanol. La fecha de hoy es " + LocalDate.now() + ". "
                + "Las unicas categorias de recursos validas son: " + categoriasDisponibles + ". "
                + "Frase del usuario: \"" + frase + "\". "
                + "Responde UNICAMENTE con un objeto JSON, sin texto adicional y sin marcas de codigo, "
                + "con exactamente estas claves: actividad (texto), fecha (formato aaaa-mm-dd), "
                + "horaInicio (formato HH:mm, 24 horas), horaFin (formato HH:mm, 24 horas), "
                + "categorias (arreglo de texto, usando exactamente la redaccion de la lista de categorias validas). "
                + "Si no podes determinar algun dato, usa null para ese campo o un arreglo vacio para categorias.";
    }

    private static String extraerTextoDeRespuesta(String cuerpoJson) {
        Object raiz = analizadorJson.analizar(cuerpoJson);
        Map<?, ?> mapaRaiz = (Map<?, ?>) raiz;
        List<Object> candidatos = analizadorJson.obtenerLista(mapaRaiz, "candidates");
        if (candidatos.isEmpty()) {
            throw new IllegalStateException("La IA no devolvio ninguna respuesta utilizable.");
        }
        Map<?, ?> primerCandidato = (Map<?, ?>) candidatos.get(0);
        Map<?, ?> contenido = (Map<?, ?>) primerCandidato.get("content");
        List<Object> partes = analizadorJson.obtenerLista(contenido, "parts");
        if (partes.isEmpty()) {
            throw new IllegalStateException("La IA no devolvio ninguna respuesta utilizable.");
        }
        Map<?, ?> primeraParte = (Map<?, ?>) partes.get(0);
        return analizadorJson.obtenerCadena(primeraParte, "text");
    }

    private static resultadoInterpretacionIA interpretarJsonExtraido(String textoExtraido) {
        int inicio = textoExtraido.indexOf('{');
        int fin = textoExtraido.lastIndexOf('}');
        String jsonLimpio = (inicio >= 0 && fin > inicio) ? textoExtraido.substring(inicio, fin + 1) : textoExtraido;

        Object valor;
        try {
            valor = analizadorJson.analizar(jsonLimpio);
        } catch (RuntimeException excepcionParseo) {
            throw new IllegalStateException("No se pudo interpretar la respuesta de la IA.");
        }
        Map<?, ?> mapa = (Map<?, ?>) valor;

        String actividad = analizadorJson.obtenerCadena(mapa, "actividad");
        LocalDate fecha = fechaONull(analizadorJson.obtenerCadena(mapa, "fecha"));
        LocalTime horaInicio = horaONull(analizadorJson.obtenerCadena(mapa, "horaInicio"));
        LocalTime horaFin = horaONull(analizadorJson.obtenerCadena(mapa, "horaFin"));

        List<String> categoriasSugeridas = new ArrayList<>();
        for (Object elemento : analizadorJson.obtenerLista(mapa, "categorias")) {
            if (elemento != null) {
                categoriasSugeridas.add(String.valueOf(elemento));
            }
        }

        return new resultadoInterpretacionIA(actividad, fecha, horaInicio, horaFin, categoriasSugeridas);
    }

    private static LocalDate fechaONull(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return LocalDate.parse(texto);
        } catch (RuntimeException excepcionFormato) {
            return null;
        }
    }

    private static LocalTime horaONull(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return LocalTime.parse(texto.length() >= 5 ? texto.substring(0, 5) : texto);
        } catch (RuntimeException excepcionFormato) {
            return null;
        }
    }
}