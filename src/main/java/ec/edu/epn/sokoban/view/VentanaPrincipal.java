package ec.edu.epn.sokoban.view;

import ec.edu.epn.sokoban.controller.ControladorTeclado;
import ec.edu.epn.sokoban.controller.GestorVentanas;
import ec.edu.epn.sokoban.model.JuegoSokoban;
import ec.edu.epn.sokoban.model.escenario.Caja;
import ec.edu.epn.sokoban.model.escenario.Casilla;
import ec.edu.epn.sokoban.model.escenario.Tablero;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class VentanaPrincipal extends BorderPane {

    private final PanelTablero panelTablero;
    private final GestorVentanas gestorVentanas;
    private final JuegoSokoban juego;

    private Label lblNivel;
    private Label lblMovimientosSuperior;
    private Label lblCajasEstado;
    private Label lblMovimientosEstado;

    private Button btnReiniciar;
    private Button btnRetroceder;
    private Button btnMenu;
    private ControladorTeclado controladorTeclado;
    private StackPane zonaTablero;

    public VentanaPrincipal(
            JuegoSokoban juego,
            Tablero tablero,
            GestorVentanas gestorVentanas) {

        this.juego = juego;
        this.gestorVentanas = gestorVentanas;

        this.panelTablero = new PanelTablero(tablero);

        configurarVentana();
        construirInterfaz();
        configurarEventos();
        actualizarEstadisticas();
    }

    private void configurarVentana() {
        setStyle(
                "-fx-background-color: " +
                        "linear-gradient(to bottom, #10202B 0%, #0B0F12 45%, #050607 100%);");
    }

    private void configurarEventos() {

        btnMenu.setOnAction(e -> gestorVentanas.mostrarMenu());

        btnReiniciar.setOnAction(e -> {
            juego.reiniciarNivelActual();
            Tablero nuevoTablero = juego.getTableroActual();
            actualizarTablero(nuevoTablero);
            if (controladorTeclado != null) {
                controladorTeclado.setTablero(nuevoTablero);
            }
            actualizarEstadisticas();
        });

        btnRetroceder.setOnAction(e -> {
            juego.deshacerUltimaAccion();
            Tablero tableroRestaurado = juego.getTableroActual();
            if (controladorTeclado != null) {
                controladorTeclado.setTablero(tableroRestaurado);
            }
            actualizarTablero(tableroRestaurado);
            actualizarEstadisticas();
        });

    }

    private void construirInterfaz() {
        setTop(crearBarraSuperior());
        setLeft(crearPanelIzquierdo());
        setCenter(crearZonaTablero());
        setBottom(crearBarraConsejo());
    }

    private HBox crearBarraSuperior() {
        HBox barra = new HBox(14);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(18, 22, 12, 22));
        barra.setStyle("-fx-background-color: transparent;");

        Label titulo = new Label("SOKOBAN");
        titulo.setMinWidth(240);
        titulo.setPrefWidth(240);
        titulo.setMaxWidth(240);
        titulo.setAlignment(Pos.CENTER);
        titulo.setStyle(
                "-fx-text-fill: #F6B43A;" +
                        "-fx-font-size: 34px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 18 8 18;" +
                        "-fx-background-color: linear-gradient(to bottom, #3A2717, #17100B);" +
                        "-fx-border-color: #7A4A1D;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;");

        DropShadow sombraTitulo = new DropShadow();
        sombraTitulo.setRadius(14);
        sombraTitulo.setOffsetY(4);
        sombraTitulo.setColor(Color.rgb(0, 0, 0, 0.65));
        titulo.setEffect(sombraTitulo);

        Region espacio1 = new Region();
        HBox.setHgrow(espacio1, Priority.ALWAYS);

        lblNivel = crearEtiquetaSuperior("★  Nivel 1", "#F6B43A");
        lblMovimientosSuperior = crearEtiquetaSuperior("Movimientos: 0", "#5BA8FF");

        Region espacio2 = new Region();
        HBox.setHgrow(espacio2, Priority.ALWAYS);

        btnReiniciar = crearBotonSuperior("↻ Reiniciar", "#105BA5", "#2F8FEA");
        btnRetroceder = crearBotonSuperior("← Retroceder", "#246A1F", "#56B047");
        btnMenu = crearBotonSuperior("☰ Menú", "#53217A", "#9B4DD9");

        barra.getChildren().addAll(
                titulo,
                espacio1,
                lblNivel,
                lblMovimientosSuperior,
                espacio2,
                btnReiniciar,
                btnRetroceder,
                btnMenu);

        return barra;
    }

    private Label crearEtiquetaSuperior(String texto, String colorIcono) {
        Label label = new Label(texto);
        label.setMinWidth(175);
        label.setPrefWidth(175);
        label.setMaxWidth(175);
        label.setAlignment(Pos.CENTER);
        label.setStyle(
                "-fx-text-fill: #F2F2F2;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 12 10 12;" +
                        "-fx-background-color: rgba(15, 15, 15, 0.82);" +
                        "-fx-border-color: #5A4025;" +
                        "-fx-border-width: 1.8;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;");

        DropShadow sombra = new DropShadow();
        sombra.setRadius(8);
        sombra.setOffsetY(3);
        sombra.setColor(Color.rgb(0, 0, 0, 0.45));
        label.setEffect(sombra);

        return label;
    }

    private Button crearBotonSuperior(String texto, String colorBase, String colorBorde) {
        Button boton = new Button(texto);
        boton.setMinWidth(118);
        boton.setPrefWidth(118);
        boton.setMaxWidth(118);
        boton.setMinHeight(46);
        boton.setFocusTraversable(false);

        boton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " + colorBorde + ", " + colorBase + ");" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgba(255,255,255,0.28);" +
                        "-fx-border-width: 2;" +
                        "-fx-cursor: hand;");

        DropShadow sombra = new DropShadow();
        sombra.setRadius(10);
        sombra.setOffsetY(3);
        sombra.setColor(Color.rgb(0, 0, 0, 0.45));
        boton.setEffect(sombra);

        return boton;
    }

    private VBox crearPanelIzquierdo() {
        VBox contenedor = new VBox(18);
        contenedor.setAlignment(Pos.TOP_CENTER);
        contenedor.setPadding(new Insets(38, 10, 10, 24));
        contenedor.setPrefWidth(245);
        contenedor.setMinWidth(245);
        contenedor.setMaxWidth(245);

        VBox panelObjetivo = crearTarjetaLateral("OBJETIVO", "#7B2A24");

        Label iconoObjetivo = new Label("◎");
        iconoObjetivo.setStyle(
                "-fx-text-fill: #E94B4B;" +
                        "-fx-font-size: 44px;" +
                        "-fx-font-weight: bold;");

        Label textoObjetivo = new Label("Empuja las cajas\nhasta las metas.");
        textoObjetivo.setAlignment(Pos.CENTER);
        textoObjetivo.setStyle(
                "-fx-text-fill: #F0E8D8;" +
                        "-fx-font-size: 14px;" +
                        "-fx-line-spacing: 3px;");

        panelObjetivo.getChildren().addAll(iconoObjetivo, textoObjetivo);

        VBox panelEstado = crearTarjetaLateral("ESTADO", "#2C5F26");

        HBox filaCajas = crearFilaEstado("▧", "Cajas en su lugar:", "0 / 1", "#D48935", "#6ED36E");
        HBox filaMovimientos = crearFilaEstado("••", "Movimientos:", "0", "#4AA3FF", "#4AA3FF");

        VBox textosCajas = (VBox) filaCajas.getChildren().get(1);
        VBox textosMovimientos = (VBox) filaMovimientos.getChildren().get(1);

        lblCajasEstado = (Label) textosCajas.getChildren().get(1);
        lblMovimientosEstado = (Label) textosMovimientos.getChildren().get(1);

        panelEstado.getChildren().addAll(filaCajas, crearSeparadorOscuro(), filaMovimientos);

        contenedor.getChildren().addAll(panelObjetivo, panelEstado);

        return contenedor;
    }

    private VBox crearTarjetaLateral(String titulo, String colorCabecera) {
        VBox tarjeta = new VBox(12);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.setPrefWidth(210);
        tarjeta.setMinWidth(210);
        tarjeta.setMaxWidth(210);
        tarjeta.setMinHeight(170);
        tarjeta.setPadding(new Insets(0, 0, 16, 0));

        tarjeta.setStyle(
                "-fx-background-color: rgba(12, 12, 10, 0.88);" +
                        "-fx-border-color: #6B4A2A;" +
                        "-fx-border-width: 2.5;" +
                        "-fx-border-radius: 16;" +
                        "-fx-background-radius: 16;");

        Label cabecera = new Label(titulo);
        cabecera.setMaxWidth(Double.MAX_VALUE);
        cabecera.setAlignment(Pos.CENTER);
        cabecera.setPadding(new Insets(10, 8, 10, 8));
        cabecera.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " + colorCabecera + ", #1B1B14);" +
                        "-fx-text-fill: #F5E5B8;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 14 14 0 0;" +
                        "-fx-border-color: rgba(255,255,255,0.12);" +
                        "-fx-border-width: 0 0 1.5 0;");

        tarjeta.getChildren().add(cabecera);

        DropShadow sombra = new DropShadow();
        sombra.setRadius(12);
        sombra.setOffsetY(4);
        sombra.setColor(Color.rgb(0, 0, 0, 0.55));
        tarjeta.setEffect(sombra);

        return tarjeta;
    }

    private HBox crearFilaEstado(String icono, String texto, String valor, String colorIcono, String colorValor) {
        HBox fila = new HBox(10);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(2, 18, 2, 18));

        Label lblIcono = new Label(icono);
        lblIcono.setMinWidth(34);
        lblIcono.setAlignment(Pos.CENTER);
        lblIcono.setStyle(
                "-fx-text-fill: " + colorIcono + ";" +
                        "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;");

        VBox textos = new VBox(1);
        textos.setAlignment(Pos.CENTER_LEFT);

        Label lblTexto = new Label(texto);
        lblTexto.setStyle(
                "-fx-text-fill: #F0E8D8;" +
                        "-fx-font-size: 13px;");

        Label lblValor = new Label(valor);
        lblValor.setStyle(
                "-fx-text-fill: " + colorValor + ";" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;");

        textos.getChildren().addAll(lblTexto, lblValor);
        fila.getChildren().addAll(lblIcono, textos);

        return fila;
    }

    private Region crearSeparadorOscuro() {
        Region separador = new Region();
        separador.setPrefHeight(1);
        separador.setMaxWidth(210);
        separador.setStyle("-fx-background-color: rgba(255,255,255,0.12);");
        return separador;
    }

    private StackPane crearZonaTablero() {
        StackPane zona = new StackPane();
        this.zonaTablero = zona;
        zona.setAlignment(Pos.CENTER);
        zona.setPadding(new Insets(18, 24, 18, 0));

        zona.getChildren().add(panelTablero);

        return zona;
    }

    private HBox crearBarraConsejo() {
        HBox contenedor = new HBox();
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(0, 24, 16, 260));

        HBox barra = new HBox(16);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(14, 26, 14, 26));
        barra.setMaxWidth(760);

        barra.setStyle(
                "-fx-background-color: rgba(15, 12, 8, 0.88);" +
                        "-fx-border-color: #5A3B1D;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 16;" +
                        "-fx-background-radius: 16;");

        Label foco = new Label("!");
        foco.setAlignment(Pos.CENTER);
        foco.setMinWidth(34);
        foco.setMinHeight(34);
        foco.setStyle(
                "-fx-text-fill: #FFD15C;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #FFD15C;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-background-radius: 50%;");

        Label tituloConsejo = new Label("Consejo:");
        tituloConsejo.setStyle(
                "-fx-text-fill: #FFD15C;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;");

        Label textoConsejo = new Label("Planifica tus movimientos y piensa antes de empujar.");
        textoConsejo.setStyle(
                "-fx-text-fill: #F0E8D8;" +
                        "-fx-font-size: 16px;");

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        Button btnConfig = new Button("⚙");
        btnConfig.setMinSize(42, 36);
        btnConfig.setFocusTraversable(false);
        btnConfig.setStyle(
                "-fx-background-color: rgba(255,255,255,0.08);" +
                        "-fx-text-fill: #B9A177;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: rgba(255,255,255,0.12);" +
                        "-fx-cursor: hand;");

        barra.getChildren().addAll(foco, tituloConsejo, textoConsejo, espacio, btnConfig);
        contenedor.getChildren().add(barra);

        return contenedor;
    }

    public void actualizarTablero(Tablero nuevoTablero) {
        panelTablero.actualizarTablero(nuevoTablero);
        if (zonaTablero != null && zonaTablero.getChildren().size() > 1) {
            zonaTablero.getChildren().remove(1, zonaTablero.getChildren().size());
        }
    }

    public void actualizarEstadisticas() {
        int indexNivel = juego.getNivelesDisponibles().indexOf(juego.getNivelActual());
        actualizarNivel(indexNivel >= 0 ? indexNivel + 1 : 1);

        int movimientos = juego.getHistorial().getMovimientosContador();
        actualizarMovimientos(movimientos);

        int totalCajas = 0;
        int cajasEnMeta = 0;
        Tablero t = juego.getTableroActual();
        if (t != null) {
            for (int f = 0; f < t.getFilas(); f++) {
                for (int c = 0; c < t.getColumnas(); c++) {
                    Casilla casilla = t.obtenerCasilla(f, c);
                    if (casilla instanceof Caja caja) {
                        totalCajas++;
                        if (caja.isEnMeta()) {
                            cajasEnMeta++;
                        }
                    }
                }
            }
        }
        actualizarCajasEstado(cajasEnMeta, totalCajas);

        boolean completado = juego.getNivelActual() != null && juego.getNivelActual().isCompletado();
        btnReiniciar.setDisable(completado);
        btnRetroceder.setDisable(completado);
        if (completado) {
            mostrarOverlayVictoria();
        }
    }

    private void mostrarOverlayVictoria() {
        if (zonaTablero == null || zonaTablero.getChildren().size() > 1) {
            return;
        }

        VBox overlay = new VBox(25);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle(
                "-fx-background-color: rgba(5, 7, 10, 0.88);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #6ED36E;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 16;" +
                        "-fx-max-width: 450px;" +
                        "-fx-max-height: 280px;");

        DropShadow sombra = new DropShadow();
        sombra.setRadius(25);
        sombra.setColor(Color.rgb(110, 211, 110, 0.4));
        overlay.setEffect(sombra);

        Label lblMensaje = new Label("¡HAS GANADO!");
        lblMensaje.setStyle(
                "-fx-text-fill: #6ED36E;" +
                        "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;");
        overlay.getChildren().add(lblMensaje);

        int indexActual = juego.getNivelesDisponibles().indexOf(juego.getNivelActual());
        boolean haySiguiente = indexActual >= 0 && indexActual < juego.getNivelesDisponibles().size() - 1;
        boolean todosCompletados = juego.getNivelesDisponibles() != null && !juego.getNivelesDisponibles().isEmpty()
                && juego.getNivelesDisponibles().stream().allMatch(ec.edu.epn.sokoban.model.historial.Nivel::isCompletado);

        Button btnAccion = new Button();
        if (todosCompletados) {
            lblMensaje.setText("¡JUEGO COMPLETADO!");
            Label lblSub = new Label("¡Has superado todos los niveles!");
            lblSub.setStyle("-fx-text-fill: #F0E8D8; -fx-font-size: 16px;");
            overlay.getChildren().add(lblSub);

            btnAccion.setText("Volver al Menú");
            btnAccion.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #F6B43A, #A86A18);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: rgba(255,255,255,0.3);" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;" +
                            "-fx-pref-width: 220px;" +
                            "-fx-pref-height: 45px;");
            btnAccion.setOnMouseEntered(e -> btnAccion.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #FFD36B, #D48A23);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: rgba(255,255,255,0.4);" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;" +
                            "-fx-pref-width: 220px;" +
                            "-fx-pref-height: 45px;"));
            btnAccion.setOnMouseExited(e -> btnAccion.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #F6B43A, #A86A18);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: rgba(255,255,255,0.3);" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;" +
                            "-fx-pref-width: 220px;" +
                            "-fx-pref-height: 45px;"));
            btnAccion.setOnAction(e -> gestorVentanas.mostrarMenu());
        } else if (haySiguiente) {
            btnAccion.setText("Siguiente Nivel");
            btnAccion.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #72DD72, #2B882B);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: rgba(255,255,255,0.3);" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;" +
                            "-fx-pref-width: 220px;" +
                            "-fx-pref-height: 45px;");
            btnAccion.setOnMouseEntered(e -> btnAccion.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #8BFF8B, #3CA43C);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: rgba(255,255,255,0.4);" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;" +
                            "-fx-pref-width: 220px;" +
                            "-fx-pref-height: 45px;"));
            btnAccion.setOnMouseExited(e -> btnAccion.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #72DD72, #2B882B);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: rgba(255,255,255,0.3);" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;" +
                            "-fx-pref-width: 220px;" +
                            "-fx-pref-height: 45px;"));
            btnAccion.setOnAction(e -> {
                int siguienteNivel = indexActual + 2;
                gestorVentanas.abrirNivel(siguienteNivel);
            });
        } else {
            btnAccion.setText("Volver al Menú");
            btnAccion.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #72DD72, #2B882B);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: rgba(255,255,255,0.3);" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;" +
                            "-fx-pref-width: 220px;" +
                            "-fx-pref-height: 45px;");
            btnAccion.setOnMouseEntered(e -> btnAccion.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #8BFF8B, #3CA43C);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: rgba(255,255,255,0.4);" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;" +
                            "-fx-pref-width: 220px;" +
                            "-fx-pref-height: 45px;"));
            btnAccion.setOnMouseExited(e -> btnAccion.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #72DD72, #2B882B);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-color: rgba(255,255,255,0.3);" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;" +
                            "-fx-pref-width: 220px;" +
                            "-fx-pref-height: 45px;"));
            btnAccion.setOnAction(e -> gestorVentanas.mostrarMenu());
        }

        overlay.getChildren().add(btnAccion);
        zonaTablero.getChildren().add(overlay);
    }

    public void mostrarOverlayDerrota() {
        if (zonaTablero == null || zonaTablero.getChildren().size() > 1) {
            return;
        }

        VBox overlay = new VBox(20);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle(
                "-fx-background-color: rgba(15, 5, 5, 0.90);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #E53935;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 16;" +
                        "-fx-max-width: 450px;" +
                        "-fx-max-height: 280px;");

        DropShadow sombra = new DropShadow();
        sombra.setRadius(25);
        sombra.setColor(Color.rgb(229, 57, 53, 0.5));
        overlay.setEffect(sombra);

        Label lblMensaje = new Label("¡HAS PERDIDO!");
        lblMensaje.setStyle(
                "-fx-text-fill: #FF5252;" +
                        "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;");

        Label lblSubtitulo = new Label("Has caído en una trampa o terreno peligroso.");
        lblSubtitulo.setStyle(
                "-fx-text-fill: #F0E8D8;" +
                        "-fx-font-size: 15px;");

        Button btnReintentar = new Button("Reintentar Nivel");
        btnReintentar.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #E53935, #B71C1C);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgba(255,255,255,0.3);" +
                        "-fx-border-width: 2;" +
                        "-fx-cursor: hand;" +
                        "-fx-pref-width: 220px;" +
                        "-fx-pref-height: 45px;");

        btnReintentar.setOnMouseEntered(e -> btnReintentar.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #FF5252, #D32F2F);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgba(255,255,255,0.4);" +
                        "-fx-border-width: 2;" +
                        "-fx-cursor: hand;" +
                        "-fx-pref-width: 220px;" +
                        "-fx-pref-height: 45px;"));

        btnReintentar.setOnMouseExited(e -> btnReintentar.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #E53935, #B71C1C);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgba(255,255,255,0.3);" +
                        "-fx-border-width: 2;" +
                        "-fx-cursor: hand;" +
                        "-fx-pref-width: 220px;" +
                        "-fx-pref-height: 45px;"));

        btnReintentar.setOnAction(e -> {
            juego.reiniciarNivelActual();
            Tablero nuevoTablero = juego.getTableroActual();
            actualizarTablero(nuevoTablero);
            if (controladorTeclado != null) {
                controladorTeclado.setTablero(nuevoTablero);
            }
            actualizarEstadisticas();
        });

        overlay.getChildren().addAll(lblMensaje, lblSubtitulo, btnReintentar);
        zonaTablero.getChildren().add(overlay);
    }

    public void setControladorTeclado(ControladorTeclado controladorTeclado) {
        this.controladorTeclado = controladorTeclado;
    }

    public void actualizarMovimientos(int movimientos) {
        lblMovimientosSuperior.setText("Movimientos: " + movimientos);
        lblMovimientosEstado.setText(String.valueOf(movimientos));
    }

    public void actualizarNivel(int nivel) {
        lblNivel.setText("★  Nivel " + nivel);
    }

    public void actualizarCajasEstado(int cajasCorrectas, int totalCajas) {
        lblCajasEstado.setText(cajasCorrectas + " / " + totalCajas);
    }

    public Button getBtnReiniciar() {
        return btnReiniciar;
    }

    public Button getBtnRetroceder() {
        return btnRetroceder;
    }

    public Button getBtnMenu() {
        return btnMenu;
    }

    public PanelTablero getPanelTablero() {
        return panelTablero;
    }
}