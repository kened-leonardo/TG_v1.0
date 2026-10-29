package ec.edu.epn.sokoban.model.factory;

import ec.edu.epn.sokoban.model.escenario.Agrietado;
import ec.edu.epn.sokoban.model.escenario.Azar;
import ec.edu.epn.sokoban.model.escenario.Caja;
import ec.edu.epn.sokoban.model.escenario.Casilla;
import ec.edu.epn.sokoban.model.escenario.Explosion;
import ec.edu.epn.sokoban.model.escenario.Lava;
import ec.edu.epn.sokoban.model.escenario.Meta;
import ec.edu.epn.sokoban.model.escenario.Pared;
import ec.edu.epn.sokoban.model.escenario.Personaje;
import ec.edu.epn.sokoban.model.escenario.Suelo;
import ec.edu.epn.sokoban.model.escenario.Tablero;
import ec.edu.epn.sokoban.model.escenario.Teletransportacion;
import ec.edu.epn.sokoban.model.historial.Nivel;
import ec.edu.epn.sokoban.model.interfaces.Accion;

import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica encargada de instanciar y armar el tablero a partir del mapa de datos
 * del nivel.
 */
public class FabricaNiveles {

    public FabricaNiveles() {
        // Constructor vacío intencional: la fábrica no mantiene estado
        // ni requiere inicialización.
    }

    public Tablero construirTablero(Nivel n) {
        validarNivel(n);

        String[][] mapaDatos = n.getMapaDatos();
        int filas = mapaDatos.length;
        int columnas = (filas > 0) ? mapaDatos[0].length : 0;

        boolean[][] metas = new boolean[filas][columnas];
        List<int[]> portales = localizarPortales(mapaDatos, filas, columnas);
        Casilla[][] celdas = crearCeldas(mapaDatos, filas, columnas, metas, portales);

        return CargadorTablero.cargar(celdas, metas);
    }

    // ------------------------------------------------------------------
    // Pasos de construcción del tablero
    // ------------------------------------------------------------------

    private void validarNivel(Nivel n) {
        if (n == null || n.getMapaDatos() == null) {
            throw new IllegalArgumentException("El nivel no tiene datos de mapa válidos");
        }
    }

    private List<int[]> localizarPortales(String[][] mapaDatos, int filas, int columnas) {
        List<int[]> portales = new ArrayList<>();
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if ("T".equals(mapaDatos[f][c])) {
                    portales.add(new int[] { f, c });
                }
            }
        }
        return portales;
    }

    private Casilla[][] crearCeldas(String[][] mapaDatos, int filas, int columnas,
            boolean[][] metas, List<int[]> portales) {
        Casilla[][] celdas = new Casilla[filas][columnas];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                celdas[f][c] = crearCasilla(mapaDatos[f][c], f, c, metas, portales);
            }
        }
        return celdas;
    }

    // ------------------------------------------------------------------
    // Creación de cada tipo de casilla
    // ------------------------------------------------------------------

    private Casilla crearCasilla(String simbolo, int fila, int columna, boolean[][] metas, List<int[]> portales) {
        if (simbolo == null) {
            return new Suelo(fila, columna);
        }

        switch (simbolo) {
            case "#":
                return new Pared(fila, columna);
            case " ":
                return new Suelo(fila, columna);
            case "A":
                return crearSueloConAccion(fila, columna, new Agrietado());
            case ".":
                return crearMeta(fila, columna, metas);
            case "$":
                return new Caja(fila, columna);
            case "*":
                return crearCajaEnMeta(fila, columna, metas);
            case "X":
                return crearCajaExplosiva(fila, columna);
            case "@":
                return new Personaje(fila, columna);
            case "+":
                return crearPersonajeEnMeta(fila, columna, metas);
            case "L": // Grupo 2: suelo con acción de Lava
                return crearSueloConAccion(fila, columna, new Lava());
            case "T":
                return crearSueloPortal(fila, columna, portales);
            case "?": // CASILLA DE AZAR (Misterio/Suerte con símbolo '?')
                return crearSueloConAccion(fila, columna, new Azar(0.80));
            default:
                return new Suelo(fila, columna);
        }
    }

    private Suelo crearSueloConAccion(int fila, int columna, Accion accion) {
        Suelo suelo = new Suelo(fila, columna);
        suelo.getGestorAcciones().agregarAccion(accion);
        return suelo;
    }

    private Meta crearMeta(int fila, int columna, boolean[][] metas) {
        metas[fila][columna] = true;
        return new Meta(fila, columna);
    }

    private Caja crearCajaEnMeta(int fila, int columna, boolean[][] metas) {
        Caja caja = new Caja(fila, columna);
        caja.setEnMeta(true);
        metas[fila][columna] = true;
        return caja;
    }

    private Caja crearCajaExplosiva(int fila, int columna) {
        Caja caja = new Caja(fila, columna);
        caja.getGestorAcciones().agregarAccion(new Explosion());
        return caja;
    }

    private Personaje crearPersonajeEnMeta(int fila, int columna, boolean[][] metas) {
        metas[fila][columna] = true;
        return new Personaje(fila, columna);
    }

    // ------------------------------------------------------------------
    // Portales: cada pareja (0-1, 2-3, ...) se teletransporta entre sí
    // ------------------------------------------------------------------

    private Suelo crearSueloPortal(int fila, int columna, List<int[]> portales) {
        Suelo sueloPortal = new Suelo(fila, columna);

        int indicePortalActual = buscarIndicePortal(portales, fila, columna);

        if (indicePortalActual != -1) {
            int indicePortalPareja = calcularIndicePortalPareja(indicePortalActual);

            if (indicePortalPareja >= 0 && indicePortalPareja < portales.size()) {
                int[] coordenadasPortalPareja = portales.get(indicePortalPareja);

                sueloPortal.getGestorAcciones().agregarAccion(
                        new Teletransportacion(coordenadasPortalPareja[0], coordenadasPortalPareja[1]));
            }
        }

        return sueloPortal;
    }

    private int buscarIndicePortal(List<int[]> portales, int fila, int columna) {
        for (int i = 0; i < portales.size(); i++) {
            int[] coordenadasPortal = portales.get(i);

            if (coordenadasPortal[0] == fila && coordenadasPortal[1] == columna) {
                return i;
            }
        }
        return -1;
    }

    private int calcularIndicePortalPareja(int indicePortalActual) {
        return (indicePortalActual % 2 == 0)
                ? indicePortalActual + 1
                : indicePortalActual - 1;
    }
}