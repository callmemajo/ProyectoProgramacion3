package reservas.controlador;

import reservas.datos.almacenDatos;
import reservas.modelo.reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class controladorActividades {

    public String textoCelda(LocalDate fecha, LocalTime hora) {
        List<String> coincidencias = new ArrayList<>();
        for (reserva r : almacenDatos.reservas) {
            if (!r.estaActiva()) {
                continue;
            }
            if (!r.getFecha().equals(fecha)) {
                continue;
            }
            boolean dentroDelRango = !hora.isBefore(r.getHoraInicio()) && hora.isBefore(r.getHoraFin());
            if (dentroDelRango) {
                coincidencias.add(r.getActividad() + " (" + r.getFuncionario().getNombreMostrar() + ")");
            }
        }
        return String.join(" / ", coincidencias);
    }
}