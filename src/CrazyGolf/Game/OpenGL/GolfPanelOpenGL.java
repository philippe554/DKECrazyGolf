package CrazyGolf.Game.OpenGL;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import CrazyGolf.PhysicsEngine.WorldContainer;
import CrazyGolf.PhysicsEngine.Objects.WorldObject;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import javafx.geometry.Point3D;

/**
 * inspired from http://www.lighthouse3d.com/cg-topics/code-samples/opengl-3-3-glsl-1-5-sample/
 *
 */
public class GolfPanelOpenGL implements GLEventListener,MouseMotionListener,MouseListener,MouseWheelListener,KeyListener{
    @Override public void mouseDragged(MouseEvent e) {
        xAngle -= (xAngleStart - e.getX()) * 0.2;
        yAngle += (yAngleStart - e.getY()) * 0.2;
        if (yAngle < -89) yAngle = -89;
        if (yAngle > 89) yAngle = 89;
        xAngleStart=e.getX();
        yAngleStart=e.getY();
    }
    @Override public void mouseMoved(MouseEvent e) {

    }
    @Override public void mouseClicked(MouseEvent e) {

    }
    @Override public void mousePressed(MouseEvent e) {
        xAngleStart=e.getX();
        yAngleStart=e.getY();
    }
    @Override public void mouseReleased(MouseEvent e) {

    }
    @Override public void mouseEntered(MouseEvent e) {

    }
    @Override public void mouseExited(MouseEvent e) {

    }
    @Override public void mouseWheelMoved(MouseWheelEvent e) {
        zoom+=e.getPreciseWheelRotation()*0.5;
        if(zoom<0.1)zoom=0.1;
        if(zoom>20)zoom=20;
    }
    @Override public void keyTyped(KeyEvent e) {

    }
    @Override public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_A)aPressed=true;
        if(e.getKeyCode() == KeyEvent.VK_S)sPressed=true;
        if(e.getKeyCode() == KeyEvent.VK_D)dPressed=true;
        if(e.getKeyCode() == KeyEvent.VK_W)wPressed=true;
    }
    @Override public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_A)aPressed=false;
        if(e.getKeyCode() == KeyEvent.VK_S)sPressed=false;
        if(e.getKeyCode() == KeyEvent.VK_D)dPressed=false;
        if(e.getKeyCode() == KeyEvent.VK_W)wPressed=false;
    }
    public void updateKeys(){
        double scaler=50;
        if(wPressed){
            xOffset-=Math.cos(xAngle/180.0*Math.PI)*zoom/scaler;
            yOffset-=Math.sin(xAngle/180.0*Math.PI)*zoom/scaler;
        }
        if(sPressed){
            xOffset+=Math.cos(xAngle/180.0*Math.PI)*zoom/scaler;
            yOffset+=Math.sin(xAngle/180.0*Math.PI)*zoom/scaler;
        }
        if(aPressed){
            xOffset+=Math.cos((xAngle+90)/180.0*Math.PI)*zoom/scaler;
            yOffset+=Math.sin((xAngle+90)/180.0*Math.PI)*zoom/scaler;
        }
        if(dPressed){
            xOffset+=Math.cos((xAngle-90)/180.0*Math.PI)*zoom/scaler;
            yOffset+=Math.sin((xAngle-90)/180.0*Math.PI)*zoom/scaler;
        }
    }

    enum ShaderType{ VertexShader, FragmentShader}

    int programID;
    int vertexLoc, colorLoc;
    int projMatrixLoc, viewMatrixLoc;

    // storage for Matrices
    float projMatrix[] = new float[16];
    float viewMatrix[] = new float[16];

    WorldContainer world;
    Map<Integer,OpenGLTriangleSet> triangles;
    float scale=200;

    public int xAngle=0;
    public int yAngle=0;
    public int xAngleStart=0;
    public int yAngleStart=0;
    public double zoom=2;
    public float xOffset=0;
    public float yOffset=0;
    public boolean aPressed=false;
    public boolean sPressed=false;
    public boolean dPressed=false;
    public boolean wPressed=false;

    public GolfPanelOpenGL(){
        world = new WorldContainer();
    }

    void crossProduct(float a[], float b[], float res[]) {

        res[0] = a[1] * b[2] - b[1] * a[2];
        res[1] = a[2] * b[0] - b[2] * a[0];
        res[2] = a[0] * b[1] - b[0] * a[1];
    }
    void normalize(float a[]) {

        float mag = (float) Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);

        a[0] /= mag;
        a[1] /= mag;
        a[2] /= mag;
    }

    void setIdentityMatrix(float[] mat, int size) {

        // fill matrix with 0s
        for (int i = 0; i < size * size; ++i)
            mat[i] = 0.0f;

        // fill diagonal with 1s
        for (int i = 0; i < size; ++i)
            mat[i + i * size] = 1.0f;
    }
    void multMatrix(float[] a, float[] b) {

        float[] res = new float[16];

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                res[j * 4 + i] = 0.0f;
                for (int k = 0; k < 4; ++k) {
                    res[j * 4 + i] += a[k * 4 + i] * b[j * 4 + k];
                }
            }
        }
        System.arraycopy(res, 0, a, 0, 16);
    }
    void setTranslationMatrix(float[] mat, float x, float y, float z) {

        setIdentityMatrix(mat, 4);
        mat[12] = x;
        mat[13] = y;
        mat[14] = z;
    }
    float[] buildProjectionMatrix(float fov, float ratio, float nearP, float farP, float[] projMatrix) {

        float f = 1.0f / (float) Math.tan(fov * (Math.PI / 360.0));

        setIdentityMatrix(projMatrix, 4);

        projMatrix[0] = f / ratio;
        projMatrix[1 * 4 + 1] = f;
        projMatrix[2 * 4 + 2] = (farP + nearP) / (nearP - farP);
        projMatrix[3 * 4 + 2] = (2.0f * farP * nearP) / (nearP - farP);
        projMatrix[2 * 4 + 3] = -1.0f;
        projMatrix[3 * 4 + 3] = 0.0f;

        return projMatrix;
    }
    float[] setCamera(float posX, float posY, float posZ, float lookAtX, float lookAtY, float lookAtZ, float[] viewMatrix) {

        float[] dir = new float[3];
        float[] right = new float[3];
        float[] up = new float[3];

        up[0] = 0.0f;
        up[1] = 1.0f;
        up[2] = 0.0f;

        dir[0] = (lookAtX - posX);
        dir[1] = (lookAtY - posY);
        dir[2] = (lookAtZ - posZ);
        normalize(dir);

        crossProduct(dir, up, right);
        normalize(right);

        crossProduct(right, dir, up);
        normalize(up);

        float[] aux = new float[16];

        viewMatrix[0] = right[0];
        viewMatrix[4] = right[1];
        viewMatrix[8] = right[2];
        viewMatrix[12] = 0.0f;

        viewMatrix[1] = up[0];
        viewMatrix[5] = up[1];
        viewMatrix[9] = up[2];
        viewMatrix[13] = 0.0f;

        viewMatrix[2] = -dir[0];
        viewMatrix[6] = -dir[1];
        viewMatrix[10] = -dir[2];
        viewMatrix[14] = 0.0f;

        viewMatrix[3] = 0.0f;
        viewMatrix[7] = 0.0f;
        viewMatrix[11] = 0.0f;
        viewMatrix[15] = 1.0f;

        setTranslationMatrix(aux, -posX, -posY, -posZ);

        multMatrix(viewMatrix, aux);

        return viewMatrix;
    }

    protected void renderScene(GL3 gl) {

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        /*setCamera(	0.5f, 0.5f,  2,
                0.5f, 0.5f, -1,
                this.viewMatrix);*/

        gl.glUseProgram(this.programID);

        // must be called after glUseProgram
        // set the view and the projection matrix
        gl.glUniformMatrix4fv( this.projMatrixLoc, 1, false, this.projMatrix, 0);
        gl.glUniformMatrix4fv( this.viewMatrixLoc, 1, false, this.viewMatrix, 0);

        /*gl.glBindVertexArray(this.triangleVAO);
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 3);

        gl.glBindVertexArray(this.axisVAO);
        gl.glDrawArrays(GL.GL_LINES, 0, 4);*/

        triangles.values().forEach(e->{
            gl.glBindVertexArray(e.VAO[0]);
            gl.glDrawArrays(GL.GL_TRIANGLES, 0, e.vertices.length/4);
        });

        // Check out error
        int error = gl.glGetError();
        if(error!=0){
            System.err.println("ERROR on render : " + error);}
    }

    @Override public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        this.programID = this.newProgram(gl);
        this.setupBuffers(gl);
    }
    @Override public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        float ratio;
        // Prevent a divide by zero, when window is too short
        // (you can't make a window of zero width).
        if (height == 0)
            height = 1;

        ratio = (1.0f * width) / height;
        this.projMatrix = buildProjectionMatrix(53.13f, ratio, 0.2f, 40.0f, this.projMatrix);
    }
    @Override public void display(GLAutoDrawable drawable) {

        setCamera((float) (xOffset + Math.cos(xAngle * Math.PI / 180.0)*zoom), (float)(zoom),  (float) (yOffset + Math.sin(xAngle * Math.PI / 180.0)*zoom),
                xOffset, 0,yOffset,
                this.viewMatrix);

        GL3 gl = drawable.getGL().getGL3();

        world.terrain.updateTerain(new Point3D(xOffset*scale,yOffset*scale,0));

        Integer wo2 = world.terrain.getNextRemoveObject();
        while(wo2!=null){
            if(triangles.containsKey(wo2)){
                triangles.get(wo2).cleanUp(gl);
                triangles.remove(wo2);
            }
            wo2 = world.terrain.getNextRemoveObject();
        }

        WorldObject wo1 = world.terrain.getNextNewObject();
        while(wo1!=null){
            addObject(gl,wo1);
            wo1 = world.terrain.getNextNewObject();
        }

        renderScene(gl);
    }
    @Override public void dispose(GLAutoDrawable drawable) {
    }

    int newProgram(GL3 gl) {
        // create the two shader and compile them
        int v = this.newShaderFromCurrentClass(gl, "vertex.shader", ShaderType.VertexShader);
        int f = this.newShaderFromCurrentClass(gl, "fragment.shader", ShaderType.FragmentShader);

        int p = this.createProgram(gl, v, f);

        gl.glBindFragDataLocation(p, 0, "outColor");

        this.vertexLoc = gl.glGetAttribLocation( p, "position");
        this.colorLoc = gl.glGetAttribLocation( p, "color");

        this.projMatrixLoc = gl.glGetUniformLocation( p, "projMatrix");
        this.viewMatrixLoc = gl.glGetUniformLocation( p, "viewMatrix");

        return p;
    }
    private int createProgram(GL3 gl, int vertexShaderId, int fragmentShaderId) {
        // generate the id of the program
        int programId = gl.glCreateProgram();
        // attach the two shader
        gl.glAttachShader(programId, vertexShaderId);
        gl.glAttachShader(programId, fragmentShaderId);
        // link them
        gl.glLinkProgram(programId);

        return programId;
    }
    int newShaderFromCurrentClass(GL3 gl, String fileName, ShaderType type){
        // load the source
        String shaderSource = this.readFile( fileName);
        // define the shaper type from the enum
        int shaderType = type==ShaderType.VertexShader?GL3.GL_VERTEX_SHADER:GL3.GL_FRAGMENT_SHADER;
        // create the shader id
        int id = gl.glCreateShader(shaderType);
        //  link the id and the source
        gl.glShaderSource(id, 1, new String[] { shaderSource }, null);
        //compile the shader
        gl.glCompileShader(id);

        return id;
    }
    protected String readFile(String fileName) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    void setupBuffers(GL3 gl) {
        triangles=new HashMap<>();
        /*for(int i=0;i<world.getAmountWorldObjects();i++){
            addObject(gl,world.getWorldObject(i));
        }
        System.out.println("World loaded: "+triangles.size());*/
        /*
        // generate the IDs
        this.triangleVAO = this.generateVAOId(gl);
        this.axisVAO = this.generateVAOId(gl);

        // create the buffer and link the data with the location inside the vertex shader
        this.newFloatVertexAndColorBuffers(gl, this.triangleVAO,
                this.vertices, this.colorArray, this.vertexLoc, this.colorLoc);
        this.newFloatVertexAndColorBuffers(gl, this.axisVAO,
                this.verticesAxis, this.colorAxis, this.vertexLoc, this.colorLoc);*/
    }
    void addObject(GL3 gl, WorldObject object){
        if(!object.mergeParent) {
            float[] points = getObjectPoints(object);
            float[] colors = getObjectColors(object);
            triangles.put(object.getID(), new OpenGLTriangleSet(gl, points, colors, vertexLoc, colorLoc));
        }
        for (int i = 0; i < object.getAmountSubObjects(); i++) {
            addObject(gl, object.getSubObject(i));
        }
    }
    public float[] getObjectPoints(WorldObject object){
        float[] points = new float[object.getAmountSides()*12];
        for(int i=0;i<object.getAmountSides();i++) {
            points[i * 12 + 0] = (float) object.getTriangle(i,0).getX();
            points[i * 12 + 1] = (float) object.getTriangle(i,0).getZ();
            points[i * 12 + 2] = (float) object.getTriangle(i,0).getY();
            points[i * 12 + 3] = scale;
            points[i * 12 + 4] = (float) object.getTriangle(i,1).getX();
            points[i * 12 + 5] = (float) object.getTriangle(i,1).getZ();
            points[i * 12 + 6] = (float) object.getTriangle(i,1).getY();
            points[i * 12 + 7] = scale;
            points[i * 12 + 8] = (float) object.getTriangle(i,2).getX();
            points[i * 12 + 9] = (float) object.getTriangle(i,2).getZ();
            points[i * 12 + 10] = (float) object.getTriangle(i,2).getY();
            points[i * 12 + 11] = scale;
        }
        for(int i=0;i<object.getAmountSubObjects();i++) {
            if(object.getSubObject(i).mergeParent){
                points=merge(points,getObjectPoints(object.getSubObject(i)));
            }
        }
        return points;
    }
    public float[] getObjectColors(WorldObject object){
        float[] colors = new float[object.getAmountSides()*12];
        for(int i=0;i<object.getAmountSides();i++) {
            colors[i*12+0]=(float) object.getTriangleColor(i).getX();
            colors[i*12+1]=(float) object.getTriangleColor(i).getY();
            colors[i*12+2]=(float) object.getTriangleColor(i).getZ();
            colors[i*12+3]=1.0f;
            colors[i*12+4]=(float) object.getTriangleColor(i).getX();
            colors[i*12+5]=(float) object.getTriangleColor(i).getY();
            colors[i*12+6]=(float) object.getTriangleColor(i).getZ();
            colors[i*12+7]=1.0f;
            colors[i*12+8]=(float) object.getTriangleColor(i).getX();
            colors[i*12+9]=(float) object.getTriangleColor(i).getY();
            colors[i*12+10]=(float) object.getTriangleColor(i).getZ();
            colors[i*12+11]=1.0f;
        }
        for(int i=0;i<object.getAmountSubObjects();i++) {
            if(object.getSubObject(i).mergeParent){
                colors=merge(colors,getObjectColors(object.getSubObject(i)));
            }
        }
        return colors;
    }
    public static float[] merge(float[] first, float[] second) {
        float[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public String printProgramInfoLog(GL3 gl, int obj) {
        // get the GL info log
        final int logLen = getProgramParameter(gl, obj, GL3.GL_INFO_LOG_LENGTH);
        if (logLen <= 0)
            return "";

        // Get the log
        final int[] retLength = new int[1];
        final byte[] bytes = new byte[logLen + 1];
        gl.glGetProgramInfoLog(obj, logLen, retLength, 0, bytes, 0);
        final String logMessage = new String(bytes);

        return logMessage;
    }
    public String getShaderInfoLog(GL3 gl, int obj) {
        // Otherwise, we'll get the GL info log
        final int logLen = getShaderParameter(gl, obj, GL3.GL_INFO_LOG_LENGTH);
        if (logLen <= 0)
            return "";

        // Get the log
        final int[] retLength = new int[1];
        final byte[] bytes = new byte[logLen + 1];
        gl.glGetShaderInfoLog(obj, logLen, retLength, 0, bytes, 0);
        final String logMessage = new String(bytes);

        return String.format("ShaderLog: %s", logMessage);
    }
    public int getProgramParameter(GL3 gl, int obj, int paramName) {
        final int params[] = new int[1];
        gl.glGetProgramiv(obj, paramName, params, 0);
        return params[0];
    }
    private int getShaderParameter(GL3 gl, int obj, int paramName) {
        final int params[] = new int[1];
        gl.glGetShaderiv(obj, paramName, params, 0);
        return params[0];
    }

    public static void main(String[] args) {
        // allocate the openGL application
        GolfPanelOpenGL sample = new GolfPanelOpenGL();

        JFrame frame = new JFrame("OpenGL tester");
        frame.setBounds(100, 100, 1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GLProfile glp = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glp);
        GLCanvas glCanvas = new GLCanvas(glCapabilities);

        glCanvas.setFocusable(true);
        glCanvas.addGLEventListener(sample);
        glCanvas.addMouseListener(sample);
        glCanvas.addMouseMotionListener(sample);
        glCanvas.addMouseWheelListener(sample);
        glCanvas.addKeyListener(sample);

        frame.add(glCanvas);

        // display it and let's go
        frame.setVisible(true);

        while(true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sample.updateKeys();
            glCanvas.repaint();
            sample.world.terrain.run();
        }
    }
}