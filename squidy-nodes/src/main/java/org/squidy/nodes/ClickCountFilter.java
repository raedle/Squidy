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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.model.AbstractNode;

/**
 * <code>ClickCountFilter</code>.
 * 
 * <pre>
 * Date: Aug 03, 2012
 * Time: 1:30:05 AM
 * </pre>
 * 
 * @author Roman Rädle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 *         @uni-konstanz.de</a>, University of Konstanz
 * @version $Id$
 */
@XmlType(name = "ClickCountFilter")
@Processor(name = "Click Count Filter", types = { Processor.Type.FILTER }, tags = {
		"count", "click" })
public class ClickCountFilter extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################

	@XmlAttribute(name = "Click-Count")
	@Property(name = "Click-Count", description = "Number of clicks.")
	@TextField
	private int clickCount = 1;

	public int getClickCount() {
		return clickCount;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	/**
	 * 
	 */
	public IData process(DataButton dataButton) {

		if (dataButton.hasAttribute(MouseIO.CLICK_COUNT)) {
			int value = (int) dataButton.getAttribute(MouseIO.CLICK_COUNT);

			if (value == clickCount)
				return dataButton;

			return null;
		}
		return dataButton;
	}
}
