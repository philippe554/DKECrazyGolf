package CrazyGolf.Editor;

import java.util.LinkedList;

/**
 * Created by pmmde on 5/26/2016.
 */
public class Import {
    private Grid[] grid;
    private double gridSize;
    private String[] playerChoice;
    public Import(LinkedList<String> data){

    }
    public Grid[] getGrid(){
        return grid;
    }
    public String[] getPlayerChoice(){
        return playerChoice;
    }
    public double getGridSize(){
        return gridSize;
    }
}
