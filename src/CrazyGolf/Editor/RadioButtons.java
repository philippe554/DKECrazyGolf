package CrazyGolf.Editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

/**
 * Created by Carla on 15/03/2016.
 */
public class RadioButtons extends JPanel {

    private JButton saveButton;
    public JRadioButton wallButton;
    public JRadioButton sandButton;
    public JRadioButton floorButton;
    public JRadioButton ballButton;
    public JRadioButton holeButton;
    public JRadioButton loopButton;
    public JRadioButton castleButton;
    public JRadioButton bridgeButton;
    public JRadioButton poolButton;
    public JRadioButton crocoButton;
    public JRadioButton removeButton;
    public ActionListener listener;
    private String chosenOption;

    private static final int BI_WIDTH = 50;
    private Icon emptyIcon;
    private Icon selectedIcon;
    private final int FONTSIZE = 25;

    public RadioButtons(){
        // the empty circle
        emptyIcon = getEmptyIcon();

        setLayout(new BorderLayout());
        class ChoiceListener implements ActionListener
        {
            public void actionPerformed(ActionEvent e) {
                //save chosen option in order to get printed on other panel
                System.out.println("works");
                if (wallButton.isSelected()) {
                    chosenOption = "W";
                    System.out.println(chosenOption);
                }
                if (sandButton.isSelected()) {
                    chosenOption = "S";
                    System.out.println(chosenOption);
                }
                if (floorButton.isSelected()) {
                    chosenOption = "F";
                    System.out.println(chosenOption);
                }
                if (ballButton.isSelected()) {
                    chosenOption = "B";
                    System.out.println(chosenOption);
                }
                if (holeButton.isSelected()) {
                    chosenOption = "H";
                    System.out.println(chosenOption);
                }
                if (loopButton.isSelected()) {
                    chosenOption = "L";
                    System.out.println(chosenOption);
                }
                if (castleButton.isSelected()) {
                    chosenOption = "C";
                    System.out.println(chosenOption);
                }
                if (bridgeButton.isSelected()) {
                    chosenOption = "R";
                    System.out.println(chosenOption);
                }
                if (poolButton.isSelected()) {
                    chosenOption = "P";
                    System.out.println(chosenOption);
                }
                if (crocoButton.isSelected()) {
                    chosenOption = "K";
                    System.out.println(chosenOption);
                }
                if (removeButton.isSelected()) {
                    chosenOption = "D";
                    System.out.println("remove");
                }
            }
        }

        listener = new ChoiceListener();
        createControlPanel();
    }

    public Icon getEmptyIcon(){
        BufferedImage img = new BufferedImage(BI_WIDTH, BI_WIDTH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setStroke(new BasicStroke(4f));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = 4;
        int y = x;
        int width = BI_WIDTH - 2 * x;
        int height = width;
        g2.setColor(Color.lightGray);
        g2.drawOval(x, y, width, height);
        g2.dispose();

        return new ImageIcon(img);
    }

    public Icon getSelectedIcon(String s){
        //filled circle
        BufferedImage img = new BufferedImage(BI_WIDTH, BI_WIDTH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setStroke(new BasicStroke(4f));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = 4;
        int y = x;
        int width = BI_WIDTH - 2 * x;
        int height = width;

        if (s.equals("Floor")){
            g2.setColor(Color.green);
        }
        if (s.equals("Sand")){
            g2.setColor(Color.orange);
        }
        if (s.equals("Wall")){
            g2.setColor(Color.red);
        }
        if (s.equals("Ball")){
            g2.setColor(Color.gray);
        }
        if (s.equals("Hole")){
            g2.setColor(Color.black);
        }
        if (s.equals("Loop")){
            g2.setColor(Color.yellow);
        }
        if (s.equals("Castle")){
            g2.setColor(Color.pink);
        }
        if (s.equals("Bridge")){
            g2.setColor(new Color(0xC6774A));
        }
        if (s.equals("Pool")){
            g2.setColor(Color.blue);
        }
        if (s.equals("Croco")){
            g2.setColor(Color.cyan);
        }
        if (s.equals("REMOVE")){
            g2.setColor(Color.lightGray);
        }

        g2.fillOval(x, y, width, height);
        g2.setColor(Color.lightGray);
        g2.drawOval(x, y, width, height);
        g2.dispose();

        selectedIcon = new ImageIcon(img);
        return selectedIcon;
    }

    public void createControlPanel() {
        JPanel choicePanel = createButtons();
        JPanel controlPanel = new JPanel();


       /* controlPanel.setLayout(new BorderLayout());
        controlPanel.setLayout(new GridLayout(3,1));
        choicePanel.setBackground(Color.GREEN);

        controlPanel.add(choicePanel, BorderLayout.CENTER);
        controlPanel.setBackground(Color.GRAY);
        this.setLayout(new GridLayout(1,1));
        add(controlPanel);*/
        add(choicePanel);
    }

    public void buttonSettings(JRadioButton b){
        b.setSelectedIcon(getSelectedIcon(b.getText()));
        b.setBackground(Color.white);
        b.setForeground(Color.darkGray);
        b.setFont(new Font("Century Gothic",Font.BOLD,FONTSIZE));
        b.addActionListener(listener);
    }

    public JPanel createButtons(){

        floorButton = new JRadioButton("Floor", emptyIcon);
        buttonSettings(floorButton);

        sandButton = new JRadioButton("Sand", emptyIcon);
        buttonSettings(sandButton);

        wallButton = new JRadioButton("Wall", emptyIcon);
        buttonSettings(wallButton);

        ballButton = new JRadioButton("Ball", emptyIcon);
        buttonSettings(ballButton);

        holeButton = new JRadioButton("Hole", emptyIcon);
        buttonSettings(holeButton);

        loopButton = new JRadioButton("Loop", emptyIcon);
        buttonSettings(loopButton);

        castleButton = new JRadioButton("Castle", emptyIcon);
        buttonSettings(castleButton);

        bridgeButton = new JRadioButton("Bridge", emptyIcon);
        buttonSettings(bridgeButton);

        poolButton = new JRadioButton("Pool", emptyIcon);
        buttonSettings(poolButton);

        crocoButton = new JRadioButton("Croco", emptyIcon);
        buttonSettings(crocoButton);

        removeButton = new JRadioButton("REMOVE", emptyIcon);
        buttonSettings(removeButton);

        ButtonGroup group = new ButtonGroup();
        group.add(floorButton); group.add(sandButton);
        group.add(wallButton); group.add(ballButton);
        group.add(holeButton); group.add(loopButton);
        group.add(castleButton); group.add(bridgeButton);
        group.add(poolButton); group.add(crocoButton);
        group.add(removeButton);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(11,1));

        panel.add(floorButton); panel.add(sandButton);
        panel.add(wallButton); panel.add(ballButton);
        panel.add(holeButton); panel.add(loopButton);
        panel.add(castleButton); panel.add(bridgeButton);
        panel.add(poolButton); panel.add(crocoButton);
        panel.add(removeButton);
        return panel;
    }

    public String getChosenOption(){
        return chosenOption;
    }

}
