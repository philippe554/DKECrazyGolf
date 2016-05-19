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
public class WorldGPUBotOpti extends PhysicsGPU {
    public WorldGPUBotOpti(LinkedList<String> input){
        super(input);
    }
    @Override
    protected void loadKernalsAndMemObjects() {
        kernels = new cl_kernel[1];
        kernels[0] = clCreateKernel(program, "botOpti", null);

        int srcSizes[] = {sides.size(),edges.size(),points.size()};
        Pointer pSizes = Pointer.to(srcSizes);
        memObjectsDedi = new cl_mem[1];
        memObjectsDedi[0]= clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * srcSizes.length, pSizes, null);

        clSetKernelArg(kernels[0], 0, Sizeof.cl_mem, Pointer.to(memObjectsDefault[0]));
        clSetKernelArg(kernels[0], 1, Sizeof.cl_mem, Pointer.to(memObjectsDefault[1]));
        clSetKernelArg(kernels[0], 2, Sizeof.cl_mem, Pointer.to(memObjectsDefault[2]));
        clSetKernelArg(kernels[0], 3, Sizeof.cl_mem, Pointer.to(memObjectsDefault[3]));
        clSetKernelArg(kernels[0], 4, Sizeof.cl_mem, Pointer.to(memObjectsDefault[4]));
        clSetKernelArg(kernels[0], 6, Sizeof.cl_mem, Pointer.to(memObjectsDedi[0]));
    }
    protected void stepCalc(int subframes,boolean useBallBallCollision) {
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
            waterComplete(subframeInv);
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
            cl_mem ball = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * srcBall.length, pBall, null);
            clFinish(commandQueue);

            clSetKernelArg(kernels[0], 5, Sizeof.cl_mem, Pointer.to(ball));
            clEnqueueNDRangeKernel(commandQueue, kernels[0], 1, null, new long[]{balls.size()}, null, 0, null, null);
            clFinish(commandQueue);

            clEnqueueReadBuffer(commandQueue, ball, CL_TRUE, 0, srcBall.length * Sizeof.cl_float, pBall, 0, null, null);
            clFinish(commandQueue);
            clReleaseMemObject(ball);

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
