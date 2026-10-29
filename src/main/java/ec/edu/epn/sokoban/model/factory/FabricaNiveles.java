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
        if (n == null || n.getMapaDatos() == null) {
            throw new IllegalArgumentException("El nivel no tiene datos de mapa válidos");
        }

        String[][] mapaDatos = n.getMapaDatos();
        int filas = mapaDatos.length;
        int columnas = (filas > 0) ? mapaDatos[0].length : 0;

        Casilla[][] celdas = new Casilla[filas][columnas];
        boolean[][] metas = new boolean[filas][columnas];

        List<int[]> portales = new ArrayList<>();
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if ("T".equals(mapaDatos[f][c])) {
                    portales.add(new int[] { f, c });
                }
            }
        }

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                String simbolo = mapaDatos[f][c];
                celdas[f][c] = crearCasilla(simbolo, f, c, metas, portales);
            }
        }

        return CargadorTablero.cargar(celdas, metas);
    }

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
                Suelo sueloAgrietado = new Suelo(fila, columna);
                sueloAgrietado.getGestorAcciones().agregarAccion(new Agrietado());
                return sueloAgrietado;

            case ".":
                metas[fila][columna] = true;
                return new Meta(fila, columna);

            case "$":
                return new Caja(fila, columna);

            case "*":
                Caja cajaEnMeta = new Caja(fila, columna);
                cajaEnMeta.setEnMeta(true);
                metas[fila][columna] = true;
                return cajaEnMeta;

            case "X":
                Caja cajaExplosiva = new Caja(fila, columna);
                cajaExplosiva.getGestorAcciones().agregarAccion(new Explosion());
                return cajaExplosiva;

            case "@":
                return new Personaje(fila, columna);

            case "+":
                metas[fila][columna] = true;
                return new Personaje(fila, columna);

            case "L": // Grupo 2: suelo con acción de Lava
                Suelo sueloLava = new Suelo(fila, columna);
                sueloLava.getGestorAcciones().agregarAccion(new Lava());
                return sueloLava;

            case "T":
                Suelo sueloPortal = new Suelo(fila, columna);

                int indicePortalActual = -1;

                for (int i = 0; i < portales.size(); i++) {
                    int[] coordenadasPortal = portales.get(i);

                    if (coordenadasPortal[0] == fila && coordenadasPortal[1] == columna) {
                        indicePortalActual = i;
                        break;
                    }
                }

                if (indicePortalActual != -1) {
                    int indicePortalPareja = (indicePortalActual % 2 == 0)
                            ? indicePortalActual + 1
                            : indicePortalActual - 1;

                    if (indicePortalPareja >= 0 && indicePortalPareja < portales.size()) {
                        int[] coordenadasPortalPareja = portales.get(indicePortalPareja);

                        sueloPortal.getGestorAcciones().agregarAccion(
                                new Teletransportacion(coordenadasPortalPareja[0], coordenadasPortalPareja[1]));
                    }
                }

                return sueloPortal;

            case "?": // CASILLA DE AZAR (Misterio/Suerte con símbolo '?')
                Suelo sueloAzar = new Suelo(fila, columna);
                sueloAzar.getGestorAcciones().agregarAccion(new Azar(0.80));
                return sueloAzar;

            default:
                return new Suelo(fila, columna);
        }
    }
}
