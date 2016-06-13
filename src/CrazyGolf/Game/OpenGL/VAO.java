package CrazyGolf.Game.OpenGL;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

/**
 * Created by pmmde on 6/12/2016.
 */
public class VAO {
    float vertices[];
    float colors[];
    int VAO[];
    int vertexBufferId[];
    int colorBufferId[];

    public VAO(GL3 gl, float[] points, float [] tColors, int verticeLoc, int colorLoc) {
        VAO = generateVAOId(gl);
        vertices = points;
        colors = tColors;
        newFloatVertexAndColorBuffers(gl, VAO[0], vertices, colors, verticeLoc, colorLoc);
    }
    public void cleanUp(GL3 gl){
        gl.glDeleteBuffers(1, vertexBufferId,0);
        gl.glDeleteBuffers(1, colorBufferId,0);
        gl.glDeleteVertexArrays(1, VAO,0);
    }

    void newFloatVertexAndColorBuffers(GL3 gl, int vaoId, float[] verticesArray, float[] colorArray, int verticeLoc, int colorLoc){
        // bind the correct VAO id
        gl.glBindVertexArray( vaoId);
        // Generate two slots for the vertex and color buffers
        vertexBufferId = this.generateBufferId(gl);
        colorBufferId = this.generateBufferId(gl);

        // bind the two buffer
        this.bindBuffer(gl, vertexBufferId[0], verticesArray, verticeLoc);
        this.bindBuffer(gl, colorBufferId[0], colorArray, colorLoc);
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
