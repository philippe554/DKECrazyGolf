package CrazyGolf.PhysicsEngine.Objects.Native;

import CrazyGolf.PhysicsEngine.Objects.Parts.Edge;
import CrazyGolf.PhysicsEngine.Objects.Parts.Side;
import CrazyGolf.PhysicsEngine.Objects.Parts.Water;
import CrazyGolf.PhysicsEngine.Objects.WorldObject;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;

/**
 * Created by pmmde on 6/11/2016.
 */
public class Grass extends WorldObject {
    public Grass(Point3D p1,Point3D p2,WorldData w){
        super(w);
        points=new Point3D[4];
        pointsOriginal=new Point3D[points.length];
        colors=new Color3f[1];
        sides=new Side[2];
        edges=new Edge[0];
        waters=new Water[0];

        colors[0]=new Color3f(0.0f, 1.0f, 0.0f);

        pointsOriginal[0]=p1.add(0,0,0);
        pointsOriginal[1]=new Point3D(p1.getX(),p2.getY(),p1.getZ());
        pointsOriginal[2]=new Point3D(p2.getX(),p2.getY(),p1.getZ());
        pointsOriginal[3]=new Point3D(p2.getX(),p1.getY(),p1.getZ());

        setCenter(p1.midpoint(p2));
        setupBoxing();

        sides[0]=new Side(this,0,1,3,0,0.1);
        sides[1]=new Side(this,1,2,3,0,0.1);
    }
}
