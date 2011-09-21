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
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataAnalog;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.protocol.udp.UDPAdapter;
import org.squidy.manager.protocol.udp.UDPServer;


/**
 * <code>WiiMote</code>.
 * 
 * <pre>
 * Date: Feb 13, 2008
 * Time: 12:49:13 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: WiimoteRemote.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "Wiimote Remote")
@Processor(
	name = "Wiimote Remote",
	types = { Processor.Type.INPUT },
	description = "/org/squidy/nodes/html/WiimoteRemote.html",
	tags = { "wiimote", "nintendo", "wii", "tracking", "ir", "infrared", "camera" },
	status = Status.UNSTABLE
)
public class WiimoteRemote extends AbstractNode {

	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(WiimoteRemote.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "port")
	@Property(
		name = "Port",
		description = "UDP Server Port"
	)
	@TextField
	private int port = 13007;

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	protected UDPServer server;
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Initializing Wiimote.");
		}

		server = new UDPServer(port);
		server.addUDPListener(new UDPAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.squidy.manager.udp.UDPListener#parseData(java.lang.String)
			 */
			public void parseData(String s) {
				
				s = s.replace(',', '.');
				String[] values = s.split("\\|");

//				String timestamp = values[0];
//				int accelCalibX0 = Integer.parseInt(values[1]);
//				int accelCalibY0 = Integer.parseInt(values[2]);
//				int accelCalibZ0 = Integer.parseInt(values[3]);
//				int accelCalibXG = Integer.parseInt(values[4]);
//				int accelCalibYG = Integer.parseInt(values[5]);
//				int accelCalibZG = Integer.parseInt(values[6]);
//				int accelStateRawX = Integer.parseInt(values[7]);
//				int accelStateRawY = Integer.parseInt(values[8]);
//				int accelStateRawZ = Integer.parseInt(values[9]);
				double accelStateX = Double.parseDouble(values[10]);
				double accelStateY = Double.parseDouble(values[11]);
				double accelStateZ = Double.parseDouble(values[12]);
//				int battery = Integer.parseInt(values[13]);
				boolean buttonA = Boolean.parseBoolean(values[14]);
				boolean buttonB = Boolean.parseBoolean(values[15]);
				boolean buttonUp = Boolean.parseBoolean(values[16]);
				boolean buttonRight = Boolean.parseBoolean(values[17]);
				boolean buttonDown = Boolean.parseBoolean(values[18]);
				boolean buttonLeft = Boolean.parseBoolean(values[19]);
				boolean buttonHome = Boolean.parseBoolean(values[20]);
				boolean buttonMinus = Boolean.parseBoolean(values[21]);
				boolean buttonPlus = Boolean.parseBoolean(values[22]);
				boolean buttonOne = Boolean.parseBoolean(values[23]);
				boolean buttonTwo = Boolean.parseBoolean(values[24]);
//				boolean buttonIRMode = Boolean.parseBoolean(values[25]);
//				boolean irStateFound1 = Boolean.parseBoolean(values[26]);
//				boolean irStateFound2 = Boolean.parseBoolean(values[27]);
//				boolean irStateFound3 = Boolean.parseBoolean(values[28]);
//				boolean irStateFound4 = Boolean.parseBoolean(values[29]);
//				double irStateSize1 = Double.parseDouble(values[30]);
//				double irStateSize2 = Double.parseDouble(values[31]);
//				double irStateSize3 = Double.parseDouble(values[32]);
//				double irStateSize4 = Double.parseDouble(values[33]);
				
				// Raw values (absolute).
				double irStateRawMidX = Double.parseDouble(values[34]);
				double irStateRawMidY = Double.parseDouble(values[35]);
				double irStateRawX1 = Double.parseDouble(values[36]);
				double irStateRawY1 = Double.parseDouble(values[37]);
//				double irStateRawX2 = Double.parseDouble(values[38]);
//				double irStateRawY2 = Double.parseDouble(values[39]);
//				double irStateRawX3 = Double.parseDouble(values[40]);
//				double irStateRawY3 = Double.parseDouble(values[41]);
//				double irStateRawX4 = Double.parseDouble(values[42]);
//				double irStateRawY4 = Double.parseDouble(values[43]);
				
				// Relative values.
//				double irStateMidX = Double.parseDouble(values[44]);
//				double irStateMidY = Double.parseDouble(values[45]);
//				double irStateX1 = Double.parseDouble(values[46]);
//				double irStateY1 = Double.parseDouble(values[47]);
//				double irStateX2 = Double.parseDouble(values[48]);
//				double irStateY2 = Double.parseDouble(values[49]);
//				double irStateX3 = Double.parseDouble(values[50]);
//				double irStateY3 = Double.parseDouble(values[51]);
//				double irStateX4 = Double.parseDouble(values[52]);
//				double irStateY4 = Double.parseDouble(values[53]);
				
				
//				boolean LEDState1 = Boolean.parseBoolean(values[54]);
//				boolean LEDState2 = Boolean.parseBoolean(values[55]);
//				boolean LEDState3 = Boolean.parseBoolean(values[56]);
//				boolean LEDState4 = Boolean.parseBoolean(values[57]);
//				boolean rumble = Boolean.parseBoolean(values[58]);
				
				double rotationX = Double.parseDouble(values[59]);
				double rotationY = Double.parseDouble(values[60]);
				double totalDegree = Double.parseDouble(values[61]);

				publish(new DataButton(WiimoteRemote.class, DataButton.BUTTON_1, buttonA));
				publish(new DataButton(WiimoteRemote.class, DataButton.BUTTON_3, buttonB));
				publish(new DataButton(WiimoteRemote.class, DataButton.BUTTON_2, buttonHome));

				// Propagate pad keys. -> UP, RIGHT, DOWN, LEFT result in VK_UP, VK_RIGHT, VK_DOWN, VK_LEFT.
				publish(new DataButton(WiimoteRemote.class, DataButton.BUTTON_4, buttonUp));
				publish(new DataButton(WiimoteRemote.class, DataButton.BUTTON_5, buttonRight));
				publish(new DataButton(WiimoteRemote.class, DataButton.BUTTON_6, buttonDown));
				publish(new DataButton(WiimoteRemote.class, DataButton.BUTTON_7, buttonLeft));

				// Button (-) and (+) pressed -> result in VK_MINUS and VK_PLUS key event.
				publish(new DataButton(WiimoteRemote.class, DataButton.BUTTON_8, buttonMinus));
				publish(new DataButton(WiimoteRemote.class, DataButton.BUTTON_9, buttonPlus));

				// Button (1) and (2) pressed -> result in VK_1 and VK_2 key event.
				publish(new DataButton(WiimoteRemote.class, DataButton.BUTTON_10, buttonOne));
				publish(new DataButton(WiimoteRemote.class, DataButton.BUTTON_11, buttonTwo));

				// Send data position event.
				double x = irStateRawX1 / 1023;
				double y = irStateRawY1 / 767;
				publish(new DataPosition2D(WiimoteRemote.class, x, y));
				
				x = irStateRawMidX / 1023;
				y = irStateRawMidY / 767;
				publish(new DataPosition2D(WiimoteRemote.class, x, y));
				
				x = rotationX / 1023;
				y = rotationY / 767;
				publish(new DataPosition2D(WiimoteRemote.class, x, y));

				// Send accelerometer data.
				publish(new DataInertial(WiimoteRemote.class, accelStateX, accelStateY, accelStateZ, false));
				
				// Send current state of degree.
				publish(new DataAnalog(WiimoteRemote.class, totalDegree));
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Closing Wiimote.");
		}
		server.close();
	}
}
