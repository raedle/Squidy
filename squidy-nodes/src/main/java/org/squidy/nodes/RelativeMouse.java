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

import java.awt.Robot;
import java.awt.event.KeyEvent;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataKey;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;

/**
 * <code>RelativeMouseIO</code>.
 * 
 * <pre>
 * Date: Aug 03, 2012
 * Time: 1:35:05 AM
 * </pre>
 * 
 * @author Roman Rädle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a>, University of Konstanz
 * @version $Id$
 */
@XmlType(name = "RelativeMouse")
@Processor(name = "Relative Mouse", types = { Processor.Type.FILTER }, tags = {
		"mouse", "relative" })
public class RelativeMouse extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################

	@XmlAttribute(name = "X-Resolution")
	@Property(name = "X-Resolution", description = "X-Resolution (in pixel) of the screen")
	@TextField
	private int resolutionX = 1920;

	public int getResolutionX() {
		return resolutionX;
	}

	public void setResolutionX(int resolutionX) {
		this.resolutionX = resolutionX;
	}

	@XmlAttribute(name = "Y-Resolution")
	@Property(name = "Y-Resolution", description = "Y-Resolution (in pixel) of the screen")
	@TextField
	private int resolutionY = 1200;

	public int getResolutionY() {
		return resolutionY;
	}

	public void setResolutionY(int resolutionY) {
		this.resolutionY = resolutionY;
	}

	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	private int x = 0;
	private int y = 0;

	/**
	 * 
	 */
	public IData process(DataDigital dataDigital) {

		if (dataDigital.hasAttribute(Keyboard.KEY_EVENT)) {
			int key = (int) dataDigital.getAttribute(Keyboard.KEY_EVENT);

			switch (key) {
			case KeyEvent.VK_LEFT:
				if (x >= 0) x--;
				break;
			case KeyEvent.VK_RIGHT:
				if (x <= resolutionX) x++;
				break;
			case KeyEvent.VK_UP:
				if (y >= 0) y--;
				break;
			case KeyEvent.VK_DOWN:
				if (y <= resolutionY) y++;
				break;
			}
			
			return new DataPosition2D(RelativeMouse.class, ((double)x / (double)resolutionX), ((double)y / (double)resolutionY));
		}

		return null;
	}
}
