package CrazyGolf.PhysicsEngine;

import javafx.geometry.Point3D;

/**
 * Created by pmmde on 3/13/2016.
 */
public class Edge {
    public int[] points;
    public Point3D unit;
    public double lenght;

    public Edge(World world,int p1,int p2){
        points=new int[2];
        points[0]=p1;
        points[1]=p2;
        unit = world.getPoint(p2).subtract(world.getPoint(p1));
        lenght=unit.magnitude();
        unit=unit.normalize();
    }
}
