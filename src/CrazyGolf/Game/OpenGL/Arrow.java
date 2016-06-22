package CrazyGolf.Game.OpenGL;

import CrazyGolf.PhysicsEngine.Matrix;
import CrazyGolf.PhysicsEngine.Objects.Parts.Edge;
import CrazyGolf.PhysicsEngine.Objects.Parts.Side;
import javafx.geometry.Point3D;

import java.util.ArrayList;

/**
 * Created by pmmde on 6/22/2016.
 */
public class Arrow {
    protected static ArrayList<Point3D> pointsOriginal;
    private static void makeArrow(){
        pointsOriginal = new ArrayList<>();
        double parts=12;
        double towerSize=1;
        double towerHeight=5;
        double angleGrowSize = Math.PI / (parts / 2);
        for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
            Point3D p1 = new Point3D(Math.cos(angle) * towerSize, 0 + Math.sin(angle) * towerSize, 0);
            Point3D p2 = new Point3D(Math.cos(angle) * towerSize, 0 + Math.sin(angle) * towerSize, 0 + towerHeight);
            Point3D p3 = new Point3D(Math.cos(angle + angleGrowSize) * towerSize, 0 + Math.sin(angle + angleGrowSize) * towerSize, 0 + towerHeight);
            Point3D p4 = new Point3D(Math.cos(angle + angleGrowSize) * towerSize, 0 + Math.sin(angle + angleGrowSize) * towerSize, 0);
            addSquare(p1, p2, p3, p4, 0,0.1);
            Point3D top = new Point3D(0, 0, 0 + towerHeight * 1.2);
            Point3D tp1 = new Point3D(Math.cos(angle) * towerSize * 12, 0 + Math.sin(angle) * towerSize * 12, 0 + towerHeight);
            Point3D tp2 = new Point3D(Math.cos(angle + angleGrowSize) * towerSize * 12, 0 + Math.sin(angle + angleGrowSize) * towerSize * 12, 0 + towerHeight);
            addTriangle(tp1, tp2, top, 1,0.1);
        }
    }
    protected static void addSquare(Point3D p1, Point3D p2, Point3D p3, Point3D p4, int c, double f) {
        pointsOriginal.add(p1);
        pointsOriginal.add(p2);
        pointsOriginal.add(p3);
        pointsOriginal.add(p1);
        pointsOriginal.add(p4);
        pointsOriginal.add(p3);
    }
    protected static void addTriangle(Point3D p1, Point3D p2, Point3D p3, int c, double f) {
        pointsOriginal.add(p1);
        pointsOriginal.add(p2);
        pointsOriginal.add(p3);
    }

    public static float[] getArrow(Point3D offset, float angleX,float angleY, float size, float scale){
        if(pointsOriginal==null)makeArrow();

        float[] sphere = new float[pointsOriginal.size()*4];

        Matrix m1 = Matrix.getRotatoinMatrix(0, (float) (Math.PI/2 - angleY/180*Math.PI), 0);
        Matrix m2 = Matrix.getRotatoinMatrix(0, 0, (float) (angleX/180*Math.PI));

        for(int i=0;i<pointsOriginal.size();i++){
            Point3D rotated = new Point3D(pointsOriginal.get(i).getX(),pointsOriginal.get(i).getY(),pointsOriginal.get(i).getZ()*size);
            rotated = m1.multiply(rotated);
            rotated = m2.multiply(rotated);
            sphere[i*4+0]= (float) (rotated.getX()+offset.getX());
            sphere[i*4+1]= (float) (rotated.getZ()+offset.getZ());
            sphere[i*4+2]= (float) (rotated.getY()+offset.getY());
            sphere[i*4+3]= scale;
        }

        return sphere;
    }
    public static float[] getColor(float r,float g,float b){
        float[] color = new float[pointsOriginal.size()*4];

        for(int i=0;i<pointsOriginal.size();i++){
            color[i*4+0]=r;
            color[i*4+1]=g;
            color[i*4+2]=b;
            color[i*4+3]=1.0f;
        }

        return color;
    }
}
