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

import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>FlipVertical2D</code>.
 * 
 * <pre>
 * Date: Feb 12, 2008
 * Time: 1:40:48 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: FlipVertical2D.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "FlipVertical2D")
@Processor(
	name = "Flip Vertical 2D",
	icon = "/org/squidy/nodes/image/48x48/flipvertical.png",
	description = "/org/squidy/nodes/html/FlipVertical2D.html",
	types = { Processor.Type.FILTER },
	tags = { "flip", "vertical" }
)
public class FlipVertical2D extends AbstractNode {

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@Property(name = "Minimum X value")
	@TextField
	private double minXValue = 0.0;

	public double getMinXValue() {
		return minXValue;
	}

	public void setMinXValue(double minXValue) {
		this.minXValue = minXValue;
	}

	@Property(name = "Maximum X value")
	@TextField
	private double maxXValue = 1.0;

	public double getMaxXValue() {
		return maxXValue;
	}

	public void setMaxXValue(double maxXValue) {
		this.maxXValue = maxXValue;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	/**
	 * @param dataPosition2D
	 * @return
	 */
	public IData process(DataPosition2D dataPosition2D) {
		dataPosition2D.setX(maxXValue - dataPosition2D.getX() + minXValue);
//		dataPosition2D.setX(1.0f - dataPosition2D.getX());
		return dataPosition2D;
	}
}
