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
        if (!sonParticipantesValidos(tablero, personaje)) {
            return false;
        }

        int deltaFila = filaDestino - personaje.getFila();
        int deltaColumna = columnaDestino - personaje.getColumna();
        if (!esDesplazamientoValido(tablero, deltaFila, deltaColumna, filaDestino, columnaDestino)) {
            return false;
        }

        Caja caja = tablero.obtenerCajaEnPosicion(filaDestino, columnaDestino);
        if (caja == null) {
<<<<<<< HEAD
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
=======
            return moverPersonajeSinCaja(tablero, personaje, filaDestino, columnaDestino);
>>>>>>> 792ce4c041297328c912e621726d035d07592791
        }

        return empujarCaja(tablero, personaje, caja, filaDestino, columnaDestino, deltaFila, deltaColumna);
    }

    // ------------------------------------------------------------------
    // Validaciones previas
    // ------------------------------------------------------------------

    private boolean sonParticipantesValidos(Tablero tablero, Personaje personaje) {
        return tablero != null && personaje != null && tablero.getPersonaje() == personaje;
    }

    /**
     * El personaje solo puede moverse una casilla en línea recta y dentro del
     * tablero.
     */
    private boolean esDesplazamientoValido(Tablero tablero, int deltaFila, int deltaColumna,
            int filaDestino, int columnaDestino) {
        return Math.abs(deltaFila) + Math.abs(deltaColumna) == 1
                && tablero.estaDentroDelTablero(filaDestino, columnaDestino);
    }

    // ------------------------------------------------------------------
    // Movimiento sin caja en el destino
    // ------------------------------------------------------------------

    private boolean moverPersonajeSinCaja(Tablero tablero, Personaje personaje,
            int filaDestino, int columnaDestino) {
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

    // ------------------------------------------------------------------
    // Movimiento con caja en el destino (empuje)
    // ------------------------------------------------------------------

    private boolean empujarCaja(Tablero tablero, Personaje personaje, Caja caja,
            int filaDestino, int columnaDestino,
            int deltaFila, int deltaColumna) {
        int filaDestinoCaja = filaDestino + deltaFila;
        int columnaDestinoCaja = columnaDestino + deltaColumna;

        if (!puedeCajaAvanzar(tablero, filaDestinoCaja, columnaDestinoCaja)) {
            return resolverCajaBloqueada(tablero, personaje, caja, filaDestino, columnaDestino);
        }

        return desplazarCajaYPersonaje(tablero, personaje, caja,
                filaDestino, columnaDestino, filaDestinoCaja, columnaDestinoCaja);
    }

    private boolean puedeCajaAvanzar(Tablero tablero, int filaDestinoCaja, int columnaDestinoCaja) {
        return tablero.estaDentroDelTablero(filaDestinoCaja, columnaDestinoCaja)
                && tablero.esCeldaTransitable(filaDestinoCaja, columnaDestinoCaja)
                && !tablero.esCeldaCaja(filaDestinoCaja, columnaDestinoCaja);
    }

    // INICIO: mecanica caja explosiva (Grupo4) =====
    // La caja no pudo avanzar. Le damos la oportunidad de reaccionar
    // a traves de sus propias Acciones registradas (ej. Explosion),
    // sin que GestorColisiones conozca ningun tipo concreto de caja.
    private boolean resolverCajaBloqueada(Tablero tablero, Personaje personaje, Caja caja,
            int filaDestino, int columnaDestino) {
        caja.getGestorAcciones().ejecutarAcciones(caja, tablero, caja);

        boolean siguePresente = tablero.obtenerCajaEnPosicion(filaDestino, columnaDestino) == caja;
        if (!siguePresente) {
            // Alguna Accion registrada (ej. Explosion) libero la posicion.
            tablero.actualizarCasilla(filaDestino, columnaDestino, personaje);
            return true;
        }
        return false;
    }
    // ===== FIN: mecanica caja explosiva =====

    private boolean desplazarCajaYPersonaje(Tablero tablero, Personaje personaje, Caja caja,
            int filaDestino, int columnaDestino,
            int filaDestinoCaja, int columnaDestinoCaja) {
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