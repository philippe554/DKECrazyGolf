package CrazyGolf.Editor;

import CrazyGolf.Bot.Brutefinder.Brutefinder;
import CrazyGolf.FileLocations;
import CrazyGolf.PhysicsEngine.*;
import javafx.geometry.Point3D;

import javax.swing.*;
import javax.vecmath.Color3f;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

public class EditorPanel extends JPanel{

    private JButton saveButton;
    private MouseListener listener1;
    private ActionListener listener2;
    private ActionListener listener3;

    private Color green = Color.green;
    private Color gray = Color.gray;
    private Color black = Color.black;
    private Color yellow = Color.yellow;
    private Color pink = Color.PINK;

    private Rectangle[][] rectangleGrid;
    private String[][] stringGrid;

    private String chosenOption = "D";

    private final int pixelSIZE  = 20;

    private RadioButtons buttons;

    private String[] layerStrings = { "Layer 1", "Layer 2",
            "Layer 3",   "Layer 4",
            "Layer 5" };
    private Grid[] grid = new Grid[layerStrings.length];
    private JLayeredPane layeredPane;
    private JLabel label;
    private JComboBox layerList;
    private static String LAYER_COMMAND = "layer";

    public EditorPanel(RadioButtons someButtons){
        setLayout(new BorderLayout());
        buttons = someButtons;

        class ChoiceListener implements MouseListener{
            Point startDrag;

            public void mouseClicked(MouseEvent e) {
                if(buttons.ballButton.isEnabled() || buttons.wallButton.isEnabled() || buttons.floorButton.isEnabled()|| buttons.holeButton.isEnabled()
                        || buttons.sandButton.isEnabled() || buttons.loopButton.isEnabled() || buttons.castleButton.isEnabled() || buttons.bridgeButton.isEnabled()
                        || buttons.poolButton.isEnabled() || buttons.crocoButton.isEnabled()){
                    chosenOption = buttons.getChosenOption();
                    int x = e.getX();
                    int y = e.getY();

                    for(int i=0; i<rectangleGrid.length; i++) {
                        for (int j=0; j<rectangleGrid[0].length; j++) {

                            if (rectangleGrid[i][j].contains(x,y)) {
                                if (chosenOption.equals("W")){
                                    stringGrid[i][j] = "W";
                                }
                                if (chosenOption.equals("S")){
                                    stringGrid[i][j] = "S";
                                }
                                if (chosenOption.equals("F")){
                                    stringGrid[i][j] = "F";
                                }
                                if (chosenOption.equals("B")){
                                   if(isPlaced("B")== true){
                                        JOptionPane.showMessageDialog(null, "You can only place one ball!", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                    else {
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
                                    }
                                }
                                if (chosenOption.equals("H")) {
                                    if (isPlaced("H")==true) {
                                        JOptionPane.showMessageDialog(null, "You can only place one hole!", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    } else {
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
                                    }
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
                                if (chosenOption.equals("R")){
                                    if ( i < stringGrid.length - 3 && j < stringGrid[0].length - 23) {
                                        stringGrid[i][j] = "R";
                                        for (int m=0;m<4;m++){
                                            for(int k=0;k<24;k++){
                                                stringGrid[i+m][j+k] = "R";
                                            }
                                        }
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("P")){
                                    if ( i < stringGrid.length - 13 && j < stringGrid[0].length - 13) {
                                        stringGrid[i][j] = "P";
                                        for (int m=0;m<14;m++){
                                            for(int k=0;k<14;k++){
                                                stringGrid[i+m][j+k] = "P";
                                            }
                                        }
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                               /* if (chosenOption.equals("K")){
                                    if ( i < stringGrid.length - 3 && j < stringGrid[0].length - 23) {
                                        stringGrid[i][j] = "K";
                                        for (int m=0;m<4;m++){
                                            for(int k=0;k<24;k++){
                                                stringGrid[i+m][j+k] = "K";
                                            }
                                        }
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                } */

                                if (chosenOption.equals("D")) {
                                    stringGrid[i][j] = "D";
                                }

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
                                if (chosenOption.equals("S")){
                                    stringGrid[i][j] = "S";
                                }
                                if (chosenOption.equals("F")){
                                    stringGrid[i][j] = "F";
                                }
                                if (chosenOption.equals("D")){
                                    stringGrid[i][j] = "D";
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

        setSaveButton(new JButton("SAVE"));
        getSaveButton().setBackground(Color.lightGray);
        getSaveButton().setForeground(Color.darkGray);
        getSaveButton().setBorderPainted(false);
        getSaveButton().setFont(new Font("Century Gothic",Font.BOLD,30));

        getSaveButton().addActionListener(listener2);
        getSaveButton().setSize(new Dimension(20,20));
        add(getSaveButton(),BorderLayout.EAST);

        label = new JLabel();
        label.setBounds(0,0,1,1);
        label.setOpaque(true);
        label.setBackground(Color.BLACK);

        class ObjectPreviewListener implements MouseMotionListener {

            public void mouseDragged(MouseEvent e) {

            }

            public void mouseMoved(MouseEvent e) {
                chosenOption = buttons.getChosenOption();
                if (chosenOption==null) {
                    label.setBounds(0, 0, 1, 1);
                    label.setBackground(Color.black);
                } else {
                    if (chosenOption.equals("W")) {
                        label.setBounds(0, 0, pixelSIZE, pixelSIZE);
                        label.setBackground(Color.red);
                        // label.setOpaque(true);
                    } else if (chosenOption.equals("S")) {
                        label.setBounds(0, 0, pixelSIZE, pixelSIZE);
                        label.setBackground(Color.orange);
                        //  label.setOpaque(true);
                    } else if (chosenOption.equals("F")) {
                        label.setBounds(0, 0, pixelSIZE, pixelSIZE);
                        label.setBackground(Color.green);
                        //   label.setOpaque(true);
                    } else if (chosenOption.equals("B")) {
                        label.setBounds(0, 0, pixelSIZE * 2, pixelSIZE * 2);
                        label.setBackground(Color.gray);
                    } else if (chosenOption.equals("H")) {
                        label.setBounds(0, 0, pixelSIZE * 3, pixelSIZE * 3);
                        label.setBackground(Color.black);
                    } else if (chosenOption.equals("L")) {
                        label.setBounds(0, 0, pixelSIZE * 14, pixelSIZE * 6);
                        label.setBackground(Color.yellow);
                    } else if (chosenOption.equals("C")) {
                        label.setBounds(0, 0, pixelSIZE * 13, pixelSIZE * 4);
                        label.setBackground(Color.pink);
                    } else if (chosenOption.equals("R")) {
                        label.setBounds(0, 0, pixelSIZE * 4, pixelSIZE * 24);
                        label.setBackground(Color.magenta);
                    } else if (chosenOption.equals("P")) {
                        label.setBounds(0, 0, pixelSIZE * 14, pixelSIZE * 14);
                        label.setBackground(Color.blue);
                    }
                /* if (chosenOption.equals("K")){
                    label.setBounds(0,0,pixelSIZE*,pixelSIZE*);
                    label.setBackground(Color.cyan);
                } */
                    else {
                        label.setBounds(0, 0, 1, 1);
                        label.setBackground(Color.black);
                    }
                    label.setLocation(e.getX(), e.getY());
                    repaint();
                }
            }
        }

        class LayeredActionListener implements ActionListener{

            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                if (LAYER_COMMAND.equals(cmd)) {
                    layeredPane.moveToFront(label);
                    layeredPane.setLayer(label,
                            layerList.getSelectedIndex());

                }
                Grid currentGrid = grid[layerList.getSelectedIndex()];
                stringGrid = currentGrid.getStringGrid();
                rectangleGrid = currentGrid.getRectanglegGrid();
                revalidate();
                repaint();
            }
        }
        listener3 = new LayeredActionListener();

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(300, 310));
        layeredPane.addMouseListener(listener1);
        layeredPane.addMouseMotionListener(new ObjectPreviewListener());

        for (int i = 0; i < layerStrings.length; i++) {
            Grid g = new Grid();
            JComponent panel = g;
            grid[i] = g;
            layeredPane.add(panel, new Integer(i));
        }

        layeredPane.add(label, new Integer(1), 0);

        layerList = new JComboBox(layerStrings);
        layerList.setSelectedIndex(-1);    //no layer
        layerList.setActionCommand(LAYER_COMMAND);
        layerList.addActionListener(listener3);

        add(layerList, BorderLayout.SOUTH);
        add(layeredPane);
    }

    public boolean isPlaced(String s){
        for(int i=0; i<rectangleGrid.length; i++) {
            for (int j = 0; j < rectangleGrid[0].length; j++) {
                if (stringGrid[i][j].equals(s)) {
                    return true;
                }
            }
        }
        return false;
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
        for(int i=0;i<grid.length;i++) {
            world.loadWorld(grid[i].getStringGrid(), pixelSIZE, 50*i);
        }
        LinkedList<String> worldData = world.outputWorldApi2();

        /*World worldWithPhysics = new WorldCPU(worldData);
        Brutefinder brutefinder = new Brutefinder();
        brutefinder.init(worldWithPhysics);
        brutefinder.makeDatabase();
        LinkedList<String> brutefinderData = brutefinder.ouputDatabase();
*/
        LinkedList<String> returnData = new LinkedList<>();
        returnData.add("Master:World");
        for(int i=0;i<worldData.size();i++)
        {
            returnData.add(worldData.get(i));
        }

        /*returnData.add("Master:Brutefinder");
        for(int i=0;i<brutefinderData.size();i++)
        {
            returnData.add(brutefinderData.get(i));
        }*/

        return returnData;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (layerList.getSelectedIndex()==-1)
            g2.drawString("Please select a layer, thank you!", 150,200);

        else {

            for (int i = 0; i < rectangleGrid.length; i++) {
                for (int j = 0; j < rectangleGrid[0].length; j++) {

                    if (stringGrid[i][j].equals("W")) {
                        g2.setPaint(Color.RED);
                        g2.fill(rectangleGrid[i][j]);
                    }
                    if (stringGrid[i][j].equals("S")) {
                        g2.setPaint(Color.orange);
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
                    if (stringGrid[i][j].equals("R")) {
                        g2.setPaint(Color.magenta);
                        g2.fill(rectangleGrid[i][j]);
                    }
                    if (stringGrid[i][j].equals("P")) {
                        g2.setPaint(Color.BLUE);
                        g2.fill(rectangleGrid[i][j]);
                    }
                    if (stringGrid[i][j].equals("K")) {
                        g2.setPaint(Color.cyan);
                        g2.fill(rectangleGrid[i][j]);
                    }
                    g2.setPaint(Color.lightGray);
                    g2.draw(rectangleGrid[i][j]);
                }
            }
        }
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(JButton saveButton) {
        this.saveButton = saveButton;
    }
}