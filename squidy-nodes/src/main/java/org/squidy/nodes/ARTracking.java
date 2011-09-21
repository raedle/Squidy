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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Throughput;
import org.squidy.manager.data.impl.DataObject;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.artracking.ARUDPServer;


/**
 * <code>ARTracking</code>.
 * 
 * <pre>
 * Date: Jun 27, 2008
 * Time: 7:33:04 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: ARTracking.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "ARTracking")
@Processor(
		name = "ARTracking",
		types = { Processor.Type.INPUT },
		icon = "/org/squidy/nodes/image/48x48/joystick.png",
		description = "/org/squidy/nodes/html/ARTracking.html",
		tags = {"A.R.T", "tracking", "6DOF"}
)
public class ARTracking extends AbstractNode {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ARTracking.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "udp-port")
	@Property(
		name = "UDP port",
		description = "The ART UPD port"
	)
	@TextField
	private int updPort = 5000;

	/**
	 * @return the updPort
	 */
	public int getUpdPort() {
		return updPort;
	}

	/**
	 * @param updPort
	 *            the updPort to set
	 */
	public void setUpdPort(int updPort) {
		stop();
		this.updPort = updPort;
		start();
	}

	@XmlAttribute(name = "width")
	@Property(
		name = "Width",
		description = "AR Tracking width."
	)
	@TextField
	private float width = 5181;

	/**
	 * @return the width
	 */
	public final float getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public final void setWidth(float width) {
		this.width = width;
	}

	@XmlAttribute(name = "height")
	@Property(
		name = "Height",
		description = "AR Tracking height."
	)
	@TextField
	private float height = 2126;

	/**
	 * @return the height
	 */
	public final float getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public final void setHeight(float height) {
		this.height = height;
	}

	@XmlAttribute(name = "depth")
	@Property(
		name = "Depth",
		description = "AR Tracking depth."
	)
	@TextField
	private float depth = 5000;

	/**
	 * @return the depth
	 */
	public final float getDepth() {
		return depth;
	}

	/**
	 * @param depth
	 *            the depth to set
	 */
	public final void setDepth(float depth) {
		this.depth = depth;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private ARUDPServer arUDPServer;

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	public final void onStart() {
		arUDPServer = new ARUDPServer(this, updPort);
		arUDPServer.refreshDriver();
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	public final void onStop() {
		if (arUDPServer != null) {
			arUDPServer.close();
		}
	}
}
