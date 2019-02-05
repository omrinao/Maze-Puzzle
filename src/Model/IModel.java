package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.Observable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Observer;

public interface IModel {

    // init Model Functionality
    void initModel();

    // generate maze
    void generateMaze(int height, int width);

    // get generated maze
    Maze getMaze();

    // calculating solution
    void findSolution();

    // returning solution
    Solution getSolution();

    // saving current game state
    void saveGame();

    // load saved game state
    boolean loadGame();

    // defining settings
    //public void setNewSettings(String[] args);

    // making a proper exit
    void exitGame();

    // moving the character
    void movePlayer(KeyCode step);

    /* should add movePlayer method with hover mouse */


    int getPlayerRow();

    int getPlayerCol();

    void dragPlayer();

    void restartMaze();
}
