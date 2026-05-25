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

    private CheckBox hRepeatCheck;
    private CheckBox vRepeatCheck;

    private Scene scene;
    private Stage primaryStage;
    private BorderPane root;
    protected static final String currentWord = "МАЛШЗКА";

    protected static HBox bottomButtonsContainer;
    private VBox sideMenu;
    protected static VBox topHeaderContainer;

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
        clearButton.setOnAction(e -> {
            hRepeatCheck.setSelected(false);
            vRepeatCheck.setSelected(false);
            DrawLogic.clearGrid();
        });

        Button openFileBtn = new Button("Відкрити файл PNG");
        openFileBtn.setOnAction(e -> DrawLogic.loadFromPNG(primaryStage, root, sideMenu));

        Label symmetryLabel = new Label("Дзеркальне малювання:");
        symmetryLabel.setStyle("-fx-font-weight: bold;");
        hSymmetryCheck = new CheckBox("Горизонтальне");
        vSymmetryCheck = new CheckBox("Вертикальне");

        Label repeatLabel = new Label("Дублювання на екрані:");
        repeatLabel.setStyle("-fx-font-weight: bold;");
        hRepeatCheck = new CheckBox("Повторити вшир (2х)");
        vRepeatCheck = new CheckBox("Повторити ввись (2х)");

        hRepeatCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            DrawLogic.toggleHorizontalRepeat(newVal);
        });

        vRepeatCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            DrawLogic.toggleVerticalRepeat(newVal);
        });

        sideMenu.getChildren().addAll(
                new Label("Оберіть колір:"), colorPicker, clearButton, openFileBtn,
                new Label("---"),
                symmetryLabel, hSymmetryCheck, vSymmetryCheck,
                new Label("---"),
                repeatLabel, hRepeatCheck, vRepeatCheck
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

        HBox canvasContainer = new HBox(canvas);
        canvasContainer.setAlignment(Pos.CENTER);
        canvasContainer.setPadding(new Insets(15));
        ScrollPane scrollPane = new ScrollPane(canvasContainer);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0;");
        scrollPane.setPannable(false);
        root.setCenter(scrollPane);

        canvasContainer.setOnScroll(e -> {
            if (e.isControlDown()) {
                e.consume();
                double zoomFactor = (e.getDeltaY() > 0) ? 1.1 : 0.9;
                double newScaleX = canvas.getScaleX() * zoomFactor;
                double newScaleY = canvas.getScaleY() * zoomFactor;

                if (newScaleX >= 0.5 && newScaleX <= 4.0) {
                    canvas.setScaleX(newScaleX);
                    canvas.setScaleY(newScaleY);
                }
            }
        });
        root.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == javafx.scene.input.KeyCode.CONTROL) {
                        scrollPane.setPannable(true);
                    }
                });

                newScene.setOnKeyReleased(event -> {
                    if (event.getCode() == javafx.scene.input.KeyCode.CONTROL) {
                        scrollPane.setPannable(false);
                    }
                });
            }
        });
        bottomButtonsContainer = new HBox(20);
        bottomButtonsContainer.setAlignment(Pos.CENTER);
        bottomButtonsContainer.setPadding(new Insets(15));
        bottomButtonsContainer.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");

        Button downloadButton = new Button("Зберегти малюнок");
        downloadButton.setPrefSize(180, 35);
        downloadButton.setOnAction(e -> DrawLogic.saveToPNG(primaryStage));

        Button newFieldButton = new Button("Нове поле");
        newFieldButton.setPrefSize(160, 35);
        newFieldButton.setOnAction(e -> {
            DrawLogic.clearGrid();
            root.setLeft(sideMenu);
            root.setBottom(null);
            hRepeatCheck.setSelected(false);
            vRepeatCheck.setSelected(false);
            if (!topHeaderContainer.getChildren().contains(backButton)) {
                topHeaderContainer.getChildren().add(backButton);
            }
        });

        bottomButtonsContainer.getChildren().addAll(downloadButton, newFieldButton);

        root.setLeft(null);
        root.setBottom(null);

        scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("Піксельна вишивка | Автор: Бачинська Віталіна");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        DrawLogic.applyTrueBrodyAlgorithm(currentWord, root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}