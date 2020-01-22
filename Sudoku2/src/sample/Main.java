package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
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
import javafx.util.Pair;
import javafx.util.StringConverter;


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
    private ComboBox<Pair<Integer, String>> cbxDifficulty;
    private Label lblMain;
    private Label lblDifficulty;
    private int selectedGridID;
    private int controlsCount;
    private Group root;

    private ObservableList<Pair<Integer, String>> listDifficulties;
    //To implement
    //private VBox menuContainer;


    public static void main(String[] args)
            throws Exception {
        try {
            launch(args);
        } catch (Exception e) {
            Exception ex = new Exception();
            ex.addSuppressed(e);
            throw ex;
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new Group();
        Initialize(primaryStage, root);

        root.setFocusTraversable(true);
        root.requestFocus();
        //root.setOnMouseMoved(this);
        root.setOnMouseClicked(this);
    }

    void Initialize(Stage stage, Group root) {
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("SUDOKU");
        stage.setScene(scene);
        stage.show();

        //init main variables
        initMenus();
        controlsCount = 0;
        initScene(root);
        initGame(root);
        initHandlers();

    }

    private void initScene(Group root) {
        listDifficulties = FXCollections.observableArrayList();
        listDifficulties.addAll(
                new Pair<>(50, "Easy (50 clues)"),
                new Pair<>(40, "Medium (40 clues)"),
                new Pair<>(30, "Hard (30 clues)"),
                new Pair<>(20, "Extreme (20 clues)")
        );


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
        cbxDifficulty.getItems().addAll(listDifficulties);

        cbxDifficulty.setConverter(new StringConverter<Pair<Integer, String>>() {
            @Override
            public String toString(Pair<Integer, String> object) {
                return object.getValue();
            }

            @Override
            public Pair<Integer, String> fromString(String string) {
                return null;
            }
        });

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

        controlsCount = root.getChildren().size();
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
                        //selectedGridID=(int)((SudokuGraphics.GridField)event.getSource()).getUserData();
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
                //GameEngine.setDebug(true);
                GameEngine.Update();
            }
        });

        //TODO:Improve on this method, without throwing exceptions
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
                            GameEngine.Solve(chbShowAnimation.isSelected());

                            //GameEngine.Update();
                            //EnableControls();
                            //butValidateSolution.fire();
                        } catch (Exception ex) {
                            Exception ee = new Exception();
                            ee.addSuppressed(ex);
                            throw ee;
                        }
                        return null;
                    }
                };
                taskSolveInBackground.setOnSucceeded((WorkerStateEvent) ->
                        {
                            GameEngine.Update();
                            EnableControls();
                            butValidateSolution.fire();
                        }

                );
                DisableControls();
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
                GameEngine.Generate(cbxDifficulty.getValue().getKey(), System.nanoTime());
                GameEngine.Update();
            }
        });
    }

    private void MouseEventHandler(MouseEvent mouseEvent, Object sender) {
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            contextMenu.hide();
        }

        if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED && mouseEvent.getButton() == MouseButton.SECONDARY && sender instanceof SudokuGraphics.GridField) {
            selectedGridID = Integer.valueOf(((SudokuGraphics.GridField) mouseEvent.getSource()).getId());
            contextMenu.show(background, mouseEvent.getScreenX(), mouseEvent.getSceneY());
        }
        mouseEvent.consume();
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        MouseEventHandler(mouseEvent, mouseEvent.getSource());
    }

    private void DisableControls() {
        for (int i = 1; i < controlsCount; i++)
            root.getChildren().get(i).setDisable(true);

    }

    private void EnableControls() {
        for (int i = 1; i < controlsCount; i++)
            root.getChildren().get(i).setDisable(false);
    }

}
