package ec.edu.epn.sokoban.model.escenario;

import ec.edu.epn.sokoban.model.interfaces.Accion;

/**
 * Suelo que pasa de intacto a agrietado y luego a roto antes de reiniciar el
 * nivel.
 */
public class Agrietado implements Accion {
    private static Runnable notificadorReinicio;
    private boolean estaAgrietado;
    private boolean roto;

    public boolean isAgrietado() {
        return estaAgrietado;
    }

    public void setAgrietado(boolean agrietado) {
        this.estaAgrietado = agrietado;
    }

    public boolean isRoto() {
        return roto;
    }

    public void setRoto(boolean roto) {
        this.roto = roto;
    }

    public static void registrarNotificadorReinicio(Runnable accion) {
        notificadorReinicio = accion;
    }

    @Override
    public void iniciarAccion(Casilla casillaActual, Tablero tablero, Casilla entidad) {
        if (!(entidad instanceof Personaje)) {
            return;
        }

        if (roto) {
            if (notificadorReinicio != null) {
                notificadorReinicio.run();
            }
        } else if (estaAgrietado) {
            roto = true;
        } else {
            estaAgrietado = true;
        }
    }

    @Override
    public String getSpriteKey() {
        if (roto) {
            return "AGRIETADO_ROTO";
        }
        return estaAgrietado ? "AGRIETADO" : null;
    }
}