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

    // to be deleted later on (just for testing issues)
    public Grid(int no){
        stringGrid = new String[82][50];
        rectangleGrid = new Rectangle[82][50];

        for(int i=0; i<rectangleGrid.length; i++) {
            for (int j=0; j<rectangleGrid[0].length; j++) {
                rectangleGrid[i][j] = new Rectangle(SIZE*i,SIZE*j, (int)pixelSIZE,(int) pixelSIZE);
                stringGrid[i][j] = "W";
            }
        }
        for(int i=1; i<rectangleGrid.length-1; i++) {
            for (int j=1; j<rectangleGrid[0].length-1; j++) {
                stringGrid[i][j] = "F";
            }
        }
        for(int i=2; i<3; i++) {
            for (int j=2; j<3; j++) {
                stringGrid[i][j] = "B";
                stringGrid[i][j + 1] = "B";
                stringGrid[i + 1][j]= "B";
                stringGrid[i + 1][j + 1]="B";            }
        }
        for(int i=77; i<80; i++) {
            for (int j=2; j<5; j++) {
                stringGrid[i][j] = "H";           }
        }
        for(int i=15; i<16; i++) {
            for (int j=0; j<40; j++) {
                stringGrid[i][j] = "W";           }
        }
        for(int i=0; i<15; i++) {
            for (int j=7; j<8; j++) {
                stringGrid[i][j] = "W";           }
        }
        for(int i=6; i<6+4; i++) {
            for (int j=6; j<6+24; j++) {
                stringGrid[i][j] = "R";           }
        }
        for(int i=16; i<16+13; i++) {
            for (int j=35; j<35+4; j++) {
                stringGrid[i][j] = "C";           }
        }
        for(int i=16+13; i<16+13+1; i++) {
            for (int j=7; j<50; j++) {
                stringGrid[i][j] = "W";           }
        }
        for(int i=20; i<20+14; i++) {
            for (int j=1; j<1+6; j++) {
                stringGrid[i][j] = "L";           }
        }
        for(int i=35; i<35+14; i++) {
            for (int j=5; j<5+14; j++) {
                stringGrid[i][j] = "P";           }
        }
        for(int i=35; i<35+14; i++) {
            for (int j=30; j<30+14; j++) {
                stringGrid[i][j] = "P";           }
        }
        for(int i=35+14; i<35+14+1; i++) {
            for (int j=0; j<0+40; j++) {
                stringGrid[i][j] = "W";           }
        }
        for(int i=35+14+1; i<82-1; i++) {
            for (int j=6; j<6+40; j++) {
                stringGrid[i][j] = "S";           }
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