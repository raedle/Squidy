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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Throughput;
import org.squidy.manager.data.domainprovider.impl.EndianDomainProvider;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataObject;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.protocol.osc.OSCServer;

import com.illposed.osc.Endian;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;


/**
 * <code>BubbleGame</code>.
 *
 * <pre>
 * Date: Sep 26, 2008
 * Time: 12:35:13 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: BubbleGame.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
@XmlType(name = "Bubble Game")
@Processor(
		name = "Bubble Game",
		types = { Processor.Type.OUTPUT },
		description = "/org/squidy/nodes/html/BubbleGame.html",
		tags = {}
)
public class BubbleGame extends AbstractNode {

	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(BubbleGame.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "osc-outgoing-address")
	@Property(
		name = "OSC outgoing address",
		description = "The outgoing address for the open-sound-control server."
	)
	@TextField
	private String oscOutgoingAddress = "127.0.0.1";

	/**
	 * @return the oscOutgoingAddress
	 */
	public final String getOscOutgoingAddress() {
		return oscOutgoingAddress;
	}

	/**
	 * @param oscOutgoingAddress the oscOutgoingAddress to set
	 */
	public final void setOscOutgoingAddress(String oscOutgoingAddress) {
		this.oscOutgoingAddress = oscOutgoingAddress;
		
		if (oscServer != null) {
			// Restart the osc server with new endian strategy.
			stopOSCServer();
			startOSCServer();
		}
	}

	@XmlAttribute(name = "osc-outgoing-port")
	@Property(
		name = "OSC outgoing port",
		description = "The outgoing port for the open-sound-control server."
	)
	@TextField
	private int oscOutgoingPort = 7777;

	/**
	 * @return the oscOutgoingPort
	 */
	public final int getOscOutgoingPort() {
		return oscOutgoingPort;
	}

	/**
	 * @param oscOutgoingPort the oscOutgoingPort to set
	 */
	public final void setOscOutgoingPort(int oscOutgoingPort) {
		this.oscOutgoingPort = oscOutgoingPort;
		
		if (oscServer != null) {
			// Restart the osc server with new endian strategy.
			stopOSCServer();
			startOSCServer();
		}
	}

	@XmlAttribute(name = "endian")
	@Property(
		name = "Endian",
		description = "Indicates which endian strategy will be used to identify bytes or not."
	)
	@ComboBox(domainProvider = EndianDomainProvider.class)
	private Endian endian = Endian.BIG_ENDIAN;

	/**
	 * @return the endian
	 */
	public final Endian getEndian() {
		return endian;
	}

	/**
	 * @param endian
	 *            the endian to set
	 */
	public final void setEndian(Endian endian) {
		this.endian = endian;

		if (oscServer != null) {
			// Restart the osc server with new endian strategy.
			stopOSCServer();
			startOSCServer();
		}
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private Map<Integer, Boolean> lastButtonState = new HashMap<Integer, Boolean>();
	
	private OSCServer oscServer;

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() {
		startOSCServer();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() {
		stopOSCServer();
	}

	/**
	 * 
	 */
	protected void startOSCServer() {
		oscServer = new OSCServer(oscOutgoingAddress, oscOutgoingPort, endian);
	}

	/**
	 * 
	 */
	protected void stopOSCServer() {
		if (oscServer != null) {
			oscServer.stopListening();
			oscServer.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.data.logic.ReflectionProcessable#beforeDataContainerProcessing(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer) {
		
		List<DataPosition2D> dataPositions2D = new ArrayList<DataPosition2D>();
		
		for (IData data : dataContainer.getData()) {
			if (data instanceof DataPosition2D) {
				dataPositions2D.add((DataPosition2D) data);
			}
			else if (data instanceof DataDigital) {
				lastButtonState.put((Integer) data.getAttribute(DataConstant.DEVICE_ID), ((DataDigital) data).getFlag());
			}
		}
		
		sendPositions(dataPositions2D.toArray(new DataPosition2D[dataPositions2D.size()]), new Date(dataContainer.getTimestamp()));
		
		return super.preProcess(dataContainer);
	}

	/**
	 * @param positions
	 * @param timestamp
	 */
	private void sendPositions(DataPosition2D[] positions, Date timestamp) {
		
		OSCBundle bundle = new OSCBundle(timestamp);

		for (DataPosition2D dataPosition2D : positions) {
		
			OSCMessage message = new OSCMessage("/squidy/2f1b");
			
			Integer deviceId = (Integer) dataPosition2D.getAttribute(DataConstant.DEVICE_ID);
			if (deviceId == null) {
				deviceId = -1;
			}
			
			Boolean buttonState = lastButtonState.get((Integer) dataPosition2D.getAttribute(DataConstant.DEVICE_ID));
			if (buttonState == null) {
				buttonState = Boolean.FALSE;
			}
			
			int fingerID = -1;
			Object fingerO = dataPosition2D.getAttribute(DataConstant.SESSION_ID);
			if(fingerO!=null) fingerID = (Integer) fingerO; 

//			System.out.println("Finger "+fingerID);
			message.addArgument((float) dataPosition2D.getX());
			message.addArgument((float) dataPosition2D.getY());
			message.addArgument(deviceId);
			message.addArgument(fingerID);
			message.addArgument(buttonState);
			
			bundle.addPacket(message);
		}
		
		oscServer.send(bundle);
	}
}

