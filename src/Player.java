import javafx.geometry.Point3D;

import javax.vecmath.Point3f;

/**
 * Created by pmmde on 3/16/2016.
 */
public class Player {
    World world;
    Golf3D golf3D;
    int ballId;
    int turns=0;

    boolean inputFlag = true;
    boolean endFlag=false;
    boolean deadFlag=false;
    int velocityZeroCounter = 0;

    int angle = 0;
    int angleUp = 0;
    int power = 5;

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
            turns++;
        }
    }

    public boolean step()
    {
        world.step((int) (world.balls.get(ballId).velocity.magnitude()/world.balls.get(ballId).size*1.1)+1);
        if(inputFlag) {
            updatePushParameters();
        }
        golf3D.requestFocus();
        golf3D.updateBall();
        if(!inputFlag&&world.getBallVelocity(0)<0.5)
        {
            velocityZeroCounter++;
        }
        else
        {
            velocityZeroCounter=0;
        }
        if(velocityZeroCounter==10)
        {
            velocityZeroCounter=0;
            inputFlag=true;
            if(world.checkBallInHole(ballId))
            {
                inputFlag=false;
                endFlag=true;
            }
            return true;
        }
        if(world.balls.get(ballId).place.getZ()<-1000)
        {
            deadFlag=true;
            return true;
        }
        return false;
    }

    public void resumeGame()
    {
        inputFlag=true;
        endFlag=false;
    }

    public void updatePushParameters()
    {
        if(leftPressed)angle+=2;
        if(rightPressed)angle-=2;
        if(upPressed && angleUp<35)angleUp++;
        if(downPressed && angleUp>0)angleUp--;
        if(powerUpPressed && power<35)power++;
        if(powerDownPressed && power>1)power--;
        pushVector= new Point3D(Math.cos(angle*Math.PI/180.0),Math.sin(angle*Math.PI/180.0),Math.tan(angleUp*Math.PI/180.0)).normalize().multiply(power);
        golf3D.createArrow(world.balls.get(ballId).place,world.balls.get(ballId).place.add(pushVector.multiply(10)));
    }

}
