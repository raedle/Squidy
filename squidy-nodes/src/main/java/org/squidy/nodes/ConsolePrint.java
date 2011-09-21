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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Throughput;
import org.squidy.manager.data.impl.DataObject;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>Print</code>.
 * 
 * <pre>
 * Date: Feb 12, 2008
 * Time: 1:34:36 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ConsolePrint.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "ConsolePrint")
@Processor(
	name = "Console Print",
	icon = "/org/squidy/nodes/image/48x48/print.png",
	description = "/org/squidy/nodes/html/ConsolePrint.html",
	types = { Processor.Type.OUTPUT, Processor.Type.FILTER},
	tags = { "print", "console", "System.out" ,"debug"}
)
public class ConsolePrint extends AbstractNode {
	
	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ConsolePrint.class);
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	/**
	 * {@inheritDoc}
	 */
	public IData process(IData data) {
		if (LOG.isInfoEnabled()) {
			LOG.info("[long timestamp=" + data.getTimestamp() + "]" + data);
		}else{
			System.out.println("[long timestamp=" + data.getTimestamp() + "]" + data);
		}
		return null;
	}
}
