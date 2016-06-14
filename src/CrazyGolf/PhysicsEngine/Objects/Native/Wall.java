package CrazyGolf.PhysicsEngine.Objects.Native;

import CrazyGolf.PhysicsEngine.Matrix;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;

/**
 * Created by pmmde on 6/13/2016.
 */
public class Wall extends Factory {
    public Wall(Point3D offset, Matrix r, double x, double y, WorldData w){
        super(w);
        dynamicColors.add(new Color3f(0.8f, 0.8f, 0.8f));//0
        dynamicColors.add(new Color3f(0.5f, 0.5f, 0.5f));//1
        double borderHeight=50;

        addSquare(new Point3D(0,0,borderHeight),
                new Point3D(0+x,0,borderHeight),
                new Point3D(0+x,0+y,borderHeight),
                new Point3D(0,0+y,borderHeight),
                0,0.1);
        addSquare(new Point3D(0,0,borderHeight),
                new Point3D(0,0+y,borderHeight),
                new Point3D(0,0+y,0),
                new Point3D(0,0,0+0),
                1,0.1);
        addSquare(new Point3D(0,0,borderHeight+0),
                new Point3D(0+x,0,borderHeight),
                new Point3D(0+x,0,0+0),
                new Point3D(0,0,0+0),
                1,0.1);
        addSquare(new Point3D(0+x,0,borderHeight+0),
                new Point3D(0+x,0+y,borderHeight+0),
                new Point3D(0+x,0+y,0+0),
                new Point3D(0+x,0,0+0),
                1,0.1);
        addSquare(new Point3D(0,0+y,borderHeight+0),
                new Point3D(0+x,0+y,borderHeight+0),
                new Point3D(0+x,0+y,0+0),
                new Point3D(0,0+y,0+0),
                1,0.1);
        print(offset,r);
    }
}