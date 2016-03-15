
import javax.swing.*;
import javax.vecmath.Vector3f;
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

        World world = new World();
        //world.loadWorld("C:\\Users\\pmmde\\GD\\Projects\\Java\\Philippe\\Github storage\\Surface2\\CrazyGolf\\src\\Field1.txt");
        world.addLoop(0,0,0);
        Golf3D w3d = new Golf3D(world,0.05f);   // panel holding the 3D canvas

        c.add(w3d, BorderLayout.WEST);
        c2.add(panel, BorderLayout.EAST);
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        pack();
        setResizable(true);
        setVisible(true);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i=0;i<10000;i++)
        {
            world.step();
            w3d.updateBall();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    { new FrameGolf(); }

}
