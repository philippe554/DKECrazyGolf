package CrazyGolf.PhysicsEngine.Objects.Native;

import CrazyGolf.PhysicsEngine.Matrix;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import CrazyGolf.PhysicsEngine.Physics3.WorldObject;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;
import java.util.ArrayList;

/**
 * Created by pmmde on 6/11/2016.
 */
public class Grass extends WorldObject {
    public Grass(Point3D offset, Matrix r, double x,double y, WorldData w,double gs){
        super(w);
        pointsOriginal=new ArrayList<>();
        colors=new ArrayList<>();
        sides=new ArrayList<>();
        edges=new ArrayList<>();
        waters=new ArrayList<>();

        colors.add(new Color3f(0.0f, 1.0f, 0.0f));
        colors.add(new Color3f(0.0f, 0.8f, 0.0f));

        Point3D p1=new Point3D(0,0,0);
        Point3D p2=new Point3D(x,0,0);
        Point3D p3=new Point3D(x,y,0);
        Point3D p4=new Point3D(0,y,0);
        addSquare(p1, p2, p3, p4, 0, 0.1);

        /*for(int i=0;i<(x/gs);i++){
            for(int j=0;j<(y/gs);j++){
                Point3D p1=new Point3D(i*gs,j*gs,0);
                Point3D p2=new Point3D(i*gs+gs,j*gs,0);
                Point3D p3=new Point3D(i*gs+gs,j*gs+gs,0);
                Point3D p4=new Point3D(i*gs,j*gs+gs,0);
                if((int)(i*gs+offset.getX())%(20*2)==0) {
                    if((int)(j*gs+offset.getY())%(20*2)==0){
                        addSquare(p1, p2, p3, p4, 0, 0.1);
                    }else{
                        addSquare(p1, p2, p3, p4, 1, 0.1);
                    }
                }
                else{
                    if((int)(j*gs+offset.getY())%(20*2)==0){
                        addSquare(p1, p2, p3, p4, 1, 0.1);
                    }else{
                        addSquare(p1, p2, p3, p4, 0, 0.1);
                    }
                }
            }
        }
*/
        center=offset;
        rotation=r;
    }
}
