package ec.edu.epn.sokoban.model.escenario;

import ec.edu.epn.sokoban.model.interfaces.Accion;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Accion concreta que representa una casilla de Azar.
 */
public class Azar implements Accion {

    private static final Logger LOGGER =
            Logger.getLogger(Azar.class.getName());

    private final Random random;
    private final double probabilidadExito;



    /** Reaccion ante la derrota o resultado desfavorable. */
    private static Runnable notificadorDerrota;

    public Azar(double probabilidadExito) {
        this.probabilidadExito = Math.max(0.0, Math.min(1.0, probabilidadExito));
        this.random = new Random();
    }

    public Azar() {
        this(0.80);
    }

    /**
     * Registra la reaccion a ejecutar cuando ocurre un resultado desfavorable.
     */
    public static void registrarNotificadorDerrota(Runnable accion) {
        notificadorDerrota = accion;
    }

    @Override
    public void iniciarAccion(Casilla casillaActual, Tablero tablero, Casilla entidad) {
        boolean tuvoSuerte = random.nextDouble() < probabilidadExito;

        if (tuvoSuerte) {
            LOGGER.info("Tuviste suerte: la casilla es segura.");
        } else {
            LOGGER.info("Mala suerte: la casilla activo trampa.");

            if (entidad instanceof Personaje) {
                LOGGER.info("El personaje ha caido en la trampa.");
                tablero.restaurarCasillaBase(entidad.getFila(), entidad.getColumna());
                if (notificadorDerrota != null) {
                    notificadorDerrota.run();
                }
            } else if (entidad instanceof Caja) {
                LOGGER.info("La caja fue destruida por la trampa.");
                tablero.restaurarCasillaBase(entidad.getFila(), entidad.getColumna());
                if (notificadorDerrota != null) {
                    notificadorDerrota.run();
                }
            }
        }
    }

    @Override
    public boolean puedeIngresarEntidad(Tablero tablero, Casilla entidad) {
        return true;
    }

    @Override
    public String getSpriteKey() {
        return "AZAR";
    }
}