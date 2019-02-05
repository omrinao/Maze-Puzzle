package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import sample.Main;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;

public class MyModel extends Observable implements IModel {

    private Maze maze = null;
    private Solution solved = null;
    private boolean solReached = false;
    private int playerRow;
    private int playerCol;
    private Server generateServer;
    private Server solveServer;

    @Override
    public void initModel(){
        generateServer = new Server(5400, 3, new ServerStrategyGenerateMaze());
        generateServer.start();
        solveServer = new Server(5600, 3, new ServerStrategySolveSearchProblem());
        solveServer.start();
    }

    @Override
    public void generateMaze(int height, int width) {
        try {
            // some stuff here needs to be changed. like 'read'
            Client me = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @SuppressWarnings("ResultOfMethodCallIgnored")
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        solved = null;
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{height, width};

                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();

                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));

                        /*
                        this lower part needs to be changed to the exact size of byte[] needed.
                        solution a - fix MyDecompress with try/catch to return -1 if byte[] is not sufficient
                                        and redo the call with 10 times byte[] size
                        solution b - completely change read(byte[]) method to return a fixed size byte[] to be used
                         */
                        byte[] decompressedMaze = new byte[100000]; //allocating byte[] for the decompressed maze -
                        while (is.read(decompressedMaze) == -1){
                            //System.out.println(decompressedMaze.length);
                            is.close();
                            is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                            decompressedMaze = new byte[decompressedMaze.length*10];
                        }
                        //is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
                        playerRow = maze.getStartPosition().getRowIndex();
                        playerCol = maze.getStartPosition().getColumnIndex();
                        solReached = false;

                        // notify when generation is completed
                        setChanged();
                        notifyObservers("mazeDisplay, solutionDisplay, playerDisplay");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // lets the client operate with server
            me.communicateWithServer();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void findSolution() {
        try {
            if (maze == null)
                return;
            Client me = new Client(InetAddress.getLocalHost(), 5600, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inputStream, OutputStream outputStream) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outputStream);
                        ObjectInputStream fromServer = new ObjectInputStream(inputStream);
                        toServer.flush();

                        toServer.writeObject(maze);
                        toServer.flush();

                        solved = (Solution) fromServer.readObject();

                        // notifying observers (View Model)
                        setChanged();
                        notifyObservers("solutionDisplay");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // communicating with server for a solution
            me.communicateWithServer();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Solution getSolution() {
        return solved;
    }

    @Override
    public void exitGame() {
        generateServer.stop();
        solveServer.stop();
    }

    @Override
    public void movePlayer(KeyCode step) {
        if (solReached)
            return;
        boolean moved = false;
        switch (step){
            case NUMPAD8: // up
                if (maze.isPath(playerRow-1, playerCol))
                    playerRow--;
                moved = true;
                break;
            case UP: // up
                if (maze.isPath(playerRow-1, playerCol))
                    playerRow--;
                moved = true;
                break;
            case NUMPAD2: // down
                if (maze.isPath(playerRow+1, playerCol))
                    playerRow++;
                moved = true;
                break;
            case DOWN: // down
                if (maze.isPath(playerRow+1, playerCol))
                    playerRow++;
                moved = true;
                break;
            case NUMPAD4: // left
                if (maze.isPath(playerRow, playerCol-1))
                    playerCol--;
                moved = true;
                break;
            case LEFT: // left
                if (maze.isPath(playerRow, playerCol-1))
                    playerCol--;
                moved = true;
                break;
            case NUMPAD6: // right
                if (maze.isPath(playerRow, playerCol+1))
                    playerCol++;
                moved = true;
                break;
            case RIGHT: // right
                if (maze.isPath(playerRow, playerCol+1))
                    playerCol++;
                moved = true;
                break;
            case NUMPAD9: // up and right
                if (maze.isPath(playerRow-1, playerCol+1)){
                    playerRow--;
                    playerCol++;
                }
                moved = true;
                break;
            case NUMPAD7: // up and left
                if (maze.isPath(playerRow-1, playerCol-1)){
                    playerRow--;
                    playerCol--;
                }
                moved = true;
                break;
            case NUMPAD3: // down and right
                if (maze.isPath(playerRow+1, playerCol+1)){
                    playerRow++;
                    playerCol++;
                }
                moved = true;
                break;
            case NUMPAD1: // down and left
                if (maze.isPath(playerRow+1, playerCol-1)){
                    playerRow++;
                    playerCol--;
                }
                moved = true;
                break;
        }
        if (moved){
            if (playerRow == maze.getGoalPosition().getRowIndex()
                    && playerCol == maze.getGoalPosition().getColumnIndex())
                solReached = true;
            setChanged();
            notifyObservers(solReached ? "playerDisplay, WINNER" : "playerDisplay");
        }
    }

    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void saveGame() {

        FileChooser fc = new FileChooser();
        fc.setTitle("Save Game");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Game Files", "*.maze"));
        fc.setInitialDirectory(new File(System.getProperty("user.dir")));

        File file = fc.showSaveDialog(Main.pStage);
        if (file != null){
            boolean success = false;
            try{
                ObjectOutputStream mazeSaver = new ObjectOutputStream(new FileOutputStream(file));
                mazeSaver.flush();
                mazeSaver.writeObject(maze);
                mazeSaver.flush();
                mazeSaver.close();
                success = true;

            } catch (Exception e){
                success = false;
            }
            finally {
                Alert status = new Alert(success ? Alert.AlertType.CONFIRMATION : Alert.AlertType.ERROR);
                status.setContentText(success ? "Game Saved!" : "Could not save game :(\n Please try again!");
                status.showAndWait();
            }

        }

    }

    @Override
    public boolean loadGame() {

        FileChooser fc = new FileChooser();
        fc.setTitle("Load Game");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Game File", "*.maze"));
        fc.setInitialDirectory(new File(System.getProperty("user.dir")));

        File file = fc.showOpenDialog(Main.pStage);
        if (file != null){

            try {
                ObjectInputStream gameLoader = new ObjectInputStream(new FileInputStream(file));
                maze = (Maze) gameLoader.readObject();
                playerRow = maze.getStartPosition().getRowIndex();
                playerCol = maze.getStartPosition().getColumnIndex();
                solved = null;
                solReached = false;

                setChanged();
                notifyObservers("mazeDisplay, solutionDisplay, playerDisplay");
                return true;

            }
            catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Could not load game :(\n Please try again!");
                a.showAndWait();
                return false;
            }

        }
        return false;
    }

    @Override
    public void dragPlayer() {

    }

    @Override
    public void restartMaze() {
        if (maze==null)
            return;

        playerRow = maze.getStartPosition().getRowIndex();
        playerCol = maze.getStartPosition().getColumnIndex();
        solved = null;
        solReached = false;


        setChanged();
        notifyObservers("mazeDisplay, playerDisplay, solutionDisplay");
    }
}
