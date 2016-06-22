package CrazyGolf.Bot.Brutefinder;

import CrazyGolf.Bot.BotInterface;
import CrazyGolf.PhysicsEngine.Objects.Parts.Ball;
import CrazyGolf.PhysicsEngine.Physics12.World;
import CrazyGolf.PhysicsEngine.Physics3.Physics;
import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by pmmde on 4/29/2016.
 */
public class Brutefinder implements BotInterface{
    public int amountDirections=12;
    public int amountPowers=4;

    public int amountDirectionsHighRes=amountDirections*3;
    public int amountPowersHighRes=amountPowers*3;

    private boolean riskyModus = false;
    private boolean maxModus = false;
    private int maxCorrectorDistance = 60;
    private double correctorChange = 10;
    private int amountErrorChecks = 4;
    private double pushLimitDatabase=1;

    //#################

    private int GS=20;

    //private World world;
    private Physics physics;

    public Node[][][] nodes;

    private int endI;
    private int endJ;
    private int endK;

    private int xOffset=0;
    private int yOffset=0;
    private int zOffset=0;

    private int amountNodes=0;
    private int amountConnections=0;
    private int amountOfNotReplicableConnections=0;

    @Override
    public void init(Physics p) {
        physics=p;
    }

    @Override public void calcNextShot(int playerNumber) {
        double bestPlace=-1;
        int bestDir=0;
        int bestPow=0;

        if(World.DEBUG)System.out.println("Brutefinder: Start finding best push");

        double dirStep = 2 * Math.PI / (double) amountDirectionsHighRes;
        double powStep = (World.maxPower / (double) amountPowersHighRes);
        for (int l = 0; l < amountDirectionsHighRes; l++) {
            for (int m = 0; m < amountPowersHighRes; m++) {
                ArrayList<Ball> balls = new ArrayList<>();
                for(int i=0;i<physics.getAmountBalls();i++) {
                    balls.add(new Ball(World.ballSize, physics.getBall(i).place));
                }
                balls.get(playerNumber).velocity = new Point3D(Math.cos(l * dirStep), Math.sin(l * dirStep), 0).multiply((m + 1) * powStep).add(physics.getBall(playerNumber).velocity);
                int velocityCounter = 0;
                int totalCounter=0;
                boolean outOfWorld = false;
                //if(World.DEBUG)System.out.print("Brutefinder: " + l + ";" + m+" Start ");
                while (velocityCounter < 20) {
                    totalCounter++;
                    physics.stepSimulated(balls,true,true);
                    velocityCounter++;
                    for(int i=0;i<balls.size();i++){
                        if (balls.get(i).velocity.magnitude() > 1.5 && balls.get(i).place.getZ() > -100) {
                            velocityCounter = 0;
                        }
                    }
                    if (balls.get(playerNumber).place.getZ() < -100 || totalCounter > 1000) {
                        outOfWorld = true;
                        velocityCounter = 20;
                    }
                }
                //if(World.DEBUG)System.out.print(+totalCounter+" Stop");
                int newi=(int)Math.round(balls.get(playerNumber).place.getX()/GS)+xOffset;
                int newj=(int)Math.round(balls.get(playerNumber).place.getY()/GS)+yOffset;
                int newk=(int)Math.round(balls.get(playerNumber).place.getZ()/GS)+zOffset;
                if (!outOfWorld) {
                    if(newi>=0&&newi<nodes.length && newj>=0&&newj<nodes[newi].length && newk>=0&&newk<nodes[newi][newj].length) {
                        if(nodes[newi][newj][newk]!=null && nodes[newi][newj][newk].minPath>-0.5) {
                            if(nodes[newi][newj][newk].minPath>0.5) {
                                double newBestPlace = getMinPath(newi,newj,newk,2);//;
                                if(newBestPlace>-0.5 && getCheckLaunch(l,m ,playerNumber,dirStep,powStep,amountErrorChecks)>-0.5) {
                                    //double minPath = getMinPath(newi, newj, newk, 0);
                                    //Point3D center = new Point3D((newi - xOffset) * GS, (newj - yOffset) * GS, (newk - zOffset) * GS);
                                    //double distance = center.distance(balls.get(playerNumber).place) / (GS * 2);
                                    //double newBestPlace = minPath + distance;
                                    if (newBestPlace < bestPlace || bestPlace < -0.5) {
                                        bestPlace = newBestPlace;
                                        bestDir = l;
                                        bestPow = m;
                                    }
                                }
                            }else{
                                double power=((double)m/(double)amountPowers);
                                if (power < bestPlace || bestPlace < -0.5) {
                                    bestPlace = power;
                                    bestDir = l;
                                    bestPow = m;
                                    //if (World.DEBUG) System.out.print(" - Inside hole");
                                }
                            }
                            //if (World.DEBUG) System.out.println(" - Min Path to hole: "+newBestPlace);
                        }
                        else
                        {
                            //if (World.DEBUG) System.out.println(" - Grid not calculated...");
                        }
                    }
                    else
                    {
                        //if(World.DEBUG)System.out.println(" - Outside grid");
                    }
                } else {
                    //if(World.DEBUG)System.out.println(" - Outside World");
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

        physics.pushBall(playerNumber,push);
    }
    @Override public void makeDatabase(){
        nodes=new Node[100][100][40];

        if(World.DEBUG)System.out.println("Brutefinder: Start calculating bruteforce...");

        calcNodes();

        if(World.DEBUG)System.out.println("Brutefinder: Nodes: "+amountNodes+" | Connections: "+amountConnections+" | Connections removed: "+amountOfNotReplicableConnections);

        if(World.DEBUG)System.out.println("Brutefinder: Start calculating pathfinding...");

        findMinPath(nodes[endI][endJ][endK],0);

        if(World.DEBUG)System.out.println("Brutefinder: Done initializing");
    }
    @Override public ArrayList<String> ouputDatabase(){
        ArrayList<String> output = new ArrayList<>();

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
    @Override public void loadDatabase(ArrayList<String> input){
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

        endI=(int)Math.round(physics.getHolePosition().getX()/GS)+xOffset;
        endJ=(int)Math.round(physics.getHolePosition().getY()/GS)+yOffset;
        endK=(int)Math.round(physics.getHolePosition().getZ()/GS)+zOffset;
        nodes[endI][endJ][endK]=new Node(amountDirections,amountPowers);
        amountNodes++;

        ArrayList<Ball> balls = new ArrayList<>();
        double dirStep = 2 * Math.PI / (double) amountDirections;
        double powStep = (World.maxPower / (double) amountPowers)*pushLimitDatabase;

        int xg=(int)Math.round(physics.getStartPosition().getX()/GS)+xOffset;
        int yg=(int)Math.round(physics.getStartPosition().getY()/GS)+yOffset;
        int zg=(int)Math.round(physics.getStartPosition().getZ()/GS)+zOffset;
        if(nodes[xg][yg][zg]==null) {
            nodes[xg][yg][zg] = new Node(amountDirections, amountPowers);
            amountNodes++;
            for (int l = 0; l < amountDirections; l++) {
                for (int m = 0; m < amountPowers; m++) {
                    balls.add(new BrutefinderBall(World.ballSize, new Point3D((xg - xOffset) * GS, (yg - yOffset) * GS, (zg - zOffset) * GS), xg, yg, zg, l, m));
                    balls.get(balls.size() - 1).velocity = new Point3D(Math.cos(l * dirStep), Math.sin(l * dirStep), 0).multiply((m + 1) * powStep);
                }
            }
        }

        int frameCounter=0;
        while(balls.size()>0) {
            long t1 = System.currentTimeMillis();
            physics.stepSimulated(balls, false,false);
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
                    if(tBall.corrector || riskyModus) {
                        if(riskyModus || tBall.predictorLocation.distance(tBall.place)<maxCorrectorDistance) {
                            int xGrid = (int) (Math.round(tBall.place.getX() / GS) + xOffset);
                            int yGrid = (int) (Math.round(tBall.place.getY() / GS) + yOffset);
                            int zGrid = (int) (Math.round(tBall.place.getZ() / GS) + zOffset);
                            if(xGrid>0 && xGrid<nodes.length && yGrid>0 && yGrid<nodes[0].length && zGrid>0 && zGrid<nodes[0][0].length) {
                                nodes[tBall.i][tBall.j][tBall.k].forward[tBall.dir][tBall.pow] = nodes[xGrid][yGrid][zGrid];
                                amountConnections++;
                                if (nodes[xGrid][yGrid][zGrid] == null) {
                                    nodes[xGrid][yGrid][zGrid] = new Node(amountDirections, amountPowers);
                                    amountNodes++;
                                    nodes[xGrid][yGrid][zGrid].backward.add(nodes[tBall.i][tBall.j][tBall.k]);
                                    if (xGrid != endI || zGrid != endJ || zGrid != endK) {
                                        for (int l = 0; l < amountDirections; l++) {
                                            for (int m = 0; m < amountPowers; m++) {
                                                balls.add(new BrutefinderBall(World.ballSize, new Point3D((xGrid - xOffset) * GS, (yGrid - yOffset) * GS, (zGrid) * GS), xGrid, yGrid, zGrid, l, m));
                                                balls.get(balls.size() - 1).velocity = new Point3D(Math.cos(l * dirStep), Math.sin(l * dirStep), 0).multiply((m + 1) * powStep);
                                            }
                                        }
                                    }
                                } else {
                                    nodes[xGrid][yGrid][zGrid].backward.add(nodes[tBall.i][tBall.j][tBall.k]);
                                }
                            }
                        }else{
                            amountOfNotReplicableConnections++;
                        }
                    }else{
                        BrutefinderBall correctedBall = new BrutefinderBall(World.ballSize, new Point3D((tBall.i - xOffset) * GS, (tBall.j - yOffset) * GS, (tBall.k) * GS)
                                .add((Math.random()-0.5)*correctorChange,(Math.random()-0.5)*correctorChange,(Math.random()-0.5)*correctorChange),
                                tBall.i, tBall.j, tBall.k, tBall.dir, tBall.pow);
                        correctedBall.corrector=true;
                        correctedBall.velocity = new Point3D(Math.cos(tBall.dir * dirStep), Math.sin(tBall.dir * dirStep), 0).multiply((tBall.pow + 1) * powStep)
                                .add((Math.random()-0.5)*correctorChange,(Math.random()-0.5)*correctorChange,(Math.random()-0.5)*correctorChange);
                        correctedBall.predictorLocation=tBall.place;
                        balls.add(correctedBall);
                    }
                }else if (tBall.place.getZ() < -100) {
                    balls.remove(i);
                    i--;
                }else if(tBall.totalCounter>500 || tBall.place.getX()==Double.NaN || tBall.place.getY()==Double.NaN || tBall.place.getZ()==Double.NaN){
                    if(World.DEBUG)System.out.println("Brutefinder: Something went wrong: "+tBall.place+" - "+tBall.velocity);
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
    private double getMinPath(int i,int j,int k,int size){
        if(size==0){
            return nodes[i][j][k].minPath;
        }else {
            int counter = 0;
            double total = 0;
            if (i - 1 > 0 && nodes[i - 1][j][k] != null && nodes[i - 1][j][k].minPath > -0.5) {
                counter++;
                total += getMinPath(i - 1, j, k, size - 1);
            }
            if (i + 1 < nodes.length && nodes[i + 1][j][k] != null && nodes[i + 1][j][k].minPath > -0.5) {
                counter++;
                total += getMinPath(i + 1, j, k, size - 1);
            }
            if (j - 1 > 0 && nodes[i][j - 1][k] != null && nodes[i][j - 1][k].minPath > -0.5) {
                counter++;
                total += getMinPath(i, j - 1, k, size - 1);
            }
            if (j + 1 < nodes[0].length && nodes[i][j + 1][k] != null && nodes[i][j + 1][k].minPath > -0.5) {
                counter++;
                total += getMinPath(i, j + 1, k, size - 1);
            }
            if (k - 1 > 0 && nodes[i][j][k - 1] != null && nodes[i][j][k - 1].minPath > -0.5) {
                counter++;
                total += getMinPath(i, j, k - 1, size - 1);
            }
            if (k + 1 < nodes[0][0].length && nodes[i][j][k + 1] != null && nodes[i][j][k + 1].minPath > -0.5) {
                counter++;
                total += getMinPath(i, j, k + 1, size - 1);
            }
            if (counter > 0) {
                return 0.5 * (total / counter) + 0.5 * nodes[i][j][k].minPath;
            }
            else{
                return nodes[i][j][k].minPath;
            }
        }
    }
    private double getCheckLaunch(int l,int m ,int playerNumber,double dirStep,double powStep, int amount){
        double total=0;
        for(int j=0;j<amount;j++) {
            ArrayList<Ball> balls = new ArrayList<>();
            for (int i = 0; i < physics.getAmountBalls(); i++) {
                balls.add(new Ball(World.ballSize, physics.getBall(i).place));
            }
            balls.get(playerNumber).velocity = new Point3D(Math.cos(l * dirStep), Math.sin(l * dirStep), 0).multiply((m + 1) * powStep)
                    .add(physics.getBall(playerNumber).velocity)
                    .add((Math.random()-0.5)*correctorChange,(Math.random()-0.5)*correctorChange,(Math.random()-0.5)*correctorChange);;
            int velocityCounter = 0;
            int totalCounter = 0;
            boolean outOfWorld = false;
            while (velocityCounter < 20) {
                totalCounter++;
                physics.stepSimulated(balls, true, true);
                velocityCounter++;
                for (int i = 0; i < balls.size(); i++) {
                    if (balls.get(i).velocity.magnitude() > 1.5 && balls.get(i).place.getZ() > -100) {
                        velocityCounter = 0;
                    }
                }
                if (balls.get(playerNumber).place.getZ() < -100 || totalCounter > 1000) {
                    outOfWorld = true;
                    velocityCounter = 20;
                }
            }
            if(outOfWorld){
                return -1;
            }
            int newi=(int)Math.round(balls.get(playerNumber).place.getX()/GS)+xOffset;
            int newj=(int)Math.round(balls.get(playerNumber).place.getY()/GS)+yOffset;
            int newk=(int)Math.round(balls.get(playerNumber).place.getZ()/GS)+zOffset;
            if(newi>=0&&newi<nodes.length && newj>=0&&newj<nodes[newi].length && newk>=0&&newk<nodes[newi][newj].length) {
                if (nodes[newi][newj][newk] != null && nodes[newi][newj][newk].minPath > -0.5) {
                    if(maxModus){
                        if(total<nodes[newi][newj][newk].minPath){
                            total=nodes[newi][newj][newk].minPath;
                        }
                    }else {
                        total += nodes[newi][newj][newk].minPath;
                    }
                }else{
                    return -1;
                }
            }else{
                return -1;
            }
        }
        if(maxModus){
            return total;
        }else {
            return total / amount;
        }
    }

    public String getProgress(){
        return "# Nodes: "+amountNodes+" | # Connections: "+amountConnections;
    }
}
