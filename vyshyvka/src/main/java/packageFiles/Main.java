package packageFiles;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    protected static final int COLS = 41;
    protected static final int CELL_SIZE = 15;

    private Color currentColor = Color.RED;
    private CheckBox hSymmetryCheck;
    private CheckBox vSymmetryCheck;

    private Scene scene;
    private Stage primaryStage;
    private BorderPane root;
    protected static final String currentWord = "ВІТАЛІНА";

    protected static HBox bottomButtonsContainer;
    private VBox sideMenu;
    protected static VBox topHeaderContainer;

    protected static ScrollPane scrollPane;
    protected static HBox canvasContainer;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new BorderPane();

        Canvas canvas = new Canvas(COLS * CELL_SIZE, COLS * CELL_SIZE);
        DrawLogic.initGrid(canvas, root);

        topHeaderContainer = new VBox(5);
        topHeaderContainer.setAlignment(Pos.CENTER);
        topHeaderContainer.setPadding(new Insets(10, 0, 5, 0));
        topHeaderContainer.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");

        Label mainTitle = new Label("Редактор піксельної вишивки");
        mainTitle.setFont(new Font("Arial", 22));
        mainTitle.setStyle("-fx-font-weight: bold;");

        Label subTitle = new Label("Автор: Бачинська Віталіна");
        subTitle.setFont(new Font("Arial", 14));
        subTitle.setStyle("-fx-text-fill: #666666;");

        topHeaderContainer.getChildren().addAll(mainTitle, subTitle);
        root.setTop(topHeaderContainer);

        Button backButton = new Button("← Назад на головну");
        backButton.setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold;");
        backButton.setOnAction(e -> start(primaryStage));

        sideMenu = new VBox();
        sideMenu.setSpacing(12);
        sideMenu.setPadding(new Insets(15));
        sideMenu.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");

        ColorPicker colorPicker = new ColorPicker(currentColor);
        colorPicker.setOnAction(e -> {
            currentColor = colorPicker.getValue();
            DrawLogic.currentColor = currentColor;
        });

        Button clearButton = new Button("Очистити поле");
        clearButton.setOnAction(e -> DrawLogic.clearGrid());

        Button downloadButton = new Button("Зберегти малюнок");
        downloadButton.setPrefWidth(140);
        downloadButton.setOnAction(e -> DrawLogic.saveToPNG(primaryStage));

        Label symmetryLabel = new Label("Дзеркальне малювання:");
        symmetryLabel.setStyle("-fx-font-weight: bold;");
        hSymmetryCheck = new CheckBox("Горизонтальне");
        vSymmetryCheck = new CheckBox("Вертикальне");

        Label repeatLabel = new Label("Дублювання:");
        repeatLabel.setStyle("-fx-font-weight: bold;");

        Button btnRepeatH = new Button("Горизонтальне");
        btnRepeatH.setPrefWidth(140);
        btnRepeatH.setOnAction(e -> DrawLogic.addHorizontalRepeat());

        Button btnRepeatV = new Button("Вертикальне");
        btnRepeatV.setPrefWidth(140);
        btnRepeatV.setOnAction(e -> DrawLogic.addVerticalRepeat());

        sideMenu.getChildren().addAll(
                new Label("Оберіть колір:"), colorPicker, clearButton, downloadButton,
                new Label("---"),
                symmetryLabel, hSymmetryCheck, vSymmetryCheck,
                new Label("---"),
                repeatLabel, btnRepeatH, btnRepeatV
        );

        canvas.setOnMouseClicked(e -> {
            if (root.getLeft() != null) {
                DrawLogic.handleMouseAction(e, e.getX(), e.getY(), hSymmetryCheck.isSelected(), vSymmetryCheck.isSelected());
            }
        });
        canvas.setOnMouseDragged(e -> {
            if (root.getLeft() != null) {
                DrawLogic.handleMouseAction(e, e.getX(), e.getY(), hSymmetryCheck.isSelected(), vSymmetryCheck.isSelected());
            }
        });

        canvasContainer = new HBox(canvas);
        canvasContainer.setAlignment(Pos.CENTER);
        canvasContainer.setPadding(new Insets(15));

        scrollPane = new ScrollPane(canvasContainer);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0;");
        scrollPane.setPannable(false);
        root.setCenter(scrollPane);
        setupScrollAndPanHandlers(canvas, canvasContainer, scrollPane, root);

        bottomButtonsContainer = new HBox(20);
        bottomButtonsContainer.setAlignment(Pos.CENTER);
        bottomButtonsContainer.setPadding(new Insets(15));
        bottomButtonsContainer.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");

        Button openButton = new Button("Відкрити файл PNG");
        openButton.setPrefSize(180, 35);
        openButton.setOnAction(e -> DrawLogic.loadFromPNG(primaryStage, root, sideMenu));

        Button newFieldButton = new Button("Нове поле");
        newFieldButton.setPrefSize(160, 35);
        newFieldButton.setOnAction(e -> {
            DrawLogic.clearGrid();
            root.setLeft(sideMenu);
            root.setBottom(null);
            if (!topHeaderContainer.getChildren().contains(backButton)) {
                topHeaderContainer.getChildren().add(backButton);
            }
        });

        bottomButtonsContainer.getChildren().addAll(openButton, newFieldButton);

        root.setLeft(null);
        root.setBottom(null);

        scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("Піксельна вишивка | Автор: Бачинська Віталіна");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        DrawLogic.applyTrueBrodyAlgorithm(currentWord, root);
    }

    public static void setupScrollAndPanHandlers(Canvas targetCanvas, HBox targetContainer, ScrollPane targetScrollPane, BorderPane rootPane) {
        targetContainer.setOnScroll(e -> {
            if (e.isControlDown()) {
                e.consume();
                double zoomFactor = (e.getDeltaY() > 0) ? 1.1 : 0.9;
                double newScaleX = targetCanvas.getScaleX() * zoomFactor;
                double newScaleY = targetCanvas.getScaleY() * zoomFactor;

                if (newScaleX >= 0.3 && newScaleX <= 4.0) {
                    targetCanvas.setScaleX(newScaleX);
                    targetCanvas.setScaleY(newScaleY);
                }
            }
        });
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == javafx.scene.input.KeyCode.CONTROL) {
                        targetScrollPane.setPannable(true);
                    }
                });

                newScene.setOnKeyReleased(event -> {
                    if (event.getCode() == javafx.scene.input.KeyCode.CONTROL) {
                        targetScrollPane.setPannable(false);
                    }
                });
            }
        });
    }

    public static void main(String[] args) {launch(args);}
}