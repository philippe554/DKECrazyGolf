package CrazyGolf.Editor;

import CrazyGolf.Bot.Brutefinder.Brutefinder;
import CrazyGolf.PhysicsEngine.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    private String[][] startEndStringGrid;

    private String chosenOption;

    private boolean noiseHandling;

    public int pixelSIZE  = 20;

    private RadioButtons buttons;

    private int[] loop;
    private int[] castle;
    private int[] bridge;
    private int loop0;
    private int loop1;
    private int castle0;
    private int castle1;
    private int bridge0;
    private int bridge1;

    private final String[] layerStrings = {"Layer 1", "Layer 2", "Layer 3", "Layer 4", "Layer 5"};
    private final String[] playerStrings = {"Human", "Brutefinder bot", "Random bot", "Nobody"};
    private String[] playerChoice = {"0", "3", "3", "3"};
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
        loop0 = 6;
        loop[1] = 14;
        loop1 = 14;
        castle = new int[2];
        castle[0] = 4;
        castle0 = 4;
        castle[1] = 13;
        castle1 = 13;
        bridge = new int[2];
        bridge[0] = 24;
        bridge0 = 24;
        bridge[1] = 4;
        bridge1 = 4;

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
        settings.add(makeNoiseCheckbox());
        playerList = makePlayerList();
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

    public JCheckBox makeNoiseCheckbox(){
        JCheckBox box = new JCheckBox("Noise handling?");
        box.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        box.setForeground(Color.darkGray);

        class NoiseHandlingListener implements ActionListener{

            public void actionPerformed(ActionEvent e) {
                if (box.isSelected()){
                    noiseHandling = true;
                } else noiseHandling = false;
            }
        }

        box.addActionListener(new NoiseHandlingListener());
        return box;
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

                    int temp1 = loop0;
                    loop0 = loop1;
                    loop1 = temp1;
                }
                if (chosenOption.equals("C")){
                    int temp = castle[0];
                    castle[0] = castle[1];
                    castle[1] = temp;

                    int temp1 = castle0;
                    castle0 = castle1;
                    castle1 = temp1;
                }
                if (chosenOption.equals("R")){
                    int temp = bridge[0];
                    bridge[0] = bridge[1];
                    bridge[1] = temp;

                    int temp1 = bridge0;
                    bridge0 = bridge1;
                    bridge1 = temp1;
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

    public void checkGridField(int i, int j){
        if (!(stringGrid[i][j].equals("E"))){
            deleteIt(stringGrid[i][j], i, j);
        }
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
                                    checkGridField(i, j);
                                    stringGrid[i][j] = "W";
                                    startEndStringGrid[i][j] = "W";
                                    for (int l=0; l<layerStrings.length; l++) {
                                        if (l != layerList.getSelectedIndex()){
                                            if(grid[l].getStringGrid()[i][j].equals("E")) {
                                                grid[l].getStringGrid()[i][j] = "Q";
                                            }
                                        }
                                    }
                                }
                                if (chosenOption.equals("S")){
                                    checkGridField(i, j);
                                    stringGrid[i][j] = "S";
                                    startEndStringGrid[i][j] = "S";
                                }
                                if (chosenOption.equals("F")){
                                    checkGridField(i, j);
                                    stringGrid[i][j] = "F";
                                    startEndStringGrid[i][j] = "F";
                                }
                                if (chosenOption.equals("B")){
                                    checkGridField(i, j);
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
                                        startEndStringGrid[i][j+1] = "B";
                                        startEndStringGrid[i+1][j] = "B";
                                        startEndStringGrid[i+1][j+1] = "BE";
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("H")) {
                                    checkGridField(i, j);
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
                                                startEndStringGrid[i+m][j+k] = "H";
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

                                    if ( i < stringGrid.length - (loop1-1) && j < stringGrid[0].length - (loop0-1)) {
                                        for (int m=0;m<loop1;m++){
                                            for(int k=0;k<loop0;k++){
                                                checkGridField(i+m, j+k);
                                                stringGrid[i+m][j+k] = "L";
                                                startEndStringGrid[i+m][j+k] = "L";
                                            }
                                        }
                                        if (loop0==6) {
                                            startEndStringGrid[i][j] = "LS";
                                            startEndStringGrid[i][j + 1] = "LSS";
                                            startEndStringGrid[i + 1][j] = "LSS";
                                            startEndStringGrid[i + 1][j + 1] = "LSS";
                                            startEndStringGrid[i + (loop1 - 1)][j + (loop0 - 1)] = "LE";
                                            startEndStringGrid[i + (loop1 - 1)][j + (loop0 - 1) - 1] = "LEE";
                                            startEndStringGrid[i + (loop1 - 1) - 1][j + (loop0 - 1)] = "LEE";
                                            startEndStringGrid[i + (loop1 - 1) - 1][j + (loop0 - 1) - 1] = "LEE";
                                        } else{
                                            startEndStringGrid[i + (loop1 - 1)][j] = "LS";
                                            startEndStringGrid[i + (loop1 - 2)][j] = "LSS";
                                            startEndStringGrid[i + (loop1 - 1)][j + 1] = "LSS";
                                            startEndStringGrid[i + (loop1 - 2)][j + 1] = "LSS";
                                            startEndStringGrid[i][j + (loop0 - 1)] = "LE";
                                            startEndStringGrid[i][j + (loop0 - 2)] = "LEE";
                                            startEndStringGrid[i + 1][j + loop0 - 1] = "LEE";
                                            startEndStringGrid[i + 1][j + loop0 - 2] = "LEE";
                                        }
                                        stringGrid[i][j] = "L";
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("C")){

                                    if ( i < stringGrid.length - (castle1-1) && j < stringGrid[0].length - (castle0-1)) {
                                        for (int m=0;m<castle1;m++){
                                            for(int k=0;k<castle0;k++){
                                                checkGridField(i+m, j+k);
                                                stringGrid[i+m][j+k] = "C";
                                                startEndStringGrid[i+m][j+k] = "C";
                                            }
                                        }
                                        stringGrid[i][j] = "C";
                                        startEndStringGrid[i][j] = "CS";
                                        startEndStringGrid[i+(castle1-1)][j+(castle0-1)] = "CE";
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("R")){

                                    if ( i < stringGrid.length - (bridge1-1) && j < stringGrid[0].length - (bridge0-1)) {
                                        for (int m=0;m<bridge1;m++){
                                            for(int k=0;k<bridge0;k++){
                                                checkGridField(i+m, j+k);
                                                stringGrid[i+m][j+k] = "R";
                                                startEndStringGrid[i+m][j+k] = "R";
                                            }
                                        }
                                        stringGrid[i][j] = "R";
                                        startEndStringGrid[i][j] = "RS";
                                        startEndStringGrid[i+(bridge1-1)][j+(bridge0-1)] = "RE";
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("P")){

                                    if ( i < stringGrid.length - 13 && j < stringGrid[0].length - 13) {
                                        for (int m=0;m<14;m++){
                                            for(int k=0;k<14;k++){
                                                checkGridField(i+m, j+k);
                                                stringGrid[i+m][j+k] = "P";
                                                startEndStringGrid[i+m][j+k] = "P";

                                            }
                                        }
                                        stringGrid[i][j] = "P";
                                        startEndStringGrid[i][j] = "PS";
                                        startEndStringGrid[i+13][j+13] = "PE";
                                    }  else {
                                        JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (chosenOption.equals("M")){

                                    if (layerList.getSelectedIndex() != layerStrings.length-1){
                                        if ( i < stringGrid.length - 3 && j < stringGrid[0].length - 3) {
                                            for (int m=0;m<4;m++){
                                                for(int k=0;k<4;k++){
                                                    checkGridField(i+m, j+k);
                                                    stringGrid[i+m][j+k] = "M";
                                                    startEndStringGrid[i+m][j+k] = "M";
                                                    grid[layerList.getSelectedIndex()+1].getStringGrid()[i+m][j+k]="MM";
                                                }
                                            }
                                            stringGrid[i][j] = "M";
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
                                    deleteIt(stringGrid[i][j], i, j);
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
                                    checkGridField(i, j);
                                    stringGrid[i][j] = "W";
                                    startEndStringGrid[i][j] = "W";
                                    for (int l=0; l<layerStrings.length; l++) {
                                        if (l != layerList.getSelectedIndex()){
                                            if(grid[l].getStringGrid()[i][j].equals("E")) {
                                                grid[l].getStringGrid()[i][j] = "Q";
                                            }
                                        }
                                    }
                                }
                                if (chosenOption.equals("S")){
                                    checkGridField(i, j);
                                    stringGrid[i][j] = "S";
                                    startEndStringGrid[i][j] = "S";
                                }
                                if (chosenOption.equals("F")){
                                    checkGridField(i, j);
                                    stringGrid[i][j] = "F";
                                    startEndStringGrid[i][j] = "F";
                                }
                                if (chosenOption.equals("D")){
                                    deleteIt(stringGrid[i][j], i, j);
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
                        label.setBounds(0, 0, pixelSIZE * loop1, pixelSIZE * loop0);
                        label.setBackground(Color.yellow);
                    } else if (chosenOption.equals("C")) {
                        label.setBounds(0, 0, pixelSIZE * castle1, pixelSIZE * castle0);
                        label.setBackground(Color.pink);
                    } else if (chosenOption.equals("R")) {
                        label.setBounds(0, 0, pixelSIZE * bridge1, pixelSIZE * bridge0);
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

        JLayeredPane pane = new JLayeredPane();
        pane.setPreferredSize(new Dimension(300, 310));
        pane.addMouseListener(new ChoiceListener());
        pane.addMouseMotionListener(new ObjectPreviewListener());

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

    public void deleteIt(String option, int i, int j){
        if (option.equals("F") || option.equals("S") || option.equals("W")) {
            stringGrid[i][j] = "E";
            startEndStringGrid[i][j] = "E";
        }

        if (option.equals("B")) {
            int startX = 0;
            int startY = 0;
            int endX = 0;
            int endY = 0;
            for (int q=i; q>=Math.abs(2-i); q--){
                for (int w=j; w>=Math.abs(2-j); w--){
                    if (startEndStringGrid[q][w].equals("BS")){
                        startX = q;
                        startY = w;
                    }
                }
            }
            // go forth until end
            for (int q=i; q<startX+2; q++){
                for (int w=j; w<startY+2; w++){
                    if (startEndStringGrid[q][w].equals("BE")){
                        endX = q;
                        endY = w;
                    }
                }
            }
            //save both points
            //delete everything inbetween them
            for (int q=startX; q<=endX; q++){
                for (int w=startY; w<=endY; w++){
                    stringGrid[q][w] = "E";
                    startEndStringGrid[q][w] = "E";
                }
            }
        }

        if (option.equals("H")) {
            int startX = 0;
            int startY = 0;
            int endX = 0;
            int endY = 0;
            for (int q=i; q>=Math.abs(3-i); q--){
                for (int w=j; w>=Math.abs(3-j); w--){
                    if (startEndStringGrid[q][w].equals("HS")){
                        startX = q;
                        startY = w;
                    }
                }
            }
            // go forth until end
            for (int q=i; q<startX+3; q++){
                for (int w=j; w<startY+3; w++){
                    if (startEndStringGrid[q][w].equals("HE")){
                        endX = q;
                        endY = w;
                    }
                }
            }
            //save both points
            //delete everything inbetween them
            for (int q=startX; q<=endX; q++){
                for (int w=startY; w<=endY; w++){
                    stringGrid[q][w] = "E";
                    startEndStringGrid[q][w] = "E";
                }
            }
        }

        if (option.equals("L")) {
            int startX = 0;
            int startY = 0;
            int endX = 0;
            int endY = 0;
            System.out.println(isISmallSide(option, i, j, 6));
            if (!(isISmallSide(option, i, j, 6))) {
                System.out.println("1 - if");
                for (int q = i; q >= Math.abs(14 - i); q--) {
                    for (int w = j; w >= Math.abs(6 - j); w--) {
                        if (startEndStringGrid[q][w].equals("LS")) {
                            System.out.println("2 - if");
                            startX = q;
                            startY = w;
                        }
                    }
                }
                System.out.println("3 - if");
                // go forth until LE
                for (int q = i; q < startX + 14; q++) {
                    for (int w = j; w < startY + 6; w++) {
                        if (startEndStringGrid[q][w].equals("LE")) {
                            System.out.println("4 - if");
                            endX = q;
                            endY = w;
                        }
                    }
                }
                System.out.println("5 - if");
                //save both points
                //delete everything inbetween them
                for (int q = startX; q <= endX; q++) {
                    for (int w = startY; w <= endY; w++) {
                        System.out.println("6 - if");
                        stringGrid[q][w] = "E";
                        startEndStringGrid[q][w] = "E";
                    }
                }
            } else {
                System.out.println("1 - else");
                for (int q = i; q < i + 6 && q < stringGrid.length; q++) {
                    for (int w = j; w >= Math.abs(14 - j); w--) {
                        if (startEndStringGrid[q][w].equals("LS")) {
                            System.out.println("2 - else");
                            startX = q;
                            startY = w;
                        }
                    }
                }
                // go forth until LE
                System.out.println("3 - else");

                for (int q = i; q >= Math.abs(6 - startX); q--) {
                    for (int w = j; w < startY + 14; w++) {
                        if (startEndStringGrid[q][w].equals("LE")) {
                            System.out.println("4 - else");
                            endX = q;
                            endY = w;
                        }
                    }
                }
                System.out.println("5 - else");

                //save both points
                //delete everything in between them
                for (int q = startX; q >=endX; q--) {
                    for (int w = startY; w <= endY; w++) {
                        System.out.println("6 - else");
                        stringGrid[q][w] = "E";
                        startEndStringGrid[q][w] = "E";
                    }
                }
            }
        }

        if (option.equals("C")) {
            int startX = 0;
            int startY = 0;
            int endX = 0;
            int endY = 0;
            if (isISmallSide(option, i, j, 4)) {
                System.out.println("true");
                for (int q = i; q >= Math.abs(4 - i); q--) {
                    for (int w = j; w >= Math.abs(13 - j); w--) {
                        if (startEndStringGrid[q][w].equals("CS")) {
                            startX = q;
                            startY = w;
                        }
                    }
                }
                // go forth until end
                for (int q = i; q < startX + 4; q++) {
                    for (int w = j; w < startY + 13; w++) {
                        if (startEndStringGrid[q][w].equals("CE")) {
                            endX = q;
                            endY = w;
                        }
                    }
                }
                //save both points
                //delete everything inbetween them
                for (int q = startX; q <= endX; q++) {
                    for (int w = startY; w <= endY; w++) {
                        stringGrid[q][w] = "E";
                        startEndStringGrid[q][w] = "E";
                    }
                }
            } else {
                for (int q = i; q >= Math.abs(13 - i); q--) {
                    for (int w = j; w >= Math.abs(4 - j); w--) {
                        if (startEndStringGrid[q][w].equals("CS")) {
                            startX = q;
                            startY = w;
                        }
                    }
                }
                // go forth until end
                for (int q = i; q < startX + 13; q++) {
                    for (int w = j; w < startY + 4; w++) {
                        if (startEndStringGrid[q][w].equals("CE")) {
                            endX = q;
                            endY = w;
                        }
                    }
                }
                //save both points
                //delete everything inbetween them
                for (int q = startX; q <= endX; q++) {
                    for (int w = startY; w <= endY; w++) {
                        stringGrid[q][w] = "E";
                        startEndStringGrid[q][w] = "E";
                    }
                }
            }
        }

        if (option.equals("R")) {
            int startX = 0;
            int startXX = 0;
            int endXX = 0;
            int startY = 0;
            int endX = 0;
            int endY = 0;
            int cntr = i;

            System.out.println(isISmallSide(option, i, j, 4));

            if (isISmallSide(option, i, j, 4)) {
                for (int q = i; q >= Math.abs(4 - i); q--) {
                    for (int w = j; w >= Math.abs(24 - j); w--) {
                        if (startEndStringGrid[q][w].equals("RS")) {
                            startX = q;
                            startY = w;
                        }
                    }
                }
                for (int q = i; q < (startX + 4); q++) {
                    for (int w = j; w < (startY + 24); w++) {
                        if (startEndStringGrid[q][w].equals("RE")) {
                            endX = q;
                            endY = w;
                        }
                    }
                }

                //save both points
                //delete everything inbetween them
                for (int q = startX; q <= endX; q++) {
                    for (int w = startY; w <= endY; w++) {
                        stringGrid[q][w] = "E";
                        startEndStringGrid[q][w] = "E";
                    }
                }
            } else {
                for (int q = i; q >= Math.abs(24 - i); q--) {
                    for (int w = j; w >= Math.abs(4 - j); w--) {
                        if (startEndStringGrid[q][w].equals("RS")) {
                            startX = q;
                            startY = w;
                        }
                    }
                }
                for (int q = i; q < (startX + 24); q++) {
                    for (int w = j; w < (startY + 4); w++) {
                        if (startEndStringGrid[q][w].equals("RE")) {
                            endX = q;
                            endY = w;
                        }
                    }
                }

                //save both points
                //delete everything inbetween them
                for (int q = startX; q <= endX; q++) {
                    for (int w = startY; w <= endY; w++) {
                        stringGrid[q][w] = "E";
                        startEndStringGrid[q][w] = "E";
                    }
                }
            }
        }

        if (option.equals("P")) {
            int startX = 0;
            int startY = 0;
            int endX = 0;
            int endY = 0;
            for (int q = i; q >= Math.abs(14 - i); q--) {
                for (int w = j; w >= Math.abs(14 - j); w--) {
                    if (startEndStringGrid[q][w].equals("PS")) {
                        startX = q;
                        startY = w;
                    }
                }
            }
            // go forth until LE
            for (int q = i; q < startX + 14; q++) {
                for (int w = j; w < startY + 14; w++) {
                    if (startEndStringGrid[q][w].equals("PE")) {
                        endX = q;
                        endY = w;
                    }
                }
            }
            //save both points
            //delete everything inbetween them
            for (int q = startX; q <= endX; q++) {
                for (int w = startY; w <= endY; w++) {
                    stringGrid[q][w] = "E";
                    startEndStringGrid[q][w] = "E";
                }
            }
        }

        if (option.equals("M")) {
            int startX = 0;
            int startY = 0;
            int endX = 0;
            int endY = 0;
            for (int q = i; q >= Math.abs(4 - i); q--) {
                for (int w = j; w >= Math.abs(4 - j); w--) {
                    if (startEndStringGrid[q][w].equals("MS")) {
                        startX = q;
                        startY = w;
                    }
                }
            }
            // go forth until LE
            for (int q = i; q < startX + 4; q++) {
                for (int w = j; w < startY + 4; w++) {
                    if (startEndStringGrid[q][w].equals("ME")) {
                        endX = q;
                        endY = w;
                    }
                }
            }
            //save both points
            //delete everything inbetween them
            for (int q = startX; q <= endX; q++) {
                for (int w = startY; w <= endY; w++) {
                    stringGrid[q][w] = "E";
                    startEndStringGrid[q][w] = "E";
                    grid[layerList.getSelectedIndex()+1].getStringGrid()[q][w]="E";
                    grid[layerList.getSelectedIndex()+1].getStartEndStringGrid()[q][w]="E";
                }
            }
        }
        stringGrid[i][j] = "E";
        startEndStringGrid[i][j] = "E";
    }

    public boolean isISmallSide(String s, int i, int j, int smallestSideLength){
        int startXX = 0;
        int endXX = 0;

        System.out.println("position of i: "+ i);
        if (!(stringGrid[i+1][j].equals(s))) {
            for (int k = i; k >= (i - smallestSideLength) && k >= 0; k--) {
                if (stringGrid[k][j].equals(s) ) {
                    startXX = k;
                } else break;
            }
        } else {
            for (int k = i; k >= (i - smallestSideLength-1) && k >= 0; k--) {
                if (stringGrid[k][j].equals(s) ) {
                    startXX = k;
                }
                else break;
            }
        }
        System.out.println("start found at: "+startXX );

        for (int l=startXX; l<=(startXX+smallestSideLength) && l<stringGrid.length; l++){
            if (stringGrid[l][j].equals(s)){
                endXX = l;
            }
        }
        System.out.println("end found at: "+endXX );

        System.out.println("difference: "+ (Math.abs(startXX-endXX)+1));
        if ((Math.abs(startXX-endXX)+1) == smallestSideLength)
            return true;
        else return false;
    }

    public void writeItDown(LinkedList<String> list, int i, boolean noiseHandling){
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

    public boolean isNoiseHandling() {
        return noiseHandling;
    }
}