package CrazyGolf.PhysicsEngine;

import javafx.geometry.Point3D;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.jocl.CL.*;
import static org.jocl.CL.clFinish;
import static org.jocl.CL.clReleaseMemObject;

/**
 * Created by pmmde on 5/15/2016.
 */
public class WorldGPUBotOpti extends WorldGPU {
    protected cl_kernel kernelBotOpti;
    protected cl_mem memSizes;

    public WorldGPUBotOpti(LinkedList<String> input){
        super(input);
        kernelBotOpti = clCreateKernel(program, "botOpti", null);

        clSetKernelArg(kernelBotOpti, 0, Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernelBotOpti, 1, Sizeof.cl_mem, Pointer.to(memObjects[4]));
        clSetKernelArg(kernelBotOpti, 2, Sizeof.cl_mem, Pointer.to(memObjects[5]));
        clSetKernelArg(kernelBotOpti, 3, Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernelBotOpti, 4, Sizeof.cl_mem, Pointer.to(memObjects[2]));

        int srcSizes[] = {sides.size(),edges.size(),points.size()};
        Pointer pSizes = Pointer.to(srcSizes);
        memSizes = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_int * srcSizes.length, pSizes, null);

        clSetKernelArg(kernelBotOpti, 6, Sizeof.cl_mem, Pointer.to(memSizes));
    }
    @Override
    public synchronized void step(boolean useBallBallCollision){
        if(!editMode) {
            double maxV = -1;
            double ballSize = 0;
            for (int i = 0; i < balls.size(); i++) {
                if (balls.get(i).velocity.magnitude() > maxV) {
                    maxV = balls.get(i).velocity.magnitude();
                    ballSize = balls.get(i).size;
                }
            }
            int subSteps=((int) (maxV / ballSize * 1.1*precision) + 1);
            stepCalc(subSteps,useBallBallCollision);
        }
    }
    @Override
    public synchronized void stepSimulated(ArrayList<Ball> simBalls, boolean useBallBallCollision){
        ArrayList<Ball> original=balls;
        balls=simBalls;
        long start = System.currentTimeMillis();
        step(useBallBallCollision);
        //System.out.println(System.currentTimeMillis()-start);
        balls=original;
    }
    private void stepCalc(int subframes,boolean useBallBallCollision) {
        double subframeInv = 1.0 / (double)(subframes);
        float friction[]=new float[balls.size()];
        for(int i=0;i<balls.size();i++)
        {
            friction[i]=0.0f;
        }
        for (int l = 0; l < subframes; l++) {
            if(useBallBallCollision) {
                ballCollisionComplete();
            }

            float srcBall[] = new float[8 * balls.size()];
            for (int i = 0; i < balls.size(); i++) {
                balls.get(i).acceleration = balls.get(i).acceleration.add(0, 0, -1*subframeInv); //gravity
                balls.get(i).velocity = balls.get(i).velocity.add(balls.get(i).acceleration);
                balls.get(i).place = balls.get(i).place.add(balls.get(i).velocity.multiply(subframeInv));
                balls.get(i).acceleration = new Point3D(0, 0, 0);

                srcBall[i * 8 + 0] = (float) balls.get(i).place.getX();
                srcBall[i * 8 + 1] = (float) balls.get(i).place.getY();
                srcBall[i * 8 + 2] = (float) balls.get(i).place.getZ();
                srcBall[i * 8 + 3] = (float) balls.get(i).velocity.getX();
                srcBall[i * 8 + 4] = (float) balls.get(i).velocity.getY();
                srcBall[i * 8 + 5] = (float) balls.get(i).velocity.getZ();
                srcBall[i * 8 + 6] = (float) balls.get(i).size;
                srcBall[i * 8 + 7] = 0;
            }

            Pointer pBall = Pointer.to(srcBall);
            memObjects[3] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * srcBall.length, pBall, null);
            clFinish(commandQueue);

            clSetKernelArg(kernelBotOpti, 5, Sizeof.cl_mem, Pointer.to(memObjects[3]));
            clEnqueueNDRangeKernel(commandQueue, kernelBotOpti, 1, null, new long[]{balls.size()}, null, 0, null, null);
            clFinish(commandQueue);

            clEnqueueReadBuffer(commandQueue, memObjects[3], CL_TRUE, 0, srcBall.length * Sizeof.cl_float, pBall, 0, null, null);
            clFinish(commandQueue);
            clReleaseMemObject(memObjects[3]);

            for (int i = 0; i < balls.size(); i++) {
                balls.get(i).place=new Point3D(srcBall[i*8+0],srcBall[i*8+1],srcBall[i*8+2]);
                balls.get(i).velocity=new Point3D(srcBall[i*8+3],srcBall[i*8+4],srcBall[i*8+5]);
                if(srcBall[i*8+7]>friction[i]){
                    friction[i]=srcBall[i*8+7];
                }
            }
        }
        for(int i=0;i<balls.size();i++) {
            if (friction[i] > 0.001f) {
                if (balls.get(i).velocity.magnitude() > friction[i]) {
                    balls.get(i).velocity = balls.get(i).velocity.subtract(balls.get(i).velocity.normalize().multiply(friction[i]));
                } else {
                    balls.get(i).velocity = new Point3D(0, 0, 0);
                }
            } else {
                balls.get(i).velocity = balls.get(i).velocity.multiply(0.999);
            }
        }
    }
}
