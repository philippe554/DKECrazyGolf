
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class FrameGolf extends JFrame
{
    public FrameGolf()
    {
        super("Crazy Golf");
        Container c = getContentPane();
        Container c2 = getContentPane();
        c.setLayout( new BorderLayout() );
        c2.setLayout( new BorderLayout() );

        //create param panel;
        JPanel panel = new JPanel();
        panel.setPreferredSize( new Dimension(300, 800));
        panel.setLayout(new BorderLayout());
        JButton button = new JButton("Play?");
        panel.add(button,BorderLayout.NORTH);

        Golf3D w3d = new Golf3D();   // panel holding the 3D canvas
        c.add(w3d, BorderLayout.WEST);
        c2.add(panel, BorderLayout.EAST);

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        pack();
        setResizable(true);
        setVisible(true);
    }


// -----------------------------------------

    public static void main(String[] args)
    { new FrameGolf(); }

}
