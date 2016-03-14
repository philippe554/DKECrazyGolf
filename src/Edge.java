import javafx.geometry.Point3D;

/**
 * Created by pmmde on 3/13/2016.
 */
public class Edge {
    public Point3D[] points;
    public Point3D unit;
    public double lenght;

    public Edge(Point3D p1,Point3D p2){
        points=new Point3D[2];
        points[0]=p1;
        points[1]=p2;
        unit = p2.subtract(p1);
        lenght=unit.magnitude();
        unit=unit.normalize();
    }
}
