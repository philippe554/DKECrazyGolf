import javafx.geometry.Point3D;

import java.util.LinkedList;

/**
 * Created by pmmde on 3/12/2016.
 */
public class World {
    public LinkedList<Point3D> points;
    public LinkedList<Edge> edges;
    public LinkedList<Side> sides;
    public LinkedList<Ball> balls;

    public World()
    {
        points = new LinkedList<>();
        edges = new LinkedList<>();
        sides = new LinkedList<>();
        balls = new LinkedList<>();

        points.add(new Point3D(1000,1000,0));//0
        points.add(new Point3D(1000,-1000,0));//1
        points.add(new Point3D(-1000,1000,0));//2
        points.add(new Point3D(-1000,-1000,0));//3

        sides.add(new Side(points.get(0),points.get(1),points.get(2)));
        sides.add(new Side(points.get(3),points.get(1),points.get(2)));

        points.add(new Point3D(100,-100,0));//4
        points.add(new Point3D(300,100,0));//5
        points.add(new Point3D(100,-100,100));//6

        sides.add(new Side(points.get(4),points.get(5),points.get(6)));

        //points.add(new Point3D(50,-200,2));//7
        //points.add(new Point3D(50,200,2));//8

        //edges.add(new Edge(points.get(7),points.get(8)));
        /*
        points.add(new Point3D(30,-200,0));//9
        points.add(new Point3D(30,200,0));//10
        points.add(new Point3D(70,-200,0));//11
        points.add(new Point3D(70,200,0));//12
        sides.add(new Side(points.get(9),points.get(7),points.get(8)));
        sides.add(new Side(points.get(9),points.get(10),points.get(8)));
        sides.add(new Side(points.get(11),points.get(7),points.get(8)));
        sides.add(new Side(points.get(11),points.get(12),points.get(8)));*/

        /*points.add(new Point3D(-500,-200,0));//13
        points.add(new Point3D(-500,200,0));//14
        sides.add(new Side(points.get(13),points.get(9),points.get(10)));
        sides.add(new Side(points.get(13),points.get(14),points.get(10)));

        points.add(new Point3D(500,-200,0));//15
        points.add(new Point3D(500,200,0));//16
        sides.add(new Side(points.get(11),points.get(15),points.get(16)));
        sides.add(new Side(points.get(11),points.get(12),points.get(16)));*/

        balls.add(new Ball(20,new Point3D(0,0,20)));
        balls.get(0).velocity=new Point3D(3,0,0);

        //balls.add(new Ball(20,new Point3D(100,100,20)));
        //balls.get(1).velocity=new Point3D(-1,0,0);
    }

    public void step(){
        System.out.println(balls.get(0).velocity.getX()+" "+balls.get(0).velocity.magnitude()+" "+balls.get(0).place.getZ());
        //move everything
        for(int i=0;i<balls.size();i++)
        {
            balls.get(i).acceleration=balls.get(i).acceleration.add(0,0,-1); //gravity

            balls.get(i).velocity=balls.get(i).velocity.add(balls.get(i).acceleration);
            balls.get(i).place= balls.get(i).place.add(balls.get(i).velocity);

            balls.get(i).acceleration=new Point3D(0,0,0);
        }
        //check collsion
        for(int i=0;i<balls.size();i++)
        {
            for(int j=0;j<edges.size();j++)
            {
                Point3D ballP1=balls.get(i).place.subtract(edges.get(j).points[0]);
                Point3D ballP2=balls.get(i).place.subtract(edges.get(j).points[1]);

                double t = edges.get(j).unit.dotProduct(ballP1);
                if(t>0 && t < edges.get(j).lenght)
                {
                    Point3D clossest = edges.get(j).points[0].add(edges.get(j).unit.multiply(t));
                    Point3D unit = balls.get(i).place.subtract(clossest);
                    double distance = unit.magnitude();
                    unit = unit.normalize();
                    if(distance<balls.get(i).size)
                    {
                        //balls.get(i).place=clossest.add(unit.multiply(balls.get(i).size));
                        //balls.get(i).acceleration=balls.get(i).acceleration.add(unit);
                    }
                }
                else if(ballP1.magnitude()<balls.get(i).size)
                {
                    //test
                }
                else if(ballP2.magnitude()<balls.get(i).size)
                {
                    //test
                }
            }
            for(int j=0;j<sides.size();j++)
            {
                double Nr0 = sides.get(j).abc.dotProduct(balls.get(i).place);

                double t = (sides.get(j).d - Nr0) / sides.get(j).Nv;

                Point3D intersection = balls.get(i).place.add(sides.get(j).normal.multiply(t));

                double distance = intersection.distance(balls.get(i).place);

                if(distance<balls.get(i).size)
                {
                    if(PointInTriangle(intersection,sides.get(j).points[0],sides.get(j).points[1],sides.get(j).points[2]))
                    {
                        double dir = -(t/Math.abs(t));
                        balls.get(i).place=intersection.add(sides.get(j).normal.multiply(dir*balls.get(i).size));
                        balls.get(i).acceleration=balls.get(i).acceleration.add(sides.get(j).normal.multiply(balls.get(i).velocity.dotProduct(sides.get(j).normal)*dir*2));
                    }
                }
            }
        }
    }

    private boolean PointInTriangle(Point3D p,Point3D a,Point3D b,Point3D c) {
        Point3D v0=c.subtract(a);
        Point3D v1=b.subtract(a);
        Point3D v2=p.subtract(a);

        double v0v0 = v0.dotProduct(v0);
        double v0v1 = v0.dotProduct(v1);
        double v0v2 = v0.dotProduct(v2);
        double v1v1 = v1.dotProduct(v1);
        double v1v2 = v1.dotProduct(v2);

        double u = (v1v1*v0v2-v0v1*v1v2)/(v0v0*v1v1-v0v1*v0v1);
        double v = (v0v0*v1v2-v0v1*v0v2)/(v0v0*v1v1-v0v1*v0v1);
        if(u>=0 && v>=0 && u<=1 && v<=1 && (u+v)<=1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
