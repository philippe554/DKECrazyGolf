package CrazyGolf.PhysicsEngine.Objects.Native;

import CrazyGolf.PhysicsEngine.Objects.Parts.Edge;
import CrazyGolf.PhysicsEngine.Objects.Parts.Side;
import CrazyGolf.PhysicsEngine.Objects.Parts.Water;
import CrazyGolf.PhysicsEngine.Physics3.WorldObject;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;
import java.util.ArrayList;

/**
 * Created by pmmde on 5/30/2016.
 */
public class Tree extends WorldObject {
    public Tree(Point3D place,WorldData w){
        super(w);

        mergeParent=true;
        useShaders=false;
        final double height = Math.random()*100+100;
        final double leaveDistance = 20;
        final int amountOfLeaves = (int) (height/leaveDistance);


        pointsOriginal=new ArrayList<>(4+3*amountOfLeaves);
        colors=new ArrayList<>(3);
        sides=new ArrayList<>(3+amountOfLeaves);
        edges=new ArrayList<>();
        waters=new ArrayList<>();

        colors.add(new Color3f(0.55f,0.27f,0.07f));
        colors.add(new Color3f(0.13f,0.55f,0.13f));
        colors.add(new Color3f(0,0.39f,0));

        pointsOriginal.add(new Point3D(-8,-8,0));
        pointsOriginal.add(new Point3D(8,-8,0));
        pointsOriginal.add(new Point3D(0,8,0));
        pointsOriginal.add(new Point3D(0,0,height));

        for(int z=0;z<amountOfLeaves;z++){
            double angle = Math.random() * Math.PI*2;
            double heightPortion = (height-z*leaveDistance)/4;
            double min=25;
            pointsOriginal.add(new Point3D(Math.cos(angle)*(Math.random()*heightPortion+heightPortion+min),Math.sin(angle)*(Math.random()*heightPortion+heightPortion+min),
                    z*leaveDistance+Math.random()*leaveDistance));
            angle+=Math.PI*2/3;
            pointsOriginal.add(new Point3D(Math.cos(angle)*(Math.random()*heightPortion+heightPortion+min),Math.sin(angle)*(Math.random()*heightPortion+heightPortion+min),
                    z*leaveDistance+Math.random()*leaveDistance));
            angle+=Math.PI*2/3;
            pointsOriginal.add(new Point3D(Math.cos(angle)*(Math.random()*heightPortion+heightPortion+min),Math.sin(angle)*(Math.random()*heightPortion+heightPortion+min),
                    z*leaveDistance+Math.random()*leaveDistance));
        }

        sides.add(new Side(0,1,3,0,0));
        sides.add(new Side(1,2,3,0,0));
        sides.add(new Side(2,0,3,0,0));

        boolean color=true;
        for(int i=0;i<amountOfLeaves;i++){
            sides.add(new Side(4+i*3,4+3*i+1,4+3*i+2,color?1:2,0));
            color=!color;
        }

        center=place;
    }
}
