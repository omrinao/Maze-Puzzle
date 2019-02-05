package View;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sample.Main;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class WinDisplay extends Display {

    private StringProperty imageFileName = new SimpleStringProperty();

    public String getImageFileName() {
        return imageFileName.get();
    }

    public StringProperty imageFileNameProperty() {
        return imageFileName;
    }

    @Override
    public void display(Object... o) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0,0, getWidth(), getHeight());

        String s = (String) o[0];
        try {
            Random rand = new Random();
            int num = rand.nextInt(3);

            String path =System.getProperty("user.dir") + "\\Resources\\Winner" + num + ".gif";

            Image win = new Image(new FileInputStream(path));
            ImageView winGif =new ImageView( );
            winGif.setImage(win);
            winGif.setFitHeight(getHeight());
            winGif.setFitWidth(getWidth());



            Pane pane = new Pane();
            Scene scene = new Scene(pane, getWidth(),getHeight());
            Stage newStage = new Stage();
            newStage.setTitle("You did it!");
            newStage.setScene(scene);

            Button button = new Button();
            button.setText("Let me play again!");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    newStage.close();
                    event.consume();
                }
            });


            winGif.setImage(win);
            pane.getChildren().addAll(winGif, button);
            newStage.initOwner(Main.pStage);

            newStage.showAndWait();;




        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
