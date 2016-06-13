package CrazyGolf.PhysicsEngine.Objects.Native;

import CrazyGolf.PhysicsEngine.Matrix;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;

/**
 * Created by pmmde on 6/13/2016.
 */
public class Sand extends Factory {
    public Sand(Point3D offset, Matrix r, double x, double y, WorldData w){
        super(w);
        dynamicColors.add(new Color3f(1.0f, 0.5f, 0.0f));
        Point3D p1=new Point3D(0,0,0);
        Point3D p2=new Point3D(0+x,0,0);
        Point3D p3=new Point3D(0+x,0+y,0);
        Point3D p4=new Point3D(0,0+y,0);
        addSquare(p1,p2,p3,p4,0,0.5);
        print(offset,r);
    }
}
