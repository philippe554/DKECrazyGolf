package CrazyGolf.PhysicsEngine.Objects.Native;

import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;

/**
 * Created by pmmde on 6/12/2016.
 */
public class Loop extends Factory{
    public Loop(WorldData w,Point3D center, double size, double width, int parts, double wallSize) {
        super(w);
        double angleGrowSize = Math.PI / (parts / 2);
        double widthCounter = 0;
        double widthIncrrement = width / parts;
        dynamicColors.add(new Color3f(0.8f, 0.8f, 0.8f));
        dynamicColors.add(new Color3f(0.5f, 0.5f, 0.5f));
        dynamicColors.add(new Color3f(0.0f, 1.0f, 0.0f));
        for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
            Point3D p1 = new Point3D(Math.sin(angle) * size, - width + widthCounter, 0 - Math.cos(angle) * size);
            Point3D p2 = new Point3D(Math.sin(angle + angleGrowSize) * size, 0 - width + widthCounter + widthIncrrement, 0 - Math.cos(angle + angleGrowSize) * size);
            Point3D p3 = new Point3D(Math.sin(angle) * size,  widthCounter, 0 - Math.cos(angle) * size);
            Point3D p4 = new Point3D(Math.sin(angle + angleGrowSize) * size, widthCounter + widthIncrrement, 0 - Math.cos(angle + angleGrowSize) * size);
            addSquare(p1, p2, p4, p3, 0,0.1);
            Point3D p1in = new Point3D(Math.sin(angle) * (size - wallSize), 0 - width + widthCounter, 0 - Math.cos(angle) * (size - wallSize));
            Point3D p2in = new Point3D(Math.sin(angle + angleGrowSize) * (size - wallSize), 0 - width + widthCounter + widthIncrrement, 0 - Math.cos(angle + angleGrowSize) * (size - wallSize));
            Point3D p3in = new Point3D(Math.sin(angle) * (size - wallSize), widthCounter, 0 - Math.cos(angle) * (size - wallSize));
            Point3D p4in = new Point3D(Math.sin(angle + angleGrowSize) * (size - wallSize), widthCounter + widthIncrrement, 0 - Math.cos(angle + angleGrowSize) * (size - wallSize));
            addSquare(p1, p2, p2in, p1in, 1,0.1);
            addSquare(p3, p4, p4in, p3in, 1,0.1);
            widthCounter += widthIncrrement;
        }
        addSquare(new Point3D(-size,-width,-140),
                new Point3D(-size,width,-140),
                new Point3D(size,width,-140),
                new Point3D(size,-width,-140)
                ,2,0.1);
        print(center);
    }
}
