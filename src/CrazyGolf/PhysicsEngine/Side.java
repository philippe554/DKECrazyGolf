package CrazyGolf.PhysicsEngine;

import javafx.geometry.Point3D;

/**
 * Created by pmmde on 3/12/2016.
 */
public class Side {
    public int[] points;
    public Point3D normal;
    public int color;

    public Point3D abc;
    public double d;
    public double Nv;
    public double friction;

    public Side(World world,int p1,int p2,int p3,int c,double f){
        points=new int[3];
        points[0]=p1;
        points[1]=p2;
        points[2]=p3;
        color=c;

        //pre calculations for collision detection

        Point3D d1= world.points.get(points[0]).subtract(world.points.get(points[2]));
        Point3D d2= world.points.get(points[1]).subtract(world.points.get(points[2]));

        abc = d1.crossProduct(d2);
        d=abc.dotProduct(world.points.get(points[2]));

        normal=abc.normalize();

        Nv = abc.dotProduct(normal);

        if(World.DEBUG) {
            if (0.0001f > Nv) {
                System.out.println("Small Nv found expected to divide by 0: " + Nv);
            }
        }

        friction=0.1;//f;
    }
}
