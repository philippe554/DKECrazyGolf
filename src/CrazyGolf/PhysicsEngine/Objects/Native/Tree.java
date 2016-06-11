package CrazyGolf.PhysicsEngine.Objects.Native;

import CrazyGolf.PhysicsEngine.Objects.Parts.Edge;
import CrazyGolf.PhysicsEngine.Objects.Parts.Side;
import CrazyGolf.PhysicsEngine.Objects.Parts.Water;
import CrazyGolf.PhysicsEngine.Objects.WorldObject;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;

/**
 * Created by pmmde on 5/30/2016.
 */
public class Tree extends WorldObject {
    public Tree(Point3D place,WorldData w){
        super(w);
        mergeParent=true;
        final double height = Math.random()*100+100;
        final double leaveDistance = 20;
        final int amountOfLeaves = (int) (height/leaveDistance);

        points=new Point3D[4+3*amountOfLeaves];
        pointsOriginal=new Point3D[points.length];
        colors=new Color3f[3];
        sides=new Side[3+amountOfLeaves];
        edges=new Edge[0];
        waters=new Water[0];

        colors[0]=new Color3f(0.55f,0.27f,0.07f);
        colors[1]=new Color3f(0.13f,0.55f,0.13f);
        colors[2]=new Color3f(0,0.39f,0);

        pointsOriginal[0]=new Point3D(-8,-8,0);
        pointsOriginal[1]=new Point3D(8,-8,0);
        pointsOriginal[2]=new Point3D(0,8,0);
        pointsOriginal[3]=new Point3D(0,0,height);

        for(int z=0;z<amountOfLeaves;z++){
            double angle = Math.random() * Math.PI*2;
            double heightPortion = (height-z*leaveDistance)/4;
            pointsOriginal[4+z*3]=new Point3D(Math.cos(angle)*(Math.random()*heightPortion+heightPortion),Math.sin(angle)*(Math.random()*heightPortion+heightPortion),z*leaveDistance+Math.random()*leaveDistance);
            angle+=Math.PI*2/3;
            pointsOriginal[4+z*3+1]=new Point3D(Math.cos(angle)*(Math.random()*heightPortion+heightPortion),Math.sin(angle)*(Math.random()*heightPortion+heightPortion),z*leaveDistance+Math.random()*leaveDistance);
            angle+=Math.PI*2/3;
            pointsOriginal[4+z*3+2]=new Point3D(Math.cos(angle)*(Math.random()*heightPortion+heightPortion),Math.sin(angle)*(Math.random()*heightPortion+heightPortion),z*leaveDistance+Math.random()*leaveDistance);
        }

        setCenter(place);
        setupBoxing();

        sides[0]=new Side(this,0,1,3,0,0);
        sides[1]=new Side(this,1,2,3,0,0);
        sides[2]=new Side(this,2,0,3,0,0);

        boolean color=true;
        for(int i=0;i<amountOfLeaves;i++){
            sides[3+i]=new Side(this,4+i*3,4+3*i+1,4+3*i+2,color?1:2,0);
            color=!color;
        }
    }
}
