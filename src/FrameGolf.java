
import javafx.geometry.Point3D;

import javax.swing.*;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.*;


public class FrameGolf extends JFrame
{
    World world;
    Golf3D w3d;

    boolean leftPressed=false;
    boolean rightPressed=false;
    boolean upPressed=false;
    boolean downPressed=false;
    boolean powerUpPressed=false;
    boolean powerDownPressed=false;

    public FrameGolf()
    {
        setLayout( new BorderLayout() );

        w3d = new Golf3D(world,0.05f);   // panel holding the 3D canvas
        add(w3d);

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

        w3d.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT)leftPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_RIGHT)rightPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_UP)upPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_DOWN)downPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_CONTROL)powerDownPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_SHIFT)powerUpPressed=true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT)leftPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_RIGHT)rightPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_UP)upPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_DOWN)downPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_CONTROL)powerDownPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_SHIFT)powerUpPressed=false;
                if(e.getKeyChar() == KeyEvent.VK_ENTER && inputFlag){
                    world.pushBall(0,pushVector);
                    w3d.removeArrow();
                    inputFlag=false;
                }
            }
        });
        w3d.setFocusable(true);

        for(int i=0;i<100000;i++)
        {
            world.step();
            if(inputFlag) {
                updatePushParameters();
            }
            w3d.requestFocus();
            w3d.updateBall();
            if(!inputFlag&&world.getBallVelocity(0)<0.5)
            {
                velocityZeroCounter++;
            }
            if(velocityZeroCounter==30)
            {
                velocityZeroCounter=0;
                inputFlag=true;
            }

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updatePushParameters()
    {
        if(leftPressed)angle-=2;
        if(rightPressed)angle+=2;
        if(upPressed && angleUp<45)angleUp++;
        if(downPressed && angleUp>0)angleUp--;
        if(powerUpPressed && power<30)power++;
        if(powerDownPressed && power>1)power--;
        pushVector= new Point3D(Math.cos(angle*Math.PI/180.0),Math.sin(angle*Math.PI/180.0),Math.tan(angleUp*Math.PI/180.0)).normalize().multiply(power);
        w3d.createArrow(new Point3f((float)world.balls.get(0).place.getX()*w3d.scale
                        ,(float)world.balls.get(0).place.getY()*w3d.scale
                        ,(float)world.balls.get(0).place.getZ()*w3d.scale),
                new Point3f((float) ((float)pushVector.getX()*w3d.scale*10+world.balls.get(0).place.getX()*w3d.scale)
                        ,(float)(pushVector.getY()*w3d.scale*10+world.balls.get(0).place.getY()*w3d.scale)
                        ,(float)(pushVector.getZ()*w3d.scale*10+world.balls.get(0).place.getZ()*w3d.scale)));
    }

    public static void main(String[] args)
    { new FrameGolf(); }

}
