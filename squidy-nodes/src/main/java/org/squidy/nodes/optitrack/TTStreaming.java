/**
 * Squidy Interaction Library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Squidy Interaction Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Squidy Interaction Library. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * 2009 Human-Computer Interaction Group, University of Konstanz.
 * <http://hci.uni-konstanz.de>
 * 
 * Please contact info@squidy-lib.de or visit our website
 * <http://www.squidy-lib.de> for further information.
 */

package org.squidy.nodes.optitrack;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.*;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.MouseIO;
import org.squidy.nodes.optitrack.RoomObject.RBIDDomainProvider;
import org.squidy.nodes.optitrack.cameraInterface.NatNetDotNet;
import org.squidy.nodes.optitrack.cameraInterface.NatNetJNI;
import org.squidy.nodes.optitrack.cameraInterface.NatNetWindow;
import org.squidy.nodes.optitrack.utils.TrackingUtility;


/*<code>Optitrack</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AM
* </pre>
* 
* @author Simon Faeh, <a href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de<a/>, University of Konstanz
* @version 27.10.2010 / sf 
*/
@XmlType(name = "TTStreaming")
@Processor(
	name = "NatNetStreaming",
	icon = "/org/squidy/nodes/image/48x48/optitrack48.png",
	description = "Camera interaface for Optitrack Trackingsystem",
	types = {Processor.Type.OUTPUT},
	tags = { "optitrack", "camera control", "trackingtool" },
	status = Status.UNSTABLE
)

public class TTStreaming extends AbstractNode {

	static{
		System.loadLibrary("/ext/optitrack/oojnidotnet");
		System.loadLibrary("/ext/optitrack/NatNetStreaming");
	}
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	@XmlAttribute(name = "room-dimensions")
	@Property(
		name = "Dimension of the tracked Area",
		description = "Set the Dimension of the tracked Area in mm (x,y,z)"
	)
	@TextField
	private String roomDimension = "6000,3000,6000";
	
	/**
	 * @return roomDimension
	 */
	public final String getRoomDimension() {
		return roomDimension;
	}

	/**
	 * @param roomDimesiom [x,y,z]
	 */
	public final void setRoomDimension(String aRoomDimension) {
		this.roomDimension = aRoomDimension;
		String dimensionChunks[] = roomDimension.split(",");
		maxX = Double.parseDouble(dimensionChunks[0]);
		maxY = Double.parseDouble(dimensionChunks[1]);
		maxZ = Double.parseDouble(dimensionChunks[2]);
		dimensions = new Point3d(maxX,maxY,maxZ);
		
	}

	// ################################################################################
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	
	
	
	public TTStreaming tts;
	public double maxX, maxY, maxZ;
	private boolean started = false;
	private NatNetWindow natNetWindow;
	private Point3d dimensions;
	@Override
	public void onStart() {
		natNetWindow = new NatNetWindow(this, dimensions);
		natNetWindow.setVisible(true);
		started = true;
	}	
	
	public void publish3d(DataPosition3D d3d, int frameID)
	{
		d3d.setAttribute(DataConstant.GROUP_ID, frameID);
		d3d.setAttribute(DataConstant.GROUP_DESCRIPTION, "SINGLEMARKER");
		d3d.setAttribute(DataConstant.MAX_X, maxX);
		d3d.setAttribute(DataConstant.MAX_Y, maxY);
		d3d.setAttribute(DataConstant.MAX_Z, maxZ);
		d3d = TrackingUtility.Room2NormCoordinates(MouseIO.class, d3d);
		publish(d3d);
	}
	
	@Override
	public void onStop() {
		natNetWindow.stop();
	}	
}
