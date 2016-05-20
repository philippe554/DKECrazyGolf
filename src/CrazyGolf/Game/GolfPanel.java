package CrazyGolf.Game;

import CrazyGolf.PhysicsEngine.World;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import javafx.geometry.Point3D;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * Created by pmmde on 5/15/2016.
 */
public class GolfPanel extends JPanel{

    private static final int PWIDTH = 1200;
    private static final int PHEIGHT = 800;
    private static final float scale=0.1f;

    private SimpleUniverse su=null;
    private BranchGroup sceneBG;
    private BranchGroup sceneBall=null;
    private BranchGroup sceneArrow=null;
    private Transform3D t3dBall[];
    private TransformGroup tgBall[];
    private Transform3D t3dArrow;
    private TransformGroup tgArrow;
    private BoundingSphere bounds;

    private World world;
    Canvas3D canvas3D;

    public GolfPanel(){
        setLayout( new BorderLayout() );
        setOpaque( false );
        setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        canvas3D = new Canvas3D(config);
        canvas3D.setSize(3*PWIDTH/4,PHEIGHT);
        add(canvas3D);
        canvas3D.setFocusable(true);     // give focus to the canvas
        canvas3D.requestFocus();

        su = new SimpleUniverse(canvas3D);

        su.getViewer().getView().setBackClipDistance(100000000);
    }

    public void loadWorld(World tworld) {
        if(sceneBG!=null)sceneBG.detach();
        if(sceneBall!=null)sceneBall.detach();
        if(sceneArrow!=null)sceneArrow.detach();

        world=tworld;

        createSceneGraph();
        initUserPosition();        // set user's viewpoint
        orbitControls(canvas3D);   // controls for moving the viewpoint
        su.addBranchGraph( sceneBG );

        createBall();
    }
    private void createSceneGraph() {
        sceneBG = new BranchGroup();
        sceneBG.setCapability(BranchGroup.ALLOW_DETACH);
        bounds = new BoundingSphere(new Point3d(0,0,0), 1000000);

        lightScene();         // add the lights
        addBackground();      // add the sky
        createScene();
        // j3dTree.recursiveApplyCapability( sceneBG );   // set capabilities for tree display
        //Ball(START);
        sceneBG.compile();   // fix the scene
    }
    private void lightScene() {
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

        // Set up the ambient light
        AmbientLight ambientLightNode = new AmbientLight(white);
        ambientLightNode.setInfluencingBounds(bounds);
        sceneBG.addChild(ambientLightNode);

        // Set up the directional lights
        Vector3f light1Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);
        // left, down, backwards
        Vector3f light2Direction  = new Vector3f(1.0f, -1.0f, 1.0f);
        // right, down, forwards

        DirectionalLight light1 =
                new DirectionalLight(white, light1Direction);
        light1.setInfluencingBounds(bounds);
        sceneBG.addChild(light1);

        DirectionalLight light2 =
                new DirectionalLight(white, light2Direction);
        light2.setInfluencingBounds(bounds);
        sceneBG.addChild(light2);
    }
    private void addBackground() { Background back = new Background();
        back.setApplicationBounds( bounds );
        back.setColor(0.17f, 0.65f, 0.92f);    // sky colour
        sceneBG.addChild( back );
    }
    private void orbitControls(Canvas3D c)    {
        OrbitBehavior orbit =new OrbitBehavior(c, OrbitBehavior.REVERSE_ROTATE|OrbitBehavior.REVERSE_TRANSLATE);
        //orbit.setRotationCenter();
        orbit.setSchedulingBounds(bounds);
        ViewingPlatform vp = su.getViewingPlatform();
        vp.setViewPlatformBehavior(orbit);
    }
    private void initUserPosition() {
        ViewingPlatform vp = su.getViewingPlatform();
        TransformGroup steerTG = vp.getViewPlatformTransform();

        Transform3D t3d = new Transform3D();
        steerTG.getTransform(t3d);
        // args are: viewer posn, where looking, up direction
        t3d.lookAt(new Point3d((float)world.getBallPosition(0).getX()*scale+50,(float)world.getBallPosition(0).getY()*scale+50,(float)world.getBallPosition(0).getZ()*scale+50),
                new Point3d((float)world.getBallPosition(0).getX()*scale,(float)world.getBallPosition(0).getY()*scale,(float)world.getBallPosition(0).getZ()*scale)
                , new Vector3d(0,0,1));

        t3d.invert();

        steerTG.setTransform(t3d);
    }
    public void createBall(){
        // located at start
        // Create the blue appearance node
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f blue = new Color3f(Color.LIGHT_GRAY);
        Color3f grey = new Color3f(Color.DARK_GRAY);
        Color3f specular = new Color3f(0.9f, 0.9f, 0.9f);

        Material blueMat= new Material(blue, black,grey, specular, 70.0f);
        // sets ambient, emissive, diffuse, specular, shininess
        blueMat.setLightingEnable(true);

        Appearance blueApp = new Appearance();
        blueApp.setMaterial(blueMat);

        tgBall = new TransformGroup[world.getAmountBalls()];
        t3dBall = new Transform3D[world.getAmountBalls()];
        sceneBall = new BranchGroup();

        for(int i=0;i<world.getAmountBalls();i++) {
            // position the sphere
            t3dBall[i] = new Transform3D();
            t3dBall[i].set(new Vector3f((float) world.getBallPosition(i).getX() * scale, (float) world.getBallPosition(i).getY() * scale,
                    (float) world.getBallPosition(i).getZ() * scale));
            tgBall[i] = new TransformGroup(t3dBall[i]);
            Sphere ball = new Sphere((float) world.getBallSize(i) * scale, blueApp);// set its radius and appearance
            ball.setPickable(true);

            tgBall[i].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
            tgBall[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

            tgBall[i].addChild(ball);

            sceneBall.addChild(tgBall[i]);
        }
        sceneBall.setCapability(BranchGroup.ALLOW_DETACH);
        sceneBall.compile();
        su.addBranchGraph( sceneBall );
    }
    public void updateBall(){
        for(int i=0;i<world.getAmountBalls();i++) {
            t3dBall[i].set(new Vector3f((float) world.getBallPosition(i).getX() * scale, (float) world.getBallPosition(i).getY() * scale,
                    (float) world.getBallPosition(i).getZ() * scale));
            tgBall[i].setTransform(t3dBall[i]);
        }
        //initUserPosition();
    }
    public void createScene() {
        BranchGroup scene = new BranchGroup();

        for(int i=0;i<world.getAmountTriangles();i++)
        {
            ArrayList<Point3f> coords = new ArrayList();
            Point3D[] triangle = world.getTriangle(i);
            for(int j=0;j<3;j++) {
                coords.add(new Point3f((float) triangle[j].getX()*scale,
                        (float) triangle[j].getY()*scale,
                        (float) triangle[j].getZ()*scale));
            }
            scene.addChild(new Triangle(coords,world.getTriangleColor(i)));
        }

        for(int i=0;i<world.getAmountOfWater();i++)
        {
            Point3D[] points = world.getWaterPoints(i);
            ArrayList<Point3f> coords1 = new ArrayList();
            coords1.add(new Point3f((float)points[0].getX()*scale,(float)points[0].getY()*scale,(float)points[1].getZ()*scale));
            coords1.add(new Point3f((float)points[1].getX()*scale,(float)points[0].getY()*scale,(float)points[1].getZ()*scale));
            coords1.add(new Point3f((float)points[1].getX()*scale,(float)points[1].getY()*scale,(float)points[1].getZ()*scale));
            scene.addChild(new Triangle(coords1,world.getWaterColor(i)));
            ArrayList<Point3f> coords2 = new ArrayList();
            coords2.add(new Point3f((float)points[0].getX()*scale,(float)points[0].getY()*scale,(float)points[1].getZ()*scale));
            coords2.add(new Point3f((float)points[0].getX()*scale,(float)points[1].getY()*scale,(float)points[1].getZ()*scale));
            coords2.add(new Point3f((float)points[1].getX()*scale,(float)points[1].getY()*scale,(float)points[1].getZ()*scale));
            scene.addChild(new Triangle(coords2,world.getWaterColor(i)));
        }

        sceneBG.addChild(scene);
    }
    public void createArrow(Point3D ballCoor, Point3D aimVector){

        ArrayList<Point3f> arrowCoor = new ArrayList<Point3f>();
        arrowCoor.add(new Point3f((float)ballCoor.getX()*scale,(float)ballCoor.getY()*scale,(float)ballCoor.getZ()*scale));
        arrowCoor.add(new Point3f((float)aimVector.getX()*scale,(float)aimVector.getY()*scale,(float)aimVector.getZ()*scale));
        Arrow arrow = new Arrow(arrowCoor, new Color3f(0.0f,0.0f,1.0f));
        t3dArrow = new Transform3D();
        tgArrow = new TransformGroup(t3dArrow);
        tgArrow.addChild(arrow);
        if(sceneArrow!=null)
        {
            sceneArrow.detach();
        }
        sceneArrow = new BranchGroup();
        sceneArrow.addChild(tgArrow);
        sceneArrow.setCapability(BranchGroup.ALLOW_DETACH);
        sceneArrow.compile();
        su.addBranchGraph( sceneArrow );
    }
    public void removeArrow() {
        if(sceneArrow!=null) {
            sceneArrow.detach();
            sceneArrow = null;
        }
    }
}