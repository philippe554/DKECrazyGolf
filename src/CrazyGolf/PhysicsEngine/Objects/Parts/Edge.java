package CrazyGolf.PhysicsEngine.Objects.Parts;

import CrazyGolf.PhysicsEngine.Physics3.WorldObject;
import CrazyGolf.PhysicsEngine.Physics12.World;
import javafx.geometry.Point3D;

/**
 * Created by pmmde on 3/13/2016.
 */
public class Edge {
    public int[] points;
    public Point3D unit;
    public double lenght;

    public Edge(World world, int p1, int p2){
        points=new int[2];
        points[0]=p1;
        points[1]=p2;
        unit = world.getPoint(p2).subtract(world.getPoint(p1));
        lenght=unit.magnitude();
        unit=unit.normalize();
    }
    public Edge(int p1, int p2){
        points=new int[2];
        points[0]=p1;
        points[1]=p2;
    }
    public void updateData(WorldObject worldObject){
        unit = worldObject.getPoint(points[1]).subtract(worldObject.getPoint(points[0]));
        lenght=unit.magnitude();
        unit=unit.normalize();
    }
}
