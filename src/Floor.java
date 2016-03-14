
import java.awt.*;
import javax.media.j3d.*;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Text2D;
import javax.vecmath.*;
import java.util.ArrayList;


public class Floor{
    public final static int FLOOR_LEN = 20;
    public  final static int FLOOR_WIDTH = 6;

    // colours for floor
    private final static Color3f greenL = new Color3f(0.2f, 0.65f, 0.2f);
    private final static Color3f greenD = new Color3f(0.2f, 0.7f, 0.2f);
    private final static Color3f medRed = new Color3f(0.8f, 0.4f, 0.3f);
    private final static Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    private BranchGroup floorBG;


    public Floor(){
        // create tiles
        ArrayList greenLCoords = new ArrayList();
        ArrayList greenDCoords = new ArrayList();
        floorBG = new BranchGroup();

        boolean isGreenL;
        //go through the plane
        for(int z=-FLOOR_LEN; z <= (FLOOR_LEN)-1; z++) {
            if(z%2 == 0)
                isGreenL = true;
            else
                isGreenL = false;

            // set colour for new row
            for(int x=-FLOOR_WIDTH; x <= (FLOOR_WIDTH)-1; x++) {
                if (isGreenL)
                    createCoords(x, z, greenLCoords);
                else
                    createCoords(x, z, greenDCoords);

                isGreenL = !isGreenL;
            }
        }
        floorBG.addChild( new ColouredTiles(greenLCoords,greenL ) );
        floorBG.addChild( new ColouredTiles(greenDCoords, greenD) );

        GolfField();
        addHole();
    }

    public void GolfField(){
        //create wall
        for( int x = -FLOOR_WIDTH; x <= FLOOR_WIDTH; x++ ){
            for( int z = -FLOOR_LEN; z <= FLOOR_LEN; z++ ){
                if( x == -FLOOR_WIDTH || x == FLOOR_WIDTH || z == -FLOOR_LEN || z == FLOOR_LEN ){

                    Appearance app = new Appearance();
                    Box box = new Box(0.65f,0.64f,0.65f,app );

                    Color3f color = new Color3f(Color.GRAY);
                    Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
                    Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
                    Material colMat = new Material(color,black,color,white,70f);
                    colMat.setLightingEnable(true);
                    app.setMaterial(colMat);


                    TransformGroup tg = new TransformGroup( );
                    Transform3D t3d = new Transform3D( );
                    t3d.setTranslation( new Vector3d(x, 0.65, z ) );
                    tg.setTransform( t3d );
                    tg.addChild(box);
                    floorBG.addChild( tg );
                }
            }
        }
    }
    private void createCoords(int x, int z, ArrayList coords){
        // Coords for a light green or darkgreen square,
        // its left hand corner at (x,0,z)
        // points created in counter-clockwise order
        Point3f p1 = new Point3f(x, 0.0f, z+1.0f);
        Point3f p2 = new Point3f(x+1.0f, 0.0f, z+1.0f);
        Point3f p3 = new Point3f(x+1.0f, 0.0f, z);
        Point3f p4 = new Point3f(x, 0.0f, z);
        coords.add(p1); coords.add(p2);
        coords.add(p3); coords.add(p4);
    }


    private void addHole(){
        Color3f color = new Color3f(Color.black);
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

        Material blueMat= new Material(color,black,color,white,Float.MAX_VALUE);
        // sets ambient, emissive, diffuse, specular, shininess
        blueMat.setLightingEnable(true);

        Appearance blueApp = new Appearance();
        blueApp.setMaterial(blueMat);

        // position the sphere
        Transform3D t3d = new Transform3D();
        t3d.set( new Vector3f(0,0,-FLOOR_LEN+3.0f));
        TransformGroup tg = new TransformGroup(t3d);
        tg.addChild(new Cylinder(0.5f,0.05f,blueApp));   // set its radius and appearance

        floorBG.addChild(tg);
    }

    public BranchGroup getBG(){
        return floorBG;  }


}

