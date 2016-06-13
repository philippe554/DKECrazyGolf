package CrazyGolf.PhysicsEngine.Objects.Native;

import CrazyGolf.PhysicsEngine.Matrix;
import CrazyGolf.PhysicsEngine.Objects.Parts.Edge;
import CrazyGolf.PhysicsEngine.Objects.Parts.Side;
import CrazyGolf.PhysicsEngine.Objects.Parts.Water;
import CrazyGolf.PhysicsEngine.Physics3.WorldObject;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;
import java.util.LinkedList;

/**
 * Created by pmmde on 6/12/2016.
 */
public abstract class Factory extends WorldObject{
    protected LinkedList<Point3D> dynamicPoints;
    protected LinkedList<Color3f> dynamicColors;
    protected LinkedList<Side> dynamicSides;
    protected LinkedList<Edge> dynamicEdges;
    protected LinkedList<Water> dynamicWaters;

    public Factory(WorldData w) {
        super(w);

        dynamicPoints = new LinkedList<>();
        dynamicColors = new LinkedList<>();
        dynamicSides = new LinkedList<>();
        dynamicEdges = new LinkedList<>();
        dynamicWaters = new LinkedList<>();
    }
    protected void print(Point3D c, Matrix r){
        pointsOriginal = new Point3D[dynamicPoints.size()];
        pointsOriginal = dynamicPoints.toArray(pointsOriginal);
        points = new Point3D[pointsOriginal.length];

        colors = new Color3f[dynamicColors.size()];
        colors = dynamicColors.toArray(colors);

        sides = new Side[dynamicSides.size()];
        sides = dynamicSides.toArray(sides);

        edges = new Edge[dynamicEdges.size()];
        edges = dynamicEdges.toArray(edges);

        waters = new Water[dynamicWaters.size()];
        waters = dynamicWaters.toArray(waters);

        center=c;
        rotation=r;
    }
    protected void addSquare(Point3D p1, Point3D p2, Point3D p3, Point3D p4, int c, double f) {
        dynamicPoints.add(p1.add(0,0,0));
        dynamicPoints.add(p2.add(0,0,0));
        dynamicPoints.add(p3.add(0,0,0));
        dynamicPoints.add(p4.add(0,0,0));
        dynamicSides.add(new Side(dynamicPoints.size()-4, dynamicPoints.size()-3, dynamicPoints.size()-2, c,f));
        dynamicSides.add(new Side(dynamicPoints.size()-4, dynamicPoints.size()-1, dynamicPoints.size()-2, c,f));
        dynamicEdges.add(new Edge(dynamicPoints.size()-4, dynamicPoints.size()-3));
        dynamicEdges.add(new Edge(dynamicPoints.size()-3, dynamicPoints.size()-2));
        dynamicEdges.add(new Edge(dynamicPoints.size()-2, dynamicPoints.size()-1));
        dynamicEdges.add(new Edge(dynamicPoints.size()-1, dynamicPoints.size()-4));
    }
    protected void addTriangle(Point3D p1, Point3D p2, Point3D p3, int c, double f) {
        dynamicPoints.add(p1);
        dynamicPoints.add(p2);
        dynamicPoints.add(p3);
        dynamicSides.add(new Side(dynamicPoints.size()-3, dynamicPoints.size()-2, dynamicPoints.size()-1, c,f));
        dynamicEdges.add(new Edge(dynamicPoints.size()-3, dynamicPoints.size()-2));
        dynamicEdges.add(new Edge(dynamicPoints.size()-2, dynamicPoints.size()-1));
        dynamicEdges.add(new Edge(dynamicPoints.size()-1, dynamicPoints.size()-3));
    }
}
