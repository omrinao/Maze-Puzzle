package View;

import Server.Server;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PropertiesController implements Initializable {

    public javafx.scene.layout.BorderPane bp;
    public javafx.scene.control.Label lbl_Properties;
    public javafx.scene.image.ImageView img_Properties;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String mazeType = Server.Configurations.getProperty("mazeType");
        String searchingAlgorithm = Server.Configurations.getProperty("searchingAlgorithm");
        lbl_Properties.setText("Maze type: " + mazeType + '\n' + "Searching Algorithm: " + searchingAlgorithm);
        lbl_Properties.setMinWidth(Region.USE_PREF_SIZE);
        lbl_Properties.setMinHeight(Region.USE_PREF_SIZE);
        Image img = new Image("file:Resources/properties.png");
        img_Properties.setImage(img);
    }

}
