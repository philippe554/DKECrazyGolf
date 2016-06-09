package CrazyGolf.PhysicsEngine.Objects;

import CrazyGolf.PhysicsEngine.Objects.Parts.Ball;
import CrazyGolf.PhysicsEngine.Objects.Parts.Edge;
import CrazyGolf.PhysicsEngine.Objects.Parts.Side;
import CrazyGolf.PhysicsEngine.Objects.Parts.Water;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by pmmde on 5/28/2016.
 */
public abstract class WorldObject {
    protected Point3D[] points;
    protected Point3D[] pointsOriginal;
    protected Color3f[] colors;
    protected Side[] sides;
    protected Edge[] edges;
    protected Water[] waters;

    protected LinkedList<WorldObject> subObjects;

    private int ID;
    private static int nextID=0;

    private Point3D center;
    private double[][] rotation;

    private boolean newPlace;
    private Point3D[] boxing;
    private Point3D[] boxingOriginal;
    public static final double gravity=1;
    public boolean mergeParent=false;

    public WorldObject(){
        ID=nextID;
        nextID++;
        newPlace=true;
        center=new Point3D(0,0,0);
        subObjects=new LinkedList<>();
    }

    protected void move(){
        newPlace=false;
        for(int i=0;i<pointsOriginal.length;i++){
            points[i]=pointsOriginal[i].add(center);
            //TODO add rotation
        }
    }
    protected void setupBoxing(){
        if(pointsOriginal.length>0) {
            boxing = new Point3D[2];
            boxingOriginal = new Point3D[2];
            boxingOriginal[0] = pointsOriginal[0].add(0,0,0);
            boxingOriginal[1] = pointsOriginal[0].add(0,0,0);
            for (int i = 0; i <pointsOriginal.length;i++){
                if(pointsOriginal[i].getX()<boxingOriginal[0].getX()){
                    boxingOriginal[0]=new Point3D(pointsOriginal[i].getX(),boxingOriginal[0].getY(),boxingOriginal[0].getZ());
                }
                if(pointsOriginal[i].getY()<boxingOriginal[0].getY()){
                    boxingOriginal[0]=new Point3D(boxingOriginal[0].getX(),pointsOriginal[i].getY(),boxingOriginal[0].getZ());
                }
                if(pointsOriginal[i].getZ()<boxingOriginal[0].getZ()){
                    boxingOriginal[0]=new Point3D(boxingOriginal[0].getX(),boxingOriginal[0].getY(),pointsOriginal[i].getZ());
                }
                if(pointsOriginal[i].getX()>boxingOriginal[1].getX()){
                    boxingOriginal[1]=new Point3D(pointsOriginal[i].getX(),boxingOriginal[1].getY(),boxingOriginal[1].getZ());
                }
                if(pointsOriginal[i].getY()>boxingOriginal[1].getY()){
                    boxingOriginal[1]=new Point3D(boxingOriginal[1].getX(),pointsOriginal[i].getY(),boxingOriginal[1].getZ());
                }
                if(pointsOriginal[i].getZ()>boxingOriginal[1].getZ()){
                    boxingOriginal[1]=new Point3D(boxingOriginal[1].getX(),boxingOriginal[1].getY(),pointsOriginal[i].getZ());
                }
            }
        }
    }
    public void setCenter(Point3D p){
        newPlace=true;
        center=p.add(0,0,0);
    }
    public void moveCenter(Point3D p){
        newPlace=true;
        center=center.add(p);
    }

    private boolean sideCollision(Side side, Ball ball){
        boolean result=false;

        double Nr0 = side.abc.dotProduct(ball.place);

        double t = (side.d - Nr0) / side.Nv;

        Point3D intersection = ball.place.add(side.normal.multiply(t));

        double distance = intersection.distance(ball.place);

        if (distance < ball.size) {
            if (PointInTriangle(intersection, points[side.points[0]],
                    points[side.points[1]],
                    points[side.points[2]])) {
                double dir = t > 0 ? -1 : 1;
                ball.place = intersection.add(side.normal.multiply(dir * ball.size));
                ball.velocity = ball.velocity.subtract(side.normal.multiply(ball.velocity.dotProduct(side.normal) * 1.8));
                result =true;
            }
        }
        return result;
    }
    private boolean edgeCollision(Edge edge, Ball ball){
        boolean result=false;
        double t = edge.unit.dotProduct(ball.place.subtract(points[edge.points[0]]));

        if (t > 0 && t < edge.lenght) {
            Point3D clossest = points[edge.points[0]].add(edge.unit.multiply(t));
            Point3D unit = ball.place.subtract(clossest);
            double distance = unit.magnitude();
            unit = unit.normalize();
            if (distance < ball.size) {
                ball.place = clossest.add(unit.multiply(ball.size));
                ball.velocity = ball.velocity.subtract(unit.multiply(ball.velocity.dotProduct(unit) * 1.8));
                result=true;
            }
        }
        return result;
    }
    private boolean pointCollision(int i, Ball ball){
        boolean result=false;
        Point3D ballEndPoint = ball.place.subtract(points[i]);
        if (ballEndPoint.magnitude() < ball.size) {
            Point3D unit = ballEndPoint.normalize();
            ball.place = points[i].add(unit.multiply(ball.size));
            ball.velocity = ball.velocity.subtract(unit.multiply(ball.velocity.dotProduct(unit) * 1.8));
            result=true;
        }
        return result;
    }
    private void ballWater(Water w, Ball ball,double subFrameInv){
        if(ball.place.getX()>w.place[0].getX()&&
                ball.place.getX()<w.place[1].getX()&&
                ball.place.getY()>w.place[0].getY()&&
                ball.place.getY()<w.place[1].getY())
        {
            if(ball.place.getZ()>(w.place[1].getZ()+ball.size)||
                    ball.place.getZ()<(w.place[0].getZ()-ball.size)){
                //Ball is outside water
            }else{
                double volume=0.0;
                double completeVolume = (4/3)*Math.PI*Math.pow(ball.size,3);
                if(ball.place.getZ()<w.place[1].getZ()){
                    volume+=completeVolume/2;
                    if(ball.place.getZ()+ball.size<w.place[1].getZ()){
                        volume+=completeVolume/2;
                    }
                    else {
                        volume+=completeVolume/2;
                        double h=ball.size-(w.place[1].getZ()-ball.place.getZ());
                        volume-=((Math.PI*h*h)/3.0)*(3*ball.size-h);
                    }
                }
                else {
                    double h=w.place[1].getZ()-(ball.place.getZ()-ball.size);
                    volume+=((Math.PI*h*h)/3.0)*(3*ball.size-h);
                }
                double ratio = volume/completeVolume;
                ball.acceleration=ball.acceleration.add(0,0,volume*gravity*subFrameInv*0.0001);
                ball.velocity = ball.velocity.multiply(Math.pow((0.85+(1-ratio)*0.15),subFrameInv));
            }
        }
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

    public int getAmountSides(){return sides.length;}
    public Point3D getPoint(int i) {
        return points[i];
    }
    public Point3D getTriangle(int i,int j) {
        return points[sides[i].points[j]];
    }
    public Color3f getTriangleColor(int i) {
        return colors[sides[i].color];
    }
    public int getAmountSubObjects(){return subObjects.size();}
    public WorldObject getSubObject(int i){return subObjects.get(i);}

    public int getID(){return ID;}
}
