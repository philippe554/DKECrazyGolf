package CrazyGolf.PhysicsEngine;

import org.jocl.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import static org.jocl.CL.*;

/**
 * Created by pmmde on 5/16/2016.
 */
public abstract class PhysicsGPU extends Physics{
    protected String kernelFile;
    protected cl_context context;
    protected cl_command_queue commandQueue;
    protected cl_program program;
    protected cl_mem memObjectsDedi[];
    protected cl_mem memObjectsDefault[];
    protected cl_kernel kernels[];

    public PhysicsGPU(LinkedList<String> input) {
        super(input);
        startOpenGL();
        loadDefaultMemObjects();
        loadKernalsAndMemObjects();
    }
    protected abstract void loadKernalsAndMemObjects();
    private final void loadDefaultMemObjects(){
        float srcPoints[] = new float[points.size()*3];
        int srcEdges[] = new int[edges.size() * 2];
        int srcSides[] = new int[sides.size() * 3];
        for (int i = 0; i < points.size(); i++) {
            srcPoints[i * 3 + 0] = (float) points.get(i).getX();
            srcPoints[i * 3 + 1] = (float) points.get(i).getY();
            srcPoints[i * 3 + 2] = (float) points.get(i).getZ();
        }
        for (int i = 0; i < edges.size(); i++) {
            srcEdges[i * 2 + 0] = edges.get(i).points[0];
            srcEdges[i * 2 + 1] = edges.get(i).points[1];
        }
        for (int i = 0; i < sides.size(); i++) {
            srcSides[i * 3 + 0] = sides.get(i).points[0];
            srcSides[i * 3 + 1] = sides.get(i).points[1];
            srcSides[i * 3 + 2] = sides.get(i).points[2];
        }
        float srcEdgeData[] = new float[edges.size() * 4];
        float srcSidesData[] = new float[sides.size() * 9];
        for (int i = 0; i < edges.size(); i++) {
            srcEdgeData[i * 4 + 0] = (float) edges.get(i).lenght;
            srcEdgeData[i * 4 + 1] = (float) edges.get(i).unit.getX();
            srcEdgeData[i * 4 + 2] = (float) edges.get(i).unit.getY();
            srcEdgeData[i * 4 + 3] = (float) edges.get(i).unit.getZ();
        }
        for (int i = 0; i < sides.size(); i++) {
            srcSidesData[i * 9 + 0] = (float) sides.get(i).abc.getX();
            srcSidesData[i * 9 + 1] = (float) sides.get(i).abc.getY();
            srcSidesData[i * 9 + 2] = (float) sides.get(i).abc.getZ();
            srcSidesData[i * 9 + 3] = (float) sides.get(i).d;
            srcSidesData[i * 9 + 4] = (float) sides.get(i).Nv;
            srcSidesData[i * 9 + 5] = (float) sides.get(i).normal.getX();
            srcSidesData[i * 9 + 6] = (float) sides.get(i).normal.getY();
            srcSidesData[i * 9 + 7] = (float) sides.get(i).normal.getZ();
            srcSidesData[i * 9 + 8] = (float )sides.get(i).friction;
        }
        Pointer pPoints = Pointer.to(srcPoints);
        Pointer pEdges = Pointer.to(srcEdges);
        Pointer pEdgesData = Pointer.to(srcEdgeData);
        Pointer pSides = Pointer.to(srcSides);
        Pointer pSidesData = Pointer.to(srcSidesData);

        memObjectsDefault = new cl_mem[5];
        memObjectsDefault[0] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * srcPoints.length, pPoints, null);
        memObjectsDefault[1] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * srcEdges.length, pEdges, null);
        memObjectsDefault[2] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * srcEdgeData.length, pEdgesData, null);
        memObjectsDefault[3] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * srcSides.length, pSides, null);
        memObjectsDefault[4] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * srcSidesData.length, pSidesData, null);
    }
    public final void cleanUp() {
        clFinish(commandQueue);
        releaseMemObjects();
        for(int i=0;i<kernels.length;i++)
        {
            clReleaseKernel(kernels[i]);
        }
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
        if(DEBUG) {
            System.out.println("OpenCl released.");
        }
    }
    protected final void startOpenGL() {
        kernelFile = readFile("open.cl");

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
    }
    protected final void releaseMemObjects() {
        clFinish(commandQueue);
        if(memObjectsDefault!=null) {
            for (int i = 0; i < memObjectsDefault.length; i++) {
                if(i!=3 && i!=6) {
                    clReleaseMemObject(memObjectsDefault[i]);
                }
            }
        }
        if(memObjectsDedi!=null) {
            for (int i = 0; i < memObjectsDedi.length; i++) {
                if(i!=3 && i!=6) {
                    clReleaseMemObject(memObjectsDedi[i]);
                }
            }
        }
    }
    protected String readFile(String fileName) {
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
