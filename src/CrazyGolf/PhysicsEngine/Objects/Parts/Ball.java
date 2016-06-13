package CrazyGolf.PhysicsEngine.Objects.Parts;

import javafx.geometry.Point3D;

/**
 * Created by pmmde on 3/12/2016.
 */
public class Ball {
    public double mass;
    public double size;
    public Point3D place;
    public Point3D velocity;
    public Point3D acceleration;

    public Point3D oldPlace;
    public float friction;
    public int zeroCounter;

    public Point3D windVector;

    public static final float minVelocity=0.3f;
    public static final int thresholdCounter=10;
    public static final float minZ = -100;

    public Ball(double tsize,Point3D tplace)
    {
        mass=1;
        size=tsize;
        place=tplace;
        velocity=new Point3D(0,0,0);
        acceleration=new Point3D(0,0,0);
        windVector=new Point3D(0,0,0);
        oldPlace=tplace;
        friction=0;
        zeroCounter=0;
    }
}
