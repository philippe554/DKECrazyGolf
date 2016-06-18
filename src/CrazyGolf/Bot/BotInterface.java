package CrazyGolf.Bot;

import CrazyGolf.PhysicsEngine.Physics12.World;
import CrazyGolf.PhysicsEngine.Physics3.Physics;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by pmmde on 4/11/2016.
 */
public interface BotInterface {
    void init(World w);
    void init(Physics p);
    void calcNextShot(int playerNumber);

    void makeDatabase();
    ArrayList<String> ouputDatabase();
    void loadDatabase(ArrayList<String> input);
}
