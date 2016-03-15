import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;

public class FieldModeliser {

    private final static int PLANE_LEN = 40;
    private  final static int PLANE_WIDTH = 40;

    private static final Point3d USERPOSN = new Point3d(0,15,40);
    private static final float BALLSIZE = 0.30f ;
    private static final float WALLH = 0.50f ;

    private int xStart;
    private int zStart;

    private BoundingSphere bounds;
    private BranchGroup fieldBG;

    private char[][] field = new char[PLANE_LEN][PLANE_WIDTH];


    public FieldModeliser(String fileName){
        fieldBG = new BranchGroup();
        readFile(fileName);
        buildField(field);
    }



    // read the map plan from file
    public void readFile(String file){

        System.out.println("Reading plan from " + file);
        try {
            BufferedReader br = new BufferedReader( new FileReader(file));
            String line;
            char charLine[];
            int numRows = 0;

            while((numRows < PLANE_LEN) && ((line = br.readLine()) != null)) {
                System.out.println(line);
                charLine = line.toCharArray();
                int x=0;

                while((x < PLANE_WIDTH) && (x < charLine.length)) {// ignore any extra chars

                    field[numRows][x] = charLine[x];
                    x++;
                }
                numRows++;
            }
            br.close();
        }
        catch (IOException e)
        { System.out.println("Error reading field plan from " + file);
            System.exit(0);
        }

    }

    public void buildField(char[][] plan ){

        char ch;
        char chz;
        char chx;

        int tile =0;
        for (int z=0; z<PLANE_LEN-1; z++) {
            for (int x=0; x<PLANE_WIDTH-1; x++) {
                ch = field[z][x];
                chz = field[z+1][x];
                chx = field[z][x+1];

                if (ch == 's') {    // starting position
                    xStart = x;
                    zStart = z;
                    buildBall(x,z);
                    field[z][x] = ' '; // clear cell

                }
                else if (ch == 'w') {
                    buildWall(x, z);
                    if(ch=='|' && !(Character.isSpaceChar(field[z+1][x])) && !(Character.isSpaceChar(field[z][x+1]))){
                        buildGrass(tile,x,z) ;
                        tile++;
                    }
                }
                else if (ch == 'h') {
                    buildHole(x, z) ;
                    buildGrass(tile,x,z) ;
                    tile++;
                }

                else if(ch=='|'){
                    buildGrass(tile,x,z) ;
                    tile++;
                }

            }
        }
    }

    public void buildWall(int xPos,int zPos){
        // create a wall at (x,z)
        Appearance app = new Appearance();
        Box box = new Box(WALLH,WALLH-0.01f,WALLH,app );

        Color3f color = new Color3f(Color.GRAY);
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
        Material colMat = new Material(color,black,color,white,70f);
        colMat.setLightingEnable(true);
        app.setMaterial(colMat);

        TransformGroup tg = new TransformGroup( );
        Transform3D t3d = new Transform3D( );
        t3d.setTranslation( new Vector3d(xPos,WALLH, zPos ) );
        tg.setTransform( t3d );
        tg.addChild(box);

        fieldBG.addChild(tg);
    }

    public void buildBall(int xPos,int zPos){
        // create ball at starting point

        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f lightGrey = new Color3f(Color.LIGHT_GRAY);
        Color3f darkGrey = new Color3f(Color.DARK_GRAY);
        Color3f specular = new Color3f(0.9f, 0.9f, 0.9f);

        // sets ambient, emissive, diffuse, specular, shininess
        Material blueMat= new Material(lightGrey, black,darkGrey, specular, 70.0f);

        blueMat.setLightingEnable(true);

        Appearance app = new Appearance();
        app.setMaterial(blueMat);

        // position the sphere
        Transform3D t3d = new Transform3D();
        t3d.setTranslation( new Vector3d(xStart, BALLSIZE, zStart-1) );
        TransformGroup tg = new TransformGroup(t3d);
        Sphere ball = new Sphere(BALLSIZE, app);// set its radius and appearance
        ball.setPickable(true);


        // TransformGroup transformGroup = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        tg.addChild(ball);
        fieldBG.addChild(tg);
    }

    public void buildHole(int xPos,int zPos){

        Color3f color = new Color3f(Color.black);
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

        Material blueMat= new Material(color,black,color,white,Float.MAX_VALUE);
        // sets ambient, emissive, diffuse, specular, shininess
        blueMat.setLightingEnable(true);

        Appearance blueApp = new Appearance();
        blueApp.setMaterial(blueMat);

        // position the hole
        Transform3D t3d = new Transform3D();
        t3d.set( new Vector3f(xPos,0,zPos+0.3f));
        TransformGroup tg = new TransformGroup(t3d);
        tg.addChild(new Cylinder(0.5f,0.05f,blueApp));   // set its radius and appearance

        fieldBG.addChild(tg);
    }

    public void buildGrass(int cntr,int xPos,int zPos){

        Color3f greenL = new Color3f(0.2f, 0.65f, 0.2f);
        Color3f greenD = new Color3f(0.2f, 0.7f, 0.2f);

        ArrayList<Point3f> greenLCoords = new ArrayList<Point3f>();
        ArrayList<Point3f> greenDCoords = new ArrayList<Point3f>();

        boolean isGreenL;

        //DarkGreen or LightGreen ?
        if(cntr%2 == 0)
            isGreenL = true;
        else
            isGreenL = false;



        //create the tiles
        /*if (isGreenL){
            createCoords(xPos, zPos, greenLCoords);
            fieldBG.addChild(new ColouredTiles(greenLCoords,greenL ));
        }else{
            createCoords(xPos, zPos, greenDCoords);
            fieldBG.addChild(new ColouredTiles(greenDCoords,greenD ));
        }*/


    }

    private void createCoords(int x, int z, ArrayList<Point3f> coords){
        // Coords for a light green or dark green square,
        // its left hand corner at (x,0,z)
        // points created in counter-clockwise order
        Point3f p1 = new Point3f(x, 0.0f, z+1.0f);
        Point3f p2 = new Point3f(x+1.0f, 0.0f, z+1.0f);
        Point3f p3 = new Point3f(x+1.0f, 0.0f, z);
        Point3f p4 = new Point3f(x, 0.0f, z);
        coords.add(p1); coords.add(p2);
        coords.add(p3); coords.add(p4);
    }

    public BranchGroup getField(){
        return fieldBG;
    }


}

