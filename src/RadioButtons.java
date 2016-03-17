import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

public class RadioButtons extends JPanel {

    private JButton saveButton;
    JRadioButton wallButton;
    JRadioButton floorButton;
    JRadioButton ballButton;
    JRadioButton holeButton;
    private ActionListener listener;
    private String chosenOption;

    public RadioButtons(){
        setLayout(new BorderLayout());
        class ChoiceListener implements ActionListener
        {
            public void actionPerformed(ActionEvent e) {
                //save chosen option in order to get printed on other panel
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
            }
        }
        listener = new ChoiceListener();
        createControlPanel();
    }



    public void createControlPanel() {
        JPanel choicePanel = createRadioButtons();
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3,1));
        choicePanel.setBackground(Color.WHITE);
        controlPanel.add(choicePanel);
        controlPanel.setBackground(Color.WHITE);
        this.setLayout(new GridLayout(1,1));
        add(controlPanel);
    }

    public JPanel createRadioButtons(){
        floorButton = new JRadioButton("Floor");
        floorButton.addActionListener(listener);

        wallButton = new JRadioButton("Wall");
        wallButton.addActionListener(listener);

        ballButton = new JRadioButton("Ball");
        ballButton.addActionListener(listener);

        holeButton = new JRadioButton("Hole");
        holeButton.addActionListener(listener);

        ButtonGroup group = new ButtonGroup();

        group.add(floorButton); group.add(wallButton);
        group.add(ballButton); group.add(holeButton);

        JPanel panel = new JPanel();
        panel.add(floorButton); panel.add(wallButton);
        panel.add(ballButton); panel.add(holeButton);
        return panel;
    }

    public String getChosenOption(){

        return chosenOption;
    }

}
