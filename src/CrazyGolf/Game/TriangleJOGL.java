package CrazyGolf.Game;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import javafx.geometry.Point3D;

/**
 * Created by pmmde on 5/23/2016.
 */
public class TriangleJOGL {
    float vertices[];
    float colorArray[];
    int VAO;

    public TriangleJOGL(GL3 gl,Point3D[] points, Point3D[]colors, int verticeLoc, int colorLoc){
        VAO=generateVAOId(gl);
        vertices=new float[]{(float)points[0].getX(),(float)points[0].getZ(),(float)points[0].getY(),10.0f,
                (float)points[1].getX(),(float)points[1].getZ(),(float)points[1].getY(),10.0f,
                (float)points[2].getX(),(float)points[2].getZ(),(float)points[2].getY(),10.0f,};
        colorArray=new float[]{(float)colors[0].getX(),(float)colors[0].getY(),(float)colors[0].getZ(),1.0f,
                (float)colors[1].getX(),(float)colors[1].getY(),(float)colors[1].getZ(),1.0f,
                (float)colors[2].getX(),(float)colors[2].getY(),(float)colors[2].getZ(),1.0f,};
        newFloatVertexAndColorBuffers(gl, VAO,vertices, colorArray, verticeLoc, colorLoc);
    }
    void newFloatVertexAndColorBuffers(GL3 gl, int vaoId, float[] verticesArray, float[] colorArray, int verticeLoc, int colorLoc){
        // bind the correct VAO id
        gl.glBindVertexArray( vaoId);
        // Generate two slots for the vertex and color buffers
        int vertexBufferId = this.generateBufferId(gl);
        int colorBufferId = this.generateBufferId(gl);

        // bind the two buffer
        this.bindBuffer(gl, vertexBufferId, verticesArray, verticeLoc);
        this.bindBuffer(gl, colorBufferId, colorArray, colorLoc);
    }
    void bindBuffer(GL3 gl, int bufferId, float[] dataArray, int dataLoc){
        // bind buffer for vertices and copy data into buffer
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, dataArray.length * Float.SIZE / 8,
                Buffers.newDirectFloatBuffer(dataArray), GL.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(dataLoc);
        gl.glVertexAttribPointer(dataLoc, 4, GL.GL_FLOAT, false, 0, 0);

    }
    protected int generateVAOId(GL3 gl) {
        // allocate an array of one element in order to strore
        // the generated id
        int[] idArray = new int[1];
        // let's generate
        gl.glGenVertexArrays(1, idArray, 0);
        // return the id
        return idArray[0];
    }
    protected int generateBufferId(GL3 gl) {
        // allocate an array of one element in order to strore
        // the generated id
        int[] idArray = new int[1];
        // let's generate
        gl.glGenBuffers( 1, idArray, 0);

        // return the id
        return idArray[0];
    }
}
