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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Throughput;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataKey;
import org.squidy.manager.data.impl.DataObject;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>DigitalChanged</code>.
 *
 * <pre>
 * Date: Feb 12, 2008
 * Time: 1:36:42 AM
 * </pre>
 *
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 */
@XmlType(name = "FilterArtifacts")
@Processor(
	name = "Filter Artifacts",
	icon = "/org/squidy/nodes/image/48x48/filterartifacts.png",
	types = { Processor.Type.FILTER },
	tags = { "pos", "touch", "artifacts", "filterArtifacts" },
	status = Status.UNSTABLE
)
public class FilterArtifacts extends AbstractNode {

	private Map<Integer, Integer> recentFlags = new HashMap<Integer, Integer>();

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "min-occurence")
	@Property(
		name = "Minimum number of occurence",
		description = "Minimum number of occurence of a position (identified by session_id) before it is routed to the output pin."
	)
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 30, showLabels = true, showTicks = true, majorTicks = 5, minorTicks = 1, snapToTicks = false)

	private int minOccurence = 3;

	/**
	 * @return the minOccurence
	 */
	public final int getMinOccurence() {
		return minOccurence;
	}

	/**
	 * @param minOccurence
	 *            the minOccurence to set
	 */
	public final void setMinOccurence(int minOccurence) {
		this.minOccurence = minOccurence;
	}


	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	/**
	 * @param dataPos2D
	 * @return
	 */
	public synchronized IData process(DataPosition2D dataPos2D) {

		int curr_id = (Integer) dataPos2D.getAttribute(DataConstant.SESSION_ID);

		// Initialized recent flags map.
		Object value = recentFlags.get(curr_id);
		if (value == null) {
			recentFlags.put(curr_id, 0);
			return null;
		}else{

			Integer oldValue = (Integer) value;
			recentFlags.put(curr_id, oldValue+1);

			if (oldValue<minOccurence) {
				return null;
			} else {
				return dataPos2D;
			}
		}
	}
}
