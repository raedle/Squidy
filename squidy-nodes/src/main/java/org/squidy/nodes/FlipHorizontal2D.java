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

import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Throughput;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataObject;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>FlipHorizontal2D</code>.
 * 
 * <pre>
 * Date: Feb 12, 2008
 * Time: 1:35:05 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: FlipHorizontal2D.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "FlipHorizontal2D")
@Processor(
	name = "Flip Horizontal 2D",
	icon = "/org/squidy/nodes/image/48x48/fliphorizontal.png",
	description = "/org/squidy/nodes/html/FlipHorizontal2D.html",
	types = { Processor.Type.FILTER },
	tags = { "flip", "horizontal" }
)
public class FlipHorizontal2D extends AbstractNode {

	/**
	 * Flips the data position horizontal by its x-axis.
	 * 
	 * @param dataPosition2D The data position 2d that gets flipped.
	 * @return The flipped data position 2d value.
	 */
	public IData process(DataPosition2D dataPosition2D) {
		dataPosition2D.setY(1.0f - dataPosition2D.getY());
		return dataPosition2D;
	}
	
	public IData process(DataDigital dataDigial) {
		return dataDigial;
	}
}
