
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import com.sun.j3d.utils.scenegraph.io.state.javax.media.j3d.BranchGroupState;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.vp.*;





public class Golf3D extends JPanel{
    // size of panel
    private static final int PWIDTH = 1200;
    private static final int PHEIGHT = 800;

    private SimpleUniverse su;
    private BranchGroup sceneBG;
    private BranchGroup scene=null;
    private Transform3D t3d;
    private TransformGroup tg;
    private BoundingSphere bounds;

    private World world;
    private float scale;

    public Golf3D(World w,float s)
    // A panel holding a 3D canvas
    {
        world=w;
        scale=s;

        setLayout( new BorderLayout() );
        setOpaque( false );
        setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setSize(3*PWIDTH/4,PHEIGHT);
        add(canvas3D);
        canvas3D.setFocusable(true);     // give focus to the canvas
        canvas3D.requestFocus();

        su = new SimpleUniverse(canvas3D);

        su.getViewer().getView().setBackClipDistance(100000000);

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
        bounds = new BoundingSphere(new Point3d(0,0,0), 1000000);

        lightScene();         // add the lights
        addBackground();      // add the sky
        createScene();
        //sceneBG.addChild(new FieldModeliser(file).getField());
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
        OrbitBehavior orbit =new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
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
        t3d = new Transform3D();
        t3d.set(new Vector3f((float)world.balls.get(0).place.getX()*scale,(float)world.balls.get(0).place.getY()*scale,(float)world.balls.get(0).place.getZ()*scale));
        tg = new TransformGroup(t3d);
        Sphere ball = new Sphere((float) world.balls.get(0).size*scale, blueApp);// set its radius and appearance
        ball.setPickable(true);


        // TransformGroup transformGroup = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        tg.addChild(ball);
        //sceneBG.addChild(transformGroup);

        // Create the drag behavior node
        MouseRotate behavior = new MouseRotate();
        behavior.setTransformGroup(tg);
        tg.addChild(behavior);
        behavior.setSchedulingBounds(bounds);

        scene = new BranchGroup();
        scene.addChild(tg);
        scene.compile();
        su.addBranchGraph( scene );
    }
    public void updateBall()
    {
        t3d.set(new Vector3f((float)world.balls.get(0).place.getX()*scale,(float)world.balls.get(0).place.getY()*scale,(float)world.balls.get(0).place.getZ()*scale));
        tg.setTransform(t3d);
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

} 