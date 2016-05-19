package CrazyGolf.PhysicsEngine;

import javafx.geometry.Point3D;

import java.util.LinkedList;

/**
 * Created by pmmde on 5/16/2016.
 */
public class WorldCPUMultiThread extends Physics{
    final int maxAmountOfThreads=4;
    private class WorkThread extends Thread{
        int start;
        int stop;
        float friction[];
        public WorkThread(int tStart,int tStop,float f[]){
            start=tStart;
            stop=tStop;
            friction=f;
        }
        @Override
        public void run() {
            for(int i=start;i<stop;i++) {
                for (int j = 0; j < sides.size(); j++) {
                    if (sideCollision(i, j)) {
                        if (sides.get(j).friction > friction[i]) {
                            friction[i] = (float) sides.get(j).friction;
                        }
                    }
                }
                for (int j = 0; j < edges.size(); j++) {
                    edgeCollision(i, j);
                }
                for (int j = 0; j < points.size(); j++) {
                    pointCollision(i, j);

                }
            }
        }
    }
    public WorldCPUMultiThread(LinkedList<String> input) {
        super(input);
    }
    @Override
    void stepCalc(int subframes, boolean useBallBallCollision) {
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
            for (int i = 0; i < balls.size(); i++) {
                balls.get(i).acceleration = balls.get(i).acceleration.add(0, 0, -1*subframeInv); //gravity
                balls.get(i).velocity = balls.get(i).velocity.add(balls.get(i).acceleration);
                balls.get(i).place = balls.get(i).place.add(balls.get(i).velocity.multiply(subframeInv));
                balls.get(i).acceleration = new Point3D(0, 0, 0);
            }
            int amountOfThreads = Math.min(maxAmountOfThreads,balls.size());
            WorkThread threads[] = new WorkThread[amountOfThreads];
            for(int i=0;i<amountOfThreads;i++) {
                threads[i] = new WorkThread((int)(balls.size()*i / amountOfThreads), (int) (balls.size()* (i+1) / amountOfThreads),friction);
                threads[i].start();
            }
            for(int i=0;i<amountOfThreads;i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
