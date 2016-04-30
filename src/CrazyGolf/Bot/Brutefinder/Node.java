package CrazyGolf.Bot.Brutefinder;

import java.util.ArrayList;

/**
 * Created by pmmde on 4/29/2016.
 */
public class Node {
    public Connection[][] forwardConnections;
    public ArrayList<Connection> backwardConnections;
    public boolean initDone=false;
    public int minPath=-1;

    public Node()
    {
        forwardConnections=new Connection[Brutefinder.amountDirections][Brutefinder.amountPowers];
        backwardConnections= new ArrayList<>();
    }
}
