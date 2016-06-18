package CrazyGolf.PhysicsEngine.Objects.Native;

import CrazyGolf.PhysicsEngine.Matrix;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import CrazyGolf.PhysicsEngine.Physics3.WorldObject;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;
import java.util.ArrayList;

/**
 * Created by pmmde on 6/13/2016.
 */
public class Castle extends WorldObject{
    public Castle(WorldData w, Point3D offset, Matrix r, double parts, double towerSize, double towerHeight) {
        super(w);
        pointsOriginal=new ArrayList<>();
        colors=new ArrayList<>();
        sides=new ArrayList<>();
        edges=new ArrayList<>();
        waters=new ArrayList<>();

        colors.add(new Color3f(1.0f, 0.0f, 0.5f));//3
        colors.add(new Color3f(0.2f, 0.2f, 0.2f));//4
        colors.add(new Color3f(0.4f, 0.4f, 0.4f));//5
        colors.add(new Color3f(0.0f, 1.0f, 0.0f));//2
        for (int j = 0; j < 2; j++) {
            double angleGrowSize = Math.PI / (parts / 2);
            for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
                Point3D p1 = new Point3D(j * towerHeight + Math.cos(angle) * towerSize, 0 + Math.sin(angle) * towerSize, 0);
                Point3D p2 = new Point3D(j * towerHeight + Math.cos(angle) * towerSize, 0 + Math.sin(angle) * towerSize, 0 + towerHeight);
                Point3D p3 = new Point3D(j * towerHeight + Math.cos(angle + angleGrowSize) * towerSize, 0 + Math.sin(angle + angleGrowSize) * towerSize, 0 + towerHeight);
                Point3D p4 = new Point3D(j * towerHeight + Math.cos(angle + angleGrowSize) * towerSize, 0 + Math.sin(angle + angleGrowSize) * towerSize, 0);
                addSquare(p1, p2, p3, p4, 0,0.1);
                Point3D top = new Point3D(j * towerHeight, 0, 0 + towerHeight * 1.5);
                Point3D tp1 = new Point3D(j * towerHeight + Math.cos(angle) * towerSize * 1.2, 0 + Math.sin(angle) * towerSize * 1.2, 0 + towerHeight);
                Point3D tp2 = new Point3D(j * towerHeight + Math.cos(angle + angleGrowSize) * towerSize * 1.2, 0 + Math.sin(angle + angleGrowSize) * towerSize * 1.2, 0 + towerHeight);
                addTriangle(tp1, tp2, top, 1,0.1);
            }
        }
        Point3D b1 = new Point3D(0, 0 + 20, 0 + towerHeight * 0.5);
        Point3D b2 = new Point3D(0, 0 - 20, 0 + towerHeight * 0.5);
        Point3D b3 = new Point3D(0 + towerHeight, 0 - 20, 0 + towerHeight * 0.5);
        Point3D b4 = new Point3D(0 + towerHeight, 0 + 20, 0 + towerHeight * 0.5);
        addSquare(b1, b2, b3, b4, 2,0.1 );
        Point3D t1 = new Point3D(0, 0 + 20, 0 + towerHeight * 0.7);
        Point3D t2 = new Point3D(0, 0 - 20, 0 + towerHeight * 0.7);
        Point3D t3 = new Point3D(0 + towerHeight, 0 - 20, 0 + towerHeight * 0.7);
        Point3D t4 = new Point3D(0 + towerHeight, 0 + 20, 0 + towerHeight * 0.7);
        addSquare(t1, t2, t3, t4, 2,0.1);
        addSquare(b1, t1, t4, b4, 2,0.1);
        addSquare(b2, t2, t3, b3, 2,0.1);

        addSquare(new Point3D(0-40,0-40,0),
                new Point3D(20*13-40,0-40,0),
                new Point3D(20*13-40,20*4-40,0),
                new Point3D(0-40,20*4-40,0),
                3,0.1);
        center=offset;
        rotation=r;
    }
}
