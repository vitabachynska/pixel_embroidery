package packageFiles;

import javafx.animation.PauseTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
    private static final int COLS = Main.COLS;
    private static final int CELL_SIZE = Main.CELL_SIZE;

    protected static Canvas canvas;
    private static Color[][] grid = new Color[COLS][COLS];
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

    public static void initGrid(Canvas newCanvas) {
        canvas = newCanvas;
        clearGrid();
    }

    public static void clearGrid() {
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < COLS; y++) {
                grid[x][y] = Color.WHITE;
            }
        }
        drawGrid();
    }

    public static void applyTrueBrodyAlgorithm(String word, BorderPane root) {
        drawStepWithTimeout(word, 0, 2, root);
    }

    private static void drawStepWithTimeout(String word, int step, int radius, BorderPane root) {
        if (step >= word.length()) {
            root.setBottom(Main.bottomButtonsContainer);
            return;
        }

        int center = COLS / 2;
        char letter = word.charAt(step);
        int style = brodyLetterStyles.getOrDefault(letter, 1);
        Color stepColor = (step % 2 == 0) ? Color.RED : Color.BLACK;

        switch (style) {
            case 1: // Ромб
                for (int i = 0; i <= radius; i++) {
                    int j = radius - i;
                    markCell(center + i, center + j, stepColor);
                    markCell(center - i, center + j, stepColor);
                    markCell(center + i, center - j, stepColor);
                    markCell(center - i, center - j, stepColor);
                }
                radius += 2;
                break;

            case 2: // Квадрат
                for (int i = -radius; i <= radius; i++) {
                    markCell(center + i, center - radius, stepColor);
                    markCell(center + i, center + radius, stepColor);
                    markCell(center - radius, center + i, stepColor);
                    markCell(center + radius, center + i, stepColor);
                }
                radius += 2;
                break;

            case 3: // Квітка
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
        if (x >= 0 && x < COLS && y >= 0 && y < COLS) {
            grid[x][y] = color;
        }
    }

    public static void handleMouseAction(double mouseX, double mouseY, boolean hSymmetry, boolean vSymmetry) {
        int x = (int) (mouseX / CELL_SIZE);
        int y = (int) (mouseY / CELL_SIZE);

        if (x >= 0 && x < COLS && y >= 0 && y < COLS) {
            grid[x][y] = currentColor;
            if (hSymmetry && vSymmetry) {grid[COLS - 1 - x][COLS - 1 - y] = currentColor;}
            if (hSymmetry) {grid[COLS - 1 - x][y] = currentColor;}
            if (vSymmetry) {grid[x][COLS - 1 - y] = currentColor;}
            drawGrid();
        }
    }
    protected static void drawGrid() {
        if (canvas == null) return;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < COLS; y++) {
                if (grid[x][y] != Color.WHITE) {
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


    public static void saveToPNG(Stage stage) {
        if (canvas == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти схему вишивки");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Файли (*.png)", "*.png"));
        fileChooser.setInitialFileName("vyshyvka_scheme.png");

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
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

                clearGrid();

                for (int x = 0; x < COLS; x++) {
                    for (int y = 0; y < COLS; y++) {
                        int pixelX = x * CELL_SIZE + (CELL_SIZE / 2);
                        int pixelY = y * CELL_SIZE + (CELL_SIZE / 2);

                        if (pixelX < image.getWidth() && pixelY < image.getHeight()) {
                            Color color = pr.getColor(pixelX, pixelY);

                            if (color.getOpacity() > 0.1 && (color.getRed() < 0.95 || color.getGreen() < 0.95 || color.getBlue() < 0.95)) {
                                grid[x][y] = color;
                            }
                        }
                    }
                }
                drawGrid();
                root.setLeft(sideMenu);
                root.setBottom(null);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /*public static void loadFromPNG(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Відкрити схему вишивки");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Зображення (*.png)", "*.png"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                WritableImage image = SwingFXUtils.toFXImage(ImageIO.read(file), null);
                PixelReader pr = image.getPixelReader();

                clearGrid();
                for (int x = 0; x < COLS; x++) {
                    for (int y = 0; y < COLS; y++) {
                        int pixelX = x * CELL_SIZE + (CELL_SIZE / 2);
                        int pixelY = y * CELL_SIZE + (CELL_SIZE / 2);

                        if (pixelX < image.getWidth() && pixelY < image.getHeight()) {
                            Color color = pr.getColor(pixelX, pixelY);

                            if (color.getOpacity() > 0.1 && (color.getRed() < 0.95 || color.getGreen() < 0.95 || color.getBlue() < 0.95)) {
                                grid[x][y] = color;
                            }
                        }
                    }
                }
                drawGrid();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }*/
}
