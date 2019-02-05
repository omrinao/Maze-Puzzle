package View;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class HelpController implements Initializable {

    public javafx.scene.layout.BorderPane bp;
    public javafx.scene.control.Label lbl_help;
    public javafx.scene.image.ImageView img_help;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String content = "Maze Rules:" + '\n'
                + "1. The character can be moved only to empty cells (non-wall cells)" + '\n'
                + "2. In order to solve the maze, you need to reach the goal cell" + '\n'
                + "Please help Le'Bron James get another NBA title!!\n" +
                "Game Instructions:" + '\n' + "Use the NumPad numbers to move the character:" + '\n' +
                "UP - 8       DOWN - 2" + '\n' + "RIGHT - 6       LEFT - 4" + '\n'
                + "Diagonal Moves:" + '\n' + "UP-LEFT - 7       DOWN-LEFT - 1" + '\n' + "UP-RIGHT - 9       DOWN-RIGHT - 3" + '\n';
        lbl_help.setText(content);
        lbl_help.setMinWidth(Region.USE_PREF_SIZE);
        lbl_help.setMinHeight(Region.USE_PREF_SIZE);
        Image img = new Image("file:Resources/help.jpg");
        img_help.setImage(img);
    }

}
