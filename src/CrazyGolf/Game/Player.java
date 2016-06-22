package CrazyGolf.Game;

import CrazyGolf.Bot.BotInterface;
import CrazyGolf.Bot.RandomBot.RandomBot;
import CrazyGolf.PhysicsEngine.Physics12.World;
import javafx.geometry.Point3D;

/**
 * Created by pmmde on 3/16/2016.
 */
public class Player {
    public Game game;
    public final int ballId;
    public final int launchSort;
    public int turns=0;

    public int angle = 0;
    public int angleUp = 0;
    public int power = 20 ;

    public Point3D pushVector;

    public static boolean leftPressed=false;
    public static boolean rightPressed=false;
    public static boolean upPressed=false;
    public static boolean downPressed=false;
    public static boolean powerUpPressed=false;
    public static boolean powerDownPressed=false;

    public Point3D oldLocation;

    public boolean createdInWorld;

    public Player(Game tGame,int tballId, int tLaunchSort) {
        ballId=tballId;
        launchSort=tLaunchSort;
        game=tGame;
        createdInWorld=false;
    }
    public void launch() {
        if(launchSort==0){
            if(World.DEBUG)System.out.println("Player: Ball "+ ballId+" pushed: "+pushVector);
            game.world.pushBall(ballId,pushVector);
        }else if(launchSort==1 && game.brutefinder!=null)
        {
            game.brutefinder.calcNextShot(ballId);
        }else if(launchSort==2)
        {
            BotInterface bot = new RandomBot();
            bot.init(game.world);
            bot.calcNextShot(ballId);
        }else{
            if(World.DEBUG)System.out.println("No bot found to push ball "+ballId);
        }
        turns++;
    }

    public void updatePushParameters() {
        if(launchSort==0) {
            if (leftPressed) angle -= 2;
            if (rightPressed) angle += 2;
            if (upPressed && angleUp < 90) angleUp++;
            if (downPressed && angleUp > 0) angleUp--;
            if (powerUpPressed && power < World.maxPower) power++;
            if (powerDownPressed && power > 1) power--;
            pushVector = new Point3D(Math.cos(angle * Math.PI / 180.0), Math.sin(angle * Math.PI / 180.0), Math.tan(angleUp * Math.PI / 180.0)).normalize().multiply(power);
            game.createArrow(game.world.getBall(ballId).place, angle,angleUp,power);
        }
    }
}
