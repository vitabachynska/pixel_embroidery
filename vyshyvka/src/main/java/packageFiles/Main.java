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
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    protected static int COLS = 5;
    protected static int ROWS = 5;
    protected static final int CELL_SIZE = 15;

    private Color currentColor = Color.RED;
    private CheckBox hSymmetryCheck;
    private CheckBox vSymmetryCheck;

    private Spinner<Integer> fragWidthSpinner;
    private Spinner<Integer> fragHeightSpinner;
    private Spinner<Integer> repeatHSpinner;
    private Spinner<Integer> repeatVSpinner;

    private Scene scene;
    private Stage primaryStage;
    private BorderPane root;
    protected static final String currentWord = "ВІТАЛІНА";

    protected static HBox bottomButtonsContainer;
    private VBox sideMenu;
    protected static VBox topHeaderContainer;
    protected static ScrollPane scrollPane;
    protected static HBox canvasContainer;
    protected static Canvas canvas;

    protected static boolean isAnimationPlayed = false;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new BorderPane();

        canvas = new Canvas(COLS * CELL_SIZE, ROWS * CELL_SIZE);
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
        sideMenu.setSpacing(10);
        sideMenu.setPadding(new Insets(12));
        sideMenu.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");

        ColorPicker colorPicker = new ColorPicker(currentColor);
        colorPicker.setOnAction(e -> {
            currentColor = colorPicker.getValue();
            DrawLogic.currentColor = currentColor;
        });

        Button clearButton = new Button("Очистити поле");
        clearButton.setOnAction(e -> DrawLogic.clearGrid());

        Button downloadButton = new Button("Зберегти в PNG");
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


        Label patternLabel = new Label("Генератор рамки орнаменту:");
        patternLabel.setStyle("-fx-font-weight: bold;");

        fragWidthSpinner = new Spinner<>(1, 30, 5);
        fragWidthSpinner.setPrefWidth(75);
        HBox fwBox = new HBox(5, new Label("Ширина фрагм.:"), fragWidthSpinner);
        fwBox.setAlignment(Pos.CENTER_LEFT);

        fragHeightSpinner = new Spinner<>(1, 30, 5);
        fragHeightSpinner.setPrefWidth(75);
        HBox fhBox = new HBox(5, new Label("Висота фрагм.:"), fragHeightSpinner);
        fhBox.setAlignment(Pos.CENTER_LEFT);

        repeatHSpinner = new Spinner<>(1, 50, 7);
        repeatHSpinner.setPrefWidth(75);
        HBox rhBox = new HBox(5, new Label("Повторів по X:"), repeatHSpinner);
        rhBox.setAlignment(Pos.CENTER_LEFT);

        repeatVSpinner = new Spinner<>(1, 50, 7);
        repeatVSpinner.setPrefWidth(75);
        HBox rvBox = new HBox(5, new Label("Повторів по Y:"), repeatVSpinner);
        rvBox.setAlignment(Pos.CENTER_LEFT);

        Button btnGenField = new Button("1. Створити сітку");
        btnGenField.setPrefWidth(165);
        btnGenField.setOnAction(e -> {
            int fw = fragWidthSpinner.getValue();
            int fh = fragHeightSpinner.getValue();
            DrawLogic.createInitialFragmentGrid(fw, fh);
        });

        Button btnFillBorder = new Button("2. Пустити по краю");
        btnFillBorder.setPrefWidth(165);
        btnFillBorder.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #a5d6a7; -fx-font-weight: bold;");
        btnFillBorder.setOnAction(e -> {
            int rx = repeatHSpinner.getValue();
            int ry = repeatVSpinner.getValue();
            DrawLogic.processBorderMultiplication(rx, ry);
        });

        Button invertButton = new Button("Інвертувати кольори");
        invertButton.setPrefWidth(140);
        invertButton.setOnAction(e -> DrawLogic.invertColors());

        sideMenu.getChildren().addAll(
                new Label("Оберіть колір:"), colorPicker, clearButton, downloadButton,
                symmetryLabel, hSymmetryCheck, vSymmetryCheck,
                new Label("---"),
                repeatLabel, btnRepeatH, btnRepeatV,
                new Label("---"),
                patternLabel, fwBox, fhBox, rhBox, rvBox, btnGenField, btnFillBorder,
                new Label("---"),
                invertButton
        );
        canvas.setOnMouseClicked(e -> DrawLogic.handleMouseAction(e, e.getX(), e.getY(), hSymmetryCheck.isSelected(), vSymmetryCheck.isSelected()));
        canvas.setOnMouseDragged(e -> DrawLogic.handleMouseAction(e, e.getX(), e.getY(), hSymmetryCheck.isSelected(), vSymmetryCheck.isSelected()));

        canvasContainer = new HBox(canvas);
        canvasContainer.setAlignment(Pos.CENTER);
        canvasContainer.setPadding(new Insets(15));

        scrollPane = new ScrollPane(canvasContainer);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(false);
        root.setCenter(scrollPane);

        setupScrollAndPanHandlers(canvas, canvasContainer, scrollPane, root);

        bottomButtonsContainer = new HBox(20);
        bottomButtonsContainer.setAlignment(Pos.CENTER);
        bottomButtonsContainer.setPadding(new Insets(15));
        bottomButtonsContainer.setPrefHeight(65);
        bottomButtonsContainer.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");

        Button openButton = new Button("Завантажити малюнок");
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
        root.setLeft(null);

        if (isAnimationPlayed) {
            DrawLogic.applyInstantBrodyPattern(currentWord);
            bottomButtonsContainer.getChildren().addAll(openButton, newFieldButton);
            root.setBottom(bottomButtonsContainer);
        } else {
            root.setBottom(bottomButtonsContainer);
            DrawLogic.applyBrodyAlgorithm(currentWord, root, openButton, newFieldButton);
        }

        scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("Піксельна вишивка | Автор: Бачинська Віталіна");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
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
}