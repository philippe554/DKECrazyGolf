package CrazyGolf.PhysicsEngine.Objects.Terain;

import CrazyGolf.PhysicsEngine.Objects.Parts.Edge;
import CrazyGolf.PhysicsEngine.Objects.Parts.Water;
import CrazyGolf.PhysicsEngine.Objects.Tree;
import CrazyGolf.PhysicsEngine.Objects.Parts.Side;
import CrazyGolf.PhysicsEngine.Objects.WorldObject;
import javafx.geometry.Point3D;

import javax.vecmath.Color3f;

/**
 * Created by pmmde on 5/28/2016.
 */
public class TerainChunk extends WorldObject {
    public static final float chunkSize=1024;
    public static final int chunkParts=32;
    public static final float chunkPartSize=chunkSize/chunkParts;

    public int x;
    public int y;
    public boolean loaded;

    public TerainChunk(SimplexNoise sm, Key key){
        points=new Point3D[(chunkParts+1)*(chunkParts+1)+4];
        pointsOriginal=new Point3D[points.length];
        colors=new Color3f[14];
        sides=new Side[chunkParts*chunkParts*2+2];
        edges=new Edge[0];
        waters=new Water[1];

        x=key.x;
        y=key.y;
        loaded=true;
        for(int i=0;i<=chunkParts;i++){
            for(int j=0;j<=chunkParts;j++){
                double height = (sm.noise((x*chunkSize+i*chunkPartSize)*0.001f,(y*chunkSize+j*chunkPartSize)*0.001f)+1)/2;
                double terrain = (sm.noise((x*chunkSize+i*chunkPartSize)*0.0001,(y*chunkSize+j*chunkPartSize)*0.0001)+1)/2;
                height*=Math.pow(terrain*2,3)*100;
                pointsOriginal[i*(chunkParts+1)+j]=new Point3D(i*chunkPartSize,j*chunkPartSize, height);

                if(height>40 && height<250 && (sm.noise((x*chunkSize+i*chunkPartSize)*0.001,(y*chunkSize+j*chunkPartSize)*0.001)+1)/2 >0.6 && Math.random()*height<10) {
                    subObjects.add(new Tree(new Point3D(x * chunkSize + (i+Math.random()) * chunkPartSize, y * chunkSize + (j+Math.random()) * chunkPartSize, height)));
                }
            }
        }
        pointsOriginal[pointsOriginal.length-4]=new Point3D(0,0,20);
        pointsOriginal[pointsOriginal.length-3]=new Point3D(chunkSize,0,20);
        pointsOriginal[pointsOriginal.length-2]=new Point3D(chunkSize,chunkSize,20);
        pointsOriginal[pointsOriginal.length-1]=new Point3D(0,chunkSize,20);
        for(int i=0;i<10;i++){
            colors[i]=new Color3f(0,1-((float) (i/20.0f)+0.5f),0);
        }
        colors[10]=new Color3f(0.93f,0.78f,0.68f);
        colors[11]=new Color3f(0.59f,0.55f,0.6f);
        colors[12]=new Color3f(1f,1f,1f);
        colors[13]=new Color3f(0.0f, 0.8f, 1.0f);

        setCenter(new Point3D(x*chunkSize,y*chunkSize,0));
        move();
        setupBoxing();

        for(int i=0;i<chunkParts;i++) {
            for (int j = 0; j < chunkParts; j++) {
                sides[(i*chunkParts+j)*2]=new Side(this,i*(chunkParts+1)+j,(i+1)*(chunkParts+1)+j,i*(chunkParts+1)+(j+1),0,0);
                sides[(i*chunkParts+j)*2+1]=new Side(this,i*(chunkParts+1)+(j+1),(i+1)*(chunkParts+1)+(j+1),(i+1)*(chunkParts+1)+(j),0,0);
                double angle = Math.acos(sides[(i*chunkParts+j)*2].normal.dotProduct(0,0,1))*180.0/Math.PI;
                double height = (sm.noise((x*chunkSize+i*chunkPartSize)*0.001f,(y*chunkSize+j*chunkPartSize)*0.001f)+1)/2;
                double terrain = (sm.noise((x*chunkSize+i*chunkPartSize)*0.0001,(y*chunkSize+j*chunkPartSize)*0.0001)+1)/2;
                height*=Math.pow(terrain*2,3)*100;
                if(angle>90)angle-=90;
                int color = (int)(angle/9.0);
                if(height<30)color=10;
                if(height>250)color=11;
                if(height>300)color=12;
                sides[(i*chunkParts+j)*2].color=color;
                sides[(i*chunkParts+j)*2+1].color=color;
            }
        }
        waters[0]=new Water(new Point3D[]{new Point3D(0,0,0), new Point3D(chunkSize,chunkSize,20)},13);
        sides[sides.length-2]=new Side(this,(chunkParts+1)*(chunkParts+1),(chunkParts+1)*(chunkParts+1)+1,(chunkParts+1)*(chunkParts+1)+2,13,0.0);
        sides[sides.length-1]=new Side(this,(chunkParts+1)*(chunkParts+1),(chunkParts+1)*(chunkParts+1)+3,(chunkParts+1)*(chunkParts+1)+2,13,0.0);
    }
}
