package CrazyGolf.PhysicsEngine.Objects.Native;

import CrazyGolf.PhysicsEngine.Matrix;
import CrazyGolf.PhysicsEngine.Objects.Parts.Water;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;

/**
 * Created by pmmde on 6/13/2016.
 */
public class Pool extends Factory{
    public Pool(WorldData w, Point3D offset, Matrix r, double size, double debt, int parts, double waterDebt) {
        super(w);
        dynamicColors.add(new Color3f(0.0f, 1.0f, 0.0f));
        dynamicColors.add(new Color3f(0.0f, 0.8f, 1.0f));

        dynamicWaters.add(new Water(new Point3D[]{new Point3D(0,0,-debt).add(offset),new Point3D(size,size,-waterDebt).add(offset)},1));
        double xStep=size/parts;
        double yStep=size/parts;
        for(double i=0;i<parts;i++)
        {
            for(double j=0;j<parts;j++){
                Point3D p1=new Point3D(i*xStep,j*yStep,((Math.max((Math.cos(i/parts*Math.PI*2)-1),(Math.cos(j/parts*Math.PI*2)-1)))/4)*debt);
                Point3D p2=new Point3D((i+1)*xStep,j*yStep,((Math.max((Math.cos((i+1.0)/parts*Math.PI*2)-1),(Math.cos(j/parts*Math.PI*2)-1))))/4*debt);
                Point3D p3=new Point3D((i+1)*xStep,(j+1)*yStep,((Math.max((Math.cos((i+1.0)/parts*Math.PI*2)-1),(Math.cos((j+1.0)/parts*Math.PI*2)-1))))/4*debt);
                Point3D p4=new Point3D(i*xStep,(j+1)*yStep,((Math.max((Math.cos(i/parts*Math.PI*2)-1),(Math.cos((j+1.0)/parts*Math.PI*2)-1))))/4*debt);
                addSquare(p1,p2,p3,p4,0,0.1);
            }
        }
        print(offset,r);
    }
}
