package CrazyGolf.Bot.Brutefinder;

import java.util.ArrayList;

/**
 * Created by pmmde on 4/29/2016.
 */
public class Node {
    public Node[][] forward;
    public ArrayList<Node> backward;
    public int minPath=-1;

    public Node(int amountDirections,int amountPowers)
    {
        forward=new Node[amountDirections][amountPowers];
        backward= new ArrayList<>();
    }

    public Node(int tMinPath)
    {
        minPath=tMinPath;
    }
}
