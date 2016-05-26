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

    private String chosenOption;

    public int pixelSIZE  = 20;

    private RadioButtons buttons;

    private final String[] layerStrings = {"Layer 1", "Layer 2", "Layer 3", "Layer 4", "Layer 5"};
    private final String[] playerStrings = {"Human", "Brutefinder bot", "One-Shooter bot", "Random bot", "Nobody"};
    private String[] playerChoice = {"0", "3", "3", "3", "3"};
    private JLayeredPane layeredPane;
    private JComboBox layerList;
    private Grid[] grid = new Grid[layerStrings.length];
    private JLabel label;
    private static String LAYER_COMMAND = "layer";

    public EditorPanel(RadioButtons someButtons){
        chosenOption = "D";

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


        settings.add(layerList);
        for (int i=0; i<playerStrings.length; i++){
            settings.add(makePlayerList()[i]);
        }
        settingPanel.add(settings, BorderLayout.NORTH);
        loadSave.add(getLoadButton());
        loadSave.add(getSaveButton());
        settingPanel.add(loadSave);
        add(settingPanel, BorderLayout.EAST);
        add(layeredPane, BorderLayout.CENTER);
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
                revalidate();
                repaint();

            }
        }

        list.addActionListener(new LayeredActionListener());

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
                                            grid[l].getStringGrid()[i][j]="Q";
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
                                    if(isPlaced("B")== true){
                                        JOptionPane.showMessageDialog(null, "You can only place one ball!", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }
                                    else {
                                        if(i<stringGrid.length-1 && j<stringGrid[0].length-1) {
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
                                        if (i < stringGrid.length - 2 && j < stringGrid[0].length - 2) {
                                            stringGrid[i][j] = "H";
                                            for (int m=0;m<3;m++){
                                                for(int k=0;k<3;k++){
                                                    stringGrid[i+m][j+k] = "H";
                                                }
                                            }
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
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Position not allowed", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, "You cannot build a hill up here", "CrazyGolf Police", JOptionPane.PLAIN_MESSAGE);
                                    }

                                }

                                if (chosenOption.equals("D")) {
                                    stringGrid[i][j] = "D";
                                }

                            }

                            for (int l = 0; l < layerStrings.length; l++) {
                                if (l != layerList.getSelectedIndex()) {
                                    if (!(grid[l].getStringGrid()[i][j].equals("Q")) && stringGrid[i][j].equals("W"))
                                        grid[l].getStringGrid()[i][j] = "Q";
                                    if (grid[l].getStringGrid()[i][j].equals("Q") && !(stringGrid[i][j].equals("W")))
                                        grid[l].getStringGrid()[i][j] = "D";
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

                    Rectangle dragQueen = new Rectangle(startDrag.x, startDrag.y,e.getX()-startDrag.x,e.getY()-startDrag.y);
                    Rectangle dragQueen2 = new Rectangle(e.getX(), e.getY(), startDrag.x-e.getX(), startDrag.y-e.getY());
                    dragQueen.setSize((int)dragQueen.getWidth(),(int)dragQueen.getHeight());

                    for(int i=0; i<rectangleGrid.length; i++) {
                        for (int j=0; j<rectangleGrid[0].length; j++) {

                            if (rectangleGrid[i][j].intersects(dragQueen)|| rectangleGrid[i][j].intersects(dragQueen2) ) {

                                if (chosenOption.equals("W")){
                                    stringGrid[i][j] = "W";
                                    for (int l=0; l<layerStrings.length; l++) {
                                        if (l != layerList.getSelectedIndex()){
                                            grid[l].getStringGrid()[i][j]="Q";
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
                                    stringGrid[i][j] = "D";
                                }

                                for (int l = 0; l < layerStrings.length; l++) {
                                    if (l != layerList.getSelectedIndex()) {
                                        if (!(grid[l].getStringGrid()[i][j].equals("Q")) && stringGrid[i][j].equals("W"))
                                            grid[l].getStringGrid()[i][j] = "Q";
                                        if (grid[l].getStringGrid()[i][j].equals("Q") && !(stringGrid[i][j].equals("W")))
                                            grid[l].getStringGrid()[i][j] = "D";
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
                        label.setBounds(0, 0, pixelSIZE * 14, pixelSIZE * 6);
                        label.setBackground(Color.yellow);
                    } else if (chosenOption.equals("C")) {
                        label.setBounds(0, 0, pixelSIZE * 13, pixelSIZE * 4);
                        label.setBackground(Color.pink);
                    } else if (chosenOption.equals("R")) {
                        label.setBounds(0, 0, pixelSIZE * 4, pixelSIZE * 24);
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

               /*         g2.setPaint(new Color(0xFFBEC6));
                       // for (int l=0; l<layerStrings.length; l++) {
                            g2.fill(grid[0].getRectanglegGrid()[i][j]);
                            /*if (l != layerList.getSelectedIndex()){
                                g2.fill(grid[l].getRectanglegGrid()[i][j]);
                            }*/
                        //}
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

    public void setGrid(Grid[] g) {
        grid= g;
    }

    public Grid[] getGrid(){
        return grid;
    }

    public String[] getPlayerChoice(){
        return playerChoice;
    }

    public void setPlayerChoice(String[]i){
        playerChoice=i;
    }
}
