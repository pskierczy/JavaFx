package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class Main extends Application
        implements EventHandler<MouseEvent> {

    protected int WIDTH = 800;
    protected int HEIGHT = 800;
    private AnimationTimer animationTimer;

    private double x, y;
    private SudokuBoard MainBoard;
    private SudokuGraphics GameGraphics;
    private SudokuEngine GameEngine;
    private ContextMenu contextMenu;
    private Rectangle background;
    private CheckBox chbShowPossibleNumbers;
    private CheckBox chbShowAnimation;
    private CheckBox chbShowInvalidFields;
    private Button butGenerate;
    private Button butSolve;
    private Button butValidateSolution;
    private Button butReset;
    private ComboBox<String> cbxDifficulty;
    private Label lblMain;
    private Label lblDifficulty;
    private int selectedGridID;
    private int controlsCount;
    private Group root;

    //To implement
    //private VBox menuContainer;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new Group();
        Initialize(primaryStage, root);

        root.setFocusTraversable(true);
        root.requestFocus();
        root.setOnMouseMoved(this);
        root.setOnMouseClicked(this);
    }

    void Initialize(Stage stage, Group root) {
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("SUDOKU");
        stage.setScene(scene);
        stage.show();

        //init main variables
        initMenus();
        controlsCount=0;
        initScene(root);
        initGame(root);
        initHandlers();

    }

    private void initScene(Group root) {
        int y0 = 10;
        //int dy = 20;

        background = new Rectangle(WIDTH, HEIGHT);
        background.setFill(Color.WHITE);
        background.setLayoutX(0);
        background.setLayoutY(0);
        root.getChildren().add(background);

        butGenerate = new Button("Generate new Sudoku");
        butGenerate.setLayoutX(10);
        butGenerate.setLayoutY(10);
        root.getChildren().add(butGenerate);

        butReset = new Button("Reset");
        butReset.setLayoutX(200);
        butReset.setLayoutY(10);
        root.getChildren().add(butReset);

        lblDifficulty = new Label();
        lblDifficulty.setLayoutX(10);
        lblDifficulty.setLayoutY(40);
        lblDifficulty.setText("Difficulty");
        root.getChildren().add(lblDifficulty);

        cbxDifficulty = new ComboBox<>();
        cbxDifficulty.getItems().addAll("Easy (40 clues)", "Medium (30 clues)", "Hard (20 clues)");
        cbxDifficulty.getSelectionModel().select(0);
        cbxDifficulty.setLayoutX(10);
        cbxDifficulty.setLayoutY(60);
        root.getChildren().add(cbxDifficulty);

        chbShowPossibleNumbers = new CheckBox("Show possible numbers");
        chbShowPossibleNumbers.setLayoutX(10);
        chbShowPossibleNumbers.setLayoutY(100);
        root.getChildren().add(chbShowPossibleNumbers);

        butValidateSolution = new Button("Validate solution");
        butValidateSolution.setLayoutX(10);
        butValidateSolution.setLayoutY(140);
        root.getChildren().add(butValidateSolution);

        chbShowInvalidFields = new CheckBox("Show invalid fields");
        chbShowInvalidFields.setLayoutX(10);
        chbShowInvalidFields.setLayoutY(170);
        root.getChildren().add(chbShowInvalidFields);

        butSolve = new Button("Solve Sudoku");
        butSolve.setLayoutX(10);
        butSolve.setLayoutY(210);
        root.getChildren().add(butSolve);

        chbShowAnimation = new CheckBox("Show solving animation");
        chbShowAnimation.setLayoutX(10);
        chbShowAnimation.setLayoutY(240);
        root.getChildren().add(chbShowAnimation);

        controlsCount=root.getChildren().size();

        lblMain = new Label();
        lblMain.setLayoutX(WIDTH * 2 / 3);
        lblMain.setLayoutY(10);
        lblMain.setText("Mouse position: 000.00; 000.00");
        root.getChildren().add(lblMain);


    }

    private void initMenus() {
        contextMenu = new ContextMenu();
        //MenuItem menuItem;
        for (int i = 0; i < 10; i++) {
            MenuItem menuItem = new MenuItem(i == 0 ? "Clear" : String.valueOf(i));
            menuItem.setUserData(i);
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        int selectedNumber = (int) ((MenuItem) event.getTarget()).getUserData();
                        GameEngine.Update(selectedGridID / 9, selectedGridID % 9, selectedNumber);
                    } catch (Exception ex) {
                        Alert messageBox = new Alert(Alert.AlertType.ERROR);
                        messageBox.setTitle("ERROR");
                        messageBox.setContentText(ex.getMessage());
                        messageBox.show();
                    }
                }
            });
            contextMenu.getItems().add(i, menuItem);
        }
    }


    private void initGame(Group root) {
        MainBoard = SudokuBoard.TestCase();
        GameGraphics = new SudokuGraphics();
        GameGraphics.setLayoutX(WIDTH / 4);
        GameGraphics.setLayoutY(HEIGHT / 10);

        GameEngine = new SudokuEngine(MainBoard, GameGraphics);
        GameEngine.InitializeGraphics(Math.min(WIDTH, HEIGHT) / 13);
        GameEngine.Update();//to show only needed data
        root.getChildren().add(GameEngine.getGraphics());
    }

    private void initHandlers() {
        GameEngine.setOnMouseEventForFields(this);

        chbShowPossibleNumbers.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GameEngine.setShowPossibleNumbers(chbShowPossibleNumbers.isSelected());
                GameEngine.Update();
            }
        });
        chbShowInvalidFields.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GameEngine.setShowInvalidFields(chbShowInvalidFields.isSelected());
                GameEngine.Update();
            }
        });

        butValidateSolution.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Alert messageBox = new Alert(Alert.AlertType.INFORMATION);
                messageBox.setTitle("Solution validation");
                try {
                    if (GameEngine.ValidateSolution()) {
                        messageBox.setContentText("Congratulations. Solve correct");
                    } else {
                        messageBox.setContentText("Sorry. You made at least one mistake");
                    }
                    messageBox.show();

                } catch (Exception ex) {
                    messageBox.setContentText(ex.getMessage());
                    messageBox.show();
                }
            }
        });

        butSolve.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Task<Void> taskSolveInBackground = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            DisableControls();
                            GameEngine.Solve(chbShowAnimation.isSelected());
                            GameEngine.Update();
                            EnableControls();
                            butValidateSolution.fire();
                        } catch (Exception ex) {
                        }
                        return null;
                    }
                };
                new Thread(taskSolveInBackground).start();
            }

        });

        butReset.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                GameEngine.Reset();
            }
        });

        butGenerate.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Alert message = new Alert(Alert.AlertType.INFORMATION);
                message.setContentText("Not implemented yet");
                message.show();
            }
        });
    }

    private void MouseEventHandler(MouseEvent mouseEvent, Object sender) {
        lblMain.setText("sender=" + sender.getClass().getName());
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            contextMenu.hide();
        }

        if (sender instanceof SudokuGraphics.GridField && mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
            selectedGridID = Integer.valueOf(((SudokuGraphics.GridField) mouseEvent.getSource()).getId());
            lblMain.setText(lblMain.getText() + "\nMouse position:" + mouseEvent.getSceneX() + ";" + mouseEvent.getSceneX() + "\nGrid ID=" + selectedGridID);
        }

        if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED && mouseEvent.getButton() == MouseButton.SECONDARY && sender instanceof SudokuGraphics.GridField) {
            contextMenu.show(background, mouseEvent.getScreenX(), mouseEvent.getSceneY());
        }
        mouseEvent.consume();
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        MouseEventHandler(mouseEvent, mouseEvent.getSource());
    }

    private void DisableControls()
    {
        for (int i=1;i<controlsCount;i++)
            root.getChildren().get(i).setDisable(true);
    }

    private void EnableControls()
    {
        for (int i=1;i<controlsCount;i++)
            root.getChildren().get(i).setDisable(false);
    }

}
