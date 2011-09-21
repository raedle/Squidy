package org.squidy.nodes.optitrack;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.swing.KeyStroke;
import javax.vecmath.*;

import org.apache.poi.hwpf.usermodel.DateAndTime;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
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
import org.squidy.nodes.optitrack.utils.TrackingConstant;
import org.squidy.nodes.optitrack.utils.TrackingUtility;

import com.sun.opengl.util.Screenshot;


/*<code>InterceptionPoint</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AM
* </pre>
* 
* @author Simon Faeh, < href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de</>, University f Konstanz
* 
* @version $Id: InterceptionPoint.java 772 2011-09-16 15:39:44Z raedle $
*/
@XmlType(name = "Intercept")
@Processor(
	name = "Intersection Point",
	icon = "/org/squidy/nodes/image/48x48/intercept.png",
	description = "",
	types = {Processor.Type.OUTPUT, Processor.Type.INPUT },
	tags = { "optitrack", "handtracking", "intersection" }
)

public class InterceptionPoint extends AbstractNode {
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "useTarget")
	@Property(name = "Use Targetdirectoin", description = "Use direction of the target")
	@CheckBox
	private boolean useTargetDirection =  false;

	public final boolean getUseTargetDirection() {
		return useTargetDirection;
	}

	public final void setUseTargetDirection(boolean isDisplay) {
		this.useTargetDirection = isDisplay;
	}
	// ################################################################################	
	
	@XmlAttribute(name = "offScreen")
	@Property(name = "Send offScreen Position", description = "Sending the offscreen Position of the pointer")
	@CheckBox
	private boolean sendOffScreen =  false;

	public final boolean getSendOffScreen() {
		return sendOffScreen;
	}

	public final void setSendOffScreen(boolean sendOffScreen) {
		this.sendOffScreen = sendOffScreen;
	}
	// ################################################################################	
	
	@XmlAttribute(name = "minExtend")
	@Property(
		name = "Min Target extend",
		description = "Minmum considered extension of markers",
		suffix = " mm"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 250,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 5,
		snapToTicks = true
	)
	private int minExtend = 10;

	public int getMinExtend() {
		return minExtend;
	}

	public void setMinExtend(int frameRate) {
		this.minExtend = frameRate;
	}
	// ################################################################################

	@XmlAttribute(name = "maxExtend")
	@Property(
		name = "Max Target extend",
		description = "Maximum considered extension of markers",
		suffix = " mm"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 250,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 5,
		snapToTicks = true
	)
	private int maxExtend = 155;

	public int getMaxExtend() {
		return maxExtend;
	}

	public void setMaxExtend(int frameRate) {
		this.maxExtend = frameRate;
	}
	// ################################################################################

	@XmlAttribute(name = "minAngle")
	@Property(
		name = "Min Angle",
		description = "Minimum of deflection (in degree)",
		suffix = " deg"
	)
	@Slider(
		type = Integer.class,
		minimumValue = -80,
		maximumValue = 80,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 1,
		snapToTicks = true
	)
	private int minDefAngle = -6;

	public int getMinDefAngle() {
		return minDefAngle;
	}

	public void setMinDefAngle(int frameRate) {
		this.minDefAngle = frameRate;
	}

	// ################################################################################

	@XmlAttribute(name = "maxAngle")
	@Property(
		name = "Max Angle",
		description = "Maximum of deflection (in degree)",
		suffix = " deg"
	)
	@Slider(
		type = Integer.class,
		minimumValue = -80,
		maximumValue = 80,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 1,
		snapToTicks = true
	)
	private int maxDefAngle = -22;

	public int getMaxDefAngle() {
		return maxDefAngle;
	}

	public void setMaxDefAngle(int frameRate) {
		this.maxDefAngle = frameRate;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "objectDistance")
	@Property(name = "Apply Object distance", description = "If close to object use relative pointing")
	@CheckBox
	private boolean applyDistance = false;

	public boolean getApplyDistance() {
		return applyDistance;
	}

	public void setApplyDistance(boolean applyDistance) {
		this.applyDistance = applyDistance;
	}
	
	// ################################################################################	
	
	@XmlAttribute(name = "screenOversize")
	@Property(
		name = "Screen Oversize",
		description = "Virutally enlarged Screen",
		suffix = " %"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 100,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 5,
		snapToTicks = true
	)
	private int overSize = 20;

	public int getOverSize() {
		return overSize;
	}

	public void setOverSize(int overSize) {
		this.overSize = overSize;
		this.overSizeRel = (double)overSize / 100.0;
	}
	// ################################################################################
	
	@XmlAttribute(name = "useRelativePointing")
	@Property(name = "Use relative Pointing", description = "On-Screen Pointing is calculated relative to handmovement")
	@CheckBox
	private boolean useRelativePointing = false;

	public boolean getUseRelativePointing() {
		return useRelativePointing;
	}

	public void setUseRelativePointing(boolean useRelativePointing) {
		this.useRelativePointing = useRelativePointing;
	}
	
	// ################################################################################	
	
	@XmlAttribute(name = "relPointingDuality")
	@Property(
		name = "Pointing mixer",
		description = "Percentage of relative Pointing",
		suffix = " %"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 100,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 5,
		snapToTicks = true
	)
	private int relativity = 25;

	public int getRelativity() {
		return relativity;
	}

	public void setRelativity(int relativity) {
		this.relativity = overSize;
		this.relativityRel = (double)relativity / 100.0;
	}
	// ################################################################################
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	
	
	private Vector3d topLeft, bottomLeft, bottomRight, displayNorm, screenCenter;
	private Vector3d displayUp, displayRight;
	private String[] corners, chunks;
	private double layerDistance;
	private double displayWidth, displayHeight, displayWidthOrig, displayHeightOrig;
	
	private MathUtility mu = new MathUtility();
	private double[][] m6d = new double[3][3];
	private double[] rotatingPoint1 = new double[3];
	private double[] rotatingPoint2 = new double[3];
	private double[] origin = new double[3];	

	private double overSizeRel, relativityRel;
	
	@Override
	public void onStart() {
		topLeft = new Vector3d();
		bottomLeft = new Vector3d();
		bottomRight = new Vector3d();
		this.overSizeRel = (double)overSize / 100.0;
	}
	
    
	private DataPosition6D lastObjectPosition;
	private DataPosition3D pointingFinger;
	private DataButton lastButtonReceived;
	private int currentGroupID = 0;
	private long lastTUIOsent = 0;
	private long[] cursorOnScreen = new long[200];
	private double mysteriousR;
	private boolean interceptionPointFound = false;
	private boolean initialPress = false;
	private boolean[] relativPointingMode =  new boolean[200];
	private Vector3d[] enterPoint3d = new Vector3d[200];
	private Vector2d[] enterPoint2d = new Vector2d[200];
	private Vector3d[] enterDirection = new Vector3d[200];
	private List<DataPosition2D> d2dList;
	private Vector2d absolutScreenPoint;
	private Vector2d relativeScreenPoint;
	private Vector2d calculatedScreenPoint;
	
	
	@Override 
	public IDataContainer preProcess(IDataContainer dataContainer)
	{
//		List<DataButton> dataButton = DataUtility.getDataOfType(DataButton.class, dataContainer);
//		if (dataButton.size() > 0)
//		{
//			lastButtonReceived = dataButton.get(0).getClone();
//			if (interceptionPointFound)
//			{
//				return dataContainer;
//			}
//			else
//				return null;
//		}
		
		List<DataPosition6D> markerData6D = DataUtility.getDataOfType(DataPosition6D.class, dataContainer);
		if (markerData6D.size() == 0)
		{
			if (interceptionPointFound)
			{
				return dataContainer;
			}
			else
			{
			    List<DataString> disp = DataUtility.getDataOfType(DataString.class, dataContainer);
			    if (disp.size() > 0)
			    {
				    if (disp.get(0).hasAttribute(TrackingConstant.OBJECTDEPTH))
				    	return dataContainer;
				    else
				    	return null;
			    }
			    else
			    {
			    	return null;
			    }
			}
		}
		

		DataPosition6D dataPosition6d = markerData6D.get(0);
		if (dataPosition6d.hasAttribute(TrackingConstant.RIGIDBODYROLE)) 
		{
			if (Integer.valueOf(dataPosition6d.getAttribute(TrackingConstant.RIGIDBODYROLE).toString()) == TrackingConstant.RBROLE_MOBILEDISPLAY)
			{
				/*
				 *		CREATE MOBILE DISPLAY 
				 */
				prepareDataforDisplayCreation(dataPosition6d);
			}
			else if (Integer.valueOf(dataPosition6d.getAttribute(TrackingConstant.RIGIDBODYROLE).toString()) == TrackingConstant.RBROLE_POINTINGDEVICE) 
			{
				/*
				 * 	Calculate intercept point
				 * 
				 */
				if (displayNorm != null && displayUp != null)
				{
//					if (markerData6D.get(0).hasAttribute(TrackingConstant.MOBILEDISPLAY)) 
//						if (Boolean.valueOf(markerData6D.get(0).getAttribute(TrackingConstant.MOBILEDISPLAY).toString()))
//							return dataContainer;
					DataPosition6D rb =  TrackingUtility.Norm2RoomCoordinates(Optitrack.class, markerData6D.get(0).getClone());
			    	Vector3d rbCenter = new Vector3d(rb.getX(), rb.getY(), rb.getZ());
			    	int rigidBodyID = Integer.valueOf(dataPosition6d.getAttribute(DataConstant.IDENTIFIER).toString());
					List<DataPosition3D> markerData3D = DataUtility.getDataOfType(DataPosition3D.class, dataContainer);
					markerData3D.remove(0);
					double angle = 0;
					if (markerData3D.size() > 0) 
					{
						double maxDist = 0;
						
						if (!useTargetDirection)
						{
							for (int i = 0; i < markerData3D.size(); i++)
							{
								DataPosition3D d3d = TrackingUtility.Norm2RoomCoordinates(Optitrack.class, markerData3D.get(i).getClone());
								for (int j = i; j < markerData3D.size(); j++)
								{
									DataPosition3D needle = TrackingUtility.Norm2RoomCoordinates(Optitrack.class, markerData3D.get(j).getClone());
								    double tmpDist = mu.euclidDist(d3d, needle);
								    if (maxDist < tmpDist)
								    	maxDist = tmpDist;
								}
							}
							if (markerData3D.size() >= 4)
								pointingFinger = markerData3D.get(1);
							maxDist = Math.min(maxExtend,maxDist);
							maxDist = Math.max(minExtend,maxDist);
							maxDist = minmax(maxDist, maxExtend, minExtend, 1, 0);
							angle = minmax(maxDist,1,0,Math.tan(Math.toRadians(minDefAngle)),Math.tan(Math.toRadians(maxDefAngle)));
						}
					}
//					System.out.println("INTERCEPT ANGLE " + angle);
					Vector3d dirZ;
					dirZ = new Vector3d(0,angle,1);
					Vector3d dir = new Vector3d();
					Vector3d cent = new Vector3d(0,0,0);
					dir = mu.rotatePoint(dirZ, cent, mu.dataPosition6D2matrix(rb),false,false);
					dir.normalize();
					double enterAngleDifX = 0, enterAngleDifY = 0;
					//System.out.println("FIRST " + dir);
					this.absolutScreenPoint = getInterceptionPoint(dir, rbCenter);
					if (this.relativPointingMode[rigidBodyID] == true && this.useRelativePointing == true)
					{
						Vector3d up = new Vector3d(0,0,1);
						Vector3d crossV = new Vector3d();
						crossV.cross(up, enterDirection[rigidBodyID]);
						enterAngleDifX = dir.angle(crossV);
//						System.out.print(Math.toDegrees(enterAngleDifX)-90);
						Vector3d left = new Vector3d(0,1,0);
						crossV.cross(left, enterDirection[rigidBodyID]);
						enterAngleDifY = dir.angle(crossV);
//						System.out.println("\t"+ (Math.toDegrees(enterAngleDifY)-90));
						dir = (Vector3d)enterPoint3d[rigidBodyID].clone();
						dir.sub(screenCenter);
						dir.scale(-1);
						dir.normalize();
						this.relativeScreenPoint = getInterceptionPoint(dir, rbCenter);
						//System.out.println(relativeScreenPoint + " \t" + dir);
					}

					
//					System.out.println("INTERCEPT " + screenX + "\t" + screenY + "\t" + intersectionPoint.toString());
					
//					double s1  = displayNorm.dot(rbCenter) - layerDistance;
//					double s2  = displayNorm.dot(dir);
//					
//					s1 = s1 / s2 * -1;
//					
//					dir.scaleAdd(s1,rbCenter);
//					System.out.println(dir);
//					Vector3d dirLen = (Vector3d) dir.clone();
//					dirLen.sub(rbCenter);
//					double objDist = dirLen.length();
//					
//					objDist = Math.min(400,objDist);
//					objDist = Math.max(170,objDist);
//					objDist = minmax(objDist, 400, 170, 1, 0);
//					
//					if (pointingFinger != null)
//						rbCenter = new Vector3d(pointingFinger.getX(),pointingFinger.getY(),pointingFinger.getZ());
//					
//					if (objDist > 0 && !this.useTargetDirection && applyDistance)
//					{
//						double currentX = minmax(rbCenter.x,bottomRight.x,bottomLeft.x,1,0);
//						double currentY = minmax(rbCenter.y,topLeft.y,bottomLeft.y,1,0);
//						screenX = (objDist * screenX +  (1-objDist) * currentX);
//						screenY = (objDist * screenY +  (1-objDist) * currentY);
//					}
					calculatedScreenPoint = absolutScreenPoint;
					if ((calculatedScreenPoint.x <= 0 || 
							calculatedScreenPoint.x > 1 || 
							calculatedScreenPoint.y < 0 || 
							calculatedScreenPoint.y > 1) && 
							mysteriousR > 0)
						{
							this.relativPointingMode[rigidBodyID] = false;
						}
					if (this.relativPointingMode[rigidBodyID] && this.useRelativePointing)
					{
						
						calculatedScreenPoint = new Vector2d();
						calculatedScreenPoint.x = (absolutScreenPoint.x* relativityRel+ relativeScreenPoint.x * (1-relativityRel));
						calculatedScreenPoint.y = (absolutScreenPoint.y * relativityRel + relativeScreenPoint.y * (1-relativityRel));
					}

//					calculatedScreenPoint.x *= this.displayWidth;
//					calculatedScreenPoint.y *= this.displayHeight;
//					calculatedScreenPoint.x -= overSizeRel * (displayWidth/2);
//					calculatedScreenPoint.x -= overSizeRel * (displayHeight/2);
//					calculatedScreenPoint.x /= this.displayWidthOrig;
//					calculatedScreenPoint.y /= this.displayHeightOrig;
//					if (screenY > overSizeRel && screenY < 1-overSizeRel && screenX > overSizeRel && screenX < 1-overSizeRel && mysteriousR > 0)
					double tempRel = 0;
					if (overSizeRel > 0)
					{
						tempRel = overSizeRel/2;
					}
					if (calculatedScreenPoint.x > (tempRel/2) && 
						calculatedScreenPoint.x < 1-(tempRel/2) && 
						calculatedScreenPoint.y > (tempRel/2) && 
						calculatedScreenPoint.y < 1-(tempRel/2) && 
						mysteriousR > 0)
					{
//						if (screenPointingX > 0 && screenPointingX < 1 && screenPointingY < 1 && screenPointingY > 0) 
//						{
//							
//						}else
//						{
//							this.relativPointingMode[rigidBodyID] = false;
//						}
						calculatedScreenPoint.x = minmax(calculatedScreenPoint.x,1-(overSizeRel/2),overSizeRel/2,1,0);
						calculatedScreenPoint.y = minmax(calculatedScreenPoint.y,1-(overSizeRel/2),overSizeRel/2,1,0);
//						calculatedScreenPoint.x *= this.displayWidth;
//						calculatedScreenPoint.y *= this.displayHeight;
//						calculatedScreenPoint.x /= this.displayWidthOrig;
//						calculatedScreenPoint.y /= this.displayHeightOrig;
//						System.out.println("Onscreen " + screenX + " "  + screenY + " " + rigidBodyID);
		        		DataPosition2D d2d = new DataPosition2D(MouseIO.class ,calculatedScreenPoint.x, calculatedScreenPoint.y);
		        		d2d.setAttribute(TrackingConstant.KEYWORD, "ONSCREEN");
		        		d2d.setAttribute(DataConstant.GROUP_ID, dataPosition6d.getAttribute(DataConstant.GROUP_ID));
		        		if (this.cursorOnScreen[rigidBodyID] == 0)
		        		{
		        			System.out.println("NEW ENTER POINT");
		        			this.cursorOnScreen[rigidBodyID] = System.currentTimeMillis();
		        			enterPoint2d[rigidBodyID] = new Vector2d(d2d.getX(),d2d.getY());
		        			enterDirection[rigidBodyID] = dir;
		        			enterPoint3d[rigidBodyID] = rbCenter;
		        			if (this.useRelativePointing)
		        				this.relativPointingMode[rigidBodyID] = true;
		        		}
		        		if (dataPosition6d.hasAttribute(TrackingConstant.GESTUREID))
		        		{
		        			if (Integer.valueOf(dataPosition6d.getAttribute(TrackingConstant.GESTUREID).toString()) > 0)
		        			{
		        				d2d.setAttribute(TrackingConstant.SENDTUIO, "TRUE");
		        				d2d.setAttribute(TrackingConstant.GESTUREID, dataPosition6d.getAttribute(TrackingConstant.GESTUREID));
		        				d2d.setAttribute(TrackingConstant.HANDSIDE, dataPosition6d.getAttribute(TrackingConstant.HANDSIDE));
//		        				System.out.println("AA "+dataPosition6d.getAttribute(TrackingConstant.GESTUREID));
		        			}else
		        			{
		        				d2d.setAttribute(TrackingConstant.GESTUREID,0);
		        				d2d.setAttribute(TrackingConstant.HANDSIDE,dataPosition6d.getAttribute(TrackingConstant.RIGIDBODYID));
		        			}
		        		}else
		        		{
		        			d2d.setAttribute(TrackingConstant.GESTUREID,0);
		        			d2d.setAttribute(TrackingConstant.HANDSIDE,dataPosition6d.getAttribute(TrackingConstant.RIGIDBODYID));
		        		}
		        		d2d.setAttribute(TrackingConstant.RIGIDBODYID, dataPosition6d.getAttribute(TrackingConstant.RIGIDBODYID));		        		
		        		if (dataPosition6d.hasAttribute(TrackingConstant.TUIOID))
		        		{
		        			//if (lastTUIOsent + 200 > System.currentTimeMillis()|| lastTUIOsent == 0)
		        			{
		        				d2d.setAttribute(TrackingConstant.TUIOID, dataPosition6d.getAttribute(TrackingConstant.TUIOID));
//		        				System.out.println("BB "+dataPosition6d.getAttribute(TrackingConstant.TUIOID));
		        				lastTUIOsent = System.currentTimeMillis();
		        			}
		        		}
		        		//System.out.println("intercept " + dataPosition6d.getAttribute(TrackingConstant.TUIOID) + " " + dataPosition6d.getAttribute(TrackingConstant.GESTUREID));
//		        		d2d.setAttribute(DataConstant.IDENTIFIER, dataPosition6d.getAttribute(DataConstant.IDENTIFIER));
		        		interceptionPointFound = true;
		        		if (d2d.hasAttribute(DataConstant.GROUP_ID))
		        		{
		        			//System.out.println(Integer.valueOf(d2d.getAttribute(DataConstant.GROUP_ID).toString()));
		        			if (d2dList == null)
		        				d2dList = new ArrayList<DataPosition2D>();		        			
		        			if (Integer.valueOf(d2d.getAttribute(DataConstant.GROUP_ID).toString()) != currentGroupID)
		        			{
		        				currentGroupID = Integer.valueOf(d2d.getAttribute(DataConstant.GROUP_ID).toString());
		        				//System.out.println(d2dList.size());
		        				publish(d2dList);
		        				d2dList = new ArrayList<DataPosition2D>();
		        				d2dList.add(d2d);
		        			}else
		        			{
		        				d2dList.add(d2d);
		        			}
		        		}		        		
		        		//publish(d2d);
		        		if (calculatedScreenPoint.x > 0.1 && calculatedScreenPoint.x < 0.9 && calculatedScreenPoint.y > 0.1 && calculatedScreenPoint.y < 0.9)
		        		{
		        			if (lastButtonReceived != null && lastButtonReceived.getFlag() && !initialPress)
		        			{
		        				//publish(lastButtonReceived);
		        			}
		        			initialPress = true;
		        		}
//		        		if (this.mouseLeave < System.currentTimeMillis() - 500 && this.mouseLeave != 0)
//		        		{
//		        			if (lastButtonReceived != null)
//		        			{
//		        				//System.out.println("button " + lastButtonReceived.getButtonType());
//		        				//lastButtonReceived.setFlag(true);
//		        				//publish(lastButtonReceived);
//		        			}
//		        		}
		        		//this.mouseLeave = 0;			        		
		        		return dataContainer;
			        }
			        else
			        {
//						if (screenPointingX > 0 && screenPointingX < 1 && screenPointingY < 1 && screenPointingY > 0) 
//						{
//							
//						}else
//						{
//							this.relativPointingMode[rigidBodyID] = false;
//						}
//			        	
//			        	if (screenX > 0 && screenX < 1 && screenY < 1 && screenY > 0) 
//		        		{
//		        			
//		        		}else{
//		        			this.relativPointingMode[rigidBodyID] = false;
//		        		}
			        	if (cursorOnScreen[rigidBodyID] > 0 && !this.relativPointingMode[rigidBodyID])
			        	{
				        	cursorOnScreen[rigidBodyID] = 0;
				        	DataPosition2D d2d = new DataPosition2D(MouseIO.class,0,1);
				        	d2d.setAttribute(TrackingConstant.KEYWORD, "OFFSCREEN");
				        	System.out.println("offcreen " + rigidBodyID);
				        	d2d.setAttribute(DataConstant.GROUP_ID, dataPosition6d.getAttribute(DataConstant.GROUP_ID));
				        	//System.out.println("dummy");
				        	d2d.setAttribute(TrackingConstant.RIGIDBODYID, dataPosition6d.getAttribute(TrackingConstant.RIGIDBODYID));

				        	if (dataPosition6d.hasAttribute(TrackingConstant.TUIOID))
				        		d2d.setAttribute(TrackingConstant.TUIOID, dataPosition6d.getAttribute(TrackingConstant.TUIOID));
			        		//this.mouseLeave = System.currentTimeMillis();
			        		//if (lastButtonReceived != null )
			        		if (lastButtonReceived != null && lastButtonReceived.getFlag())
			        		{
			        			lastButtonReceived.setFlag(false);
			        			publish(lastButtonReceived);
			        		}
			        		if (d2d.hasAttribute(DataConstant.GROUP_ID))
			        		{
			        			int cursorCounter = 0;
			        			for (int i = 0; i < cursorOnScreen.length; i++)
			        			{
			        				if (cursorOnScreen[i] > 0)
			        					cursorCounter++;
			        			}
			        			if (d2dList == null)
			        				d2dList = new ArrayList<DataPosition2D>();
			        			if (cursorCounter == 2)
			        			{
				        			if (Integer.valueOf(d2d.getAttribute(DataConstant.GROUP_ID).toString()) != currentGroupID)
				        			{
				        				currentGroupID = Integer.valueOf(d2d.getAttribute(DataConstant.GROUP_ID).toString());
				        				publish(d2dList);
				        				d2dList = new ArrayList<DataPosition2D>();
				        				d2dList.add(d2d);
				        			}else
				        			{
				        				d2dList.add(d2d);
				        			}
			        			}else
			        			{
			        				publish (d2d);
			        			}
			        		}			        		
			        		//publish (d2d);
			        		initialPress = false;
			        	}
			        	/*
			        	 * 	send offscreen position
			        	 */
			        	if (this.sendOffScreen)
			        	{
			        		DataPosition2D d2d;
			        		//System.out.println(absolutScreenPoint);
			        		if (calculatedScreenPoint.x > 0 && calculatedScreenPoint.x < 1 && calculatedScreenPoint.y < 1 && calculatedScreenPoint.y > 0) 
			        		{
			        			System.out.println("Border " + calculatedScreenPoint);
			        			d2d = new DataPosition2D(MouseIO.class,calculatedScreenPoint.x,calculatedScreenPoint.y);
					        	d2d.setAttribute(TrackingConstant.KEYWORD, "OFFSCREENDATA");
					        	d2d.setAttribute(DataConstant.GROUP_ID, dataPosition6d.getAttribute(DataConstant.GROUP_ID));
					        	//System.out.println("dummy");
					        	d2d.setAttribute(TrackingConstant.RIGIDBODYID, dataPosition6d.getAttribute(TrackingConstant.RIGIDBODYID));
					        	d2d.setAttribute(TrackingConstant.TUIOID, 0);
//					        	if (d2d.hasAttribute(DataConstant.GROUP_ID))
//				        		{
//				        			int cursorCounter = 0;
//				        			for (int i = 0; i < cursorOnScreen.length; i++)
//				        			{
//				        				if (cursorOnScreen[i] > 0)
//				        					cursorCounter++;
//				        			}
//				        			if (d2dList == null)
//				        				d2dList = new ArrayList<DataPosition2D>();
//				        			if (cursorCounter == 2)
//				        			{
//					        			if (Integer.valueOf(d2d.getAttribute(DataConstant.GROUP_ID).toString()) != currentGroupID)
//					        			{
//					        				currentGroupID = Integer.valueOf(d2d.getAttribute(DataConstant.GROUP_ID).toString());
//					        				publish(d2dList);
//					        				d2dList = new ArrayList<DataPosition2D>();
//					        				d2dList.add(d2d);
//					        			}else
//					        			{
//					        				d2dList.add(d2d);
//					        			}
//				        			}else
				        			{
				        				publish (d2d);
				        			}
//				        		}
			        		}
			        	}
			        	//this.mouseEnter = 0;

			        	interceptionPointFound = false;
			        	return null;
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
		corners = dataString.data.split(";");
		chunks = corners[0].split(",");
        topLeft = new Vector3d(Double.parseDouble(chunks[0]),
			      Double.parseDouble(chunks[1]),
			      Double.parseDouble(chunks[2]));
        chunks = corners[1].split(",");
		bottomLeft = new Vector3d(Double.parseDouble(chunks[0]),
					      Double.parseDouble(chunks[1]),
					      Double.parseDouble(chunks[2]));
		chunks = corners[2].split(",");
		bottomRight = new Vector3d(Double.parseDouble(chunks[0]),
					      Double.parseDouble(chunks[1]),
					      Double.parseDouble(chunks[2]));
		
		createDisplay();
		return null;
	}
	
	/**
	 * Calculates interception point 
	 * @param dir direction of the target
	 * @param rbCenter center of the target
	 * @return normalized screenCoordinates
	 */
	private Vector2d getInterceptionPoint(Vector3d dir, Vector3d rbCenter)
	{
		Vector2d screenVector = new Vector2d();
		Vector3d w0, w = new Vector3d();
		double a,b;
		w0 = (Vector3d) rbCenter.clone();
		w0.sub(bottomLeft);
		a = displayNorm.dot(w0) * -1;
		b = displayNorm.dot(dir);
		
		mysteriousR = a / b;
		if (mysteriousR < 0.0)
		{
		//	return null;
			//System.out.println("internullll ");
		}
		
		Vector3d intersectionPoint = new Vector3d();
		intersectionPoint = (Vector3d) rbCenter.clone();
		dir.scale(mysteriousR);
		intersectionPoint.add(dir);
		double uu,uv,vv,wu,wv,D;
		
		uu = displayUp.dot(displayUp);
		uv = displayUp.dot(displayRight);
		vv = displayRight.dot(displayRight);
		w = (Vector3d) intersectionPoint.clone();
		w.sub(bottomLeft);
		wu = w.dot(displayUp);
		wv = w.dot(displayRight);
		
		D = uv * uv - uu * vv;
		
		screenVector.y = (uv * wv - vv * wu) / D;
		screenVector.x = (uv * wu - uu * wv) / D;		
		return screenVector;
	}
	
	/**
	 * Prepares global parameters for displaycreation based on 6D Values
	 * @param dataPosition6d
	 */
	private void prepareDataforDisplayCreation(DataPosition6D dataPosition6d)
	{
		DataPosition6D d6d;
		lastObjectPosition = dataPosition6d.getClone();
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
				
		origin[0] = 0;
		origin[1] = 0;
		origin[2] = 0;
		
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
			rotatingPoint1[0] = Double.valueOf(lastObjectPosition.getAttribute(TrackingConstant.OBJECTWIDHT).toString());
			rotatingPoint1[1] = 0;
			rotatingPoint1[2] = 0;
		
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
		createDisplay();
	}
	
	/**
	 * creates static or mobile display from global parameters
	 */
	private void createDisplay()
	{
       
		Vector3d bltl,brtl, blbr, n3,n4 = new Vector3d();
		bltl = (Vector3d)bottomLeft.clone();
		brtl = (Vector3d)bottomRight.clone();
		blbr = (Vector3d)bottomRight.clone();
		screenCenter = (Vector3d)bottomRight.clone();
		bltl.sub(topLeft);
		brtl.sub(topLeft);
		blbr.sub(bottomLeft);
		displayHeightOrig = bltl.length();
		displayWidthOrig = blbr.length();
		brtl.scale(-0.5);
		screenCenter.add(brtl);
		brtl = (Vector3d)bottomRight.clone();

		
		if (this.overSize > 0)
		{
			bltl.scale(((double)overSize / 100.0));
			blbr.scale(((double)overSize / 100.0));
			bottomLeft.add(bltl);
			bottomLeft.sub(blbr);
			bottomRight.add(blbr);
			bottomRight.add(bltl);
			topLeft.sub(bltl);
			topLeft.sub(blbr);
			bltl = (Vector3d)bottomLeft.clone();
			brtl = (Vector3d)bottomRight.clone();
			blbr = (Vector3d)bottomRight.clone();
			bltl.sub(topLeft);
			brtl.sub(topLeft);
			blbr.sub(bottomLeft);
		}
		if (displayNorm ==  null)
			displayNorm = new Vector3d();
		displayNorm.cross(bltl,brtl); 
		displayNorm.normalize();	
		
        layerDistance = displayNorm.dot(topLeft);
        //displayHeight = TL.y - BL.y;
        //displayWidth = BR.x - BL.x;
        n3 = (Vector3d)bottomLeft.clone();
        n4 = (Vector3d)bottomRight.clone();
        n3.sub(n4);
        displayWidth = n3.length();
        n3 = (Vector3d)bottomLeft.clone();
        n4 = (Vector3d)topLeft.clone();
        n3.sub(n4);
        displayHeight = n3.length();
        
        displayUp = (Vector3d)topLeft.clone();
        displayWidth = displayUp.length();
        displayRight = (Vector3d)bottomRight.clone();
        displayHeight = displayRight.length();
        displayUp.sub(bottomLeft);
        displayRight.sub(bottomLeft);
        if (displayNorm == null)
        	displayNorm = new Vector3d();
        displayNorm.cross(displayUp,displayRight); 
        displayNorm.normalize();
       
        layerDistance = displayNorm.dot(topLeft);
	}


    private double minmax(double x, double max, double min, double new_max, double new_min)
    {
          x = (x - min) * (new_max - new_min) / (max - min) + new_min;
          return x;
    }
}