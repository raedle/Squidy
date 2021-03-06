/*****************************************************************************
 *                           J3D.org Copyright (c) 2000
 *                                Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.squidy.nodes.optitrack.utils;

// Standard imports
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

// Application specific imports

/**
 * A collection of utility methods to do geometry intersection tests.
 * <p>
 *
 * The design of the implementation is focused towards realtime intersection
 * requirements for collision detection and terrain following. We avoid the
 * standard pattern of making the methods static because we believe that you
 * may need multiple copies of this class floating around. Internally it will
 * also seek to reduce the amount of garbage generated by allocating arrays of
 * data and then maintaining those arrays between calls. Arrays are only
 * resized if they need to get bigger. Smaller data than the currently
 * allocated structures will use the existing data. For the same reason, we
 * do not synchronise any of the methods. If you expect to have multiple
 * threads needing to do intersection testing, we suggest you have separate
 * copies of this class as no results are guaranteed if you are accessing this
 * instance with multiple threads.
 * <p>
 *
 * Calculation of the values works by configuring the class for the sort of
 * data that you want returned. For the higher level methods that allow you
 * <p>
 *
 * If you need the intersection tools for collision detection only, then you
 * can tell the routines that you only need to know if they intersect. That is
 * as soon as you detect one polygon that intersects, exit immediately. This is
 * useful for doing collision detection because you really don't care where on
 * the object you collide, just that you have.
 * <p>
 *
 * The ray/polygon intersection test is a combination test. Firstly it will
 * check for the segment intersection if requested. Then, for an infinite ray
 * or an intersecting segment, we use the algorithm defined from the Siggraph
 * paper in their education course:
 * <ul>
 * <li><a href="http://www.education.siggraph.org/materials/HyperGraph/raytrace/raypolygon_intersection.htm">
 * Ray-Polygon</a></li>
 * <li><a href="http://www.siggraph.org/education/materials/HyperGraph/raytrace/rtinter1.htm">Ray-Sphere</a></li>
 * <li><a href="http://www.siggraph.org/education/materials/HyperGraph/raytrace/rayplane_intersection.htm">Ray-Plane</a></li>
 * <li><a href="http://www.2tothex.com/raytracing/primitives.html">Ray-Cylinder</a></li>
 * </ul>
 *
 * @author Justin Couch
 * @version $Revision: 1.20 $
 */
public class IntersectionUtils
{
    /** Cylinder intersection axis X */
    public static final int X_AXIS = 1;

    /** Cylinder intersection axis Y */
    public static final int Y_AXIS = 2;

    /** Cylinder intersection axis Z */
    public static final int Z_AXIS = 3;

    /** A point that we use for working calculations (coord transforms) */
    private Point3d wkPoint;
    private Vector3d wkVec;

    /** Working vectors */
    private Vector3d v0;
    private Vector3d v1;
    private Vector3d normal;
    private Vector3d diffVec;

    /** Transformed pick items */
    protected Point3d pickStart;
    protected Vector3d pickDir;

    /** The current coordinate list that we work from */
    protected float[] workingCoords;
    protected int[] workingStrips;
    protected int[] workingIndicies;

    /** The current 2D coordinate list that we work from */
    protected float[] working2dCoords;

    /** Working places for a single quad */
    protected float[] wkPolygon;



    /**
     * Create a default instance of this class with no internal data
     * structures allocated.
     */
    public IntersectionUtils()
    {
        wkPoint = new Point3d();
        wkVec = new Vector3d();
        v0 = new Vector3d();
        v1 = new Vector3d();
        normal = new Vector3d();
        diffVec = new Vector3d();
        wkPolygon = new float[12];

        pickStart = new Point3d();
        pickDir = new Vector3d();
    }

    /**
     * Clear the current internal structures to reduce the amount of memory
     * used. It is recommended you use this method with caution as then next
     * time a user calls this class, all the internal structures will be
     * reallocated. If this is running in a realtime environment, that could
     * be very costly - both allocation and the garbage collection that
     * results from calling this method
     */
    public void clear()
    {
        workingCoords = null;
        working2dCoords = null;
        workingStrips = null;
        workingIndicies = null;
    }


   
    /**
     * Compute the intersection point of the ray and a plane. Assumes that the
     * plane equation defines a unit normal in the coefficients a,b,c. If not,
     * weird things happen.
     *
     * @param origin The origin of the ray
     * @param direction The direction of the ray
     * @param plane The coefficients for the plane equation (ax + by + cz + d = 0)
     * @param point The intersection point for returning
     * @return true if there was an intersection, false if not
     */
    public boolean rayPlane(float[] origin,
                             float[] direction,
                             float[] plane,
                             float[] point)
    {
        return rayPlane(origin[0], origin[1], origin[2],
                        direction[0], direction[1], direction[2],
                        plane,
                        point);
    }

    /**
     * Compute the intersection point of the ray and a plane. Assumes that the
     * plane equation defines a unit normal in the coefficients a,b,c. If not,
     * weird things happen.
     *
     * @param origin The origin of the ray
     * @param direction The direction of the ray
     * @param plane The coefficients for the plane equation (ax + by + cz + d = 0)
     * @param point The intersection point for returning
     * @return true if there was an intersection, false if not
     */
    public boolean rayPlane(Point3d origin,
                            Vector3d direction,
                            float[] plane,
                            Point3d point)
    {
        boolean ret_val = rayPlane(origin.x, origin.y, origin.z,
                                   direction.x, direction.y, direction.z,
                                   plane,
                                   wkPolygon);

        point.x = wkPolygon[0];
        point.y = wkPolygon[1];
        point.z = wkPolygon[2];

        return ret_val;
    }


    /**
     * Internal computation of the intersection point of the ray and a plane.
     * Uses raw data types.
     *
     * @param Xo The X coordinate of the origin of the ray
     * @param Yo The Y coordinate of the origin of the ray
     * @param Zo The Z coordinate of the origin of the ray
     * @param Xd The X coordinate of the direction of the ray
     * @param Yd The Y coordinate of the direction of the ray
     * @param Zd The Z coordinate of the direction of the ray
     * @param plane The coefficients for the plane equation (ax + by + cz + d = 0)
     * @param point The intersection point for returning
     * @return true if there was an intersection, false if not
     */
    private boolean rayPlane(double Xo, double Yo, double Zo,
                             double Xd, double Yd, double Zd,
                             float[] plane,
                             float[] point)
   {
        // Dot product between the ray and the normal to the plane
        double angle = Xd * plane[0] + Yd * plane[1] + Zd * plane[2];

        if(angle == 0)
            return false;

        // t = (Pn . Origin + D) / (Pn . Direction)
        // The divisor is the angle calc already calculated
        double Vo = -((plane[0] * Xo + plane[1] * Yo + plane[2] * Zo) + plane[3]);
        double t = Vo / angle;

        if(t < 0)
            return false;

        point[0] = (float)(Xo + Xd * t);
        point[1] = (float)(Yo + Yd * t);
        point[2] = (float)(Zo + Zd * t);

        return true;
    }

    /**
     * Test to see if the polygon intersects with the given ray. The
     * coordinates are ordered as [Xn, Yn, Zn]. The algorithm assumes that
     * the points are co-planar. If they are not, the results may not be
     * accurate. The normal is calculated based on the first 3 points of the
     * polygon. We don't do any testing for less than 3 points.
     *
     * @param origin The origin of the ray
     * @param direction The direction of the ray
     * @param length An optional length for to make the ray a segment. If
     *   the value is zero, it is ignored
     * @param coords The coordinates of the polygon
     * @param numCoords The number of coordinates to use from the array
     * @param point The intersection point for returning
     * @return true if there was an intersection, false if not
     */
    public boolean rayPolygon(Point3d origin,
                              Vector3d direction,
                              float length,
                              float[] coords,
                              int numCoords,
                              Point3d point,
                              boolean faceForward)
    {
        if(coords.length < numCoords * 2)
            throw new IllegalArgumentException("coords too small for numCoords");

        if((working2dCoords == null) ||
           (working2dCoords.length < numCoords * 2))
            working2dCoords = new float[numCoords * 2];

        return rayPolygonChecked(origin,
                                 direction,
                                 length,
                                 coords,
                                 numCoords,
                                 point,
                                 faceForward);
    }
    
    public boolean rayPolygon(Point3d origin,
            Vector3d direction,
            float length,
            Point3d topLeft,
            Point3d bottomLeft,
            Point3d bottomRight,
            Point3d intersectionPiont,
            boolean faceForward)
	{
	
	float coords[] = new float[12];
	
	coords[0] = (float) bottomLeft.x;
	coords[1] = (float) bottomLeft.y;
	coords[2] = (float) bottomLeft.z;
	
	coords[3] = (float) bottomRight.x;
	coords[4] = (float) bottomRight.y;
	coords[5] = (float) bottomRight.z;
	
	coords[6] = (float) (bottomRight.x + (topLeft.x - bottomLeft.x));
	coords[7] = (float) (bottomRight.y + (topLeft.y - bottomLeft.y));
	coords[8] = (float) (bottomRight.z + (topLeft.z - bottomLeft.z));
	
	coords[9] = (float) topLeft.x;
	coords[10] = (float) topLeft.y;
	coords[11] = (float) topLeft.z;
	
	return rayPolygonChecked(origin,
	               direction,
	               length,
	               coords,
	               4,
	               intersectionPiont,
	               faceForward);
	}

    /**
     * Private version of the ray - Polygon intersection test that does not
     * do any bounds checking on arrays and assumes everything is correct.
     * Allows fast calls to this method for internal use as well as more
     * expensive calls with checks for the public interfaces.
     * <p>
     * This method does not use wkPoint.
     *
     * @param origin The origin of the ray
     * @param direction The direction of the ray
     * @param length An optional length for to make the ray a segment. If
     *   the value is zero, it is ignored
     * @param coords The coordinates of the polygon
     * @param numCoords The number of coordinates to use from the array
     * @param point The intersection point for returning
     * @return true if there was an intersection, false if not
     */
    private boolean rayPolygonChecked(Point3d origin,
                                      Vector3d direction,
                                      float length,
                                      float[] coords,
                                      int numCoords,
                                      Point3d intersectionPiont,
                                      boolean faceForward)
    {
        int i, j;

        v0.x = coords[3] - coords[0];
        v0.y = coords[4] - coords[1];
        v0.z = coords[5] - coords[2];

        v1.x = coords[6] - coords[3];
        v1.y = coords[7] - coords[4];
        v1.z = coords[8] - coords[5];

        normal.cross(v0, v1);

        // degenerate polygon?
        if(normal.lengthSquared() == 0)
            return false;

        double n_dot_dir = normal.dot(direction);

        // ray and plane parallel?
        if(n_dot_dir == 0)
            return false;

        wkVec.x = coords[0];
        wkVec.y = coords[1];
        wkVec.z = coords[2];
        double d = normal.dot(wkVec);

        wkVec.set(origin);
        double n_dot_o = normal.dot(wkVec);
        
        // check if pointing to front or backside of object
        if (n_dot_o < 0)
        	faceForward = false;
        else
        	faceForward = true;
        
        // t = (d - N.O) / N.D
        double t = Math.abs((d - n_dot_o) / n_dot_dir);

        // intersection before the origin
//        if(t < 0)
//            return false;

        // So we have an intersection with the plane of the polygon and the
        // segment/ray. Using the winding rule to see if inside or outside
        // First store the exact intersection point anyway, regardless of
        // whether this is an intersection or not.
        intersectionPiont.x = origin.x + direction.x * t;
        intersectionPiont.y = origin.y + direction.y * t;
        intersectionPiont.z = origin.z + direction.z * t;

        // Intersection point after the end of the segment?
        if((length != 0) && (origin.distance(intersectionPiont) > length))
            return false;

        // bounds check

        // find the dominant axis to resolve to a 2 axis system
        double abs_nrm_x = (normal.x >= 0) ? normal.x : -normal.x;
        double abs_nrm_y = (normal.y >= 0) ? normal.y : -normal.y;
        double abs_nrm_z = (normal.z >= 0) ? normal.z : -normal.z;

        int dom_axis;

        if(abs_nrm_x > abs_nrm_y)
            dom_axis = 0;
        else
            dom_axis = 1;

        if(dom_axis == 0)
        {
            if(abs_nrm_x < abs_nrm_z)
                dom_axis = 2;
        }
        else if(abs_nrm_y < abs_nrm_z)
        {
            dom_axis = 2;
        }

        // Map all the coordinates to the 2D plane. The u and v coordinates
        // are interleaved as u == even indicies and v = odd indicies

        // Steps 1 & 2 combined
        // 1. For NV vertices [Xn Yn Zn] where n = 0..Nv-1, project polygon
        // vertices [Xn Yn Zn] onto dominant coordinate plane (Un Vn).
        // 2. Translate (U, V) polygon so intersection point is origin from
        // (Un', Vn').
        j = 2 * numCoords - 1;
        working2dCoords = new float[j+1];
        switch(dom_axis)
        {
            case 0:
                for(i = numCoords; --i >= 0; )
                {
                    working2dCoords[j--] = coords[i * 3 + 2] - (float)intersectionPiont.z;
                    working2dCoords[j--] = coords[i * 3 + 1] - (float)intersectionPiont.y;
                }
                break;

            case 1:
                for(i = numCoords; --i >= 0; )
                {
                    working2dCoords[j--] = coords[i * 3 + 2] - (float)intersectionPiont.z;
                    working2dCoords[j--] = coords[i * 3]     - (float)intersectionPiont.x;
                }
                break;

            case 2:
                for(i = numCoords; --i >= 0; )
                {
                    working2dCoords[j--] = coords[i * 3 + 1] - (float)intersectionPiont.y;
                    working2dCoords[j--] = coords[i * 3]     - (float)intersectionPiont.x;
                }
                break;
        }

        int sh;  // current sign holder
        int nsh; // next sign holder
        float dist;
        int crossings = 0;

        // Step 4.
        // Set sign holder as f(V' o) ( V' value of 1st vertex of 1st edge)
        if(working2dCoords[1] < 0.0)
            sh = -1;
        else
            sh = 1;

        for(i = 0; i < numCoords; i++)
        {
            // Step 5.
            // For each edge of polygon (Ua' V a') -> (Ub', Vb') where
            // a = 0..Nv-1 and b = (a + 1) mod Nv

            // b = (a + 1) mod Nv
            j = (i + 1) % numCoords;

            int i_u = i * 2;           // index of Ua'
            int j_u = j * 2;           // index of Ub'
            int i_v = i * 2 + 1;       // index of Va'
            int j_v = j * 2 + 1;       // index of Vb'

            // Set next sign holder (Nsh) as f(Vb')
            // Nsh = -1 if Vb' < 0
            // Nsh = +1 if Vb' >= 0
            if(working2dCoords[j_v] < 0.0)
                nsh = -1;
            else
                nsh = 1;

            // If Sh <> NSH then if = then edge doesn't cross +U axis so no
            // ray intersection and ignore

            if(sh != nsh)
            {
                // if Ua' > 0 and Ub' > 0 then edge crosses + U' so Nc = Nc + 1
                if((working2dCoords[i_u] > 0.0) && (working2dCoords[j_u] > 0.0))
                {
                    crossings++;
                }
                else if ((working2dCoords[i_u] > 0.0) ||
                         (working2dCoords[j_u] > 0.0))
                {
                    // if either Ua' or U b' > 0 then line might cross +U, so
                    // compute intersection of edge with U' axis
                    dist = working2dCoords[i_u] -
                           (working2dCoords[i_v] *
                            (working2dCoords[j_u] - working2dCoords[i_u])) /
                           (working2dCoords[j_v] - working2dCoords[i_v]);

                    // if intersection point is > 0 then must cross,
                    // so Nc = Nc + 1
                    if(dist > 0)
                        crossings++;
                }

                // Set SH = Nsh and process the next edge
                sh = nsh;
            }
        }

        // Step 6. If Nc odd, point inside else point outside.
        // Note that we have already stored the intersection point way back up
        // the start.
        return ((crossings % 2) == 1);
    }
}
