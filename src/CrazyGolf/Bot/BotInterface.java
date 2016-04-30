package CrazyGolf.Bot;

import CrazyGolf.Game.Player;
import CrazyGolf.PhysicsEngine.World;

/**
 * Created by pmmde on 4/11/2016.
 */
public interface BotInterface {
    void init(World w);
    void calcNextShot(Player p);
}
