package org.squidy.nodes.optitrack.gestures;

public class OctreeBox {

    public float Top;
    public float Bottom;
    public float Left;
    public float Right;
    public float Front;
    public float Back;

    public OctreeBox(float xMax, float xMin, float yMax, float yMin, float zMax, float zMin)
    {
        Right = xMax;
        Left = xMin;
        Front = yMax;
        Back = yMin;
        Top = zMax;
        Bottom = zMin;
    }

    public OctreeBox(double xMax, double xMin, double yMax, double yMin, double zMax, double zMin)
    {
    	this((float)xMax, (float)xMin, (float)yMax, (float)yMin, (float)zMax, (float)zMin);
    }

    public boolean within(OctreeBox Box)
    {
        return within(Box.Top, Box.Left, Box.Bottom, Box.Right, Box.Front, Box.Back);
    }
    public boolean within(float xMax, float xMin, float yMax, float yMin, float zMax, float zMin)
    {
        if (xMin >= Right ||
            xMax < Left ||
            yMin >= Front ||
            yMax < Back ||
            zMin >= Top ||
            zMax < Bottom)
            return false;

        return true;
    }
    public boolean within(double xMax, double xMin, double yMax, double yMin, double zMax, double zMin)
    {
        if (
            xMin >= Right ||
            xMax < Left ||
            yMin >= Front ||
            yMax < Back ||
            zMin >= Top ||
            zMax < Bottom
            )
            return false;

        return true;
    }

    public boolean pointWithinBounds(float x, float y, float z)
    {
        if (x <= Right && 
            x > Left &&  
            y <= Front && 
            y > Back && 
            z <= Top && 
            z > Bottom)
            return true;
        else
            return false;
    }
    public boolean pointWithinBounds(double x, double y, double z)
    {
        if (x <= Right &&
            x > Left &&
            y <= Front &&
            y > Back &&
            z <= Top &&
            z > Bottom) 
            return true;
        else
            return false;
    }

    /// <summary> A utility method to figure out the closest distance of a border
    /// to a point. If the point is inside the rectangle, return 0.
    /// </summary>
    /// <param name="x">up-down location in Octree Grid (x, y)</param>
    /// <param name="y">left-right location in Octree Grid (y, x)</param>
    /// <returns> closest distance to the point. </returns>
    public double borderDistance(float x, float y, float z)
    {
        double nsdistance;
        double ewdistance;
        double fbdistance;

        if (Left <= x && x <= Right)
            ewdistance = 0;
        else
            ewdistance = Math.min((Math.abs(x - Right)), (Math.abs(x - Left)));

        if (Front <= y && y <= Back)
            fbdistance = 0;
        else
            fbdistance = Math.min(Math.abs(y - Back), Math.abs(y - Front));

        if (Bottom <= z && z <= Top)
            nsdistance = 0;
        else
            nsdistance = Math.min(Math.abs(z - Top), Math.abs(z - Bottom));

        return Math.sqrt(nsdistance * nsdistance +
                         ewdistance * ewdistance +
                         fbdistance * fbdistance);
    }
    public double borderDistance(double x, double y, double z)
    {
        return borderDistance((float)x, (float)y, (float)z);
    }

    /*public float Right
    {
        get { return this.right; }
        set { this.right = value; }
    }
    public float Left
    {
        get { return this.left; }
        set { this.left = value; }
    }
    public float Front
    {
        get { return this.front; }
        set { this.front = value; }
    }
    public float Back
    {
        get { return this.back; }
        set { this.back = value; }
    }
    public float Top
    {
        get { return this.top; }
        set { this.top = value; }
    }
    public float Bottom
    {
        get { return this.bottom; }
        set { this.bottom = value; }
    }*/
}
