package CrazyGolf.Game;

import CrazyGolf.PhysicsEngine.World;
import javafx.geometry.Point3D;

/**
 * Created by pmmde on 3/16/2016.
 */
public class Player {
    World world;
    Golf3D golf3D;
    Game game;
    public final int ballId;
    int turns=0;

    private boolean inputFlag = true;
    private boolean endFlag=false;
    private boolean deadFlag=false;
    private int velocityZeroCounter = 0;
    private boolean launchFlag=false;

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

    public Player(Golf3D g3d, World tworld,int tballId,Game tGame) {
        golf3D=g3d;
        world = tworld;
        ballId=tballId;
        game=tGame;
    }

    public void launch() {
        launchFlag=true;
    }

    public boolean step() {

        if(launchFlag)
        {
            launchFlag=false;
            if(inputFlag)
            {
                /*if(World.DEBUG)
                {
                    System.out.println("Ball pushed: "+ballId);
                }*/
                //world.pushBall(ballId,pushVector);
                game.brutefinder.calcNextShot(ballId);
                golf3D.removeArrow();
                inputFlag=false;
                turns++;
            }
        }
        long startTime = System.currentTimeMillis();
        for(int i=0;i<1;i++) {
            world.step(true);
        }
        long time = System.currentTimeMillis()-startTime;
        totalTime+=time;
        framesCalculated++;
        //System.out.println(totalTime/framesCalculated);
        if(inputFlag) {
            updatePushParameters();
        }
        golf3D.requestFocus();
        golf3D.updateBall();
        boolean allBallsVelocityZero=true;
        for(int i=0;i<world.getAmountBalls();i++)
        {
            if(world.getBallVelocity(i).magnitude()>1.5)
            {
                allBallsVelocityZero=false;
            }
            if(world.getBallPosition(i).getZ()<-200)
            {
                if(World.DEBUG)
                {
                    System.out.println("Ball dead: "+ballId);
                }
                deadFlag=true;
                return true;
            }
        }
        if((!inputFlag)&&allBallsVelocityZero)
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
                System.out.println("Ball in hole");
                inputFlag=false;
                endFlag=true;
            }
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
        if(powerUpPressed && power<World.maxPower)power++;
        if(powerDownPressed && power>1)power--;
        pushVector= new Point3D(Math.cos(angle*Math.PI/180.0),Math.sin(angle*Math.PI/180.0),Math.tan(angleUp*Math.PI/180.0)).normalize().multiply(power);
        golf3D.createArrow(world.getBallPosition(ballId),world.getBallPosition(ballId).add(pushVector.multiply(10)));
    }


    public boolean getInputFlag(){return inputFlag;}
    public boolean getEndFlag(){return endFlag;}
    public boolean getDeadFlag(){return deadFlag;}
}
