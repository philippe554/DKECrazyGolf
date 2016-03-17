
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.vp.*;
import javafx.geometry.Point3D;


public class Golf3D extends JComponent{
    // size of panel
    private static final int PWIDTH = 1200;
    private static final int PHEIGHT = 800;

    private SimpleUniverse su=null;
    private BranchGroup sceneBG;
    private BranchGroup sceneBall=null;
    private BranchGroup sceneArrow=null;
    private Transform3D t3dBall;
    private TransformGroup tgBall;
    private Transform3D t3dArrow;
    private TransformGroup tgArrow;
    private BoundingSphere bounds;

    private World world;
    private float scale;
    Canvas3D canvas3D;

    public Golf3D(float s)
    // A panel holding a 3D canvas
    {
        scale=s;

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

    public void loadWorld(World tworld)
    {
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



    private void createSceneGraph()
    // initilise the scene
    {
        sceneBG = new BranchGroup();
        sceneBG.setCapability(BranchGroup.ALLOW_DETACH);
        bounds = new BoundingSphere(new Point3d(0,0,0), 1000000);

        lightScene();         // add the lights
        addBackground();      // add the sky
        createScene();
        // j3dTree.recursiveApplyCapability( sceneBG );   // set capabilities for tree display
        //Ball(START);
        sceneBG.compile();   // fix the scene
    } // end of createSceneGraph()


    private void lightScene()
  /* One ambient light, 2 directional lights */
    {
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
    }  // end of lightScene()



    private void addBackground()
    // A blue sky
    { Background back = new Background();
        back.setApplicationBounds( bounds );
        back.setColor(0.17f, 0.65f, 0.92f);    // sky colour
        sceneBG.addChild( back );
    }



    private void orbitControls(Canvas3D c)
  /* OrbitBehaviour allows the user to rotate around the scene, and to
     zoom in and out.  */
    {
        OrbitBehavior orbit =new OrbitBehavior(c, OrbitBehavior.REVERSE_ROTATE|OrbitBehavior.REVERSE_TRANSLATE);
        orbit.setSchedulingBounds(bounds);

        ViewingPlatform vp = su.getViewingPlatform();
        vp.setViewPlatformBehavior(orbit);
    }



    private void initUserPosition()
    // Set the user's initial viewpoint using lookAt()
    {
        ViewingPlatform vp = su.getViewingPlatform();
        TransformGroup steerTG = vp.getViewPlatformTransform();

        Transform3D t3d = new Transform3D();
        steerTG.getTransform(t3d);

        // args are: viewer posn, where looking, up direction
        t3d.lookAt(new Point3d(-20,20,40),
                new Point3d((float)world.balls.get(0).place.getX()*scale,(float)world.balls.get(0).place.getY()*scale,(float)world.balls.get(0).place.getZ()*scale)
                , new Vector3d(0,0,1));

        t3d.invert();

        steerTG.setTransform(t3d);
    }
    public void setUserPosition(int i,int angle)
    {
        ViewingPlatform vp = su.getViewingPlatform();
        TransformGroup steerTG = vp.getViewPlatformTransform();

        Transform3D t3d = new Transform3D();

        // args are: viewer posn, where looking, up direction
        t3d.lookAt(new Point3d((float)world.balls.get(0).place.getX()*scale-Math.cos(angle*Math.PI/180.0),(float)world.balls.get(0).place.getY()*scale-Math.sin(angle*Math.PI/180.0),(float)world.balls.get(0).place.getZ()*scale+40),
                new Point3d((float)world.balls.get(0).place.getX()*scale,(float)world.balls.get(0).place.getY()*scale,(float)world.balls.get(0).place.getZ()*scale),
                new Vector3d(0,0,1));
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

        // position the sphere
        t3dBall = new Transform3D();
        t3dBall.set(new Vector3f((float)world.balls.get(0).place.getX()*scale,(float)world.balls.get(0).place.getY()*scale,(float)world.balls.get(0).place.getZ()*scale));
        tgBall = new TransformGroup(t3dBall);
        Sphere ball = new Sphere((float) world.balls.get(0).size*scale, blueApp);// set its radius and appearance
        ball.setPickable(true);

        tgBall.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tgBall.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        tgBall.addChild(ball);

        sceneBall = new BranchGroup();
        sceneBall.addChild(tgBall);
        sceneBall.setCapability(BranchGroup.ALLOW_DETACH);
        sceneBall.compile();
        su.addBranchGraph( sceneBall );
    }
    public void updateBall(){

        //t3dBall = new Transform3D();
        t3dBall.set(new Vector3f((float)world.balls.get(0).place.getX()*scale,(float)world.balls.get(0).place.getY()*scale,(float)world.balls.get(0).place.getZ()*scale));
        //tgBall = new TransformGroup();
        tgBall.setTransform(t3dBall);
        //initUserPosition();
    }
    public void createScene()
    {
        BranchGroup scene = new BranchGroup();

        for(int i=0;i<world.sides.size();i++)
        {
            ArrayList<Point3f> coords = new ArrayList();
            for(int j=0;j<3;j++) {
                coords.add(new Point3f((float) world.sides.get(i).points[j].getX()*scale, (float) world.sides.get(i).points[j].getY()*scale, (float) world.sides.get(i).points[j].getZ()*scale));
            }
            scene.addChild(new Triangle(coords,world.sides.get(i).color));
        }

        sceneBG.addChild(scene);
    }

    public void createArrow(Point3D ballCoor, Point3D aimVector){

        ArrayList<Point3f> arrowCoor = new ArrayList<Point3f>();
        arrowCoor.add(new Point3f((float)ballCoor.getX()*scale,(float)ballCoor.getY()*scale,(float)ballCoor.getZ()*scale));
        arrowCoor.add(new Point3f((float)aimVector.getX()*scale,(float)aimVector.getY()*scale,(float)aimVector.getZ()*scale));
        Arrow arrow = new Arrow(arrowCoor, new Color3f(1.0f,0.0f,0.0f));
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

    public void removeArrow()
    {
        if(sceneArrow!=null) {
            sceneArrow.detach();
            sceneArrow = null;
        }
    }

} 