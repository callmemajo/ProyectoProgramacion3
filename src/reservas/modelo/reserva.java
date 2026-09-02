package reservas.modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class reserva {

    private final String id;
    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private estadoReserva estado;
    private final funcionario funcionario;
    private final List<recurso> recursosAsignados;

    public reserva(String id, String actividad, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, estadoReserva estado, funcionario funcionario,
                   List<recurso> recursosAsignados) {
        this.id = id;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.funcionario = funcionario;
        this.recursosAsignados = new ArrayList<>(recursosAsignados);
    }

    public String getId() {
        return id;
    }

    public String getActividad() {
        return actividad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public estadoReserva getEstado() {
        return estado;
    }

    public funcionario getFuncionario() {
        return funcionario;
    }

    public List<recurso> getRecursosAsignados() {
        return Collections.unmodifiableList(recursosAsignados);
    }

    public boolean estaActiva() {
        return estado == estadoReserva.ACTIVA;
    }

    public boolean esFutura() {
        LocalDate hoy = LocalDate.now();
        if (fecha.isAfter(hoy)) {
            return true;
        }
        if (fecha.isEqual(hoy)) {
            return horaInicio.isAfter(LocalTime.now());
        }
        return false;
    }

    public void cancelar() {
        this.estado = estadoReserva.CANCELADA;
    }
}