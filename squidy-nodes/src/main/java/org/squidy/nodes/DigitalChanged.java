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

import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Throughput;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataKey;
import org.squidy.manager.data.impl.DataObject;
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
 * @version $Id: DigitalChanged.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "DigitalChanged")
@Processor(
		name = "Digital Changed",
		icon = "/org/squidy/nodes/image/48x48/digitalchanged.png",
		description = "/org/squidy/nodes/html/DigitalChanged.html",
		types = { Processor.Type.FILTER },
		tags = { "digital", "change" }
	)
public class DigitalChanged extends AbstractNode {
	
	private Map<String, Boolean> recentFlags = new HashMap<String, Boolean>();

	/**
	 * @param dataDigital
	 * @return
	 */
	public synchronized IData process(DataDigital dataDigital) {
		return processDigital(dataDigital, "" + IData.Type.DIGITAL);
	}

	/**
	 * @param dataButton
	 * @return
	 */
	public synchronized IData process(DataButton dataButton) {
		return processDigital(dataButton, "" + IData.Type.BUTTON + dataButton.getButtonType());
	}

	/**
	 * @param data
	 * @param identifier
	 * @return
	 */
	private IData processDigital(IData data, String identifier) {
		DataDigital dataDigital = (DataDigital) data;

		// Initialized recent flags map.
		Object value = recentFlags.get(identifier);
		if (value == null) {
			recentFlags.put(identifier, dataDigital.getFlag());
			return dataDigital;
		}

		Boolean oldValue = (Boolean) value;

		if (dataDigital.getFlag() == oldValue) {
			return null;
		} else {
			recentFlags.put(identifier, dataDigital.getFlag());
		}
		return dataDigital;
	}
}
