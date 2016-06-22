package CrazyGolf.Bot.RandomBot;

import CrazyGolf.Bot.BotInterface;
import CrazyGolf.PhysicsEngine.Physics3.Physics;
import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.LinkedList;

import java.lang.Math;

/**
 * Created by Maxim on 26-5-2016.
 */
public class RandomBot implements BotInterface {

    Physics world;
    double scalar =80;

    @Override
    public void init(Physics p) {
        world=p;
    }

    @Override
    public void calcNextShot(int playerNumber) {
        double i = (Math.random() * scalar) - scalar/2;
        double j = (Math.random() * scalar) - scalar/2;
        Point3D randomShot = new Point3D(i,j,0);
        world.pushBall(playerNumber, randomShot);
    }

    @Override
    public void makeDatabase() {

    }

    @Override
    public ArrayList<String> ouputDatabase() {
        return null;
    }

    @Override
    public void loadDatabase(ArrayList<String> input) {

    }
}
