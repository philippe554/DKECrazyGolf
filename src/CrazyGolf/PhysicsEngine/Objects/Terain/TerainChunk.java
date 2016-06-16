package CrazyGolf.PhysicsEngine.Objects.Terain;

import CrazyGolf.PhysicsEngine.Objects.Parts.Edge;
import CrazyGolf.PhysicsEngine.Objects.Parts.Water;
import CrazyGolf.PhysicsEngine.Objects.Native.Tree;
import CrazyGolf.PhysicsEngine.Objects.Parts.Side;
import CrazyGolf.PhysicsEngine.Physics3.WorldObject;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;

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

    public TerainChunk(SimplexNoise sm, Key key, WorldData w){
        super(w);
        points=new Point3D[(chunkParts+1)*(chunkParts+1)+4];
        pointsOriginal=new Point3D[points.length];
        colors=new Color3f[15];
        sides=new Side[chunkParts*chunkParts*2];
        edges=new Edge[chunkParts*chunkParts*2];
        waters=new Water[0];

        x=key.x;
        y=key.y;
        loaded=true;
        for(int i=0;i<=chunkParts;i++){
            for(int j=0;j<=chunkParts;j++){
                double height = (sm.noise((x*chunkSize+i*chunkPartSize)*0.0005f,(y*chunkSize+j*chunkPartSize)*0.0005f)+1)/2;
                double terrain = (sm.noise((x*chunkSize+i*chunkPartSize)*0.0001,(y*chunkSize+j*chunkPartSize)*0.0001)+1)/2;
                double smallNoise = (sm.noise((x*chunkSize+i*chunkPartSize)*0.1,(y*chunkSize+j*chunkPartSize)*0.1)+1)/2;
                double bigNoise = (sm.noise((x*chunkSize+i*chunkPartSize)*0.01,(y*chunkSize+j*chunkPartSize)*0.01)+1)/2;
                double mountainNoise = sm.noise((x*chunkSize+i*chunkPartSize)*0.5,(y*chunkSize+j*chunkPartSize)*0.5);
                height*=Math.pow(terrain*2,3)*250+smallNoise*20+bigNoise*50;
                if(height>400)height+=mountainNoise*100;
                if(height<45)height=40;
                pointsOriginal[i*(chunkParts+1)+j]=new Point3D(i*chunkPartSize,j*chunkPartSize, height);

                if(height>60 && height<600 && (sm.noise((x*chunkSize+i*chunkPartSize)*0.001,(y*chunkSize+j*chunkPartSize)*0.001)+1)/2 > 0.55 && Math.random()*height<50) {
                    subObjects.add(new Tree(new Point3D(x * chunkSize + (i+Math.random()) * chunkPartSize, y * chunkSize + (j+Math.random()) * chunkPartSize, height),world));
                }
            }
        }
        pointsOriginal[pointsOriginal.length-4]=new Point3D(0,0,20);
        pointsOriginal[pointsOriginal.length-3]=new Point3D(chunkSize,0,20);
        pointsOriginal[pointsOriginal.length-2]=new Point3D(chunkSize,chunkSize,20);
        pointsOriginal[pointsOriginal.length-1]=new Point3D(0,chunkSize,20);

        //water
        colors[0]=new Color3f(0.36f,0.81f,0.88f);
        colors[1]=new Color3f(0.16f,0.74f,0.78f);
        colors[2]=new Color3f(0.58f,0.87f,0.95f);
        //sand
        colors[3]=new Color3f(0.98f,0.98f,0.61f);
        colors[4]=new Color3f(0.95f,0.95f,0.42f);
        colors[5]=new Color3f(0.94f,0.82f,0.36f);
        //grass
        colors[6]=new Color3f(0.25f,0.82f,0.25f);
        colors[7]=new Color3f(0.17f,0.78f,0.17f);
        colors[8]=new Color3f(0.08f,0.91f,0.27f);
        //rock
        colors[9]=new Color3f(0.86f,0.86f,0.82f);
        colors[10]=new Color3f(0.81f,0.81f,0.75f);
        colors[11]=new Color3f(0.80f,0.80f,0.79f);
        //snow
        colors[12]=new Color3f(0.91f,0.92f,0.95f);
        colors[13]=new Color3f(0.99f,0.99f,1.00f);
        colors[14]=new Color3f(0.95f,0.97f,0.98f);

        center=new Point3D(x*chunkSize,y*chunkSize,0);

        for(int i=0;i<chunkParts;i++) {
            for (int j = 0; j < chunkParts; j++) {
                sides[(i*chunkParts+j)*2]=new Side(i*(chunkParts+1)+j,(i+1)*(chunkParts+1)+j,i*(chunkParts+1)+(j+1),0,0.5);
                double avgHeight=(pointsOriginal[i*(chunkParts+1)+j].getZ()+pointsOriginal[(i+1)*(chunkParts+1)+j].getZ()+pointsOriginal[i*(chunkParts+1)+(j+1)].getZ())/3.0;
                int color;
                if(avgHeight<40.001){
                    color= (int) (Math.random()*3);
                }else if(avgHeight<50){
                    color= (int) (Math.random()*3+3);
                }else if(avgHeight<500){
                    color= (int) (Math.random()*3+6);
                }else if(avgHeight<700){
                    color= (int) (Math.random()*3+9);
                }else{
                    color= (int) (Math.random()*3+12);
                }
                sides[(i*chunkParts+j)*2].color=color;

                sides[(i*chunkParts+j)*2+1]=new Side(i*(chunkParts+1)+(j+1),(i+1)*(chunkParts+1)+(j+1),(i+1)*(chunkParts+1)+(j),0,0.5);
                avgHeight=(pointsOriginal[i*(chunkParts+1)+(j+1)].getZ()+pointsOriginal[(i+1)*(chunkParts+1)+(j+1)].getZ()+pointsOriginal[(i+1)*(chunkParts+1)+(j)].getZ())/3.0;
                if(avgHeight<40.001){
                    color= (int) (Math.random()*3);
                }else if(avgHeight<50){
                    color= (int) (Math.random()*3+3);
                }else if(avgHeight<500){
                    color= (int) (Math.random()*3+6);
                }else if(avgHeight<700){
                    color= (int) (Math.random()*3+9);
                }else{
                    color= (int) (Math.random()*3+12);
                }
                sides[(i*chunkParts+j)*2+1].color=color;

                edges[(i*chunkParts+j)*2]=new Edge(i*(chunkParts+1)+j,(i+1)*(chunkParts+1)+j);
                edges[(i*chunkParts+j)*2+1]=new Edge(i*(chunkParts+1)+(j),i*(chunkParts+1)+(j+1));

                sides[(i*chunkParts+j)*2].normals=new Point3D[]{new Point3D(0,0,1),new Point3D(0,0,1),new Point3D(0,0,1)};
                sides[(i*chunkParts+j)*2+1].normals=new Point3D[]{new Point3D(0,0,1),new Point3D(0,0,1),new Point3D(0,0,1)};
            }
        }
        setup(false);
        hasPointNormals=true;
        for(int i=0;i<chunkParts;i++) {
            for (int j = 0; j < chunkParts; j++) {
                if(i==0 ||  j==0) {

                }else{
                    Point3D total=sides[(i*chunkParts+j)*2].normal.getZ()>0?sides[(i*chunkParts+j)*2].normal:sides[(i*chunkParts+j)*2].normal.multiply(-1.0)
                            .add(sides[((i-1)*chunkParts+j)*2].normal.getZ()>0?sides[((i-1)*chunkParts+j)*2].normal:sides[((i-1)*chunkParts+j)*2].normal.multiply(-1.0))
                            .add(sides[((i-1)*chunkParts+j)*2+1].normal.getZ()>0?sides[((i-1)*chunkParts+j)*2+1].normal:sides[((i-1)*chunkParts+j)*2+1].normal.multiply(-1.0))
                            .add(sides[((i)*chunkParts+j-1)*2].normal.getZ()>0?sides[((i)*chunkParts+j-1)*2].normal:sides[((i)*chunkParts+j-1)*2].normal.multiply(-1.0))
                            .add(sides[((i)*chunkParts+j-1)*2+1].normal.getZ()>0?sides[((i)*chunkParts+j-1)*2+1].normal:sides[((i)*chunkParts+j-1)*2+1].normal.multiply(-1.0))
                            .add(sides[((i-1)*chunkParts+j-1)*2+1].normal.getZ()>0?sides[((i-1)*chunkParts+j-1)*2+1].normal:sides[((i-1)*chunkParts+j-1)*2+1].normal.multiply(-1.0));

                    Point3D pointNormal = total.normalize();

                    sides[(i*chunkParts+j)*2].normals[0]=pointNormal;
                    sides[((i-1)*chunkParts+j)*2].normals[1]=pointNormal;
                    sides[((i-1)*chunkParts+j)*2+1].normals[2]=pointNormal;
                    sides[(i*chunkParts+(j-1))*2].normals[2]=pointNormal;
                    sides[(i*chunkParts+(j-1))*2+1].normals[0]=pointNormal;
                    sides[((i-1)*chunkParts+(j-1))*2+1].normals[1]=pointNormal;
                }
            }
        }
        //waters[0]=new Water(new Point3D[]{new Point3D(0,0,0).add(center), new Point3D(chunkSize,chunkSize,30).add(center)},13);
    }
}
