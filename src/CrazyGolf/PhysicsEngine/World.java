package CrazyGolf.PhysicsEngine;

import javafx.geometry.Point3D;

import javax.vecmath.Color3f;
import java.util.ArrayList;

/**
 * Created by pmmde on 4/30/2016.
 */
public interface World {
    void step(boolean useBallBallCollision);
    void stepSimulated(ArrayList<Ball> simBalls,boolean useBallBallCollision);
    void cleanUp();

    int getAmountBalls();
    double getBallSize(int i);
    void pushBall(int i, Point3D dir);
    boolean checkBallInHole(int i);
    Point3D getHolePosition();
    Point3D getBallPosition(int i);
    Point3D getBallVelocity(int i);
    void setBallPosition(int i,Point3D pos);
    void setBallVelocity(int i,Point3D vel);
    int getAmountTriangles();
    Point3D[] getTriangle(int i);
    Color3f getTriangleColor(int i);
    int getAmountPoints();
    Point3D getPoint(int i);
    boolean isBallOutsideWorld(int i);

    boolean DEBUG = true;
    boolean CPUCHECK = false;
    int precision=4;
    int maxPower=40;
    int ballSize=20;
}
