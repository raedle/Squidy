package org.squidy.nodes.optitrack;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.swing.KeyStroke;
import javax.vecmath.*;

import org.apache.poi.hwpf.usermodel.DateAndTime;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.data.impl.DataString;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.Keyboard;
import org.squidy.nodes.MouseIO;
import org.squidy.nodes.Tracking;
import org.squidy.nodes.optitrack.RigidBody.RB_MODEDomainProvider;
import org.squidy.nodes.optitrack.intercept.FilterQueue;
import org.squidy.nodes.optitrack.intercept.InterceptObject;
import org.squidy.nodes.optitrack.intercept.Intersection;
import org.squidy.nodes.optitrack.intercept.PointingDevice;
import org.squidy.nodes.optitrack.utils.TrackingConstant;
import org.squidy.nodes.optitrack.utils.TrackingUtility;

import com.sun.opengl.util.Screenshot;


/*<code>MultiIntersection</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AM
* </pre>
* 
* @author Simon Faeh, < href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de</>, University f Konstanz
* 
* @version $Id$
*/
@XmlType(name = "MultiIntersection")
@Processor(
	name = "MultiIntersection",
	icon = "/org/squidy/nodes/image/48x48/intercept.png",
	description = "",
	types = {Processor.Type.OUTPUT, Processor.Type.INPUT },
	tags = { "optitrack", "handtracking", "intersection" }
)

public class MultiIntersection extends AbstractNode {
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "rbMode")
	@Property(
		name = "Force Pointingmode",
		description = "Forces the use of the same Pointingmode for any device"
	)
	@ComboBox(domainProvider = RB_MODEDomainProvider.class)
	private int rbMode = TrackingConstant.RBMODE_NONE;
	
	public final int getRbMode() {
		return rbMode;
	}

	public final void setRbMode(int rbMode) {
		
		this.rbMode = rbMode;
		this.maxDirectPointRatio = (double)maxDPR / 100.0;
		this.minDirectPointRatio = (double)minDPR / 100.0;
		this.increasingSpeedThresold = (double)incSpeedThres / 100.0;
		pointingDevices = new ArrayList<PointingDevice>();
		calcPoints = new FilterQueue(50);
	}
	// ################################################################################	
	
	@XmlAttribute(name = "minHybridSpeedThreshold")
	@Property(
		name = "Min Acceleration Threshold (Weighting)",
		description = "Min Acceleration Threshold (Weighting)",
		group ="Hybrid",
		suffix = ""
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 40,
		showLabels = true,
		showTicks = true,
		majorTicks = 10,
		minorTicks = 1,
		snapToTicks = true
	)
	private int minHybridSpeedThreshold = 5;

	public int getMinHybridSpeedThreshold() {
		return minHybridSpeedThreshold;
	}

	public void setMinHybridSpeedThreshold(int minHybridSpeedThreshold) {
		this.minHybridSpeedThreshold = minHybridSpeedThreshold;
	}
	
	// ################################################################################	
	
	@XmlAttribute(name = "maxHybridSpeedThreshold")
	@Property(
		name = "Max Acceleration Threshold (Weighting)",
		description = "Max Acceleration Threshold (Weighting)",
		group ="Hybrid",
		suffix = ""
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 40,
		showLabels = true,
		showTicks = true,
		majorTicks = 10,
		minorTicks = 1,
		snapToTicks = true
	)
	private int maxHybridSpeedThreshold = 11;

	public int getMaxHybridSpeedThreshold() {
		return maxHybridSpeedThreshold;
	}

	public void setMaxHybridSpeedThreshold(int maxHybridSpeedThreshold) {
		this.maxHybridSpeedThreshold = maxHybridSpeedThreshold;
	}

	// ################################################################################	
	
	@XmlAttribute(name = "maxDirectPointRatio")
	@Property(
		name = "Maximum DirectPointing Ratio",
		description = "Maximum ratio the directPionting value can get",
		group ="Hybrid",
		suffix = "%"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 50,
		maximumValue = 150,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 1,
		snapToTicks = true
	)
	private int maxDPR = 80;
	private double maxDirectPointRatio;

	public int getMaxDPR() {
		return maxDPR;
	}

	public void setMaxDPR(int maxDirectPointRatio) {
		this.maxDPR = maxDirectPointRatio;
		this.maxDirectPointRatio = this.maxDPR / 100.0;
	}
	
	// ################################################################################	
	
	@XmlAttribute(name = "minDirectPointRatio")
	@Property(
		name = "Minimum DirectPointing Ratio",
		description = "Minimum ratio the directPionting value can get",
		group ="Hybrid",
		suffix = "%"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 30,
		showLabels = true,
		showTicks = true,
		majorTicks = 15,
		minorTicks = 1,
		snapToTicks = true
	)
	private int minDPR = 0;
	private double minDirectPointRatio = 0;
	

	public int getMinDPR() {
		return minDPR;
	}

	public void setMinDPR(int minDirectPointRatio) {
		this.minDirectPointRatio = minDirectPointRatio / 100.0;
		this.minDPR = minDirectPointRatio;
	}
	// ################################################################################	

	
	@XmlAttribute(name = "minHybAccelThreshold")
	@Property(
		name = "Min Speed Threshold (Relative Part)",
		description = "Min Speed Threshold (Relative Part)",
		group ="Hybrid",
		suffix = ""
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 60,
		showLabels = true,
		showTicks = true,
		majorTicks = 20,
		minorTicks = 1,
		snapToTicks = true
	)
	private int minHybAccelThreshold = 5;

	public int getMinHybAccelThreshold() {
		return minHybAccelThreshold;
	}

	public void setMinHybAccelThreshold(int minHybAccelThreshold) {
		this.minHybAccelThreshold = minHybAccelThreshold;
	}

	// ################################################################################	
	
	
	@XmlAttribute(name = "maxHybAccelThreshold")
	@Property(
		name = "Max Speed Threshold (Relative Part)",
		description = "Max Speed Threshold (Relative Part)",
		group ="Hybrid",
		suffix = ""
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 100,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 1,
		snapToTicks = true
	)
	private int maxHybAccelThreshold = 30;

	public int getMaxHybAccelThreshold() {
		return maxHybAccelThreshold;
	}

	public void setMaxHybAccelThreshold(int maxHybAccelThreshold) {
		this.maxHybAccelThreshold = maxHybAccelThreshold;
	}

	// ################################################################################	
	
	
	@XmlAttribute(name = "increasingSpeedThresold")
	@Property(
		name = "Threshold for prefetching direct pointnig",
		description = "Threshold for prefetching direct pointnig",
		group ="Hybrid",
		suffix = "%"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 50,
		maximumValue = 150,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 1,
		snapToTicks = true
	)
	private int incSpeedThres = 75;
	private double increasingSpeedThresold;

	public int getIncSpeedThres() {
		return incSpeedThres;
	}

	public void setIncSpeedThres(int increasingSpeedThresold) {
		this.increasingSpeedThresold = increasingSpeedThresold / 100.0;
		this.incSpeedThres = increasingSpeedThresold;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "minRelAccelThreshold")
	@Property(
		name = "Min Relative Acceleration Threshold",
		description = "Min Relative Acceleration Threshold",
		group ="Relative",
		suffix = ""
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 60,
		showLabels = true,
		showTicks = true,
		majorTicks = 20,
		minorTicks = 1,
		snapToTicks = true
	)
	private int minRelAccelThreshold = 15;

	public int getMinRelAccelThreshold() {
		return minRelAccelThreshold;
	}

	public void setMinRelAccelThreshold(int minRelAccelThreshold) {
		this.minRelAccelThreshold = minRelAccelThreshold;
	}

	// ################################################################################	
	
	@XmlAttribute(name = "maxRelAccelThreshold")
	@Property(
		name = "Max Relative Acceleration Threshold",
		description = "Max Relative Acceleration Threshold",
		group ="Relative",
		suffix = ""
	)
	@Slider(
		type = Integer.class,
		minimumValue = 40,
		maximumValue = 200,
		showLabels = true,
		showTicks = true,
		majorTicks = 20,
		minorTicks = 1,
		snapToTicks = true
	)
	private int maxRelAccelThreshold = 120;

	public int getMaxRelAccelThreshold() {
		return maxRelAccelThreshold;
	}

	public void setMaxRelAccelThreshold(int maxRelAccelThreshold) {
		this.maxRelAccelThreshold = maxRelAccelThreshold;
	}

	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	

	// ################################################################################
	// BEGIN OF DOMAIN PROVIDERS
	// ################################################################################
	
	public static class RB_MODEDomainProvider implements DomainProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[4];
			values[0] = new ComboBoxItemWrapper(TrackingConstant.RBMODE_NONE, "don't force");
			values[1] = new ComboBoxItemWrapper(TrackingConstant.RBMODE_DIRECTPOINTING, "Direct Pointing");
			values[2] = new ComboBoxItemWrapper(TrackingConstant.RBMODE_RELATIVEPIONTING, "Relative Pointing");
			values[3] = new ComboBoxItemWrapper(TrackingConstant.RBMODE_HYBRIDPOINTING, "Hyprid Pointing");
			return values;
		}
	}
	
	// ################################################################################
	// END OF DOMAIN PROVIDERS
	// ################################################################################
	
	
	private ArrayList<InterceptObject> interceptObjects;
	private ArrayList<PointingDevice> pointingDevices;
	private DataPosition6D trackedPerson;
	private PointingDevice relDevice;
	private InterceptObject humanVScreen;
	private DataButton lastButtonReceived;
	private double relativeWeight = 0;
	private FilterQueue calcPoints;
	
	@Override
	public void onStart()
	{
		this.maxDirectPointRatio = (double)maxDPR / 100.0;
		this.minDirectPointRatio = (double)minDPR / 100.0;
		this.increasingSpeedThresold = (double)incSpeedThres / 100.0;
		interceptObjects = new ArrayList();
		pointingDevices = new ArrayList<PointingDevice>();
		calcPoints = new FilterQueue(50);
	}
    	
	@Override 
	public IDataContainer preProcess(IDataContainer dataContainer)
	{
		return dataContainer;
	}

	private boolean sendDummyClick = false;
	/**
	 * 
	 * @param dataPosition6d
	 * @return
	 */
	public IData process (DataPosition6D dataPosition6d)
	{
		if (dataPosition6d.hasAttribute(TrackingConstant.RIGIDBODYROLE) == true &&
				dataPosition6d.hasAttribute(DataConstant.IDENTIFIER) == true)
		{
			dataPosition6d = TrackingUtility.Norm2RoomCoordinates(Optitrack.class, dataPosition6d);
			/*
			 * Mobile Display
			 */
			if (Integer.valueOf(dataPosition6d.getAttribute(TrackingConstant.RIGIDBODYROLE).toString()) == TrackingConstant.RBROLE_MOBILEDISPLAY)
			{
				InterceptObject iObject = getInterceptObject(dataPosition6d.getAttribute(DataConstant.IDENTIFIER).toString()); 
				if (iObject == null)
				{
					iObject = new InterceptObject(dataPosition6d);
					interceptObjects.add(iObject);
					Collections.sort(interceptObjects);
				}
				else
				{
					iObject.updateMobileDisplay(dataPosition6d);
				}
			}
			/*
			 * Tracked Person
			 */
			else if (Integer.valueOf(dataPosition6d.getAttribute(TrackingConstant.RIGIDBODYROLE).toString()) == TrackingConstant.RBROLE_PERSON)
			{
				trackedPerson = dataPosition6d;
				dataPosition6d.setAttribute(TrackingConstant.SCREENOVERSIZE, 0.0);
				dataPosition6d.setAttribute(TrackingConstant.REMOTEHOST, "");
				dataPosition6d.setAttribute(TrackingConstant.REMOTEPORT, 0);
				dataPosition6d.setAttribute(TrackingConstant.BACKFACETRACKING, true);
				dataPosition6d.setAttribute(TrackingConstant.OBJECTWIDHT, 2000.0);
				dataPosition6d.setAttribute(TrackingConstant.OBJECTHEIGHT, 2000.0);
				if (humanVScreen == null)
				{
					humanVScreen = new InterceptObject(dataPosition6d);
				}
				else
				{
					humanVScreen.updateMobileDisplay(dataPosition6d);
				}
			}
			/*
			 * Pointing device
			 */
			else if (Integer.valueOf(dataPosition6d.getAttribute(TrackingConstant.RIGIDBODYROLE).toString()) == TrackingConstant.RBROLE_POINTINGDEVICE)
			{
				PointingDevice pDevice = null;
				if (pointingDevices.size() > 0)
					 pDevice = getDevice(TrackingUtility.getAttributesInteger(dataPosition6d, TrackingConstant.RIGIDBODYID));
				if (pDevice ==  null)
				{
					pDevice = new PointingDevice(dataPosition6d);
					pointingDevices.add(pDevice);
					Collections.sort(pointingDevices);
				} 
				else
				{
					if (!pDevice.updateDevice(dataPosition6d))
						return null;
				}
				InterceptObject currentIObject = null;
				Intersection currentIntersection = null;
				if (pDevice.getPublishedIntersection() != null)
				{
					currentIObject = pDevice.getPublishedIntersection().getInterceptObject();
					currentIntersection = pDevice.getPublishedIntersection();
				}
				if (this.rbMode != TrackingConstant.RBMODE_NONE)
					pDevice.setPointingMode(this.rbMode);
				switch (pDevice.getPointingMode())
				{
					case TrackingConstant.RBMODE_DIRECTPOINTING :
					{
						relativeWeight = 1.0;
						pDevice.setPublishedIntersection(directPointing(pDevice));
						break;
					}
					case TrackingConstant.RBMODE_RELATIVEPIONTING :
					{
						relativeWeight = 0.0;
						if (TrackingUtility.getGestureBivariate(pDevice.getGesture()) != TrackingConstant.GESTURE_CLUTCH)
						{
//							System.out.println(pDevice.getGesture() +"=="+ TrackingConstant.GESTURE_SINGLEPOINT);
							if (pDevice.getGesture() == TrackingConstant.GESTURE_SINGLEPOINT || pDevice.getGesture() == TrackingConstant.GESTURE_SINGLEPOINTCLICK)
							{
								if (pDevice.getGesture() == TrackingConstant.GESTURE_SINGLEPOINTCLICK)
								{
									pDevice.setGesture(TrackingConstant.GESTURE_CLICK);
								}
								pDevice.setPublishedIntersection(directPointing(pDevice));
							}
							else
							{
								if (pDevice.getPublishedIntersection() != null)
									pDevice.setPublishedIntersection(relativePointing(pDevice));
							}
						}
						break;
					}
					case TrackingConstant.RBMODE_HYBRIDPOINTING:
					{
						if (pDevice.getGesture() == TrackingConstant.GESTURE_SINGLEPOINT)
							pDevice.setPublishedIntersection(directPointing(pDevice));
						else
							pDevice.setPublishedIntersection(hybridPointing(pDevice));
						break;
					}
					default : 
					{
						pDevice.setPublishedIntersection(directPointing(pDevice));
					}
				}
				Intersection intersection = pDevice.getPublishedIntersection();
				if (intersection == null || intersection.getIntersectionPoint2d() == null)
				{
					
					for (Intersection is : pDevice.getAllOffScreenIntersections())
					{
						if (is.getIsOffscreen())
						{
							Point2d p2dOff = is.getIntersectionPoint2d();
							
							DataPosition2D d2d = new DataPosition2D(Optitrack.class,p2dOff.x,p2dOff.y);
							d2d.setAttribute(TrackingConstant.REMOTEHOST, is.getInterceptObject().host);
							d2d.setAttribute(TrackingConstant.REMOTEPORT, is.getInterceptObject().port);
							d2d.setAttribute(TrackingConstant.SCREENOVERSIZE, is.getInterceptObject().screenOverSize);
							d2d.setAttribute(TrackingConstant.RIGIDBODYID, pDevice.rigidBodyID);
							d2d.setAttribute(DataConstant.GROUP_ID, pDevice.groupID);
							d2d.setAttribute(TrackingConstant.GESTUREID, pDevice.getGesture());
							d2d.setAttribute(TrackingConstant.TUIOID, 0);
							if ((p2dOff.x > 0 && p2dOff.x < 1) || (p2dOff.y > 0 && p2dOff.y < 1))
							{
//								System.out.println(p2dOff);
								d2d.setAttribute(TrackingConstant.KEYWORD, "SCREENOVERSIZE");
							}
							publish(d2d);
						}
					}
//					System.out.println("NO INTERSECTION");
					if (currentIObject != null)
					{
						// sending mouse buttons on object leave
						if (pDevice.hasMouseButtons && this.lastButtonReceived != null)
						{
							lastButtonReceived.setAttribute(TrackingConstant.REMOTEHOST, currentIObject.host);
							lastButtonReceived.setAttribute(TrackingConstant.REMOTEPORT, currentIObject.port);
							publish(lastButtonReceived);
							lastButtonReceived = null;
						}
						// release tuio finger
//						Point2d p = new Point2d(currentIntersection.getIntersectionPoint2d());
//						System.out.println("last point " + p.toString());
//						DataPosition2D d2dLast = new DataPosition2D(Optitrack.class, p.x, p.y);
//						d2dLast.setAttribute(TrackingConstant.SENDTUIO, "LASTONSCREEN");
//						d2dLast.setAttribute(TrackingConstant.REMOTEHOST, currentIObject.host);
//						d2dLast.setAttribute(TrackingConstant.REMOTEPORT, currentIObject.port);
//						d2dLast.setAttribute(TrackingConstant.KEYWORD, "LASTONSCREEN");
//						d2dLast.setAttribute(DataConstant.GROUP_ID, pDevice.groupID);
//						d2dLast.setAttribute(TrackingConstant.RIGIDBODYID, pDevice.rigidBodyID);
//						d2dLast.setAttribute(TrackingConstant.GESTUREID, TrackingConstant.GESTURE_DEFAULT);
//						d2dLast.setAttribute(TrackingConstant.TUIOID, pDevice.tuioID);
//						publish(d2dLast);
//						return null;
						
						Point2d p = new Point2d(currentIntersection.getIntersectionPoint2d());
						DataPosition2D d2dOff = new DataPosition2D(Optitrack.class, p.x, p.y);
						d2dOff.setAttribute(TrackingConstant.SENDTUIO, "LAST");
						d2dOff.setAttribute(TrackingConstant.REMOTEHOST, currentIObject.host);
						d2dOff.setAttribute(TrackingConstant.REMOTEPORT, currentIObject.port);
						d2dOff.setAttribute(TrackingConstant.KEYWORD, "OFFSCREEN");
						d2dOff.setAttribute(DataConstant.GROUP_ID, pDevice.groupID);
						d2dOff.setAttribute(TrackingConstant.RIGIDBODYID, pDevice.rigidBodyID);
						d2dOff.setAttribute(TrackingConstant.GESTUREID, pDevice.getGesture());
						if (pDevice.getGesture() == TrackingConstant.GESTURE_CLICK)
						{

							boolean noOtherIntersections = true;
							for (PointingDevice pd : pointingDevices)
							{
								try{
									if (pd.getPublishedIntersection().getInterceptObject().host == currentIObject.host)
									{
										noOtherIntersections = false;
										break;
									}
								}catch (Exception ex)
								{}
							}
							if (noOtherIntersections)
								d2dOff.setAttribute(TrackingConstant.MERGEDIRECTLY, true);
							if (pDevice.tuioID > 0)
							{
//								System.out.println("LAST " +pDevice.tuioID + " " + currentIObject.host);
								d2dOff.setAttribute(TrackingConstant.TUIOID, pDevice.tuioID);
							}	
							
						}
						publish(d2dOff);
					}
					currentIObject = null;
					return null;
				}
				Point2d p2d = intersection.getIntersectionPoint2d();
				InterceptObject iObject = intersection.getInterceptObject();
				//object changed?
//				System.out.println(iObject.objectName + " " + currentIObject.objectName);
				if (currentIObject== null || iObject != currentIObject)
				{
					if (currentIObject != null)
					{

					}
					if (iObject != null)
					{
						// sending mouse buttons on object enter
						if (pDevice.hasMouseButtons && this.lastButtonReceived != null)
						{
							lastButtonReceived.setAttribute(TrackingConstant.REMOTEHOST, iObject.host);
							lastButtonReceived.setAttribute(TrackingConstant.REMOTEPORT, iObject.port);
							publish(lastButtonReceived);
							lastButtonReceived = null;	
						}
						
						
						if (pDevice.getGesture() == TrackingConstant.GESTURE_CLICK)
						{
							Point2d p = new Point2d(pDevice.getPublishedIntersection().getIntersectionPoint2d());
							DataPosition2D d2dOn = new DataPosition2D(Optitrack.class,p.x, p.y);
							d2dOn.setAttribute(TrackingConstant.SENDTUIO, "FIRST");
							d2dOn.setAttribute(TrackingConstant.REMOTEHOST, iObject.host);
							d2dOn.setAttribute(TrackingConstant.REMOTEPORT, iObject.port);
							d2dOn.setAttribute(TrackingConstant.KEYWORD, "ENTERSCREEN");
							d2dOn.setAttribute(DataConstant.GROUP_ID, pDevice.groupID);
							d2dOn.setAttribute(TrackingConstant.RIGIDBODYID, pDevice.rigidBodyID);
							d2dOn.setAttribute(TrackingConstant.GESTUREID, pDevice.getGesture());
							d2dOn.setAttribute(TrackingConstant.TUIOID, pDevice.tuioID);
							sendDummyClick = false;
							publish(d2dOn);
						}
//						System.out.println("object changed");
						//relDevice = new PointingDevice(trackedPerson);
						
//						Vector3d body2Center = new Vector3d(iObject.screenCenter.x - pDevice.getPosition().x,
//								iObject.screenCenter.y - pDevice.getPosition().y,
//								iObject.screenCenter.z - pDevice.getPosition().z);
//						Vector3d xVec = new Vector3d(1,0,0);
//						Vector3d yVec = new Vector3d(0,1,0);
//						Vector3d zVec = new Vector3d(0,0,1);
//						double angleX = Math.toDegrees(body2Center.angle(xVec));
//						double angleY = Math.toDegrees(body2Center.angle(yVec));
//						double angleZ = Math.toDegrees(body2Center.angle(zVec));
//						System.out.println("CENTER " + body2Center);
					}
					// click and gesture handling for enterscreen and leavesreen
				}
				Intersection dirIntersect = pDevice.getDPIntersection();
			
				// pointer compare
//				if (dirIntersect!= null)
//				{
//					Point2d p2dir = dirIntersect.getIntersectionPoint2d();
//					DataPosition2D d2d = new DataPosition2D(Optitrack.class,p2d.x,p2d.y);
//					d2d.setAttribute(TrackingConstant.REMOTEHOST, iObject.host);
//					d2d.setAttribute(TrackingConstant.REMOTEPORT, iObject.port);
//					d2d.setAttribute(TrackingConstant.KEYWORD, "OFFSCREEN");
//					d2d.setAttribute(DataConstant.GROUP_ID, pDevice.groupID);
//					publish(d2d);
//				}
				if (p2d != null)
				{
//					System.out.println("MI GESTURE  " + pDevice.getGesture());
//					System.out.println(p2d.toString());
//					calcPoints.add(p2d);
//					p2d = calcPoints.winsorize(0.25);
					DataPosition2D d2d = new DataPosition2D(Optitrack.class,p2d.x,p2d.y);
					d2d.setAttribute(TrackingConstant.REMOTEHOST, iObject.host);
					d2d.setAttribute(TrackingConstant.REMOTEPORT, iObject.port);
					d2d.setAttribute(TrackingConstant.DPRATIO, relativeWeight);
					d2d.setAttribute(TrackingConstant.KEYWORD, "ONSCREEN");
					d2d.setAttribute(DataConstant.GROUP_ID, pDevice.groupID);
					d2d.setAttribute(TrackingConstant.GESTUREID, pDevice.getGesture());
					if (pDevice.getGesture() == TrackingConstant.GESTURE_CLICK)
					{
						d2d.setAttribute(TrackingConstant.SENDTUIO, "TRUE");
						if (pDevice.tuioID > 0)
						{
							d2d.setAttribute(TrackingConstant.TUIOID, pDevice.tuioID);
//							System.out.println("TUIO " + pDevice.tuioID);
						}	
						if (p2d.x > 0.05 && p2d.x < 0.95 && p2d.y > 0.05 && p2d.y > 0.05 && p2d.y < 0.95 && sendDummyClick == false)
						{
							sendDummyClick = true;
							d2d.setAttribute(TrackingConstant.GESTUREID, TrackingConstant.GESTURE_DEFAULT);
							d2d.setAttribute(TrackingConstant.TUIOID, 0);
						}
						
						//d2d.setAttribute(TrackingConstant.HANDSIDE, dataPosition6d.getAttribute(TrackingConstant.HANDSIDE));
					}else
					{
						if (pDevice.getGestureChanged())
						{
//							d2d.setAttribute(TrackingConstant.SENDTUIO, "LAST");
							
//							System.out.println("TUIO " + 0);
						}	
						d2d.setAttribute(TrackingConstant.TUIOID, 0);
					}
					
					d2d.setAttribute(TrackingConstant.RIGIDBODYID, pDevice.rigidBodyID);

					publish(d2d);
					DataPosition3D d3d = new DataPosition3D(Optitrack.class,dataPosition6d.getX(),dataPosition6d.getY(),dataPosition6d.getZ());
					d3d.setAttribute(TrackingConstant.REMOTEHOST, iObject.host);
					d3d.setAttribute(TrackingConstant.REMOTEPORT, iObject.port);
					publish(d3d);
				}
				// publish offscreen intersections
				for (Intersection is : pDevice.getAllOffScreenIntersections())
				{
					if (is.getIsOffscreen())
					{
						Point2d p2dOff = is.getIntersectionPoint2d();
//						System.out.println("OFFSCREEM " + p2dOff.y);
						DataPosition2D d2d = new DataPosition2D(Optitrack.class,p2dOff.x,p2dOff.y);
						d2d.setAttribute(TrackingConstant.REMOTEHOST, is.getInterceptObject().host);
						d2d.setAttribute(TrackingConstant.REMOTEPORT, is.getInterceptObject().port);
						d2d.setAttribute(TrackingConstant.SCREENOVERSIZE, is.getInterceptObject().screenOverSize);
						d2d.setAttribute(TrackingConstant.RIGIDBODYID, pDevice.rigidBodyID);
						d2d.setAttribute(TrackingConstant.GESTUREID, TrackingConstant.GESTURE_DEFAULT);
						d2d.setAttribute(TrackingConstant.KEYWORD, "OFFSCREEN");
						d2d.setAttribute(DataConstant.GROUP_ID, pDevice.groupID);
						publish(d2d);
					}
				}
			}
			
		}
		return null;
	}
	
	/**
	 * 
	 * @param dataString creates a room object from string coordinates (see RoomObject.java)
	 * @return
	 */
	public IData process (DataString dataString)
	{
		if (dataString.hasAttribute(TrackingConstant.OBJECTHEIGHT) == true)
		{
			InterceptObject iObject = getInterceptObject(dataString.getAttribute(DataConstant.IDENTIFIER).toString()); 
			if (iObject == null)
			{
				iObject = new InterceptObject(dataString);
				interceptObjects.add(iObject);
			}else
			{
				interceptObjects.remove(iObject);
				iObject = new InterceptObject(dataString);
				interceptObjects.add(iObject);				
			}
		}
		return dataString;
	}
    /**
     * 
     * @param dataButton
     */
    public void process(DataButton dataButton)
    {
    	lastButtonReceived = dataButton;   	
    }
		
	/**
	 * 
	 * @param pDevice
	 * @return
	 */
	private Intersection relativePointing(PointingDevice pDevice)
	{
		Intersection currentIntersection = pDevice.getPublishedIntersection();
		double relSpeed = 0.0;
		if (pDevice.getPointingMode() == TrackingConstant.RBMODE_HYBRIDPOINTING)
		{
			double maxThreshold = this.maxHybAccelThreshold;//30;
			double minThreshold = this.minHybAccelThreshold;//5;
			double minMaxSpeed = Math.max(minThreshold, pDevice.getInteractionSpeed());
			minMaxSpeed = Math.min(maxThreshold, minMaxSpeed);
//			System.out.println(minMaxSpeed);
			
			relSpeed = TrackingUtility.minmax(minMaxSpeed, maxThreshold, minThreshold, 1, 0);
			relSpeed = Math.exp(relSpeed);
//			System.out.println(relSpeed);
			relSpeed = TrackingUtility.minmax(relSpeed, maxRelSpeed, 1, 1.9, 1);
		}
		else
		{
			double maxThreshold = this.maxRelAccelThreshold;//120;
			double minThreshold = this.minRelAccelThreshold;//15;
			double minMaxSpeed = Math.max(minThreshold, pDevice.getInteractionSpeed());
			minMaxSpeed = Math.min(maxThreshold, minMaxSpeed);
			
			relSpeed = TrackingUtility.minmax(minMaxSpeed, maxThreshold, minThreshold, 1, 0);
			relSpeed = Math.exp(relSpeed);
			relSpeed = TrackingUtility.minmax(relSpeed, maxRelSpeed, 1, 2.9, 1.2);
		} 

		Point2d iPoint;
		Vector3d normTurned = (Vector3d)currentIntersection.getInterceptObject().displayNorm.clone();
		normTurned.scale(-1.0);
		Point2d pCurrent = currentIntersection.getInterceptObject().getOversizeIntersectionPoint2dSingle(pDevice.getPosition(), normTurned);
		Point2d pOld = currentIntersection.getInterceptObject().getOversizeIntersectionPoint2dSingle(pDevice.getPositionOld(),normTurned);
//		pCurrent.sub(pOld);
//		System.out.println(pCurrent);
		Point2d delta = new Point2d();
//		if (relSpeed > 0.2)
//			System.out.println(Math.floor(relSpeed*100));
		delta.x = (pCurrent.x - pOld.x) * currentIntersection.getInterceptObject().displayHeight * relSpeed;
		delta.y = (pCurrent.y - pOld.y) * currentIntersection.getInterceptObject().displayHeight * relSpeed;
		try
		{
			iPoint = currentIntersection.getIntersectionPoint2d();
		}
		catch(Exception ex)
		{
			return null;
		}
		delta = currentIntersection.getInterceptObject().normalizeDistance(delta);
		iPoint.x += delta.x;
		iPoint.y += delta.y;

		if (iPoint.x < 0 || iPoint.y < 0 || iPoint.x > 1 || iPoint.y > 1)
		{ 
			
			pDevice.setRPIntersection(currentIntersection);
			Intersection offI = new Intersection();
			offI.setIntersection(iPoint);
			offI.setIntercepObject(currentIntersection.getInterceptObject());
			offI.setIsOffscreen(true);
			pDevice.addOffscreenIntersection(offI);
			currentIntersection = null;
			return null;
		}
		else
		{
			pDevice.setRPIntersection(currentIntersection);
			return currentIntersection;
		}

	}
	/**
		 * 
		 * @param pDevice
		 * @return
		 */
		private Intersection directPointing(PointingDevice pDevice)
		{
			Intersection currentIntersection = pDevice.getDPIntersection();
			Point2d tmpIntersection;
			for (InterceptObject iObject : interceptObjects)
			{
				tmpIntersection = iObject.getIntersectionPoint2d(pDevice);
				Point2d oversize = iObject.getOversizeIntersectionPoint2d(pDevice, true);
	//			pDevice.addOffscreenIntersection()
				//System.out.println("OVERSIZE " +oversize);
				if (tmpIntersection != null)
				{
					if (currentIntersection.getCenterDistance() < pDevice.getDPIntersection().getCenterDistance())
					{
						currentIntersection = pDevice.getDPIntersection();
					}
				}
			}
	//		pDevice.setPublishedIntersection(currentIntersection);
			if (currentIntersection.getIntersectionPoint2d() != null)
			{
				pDevice.setDPIntersection(currentIntersection);
				return currentIntersection;
			}
			else
			{
				pDevice.setDPIntersection(currentIntersection);
				return null;
			}
		}

	private double maxRelSpeed = Math.exp(1); 
	private LinkedList<Double> speeds = new LinkedList<Double>();
	/**
	 * 
	 * @param pDevice
	 * @return
	 */
	private Intersection hybridPointing(PointingDevice pDevice)
	{
		Intersection currentIntersection = pDevice.getPublishedIntersection();
		double maxThreshold = this.maxHybridSpeedThreshold;//11;
		double minThreshold = this.minHybridSpeedThreshold;//5;
		double minMaxSpeed = Math.max(minThreshold, pDevice.adapedRotationSpeed);
		minMaxSpeed = Math.min(maxThreshold, minMaxSpeed);
		double tmpMinRatio;
		double minRotThresh  = 0.1;
		if (pDevice.rotationSpeed <= minRotThresh || this.minDirectPointRatio == 0)
			tmpMinRatio = 0;
		else
		{
			if (pDevice.rotationSpeed/100 < this.minDirectPointRatio-(minRotThresh/10))
			tmpMinRatio = (pDevice.rotationSpeed-minRotThresh)/100;
				else
			tmpMinRatio = this.minDirectPointRatio-(minRotThresh/10);
		}
//		System.out.println(pDevice.rotationSpeed + "\t" + tmpMinRatio);
		double relSpeed = TrackingUtility.minmax(minMaxSpeed, maxThreshold, minThreshold, this.maxDirectPointRatio, tmpMinRatio);
//		System.out.println(relSpeed);
		relativeWeight = 1.0;
		if (currentIntersection == null || currentIntersection.getInterceptObject() == null)
		{
			directPointing(pDevice);
			currentIntersection = pDevice.getDPIntersection();
		}
		else
		{
			Intersection dirIntersect;
			dirIntersect = directPointing(pDevice);
			Point2d currentPoint = currentIntersection.getInterceptObject().getOversizeIntersectionPoint2d(pDevice, true);
			if (currentPoint == null)
			{
				System.out.println("OUT OF AREA");
				currentIntersection = null;
				return currentIntersection;
			}
			else
			{
				double screenOverSize = currentIntersection.getInterceptObject().screenOverSize;
				double x = TrackingUtility.minmax(currentPoint.x, 1, 0, 1 + screenOverSize, 0-screenOverSize);
				double y = TrackingUtility.minmax(currentPoint.y, 1, 0, 1 + screenOverSize, 0-screenOverSize);
				if (dirIntersect != null)
				{
					dirIntersect.setIntersection(new Point2d(x,y));
				}
				else
				{
					dirIntersect = new Intersection();
					dirIntersect.setIntercepObject(currentIntersection.getInterceptObject());
					dirIntersect.setIntersection(new Point2d(x,y));
				}
			}
			if (dirIntersect != null && dirIntersect.getIntersectionPoint2d() != null)
			{
				if (relativePointing(pDevice) != null && !Double.isNaN(pDevice.getRPIntersection().getIntersectionPoint2d().x))
				{			
					Intersection relIntersect = pDevice.getRPIntersection();
					Point2d rel2d = null;
//					System.out.println("RELSPEED " + relSpeed);
//					rel2d = relIntersect.getWeightedIntersetionPoint2d(dirIntersect, relSpeed);
					relativeWeight = relSpeed;
					rel2d = relIntersect.getAdaptiveIntersection2d(dirIntersect, relSpeed, this.increasingSpeedThresold);
//					System.out.println(rel2d.toString());
					if (rel2d != null)
					{
						currentIntersection.setIntersection(rel2d);
						pDevice.setRPIntersection(currentIntersection);
					}
					else
					{
						currentIntersection = dirIntersect;
					}
				}
				else
				{
					//System.out.println(dirIntersect);
//					System.out.println(dirIntersect.getInterceptObject().getOversizeIntersectionPoint2d(pDevice, false));
					currentIntersection = null;
				}
			}
//			else
//			{
//				System.out.println("OFFSCREEN NULL");
//				currentIntersection = pDevice.getDPIntersection();
//			}
		}
		return currentIntersection;
	}
	/**
	 * 
	 * @param name
	 * @return
	 */
	private InterceptObject getInterceptObject(String name)
	{
		for (InterceptObject iObject : interceptObjects)
		{
			if (iObject.compareTo(name) == 0)
			{
				return iObject;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	private PointingDevice getDevice(int id)
	{
		for (PointingDevice pd : pointingDevices)
		{
			if (pd.compareTo(id) == 0)
			{
				return pd;
			}
		}
		return null;
	}
	
	
}