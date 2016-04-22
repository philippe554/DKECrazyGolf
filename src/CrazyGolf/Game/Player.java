package CrazyGolf.Game;

import CrazyGolf.PhysicsEngine.World;
import javafx.geometry.Point3D;

/**
 * Created by pmmde on 3/16/2016.
 */
public class Player {
    World world;
    Golf3D golf3D;
    int ballId;
    int turns=0;

    private boolean inputFlag = true;
    private boolean endFlag=false;
    private boolean deadFlag=false;
    private int velocityZeroCounter = 0;

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

    private long totalTime=0;
    private long framesCalculated=0;


    public Player(Golf3D g3d, World tworld,int tballId) {
        golf3D=g3d;
        world = tworld;
        ballId=tballId;
    }

    public void launch() {
        if(inputFlag)
        {
            world.pushBall(ballId,pushVector);
            golf3D.removeArrow();
            inputFlag=false;
            turns++;
        }
    }

    public boolean step() {
        long startTime = System.currentTimeMillis();
        for(int i=0;i<1;i++) {
            world.step(4);
        }
        long time = System.currentTimeMillis()-startTime;
        totalTime+=time;
        framesCalculated++;
        System.out.println(totalTime/framesCalculated);
        if(inputFlag) {
            updatePushParameters();
        }
        golf3D.requestFocus();
        golf3D.updateBall();
        if((!inputFlag)&&(world.getBallVelocity(0)<1))
        {
            velocityZeroCounter++;
        }
        else
        {
            velocityZeroCounter=0;
        }
        if(velocityZeroCounter>20)
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

    public void resumeGame() {
        inputFlag=true;
        endFlag=false;
        velocityZeroCounter=0;
    }

    public void updatePushParameters() {
        if(leftPressed)angle+=2;
        if(rightPressed)angle-=2;
        if(upPressed && angleUp<35)angleUp++;
        if(downPressed && angleUp>0)angleUp--;
        if(powerUpPressed && power<35)power++;
        if(powerDownPressed && power>1)power--;
        pushVector= new Point3D(Math.cos(angle*Math.PI/180.0),Math.sin(angle*Math.PI/180.0),Math.tan(angleUp*Math.PI/180.0)).normalize().multiply(power);
        golf3D.createArrow(world.balls.get(ballId).place,world.balls.get(ballId).place.add(pushVector.multiply(10)));
    }

    public boolean getInputFlag(){return inputFlag;}
    public boolean getEndFlag(){return endFlag;}
    public boolean getDeadFlag(){return deadFlag;}
}
