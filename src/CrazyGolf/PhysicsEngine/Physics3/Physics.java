package CrazyGolf.PhysicsEngine.Physics3;

import CrazyGolf.PhysicsEngine.Objects.Parts.Ball;
import javafx.geometry.Point3D;

import java.util.ArrayList;

/**
 * Created by pmmde on 6/9/2016.
 */
public interface Physics {
    void step(boolean useBallBallCollision);
    void stepSimulated(ArrayList<Ball> simBalls, boolean useBallBallCollision);

    int getAmountBalls();
    Ball getBall(int i);
    void pushBall(int i, Point3D dir);
    boolean checkBallInHole(int i);
    boolean ballStoppedMoving(int i);
    void addNewBall();

    Point3D getHolePosition();
    Point3D getStartPosition();
}
