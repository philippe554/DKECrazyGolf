package CrazyGolf.PhysicsEngine.Objects.Terain;

/**
 * Created by pmmde on 6/1/2016.
 */
public class Key {
    public final int x;
    public final int y;
     public Key(int tx,int ty){
         x=tx;
         y=ty;
     }
    @Override public int hashCode(){
        return x*31+y;
    }
    @Override public boolean equals(Object obj){
        if(this==obj){
            return true;
        }else if(obj==null){
            return false;
        }else if (obj instanceof Key) {
            Key k = (Key) obj;
            return x==k.x && y==k.y;
        } else {
            return false;
        }
    }
}
