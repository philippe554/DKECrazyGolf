package CrazyGolf.PhysicsEngine.Physics3;

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
    protected Ball start;
    protected Point3D hole;

    public Queue<WorldObject> newObjects;
    public Queue<WorldObject> updatedObjects;
    public Queue<Integer> deletedObjects;

    private int time = 0;
    private SimplexNoise wind;

    public WorldData(){
        objects=new ArrayList<>();
        terrain=new Terrain(1631365,this);
        balls=new ArrayList<>();
        newObjects = new LinkedList<>();
        updatedObjects = new LinkedList<>();
        deletedObjects = new LinkedList<>();
        wind=new SimplexNoise(4654654);
        start = new Ball(20, new Point3D(0,0,0));
        hole = new Point3D(0,0,0);
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
            stepWithoutCollision();
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
        return balls.size();
    }
    @Override public Ball getBall(int i) {
        return balls.get(i);
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

    protected void stepWithoutCollision(){
        for(Ball ball:balls) {
            double completed=0;
            int subframes=((int) (ball.velocity.magnitude() / ball.size * 1.1 * precision) + 1);
            double subframeInv = 1.0 / (double)(subframes);

            while(completed<1){
                if(completed+subframeInv > 1){
                    subframeInv = Math.abs(completed-subframeInv)+0.0001;
                }
                ball.acceleration = ball.acceleration.add(0, 0, -gravity*subframeInv); //gravity
                ball.velocity = ball.velocity.add(ball.acceleration);
                ball.place = ball.place.add(ball.velocity.multiply(subframeInv));
                ball.acceleration = new Point3D(0, 0, 0);

                for(int j=0;j<objects.size();j++){
                    objects.get(j).applyCollision(ball,subframeInv);
                }
                terrain.applyCollision(ball,subframeInv);
                completed+=subframeInv;
                subframes=((int) (ball.velocity.magnitude() / ball.size * 1.1 * precision) + 1);
                subframeInv = 1.0 / (double)(subframes);
            }
        }
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
                ball.acceleration = ball.acceleration.add(0, 0, -gravity*subframeInv); //gravity
                ball.velocity = ball.velocity.add(ball.acceleration);
                ball.place = ball.place.add(ball.velocity.multiply(subframeInv));
                ball.acceleration = new Point3D(0, 0, 0);

                for(int j=0;j<objects.size();j++){
                    objects.get(j).applyCollision(ball,subframeInv);
                }
                terrain.applyCollision(ball,subframeInv);
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

    @Override public void load(LinkedList<String> data) {
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
                        wo.setupBoxing();
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
                                    wo.setupBoxing();
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
        WorldObject wo = new WorldObject(this);
        boolean[][]alreadyConverted=new boolean[data.length][data[0].length];
        for(int i=0;i<alreadyConverted.length;i++)
        {
            for(int j=0;j<alreadyConverted[i].length;j++)
            {
                alreadyConverted[i][j]=false;
            }
        }
        for(int i=0;i<data.length;i++) {
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
        for(int i=0;i<alreadyConverted.length;i++)
        {
            for(int j=0;j<alreadyConverted[i].length;j++)
            {
                alreadyConverted[i][j]=false;
            }
        }
        for(int i=0;i<data.length;i++) {
            for (int j = 0; j < data[i].length; j++) {
                if(!alreadyConverted[i][j]) {
                    if (data[i][j].equals("W")) {
                        //addWall(data,alreadyConverted,i,j,gs,Z);
                    } else if (data[i][j].equals("F") || data[i][j].equals("B")) {
                        wo.subObjects.add(addGrass(data,alreadyConverted,i,j,gs,offset.getZ()));
                    } else if (data[i][j].equals("S")) {
                        //addSand(data,alreadyConverted,i,j,gs,Z);
                    } else if (data[i][j].equals("H")) {
                        wo.subObjects.add(new Hole(this,offset.add(i*gs+1.5*gs,j*gs+1.5*gs,0),30,80,30));
                        hole=offset.add(i*gs+1.5*gs,j*gs+1.5*gs,-80+20);
                        for(int k=0;k<3;k++)
                        {
                            for(int l=0;l<3;l++)
                            {
                                if(i+k<data.length && j+l<data[i+k].length) {
                                    alreadyConverted[i + k][j + l] = true;
                                }
                            }
                        }
                    }else if(data[i][j].equals("L"))
                    {
                        wo.subObjects.add(new Loop(this,offset.add(i*gs+140,j*gs+60,140),140,60,24,25));
                        for(int k=0;k<14;k++)
                        {
                            for(int l=0;l<6;l++)
                            {
                                if((i+k)<data.length && (j+l)<data[i+k].length) {
                                    alreadyConverted[i + k][j + l] = true;
                                }
                            }
                        }
                    }else if(data[i][j].equals("C"))
                    {
                        wo.subObjects.add(new Castle(this,offset.add(i*gs+40,j*gs+40,0),20,40,180));
                        for(int k=0;k<13;k++)
                        {
                            for(int l=0;l<4;l++)
                            {
                                if((i+k)<data.length && (j+l)<data[i+k].length) {
                                    alreadyConverted[i + k][j + l] = true;
                                }
                            }
                        }
                    } else if(data[i][j].equals("R"))
                    {
                        wo.subObjects.add(new Bridge(this,offset.add(i*gs+40,j*gs+140,0),200,50,20,80,20));
                        for(int k=0;k<4;k++)
                        {
                            for(int l=0;l<24;l++)
                            {
                                if((i+k)<data.length && (j+l)<data[i+k].length) {
                                    alreadyConverted[i + k][j + l] = true;
                                }
                            }
                        }
                    }else if(data[i][j].equals("P"))
                    {
                        wo.subObjects.add(new Pool(this,offset.add(i*gs,j*gs,0),280,150,12,25));
                        for(int k=0;k<14;k++)
                        {
                            for(int l=0;l<14;l++)
                            {
                                if((i+k)<data.length && (j+l)<data[i+k].length) {
                                    alreadyConverted[i + k][j + l] = true;
                                }
                            }
                        }
                    }else if(data[i][j].equals("M")){
                        //addHill(data,i,j,gs,Z,"FBS");
                        for(int k=0;k<4;k++)
                        {
                            for(int l=0;l<4;l++)
                            {
                                if((i+k)<data.length && (j+l)<data[i+k].length) {
                                    alreadyConverted[i + k][j + l] = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if(wo.getAmountSubObjects()>0) {
            newObjects.offer(wo);
            wo.setupBoxing();
            objects.add(wo);
        }
    }
    @Override public LinkedList<String> save() {
        LinkedList<String> data =new LinkedList<>();
        data.add("balls");
        data.add(start.place.getX()+";"+start.place.getY()+";"+start.place.getZ()+";"+start.size+";"+start.mass);
        data.add("holes");
        data.add(hole.getX() + ";" + hole.getY() + ";" + hole.getZ());

        for(int i=0;i<objects.size();i++){
            data.addAll(objects.get(i).save());
        }
        return data;
    }

    private WorldObject addGrass(String[][]data,boolean[][]alreadyConverted,int i,int j,double gs,double Z) {
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
        return new Grass(new Point3D(i*gs,j*gs,Z),new Point3D(i*gs+iCounter*gs,j*gs+jCounter*gs,Z),this);
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

}
