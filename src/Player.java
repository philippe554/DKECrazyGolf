import javafx.geometry.Point3D;

import javax.vecmath.Point3f;

/**
 * Created by pmmde on 3/16/2016.
 */
public class Player {
    World world;
    Golf3D golf3D;
    int ballId;

    boolean inputFlag = true;
    int velocityZeroCounter = 0;

    int angle = 0;
    int angleUp = 0;
    int power = 10;

    Point3D pushVector;

    static boolean leftPressed=false;
    static boolean rightPressed=false;
    static boolean upPressed=false;
    static boolean downPressed=false;
    static boolean powerUpPressed=false;
    static boolean powerDownPressed=false;

    public Player(Golf3D g3d, World tworld,int tballId)
    {
        golf3D=g3d;
        world = tworld;
        ballId=tballId;
    }

    public void launch()
    {
        if(inputFlag)
        {
            world.pushBall(0,pushVector);
            golf3D.removeArrow();
            inputFlag=false;
        }
    }

    public boolean step()
    {
        world.step();
        if(inputFlag) {
            updatePushParameters();
        }
        golf3D.requestFocus();
        golf3D.updateBall();
        if(!inputFlag&&world.getBallVelocity(0)<0.5)
        {
            velocityZeroCounter++;
        }
        if(velocityZeroCounter==30)
        {
            velocityZeroCounter=0;
            inputFlag=true;
            return true;
        }
        return false;
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
        golf3D.createArrow(world.balls.get(ballId).place,world.balls.get(ballId).place.add(pushVector));
    }

}
