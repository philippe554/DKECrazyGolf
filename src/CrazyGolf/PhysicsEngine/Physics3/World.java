package CrazyGolf.PhysicsEngine.Physics3;

import CrazyGolf.PhysicsEngine.Objects.Parts.Ball;
import javafx.geometry.Point3D;

import java.util.LinkedList;

/**
 * Created by pmmde on 6/9/2016.
 */
public interface World {
    int getAmountWorldObjects();
    WorldObject getWorldObject(int i);

    void updateTerain(Point3D center);
    WorldObject getNextNewObject();
    WorldObject getNextUpdateObject();
    Integer getNextRemoveObject();

    int getAmountBalls();
    Ball getBall(int i);

    void load(LinkedList<String> data);
    void load(String[][]data,double gs,Point3D offset);
    LinkedList<String> save();
}
