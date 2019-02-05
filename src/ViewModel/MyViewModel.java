package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;
    private int playerRow;
    private int playerCol;
    private StringProperty playerRowProperty = new SimpleStringProperty("");
    private StringProperty playerColProperty = new SimpleStringProperty("");

    public StringProperty playerRowPropertyProperty() {
        return playerRowProperty;
    }

    public StringProperty playerColPropertyProperty() {
        return playerColProperty;
    }

    /**
     * c'tor
     * @param theModel - the desired model of the game
     */
    public MyViewModel(IModel theModel){
        this.model = theModel;
    }

    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == model){
            playerRow = model.getPlayerRow();
            playerCol = model.getPlayerCol();
            playerRowProperty.setValue(playerRow+1 + "");
            playerColProperty.setValue(playerCol+1 + "");

            setChanged();
            notifyObservers(arg);
        }
    }

    /**
     * method that will move a player by pressed NumPad
     * @param move - the key code of the movement
     */
    public void movePlayer(KeyCode move){model.movePlayer(move);}

    /**
     * method to generate a maze
     * @param height - the desired row number
     * @param width - the desired columns number
     */
    public void generateMaze(int height, int width){model.generateMaze(height, width);}

    /**
     * a method to get the generated maze
     * @return - an instance of maze
     */
    public Maze getMaze(){return model.getMaze();}

    /**
     * a method to get the solution of the maze
     * @return - a proper solution of the maze
     */
    public Solution getSolution(){return model.getSolution();}

    /**
     * a method to solve the maze
     */
    public void solve(){model.findSolution();}

    /**
     * a method to exit the game properly
     */
    public void exitGame(){model.exitGame();}

    /**
     * a method to save the current game
     */
    public void saveGame() {
        model.saveGame();
    }

    /**
     * a method to load a game
     */
    public boolean loadGame() {
        return model.loadGame();
    }

    public void dragOver() {
        model.dragPlayer();
    }

    public void restartMaze() {
        model.restartMaze();
    }
}
