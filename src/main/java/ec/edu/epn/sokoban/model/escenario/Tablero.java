package ec.edu.epn.sokoban.model.escenario;

import ec.edu.epn.sokoban.model.interfaces.Transitable;
import ec.edu.epn.sokoban.model.interfaces.Dibujador;

/**
 * Matriz bidimensional que gestiona las casillas y entidades del escenario.
 */
public class Tablero extends Casilla {
    private int filas;
    private int columnas;
    private Casilla[][] celdas;
    private boolean[][] metas;
    private Personaje personaje;
    private Casilla[][] casillasBase;

    public Tablero() {
        this(new Casilla[0][0], new boolean[0][0], null);
    }

    public Tablero(Casilla[][] celdas, boolean[][] metas, Personaje personaje) {
        super(0, 0);
        this.celdas = celdas != null ? celdas : new Casilla[0][0];
        this.metas = metas != null ? metas : new boolean[0][0];
        this.filas = this.celdas.length;
        this.columnas = this.filas > 0 ? this.celdas[0].length : 0;
        this.personaje = personaje;

        this.casillasBase = new Casilla[filas][columnas];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Casilla casilla = this.celdas[f][c];
                if (casilla instanceof Personaje || casilla instanceof Caja) {
                    if (esCeldaMeta(f, c)) {
                        this.casillasBase[f][c] = new Meta(f, c);
                    } else {
                        this.casillasBase[f][c] = new Suelo(f, c);
                    }
                } else {
                    this.casillasBase[f][c] = casilla;
                }
            }
        }
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public boolean esCeldaMeta(int f, int c) {
        return estaDentroDelTablero(f, c) && metas[f][c];
    }

    public Casilla obtenerCasilla(int f, int c) {
        if (estaDentroDelTablero(f, c)) {
            return celdas[f][c];
        }
        return null;
    }

    public void actualizarCasilla(int f, int c, Casilla nuevaCasilla) {
        if (!estaDentroDelTablero(f, c)) {
            return;
        }

        if (nuevaCasilla != null) {
            int oldFila = nuevaCasilla.getFila();
            int oldColumna = nuevaCasilla.getColumna();

            if ((nuevaCasilla instanceof Personaje || nuevaCasilla instanceof Caja)
                    && (estaDentroDelTablero(oldFila, oldColumna) && celdas[oldFila][oldColumna] == nuevaCasilla)
                    && (oldFila != f || oldColumna != c)) {
                liberarPosicion(oldFila, oldColumna);
            } else if (nuevaCasilla instanceof Personaje || nuevaCasilla instanceof Caja) {
                boolean encontrado = false;
                for (int fIdx = 0; fIdx < filas; fIdx++) {
                    for (int cIdx = 0; cIdx < columnas; cIdx++) {
                        if (celdas[fIdx][cIdx] == nuevaCasilla) {
                            if (fIdx != f || cIdx != c) {
                                liberarPosicion(fIdx, cIdx);
                            }
                            encontrado = true;
                            break;
                        }
                    }
                    if (encontrado) {
                        break;
                    }
                }
            }

            nuevaCasilla.setFila(f);
            nuevaCasilla.setColumna(c);

            if (nuevaCasilla instanceof Meta) {
                this.metas[f][c] = true;
            } else if (nuevaCasilla instanceof Personaje personaje) {
                this.personaje = personaje;
            } else if (nuevaCasilla instanceof Caja caja) {
                caja.setEnMeta(esCeldaMeta(f, c));
            }
        }

        celdas[f][c] = nuevaCasilla;
    }

    /**
     * Reemplaza el terreno de una posicion de forma PERMANENTE (por ejemplo,
     * cuando una pared es destruida por una explosion). A diferencia de
     * actualizarCasilla(), este metodo tambien actualiza casillasBase, para
     * que restaurarCasillaBase() (invocado cuando un Personaje o Caja
     * abandona esa posicion) no reconstruya el terreno anterior.
     *
     * @param f            fila de la posicion a modificar
     * @param c            columna de la posicion a modificar
     * @param nuevoTerreno la nueva casilla de terreno (por ejemplo, un Suelo)
     */
    public void reemplazarTerrenoPermanente(int f, int c, Casilla nuevoTerreno) {
        if (!estaDentroDelTablero(f, c)) {
            return;
        }
        casillasBase[f][c] = nuevoTerreno;
        actualizarCasilla(f, c, nuevoTerreno);
    }

    public boolean esCeldaTransitable(int f, int c) {
        Casilla casilla = obtenerCasilla(f, c);
        return casilla instanceof Transitable transitable && transitable.verificarTransitabilidad();
    }

    public boolean estaDentroDelTablero(int fila, int columna) {
        return fila >= 0 && fila < filas && columna >= 0 && columna < columnas;
    }

    public boolean esCeldaCaja(int fila, int columna) {
        return obtenerCasilla(fila, columna) instanceof Caja;
    }

    public Caja obtenerCajaEnPosicion(int fila, int columna) {
        Casilla casilla = obtenerCasilla(fila, columna);
        return casilla instanceof Caja ? (Caja) casilla : null;
    }

    private void liberarPosicion(int fila, int columna) {
        restaurarCasillaBase(fila, columna);
    }

    /**
     * Restaura la casilla de fondo original en la coordenada especificada.
     */
    public void restaurarCasillaBase(int f, int c) {
        if (estaDentroDelTablero(f, c) && casillasBase != null) {
            Casilla reemplazo = casillasBase[f][c];
            actualizarCasilla(f, c, reemplazo);
        }
    }

    public Personaje getPersonaje() {
        return personaje;
    }

    public Casilla obtenerCasillaBase(int f, int c) {
        if (estaDentroDelTablero(f, c) && casillasBase != null) {
            return casillasBase[f][c];
        }
        return null;
    }

    @Override
    public <T> void dibujar(Dibujador<T> dibujador, T contenedor, int tamCelda) {
        // Intencionalmente vacío: el Tablero no se dibuja a sí mismo;
        // el renderizado de las casillas lo realiza cada elemento por separado.
    }
}
