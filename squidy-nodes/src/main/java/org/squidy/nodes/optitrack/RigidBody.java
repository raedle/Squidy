package org.squidy.nodes.optitrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.KeyStroke;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.math.stat.descriptive.moment.ThirdMoment;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.Keyboard;
import org.squidy.nodes.Kalman.ModeDomainProvider;
import org.squidy.nodes.optitrack.utils.RBConnector;
import org.squidy.nodes.optitrack.utils.TrackingConstant;
import org.squidy.nodes.optitrack.utils.TrackingUtility;


/*<code>RigidBody</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AM
* </pre>
* 
* @author Simon Faeh, <a href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de<a/>, University of Konstanz
* 
* @version 27.10.2010 / sf 
*/
@XmlType(name = "RigidBody")
@Processor(
	name = "Rigid Body",
	icon = "/org/squidy/nodes/image/48x48/handtracking.png",
	description = "Selects Rigidbodys due to definition",
	types = {Processor.Type.OUTPUT, Processor.Type.INPUT },
	tags = { "rigid body", "optitrack", "handtracking", "interception" },
	status = Status.UNSTABLE
)

public class RigidBody extends AbstractNode {
	
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "rigidBodyID")
	@Property(
		name = "Rigid Body ID",
		description = "ID of this Rigid Body"
	)
	@ComboBox(domainProvider = RBIDDomainProvider.class)
	private int rigidBodyID = TrackingConstant.RB_HANDRIGHT;
	
	public final int getRigidBodyID() {
		return rigidBodyID;
	}

	public final void setRigidBodyID(int rID) {
		this.rigidBodyID = rID;
	}
	
	// ################################################################################

	@XmlAttribute(name = "rbfunction")
	@Property(
		name = "Role",
		description = "Defines which role this rigid body plays in the secene"
	)
	@ComboBox(domainProvider = RB_ROLEDomainProvider.class)
	private int rbRole = TrackingConstant.RBROLE_POINTINGDEVICE;
	
	public final int getRbRole() {
		return rbRole;
	}

	public final void setRbRole(int rbRole) {
		this.rbRole = rbRole;
	}
	// ################################################################################

	@XmlAttribute(name = "rbMode")
	@Property(
		name = "PointinMode",
		description = "Defines which role this rigid body plays in the secene"
	)
	@ComboBox(domainProvider = RB_MODEDomainProvider.class)
	private int rbMode = TrackingConstant.RBMODE_DIRECTPOINTING;
	
	public final int getRbMode() {
		return rbMode;
	}

	public final void setRbMode(int rbMode) {
		this.rbMode = rbMode;
	}	
	
	// ################################################################################
	
	@XmlAttribute(name = "max-target-extend")
	@Property(
		name = "Maximum Target extend",
		description = "The maximum extend of any Target for additional Marker search (set 0 if no additional Markers required)"
	)
	@TextField
	private int maxExtend = 0;
	
	public final int getMaxExtend() {
		return maxExtend;
	}

	public final void setMaxExtend(int aMaxExtend) {
		this.maxExtend = aMaxExtend;
	}

	// ################################################################################
	@XmlAttribute(name = "yDeflection")
	@Property(
		name = "Y Deflection Adjustment",
		description = "Setting the Delection-Adjustment of the Y Axis"
	)
	@TextField
	private double yDeflectionAdjust = 1.0;

	public final double getYDeflectionAdjust() {
		return yDeflectionAdjust;
	}

	public final void setYDeflectionAdjust(double yDeflection) {
		this.yDeflectionAdjust = yDeflection;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "mouseButtons")
	@Property(name = "Mouse-Buttons attached", description = "External Mouse-Events are attached (Laserpointer)")
	@CheckBox
	private boolean sendButtons = false;

	public boolean getSendButtons() {
		return sendButtons;
	}

	public void setSendButtons(boolean sendButtons) {
		this.sendButtons = sendButtons;
	}
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	

	// ################################################################################
	// BEGIN OF DOMAIN PROVIDERS
	// ################################################################################

	public static class RBIDDomainProvider implements DomainProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[23];
			values[0] = new ComboBoxItemWrapper(TrackingConstant.RB_HANDRIGHT, "Glove (right)");
			values[1] = new ComboBoxItemWrapper(TrackingConstant.RB_HANDLEFT, "Glove (left)");
			values[2] = new ComboBoxItemWrapper(TrackingConstant.RB_PEN, "Pen");
			values[3] = new ComboBoxItemWrapper(TrackingConstant.RB_LASER, "Laser-Pointer");
			values[4] = new ComboBoxItemWrapper(TrackingConstant.RB_IPHONE, "iPhone1");
			values[4] = new ComboBoxItemWrapper(TrackingConstant.RB_IPHONE2, "iPhone2");
			values[5] = new ComboBoxItemWrapper(TrackingConstant.RB_MOBILEDISPLAY, "Mobile-Display");
			values[6] = new ComboBoxItemWrapper(TrackingConstant.RB_CITRON, "Citron");
			values[7] = new ComboBoxItemWrapper(TrackingConstant.RB_BASE1, "Base1");
			values[8] = new ComboBoxItemWrapper(TrackingConstant.RB_BASE2, "MBase2");
			values[9] = new ComboBoxItemWrapper(TrackingConstant.RB_PERSON1, "Person1");
			values[10] = new ComboBoxItemWrapper(TrackingConstant.RB_LEFT_CLICK, "Left Click Gesture");
			values[11] = new ComboBoxItemWrapper(TrackingConstant.RB_LEFT_GRAB, "Left Grab Gesture");
			values[12] = new ComboBoxItemWrapper(TrackingConstant.RB_LEFT_POINT, "Left Point Gesture");
			values[13] = new ComboBoxItemWrapper(TrackingConstant.RB_RIGHT_CLICK, "Right Click Gesture");
			values[14] = new ComboBoxItemWrapper(TrackingConstant.RB_RIGHT_GRAB, "Right Grab Gesture");
			values[15] = new ComboBoxItemWrapper(TrackingConstant.RB_RIGHT_POINT, "Right Point Gesture");
			values[16] = new ComboBoxItemWrapper(TrackingConstant.RB_LEFT_CLICK2, "Left Click PartialGesture");
			values[17] = new ComboBoxItemWrapper(TrackingConstant.RB_LEFT_POINT2, "Leftt Point PartialGesture");	
			values[18] = new ComboBoxItemWrapper(TrackingConstant.RB_RIGHT_CLICK2, "Right Click PartialGesture");
			values[19] = new ComboBoxItemWrapper(TrackingConstant.RB_RIGHT_POINT2, "Right Point PartialGesture");
			values[20] = new ComboBoxItemWrapper(TrackingConstant.RB_RIGHT_BASE, "Right Base");
			values[21] = new ComboBoxItemWrapper(TrackingConstant.RB_RIGHT_DEFAULT, "Right Default");
			values[22] = new ComboBoxItemWrapper(TrackingConstant.RB_LEFT_DEFAULT, "Left Default");
			return values;
		}
	}
	public static class RB_ROLEDomainProvider implements DomainProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[5];
			values[0] = new ComboBoxItemWrapper(TrackingConstant.RBROLE_POINTINGDEVICE, "Pointing Device");
			values[1] = new ComboBoxItemWrapper(TrackingConstant.RBROLE_MOBILEDISPLAY, "Mobile Display");
			values[2] = new ComboBoxItemWrapper(TrackingConstant.RBROLE_PERSON, "Tracked Person");
			values[3] = new ComboBoxItemWrapper(TrackingConstant.RBROLE_GESTURE, "Tracked Gesture");

			return values;
		}
	}	
	public static class RB_MODEDomainProvider implements DomainProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[4];
			values[0] = new ComboBoxItemWrapper(TrackingConstant.RBMODE_DIRECTPOINTING, "Direct Pointing");
			values[1] = new ComboBoxItemWrapper(TrackingConstant.RBMODE_RELATIVEPIONTING, "Relative Pointing");
			values[2] = new ComboBoxItemWrapper(TrackingConstant.RBMODE_HYBRIDPOINTING, "Hyprid Pointing");
			values[3] = new ComboBoxItemWrapper(TrackingConstant.RBMODE_NONE, "None");
			return values;
		}
	}	

	// ################################################################################
	// END OF DOMAIN PROVIDERS
	// ################################################################################
	
    private DataPosition6D rigidBody;
    private DataPosition3D rigidBody3D, additonalMarker3D;
	private MathUtility mu = new MathUtility();
    //HashMap<Integer, ArrayList> historyMap;
	private Queue<RBConnector> pointQueue;
    private int zeroCounter;
    private DataButton lastButtonReceived;
    private int currentGesture;
    private int currentMode;
    private long gestureTime;
    private long modeTime;
    private int gestureFrame;
	private int publishedGesture;
	@Override
	public void onStart()
	{
//		historyMap = new HashMap<Integer, ArrayList>();
		pointQueue = new LinkedList<RBConnector>();
		zeroCounter = 0;
		currentGesture = -1;
	}
	
    /* (non-Javadoc)
     * @see org.squidy.manager.model.AbstractNode#preProcess(org.squidy.manager.data.IDataContainer)
     */
    @Override
	public IDataContainer preProcess(IDataContainer dataContainer) 
    {
    	List<DataPosition6D> rigidBodies = DataUtility.getDataOfType(DataPosition6D.class, dataContainer);
    	if (rigidBodies.size() > 0)
    	{
    		rigidBody = rigidBodies.get(0);
    		if (!rigidBody.hasAttribute(TrackingConstant.NATNET))
    		{
	//        	if (rigidBody.hasAttribute(TrackingConstant.RIGIDBODYROLE)) 
	//        		return null;
	    		if (rigidBody.hasAttribute(DataConstant.IDENTIFIER)) 
	    		{
	    			if (rigidBodyID != (Integer.valueOf(rigidBody.getAttribute(DataConstant.IDENTIFIER).toString()))) 
	    			{
	    				return null;
	    			}
	    		} else 
	    		{
	    			return null;
	    		}	
		    	rigidBody3D = rigidBody.getClone();
			    rigidBody3D = TrackingUtility.Norm2RoomCoordinates(Optitrack.class,rigidBody3D);
				rigidBody.setAttribute(TrackingConstant.Y_DEFLECTION, this.yDeflectionAdjust);
				rigidBody.setAttribute(TrackingConstant.RIGIDBODYROLE, this.rbRole);
				rigidBody.setAttribute(TrackingConstant.RIGIDBODYID, this.rigidBodyID);
				if (this.rbMode != 0)
					rigidBody.setAttribute(TrackingConstant.POINTINGMODE, this.rbMode);
    		}
    	}
    	
    	if (this.maxExtend > 0 && rigidBody != null && !rigidBody.hasAttribute(TrackingConstant.NATNET))
    	{
			List<DataPosition3D> additionalMarker = DataUtility.getDataOfType(DataPosition3D.class, dataContainer);
			if (additionalMarker.size() > 0)
			{
				additionalMarker.remove(0);
				//System.out.println(rigidBody.getAttribute(DataConstant.IDENTIFIER).toString()+"\t"+additionalMarker.size());
				if(additionalMarker.size() > 0)
				{
					if(rigidBody.getGroupID() > 0)
					{
						int removeCounter = 0;
						for(int i = 0; i < additionalMarker.size(); i++)
						{
							additonalMarker3D = TrackingUtility.Norm2RoomCoordinates(Optitrack.class, additionalMarker.get(i).getClone());
							if(mu.euclidDist(additonalMarker3D, rigidBody3D) > this.maxExtend && this.maxExtend > 0)
							{
								additionalMarker.remove(i);
								i--;
								removeCounter++;
							}
							//insertionSort(additionalMarker);	
						}
						// sort by Y
						
					}
				// retrieve fingers
//				Collections.sort(additionalMarker, new SortByX());
//				sortAndEliminate(additionalMarker);
				}
		    	//System.out.println(rigidBody.getAttribute(DataConstant.IDENTIFIER).toString()+"\t"+additionalMarker.size());
			}
    	}
    	return dataContainer;
    }
    
    

    public DataPosition3D process(DataPosition3D d3d)
    {
    	if (d3d.hasAttribute(TrackingConstant.NATNET) && this.maxExtend > 0)
    	{
    		int currentID = TrackingUtility.getAttributesInteger(d3d, DataConstant.GROUP_ID);
//    		ArrayList currentGroup = historyMap.get(currentID);
    		ArrayList currentGroup = null;
    		for (RBConnector rbc : pointQueue)
    		{
    			if (rbc.getIndex(currentID) != null)
    			{
    				currentGroup = rbc.getIndex(currentID);
    				break;
    			}
    		}		
    		if (currentGroup != null)
    		{
    			currentGroup.add(d3d);
    			//historyMap.
    		}else
    		{
    			currentGroup = new ArrayList<IData>();
    			currentGroup.add(d3d);
    			if (pointQueue.size() > 10)
    			{
    				pointQueue.poll();
    			}
    			RBConnector rbcOut = new RBConnector(currentID, currentGroup);
    			pointQueue.offer(rbcOut);
    		}  		
    	}
    	return null;
    }
    public DataPosition6D process(DataPosition6D d6d) throws Exception
    {
    	if (d6d.hasAttribute(TrackingConstant.NATNET))
    	{
    		if (Integer.valueOf(TrackingUtility.getAttributesAlpha(d6d, DataConstant.IDENTIFIER)) == rigidBodyID)
    		{
//    			System.out.println(Integer.valueOf(TrackingUtility.getAttributesAlpha(d6d, DataConstant.IDENTIFIER)) + " " + this.rigidBodyID);
    			if (d6d.getX() == 0.0 && d6d.getY() == 0.0 && d6d.getZ() == 0.0)
    			{
    				zeroCounter++;
    				if (zeroCounter > 100)
    				{
//    					if (this.rbRole != TrackingConstant.RBROLE_GESTURE)
//    						System.err.println("RIGID BODY UNTRACKED: " + rigidBodyID);
    					//this.stop();
    				}
    				return null;
    			}else
    			{
    				zeroCounter = 0;
    			}

    			d6d.setAttribute(TrackingConstant.Y_DEFLECTION, this.yDeflectionAdjust);
    			d6d.setAttribute(TrackingConstant.RIGIDBODYROLE, this.rbRole);
    			d6d.setAttribute(TrackingConstant.RIGIDBODYID, this.rigidBodyID);
//				System.out.println(modeTime - System.currentTimeMillis());
    			
    			if (this.rbRole != TrackingConstant.RBROLE_GESTURE)
    			{
//    				System.out.println("MODE " + currentGesture);
	    			if (modeTime - System.currentTimeMillis() < -100)
						currentMode = 0;
	    			if (currentGesture >= 0)
	    			{
//	    				System.out.println("GE " + currentGesture +" " + gestureFrame + " " + TrackingUtility.getAttributesInteger(d6d, DataConstant.GROUP_ID));
	//    				System.out.println(gestureFrame - TrackingUtility.getAttributesInteger(d6d, DataConstant.GROUP_ID));
	
	    				if (gestureTime - System.currentTimeMillis() < -100)
	    				{
//	    					System.out.println("RESET G " + System.currentTimeMillis());
	    					currentGesture = TrackingConstant.GESTURE_DEFAULT;
	    				}
	    				if (currentGesture == 5500 && currentMode == 5504)
	    					publishedGesture = TrackingConstant.GESTURE_SINGLEPOINT;
	    				else if (currentGesture == 5501 && currentMode == 5504)
	    					publishedGesture = TrackingConstant.GESTURE_SINGLEPOINTCLICK;
	    				else if (currentGesture == 5501 && currentMode == 0)
	    					publishedGesture = TrackingConstant.GESTURE_CLICK;
	    				else
	    					publishedGesture = TrackingConstant.GESTURE_DEFAULT;
//	    				publishedGesture = currentGesture;
	    				d6d.setAttribute(TrackingConstant.GESTUREID, publishedGesture);
//	    				System.out.println(publishedGesture);
	    					
	//    					if (currentGesture == TrackingConstant.GESTURE_CLICK && currentMode == TrackingConstant.GESTURE_SINGLEPOINT)
	//    					{
	//    						currentGesture = TrackingConstant.GESTURE_SINGLEPOINTCLICK;
	//    					}
	//    					else if (publishedGesture == TrackingConstant.GESTURE_CLICK && currentGesture == TrackingConstant.GESTURE_SINGLEPOINT)
	//    					{
	//    						currentGesture = TrackingConstant.GESTURE_SINGLEPOINTCLICK;
	//    					} else if (publishedGesture == TrackingConstant.GESTURE_SINGLEPOINTCLICK && currentGesture == TrackingConstant.GESTURE_CLICK)
	//    					{
	//    						currentGesture = TrackingConstant.GESTURE_SINGLEPOINTCLICK;
	//    					}
	//    					System.out.println("GESTURE " + currentGesture);
	//    					if (currentGesture == 5500)
	//    						System.out.println("HEEEEEEEEEEEEEEE");
	//    					d6d.setAttribute(TrackingConstant.GESTUREID, currentGesture);
	//    					publishedGesture = currentGesture;
	//    				}
	//    			else
	//    				{
	////    					System.out.println("   " + 0);
	//    					currentGesture = 0;
	//    					publishedGesture = 0;
	////    					System.out.println("GESTURE " + currentGesture);
	//    					d6d.setAttribute(TrackingConstant.GESTUREID, 0);
	//    				}
	    			}
    			}

				if (this.rbMode != 0)
					d6d.setAttribute(TrackingConstant.POINTINGMODE, this.rbMode);
				d6d.setAttribute(TrackingConstant.HASBUTTONS, this.sendButtons);
//				d6d = TrackingUtility.Room2NormCoordinates(Optitrack.class, d6d);
    			if (this.maxExtend > 0)
	    		{
	    			int currentID = TrackingUtility.getAttributesInteger(d6d, DataConstant.GROUP_ID);
	    			ArrayList currentGroup = null;
	        		for (RBConnector rbc : pointQueue)
	        		{
	        			if (rbc.getIndex(currentID) != null)
	        			{
	        				currentGroup = rbc.getIndex(currentID);
	        				break;
	        			}
	        		}	
		    		if (currentGroup != null)
		    		{
		    			currentGroup.add(0, d6d);
//		    			System.out.println("IN  " +currentID + " " + d6d.toString());
		    		}else
		    		{
		    			currentGroup = new ArrayList<IData>();
		    			currentGroup.add(d6d);
		    			if (pointQueue.size() > 10)
		    			{
		    				pointQueue.poll();
		    			}
		    			RBConnector rbcOut = new RBConnector(currentID, currentGroup);
		    			pointQueue.offer(rbcOut);
		    		}
		    		
		    		if (currentID - 3 > 0)
		    		{
		    			RBConnector rbc = pointQueue.peek();
		    			ArrayList<IData> publishGroup = rbc.getIndex();
//				    	rigidBody3D = d6d.getClone();
		    			IData iData = publishGroup.get(0);
		    			try
		    			{
			    			DataPosition6D tmp6D = (DataPosition6D)iData;
			    			
						    rigidBody3D = TrackingUtility.Norm2RoomCoordinates(Optitrack.class,tmp6D);
						    double internalDist = 0;
							for(int i = 1; i < publishGroup.size(); i++)
							{
								if (publishGroup.get(i) != null)
								{	
									additonalMarker3D = TrackingUtility.Norm2RoomCoordinates(Optitrack.class, (DataPosition3D)publishGroup.get(i).getClone());
									double tmpDist = mu.euclidDist(additonalMarker3D, rigidBody3D);
									if(tmpDist > this.maxExtend && this.maxExtend > 0)
									{
										publishGroup.remove(i);
										i--;
									}else
									{
										internalDist += tmpDist;
									}
									additonalMarker3D = TrackingUtility.Room2NormCoordinates(Optitrack.class, additonalMarker3D);
								}
//								System.out.println(internalDist);
								//insertionSort(additionalMarker);	
							}
						
							rigidBody3D = TrackingUtility.Room2NormCoordinates(Optitrack.class, rigidBody3D);
			    			publish(publishGroup);
		    			
//		    				System.out.println(currentID + " " + publishGroup.size());
	//	    				System.out.println("OUT " + (currentID-3) + " " + publishGroup.get(0).toString());
		    			}catch (Exception ex)
		    			{
		    				
		    			}
		    		}
	    		}
	    		else
	    		{
	    			if (this.rbRole == TrackingConstant.RBROLE_GESTURE)
					{
//						System.out.println(rigidBodyID);
//						if (this.rigidBodyID == TrackingConstant.RB_RIGHT_GRAB)
//						{
//							d6d.setAttribute(TrackingConstant.GESTUREID, TrackingConstant.GESTURE_GRAB);
//							return d6d;	
//						}		
						if (this.rigidBodyID == TrackingConstant.RB_RIGHT_DEFAULT|| (this.rigidBodyID == TrackingConstant.RB_LEFT_DEFAULT))
						{
							d6d.setAttribute(TrackingConstant.GESTUREID, TrackingConstant.GESTURE_DEFAULT);
							return d6d;	
						}	    				
						if (this.rigidBodyID == TrackingConstant.RB_RIGHT_POINT || (this.rigidBodyID == TrackingConstant.RB_RIGHT_POINT2))
						{
							d6d.setAttribute(TrackingConstant.GESTUREID, TrackingConstant.GESTURE_SINGLEPOINT);
							return d6d;	
						}	
						if (this.rigidBodyID == TrackingConstant.RB_LEFT_CLICK || this.rigidBodyID == TrackingConstant.RB_LEFT_CLICK2)
						{
							d6d.setAttribute(TrackingConstant.GESTUREID, TrackingConstant.GESTURE_CLICK);
							return d6d;	
						}	
						if (this.rigidBodyID == TrackingConstant.RB_RIGHT_CLICK || this.rigidBodyID == TrackingConstant.RB_RIGHT_CLICK2)
						{
							d6d.setAttribute(TrackingConstant.GESTUREID, TrackingConstant.GESTURE_CLICK);
							return d6d;	
						}							
						if (this.rigidBodyID == TrackingConstant.RB_LEFT_POINT || this.rigidBodyID == TrackingConstant.RB_LEFT_POINT2)
						{
							d6d.setAttribute(TrackingConstant.GESTUREID, TrackingConstant.GESTURE_SINGLEPOINT);
							return d6d;	
						}
//						if (this.rigidBodyID == TrackingConstant.RB_LEFT_GRAB)
//						{
//							d6d.setAttribute(TrackingConstant.GESTUREID, TrackingConstant.GESTURE_GRAB);
//							return d6d;	
//						}						
//						d6d.setAttribute(TrackingConstant.GESTUREID, TrackingConstant.GESTURE_DEFAULT);
//						return d6d;
					}
	    			return d6d;
	    		}
    		}
    		else
    		{
    			if (d6d.hasAttribute(TrackingConstant.GESTUREID) && (TrackingUtility.getAttributesInteger(d6d, DataConstant.IDENTIFIER) / 10) == rigidBodyID)
    			{
    				currentGesture = TrackingUtility.getAttributesInteger(d6d, TrackingConstant.GESTUREID);
//    				System.out.println(currentGesture);
    				if (currentGesture < 5000)
    					currentGesture += 5500;
//    				System.out.println("currentGesture " + currentGesture);
    				if (currentGesture != TrackingConstant.GESTURE_DEFAULT)
    				{
//    					System.out.println("RB " + TrackingUtility.getAttributesAlpha(d6d, DataConstant.IDENTIFIER) + " "  + currentGesture + "\t" + (gestureFrame-TrackingUtility.getAttributesInteger(d6d, DataConstant.GROUP_ID))+ " " +TrackingUtility.getAttributesInteger(d6d, DataConstant.GROUP_ID));
    					gestureFrame = TrackingUtility.getAttributesInteger(d6d, DataConstant.GROUP_ID);
    					gestureTime = System.currentTimeMillis();
    				}
    			}
    			else if (d6d.hasAttribute(TrackingConstant.GESTUREID) && (TrackingUtility.getAttributesInteger(d6d, DataConstant.IDENTIFIER) / 100) == rigidBodyID)
    			{
    				
    				int tmpG = TrackingUtility.getAttributesInteger(d6d, TrackingConstant.GESTUREID);
    				
    				if(tmpG == TrackingConstant.GESTURE_CLICK)
    				{
    					currentGesture = tmpG;
    					gestureFrame = TrackingUtility.getAttributesInteger(d6d, DataConstant.GROUP_ID);
    					gestureTime = System.currentTimeMillis();
//    					System.out.println("currentGesture " + currentGesture + " " + tmpG);
    				}
    				else if(tmpG == TrackingConstant.GESTURE_DEFAULT)
    				{
    					currentGesture = tmpG;
    				}
    				else if(tmpG == TrackingConstant.GESTURE_SINGLEPOINT)
    				{
    					currentMode = tmpG;
    					modeTime = System.currentTimeMillis();
    				}
    			}
    		}
    	}
    	return null;
    }

    
	/**
	 * sortAndEliminate
	 * @param list ArrayList with 3D Positions 
	 * eliminates non-finger markers
	 */
    private void sortAndEliminate(List<DataPosition3D> list)
    {
		Collections.sort(list,new SortByY());
		if (list.size() >= 4)
		{
			for(int i = 4; i < list.size(); i++)
			{
				list.remove(i);
				i--;
			}
		}
		Collections.sort(list, new SortByX());
    }
    
	/**
	 * insertionSort
	 * @param list ArrayList with 3D Positions 
	 */
	private void insertionSort(List<DataPosition3D> list) 
	{
	    int firstOutOfOrder, location;
	    DataPosition3D temp;
	    for(firstOutOfOrder = 1; firstOutOfOrder < list.size(); firstOutOfOrder++) 
	    { //Starts at second term, goes until the end of the array.
	    	
	        if(list.get(firstOutOfOrder).getY() < list.get(firstOutOfOrder - 1).getY()) { //If the two are out of order, we move the element to its rightful place.
	            temp = list.get(firstOutOfOrder);
	            location = firstOutOfOrder;
	            do { //Keep moving down the array until we find exactly where it's supposed to go.
	                list.set(location,list.get(location-1));
	                location--;
	            }
	            while (location > 0 && list.get(location-1).getY() > temp.getY());
	            list.set(location,temp);
	        }
	    }
	} 
	/**
	 * 
	 * @param dataDigital
	 * @return
	 */
    public DataDigital process(DataDigital dataDigital) 
    {
    	if (dataDigital.hasAttribute(Keyboard.KEY_EVENT))
    	{
        	Integer key_event = (Integer) dataDigital.getAttribute(Keyboard.KEY_EVENT);
        	if (dataDigital.getFlag())
        	{
	        	if (KeyStroke.getKeyStroke("D") == KeyStroke.getKeyStroke(key_event.intValue(),0))
	    		{
        			this.setRbMode(TrackingConstant.RBMODE_DIRECTPOINTING);
        			return null;
        		}
	        	if (KeyStroke.getKeyStroke("H") == KeyStroke.getKeyStroke(key_event.intValue(),0))
	    		{
        			this.setRbMode(TrackingConstant.RBMODE_HYBRIDPOINTING);
        			return null;
        		}
	        	if (KeyStroke.getKeyStroke("R") == KeyStroke.getKeyStroke(key_event.intValue(),0))
	    		{
        			this.setRbMode(TrackingConstant.RBMODE_RELATIVEPIONTING);
        			return null;
        		}	        	
    		}
    	}
    	return dataDigital;
    }
	/**
	 * @author Simon Fäh
	 * comparator to sort markerList by X-Coord
	 */
	private class SortByX implements Comparator<DataPosition3D>
	{
		public int compare(DataPosition3D d3d1, DataPosition3D d3d2)
		{
			if (d3d1.getX() == d3d2.getX())
				return 0;
			else if (d3d1.getX() < d3d2.getX())
				return -1;
			else 
				return 1;
		}
	}
	private class SortByY implements Comparator<DataPosition3D>
	{
		public int compare(DataPosition3D d3d1, DataPosition3D d3d2)
		{
			if (d3d1.getY() == d3d2.getY())
				return 0;
			else if (d3d1.getY() < d3d2.getY())
				return -1;
			else 
				return 1;
		}
	}	
}

