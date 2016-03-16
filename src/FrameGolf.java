
import javafx.geometry.Point3D;

import javax.swing.*;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;


public class FrameGolf extends JFrame
{
    private Golf3D golf3D;
    private LinkedList<Player> players;
    private int currentPlayer;

    public FrameGolf()
    {
        setLayout( new BorderLayout() );

        golf3D = new Golf3D(0.05f);   // panel holding the 3D canvas

        World world= new World();
        world.loadWorld("C:\\Users\\pmmde\\GD\\Projects\\Java\\Philippe\\Github storage\\Surface2\\CrazyGolf\\src\\Field1.txt");
        world.addHole(0,0,0,30,70,30);
        golf3D.loadWorld(world);

        add(golf3D);

        players=new LinkedList<>();
        players.add(new Player(golf3D,world,0));
        currentPlayer=0;

        JComponent EastPanel = new JPanel();
        EastPanel.setPreferredSize( new Dimension(300, 800));
        EastPanel.setLayout(new BorderLayout());
        JButton button = new JButton("Play?");
        EastPanel.add(button,BorderLayout.NORTH);
        add(EastPanel, BorderLayout.EAST);

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setResizable(true);
        setVisible(true);
        pack();

        golf3D.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT)Player.leftPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_RIGHT)Player.rightPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_UP)Player.upPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_DOWN)Player.downPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_CONTROL)Player.powerDownPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_SHIFT)Player.powerUpPressed=true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT)Player.leftPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_RIGHT)Player.rightPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_UP)Player.upPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_DOWN)Player.downPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_CONTROL)Player.powerDownPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_SHIFT)Player.powerUpPressed=false;
                if(e.getKeyChar() == KeyEvent.VK_ENTER )players.get(currentPlayer).launch();
            }
        });
        golf3D.setFocusable(true);

        for(int i=0;i<100000;i++)
        {
            players.get(currentPlayer).step();
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
