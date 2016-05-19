package CrazyGolf.Bot.Brutefinder;

import CrazyGolf.Bot.BotInterface;
import CrazyGolf.Game.Player;
import CrazyGolf.PhysicsEngine.Ball;
import CrazyGolf.PhysicsEngine.World;
import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by pmmde on 4/29/2016.
 */
public class Brutefinder implements BotInterface{
    public int amountDirections=12;
    public int amountPowers=5;

    public int amountDirectionsHighRes=40;
    public int amountPowersHighRes=10;

    private int GS=20;

    private int simCounter=0;
    private int nodeCounter=0;

    private World world;

    private Node[][][] nodes;

    private int endI;
    private int endJ;
    private int endK;

    private int xOffset=0;
    private int yOffset=0;
    private int zOffset=5;

    @Override
    public void init(World w) {
        world=w;
    }
    @Override
    public void calcNextShot(int playerNumber) {
        double bestPlace=-1;
        int bestDir=0;
        int bestPow=0;

        if(World.DEBUG)System.out.println("Brutefinder: Start finding best push");

        double dirStep = 2 * Math.PI / (double) amountDirectionsHighRes;
        double powStep = World.maxPower / (double) amountPowersHighRes;
        for (int l = 0; l < amountDirectionsHighRes; l++) {
            for (int m = 0; m < amountPowersHighRes; m++) {
                ArrayList<Ball> balls = new ArrayList<>();
                balls.add(new Ball(World.ballSize, world.getBallPosition(playerNumber)));
                balls.get(0).velocity = new Point3D(Math.cos(l * dirStep), Math.sin(l * dirStep), 0).multiply((m + 1) * powStep).add(world.getBallVelocity(playerNumber));
                int velocityCounter = 0;
                int totalCounter=0;
                boolean outOfWorld = false;
                if(World.DEBUG)System.out.print("Brutefinder: " + l + ";" + m+" Start ");
                while (velocityCounter < 20) {
                    totalCounter++;
                    world.stepSimulated(balls,true);
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
                int newi=(int)(balls.get(0).place.getX()/GS)+xOffset;
                int newj=(int)(balls.get(0).place.getY()/GS)+yOffset;
                int newk=(int)(balls.get(0).place.getZ()/GS)+zOffset;
                if (!outOfWorld) {
                    if(newi>=0&&newi<nodes.length && newj>=0&&newj<nodes[newi].length && newk>=0&&newk<nodes[newi][newj].length) {
                        if(nodes[newi][newj][newk]!=null && nodes[newi][newj][newk].minPath>-0.5) {
                            Point3D center = new Point3D((newi-xOffset) * GS, (newj-yOffset) * GS, (newk-zOffset) * GS);
                            double distance = center.distance(balls.get(0).place)/(GS*2);
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

        if(bestPlace<-0.5)
        {
            if(World.DEBUG)System.out.println("Brutefinder: All simulated shots are outside calculated grid");
        }

        Point3D push = new Point3D(Math.cos(bestDir * 2 * Math.PI / amountDirectionsHighRes)
                , Math.sin(bestDir * 2 * Math.PI / amountDirectionsHighRes), 0).multiply((bestPow + 1) * World.maxPower / amountPowersHighRes);

        if(World.DEBUG)System.out.println("Brutefinder: Push "+bestDir+";"+bestPow+": Successful: " + push+" - Expected path to hole: "+ bestPlace);

        world.pushBall(playerNumber,push);
    }
    @Override
    public void makeDatabase(){
        nodes=new Node[100][100][40];

        if(World.DEBUG)System.out.println("Brutefinder: Start calculating bruteforce...");

        calcNodes();

        if(World.DEBUG)System.out.println("Brutefinder: Start calculating pathfinding...");

        findMinPath(nodes[endI][endJ][endK],0);

        if(World.DEBUG)System.out.println("Brutefinder: Done initializing");
    }
    @Override
    public LinkedList<String> ouputDatabase(){
        LinkedList<String> output = new LinkedList<>();

        output.add(Integer.toString(GS));
        output.add(Integer.toString(nodes.length)+";"+Integer.toString(nodes[0].length)+";"+Integer.toString(nodes[0][0].length));
        output.add(Integer.toString(xOffset)+";"+Integer.toString(yOffset)+";"+Integer.toString(zOffset));
        output.add(Integer.toString(amountDirections)+";"+Integer.toString(amountPowers));

        for(int i=0;i<nodes.length;i++) {
            for(int j=0;j<nodes[0].length;j++) {
                for(int k=0;k<nodes[0][0].length;k++) {
                    if(nodes[i][j][k]!=null) {
                        if (nodes[i][j][k].minPath > -0.5) {
                            output.add(Integer.toString(i) + ";" + Integer.toString(j) + ";" + Integer.toString(k) + ";" + Integer.toString(nodes[i][j][k].minPath));
                        }
                    }
                }
            }
        }

        return output;
    }
    @Override
    public void loadDatabase(LinkedList<String> input){
        GS=Integer.parseInt(input.get(0));
        String[] data = input.get(1).split(";");
        nodes = new Node[Integer.parseInt(data[0])][Integer.parseInt(data[1])][Integer.parseInt(data[2])];
        data = input.get(2).split(";");
        xOffset=Integer.parseInt(data[0]);
        yOffset=Integer.parseInt(data[1]);
        zOffset=Integer.parseInt(data[2]);
        data = input.get(3).split(";");
        amountDirections=Integer.parseInt(data[0]);
        amountPowers=Integer.parseInt(data[1]);
        for(int i=4;i<input.size();i++)
        {
            data = input.get(i).split(";");
            int x=Integer.parseInt(data[0]);
            int y=Integer.parseInt(data[1]);
            int z=Integer.parseInt(data[2]);
            int minPath=Integer.parseInt(data[3]);
            nodes[x][y][z] = new Node(minPath);
        }
    }

    private void calcNodes() {
        nodes=new Node[100][100][20];

        endI=(int)(world.getHolePosition().getX()/GS)+xOffset;
        endJ=(int)(world.getHolePosition().getY()/GS)+yOffset;
        endK=(int)(world.getHolePosition().getZ()/GS)+zOffset;
        nodes[endI][endJ][endK]=new Node(amountDirections,amountPowers);

        ArrayList<Ball> balls = new ArrayList<>();
        double dirStep = 2 * Math.PI / (double) amountDirections;
        double powStep = World.maxPower / (double) amountPowers;
        for(int i=0;i<world.getAmountBalls();i++)
        {
            int xGrid=(int)(world.getBallPosition(i).getX()/GS)+xOffset;
            int yGrid=(int)(world.getBallPosition(i).getY()/GS)+yOffset;
            int zGrid=(int)(world.getBallPosition(i).getZ()/GS)+zOffset;
            if(nodes[xGrid][yGrid][zGrid]==null) {
                nodes[xGrid][yGrid][zGrid] = new Node(amountDirections,amountPowers);
                for (int l = 0; l < amountDirections; l++) {
                    for (int m = 0; m < amountPowers; m++) {
                        balls.add(new BrutefinderBall(World.ballSize, new Point3D((xGrid - xOffset) * GS, (yGrid - yOffset) * GS, (zGrid - zOffset) * GS), xGrid, yGrid, zGrid, l, m));
                        balls.get(balls.size() - 1).velocity = new Point3D(Math.cos(l * dirStep), Math.sin(l * dirStep), 0).multiply((m + 1) * powStep);
                    }
                }
            }
        }
        int frameCounter=0;
        while(balls.size()>0) {
            long t1 = System.currentTimeMillis();
            world.stepSimulated(balls, false);
            long t2 = System.currentTimeMillis();
            for (int i = 0; i < balls.size(); i++) {
                BrutefinderBall tBall = (BrutefinderBall) balls.get(i);
                tBall.totalCounter++;
                if (tBall.velocity.magnitude() < 1.5) {
                    tBall.velocityCounter++;
                } else {
                    tBall.velocityCounter = 0;
                }
                if (tBall.velocityCounter == 20) {
                    balls.remove(i);
                    i--;
                    int xGrid=(int)(tBall.place.getX()/GS)+xOffset;
                    int yGrid=(int)(tBall.place.getY()/GS)+yOffset;
                    int zGrid=(int)(tBall.place.getZ()/GS)+zOffset;
                    nodes[tBall.i][tBall.j][tBall.k].forward[tBall.dir][tBall.pow]=nodes[xGrid][yGrid][zGrid];
                    if(nodes[xGrid][yGrid][zGrid]==null) {
                        nodes[xGrid][yGrid][zGrid]=new Node(amountDirections,amountPowers);
                        nodes[xGrid][yGrid][zGrid].backward.add(nodes[tBall.i][tBall.j][tBall.k]);
                        if (xGrid != endI || zGrid != endJ || zGrid != endK) {
                            for (int l = 0; l < amountDirections; l++) {
                                for (int m = 0; m < amountPowers; m++) {
                                    balls.add(new BrutefinderBall(World.ballSize, new Point3D((xGrid - xOffset) * GS, (yGrid - yOffset) * GS, (zGrid - zOffset) * GS), xGrid, yGrid, zGrid, l, m));
                                    balls.get(balls.size() - 1).velocity = new Point3D(Math.cos(l * dirStep), Math.sin(l * dirStep), 0).multiply((m + 1) * powStep);
                                }
                            }
                        }
                    }
                    else
                    {
                        nodes[xGrid][yGrid][zGrid].backward.add(nodes[tBall.i][tBall.j][tBall.k]);
                    }
                }
                if (tBall.place.getZ() < -100) {
                    balls.remove(i);
                    i--;
                }
            }
            frameCounter++;
            System.out.println(frameCounter+"/"+balls.size()+"/"+(t2-t1));
        }
    }
    private void findMinPath(Node n,int counter){
        n.minPath=counter;
        for(int i=0;i<n.backward.size();i++) {
            if(n.backward.get(i).minPath>(counter+1) || n.backward.get(i).minPath==-1) {
                findMinPath(n.backward.get(i), counter+1);
            }
        }
    }
}
