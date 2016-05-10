package CrazyGolf.Editor;

import CrazyGolf.Bot.Brutefinder.Brutefinder;
import CrazyGolf.FileLocations;
import CrazyGolf.PhysicsEngine.World;
import CrazyGolf.PhysicsEngine.WorldCPU;
import CrazyGolf.PhysicsEngine.WorldContainer;
import javafx.geometry.Point3D;

import javax.swing.*;
import javax.vecmath.Color3f;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
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
    private Color yellow= Color.yellow;
    private Color pink = Color.PINK;

    private Rectangle[][] rectangleGrid;
    private String[][] stringGrid;

    private String chosenOption;

    private final double pixelSIZE = 20;
    private final int SIZE = (int)pixelSIZE;
    private RadioButtons buttons;
    // private int cntrB;
    // private int cntrH;

    public EditorPanel(RadioButtons someButtons){
        //cntrB =0;
        //cntrH =0;
        setLayout(new BorderLayout());
        buttons = someButtons;
        stringGrid = new String[124][85];
        rectangleGrid = new Rectangle[124][85];

        for(int i=0; i<rectangleGrid.length; i++) {
            for (int j=0; j<rectangleGrid[0].length; j++) {
                rectangleGrid[i][j] = new Rectangle(SIZE*i,SIZE*j, (int)pixelSIZE,(int) pixelSIZE);
                //if there are certain panels not used, what should the array store then?
                stringGrid[i][j] = "E";
            }
        }

        class ChoiceListener implements MouseListener{
            Point startDrag;

            public void mouseClicked(MouseEvent e) {
                if(buttons.ballButton.isEnabled() || buttons.wallButton.isEnabled() || buttons.floorButton.isEnabled()|| buttons.holeButton.isEnabled()){
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
                                   /* if(cntrB!=0){
                                        JOptionPane.showMessageDialog(null, "You can only place one ball, duh!", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                    else {*/
                                    if(i<stringGrid.length-1 && j<stringGrid[0].length-1) {
                                        //cntrB++;
                                        stringGrid[i][j] = "B";
                                        stringGrid[i][j + 1] = "B";
                                        stringGrid[i + 1][j]= "B";
                                        stringGrid[i + 1][j + 1]="B";
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                    //}
                                }
                                if (chosenOption.equals("H")) {
                                    /*if (cntrH != 0) {
                                        JOptionPane.showMessageDialog(null, "You can only place one hole, duh!", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    } else {*/
                                    if (i > 0 && i < stringGrid.length - 1 && j > 0 && j < stringGrid[0].length - 1) {
                                        //cntrH++;
                                        stringGrid[i][j] = "H";
                                        stringGrid[i - 1][j - 1] = "H";
                                        stringGrid[i - 1][j] = "H";
                                        stringGrid[i - 1][j + 1] = "H";
                                        stringGrid[i][j - 1] = "H";
                                        stringGrid[i][j + 1] = "H";
                                        stringGrid[i + 1][j - 1] = "H";
                                        stringGrid[i + 1][j] = "H";
                                        stringGrid[i + 1][j + 1] = "H";
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                    // }
                                }
                                if (chosenOption.equals("L")){
                                    if ( i < stringGrid.length - 13 && j < stringGrid[0].length - 5) {
                                        stringGrid[i][j] = "L";
                                        for (int m=0;m<14;m++){
                                            for(int k=0;k<6;k++){
                                                stringGrid[i+m][j+k] = "L";
                                            }
                                        }
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("C")){
                                    if ( i < stringGrid.length - 12 && j < stringGrid[0].length - 3) {
                                        stringGrid[i][j] = "C";
                                        for (int m=0;m<13;m++){
                                            for(int k=0;k<4;k++){
                                                stringGrid[i+m][j+k] = "C";
                                            }
                                        }
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("P")){
                                    if ( i < stringGrid.length - 3 && j < stringGrid[0].length - 23) {
                                        stringGrid[i][j] = "P";
                                        for (int m=0;m<4;m++){
                                            for(int k=0;k<24;k++){
                                                stringGrid[i+m][j+k] = "P";
                                            }
                                        }
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }

                          /*      if (chosenOption.equals("R")) {
                                    if (stringGrid[i][j] == "W" || stringGrid[i][j] == "F")
                                        stringGrid[i][j] = "E";

                                    else if (stringGrid[i][j] == "B") {
                                        cntrB--;
                                        stringGrid[i][j] = "E";
                                        stringGrid[i][j + 1] = "E";
                                        stringGrid[i + 1][j] = "E";
                                        stringGrid[i + 1][j + 1] = "E";

                                    } else if (stringGrid[i][j] == "H") {
                                        cntrH--;
                                        stringGrid[i][j] = "E";
                                        stringGrid[i - 1][j - 1] = "E";
                                        stringGrid[i - 1][j] = "E";
                                        stringGrid[i - 1][j + 1] = "E";
                                        stringGrid[i][j - 1] = "E";
                                        stringGrid[i][j + 1] = "E";
                                        stringGrid[i + 1][j - 1] = "E";
                                        stringGrid[i + 1][j] = "E";
                                        stringGrid[i + 1][j + 1] = "E";

                                    } else if (stringGrid[i][j] == "L") {
                                        int k=i;
                                        int l=j;
                                        while(stringGrid[k][l]=="L") {
                                            k--;
                                            while (stringGrid[k][l] == "L") {
                                                j--;
                                            }
                                        }
                                        for(int o = 0; o<14;o++) {
                                            for (int p = 0; p < 6; p++) {
                                                stringGrid[k+1+o][j+1+p]="E";
                                            }
                                        }
                                    }

                                } */

                            }
                            repaint();
                        }
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                startDrag = new Point(e.getX(), e.getY());
            }

            public void mouseReleased(MouseEvent e) {
                if(buttons.ballButton.isSelected() || buttons.wallButton.isSelected() || buttons.floorButton.isEnabled()|| buttons.holeButton.isEnabled()){
                    chosenOption = buttons.getChosenOption();

                    Rectangle dragQueen = new Rectangle(startDrag.x, startDrag.y,e.getX()-startDrag.x,e.getY()-startDrag.y);
                    Rectangle dragQueen2 = new Rectangle(e.getX(), e.getY(), startDrag.x-e.getX(), startDrag.y-e.getY());
                    dragQueen.setSize((int)dragQueen.getWidth(),(int)dragQueen.getHeight());

                    for(int i=0; i<rectangleGrid.length; i++) {
                        for (int j=0; j<rectangleGrid[0].length; j++) {

                            if (rectangleGrid[i][j].intersects(dragQueen)|| rectangleGrid[i][j].intersects(dragQueen2) ) {

                                if (chosenOption.equals("W")){
                                    stringGrid[i][j] = "W";
                                }
                                if (chosenOption.equals("F")){
                                    stringGrid[i][j] = "F";
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
        File field= new File(FileLocations.level1);
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

        WorldContainer world = new WorldContainer();
        world.loadWorld(stringGrid,pixelSIZE,0);
        LinkedList<String> worldData = world.outputWorldApi2();

        World worldWithPhysics = new WorldCPU(worldData);
        Brutefinder brutefinder = new Brutefinder();
        brutefinder.init(worldWithPhysics);
        brutefinder.makeDatabase();
        LinkedList<String> brutefinderData = brutefinder.ouputDatabase();

        LinkedList<String> returnData = new LinkedList<>();
        returnData.add("Master:World");
        for(int i=0;i<worldData.size();i++)
        {
            returnData.add(worldData.get(i));
        }

        returnData.add("Master:Brutefinder");
        for(int i=0;i<brutefinderData.size();i++)
        {
            returnData.add(brutefinderData.get(i));
        }

        return returnData;

    }

    public LinkedList<String> outputWorld(String[][]data, double gs) {
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
                    output.add((i*gs+gs)+";"+(j*gs+gs)+";20");
                }
            }
        }
        output.add("triangels");
        for(int i=0;i<data.length;i++) {
            for (int j = 0; j < data[i].length; j++) {
                if(!alreadyConverted[i][j]) {
                    if (data[i][j].equals("W")) {
                        addWall(data,output,alreadyConverted,i,j,gs);
                    } else if (data[i][j].equals("F") || data[i][j].equals("B")) {
                        addGrass(data,output,alreadyConverted,i,j,gs);
                    } else if (data[i][j].equals("H")) {
                        addHole(output,i*gs+1.5*gs,j*gs+1.5*gs,0,30,80,30);
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
                        addSquare(output,new Point3D(i*gs,j*gs,0),
                                new Point3D(i*gs+gs*14,j*gs,0),
                                new Point3D(i*gs+gs*14,j*gs+gs*6,0),
                                new Point3D(i*gs,j*gs+gs*6,0),
                                new Color3f(0,1,0));
                        addLoop(output,i*gs+140,j*gs+30,140,140,60,24,25);
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
                        addSquare(output,new Point3D(i*gs,j*gs,0),
                                new Point3D(i*gs+gs*13,j*gs,0),
                                new Point3D(i*gs+gs*13,j*gs+gs*4,0),
                                new Point3D(i*gs,j*gs+gs*4,0),
                                new Color3f(0,1,0));
                        addCastle(output,i*gs+40,j*gs+40,0,20,40,180);
                        for(int k=0;k<13;k++)
                        {
                            for(int l=0;l<4;l++)
                            {
                                if((i+k)<data.length && (j+l)<data[i+k].length) {
                                    alreadyConverted[i + k][j + l] = true;
                                }
                            }
                        }
                    } else if(data[i][j].equals("P"))
                    {
                        addSquare(output,new Point3D(i*gs,j*gs,0),
                                new Point3D(i*gs+gs*4,j*gs,0),
                                new Point3D(i*gs+gs*4,j*gs+gs*24,0),
                                new Point3D(i*gs,j*gs+gs*24,0),
                                new Color3f(0,1,0));
                        addBridge(output,i*gs+40,j*gs+140,0,200,50,20,80,20);
                        for(int k=0;k<4;k++)
                        {
                            for(int l=0;l<24;l++)
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
        return output;
    }
    public void addGrass(String[][]data,LinkedList<String> output,boolean[][]alreadyConverted,int i,int j,double gs) {
        int iCounter=0;
        int jCounter=0;
        boolean keepCountingI=true;
        boolean keepCountingJ=true;
        while(keepCountingI||keepCountingJ)
        {
            if(keepCountingI) {
                if ((iCounter + i) < data.length && expandGrass(data,i, j, iCounter + 1, jCounter, alreadyConverted)) {
                    iCounter++;
                }else{
                    keepCountingI=false;
                }
            }
            if(keepCountingJ) {
                if ((jCounter + j) < data[0].length && expandGrass(data,i, j, iCounter , jCounter+1, alreadyConverted)) {
                    jCounter++;
                }else{
                    keepCountingJ=false;
                }
            }
        }
        addSquare(output,new Point3D(i*gs,j*gs,0),
                new Point3D(i*gs+iCounter*gs,j*gs,0),
                new Point3D(i*gs+iCounter*gs,j*gs+jCounter*gs,0),
                new Point3D(i*gs,j*gs+jCounter*gs,0),
                new Color3f(0,1,0));
        for(int k=i;k<(i+iCounter);k++) {
            for(int l=j;l<(j+jCounter);l++) {
                alreadyConverted[k][l] = true;
            }
        }
    }
    public boolean expandGrass(String[][]data,int iStart,int jStart,int iSize,int jSize,boolean[][]alreadyConverted) {
        boolean possible=true;
        for(int i=iStart;i<(iStart+iSize);i++)
        {
            for(int j=jStart;j<(jStart+jSize);j++)
            {
                if(alreadyConverted[i][j]==true || !(data[i][j].equals("F") || data[i][j].equals("B")))
                {
                    possible=false;
                }
            }
        }
        return possible;
    }
    public void addWall(String[][]data,LinkedList<String> output,boolean[][]alreadyConverted,int i,int j,double gs) {
        int iCounter=0;
        int jCounter=0;
        boolean keepCountingI=true;
        boolean keepCountingJ=true;
        while(keepCountingI||keepCountingJ)
        {
            if(keepCountingI) {
                if ((iCounter + i) < data.length && expandWall(data,i, j, iCounter + 1, jCounter, alreadyConverted) && iCounter<10) {
                    iCounter++;
                }else{
                    keepCountingI=false;
                }
            }
            if(keepCountingJ) {
                if ((jCounter + j) < data[0].length && expandWall(data,i, j, iCounter , jCounter+1, alreadyConverted) && jCounter<10) {
                    jCounter++;
                }else{
                    keepCountingJ=false;
                }
            }
        }

        //iCounter=1;
        //jCounter=1;
        addSquare(output,new Point3D(i*gs+1,j*gs,30),
                new Point3D(i*gs+gs*iCounter+1,j*gs,30),
                new Point3D(i*gs+gs*iCounter,j*gs+gs*jCounter,30),
                new Point3D(i*gs,j*gs+gs*jCounter,30),
                new Color3f(0.8f,0.8f,0.8f));
        addSquare(output,new Point3D(i*gs+1,j*gs,30),
                new Point3D(i*gs+1,j*gs+gs*jCounter,30),
                new Point3D(i*gs,j*gs+gs*jCounter,0),
                new Point3D(i*gs,j*gs,0),
                new Color3f(0.5f,0.5f,0.5f));
        addSquare(output,new Point3D(i*gs+1,j*gs,30),
                new Point3D(i*gs+1+gs*iCounter,j*gs,30),
                new Point3D(i*gs+gs*iCounter,j*gs,0),
                new Point3D(i*gs,j*gs,0),
                new Color3f(0.5f,0.5f,0.5f));
       addSquare(output,new Point3D(i*gs+gs*iCounter+1,j*gs,30),
                new Point3D(i*gs+gs*iCounter+1,j*gs+gs*jCounter,30),
                new Point3D(i*gs+gs*iCounter,j*gs+gs*jCounter,0),
                new Point3D(i*gs+gs*iCounter,j*gs,0),
                new Color3f(0.5f,0.5f,0.5f));
        addSquare(output,new Point3D(i*gs+1,j*gs+gs*jCounter,30),
                new Point3D(i*gs+gs*iCounter+1,j*gs+gs*jCounter,30),
                new Point3D(i*gs+gs*iCounter,j*gs+gs*jCounter,0),
                new Point3D(i*gs,j*gs+gs*jCounter,0),
                new Color3f(0.5f,0.5f,0.5f));
        for(int k=i;k<(i+iCounter);k++) {
            for(int l=j;l<(j+jCounter);l++) {
                alreadyConverted[k][l] = true;
            }
        }
    }
    public boolean expandWall(String[][]data,int iStart,int jStart,int iSize,int jSize,boolean[][]alreadyConverted) {
        boolean possible=true;
        for(int i=iStart;i<(iStart+iSize);i++)
        {
            for(int j=jStart;j<(jStart+jSize);j++)
            {
                if(alreadyConverted[i][j]==true || !(data[i][j].equals("W")))
                {
                    possible=false;
                }
            }
        }
        return possible;
    }
    public void addHole(LinkedList<String> output,double x,double y, double z,double radius,double depth,int parts) {
        output.add("hole");
        output.add(x+";"+y+";"+(z-60));
        output.add("triangels");
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
    public void addLoop(LinkedList<String> output,double x,double y,double z,double size,double width,int parts,double wallSize) {
        double angleGrowSize=Math.PI/(parts/2);
        double widthCounter=0;
        double widthIncrrement=width/parts;
        for(double angle = 0;angle<Math.PI*1.99;angle+=angleGrowSize)
        {
            Point3D p1=new Point3D(x+Math.sin(angle)*size,y-width/2+widthCounter,z-Math.cos(angle)*size);
            Point3D p2=new Point3D(x+Math.sin(angle+angleGrowSize)*size,y-width/2+widthCounter+widthIncrrement,z-Math.cos(angle+angleGrowSize)*size);
            Point3D p3=new Point3D(x+Math.sin(angle)*size,y+width/2+widthCounter,z-Math.cos(angle)*size);
            Point3D p4=new Point3D(x+Math.sin(angle+angleGrowSize)*size,y+width/2+widthCounter+widthIncrrement,z-Math.cos(angle+angleGrowSize)*size);
            addSquare(output,p1,p2,p4,p3,new Color3f(0.8f,0.8f,0.8f));
            Point3D p1in=new Point3D(x+Math.sin(angle)*(size-wallSize),y-width/2+widthCounter,z-Math.cos(angle)*(size-wallSize));
            Point3D p2in=new Point3D(x+Math.sin(angle+angleGrowSize)*(size-wallSize),y-width/2+widthCounter+widthIncrrement,z-Math.cos(angle+angleGrowSize)*(size-wallSize));
            Point3D p3in=new Point3D(x+Math.sin(angle)*(size-wallSize),y+width/2+widthCounter,z-Math.cos(angle)*(size-wallSize));
            Point3D p4in=new Point3D(x+Math.sin(angle+angleGrowSize)*(size-wallSize),y+width/2+widthCounter+widthIncrrement,z-Math.cos(angle+angleGrowSize)*(size-wallSize));
            addSquare(output,p1,p2,p2in,p1in,new Color3f(0.5f,0.5f,0.5f));
            addSquare(output,p3,p4,p4in,p3in,new Color3f(0.5f,0.5f,0.5f));
            widthCounter+=widthIncrrement;
        }
    }
    public void addCastle(LinkedList<String> output,double x,double y,double z,double parts,double towerSize,double towerHeight) {
        for(int j=0;j<2;j++) {
            double angleGrowSize = Math.PI / (parts / 2);
            for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
                Point3D p1 = new Point3D(j*towerHeight+x + Math.cos(angle) * towerSize, y + Math.sin(angle) * towerSize, z);
                Point3D p2 = new Point3D(j*towerHeight+x + Math.cos(angle) * towerSize, y + Math.sin(angle) * towerSize, z + towerHeight);
                Point3D p3 = new Point3D(j*towerHeight+x + Math.cos(angle + angleGrowSize) * towerSize, y + Math.sin(angle + angleGrowSize) * towerSize, z + towerHeight);
                Point3D p4 = new Point3D(j*towerHeight+x + Math.cos(angle + angleGrowSize) * towerSize, y + Math.sin(angle + angleGrowSize) * towerSize, z);
                addSquare(output,p1, p2, p3, p4, new Color3f(1f, 0f, 0.5f));
                Point3D top = new Point3D(j*towerHeight+x, y, z + towerHeight * 1.5);
                Point3D tp1 = new Point3D(j*towerHeight+x + Math.cos(angle) * towerSize * 1.2, y + Math.sin(angle) * towerSize * 1.2, z + towerHeight);
                Point3D tp2 = new Point3D(j*towerHeight+x + Math.cos(angle + angleGrowSize) * towerSize * 1.2, y + Math.sin(angle + angleGrowSize) * towerSize * 1.2, z + towerHeight);
                addTriangle(output,tp1, tp2, top, new Color3f(0.2f, 0.2f, 0.2f));
            }
        }
        Point3D b1 = new Point3D(x,y+20,z+towerHeight*0.5);
        Point3D b2 = new Point3D(x,y-20,z+towerHeight*0.5);
        Point3D b3 = new Point3D(x+towerHeight,y-20,z+towerHeight*0.5);
        Point3D b4 = new Point3D(x+towerHeight,y+20,z+towerHeight*0.5);
        addSquare(output,b1, b2, b3, b4, new Color3f(0.4f, 0.4f, 0.4f));
        Point3D t1 = new Point3D(x,y+20,z+towerHeight*0.7);
        Point3D t2 = new Point3D(x,y-20,z+towerHeight*0.7);
        Point3D t3 = new Point3D(x+towerHeight,y-20,z+towerHeight*0.7);
        Point3D t4 = new Point3D(x+towerHeight,y+20,z+towerHeight*0.7);
        addSquare(output,t1, t2, t3, t4, new Color3f(0.4f, 0.4f, 0.4f));
        addSquare(output,b1, t1, t4, b4, new Color3f(0.4f, 0.4f, 0.4f));
        addSquare(output,b2, t2, t3, b3, new Color3f(0.4f, 0.4f, 0.4f));
    }
    public void addBridge(LinkedList<String> output,double x,double y,double z,double length,double height,double borderHeight,double width,int parts) {
        for(int j=0;j<2;j++) {
            double angleGrowSize = Math.PI / (parts / 2);
            for (double angle = 0; angle < Math.PI * 1.99; angle += angleGrowSize) {
                Point3D p1 = new Point3D(x + Math.cos(angle) * (width*0.25),j*length + y + Math.sin(angle) * (width*0.25), z);
                Point3D p2 = new Point3D(x + Math.cos(angle) * (width*0.25), j*length + y + Math.sin(angle) * (width*0.25), z + height);
                Point3D p3 = new Point3D(x + Math.cos(angle + angleGrowSize) * (width*0.25), j*length+y + Math.sin(angle + angleGrowSize) * (width*0.25), z + height);
                Point3D p4 = new Point3D(x + Math.cos(angle + angleGrowSize) * (width*0.25), j*length+y + Math.sin(angle + angleGrowSize) * (width*0.25), z);
                addSquare(output,p1, p2, p3, p4, new Color3f(0.2f, 0.2f, 0.2f));
            }
        }
        Point3D p1 = new Point3D(x-width*0.5,y-width*0.5,z+height);
        Point3D p2 = new Point3D(x+width*0.5,y-width*0.5,z+height);
        Point3D p3 = new Point3D(x+width*0.5,y+width*0.5+length,z+height);
        Point3D p4 = new Point3D(x-width*0.5,y+width*0.5+length,z+height);
        addSquare(output,p1, p2, p3, p4, new Color3f(0.8f, 0.8f, 0.8f));
        Point3D p1d = new Point3D(x-width*0.5,y-width*0.5-length/2,z);
        Point3D p2d = new Point3D(x+width*0.5,y-width*0.5-length/2,z);
        Point3D p3d = new Point3D(x+width*0.5,y+width*0.5+1.5*length,z);
        Point3D p4d = new Point3D(x-width*0.5,y+width*0.5+1.5*length,z);
        addSquare(output,p1, p2, p2d, p1d, new Color3f(0.8f, 0.8f, 0.8f));
        addSquare(output,p3, p4, p4d, p3d, new Color3f(0.8f, 0.8f, 0.8f));
        addSquare(output,p1, p4, p4.add(0,0,borderHeight), p1.add(0,0,borderHeight), new Color3f(0.5f, 0.5f, 0.5f));
        addSquare(output,p2, p3, p3.add(0,0,borderHeight), p2.add(0,0,borderHeight), new Color3f(0.5f, 0.5f, 0.5f));

        addSquare(output,p1, p1d, p1d.add(0,0,borderHeight), p1.add(0,0,borderHeight), new Color3f(0.5f, 0.5f, 0.5f));
        addSquare(output,p2, p2d, p2d.add(0,0,borderHeight), p2.add(0,0,borderHeight), new Color3f(0.5f, 0.5f, 0.5f));
        addSquare(output,p3, p3d, p3d.add(0,0,borderHeight), p3.add(0,0,borderHeight), new Color3f(0.5f, 0.5f, 0.5f));
        addSquare(output,p4, p4d, p4d.add(0,0,borderHeight), p4.add(0,0,borderHeight), new Color3f(0.5f, 0.5f, 0.5f));
        System.out.println(p1d.getY());
        System.out.println(p3d.getY());
    }
    public void addSquare(LinkedList<String> output,Point3D p1,Point3D p2,Point3D p3,Point3D p4,Color3f c) {
        addTriangle(output,p1,p2,p3,c);
        addTriangle(output,p1,p4,p3,c);
    }
    public void addTriangle(LinkedList<String> output, Point3D p1, Point3D p2, Point3D p3, Color3f c) {
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
                }
                if (stringGrid[i][j].equals("H")) {
                    g2.setPaint(black);
                    g2.fill(rectangleGrid[i][j]);
                }
                if (stringGrid[i][j].equals("L")) {
                    g2.setPaint(yellow);
                    g2.fill(rectangleGrid[i][j]);
                }
                if (stringGrid[i][j].equals("C")) {
                    g2.setPaint(pink);
                    g2.fill(rectangleGrid[i][j]);
                }
                if (stringGrid[i][j].equals("P")) {
                    g2.setPaint(Color.BLUE);
                    g2.fill(rectangleGrid[i][j]);
                }
                g2.setPaint(Color.lightGray);
                g2.draw(rectangleGrid[i][j]);
            }
        }

    }
}
