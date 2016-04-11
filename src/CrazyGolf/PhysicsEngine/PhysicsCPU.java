package CrazyGolf.PhysicsEngine;

class PhysicsCPU extends Thread implements Physics {
    @Override
    public void loadWorld(World w) {

    }

    @Override
    public void step(int subframes) {

    }

    @Override
    public void cleanUp() {

    }
    /*int i;
    int jstart;
    int jstop;
    Point3D v;
    public boolean collisionWithWalls;
    public int mode;

    public PhysicsGPU(int ti, int tjstart, int tjstop, Point3D tv, int tmode) {
        i = ti;
        jstart = tjstart;
        jstop = tjstop;
        v = tv;
        collisionWithWalls = false;
        mode = tmode;
    }

    @Override
    public void run() {
        if (mode == 0) {
            //ball-plane
            for (int j = jstart; j < jstop; j++) {
                double Nr0 = sides.get(j).abc.dotProduct(balls.get(i).place);

                double t = (sides.get(j).d - Nr0) / sides.get(j).Nv;

                Point3D intersection = balls.get(i).place.add(sides.get(j).normal.multiply(t));

                double distance = intersection.distance(balls.get(i).place);

                if (distance < balls.get(i).size) {
                    if (PointInTriangle(intersection, sides.get(j).points[0], sides.get(j).points[1], sides.get(j).points[2])) {
                        if (t != 0) {
                            double dir = -(t / Math.abs(t));
                            balls.get(i).place = intersection.add(sides.get(j).normal.multiply(dir * balls.get(i).size));
                            //balls.get(i).acceleration=balls.get(i).acceleration.add(sides.get(j).normal.multiply(balls.get(i).velocity.dotProduct(sides.get(j).normal)*dir*2));
                            balls.get(i).velocity = v.subtract(sides.get(j).normal.multiply(v.dotProduct(sides.get(j).normal) * 1.8));
                            collisionWithWalls = true;
                        } else {
                            System.out.println("Physics engine stopped step, devided by 0");
                        }
                    }
                }
            }
        } else if (mode == 1) {
            //ball-edge
            for (int j = jstart; j < jstop; j++) {
                for (int k = 0; k < sides.get(j).edges.length; k++) {
                    double t = sides.get(j).edges[k].unit.dotProduct(balls.get(i).place.subtract(sides.get(j).edges[k].points[0]));

                    if (t > 0 && t < sides.get(j).edges[k].lenght) {
                        Point3D clossest = sides.get(j).edges[k].points[0].add(sides.get(j).edges[k].unit.multiply(t));
                        Point3D unit = balls.get(i).place.subtract(clossest);
                        double distance = unit.magnitude();
                        unit = unit.normalize();
                        if (distance < balls.get(i).size) {
                            balls.get(i).place = clossest.add(unit.multiply(balls.get(i).size));
                            balls.get(i).velocity = v.subtract(unit.multiply(v.dotProduct(unit) * 1.8));
                            collisionWithWalls = true;
                        }
                    }
                }
            }
        } else if (mode == 2) {
            //ball-point
            for (int j = jstart; j < jstop; j++) {
                Point3D ballEndPoint = balls.get(i).place.subtract(points.get(j));
                if (ballEndPoint.magnitude() < balls.get(i).size) {
                    Point3D unit = ballEndPoint.normalize();
                    balls.get(i).place = points.get(j).add(unit.multiply(balls.get(i).size));
                    balls.get(i).velocity = v.subtract(unit.multiply(v.dotProduct(unit) * 1.8));
                    collisionWithWalls = true;
                }
            }
        }
    }

    public void step(int subframes) {
        double subframeInv = 1.0 / subframes;
        //move everything
        for (int l = 0; l < subframes; l++) {
            for (int i = 0; i < balls.size(); i++) {
                balls.get(i).acceleration = balls.get(i).acceleration.add(0, 0, -1); //gravity

                balls.get(i).velocity = balls.get(i).velocity.add(balls.get(i).acceleration.multiply(subframeInv));
                balls.get(i).place = balls.get(i).place.add(balls.get(i).velocity.multiply(subframeInv));

                balls.get(i).acceleration = new Point3D(0, 0, 0);
            }

            //check collsion
            for (int i = 0; i < balls.size(); i++) {
                boolean collisionWithWalls = false;

                Point3D v = balls.get(i).velocity.multiply(1);

                for (int k = 0; k < 3; k++) {
                    WorkThread workThread[] = new WorkThread[amountOfThreads];
                    for (int j = 0; j < amountOfThreads; j++) {
                        workThread[j] = new WorkThread(i, (int) (sides.size() * j / amountOfThreads), (int) (sides.size() * (j + 1) / amountOfThreads), v, k);
                        workThread[j].start();
                    }
                    for (int j = 0; j < amountOfThreads; j++) {
                        try {
                            workThread[j].join();
                            if (workThread[j].collisionWithWalls == true) {
                                collisionWithWalls = true;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (collisionWithWalls) {
                    balls.get(i).velocity = balls.get(i).velocity.multiply(Math.pow(0.99, subframeInv));
                } else {
                    balls.get(i).velocity = balls.get(i).velocity.multiply(Math.pow(0.999, subframeInv));
                }
            }
        }
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

    public void loadWorld(String file) {
        points = new LinkedList<>();
        sides = new LinkedList<>();
        balls = new LinkedList<>();

        LinkedList<String> field = new LinkedList<>();
        try {
            Scanner s = new Scanner(new FileReader(file));
            while (s.hasNextLine()) {
                field.add(s.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading field plan from " + file);
            System.exit(0);
        }
        int sort = 0;
        for (int i = 0; i < field.size(); i++) {
            if (field.get(i).equals("balls")) {
                sort = 0;
            } else if (field.get(i).equals("triangels")) {
                sort = 1;
            } else if (field.get(i).equals("hole")) {
                sort = 2;
            } else {
                if (sort == 0) {
                    String[] data = field.get(i).split(";");
                    if (data.length == 3) {
                        balls.add(new Ball(20, new Point3D(
                                Double.parseDouble(data[0]),
                                Double.parseDouble(data[1]),
                                Double.parseDouble(data[2]))));
                    }
                } else if (sort == 1) {
                    String[] data = field.get(i).split(";");
                    if (data.length == 12) {
                        points.add(new Point3D(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2])));
                        points.add(new Point3D(Double.parseDouble(data[3]), Double.parseDouble(data[4]), Double.parseDouble(data[5])));
                        points.add(new Point3D(Double.parseDouble(data[6]), Double.parseDouble(data[7]), Double.parseDouble(data[8])));
                        Color3f c = new Color3f(Float.parseFloat(data[9]), Float.parseFloat(data[10]), Float.parseFloat(data[11]));
                        sides.add(new Side(points.get(points.size() - 3), points.get(points.size() - 2), points.get(points.size() - 1), c));
                    }
                } else if (sort == 2) {
                    String[] data = field.get(i).split(";");
                    if (data.length == 3) {
                        hole = new Point3D(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]));
                    }
                }
            }
        }
    }

    */
}

