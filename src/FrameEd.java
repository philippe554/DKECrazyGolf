import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class FrameEd {

    public static void main(String[] args)
    {

        JFrame frame = new JFrame();


        frame.setLayout(new GridLayout(1,2));

        Toolkit tk = Toolkit.getDefaultToolkit();
        int xSize = ((int) tk.getScreenSize().getWidth());
        int ySize = ((int) tk.getScreenSize().getHeight());
        frame.setSize(xSize,ySize);


        frame.setTitle("EDITOR");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        RadioButtons radio = new RadioButtons();
        radio.setSize(new Dimension(500,500));

        EditorPanel editor = new EditorPanel(radio);
        editor.setSize(new Dimension(700,600));
        editor.setBackground(Color.WHITE);
        frame.add(editor);
        frame.add(radio);

        frame.setVisible(true);

    }
}
