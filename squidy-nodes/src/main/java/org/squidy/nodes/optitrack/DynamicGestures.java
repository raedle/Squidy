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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.MouseIO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.vecmath.*;


/*<code>Optitrack</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AM
* </pre>
* 
* @author Simon Faeh, <a href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de<a/>, University of Konstanz
* 
* @version 
*/
@XmlType(name = "DynamicGestures")
@Processor(
	name = "Dynamic Gestures",
	icon = "/org/squidy/nodes/image/48x48/optitrack.png",
	description = "Recognizes Static Handgestures",
	types = {Processor.Type.FILTER},
	tags = { "gesture", "handtracking", "trackingtool" }
)

public class DynamicGestures extends AbstractNode {
		

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "gesture1")
	@Property(
		name = "Gestrue Definition File",
		description = "Path to the gesture Definition File (*.xml)"
	)
	@TextField
	private String gestureFile = "D:\\Development\\Optitrack\\TrackingToolProjects\\StaticGestures.xml";
	
	/**
	 * @return the multicastGroupAddress
	 */
	public final String getGestureFile() {
		return gestureFile;
	}

	/**
	 * @param multicastGroupAddress the multicastGroupAddress to set
	 */
	public final void setGestureFile(String aGestureFile) {
		this.gestureFile = aGestureFile;
	}

	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################
	
	
    public IData process(DataPosition6D dataPosition6d) {
    	//d3d =  TrackingUtility.Norm2RoomCoordinates(Optitrack.class, dataPosition6d);
    	return new DataInertial(Optitrack.class, (float)dataPosition6d.getX(), (float)dataPosition6d.getY(), (float)dataPosition6d.getZ());
    }
	
}