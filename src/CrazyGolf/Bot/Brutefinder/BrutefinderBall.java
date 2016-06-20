package CrazyGolf.Bot.Brutefinder;

import CrazyGolf.PhysicsEngine.Objects.Parts.Ball;
import javafx.geometry.Point3D;

/**
 * Created by pmmde on 5/1/2016.
 */
public class BrutefinderBall extends Ball {
    public int i;
    public int j;
    public int k;
    public int dir;
    public int pow;
    public int velocityCounter=0;
    public int totalCounter=0;
    public boolean corrector;
    public Point3D predictorLocation;

    public BrutefinderBall(double tsize, Point3D tplace, int ti,int tj,int tk,int tdir,int tpow) {
        super(tsize, tplace);
        i=ti;
        j=tj;
        k=tk;
        dir=tdir;
        pow=tpow;
        corrector=false;
    }
}
