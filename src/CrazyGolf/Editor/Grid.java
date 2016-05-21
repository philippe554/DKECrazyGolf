package CrazyGolf.Editor;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Carla on 11/04/2016.
 */


public class Grid extends JComponent {

    private String[][] stringGrid;
    private Rectangle[][] rectangleGrid;
    private final int pixelSIZE = 20;
    private final int SIZE = (int)pixelSIZE;

    public Grid(){
        stringGrid = new String[82][50];
        rectangleGrid = new Rectangle[82][50];

        for(int i=0; i<rectangleGrid.length; i++) {
            for (int j=0; j<rectangleGrid[0].length; j++) {
                rectangleGrid[i][j] = new Rectangle(SIZE*i,SIZE*j, (int)pixelSIZE,(int) pixelSIZE);
                stringGrid[i][j] = "E";
            }
        }
    }

    public int getPixelSize(){
        return pixelSIZE;
    }

    public String[][] getStringGrid(){
        return stringGrid;
    }

    public Rectangle[][] getRectanglegGrid(){
        return rectangleGrid;
    }
}
