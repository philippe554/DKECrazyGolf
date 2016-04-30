package CrazyGolf.Bot.Brutefinder;

import CrazyGolf.Bot.BotInterface;
import CrazyGolf.Game.Player;
import CrazyGolf.PhysicsEngine.Ball;
import CrazyGolf.PhysicsEngine.World;
import javafx.geometry.Point3D;

import java.util.ArrayList;

/**
 * Created by pmmde on 4/29/2016.
 */
public class Brutefinder implements BotInterface{
    public static final int amountDirections=12;
    public static final int amountPowers=5;

    public static final int amountDirectionsHighRes=40;
    public static final int amountPowersHighRes=10;

    private int simCounter=0;
    private int nodeCounter=0;

    World world;

    public Node[][][] nodes;

    private int endI;
    private int endJ;
    private int endK;

    private int xOffset=0;
    private int yOffset=0;
    private int zOffset=5;

    @Override
    public void init(World w) {
        world=w;
        nodes=new Node[100][100][20];

        endI=(int)(world.hole.getX()/World.GS);
        endJ=(int)(world.hole.getY()/World.GS);
        endK=(int)(world.hole.getZ()/World.GS)+5;
        nodes[endI][endJ][endK]=new Node();

        if(World.DEBUG)System.out.println("Brutefinder: Start calculating bruteforce...");

        for(int i=0;i<world.balls.size();i++) {
            calcNode((int)(world.balls.get(i).place.getX()/World.GS)+xOffset,(int)(world.balls.get(i).place.getY()/World.GS)+yOffset,(int)(world.balls.get(i).place.getZ()/World.GS)+zOffset);
        }

        if(World.DEBUG)System.out.println("Brutefinder: Start calculating pathfinding...");

        findMinPath(nodes[endI][endJ][endK],0);

        if(World.DEBUG)System.out.println("Brutefinder: Done initializing");

    }

    @Override
    public void calcNextShot(Player p) {
        double bestPlace=-1;
        int bestDir=0;
        int bestPow=0;

        if(World.DEBUG)System.out.println("Brutefinder: Start finding best push");

        double dirStep = 2 * Math.PI / (double) amountDirectionsHighRes;
        double powStep = World.maxPower / (double) amountPowersHighRes;
        for (int l = 0; l < amountDirectionsHighRes; l++) {
            for (int m = 0; m < amountPowersHighRes; m++) {
                ArrayList<Ball> balls = new ArrayList<>();
                balls.add(new Ball(World.ballSize, world.balls.get(p.ballId).place.add(0,0,0)));
                balls.get(0).velocity = new Point3D(Math.cos(l * dirStep), Math.sin(l * dirStep), 0).multiply((m + 1) * powStep).add(world.balls.get(p.ballId).velocity);
                int velocityCounter = 0;
                int totalCounter=0;
                boolean outOfWorld = false;
                if(World.DEBUG)System.out.print("Brutefinder: " + l + ";" + m+" Start ");
                while (velocityCounter < 20) {
                    totalCounter++;
                    world.stepSimulated(balls);
                    if (balls.get(0).velocity.magnitude() < 1.5) {
                        velocityCounter++;
                    } else {
                        velocityCounter = 0;
                    }
                    if (balls.get(0).place.getZ() < -100 || totalCounter > 1000) {
                        outOfWorld = true;
                        velocityCounter = 20;
                    }
                }
                if(World.DEBUG)System.out.print(+totalCounter+" Stop");
                int newi=(int)(balls.get(0).place.getX()/World.GS)+xOffset;
                int newj=(int)(balls.get(0).place.getY()/World.GS)+yOffset;
                int newk=(int)(balls.get(0).place.getZ()/World.GS)+zOffset;
                if (!outOfWorld) {
                    if(newi>=0&&newi<nodes.length && newj>=0&&newj<nodes[newi].length && newk>=0&&newk<nodes[newi][newj].length) {
                        if(nodes[newi][newj][newk]!=null && nodes[newi][newj][newk].minPath>-0.5) {
                            Point3D center = new Point3D((newi-xOffset) * World.GS, (newj-yOffset) * World.GS, (newk-zOffset) * World.GS);
                            double distance = center.distance(balls.get(0).place)/(World.GS*2);
                            double newBestPlace=nodes[newi][newj][newk].minPath+distance;
                            if (newBestPlace < bestPlace || bestPlace < -0.5) {
                                bestPlace = newBestPlace;
                                bestDir = l;
                                bestPow = m;
                                if (nodes[newi][newj][newk].minPath == 0) {
                                    if (World.DEBUG) System.out.print(" - Inside hole");
                                }
                            }
                            if (World.DEBUG) System.out.println(" - Min Path to hole: "+newBestPlace);
                        }
                        else
                        {
                            if (World.DEBUG) System.out.println(" - Grid not calculated...");
                        }
                    }
                    else
                    {
                        if(World.DEBUG)System.out.println(" - Outside grid");
                    }
                } else {
                    if(World.DEBUG)System.out.println(" - Outside World");
                }
            }
        }

        Point3D push = new Point3D(Math.cos(bestDir * 2 * Math.PI / amountDirectionsHighRes)
                , Math.sin(bestDir * 2 * Math.PI / amountDirectionsHighRes), 0).multiply((bestPow + 1) * World.maxPower / amountPowersHighRes);

        if(World.DEBUG)System.out.println("Brutefinder: Push "+bestDir+";"+bestPow+": Successful: " + push+" - Expected path to hole: "+ bestPlace);

        world.pushBall(p.ballId,push);
    }

    private void calcNode(int i,int j,int k){
        if(i>=0&&i<nodes.length&&j>=0&&j<nodes[i].length&&k>=0&&k<nodes[i][j].length) {
            if(nodes[i][j][k]==null)
            {
                nodes[i][j][k] = new Node();
            }
            if (!nodes[i][j][k].initDone) {
                nodes[i][j][k].initDone=true;
                nodeCounter++;
                double dirStep = 2 * Math.PI / (double) amountDirections;
                double powStep = World.maxPower / (double) amountPowers;
                for (int l = 0; l < amountDirections; l++) {
                    for (int m = 0; m < amountPowers; m++) {
                        if(World.DEBUG)System.out.print("Brutefinder: "+simCounter +"/"+(simCounter/(amountDirections*amountPowers))+"/"+nodeCounter+ " : " + i + ";" + j + ";" + k + ";" + l + ";" + m+" Start ");
                        ArrayList<Ball> balls = new ArrayList<>();
                        balls.add(new Ball(World.ballSize, new Point3D((i-xOffset) * World.GS, (j-yOffset) * World.GS, (k-zOffset) * World.GS)));
                        balls.get(0).velocity = new Point3D(Math.cos(l * dirStep), Math.sin(l * dirStep), 0).multiply((m + 1) * powStep);
                        int velocityCounter = 0;
                        int totalCounter=0;
                        boolean outOfWorld = false;
                        while (velocityCounter < 20) {
                            totalCounter++;
                            world.stepSimulated(balls);
                            if (balls.get(0).velocity.magnitude() < 1.5) {
                                velocityCounter++;
                            } else {
                                velocityCounter = 0;
                            }
                            if (balls.get(0).place.getZ() < -100 || totalCounter > 1000) {
                                outOfWorld = true;
                                velocityCounter = 20;
                            }
                        }
                        if(World.DEBUG)System.out.print(+totalCounter+" Stop");
                        simCounter++;
                        int newi=(int)(balls.get(0).place.getX()/World.GS)+xOffset;
                        int newj=(int)(balls.get(0).place.getY()/World.GS)+yOffset;
                        int newk=(int)(balls.get(0).place.getZ()/World.GS)+zOffset;
                        if (!outOfWorld) {
                            if(newi>=0&&newi<nodes.length && newj>=0&&newj<nodes[newi].length && newk>=0&&newk<nodes[newi][newj].length) {
                                if (nodes[newi][newj][newk] == null) {
                                    nodes[newi][newj][newk] = new Node();
                                }
                                Connection c = new Connection(l,m,nodes[i][j][k],nodes[newi][newj][newk]);
                                nodes[i][j][k].forwardConnections[l][m]=c;
                                nodes[newi][newj][newk].backwardConnections.add(c);
                                if (newi == endI && newj == endJ && newk == endK) {
                                    if(World.DEBUG)System.out.println(" - In Hole");
                                }else{
                                    if(World.DEBUG)System.out.println();
                                    calcNode(newi,newj,newk);
                                }
                            }
                            else
                            {
                                if(World.DEBUG)System.out.println(" - Outside grid");
                            }
                        } else {
                            if(World.DEBUG)System.out.println(" - Outside World");
                        }
                    }
                }
            }
        }
        else
        {
            if(World.DEBUG)System.out.println("Brutefinder: ball outside grid: " + i + ";" + j + ";" + k);
        }
    }
    private void findMinPath(Node n,int counter){
        n.minPath=counter;
        for(int i=0;i<n.backwardConnections.size();i++) {
            if(n.backwardConnections.get(i).start.minPath>(counter+1) || n.backwardConnections.get(i).start.minPath==-1) {
                findMinPath(n.backwardConnections.get(i).start, counter+1);
            }
        }
    }

    public void makeDatabase(){

    }
    public void ouputDatabase(){

    }
    public void loadDatabase(){

    }
}
