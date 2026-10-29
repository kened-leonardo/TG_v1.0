package ec.edu.epn.sokoban.model.escenario;

import ec.edu.epn.sokoban.Direccion;
import ec.edu.epn.sokoban.model.interfaces.Dibujador;
import ec.edu.epn.sokoban.model.reglas.GestorColisiones;

/**
 * Clase abstracta base para los componentes del escenario.
 */
public abstract class Casilla {
    private int fila;
    private int columna;
    private final GestorAcciones gestorAcciones;

    protected Casilla() {
        this.gestorAcciones = new GestorAcciones();
    }

    protected Casilla(int fila, int columna) {
        this.gestorAcciones = new GestorAcciones();
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public GestorAcciones getGestorAcciones() {
        return gestorAcciones;
    }

    /**
     * Intenta mover la casilla en el tablero en la dirección dada usando un gestor
     * de colisiones.
     * Por defecto las casillas no se mueven (retorna false).
     * Las subclases (como Personaje o Caja) sobrescriben este comportamiento.
     *
     * @param d                la dirección del movimiento
     * @param t                el tablero sobre el cual se realiza el movimiento
     * @param gestorColisiones el gestor de colisiones único
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    public boolean mover(Direccion d, Tablero t, GestorColisiones gestorColisiones) {
        return false;
    }

    /**
     * Delega el dibujo de la casilla al dibujador (Patrón Visitor).
     */
    public abstract <T> void dibujar(Dibujador<T> dibujador, T contenedor, int tamCelda);
}
