package org.squidy.nodes.optitrack.gestures;

public class OctreeLeaf {
    public float X, Y, Z;
    public Object LeafObject;
    public int LeafIndex;
    public String LeafName;
    public double LeafDistance;
    public String LeafGestureName;
    public String LeafHandSide;

    public OctreeLeaf(float x, float y, float z, Object obj, int index, String gestureName, String handSide)
    {
        X = x;
        Y = y;
        Z = z;
        LeafObject = obj;
        LeafIndex = index;
        LeafGestureName = gestureName;
        LeafHandSide = handSide;
       
    }
    public OctreeLeaf(float x, float y, float z, Object obj, String name)
    {
        X = x;
        Y = y;
        Z = z;
        LeafObject = obj;
        LeafName = name;
        LeafGestureName = "";
        LeafHandSide = "";
       
    }
    public OctreeLeaf(float x, float y, float z, int obj, String name)
    {
    	this(x, y, z, (Object)obj,name);
    }
    /*public OctreeLeaf(float x, float y, float z, uint obj)
    {
    	this(x, y, z, (Object)obj);
    }*/
    public OctreeLeaf(float x, float y, float z, short obj, String name)
    {
    	this(x, y, z, (Object)obj,name);
    }   
    public OctreeLeaf(float x, float y, float z, long obj, String name)
    {
    	this(x, y, z, (Object)obj,name);
    }
    public OctreeLeaf(float x, float y, float z, float obj, String name)
    {
    	this(x, y, z, (Object)obj,name);
    }
    public OctreeLeaf(float x, float y, float z, double obj, String name)
    {
    	this(x, y, z, (Object)obj,name);
    }
    public OctreeLeaf(float x, float y, float z, boolean obj, String name)
    {
    	this(x, y, z, (Object)obj,name);
    }

    public OctreeLeaf(double x, double y, double z, Object obj, String name)
    {
    	this((float)x, (float)y, (float)z, (Object)obj,name);
    }
    public OctreeLeaf(double x, double y, double z, int obj, String name)
    {
    	this(x, y, z, (Object)obj,name);
    }
    /*public OctreeLeaf(double x, double y, double z, uint obj)
    {
    	this(x, y, z, (Object)obj);
    }*/
    public OctreeLeaf(double x, double y, double z, short obj, String name)
    {
    	this(x, y, z, (Object)obj,name);
    }
    public OctreeLeaf(double x, double y, double z, long obj, String name)
    {
    	this(x, y, z, (Object)obj,name);
    }
    public OctreeLeaf(double x, double y, double z, float obj, String name)
    {
    	this(x, y, z, (Object)obj,name);
    }
    public OctreeLeaf(double x, double y, double z, double obj, String name)
    {
    	this(x, y, z, (Object)obj,name);
    }
    public OctreeLeaf(double x, double y, double z, boolean obj, String name)
    {
    	this(x, y, z, (Object)obj,name);
    }

  /*  public Object LeafObject
    {
        get
        {
            return LeafObject;
        }
    }

    public float X
    {
        get
        {
            return X;
        }
        set
        {
            X = value; ;
        }
    }
    public float Y
    {
        get
        {
            return Y;
        }
        set
        {
            Y = value; ;
        }
    }
    public float Z
    {
        get
        {
            return Z;
        }
        set
        {
            Z = value; ;
        }
    }*/
}
