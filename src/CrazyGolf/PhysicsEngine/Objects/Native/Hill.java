package CrazyGolf.PhysicsEngine.Objects.Native;

import CrazyGolf.PhysicsEngine.Matrix;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import CrazyGolf.PhysicsEngine.Physics3.WorldObject;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;
import java.util.ArrayList;

/**
 * Created by pmmde on 6/13/2016.
 */
public class Hill extends WorldObject {
    public Hill(WorldData w, Point3D offset, Matrix r, double xSize, double ySize,double zSize, int parts) {
        super(w);
        pointsOriginal=new ArrayList<>();
        colors=new ArrayList<>();
        sides=new ArrayList<>();
        edges=new ArrayList<>();
        waters=new ArrayList<>();

        double xStep = xSize/parts;
        double yStep = ySize/parts;
        colors.add(new Color3f(0.0f, 1.0f, 0.0f));
        for(double i=0;i<(parts);i++)
        {
            for(double j=0;j<(parts);j++)
            {
                Point3D p1= new Point3D(i*xStep-xSize/2,j*yStep-ySize/2,(-Math.cos(i/parts*Math.PI)+1)*zSize/2);
                Point3D p2= new Point3D((i+1)*xStep-xSize/2,j*yStep-ySize/2,(-Math.cos((i+1)/parts*Math.PI)+1)*zSize/2);
                Point3D p3= new Point3D((i+1)*xStep-xSize/2,(j+1)*yStep-ySize/2,(-Math.cos((i+1)/parts*Math.PI)+1)*zSize/2);
                Point3D p4= new Point3D(i*xStep-xSize/2,(j+1)*yStep-ySize/2,(-Math.cos(i/parts*Math.PI)+1)*zSize/2);
                addSquare(p1,p2,p3,p4,0,0.1);
            }
        }
        center=offset;
        rotation=r;
    }
}
