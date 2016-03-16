import javafx.geometry.Point3D;

/**
 * Created by pmmde on 3/16/2016.
 */
public class Player {
    World world;

    boolean inputFlag = true;
    int velocityZeroCounter = 0;

    int angle = 0;
    int angleUp = 0;
    int power = 10;

    Point3D pushVector;

    public Player()
    {
        world = new World();
        world.loadWorld("C:\\Users\\pmmde\\GD\\Projects\\Java\\Philippe\\Github storage\\Surface2\\CrazyGolf\\src\\Field1.txt");
        world.addHole(0,0,0,30,70,30);
    }

}
