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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.impl.EndianDomainProvider;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.protocol.osc.OSCServer;

import com.illposed.osc.Endian;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;


/**
 * <code>Touchlib</code>.
 *
 * <pre>
 * Date: Aug 4, 2008
 * Time: 10:38:23 PM
 * </pre>
 *
 * @author Nicolas Hirrle, <a
 *         href="mailto:nihirrle@htwg-konstanz.de">nihirrle@htwg-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: TuioICTToken.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
@XmlType(name = "TuioICTToken")
@Processor(
	name = "TuioICTToken",
	icon = "/org/squidy/nodes/image/48x48/tuio_logo.png",
	description = "/org/squidy/nodes/html/TuioICTToken.html",
	types = { Processor.Type.OUTPUT },
	tags = { "flexable", "hci", "konstanz", "design", "framework", "Flex" },
	status = Status.UNSTABLE
)
public class TuioICTToken extends AbstractNode {

	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(TuioICTToken.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "osc-server-address-outgoing")
	@Property(
		name = "OSC server address out",
		description = "The outgoing address for the open-sound-control server."
	)
	@TextField
	private String oscServerAddressOut = "127.0.0.1";

	/**
	 * @return the oscServerAddressOut
	 */
	public final String getOscServerAddressOut() {
		return oscServerAddressOut;
	}

	/**
	 * @param oscServerAddressOut
	 *            the oscServerAddressOut to set
	 */
	public final void setOscServerAddressOut(String oscServerAddressOut) {
		this.oscServerAddressOut = oscServerAddressOut;
	}

	@XmlAttribute(name = "osc-server-port-outgoing")
	@Property(
		name = "OSC server port out",
		description = "The outgoing port for the open-sound-control server."
	)
	@TextField
	private int oscServerPortOut = 57109;

	/**
	 * @return the oscServerPortOut
	 */
	public final int getOscServerPortOut() {
		return oscServerPortOut;
	}

	/**
	 * @param oscServerPortOut
	 *            the oscServerPortOut to set
	 */
	public final void setOscServerPortOut(int oscServerPortOut) {
		this.oscServerPortOut = oscServerPortOut;
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

	// ################################################################################
	// BEGIN OF DATA CONSTANTS
	// ################################################################################

	private OSCServer oscServer;
	private int frameId = 0;

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {
		startOSCServer();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		stopOSCServer();
		objectsAlive.clear();
	}

	/**
	 *
	 */
	protected void startOSCServer() {
		oscServer = new OSCServer(oscServerAddressOut, oscServerPortOut, endian);
	}

	/**
	 *
	 */
	protected void stopOSCServer() {
		if (oscServer != null) {
			oscServer.close();
		}
	}

	/**
	 * @return
	 */
	private synchronized int getFrameId() {
		if (frameId > Integer.MAX_VALUE) {
			frameId = 0;
		}
		return ++frameId;
	}
	
	private final Set<DataPosition2D> objectsAlive = new HashSet<DataPosition2D>();
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#preProcess(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer) {
		
		// Clear all objects that are alive.
		objectsAlive.clear();
		
//		List<DataPosition2D> dataPositions2D = DataUtility.getDataOfType(DataPosition2D.class, dataContainer);
		DataPosition2D setDataPosition2D = null;
		for (IData data : dataContainer.getData()) {
			if (data instanceof DataPosition2D && data.hasAttribute(TUIO.OBJECT_STATE)) {
				String objectState = (String) data.getAttribute(TUIO.OBJECT_STATE);
				
				if (!objectState.equals("remove") && !objectState.equals("periodic")) {
					objectsAlive.add((DataPosition2D) data);
					
					if (!objectState.equals("refresh")) {
						if (setDataPosition2D != null) {
							throw new ProcessException("Multiple data positions with object state != refresh detected in data container.");
						}
						setDataPosition2D = (DataPosition2D) data;
					}
				}
			}
		}
		
		//System.out.println("SIZE OF OBJECTS: " + objectsAlive.size());
		
		sendObjects(setDataPosition2D, objectsAlive);
		
		// Used to throw process exception -> see above.
		setDataPosition2D = null;
		
		return super.preProcess(dataContainer);
	}
	
//	public IData process(DataPosition2D dataPosition2D) {
//		
//		if (dataPosition2D.hasAttribute(TUIO.OBJECT_STATE)) {
//			String objectState = (String) dataPosition2D.getAttribute(TUIO.OBJECT_STATE);
//			Integer fiducialId = (Integer) dataPosition2D.getAttribute(TUIO.FIDUCIAL_ID);
//			
//			if ("add".equals(objectState)) {
//				objectsAlive.put(fiducialId, dataPosition2D);
//				
//				sendObjects(dataPosition2D);
//			}
//			else if ("update".equals(objectState)) {
//				objectsAlive.put(fiducialId, dataPosition2D);
//				
//				sendObjects(dataPosition2D);
//			}
//			else if ("remove".equals(objectState)) {
//				if (objectsAlive.containsKey(fiducialId)) {
//					objectsAlive.remove(fiducialId);
//					
//					sendObjects(null);
//				}
//			}
//		}
//		
//		return dataPosition2D;
//	}

	/**
	 * 
	 */
	private void sendObjects(DataPosition2D object, Collection<DataPosition2D> objectsAlive) {

		OSCBundle bundle = new OSCBundle(new Date());

	//	OSCMessage remoteMessage = new OSCMessage("/tuio/2Dobj");
	//	remoteMessage.addArgument("source");
	//	remoteMessage.addArgument("simulator");

		OSCMessage aliveMessage = new OSCMessage("/tuio/2Dobj");
		aliveMessage.addArgument("alive");
		if (objectsAlive.size() > 0) {
			for (DataPosition2D tuioObject : objectsAlive) {
				aliveMessage.addArgument(tuioObject.getAttribute(DataConstant.SESSION_ID));
			}
		}
		bundle.addPacket(aliveMessage);

		if (object != null) {
			OSCMessage setMessage = new OSCMessage();
			setMessage = new OSCMessage("/tuio/2Dobj");
			setMessage.addArgument("set");
			setMessage.addArgument(object.getAttribute(DataConstant.SESSION_ID));
			setMessage.addArgument(object.getAttribute(TUIO.FIDUCIAL_ID)); // Token ID
			setMessage.addArgument((float) object.getX());
			setMessage.addArgument((float) object.getY());
			setMessage.addArgument(object.getAttribute(TUIO.ANGLE_A));
			setMessage.addArgument(object.getAttribute(TUIO.MOVEMENT_VECTOR_X));
			setMessage.addArgument(object.getAttribute(TUIO.MOVEMENT_VECTOR_Y));
			setMessage.addArgument(object.getAttribute(TUIO.ROTATION_VECTOR_A));
			setMessage.addArgument(object.getAttribute(TUIO.MOTION_ACCELERATION));
			setMessage.addArgument(object.getAttribute(TUIO.ROTATION_ACCELERATION));
			
			bundle.addPacket(setMessage);
		}
		
		OSCMessage frameMessage = new OSCMessage("/tuio/2Dobj");
		frameMessage.addArgument("fseq");
		frameMessage.addArgument(getFrameId());
		bundle.addPacket(frameMessage);
		
		oscServer.send(bundle);
		
//		for (OSCPacket packet : bundle.getPackets()) {
//			if (packet instanceof OSCMessage) {
//				Object[] args = ((OSCMessage) packet).getArguments();
//				
//				System.out.println(args[0]);
//			}
	}
}
