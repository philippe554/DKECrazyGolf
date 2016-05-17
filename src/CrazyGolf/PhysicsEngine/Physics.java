package CrazyGolf.PhysicsEngine;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by pmmde on 5/16/2016.
 */
public abstract class Physics extends WorldContainer{

    public Physics(LinkedList<String> input){
        super(input);
    }

    @Override
    public synchronized void step(boolean useBallBallCollision) {
        if(!editMode) {
            double maxV = -1;
            double ballSize = 0;
            for (int i = 0; i < balls.size(); i++) {
                if (balls.get(i).velocity.magnitude() > maxV) {
                    maxV = balls.get(i).velocity.magnitude();
                    ballSize = balls.get(i).size;
                }
            }
            int subSteps=((int) (maxV / ballSize * 1.1*precision) + 1);
            stepCalc(subSteps,useBallBallCollision);
        }
    }
    @Override
    public synchronized void stepSimulated(ArrayList<Ball> simBalls, boolean useBallBallCollision){
        ArrayList<Ball> original=balls;
        balls=simBalls;
        step(useBallBallCollision);
        balls=original;
    }
    abstract void stepCalc(int subframes,boolean useBallBallCollision);

    protected boolean sideCollision(int i,int j){
        boolean result=false;

        double Nr0 = sides.get(j).abc.dotProduct(balls.get(i).place);

        double t = (sides.get(j).d - Nr0) / sides.get(j).Nv;

        Point3D intersection = balls.get(i).place.add(sides.get(j).normal.multiply(t));

        double distance = intersection.distance(balls.get(i).place);

        if (distance < balls.get(i).size) {
            if (PointInTriangle(intersection, points.get(sides.get(j).points[0]),
                    points.get(sides.get(j).points[1]),
                    points.get(sides.get(j).points[2]))) {
                double dir = t > 0 ? -1 : 1;
                balls.get(i).place = intersection.add(sides.get(j).normal.multiply(dir * balls.get(i).size));
                balls.get(i).velocity = balls.get(i).velocity.subtract(sides.get(j).normal.multiply(balls.get(i).velocity.dotProduct(sides.get(j).normal) * 1.8));
                result =true;
            }
        }
        return result;
    }
    protected boolean edgeCollision(int i,int j){
        boolean result=false;
        double t = edges.get(j).unit.dotProduct(balls.get(i).place.subtract(points.get(edges.get(j).points[0])));

        if (t > 0 && t < edges.get(j).lenght) {
            Point3D clossest = points.get(edges.get(j).points[0]).add(edges.get(j).unit.multiply(t));
            Point3D unit = balls.get(i).place.subtract(clossest);
            double distance = unit.magnitude();
            unit = unit.normalize();
            if (distance < balls.get(i).size) {
                balls.get(i).place = clossest.add(unit.multiply(balls.get(i).size));
                balls.get(i).velocity = balls.get(i).velocity.subtract(unit.multiply(balls.get(i).velocity.dotProduct(unit) * 1.8));
                result=true;
            }
        }
        return result;
    }
    protected boolean pointCollision(int i,int j){
        boolean result=false;
        Point3D ballEndPoint = balls.get(i).place.subtract(points.get(j));
        if (ballEndPoint.magnitude() < balls.get(i).size) {
            Point3D unit = ballEndPoint.normalize();
            balls.get(i).place = points.get(j).add(unit.multiply(balls.get(i).size));
            balls.get(i).velocity = balls.get(i).velocity.subtract(unit.multiply(balls.get(i).velocity.dotProduct(unit) * 1.8));
            result=true;
        }
        return result;
    }
    protected boolean ballCollision(int i, int j){
        boolean result=false;

        Point3D distanceVector = balls.get(i).place.subtract(balls.get(j).place);

        if(distanceVector.magnitude()<(balls.get(i).size+balls.get(j).size))
        {
            Point3D l12 = balls.get(j).place.subtract(balls.get(i).place).normalize();
            Point3D l21 = balls.get(i).place.subtract(balls.get(j).place).normalize();

            Point3D v1=l12.multiply(balls.get(i).velocity.dotProduct(l12));
            Point3D v2=l21.multiply(balls.get(j).velocity.dotProduct(l21));

            Point3D v1p=balls.get(i).velocity.subtract(v1);
            Point3D v2p=balls.get(j).velocity.subtract(v2);

            double m1=balls.get(i).mass;
            double m2=balls.get(j).mass;

            balls.get(i).velocity=v1.multiply (m1-m2).add(v2.multiply(2 * m2)).multiply(1/(m1+m2)).add(v1p);
            balls.get(j).velocity=v2.multiply (m2-m1).add(v1.multiply(2 * m1)).multiply(1/(m1+m2)).add(v2p);

            balls.get(i).place=balls.get(i).place.add(balls.get(i).velocity);
            balls.get(j).place=balls.get(j).place.add(balls.get(j).velocity);
        }

        return result;
    }
    protected void ballCollisionComplete() {
        for(int i=0;i<balls.size();i++)
        {
            for(int j=i+1;j<balls.size();j++)
            {
                ballCollision(i,j);
            }
        }
        /*double total=0;
        for(int i=0;i<balls.size();i++)
        {
            total+=balls.get(i).velocity.magnitude()*balls.get(i).velocity.magnitude()*balls.get(i).mass;
        }
        System.out.println("Total kinetic energy: "+total);*/
    }
    private boolean PointInTriangle(Point3D p, Point3D a, Point3D b, Point3D c) {
        Point3D v0 = c.subtract(a);
        Point3D v1 = b.subtract(a);
        Point3D v2 = p.subtract(a);

        double v0v0 = v0.dotProduct(v0);
        double v0v1 = v0.dotProduct(v1);
        double v0v2 = v0.dotProduct(v2);
        double v1v1 = v1.dotProduct(v1);
        double v1v2 = v1.dotProduct(v2);

        double u = (v1v1 * v0v2 - v0v1 * v1v2) / (v0v0 * v1v1 - v0v1 * v0v1);
        double v = (v0v0 * v1v2 - v0v1 * v0v2) / (v0v0 * v1v1 - v0v1 * v0v1);
        if (u >= 0 && v >= 0 && u <= 1 && v <= 1 && (u + v) <= 1) {
            return true;
        } else {
            return false;
        }
    }
}
