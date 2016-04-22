package CrazyGolf.PhysicsEngine;

import CrazyGolf.FileLocations;
import javafx.geometry.Point3D;
import org.jocl.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.jocl.CL.*;

public class PhysicsGPU extends PhysicsCPU{
    protected String kernelFile;
    protected cl_context context;
    protected cl_kernel kernelBallSide;
    protected cl_kernel kernelBallEdge;
    protected cl_kernel kernelBallPoint;
    protected cl_command_queue commandQueue;
    protected cl_program program;
    protected cl_mem memObjects[];

    protected byte srcCollision[];
    protected Pointer pCollision;

    public void step(int subframes) {
        double subframeInv = 1.0 / (double)(subframes);
        float friction[]=new float[world.balls.size()];
        for(int i=0;i<world.balls.size();i++)
        {
            friction[i]=0.0f;
        }
        for (int l = 0; l < subframes; l++) {
            float srcBall[] = new float[4 * world.balls.size()];
            for (int i = 0; i < world.balls.size(); i++) {
                world.balls.get(i).acceleration = world.balls.get(i).acceleration.add(0, 0, -1*subframeInv); //gravity
                world.balls.get(i).velocity = world.balls.get(i).velocity.add(world.balls.get(i).acceleration);
                world.balls.get(i).place = world.balls.get(i).place.add(world.balls.get(i).velocity.multiply(subframeInv));
                world.balls.get(i).acceleration = new Point3D(0, 0, 0);

                srcBall[i * 4 + 0] = (float) world.balls.get(i).place.getX();
                srcBall[i * 4 + 1] = (float) world.balls.get(i).place.getY();
                srcBall[i * 4 + 2] = (float) world.balls.get(i).place.getZ();
                srcBall[i * 4 + 3] = (float) world.balls.get(i).size;
            }
            Pointer pBall = Pointer.to(srcBall);
            memObjects[3] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * srcBall.length, pBall, null);
            clFinish(commandQueue);

            clSetKernelArg(kernelBallSide, 3, Sizeof.cl_mem, Pointer.to(memObjects[3]));
            clEnqueueNDRangeKernel(commandQueue, kernelBallSide, 2, null, new long[]{world.sides.size(),world.balls.size()}, null, 0, null, null);
            clSetKernelArg(kernelBallEdge, 3, Sizeof.cl_mem, Pointer.to(memObjects[3]));
            clEnqueueNDRangeKernel(commandQueue, kernelBallEdge, 2, null, new long[]{world.edges.size(),world.balls.size() * 3}, null, 0, null, null);
            clSetKernelArg(kernelBallPoint, 1, Sizeof.cl_mem, Pointer.to(memObjects[3]));
            clEnqueueNDRangeKernel(commandQueue, kernelBallPoint, 2, null, new long[]{world.points.size(),world.balls.size() * 3}, null, 0, null, null);
            clFinish(commandQueue);

            clEnqueueReadBuffer(commandQueue, memObjects[6], CL_TRUE, 0, world.sides.size() * Sizeof.cl_char*world.balls.size(), pCollision, 0, null, null);
            clReleaseMemObject(memObjects[3]);
            clFinish(commandQueue);

            if(World.CPUCHECK){
                applyCollisionCPUCheck(friction);
            }else
            {
                applyCollision(friction);
            }

        }
        for(int i=0;i<world.balls.size();i++) {
            if (friction[i] > 0.001) {
                if (world.balls.get(i).velocity.magnitude() > friction[i]) {
                    world.balls.get(i).velocity = world.balls.get(i).velocity.subtract(world.balls.get(i).velocity.normalize().multiply(friction[i]));

                } else {
                    world.balls.get(i).velocity = new Point3D(0, 0, 0);
                }
            } else {
                world.balls.get(i).velocity = world.balls.get(0).velocity.multiply(0.999);
            }
        }
    }
    public void loadWorld(World w){
        world = w;
        kernelFile = readFile(FileLocations.openCLFile);
        startOpenGL();
        memObjects=null;

        float srcPoints[] = new float[world.points.size()*3];
        int srcEdges[] = new int[world.edges.size() * 2];
        int srcSides[] = new int[world.sides.size() * 3];
        for (int i = 0; i < world.points.size(); i++) {
            srcPoints[i * 3 + 0] = (float) world.points.get(i).getX();
            srcPoints[i * 3 + 1] = (float) world.points.get(i).getY();
            srcPoints[i * 3 + 2] = (float) world.points.get(i).getZ();
        }

        for (int i = 0; i < world.edges.size(); i++) {
            srcEdges[i * 2 + 0] = world.edges.get(i).points[0];
            srcEdges[i * 2 + 1] = world.edges.get(i).points[1];
        }
        for (int i = 0; i < world.sides.size(); i++) {
            srcSides[i * 3 + 0] = world.sides.get(i).points[0];
            srcSides[i * 3 + 1] = world.sides.get(i).points[1];
            srcSides[i * 3 + 2] = world.sides.get(i).points[2];
        }
        float srcEdgeData[] = new float[world.edges.size() * 4];
        float srcSidesData[] = new float[world.sides.size() * 9];
        for (int i = 0; i < world.edges.size(); i++) {
            srcEdgeData[i * 4 + 0] = (float) world.edges.get(i).lenght;
            srcEdgeData[i * 4 + 1] = (float) world.edges.get(i).unit.getX();
            srcEdgeData[i * 4 + 2] = (float) world.edges.get(i).unit.getY();
            srcEdgeData[i * 4 + 3] = (float) world.edges.get(i).unit.getZ();
        }
        for (int i = 0; i < world.sides.size(); i++) {
            srcSidesData[i * 9 + 0] = (float) world.sides.get(i).abc.getX();
            srcSidesData[i * 9 + 1] = (float) world.sides.get(i).abc.getY();
            srcSidesData[i * 9 + 2] = (float) world.sides.get(i).abc.getZ();
            srcSidesData[i * 9 + 3] = (float) world.sides.get(i).d;
            srcSidesData[i * 9 + 4] = (float) world.sides.get(i).Nv;
            srcSidesData[i * 9 + 5] = (float) world.sides.get(i).normal.getX();
            srcSidesData[i * 9 + 6] = (float) world.sides.get(i).normal.getY();
            srcSidesData[i * 9 + 7] = (float) world.sides.get(i).normal.getZ();
            srcSidesData[i * 9 + 8] = (float )world.sides.get(i).color;
        }
        int max=Math.max(world.sides.size(),Math.max(world.points.size(),world.edges.size()));
        srcCollision=new byte[max*world.balls.size()];
        for(int i=0;i<srcCollision.length;i++)
        {
            srcCollision[i]=0;
        }

        Pointer pPoints = Pointer.to(srcPoints);
        Pointer pSides = Pointer.to(srcSides);
        Pointer pSidesData = Pointer.to(srcSidesData);
        Pointer pEdges = Pointer.to(srcEdges);
        Pointer pEdgesData = Pointer.to(srcEdgeData);
        pCollision = Pointer.to(srcCollision);

        releaseMemObjects();


        memObjects = new cl_mem[7];
        memObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float * srcPoints.length, pPoints, null);
        memObjects[1] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_int * srcSides.length, pSides, null);
        memObjects[2] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float * srcSidesData.length, pSidesData, null);
        memObjects[4] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_int * srcEdges.length, pEdges, null);
        memObjects[5] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float * srcEdgeData.length, pEdgesData, null);

        memObjects[6] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_char * srcCollision.length, pCollision, null);

        clSetKernelArg(kernelBallSide, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernelBallSide, 1,
                Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernelBallSide, 2,
                Sizeof.cl_mem, Pointer.to(memObjects[2]));
        clSetKernelArg(kernelBallSide, 4,
                Sizeof.cl_mem, Pointer.to(memObjects[6]));

        clSetKernelArg(kernelBallEdge, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernelBallEdge, 1,
                Sizeof.cl_mem, Pointer.to(memObjects[4]));
        clSetKernelArg(kernelBallEdge, 2,
                Sizeof.cl_mem, Pointer.to(memObjects[5]));
        clSetKernelArg(kernelBallEdge, 4,
                Sizeof.cl_mem, Pointer.to(memObjects[6]));

        clSetKernelArg(kernelBallPoint, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernelBallPoint, 2,
                Sizeof.cl_mem, Pointer.to(memObjects[6]));
    }
    public void cleanUp() {
        clFinish(commandQueue);
        releaseMemObjects();
        clReleaseKernel(kernelBallSide);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
        if(World.DEBUG) {
            System.out.println("OpenCl released.");
        }
    }

    protected void startOpenGL() {
        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Create a command-queue for the selected device
        commandQueue = clCreateCommandQueue(context, device, 0, null);

        program = clCreateProgramWithSource(context, 1, new String[]{kernelFile}, null, null);
        clBuildProgram(program, 0, null, null, null, null);
        kernelBallSide = clCreateKernel(program, "ballSide", null);
        kernelBallEdge = clCreateKernel(program, "ballEdge", null);
        kernelBallPoint = clCreateKernel(program, "ballPoint", null);
    }
    protected static String readFile(String fileName) {
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

    protected void applyCollision(float friction[]){
        int falseAlarms=0;
        for (int i = 0; i < world.balls.size(); i++) {
            for (int j = 0; j < world.sides.size(); j++) {
                if(((srcCollision[j*world.balls.size()+i]>>0)&1) != 0) {
                    if (sideCollision(i, j)) {
                        if (world.sides.get(j).friction > friction[i]) {
                            friction[i] = (float) world.sides.get(j).friction;
                        }
                    }
                    else
                    {
                        falseAlarms++;
                    }
                }
            }
            for (int j = 0; j < world.edges.size(); j++) {
                if(((srcCollision[j*world.balls.size()+i]>>1)&1) != 0) {
                    if(!edgeCollision(i,j))
                    {
                        falseAlarms++;
                    }
                }
            }
            for (int j = 0; j < world.points.size(); j++) {
                if(((srcCollision[j*world.balls.size()+i]>>2)&1) != 0) {
                    if(!pointCollision(i, j))
                    {
                        falseAlarms++;
                    }
                }
            }
        }
        if(World.DEBUG && falseAlarms>0)
        {
            System.out.println("False Alarms: "+falseAlarms);
        }
    }
    protected void applyCollisionCPUCheck(float friction[]){
        int falseAlarms=0;
        int missed=0;
        for (int i = 0; i < world.balls.size(); i++) {
            for (int j = 0; j < world.sides.size(); j++) {
                if(sideCollision(i,j)) {
                    if (world.sides.get(j).friction > friction[i]) {
                        friction[i] = (float) world.sides.get(j).friction;
                    }
                    if (((srcCollision[j*world.balls.size()+i]>>0)&1) == 0)
                    {
                        missed++;
                    }
                }
                else
                {
                    if (((srcCollision[j*world.balls.size()+i]>>0)&1) != 0)
                    {
                        falseAlarms++;
                    }
                }
            }

            for (int j = 0; j < world.edges.size(); j++) {
                if(edgeCollision(i,j)) {
                    if (((srcCollision[j*world.balls.size()+i]>>1)&1) == 0)
                    {
                        missed++;
                    }
                }
                else
                {
                    if (((srcCollision[j*world.balls.size()+i]>>1)&1) != 0)
                    {
                        falseAlarms++;
                    }
                }
            }
            for (int j = 0; j < world.points.size(); j++) {
                if(pointCollision(i,j)) {
                    if (((srcCollision[j*world.balls.size()+i]>>2)&1) == 0)
                    {
                        missed++;
                    }
                }
                else
                {
                    if (((srcCollision[j*world.balls.size()+i]>>2)&1) != 0)
                    {
                        falseAlarms++;
                    }
                }
            }
        }
        if(World.DEBUG && falseAlarms>0)
        {
            System.out.println("False Alarms: "+falseAlarms);
        }
        if(World.DEBUG && missed>0)
        {
            System.out.println("Missed: "+missed);
        }
    }

    protected void releaseMemObjects() {
        clFinish(commandQueue);
        if(memObjects!=null) {
            for (int i = 0; i < memObjects.length; i++) {
                if(i!=3) {
                    clReleaseMemObject(memObjects[i]);
                }
            }
        }
    }
}
