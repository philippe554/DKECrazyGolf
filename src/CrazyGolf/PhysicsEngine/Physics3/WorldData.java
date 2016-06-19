package CrazyGolf.PhysicsEngine.Physics3;

import CrazyGolf.PhysicsEngine.Matrix;
import CrazyGolf.PhysicsEngine.Objects.Native.*;
import CrazyGolf.PhysicsEngine.Objects.Parts.Ball;
import CrazyGolf.PhysicsEngine.Objects.Terain.SimplexNoise;
import CrazyGolf.PhysicsEngine.Objects.Terain.Terrain;
import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by pmmde on 6/9/2016.
 */
public class WorldData implements World,Physics{
    public final int precision=4;
    public final double gravity=1;

    protected ArrayList<WorldObject> objects;
    protected Terrain terrain;
    protected ArrayList<Ball> balls;
    protected ArrayList<Ball> pointerToBalls;
    protected Ball start;
    protected Point3D hole;

    public Queue<WorldObject> newObjects;
    public Queue<WorldObject> updatedObjects;
    public Queue<Integer> deletedObjects;

    private int time = 0;
    private SimplexNoise wind;

    private static final int amountOfThreads=4;

    private boolean terainPhysics=false;

    private boolean borderAdded=false;

    public WorldData(){
        objects=new ArrayList<>();
        terrain=new Terrain(1631365,this);
        balls=new ArrayList<>();
        pointerToBalls=balls;
        newObjects = new LinkedList<>();
        updatedObjects = new LinkedList<>();
        deletedObjects = new LinkedList<>();
        wind=new SimplexNoise(4654654);
        start = new Ball(20, new Point3D(0,0,0));
        hole = new Point3D(0,0,0);
        terrain.run();
    }

    @Override public void step(boolean useBallBallCollision) {
        time++;
        for(Ball ball:balls){
            ball.oldPlace=ball.place;
            ball.friction=0;
        }
        if(useBallBallCollision){
            stepWithCollision();
        }else
        {
            WorkThreadWithoutBallBallCollision RT[]= new WorkThreadWithoutBallBallCollision[amountOfThreads];
            for(int i=0;i<amountOfThreads;i++) {
                RT[i] = new WorkThreadWithoutBallBallCollision((int)(balls.size()*i / amountOfThreads), (int) (balls.size()* (i+1) / amountOfThreads));
                RT[i].start();
            }
            for(int i=0;i<amountOfThreads;i++) {
                try {
                    RT[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        for(Ball ball:balls){
            if(ball.place.subtract(ball.oldPlace).magnitude()<Ball.minVelocity){
                ball.zeroCounter++;
                ball.place=ball.oldPlace;
            }else{
                ball.zeroCounter=0;
            }

            double windPowerX = wind.noise(ball.place.getX()*0.0001,ball.place.getY()*0.0001,time*0.0002)*0.05;
            double windPowerY = wind.noise(ball.place.getX()*0.0001+100,ball.place.getY()*0.0001+100,time*0.0002)*0.05;
            ball.windVector = new Point3D(windPowerX,windPowerY,0);
            ball.acceleration = ball.acceleration.add(ball.windVector);

            if (ball.friction > 0.001f) {
                if (ball.velocity.magnitude() > ball.friction) {
                    ball.velocity = ball.velocity.subtract(ball.velocity.normalize().multiply(ball.friction));
                } else {
                    ball.velocity = new Point3D(0, 0, 0);
                }
            } else {
                ball.velocity = ball.velocity.multiply(0.995);
            }
        }
    }
    @Override public void stepSimulated(ArrayList<Ball> simBalls, boolean useBallBallCollision) {
        ArrayList<Ball> original=balls;
        balls=simBalls;
        time--;
        step(useBallBallCollision);
        balls=original;
    }

    @Override public Point3D getHolePosition() {
        return hole;
    }
    @Override public Point3D getStartPosition() {
        return start.place;
    }
    @Override public int getAmountWorldObjects() {
        return objects.size();
    }
    @Override public WorldObject getWorldObject(int i) {
        return objects.get(i);
    }
    @Override public void updateTerain(Point3D center) {
        terrain.updateTerain(center);
        terrain.run();
    }
    @Override public int getAmountBalls() {
        return pointerToBalls.size();
    }
    @Override public Ball getBall(int i) {
        return pointerToBalls.get(i);
    }
    @Override public void pushBall(int i, Point3D dir) {
        balls.get(i).velocity= balls.get(i).velocity
                .add(dir.add(Math.random()-0.5,Math.random()-0.5,Math.random()-0.5));
    }
    @Override public boolean checkBallInHole(int i) {
        if (hole.distance(balls.get(i).place) < (balls.get(i).size)) {
            return true;
        }
        return false;
    }
    @Override public boolean ballStoppedMoving(int i) {
        if(balls.get(i).zeroCounter>=Ball.thresholdCounter){
            return true;
        }
        return false;
    }
    @Override public void addNewBall() {
        double moveup=0;
        boolean noCollision=false;
        while(!noCollision){
            noCollision=true;
            for(int i=0;i<balls.size();i++){
                if(start.place.add(0,0,moveup).distance(balls.get(i).place)<start.size+balls.get(i).size){
                    noCollision=false;
                }
            }
            if(!noCollision){
                moveup++;
            }
        }
        balls.add(new Ball(start.size,start.place.add(0,0,moveup)));
    }

    @Override public WorldObject getNextNewObject() {
        return newObjects.poll();
    }
    @Override public WorldObject getNextUpdateObject() {
        return updatedObjects.poll();
    }
    @Override public Integer getNextRemoveObject() {
        return deletedObjects.poll();
    }

    protected void stepWithCollision(){
        double maxV = -1;
        double ballSize = 0;
        for(Ball ball:balls) {
            if (ball.velocity.magnitude() > maxV) {
                maxV = ball.velocity.magnitude();
                ballSize = ball.size;
            }
        }
        int subframes=((int) (maxV / ballSize * 1.1*precision) + 1);
        double subframeInv = 1.0 / (double)(subframes);
        for (int l = 0; l < subframes; l++) {
            ballCollisionComplete();
            for(Ball ball:balls) {
                /*ball.acceleration = ball.acceleration.add(0, 0, -gravity*subframeInv); //gravity
                ball.velocity = ball.velocity.add(ball.acceleration);
                ball.place = ball.place.add(ball.velocity.multiply(subframeInv));
                ball.acceleration = new Point3D(0, 0, 0);

                for(int j=0;j<objects.size();j++){
                    objects.get(j).applyCollision(ball,subframeInv);
                }
                if(terainPhysics)terrain.applyCollision(ball,subframeInv);*/
                ball.acceleration = ball.acceleration.add(0, 0, -gravity*subframeInv); //gravity
                ball.normalTotal=new Point3D(0,0,0);
                ball.normalCounter=0;

                for(int j=0;j<objects.size();j++){
                    objects.get(j).applyCollision(ball,subframeInv);
                }
                if(terainPhysics)terrain.applyCollision(ball,subframeInv);

                if(ball.normalCounter>0){
                    Point3D normal = ball.normalTotal.multiply(1.0/ball.normalCounter).normalize();
                    ball.acceleration = ball.acceleration.add(normal.multiply(ball.velocity.dotProduct(normal) * -1.8));
                }
                ball.velocity = ball.velocity.add(ball.acceleration);
                ball.place = ball.place.add(ball.velocity.multiply(subframeInv));
                ball.acceleration = new Point3D(0, 0, 0);
            }
        }
    }

    protected boolean ballCollision(int i, int j){
        boolean result=false;

        Point3D distanceVector = balls.get(i).place.subtract(balls.get(j).place);

        if(distanceVector.magnitude()<(balls.get(i).size+balls.get(j).size))
        {
            Point3D l12 = balls.get(j).place.subtract(balls.get(i).place).normalize();
            Point3D l21 = balls.get(i).place.subtract(balls.get(j).place).normalize();

            Point3D v1=l12.multiply(balls.get(i).velocity.dotProduct(l12));
            Point3D v2=l21.multiply(balls.get(j).velocity.dotProduct(l21));

            Point3D v1p=balls.get(i).velocity.subtract(v1);
            Point3D v2p=balls.get(j).velocity.subtract(v2);

            double m1=balls.get(i).mass;
            double m2=balls.get(j).mass;

            balls.get(i).velocity=v1.multiply (m1-m2).add(v2.multiply(2 * m2)).multiply(1/(m1+m2)).add(v1p);
            balls.get(j).velocity=v2.multiply (m2-m1).add(v1.multiply(2 * m1)).multiply(1/(m1+m2)).add(v2p);

            balls.get(i).place=balls.get(i).place.add(balls.get(i).velocity);
            balls.get(j).place=balls.get(j).place.add(balls.get(j).velocity);
        }

        return result;
    }
    protected void ballCollisionComplete() {
        for(int i=0;i<balls.size();i++)
        {
            for(int j=i+1;j<balls.size();j++)
            {
                ballCollision(i,j);
            }
        }
    }

    @Override public void load(ArrayList<String> data) {
        String copyEnd="";
        boolean copyLock=false;
        Class copyObject = null;
        boolean nativeClassError=false;
        LinkedList<String>copyData = null;
        int sort = 0;
        for (int i = 0; i < data.size(); i++) {
            if(!copyLock) {
                if (data.get(i).equals("world")) {
                    sort = 0;
                } else if (data.get(i).equals("ObjectStart")) {
                    sort = 1;
                    copyLock=true;
                    copyEnd="ObjectEnd-"+data.get(i+1);
                    copyData=new LinkedList<>();
                    copyData.add(data.get(i+1));
                    i++;
                } else if (data.get(i).equals("ObjectStartNative")) {
                    sort = 2;
                    copyLock=true;
                    copyEnd="ObjectEndNative-"+data.get(i+1);
                    nativeClassError=false;
                    copyData=new LinkedList<>();
                    copyData.add(data.get(i+1));
                    try {
                        copyObject=Class.forName(data.get(i+2));
                    } catch (ClassNotFoundException e) {
                        nativeClassError=true;
                        System.out.println("Class not found (1): "+data.get(i+2));
                    }
                    i+=2;
                }else if(data.get(i).equals("balls")) {
                    sort=3;
                }else if(data.get(i).equals("holes")) {
                    sort=4;
                }else{
                    if(sort==0){

                    }else if(sort==3) {
                        String[] split = data.get(i).split(";");
                        if (split.length == 5) {
                            start = new Ball(20, new Point3D(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
                        }
                    }
                    else if(sort==4){
                        String[] split = data.get(i).split(";");
                        if (split.length == 3) {
                            hole = new Point3D(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                        }
                    }
                }
            }else{
                if(data.get(i).equals(copyEnd)){
                    if(sort==1){
                        copyLock=false;
                        WorldObject wo = new WorldObject(this);
                        wo.load(copyData);
                        wo.setup(true);
                        objects.add(wo);
                        newObjects.offer(wo);
                    }else if(sort==2)
                    {
                        copyLock=false;
                        if(!nativeClassError && copyObject!=null){
                            Object obj=null;
                            try {
                                obj = copyObject.newInstance();
                            } catch (InstantiationException e) {
                                System.out.println("Class not found (2): "+copyObject.getName());
                                nativeClassError=true;
                            } catch (IllegalAccessException e) {
                                System.out.println("Class not found (3): "+copyObject.getName());
                                nativeClassError=true;
                            }
                            if(!nativeClassError && obj!=null){
                                if(obj instanceof WorldObject){
                                    WorldObject wo = (WorldObject)obj;
                                    wo.load(copyData);
                                    wo.setup(true);
                                    objects.add(wo);
                                    newObjects.offer(wo);
                                }else{
                                    System.out.println("Class is not an instance of WorldObject: "+copyObject.getName());
                                }
                            }
                        }
                    }
                }else{
                    copyData.add(data.get(i));
                }
            }
        }
    }
    @Override public void load(String[][] data, double gs, Point3D offset) {
        boolean isEmpty=true;
        for(int i=0;i<data.length;i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (!data[i][j].equals("E") && (!data[i][j].equals("Q"))) {
                    isEmpty = false;
                }
            }
        }
        if(!isEmpty) {
            WorldObject wo = new WorldObject(this);
            if (!borderAdded) {
                borderAdded = true;
                Point3D borderOffset=new Point3D(offset.getX(),offset.getY(),0);
                wo.subObjects.add(new FieldBorder(borderOffset, Matrix.getRotatoinMatrix(0, 0, 0), gs, data, this,offset.getZ()));
            }
            boolean[][] alreadyConverted = new boolean[data.length][data[0].length];
            for (int i = 0; i < alreadyConverted.length; i++) {
                for (int j = 0; j < alreadyConverted[i].length; j++) {
                    alreadyConverted[i][j] = false;
                }
            }
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    if (!alreadyConverted[i][j]) {
                        if (data[i][j].equals("B")) {
                            start = new Ball(20, new Point3D(i * gs + gs, j * gs + gs, 20).add(offset));
                            for (int k = 0; k < 2; k++) {
                                for (int l = 0; l < 2; l++) {
                                    if ((i + k) < data.length && (j + l) < data[i + k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < alreadyConverted.length; i++) {
                for (int j = 0; j < alreadyConverted[i].length; j++) {
                    alreadyConverted[i][j] = false;
                }
            }
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    if (!alreadyConverted[i][j]) {
                        if (data[i][j].equals("W")) {
                            wo.subObjects.add(addWall(data, alreadyConverted, i, j, gs, offset));
                        } else if (data[i][j].equals("F") || data[i][j].equals("B")) {
                            wo.subObjects.add(addGrass(data, alreadyConverted, i, j, gs, offset));
                        } else if (data[i][j].equals("S")) {
                            wo.subObjects.add(addSand(data, alreadyConverted, i, j, gs, offset));
                        } else if (data[i][j].equals("H")) {
                            wo.subObjects.add(new Hole(this, offset.add(i * gs + 1.5 * gs, j * gs + 1.5 * gs, 0), Matrix.getRotatoinMatrix(0, 0, 0), 30, 80, 30));
                            hole = offset.add(i * gs + 1.5 * gs, j * gs + 1.5 * gs, -80 + 20);
                            for (int k = 0; k < 3; k++) {
                                for (int l = 0; l < 3; l++) {
                                    if (i + k < data.length && j + l < data[i + k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        } else if (data[i][j].equals("L")) {
                            wo.subObjects.add(new Loop(this, offset.add(i * gs + 140, j * gs + 60, 140), Matrix.getRotatoinMatrix(0, 0, 0), 140, 60, 24, 25));
                            for (int k = 0; k < 14; k++) {
                                for (int l = 0; l < 6; l++) {
                                    if ((i + k) < data.length && (j + l) < data[i + k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        } else if (data[i][j].equals("C")) {
                            wo.subObjects.add(new Castle(this, offset.add(i * gs + 40, j * gs + 40, 0), Matrix.getRotatoinMatrix(0, 0, 0), 20, 40, 180));
                            for (int k = 0; k < 13; k++) {
                                for (int l = 0; l < 4; l++) {
                                    if ((i + k) < data.length && (j + l) < data[i + k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        } else if (data[i][j].equals("R")) {
                            wo.subObjects.add(new Bridge(this, offset.add(i * gs + 40, j * gs + 140, 0), Matrix.getRotatoinMatrix(0, 0, 0), 200, 50, 20, 80, 20));
                            for (int k = 0; k < 4; k++) {
                                for (int l = 0; l < 24; l++) {
                                    if ((i + k) < data.length && (j + l) < data[i + k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        } else if (data[i][j].equals("P")) {
                            wo.subObjects.add(new Pool(this, offset.add(i * gs, j * gs, 0), Matrix.getRotatoinMatrix(0, 0, 0), 280, 150, 24, 25));
                            for (int k = 0; k < 14; k++) {
                                for (int l = 0; l < 14; l++) {
                                    if ((i + k) < data.length && (j + l) < data[i + k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        } else if (data[i][j].equals("M")) {
                            wo.subObjects.add(addHill(data, i, j, gs, offset, "FBS"));
                            for (int k = 0; k < 4; k++) {
                                for (int l = 0; l < 4; l++) {
                                    if ((i + k) < data.length && (j + l) < data[i + k].length) {
                                        alreadyConverted[i + k][j + l] = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (wo.getAmountSubObjects() > 0) {
                newObjects.offer(wo);
                wo.setup(true);
                objects.add(wo);
            }
        }
    }
    @Override public ArrayList<String> save() {
        ArrayList<String> data =new ArrayList<>();
        data.add("balls");
        data.add(start.place.getX()+";"+start.place.getY()+";"+start.place.getZ()+";"+start.size+";"+start.mass);
        data.add("holes");
        data.add(hole.getX() + ";" + hole.getY() + ";" + hole.getZ());

        for(int i=0;i<objects.size();i++){
            data.addAll(objects.get(i).save());
        }
        return data;
    }

    private WorldObject addGrass(String[][]data,boolean[][]alreadyConverted,int i,int j,double gs,Point3D offset) {
        int iCounter=0;
        int jCounter=0;
        boolean keepCountingI=true;
        boolean keepCountingJ=true;
        while(keepCountingI||keepCountingJ)
        {
            if(keepCountingI) {
                if ((iCounter + i) < data.length && expand(data,i, j, iCounter + 1, jCounter, alreadyConverted,"FB")) {
                    iCounter++;
                }else{
                    keepCountingI=false;
                }
            }
            if(keepCountingJ) {
                if ((jCounter + j) < data[0].length && expand(data,i, j, iCounter , jCounter+1, alreadyConverted,"FB")) {
                    jCounter++;
                }else{
                    keepCountingJ=false;
                }
            }
        }
        for(int k=i;k<(i+iCounter);k++) {
            for(int l=j;l<(j+jCounter);l++) {
                alreadyConverted[k][l] = true;
            }
        }
        return new Grass(offset.add(i*gs,j*gs,0),Matrix.getRotatoinMatrix(0,0,0),iCounter*gs,jCounter*gs,this,gs);
    }
    private WorldObject addWall(String[][]data,boolean[][]alreadyConverted,int i,int j,double gs,Point3D offset) {
        int iCounter=0;
        int jCounter=0;
        boolean keepCountingI=true;
        boolean keepCountingJ=true;
        while(keepCountingI||keepCountingJ)
        {
            if(keepCountingI) {
                if ((iCounter + i) < data.length && expand(data,i, j, iCounter + 1, jCounter, alreadyConverted,"W")) {
                    iCounter++;
                }else{
                    keepCountingI=false;
                }
            }
            if(keepCountingJ) {
                if ((jCounter + j) < data[0].length && expand(data,i, j, iCounter , jCounter+1, alreadyConverted,"W")) {
                    jCounter++;
                }else{
                    keepCountingJ=false;
                }
            }
        }
        for(int k=i;k<(i+iCounter);k++) {
            for(int l=j;l<(j+jCounter);l++) {
                alreadyConverted[k][l] = true;
            }
        }
        return new Wall(offset.add(i*gs,j*gs,0),Matrix.getRotatoinMatrix(0,0,0),iCounter*gs,jCounter*gs,this);
    }
    private WorldObject addSand(String[][]data,boolean[][]alreadyConverted,int i,int j,double gs,Point3D offset) {
        int iCounter=0;
        int jCounter=0;
        boolean keepCountingI=true;
        boolean keepCountingJ=true;
        while(keepCountingI||keepCountingJ)
        {
            if(keepCountingI) {
                if ((iCounter + i) < data.length && expand(data,i, j, iCounter + 1, jCounter, alreadyConverted,"S")) {
                    iCounter++;
                }else{
                    keepCountingI=false;
                }
            }
            if(keepCountingJ) {
                if ((jCounter + j) < data[0].length && expand(data,i, j, iCounter , jCounter+1, alreadyConverted,"S")) {
                    jCounter++;
                }else{
                    keepCountingJ=false;
                }
            }
        }
        for(int k=i;k<(i+iCounter);k++) {
            for(int l=j;l<(j+jCounter);l++) {
                alreadyConverted[k][l] = true;
            }
        }
        return new Sand(offset.add(i*gs,j*gs,0),Matrix.getRotatoinMatrix(0,0,0),iCounter*gs,jCounter*gs,this);
    }
    protected WorldObject addHill(String[][]data,int i,int j,double gs,Point3D offset,String link){
        if(i>0 && link.contains(data[i-1][j])){
            return new Hill(this,offset.add(i*gs+2*gs,j*gs+2*gs,0), Matrix.getRotatoinMatrix(0,0,0),4*gs,4*gs,40,8);
        }else if(j>0 && link.contains(data[i][j-1])){
            return new Hill(this,offset.add(i*gs+2*gs,j*gs+2*gs,0), Matrix.getRotatoinMatrix(0,0, (float) (Math.PI/2)),4*gs,4*gs,40,8);
        }else if(i<(data.length-4) && link.contains(data[i+4][j])){
            return new Hill(this,offset.add(i*gs+2*gs,j*gs+2*gs,0), Matrix.getRotatoinMatrix(0,0, (float) Math.PI),4*gs,4*gs,40,8);
        }else{
            return new Hill(this,offset.add(i*gs+2*gs,j*gs+2*gs,0), Matrix.getRotatoinMatrix(0,0, (float) (Math.PI/2*3)),4*gs,4*gs,40,8);
        }
    }
    private boolean expand(String[][]data,int iStart,int jStart,int iSize,int jSize,boolean[][]alreadyConverted,String ignoreData) {
        boolean possible=true;
        for(int i=iStart;i<(iStart+iSize);i++)
        {
            for(int j=jStart;j<(jStart+jSize);j++)
            {
                if(alreadyConverted[i][j]==true || !ignoreData.contains(data[i][j]))
                {
                    possible=false;
                }
            }
        }
        return possible;
    }

    public class WorkThreadWithoutBallBallCollision extends Thread{
        public int start;
        public int stop;
        public WorkThreadWithoutBallBallCollision(int tStart,int tStop){
            start=tStart;
            stop=tStop;
        }

        @Override
        public void run() {
            for(int i=start;i<stop;i++) {
                Ball ball= balls.get(i);
                double completed=0;
                int subframes=((int) (ball.velocity.magnitude() / ball.size * 1.1 * precision) + 1);
                double subframeInv = 1.0 / (double)(subframes);

                while(completed<1){
                    if(completed+subframeInv > 1){
                        subframeInv = Math.abs(completed-subframeInv)+0.0001;
                    }
                    /*
                    ball.acceleration = ball.acceleration.add(0, 0, -gravity*subframeInv); //gravity
                    ball.velocity = ball.velocity.add(ball.acceleration);
                    ball.place = ball.place.add(ball.velocity.multiply(subframeInv));
                    ball.acceleration = new Point3D(0, 0, 0);

                    for(int j=0;j<objects.size();j++){
                        objects.get(j).applyCollision(ball,subframeInv);
                    }
                    if(terainPhysics)terrain.applyCollision(ball,subframeInv);*/

                    ball.acceleration = ball.acceleration.add(0, 0, -gravity*subframeInv); //gravity
                    ball.normalTotal=new Point3D(0,0,0);
                    ball.normalCounter=0;

                    for(int j=0;j<objects.size();j++){
                        objects.get(j).applyCollision(ball,subframeInv);
                    }
                    if(terainPhysics)terrain.applyCollision(ball,subframeInv);

                    if(ball.normalCounter>0){
                        Point3D normal = ball.normalTotal.multiply(1.0/ball.normalCounter).normalize();
                        ball.acceleration = ball.acceleration.add(normal.multiply(ball.velocity.dotProduct(normal) * -1.8));
                    }
                    ball.velocity = ball.velocity.add(ball.acceleration);
                    ball.place = ball.place.add(ball.velocity.multiply(subframeInv));
                    ball.acceleration = new Point3D(0, 0, 0);

                    completed+=subframeInv;
                    subframes=((int) (ball.velocity.magnitude() / ball.size * 1.1 * precision) + 1);
                    subframeInv = 1.0 / (double)(subframes);
                }
            }
        }
    }
}
