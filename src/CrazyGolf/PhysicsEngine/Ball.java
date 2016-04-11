package CrazyGolf.PhysicsEngine;

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

    public Ball(double tsize,Point3D tplace)
    {
        mass=1;
        size=tsize;
        place=tplace;
        velocity=new Point3D(0,0,0);
        acceleration=new Point3D(0,0,0);
    }
}
