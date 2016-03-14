import javafx.geometry.Point3D;

import javax.swing.*;
import java.awt.*;

/**
 * Created by pmmde on 3/12/2016.
 */
public class Tester2D extends JPanel {

    public JFrame window;
    public World world;

    public Tester2D(int x, int y) {
        //make new frame
        window = new JFrame("Pentomino");
        setPreferredSize(new Dimension(x, y));

        //set some parameters
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        //place in the center of the screen
        Dimension localDimension = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation((localDimension.width - x) / 2, (localDimension.height - y) / 2);

        //add the PaintComponent to the window
        window.add(this);

        //fit size
        window.pack();

        //make visible
        window.setVisible(true);

        world = new World();

        while(true)
        {
            world.step();
            repaint();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void paintComponent(Graphics g) {
        Graphics2D localGraphics2D = (Graphics2D) g;

        localGraphics2D.setColor(Color.LIGHT_GRAY);
        localGraphics2D.fill(getVisibleRect());

        if (world != null) {
            localGraphics2D.setColor(Color.BLACK);

            for(int i = 0;i<world.edges.size();i++)
            {
                localGraphics2D.drawLine((int)world.edges.get(i).points[0].getX()+200,(int)world.edges.get(i).points[0].getY()+200,(int)world.edges.get(i).points[1].getX()+200,(int)world.edges.get(i).points[1].getY()+200);
            }

            for(int i = 0;i<world.sides.size();i++)
            {
                localGraphics2D.drawLine((int)world.sides.get(i).points[0].getX()+200,(int)world.sides.get(i).points[0].getY()+200,(int)world.sides.get(i).points[1].getX()+200,(int)world.sides.get(i).points[1].getY()+200);
                localGraphics2D.drawLine((int)world.sides.get(i).points[1].getX()+200,(int)world.sides.get(i).points[1].getY()+200,(int)world.sides.get(i).points[2].getX()+200,(int)world.sides.get(i).points[2].getY()+200);
                localGraphics2D.drawLine((int)world.sides.get(i).points[2].getX()+200,(int)world.sides.get(i).points[2].getY()+200,(int)world.sides.get(i).points[0].getX()+200,(int)world.sides.get(i).points[0].getY()+200);
            }

            localGraphics2D.setColor(Color.red);
            for(int i = 0;i<world.balls.size();i++)
            {
                localGraphics2D.fillOval((int) world.balls.get(i).place.getX()+180, (int) world.balls.get(i).place.getY()+180, 40, 40);
            }
        }
    }

    public static void main(String[]args)
    {
        Tester2D tester2D = new Tester2D(800,600);
    }
}
