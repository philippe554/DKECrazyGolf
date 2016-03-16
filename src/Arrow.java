import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import java.util.ArrayList;

/**
 * Created by Maxim on 3/16/2016.
 */
public class Arrow extends Shape3D {

    private LineArray line;


    public Arrow(ArrayList coords, Color3f col)
    {
        line = new LineArray (coords.size(), GeometryArray.COORDINATES | GeometryArray.COLOR_3 );

        createGeometry(coords, col);
        createAppearance();
    }


    private void createGeometry(ArrayList<Point3f> coords, Color3f col)
    {
        int numPoints = coords.size();

        Point3f[] points = new Point3f[numPoints];
        for(int i=0; i < numPoints; i++)
            points[i] = coords.get(i);
        line.setCoordinates(0, points);

        Color3f cols[] = new Color3f[numPoints];
        for(int i=0; i < numPoints; i++)
            cols[i] = col;
        line.setColors(0, cols);

        setGeometry(line);
    }  // end of createGeometry()


    private void createAppearance()
    {
        Appearance app = new Appearance();
        LineAttributes attribute = new LineAttributes();
        attribute.setLineWidth(10.0f);
        app.setLineAttributes(attribute);
        PolygonAttributes pa = new PolygonAttributes();
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        // so can see the ColouredTiles from both sides
        app.setPolygonAttributes(pa);

        setAppearance(app);
    }  // end of createAppearance()
}