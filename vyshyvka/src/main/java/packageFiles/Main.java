package packageFiles;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int COLS = 50;
    private static final int ROWS = 40;
    private static final int CELL_SIZE = 16;

    private Color[][] grid = new Color[COLS][ROWS];

    private Color currentColor = Color.RED;

    private Canvas canvas;
    private CheckBox hSymmetryCheck;
    private CheckBox vSymmetryCheck;

    private Scene scene;
    private Stage primaryStage;

    /* public void start(Stage primaryStage){
        BorderPane root = new BorderPane();
        initGrid();

        HBox topToolbar = new HBox();
        topToolbar.setSpacing(15);
        topToolbar.setPadding(new Insets(10));
        topToolbar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");

        canvas = new Canvas(COLS * CELL_SIZE, ROWS * CELL_SIZE);
        canvas.setOnMouseClicked(e -> handleMouseAction(e.getX(), e.getY()));

        HBox canvasContainer = new HBox(canvas);
        canvasContainer.setPadding(new Insets(15));
        root.setCenter(canvasContainer);



        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("Піксельна вишивка. Бачинська Віталіна");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        Button newFieldButton = new Button("Нове поле");
        newFieldButton.setOnAction(e -> {
            drawing();
        });
        Button clearButton = new Button("Завантажити поле");
        clearButton.setOnAction(e -> {
        });

    }*/
    @Override
    public void start(Stage primaryStage) {
        VBox menuRoot = new VBox();
        menuRoot.setSpacing(20);
        menuRoot.setPadding(new Insets(30));
        menuRoot.setAlignment(Pos.CENTER);
        menuRoot.setStyle("-fx-background-color: #f5f5f5;");

        Label titleLabel = new Label("Редактор піксельної вишивки");
        titleLabel.setFont(new Font("Arial", 28));
        titleLabel.setStyle("-fx-font-weight: bold;");

        Label authorLabel = new Label("Автор: Бачинська Віталіна");
        authorLabel.setFont(new Font("Arial", 16));
        authorLabel.setStyle("-fx-text-fill: #555555;");

        Button newFieldButton = new Button("Нове поле");
        newFieldButton.setPrefSize(200, 40);
        newFieldButton.setOnAction(e -> {
            drawing();
        });

        Button loadFieldButton = new Button("Завантажити поле");
        loadFieldButton.setPrefSize(200, 40);
        loadFieldButton.setOnAction(e -> {
        });
        menuRoot.getChildren().addAll(titleLabel, authorLabel, newFieldButton, loadFieldButton);

        scene = new Scene(menuRoot, 1000, 800);

        primaryStage.setTitle("Піксельна вишивка | Автор: Бачинська Віталіна");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public void drawing() {
        BorderPane root = new BorderPane();

        initGrid();

        HBox topToolbar = new HBox();
        topToolbar.setSpacing(15);
        topToolbar.setPadding(new Insets(10));
        topToolbar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        Button backButton = new Button("Назад в меню");
        backButton.setOnAction(e -> {
            start(primaryStage);
        });
        ColorPicker colorPicker = new ColorPicker(currentColor);
        colorPicker.setOnAction(e -> {
            currentColor = colorPicker.getValue();
        });

        Button clearButton = new Button("Очистити поле");
        clearButton.setOnAction(e -> {
            initGrid();
            drawGrid();
        });


        topToolbar.getChildren().addAll(backButton, new Label("Оберіть колір хref:"), colorPicker, clearButton);
        root.setTop(topToolbar);

        VBox sideMenu = new VBox();
        sideMenu.setSpacing(15);
        sideMenu.setPadding(new Insets(15));
        sideMenu.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");

        Label symmetryLabel = new Label("Дзеркальне малювання:");
        hSymmetryCheck = new CheckBox("Горизонтальне");
        vSymmetryCheck = new CheckBox("Вертикальне");

        sideMenu.getChildren().addAll(symmetryLabel, hSymmetryCheck, vSymmetryCheck);
        root.setLeft(sideMenu);

        canvas = new Canvas(COLS * CELL_SIZE, ROWS * CELL_SIZE);
        canvas.setOnMouseClicked(e -> handleMouseAction(e.getX(), e.getY()));
        canvas.setOnMouseDragged(e -> handleMouseAction(e.getX(), e.getY()));

        HBox canvasContainer = new HBox(canvas);
        canvasContainer.setPadding(new Insets(15));
        root.setCenter(canvasContainer);

        drawGrid();

        scene.setRoot(root);
        //Scene scene = new Scene(root, 1000, 800);
        //primaryStage.setTitle("Піксельна вишивка. Бачинська Віталіна");
        //primaryStage.setScene(scene);
        //primaryStage.setResizable(false);
        //primaryStage.show();
    }

    private void initGrid() {
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                grid[x][y] = Color.WHITE;
            }
        }
    }

    private void handleMouseAction(double mouseX, double mouseY) {
        int x = (int) (mouseX / CELL_SIZE);
        int y = (int) (mouseY / CELL_SIZE);

        if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
            grid[x][y] = currentColor;

            if (hSymmetryCheck.isSelected()) {
                int mirrorX = COLS - 1 - x;
                grid[mirrorX][y] = currentColor;
            }

            if (vSymmetryCheck.isSelected()) {
                int mirrorY = ROWS - 1 - y;
                grid[x][mirrorY] = currentColor;
            }

            if (hSymmetryCheck.isSelected() && vSymmetryCheck.isSelected()) {
                int mirrorX = COLS - 1 - x;
                int mirrorY = ROWS - 1 - y;
                grid[mirrorX][mirrorY] = currentColor;
            }
            drawGrid();
        }
    }
    private void drawGrid() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
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

    public static void main(String[] args) {
        launch(args);
    }
}