package CrazyGolf.Game.OpenGL;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

/**
 * Created by pmmde on 6/15/2016.
 */
public class VAONormal {
    float vertices[];
    float colors[];
    float normals[];
    int VAO[];
    int vertexBufferId[];
    int colorBufferId[];
    int normalBufferId[];

    public VAONormal(GL3 gl, float[] points, float [] tColors,float[] tNormals, int verticeLoc, int colorLoc,int normalLoc) {
        VAO = generateVAOId(gl);
        vertices = points;
        colors = tColors;
        normals=tNormals;

        gl.glBindVertexArray( VAO[0]);
        vertexBufferId = this.generateBufferId(gl);
        colorBufferId = this.generateBufferId(gl);
        normalBufferId = generateBufferId(gl);

        this.bindBuffer(gl, vertexBufferId[0], vertices, verticeLoc);
        this.bindBuffer(gl, colorBufferId[0], colors, colorLoc);
        this.bindBuffer(gl, normalBufferId[0], normals, normalLoc);

    }
    public void cleanUp(GL3 gl){
        gl.glDeleteBuffers(1, vertexBufferId,0);
        gl.glDeleteBuffers(1, colorBufferId,0);
        gl.glDeleteVertexArrays(1, VAO,0);
    }

    void bindBuffer(GL3 gl, int bufferId, float[] dataArray, int dataLoc){
        // bind buffer for vertices and copy data into buffer
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, dataArray.length * Float.SIZE / 8, Buffers.newDirectFloatBuffer(dataArray), GL.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(dataLoc);
        gl.glVertexAttribPointer(dataLoc, 4, GL.GL_FLOAT, false, 0, 0);

    }
    protected int[] generateVAOId(GL3 gl) {
        // allocate an array of one element in order to strore
        // the generated id
        int[] idArray = new int[1];
        // let's generate
        gl.glGenVertexArrays(1, idArray, 0);
        // return the id
        return idArray;
    }
    protected int[] generateBufferId(GL3 gl) {
        // allocate an array of one element in order to strore
        // the generated id
        int[] idArray = new int[1];
        // let's generate
        gl.glGenBuffers( 1, idArray, 0);

        // return the id
        return idArray;
    }
}
