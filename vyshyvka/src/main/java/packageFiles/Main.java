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

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new BorderPane();

        Canvas canvas = new Canvas(COLS * CELL_SIZE, COLS * CELL_SIZE);
        DrawLogic.initGrid(canvas);

        //draw header with name of program and author
        VBox topHeaderContainer = new VBox(5);
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

        VBox canvasCenterWrapper = new VBox(canvas);
        canvasCenterWrapper.setAlignment(Pos.CENTER);
        root.setCenter(canvasCenterWrapper);

        sideMenu = new VBox(15);
        sideMenu.setPadding(new Insets(15));
        sideMenu.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");

        ColorPicker colorPicker = new ColorPicker(currentColor);
        colorPicker.setOnAction(e -> {
            currentColor = colorPicker.getValue();
            DrawLogic.currentColor = currentColor;
        });
        Button openButton = new Button("Зберегти в PNG");
        openButton.setPrefWidth(140);
        openButton.setOnAction(e -> DrawLogic.saveToPNG(primaryStage));

        Button clearButton = new Button("Очистити поле");
        clearButton.setOnAction(e -> DrawLogic.clearGrid());

        Label symmetryLabel = new Label("Дзеркальне малювання:");
        hSymmetryCheck = new CheckBox("Горизонтальне");
        vSymmetryCheck = new CheckBox("Вертикальне");

        sideMenu.getChildren().addAll(new Label("Оберіть колір:"), colorPicker, openButton, clearButton, symmetryLabel, hSymmetryCheck, vSymmetryCheck);

        canvas.setOnMouseClicked(e -> {
            if (root.getLeft() != null) { // Малюємо лише у режимі редагування
                DrawLogic.handleMouseAction(e.getX(), e.getY(), hSymmetryCheck.isSelected(), vSymmetryCheck.isSelected());
            }
        });
        canvas.setOnMouseDragged(e -> {
            if (root.getLeft() != null) {
                DrawLogic.handleMouseAction(e.getX(), e.getY(), hSymmetryCheck.isSelected(), vSymmetryCheck.isSelected());
            }
        });

        bottomButtonsContainer = new HBox(20);
        bottomButtonsContainer.setAlignment(Pos.CENTER);
        bottomButtonsContainer.setPadding(new Insets(15));
        bottomButtonsContainer.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");

        Button downloadButton = new Button("Завантажити малюнок");
        downloadButton.setPrefSize(180, 35);
        downloadButton.setOnAction(e -> DrawLogic.loadFromPNG(primaryStage, root, sideMenu));
        //downloadButton.setOnAction(e -> DrawLogic.loadFromPNG(primaryStage));

        Button newFieldButton = new Button("Нове поле");
        newFieldButton.setPrefSize(160, 35);
        newFieldButton.setOnAction(e -> {
            DrawLogic.clearGrid();
            root.setLeft(sideMenu);
            root.setBottom(null);
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