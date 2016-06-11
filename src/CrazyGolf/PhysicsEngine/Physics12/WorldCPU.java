package CrazyGolf.PhysicsEngine.Physics12;

import javafx.geometry.Point3D;

import java.util.LinkedList;

public class WorldCPU extends Physics {
    public WorldCPU(LinkedList<String> input){
        super(input);
    }
    @Override protected void stepCalc(int subframes,boolean useBallBallCollision) {
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
                balls.get(i).acceleration = balls.get(i).acceleration.add(0, 0, -gravity*subframeInv); //gravity
                balls.get(i).velocity = balls.get(i).velocity.add(balls.get(i).acceleration);
                balls.get(i).place = balls.get(i).place.add(balls.get(i).velocity.multiply(subframeInv));
                balls.get(i).acceleration = new Point3D(0, 0, 0);
            }
            for (int i = 0; i < balls.size(); i++) {
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

