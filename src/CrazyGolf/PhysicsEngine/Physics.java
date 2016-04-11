package CrazyGolf.PhysicsEngine;

/**
 * Created by pmmde on 4/8/2016.
 */
public interface Physics {
    void loadWorld(World w);
    void step(int subframes);
    void cleanUp();
}
