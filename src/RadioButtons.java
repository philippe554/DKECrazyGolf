import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Carla on 15/03/2016.
 */
public class RadioButtons extends JPanel {

    private JLabel sampleField;
    private JRadioButton wallButton;
    private JRadioButton floorButton;
    private JRadioButton ballButton;
    private JRadioButton holeButton;
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
        controlPanel.setLayout(new GridLayout(2,1));
        //controlPanel.add(ePanel);
        controlPanel.add(choicePanel);
        this.setLayout(new GridLayout(1,1));
        add(controlPanel);
    }

    public JPanel createRadioButtons(){
        wallButton = new JRadioButton("Wall");
        wallButton.addActionListener(listener);
        floorButton = new JRadioButton("Floor");
        floorButton.addActionListener(listener);
        ballButton = new JRadioButton("Ball");
        ballButton.addActionListener(listener);
        holeButton = new JRadioButton("Hole");
        holeButton.addActionListener(listener);
        holeButton.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(wallButton); group.add(floorButton);
        group.add(ballButton); group.add(holeButton);
        JPanel panel = new JPanel();
        panel.add(wallButton); panel.add(floorButton);
        panel.add(ballButton); panel.add(holeButton);
        return panel;
    }

    public String getChosenOption(){
        return chosenOption;
    }

}
