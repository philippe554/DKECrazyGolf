import javafx.geometry.Point3D;

import javax.vecmath.Color3f;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by pmmde on 3/12/2016.
 */
public class World {
    public LinkedList<Point3D> points;
    public LinkedList<Side> sides;
    public LinkedList<Ball> balls;
    public Point3D hole;

    public void step(){
        //move everything
        for(int i=0;i<balls.size();i++)
        {
            balls.get(i).acceleration=balls.get(i).acceleration.add(0,0,-1); //gravity

            balls.get(i).velocity=balls.get(i).velocity.add(balls.get(i).acceleration);
            balls.get(i).place= balls.get(i).place.add(balls.get(i).velocity);

            balls.get(i).acceleration=new Point3D(0,0,0);
        }
        //check collsion
        for(int i=0;i<balls.size();i++)
        {
            boolean collisionWithWalls=false;
            //ball-plane
            Point3D v = balls.get(i).velocity.multiply(1);
            for(int j=0;j<sides.size();j++)
            {
                double Nr0 = sides.get(j).abc.dotProduct(balls.get(i).place);

                double t = (sides.get(j).d - Nr0) / sides.get(j).Nv;

                Point3D intersection = balls.get(i).place.add(sides.get(j).normal.multiply(t));

                double distance = intersection.distance(balls.get(i).place);

                if(distance<balls.get(i).size)
                {
                    if(PointInTriangle(intersection,sides.get(j).points[0],sides.get(j).points[1],sides.get(j).points[2]))
                    {
                        double dir = -(t/Math.abs(t));
                        balls.get(i).place=intersection.add(sides.get(j).normal.multiply(dir*balls.get(i).size));
                        //balls.get(i).acceleration=balls.get(i).acceleration.add(sides.get(j).normal.multiply(balls.get(i).velocity.dotProduct(sides.get(j).normal)*dir*2));
                        balls.get(i).velocity= v.subtract(sides.get(j).normal.multiply(v.dotProduct(sides.get(j).normal)*1.8));
                        collisionWithWalls=true;
                    }
                }
            }
            //ball-edge
            for(int j=0;j<sides.size();j++)
            {
                for(int k=0;k<sides.get(j).edges.length;k++)
                {
                    double t = sides.get(j).edges[k].unit.dotProduct(balls.get(i).place.subtract(sides.get(j).edges[k].points[0]));

                    if (t > 0 && t < sides.get(j).edges[k].lenght)
                    {
                        Point3D clossest = sides.get(j).edges[k].points[0].add(sides.get(j).edges[k].unit.multiply(t));
                        Point3D unit = balls.get(i).place.subtract(clossest);
                        double distance = unit.magnitude();
                        unit = unit.normalize();
                        if (distance < balls.get(i).size)
                        {
                            balls.get(i).place = clossest.add(unit.multiply(balls.get(i).size));
                            balls.get(i).velocity = v.subtract(unit.multiply(v.dotProduct(unit) * 1.8));
                            collisionWithWalls=true;
                        }
                    }
                }
            }
            //ball-point
            for(int j=0;j<points.size();j++)
            {
                Point3D ballEndPoint = balls.get(i).place.subtract(points.get(j));
                if(ballEndPoint.magnitude()<balls.get(i).size)
                {
                    Point3D unit=ballEndPoint.normalize();
                    balls.get(i).place=points.get(j).add(unit.multiply(balls.get(i).size));
                    balls.get(i).velocity= v.subtract(unit.multiply(v.dotProduct(unit)*1.8));
                    collisionWithWalls=true;
                }
            }
            if(collisionWithWalls)
            {
                balls.get(i).velocity=balls.get(i).velocity.multiply(0.99);
            }
            else
            {
                balls.get(i).velocity=balls.get(i).velocity.multiply(0.999);
            }
        }
    }

    private boolean PointInTriangle(Point3D p,Point3D a,Point3D b,Point3D c) {
        Point3D v0=c.subtract(a);
        Point3D v1=b.subtract(a);
        Point3D v2=p.subtract(a);

        double v0v0 = v0.dotProduct(v0);
        double v0v1 = v0.dotProduct(v1);
        double v0v2 = v0.dotProduct(v2);
        double v1v1 = v1.dotProduct(v1);
        double v1v2 = v1.dotProduct(v2);

        double u = (v1v1*v0v2-v0v1*v1v2)/(v0v0*v1v1-v0v1*v0v1);
        double v = (v0v0*v1v2-v0v1*v0v2)/(v0v0*v1v1-v0v1*v0v1);
        if(u>=0 && v>=0 && u<=1 && v<=1 && (u+v)<=1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void loadWorld(String file) {
        points = new LinkedList<>();
        sides = new LinkedList<>();
        balls = new LinkedList<>();

        LinkedList<String> field = new LinkedList<>();
        try {
            Scanner s = new Scanner( new FileReader(file));
            while(s.hasNextLine())
            {
                field.add(s.nextLine());
            }
        }
        catch (IOException e)
        { System.out.println("Error reading field plan from " + file);
            System.exit(0);
        }
        int sort=0;
        for(int i=0;i<field.size();i++)
        {
            if(field.get(i).equals("balls")) {sort=0;}
            else if(field.get(i).equals("triangels")) {sort=1;}
            else if(field.get(i).equals("hole")) {sort=2;}
            else
            {
                if(sort==0)
                {
                    String[] data = field.get(i).split(";");
                    if(data.length==3)
                    {
                        balls.add(new Ball(20,new Point3D(
                                Double.parseDouble(data[0]),
                                Double.parseDouble(data[1]),
                                Double.parseDouble(data[2]))));
                    }
                }else if(sort==1)
                {
                    String[] data = field.get(i).split(";");
                    if(data.length==12)
                    {
                        points.add(new Point3D(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2])));
                        points.add(new Point3D(Double.parseDouble(data[3]), Double.parseDouble(data[4]), Double.parseDouble(data[5])));
                        points.add(new Point3D(Double.parseDouble(data[6]), Double.parseDouble(data[7]), Double.parseDouble(data[8])));
                        Color3f c =new Color3f(Float.parseFloat(data[9]), Float.parseFloat(data[10]),Float.parseFloat(data[11]));
                        sides.add(new Side(points.get(points.size()-3),points.get(points.size()-2),points.get(points.size()-1),c));
                    }
                }
                else if(sort==2)
                {
                    String[] data = field.get(i).split(";");
                    if(data.length==3)
                    {
                        hole = new Point3D(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]));
                    }
                }
            }
        }
    }

    public void pushBall(int i,Point3D dir)
    {
        balls.get(i).velocity=dir;
    }

    public boolean checkBallInHole(int i) {
        if(hole.distance(balls.get(i).place)<balls.get(i).size)
        {
            return true;
        }
        return false;
    }
    public double getBallVelocity(int i)
    {
        return balls.get(i).velocity.magnitude();
    }

    public void addLoop(double x,double y,double z,double size,double width,int parts,double wallSize) {
        double angleGrowSize=Math.PI/(parts/2);
        double widthCounter=0;
        double widthIncrrement=width/parts;
        for(double angle = 0;angle<Math.PI*1.99;angle+=angleGrowSize)
        {
            Point3D p1=new Point3D(x+Math.sin(angle)*size,y-width/2+widthCounter,z-Math.cos(angle)*size);
            Point3D p2=new Point3D(x+Math.sin(angle+angleGrowSize)*size,y-width/2+widthCounter+widthIncrrement,z-Math.cos(angle+angleGrowSize)*size);
            Point3D p3=new Point3D(x+Math.sin(angle)*size,y+width/2+widthCounter,z-Math.cos(angle)*size);
            Point3D p4=new Point3D(x+Math.sin(angle+angleGrowSize)*size,y+width/2+widthCounter+widthIncrrement,z-Math.cos(angle+angleGrowSize)*size);
            addSquare(p1,p2,p4,p3,true,new Color3f(0.8f,0.8f,0.8f));
            Point3D p1in=new Point3D(x+Math.sin(angle)*(size-wallSize),y-width/2+widthCounter,z-Math.cos(angle)*(size-wallSize));
            Point3D p2in=new Point3D(x+Math.sin(angle+angleGrowSize)*(size-wallSize),y-width/2+widthCounter+widthIncrrement,z-Math.cos(angle+angleGrowSize)*(size-wallSize));
            Point3D p3in=new Point3D(x+Math.sin(angle)*(size-wallSize),y+width/2+widthCounter,z-Math.cos(angle)*(size-wallSize));
            Point3D p4in=new Point3D(x+Math.sin(angle+angleGrowSize)*(size-wallSize),y+width/2+widthCounter+widthIncrrement,z-Math.cos(angle+angleGrowSize)*(size-wallSize));
            addSquare(p1,p2,p2in,p1in,true,new Color3f(0.5f,0.5f,0.5f));
            addSquare(p3,p4,p4in,p3in,true,new Color3f(0.5f,0.5f,0.5f));
            widthCounter+=widthIncrrement;
        }
    }
    public void addHole(double x,double y, double z,double radius,double depth,int parts) {
        Point3D center = new Point3D(x,y,z-depth);
        Point3D cornerPoints[]=new Point3D[4];
        cornerPoints[0]=new Point3D(x+radius,y+radius,z);
        cornerPoints[1]=new Point3D(x-radius,y+radius,z);
        cornerPoints[2]=new Point3D(x-radius,y-radius,z);
        cornerPoints[3]=new Point3D(x+radius,y-radius,z);
        double angleGrowSize=Math.PI/(parts/2);
        for(int i=0;i<4;i++) {
            for (double angle = i*Math.PI/2; angle < (i+1)*Math.PI*1.99/4; angle += angleGrowSize) {
                Point3D p1 = new Point3D(x + Math.cos(angle) * radius, y + Math.sin(angle) * radius, z);
                Point3D p2 = new Point3D(x + Math.cos(angle + angleGrowSize) * radius, y + Math.sin(angle + angleGrowSize) * radius, z);
                Point3D p3 = new Point3D(x + Math.cos(angle) * radius, y + Math.sin(angle) * radius, z - depth);
                Point3D p4 = new Point3D(x + Math.cos(angle + angleGrowSize) * radius, y + Math.sin(angle + angleGrowSize) * radius, z - depth);
                addSquare(p1, p2, p4, p3, true, new Color3f(0.8f, 0.8f, 0.8f));
                sides.add(new Side(p3, p4, center, new Color3f(0.5f, 0.5f, 0.5f)));
                sides.add(new Side(p1, p2, cornerPoints[i], new Color3f(0, 1, 0)));
            }
        }
    }
    public void addWhirepool(double x,double y, double z, double radiusTop,double radiusBottom, double height,int parts) {
        double angleGrowSize=Math.PI/(parts/2.0);
        double verticalGrowSize=height/parts;
        double horizntalGrowSize=(radiusTop-radiusBottom)/parts;
        for(double i=0;i<parts;i++) {
            double minRadius = radiusBottom+i*horizntalGrowSize;
            double maxRadius = radiusBottom+(i+1)*horizntalGrowSize;
            double minHeight = z+(0.2*Math.pow(i,2)+2)*2;
            double maxHeight = z+(0.2*Math.pow(i+1,2)+2)*2;
            boolean color=true;
            for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
                Point3D p1 = new Point3D(x + Math.cos(angle) * minRadius, y + Math.sin(angle) * minRadius, minHeight);
                Point3D p2 = new Point3D(x + Math.cos(angle + angleGrowSize) * minRadius, y + Math.sin(angle + angleGrowSize) * minRadius, minHeight);
                Point3D p3 = new Point3D(x + Math.cos(angle) * maxRadius, y + Math.sin(angle) * maxRadius, maxHeight);
                Point3D p4 = new Point3D(x + Math.cos(angle + angleGrowSize) * maxRadius, y + Math.sin(angle + angleGrowSize) * maxRadius, maxHeight);
                if(color){
                    addSquare(p1, p2, p4, p3, true, new Color3f(0.8f, 0.8f, 0.8f));
                    color=false;
                }
                else
                {
                    addSquare(p1, p2, p4, p3, true, new Color3f(0.5f, 0.5f, 0.5f));
                    color=true;
                }
            }
        }
    }
    public void addSquare(Point3D p1,Point3D p2,Point3D p3,Point3D p4,boolean addToPoints,Color3f c) {
        if(addToPoints)
        {
            points.add(p1);
            points.add(p2);
            points.add(p3);
            points.add(p4);
        }
        sides.add(new Side(p1,p2,p3,c));
        sides.add(new Side(p1,p4,p3,c));
    }

    /*

        //balls.add(new Ball(20,new Point3D(0,0,20)));
        //balls.get(0).velocity=new Point3D(5,-2.5,0);
        //balls.get(0).velocity=new Point3D(0,10,0);

        points = new LinkedList<>();
        edges = new LinkedList<>();
        sides = new LinkedList<>();
        balls = new LinkedList<>();

        points.add(new Point3D(500,200,0));//0
        points.add(new Point3D(500,-500,0));//1
        points.add(new Point3D(-500,200,0));//2
        points.add(new Point3D(-500,-500,0));//3

        sides.add(new Side(points.get(0),points.get(1),points.get(2),new Color3f(0.0f,1.0f,0.0f)));
        sides.add(new Side(points.get(3),points.get(1),points.get(2),new Color3f(0.0f,1.0f,0.0f)));

        points.add(new Point3D(100,-100,0));//4
        points.add(new Point3D(300,100,0));//5
        points.add(new Point3D(100,-100,100));//6

        sides.add(new Side(points.get(4),points.get(5),points.get(6),new Color3f(0.5f,0.5f,0.5f)));

        //hole
        points.add(new Point3D(30,200,0));//7
        points.add(new Point3D(-30,200,0));//8
        points.add(new Point3D(30,260,0));//9
        points.add(new Point3D(-30,260,0));//10

        points.add(new Point3D(30,200,-40));//11
        points.add(new Point3D(-30,200,-40));//12
        points.add(new Point3D(30,260,-40));//13
        points.add(new Point3D(-30,260,-40));//14
        sides.add(new Side(points.get(7),points.get(8),points.get(12),new Color3f(0.5f,0.5f,0.5f)));
        sides.add(new Side(points.get(7),points.get(11),points.get(12),new Color3f(0.5f,0.5f,0.5f)));
        sides.add(new Side(points.get(11),points.get(12),points.get(14),new Color3f(0.7f,0.7f,0.7f)));
        sides.add(new Side(points.get(11),points.get(13),points.get(14),new Color3f(0.7f,0.7f,0.7f)));
        sides.add(new Side(points.get(7),points.get(9),points.get(13),new Color3f(0.5f,0.5f,0.5f)));
        sides.add(new Side(points.get(7),points.get(11),points.get(13),new Color3f(0.5f,0.5f,0.5f)));
        sides.add(new Side(points.get(8),points.get(10),points.get(14),new Color3f(0.5f,0.5f,0.5f)));
        sides.add(new Side(points.get(8),points.get(12),points.get(14),new Color3f(0.5f,0.5f,0.5f)));
        sides.add(new Side(points.get(9),points.get(10),points.get(14),new Color3f(0.5f,0.5f,0.5f)));
        sides.add(new Side(points.get(9),points.get(13),points.get(14),new Color3f(0.5f,0.5f,0.5f)));
        edges.add(new Edge(points.get(7),points.get(8)));
        edges.add(new Edge(points.get(8),points.get(10)));
        edges.add(new Edge(points.get(10),points.get(9)));
        edges.add(new Edge(points.get(9),points.get(10)));

        points.add(new Point3D(500,260,0));//15
        points.add(new Point3D(-500,260,0));//16
        sides.add(new Side(points.get(7),points.get(0),points.get(15),new Color3f(0.0f,1.0f,0.0f)));
        sides.add(new Side(points.get(7),points.get(9),points.get(15),new Color3f(0.0f,1.0f,0.0f)));
        sides.add(new Side(points.get(8),points.get(2),points.get(16),new Color3f(0.0f,1.0f,0.0f)));
        sides.add(new Side(points.get(8),points.get(10),points.get(16),new Color3f(0.0f,1.0f,0.0f)));

        points.add(new Point3D(500,500,0));//17
        points.add(new Point3D(-500,500,0));//18
        sides.add(new Side(points.get(15),points.get(16),points.get(18),new Color3f(0.0f,1.0f,0.0f)));
        sides.add(new Side(points.get(15),points.get(17),points.get(18),new Color3f(0.0f,1.0f,0.0f)));

        //slope
        points.add(new Point3D(50,-100,0));//19
        points.add(new Point3D(-50,-100,0));//20
        points.add(new Point3D(50,-200,20));//21
        points.add(new Point3D(-50,-200,20));//22
        points.add(new Point3D(50,-300,20));//23
        points.add(new Point3D(-50,-300,20));//24
        points.add(new Point3D(50,-400,0));//25
        points.add(new Point3D(-50,-400,0));//26
        sides.add(new Side(points.get(19),points.get(20),points.get(22),new Color3f(0.5f,0.5f,0.5f)));
        sides.add(new Side(points.get(19),points.get(21),points.get(22),new Color3f(0.5f,0.5f,0.5f)));
        sides.add(new Side(points.get(21),points.get(22),points.get(24),new Color3f(0.5f,0.5f,0.5f)));
        sides.add(new Side(points.get(21),points.get(23),points.get(24),new Color3f(0.5f,0.5f,0.5f)));
        sides.add(new Side(points.get(23),points.get(24),points.get(26),new Color3f(0.5f,0.5f,0.5f)));
        sides.add(new Side(points.get(23),points.get(25),points.get(26),new Color3f(0.5f,0.5f,0.5f)));

        points.add(new Point3D(50,-200,00));//27
        points.add(new Point3D(-50,-200,00));//28
        points.add(new Point3D(50,-300,00));//29
        points.add(new Point3D(-50,-300,00));//30
        sides.add(new Side(points.get(19),points.get(21),points.get(27),new Color3f(0.8f,0.8f,0.8f)));
        sides.add(new Side(points.get(25),points.get(23),points.get(29),new Color3f(0.8f,0.8f,0.8f)));
        sides.add(new Side(points.get(20),points.get(22),points.get(28),new Color3f(0.8f,0.8f,0.8f)));
        sides.add(new Side(points.get(26),points.get(24),points.get(30),new Color3f(0.8f,0.8f,0.8f)));

        sides.add(new Side(points.get(21),points.get(27),points.get(29),new Color3f(0.8f,0.8f,0.8f)));
        sides.add(new Side(points.get(21),points.get(23),points.get(29),new Color3f(0.8f,0.8f,0.8f)));
        sides.add(new Side(points.get(22),points.get(28),points.get(30),new Color3f(0.8f,0.8f,0.8f)));
        sides.add(new Side(points.get(22),points.get(24),points.get(30),new Color3f(0.8f,0.8f,0.8f)));*/

}
