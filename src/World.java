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
    public int amountOfThreads=1;
    public int mode;

    class WorkThread extends Thread {
        int i;
        int jstart;
        int jstop;
        Point3D v;
        public boolean collisionWithWalls;
        public WorkThread(int ti,int tjstart,int tjstop,Point3D tv,int tmode) {
            i=ti;
            jstart=tjstart;
            jstop=tjstop;
            v=tv;
            collisionWithWalls=false;
            mode=tmode;
        }
        @Override
        public void run() {
            if (mode == 0){
                //ball-plane
                for (int j = jstart; j < jstop; j++) {
                    double Nr0 = sides.get(j).abc.dotProduct(balls.get(i).place);

                    double t = (sides.get(j).d - Nr0) / sides.get(j).Nv;

                    Point3D intersection = balls.get(i).place.add(sides.get(j).normal.multiply(t));

                    double distance = intersection.distance(balls.get(i).place);

                    if (distance < balls.get(i).size) {
                        if (PointInTriangle(intersection, sides.get(j).points[0], sides.get(j).points[1], sides.get(j).points[2])) {
                            if (t != 0) {
                                double dir = -(t / Math.abs(t));
                                balls.get(i).place = intersection.add(sides.get(j).normal.multiply(dir * balls.get(i).size));
                                //balls.get(i).acceleration=balls.get(i).acceleration.add(sides.get(j).normal.multiply(balls.get(i).velocity.dotProduct(sides.get(j).normal)*dir*2));
                                balls.get(i).velocity = v.subtract(sides.get(j).normal.multiply(v.dotProduct(sides.get(j).normal) * 1.8));
                                collisionWithWalls = true;
                            } else {
                                System.out.println("Physics engine stopped step, devided by 0");
                            }
                        }
                    }
                }
            }else if(mode ==1)
            {
                //ball-edge
                for(int j=jstart;j<jstop;j++)
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
            }else if(mode==2)
            {
                //ball-point
                for(int j=jstart;j<jstop;j++)
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
            }
        }
    }

    public void step(int subframes){
        double subframeInv=1.0/subframes;
        //move everything
        for(int l=0;l<subframes;l++) {
            for (int i = 0; i < balls.size(); i++) {
                balls.get(i).acceleration = balls.get(i).acceleration.add(0, 0, -1); //gravity

                balls.get(i).velocity = balls.get(i).velocity.add(balls.get(i).acceleration.multiply(subframeInv));
                balls.get(i).place = balls.get(i).place.add(balls.get(i).velocity.multiply(subframeInv));

                balls.get(i).acceleration = new Point3D(0, 0, 0);
            }

            //check collsion
            for (int i = 0; i < balls.size(); i++) {
                boolean collisionWithWalls = false;

                Point3D v = balls.get(i).velocity.multiply(1);

                for (int k = 0; k < 3; k++) {
                    WorkThread workThread[] = new WorkThread[amountOfThreads];
                    for (int j = 0; j < amountOfThreads; j++) {
                        workThread[j] = new WorkThread(i, (int) (sides.size() * j / amountOfThreads), (int) (sides.size() * (j + 1) / amountOfThreads), v, k);
                        workThread[j].start();
                    }
                    for (int j = 0; j < amountOfThreads; j++) {
                        try {
                            workThread[j].join();
                            if (workThread[j].collisionWithWalls == true) {
                                collisionWithWalls = true;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (collisionWithWalls) {
                    balls.get(i).velocity = balls.get(i).velocity.multiply(Math.pow(0.99,subframeInv));
                } else {
                    balls.get(i).velocity = balls.get(i).velocity.multiply(Math.pow(0.999,subframeInv));
                }
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
        if(hole.distance(balls.get(i).place)<(balls.get(i).size))
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
            double minHeight = z+(0.2*Math.pow(i,2)+2)*0.4;
            double maxHeight = z+(0.2*Math.pow(i+1,2)+2)*0.4;
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
    public void addCastle(double x,double y,double z,double parts,double towerSize,double towerHeight) {
        for(int j=0;j<2;j++) {
            double angleGrowSize = Math.PI / (parts / 2);
            for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
                Point3D p1 = new Point3D(j*towerHeight+x + Math.cos(angle) * towerSize, y + Math.sin(angle) * towerSize, z);
                Point3D p2 = new Point3D(j*towerHeight+x + Math.cos(angle) * towerSize, y + Math.sin(angle) * towerSize, z + towerHeight);
                Point3D p3 = new Point3D(j*towerHeight+x + Math.cos(angle + angleGrowSize) * towerSize, y + Math.sin(angle + angleGrowSize) * towerSize, z + towerHeight);
                Point3D p4 = new Point3D(j*towerHeight+x + Math.cos(angle + angleGrowSize) * towerSize, y + Math.sin(angle + angleGrowSize) * towerSize, z);
                addSquare(p1, p2, p3, p4, true, new Color3f(1f, 0f, 0.5f));
                Point3D top = new Point3D(j*towerHeight+x, y, z + towerHeight * 1.5);
                Point3D tp1 = new Point3D(j*towerHeight+x + Math.cos(angle) * towerSize * 1.2, y + Math.sin(angle) * towerSize * 1.2, z + towerHeight);
                Point3D tp2 = new Point3D(j*towerHeight+x + Math.cos(angle + angleGrowSize) * towerSize * 1.2, y + Math.sin(angle + angleGrowSize) * towerSize * 1.2, z + towerHeight);
                addTriangle(tp1, tp2, top, new Color3f(0.2f, 0.2f, 0.2f));
            }
        }
        Point3D b1 = new Point3D(x,y+20,z+towerHeight*0.5);
        Point3D b2 = new Point3D(x,y-20,z+towerHeight*0.5);
        Point3D b3 = new Point3D(x+towerHeight,y-20,z+towerHeight*0.5);
        Point3D b4 = new Point3D(x+towerHeight,y+20,z+towerHeight*0.5);
        addSquare(b1, b2, b3, b4, true, new Color3f(0.4f, 0.4f, 0.4f));
        Point3D t1 = new Point3D(x,y+20,z+towerHeight*0.7);
        Point3D t2 = new Point3D(x,y-20,z+towerHeight*0.7);
        Point3D t3 = new Point3D(x+towerHeight,y-20,z+towerHeight*0.7);
        Point3D t4 = new Point3D(x+towerHeight,y+20,z+towerHeight*0.7);
        addSquare(t1, t2, t3, t4, true, new Color3f(0.4f, 0.4f, 0.4f));
        addSquare(b1, t1, t4, b4, true, new Color3f(0.4f, 0.4f, 0.4f));
        addSquare(b2, t2, t3, b3, true, new Color3f(0.4f, 0.4f, 0.4f));
    }
    public void addBridge(double x,double y,double z,double length,double height,double borderHeight,double width,int parts) {
        for(int j=0;j<2;j++) {
            double angleGrowSize = Math.PI / (parts / 2);
            for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
                Point3D p1 = new Point3D(x + Math.cos(angle) * (width*0.25),j*length + y + Math.sin(angle) * (width*0.25), z);
                Point3D p2 = new Point3D(x + Math.cos(angle) * (width*0.25), j*length + y + Math.sin(angle) * (width*0.25), z + height);
                Point3D p3 = new Point3D(x + Math.cos(angle + angleGrowSize) * (width*0.25), j*length+y + Math.sin(angle + angleGrowSize) * (width*0.25), z + height);
                Point3D p4 = new Point3D(x + Math.cos(angle + angleGrowSize) * (width*0.25), j*length+y + Math.sin(angle + angleGrowSize) * (width*0.25), z);
                addSquare(p1, p2, p3, p4, true, new Color3f(0.2f, 0.2f, 0.2f));
            }
        }
        Point3D p1 = new Point3D(x-width*0.5,y-width*0.5,z+height);
        Point3D p2 = new Point3D(x+width*0.5,y-width*0.5,z+height);
        Point3D p3 = new Point3D(x+width*0.5,y+width*0.5+length,z+height);
        Point3D p4 = new Point3D(x-width*0.5,y+width*0.5+length,z+height);
        addSquare(p1, p2, p3, p4, true, new Color3f(0.8f, 0.8f, 0.8f));
        Point3D p1d = new Point3D(x-width*0.5,y-width*0.5-length/2,z);
        Point3D p2d = new Point3D(x+width*0.5,y-width*0.5-length/2,z);
        Point3D p3d = new Point3D(x+width*0.5,y+width*0.5+1.5*length,z);
        Point3D p4d = new Point3D(x-width*0.5,y+width*0.5+1.5*length,z);
        addSquare(p1, p2, p2d, p1d, true, new Color3f(0.8f, 0.8f, 0.8f));
        addSquare(p3, p4, p4d, p3d, true, new Color3f(0.8f, 0.8f, 0.8f));
        addSquare(p1, p4, p4.add(0,0,borderHeight), p1.add(0,0,borderHeight), true, new Color3f(0.5f, 0.5f, 0.5f));
        addSquare(p2, p3, p3.add(0,0,borderHeight), p2.add(0,0,borderHeight), true, new Color3f(0.5f, 0.5f, 0.5f));

        addSquare(p1, p1d, p1d.add(0,0,borderHeight), p1.add(0,0,borderHeight), true, new Color3f(0.5f, 0.5f, 0.5f));
        addSquare(p2, p2d, p2d.add(0,0,borderHeight), p2.add(0,0,borderHeight), true, new Color3f(0.5f, 0.5f, 0.5f));
        addSquare(p3, p3d, p3d.add(0,0,borderHeight), p3.add(0,0,borderHeight), true, new Color3f(0.5f, 0.5f, 0.5f));
        addSquare(p4, p4d, p4d.add(0,0,borderHeight), p4.add(0,0,borderHeight), true, new Color3f(0.5f, 0.5f, 0.5f));
        System.out.println(p1d.getY());
        System.out.println(p3d.getY());
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
    public void addTriangle(Point3D p1, Point3D p2, Point3D p3, Color3f c) {
        points.add(p1);
        points.add(p2);
        points.add(p3);
        sides.add(new Side(p1,p2,p3,c));
    }


}
