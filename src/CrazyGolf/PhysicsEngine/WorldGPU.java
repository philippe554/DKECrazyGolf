package CrazyGolf.PhysicsEngine;

import CrazyGolf.FileLocations;
import javafx.geometry.Point3D;
import org.jocl.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.jocl.CL.*;

public class WorldGPU extends PhysicsGPU{
    public WorldGPU(LinkedList<String> input){
        super(input);
    }
    @Override
    protected void loadKernalsAndMemObjects() {
        kernels = new cl_kernel[3];
        kernels[0] = clCreateKernel(program, "ballSide", null);
        kernels[1] = clCreateKernel(program, "ballEdge", null);
        kernels[2] = clCreateKernel(program, "ballPoint", null);

        clSetKernelArg(kernels[0], 0, Sizeof.cl_mem, Pointer.to(memObjectsDefault[0]));
        clSetKernelArg(kernels[0], 1, Sizeof.cl_mem, Pointer.to(memObjectsDefault[3]));
        clSetKernelArg(kernels[0], 2, Sizeof.cl_mem, Pointer.to(memObjectsDefault[4]));

        clSetKernelArg(kernels[1], 0, Sizeof.cl_mem, Pointer.to(memObjectsDefault[0]));
        clSetKernelArg(kernels[1], 1, Sizeof.cl_mem, Pointer.to(memObjectsDefault[1]));
        clSetKernelArg(kernels[1], 2, Sizeof.cl_mem, Pointer.to(memObjectsDefault[2]));

        clSetKernelArg(kernels[2], 0, Sizeof.cl_mem, Pointer.to(memObjectsDefault[0]));
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
            float srcBall[] = new float[4 * balls.size()];
            for (int i = 0; i < balls.size(); i++) {
                balls.get(i).acceleration = balls.get(i).acceleration.add(0, 0, -1*subframeInv); //gravity
                balls.get(i).velocity = balls.get(i).velocity.add(balls.get(i).acceleration);
                balls.get(i).place = balls.get(i).place.add(balls.get(i).velocity.multiply(subframeInv));
                balls.get(i).acceleration = new Point3D(0, 0, 0);

                srcBall[i * 4 + 0] = (float) balls.get(i).place.getX();
                srcBall[i * 4 + 1] = (float) balls.get(i).place.getY();
                srcBall[i * 4 + 2] = (float) balls.get(i).place.getZ();
                srcBall[i * 4 + 3] = (float) balls.get(i).size;
            }
            Pointer pBall = Pointer.to(srcBall);
            cl_mem ball = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * srcBall.length, pBall, null);

            byte srcCollision[];
            Pointer pCollision;
            int max=Math.max(sides.size(),Math.max(points.size(),edges.size()));
            srcCollision=new byte[max*balls.size()];
            for(int i=0;i<srcCollision.length;i++)
            {
                srcCollision[i]=0;
            }
            pCollision = Pointer.to(srcCollision);
            cl_mem collision = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_char * srcCollision.length, pCollision, null);
            clFinish(commandQueue);

            clSetKernelArg(kernels[0], 3, Sizeof.cl_mem, Pointer.to(ball));
            clSetKernelArg(kernels[0], 4, Sizeof.cl_mem, Pointer.to(collision));
            clEnqueueNDRangeKernel(commandQueue, kernels[0], 2, null, new long[]{sides.size(),balls.size()}, null, 0, null, null);
            clSetKernelArg(kernels[1], 3, Sizeof.cl_mem, Pointer.to(ball));
            clSetKernelArg(kernels[1], 4, Sizeof.cl_mem, Pointer.to(collision));
            clEnqueueNDRangeKernel(commandQueue, kernels[1], 2, null, new long[]{edges.size(),balls.size()}, null, 0, null, null);
            clSetKernelArg(kernels[2], 1, Sizeof.cl_mem, Pointer.to(ball));
            clSetKernelArg(kernels[2], 2, Sizeof.cl_mem, Pointer.to(collision));
            clEnqueueNDRangeKernel(commandQueue, kernels[2], 2, null, new long[]{points.size(),balls.size()}, null, 0, null, null);
            clFinish(commandQueue);

            clEnqueueReadBuffer(commandQueue, collision, CL_TRUE, 0, sides.size() * Sizeof.cl_char*balls.size(), pCollision, 0, null, null);
            clReleaseMemObject(ball);
            clReleaseMemObject(collision);
            clFinish(commandQueue);

            if(CPUCHECK){
                applyCollisionCPUCheck(friction,srcCollision);
            }else
            {
                applyCollision(friction,srcCollision);
            }
        }
        for(int i=0;i<balls.size();i++) {
            if (friction[i] > 0.001) {
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
    protected void applyCollision(float friction[],byte srcCollision[]){
        int ballsSize=balls.size();
        for (int i = 0; i < ballsSize; i++) {
            for (int j = 0; j < sides.size(); j++) {
                if(((srcCollision[j*ballsSize+i]>>0)&1) != 0) {
                    if (sideCollision(i, j)) {
                        if (sides.get(j).friction > friction[i]) {
                            friction[i] = (float) sides.get(j).friction;
                        }
                    }
                }
            }
            for (int j = 0; j < edges.size(); j++) {
                if(((srcCollision[j*ballsSize+i]>>1)&1) != 0) {
                    edgeCollision(i,j);
                }
            }
            for (int j = 0; j < points.size(); j++) {
                if(((srcCollision[j*ballsSize+i]>>2)&1) != 0) {
                    pointCollision(i, j);
                }
            }
        }
    }
    protected void applyCollisionCPUCheck(float friction[],byte srcCollision[]){
        int falseAlarms=0;
        int missed=0;
        for (int i = 0; i < balls.size(); i++) {
            for (int j = 0; j < sides.size(); j++) {
                if(sideCollision(i,j)) {
                    if (sides.get(j).friction > friction[i]) {
                        friction[i] = (float) sides.get(j).friction;
                    }
                    if (((srcCollision[j*balls.size()+i]>>0)&1) == 0)
                    {
                        missed++;
                    }
                }
                else
                {
                    if (((srcCollision[j*balls.size()+i]>>0)&1) != 0)
                    {
                        falseAlarms++;
                    }
                }
            }

            for (int j = 0; j < edges.size(); j++) {
                if(edgeCollision(i,j)) {
                    if (((srcCollision[j*balls.size()+i]>>1)&1) == 0)
                    {
                        missed++;
                    }
                }
                else
                {
                    if (((srcCollision[j*balls.size()+i]>>1)&1) != 0)
                    {
                        falseAlarms++;
                    }
                }
            }
            for (int j = 0; j < points.size(); j++) {
                if(pointCollision(i,j)) {
                    if (((srcCollision[j*balls.size()+i]>>2)&1) == 0)
                    {
                        missed++;
                    }
                }
                else
                {
                    if (((srcCollision[j*balls.size()+i]>>2)&1) != 0)
                    {
                        falseAlarms++;
                    }
                }
            }
        }
        if(DEBUG && falseAlarms>0)
        {
            System.out.println("False Alarms: "+falseAlarms);
        }
        if(DEBUG && missed>0)
        {
            System.out.println("Missed: "+missed);
        }
    }
}
