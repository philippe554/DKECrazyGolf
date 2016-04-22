package CrazyGolf.PhysicsEngine;

import javafx.geometry.Point3D;

class PhysicsCPU extends Thread implements Physics {

    protected World world;

    @Override
    public void loadWorld(World w) {
        world=w;
    }

    @Override
    public void step(int subframes) {
        double subframeInv = 1.0 / (double)(subframes);
        float friction[]=new float[world.balls.size()];
        for(int i=0;i<world.balls.size();i++)
        {
            friction[i]=0.0f;
        }
        for (int l = 0; l < subframes; l++) {
            for (int i = 0; i < world.balls.size(); i++) {
                world.balls.get(i).acceleration = world.balls.get(i).acceleration.add(0, 0, -1*subframeInv); //gravity
                world.balls.get(i).velocity = world.balls.get(i).velocity.add(world.balls.get(i).acceleration);
                world.balls.get(i).place = world.balls.get(i).place.add(world.balls.get(i).velocity.multiply(subframeInv));
                world.balls.get(i).acceleration = new Point3D(0, 0, 0);
            }
            for (int i = 0; i < world.balls.size(); i++) {
                for (int j = 0; j < world.sides.size(); j++) {
                    if (sideCollision(i, j)) {
                        if (world.sides.get(j).friction > friction[i]) {
                            friction[i] = (float) world.sides.get(j).friction;
                        }
                    }
                }
                for (int j = 0; j < world.edges.size(); j++) {
                    edgeCollision(i, j);
                }
                for (int j = 0; j < world.points.size(); j++) {
                    pointCollision(i, j);

                }
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

    @Override
    public void cleanUp() {

    }

    protected boolean sideCollision(int i,int j){
        boolean result=false;

        double Nr0 = world.sides.get(j).abc.dotProduct(world.balls.get(i).place);

        double t = (world.sides.get(j).d - Nr0) / world.sides.get(j).Nv;

        Point3D intersection = world.balls.get(i).place.add(world.sides.get(j).normal.multiply(t));

        double distance = intersection.distance(world.balls.get(i).place);

        if (distance < world.balls.get(i).size) {
            if (PointInTriangle(intersection, world.points.get(world.sides.get(j).points[0]),
                    world.points.get(world.sides.get(j).points[1]),
                    world.points.get(world.sides.get(j).points[2]))) {
                double dir = t > 0 ? -1 : 1;
                world.balls.get(i).place = intersection.add(world.sides.get(j).normal.multiply(dir * world.balls.get(i).size));
                //balls.get(i).acceleration=balls.get(i).acceleration.add(sides.get(j).normal.multiply(balls.get(i).velocity.dotProduct(sides.get(j).normal)*dir*2));
                world.balls.get(i).velocity = world.balls.get(i).velocity.subtract(world.sides.get(j).normal.multiply(world.balls.get(i).velocity.dotProduct(world.sides.get(j).normal) * 1.8));
                result =true;
            }
        }
        return result;
    }
    protected boolean edgeCollision(int i,int j){
        boolean result=false;
        double t = world.edges.get(j).unit.dotProduct(world.balls.get(i).place.subtract(world.points.get(world.edges.get(j).points[0])));

        if (t > 0 && t < world.edges.get(j).lenght) {
            Point3D clossest = world.points.get(world.edges.get(j).points[0]).add(world.edges.get(j).unit.multiply(t));
            Point3D unit = world.balls.get(i).place.subtract(clossest);
            double distance = unit.magnitude();
            unit = unit.normalize();
            if (distance < world.balls.get(i).size) {
                world.balls.get(i).place = clossest.add(unit.multiply(world.balls.get(i).size));
                world.balls.get(i).velocity = world.balls.get(i).velocity.subtract(unit.multiply(world.balls.get(i).velocity.dotProduct(unit) * 1.8));
                result=true;
            }
        }
        return result;
    }
    protected boolean pointCollision(int i,int j){
        boolean result=false;
        Point3D ballEndPoint = world.balls.get(i).place.subtract(world.points.get(j));
        if (ballEndPoint.magnitude() < world.balls.get(i).size) {
            Point3D unit = ballEndPoint.normalize();
            world.balls.get(i).place = world.points.get(j).add(unit.multiply(world.balls.get(i).size));
            world.balls.get(i).velocity = world.balls.get(i).velocity.subtract(unit.multiply(world.balls.get(i).velocity.dotProduct(unit) * 1.8));
            result=true;
        }
        return result;
    }
    private boolean PointInTriangle(Point3D p, Point3D a, Point3D b, Point3D c) {
        Point3D v0 = c.subtract(a);
        Point3D v1 = b.subtract(a);
        Point3D v2 = p.subtract(a);

        double v0v0 = v0.dotProduct(v0);
        double v0v1 = v0.dotProduct(v1);
        double v0v2 = v0.dotProduct(v2);
        double v1v1 = v1.dotProduct(v1);
        double v1v2 = v1.dotProduct(v2);

        double u = (v1v1 * v0v2 - v0v1 * v1v2) / (v0v0 * v1v1 - v0v1 * v0v1);
        double v = (v0v0 * v1v2 - v0v1 * v0v2) / (v0v0 * v1v1 - v0v1 * v0v1);
        if (u >= 0 && v >= 0 && u <= 1 && v <= 1 && (u + v) <= 1) {
            return true;
        } else {
            return false;
        }
    }
}

