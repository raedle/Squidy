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


package org.squidy.nodes;

import javax.sound.midi.Track;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.optitrack.utils.TrackingConstant;



/**
 * <code>TuioOrMouse</code>.
 * 
 * <pre>
 * Date: Feb 12, 2008
 * Time: 1:42:21 AM
 * </pre>
 * 
 * @author Simon Fäh, simon.faeh@uni-konstanz.de, University of Konstanz

 */
@XmlType(name = "TuioOrMouse")
@Processor(
	name = "TUIO / Mouse",
	icon = "/org/squidy/nodes/image/48x48/tuioOrMouse.png",
	types = { Processor.Type.FILTER },
	tags = { "tuio", "mouse" }
)
public class TuioOrMouse extends AbstractNode {

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	@XmlAttribute(name = "blockTUIO")
	@Property(name = "Blocks TUIO Data", description = "Blocking 2D-Data with attribute TUIOID")
	@CheckBox
	private boolean blockTUIO = false;

	public boolean getBlockTUIO() {
		return blockTUIO;
	}

	public void setBlockTUIO(boolean blockTUIO) {
		this.blockTUIO = blockTUIO;
	}	
	// ################################################################################
	
	@XmlAttribute(name = "blockMouse")
	@Property(name = "Blocks Mouse-Data", description = "Blocking 2-Data withouth TUIOID")
	@CheckBox
	private boolean blockMouse = false;

	public boolean getBlockMouse() {
		return blockMouse;
	}

	public void setBlockMouse(boolean blockMouse) {
		this.blockMouse = blockMouse;
	}	

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	
	
	/**
	 * @param dataPosition2D
	 * @return
	 */
	public IData process(DataPosition2D dataPosition2D) {
			
		if (dataPosition2D.hasAttribute(TrackingConstant.TUIOID))
		{
			if (this.blockTUIO)
				return null;
			else 
				return dataPosition2D;
		}else
		{
			if (this.blockMouse)
				return null;
			else
				return dataPosition2D;
			
		}
	}
	public IData process(DataDigital dataDigital)
	{
		if (dataDigital.hasAttribute(TrackingConstant.TUIOID))
		{
			if (this.blockTUIO)
				return null;
			else
				return dataDigital;
		}
		else
		{
			if (this.blockMouse)
				return null;
			else
				return dataDigital;
		}
	}
	
	public IData process (DataButton dataButton)
	{
		if (dataButton.hasAttribute(TrackingConstant.TUIOID))
		{
			if (this.blockTUIO)
				return null;
			else
				return dataButton;
		}
		else
		{
			if (this.blockMouse)
				return null;
			else
				return dataButton;
		}
	}
}
