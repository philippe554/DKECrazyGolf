package CrazyGolf.PhysicsEngine.Objects.Native;

import javafx.geometry.Point3D;

/**
 * Created by pmmde on 6/11/2016.
 */
public class Sphere {
    private static Point3D[] unitSphere=null;
    private static void makeSphere(){
        Point3D[] sphere = new Point3D[24];

        sphere[0]=new Point3D(0,0,1);
        sphere[1]=new Point3D(1,0,0);
        sphere[2]=new Point3D(0,1,0);

        sphere[3]=new Point3D(0,0,1);
        sphere[4]=new Point3D(-1,0,0);
        sphere[5]=new Point3D(0,1,0);

        sphere[6]=new Point3D(0,0,1);
        sphere[7]=new Point3D(1,0,0);
        sphere[8]=new Point3D(0,-1,0);

        sphere[9]=new Point3D(0,0,1);
        sphere[10]=new Point3D(-1,0,0);
        sphere[11]=new Point3D(0,-1,0);

        sphere[12]=new Point3D(0,0,-1);
        sphere[13]=new Point3D(1,0,0);
        sphere[14]=new Point3D(0,1,0);

        sphere[15]=new Point3D(0,0,-1);
        sphere[16]=new Point3D(-1,0,0);
        sphere[17]=new Point3D(0,1,0);

        sphere[18]=new Point3D(0,0,-1);
        sphere[19]=new Point3D(1,0,0);
        sphere[20]=new Point3D(0,-1,0);

        sphere[21]=new Point3D(0,0,-1);
        sphere[22]=new Point3D(-1,0,0);
        sphere[23]=new Point3D(0,-1,0);

        sphere = split(sphere);
        sphere = split(sphere);

        for(int i=0;i<sphere.length;i++){
            sphere[i]=sphere[i].normalize();
        }

        unitSphere=sphere;
    }
    private static Point3D[] split(Point3D[] input){
        Point3D[] output = new Point3D[input.length*4];

        for(int i=0;i<input.length;i+=3) {
            output[i*4+0]=input[i+0];
            output[i*4+1]=input[i+0].midpoint(input[i+1]);
            output[i*4+2]=input[i+0].midpoint(input[i+2]);

            output[i*4+3]=input[i+1];
            output[i*4+4]=input[i+1].midpoint(input[i+0]);
            output[i*4+5]=input[i+1].midpoint(input[i+2]);

            output[i*4+6]=input[i+2];
            output[i*4+7]=input[i+2].midpoint(input[i+0]);
            output[i*4+8]=input[i+2].midpoint(input[i+1]);

            output[i*4+9]=input[i+0].midpoint(input[i+1]);
            output[i*4+10]=input[i+1].midpoint(input[i+2]);
            output[i*4+11]=input[i+2].midpoint(input[i+0]);
        }

        return output;
    }
    public static float[] getSphere(Point3D offset,float size,float scale){
        if(unitSphere==null)makeSphere();

        float[] sphere = new float[unitSphere.length*4];

        for(int i=0;i<unitSphere.length;i++){
            sphere[i*4+0]= (float) (unitSphere[i].getX()*size+offset.getX());
            sphere[i*4+1]= (float) (unitSphere[i].getZ()*size+offset.getZ());
            sphere[i*4+2]= (float) (unitSphere[i].getY()*size+offset.getY());
            sphere[i*4+3]= scale;
        }

        return sphere;
    }
    public static float[] getSphereColor(float r,float g,float b){
        float[] color = new float[unitSphere.length*4];

        for(int i=0;i<unitSphere.length;i++){
            color[i*4+0]=r;
            color[i*4+1]=g;
            color[i*4+2]=b;
            color[i*4+3]=1.0f;
        }

        return color;
    }
}
