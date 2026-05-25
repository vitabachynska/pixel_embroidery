package packageFiles;

import javafx.animation.PauseTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DrawLogic {
    protected static int COLS = Main.COLS;
    protected static int ROWS = Main.COLS;
    private static final int CELL_SIZE = Main.CELL_SIZE;

    protected static Canvas canvas;
    protected static BorderPane rootContainer;

    private static final Color[][] grid = new Color[Main.COLS * 2][Main.COLS * 2];
    protected static Color currentColor = Color.RED;

    private static final Map<Character, Integer> brodyLetterStyles = new HashMap<>();

    static {
        brodyLetterStyles.put('А', 1); brodyLetterStyles.put('Б', 2); brodyLetterStyles.put('В', 3);
        brodyLetterStyles.put('Г', 1); brodyLetterStyles.put('Д', 2); brodyLetterStyles.put('Е', 3);
        brodyLetterStyles.put('Є', 1); brodyLetterStyles.put('Ж', 2); brodyLetterStyles.put('З', 3);
        brodyLetterStyles.put('И', 1); brodyLetterStyles.put('І', 2); brodyLetterStyles.put('Ї', 3);
        brodyLetterStyles.put('Й', 1); brodyLetterStyles.put('К', 2); brodyLetterStyles.put('Л', 3);
        brodyLetterStyles.put('М', 1); brodyLetterStyles.put('Н', 2); brodyLetterStyles.put('О', 3);
        brodyLetterStyles.put('П', 1); brodyLetterStyles.put('Р', 2); brodyLetterStyles.put('С', 3);
        brodyLetterStyles.put('Т', 1); brodyLetterStyles.put('У', 2); brodyLetterStyles.put('Ф', 3);
        brodyLetterStyles.put('Х', 1); brodyLetterStyles.put('Ц', 2); brodyLetterStyles.put('Ч', 3);
        brodyLetterStyles.put('Ш', 1); brodyLetterStyles.put('Щ', 2); brodyLetterStyles.put('Ь', 3);
        brodyLetterStyles.put('Ю', 1); brodyLetterStyles.put('Я', 2);
    }

    public static void initGrid(Canvas newCanvas, BorderPane root) {
        canvas = newCanvas;
        rootContainer = root;
        clearGrid();
    }

    public static void clearGrid() {
        COLS = Main.COLS;
        ROWS = Main.COLS;

        if (canvas != null) {
            canvas.setWidth(COLS * CELL_SIZE);
            canvas.setHeight(ROWS * CELL_SIZE);
        }
        // Очищаємо весь масив (з запасом) в білий колір
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                grid[x][y] = Color.WHITE;
            }
        }
        drawGrid();
    }

    public static void toggleHorizontalRepeat(boolean repeat) {
        int base = Main.COLS;
        if (repeat) {
            for (int x = 0; x < base; x++) {
                for (int y = 0; y < ROWS; y++) {
                    grid[x + base][y] = grid[x][y];
                }
            }
            COLS = base * 2;
        } else {COLS = base;}
        updateCanvasDimensions();
    }
    public static void toggleVerticalRepeat(boolean repeat) {
        int base = Main.COLS;
        if (repeat) {
            for (int x = 0; x < COLS; x++) {
                for (int y = 0; y < base; y++) {
                    grid[x][y + base] = grid[x][y];
                }
            }
            ROWS = base * 2;
        } else {
            ROWS = base;
        }
        updateCanvasDimensions();
    }

    private static void updateCanvasDimensions() {
        if (canvas != null) {
            canvas.setWidth(COLS * CELL_SIZE);
            canvas.setHeight(ROWS * CELL_SIZE);
            drawGrid();
        }
    }

    public static void applyTrueBrodyAlgorithm(String word, BorderPane root) {
        drawStepWithTimeout(word, 0, 2, root);
    }

    private static void drawStepWithTimeout(String word, int step, int radius, BorderPane root) {
        if (step >= word.length()) {
            root.setBottom(Main.bottomButtonsContainer);
            return;
        }

        int center = Main.COLS / 2;
        char letter = word.charAt(step);
        int style = brodyLetterStyles.getOrDefault(letter, 1);
        Color stepColor = (step % 2 == 0) ? Color.RED : Color.BLACK;

        switch (style) {
            case 1:
                for (int i = 0; i <= radius; i++) {
                    int j = radius - i;
                    markCell(center + i, center + j, stepColor);
                    markCell(center - i, center + j, stepColor);
                    markCell(center + i, center - j, stepColor);
                    markCell(center - i, center - j, stepColor);
                }
                radius += 2;
                break;

            case 2:
                for (int i = -radius; i <= radius; i++) {
                    markCell(center + i, center - radius, stepColor);
                    markCell(center + i, center + radius, stepColor);
                    markCell(center - radius, center + i, stepColor);
                    markCell(center + radius, center + i, stepColor);
                }
                radius += 2;
                break;

            case 3:
                for (int i = 1; i <= radius; i++) {
                    markCell(center, center - i, stepColor);
                    markCell(center, center + i, stepColor);
                    markCell(center - i, center, stepColor);
                    markCell(center + i, center, stepColor);
                }
                markCell(center - radius, center - radius, stepColor);
                markCell(center + radius, center - radius, stepColor);
                markCell(center - radius, center + radius, stepColor);
                markCell(center + radius, center + radius, stepColor);
                radius += 2;
                break;
        }
        drawGrid();

        PauseTransition pause = new PauseTransition(Duration.millis(450));
        final int nextRadius = radius;
        pause.setOnFinished(e -> drawStepWithTimeout(word, step + 1, nextRadius, root));
        pause.play();
    }

    private static void markCell(int x, int y, Color color) {
        if (x >= 0 && x < Main.COLS && y >= 0 && y < Main.COLS) {
            grid[x][y] = color;
        }
    }
    protected static void drawGrid() {
        if (canvas == null) return;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                if (grid[x][y] != Color.WHITE && grid[x][y] != null) {
                    gc.setStroke(grid[x][y]);
                    gc.setLineWidth(2.5);
                    double startX = x * CELL_SIZE + 2;
                    double startY = y * CELL_SIZE + 2;
                    double endX = (x + 1) * CELL_SIZE - 2;
                    double endY = (y + 1) * CELL_SIZE - 2;

                    gc.strokeLine(startX, startY, endX, endY);
                    gc.strokeLine(endX, startY, startX, endY);
                }
                gc.setStroke(Color.LIGHTGRAY);
                gc.setLineWidth(0.5);
                gc.strokeRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }
    public static void handleMouseAction(javafx.scene.input.MouseEvent event, double mouseX, double mouseY, boolean hSymmetry, boolean vSymmetry) {
        if (event.isControlDown()) {return;}
        int x = (int) (mouseX / CELL_SIZE);
        int y = (int) (mouseY / CELL_SIZE);

        if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
            Color colorToApply = (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) ? Color.WHITE : currentColor;

            int baseLetterX = x % Main.COLS;
            int baseLetterY = y % Main.COLS;

            applyCellWithSymmetry(baseLetterX, baseLetterY, colorToApply, hSymmetry, vSymmetry);

            int base = Main.COLS;
            if (COLS > base) {
                for (int ty = 0; ty < ROWS; ty++) {
                    for (int tx = 0; tx < base; tx++) {
                        grid[tx + base][ty] = grid[tx][ty];
                    }
                }
            }
            if (ROWS > base) {
                for (int tx = 0; tx < COLS; tx++) {
                    for (int ty = 0; ty < base; ty++) {
                        grid[tx][ty + base] = grid[tx][ty];
                    }
                }
            }
            drawGrid();
        }
    }

    private static void applyCellWithSymmetry(int x, int y, Color color, boolean hSym, boolean vSym) {
        int base = Main.COLS;
        grid[x][y] = color;
        if (hSym) grid[base - 1 - x][y] = color;
        if (vSym) grid[x][base - 1 - y] = color;
        if (hSym && vSym) grid[base - 1 - x][base - 1 - y] = color;
    }

    public static void saveToPNG(Stage stage) {
        if (canvas == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти схему вишивки");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Файли (*.png)", "*.png"));
        fileChooser.setInitialFileName("vyshyvka_scheme.png");

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                WritableImage finalImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, finalImage);
                ImageIO.write(SwingFXUtils.fromFXImage(finalImage, null), "png", file);
                System.out.println("Схему успішно збережено!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void loadFromPNG(Stage stage, BorderPane root, javafx.scene.layout.VBox sideMenu) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Відкрити схему вишивки");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Зображення (*.png)", "*.png"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                WritableImage image = SwingFXUtils.toFXImage(ImageIO.read(file), null);
                PixelReader pr = image.getPixelReader();

                COLS = (int) (image.getWidth() / CELL_SIZE);
                ROWS = (int) (image.getHeight() / CELL_SIZE);

                canvas.setWidth(COLS * CELL_SIZE);
                canvas.setHeight(ROWS * CELL_SIZE);

                for (int x = 0; x < COLS; x++) {
                    for (int y = 0; y < ROWS; y++) {
                        int pixelX = x * CELL_SIZE + (CELL_SIZE / 2);
                        int pixelY = y * CELL_SIZE + (CELL_SIZE / 2);

                        if (pixelX < image.getWidth() && pixelY < image.getHeight()) {
                            Color color = pr.getColor(pixelX, pixelY);
                            if (color.getOpacity() > 0.1 && (color.getRed() < 0.95 || color.getGreen() < 0.95 || color.getBlue() < 0.95)) {
                                grid[x][y] = color;
                            } else {
                                grid[x][y] = Color.WHITE;
                            }
                        }
                    }
                }
                drawGrid();

                root.setLeft(sideMenu);
                root.setBottom(null);

                javafx.scene.layout.HBox container = new javafx.scene.layout.HBox(canvas);
                container.setAlignment(javafx.geometry.Pos.CENTER);
                container.setPadding(new Insets(15));
                javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(container);
                scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0;");
                scrollPane.setPannable(true);
                root.setCenter(scrollPane);

                Button openBackButton = new Button("← Назад на головну");
                openBackButton.setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold;");
                openBackButton.setOnAction(ev -> new Main().start(stage));

                if (Main.topHeaderContainer.getChildren().size() < 3) {
                    Main.topHeaderContainer.getChildren().add(openBackButton);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
