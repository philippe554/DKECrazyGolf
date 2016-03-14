import javafx.geometry.Point3D;

/**
 * Created by pmmde on 3/12/2016.
 */
public class Tester {
    public static void main(String[]args)
    {
        Point3D p1 = new Point3D(0,0,0);
        Point3D p2 = p1.add(10,10,10);
        p2=p2.normalize();
        System.out.print(p2.getX());
    }
}
