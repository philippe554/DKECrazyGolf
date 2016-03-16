import javafx.geometry.Point3D;

import javax.vecmath.Color3f;

/**
 * Created by pmmde on 3/12/2016.
 */
public class Side {
    public Point3D[] points;
    public Edge[] edges;
    public Point3D normal;
    public Color3f color;

    public Point3D abc;
    public double d;
    public double Nv;

    public Side(Point3D p1,Point3D p2,Point3D p3,Color3f c){
        points=new Point3D[3];
        points[0]=p1;
        points[1]=p2;
        points[2]=p3;
        color=c;

        //pre calculations for collision detection

        Point3D d1=points[0].subtract(points[2]);
        Point3D d2=points[1].subtract(points[2]);

        abc = d1.crossProduct(d2);
        d=abc.dotProduct(points[2]);

        normal=abc.normalize();

        Nv = abc.dotProduct(normal);

        edges=new Edge[3];
        edges[0]=new Edge(p1,p2);
        edges[1]=new Edge(p2,p3);
        edges[2]=new Edge(p3,p1);
    }
}
