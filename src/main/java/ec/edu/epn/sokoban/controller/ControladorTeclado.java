package ec.edu.epn.sokoban.controller;

import ec.edu.epn.sokoban.Direccion;
import ec.edu.epn.sokoban.model.escenario.Personaje;
import ec.edu.epn.sokoban.model.escenario.Tablero;
import ec.edu.epn.sokoban.model.reglas.GestorColisiones;
import ec.edu.epn.sokoban.view.VentanaPrincipal;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.function.BooleanSupplier;

/**
 * Event handler de teclado de JavaFX.
 * Se comunica directamente con la clase Personaje y Tablero para invocar el
 * movimiento,
 * utilizando el GestorColisiones único.
 */
public class ControladorTeclado implements EventHandler<KeyEvent> {

    private Personaje personaje;
    private Tablero tablero;
    private final GestorColisiones gestorColisiones;
    private VentanaPrincipal ventanaPrincipal;
    private Runnable antesDeMover;
    private Runnable despuesDeMover;
    private Runnable accionDeshacer;
    private BooleanSupplier checkNivelCompletado;

    private KeyCode ultimoCodigo = null;
    private long ultimoMovimientoTiempo = 0;

    public ControladorTeclado(Personaje personaje, Tablero tablero, GestorColisiones gestorColisiones) {
        this.personaje = personaje;
        this.tablero = tablero;
        this.gestorColisiones = gestorColisiones;
    }

    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
    }

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
        if (tablero != null) {
            this.personaje = tablero.getPersonaje();
        }
    }

    public void setVentanaPrincipal(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
    }

    public void setAntesDeMover(Runnable antesDeMover) {
        this.antesDeMover = antesDeMover;
    }

    public void setDespuesDeMover(Runnable despuesDeMover) {
        this.despuesDeMover = despuesDeMover;
    }

    public void setAccionDeshacer(Runnable accionDeshacer) {
        this.accionDeshacer = accionDeshacer;
    }

    public void setCheckNivelCompletado(BooleanSupplier checkNivelCompletado) {
        this.checkNivelCompletado = checkNivelCompletado;
    }

    // ------------------------------------------------------------------
    // Método principal: ahora solo orquesta, cada paso tiene nombre propio
    // ------------------------------------------------------------------
    @Override
    public void handle(KeyEvent evento) {
        if (esNivelCompletado()) {
            return;
        }

        KeyCode codigo = evento.getCode();

        if (esTeclaDeshacer(codigo)) {
            procesarDeshacer(codigo);
            return;
        }

        Direccion direccion = obtenerDireccion(codigo);
        if (direccion == null) {
            return; // Ignora cualquier otra tecla
        }

        if (!registrarPulsacionSiPermitida(codigo, calcularCooldownMovimiento(codigo))) {
            return;
        }

        procesarMovimiento(direccion);
    }

    // ----------------------- Extract Method -----------------------

    private boolean esNivelCompletado() {
        return checkNivelCompletado != null && checkNivelCompletado.getAsBoolean();
    }

    private boolean esTeclaDeshacer(KeyCode codigo) {
        return codigo == KeyCode.Z || codigo == KeyCode.BACK_SPACE || codigo == KeyCode.U;
    }

    private void procesarDeshacer(KeyCode codigo) {
        if (accionDeshacer != null && registrarPulsacionSiPermitida(codigo, 40)) {
            accionDeshacer.run();
        }
    }

    private Direccion obtenerDireccion(KeyCode codigo) {
        return switch (codigo) {
            case UP, W -> Direccion.ARRIBA;
            case DOWN, S -> Direccion.ABAJO;
            case LEFT, A -> Direccion.IZQUIERDA;
            case RIGHT, D -> Direccion.DERECHA;
            default -> null;
        };
    }

    private long calcularCooldownMovimiento(KeyCode codigo) {
        return (codigo == ultimoCodigo) ? 50 : 20;
    }

    /**
     * Verifica el tiempo mínimo entre pulsaciones y, si se cumple, registra la
     * pulsación.
     */
    private boolean registrarPulsacionSiPermitida(KeyCode codigo, long cooldownRequerido) {
        long ahora = System.currentTimeMillis();
        if (ahora - ultimoMovimientoTiempo < cooldownRequerido) {
            return false;
        }
        ultimoMovimientoTiempo = ahora;
        ultimoCodigo = codigo;
        return true;
    }

    private Personaje resolverPersonajeActual() {
        Personaje actual = (tablero != null && tablero.getPersonaje() != null)
                ? tablero.getPersonaje()
                : personaje;
        if (actual != null) {
            this.personaje = actual;
        }
        return actual;
    }

    private void procesarMovimiento(Direccion direccion) {
        Personaje actual = resolverPersonajeActual();

        if (actual != null && tablero != null && gestorColisiones != null) {
            ejecutarSiExiste(antesDeMover);

            if (actual.mover(direccion, tablero, gestorColisiones)) {
                alMoverseConExito();
            }
        }
    }

    private void alMoverseConExito() {
        ejecutarSiExiste(despuesDeMover);
        actualizarVista();
    }

    private void actualizarVista() {
        if (ventanaPrincipal != null) {
            ventanaPrincipal.actualizarTablero(tablero);
            ventanaPrincipal.actualizarEstadisticas();
        }
    }

    private void ejecutarSiExiste(Runnable accion) {
        if (accion != null) {
            accion.run();
        }
    }
}