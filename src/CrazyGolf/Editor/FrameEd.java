package CrazyGolf.Editor;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Carla on 15/03/2016.
 */
public class FrameEd {

    public static void main(String[] args)
    {

        JFrame frame = new JFrame();


        frame.setLayout(new BorderLayout());

        Toolkit tk = Toolkit.getDefaultToolkit();
        int xSize = ((int) tk.getScreenSize().getWidth());
        int ySize = ((int) tk.getScreenSize().getHeight());
        frame.setSize(xSize,ySize);


        frame.setTitle("EDITOR");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        RadioButtons radio = new RadioButtons();

        EditorPanel editor = new EditorPanel(radio);
        editor.setBackground(Color.WHITE);
        frame.add(editor);
        frame.add(radio, BorderLayout.EAST);

        frame.setVisible(true);

    }
}