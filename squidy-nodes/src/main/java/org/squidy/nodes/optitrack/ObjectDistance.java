package org.squidy.nodes.optitrack;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Track;
import javax.vecmath.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.designer.model.Data;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.optitrack.utils.TrackingConstant;
import org.squidy.nodes.optitrack.utils.TrackingUtility;


/*<code>RigidBody</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AMd
* </pre>
* 
* @author Simon Faeh, <a href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de<a/>, University of Konstanz
* 
* @version 
*/
@XmlType(name = "ObjectDistance")
@Processor(
	name = "Object-Distance",
	icon = "/org/squidy/nodes/image/48x48/objectdistance.png",
	description = "",
	types = {Processor.Type.OUTPUT, Processor.Type.INPUT },
	tags = { "distance", "object", "optitrack", "handtracking", "interception" }
)

public class ObjectDistance extends AbstractNode {
	
	
	@XmlAttribute(name = "maxRange")
	@Property(name = "Maximal Range", description = "Specify the maximum distance to other trackale (in mm)")
	@TextField
	private Double maxRange = 1000.0;

	/**
	 * @return the maxRange
	 */
	public final Double getMaxRange() {
		return maxRange;
	}

	/**
	 * @param maxRange
	 *            the maxRange to set
	 */
	public final void setMaxRange(Double maxRange) {
		this.maxRange = maxRange;
	}

	// ################################################################################
	
	@XmlAttribute(name = "maxRotation")
	@Property(name = "Maximal Rotation", description = "Specify the maximum rotation to other trackable (in degrees)")
	@TextField
	private int maxRotation = 0;

	/**
	 * @return the maxRotation
	 */
	public final int getMaxRotation() {
		return maxRotation;
	}

	/**
	 * @param maxRotation
	 *            the maxRotation to set
	 */
	public final void setMaxRotation(int maxRotation) {
		this.maxRotation = maxRotation;
	}

	// ################################################################################	
	
	@XmlAttribute(name = "onlyRoomObjects")
	@Property(name = "Publish only room objects", description = "Onldy Rigidbody with the attribute roomobject will be processed")
	@CheckBox
	private boolean onlyRoomObjects =  false;

	/**
	 * @return the waitForAlarm
	 */
	public final boolean getOnlyRoomObjects() {
		return onlyRoomObjects;
	}

	/**
	 * @param waitForAlarm
	 *            the waitForAlarm to set
	 */
	public final void setOnlyRoomObjects(boolean onlyRoomObjects) {
		this.onlyRoomObjects = onlyRoomObjects;
	}
	// ################################################################################	
	
	@XmlAttribute(name = "twoDDistance")
	@Property(name = "Use 2D distance", description = "Calculates distance form projection view")
	@CheckBox
	private boolean twoDDistance =  false;

	/**
	 * @return the waitForAlarm
	 */
	public final boolean getTwoDDistance() {
		return twoDDistance;
	}

	/**
	 * @param waitForAlarm
	 *            the waitForAlarm to set
	 */
	public final void setTwoDDistance(boolean twoDDistance) {
		this.twoDDistance = twoDDistance;
	}	
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	
	

	private ArrayList<DataPosition6D> frameObjects,roomObjects; 
	private int currentFrame;
	private MathUtility mu = new MathUtility();
	
	@Override
	public void onStart() {
		frameObjects = new ArrayList<DataPosition6D>();
		roomObjects = new ArrayList<DataPosition6D>();
	}
	
    /* (non-Javadoc)
     * @see org.squidy.manager.model.AbstractNode#preProcess(org.squidy.manager.data.IDataContainer)
     */
	public IData process(DataPosition6D d6d) 
    {
		if (d6d.getGroupID() != this.currentFrame)
		{
			this.currentFrame = d6d.getGroupID();
			
			for (DataPosition6D d6d1 : roomObjects)
			{
				Vector3d v3d1 = new Vector3d(d6d1.getX(),d6d1.getY(),d6d1.getZ());
				for (DataPosition6D d6d2 : frameObjects)
				{
					Vector3d v3d2 = new Vector3d(d6d2.getX(),d6d2.getY(),d6d2.getZ());
					v3d2.sub(v3d1);
					double dist;
					if (this.twoDDistance)
						dist = mu.euclidDist2D(TrackingUtility.Norm2RoomCoordinates(Optitrack.class, d6d2),TrackingUtility.Norm2RoomCoordinates(Optitrack.class, d6d1));
					else
						dist = mu.euclidDist(TrackingUtility.Norm2RoomCoordinates(Optitrack.class, d6d2),TrackingUtility.Norm2RoomCoordinates(Optitrack.class, d6d1));
					
					if (dist < this.maxRange)
					{
						dist /= 2*this.maxRange;
						
						//System.out.println(0.5 + dist * v3d2.x);
						v3d2.normalize();
						v3d2.x = 0.5 + dist * v3d2.x;
						v3d2.z = 0.5 + dist * v3d2.z;
						//v3d2.normalize();
						DataPosition2D d2d = new DataPosition2D(Optitrack.class,v3d2.x,v3d2.z);
						d2d.setAttribute(DataConstant.IDENTIFIER,TrackingUtility.getAttributesAlpha(d6d1, DataConstant.IDENTIFIER));
						d2d.setAttribute(TrackingConstant.REMOTEOBJECT,TrackingUtility.getAttributesAlpha(d6d2, DataConstant.IDENTIFIER));
						d2d.setAttribute(TrackingConstant.MAXRANGE, this.maxRange);	
						publish(d2d);
					}					
				}
			}
		    frameObjects.removeAll(frameObjects);
		    roomObjects.removeAll(roomObjects);
			if (TrackingUtility.getAttributesInteger(d6d, TrackingConstant.RIGIDBODYROLE) == TrackingConstant.RBROLE_MOBILEDISPLAY)
				roomObjects.add(d6d);
			else
				frameObjects.add(d6d);
		}else
		{
			if (TrackingUtility.getAttributesInteger(d6d, TrackingConstant.RIGIDBODYROLE) == TrackingConstant.RBROLE_MOBILEDISPLAY)
				roomObjects.add(d6d);
			else
				frameObjects.add(d6d);
		}
		return null;
    }
}

