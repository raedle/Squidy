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

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.impl.EndianDomainProvider;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;

import com.illposed.osc.Endian;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;


@XmlType(name = "TUIOTokenListener")
@Processor(
	name = "TUIO Token Listener",
	icon = "/org/squidy/nodes/image/48x48/tuio.png", 
	description = "/org/squidy/nodes/html/TUIOTokenListener.html",
	types = { Processor.Type.INPUT },
	tags = { "tuio", "token", "tangible", "interface" },
	status = Status.UNSTABLE
)
public class TUIOTokenListener extends AbstractNode {

	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(TUIOTokenListener.class);

	public static final DataConstant TUIO_ORIGIN_ADDRESS = DataConstant.get(
			String.class, "TUIO_ORIGIN_ADDRESS");
	public static final DataConstant TUIO_MARKER_ID = DataConstant.get(
			Integer.class, "TUIO_MARKER_ID");

	public static final DataConstant TUIO_MOVEMENT_VECTOR_X = DataConstant.get(
			Float.class, "TUIO_MOVEMENT_VECTOR_X");
	public static final DataConstant TUIO_MOVEMENT_VECTOR_Y = DataConstant.get(
			Float.class, "TUIO_MOVEMENT_VECTOR_Y");
	public static final DataConstant TUIO_MOTION_ACCELERATION = DataConstant
			.get(Float.class, "TUIO_MOTION_ACCELERATION");
	public static final DataConstant TUIO_ROTATION_ACCELERATION = DataConstant
			.get(Float.class, "TUIO_ROTATION_ACCELERATION");
	public static final DataConstant TUIO_ROTATION_VECTOR_A = DataConstant.get(
			Float.class, "TUIO_ROTATION_A");

	public static final DataConstant TUIO_ANGLE_A = DataConstant.get(
			Float.class, "TUIO_ANGLE_A");
	public static final DataConstant TUIO_ANGLE_B = DataConstant.get(
			Float.class, "TUIO_ANGLE_B");
	public static final DataConstant TUIO_ANGLE_C = DataConstant.get(
			Float.class, "TUIO_ANGLE_C");

	/*
	 * 
	 * @XmlAttribute(name = "address-outgoing")
	 * 
	 * @Property( name = "Address outgoing", group = "Connection Settings",
	 * description = "The outgoing address for the TUIO server." )
	 * 
	 * @TextField private String addressOutgoing = "127.0.0.1";
	 * 
	 * 
	 * public final String getAddressOutgoing() { return addressOutgoing; }
	 * 
	 * 
	 * public final void setAddressOutgoing(String addressOutgoing) {
	 * this.addressOutgoing = addressOutgoing; }
	 */
	/*
	 * @XmlAttribute(name = "port-outgoing")
	 * 
	 * @Property( name = "Port outgoing", group = "Connection Settings",
	 * description = "The outgoing port for the TUIO server." )
	 * 
	 * @TextField private int portOutgoing = 3334;
	 * 
	 * 
	 * public final int getPortOutgoing() { return portOutgoing; }
	 * 
	 * public final void setPortOutgoing(int portOutgoing) { this.portOutgoing =
	 * portOutgoing; }
	 */
	
	@XmlAttribute(name = "timeout")
	@Property(name = "Timeout", description = "Timeout after which a Token Lifted Message will be sent if no Tokens are recognized.")
	@TextField
	protected int timeout = 200;
	
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@XmlAttribute(name = "port-incoming")
	@Property(name = "Port incoming", group = "Connection Settings", description = "The incoming port for the TUIO server.")
	@TextField
	protected int portIncoming = 3332;

	/**
	 * @return the portIncoming
	 */
	public final int getPortIncoming() {
		return portIncoming;
	}

	/**
	 * @param portIncoming
	 *            the portIncoming to set
	 */
	public void setPortIncoming(int portIncoming) {
		this.portIncoming = portIncoming;
	}

	@XmlAttribute(name = "endian")
	@Property(name = "Endian", description = "Indicates which endian strategy will be used to identify bytes or not.")
	@ComboBox(domainProvider = EndianDomainProvider.class)
	private Endian endian = Endian.LITTLE_ENDIAN;

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

		if (isProcessing()) {
			// Restart the osc server with new endian strategy.
			stopOSCServer();
			startOSCServer();
		}
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private List<DataPosition2D> tokenList = new ArrayList<DataPosition2D>(0);

	// private OSCPortOut oscPortOut;
	private OSCPortIn oscPortIn;
	private long currentTimeout;
	private boolean tokenUpdated = false;
	// private bool
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() {
		startOSCServer();
		
		currentTimeout = System.currentTimeMillis();
		new Thread() {
			private int lastTokenCount = 0;
			boolean publishedTokenLifted = true;
			boolean publishedTokenDown = false;

			public void run() {
				super.run();
				while (isProcessing()) {
					long currentTime = System.currentTimeMillis();
					int tokenCount = tokenList.size();
					if (tokenCount > lastTokenCount) {
						if( !publishedTokenDown ){
							publishTokenDown();
							publishedTokenDown = true;
							publishedTokenLifted = false;
						}
						
					}
					else if (tokenCount == 0 && !publishedTokenLifted && (currentTimeout + timeout) < currentTime) {
						tokenList.clear();
						publishTokenLifted();
						publishedTokenLifted = true;
						publishedTokenDown = false;
					}
					lastTokenCount = tokenCount;
					try {
						tokenList.clear();
						long sleep = timeout;//Math.max((currentTimeout + 500)-currentTime, 10);

						Thread.sleep(sleep);
						

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	public void publishTokenDown() {
			System.out.println("TOKEN DOWN");
	}

	public void publishTokenLifted() {
		System.out.println("TOKEN LIFTED");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() {
		stopOSCServer();
		super.onStop();
	}

	/**
	 * 
	 */
	protected void startOSCServer() {
		/*
		 * try { oscPortOut = new
		 * OSCPortOut(InetAddress.getByName(addressOutgoing), portOutgoing); }
		 * catch (SocketException e) { throw new
		 * ProcessException(e.getMessage(), e); } catch (UnknownHostException e)
		 * { throw new ProcessException(e.getMessage(), e); }
		 */
		try {
			oscPortIn = new OSCPortIn(portIncoming, endian);
		} catch (SocketException e) {
			throw new ProcessException(e.getMessage(), e);
		}

		oscPortIn.addListener("/tuio/2Dobj", new OSCListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.illposed.osc.OSCListener#acceptMessages(java.util.Date,
			 * com.illposed.osc.OSCMessage[])
			 */
			public void acceptMessages(Date time, OSCMessage[] messages) {
				
				List<DataPosition2D> tokens = new ArrayList<DataPosition2D>(1);

				int fseq = -1;
				for (OSCMessage message : messages) {
					Object[] arguments = message.getArguments();
					if ("fseq".equals(arguments[0])) {
						fseq = (Integer) arguments[1];
					}
				}
				
				for (OSCMessage message : messages) {
					Object[] arguments = message.getArguments();

					if ("set".equals(arguments[0])) {

						int sessionId = (Integer) arguments[1];
						int markerId = (Integer) arguments[2];
						float x = (Float) arguments[3];
						float y = (Float) arguments[4];
						float angle = (Float) arguments[5];
						float movementVectorX = (Float) arguments[6];
						float movementVectorY = (Float) arguments[7];
						float rotationVector = (Float) arguments[8];
						float motionAcceleration = (Float) arguments[9];
						float rotationAcceleration = (Float) arguments[10];

						DataPosition2D dataPosition2D = new DataPosition2D(TUIO.class, x, y);
						dataPosition2D.setAttribute(TUIO_ORIGIN_ADDRESS,
								"/tuio/2Dobj");
						dataPosition2D.setAttribute(
								DataConstant.FRAME_SEQUENCE_ID, fseq);
						dataPosition2D.setAttribute(DataConstant.SESSION_ID,
								sessionId);
						dataPosition2D.setAttribute(TUIO_MARKER_ID, markerId);
						dataPosition2D.setAttribute(TUIO_MOVEMENT_VECTOR_X,
								movementVectorX);
						dataPosition2D.setAttribute(TUIO_MOVEMENT_VECTOR_Y,
								movementVectorY);
						dataPosition2D.setAttribute(TUIO_ROTATION_VECTOR_A,
								rotationVector);
						dataPosition2D.setAttribute(TUIO_ANGLE_A, angle);
						dataPosition2D.setAttribute(TUIO_ROTATION_ACCELERATION,
								rotationAcceleration);
						dataPosition2D.setAttribute(TUIO_MOTION_ACCELERATION,
								motionAcceleration);
						tokenUpdated = true;
						tokenList.clear();
						tokens.add(dataPosition2D);
						tokenList.add(dataPosition2D);
					}
				}
				publish(tokens);
			}
		});

		oscPortIn.startListening();
	}

	/**
	 * 
	 */
	protected void stopOSCServer() {
		if (oscPortIn != null) {
			oscPortIn.stopListening();
			oscPortIn.close();
			oscPortIn = null;
		}
	}

	// ################################################################################
	// BEGIN OF Processing
	// ################################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.ukn.hci.squidy.manager.data.logic.ReflectionProcessable#
	 * beforeDataContainerProcessing
	 * (org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(
			IDataContainer dataContainer) {
		List<DataPosition2D> dataPositions2D = DataUtility.getDataOfType(
				DataPosition2D.class, dataContainer);
		if (dataPositions2D.size() <= 0) {
			return super.preProcess(dataContainer);
		}

		return super.preProcess(dataContainer);
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	private OSCMessage prepare2DObj(DataPosition2D dataPosition2D) {
		OSCMessage set = new OSCMessage("/tuio/2Dcur");
		set.addArgument("set");
		set.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));
		set.addArgument(dataPosition2D.getAttribute(TUIO_MARKER_ID));
		set.addArgument((float) dataPosition2D.getX());
		set.addArgument((float) dataPosition2D.getY());
		set.addArgument(dataPosition2D.getAttribute(TUIO_ANGLE_A));
		set.addArgument(dataPosition2D.getAttribute(TUIO_MOVEMENT_VECTOR_X));
		set.addArgument(dataPosition2D.getAttribute(TUIO_MOVEMENT_VECTOR_Y));
		set.addArgument(dataPosition2D.getAttribute(TUIO_ROTATION_VECTOR_A));
		set.addArgument(dataPosition2D.getAttribute(TUIO_MOTION_ACCELERATION));
		set
				.addArgument(dataPosition2D
						.getAttribute(TUIO_ROTATION_ACCELERATION));
		return set;
	}

	// ################################################################################
	// END OF Processing
	// ################################################################################

}
