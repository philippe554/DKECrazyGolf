package CrazyGolf.PhysicsEngine;

import javafx.geometry.Point3D;

/**
 * Created by pmmde on 6/13/2016.
 */
public class Matrix {
    float[][] data;

    public Matrix(int i,int j){
        data=new float[i][j];
    }
    public Matrix(float[][] d){
        data=d;
    }
    public Matrix multiply(Matrix m){
        if(data[0].length==m.data.length) {
            Matrix result = new Matrix(data.length, m.data[0].length);
            for (int i = 0; i < result.data.length; i++) {
                for (int j = 0; j < result.data[i].length; j++) {
                    float sum = 0;
                    for (int k = 0; k < data[0].length; k++) {
                        sum+=data[i][k]*m.data[k][j];
                    }
                    result.data[i][j]=sum;
                }
            }
            return result;
        }
        return null;
    }
    public Point3D multiply(Point3D p){
        if(data[0].length==3 && data.length==3) {
            double x = data[0][0]*p.getX()+data[0][1]*p.getY()+data[0][2]*p.getZ();
            double y = data[1][0]*p.getX()+data[1][1]*p.getY()+data[1][2]*p.getZ();
            double z = data[2][0]*p.getX()+data[2][1]*p.getY()+data[2][2]*p.getZ();
            return new Point3D(x,y,z);
        }
        return null;
    }
    public static Matrix getRotatoinMatrix(float x,float y,float z){
        Matrix m1 = new Matrix(new float[][]{
                {1,0,0},
                {0, (float) Math.cos(x), (float) -Math.sin(x)},
                {0, (float) Math.sin(x), (float) Math.cos(x)}});
        Matrix m2 = new Matrix(new float[][]{
                {(float) Math.cos(y),0, (float) Math.sin(y)},
                {0, 1,0},
                {(float) -Math.sin(y),0, (float) Math.cos(y)}});
        Matrix m3 = new Matrix(new float[][]{
                {(float) Math.cos(z), (float) -Math.sin(z),0},
                {(float) Math.sin(z), (float) Math.cos(z),0},
                {0, 0,1}});
        return m1.multiply(m2).multiply(m3);
    }
    @Override public String toString(){
        String r="";
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                r+=data[i][j]+" ";
            }
            r+="\n";
        }
        return r;
    }
}
