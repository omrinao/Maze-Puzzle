package View;

import Server.Server;
import ViewModel.MyViewModel;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseDragEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.layout.BorderPane;
import sample.Main;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements IView, Observer {

    private MyViewModel viewModel;
    @FXML
    public Display mazeDisplay;
    public Display solutionDisplay;
    public Display playerDisplay;
    public Display winDisplay;
    public Pane pane;
    public javafx.scene.control.TextField txt_rowsFromUser;
    public javafx.scene.control.TextField txt_colsFromUser;
    public javafx.scene.control.Button btn_generateButton;
    public javafx.scene.control.Button btn_solveButton;
    public javafx.scene.control.Button btn_restart;
    public javafx.scene.control.Label lbl_playerRow;
    public javafx.scene.control.Label lbl_playerCol;
    public javafx.scene.image.ImageView img_music;
    public javafx.scene.control.Button btn_music;
    public javafx.scene.layout.BorderPane lyt_mainPane;
  
    Media startMusic = new Media(new File("Resources/Muse.mp3").toURI().toString());
    Media winnerMusic = new Media(new File("Resources/gameover.mp3").toURI().toString());
    MediaPlayer mediaPlayerWinner = new MediaPlayer(winnerMusic);
    MediaPlayer mediaPlayerStart = new MediaPlayer(startMusic);
    Image PlayButtonImage = new Image("file:Resources/play.jpg");
    Image PauseButtonImage = new Image("file:Resources/stop.jpg");


    /**
     * a method to take care of the About in the menu
     * @param actionEvent - about being pressed
     */
    public void SetStageAboutEvent(ActionEvent actionEvent) {

        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 800, 450);
            scene.getStylesheets().add(getClass().getResource("About.css").toExternalForm());
            stage.setScene(scene);
            AboutController a = fxmlLoader.getController();
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * a method to take care of the Help in the menu
     * @param actionEvent - about being pressed
     */
    public void SetStageHelpEvent(ActionEvent actionEvent) {

        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml").openStream());
            Scene scene = new Scene(root, 800, 550);
            scene.getStylesheets().add(getClass().getResource("Help.css").toExternalForm());
            stage.setScene(scene);
            HelpController a = fxmlLoader.getController();
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * managing the properties method
     * @param actionEvent - properties being presseds
     */
    public void SetStagePropertiesEvent(ActionEvent actionEvent) {

        try {
            Stage stage = new Stage();
            stage.setTitle("Properties");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Properties.fxml").openStream());
            Scene scene = new Scene(root, 600, 400);
            scene.getStylesheets().add(getClass().getResource("Properties.css").toExternalForm());
            stage.setScene(scene);
            PropertiesController a = fxmlLoader.getController();
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * generate a new maze button pressed
     * @param actionEvent - new maze evet
     */
    public void SetStageNewEvent(ActionEvent actionEvent) {
        generateMaze();
        actionEvent.consume();
    }

    /**
     * a method to play/pause the background music
     * @param actionEvent - pressing the proper button
     */
    public void SetPlayPauseEvent(ActionEvent actionEvent) {

        MediaPlayer.Status statusStart = mediaPlayerStart.getStatus();
        MediaPlayer.Status statusWinner = mediaPlayerWinner.getStatus();

        if (statusStart == MediaPlayer.Status.PLAYING
                || statusStart == MediaPlayer.Status.PAUSED){
            if (statusStart == MediaPlayer.Status.PAUSED) {
                mediaPlayerWinner.stop();
                mediaPlayerStart.play();
                //img_music.setImage(PlayButtonImage);
                btn_music.setText("Pause");

            } else {
                mediaPlayerStart.pause();
                mediaPlayerWinner.stop();
                //img_music.setImage(PauseButtonImage);
                btn_music.setText("Play");
            }
        }
        else {
            if (statusWinner == MediaPlayer.Status.PLAYING
                    || statusWinner == MediaPlayer.Status.READY){
                mediaPlayerWinner.pause();
                btn_music.setText("Play");
            }

            else{
                mediaPlayerWinner.play();
                btn_music.setText("Pause");
            }

        }

        actionEvent.consume();
    }

    /**
     * a method to properly exit the game on menu request
     * @param actionEvent - the event of closing the game from the menu
     */
    public void setOnCloseRequest(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to leave?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
            viewModel.exitGame();
            System.exit(0);
        } else {
            // ... user chose CANCEL or closed the dialog
            actionEvent.consume();
        }

    }

    /**
     * a method to set the size of the maze during scrolling with CTRL button pressed
     * @param scrollEvent - a scrolling event
     */
    public void addMouseScrolling(ScrollEvent scrollEvent) {
        if(scrollEvent.isControlDown()) {
            double zoomFactor = 1.05;
            double deltaY = scrollEvent.getDeltaY();
            if (deltaY < 0) {
                zoomFactor = 2.0 - zoomFactor;
            }
            mazeDisplay.setScaleX(mazeDisplay.getScaleX() * zoomFactor);
            mazeDisplay.setScaleY(mazeDisplay.getScaleY() * zoomFactor);
            playerDisplay.setScaleX(playerDisplay.getScaleX() * zoomFactor);
            playerDisplay.setScaleY(playerDisplay.getScaleY() * zoomFactor);
            solutionDisplay.setScaleX(solutionDisplay.getScaleX() * zoomFactor);
            solutionDisplay.setScaleY(solutionDisplay.getScaleY() * zoomFactor);
        }
    }

    /**
     * a method to set the ViewModel of the application
     *
     * @param given - the given ViewModel
     */
    public void setViewModel(MyViewModel given) {
        this.viewModel = given;
        setProperties();
    }

    /**
     * a method to bind the properties for display
     */
    private void setProperties() {
        lbl_playerRow.textProperty().bind(viewModel.playerRowPropertyProperty());
        lbl_playerCol.textProperty().bind(viewModel.playerColPropertyProperty());
/*
        btn_generateButton.prefHeightProperty().bind(lyt_mainPane.getLeft().layoutYProperty());
        btn_generateButton.prefWidthProperty().bind(lyt_mainPane.getLeft().layoutXProperty());
        */
    }

    /**
     * a method to generate a maze with the sizes inserted
     */
    private void generateMaze() {
        try {
            int row = Integer.valueOf(txt_rowsFromUser.getText());
            int col = Integer.valueOf(txt_colsFromUser.getText());
            btn_generateButton.setDisable(true);
            viewModel.generateMaze(row, col);
            btn_generateButton.setDisable(false);
            btn_solveButton.setDisable(false);
            btn_restart.setDisable(false);

            //img_music.setImage(PlayButtonImage);
            btn_music.setText("Pause");
            mediaPlayerWinner.stop();
            mediaPlayerStart.play();

        } catch (NumberFormatException e) {
            //e.printStackTrace();
            popProblem("Please insert a numeric value to maze sizes!");
        }
    }

    /**
     * a method that will move the player
     *
     * @param pressed - the key pressed to move the player
     */
    public void movePlayer(KeyEvent pressed) {
        viewModel.movePlayer(pressed.getCode());
        pressed.consume();
    }

    @Override
    public void update(Observable o, Object arg) {
        String args = (String) arg;
        if (o == viewModel && args.contains("mazeDisplay"))
            mazeDisplay.display(viewModel.getMaze());

        if (o == viewModel && args.contains("solutionDisplay"))
            solutionDisplay.display(viewModel.getMaze(), viewModel.getSolution());

        if (o == viewModel && args.contains("playerDisplay"))
            playerDisplay.display(viewModel.getMaze(), viewModel.getPlayerRow(), viewModel.getPlayerCol());

        if (o == viewModel && args.contains("WINNER")) {
            /* functionality for finished game!
             * maybe cancel all other current maze related operations
             */

            /* music for winning */
            mediaPlayerStart.stop();
            btn_music.setText("Pause");
            mediaPlayerWinner.play();


            winDisplay.display("Won");
        }
    }

    /**
     * a method to pop errors with a description
     *
     * @param description - of the error occured
     */
    private void popProblem(String description) {
        Alert prob = new Alert(Alert.AlertType.ERROR);
        DialogPane dialogPane = prob.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        prob.setContentText(description);
        prob.showAndWait();
    }

    /**
     * a method to solve the maze
     *
     * @param actionEvent - ignored click event
     */
    public void solveMaze(ActionEvent actionEvent) {
        btn_solveButton.setDisable(true);
        btn_solveButton.setDisable(true);
        viewModel.solve();
        btn_solveButton.setDisable(false);
        btn_generateButton.setDisable(false);
        actionEvent.consume();
    }

    /**
     * a method to save the current game
     *
     * @param actionEvent - ignored
     */
    public void saveGame(ActionEvent actionEvent) {
        if (viewModel.getMaze() == null)
            popProblem("You must generate a maze before saving it!");
        else
            viewModel.saveGame();

        actionEvent.consume();
    }

    /**
     * a method to load a previously saved game
     */
    public void loadGame(ActionEvent actionEvent) {
        if (viewModel.loadGame()){
            btn_solveButton.setDisable(false);
            btn_restart.setDisable(false);

            mediaPlayerWinner.stop();
            mediaPlayerStart.play();
            btn_music.setText("Pause");
        }


        actionEvent.consume();
    }

    /**
     * a method to redisplay the entire displayers because of resize event
     */
    private void display() {
        mazeDisplay.display(viewModel.getMaze());
        solutionDisplay.display(viewModel.getMaze(), viewModel.getSolution());
        playerDisplay.display(viewModel.getMaze(), viewModel.getPlayerRow(), viewModel.getPlayerCol());
    }

    public void setMaxMinEvent(Stage stage) {
        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> display());
    }

    public void dragOver(MouseDragEvent mouseDragEvent) {
        if (viewModel.getMaze() == null)
            return;
        System.out.println("In the method");
        double mouseX = mouseDragEvent.getX() / playerDisplay.getWidth();
        double mouseY = mouseDragEvent.getY() / playerDisplay.getHeight();

        if (Math.abs(viewModel.getPlayerRow() - mouseX) < 2 || Math.abs(viewModel.getPlayerCol() - mouseY) < 2) {
            if (mouseX < viewModel.getPlayerCol())
                viewModel.movePlayer(KeyCode.LEFT);

            if (mouseX > viewModel.getPlayerCol())
                viewModel.movePlayer(KeyCode.RIGHT);

            if (mouseY < viewModel.getPlayerRow())
                viewModel.movePlayer(KeyCode.UP);

            if (mouseY > viewModel.getPlayerCol())
                viewModel.movePlayer(KeyCode.DOWN);
        }

    }

    /**
     * a method to set events of the resize
     * @param scene - the scene of the displayers
     */
    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                display();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                display();
            }
        });
    }

    /**
     * a method to active restart button
     * @param actionEvent - a click event
     */
    public void restartMaze(ActionEvent actionEvent) {

        mediaPlayerWinner.stop();
        mediaPlayerStart.pause();

        if (btn_music.getText().equals("Pause")){
            mediaPlayerStart.play();
        }
        else if (btn_music.getText().equals("Play")){
            mediaPlayerStart.pause();
            btn_music.setText("Play");
        }

        viewModel.restartMaze();

        actionEvent.consume();

    }
}

