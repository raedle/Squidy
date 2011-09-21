package org.squidy.nodes.optitrack.utils;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.squidy.designer.model.Data;
import org.squidy.manager.IProcessable;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.util.MathUtility;


public class TrackingUtility {

	public static DataPosition6D Room2NormCoordinates(Class<? extends IProcessable<?>> source, DataPosition6D tmp6D)
	{
		tmp6D.setX(Room2NormPoint(tmp6D.getX(),Double.valueOf((tmp6D.getAttribute(DataConstant.MAX_X)).toString()),Double.valueOf((tmp6D.getAttribute(DataConstant.CenterOffset_X)).toString())));
		tmp6D.setY(Room2NormPoint(tmp6D.getY(),Double.valueOf((tmp6D.getAttribute(DataConstant.MAX_Y)).toString()),Double.valueOf((tmp6D.getAttribute(DataConstant.CenterOffset_Y)).toString())));
		tmp6D.setZ(Room2NormPoint(tmp6D.getZ(),Double.valueOf((tmp6D.getAttribute(DataConstant.MAX_Z)).toString()),Double.valueOf((tmp6D.getAttribute(DataConstant.CenterOffset_Z)).toString())));
	    return tmp6D;
	}
	public static DataPosition6D Norm2RoomCoordinates(Class<? extends IProcessable<?>> source, DataPosition6D tmp6D)
	{
		tmp6D.setX(Norm2RoomPoint(tmp6D.getX(),Double.valueOf((tmp6D.getAttribute(DataConstant.MAX_X)).toString()),Double.valueOf((tmp6D.getAttribute(DataConstant.CenterOffset_X)).toString())));
		tmp6D.setY(Norm2RoomPoint(tmp6D.getY(),Double.valueOf((tmp6D.getAttribute(DataConstant.MAX_Y)).toString()),Double.valueOf((tmp6D.getAttribute(DataConstant.CenterOffset_Y)).toString())));
		tmp6D.setZ(Norm2RoomPoint(tmp6D.getZ(),Double.valueOf((tmp6D.getAttribute(DataConstant.MAX_Z)).toString()),Double.valueOf((tmp6D.getAttribute(DataConstant.CenterOffset_Z)).toString())));
		return tmp6D;
	}
	public static DataPosition3D Room2NormCoordinates(Class<? extends IProcessable<?>> source, DataPosition3D tmp3D)
	{		
		tmp3D.setX(Room2NormPoint(tmp3D.getX(),Double.valueOf((tmp3D.getAttribute(DataConstant.MAX_X)).toString()),Double.valueOf((tmp3D.getAttribute(DataConstant.CenterOffset_X)).toString())));
		tmp3D.setY(Room2NormPoint(tmp3D.getY(),Double.valueOf((tmp3D.getAttribute(DataConstant.MAX_Y)).toString()),Double.valueOf((tmp3D.getAttribute(DataConstant.CenterOffset_Y)).toString())));
		tmp3D.setZ(Room2NormPoint(tmp3D.getZ(),Double.valueOf((tmp3D.getAttribute(DataConstant.MAX_Z)).toString()),Double.valueOf((tmp3D.getAttribute(DataConstant.CenterOffset_Z)).toString())));
		return tmp3D;
	}
	
	public static DataPosition3D Norm2RoomCoordinates(Class<? extends IProcessable<?>> source,DataPosition3D tmp3D)
	{
		tmp3D.setX(Norm2RoomPoint(tmp3D.getX(),Double.valueOf((tmp3D.getAttribute(DataConstant.MAX_X)).toString()),Double.valueOf((tmp3D.getAttribute(DataConstant.CenterOffset_X)).toString())));
		tmp3D.setY(Norm2RoomPoint(tmp3D.getY(),Double.valueOf((tmp3D.getAttribute(DataConstant.MAX_Y)).toString()),Double.valueOf((tmp3D.getAttribute(DataConstant.CenterOffset_Y)).toString())));
		tmp3D.setZ(Norm2RoomPoint(tmp3D.getZ(),Double.valueOf((tmp3D.getAttribute(DataConstant.MAX_Z)).toString()),Double.valueOf((tmp3D.getAttribute(DataConstant.CenterOffset_Z)).toString())));
		return tmp3D;
	}

	public static double Room2NormPoint(double point, double max, double centerOffset)
	{
		if(centerOffset < 0)
			return (point + centerOffset *-1) / max;
		else
			return (point + centerOffset) / max;
	}
	
	public static double Norm2RoomPoint(double point, double max , double centerOffset)
	{
		if (centerOffset < 0)
			return (point * -max) - centerOffset;
		else
			return (point * max) - centerOffset;
	}
	
	public static String setAttributes(double value)
	{
		return String.valueOf(value);
	}
	
	public static String getAttributesAlpha(IData d6d, DataConstant dc)
	{
		if (d6d.hasAttribute(dc))
			return d6d.getAttribute(dc).toString();
		else
			return "";
	}
	
	public static Integer getAttributesInteger(IData  data, DataConstant dc)
	{
		if(data.hasAttribute(dc))
			return (Integer.valueOf(data.getAttribute(dc).toString()));
		else
			return -1;
	}
	public static Double getAttributesDouble(IData  d6d, DataConstant dc)
	{
		if(d6d.hasAttribute(dc))
			return (Double.valueOf(d6d.getAttribute(dc).toString()));
		else
			return -1.0;
	}	
	public static Boolean getAttributesBoolean(IData  d6d, DataConstant dc)
	{
		if(d6d.hasAttribute(dc))
			return (Boolean.valueOf(d6d.getAttribute(dc).toString()));
		else
			return false;
	}	
	
	public static Vector3d getTargetNorm(DataPosition6D d6d)
	{
		return  new Vector3d(d6d.getM00(), d6d.getM01(), d6d.getM02());
	}
	public static Vector3d getTargetDirection(DataPosition6D d6d)
	{
		Vector3d dirZ = new Vector3d(0,0,1);
		Vector3d dir = new Vector3d();
		Vector3d cent = new Vector3d(0,0,0);
		MathUtility mu = new MathUtility();
		dir = mu.rotatePoint(dirZ, cent, mu.dataPosition6D2matrix(d6d),false,false);
		dir.normalize();
		return dir;
	}
	public static Point3d getPoint3d(DataPosition3D d3d)
	{
		return new Point3d(d3d.getX(),d3d.getY(), d3d.getZ());
	}
	public static Vector3d getVector3d(DataPosition3D d3d)
	{
		return new Vector3d(d3d.getX(),d3d.getY(), d3d.getZ());
	}
	
	public static int gestureID2Constant(int input)
	{
		return input + TrackingConstant.GESTURE_DEFAULT;
	}
	public static int gestureConstant2ID(int input)
	{
		return input - TrackingConstant.GESTURE_DEFAULT;
	}
	public static int getGestureBivariate(int input)
	{
		if (input > 10)
		{
			return gestureID2Constant(input);
		}else
		{
			return gestureConstant2ID(input);
		}
	}
    public static double minmax(double x, double max, double min, double new_max, double new_min)
    {
          x = (x - min) * (new_max - new_min) / (max - min) + new_min;
          return x;
    }
}
