package reservas;

import reservas.modelo.categoria;
import reservas.api.clienteGemini;

import java.util.List;

public class pruebaClienteGeminiManual {

    public static void main(String[] args) throws Exception {
        List<categoria> categorias = List.of(
                new categoria("1", "Sala de reuniones"),
                new categoria("2", "Proyector"),
                new categoria("3", "Auditorio"),
                new categoria("4", "Laboratorio de computo"));

        String frase = "reservar una sala manana de 2 a 4pm para reunion de ventas";
        if (args.length > 0) {
            frase = String.join(" ", args);
        }

        System.out.println("Frase: " + frase);
        clienteGemini.resultadoInterpretacionIA resultado = clienteGemini.interpretar(frase, categorias);

        System.out.println("actividad: " + resultado.actividad);
        System.out.println("fecha: " + resultado.fecha);
        System.out.println("horaInicio: " + resultado.horaInicio);
        System.out.println("horaFin: " + resultado.horaFin);
        System.out.println("categoriasSugeridas: " + resultado.categoriasSugeridas);
    }
}