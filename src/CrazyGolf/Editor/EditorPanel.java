package CrazyGolf.Editor;

import CrazyGolf.Bot.Brutefinder.Brutefinder;
import CrazyGolf.PhysicsEngine.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

public class EditorPanel extends JPanel{

    private JButton saveButton;
    private JButton loadButton;

    private Color green = Color.green;
    private Color gray = Color.gray;
    private Color black = Color.black;
    private Color yellow = Color.yellow;
    private Color pink = Color.PINK;

    private Rectangle[][] rectangleGrid;
    private String[][] stringGrid;
    private Rectangle[][] startEndRectangleGrid;
    private String[][] startEndStringGrid;

    private String chosenOption;

    public int pixelSIZE  = 20;

    private RadioButtons buttons;

    private int[] loop;
    private int[] castle;
    private int[] bridge;

    private final String[] layerStrings = {"Layer 1", "Layer 2", "Layer 3", "Layer 4", "Layer 5"};
    private final String[] playerStrings = {"Human", "Brutefinder bot", "One-Shooter bot", "Random bot", "Nobody"};
    private String[] playerChoice = {"0", "3", "3", "3", "3"};
    private JLayeredPane layeredPane;
    public JComboBox layerList;
    public JComboBox[] playerList;
    private Grid[] grid = new Grid[layerStrings.length];
    private JLabel label;
    private JButton rotate;
    private static String LAYER_COMMAND = "layer";

    public EditorPanel(RadioButtons someButtons){
        chosenOption = "D";
        loop = new int[2];
        loop[0] = 6;
        loop[1] = 14;
        castle = new int[2];
        castle[0] = 4;
        castle[1] = 13;
        bridge = new int[2];
        bridge[0] = 24;
        bridge[1] = 4;

        setLayout(new BorderLayout());

        JPanel settingPanel = new JPanel();
        settingPanel.setLayout(new BorderLayout());

        JPanel loadSave = new JPanel();
        loadSave.setLayout(new GridLayout(2,1));

        JPanel settings = new JPanel();
        settings.setLayout(new GridLayout(6,1));

        buttons = someButtons;

        layeredPane = makeLayeredPane();
        layerList = makeLayerList();

        // layeredPane.setFocusable(true);
        // layeredPane.requestFocusInWindow();

     /*   class RotateListener implements KeyListener{

            public void keyTyped(KeyEvent e) {
                System.out.println("works");
                if (e.getKeyCode()==KeyEvent.VK_UNDEFINED){
                    System.out.println("left");
                }
                if (e.getKeyCode()==KeyEvent.VK_R){
                    System.out.println("right");
                }
            }

            public void keyPressed(KeyEvent e) {}

            public void keyReleased(KeyEvent e) {}
        } */

        //   layeredPane.addKeyListener(new RotateListener());

        setSaveButton(new JButton("SAVE"));
        getSaveButton().setBackground(Color.lightGray);
        getSaveButton().setForeground(Color.darkGray);
        getSaveButton().setBorderPainted(false);
        getSaveButton().setFont(new Font("Century Gothic",Font.BOLD,30));
        getSaveButton().setSize(new Dimension(20,20));

        setLoadButton(new JButton("LOAD"));
        getLoadButton().setBackground(Color.lightGray);
        getLoadButton().setForeground(Color.darkGray);
        getLoadButton().setBorderPainted(false);
        getLoadButton().setFont(new Font("Century Gothic",Font.BOLD,30));

        rotate = makeRotationButton();

        settings.add(layerList);
        playerList= makePlayerList();
        for (int i=0; i<playerStrings.length; i++){
            settings.add(playerList[i]);
        }
        settingPanel.add(settings, BorderLayout.NORTH);
        loadSave.add(getLoadButton());
        loadSave.add(getSaveButton());
        settingPanel.add(loadSave);
        settingPanel.add(rotate, BorderLayout.SOUTH);

        add(settingPanel, BorderLayout.EAST);
        add(layeredPane, BorderLayout.CENTER);
    }

    public JButton makeRotationButton(){
        JButton button = new JButton("Rotate");
        button.setFont(new Font("Century Gothic", Font.BOLD, 16));
        button.setBackground(Color.darkGray);
        button.setForeground(Color.lightGray);

        class RotateListener implements ActionListener{

            public void actionPerformed(ActionEvent e) {
                if (chosenOption.equals("L")){
                    int temp = loop[0];
                    loop[0] = loop[1];
                    loop[1] = temp;
                }
                if (chosenOption.equals("C")){
                    int temp = castle[0];
                    castle[0] = castle[1];
                    castle[1] = temp;
                }
                if (chosenOption.equals("R")){
                    int temp = bridge[0];
                    bridge[0] = bridge[1];
                    bridge[1] = temp;
                }
            }
        }

        button.addActionListener(new RotateListener());

        return button;
    }

    public JComboBox makeLayerList(){

        JComboBox list = new JComboBox(layerStrings);
        list.setSelectedIndex(-1);    //no layer
        list.setFont(new Font("Century Gothic", Font.PLAIN, 16));
        list.setActionCommand(LAYER_COMMAND);

        class LayeredActionListener implements ActionListener{

            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if (LAYER_COMMAND.equals(command)) {
                    layeredPane.moveToFront(label);
                    layeredPane.setLayer(label,
                            list.getSelectedIndex());

                }
                Grid currentGrid = grid[list.getSelectedIndex()];
                stringGrid = currentGrid.getStringGrid();
                rectangleGrid = currentGrid.getRectanglegGrid();
                startEndRectangleGrid = currentGrid.getStartEndRectangleGrid();
                startEndStringGrid = currentGrid.getStartEndStringGrid();
                revalidate();
                repaint();

            }
        }

        list.addActionListener(new LayeredActionListener());
        list.setSelectedIndex(2);

        return list;
    }

    public JComboBox[] makePlayerList(){

        JComboBox[] listArray = new JComboBox[playerStrings.length];

        class PlayerActionListener implements ActionListener{

            public void actionPerformed(ActionEvent e) {

                for (int i=0; i<listArray.length; i++){
                    if (e.getSource()==listArray[i]){
                        playerChoice[i]= Integer.toString(listArray[i].getSelectedIndex());
                    }
                }

            }
        }

        for (int i=0; i<playerStrings.length; i++){
            JComboBox list = new JComboBox(playerStrings);
            list.setSelectedIndex(playerStrings.length-1);    //last value >> Nobody
            list.setFont(new Font("Century Gothic", Font.PLAIN, 12));
            list.addActionListener(new PlayerActionListener());
            listArray[i] = list;
        }
        listArray[0].setSelectedIndex(0); //first value >> Human (so at least one player is playing)

        return listArray;
    }

    public JLayeredPane makeLayeredPane(){

        class ChoiceListener implements MouseListener{
            Point startDrag;

            public void mouseClicked(MouseEvent e) {
                if(layerList.getSelectedIndex() != -1){
                    chosenOption = buttons.getChosenOption();
                    int x = e.getX();
                    int y = e.getY();

                    for(int i=0; i<rectangleGrid.length; i++) {
                        for (int j=0; j<rectangleGrid[0].length; j++) {

                            if (rectangleGrid[i][j].contains(x,y)) {

                                if (chosenOption.equals("W")){
                                    stringGrid[i][j] = "W";
                                    for (int l=0; l<layerStrings.length; l++) {
                                        if (l != layerList.getSelectedIndex()){
                                            if(grid[l].getStringGrid()[i][j].equals("E")) {
                                                grid[l].getStringGrid()[i][j] = "Q";
                                            }
                                        }
                                    }
                                }
                                if (chosenOption.equals("S")){
                                    stringGrid[i][j] = "S";
                                }
                                if (chosenOption.equals("F")){
                                    stringGrid[i][j] = "F";
                                }
                                if (chosenOption.equals("B")){
                                    if(i<stringGrid.length-1 && j<stringGrid[0].length-1) {
                                        for (int h = 0; h < grid.length; h++) {
                                            for (int l = 0; l < grid[h].getRectanglegGrid().length; l++) {
                                                for (int k = 0; k < grid[h].getRectanglegGrid()[0].length; k++) {
                                                    if (grid[h].getStringGrid()[l][k].equals("B")) {
                                                        grid[h].getStringGrid()[l][k]="F";
                                                    }
                                                }
                                            }
                                        }
                                        stringGrid[i][j] = "B";
                                        stringGrid[i][j + 1] = "B";
                                        stringGrid[i + 1][j]= "B";
                                        stringGrid[i + 1][j + 1]="B";
                                        startEndStringGrid[i][j] = "BS";
                                        startEndStringGrid[i+1][j+1] = "BE";
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("H")) {
                                    if(i<stringGrid.length-2 && j<stringGrid[0].length-2) {
                                        for (int h = 0; h < grid.length; h++) {
                                            for (int l = 0; l < grid[h].getRectanglegGrid().length; l++) {
                                                for (int k = 0; k < grid[h].getRectanglegGrid()[0].length; k++) {
                                                    if (grid[h].getStringGrid()[l][k].equals("H")) {
                                                        grid[h].getStringGrid()[l][k]="F";
                                                    }
                                                }
                                            }
                                        }
                                        for (int m=0;m<3;m++){
                                            for(int k=0;k<3;k++){
                                                stringGrid[i+m][j+k] = "H";
                                            }
                                        }
                                        startEndStringGrid[i][j] = "HS";
                                        startEndStringGrid[i+2][j+2] = "HE";
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("L")){
                                    if ( i < stringGrid.length - (loop[1]-1) && j < stringGrid[0].length - (loop[0]-1)) {
                                        stringGrid[i][j] = "L";
                                        for (int m=0;m<loop[1];m++){
                                            for(int k=0;k<loop[0];k++){
                                                stringGrid[i+m][j+k] = "L";
                                            }
                                        }
                                        if (loop[0]==6) {
                                            startEndStringGrid[i][j] = "LS";
                                            startEndStringGrid[i][j + 1] = "LSS";
                                            startEndStringGrid[i + 1][j] = "LSS";
                                            startEndStringGrid[i + 1][j + 1] = "LSS";
                                            startEndStringGrid[i + (loop[1] - 1)][j + (loop[0] - 1)] = "LE";
                                            startEndStringGrid[i + (loop[1] - 1)][j + (loop[0] - 1) - 1] = "LEE";
                                            startEndStringGrid[i + (loop[1] - 1) - 1][j + (loop[0] - 1)] = "LEE";
                                            startEndStringGrid[i + (loop[1] - 1) - 1][j + (loop[0] - 1) - 1] = "LEE";
                                        } else{
                                            startEndStringGrid[i + (loop[1] - 1)][j] = "LS";
                                            startEndStringGrid[i + (loop[1] - 2)][j] = "LSS";
                                            startEndStringGrid[i + (loop[1] - 1)][j + 1] = "LSS";
                                            startEndStringGrid[i + (loop[1] - 2)][j + 1] = "LSS";
                                            startEndStringGrid[i][j + (loop[0] - 1)] = "LE";
                                            startEndStringGrid[i][j + (loop[0] - 2)] = "LEE";
                                            startEndStringGrid[i + 1][j + loop[0] - 1] = "LEE";
                                            startEndStringGrid[i + 1][j + loop[0] - 2] = "LEE";
                                        }
                                        startEndRectangleGrid[i][j] = new Rectangle(i*pixelSIZE, j*pixelSIZE, loop[1]*pixelSIZE, loop[0]*pixelSIZE);
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("C")){
                                    if ( i < stringGrid.length - (castle[1]-1) && j < stringGrid[0].length - (castle[0]-1)) {
                                        stringGrid[i][j] = "C";
                                        for (int m=0;m<castle[1];m++){
                                            for(int k=0;k<castle[0];k++){
                                                stringGrid[i+m][j+k] = "C";
                                            }
                                        }
                                        startEndStringGrid[i][j] = "CS";
                                        startEndStringGrid[i+(castle[1]-1)][j+(castle[0]-1)] = "CE";
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("R")){
                                    if ( i < stringGrid.length - (bridge[1]-1) && j < stringGrid[0].length - (bridge[0]-1)) {
                                        stringGrid[i][j] = "R";
                                        for (int m=0;m<bridge[1];m++){
                                            for(int k=0;k<bridge[0];k++){
                                                stringGrid[i+m][j+k] = "R";
                                            }
                                        }
                                        startEndStringGrid[i][j] = "RS";
                                        startEndStringGrid[i+(bridge[1]-1)][j+(bridge[0]-1)] = "RE";
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
                                        startEndStringGrid[i][j] = "PS";
                                        startEndStringGrid[i+13][j+13] = "PE";
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("M")){
                                    if (layerList.getSelectedIndex() != layerStrings.length-1){
                                        if ( i < stringGrid.length - 3 && j < stringGrid[0].length - 3) {
                                            stringGrid[i][j] = "M";
                                            for (int m=0;m<4;m++){
                                                for(int k=0;k<4;k++){
                                                    stringGrid[i+m][j+k] = "M";
                                                    grid[layerList.getSelectedIndex()+1].getStringGrid()[i+m][j+k]="MM";
                                                }
                                            }
                                            startEndStringGrid[i][j] = "MS";
                                            startEndStringGrid[i+3][j+3] = "ME";
                                            grid[layerList.getSelectedIndex()+1].getStartEndStringGrid()[i][j] = "MS";
                                            grid[layerList.getSelectedIndex()+1].getStartEndStringGrid()[i+3][j+3] = "ME";
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, "You cannot build a hill up here", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }

                                }

                                if (chosenOption.equals("D")) {
                                    //startEndRectangleGrid[i][j].contains(new Point(x,y)) &&
                                    /*if (startEndRectangleGrid[i][j].getBounds().contains(x,y)) {
                                        if ( startEndRectangleGrid[i][j].getHeight()!=20 && startEndRectangleGrid[i][j].getWidth()!=20){
                                            JOptionPane.showMessageDialog(null, "intersects", "error checking", JOptionPane.PLAIN_MESSAGE);
                                        }
                                    }*/
                                   /* if (stringGrid[i][j].equals("L")){
                                        //go back until LS
                                        int startX = 0;
                                        int startY = 0;
                                        int endX = 0;
                                        int endY = 0;
                                        for (int q=i; q>=0; q--){
                                            for (int w=j; w>=0; w--){
                                                if (stringGrid[q][w].equals("LS")){
                                                    System.out.println("klick");
                                                    startX = q;
                                                    startY = w;
                                                    System.out.println("start coords: " + q + " " + w);
                                                }
                                            }
                                        }
                                        // go forth until LE
                                        for (int q=i; q<stringGrid.length; q++){
                                            for (int w=j; w<stringGrid[0].length; w++){
                                                if (stringGrid[q][w].equals("LE")){
                                                    endX = q;
                                                    endY = w;
                                                }
                                            }
                                        }
                                        //save both points
                                        //delete everything inbetween them
                                        for (int q=startX; q<endX; q++){
                                            for (int w=startY; w<endY; w++){
                                                stringGrid[i][j] = "E";
                                                startEndStringGrid[i][j] = "E";
                                            }
                                        }
                                        //also delete in startEndString

                                        //done! :D
                                        stringGrid[i][j] = "E";
                                        startEndStringGrid[i][j] = "E";
                                    }*/
                                    stringGrid[i][j] = "E";
                                    startEndStringGrid[i][j] = "E";
                                }

                            }

                            boolean isWall=false;
                            for (int l = 0; l < layerStrings.length; l++) {
                                if(grid[l].getStringGrid()[i][j].equals("W")){
                                    isWall=true;
                                }
                            }
                            if(isWall){
                                for (int l = 0; l < layerStrings.length; l++) {
                                    if(grid[l].getStringGrid()[i][j].equals("E")){
                                        grid[l].getStringGrid()[i][j] = "Q";
                                    }
                                }
                            }else{
                                for (int l = 0; l < layerStrings.length; l++) {
                                    if(grid[l].getStringGrid()[i][j].equals("Q")){
                                        grid[l].getStringGrid()[i][j] = "E";
                                    }
                                }
                            }

                         /*   for (int l = 0; l < layerStrings.length-1; l++) {
                                if(grid[l].getStringGrid()[i][j].equals("M")){
                                    grid[l+1].getStringGrid()[i][j] = "MM";
                                } else {
                                    grid[l+1].getStringGrid()[i][j] = "E";
                                }
                            } */

                            for (int l = 0; l < layerStrings.length; l++) {
                                if(grid[l].getStartEndStringGrid()[i][j].equals("LS") || grid[l].getStartEndStringGrid()[i][j].equals("LSS") || grid[l].getStartEndStringGrid()[i][j].equals("LE") || grid[l].getStartEndStringGrid()[i][j].equals("LEE")){
                                    if(!(grid[l].getStringGrid()[i][j].equals("L"))){
                                        grid[l].getStartEndStringGrid()[i][j] = "E";
                                    }
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
                if(layerList.getSelectedIndex() != -1){
                    chosenOption = buttons.getChosenOption();



                    Rectangle dragQueen = new Rectangle(Math.min(startDrag.x,e.getX()), Math.min(startDrag.y,e.getY())
                            ,Math.abs(e.getX()-startDrag.x), Math.abs(e.getY()-startDrag.y));

                    for(int i=0; i<rectangleGrid.length; i++) {
                        for (int j=0; j<rectangleGrid[0].length; j++) {

                            if (rectangleGrid[i][j].intersects(dragQueen)) {

                                if (chosenOption.equals("W")){
                                    /*stringGrid[i][j] = "W";
                                    for (int l=0; l<layerStrings.length; l++) {
                                        if (l != layerList.getSelectedIndex()){
                                            grid[l].getStringGrid()[i][j]="Q";
                                        }
                                    }*/
                                    stringGrid[i][j] = "W";
                                    for (int l=0; l<layerStrings.length; l++) {
                                        if (l != layerList.getSelectedIndex()){
                                            if(grid[l].getStringGrid()[i][j].equals("E")) {
                                                grid[l].getStringGrid()[i][j] = "Q";
                                            }
                                        }
                                    }
                                }
                                if (chosenOption.equals("S")){
                                    stringGrid[i][j] = "S";
                                }
                                if (chosenOption.equals("F")){
                                    stringGrid[i][j] = "F";
                                }
                                if (chosenOption.equals("D")){
                                    stringGrid[i][j] = "E";
                                }

                                /*for (int l = 0; l < layerStrings.length; l++) {
                                    if (l != layerList.getSelectedIndex()) {
                                        if (!(grid[l].getStringGrid()[i][j].equals("Q")) && stringGrid[i][j].equals("W"))
                                            grid[l].getStringGrid()[i][j] = "Q";
                                        if (grid[l].getStringGrid()[i][j].equals("Q") && !(stringGrid[i][j].equals("W")))
                                            grid[l].getStringGrid()[i][j] = "D";
                                    }
                                }*/

                                boolean isWall=false;
                                for (int l = 0; l < layerStrings.length; l++) {
                                    if(grid[l].getStringGrid()[i][j].equals("W")){
                                        isWall=true;
                                    }
                                }
                                if(isWall){
                                    for (int l = 0; l < layerStrings.length; l++) {
                                        if(grid[l].getStringGrid()[i][j].equals("E")){
                                            grid[l].getStringGrid()[i][j] = "Q";
                                        }
                                    }
                                }else{
                                    for (int l = 0; l < layerStrings.length; l++) {
                                        if(grid[l].getStringGrid()[i][j].equals("Q")){
                                            grid[l].getStringGrid()[i][j] = "E";
                                        }
                                    }
                                }

                                for (int l = 0; l < layerStrings.length; l++) {
                                    if(grid[l].getStartEndStringGrid()[i][j].equals("LS") || grid[l].getStartEndStringGrid()[i][j].equals("LSS") || grid[l].getStartEndStringGrid()[i][j].equals("LE") || grid[l].getStartEndStringGrid()[i][j].equals("LEE")){
                                        if(!(grid[l].getStringGrid()[i][j].equals("L"))){
                                            grid[l].getStartEndStringGrid()[i][j] = "E";
                                        }
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

        class ObjectPreviewListener implements MouseMotionListener {

            public void mouseDragged(MouseEvent e) {}

            public void mouseMoved(MouseEvent e) {
                chosenOption = buttons.getChosenOption();
                if (chosenOption==null) {
                    label.setBounds(0, 0, 1, 1);
                    label.setBackground(Color.black);
                } else {
                    if (chosenOption.equals("W")) {
                        label.setBounds(0, 0, pixelSIZE, pixelSIZE);
                        label.setBackground(Color.red);
                    } else if (chosenOption.equals("S")) {
                        label.setBounds(0, 0, pixelSIZE, pixelSIZE);
                        label.setBackground(Color.orange);
                    } else if (chosenOption.equals("F")) {
                        label.setBounds(0, 0, pixelSIZE, pixelSIZE);
                        label.setBackground(Color.green);
                    } else if (chosenOption.equals("B")) {
                        label.setBounds(0, 0, pixelSIZE * 2, pixelSIZE * 2);
                        label.setBackground(Color.gray);
                    } else if (chosenOption.equals("H")) {
                        label.setBounds(0, 0, pixelSIZE * 3, pixelSIZE * 3);
                        label.setBackground(Color.black);
                    } else if (chosenOption.equals("L")) {
                        label.setBounds(0, 0, pixelSIZE * loop[1], pixelSIZE * loop[0]);
                        label.setBackground(Color.yellow);
                    } else if (chosenOption.equals("C")) {
                        label.setBounds(0, 0, pixelSIZE * castle[1], pixelSIZE * castle[0]);
                        label.setBackground(Color.pink);
                    } else if (chosenOption.equals("R")) {
                        label.setBounds(0, 0, pixelSIZE * bridge[1], pixelSIZE * bridge[0]);
                        label.setBackground(new Color(0xC6774A));
                    } else if (chosenOption.equals("P")) {
                        label.setBounds(0, 0, pixelSIZE * 14, pixelSIZE * 14);
                        label.setBackground(Color.blue);
                    } else if (chosenOption.equals("M")){
                        label.setBounds(0, 0, pixelSIZE*4, pixelSIZE*4);
                        label.setBackground(new Color(0xB7FF56));
                    }
                    else {
                        label.setBounds(0, 0, 1, 1);
                        label.setBackground(Color.black);
                    }
                    label.setLocation(e.getX(), e.getY());
                    repaint();
                }
            }
        }

    /*    class RotateListener implements KeyListener{

            public void keyTyped(KeyEvent e) {
                System.out.println("works");
                if (e.getKeyCode()==KeyEvent.VK_L){
                    System.out.println("left");
                }
                if (e.getKeyCode()==KeyEvent.VK_R){
                    System.out.println("right");
                }
            }

            public void keyPressed(KeyEvent e) {}

            public void keyReleased(KeyEvent e) {}
        } */

        JLayeredPane pane = new JLayeredPane();
        pane.setPreferredSize(new Dimension(300, 310));
        pane.addMouseListener(new ChoiceListener());
        pane.addMouseMotionListener(new ObjectPreviewListener());
        //  pane.addKeyListener(new RotateListener());

        for (int i = 0; i < layerStrings.length; i++) {
            Grid g = new Grid();
            JComponent panel = g;
            grid[i] = g;
            pane.add(panel, new Integer(i));
        }

        label = new JLabel();
        label.setBounds(0,0,1,1);
        label.setOpaque(true);
        label.setBackground(Color.BLACK);

        pane.add(label, new Integer(1), 0);

        return pane;
    }

    public boolean isPlaced(String s) {
        for (int h = 0; h < grid.length; h++) {
            for (int i = 0; i < grid[h].getRectanglegGrid().length; i++) {
                for (int j = 0; j < grid[h].getRectanglegGrid()[0].length; j++) {
                    if (grid[h].getStringGrid()[i][j].equals(s)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void writeItDown(LinkedList<String> list, int i){
        File field= new File("Slot"+i+".txt");
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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (layerList.getSelectedIndex()==-1) {
            g2.setFont(new Font("Century Gothic", Font.BOLD, 21));
            g2.drawString("Please select a layer, thank you!", 150, 200);
        }
        else {

            for (int i = 0; i < rectangleGrid.length; i++) {
                for (int j = 0; j < rectangleGrid[0].length; j++) {

                    if (stringGrid[i][j].equals("W")) {
                        g2.setPaint(Color.RED);
                        g2.fill(rectangleGrid[i][j]);
                    }
                    if (stringGrid[i][j].equals("Q")) {
                        g2.setPaint(new Color(0xFFBEC6));
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
                        g2.setPaint(Color.red);
                        g2.fill(startEndRectangleGrid[i][j]);
                    }
                    if (stringGrid[i][j].equals("C")) {
                        g2.setPaint(pink);
                        g2.fill(rectangleGrid[i][j]);
                    }
                    if (stringGrid[i][j].equals("R")) {
                        g2.setPaint(new Color(0xC6774A));
                        g2.fill(rectangleGrid[i][j]);
                    }
                    if (stringGrid[i][j].equals("P")) {
                        g2.setPaint(Color.BLUE);
                        g2.fill(rectangleGrid[i][j]);
                    }
                    if (stringGrid[i][j].equals("K")) {
                        g2.setPaint(Color.cyan);
                    }
                    if (stringGrid[i][j].equals("M") || stringGrid[i][j].equals("MM")) {
                        g2.setPaint(new Color(0xB7FF56));
                        g2.fill(rectangleGrid[i][j]);
                    }
                    if (startEndStringGrid[i][j].equals("LS") || startEndStringGrid[i][j].equals("LSS") || startEndStringGrid[i][j].equals("LE") || startEndStringGrid[i][j].equals("LEE")) {
                        g2.setPaint(new Color(0xFFD109));
                        g2.fill(rectangleGrid[i][j]);
                    }
                    g2.setPaint(Color.lightGray);
                    g2.draw(rectangleGrid[i][j]);
                }
            }
          /*  for (int i = 0; i < startEndStringGrid.length; i++) {
                for (int j = 0; j < startEndStringGrid[0].length; j++) {
                     if (startEndStringGrid[i][j].equals("LS") || startEndStringGrid[i][j].equals("LSS") || startEndStringGrid[i][j].equals("LE") || startEndStringGrid[i][j].equals("LEE")) {
                        g2.setPaint(new Color(0xFFD109));
                        g2.fill(rectangleGrid[i][j]);
                      //   g2.setPaint(Color.red);
                       //  g2.fill(startEndRectangleGrid[i][j]);
                    }
                  //  if (startEndStringGrid[i][j].equals("LS")){
                    //    g2.setPaint(Color.red);
                      //  g2.fill(startEndRectangleGrid[i][j]);
                  //  }
                    g2.setPaint(Color.lightGray);
                    g2.draw(rectangleGrid[i][j]);
                }
            }*/
        }
    }

    public JButton getLoadButton(){ return loadButton; }

    public JButton getSaveButton() {
        return saveButton;
    }

    public void setLoadButton(JButton loadbutton){ this.loadButton = loadbutton; }

    public void setSaveButton(JButton saveButton) {
        this.saveButton = saveButton;
    }

    public void setGrid(Grid[] g) { grid=g; }

    public Grid[] getGrid(){
        return grid;
    }

    public String[] getPlayerChoice(){
        return playerChoice;
    }

    public void setPlayerChoice(String[]pc){
        playerChoice=pc;
        for(int i=0;i<pc.length;i++){
            playerList[i].setSelectedIndex(Integer.parseInt(pc[i]));
        }
    }
}
