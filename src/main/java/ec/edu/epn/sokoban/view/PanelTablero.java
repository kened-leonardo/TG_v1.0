package ec.edu.epn.sokoban.view;

import ec.edu.epn.sokoban.model.escenario.*;
import ec.edu.epn.sokoban.model.interfaces.Dibujador;
import ec.edu.epn.sokoban.model.interfaces.Accion;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Panel visual que representa el tablero de juego en la UI de JavaFX.
 * Implementa la interfaz Dibujador (Visitor) para aislar la visualización de
 * los modelos.
 */
public class PanelTablero extends GridPane implements Dibujador<StackPane> {
    private static final Logger LOGGER =
            Logger.getLogger(PanelTablero.class.getName());

    private Tablero tablero;

    private static final int ANCHO_MAX_TABLERO = 760;
    private static final int ALTO_MAX_TABLERO = 560;

    private static final int TAM_CELDA_MAX = 64;

    private static final int ESPACIO_ENTRE_CELDAS = 1;

    private int tamCelda = TAM_CELDA_MAX;

    private final Map<String, Image> sprites = new HashMap<>();
    private StackPane[][] celdasNodes;

    public PanelTablero(Tablero tablero) {
        this.tablero = tablero;
        configurarPanel();
        cargarSprites();
        calcularTamanoCelda();
        dibujarTablero();
    }

    public void actualizarTablero(Tablero nuevoTablero) {
        this.tablero = nuevoTablero;
        calcularTamanoCelda();
        dibujarTablero();
    }

    private void configurarPanel() {
        setAlignment(Pos.CENTER);
        setHgap(ESPACIO_ENTRE_CELDAS);
        setVgap(ESPACIO_ENTRE_CELDAS);
        setPadding(new Insets(18));

        setStyle(
                "-fx-background-color: linear-gradient(to bottom, #2B2F33, #151719);" +
                        "-fx-border-color: #4A4F55;" +
                        "-fx-border-width: 5;" +
                        "-fx-border-radius: 18;" +
                        "-fx-background-radius: 18;");

        DropShadow sombraExterior = new DropShadow();
        sombraExterior.setRadius(24);
        sombraExterior.setOffsetY(6);
        sombraExterior.setColor(Color.rgb(0, 0, 0, 0.65));

        setEffect(sombraExterior);
    }

    private void calcularTamanoCelda() {
        if (tablero == null) {
            tamCelda = TAM_CELDA_MAX;
            return;
        }

        int filas = tablero.getFilas();
        int columnas = tablero.getColumnas();

        if (filas <= 0 || columnas <= 0) {
            tamCelda = TAM_CELDA_MAX;
            return;
        }

        int tamPorAncho = ANCHO_MAX_TABLERO / columnas;
        int tamPorAlto = ALTO_MAX_TABLERO / filas;

        tamCelda = Math.min(tamPorAncho, tamPorAlto);

        if (tamCelda > TAM_CELDA_MAX) {
            tamCelda = TAM_CELDA_MAX;
        }

        if (tamCelda < 1) {
            tamCelda = 1;
        }
    }

    private void cargarSprites() {
        sprites.put("PARED", cargarImagen("/images/wall.png"));
        sprites.put("SUELO", cargarImagen("/images/floor.png"));
        sprites.put("META", cargarImagen("/images/goal.png"));
        sprites.put("CAJA", cargarImagen("/images/box.png"));
        sprites.put("JUGADOR", cargarImagen("/images/player.png"));
        sprites.put("AZAR", cargarImagen("/images/azar.png"));
        sprites.put("AGRIETADO", cargarImagen("/images/agrietado.png"));
        sprites.put("AGRIETADO_ROTO", cargarImagen("/images/agrietado_roto.png"));
        sprites.put("CAJA_EXPLOSIVA", cargarImagen("/images/explosionBox.png"));
        sprites.put("LAVA", cargarImagen("/images/lava.png"));
        sprites.put("PORTAL", cargarImagen("/images/portal.png"));
    }

    private Image cargarImagen(String ruta) {
        InputStream stream = getClass().getResourceAsStream(ruta);

        if (stream == null) {
            LOGGER.warning("No se encontró la imagen: " + ruta);
            return null;
        }

        return new Image(stream);
    }

    private void inicializarEstructuraGrid() {
        if (tablero == null) {
            return;
        }

        int filas = tablero.getFilas();
        int columnas = tablero.getColumnas();

        if (filas <= 0 || columnas <= 0) {
            return;
        }

        if (celdasNodes != null
                && celdasNodes.length == filas
                && celdasNodes[0].length == columnas) {
            for (int f = 0; f < filas; f++) {
                for (int c = 0; c < columnas; c++) {
                    StackPane celda = celdasNodes[f][c];
                    celda.setPrefSize(tamCelda, tamCelda);
                    celda.setMinSize(tamCelda, tamCelda);
                    celda.setMaxSize(tamCelda, tamCelda);
                }
            }
            return;
        }

        getChildren().clear();
        celdasNodes = new StackPane[filas][columnas];

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                StackPane celda = new StackPane();
                celda.setAlignment(Pos.CENTER);
                celda.setPrefSize(tamCelda, tamCelda);
                celda.setMinSize(tamCelda, tamCelda);
                celda.setMaxSize(tamCelda, tamCelda);

                celdasNodes[fila][columna] = celda;
                add(celda, columna, fila);
            }
        }
    }

    private void dibujarTablero() {
        if (tablero == null) {
            return;
        }

        inicializarEstructuraGrid();

        int filas = tablero.getFilas();
        int columnas = tablero.getColumnas();

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                StackPane celda = celdasNodes[fila][columna];
                celda.getChildren().clear();

                Casilla casilla = tablero.obtenerCasilla(fila, columna);
                if (casilla != null) {
                    casilla.dibujar(this, celda, tamCelda);
                }
            }
        }
    }

    private void agregarSueloBase(StackPane celda) {
        Image spriteSuelo = sprites.get("SUELO");

        if (spriteSuelo != null) {
            ImageView suelo = crearImageView(spriteSuelo, tamCelda);
            celda.getChildren().add(suelo);
        } else {
            celda.getChildren().add(crearFondoRespaldo(Color.web("#D9C58A")));
        }

        celda.setStyle("-fx-border-color: rgba(0, 0, 0, 0.35); -fx-border-width: 0.5px;");
    }

    private void agregarSprite(StackPane celda, String claveSprite, Color colorRespaldo) {
        Image sprite = sprites.get(claveSprite);

        if (sprite != null) {
            double tamanioSprite = obtenerTamanoSprite(claveSprite);

            ImageView imageView = crearImageView(sprite, tamanioSprite);

            if (!claveSprite.equals("PARED")) {
                DropShadow sombra = new DropShadow();
                sombra.setRadius(6);
                sombra.setOffsetY(3);
                sombra.setColor(Color.rgb(0, 0, 0, 0.35));
                imageView.setEffect(sombra);
            }

            celda.getChildren().add(imageView);
        } else {
            celda.getChildren().add(crearFondoRespaldo(colorRespaldo));
        }
    }

    private double obtenerTamanoSprite(String claveSprite) {
        return switch (claveSprite) {
            case "CAJA" -> tamCelda * 0.78;
            case "JUGADOR" -> tamCelda * 0.88;
            case "META" -> tamCelda * 0.97;
            default -> tamCelda;
        };
    }

    private ImageView crearImageView(Image imagen, double tamanio) {
        ImageView imageView = new ImageView(imagen);
        imageView.setFitWidth(tamanio);
        imageView.setFitHeight(tamanio);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(false);
        imageView.setCache(true);
        return imageView;
    }

    private Rectangle crearFondoRespaldo(Color color) {
        Rectangle rect = new Rectangle(tamCelda, tamCelda);
        rect.setFill(color);
        return rect;
    }

    // =========================================================================
    // Métodos del patrón Visitor (Dibujador)
    // =========================================================================

    @Override
    public void dibujarPared(Pared pared, StackPane celda, int tamCelda) {
        celda.getChildren().clear();
        Image sprite = sprites.get("PARED");
        if (sprite != null) {
            ImageView imageView = crearImageView(sprite, tamCelda);
            celda.getChildren().add(imageView);
        } else {
            celda.getChildren().add(crearFondoRespaldo(Color.web("#3A3A3A")));
        }
    }

    @Override
    public void dibujarCaja(Caja caja, StackPane celda, int tamCelda) {
        agregarSueloBase(celda);
        dibujarAccionesDeCasilla(caja.getFila(), caja.getColumna(), celda);
        if (caja.isEnMeta()) {
            agregarSprite(celda, "META", Color.web("#F4D35E"));
        }

        String spriteKey = "CAJA";
        for (Accion accion : caja.getGestorAcciones().getAcciones()) {
            String key = accion.getSpriteKey();
            if (key != null && !key.isEmpty()) {
                spriteKey = key;
                break;
            }
        }

        Color colorRespaldo = spriteKey.equals("CAJA") ? Color.web("#B8793B")
                : obtenerColorRespaldoParaAccion(spriteKey);
        agregarSprite(celda, spriteKey, colorRespaldo);
    }

    @Override
    public void dibujarPersonaje(Personaje personaje, StackPane celda, int tamCelda) {
        agregarSueloBase(celda);
        dibujarAccionesDeCasilla(personaje.getFila(), personaje.getColumna(), celda);
        if (tablero != null && tablero.esCeldaMeta(personaje.getFila(), personaje.getColumna())) {
            agregarSprite(celda, "META", Color.web("#F4D35E"));
        }
        agregarSprite(celda, "JUGADOR", Color.web("#4DA6FF"));
    }

    @Override
    public void dibujarMeta(Meta meta, StackPane celda, int tamCelda) {
        agregarSueloBase(celda);
        dibujarAccionesDeCasilla(meta.getFila(), meta.getColumna(), celda);
        agregarSprite(celda, "META", Color.web("#F4D35E"));
    }

    @Override
    public void dibujarSuelo(Suelo suelo, StackPane celda, int tamCelda) {
        agregarSueloBase(celda);
        dibujarAccionesDeCasilla(suelo.getFila(), suelo.getColumna(), celda);
    }

    private void dibujarAccionesDeCasilla(int fila, int columna, StackPane celda) {
        if (tablero == null)
            return;
        Casilla casilla = tablero.obtenerCasillaBase(fila, columna);
        if (casilla != null) {
            for (Accion accion : casilla.getGestorAcciones().getAcciones()) {
                String spriteKey = accion.getSpriteKey();
                if (spriteKey != null && !spriteKey.isEmpty()) {
                    Color colorRespaldo = obtenerColorRespaldoParaAccion(spriteKey);
                    agregarSprite(celda, spriteKey, colorRespaldo);
                }
            }
        }
    }

    private Color obtenerColorRespaldoParaAccion(String spriteKey) {
        return switch (spriteKey) {
            case "AZAR" -> Color.web("#FFD700"); // Color dorado de respaldo
            case "AGRIETADO" -> Color.web("#6B5140");
            case "AGRIETADO_ROTO" -> Color.web("#1F1712");
            case "LAVA" -> Color.web("#FF4500"); // Grupo 2: respaldo naranja/rojo
            case "PORTAL" -> Color.web("#8A2BE2");
            case "CAJA_EXPLOSIVA" -> Color.web("#D32F2F");
            default -> Color.TRANSPARENT;
        };
    }
}