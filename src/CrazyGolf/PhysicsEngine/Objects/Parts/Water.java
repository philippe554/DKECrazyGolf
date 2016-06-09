package CrazyGolf.PhysicsEngine.Objects.Parts;

import javafx.geometry.Point3D;

/**
 * Created by pmmde on 5/17/2016.
 */
public class Water {
    public Point3D[] place;
    public int color;
    public Water(Point3D[] tPlace,int c){
        place=tPlace;
        color=c;
    }
}
