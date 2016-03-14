import javafx.geometry.Point3D;

/**
 * Created by pmmde on 3/12/2016.
 */
public class Side {
    public Point3D[] points;
    public Point3D normal;

    public Point3D abc;
    public double d;
    public double Nv;

    public Side(Point3D p1,Point3D p2,Point3D p3){
        points=new Point3D[3];
        points[0]=p1;
        points[1]=p2;
        points[2]=p3;

        //pre calculations for collision detection

        Point3D d1=points[0].subtract(points[2]);
        Point3D d2=points[1].subtract(points[2]);

        abc = d1.crossProduct(d2);
        d=abc.dotProduct(points[2]);

        normal=abc.normalize();

        Nv = abc.dotProduct(normal);
    }
}
