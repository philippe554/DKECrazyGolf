import javafx.geometry.Point3D;

import javax.swing.*;
import javax.vecmath.Color3f;
import java.awt.*;
import java.util.LinkedList;

/**
 * Created by Carla on 15/03/2016.
 */
public class EditorPanel extends JPanel{

    Color white = Color.white;;
    Color green= Color.green;
    Color gray= Color.gray;
    Color black= Color.black;
    Rectangle[][] grid;
    int size = 30;

    public EditorPanel(){
        add(new JLabel("grid"));
        grid = new Rectangle[30][30];

        for(int i=0; i<=grid.length-1; i++) {
            for (int j = 0; j <= grid[0].length - 1; j++) {
                grid[i][j] = new Rectangle(size, size, size*i,size*j);
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.blue);
        for(int i=1; i<grid.length; i++){
            for (int j=1; j<grid[0].length; j++) {

                    g2.setPaint(Color.blue);

                g2.fill(grid[i][j]);
                g2.draw(grid[i][j]);
            }
        }
    }

    public LinkedList<String> outputWorld(String[][]data, String file,double gs)
    {
        LinkedList<String> output = new LinkedList<>();
        boolean[][]alreadyConverted=new boolean[data.length][data[0].length];
        for(int i=0;i<alreadyConverted.length;i++)
        {
            for(int j=0;j<alreadyConverted[i].length;j++)
            {
                alreadyConverted[i][j]=false;
            }
        }
        output.add("balls");
        for(int i=0;i<data.length;i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j].equals("Ball")){
                    output.add(i*20+";"+j*20+";20");
                }
            }
        }
        output.add("triangels");
        for(int i=0;i<data.length;i++) {
            for (int j = 0; j < data[i].length; j++) {
                if(!alreadyConverted[i][j]) {
                    if (data[i][j].equals("Wall")) {
                        addSquare(output,new Point3D(i*gs,j*gs,30),
                                new Point3D(i*gs+gs,j*gs,30),
                                new Point3D(i*gs+gs,j*gs+gs,30),
                                new Point3D(i*gs,j*gs+gs,30),
                                new Color3f(0.8f,0.8f,0.8f));
                        if(i-1>=0 && !data[i-1][j].equals("Wall"))
                        {
                            addSquare(output,new Point3D(i*gs,j*gs,30),
                                    new Point3D(i*gs,j*gs+gs,30),
                                    new Point3D(i*gs,j*gs+gs,0),
                                    new Point3D(i*gs,j*gs,0),
                                    new Color3f(0.5f,0.5f,0.5f));
                        }
                        if(j-1>=0 && !data[i][j-1].equals("Wall"))
                        {
                            addSquare(output,new Point3D(i*gs,j*gs,30),
                                    new Point3D(i*gs+gs,j*gs,30),
                                    new Point3D(i*gs+gs,j*gs,0),
                                    new Point3D(i*gs,j*gs,0),
                                    new Color3f(0.5f,0.5f,0.5f));
                        }
                        if(i+1<data.length && !data[i+1][j].equals("Wall"))
                        {
                            addSquare(output,new Point3D(i*gs+gs,j*gs,30),
                                    new Point3D(i*gs+gs,j*gs+gs,30),
                                    new Point3D(i*gs+gs,j*gs+gs,0),
                                    new Point3D(i*gs+gs,j*gs,0),
                                    new Color3f(0.5f,0.5f,0.5f));
                        }
                        if(j+1<data[i].length && !data[i][j+1].equals("Wall"))
                        {
                            addSquare(output,new Point3D(i*gs,j*gs+gs,30),
                                    new Point3D(i*gs+gs,j*gs+gs,30),
                                    new Point3D(i*gs+gs,j*gs+gs,0),
                                    new Point3D(i*gs,j*gs+gs,0),
                                    new Color3f(0.5f,0.5f,0.5f));
                        }
                        alreadyConverted[i][j]=true;
                    } else if (data[i][j].equals("Floor") || data[i][j].equals("Ball")) {
                        addSquare(output,new Point3D(i*gs,j*gs,30),
                                new Point3D(i*gs+gs,j*gs,30),
                                new Point3D(i*gs+gs,j*gs+gs,30),
                                new Point3D(i*gs,j*gs+gs,30),
                                new Color3f(0,1,0));
                        alreadyConverted[i][j]=true;
                    } else if (data[i][j].equals("Hole")) {
                        addHole(output,i*gs+1.5*gs,j*gs+1.5*gs,0,30,80,14);
                        for(int k=0;k<3;k++)
                        {
                            for(int l=0;l<3;l++)
                            {
                                if(i+k<data.length && i+l<data[i+k].length) {
                                    alreadyConverted[i + k][j + l] = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return output;
    }
    public void addHole(LinkedList<String> output,double x,double y, double z,double radius,double depth,int parts) {
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
                addSquare(output,p1, p2, p4, p3, new Color3f(0.8f, 0.8f, 0.8f));
                addTriangle(output,p3, p4, center, new Color3f(0.5f, 0.5f, 0.5f));
                addTriangle(output,p1, p2, cornerPoints[i], new Color3f(0, 1, 0));
            }
        }
    }
    public void addSquare(LinkedList<String> output,Point3D p1,Point3D p2,Point3D p3,Point3D p4,Color3f c) {
        addTriangle(output,p1,p2,p3,c);
        addTriangle(output,p1,p4,p3,c);
    }
    public void addTriangle(LinkedList<String> output,Point3D p1,Point3D p2,Point3D p3,Color3f c)
    {
        output.add(p1.getX()+";"+p1.getY()+";"+p1.getZ()+";"+
                p2.getX()+";"+p2.getY()+";"+p2.getZ()+";"+
                p3.getX()+";"+p3.getY()+";"+p3.getZ()+";"+
                c.getX()+";"+c.getY()+";"+c.getZ());
    }
}
