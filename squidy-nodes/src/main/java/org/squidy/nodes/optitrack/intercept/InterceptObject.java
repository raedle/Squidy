package org.squidy.nodes.optitrack.intercept;

import java.awt.Point;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.data.impl.DataString;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.Tracking;
import org.squidy.nodes.optitrack.Optitrack;
import org.squidy.nodes.optitrack.utils.IntersectionUtils;
import org.squidy.nodes.optitrack.utils.TrackingConstant;
import org.squidy.nodes.optitrack.utils.TrackingUtility;

import com.jhlabs.image.RaysFilter;


public class InterceptObject implements Comparable {
	
	public String objectName;
	private Point3d topLeft;
	private Point3d topRight;
	private Point3d bottomLeft;
	private Point3d bottomRight;
	public Point3d screenCenter;
	public Vector3d displayNorm;
	private Vector3d blbr;
	private Vector3d bltl;
	private boolean isMobileDisplay;
	public double screenOverSize;
	private double distanceToCenter;
	public String host;
	public int port;
	private boolean backFaceTracking;
	private MathUtility mu = new MathUtility();
	private IntersectionUtils interseptor = new IntersectionUtils();
	public int displayWidth;
	public int displayHeight;
	
	/**
	 * 
	 * @param d6d
	 */
	public InterceptObject(DataPosition6D d6d)
	{
		objectName = d6d.getAttribute(DataConstant.IDENTIFIER).toString();
		isMobileDisplay = true;
		screenOverSize = TrackingUtility.getAttributesDouble(d6d, TrackingConstant.SCREENOVERSIZE);
		host = TrackingUtility.getAttributesAlpha(d6d, TrackingConstant.REMOTEHOST);
		port = TrackingUtility.getAttributesInteger(d6d, TrackingConstant.REMOTEPORT);
		backFaceTracking = TrackingUtility.getAttributesBoolean(d6d, TrackingConstant.BACKFACETRACKING);
		bottomLeft = new Point3d();
		bottomRight = new Point3d();
		topLeft = new Point3d();
		updateMobileDisplay(d6d);
	}
	
	/**
	 * 
	 * @param dString
	 */
	public InterceptObject(DataString dString)
	{
		objectName = dString.getAttribute(DataConstant.IDENTIFIER).toString();
		isMobileDisplay = false;
		screenOverSize = Double.valueOf(dString.getAttribute(TrackingConstant.SCREENOVERSIZE).toString());
		host = dString.getAttribute(TrackingConstant.REMOTEHOST).toString();
		port = Integer.valueOf(dString.getAttribute(TrackingConstant.REMOTEPORT).toString());
		backFaceTracking = Boolean.valueOf(dString.getAttribute(TrackingConstant.BACKFACETRACKING).toString());
		
		String[] corners = dString.data.split(";");
		String[] chunks = corners[0].split(",");
        topLeft = new Point3d(Double.parseDouble(chunks[0]),
			      Double.parseDouble(chunks[1]),
			      Double.parseDouble(chunks[2]));
        chunks = corners[1].split(",");
		bottomLeft = new Point3d(Double.parseDouble(chunks[0]),
					      Double.parseDouble(chunks[1]),
					      Double.parseDouble(chunks[2]));
		chunks = corners[2].split(",");
		bottomRight = new Point3d(Double.parseDouble(chunks[0]),
					      Double.parseDouble(chunks[1]),
					      Double.parseDouble(chunks[2]));
		
		Point3d offset = new Point3d(4000,0,3000);
		bottomRight.sub(offset);
		bottomLeft.sub(offset);
		topLeft.sub(offset);
		createNorm();
	}
	
	/**
	 * 
	 * @param d6d
	 */
	public void updateMobileDisplay(DataPosition6D d6d)
	{
		DataPosition6D lastObjectPosition = d6d.getClone();
		if(lastObjectPosition.hasAttribute(DataConstant.MAX_X))
		{
			d6d = TrackingUtility.Norm2RoomCoordinates(Optitrack.class, lastObjectPosition);	
		}else
		{
			d6d = lastObjectPosition;
		}
		bottomLeft.x = d6d.getX();
		bottomLeft.y = d6d.getY();
		bottomLeft.z = d6d.getZ();
				
		double[] origin = new double[3];
		origin[0] = 0;
		origin[1] = 0;
		origin[2] = 0;
		
		double[][] m6d = new double[3][3];
		m6d[0][0] = d6d.getM00();
		m6d[0][1] = d6d.getM01();
		m6d[0][2] = d6d.getM02();
		
		m6d[1][0] = d6d.getM10();
		m6d[1][1] = d6d.getM11();
		m6d[1][2] = d6d.getM12();
		
		m6d[2][0] = d6d.getM20();
		m6d[2][1] = d6d.getM21();
		m6d[2][2] = d6d.getM22();
		
		try{
			double[] rotatingPoint1 = new double[3];
			rotatingPoint1[0] = Double.valueOf(lastObjectPosition.getAttribute(TrackingConstant.OBJECTWIDHT).toString());
			rotatingPoint1[1] = 0.0;
			rotatingPoint1[2] = 0.0;
		
			mu.rotatePoint(rotatingPoint1, origin, m6d, true);
			
			bottomRight.x = d6d.getX() + rotatingPoint1[0];
			bottomRight.y = d6d.getY() + rotatingPoint1[1];
			bottomRight.z = d6d.getZ() + rotatingPoint1[2];
		}catch (Exception ex)
		{
			System.err.println("INTERCEPTION POINT " + ex.toString());
		}
		try
		{
			double[] rotatingPoint2 = new double[3];
			rotatingPoint2[0] = 0;
			rotatingPoint2[1] = 0;
			rotatingPoint2[2] = Double.valueOf(lastObjectPosition.getAttribute(TrackingConstant.OBJECTHEIGHT).toString());
			
			mu.rotatePoint(rotatingPoint2, origin, m6d, true);			
			
			topLeft.x = d6d.getX() + rotatingPoint2[0];
			topLeft.y = d6d.getY() - rotatingPoint2[1];
			topLeft.z = d6d.getZ() + rotatingPoint2[2];
		} catch(Exception ex)
		{
			System.err.println("INTERCEPTION POINT " + ex.toString());
		}
		createNorm();
	}
	/**
	 * 
	 * @return
	 */
	public double getCenterDistance()
	{
		return this.distanceToCenter;
	}
	/**
	 * 
	 * @param origin
	 * @param direction
	 * @return
	 */
	public Point3d getIntersectionPoint3d(PointingDevice pDevice)
	{
		Vector3d direction = pDevice.getOrientation();
		return getIntersectionPoint3d(pDevice,direction,true);
	}
	public Point3d getIntersectionPoint3d(PointingDevice pDevice, Vector3d direction, boolean dp)
	{
		Point3d p3d = new Point3d();
		Point3d origin = pDevice.getPosition();
		
		boolean fForward = false;
		boolean iPointFound = interseptor.rayPolygon(origin, direction, 0, topLeft, bottomLeft, bottomRight, p3d, fForward);
		Vector3d tmp = new Vector3d(screenCenter);
		tmp.sub(p3d);		
		if (iPointFound)
		{
//			if (!this.backFaceTracking && !fForward)
//			{
//				return null;
//			}
			if (dp)
			{
				pDevice.setDPCenterDistance(tmp.length());
				pDevice.setDPIntersection(p3d);
				pDevice.setDPInterceptObject(this);
			}
			else
			{
//				pDevice.setRPCenterDistance(tmp.length());
//				pDevice.setRPIntersection(p3d);
//				pDevice.setRPInterceptObject(this);
			}			
			return p3d;
		} else
		{
			Intersection is = new Intersection();
			is.setCenterDistance(tmp.length());
			is.setIntercepObject(this);
			is.setIntersection(p3d);
			is.setIsOffscreen(true);
			is.setIntersection(getOversizeIntersectionPoint2d(pDevice, true));
			pDevice.addOffscreenIntersection(is);
			return null;
		}
	}
	/**
	 * 
	 * @param d6d
	 * @return
	 */
	public Point2d getIntersectionPoint2d(PointingDevice pDevice)
	{
		Point3d p3d = new Point3d();
		p3d = getIntersectionPoint3d(pDevice);
		if (p3d == null)
			return null;
		else
		{
			Point2d p2d =  normalize2d(p3d);
			pDevice.setDPIntersection(p2d);
			return p2d;
		}	
	}
	public Point2d getIntersectionPoint2d(PointingDevice pDevice, Vector3d v3d, boolean dp)
	{
		Point3d p3d = new Point3d();
		p3d = getIntersectionPoint3d(pDevice, v3d, dp);
		if (p3d == null)
			return null;
		else
		{
			Point2d p2d =  normalize2d(p3d);
			if (dp)
				pDevice.setDPIntersection(p2d);
			else
				pDevice.setRPIntersection(p2d);
			return p2d;
		}	
	}
	public Point2d getSingleIntersectionPoint2d(PointingDevice pDevice, Vector3d v3d)
	{
		Point3d p3d = new Point3d();
		p3d = getOversizeIntersectionPoint3d(pDevice, v3d);
		if (p3d == null)
			return null;
		else
		{
			Point2d p2d =  normalize2d(p3d);
			return p2d;
		}	
	}
	/**
	 * 
	 * @param origin
	 * @param direction
	 * @return
	 */
	public Point3d getOversizeIntersectionPoint3d(PointingDevice pDevice, Vector3d v3d)
	{
		Point3d p3d = new Point3d();
		Point3d origin = pDevice.getPosition();
		Vector3d direction = pDevice.getOrientation();
		boolean fForward = false;
		if (!interseptor.rayPolygon(origin, direction, 0, topLeft, bottomLeft, bottomRight, p3d, fForward))
		{
//			if (!this.backFaceTracking && !fForward)
//				return null;
//			else
				return p3d;
		}
		else
		{
			return null;
		}
	}
	public Point3d getOversizeIntersectionPoint3dSingle(Point3d position, Vector3d direction)
	{
		Point3d p3d = new Point3d();
		boolean fForward = false;
		interseptor.rayPolygon(position, direction, 0, topLeft, bottomLeft, bottomRight, p3d, fForward);
		return p3d;
	}	
	public Point2d getOversizeIntersectionPoint2dSingle(Point3d position, Vector3d direction)
	{
		return normalize2d(getOversizeIntersectionPoint3dSingle(position,direction));
	}
	public Point3d getOversizeIntersectionPoint3d(PointingDevice pDevice)
	{
		Vector3d direction = pDevice.getOrientation();
		return getOversizeIntersectionPoint3d(pDevice,direction);
	}
		
	/**
	 * 
	 * @param origin
	 * @param direction
	 * @return
	 */
	public Point2d getOversizeIntersectionPoint2d(PointingDevice pDevice, boolean sendAlways)
	{
		Point3d p3d = new Point3d();
		p3d = getOversizeIntersectionPoint3d(pDevice);
		if (p3d == null && sendAlways)
			p3d = getIntersectionPoint3d(pDevice);
		else if (p3d == null && !sendAlways)
			return null;
		Point2d p2d = normalize2d(p3d);
		if (p2d.x < -screenOverSize)
		{
			if (sendAlways)
				p2d.x = -screenOverSize;
			else 
				return null;
		}
		if (p2d.x >  1 + screenOverSize)
		{
			if (sendAlways)
				p2d.x = (1 + screenOverSize);
			else 
				return null;
		}
		if (p2d.y < -screenOverSize)
		{
			if (sendAlways)
				p2d.y = -screenOverSize;
			else 
				return null;
		}
		if (p2d.y >  1 + screenOverSize)
		{
			if (sendAlways)
				p2d.y = (1 + screenOverSize);
			else 
				return null;
		}
		p2d.x = TrackingUtility.minmax(p2d.x, 1 + screenOverSize, -screenOverSize, 1, 0);
		p2d.y = TrackingUtility.minmax(p2d.y, 1 + screenOverSize, -screenOverSize, 1, 0);
		//pDevice.setOffScreenIntersection(p2d);		
		return p2d;
	}
	/**
	 * 
	 * @return
	 */
	public double getObjectWidth()
	{
		return mu.euclidDist(bottomLeft, bottomRight);
	}
	/**
	 * 
	 * @return
	 */
	public double getObjectHeight()
	{
		return mu.euclidDist(bottomLeft, topLeft);
	}
	/**
	 * 
	 */
	private void createNorm()
	{
		bltl = new Vector3d(topLeft);
		blbr = new Vector3d(bottomRight);
		bltl.sub(bottomLeft);
		blbr.sub(bottomLeft);
		displayWidth = (int)Math.floor(blbr.length());
		displayHeight = (int)Math.floor(bltl.length());
		displayNorm = new Vector3d();
		displayNorm.cross(bltl,blbr);
		topRight = new Point3d(bottomRight.x + (topLeft.x - bottomLeft.x),
				bottomRight.y + (topLeft.y - bottomLeft.y),
				bottomRight.z + (topLeft.z - bottomLeft.z));
		Vector3d bltr = new Vector3d(topRight);
		bltr.sub(bottomLeft);
		screenCenter = (Point3d)bottomLeft.clone();
		bltr.scale(0.5);
		screenCenter.add(bltr);
	}
	public Point2d normalizeDistance(Point2d p2d)
	{
		p2d.x /= this.getObjectHeight();
		p2d.y /= this.getObjectWidth();
		return p2d;
	}
	/**
	 * 
	 * @param p3d
	 * @return
	 */
	private Point2d normalize2d(Point3d p3d)
	{
		Vector3d AP = new Vector3d(p3d.x - bottomLeft.x,p3d.y - bottomLeft.y,p3d.z - bottomLeft.z);
		Point2d intersection2d = new Point2d();
		double alpha = AP.angle(blbr);
		double beta = AP.angle(bltl);
		intersection2d.y = (AP.length() * Math.sin(alpha)) / getObjectHeight();
		intersection2d.x = (AP.length() * Math.sin(beta)) / getObjectWidth();
		if (Math.toDegrees(alpha) > 90)
		{
			intersection2d.x *= -1.0;
		}
		if (Math.toDegrees(beta) > 90)
		{
			intersection2d.y *= -1.0;
		}
		return intersection2d;
	}

	
	public int compareTo(Object o) {
		return (objectName.compareToIgnoreCase(o.toString()));
	}
}
