package CrazyGolf.Bot;

import CrazyGolf.Game.Player;
import CrazyGolf.PhysicsEngine.World;

import java.util.LinkedList;

/**
 * Created by pmmde on 4/11/2016.
 */
public interface BotInterface {
    void init(World w);
    void calcNextShot(int playerNumber);

    void makeDatabase();
    LinkedList<String> ouputDatabase();
    void loadDatabase(LinkedList<String> input);
}
