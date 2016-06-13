package CrazyGolf.PhysicsEngine.Objects.Terain;

import CrazyGolf.PhysicsEngine.Objects.Parts.Ball;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import CrazyGolf.PhysicsEngine.Physics3.WorldObject;
import javafx.geometry.Point3D;

import java.util.*;

/**
 * Created by pmmde on 6/1/2016.
 */
public class Terrain{
    private Map<Key,TerainChunk> chunks;
    private SimplexNoise sn;

    public static final int viewDistance = 5;
    public static final int loadDistance = 7;

    private int x;
    private int y;

    WorldData world;

    public Terrain(int seed,WorldData w){
        world=w;
        chunks=new HashMap<>();
        sn = new SimplexNoise(seed);
        x=0;
        y=0;
    }

    public void updateTerain(Point3D center){
        x = (int) Math.floor(center.getX()/TerainChunk.chunkSize);
        y = (int) Math.floor(center.getY()/TerainChunk.chunkSize);
    }

    public void run() {
        chunks.entrySet().removeIf(e->{
            Key key = e.getKey();
            if(Math.abs(key.x-x)>viewDistance || Math.abs(key.y-y)>viewDistance){
                if(e.getValue().loaded) {
                    e.getValue().loaded=false;
                    world.newObjects.remove(e.getValue());
                    world.deletedObjects.add(e.getValue().getID());
                }
            }
            if(Math.abs(key.x-x)>loadDistance || Math.abs(key.y-y)>loadDistance){
                saveObject(key);
                return true;
            }
            return false;
        });

        for(int i=x-viewDistance;i<=x+viewDistance;i++){
            for(int j=y-viewDistance;j<=y+viewDistance;j++){
                Key key = new Key(i,j);
                if(!chunks.containsKey(key)){
                    TerainChunk tc = loadObject(key);
                    chunks.put(key,tc);
                    world.deletedObjects.remove(chunks.get(key).getID());
                    world.newObjects.add(chunks.get(key));
                }else {
                    if (!chunks.get(key).loaded) {
                        world.deletedObjects.remove(chunks.get(key).getID());
                        world.newObjects.add(chunks.get(key));
                        chunks.get(key).loaded = true;
                    }
                }
            }
        }
    }
    public void applyCollision(Ball ball, double subFrameInv){
        int cx = (int) Math.floor(ball.place.getX()/TerainChunk.chunkSize);
        int cy = (int) Math.floor(ball.place.getY()/TerainChunk.chunkSize);
        TerainChunk tc = chunks.get(new Key(cx,cy));
        if(tc!=null){
            tc.applyCollision(ball,subFrameInv);
        }
    }

    private void saveObject(Key key){

    }
    private TerainChunk loadObject(Key key){
        TerainChunk tc = new TerainChunk(sn,key,world);
        tc.setupBoxing();
        return tc;
    }

    public double getHeight(double tx,double ty){
        int cx = (int) (tx/TerainChunk.chunkSize);
        int cy = (int) (ty/TerainChunk.chunkSize);
        TerainChunk tc = chunks.get(new Key(cx,cy));
        if(tc==null)return 0.0;
        double px = tx%TerainChunk.chunkSize;
        double py = ty%TerainChunk.chunkSize;
        if(px<0)px=TerainChunk.chunkSize+px;
        if(py<0)py=TerainChunk.chunkSize+py;
        int ix = (int) (px/TerainChunk.chunkPartSize);
        int iy = (int) (py/TerainChunk.chunkPartSize);
        return tc.getPoint((ix*(TerainChunk.chunkParts+1)+iy)).getZ();
    }

}
