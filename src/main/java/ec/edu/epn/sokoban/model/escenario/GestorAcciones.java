package ec.edu.epn.sokoban.model.escenario;

import ec.edu.epn.sokoban.model.interfaces.Accion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Coordinador del Patrón Strategy para gestionar las acciones dinámicas de una
 * casilla.
 */
public class GestorAcciones {

    private final List<Accion> acciones;

    public GestorAcciones() {
        this.acciones = new ArrayList<>();
    }

    /**
     * Agrega una acción al registro interno.
     *
     * @param accion instancia de la acción a registrar
     */
    public void agregarAccion(Accion accion) {
        if (accion != null) {
            acciones.add(accion);
        }
    }

    /**
     * Remueve una acción del registro interno.
     *
     * @param accion instancia de la acción a remover
     */
    public void quitarAccion(Accion accion) {
        if (accion != null) {
            acciones.remove(accion);
        }
    }

    /**
     * Ejecuta todas las acciones registradas para la casilla pisada.
     *
     * @param casillaActual casilla que contiene las acciones
     * @param tablero       tablero activo
     * @param entidad       entidad (Personaje o Caja) que activó la casilla
     */
    public void ejecutarAcciones(Casilla casillaActual, Tablero tablero, Casilla entidad) {
        for (Accion accion : acciones) {
            accion.iniciarAccion(casillaActual, tablero, entidad);
        }
    }

    /**
     * Ejecuta las acciones sin contexto.
     *
     * @deprecated Usar {@link #ejecutarAcciones(Casilla, Tablero, Casilla)}.
     */
    @Deprecated(since = "1.1", forRemoval = true)
    public void ejecutarAcciones() {
        ejecutarAcciones(null, null, null);
    }

    /**
     * Retorna una vista no modificable de las acciones registradas.
     *
     * @return lista de acciones registradas
     */
    public List<Accion> getAcciones() {
        return Collections.unmodifiableList(acciones);
    }

    /**
     * Verifica si todas las acciones registradas permiten el ingreso de la entidad.
     *
     * @param tablero tablero activo
     * @param entidad entidad que intenta ingresar (Personaje o Caja)
     * @return true si todas permiten el ingreso; false en caso contrario
     */
    public boolean puedenIngresarAcciones(Tablero tablero, Casilla entidad) {
        for (Accion accion : acciones) {
            if (!accion.puedeIngresarEntidad(tablero, entidad)) {
                return false;
            }
        }
        return true;
    }
}
