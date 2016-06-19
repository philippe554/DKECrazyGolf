package CrazyGolf.PhysicsEngine.Physics3;

import CrazyGolf.PhysicsEngine.Matrix;
import CrazyGolf.PhysicsEngine.Objects.Parts.Ball;
import CrazyGolf.PhysicsEngine.Objects.Parts.Edge;
import CrazyGolf.PhysicsEngine.Objects.Parts.Side;
import CrazyGolf.PhysicsEngine.Objects.Parts.Water;
import CrazyGolf.PhysicsEngine.Objects.Terain.TerainChunk;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by pmmde on 5/28/2016.
 */
public class WorldObject {
    public static final double gravity=1;

    protected ArrayList<Point3D> points;
    protected ArrayList<Point3D> pointsOriginal;
    protected ArrayList<Color3f> colors;
    protected ArrayList<Side> sides;
    protected ArrayList<Edge>edges;
    protected ArrayList<Water> waters;

    public ArrayList<WorldObject> subObjects;
    public WorldData world;

    private int ID;
    private static int nextID=0;

    protected Point3D center;
    protected Matrix rotation;
    protected Point3D[] boxing;

    public boolean mergeParent=false;
    public boolean hasPointNormals=false;

    public WorldObject(WorldData w){
        ID=nextID;
        nextID++;
        center = new Point3D(0,0,0);
        rotation = Matrix.getRotatoinMatrix(0,0,0);
        subObjects=new ArrayList<>();
        world=w;
    }
    public void load(LinkedList<String> field){
        int sort=0;
        String copyEnd="";
        boolean copyLock=false;
        Class copyObject = null;
        boolean nativeClassError=false;
        LinkedList<String>copyData = null;
        for (int i = 0; i < field.size(); i++) {
            if(!copyLock) {
                if (field.get(i).equals("points")) {
                    sort = 1;
                    pointsOriginal = new ArrayList(Integer.parseInt(field.get(i + 1)));
                    i++;
                } else if (field.get(i).equals("edges")) {
                    sort = 2;
                    edges = new ArrayList(Integer.parseInt(field.get(i + 1)));
                    i++;
                } else if (field.get(i).equals("triangels")) {
                    sort = 3;
                    sides = new ArrayList(Integer.parseInt(field.get(i + 1)));
                    i++;
                } else if (field.get(i).equals("colors")) {
                    sort = 4;
                    colors = new ArrayList(Integer.parseInt(field.get(i + 1)));
                    i++;
                } else if (field.get(i).equals("water")) {
                    sort = 5;
                    waters = new ArrayList(Integer.parseInt(field.get(i + 1)));
                    i++;
                } else if (field.get(i).equals("ObjectStart")) {
                    sort = 6;
                    copyLock = true;
                    copyEnd = "ObjectEnd-" + field.get(i + 1);
                    copyData = new LinkedList<>();
                    copyData.add(field.get(i + 1));
                    i++;
                } else if (field.get(i).equals("ObjectStartNative")) {
                    sort = 7;
                    copyLock = true;
                    copyEnd = "ObjectEndNative-" + field.get(i + 1);
                    nativeClassError = false;
                    copyData = new LinkedList<>();
                    copyData.add(field.get(i + 1));
                    try {
                        copyObject = Class.forName(field.get(i + 2));
                    } catch (ClassNotFoundException e) {
                        nativeClassError = true;
                        System.out.println("Class not found (1): " + field.get(i + 2));
                    }
                    i += 2;
                } else {
                    if (sort == 1) {
                        String[] data = field.get(i).split(";");
                        if (data.length == 3) {
                            pointsOriginal.add(new Point3D(Double.parseDouble(data[0]),
                                    Double.parseDouble(data[1]), Double.parseDouble(data[2])));
                        }
                    } else if (sort == 2) {
                        String[] data = field.get(i).split(";");
                        if (data.length == 2) {
                            edges.add(new Edge(Integer.parseInt(data[0]), Integer.parseInt(data[1])));
                        }
                    } else if (sort == 3) {
                        String[] data = field.get(i).split(";");
                        if (data.length == 5) {
                            sides.add(new Side(Integer.parseInt(data[0]), Integer.parseInt(data[1]),
                                    Integer.parseInt(data[2]),
                                    Integer.parseInt(data[3]), Double.parseDouble(data[4])));
                        }
                    } else if (sort == 4) {
                        String[] data = field.get(i).split(";");
                        if (data.length == 4) {
                            colors.add(new Color3f(Float.parseFloat(data[0]), Float.parseFloat(data[1]),
                                    Float.parseFloat(data[2])));
                        }
                    } else if (sort == 5) {
                        String[] data = field.get(i).split(";");
                        if (data.length == 7) {
                            waters.add(new Water(new Point3D[]{new Point3D(Double.parseDouble(data[0]),
                                    Double.parseDouble(data[1]), Double.parseDouble(data[2])),
                                    new Point3D(Double.parseDouble(data[3]), Double.parseDouble(data[4]),
                                            Double.parseDouble(data[5]))}, Integer.parseInt(data[6])));
                        }
                    }
                }
            }else{
                if(field.get(i).equals(copyEnd)){
                    if(sort==6){
                        copyLock=false;
                        WorldObject wo = new WorldObject(world);
                        wo.load(copyData);
                        subObjects.add(wo);
                    }else if(sort==7)
                    {
                        copyLock=false;
                        if(!nativeClassError && copyObject!=null){
                            Object obj=null;
                            try {
                                obj = copyObject.newInstance();
                            } catch (InstantiationException e) {
                                System.out.println("Class not found (2): "+copyObject.getName());
                                nativeClassError=true;
                            } catch (IllegalAccessException e) {
                                System.out.println("Class not found (3): "+copyObject.getName());
                                nativeClassError=true;
                            }
                            if(!nativeClassError && obj!=null){
                                if(obj instanceof WorldObject){
                                    WorldObject wo = (WorldObject)obj;
                                    wo.load(copyData);
                                    subObjects.add(wo);
                                }else{
                                    System.out.println("Class is not an instance of WorldObject: "+copyObject.getName());
                                }
                            }
                        }
                    }
                }else{
                    copyData.add(field.get(i));
                }
            }
        }
    }
    public LinkedList<String> save(){
        LinkedList<String> data =new LinkedList<>();
        data.add("ObjectStart");
        data.add(String.valueOf(ID));
        if(pointsOriginal!=null && pointsOriginal.size()>0) {
            data.add("points");
            data.add(String.valueOf(points.size()));
            for (int i = 0; i < points.size(); i++) {
                data.add(points.get(i).getX() + ";" + points.get(i).getY() + ";" + points.get(i).getZ());
            }
            data.add("colors");
            data.add(String.valueOf(colors.size()));
            for (int i = 0; i < colors.size(); i++) {
                data.add(colors.get(i).getX() + ";" + colors.get(i).getY() + ";" + colors.get(i).getZ() + ";1");
            }
            data.add("edges");
            data.add(String.valueOf(edges.size()));
            for (int i = 0; i < edges.size(); i++) {
                data.add(edges.get(i).points[0] + ";" + edges.get(i).points[1]);
            }
            data.add("triangels");
            data.add(String.valueOf(sides.size()));
            for (int i = 0; i < sides.size(); i++) {
                data.add(sides.get(i).points[0] + ";" + sides.get(i).points[1] + ";" +
                        sides.get(i).points[2] + ";" + sides.get(i).color + ";" + sides.get(i).friction);
            }
            data.add("water");
            data.add(String.valueOf(waters.size()));
            for (int i = 0; i < waters.size(); i++) {
                data.add(waters.get(i).place[0].getX() + ";" + waters.get(i).place[0].getY() + ";" +
                        waters.get(i).place[0].getZ() + ";" + waters.get(i).place[1].getX() + ";" +
                        waters.get(i).place[1].getY() + ";" + waters.get(i).place[1].getZ() + ";" +
                        waters.get(i).color);
            }
        }
        for(int i=0;i<subObjects.size();i++){
            data.addAll(subObjects.get(i).save());
        }
        data.add("ObjectEnd-"+ID);
        return data;
    }

    private void setupBoxing(boolean recursively){
        if(points!=null && points.size()>0) {
            boxing = new Point3D[2];
            boxing[0] = points.get(0);
            boxing[1] = points.get(0);
            for (int i = 0; i < points.size(); i++) {
                if (points.get(i).getX() < boxing[0].getX()) {
                    boxing[0] = new Point3D(points.get(i).getX(), boxing[0].getY(), boxing[0].getZ());
                }
                if (points.get(i).getY() < boxing[0].getY()) {
                    boxing[0] = new Point3D(boxing[0].getX(), points.get(i).getY(), boxing[0].getZ());
                }
                if (points.get(i).getZ() < boxing[0].getZ()) {
                    boxing[0] = new Point3D(boxing[0].getX(), boxing[0].getY(), points.get(i).getZ());
                }
                if (points.get(i).getX() > boxing[1].getX()) {
                    boxing[1] = new Point3D(points.get(i).getX(), boxing[1].getY(), boxing[1].getZ());
                }
                if (points.get(i).getY() > boxing[1].getY()) {
                    boxing[1] = new Point3D(boxing[1].getX(), points.get(i).getY(), boxing[1].getZ());
                }
                if (points.get(i).getZ() > boxing[1].getZ()) {
                    boxing[1] = new Point3D(boxing[1].getX(), boxing[1].getY(), points.get(i).getZ());
                }
            }
            for (int i = 0; i < waters.size(); i++) {
                if (waters.get(i).place[0].getX() < boxing[0].getX()) {
                    boxing[0] = new Point3D(waters.get(i).place[0].getX(), boxing[0].getY(), boxing[0].getZ());
                }
                if (waters.get(i).place[0].getY() < boxing[0].getY()) {
                    boxing[0] = new Point3D(boxing[0].getX(), waters.get(i).place[0].getY(), boxing[0].getZ());
                }
                if (waters.get(i).place[0].getZ() < boxing[0].getZ()) {
                    boxing[0] = new Point3D(boxing[0].getX(), boxing[0].getY(),waters.get(i).place[0].getZ());
                }
                if (waters.get(i).place[1].getX() > boxing[1].getX()) {
                    boxing[1] = new Point3D(waters.get(i).place[1].getX(), boxing[1].getY(), boxing[1].getZ());
                }
                if (waters.get(i).place[1].getY() > boxing[1].getY()) {
                    boxing[1] = new Point3D(boxing[1].getX(), waters.get(i).place[1].getY(), boxing[1].getZ());
                }
                if (waters.get(i).place[1].getZ() > boxing[1].getZ()) {
                    boxing[1] = new Point3D(boxing[1].getX(), boxing[1].getY(), waters.get(i).place[1].getZ());
                }
            }
        }
        else {
            boxing = new Point3D[2];
            if(subObjects.size()>0){
                boxing[0] = subObjects.get(0).boxing[0];
                boxing[1] = subObjects.get(0).boxing[1];
            }else{
                boxing[0] = new Point3D(0,0,0);
                boxing[1] = new Point3D(0,0,0);
            }
        }
        if(recursively) {
            for (int i = 0; i < subObjects.size(); i++) {
                if (subObjects.get(i).boxing[0].getX() < boxing[0].getX()) {
                    boxing[0] = new Point3D(subObjects.get(i).boxing[0].getX(), boxing[0].getY(), boxing[0].getZ());
                }
                if (subObjects.get(i).boxing[0].getY() < boxing[0].getY()) {
                    boxing[0] = new Point3D(boxing[0].getX(), subObjects.get(i).boxing[0].getY(), boxing[0].getZ());
                }
                if (subObjects.get(i).boxing[0].getZ() < boxing[0].getZ()) {
                    boxing[0] = new Point3D(boxing[0].getX(), boxing[0].getY(), subObjects.get(i).boxing[0].getZ());
                }
                if (subObjects.get(i).boxing[1].getX() > boxing[1].getX()) {
                    boxing[1] = new Point3D(subObjects.get(i).boxing[1].getX(), boxing[1].getY(), boxing[1].getZ());
                }
                if (subObjects.get(i).boxing[1].getY() > boxing[1].getY()) {
                    boxing[1] = new Point3D(boxing[1].getX(), subObjects.get(i).boxing[1].getY(), boxing[1].getZ());
                }
                if (subObjects.get(i).boxing[1].getZ() > boxing[1].getZ()) {
                    boxing[1] = new Point3D(boxing[1].getX(), boxing[1].getY(), subObjects.get(i).boxing[1].getZ());
                }
            }
        }
    }
    public void setup(boolean recursively){
        if(recursively){
            for(int i=0;i<subObjects.size();i++){
                subObjects.get(i).setup(recursively);
            }
        }
        if(pointsOriginal!=null && pointsOriginal.size()>0) {
            if (points == null || points.size() != pointsOriginal.size()) {
                points = new ArrayList<>(pointsOriginal.size());
                for (int i = 0; i < pointsOriginal.size(); i++) {
                    points.add(rotation.multiply(pointsOriginal.get(i)).add(center));
                }
            }else {
                for (int i = 0; i < pointsOriginal.size(); i++) {
                    points.set(i, rotation.multiply(pointsOriginal.get(i)).add(center));
                }
            }
            for(int i=0;i<sides.size();i++) {
                sides.get(i).updateData(this);
            }
            for(int i=0;i<edges.size();i++) {
                edges.get(i).updateData(this);
            }
        }
        setupBoxing(recursively);
    }

    public void applyCollision(Ball ball,double subframeInv){
        if(ball.place.getX()+ball.size>boxing[0].getX()&&ball.place.getY()+ball.size>boxing[0].getY()&&ball.place.getZ()+ball.size>boxing[0].getZ()&&
                ball.place.getX()-ball.size<boxing[1].getX()&&ball.place.getY()-ball.size<boxing[1].getY()&&ball.place.getZ()-ball.size<boxing[1].getZ()) {
            if (pointsOriginal != null) {
                for (int j = 0; j < waters.size(); j++) {
                    ballWater(waters.get(j), ball, subframeInv);
                }
                for (int j = 0; j < sides.size(); j++) {
                    sideCollision(sides.get(j), ball);
                }
                for (int j = 0; j < edges.size(); j++) {
                    edgeCollision(edges.get(j), ball);
                }
                for (int j = 0; j < points.size(); j++) {
                    pointCollision(j, ball);

                }
            }
            for(int i=0;i<subObjects.size();i++)
            {
                subObjects.get(i).applyCollision(ball,subframeInv);
            }
        }
    }
    protected void sideCollision(Side side, Ball ball){
        double Nr0 = side.abc.dotProduct(ball.place);

        double t = (side.d - Nr0) / side.Nv;

        Point3D intersection = ball.place.add(side.normal.multiply(t));

        double distance = intersection.distance(ball.place);

        if (distance < ball.size) {
            if (PointInTriangle(intersection, points.get(side.points[0]),
                    points.get(side.points[1]),
                    points.get(side.points[2]))) {
                double dir = t > 0 ? -1 : 1;
                ball.place = intersection.add(side.normal.multiply(dir * ball.size));

                //ball.velocity = ball.velocity.subtract(side.normal.multiply(ball.velocity.dotProduct(side.normal) * 1.8));

                double angle=Math.asin(side.normal.multiply(-dir).dotProduct(ball.velocity.normalize()));
                ball.normalTotal=ball.normalTotal.add(side.normal.multiply(dir*angle));
                ball.normalCounter+=angle;

                if (side.friction > ball.friction) {
                    ball.friction = (float) side.friction;
                }
            }
        }
    }
    protected void edgeCollision(Edge edge, Ball ball){
        double t = edge.unit.dotProduct(ball.place.subtract(points.get(edge.points[0])));
        if (t > 0 && t < edge.lenght) {
            Point3D clossest = points.get(edge.points[0]).add(edge.unit.multiply(t));
            Point3D unit = ball.place.subtract(clossest);
            if (unit.magnitude() < ball.size) {
                unit = unit.normalize();
                ball.place = clossest.add(unit.multiply(ball.size));
                //ball.velocity = ball.velocity.subtract(unit.multiply(ball.velocity.dotProduct(unit) * 1.8));
                double angle=Math.asin(unit.multiply(-1).dotProduct(ball.velocity.normalize()));
                ball.normalTotal=ball.normalTotal.add(unit.multiply(angle));
                ball.normalCounter+=angle;
            }
        }
    }
    protected void pointCollision(int i, Ball ball){
        Point3D ballEndPoint = ball.place.subtract(points.get(i));
        if (ballEndPoint.magnitude() < ball.size) {
            Point3D unit = ballEndPoint.normalize();
            ball.place = points.get(i).add(unit.multiply(ball.size));
            //ball.velocity = ball.velocity.subtract(unit.multiply(ball.velocity.dotProduct(unit) * 1.8));
            double angle=Math.asin(unit.multiply(-1).dotProduct(ball.velocity.normalize()));
            ball.normalTotal=ball.normalTotal.add(unit.multiply(angle));
            ball.normalCounter+=angle;
        }
    }
    protected void ballWater(Water w, Ball ball,double subFrameInv){
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
                float f = (float) (volume/completeVolume)/2f;
                ball.acceleration=ball.acceleration.add(0,0,volume*gravity*subFrameInv*0.0001);
                if(f>ball.friction){
                    ball.friction=f;
                }
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

    public int getAmountSides(){return sides.size();}
    public Point3D getPoint(int i) {
        return points.get(i);
    }
    public Point3D getTriangle(int i,int j) {
        return points.get(sides.get(i).points[j]);
    }
    public Color3f getTriangleColor(int i) {
        return colors.get(sides.get(i).color);
    }
    public Point3D getTriangleNormal(int i,int j){
        if(hasPointNormals) {
            return sides.get(i).normals[j];
        }else{
            return sides.get(i).normal;
        }
    }
    public int getAmountSubObjects(){return subObjects.size();}
    public WorldObject getSubObject(int i){return subObjects.get(i);}
    public boolean containsNonObjectData(){
        if(pointsOriginal!=null && pointsOriginal.size()>0)
        {
            return true;
        }
        return false;
    }
    public int getAmountWaters(){return waters.size();}
    public Point3D[] getWaterPlace(int i){return waters.get(i).place;}
    public Color3f getWaterColor(int i){return colors.get(waters.get(i).color);}

    public int getID(){return ID;}

    protected void addSquare(Point3D p1, Point3D p2, Point3D p3, Point3D p4, int c, double f) {
        pointsOriginal.add(p1.add(0,0,0));
        pointsOriginal.add(p2.add(0,0,0));
        pointsOriginal.add(p3.add(0,0,0));
        pointsOriginal.add(p4.add(0,0,0));
        sides.add(new Side(pointsOriginal.size()-4, pointsOriginal.size()-3, pointsOriginal.size()-2, c,f));
        sides.add(new Side(pointsOriginal.size()-4, pointsOriginal.size()-1, pointsOriginal.size()-2, c,f));
        edges.add(new Edge(pointsOriginal.size()-4, pointsOriginal.size()-3));
        edges.add(new Edge(pointsOriginal.size()-3, pointsOriginal.size()-2));
        edges.add(new Edge(pointsOriginal.size()-2, pointsOriginal.size()-1));
        edges.add(new Edge(pointsOriginal.size()-1, pointsOriginal.size()-4));
    }
    protected void addTriangle(Point3D p1, Point3D p2, Point3D p3, int c, double f) {
        pointsOriginal.add(p1);
        pointsOriginal.add(p2);
        pointsOriginal.add(p3);
        sides.add(new Side(pointsOriginal.size()-3, pointsOriginal.size()-2, pointsOriginal.size()-1, c,f));
        edges.add(new Edge(pointsOriginal.size()-3, pointsOriginal.size()-2));
        edges.add(new Edge(pointsOriginal.size()-2, pointsOriginal.size()-1));
        edges.add(new Edge(pointsOriginal.size()-1, pointsOriginal.size()-3));
    }
}
