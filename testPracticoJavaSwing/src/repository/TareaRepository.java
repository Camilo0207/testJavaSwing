package repository;

import model.Tarea;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TareaRepository {

    private final List<Tarea> tareas;

    public TareaRepository() {
        this.tareas = new ArrayList<>();
    }

    public void agregar(Tarea tarea) {
        tareas.add(tarea);
    }


    public void eliminar(int indice) {
        if (indice >= 0 && indice < tareas.size()) {
            tareas.remove(indice);
        }
    }


    public List<Tarea> obtenerTodas() {
        return new ArrayList<>(tareas);
    }


    public List<Tarea> filtrarPorEstado(String estado) {
        if (estado.equals("Todas")) {
            return obtenerTodas();
        }
        return tareas.stream()
                .filter(t -> t.getEstado().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }


    public void actualizarEstado(int indice, String nuevoEstado) {
        if (indice >= 0 && indice < tareas.size()) {
            tareas.get(indice).setEstado(nuevoEstado);
        }
    }
}
