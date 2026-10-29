package ec.edu.epn.sokoban.model.factory;

import ec.edu.epn.sokoban.model.escenario.Casilla;
import ec.edu.epn.sokoban.model.escenario.Meta;
import ec.edu.epn.sokoban.model.escenario.Personaje;
import ec.edu.epn.sokoban.model.escenario.Tablero;

/**
 * Clase encargada de validar, estructurar e inicializar un tablero.
 */
public class CargadorTablero {

    /**
     * Carga y retorna una instancia de Tablero completamente validada y sincronizada.
     *
     * @param celdas la matriz bidimensional de casillas
     * @param metas  la matriz de metas registradas
     * @return un Tablero inicializado
     */
    public static Tablero cargar(Casilla[][] celdas, boolean[][] metas) {
        validarMatriz(celdas);
        int filas = celdas.length;
        int columnas = filas > 0 ? celdas[0].length : 0;

        boolean[][] metasValidadas = metas;
        if (metasValidadas == null || metasValidadas.length != filas
                || (filas > 0 && metasValidadas[0].length != columnas)) {
            metasValidadas = new boolean[filas][columnas];
        }

        Personaje personaje = sincronizarCoordenadasYBuscarPersonaje(celdas, metasValidadas);

        return new Tablero(celdas, metasValidadas, personaje);
    }

    private static void validarMatriz(Casilla[][] celdas) {
        if (celdas == null) {
            throw new IllegalArgumentException("La matriz de celdas no puede ser nula.");
        }
        int filas = celdas.length;
        if (filas < 0) {
            throw new IllegalArgumentException("Las dimensiones del tablero no pueden ser negativas.");
        }
        int columnas = filas > 0 ? celdas[0].length : 0;
        for (Casilla[] fila : celdas) {
            if (fila == null || fila.length != columnas) {
                throw new IllegalArgumentException("La matriz de casillas debe ser rectangular.");
            }
        }
    }

    private static Personaje sincronizarCoordenadasYBuscarPersonaje(Casilla[][] celdas, boolean[][] metas) {
        Personaje personaje = null;
        int filas = celdas.length;
        int columnas = filas > 0 ? celdas[0].length : 0;

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                Casilla casilla = celdas[fila][columna];
                if (casilla != null) {
                    casilla.setFila(fila);
                    casilla.setColumna(columna);
                    
                    if (casilla instanceof Meta) {
                        metas[fila][columna] = true;
                    }
                    if (casilla instanceof Personaje p) {
                        personaje = p;
                    }
                }
            }
        }
        return personaje;
    }
}
