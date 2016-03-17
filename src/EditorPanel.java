import javafx.geometry.Point3D;

import javax.swing.*;
import javax.vecmath.Color3f;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class EditorPanel extends JPanel{

    private JButton saveButton;
    private MouseListener listener1;
    private ActionListener listener2;

    private Color white = Color.white;;
    private Color green= Color.green;
    private Color gray= Color.gray;
    private Color black= Color.black;

    private Rectangle[][] rectangleGrid;
    private String[][] stringGrid;
    private final int SIZE = 20;
    private String chosenOption;

    private final double pixelSIZE = 20;
    private RadioButtons buttons;

    public EditorPanel(RadioButtons someButtons){

        setLayout(new BorderLayout());
        buttons = someButtons;
        stringGrid = new String[40][48];
        rectangleGrid = new Rectangle[40][48];

        for(int i=0; i<rectangleGrid.length; i++) {
            for (int j=0; j<rectangleGrid[0].length; j++) {
                rectangleGrid[i][j] = new Rectangle(20+SIZE*i,20+ SIZE*j, (int)pixelSIZE,(int) pixelSIZE);
                //if there are certain panels not used, what should the array store then?
                stringGrid[i][j] = "E";
            }
        }

        class ChoiceListener implements MouseListener{
            Point startDrag;

            public void mouseClicked(MouseEvent e) {
                if(buttons.ballButton.isSelected() || buttons.wallButton.isSelected() || buttons.floorButton.isSelected()|| buttons.holeButton.isSelected()){
                    chosenOption = buttons.getChosenOption();
                    int x = e.getX();
                    int y =e.getY();

                    for(int i=0; i<rectangleGrid.length; i++) {
                        for (int j=0; j<rectangleGrid[0].length; j++) {

                            if (rectangleGrid[i][j].contains(x,y)) {
                                if (chosenOption.equals("W")){
                                    stringGrid[i][j] = "W";
                                }
                                if (chosenOption.equals("F")){
                                    stringGrid[i][j] = "F";
                                }
                                if (chosenOption.equals("B")){
                                    if(i<stringGrid.length-1 && j<stringGrid.length-1) {
                                        stringGrid[i][j] = "B";
                                    }
                                    else {JOptionPane.showMessageDialog(null, "Position not allowed", "Thank You, come again", JOptionPane.PLAIN_MESSAGE);}
                                }
                                if (chosenOption.equals("H")){
                                    if(i>0 && i<stringGrid.length-1 && j>0 && j<stringGrid.length-1) {
                                        stringGrid[i][j] = "H";
                                    }else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "Thank You, come again", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                repaint();
                            }
                        }
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                startDrag = new Point(e.getX(), e.getY());
            }

            public void mouseReleased(MouseEvent e) {
                if(buttons.ballButton.isSelected() || buttons.wallButton.isSelected() || buttons.floorButton.isSelected()|| buttons.holeButton.isSelected()){
                    chosenOption = buttons.getChosenOption();

                    Rectangle dragQueen = new Rectangle(startDrag.x, startDrag.y,e.getX()-startDrag.x,e.getY()-startDrag.y);
                    Rectangle dragQueen2 = new Rectangle(e.getX(), e.getY(), startDrag.x-e.getX(), startDrag.y-e.getY());
                    dragQueen.setSize((int)(dragQueen.getWidth()+3.5),(int)(dragQueen.getHeight()+3.5));

                    for(int i=0; i<rectangleGrid.length; i++) {
                        for (int j=0; j<rectangleGrid[0].length; j++) {

                            if (rectangleGrid[i][j].intersects(dragQueen)|| rectangleGrid[i][j].intersects(dragQueen2) ) {

                                if (chosenOption.equals("W")){
                                    stringGrid[i][j] = "W";
                                }
                                if (chosenOption.equals("F")){
                                    stringGrid[i][j] = "F";
                                }
                                if (chosenOption.equals("B")){
                                    if(i<stringGrid.length-1 && j<stringGrid.length-1) {
                                        stringGrid[i][j] = "B";
                                    }
                                    else {JOptionPane.showMessageDialog(null, "Position not allowed", "Thank You, come again", JOptionPane.PLAIN_MESSAGE);}
                                }
                                if (chosenOption.equals("H")){
                                    if(i>0 && i<stringGrid.length-1 && j>0 && j<stringGrid.length-1) {
                                        stringGrid[i][j] = "H";
                                    }else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "Thank You, come again", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                repaint();
                            }
                        }
                    }

                    startDrag = null;
                    repaint();
                }
            }


            public void mouseEntered(MouseEvent e) {}

            public void mouseExited(MouseEvent e) {}

        }

        class SaveListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                //write into file
                writeItDown(getDataForFileWriting());
            }
        }

        listener1 = new ChoiceListener();
        listener2 = new SaveListener();
        addMouseListener(listener1);


        saveButton = new JButton("SAVE");
        saveButton.setBackground(Color.lightGray);
        saveButton.setForeground(Color.darkGray);
        saveButton.setBorderPainted(false);
        saveButton.setFont(new Font("Century Gothic",Font.BOLD,30));

        saveButton.addActionListener(new SaveListener());
        saveButton.setSize(new Dimension(20,20));
        add(saveButton,BorderLayout.EAST);
    }


    public void writeItDown(LinkedList<String> list){
        File field= new File("C:\\Users\\pmmde\\Desktop\\field.txt");
        FileWriter writeFile = null;
        // allows us to write the file
        PrintWriter writer = null;
        Scanner inWriteDown = null;
        try {
            writeFile = new FileWriter(field);
            writer = new PrintWriter(writeFile);
            inWriteDown = new Scanner(field);

            for (String str : list) {
                writer.write(str+ "\r\n");
            }
        } catch (Exception e) {
            // errors
        } finally // closes the writer
        {
            try {
                if (writer != null)
                    writer.close();
                if (inWriteDown != null)
                    inWriteDown.close();
            } catch (Exception e) {
            }
        }
    }

    public LinkedList<String> getDataForFileWriting(){
        return outputWorld(stringGrid, pixelSIZE);
    }

    public LinkedList<String> outputWorld(String[][]data, double gs)
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
                if (data[i][j].equals("B")){
                    output.add(i*20+";"+j*20+";20");
                }
            }
        }
        output.add("triangels");
        for(int i=0;i<data.length;i++) {
            for (int j = 0; j < data[i].length; j++) {
                if(!alreadyConverted[i][j]) {
                    if (data[i][j].equals("W")) {
                        addSquare(output,new Point3D(i*gs,j*gs,30),
                                new Point3D(i*gs+gs,j*gs,30),
                                new Point3D(i*gs+gs,j*gs+gs,30),
                                new Point3D(i*gs,j*gs+gs,30),
                                new Color3f(0.8f,0.8f,0.8f));
                        if(i-1>=0 && !data[i-1][j].equals("W"))
                        {
                            addSquare(output,new Point3D(i*gs,j*gs,30),
                                    new Point3D(i*gs,j*gs+gs,30),
                                    new Point3D(i*gs,j*gs+gs,0),
                                    new Point3D(i*gs,j*gs,0),
                                    new Color3f(0.5f,0.5f,0.5f));
                        }
                        if(j-1>=0 && !data[i][j-1].equals("W"))
                        {
                            addSquare(output,new Point3D(i*gs,j*gs,30),
                                    new Point3D(i*gs+gs,j*gs,30),
                                    new Point3D(i*gs+gs,j*gs,0),
                                    new Point3D(i*gs,j*gs,0),
                                    new Color3f(0.5f,0.5f,0.5f));
                        }
                        if(i+1<data.length && !data[i+1][j].equals("W"))
                        {
                            addSquare(output,new Point3D(i*gs+gs,j*gs,30),
                                    new Point3D(i*gs+gs,j*gs+gs,30),
                                    new Point3D(i*gs+gs,j*gs+gs,0),
                                    new Point3D(i*gs+gs,j*gs,0),
                                    new Color3f(0.5f,0.5f,0.5f));
                        }
                        if(j+1<data[i].length && !data[i][j+1].equals("W"))
                        {
                            addSquare(output,new Point3D(i*gs,j*gs+gs,30),
                                    new Point3D(i*gs+gs,j*gs+gs,30),
                                    new Point3D(i*gs+gs,j*gs+gs,0),
                                    new Point3D(i*gs,j*gs+gs,0),
                                    new Color3f(0.5f,0.5f,0.5f));
                        }
                        alreadyConverted[i][j]=true;
                    } else if (data[i][j].equals("F") || data[i][j].equals("B")) {
                        addSquare(output,new Point3D(i*gs,j*gs,0),
                                new Point3D(i*gs+gs,j*gs,0),
                                new Point3D(i*gs+gs,j*gs+gs,0),
                                new Point3D(i*gs,j*gs+gs,0),
                                new Color3f(0,1,0));
                        alreadyConverted[i][j]=true;
                    } else if (data[i][j].equals("H")) {
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
    public void addTriangle(LinkedList<String> output, Point3D p1, Point3D p2, Point3D p3, Color3f c)
    {
        output.add(p1.getX()+";"+p1.getY()+";"+p1.getZ()+";"+
                p2.getX()+";"+p2.getY()+";"+p2.getZ()+";"+
                p3.getX()+";"+p3.getY()+";"+p3.getZ()+";"+
                c.getX()+";"+c.getY()+";"+c.getZ());
    }



    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;


        for(int i=0; i<rectangleGrid.length; i++){
            for (int j=0; j<rectangleGrid[0].length; j++){
                g2.setPaint(black);
                g2.draw(rectangleGrid[i][j]);
                if (stringGrid[i][j].equals("W")) {
                    g2.setPaint(Color.RED);
                    g2.fill(rectangleGrid[i][j]);
                }
                if (stringGrid[i][j].equals("F")) {
                    g2.setPaint(green);
                    g2.fill(rectangleGrid[i][j]);
                }
                if (stringGrid[i][j].equals("B")) {
                    g2.setPaint(gray);
                    g2.fill(rectangleGrid[i][j]);
                    g2.fill(rectangleGrid[i][j + 1]);
                    g2.fill(rectangleGrid[i + 1][j]);
                    g2.fill(rectangleGrid[i + 1][j + 1]);
                }
                if (stringGrid[i][j].equals("H")) {
                    g2.setPaint(black);
                    g2.fill(rectangleGrid[i][j]);
                    g2.fill(rectangleGrid[i - 1][j - 1]);
                    g2.fill(rectangleGrid[i - 1][j]);
                    g2.fill(rectangleGrid[i - 1][j + 1]);
                    g2.fill(rectangleGrid[i][j - 1]);
                    g2.fill(rectangleGrid[i][j + 1]);
                    g2.fill(rectangleGrid[i + 1][j - 1]);
                    g2.fill(rectangleGrid[i + 1][j]);
                    g2.fill(rectangleGrid[i + 1][j + 1]);
                }
            }
        }

    }
}
