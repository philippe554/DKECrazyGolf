import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import java.util.ArrayList;

/**
 * Created by pmmde on 3/14/2016.
 */
public class Triangle extends Shape3D {

    private TriangleArray plane;


    public Triangle(ArrayList coords, Color3f col)
    {
        plane = new TriangleArray (coords.size(), GeometryArray.COORDINATES | GeometryArray.COLOR_3 );
        createGeometry(coords, col);
        createAppearance();
    }


    private void createGeometry(ArrayList<Point3f> coords, Color3f col)
    {
        int numPoints = coords.size();

        Point3f[] points = new Point3f[numPoints];
        for(int i=0; i < numPoints; i++)
            points[i] = coords.get(i);
        plane.setCoordinates(0, points);

        Color3f cols[] = new Color3f[numPoints];
        for(int i=0; i < numPoints; i++)
            cols[i] = col;
        plane.setColors(0, cols);

        setGeometry(plane);
    }  // end of createGeometry()


    private void createAppearance()
    {
        Appearance app = new Appearance();

        PolygonAttributes pa = new PolygonAttributes();
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        // so can see the ColouredTiles from both sides
        app.setPolygonAttributes(pa);

        setAppearance(app);
    }  // end of createAppearance()
}
