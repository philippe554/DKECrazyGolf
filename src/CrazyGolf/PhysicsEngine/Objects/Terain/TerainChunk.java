package CrazyGolf.PhysicsEngine.Objects.Terain;

import CrazyGolf.PhysicsEngine.Objects.Parts.Ball;
import CrazyGolf.PhysicsEngine.Objects.Parts.Edge;
import CrazyGolf.PhysicsEngine.Objects.Parts.Water;
import CrazyGolf.PhysicsEngine.Objects.Native.Tree;
import CrazyGolf.PhysicsEngine.Objects.Parts.Side;
import CrazyGolf.PhysicsEngine.Physics3.WorldObject;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import javafx.geometry.Point3D;


import javax.vecmath.Color3f;
import java.util.ArrayList;

/**
 * Created by pmmde on 5/28/2016.
 */
public class TerainChunk extends WorldObject {
    public static final float chunkSize=1024;
    public static final int chunkParts=12;
    public static final float chunkPartSize=chunkSize/chunkParts;

    public int x;
    public int y;
    public boolean loaded;

    SimplexNoise sm;

    private static ArrayList<Color3f> staticColors;
    private Water water;

    public TerainChunk(SimplexNoise tsm, Key key, WorldData w){
        super(w);
        sm=tsm;

        pointsOriginal=new ArrayList<>((chunkParts+1)*(chunkParts+1)+4);
        sides=new ArrayList<>(chunkParts*chunkParts*2);
        edges=new ArrayList<>();
        waters=new ArrayList<>();
        loadColors();

        x=key.x;
        y=key.y;
        loaded=true;
        for(int i=0;i<=chunkParts;i++){
            for(int j=0;j<=chunkParts;j++){
                double biome = getBiome(i,j);
                double height = getHeight(i,j,biome);
                pointsOriginal.add(new Point3D(i*chunkPartSize,j*chunkPartSize, height));
                if(biome>0.25) {
                    if (height > 60 && height < 600 && (sm.noise((x * chunkSize + i * chunkPartSize) * 0.001, (y * chunkSize + j * chunkPartSize) * 0.001) + 1) / 2 > 0.55 && Math.random() * height < 50) {
                        subObjects.add(new Tree(new Point3D(x * chunkSize + (i + Math.random()) * chunkPartSize, y * chunkSize + (j + Math.random()) * chunkPartSize, height), world));
                    }
                }
            }
        }
        pointsOriginal.add(new Point3D(0,0,20));
        pointsOriginal.add(new Point3D(chunkSize,0,20));
        pointsOriginal.add(new Point3D(chunkSize,chunkSize,20));
        pointsOriginal.add(new Point3D(0,chunkSize,20));

        water=new Water(new Point3D[]{new Point3D(0,0,-100).add(center), new Point3D(chunkSize,chunkSize,40).add(center)},0);
        center=new Point3D(x*chunkSize,y*chunkSize,0);

        for(int i=0;i<chunkParts;i++) {
            for (int j = 0; j < chunkParts; j++) {
                sides.add(new Side(i*(chunkParts+1)+j,(i+1)*(chunkParts+1)+j,i*(chunkParts+1)+(j+1),0,0.5));
                double avgHeight=(pointsOriginal.get(i*(chunkParts+1)+j).getZ()+pointsOriginal.get((i+1)*(chunkParts+1)+j).getZ()+pointsOriginal.get(i*(chunkParts+1)+(j+1)).getZ())/3.0;
                sides.get((i*chunkParts+j)*2).color=getColor(avgHeight,i,j);

                sides.add(new Side(i*(chunkParts+1)+(j+1),(i+1)*(chunkParts+1)+(j+1),(i+1)*(chunkParts+1)+(j),0,0.5));
                avgHeight=(pointsOriginal.get(i*(chunkParts+1)+(j+1)).getZ()+pointsOriginal.get((i+1)*(chunkParts+1)+(j+1)).getZ()+pointsOriginal.get((i+1)*(chunkParts+1)+(j)).getZ())/3.0;
                sides.get((i*chunkParts+j)*2+1).color=getColor(avgHeight,i,j);
            }
        }
        setup(false);
        hasPointNormals=true;
        for(int i=0;i<chunkParts;i++) {
            for (int j = 0; j < chunkParts; j++) {

                sides.get((i*chunkParts+j)*2).normals=new Point3D[3];
                sides.get((i*chunkParts+j)*2).normals[0]=calculateNormal(i,j);
                sides.get((i*chunkParts+j)*2).normals[1]=calculateNormal(i+1,j);
                sides.get((i*chunkParts+j)*2).normals[2]=calculateNormal(i,j+1);

                sides.get((i*chunkParts+j)*2+1).normals=new Point3D[3];
                sides.get((i*chunkParts+j)*2+1).normals[0]=calculateNormal(i,j+1);
                sides.get((i*chunkParts+j)*2+1).normals[1]=calculateNormal(i+1,j+1);
                sides.get((i*chunkParts+j)*2+1).normals[2]=calculateNormal(i+1,j);
            }

        }
    }
    private Point3D calculateNormal(int i,int j){
        double biome = getBiome(i,j);
        double heightL = getHeight(i-1,j,biome);//pointsOriginal[(i-1)*(chunkParts+1)+j].getZ();
        double heightR = getHeight(i+1,j,biome);//pointsOriginal[(i+1)*(chunkParts+1)+j].getZ();
        double heightD= getHeight(i,j-1,biome);//pointsOriginal[(i)*(chunkParts+1)+(j+1)].getZ();
        double heightU = getHeight(i,j+1,biome);//pointsOriginal[(i)*(chunkParts+1)+(j-1)].getZ();
        Point3D normal = new Point3D(heightL-heightR, heightD-heightU, 20);
        return normal.normalize();
    }
    private double getHeight(int i,int j,double biome){
        if(i<0 || i >chunkParts || j<0 || j >chunkParts || pointsOriginal.size()<=(i*(chunkParts+1)+j)) {
            double height = (sm.noise((x * chunkSize + i * chunkPartSize) * 0.0005f, (y * chunkSize + j * chunkPartSize) * 0.0005f) + 1) / 2;
            double terrain = (sm.noise((x * chunkSize + i * chunkPartSize) * 0.0001, (y * chunkSize + j * chunkPartSize) * 0.0001) + 1) / 2;
            double smallNoise = (sm.noise((x * chunkSize + i * chunkPartSize) * 0.1, (y * chunkSize + j * chunkPartSize) * 0.1) + 1) / 2;
            double bigNoise = (sm.noise((x * chunkSize + i * chunkPartSize) * 0.01, (y * chunkSize + j * chunkPartSize) * 0.01) + 1) / 2;
            double mountainNoise = sm.noise((x * chunkSize + i * chunkPartSize) * 0.5, (y * chunkSize + j * chunkPartSize) * 0.5);
            height *= Math.pow(terrain * 2, 2 + biome) * 250 + smallNoise * 20 + bigNoise * 50;
            if (height > 400) height += mountainNoise * 100;
            if (height < 45) height = 40;
            return height;
        }else{
            return pointsOriginal.get(i*(chunkParts+1)+j).getZ();
        }
    }
    private double getBiome(int i,int j){
        double biome=(sm.noise((x*chunkSize+i*chunkPartSize)*0.00002f,(y*chunkSize+j*chunkPartSize)*0.00002f)+1)/2+
                0.1*(sm.noise((x*chunkSize+i*chunkPartSize)*0.0005f,(y*chunkSize+j*chunkPartSize)*0.0005f)+1)/2;
        return biome;
    }
    private void loadColors(){
        if(staticColors==null) {
            staticColors = new ArrayList<>();
            //water
            staticColors.add(new Color3f(0.36f, 0.81f, 0.88f));
            staticColors.add(new Color3f(0.16f, 0.74f, 0.78f));
            staticColors.add(new Color3f(0.58f, 0.87f, 0.95f));
            //sand
            staticColors.add(new Color3f(0.98f, 0.98f, 0.61f));
            staticColors.add(new Color3f(0.95f, 0.95f, 0.42f));
            staticColors.add(new Color3f(0.94f, 0.82f, 0.36f));
            //grass
            staticColors.add(new Color3f(0.25f, 0.82f, 0.25f));
            staticColors.add(new Color3f(0.17f, 0.78f, 0.17f));
            staticColors.add(new Color3f(0.08f, 0.91f, 0.27f));
            //rock
            staticColors.add(new Color3f(0.86f, 0.86f, 0.82f));
            staticColors.add(new Color3f(0.81f, 0.81f, 0.75f));
            staticColors.add(new Color3f(0.80f, 0.80f, 0.79f));
            //snow
            staticColors.add(new Color3f(0.91f, 0.92f, 0.95f));
            staticColors.add(new Color3f(0.99f, 0.99f, 1.00f));
            staticColors.add(new Color3f(0.95f, 0.97f, 0.98f));

            //water
            staticColors.add(new Color3f(75 / 255f, 157 / 255f, 157 / 255f));
            staticColors.add(new Color3f(40 / 255f, 144 / 255f, 142 / 255f));
            staticColors.add(new Color3f(52 / 255f, 145 / 255f, 146 / 255f));
            staticColors.add(new Color3f(82 / 255f, 134 / 255f, 127 / 255f));
            staticColors.add(new Color3f(52 / 255f, 144 / 255f, 149 / 255f));
            staticColors.add(new Color3f(100 / 255f, 166 / 255f, 161 / 255f));
            staticColors.add(new Color3f(86 / 255f, 142 / 255f, 137 / 255f));
            //grass
            staticColors.add(new Color3f(223 / 255f, 220 / 255f, 105 / 255f));
            staticColors.add(new Color3f(208 / 255f, 206 / 255f, 103 / 255f));
            staticColors.add(new Color3f(159 / 255f, 163 / 255f, 104 / 255f));
            staticColors.add(new Color3f(186 / 255f, 185 / 255f, 105 / 255f));
            staticColors.add(new Color3f(244 / 255f, 235 / 255f, 106 / 255f));
            staticColors.add(new Color3f(223 / 255f, 220 / 255f, 105 / 255f));
            staticColors.add(new Color3f(207 / 255f, 205 / 255f, 104 / 255f));
        }
        colors=staticColors;
    }
    private int getColor(double avgHeight,int i,int j){
        int color;
        if(avgHeight<40.001){
            if(getBiome(i,j)>0.25) {
                color = (int) (Math.random() * 3);
            }else{
                color = (int) (15+Math.random() * 7);
            }
        }else if(avgHeight<50){
            color= (int) (Math.random()*3+3);
        }else if(avgHeight<500){
            if(getBiome(i,j)>0.25) {
                color = (int) (Math.random() * 3+6);
            }else{
                color = (int) (22+Math.random() * 7);
            }
        }else if(avgHeight<700){
            color= (int) (Math.random()*3+9);
        }else{
            if(getBiome(i,j)>0.25) {
                color= (int) (Math.random()*3+12);
            }else{
                color= (int) (Math.random()*3+9);
            }
        }
        return color;
    }
    public void applyCollision(Ball ball, double subframeInv){
        if(ball.place.getX()+ball.size>boxing[0].getX()&&ball.place.getY()+ball.size>boxing[0].getY()&&ball.place.getZ()+ball.size>boxing[0].getZ()&&
                ball.place.getX()-ball.size<boxing[1].getX()&&ball.place.getY()-ball.size<boxing[1].getY()&&ball.place.getZ()-ball.size<boxing[1].getZ()) {
            ballWater(water, ball, subframeInv);
            for (int j = 0; j < sides.size(); j++) {
                if(points.get(sides.get(j).points[0]).getZ()>40 || points.get(sides.get(j).points[1]).getZ()>40 || points.get(sides.get(j).points[2]).getZ()>40) {
                    sideCollision(sides.get(j), ball);
                }
            }
            for(int i=0;i<subObjects.size();i++)
            {
                subObjects.get(i).applyCollision(ball,subframeInv);
            }
        }
    }
}
