package CrazyGolf.PhysicsEngine;

import CrazyGolf.FileLocations;
import javafx.geometry.Point3D;
import org.jocl.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.jocl.CL.*;

public class PhysicsGPU implements Physics{
    private World world;

    private String kernelFile;
    private cl_context context;
    private cl_kernel kernelBallSide;
    private cl_kernel kernelBallEdge;
    private cl_kernel kernelBallPoint;
    private cl_command_queue commandQueue;
    private cl_program program;
    private cl_mem memObjects[];

    public void step(int subframes) {
        double subframeInv = 1.0 / subframes;
        for (int l = 0; l < subframes; l++) {
            float srcBall[] = new float[12 * world.balls.size()];

            for (int i = 0; i < world.balls.size(); i++) {
                world.balls.get(i).acceleration = world.balls.get(i).acceleration.add(0, 0, -1); //gravity
                world.balls.get(i).velocity = world.balls.get(i).velocity.add(world.balls.get(i).acceleration.multiply(subframeInv));
                world.balls.get(i).place = world.balls.get(i).place.add(world.balls.get(i).velocity.multiply(subframeInv));
                world.balls.get(i).acceleration = new Point3D(0, 0, 0);

                srcBall[i * 11 + 0] = (float) world.balls.get(i).place.getX();
                srcBall[i * 11 + 1] = (float) world.balls.get(i).place.getY();
                srcBall[i * 11 + 2] = (float) world.balls.get(i).place.getZ();
                srcBall[i * 11 + 3] = (float) world.balls.get(i).velocity.getX();
                srcBall[i * 11 + 4] = (float) world.balls.get(i).velocity.getY();
                srcBall[i * 11 + 5] = (float) world.balls.get(i).velocity.getZ();
                srcBall[i * 11 + 6] = (float) world.balls.get(i).velocity.getX();
                srcBall[i * 11 + 7] = (float) world.balls.get(i).velocity.getY();
                srcBall[i * 11 + 8] = (float) world.balls.get(i).velocity.getZ();
                srcBall[i * 11 + 9] = (float) world.balls.get(i).size;
                srcBall[i * 11 + 10] = 0;
            }
            Pointer pBall = Pointer.to(srcBall);
            memObjects[3] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * srcBall.length, pBall, null);
            clFinish(commandQueue);
            clSetKernelArg(kernelBallSide, 3, Sizeof.cl_mem, Pointer.to(memObjects[3]));
            clEnqueueNDRangeKernel(commandQueue, kernelBallSide, 1, null, new long[]{world.sides.size() * world.balls.size()}, new long[]{world.balls.size()}, 0, null, null);
            clFinish(commandQueue);
            clSetKernelArg(kernelBallEdge, 3, Sizeof.cl_mem, Pointer.to(memObjects[3]));
            clEnqueueNDRangeKernel(commandQueue, kernelBallEdge, 1, null, new long[]{world.sides.size() * world.balls.size() * 3}, new long[]{world.balls.size()}, 0, null, null);
            clFinish(commandQueue);
            clSetKernelArg(kernelBallPoint, 1, Sizeof.cl_mem, Pointer.to(memObjects[3]));
            clEnqueueNDRangeKernel(commandQueue, kernelBallPoint, 1, null, new long[]{world.sides.size() * world.balls.size() * 3}, new long[]{world.balls.size()}, 0, null, null);
            clFinish(commandQueue);
            clEnqueueReadBuffer(commandQueue, memObjects[3], CL_TRUE, 0, srcBall.length * Sizeof.cl_float, pBall, 0, null, null);
            clReleaseMemObject(memObjects[3]);

            for (int i = 0; i < world.balls.size(); i++) {
                world.balls.get(i).place = new Point3D(srcBall[i * 11 + 0], srcBall[i * 11 + 1], srcBall[i * 11 + 2]);
                world.balls.get(i).velocity = new Point3D(srcBall[i * 11 + 3], srcBall[i * 11 + 4], srcBall[i * 11 + 5]);
                if (srcBall[i * 11 + 10] > 0.5) {
                    double friction=0.1*subframeInv;
                    if(world.balls.get(i).velocity.magnitude()>friction){
                        world.balls.get(i).velocity = world.balls.get(i).velocity.subtract(world.balls.get(i).velocity.normalize().multiply(friction));
                    }else {
                        world.balls.get(i).velocity = new Point3D(0, 0, 0);
                    }
                } else {
                    world.balls.get(i).velocity = world.balls.get(i).velocity.multiply(Math.pow(0.999, subframeInv));
                }
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
            srcSidesData[i * 9 + 8] = world.sides.get(i).color;
        }

        Pointer pPoints = Pointer.to(srcPoints);
        Pointer pSides = Pointer.to(srcSides);
        Pointer pSidesData = Pointer.to(srcSidesData);
        Pointer pEdges = Pointer.to(srcEdges);
        Pointer pEdgesData = Pointer.to(srcEdgeData);

        if(memObjects!=null)
        {
            clFinish(commandQueue);
            clReleaseMemObject(memObjects[0]);
            clReleaseMemObject(memObjects[1]);
            clReleaseMemObject(memObjects[2]);
            clReleaseMemObject(memObjects[4]);
            clReleaseMemObject(memObjects[5]);
        }

        memObjects = new cl_mem[6];
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

        clSetKernelArg(kernelBallSide, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernelBallSide, 1,
                Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernelBallSide, 2,
                Sizeof.cl_mem, Pointer.to(memObjects[2]));

        clSetKernelArg(kernelBallEdge, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernelBallEdge, 1,
                Sizeof.cl_mem, Pointer.to(memObjects[4]));
        clSetKernelArg(kernelBallEdge, 2,
                Sizeof.cl_mem, Pointer.to(memObjects[5]));

        clSetKernelArg(kernelBallPoint, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
    }
    public void cleanUp() {
        clFinish(commandQueue);
        if(memObjects!=null)
        {
            clReleaseMemObject(memObjects[0]);
            clReleaseMemObject(memObjects[1]);
            clReleaseMemObject(memObjects[2]);
            clReleaseMemObject(memObjects[4]);
            clReleaseMemObject(memObjects[5]);
        }

        clReleaseKernel(kernelBallSide);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
    }

    private void startOpenGL() {
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
    private static String readFile(String fileName) {
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
}
