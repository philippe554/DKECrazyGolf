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
    public JRadioButton floorButton;
    public JRadioButton ballButton;
    public JRadioButton holeButton;
    public JRadioButton loopButton;
    public JRadioButton castelButton;
    public JRadioButton bridgeButton;
    //public JRadioButton removeButton;
    public ActionListener listener;
    private String chosenOption;

    private static final int BI_WIDTH = 50;
    private Icon emptyIcon;
    private Icon selectedIcon;
    private final int FONTSIZE = 25;

    public RadioButtons(){
        // the empty circle
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

        emptyIcon = new ImageIcon(img);

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
                if (castelButton.isSelected()) {
                    chosenOption = "C";
                    System.out.println(chosenOption);
                }
                if (bridgeButton.isSelected()) {
                    chosenOption = "P";
                    System.out.println(chosenOption);
                }

                /*if (removeButton.isSelected()) {
                    chosenOption = "R";
                    System.out.println("remove");
                }*/
            }
        }

        listener = new ChoiceListener();
        createControlPanel();
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
        if (s.equals("Wall")){
            g2.setColor(Color.red);
        }
        if (s.equals("Ball")){
            g2.setColor(Color.black);
        }
        if (s.equals("Hole")){
            g2.setColor(Color.gray);
        }
        if (s.equals("Loop")){
            g2.setColor(Color.yellow);
        }
        if (s.equals("Castle")){
            g2.setColor(Color.pink);
        }
        if (s.equals("Bridge")){
            g2.setColor(Color.blue);
        }

        /*if (s.equals("Remove")){
            g2.setColor(Color.blue);
        }*/

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

    public JPanel createButtons(){

        floorButton = new JRadioButton("Floor", emptyIcon);
        floorButton.setSelectedIcon(getSelectedIcon(floorButton.getText()));
        floorButton.setBackground(Color.white);
        floorButton.setForeground(Color.darkGray);
        floorButton.setFont(new Font("Century Gothic",Font.BOLD,FONTSIZE));
        floorButton.addActionListener(listener);

        wallButton = new JRadioButton("Wall", emptyIcon);
        wallButton.setSelectedIcon(getSelectedIcon(wallButton.getText()));
        wallButton.setBackground(Color.white);
        wallButton.setForeground(Color.darkGray);
        wallButton.setFont(new Font("Century Gothic",Font.BOLD,FONTSIZE));
        wallButton.addActionListener(listener);

        ballButton = new JRadioButton("Ball", emptyIcon);
        ballButton.setSelectedIcon(getSelectedIcon(ballButton.getText()));
        ballButton.setBackground(Color.white);
        ballButton.setForeground(Color.darkGray);
        ballButton.setFont(new Font("Century Gothic",Font.BOLD,FONTSIZE));
        ballButton.addActionListener(listener);

        holeButton = new JRadioButton("Hole", emptyIcon);
        holeButton.setSelectedIcon(getSelectedIcon(holeButton.getText()));
        holeButton.setBackground(Color.white);
        holeButton.setForeground(Color.darkGray);
        holeButton.setFont(new Font("Century Gothic",Font.BOLD,FONTSIZE));
        holeButton.addActionListener(listener);

        loopButton = new JRadioButton("Loop", emptyIcon);
        loopButton.setSelectedIcon(getSelectedIcon(loopButton.getText()));
        loopButton.setBackground(Color.white);
        loopButton.setForeground(Color.darkGray);
        loopButton.setFont(new Font("Century Gothic",Font.BOLD,FONTSIZE));
        loopButton.addActionListener(listener);

        castelButton = new JRadioButton("Castle", emptyIcon);
        castelButton.setSelectedIcon(getSelectedIcon(castelButton.getText()));
        castelButton.setBackground(Color.white);
        castelButton.setForeground(Color.darkGray);
        castelButton.setFont(new Font("Century Gothic",Font.BOLD,FONTSIZE));
        castelButton.addActionListener(listener);

        bridgeButton = new JRadioButton("Bridge", emptyIcon);
        bridgeButton.setSelectedIcon(getSelectedIcon(bridgeButton.getText()));
        bridgeButton.setBackground(Color.white);
        bridgeButton.setForeground(Color.darkGray);
        bridgeButton.setFont(new Font("Century Gothic",Font.BOLD,FONTSIZE));
        bridgeButton.addActionListener(listener);


        /*removeButton = new JRadioButton("Remove", emptyIcon);
        removeButton.setSelectedIcon(getSelectedIcon(removeButton.getText()));
        removeButton.setBackground(Color.white);
        removeButton.setFont(new Font("Century Gothic",Font.BOLD,FONTSIZE));
        removeButton.addActionListener(listener);*/

        ButtonGroup group = new ButtonGroup();
        group.add(floorButton); group.add(wallButton);
        group.add(ballButton); group.add(holeButton);
        group.add(loopButton); group.add(castelButton);
        group.add(bridgeButton);
        //group.add(removeButton);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7,1));

        panel.add(floorButton); panel.add(wallButton);
        panel.add(ballButton); panel.add(holeButton);
        panel.add(loopButton);panel.add(castelButton);
        panel.add(bridgeButton);
        // panel.add(removeButton);
        return panel;
    }

    public String getChosenOption(){

        return chosenOption;
    }

}
