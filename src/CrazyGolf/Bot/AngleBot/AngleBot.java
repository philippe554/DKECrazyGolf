package CrazyGolf.Bot.AngleBot;

import CrazyGolf.Bot.BotInterface;
import CrazyGolf.PhysicsEngine.Objects.Parts.Ball;
import CrazyGolf.PhysicsEngine.World;
import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Maxim on 24-5-2016.
 */
public class AngleBot implements BotInterface {

    private World world;

    @Override
    public void init(World w) {
        world = w;
    }

    @Override
    public void calcNextShot(int playerNumber) {
        ArrayList<Ball> ballArray = new ArrayList<Ball>();
        Point3D direc = world.getHolePosition();
        for(double i=10; i>0; i--){
            for(double j=10;j>0; j--){
                for(int h=0; h<=90;h++){

                    Ball ball = new Ball(world.getBallSize(playerNumber), world.getBallPosition(playerNumber));
                    ball.velocity = new Point3D(direc.getX() *0.1* i, direc.getY() + h , direc.getZ() *0.1*j);
                    ballArray.add(ball);

                    direc = new Point3D(direc.getX() * i, direc.getY() + h , direc.getZ() *j);

                }
            }
        }
        world.stepSimulated(ballArray, false);
        Ball winner;
        int bestDistanceBall = -1;
        double bestDistance = -1;
        for(int i = 0; i<ballArray.size()-1; i++){
            Ball tempBall = ballArray.get(i);
            //if(world.getHolePosition().distance(tempBall.place) < (tempBall.size)){
            //    winner = tempBall;
            //    world.pushBall(playerNumber, winner.velocity);
            //}
            //else{
            double distance = Math.sqrt(Math.sqrt(world.getHolePosition().getX()-tempBall.place.getX()) + Math.sqrt(world.getHolePosition().getY()-tempBall.place.getY()));
            if(bestDistance<distance){

                bestDistanceBall = i;
                //}
            }
            world.pushBall(playerNumber, ballArray.get(bestDistanceBall).velocity);
        }

    }

    @Override
    public void makeDatabase() {

    }

    @Override
    public LinkedList<String> ouputDatabase() {
        return null;
    }

    @Override
    public void loadDatabase(LinkedList<String> input) {

    }
}
