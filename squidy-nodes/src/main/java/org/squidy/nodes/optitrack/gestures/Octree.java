package org.squidy.nodes.optitrack.gestures;

import java.util.ArrayList;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.squidy.manager.data.impl.DataPosition3D;


public class Octree {

    protected  OctreeNode top;

    public Octree()
    {
    	this(1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 20, OctreeNode.NO_MIN_SIZE);
    }

    public Octree(float xMax, float xMin, float yMax, float yMin, float zMax, float zMin, int maxItems)
    {
    	this(xMax, xMin, yMax, yMin, zMax, zMin, maxItems, OctreeNode.NO_MIN_SIZE);
    }

    public Octree(int up, int left, int down, int right, int Front, int Back, int maxItems)
    {
    	 this((float)up, (float)left, (float)down, (float)right, (float)Front, (float)Back, maxItems, OctreeNode.DEFAULT_MIN_SIZE);
    }

    public Octree(float xMax, float xMin, float yMax, float yMin, float zMax, float zMin, int maxItems, float minSize)
    {
        top = new OctreeNode(xMax, xMin, yMax, yMin, zMax, zMin, maxItems, minSize);
    }

 

    /// <summary> Add a Object into the tree at a location.
    /// </summary>
    /// <param name="x">up-down location in Octree Grid</param>
    /// <param name="y">left-right location in Octree Grid</param>
    /// <param name="z">front-back location in Octree Grid</param>
    /// <returns> true if the insertion worked. </returns>
    public boolean AddNode(float x, float y, float z, Object obj, String name)
    {
        return top.AddNode(x, y, z, obj, name);
    }
    public boolean AddNode(float x, float y, float z, int obj, String name)
    {
        return top.AddNode(x, y, z, obj, name);
    }
    /*public boolean AddNode(float x, float y, float z, uint obj)
    {
        return top.AddNode(x, y, z, obj);
    }*/
    public boolean AddNode(float x, float y, float z, short obj, String name)
    {
        return top.AddNode(x, y, z, obj, name);
    }
    public boolean AddNode(float x, float y, float z, long obj, String name)
    {
        return top.AddNode(x, y, z, obj, name);
    }
    public boolean AddNode(float x, float y, float z, float obj, String name)
    {
        return top.AddNode(x, y, z, obj, name);
    }
    public boolean AddNode(float x, float y, float z, double obj, String name)
    {
        return top.AddNode(x, y, z, obj, name);
    }
    public boolean AddNode(float x, float y, float z, boolean obj, String name)
    {
        return top.AddNode(x, y, z, obj, name);
    }

    public boolean AddNode(Vector3f vector, Object obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }
    public boolean AddNode(Vector3f vector, int obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }
    /*public boolean AddNode(Vector3f vector, uint obj)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj);
    }*/
    public boolean AddNode(Vector3f vector, short obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }
    public boolean AddNode(Vector3f vector, long obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }
    public boolean AddNode(Vector3f vector, float obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }
    public boolean AddNode(Vector3f vector, double obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }
    public boolean AddNode(Vector3f vector, boolean obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }


    /*public boolean AddNode(double x, double y, double z, Object obj)
    {
        return top.AddNode(x, y, z, obj);
    }*/
    public boolean AddNode(double x, double y, double z, StaticGesture staticGesture, int index, String gestureName, String handSide)
    {
        return top.AddNode(x, y, z, staticGesture, index,gestureName, handSide);
    }
    /*public boolean AddNode(double x, double y, double z, uint obj)
    {
        return top.AddNode(x, y, z, obj);
    }*/
    public boolean AddNode(double x, double y, double z, short obj, String name)
    {
        return top.AddNode(x, y, z, obj, name);
    }
    public boolean AddNode(double x, double y, double z, long obj, String name)
    {
        return top.AddNode(x, y, z, obj, name);
    }
    public boolean AddNode(double x, double y, double z, float obj, String name)
    {
        return top.AddNode(x, y, z, obj, name);
    }
    public boolean AddNode(double x, double y, double z, double obj, String name)
    {
        return top.AddNode(x, y, z, obj, name);
    }
    public boolean AddNode(double x, double y, double z, boolean obj, String name)
    {
        return top.AddNode(x, y, z, obj, name);
    }

    public boolean AddNode(Vector3d vector, Object obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }
    public boolean AddNode(Vector3d vector, int obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }
    /*public boolean AddNode(Vector3d vector, uint obj)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj);
    }*/
    public boolean AddNode(Vector3d vector, short obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }
    public boolean AddNode(Vector3d vector, long obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }
    public boolean AddNode(Vector3d vector, float obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }
    public boolean AddNode(Vector3d vector, double obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }
    public boolean AddNode(Vector3d vector, boolean obj, String name)
    {
        return top.AddNode(vector.x, vector.y, vector.z, obj, name);
    }

    /// <summary> Remove a Object out of the tree at a location. </summary>
    /// <param name="x">up-down location in Octree Grid (x, y)</param>
    /// <param name="y">left-right location in Octree Grid (y, x)</param>
    /// <returns> the Object removed, null if the Object not found.
    /// </returns>
    public Object RemoveNode(float x, float y, float z, Object obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }
    public Object RemoveNode(float x, float y, float z, int obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }
    /*public Object RemoveNode(float x, float y, float z, uint obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }*/
    public Object RemoveNode(float x, float y, float z, short obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }
    public Object RemoveNode(float x, float y, float z, long obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }
    public Object RemoveNode(float x, float y, float z, float obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }
    public Object RemoveNode(float x, float y, float z, double obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }
    public Object RemoveNode(float x, float y, float z, boolean obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }

    public Object RemoveNode(Vector3f vector, Object obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
    public Object RemoveNode(Vector3f vector, int obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
    /*public Object RemoveNode(Vector3f vector, uint obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }*/
    public Object RemoveNode(Vector3f vector, short obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
    public Object RemoveNode(Vector3f vector, long obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
    public Object RemoveNode(Vector3f vector, float obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
    public Object RemoveNode(Vector3f vector, double obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
    public Object RemoveNode(Vector3f vector, boolean obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }

    public Object RemoveNode(double x, double y, double z, Object obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }
    public Object RemoveNode(double x, double y, double z, int obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }
    /*public Object RemoveNode(double x, double y, double z, uint obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }*/
    public Object RemoveNode(double x, double y, double z, short obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }
    public Object RemoveNode(double x, double y, double z, long obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }
    public Object RemoveNode(double x, double y, double z, float obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }
    public Object RemoveNode(double x, double y, double z, double obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }
    public Object RemoveNode(double x, double y, double z, boolean obj)
    {
        return top.RemoveNode(x, y, z, obj);
    }

    public Object RemoveNode(Vector3d vector, Object obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
    public Object RemoveNode(Vector3d vector, int obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
    /*public Object RemoveNode(Vector3d vector, uint obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }*/
    public Object RemoveNode(Vector3d vector, short obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
    public Object RemoveNode(Vector3d vector, long obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
    public Object RemoveNode(Vector3d vector, float obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
    public Object RemoveNode(Vector3d vector, double obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
    public Object RemoveNode(Vector3d vector, boolean obj)
    {
        return top.RemoveNode(vector.x, vector.y, vector.z, obj);
    }
   

    /// <summary> Get an Object closest to a x/y. </summary>
    /// <param name="x">up-down location in Octree Grid (x, y)</param>
    /// <param name="y">left-right location in Octree Grid (y, x)</param>
    /// <returns> the Object that was found.</returns>
    public Object GetNode(float x, float y, float z)
    {
        return top.GetNode(x, y, z);
    }
    public Object GetNode(Vector3f vector)
    {
        return top.GetNode(vector.x, vector.y, vector.z);
    }
    public Object GetNode(DataPosition3D d3d)
    {
        return top.GetNode(d3d.getX(), d3d.getY(), d3d.getZ());
    }
    public Object GetNode(double x, double y, double z)
    {
        return top.GetNode(x, y, z);
    }
    public Object GetNode(Vector3d vector)
    {
        return top.GetNode(vector.x, vector.y, vector.z);
    }

    /// <summary> Get an Object closest to a x/y, within a maximum distance.
    /// 
    /// </summary>
    /// <param name="x">up-down location in Octree Grid (x, y)
    /// </param>
    /// <param name="y">left-right location in Octree Grid (y, x)
    /// </param>
    /// <param name="withinDistance">the maximum distance to get a hit, in
    /// decimal degrees.
    /// </param>
    /// <returns> the Object that was found, null if nothing is within
    /// the maximum distance.
    /// </returns>
    public Object GetNode(float x, float y, float z, double withinDistance)
    {
        return top.GetNode(x, y, z, withinDistance);
    }
    public Object GetNode(Vector3f vector, double withinDistance)
    {
        return top.GetNode(vector.x, vector.y, vector.z, withinDistance);
    }
    public Object GetNode(double x, double y, double z, double withinDistance)
    {
        return top.GetNode(x, y, z, withinDistance);
    }
    public Object GetNode(Vector3d vector, double withinDistance)
    {
        return top.GetNode(vector.x, vector.y, vector.z, withinDistance);
    }

    /// <summary> Get all the Objects within a bounding box.
    /// 
    /// </summary>
    /// <param name="Top">top location in Octree Grid (x, y)
    /// </param>
    /// <param name="Left">left location in Octree Grid (y, x)
    /// </param>
    /// <param name="Bottom">lower location in Octree Grid (x, y)
    /// </param>
    /// <param name="Right">right location in Octree Grid (y, x)
    /// </param>
    /// <returns> ArrayList of Objects.
    /// </returns>
    public ArrayList GetNode(float xMax, float xMin, float yMax, float yMin, float zMax, float zMin)
    {
        return GetNode(xMax, xMin, yMax, yMin, zMax, zMin, new ArrayList(100));
    }
    public ArrayList GetNode(double xMax, double xMin, double yMax, double yMin, double zMax, double zMin)
    {
        return GetNode(xMax, xMin, yMax, yMin, zMax, zMin, new ArrayList());
    }

    /// <summary> Get all the Objects within a bounding box, and return the
    /// Objects within a given Vector.
    /// 
    /// </summary>
    /// <param name="Top">top location in Octree Grid (x, y)
    /// </param>
    /// <param name="Left">left location in Octree Grid (y, x)
    /// </param>
    /// <param name="Bottom">lower location in Octree Grid (x, y)
    /// </param>
    /// <param name="Right">right location in Octree Grid (y, x)
    /// </param>
    /// <param name="vector">a vector to add Objects to.
    /// </param>
    /// <returns> ArrayList of Objects.
    /// </returns>
    public ArrayList GetNode(float xMax, float xMin, float yMax, float yMin, float zMax, float zMin, ArrayList nodes)
    {
        if (nodes == null)
            nodes = new ArrayList(10);
        
        if (xMin > xMax || (Math.abs(xMin - xMax) < 1e-6))
            return top.GetNode(xMax, xMin, yMax, yMin, zMax, zMin, top.GetNode(xMax, 0, yMax, yMin, zMax, zMin, nodes));
        else
            return top.GetNode(xMax, xMin, yMax, yMin, zMax, zMin, nodes);
    }
    /// <summary>
    /// 
    /// </summary>
    /// <param name="xMax"></param>
    /// <param name="xMin"></param>
    /// <param name="yMax"></param>
    /// <param name="yMin"></param>
    /// <param name="zMax"></param>
    /// <param name="zMin"></param>
    /// <param name="nodes"></param>
    /// <returns></returns>
    public ArrayList GetNode(double xMax, double xMin, double yMax, double yMin, double zMax, double zMin, ArrayList nodes)
    {
        if (nodes == null)
            nodes = new ArrayList(10);

        if (xMin > xMax || (Math.abs(xMin - xMax) < 1e-6))
            return top.GetNode(xMax, xMin, yMax, yMin, zMax, zMin, top.GetNode(xMax, 0, yMax, yMin, zMax, zMin, nodes));
        else
            return top.GetNode(xMax, xMin, yMax, yMin, zMax, zMin, nodes);
    }

            /// <summary> Get an Object closest to a x/y, within a maximum distance.
    /// 
    /// </summary>
    /// <param name="x">up-down location in Octree Grid (x, y)
    /// </param>
    /// <param name="y">left-right location in Octree Grid (y, x)
    /// </param>
    /// <param name="withinDistance">the maximum distance to get a hit, in
    /// decimal degrees.
    /// </param>
    /// <returns> the Objects that were found  within the maximum radius.
    /// </returns>
    public ArrayList GetNodes(float x, float y, float z, double radius)
    {
        return top.GetNodes(x, y, z, radius);
    }
    public ArrayList GetNodes(Vector3f vector, double radius)
    {
        return top.GetNodes(vector.x, vector.y, vector.z, radius);
    }
    public ArrayList GetNodes(double x, double y, double z, double radius)
    {
        return top.GetNodes(x, y, z, radius);
    }
    public ArrayList GetNodes(Vector3d vector, double radius)
    {
        return top.GetNodes(vector.x, vector.y, vector.z, radius);
    }

    /// <summary> Get an Object closest to a x/y, within a maximum distance./// </summary>
    /// <param name="x">up-down location in Octree Grid (x, y)</param>
    /// <param name="y">left-right location in Octree Grid (y, x)</param>
    /// <param name="withinDistance">the maximum distance to get a hit, in
    /// decimal degrees.</param>
    /// <returns> the Objects that were found  within the maximum radius.</returns>
    public ArrayList GetNodes(float x, float y, float z, double MinRadius, double MaxRadius)
    {
        return top.GetNodes(x, y, z, MinRadius, MaxRadius);
    }
    public ArrayList GetNodes(Vector3f vector, double MinRadius, double MaxRadius)
    {
        return top.GetNodes(vector.x, vector.y, vector.z, MinRadius, MaxRadius);
    }
    public ArrayList GetNodes(double x, double y, double z, double MinRadius, double MaxRadius)
    {
        return top.GetNodes(x, y, z, MinRadius, MaxRadius);
    }
    public ArrayList GetNodes(Vector3d vector, double MinRadius, double MaxRadius)
    {
        return top.GetNodes(vector.x, vector.y, vector.z, MinRadius, MaxRadius);
    }

    /// <summary>Clear the tree. </summary>
    public void Clear()
    {
        top.Clear();
    }
}
