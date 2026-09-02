package reservas.controlador;

import reservas.datos.almacenDatos;
import reservas.modelo.categoria;
import reservas.modelo.recurso;
import reservas.modelo.reserva;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class controladorEstadisticas {

    public Map<categoria, Integer> reservasPorCategoria(LocalDate desde, LocalDate hasta) {
        Map<categoria, Integer> conteo = new LinkedHashMap<>();
        for (categoria c : almacenDatos.categorias) {
            conteo.put(c, 0);
        }
        for (reserva r : almacenDatos.reservas) {
            if (r.getFecha().isBefore(desde) || r.getFecha().isAfter(hasta)) {
                continue;
            }
            Set<categoria> categoriasDeEstaReserva = new HashSet<>();
            for (recurso rec : r.getRecursosAsignados()) {
                categoriasDeEstaReserva.add(rec.getCategoria());
            }
            for (categoria c : categoriasDeEstaReserva) {
                conteo.merge(c, 1, Integer::sum);
            }
        }
        return conteo;
    }

    public Map<LocalDate, Integer> actividadesPorSemana(LocalDate desde, LocalDate hasta) {
        Map<LocalDate, Integer> conteo = new TreeMap<>();
        LocalDate cursor = desde.minusDays(desde.getDayOfWeek().getValue() - 1);
        while (!cursor.isAfter(hasta)) {
            conteo.put(cursor, 0);
            cursor = cursor.plusDays(7);
        }
        for (reserva r : almacenDatos.reservas) {
            if (r.getFecha().isBefore(desde) || r.getFecha().isAfter(hasta)) {
                continue;
            }
            LocalDate lunes = r.getFecha().minusDays(r.getFecha().getDayOfWeek().getValue() - 1);
            conteo.merge(lunes, 1, Integer::sum);
        }
        return conteo;
    }
}