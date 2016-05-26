package CrazyGolf.PhysicsEngine;

import CrazyGolf.Bot.Brutefinder.Brutefinder;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class WorldContainer implements World {
    protected ArrayList<Point3D> points;
    protected ArrayList<Color3f> colors;
    protected ArrayList<Side> sides;
    protected ArrayList<Edge> edges;
    protected ArrayList<Ball> balls;
    protected ArrayList<Water> waters;
    protected Ball start;
    protected Point3D hole;

    protected boolean editMode;

    public WorldContainer(LinkedList<String> input) {
        editMode=true;
        initContainer();
        loadWorldApi2(input);
        if(DEBUG)System.out.println("WorldContainer: World file loaded: "+sides.size()+" sides");
        editMode=false;
    }
    public WorldContainer() {
        editMode=true;
        initContainer();
    }
    private void initContainer() {
        points = new ArrayList<>();
        colors = new ArrayList<>();
        edges = new ArrayList<>();
        sides = new ArrayList<>();
        balls = new ArrayList<>();
        waters=new ArrayList<>();
        loadDefaultColors();
        hole = new Point3D(0,0,0);
    }
    @Override public void step(boolean useBallBallCollision) {

    }
    @Override public void stepSimulated(ArrayList<Ball> simBalls, boolean useBallBallCollision) {

    }
    @Override public void cleanUp() {
    }

    @Override public int getAmountBalls() {
        return balls.size();
    }
    @Override public double getBallSize(int i) {
        return balls.get(i).size;
    }
    @Override public void pushBall(int i, Point3D dir) {
        if(!editMode) {
            balls.get(i).velocity= balls.get(i).velocity.add(dir);
        }
    }
    @Override public boolean checkBallInHole(int i) {
        if(!editMode) {
            if (hole.distance(balls.get(i).place) < (balls.get(i).size)) {
                return true;
            }
        }
        return false;
    }
    @Override public Point3D getHolePosition() {
        return hole;
    }
    @Override public Point3D getStartPosition() {
        return start.place;
    }
    @Override public void addNewBall() {
        balls.add(new Ball(start.size,start.place.add(0,0,0)));
    }
    @Override public Point3D getBallVelocity(int i) {
        if(!editMode) {
            return balls.get(i).velocity;
        }
        return new Point3D(0,0,0);
    }
    @Override public void setBallPosition(int i, Point3D pos) {
        balls.get(i).place=pos;
    }
    @Override public void setBallVelocity(int i, Point3D vel) {
        balls.get(i).velocity=vel;
    }
    @Override public int getAmountTriangles() {
        return sides.size();
    }
    @Override public Point3D[] getTriangle(int i) {
        Point3D[] triangle = new Point3D[3];
        triangle[0]=points.get(sides.get(i).points[0]);
        triangle[1]=points.get(sides.get(i).points[1]);
        triangle[2]=points.get(sides.get(i).points[2]);
        return triangle;
    }
    @Override public Color3f getTriangleColor(int i) {
        return colors.get(sides.get(i).color);
    }
    @Override public int getAmountPoints() {
        return points.size();
    }
    @Override public Point3D getPoint(int i) {
        return points.get(i);
    }
    @Override public int getAmountOfWater() {
        return waters.size();
    }
    @Override public Point3D[] getWaterPoints(int i) {
        return waters.get(i).place;
    }
    @Override public Color3f getWaterColor(int i) {
        return colors.get(waters.get(i).color);
    }
    @Override public Point3D getBallPosition(int i) {
        return balls.get(i).place;
    }

    public void loadWorldApi2(LinkedList<String> field) {
        if(editMode) {
            int startPoint = points.size();
            int startColor = colors.size();
            int sort = 0;
            for (int i = 0; i < field.size(); i++) {
                if (field.get(i).equals("world")) {
                    sort = 0;
                } else if (field.get(i).equals("balls")) {
                    sort = 1;
                } else if (field.get(i).equals("holes")) {
                    sort = 2;
                } else if (field.get(i).equals("points")) {
                    sort = 3;
                } else if (field.get(i).equals("edges")) {
                    sort = 4;
                } else if (field.get(i).equals("triangels")) {
                    sort = 5;
                } else if (field.get(i).equals("colors")) {
                    sort = 6;
                } else if (field.get(i).equals("water")) {
                    sort = 7;
                }
                else {
                    if (sort == 0) {
                    } else if (sort == 1) {
                        String[] data = field.get(i).split(";");
                        if (data.length == 5) {
                            start = new Ball(20, new Point3D(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2])));
                            System.out.println("added");
                        }
                    } else if (sort == 2) {
                        String[] data = field.get(i).split(";");
                        if (data.length == 3) {
                            hole = new Point3D(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]));
                        }
                    } else if (sort == 3) {
                        String[] data = field.get(i).split(";");
                        if (data.length == 3) {
                            points.add(new Point3D(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2])));
                        }
                    } else if (sort == 4) {
                        String[] data = field.get(i).split(";");
                        if (data.length == 2) {
                            edges.add(new Edge(this, Integer.parseInt(data[0]) + startPoint, Integer.parseInt(data[1]) + startPoint));
                        }
                    } else if (sort == 5) {
                        String[] data = field.get(i).split(";");
                        if (data.length == 5) {
                            sides.add(new Side(this, Integer.parseInt(data[0]) + startPoint, Integer.parseInt(data[1]) + startPoint, Integer.parseInt(data[2]) + startPoint,
                                    Integer.parseInt(data[3]) + startColor, Double.parseDouble(data[4])));
                        }
                    } else if (sort == 6) {
                        String[] data = field.get(i).split(";");
                        if (data.length == 4) {
                            colors.add(new Color3f(Float.parseFloat(data[0]), Float.parseFloat(data[1]), Float.parseFloat(data[2])));
                        }
                    } else if (sort == 7) {
                        String[] data = field.get(i).split(";");
                        if (data.length == 7) {
                            waters.add(new Water(new Point3D[]{new Point3D(Double.parseDouble(data[0]),Double.parseDouble(data[1]),Double.parseDouble(data[2])),
                                    new Point3D(Double.parseDouble(data[3]),Double.parseDouble(data[4]),Double.parseDouble(data[5]))},Integer.parseInt(data[6])));
                        }
                    }
                }
            }
        }
    }
    public void loadWorld(String[][]data, double gs,double Z){
        if(editMode) {
            int startPoint = points.size();
            int startColor = colors.size();
            boolean[][]alreadyConverted=new boolean[data.length][data[0].length];
            for(int i=0;i<alreadyConverted.length;i++)
            {
                for(int j=0;j<alreadyConverted[i].length;j++)
                {
                    alreadyConverted[i][j]=false;
                }
            }
            for(int i=0;i<data.length;i++) {
                for (int j = 0; j < data[i].length; j++) {
                    if (!alreadyConverted[i][j]) {
                        if (data[i][j].equals("B")) {
                            start = new Ball(20, new Point3D(i * gs + gs, j * gs + gs, Z + 20));
                            for (int k = 0; k < 2; k++) {
                                for (int l = 0; l < 2; l++) {
                                    if ((i + k) < data.length && (j + l) < data[i + k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for(int i=0;i<alreadyConverted.length;i++)
            {
                for(int j=0;j<alreadyConverted[i].length;j++)
                {
                    alreadyConverted[i][j]=false;
                }
            }
            for(int i=0;i<data.length;i++) {
                for (int j = 0; j < data[i].length; j++) {
                    if(!alreadyConverted[i][j]) {
                        if (data[i][j].equals("W")) {
                            addWall(data,alreadyConverted,i,j,gs,Z);
                        } else if (data[i][j].equals("F") || data[i][j].equals("B")) {
                            addGrass(data,alreadyConverted,i,j,gs,Z);
                        } else if (data[i][j].equals("S")) {
                            addSand(data,alreadyConverted,i,j,gs,Z);
                        } else if (data[i][j].equals("H")) {
                            addHole(i*gs+1.5*gs,j*gs+1.5*gs,Z,30,80,30);
                            for(int k=0;k<3;k++)
                            {
                                for(int l=0;l<3;l++)
                                {
                                    if(i+k<data.length && j+l<data[i+k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        }else if(data[i][j].equals("L"))
                        {
                            addSquare(new Point3D(i*gs,j*gs,Z),
                                    new Point3D(i*gs+gs*14,j*gs,Z),
                                    new Point3D(i*gs+gs*14,j*gs+gs*6,Z),
                                    new Point3D(i*gs,j*gs+gs*6,Z),
                                    2,1);
                            addLoop(i*gs+140,j*gs+30,Z+140,140,60,24,25);
                            for(int k=0;k<14;k++)
                            {
                                for(int l=0;l<6;l++)
                                {
                                    if((i+k)<data.length && (j+l)<data[i+k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        }else if(data[i][j].equals("C"))
                        {
                            addSquare(new Point3D(i*gs,j*gs,Z),
                                    new Point3D(i*gs+gs*13,j*gs,Z),
                                    new Point3D(i*gs+gs*13,j*gs+gs*4,Z),
                                    new Point3D(i*gs,j*gs+gs*4,Z),
                                    2,1);
                            addCastle(i*gs+40,j*gs+40,Z,20,40,180);
                            for(int k=0;k<13;k++)
                            {
                                for(int l=0;l<4;l++)
                                {
                                    if((i+k)<data.length && (j+l)<data[i+k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        } else if(data[i][j].equals("R"))
                        {
                            addSquare(new Point3D(i*gs,j*gs,Z),
                                    new Point3D(i*gs+gs*4,j*gs,Z),
                                    new Point3D(i*gs+gs*4,j*gs+gs*24,Z),
                                    new Point3D(i*gs,j*gs+gs*24,Z),
                                    2,1);
                            addBridge(i*gs+40,j*gs+140,Z,200,50,20,80,20);
                            for(int k=0;k<4;k++)
                            {
                                for(int l=0;l<24;l++)
                                {
                                    if((i+k)<data.length && (j+l)<data[i+k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        }else if(data[i][j].equals("P"))
                        {
                            addPool(i*gs,j*gs,Z,280,150,12,25);
                            for(int k=0;k<14;k++)
                            {
                                for(int l=0;l<14;l++)
                                {
                                    if((i+k)<data.length && (j+l)<data[i+k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        }else if(data[i][j].equals("M")){
                            addHill(data,i,j,gs,Z,"FBS");
                            for(int k=0;k<4;k++)
                            {
                                for(int l=0;l<4;l++)
                                {
                                    if((i+k)<data.length && (j+l)<data[i+k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public LinkedList<String> outputWorldApi2(){
        LinkedList<String> output = new LinkedList<>();
        output.add("balls");
        output.add(start.place.getX()+";"+start.place.getY()+";"+start.place.getZ()+";"+start.size+";"+start.mass);
        output.add("holes");
        output.add(hole.getX() + ";" + hole.getY() + ";" + hole.getZ());
        output.add("points");
        for(int i=0;i<points.size();i++)
        {
            output.add(points.get(i).getX()+";"+points.get(i).getY()+";"+points.get(i).getZ());
        }
        output.add("colors");
        for(int i=0;i<colors.size();i++)
        {
            output.add(colors.get(i).getX()+";"+colors.get(i).getY()+";"+colors.get(i).getZ()+";1");
        }
        output.add("edges");
        for(int i=0;i<edges.size();i++)
        {
            output.add(edges.get(i).points[0]+";"+edges.get(i).points[1]);
        }
        output.add("triangels");
        for(int i=0;i<sides.size();i++)
        {
            output.add(sides.get(i).points[0]+";"+sides.get(i).points[1]+";"+sides.get(i).points[2]+";"+sides.get(i).color+";"+sides.get(i).friction);
        }
        output.add("water");
        for(int i=0;i<waters.size();i++)
        {
            output.add(waters.get(i).place[0].getX()+";"+waters.get(i).place[0].getY()+";"+waters.get(i).place[0].getZ()
                    +";"+waters.get(i).place[1].getX()+";"+waters.get(i).place[1].getY()+";"+waters.get(i).place[1].getZ()
                    +";"+waters.get(i).color);
        }
        return output;
    }
    private void loadDefaultColors() {
        colors.add(new Color3f(0.8f, 0.8f, 0.8f));//0
        colors.add(new Color3f(0.5f, 0.5f, 0.5f));//1
        colors.add(new Color3f(0.0f, 1.0f, 0.0f));//2
        colors.add(new Color3f(1.0f, 0.0f, 0.5f));//3
        colors.add(new Color3f(0.2f, 0.2f, 0.2f));//4
        colors.add(new Color3f(0.4f, 0.4f, 0.4f));//5
        colors.add(new Color3f(0.0f, 0.8f, 1.0f));//6
        colors.add(new Color3f(1.0f, 0.5f, 0.0f));//7
    }

    public void addSquare(Point3D p1, Point3D p2, Point3D p3, Point3D p4, int c, double f) {
        points.add(p1.add(0,0,0));
        points.add(p2.add(0,0,0));
        points.add(p3.add(0,0,0));
        points.add(p4.add(0,0,0));
        sides.add(new Side(this,points.size()-4, points.size()-3, points.size()-2, c,f));
        sides.add(new Side(this,points.size()-4, points.size()-1, points.size()-2, c,f));
        edges.add(new Edge(this,points.size()-4, points.size()-3));
        edges.add(new Edge(this,points.size()-3, points.size()-2));
        edges.add(new Edge(this,points.size()-2, points.size()-1));
        edges.add(new Edge(this,points.size()-1, points.size()-4));
    }
    public void addTriangle(Point3D p1, Point3D p2, Point3D p3, int c, double f) {
        points.add(p1);
        points.add(p2);
        points.add(p3);
        sides.add(new Side(this,points.size()-3, points.size()-2, points.size()-1, c,f));
        edges.add(new Edge(this,points.size()-3, points.size()-2));
        edges.add(new Edge(this,points.size()-2, points.size()-1));
        edges.add(new Edge(this,points.size()-1, points.size()-3));
    }

    private void addGrass(String[][]data,boolean[][]alreadyConverted,int i,int j,double gs,double Z) {
        int iCounter=0;
        int jCounter=0;
        boolean keepCountingI=true;
        boolean keepCountingJ=true;
        while(keepCountingI||keepCountingJ)
        {
            if(keepCountingI) {
                if ((iCounter + i) < data.length && expand(data,i, j, iCounter + 1, jCounter, alreadyConverted,"FB")) {
                    iCounter++;
                }else{
                    keepCountingI=false;
                }
            }
            if(keepCountingJ) {
                if ((jCounter + j) < data[0].length && expand(data,i, j, iCounter , jCounter+1, alreadyConverted,"FB")) {
                    jCounter++;
                }else{
                    keepCountingJ=false;
                }
            }
        }
        addSquare(new Point3D(i*gs,j*gs,Z),
                new Point3D(i*gs+iCounter*gs,j*gs,Z),
                new Point3D(i*gs+iCounter*gs,j*gs+jCounter*gs,Z),
                new Point3D(i*gs,j*gs+jCounter*gs,Z),
                2,0.1);
        for(int k=i;k<(i+iCounter);k++) {
            for(int l=j;l<(j+jCounter);l++) {
                alreadyConverted[k][l] = true;
            }
        }
    }
    private void addWall(String[][]data,boolean[][]alreadyConverted,int i,int j,double gs,double Z) {
        int iCounter=0;
        int jCounter=0;
        boolean keepCountingI=true;
        boolean keepCountingJ=true;
        while(keepCountingI||keepCountingJ)
        {
            if(keepCountingI) {
                if ((iCounter + i) < data.length && expand(data,i, j, iCounter + 1, jCounter, alreadyConverted,"W")) {
                    iCounter++;
                }else{
                    keepCountingI=false;
                }
            }
            if(keepCountingJ) {
                if ((jCounter + j) < data[0].length && expand(data,i, j, iCounter , jCounter+1, alreadyConverted,"W")) {
                    jCounter++;
                }else{
                    keepCountingJ=false;
                }
            }
        }

        double borderHeight=50;

        addSquare(new Point3D(i*gs,j*gs,borderHeight+Z),
                new Point3D(i*gs+gs*iCounter,j*gs,borderHeight+Z),
                new Point3D(i*gs+gs*iCounter,j*gs+gs*jCounter,borderHeight+Z),
                new Point3D(i*gs,j*gs+gs*jCounter,borderHeight+Z),
                0,0.1);
        addSquare(new Point3D(i*gs,j*gs,borderHeight+Z),
                new Point3D(i*gs,j*gs+gs*jCounter,borderHeight+Z),
                new Point3D(i*gs,j*gs+gs*jCounter,0+Z),
                new Point3D(i*gs,j*gs,0+Z),
                1,0.1);
        addSquare(new Point3D(i*gs,j*gs,borderHeight+Z),
                new Point3D(i*gs+gs*iCounter,j*gs,borderHeight+Z),
                new Point3D(i*gs+gs*iCounter,j*gs,0+Z),
                new Point3D(i*gs,j*gs,0+Z),
                1,0.1);
        addSquare(new Point3D(i*gs+gs*iCounter,j*gs,borderHeight+Z),
                new Point3D(i*gs+gs*iCounter,j*gs+gs*jCounter,borderHeight+Z),
                new Point3D(i*gs+gs*iCounter,j*gs+gs*jCounter,0+Z),
                new Point3D(i*gs+gs*iCounter,j*gs,0+Z),
                1,0.1);
        addSquare(new Point3D(i*gs,j*gs+gs*jCounter,borderHeight+Z),
                new Point3D(i*gs+gs*iCounter,j*gs+gs*jCounter,borderHeight+Z),
                new Point3D(i*gs+gs*iCounter,j*gs+gs*jCounter,0+Z),
                new Point3D(i*gs,j*gs+gs*jCounter,0+Z),
                1,0.1);
        for(int k=i;k<(i+iCounter);k++) {
            for(int l=j;l<(j+jCounter);l++) {
                alreadyConverted[k][l] = true;
            }
        }
    }
    private void addSand(String[][]data,boolean[][]alreadyConverted,int i,int j,double gs,double Z) {
        int iCounter=0;
        int jCounter=0;
        boolean keepCountingI=true;
        boolean keepCountingJ=true;
        while(keepCountingI||keepCountingJ)
        {
            if(keepCountingI) {
                if ((iCounter + i) < data.length && expand(data,i, j, iCounter + 1, jCounter, alreadyConverted,"S")) {
                    iCounter++;
                }else{
                    keepCountingI=false;
                }
            }
            if(keepCountingJ) {
                if ((jCounter + j) < data[0].length && expand(data,i, j, iCounter , jCounter+1, alreadyConverted,"S")) {
                    jCounter++;
                }else{
                    keepCountingJ=false;
                }
            }
        }

        double borderHeight=60;

        addSquare(new Point3D(i*gs,j*gs,Z),
                new Point3D(i*gs+gs*iCounter,j*gs,Z),
                new Point3D(i*gs+gs*iCounter,j*gs+gs*jCounter,Z),
                new Point3D(i*gs,j*gs+gs*jCounter,Z),
                7,0.5);
        for(int k=i;k<(i+iCounter);k++) {
            for(int l=j;l<(j+jCounter);l++) {
                alreadyConverted[k][l] = true;
            }
        }
    }
    private boolean expand(String[][]data,int iStart,int jStart,int iSize,int jSize,boolean[][]alreadyConverted,String ignoreData) {
        boolean possible=true;
        for(int i=iStart;i<(iStart+iSize);i++)
        {
            for(int j=jStart;j<(jStart+jSize);j++)
            {
                if(alreadyConverted[i][j]==true || !ignoreData.contains(data[i][j]))
                {
                    possible=false;
                }
            }
        }
        return possible;
    }

    public void addLoop(double x, double y, double z, double size, double width, int parts, double wallSize) {
        double angleGrowSize = Math.PI / (parts / 2);
        double widthCounter = 0;
        double widthIncrrement = width / parts;
        for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
            Point3D p1 = new Point3D(x + Math.sin(angle) * size, y - width / 2 + widthCounter, z - Math.cos(angle) * size);
            Point3D p2 = new Point3D(x + Math.sin(angle + angleGrowSize) * size, y - width / 2 + widthCounter + widthIncrrement, z - Math.cos(angle + angleGrowSize) * size);
            Point3D p3 = new Point3D(x + Math.sin(angle) * size, y + width / 2 + widthCounter, z - Math.cos(angle) * size);
            Point3D p4 = new Point3D(x + Math.sin(angle + angleGrowSize) * size, y + width / 2 + widthCounter + widthIncrrement, z - Math.cos(angle + angleGrowSize) * size);
            addSquare(p1, p2, p4, p3, 0,0.1);
            Point3D p1in = new Point3D(x + Math.sin(angle) * (size - wallSize), y - width / 2 + widthCounter, z - Math.cos(angle) * (size - wallSize));
            Point3D p2in = new Point3D(x + Math.sin(angle + angleGrowSize) * (size - wallSize), y - width / 2 + widthCounter + widthIncrrement, z - Math.cos(angle + angleGrowSize) * (size - wallSize));
            Point3D p3in = new Point3D(x + Math.sin(angle) * (size - wallSize), y + width / 2 + widthCounter, z - Math.cos(angle) * (size - wallSize));
            Point3D p4in = new Point3D(x + Math.sin(angle + angleGrowSize) * (size - wallSize), y + width / 2 + widthCounter + widthIncrrement, z - Math.cos(angle + angleGrowSize) * (size - wallSize));
            addSquare(p1, p2, p2in, p1in, 1,0.1);
            addSquare(p3, p4, p4in, p3in, 1,0.1);
            widthCounter += widthIncrrement;
        }
    }
    public void addHole(double x, double y, double z, double radius, double depth, int parts) {
        hole=new Point3D(x,y,z-depth+20);
        Point3D center = new Point3D(x, y, z - depth);
        Point3D cornerPoints[] = new Point3D[4];
        cornerPoints[0] = new Point3D(x + radius, y + radius, z);
        cornerPoints[1] = new Point3D(x - radius, y + radius, z);
        cornerPoints[2] = new Point3D(x - radius, y - radius, z);
        cornerPoints[3] = new Point3D(x + radius, y - radius, z);
        double angleGrowSize = Math.PI / (parts / 2);
        for (int i = 0; i < 4; i++) {
            for (double angle = i * Math.PI / 2; angle < (i + 1) * Math.PI * 1.99 / 4; angle += angleGrowSize) {
                Point3D p1 = new Point3D(x + Math.cos(angle) * radius, y + Math.sin(angle) * radius, z);
                Point3D p2 = new Point3D(x + Math.cos(angle + angleGrowSize) * radius, y + Math.sin(angle + angleGrowSize) * radius, z);
                Point3D p3 = new Point3D(x + Math.cos(angle) * radius, y + Math.sin(angle) * radius, z - depth);
                Point3D p4 = new Point3D(x + Math.cos(angle + angleGrowSize) * radius, y + Math.sin(angle + angleGrowSize) * radius, z - depth);
                addSquare(p1, p2, p4, p3, 0,0.1);
                addTriangle(p3,p4,center,1,0.1);
                addTriangle(p1, p2, cornerPoints[i], 2,0.1);
            }
        }
    }
    public void addWhirepool(double x, double y, double z, double radiusTop, double radiusBottom, double height, int parts) {
        double angleGrowSize = Math.PI / (parts / 2.0);
        double verticalGrowSize = height / parts;
        double horizntalGrowSize = (radiusTop - radiusBottom) / parts;
        for (double i = 0; i < parts; i++) {
            double minRadius = radiusBottom + i * horizntalGrowSize;
            double maxRadius = radiusBottom + (i + 1) * horizntalGrowSize;
            double minHeight = z + (0.2 * Math.pow(i, 2) + 2) * 0.4;
            double maxHeight = z + (0.2 * Math.pow(i + 1, 2) + 2) * 0.4;
            boolean color = true;
            for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
                Point3D p1 = new Point3D(x + Math.cos(angle) * minRadius, y + Math.sin(angle) * minRadius, minHeight);
                Point3D p2 = new Point3D(x + Math.cos(angle + angleGrowSize) * minRadius, y + Math.sin(angle + angleGrowSize) * minRadius, minHeight);
                Point3D p3 = new Point3D(x + Math.cos(angle) * maxRadius, y + Math.sin(angle) * maxRadius, maxHeight);
                Point3D p4 = new Point3D(x + Math.cos(angle + angleGrowSize) * maxRadius, y + Math.sin(angle + angleGrowSize) * maxRadius, maxHeight);
                if (color) {
                    addSquare(p1, p2, p4, p3,0,0.1);
                    color = false;
                } else {
                    addSquare(p1, p2, p4, p3, 1,0.1);
                    color = true;
                }
            }
        }
    }
    public void addCastle(double x, double y, double z, double parts, double towerSize, double towerHeight) {
        for (int j = 0; j < 2; j++) {
            double angleGrowSize = Math.PI / (parts / 2);
            for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
                Point3D p1 = new Point3D(j * towerHeight + x + Math.cos(angle) * towerSize, y + Math.sin(angle) * towerSize, z);
                Point3D p2 = new Point3D(j * towerHeight + x + Math.cos(angle) * towerSize, y + Math.sin(angle) * towerSize, z + towerHeight);
                Point3D p3 = new Point3D(j * towerHeight + x + Math.cos(angle + angleGrowSize) * towerSize, y + Math.sin(angle + angleGrowSize) * towerSize, z + towerHeight);
                Point3D p4 = new Point3D(j * towerHeight + x + Math.cos(angle + angleGrowSize) * towerSize, y + Math.sin(angle + angleGrowSize) * towerSize, z);
                addSquare(p1, p2, p3, p4, 3,0.1);
                Point3D top = new Point3D(j * towerHeight + x, y, z + towerHeight * 1.5);
                Point3D tp1 = new Point3D(j * towerHeight + x + Math.cos(angle) * towerSize * 1.2, y + Math.sin(angle) * towerSize * 1.2, z + towerHeight);
                Point3D tp2 = new Point3D(j * towerHeight + x + Math.cos(angle + angleGrowSize) * towerSize * 1.2, y + Math.sin(angle + angleGrowSize) * towerSize * 1.2, z + towerHeight);
                addTriangle(tp1, tp2, top, 4,0.1);
            }
        }
        Point3D b1 = new Point3D(x, y + 20, z + towerHeight * 0.5);
        Point3D b2 = new Point3D(x, y - 20, z + towerHeight * 0.5);
        Point3D b3 = new Point3D(x + towerHeight, y - 20, z + towerHeight * 0.5);
        Point3D b4 = new Point3D(x + towerHeight, y + 20, z + towerHeight * 0.5);
        addSquare(b1, b2, b3, b4, 5,0.1 );//?? color ??
        Point3D t1 = new Point3D(x, y + 20, z + towerHeight * 0.7);
        Point3D t2 = new Point3D(x, y - 20, z + towerHeight * 0.7);
        Point3D t3 = new Point3D(x + towerHeight, y - 20, z + towerHeight * 0.7);
        Point3D t4 = new Point3D(x + towerHeight, y + 20, z + towerHeight * 0.7);
        addSquare(t1, t2, t3, t4, 5,0.1);
        addSquare(b1, t1, t4, b4, 5,0.1);
        addSquare(b2, t2, t3, b3, 5,0.1);
    }
    public void addBridge(double x, double y, double z, double length, double height, double borderHeight, double width, int parts) {
        for (int j = 0; j < 2; j++) {
            double angleGrowSize = Math.PI / (parts / 2);
            for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
                Point3D p1 = new Point3D(x + Math.cos(angle) * (width * 0.25), j * length + y + Math.sin(angle) * (width * 0.25), z);
                Point3D p2 = new Point3D(x + Math.cos(angle) * (width * 0.25), j * length + y + Math.sin(angle) * (width * 0.25), z + height);
                Point3D p3 = new Point3D(x + Math.cos(angle + angleGrowSize) * (width * 0.25), j * length + y + Math.sin(angle + angleGrowSize) * (width * 0.25), z + height);
                Point3D p4 = new Point3D(x + Math.cos(angle + angleGrowSize) * (width * 0.25), j * length + y + Math.sin(angle + angleGrowSize) * (width * 0.25), z);
                addSquare(p1, p2, p3, p4, 4,0.1);
            }
        }
        Point3D p1 = new Point3D(x - width * 0.5, y - width * 0.5, z + height);
        Point3D p2 = new Point3D(x + width * 0.5, y - width * 0.5, z + height);
        Point3D p3 = new Point3D(x + width * 0.5, y + width * 0.5 + length, z + height);
        Point3D p4 = new Point3D(x - width * 0.5, y + width * 0.5 + length, z + height);
        addSquare(p1, p2, p3, p4, 0,0.1);
        Point3D p1d = new Point3D(x - width * 0.5, y - width * 0.5 - length / 2, z);
        Point3D p2d = new Point3D(x + width * 0.5, y - width * 0.5 - length / 2, z);
        Point3D p3d = new Point3D(x + width * 0.5, y + width * 0.5 + 1.5 * length, z);
        Point3D p4d = new Point3D(x - width * 0.5, y + width * 0.5 + 1.5 * length, z);
        addSquare(p1, p2, p2d, p1d, 0,0.1);
        addSquare(p3, p4, p4d, p3d, 0,0.1);
        addSquare(p1, p4, p4.add(0, 0, borderHeight), p1.add(0, 0, borderHeight), 1,0.1);
        addSquare(p2, p3, p3.add(0, 0, borderHeight), p2.add(0, 0, borderHeight), 1,0.1);

        addSquare(p1, p1d, p1d.add(0, 0, borderHeight), p1.add(0, 0, borderHeight), 1,0.1);
        addSquare(p2, p2d, p2d.add(0, 0, borderHeight), p2.add(0, 0, borderHeight), 1,0.1);
        addSquare(p3, p3d, p3d.add(0, 0, borderHeight), p3.add(0, 0, borderHeight), 1,0.1);
        addSquare(p4, p4d, p4d.add(0, 0, borderHeight), p4.add(0, 0, borderHeight), 1,0.1);
    }
    public void addPool(double x,double y,double z,double size,double debt,int parts,double waterDebt){
        waters.add(new Water(new Point3D[]{new Point3D(x,y,z-debt),new Point3D(x+size,y+size,z-waterDebt)},6));
        double xStep=size/parts;
        double yStep=size/parts;
        for(double i=0;i<parts;i++)
        {
            for(double j=0;j<parts;j++){
                Point3D p1=new Point3D(x+i*xStep,y+j*yStep,z+((Math.max((Math.cos(i/parts*Math.PI*2)-1),(Math.cos(j/parts*Math.PI*2)-1)))/4)*debt);
                Point3D p2=new Point3D(x+(i+1)*xStep,y+j*yStep,z+((Math.max((Math.cos((i+1.0)/parts*Math.PI*2)-1),(Math.cos(j/parts*Math.PI*2)-1))))/4*debt);
                Point3D p3=new Point3D(x+(i+1)*xStep,y+(j+1)*yStep,z+((Math.max((Math.cos((i+1.0)/parts*Math.PI*2)-1),(Math.cos((j+1.0)/parts*Math.PI*2)-1))))/4*debt);
                Point3D p4=new Point3D(x+i*xStep,y+(j+1)*yStep,z+((Math.max((Math.cos(i/parts*Math.PI*2)-1),(Math.cos((j+1.0)/parts*Math.PI*2)-1))))/4*debt);
                addSquare(p1,p2,p3,p4,2,0.1);
            }
        }
    }

    public void addHill(String[][]data,int i,int j,double gs,double Z,String link){
        if(i>0 && link.contains(data[i-1][j])){
            addHillX(i*gs,j*gs,Z,4*gs,4*gs,50,8);
        }else if(j>0 && link.contains(data[i][j-1])){
            addHillY(i*gs,j*gs,Z,4*gs,4*gs,50,8);
        }else if(i<(data.length-4) && link.contains(data[i+4][j])){
            addHillx(i*gs,j*gs,Z,4*gs,4*gs,50,8);
        }else{
            addHilly(i*gs,j*gs,Z,4*gs,4*gs,50,8);
        }
    }
    public void addHillX(double x,double y,double z,double xSize,double ySize,double zSize,int parts){
        double xStep = xSize/parts;
        double yStep = ySize/parts;
        double zStep = zSize/parts;
        for(double i=0;i<(parts);i++)
        {
            for(double j=0;j<(parts);j++)
            {
                Point3D p1= new Point3D(x+i*xStep,y+j*yStep,z+(-Math.cos(i/parts*Math.PI)+1)*zSize/2);
                Point3D p2= new Point3D(x+(i+1)*xStep,y+j*yStep,z+(-Math.cos((i+1)/parts*Math.PI)+1)*zSize/2);
                Point3D p3= new Point3D(x+(i+1)*xStep,y+(j+1)*yStep,z+(-Math.cos((i+1)/parts*Math.PI)+1)*zSize/2);
                Point3D p4= new Point3D(x+i*xStep,y+(j+1)*yStep,z+(-Math.cos(i/parts*Math.PI)+1)*zSize/2);
                addSquare(p1,p2,p3,p4,2,0.1);
            }
        }
    }
    public void addHillx(double x,double y,double z,double xSize,double ySize,double zSize,int parts){
        double xStep = xSize/parts;
        double yStep = ySize/parts;
        double zStep = zSize/parts;
        for(double i=0;i<(parts);i++)
        {
            for(double j=0;j<(parts);j++)
            {
                Point3D p1= new Point3D(x+i*xStep,y+j*yStep,z+(-Math.cos(Math.PI-i/parts*Math.PI)+1)*zSize/2);
                Point3D p2= new Point3D(x+(i+1)*xStep,y+j*yStep,z+(-Math.cos(Math.PI-(i+1)/parts*Math.PI)+1)*zSize/2);
                Point3D p3= new Point3D(x+(i+1)*xStep,y+(j+1)*yStep,z+(-Math.cos(Math.PI-(i+1)/parts*Math.PI)+1)*zSize/2);
                Point3D p4= new Point3D(x+i*xStep,y+(j+1)*yStep,z+(-Math.cos(Math.PI-i/parts*Math.PI)+1)*zSize/2);
                addSquare(p1,p2,p3,p4,2,0.1);
            }
        }
    }
    public void addHillY(double x,double y,double z,double xSize,double ySize,double zSize,int parts){
        double xStep = xSize/parts;
        double yStep = ySize/parts;
        double zStep = zSize/parts;
        for(double i=0;i<(parts);i++)
        {
            for(double j=0;j<(parts);j++)
            {
                Point3D p1= new Point3D(x+i*xStep,y+j*yStep,z+(-Math.cos(j/parts*Math.PI)+1)*zSize/2);
                Point3D p2= new Point3D(x+(i+1)*xStep,y+j*yStep,z+(-Math.cos(j/parts*Math.PI)+1)*zSize/2);
                Point3D p3= new Point3D(x+(i+1)*xStep,y+(j+1)*yStep,z+(-Math.cos((j+1)/parts*Math.PI)+1)*zSize/2);
                Point3D p4= new Point3D(x+i*xStep,y+(j+1)*yStep,z+(-Math.cos((j+1)/parts*Math.PI)+1)*zSize/2);
                addSquare(p1,p2,p3,p4,2,0.1);
            }
        }
    }
    public void addHilly(double x,double y,double z,double xSize,double ySize,double zSize,int parts){
        double xStep = xSize/parts;
        double yStep = ySize/parts;
        double zStep = zSize/parts;
        for(double i=0;i<(parts);i++)
        {
            for(double j=0;j<(parts);j++)
            {
                Point3D p1= new Point3D(x+i*xStep,y+j*yStep,z+(-Math.cos(Math.PI-j/parts*Math.PI)+1)*zSize/2);
                Point3D p2= new Point3D(x+(i+1)*xStep,y+j*yStep,z+(-Math.cos(Math.PI-j/parts*Math.PI)+1)*zSize/2);
                Point3D p3= new Point3D(x+(i+1)*xStep,y+(j+1)*yStep,z+(-Math.cos(Math.PI-(j+1)/parts*Math.PI)+1)*zSize/2);
                Point3D p4= new Point3D(x+i*xStep,y+(j+1)*yStep,z+(-Math.cos(Math.PI-(j+1)/parts*Math.PI)+1)*zSize/2);
                addSquare(p1,p2,p3,p4,2,0.1);
            }
        }
    }
}
