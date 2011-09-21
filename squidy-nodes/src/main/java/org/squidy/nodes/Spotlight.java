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

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.designer.component.TransparentWindow;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.MouseIO;



/*<code>Spotlight</code>.
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
@XmlType(name = "Spotlight")
@Processor(
	name = "Spotlight TT",
	icon = "/org/squidy/nodes/image/48x48/optitrack.png",
	description = "Optitrack using TrackingTool Toolkit",
	types = {Processor.Type.OUTPUT},
	tags = { "optitrack", "handtracking", "trackingtool" }
)

public class Spotlight extends AbstractNode {
 
	private SpotlightWindow sWindow;
	
	@Override
	public void onStart() {
		sWindow = new SpotlightWindow();
		
	}

	@Override
	public void onStop() {
		
	}
	
	public IData process(DataPosition2D dataPosition2D) {
		
		
		return dataPosition2D;
	}
	
	
	public class SpotlightWindow extends TransparentWindow {
		
		private static final int SIZE = 1000;
		
		public SpotlightWindow() {
			super(SIZE, SIZE);
			
			setAlwaysOnTop(true);
			centerToPointer();
			
			disposeOnMouseExit(true);
		}
	}
	
}


