package CrazyGolf.PhysicsEngine.Objects.Native;

import CrazyGolf.PhysicsEngine.Matrix;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import CrazyGolf.PhysicsEngine.Physics3.WorldObject;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;
import java.util.ArrayList;

/**
 * Created by pmmde on 6/18/2016.
 */
public class FieldBorder extends WorldObject{
    public FieldBorder(Point3D offset, Matrix r,double gs, String[][] data,WorldData w,double height) {
        super(w);
        center=offset;
        rotation=r;
        pointsOriginal=new ArrayList<>();
        colors=new ArrayList<>();
        sides=new ArrayList<>();
        edges=new ArrayList<>();
        waters=new ArrayList<>();
        colors.add(new Color3f(0.5f, 0.5f, 0.5f));

        boolean[][][]alreadyConverted=new boolean[4][data.length][data[0].length];
        for(int k=0;k<4;k++) {
            for (int i = 0; i < alreadyConverted[k].length; i++) {
                for (int j = 0; j < alreadyConverted[k][i].length; j++) {
                    alreadyConverted[k][i][j]= false;
                }
            }
        }

        for(int i=0;i<data.length;i++){
            for(int j=0;j<data[i].length;j++){
                if(!data[i][j].equals("E")){
                    if(alreadyConverted[0][i][j]==false) {
                        int amountTop = expand(i, j, 1, 0, 0, -1, data, alreadyConverted[0]);
                        if (amountTop > 0) {
                            addSquare(new Point3D(i * gs, j * gs, height),
                                    new Point3D((i + amountTop) * gs, j * gs, height),
                                    new Point3D((i + amountTop) * gs, j * gs, 0),
                                    new Point3D(i * gs, j * gs, 0),
                                    0, 0.1);
                            for (int k = 0; k < amountTop; k++) {
                                alreadyConverted[0][i + k][j] = true;
                            }
                        }
                    }
                    if(alreadyConverted[1][i][j]==false) {
                        int amountBottom = expand(i, j, 1, 0, 0, 1, data, alreadyConverted[1]);
                        if (amountBottom > 0) {
                            addSquare(new Point3D(i * gs, j * gs + gs, height),
                                    new Point3D((i + amountBottom) * gs, j * gs + gs, height),
                                    new Point3D((i + amountBottom) * gs, j * gs + gs, 0),
                                    new Point3D(i * gs, j * gs + gs, 0),
                                    0, 0.1);
                        }
                        for (int k = 0; k < amountBottom; k++) {
                            alreadyConverted[1][i + k][j] = true;
                        }
                    }
                    if(alreadyConverted[2][i][j]==false) {
                        int amountLeft = expand(i, j, 0, 1, -1, 0, data, alreadyConverted[2]);
                        if (amountLeft > 0) {
                            addSquare(new Point3D(i * gs, j * gs, height),
                                    new Point3D(i * gs, (j + amountLeft) * gs, height),
                                    new Point3D(i * gs, (j + amountLeft) * gs, 0),
                                    new Point3D(i * gs, j * gs, 0),
                                    0, 0.1);
                        }
                        for (int k = 0; k < amountLeft; k++) {
                            alreadyConverted[2][i][j+k] = true;
                        }
                    }
                    if(alreadyConverted[3][i][j]==false) {
                        int amountRight=expand(i,j,0,1,1,0,data,alreadyConverted[3]);
                        if(amountRight>0){
                            addSquare(new Point3D(i*gs+gs,j*gs,height),
                                    new Point3D(i*gs+gs,(j+amountRight)*gs,height),
                                    new Point3D(i*gs+gs,(j+amountRight)*gs,0),
                                    new Point3D(i*gs+gs,j*gs,0),
                                    0,0.1);
                        }
                        for (int k = 0; k < amountRight; k++) {
                            alreadyConverted[3][i][j+k] = true;
                        }
                    }
                }
            }
        }
    }
    private int expand(int x,int y,int dirX,int dirY,int borderX,int borderY, String[][] data,boolean[][]alreadyConverted){
        boolean stillTrue=true;
        int counter=0;
        while(stillTrue){
            if(x+counter*dirX<data.length && y+counter*dirY<data[0].length &&
                    alreadyConverted[x+counter*dirX][y+counter*dirY]==false &&
                    !data[x+counter*dirX][y+counter*dirY].equals("E") ){
                if(x+counter*dirX+borderX<data.length && y+counter*dirY+borderY<data[0].length &&
                        x+counter*dirX+borderX>=0 && y+counter*dirY+borderY>=0){
                    if(data[x+counter*dirX+borderX][y+counter*dirY+borderY].equals("E")){
                        counter++;
                    }else {
                        stillTrue=false;
                    }
                }
                else{
                    counter++;
                }
            }
            else{
                stillTrue=false;
            }
        }

        return counter;
    }
}
