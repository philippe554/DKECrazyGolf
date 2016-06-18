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
public class Bridge extends WorldObject{
    public Bridge(WorldData w, Point3D offset, Matrix r , double length, double height, double borderHeight, double width, int parts) {
        super(w);
        pointsOriginal=new ArrayList<>();
        colors=new ArrayList<>();
        sides=new ArrayList<>();
        edges=new ArrayList<>();
        waters=new ArrayList<>();

        colors.add(new Color3f(0.8f, 0.8f, 0.8f));//0
        colors.add(new Color3f(0.5f, 0.5f, 0.5f));//1
        colors.add(new Color3f(0.0f, 1.0f, 0.0f));//2
        colors.add(new Color3f(0.2f, 0.2f, 0.2f));
        for (int j = 0; j < 2; j++) {
            double angleGrowSize = Math.PI / (parts / 2);
            for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
                Point3D p1 = new Point3D(Math.cos(angle) * (width * 0.25), j * length + Math.sin(angle) * (width * 0.25), 0);
                Point3D p2 = new Point3D(Math.cos(angle) * (width * 0.25), j * length + Math.sin(angle) * (width * 0.25), height);
                Point3D p3 = new Point3D(Math.cos(angle + angleGrowSize) * (width * 0.25), j * length + Math.sin(angle + angleGrowSize) * (width * 0.25), height);
                Point3D p4 = new Point3D(Math.cos(angle + angleGrowSize) * (width * 0.25), j * length + Math.sin(angle + angleGrowSize) * (width * 0.25), 0);
                addSquare(p1, p2, p3, p4, 3,0.1);
            }
        }
        Point3D p1 = new Point3D(0 - width * 0.5, 0 - width * 0.5, 0 + height);
        Point3D p2 = new Point3D(0 + width * 0.5, 0 - width * 0.5, 0 + height);
        Point3D p3 = new Point3D(0 + width * 0.5, 0 + width * 0.5 + length, 0 + height);
        Point3D p4 = new Point3D(0 - width * 0.5, 0 + width * 0.5 + length, 0 + height);
        addSquare(p1, p2, p3, p4, 0,0.1);
        Point3D p1d = new Point3D(0 - width * 0.5, 0 - width * 0.5 - length / 2, 0);
        Point3D p2d = new Point3D(0 + width * 0.5, 0 - width * 0.5 - length / 2, 0);
        Point3D p3d = new Point3D(0 + width * 0.5, 0 + width * 0.5 + 1.5 * length, 0);
        Point3D p4d = new Point3D(0 - width * 0.5, 0 + width * 0.5 + 1.5 * length, 0);
        addSquare(p1, p2, p2d, p1d, 0,0.1);
        addSquare(p3, p4, p4d, p3d, 0,0.1);
        addSquare(p1, p4, p4.add(0, 0, borderHeight), p1.add(0, 0, borderHeight), 1,0.1);
        addSquare(p2, p3, p3.add(0, 0, borderHeight), p2.add(0, 0, borderHeight), 1,0.1);

        addSquare(p1, p1d, p1d.add(0, 0, borderHeight), p1.add(0, 0, borderHeight), 1,0.1);
        addSquare(p2, p2d, p2d.add(0, 0, borderHeight), p2.add(0, 0, borderHeight), 1,0.1);
        addSquare(p3, p3d, p3d.add(0, 0, borderHeight), p3.add(0, 0, borderHeight), 1,0.1);
        addSquare(p4, p4d, p4d.add(0, 0, borderHeight), p4.add(0, 0, borderHeight), 1,0.1);

        addSquare(new Point3D(0-40,0-140,0),
                new Point3D(20*4-40,0-140,0),
                new Point3D(20*4-40,20*24-140,0),
                new Point3D(0-40,20*24-140,0),
                2,1);

        center=offset;
        rotation=r;
    }
}
