package ec.edu.epn.sokoban.model.historial;

import ec.edu.epn.sokoban.model.escenario.*;
import ec.edu.epn.sokoban.model.interfaces.Accion;

import java.util.Map;

/**
 * Representa el estado registrado de una partida en un momento dado,
 * permitiendo su restauración completa directamente sobre el tablero.
 */
public class PartidaMomento {

    private final Casilla[][] celdasSnapshot;
    private final int filas;
    private final int columnas;

    public PartidaMomento(Tablero t) {
        if (t == null) {
            this.filas = 0;
            this.columnas = 0;
            this.celdasSnapshot = new Casilla[0][0];
            return;
        }

        this.filas = t.getFilas();
        this.columnas = t.getColumnas();
        this.celdasSnapshot = new Casilla[filas][columnas];

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                this.celdasSnapshot[f][c] = copiarCasilla(t.obtenerCasilla(f, c));
            }
        }
    }

    /**
 * Crea un momento vacío a partir de posiciones sueltas.
 *
 * @param posicionesCajas posiciones de las cajas (sin uso)
 * @param posicionJugador posición del jugador (sin uso)
 * @deprecated Usar {@link #PartidaMomento(Tablero)}.
 */
    @Deprecated(since = "1.1", forRemoval = true)
    public PartidaMomento(Map<Caja, Casilla> posicionesCajas, Casilla posicionJugador) {
        this.filas = 0;
        this.columnas = 0;
        this.celdasSnapshot = new Casilla[0][0];
    }

    /**
     * Restaura el estado guardado directamente sobre el tablero dado.
     */
    public void restaurarEnTablero(Tablero t) {
        if (t == null || celdasSnapshot == null || filas == 0) {
            return;
        }

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Casilla celdaGuardada = celdasSnapshot[f][c];
                Casilla celdaRestaurada = copiarCasilla(celdaGuardada);

                if (celdaRestaurada instanceof Pared || celdaRestaurada instanceof Suelo
                        || celdaRestaurada instanceof Meta) {
                    t.reemplazarTerrenoPermanente(f, c, celdaRestaurada);
                } else {
                    t.actualizarCasilla(f, c, celdaRestaurada);
                }
            }
        }
    }

    private Casilla copiarCasilla(Casilla c) {
        if (c == null) {
            return null;
        }

        Casilla copia;
        if (c instanceof Pared) {
            copia = new Pared(c.getFila(), c.getColumna());
        } else if (c instanceof Meta) {
            copia = new Meta(c.getFila(), c.getColumna());
        } else if (c instanceof Personaje) {
            copia = new Personaje(c.getFila(), c.getColumna());
        } else if (c instanceof Caja caja) {
            copia = new Caja(c.getFila(), c.getColumna(), caja.isEnMeta());

        } else {
            copia = new Suelo(c.getFila(), c.getColumna());
        }

        for (Accion accion : c.getGestorAcciones().getAcciones()) {
            copia.getGestorAcciones().agregarAccion(copiarAccion(accion));
        }

        return copia;
    }

    private Accion copiarAccion(Accion a) {
        if (a instanceof Agrietado orig) {
            Agrietado copia = new Agrietado();
            copia.setAgrietado(orig.isAgrietado());
            copia.setRoto(orig.isRoto());
            return copia;
        } else if (a instanceof Explosion orig) {
            Explosion copia = new Explosion();
            copia.setDetonada(orig.isDetonada());
            return copia;
        } else if (a instanceof Teletransportacion orig) {
            return new Teletransportacion(orig.getFilaDestino(), orig.getColumnaDestino());
        }
        return a;
    }
}
