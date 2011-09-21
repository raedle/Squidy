package org.squidy.nodes.optitrack.intercept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.Tracking;
import org.squidy.nodes.optitrack.utils.TrackingConstant;
import org.squidy.nodes.optitrack.utils.TrackingUtility;


public class PointingDevice implements Comparable, Cloneable {
	
	public int rigidBodyID;
	private int pointingMode;
	private int rigidBodyRole;
	private int currentGesture;
	private int gestureOld;
	private int currentButton;
	public boolean hasMouseButtons;
	public int tuioID;
	public int handSide;
	public int groupID;
	private Point3d position;
	private Vector3d orientation;
	private Point3d positionOld;
	private Vector3d orientationOld;
	private Point3d oldRotator;
	private Intersection dpIntersection;
	private Intersection rpIntersection;
	private Intersection hpIntersection;
	private ArrayList<Intersection> offScreenIntersections;
	private Intersection publishedIntersection;
	private PointingModes currentPointingMode;
	public double translationSpeed;
	public double rotationSpeed;
	public double adapedRotationSpeed;
	private double interactionSpeed;
	private long lastUpdate;
	private MathUtility mu;
	private Vector3d zVec;
	private Point3d origin;
	private HashMap<Integer,Integer> gestureMap;
	private StatisticQueue speedQueue;
	private StatisticQueue rotationSpeedQueue;

	
	public PointingDevice(DataPosition6D d6d)
	{
		rigidBodyID = TrackingUtility.getAttributesInteger(d6d, TrackingConstant.RIGIDBODYID);
		pointingMode = TrackingUtility.getAttributesInteger(d6d, TrackingConstant.POINTINGMODE);
		rigidBodyRole = TrackingUtility.getAttributesInteger(d6d, TrackingConstant.RIGIDBODYROLE);
		currentGesture = TrackingUtility.getAttributesInteger(d6d, TrackingConstant.GESTUREID);
		tuioID = TrackingUtility.getAttributesInteger(d6d, TrackingConstant.TUIOID);
		handSide = TrackingUtility.getAttributesInteger(d6d, TrackingConstant.HANDSIDE);
		groupID = TrackingUtility.getAttributesInteger(d6d, DataConstant.GROUP_ID);
		position = TrackingUtility.getPoint3d(d6d);
		orientation = TrackingUtility.getTargetDirection(d6d);
		hasMouseButtons = TrackingUtility.getAttributesBoolean(d6d, TrackingConstant.HASBUTTONS);
		this.dpIntersection = new Intersection();
		this.rpIntersection = new Intersection();
		this.hpIntersection = new Intersection();
		this.offScreenIntersections = new ArrayList<Intersection>();
		this.lastUpdate = System.currentTimeMillis();
		this.mu = new MathUtility();
		oldRotator = new Point3d();
		zVec = new Vector3d(0,0,100);
		origin = new Point3d(0,0,0);
		oldRotator = mu.rotatePoint(zVec, origin, d6d, false);
		tuioID = 0;
		gestureMap = new HashMap<Integer,Integer>();
		speedQueue = new StatisticQueue(15);
		rotationSpeedQueue = new StatisticQueue(15);
	}
	
	public boolean updateDevice(DataPosition6D d6d)
	{
		positionOld = position;
		orientationOld = orientation;
		gestureOld = currentGesture;

		position = TrackingUtility.getPoint3d(d6d);
		double dist = position.distance(positionOld);
		if (dist > 0)
		{
			pointingMode = TrackingUtility.getAttributesInteger(d6d, TrackingConstant.POINTINGMODE);
			rigidBodyRole = TrackingUtility.getAttributesInteger(d6d, TrackingConstant.RIGIDBODYROLE);
			currentGesture = calculateGestures(TrackingUtility.getAttributesInteger(d6d, TrackingConstant.GESTUREID));
			if (currentGesture == TrackingConstant.GESTURE_CLICK && getGestureChanged())
			{
				tuioID++;
			}
//			System.out.println(currentGesture);
			orientation = TrackingUtility.getTargetDirection(d6d);
			groupID = TrackingUtility.getAttributesInteger(d6d, DataConstant.GROUP_ID);
			this.dpIntersection = new Intersection();
			this.rpIntersection = new Intersection();
			this.hpIntersection = new Intersection();
			if (offScreenIntersections == null)
				this.offScreenIntersections = new ArrayList<Intersection>();
			else
				this.offScreenIntersections.clear();
			// calculate interaction speed
			long timeStamp = (System.currentTimeMillis() - lastUpdate);
			
			if (dist == 0)
				this.translationSpeed = 0;
			else
				this.translationSpeed =	dist / timeStamp;
			Point3d newRotator = new Point3d();
			newRotator = mu.rotatePoint(zVec, origin, d6d, false);
			if (timeStamp > 0)
			{
				rotationSpeedQueue.add(newRotator.distance(oldRotator) / timeStamp);
				this.rotationSpeed = Math.floor(rotationSpeedQueue.getAverage(0, 0.2) *1000)/10;
			}
//			System.out.println( " " + rotationSpeed * 1000 + "\t"+translationSpeed * 1000 + "  ");
			Point3d rotnTransOld = (Point3d)positionOld.clone();
			rotnTransOld.add(oldRotator);
			Point3d rotnTransNew = (Point3d)position.clone();
			rotnTransNew.add(newRotator);
			double speed = rotnTransNew.distance(rotnTransOld)/timeStamp;
			
			if (speed < 1000)
			{
				speedQueue.add(speed);
				this.interactionSpeed = Math.floor(speedQueue.getAverage(0,0.2) *100);
				this.adapedRotationSpeed = this.interactionSpeed *0.1 + this.rotationSpeed * 0.8;
			}
				 
			//System.out.println(this.interactionSpeed * 1000);
//			System.out.println((Math.floor(this.interactionSpeed *100)));
			this.lastUpdate = System.currentTimeMillis();
			oldRotator = (Point3d)newRotator.clone();
			return true;
		}
		else
		{
			return false;
		}

	}
	
	public double getInteractionSpeed()
	{
		return this.interactionSpeed;
	}
	
	public void setOrientation(PointingDevice pd)
	{
		orientation = pd.getOrientation();
	}
	
//	public InterceptObject getInterceptObject()
//	{
//		return iObject;
//	}
//	public void setInterceptObject(InterceptObject iObject)
//	{
//		this.iObject = iObject;
//	}
	public Point3d getPosition()
	{
		return this.position;
	}
	public Point3d getPositionOld()
	{
		return this.positionOld;
	}
	public Vector3d getOrientation()
	{
		return this.orientation;
	}
	public int getPointingMode()
	{
		return this.pointingMode;
	}
	public void setPointingMode(int mode)
	{
		this.pointingMode = mode;
	}
	/*
	 * DirectPointing
	 */
	public void setDPIntersection(Intersection is)
	{
		this.dpIntersection = is;
	}
	public void setDPIntersection(Point3d p)
	{
		this.dpIntersection.setIntersection(p);
		this.dpIntersection.setIsOffscreen(false);
	}
	public void setDPIntersection(Point2d p)
	{
		this.dpIntersection.setIntersection(p);
		this.dpIntersection.setIsOffscreen(false);
	}	
	public void setDPCenterDistance(double d)
	{
		this.dpIntersection.setCenterDistance(d);
	}
	public void setDPInterceptObject(InterceptObject o)
	{
		this.dpIntersection.setIntercepObject(o);
	}
	public Intersection getDPIntersection()
	{
		return this.dpIntersection;
	}
	/*
	 * Offscreen Pointing
	 */
	
	public void addOffscreenIntersection(Intersection i)
	{
		this.offScreenIntersections.add(i);
	}

	public ArrayList<Intersection> getAllOffScreenIntersections()
	{
		return this.offScreenIntersections;
	}
	
	public Intersection getOffScreenIntersection(InterceptObject iObject)
	{
		for (Intersection i : this.offScreenIntersections)
		{
			if (i.getInterceptObject().host == iObject.host)
			{
				return i;
			}
		}
		return null;
	}
		
	
//	public void setOffScreenIntersection(Point3d p)
//	{
//		
//		this.offScreenIntersections.setIntersection(p);
//	}
//	public void setOffScreenIntersection(Point2d p)
//	{
//		this.offScreenIntersection.setIntersection(p);
//	}	
//	public void setOffScreenCenterDistance(double d)
//	{
//		this.offScreenIntersection.setCenterDistance(d);
//	}
//	public void setOffScreenInterceptObject(InterceptObject o)
//	{
//		this.offScreenIntersection.setIntercepObject(o);
//	}
//	public Intersection getOffScreenIntersection()
//	{
//		return this.offScreenIntersection;
//	}
	
	/*
	 * Relative Pointing
	 */
	public void setRPIntersection(Intersection is)
	{
		this.rpIntersection = is;
	}
	public void setRPIntersection(Point3d p)
	{
		this.rpIntersection.setIntersection(p);
	}
	public void setRPIntersection(Point2d p)
	{
		this.rpIntersection.setIntersection(p);
	}	
	public void setRPCenterDistance(double d)
	{
		this.rpIntersection.setCenterDistance(d);
	}
	public void setRPInterceptObject(InterceptObject o)
	{
		this.rpIntersection.setIntercepObject(o);
	}
	public Intersection getRPIntersection()
	{
		return this.rpIntersection;
	}	
	/*
	 * Published Intersection Point
	 */
	public void setPublishedIntersection(Intersection is)
	{
		this.publishedIntersection = is;
	}	
	public Intersection getPublishedIntersection()
	{
		return this.publishedIntersection;
	}
	
	public int getGesture()
	{
		return this.currentGesture;
	}
	public void setGesture(int gesture)
	{
		this.currentGesture = gesture;
	}
	public int calculateGestures(int incomming)
	{
		int counter;
		try
		{
		counter = gestureMap.get(incomming);
		}
		catch (Exception ex)
		{
			counter = 1;
		}
		
		if (counter > 10)
		{
			gestureMap.put(incomming, --counter);
			return incomming;
		}
		else
		{
			gestureMap.put(incomming, ++counter);
			return currentGesture;
		}
	}
	public boolean getGestureChanged()
	{
		if (gestureOld == currentGesture)
			return false;
		else
			return true;
	}
	
	public PointingModes getCurrentPointingMode()
	{
		return this.currentPointingMode;
	}
	public void setCurrentPointingMode(PointingModes pm)
	{
		this.currentPointingMode = pm;
	}


	
	public int compareTo(Object o) {
		if (Integer.valueOf(o.toString()) == rigidBodyID)
			return 0;
		else if(Integer.valueOf(o.toString()) > rigidBodyID)
			return 1;
		else 
			return -1;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
	    return super.clone();
	 }
}
