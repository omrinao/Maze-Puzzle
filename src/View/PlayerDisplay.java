package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class PlayerDisplay extends Display {

    /**
     * displaying the player layer
     * @param o - an array of given object to be of aid
     */
    public void display(Object... o){
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());


        if (!(o[0] instanceof Maze) || !(o[1] instanceof Integer) || !(o[2] instanceof Integer))
            return;

        Maze m = (Maze) o[0];
        int[][] maze = m.getMaze();

        double cellHeight = getHeight() / maze.length;
        double cellWidth = getWidth() / maze[0].length;
        ImageView player = new ImageView();
        Image playerImage = new Image("file:Resources/lbj.jpg");
        //gc.setFill(Color.WHITE);
        gc.drawImage(playerImage, (Integer)o[2] * cellWidth, (Integer)o[1] * cellHeight, cellWidth, cellHeight);
        //gc.fillRect((Integer)o[2] * cellWidth, (Integer)o[1] * cellHeight, cellWidth, cellHeight);

    }

}
