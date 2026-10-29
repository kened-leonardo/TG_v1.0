package ec.edu.epn.sokoban.model.reglas;

import ec.edu.epn.sokoban.model.escenario.Caja;
import ec.edu.epn.sokoban.model.escenario.Casilla;
import ec.edu.epn.sokoban.model.escenario.Personaje;
import ec.edu.epn.sokoban.model.escenario.Tablero;

/**
 * Validador y coordinador central del movimiento y colisiones del juego.
 */
public final class GestorColisiones {

    public GestorColisiones() {
        // Constructor vacío intencional: la clase no mantiene estado
        // ni requiere inicialización.
    }

    /**
     * Procesa y valida el movimiento del personaje y empuje de cajas sobre el
     * tablero.
     *
     * @param tablero        tablero activo
     * @param personaje      personaje a desplazar
     * @param filaDestino    fila de la casilla destino
     * @param columnaDestino columna de la casilla destino
     * @return true si el movimiento se realizó con éxito; false en caso contrario
     */
    public boolean procesarMovimiento(
            Tablero tablero,
            Personaje personaje,
            int filaDestino,
            int columnaDestino) {
        if (tablero == null || personaje == null || tablero.getPersonaje() != personaje) {
            return false;
        }

        int deltaFila = filaDestino - personaje.getFila();
        int deltaColumna = columnaDestino - personaje.getColumna();
        if (Math.abs(deltaFila) + Math.abs(deltaColumna) != 1
                || !tablero.estaDentroDelTablero(filaDestino, columnaDestino)) {
            return false;
        }

        Caja caja = tablero.obtenerCajaEnPosicion(filaDestino, columnaDestino);
        if (caja == null) {
            if (!tablero.esCeldaTransitable(filaDestino, columnaDestino)) {
                return false;
            }

            Casilla casillaDestino = tablero.obtenerCasilla(filaDestino, columnaDestino);
            if (!casillaDestino.getGestorAcciones().puedenIngresarAcciones(tablero, personaje)) {
                return false;
            }

            tablero.actualizarCasilla(filaDestino, columnaDestino, personaje);
            casillaDestino.getGestorAcciones().ejecutarAcciones(casillaDestino, tablero, personaje);
            return true;
        }

        int filaDestinoCaja = filaDestino + deltaFila;
        int columnaDestinoCaja = columnaDestino + deltaColumna;
        if (!tablero.estaDentroDelTablero(filaDestinoCaja, columnaDestinoCaja)
                || !tablero.esCeldaTransitable(filaDestinoCaja, columnaDestinoCaja)
                || tablero.esCeldaCaja(filaDestinoCaja, columnaDestinoCaja)) {

            // INICIO: mecanica caja explosiva (Grupo4) =====
            // La caja no pudo avanzar. Le damos la oportunidad de reaccionar
            // a traves de sus propias Acciones registradas (ej. Explosion),
            // sin que GestorColisiones conozca ningun tipo concreto de caja.
            caja.getGestorAcciones().ejecutarAcciones(caja, tablero, caja);

            boolean siguePresente = tablero.obtenerCajaEnPosicion(filaDestino, columnaDestino) == caja;
            if (!siguePresente) {
                // Alguna Accion registrada (ej. Explosion) libero la posicion.
                tablero.actualizarCasilla(filaDestino, columnaDestino, personaje);
                return true;
            }
            // ===== FIN: mecanica caja explosiva =====

            return false;
        }

        Casilla casillaDestinoCaja = tablero.obtenerCasilla(filaDestinoCaja, columnaDestinoCaja);
        Casilla casillaBasePersonaje = tablero.obtenerCasillaBase(filaDestino, columnaDestino);

        if (!casillaDestinoCaja.getGestorAcciones().puedenIngresarAcciones(tablero, caja)
                || (casillaBasePersonaje != null
                        && !casillaBasePersonaje.getGestorAcciones().puedenIngresarAcciones(tablero, personaje))) {
            return false;
        }

        tablero.actualizarCasilla(filaDestinoCaja, columnaDestinoCaja, caja);
        tablero.actualizarCasilla(filaDestino, columnaDestino, personaje);
        casillaDestinoCaja.getGestorAcciones().ejecutarAcciones(casillaDestinoCaja, tablero, caja);
        if (casillaBasePersonaje != null) {
            casillaBasePersonaje.getGestorAcciones().ejecutarAcciones(casillaBasePersonaje, tablero, personaje);
        }
        return true;
    }
}
