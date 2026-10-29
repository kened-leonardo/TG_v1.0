package ec.edu.epn.sokoban.controller;

import ec.edu.epn.sokoban.model.JuegoSokoban;
import ec.edu.epn.sokoban.model.escenario.Agrietado;
import ec.edu.epn.sokoban.model.escenario.Azar;
import ec.edu.epn.sokoban.model.escenario.Lava;
import ec.edu.epn.sokoban.model.escenario.Tablero;
import ec.edu.epn.sokoban.model.historial.Nivel;
import ec.edu.epn.sokoban.model.persistencia.GestorPersistencia;
import ec.edu.epn.sokoban.view.Creditos;
import ec.edu.epn.sokoban.view.MenuInicio;
import ec.edu.epn.sokoban.view.SeleccionNivel;
import ec.edu.epn.sokoban.view.VentanaPrincipal;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.ArrayList;

public class GestorVentanas {
    private static final String TITULO_JUEGO = "Sokoban";

    private final Stage stage;
    private final JuegoSokoban juego;
    private final GestorPersistencia gestorPersistencia;

    public GestorVentanas(Stage stage) {

        this.stage = stage;
        this.juego = new JuegoSokoban(new ArrayList<Nivel>());
        this.gestorPersistencia = new GestorPersistencia("progress.txt");
        cargarNivelesDisponibles();

    }

    /**
     * Muestra la ventana principal del juego.
     */
    public void mostrarJuego(Tablero tablero) {

        VentanaPrincipal ventana = new VentanaPrincipal(juego, tablero, this);

        class FlujoMovimiento {
            ec.edu.epn.sokoban.model.historial.PartidaMomento estadoAnterior;
        }
        final FlujoMovimiento flujo = new FlujoMovimiento();

        ControladorTeclado controlador = new ControladorTeclado(
            tablero.getPersonaje(), 
            tablero, 
            juego.getReglasJuego().getGestorColisiones()
        );
        controlador.setVentanaPrincipal(ventana);
        controlador.setCheckNivelCompletado(() ->
            juego.getNivelActual() != null && juego.getNivelActual().isCompletado());
        
        controlador.setAntesDeMover(() -> {
            flujo.estadoAnterior = juego.capturarEstadoActual();
        });
        
        controlador.setDespuesDeMover(() -> {
            if (flujo.estadoAnterior != null) {
                juego.getHistorial().registrarEstado(flujo.estadoAnterior);
                flujo.estadoAnterior = null;
            }
            juego.registrarVictoria();
        });
        
        controlador.setAccionDeshacer(() -> {
            juego.deshacerUltimaAccion();
            Tablero tableroRestaurado = juego.getTableroActual();
            controlador.setTablero(tableroRestaurado);
            ventana.actualizarTablero(tableroRestaurado);
            ventana.actualizarEstadisticas();
        });

        // Callback compartido de derrota (Lava, Agrietado, Azar): al perder muestra el overlay de derrota.
        Runnable accionDerrota = () -> Platform.runLater(() -> {
            ventana.mostrarOverlayDerrota();
        });

        Lava.registrarNotificadorDerrota(accionDerrota);
        Agrietado.registrarNotificadorReinicio(accionDerrota);
        Azar.registrarNotificadorDerrota(accionDerrota);

        ventana.setControladorTeclado(controlador);

        Scene scene = new Scene(ventana, 1280, 720);
        scene.setOnKeyPressed(controlador);

        stage.setTitle(TITULO_JUEGO);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    /**
     * Abre el nivel seleccionado.
     * Temporalmente crea un tablero de prueba.
     * Cuando GestorPersistencia implemente la lectura de los TXT,
     * este método deberá cargar el tablero correspondiente.
     */
    public void abrirNivel(int numeroNivel) {
        if (numeroNivel <= 0 || numeroNivel > juego.getNivelesDisponibles().size()) {
            return;
        }

        Nivel nivel = juego.getNivelesDisponibles().get(numeroNivel - 1);
        juego.seleccionarNivel(nivel);
        if (juego.getNivelActual() == nivel) {
            mostrarJuego(juego.getTableroActual());
        }
    }

    /**
     * Muestra el menú principal.
     */
    public void mostrarMenu() {

        MenuInicio menu = new MenuInicio(this);

        Scene scene = new Scene(menu, 1280, 720);

        stage.setTitle(TITULO_JUEGO);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Muestra la pantalla de selección de niveles.
     */
    public void mostrarSeleccionNiveles() {

        SeleccionNivel seleccion = new SeleccionNivel(juego, this);

        Scene scene = new Scene(seleccion, 1280, 720);

        stage.setScene(scene);
        stage.show();

    }

    /**
     * Inicia una nueva partida desde el primer nivel.
     */
    public void nuevaPartida() {
        for (Nivel nivel : juego.getNivelesDisponibles()) {
            nivel.setCompletado(false);
        }
        gestorPersistencia.guardarProgreso(juego.getNivelesDisponibles());
        abrirNivel(1);
    }

    /**
     * Muestra la ventana de créditos.
     */
    public void mostrarCreditos() {

        Creditos creditos = new Creditos(this);

        Scene scene = new Scene(creditos, 1280, 720);

        stage.setTitle(TITULO_JUEGO);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Cierra la aplicación.
     */
    public void salir() {

        stage.close();

    }

    private void cargarNivelesDisponibles() {
        int nivelId = 1;
        java.util.List<Integer> completados = gestorPersistencia.cargarProgreso();
        while (true) {
            Nivel nivel = gestorPersistencia.cargarNivelDesdeRecursos(nivelId);
            if (nivel == null) {
                break;
            }
            if (completados.contains(nivelId)) {
                nivel.marcarComoCompletado();
            }
            juego.agregarNivel(nivel);
            nivelId++;
        }
    }

}
