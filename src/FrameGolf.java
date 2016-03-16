
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

    boolean inputFlag=true;

    int angle=0;
    int angleUp=0;
    int power=0;

    Point3D pushVector;

    public FrameGolf()
    {
        setLayout( new BorderLayout() );

        world = new World();
        world.loadWorld("C:\\Users\\pmmde\\GD\\Projects\\Java\\Philippe\\Github storage\\Surface2\\CrazyGolf\\src\\Field1.txt");
        world.addLoop(-200,0,100,100,100,50,25);
        world.addHole(0,0,0,30,70,30);
        world.addWhirepool(300,0, 50, 200,30, 100,14);
        //world.pushBall(0,new Point3D(0.767,-5,0));
        //world.pushBall(0,new Point3D(-30,0,0));
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
                if(e.getKeyChar() == KeyEvent.VK_ENTER)world.pushBall(0,pushVector);
            }
        });
        w3d.setFocusable(true);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i=0;i<100000;i++)
        {
            world.step();
            updatePushParameters();
            w3d.requestFocus();
            w3d.updateBall();

            if(inputFlag)
            {

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
        if(leftPressed)angle--;
        if(rightPressed)angle++;
        if(upPressed && angleUp<45)angleUp++;
        if(downPressed && angleUp>0)angleUp--;
        if(powerUpPressed && power<30)power++;
        if(powerDownPressed && power>1)power--;
        pushVector= new Point3D(Math.cos(angle*Math.PI/180.0),Math.sin(angle*Math.PI/180.0),Math.tan(angleUp*Math.PI/180.0)).normalize().multiply(power);
        w3d.updateArrow(new Point3f((float)world.balls.get(0).place.getX()*w3d.scale
                        ,(float)world.balls.get(0).place.getY()*w3d.scale
                        ,(float)world.balls.get(0).place.getZ()*w3d.scale),
                new Point3f((float)pushVector.getX()*w3d.scale,(float)pushVector.getY()*w3d.scale,(float)pushVector.getZ()*w3d.scale));
    }

    public class MyKeyListerner implements KeyListener {

        Point3D vector;

        int angle;
        int angleUP;
        int power;
        int quadrant;

        double x;
        double y;
        double z;

        boolean flagFlatPhase = true;
        boolean flagPowerPhase = false;
        boolean flagHeightPhase = false;

        @Override
        public void keyPressed(KeyEvent e) {
            //System.out.print("1");
            if (flagFlatPhase) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (angle == 359) {
                        angle = 0;
                    } else {
                        angle++;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (angle == 0) {
                        angle = 359;
                    } else {
                        angle--;
                    }
                }
            }
            if (flagPowerPhase) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (power == 50) {

                    } else {
                        power++;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (power == 1) {
                    } else {
                        power--;
                    }
                }
            }
            if (flagHeightPhase) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (angleUP == 45) {

                    } else {
                        angleUP++;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (angleUP == 0) {

                    } else {
                        angleUP--;
                    }
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (flagFlatPhase) {

                    flagFlatPhase = false;
                    flagHeightPhase = true;
                    System.out.println("Given dir: "+angle);
                    return;
                }
                if (flagHeightPhase) {
                    flagHeightPhase = false;
                    flagPowerPhase = true;
                    System.out.println("Given Inclination: "+angleUP);
                    return;
                }
                if (flagPowerPhase) {
                    adjustAngle();
                    calculateYGain();
                    calculateZGain();
                    magnitudeByPower();
                    createVector();
                    Point3D v= new Point3D(Math.cos(angle*Math.PI/180.0),Math.sin(angle*Math.PI/180.0),Math.tan(angleUP*Math.PI/180.0)).normalize().multiply(power);
                    w3d.updateArrow(new Point3f((float)world.balls.get(0).place.getX()*w3d.scale
                            ,(float)world.balls.get(0).place.getY()*w3d.scale
                            ,(float)world.balls.get(0).place.getZ()*w3d.scale),
                            new Point3f((float)v.getX()*w3d.scale,(float)v.getY()*w3d.scale,(float)v.getZ()*w3d.scale));
                    world.pushBall(0, v);
                    System.out.println("Given Power: "+power);
                    System.out.println("Given vector: "+v);
                }
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            //System.out.print("2");
        }

        @Override
        public void keyReleased(KeyEvent e) {
            //System.out.print("3");
        }

        public void adjustAngle() {
            if (angle > 0 && angle < 90) {
                angle = 90 - angle;
                quadrant = 1;
            }
            if (angle > 90 && angle < 180) {
                angle = 180 - angle;
                quadrant = 4;
            }
            if (angle > 180 && angle < 270) {
                angle = 270 - angle;
                quadrant = 3;
            }
            if (angle > 270 && angle < 360) {
                angle = 90 - angle;
                quadrant = 2;
            }
            if (angle == 90) {
                angle = -1;
            }
            if (angle == 180) {
                angle = -2;
            }
            if (angle == 270) {
                angle = -3;
            }
        }

        public void calculateZGain() {
            if (angle == 0) {
                z = 0;
            } else {
                z = x * (Math.tan(angleUP));
            }
        }

        public void calculateYGain() {
            if (angle > 0) {
                if (quadrant == 1) {
                    x = 1;
                    y = x * (Math.tan(angle * Math.PI / 180.));
                }
                if (quadrant == 2) {
                    x = -1;
                    y = x * (Math.tan(angle * Math.PI / 180.));
                }
                if (quadrant == 3) {
                    x = -1;
                    y = -(x * (Math.tan(angle * Math.PI / 180.)));
                }
                if (quadrant == 4) {
                    x = 1;
                    y = -(x * (Math.tan(angle * Math.PI / 180.)));
                }

            } else {
                if (angle == 0) {
                    x = -1;
                    y = 0;
                }
                if (angle == -1) {
                    x = 0;
                    y = 1;
                }
                if (angle == -2) {
                    x = 1;
                    y = 0;
                }
                if (angle == -3) {
                    x = 0;
                    y = -1;
                }
            }
        }

        public void magnitudeByPower() {
            x = x * power;
            y = y * power;
            z = z * power;
        }

        public void createVector() {
            vector = new Point3D(x, y, z);
        }

    }
    public static void main(String[] args)
    { new FrameGolf(); }

}
