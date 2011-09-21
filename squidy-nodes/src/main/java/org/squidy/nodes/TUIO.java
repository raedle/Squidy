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

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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
import org.squidy.manager.data.domainprovider.impl.EndianDomainProvider;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;

import com.illposed.osc.Endian;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;


/**
 * <code>TUIO</code>.
 *
 * <pre>
 * Date: May 23, 2008
 * Time: 3:12:59 PM
 * </pre>
 *
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: TUIO.java 772 2011-09-16 15:39:44Z raedle $  * @since 1.0.0  * @since 1.0.0
 * @since 1.0.0
 */
@XmlType(name = "TUIO")
@Processor(
	name = "TUIO",
	icon = "/org/squidy/nodes/image/48x48/tuio_logo.png",
	description = "/org/squidy/nodes/html/TUIO.html",
	types = { Processor.Type.INPUT, Processor.Type.OUTPUT },
	tags = { "tuio", "tangible", "interface" }
)
public class TUIO extends AbstractNode {

	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(TUIO.class);

	// ################################################################################
	// BEGIN OF DATA CONSTANTS
	// ################################################################################

	// ################################################################################
	// BEGIN OF TUIO DEFINED PARAMETERS
	// ################################################################################
	
	public static final DataConstant SESSION_ID = DataConstant.SESSION_ID;
	public static final DataConstant FIDUCIAL_ID = DataConstant.get(Integer.class, "TUIO_FIDUCIAL_ID");

	public static final DataConstant MOVEMENT_VECTOR_X = DataConstant.get(Float.class, "TUIO_MOVEMENT_VECTOR_X");
	public static final DataConstant MOVEMENT_VECTOR_Y = DataConstant.get(Float.class, "TUIO_MOVEMENT_VECTOR_Y");
	public static final DataConstant MOTION_ACCELERATION = DataConstant.get(Float.class, "TUIO_MOTION_ACCELERATION");
	public static final DataConstant ROTATION_ACCELERATION = DataConstant.get(Float.class, "TUIO_ROTATION_ACCELERATION");
	public static final DataConstant ROTATION_VECTOR_A = DataConstant.get(Float.class, "TUIO_ROTATION_A");
	public static final DataConstant TUIO_TOKEN = DataConstant.get(Boolean.class, "TUIO_TOKEN");

	public static final DataConstant ANGLE_A = DataConstant.get(Float.class, "TUIO_ANGLE_A");
	public static final DataConstant ANGLE_B = DataConstant.get(Float.class, "TUIO_ANGLE_B");
	public static final DataConstant ANGLE_C = DataConstant.get(Float.class, "TUIO_ANGLE_C");
	
	// ################################################################################
	// END OF TUIO DEFINED PARAMETERS
	// ################################################################################
	
	// ################################################################################
	// BEGIN OF TUIO 1.1 DEFINED PARAMETERS
	// ################################################################################
	
	public static final DataConstant WIDTH = DataConstant.get(Float.class, "TUIO_WIDTH");
	public static final DataConstant HEIGHT = DataConstant.get(Float.class, "TUIO_HEIGHT");
	public static final DataConstant AREA = DataConstant.get(Float.class, "TUIO_AREA");

	// ################################################################################
	// END OF TUIO 1.1 DEFINED PARAMETERS
	// ################################################################################

	// ################################################################################
	// BEGIN OF FREE DEFINED PARAMETERS
	// ################################################################################
	
	public static final DataConstant ORIGIN_ADDRESS = DataConstant.get(String.class, "TUIO_ORIGIN_ADDRESS");
	public static final DataConstant OBJECT_STATE = DataConstant.get(String.class, "TUIO_OBJECT_STATE");

	public static final DataConstant HAND_WIDTH = DataConstant.get(Float.class, "TUIO_HAND_WIDTH");
	public static final DataConstant HAND_HEIGHT = DataConstant.get(Float.class, "TUIO_HAND_HEIGHT");

	// ################################################################################
	// END OF FREE DEFINED PARAMETERS
	// ################################################################################

	// ################################################################################
	// BEGIN OF TUIO MESSAGE TYPES
	// ################################################################################
	
	public static final String MESSAGE_TYPE_SOURCE_SIMULATOR = "simulator";
	public static final String MESSAGE_TYPE_ALIVE = "alive";
	public static final String MESSAGE_TYPE_SET = "set";
	public static final String MESSAGE_TYPE_FSEQ = "fseq";
	
	// ################################################################################
	// BEGIN OF TUIO MESSAGE TYPES
	// ################################################################################
	
	// ################################################################################
	// BEGIN OF TUIO PROFILES
	// ################################################################################
	
	public static final String PROFILE_2D_CURSOR = "/tuio/2Dcur";
	public static final String PROFILE_2D_OBJECT = "/tuio/2Dobj";
	public static final String PROFILE_2D_BLOB = "/tuio/2Dblb";
	
	// ################################################################################
	// BEGIN OF TUIO PROFILES
	// ################################################################################
	
	// ################################################################################
	// END OF DATA CONSTANTS
	// ################################################################################

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################




	@XmlAttribute(name = "address-outgoing")
	@Property(
		name = "Address outgoing",
		group = "Connection Settings",
		description = "The outgoing address for the TUIO server."
	)
	@TextField
	private String addressOutgoing = "127.0.0.1";

	/**
	 * @return the addressOutgoing
	 */
	public final String getAddressOutgoing() {
		return addressOutgoing;
	}

	/**
	 * @param addressOutgoing the addressOutgoing to set
	 */
	public final void setAddressOutgoing(String addressOutgoing) {
		this.addressOutgoing = addressOutgoing;
	}

	@XmlAttribute(name = "port-outgoing")
	@Property(
		name = "Port outgoing",
		group = "Connection Settings",
		description = "The outgoing port for the TUIO server."
	)
	@TextField
	private int portOutgoing = 3333;

	/**
	 * @return the portOutgoing
	 */
	public final int getPortOutgoing() {
		return portOutgoing;
	}

	/**
	 * @param portOutgoing the portOutgoing to set
	 */
	public final void setPortOutgoing(int portOutgoing) {
		this.portOutgoing = portOutgoing;
	}

	@XmlAttribute(name = "port-incoming")
	@Property(
		name = "Port incoming",
		group = "Connection Settings",
		description = "The incoming port for the TUIO server."
	)
	@TextField
	protected int portIncoming = 3333;

	/**
	 * @return the portIncoming
	 */
	public final int getPortIncoming() {
		return portIncoming;
	}

	/**
	 * @param portIncoming the portIncoming to set
	 */
	public void setPortIncoming(int portIncoming) {
		this.portIncoming = portIncoming;
	}

	@XmlAttribute(name = "endian")
	@Property(
		name = "Endian",
		description = "Indicates which endian strategy will be used to identify bytes or not."
	)
	@ComboBox(
		domainProvider = EndianDomainProvider.class
	)
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
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private OSCPortOut oscPortOut;
	private OSCPortIn oscPortIn;
	private List<DataPosition2D> tokenList = new ArrayList<DataPosition2D>(0);

	// private OSCPortOut oscPortOut;

	private long currentTimeout;
	private boolean tokenUpdated = false;

	/* (non-Javadoc)
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
							publishTokenDown(tokenList.get(0));
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

	/**
	 * @param data
	 */
	private void publishTokenDown(DataPosition2D data) {
		int markerId = (Integer) data.getAttribute(FIDUCIAL_ID);
		DataDigital dd = new DataDigital(TUIO.class, true);
		dd.setAttribute(FIDUCIAL_ID, markerId);
		dd.setAttribute(TUIO_TOKEN, true);
		publish(dd);

		if (LOG.isDebugEnabled()) {
	//		LOG.debug("Token " + markerId + " down.");
		}
	}

	/**
	 *
	 */
	private void publishTokenLifted() {
		DataDigital dd = new DataDigital(TUIO.class, false);
		dd.setAttribute(TUIO_TOKEN, false);
		publish(dd);

		if (LOG.isDebugEnabled()) {
	//		LOG.debug("Token lifted.");
		}
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

		try {
			oscPortOut = new OSCPortOut(InetAddress.getByName(addressOutgoing), portOutgoing);
		}
		catch (SocketException e) {
			throw new ProcessException(e.getMessage(), e);
		}
		catch (UnknownHostException e) {
			throw new ProcessException(e.getMessage(), e);
		}

		try {
			oscPortIn = new OSCPortIn(portIncoming, endian);
		}
		catch (SocketException e) {
			throw new ProcessException(e.getMessage(), e);
		}

		oscPortIn.addListener("/tuio/2Dcur", new OSCListener() {

			/* (non-Javadoc)
			 * @see com.illposed.osc.OSCListener#acceptMessages(java.util.Date, com.illposed.osc.OSCMessage[])
			 */
			public void acceptMessages(Date time, OSCMessage[] messages) {

				List<DataPosition2D> cursors = new ArrayList<DataPosition2D>(1);

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
						float x = (Float) arguments[2];
						float y = (Float) arguments[3];
						float movementVectorX = (Float) arguments[4];
						float movementVectorY = (Float) arguments[5];
						float motionAcceleration = (Float) arguments[6];

						DataPosition2D dataPosition2D = new DataPosition2D(TUIO.class, x, y);
						dataPosition2D.setAttribute(ORIGIN_ADDRESS, "/tuio/2Dcur");
						dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID, fseq);
						dataPosition2D.setAttribute(DataConstant.SESSION_ID, sessionId);
						dataPosition2D.setAttribute(MOVEMENT_VECTOR_X, movementVectorX);
						dataPosition2D.setAttribute(MOVEMENT_VECTOR_Y, movementVectorY);
						dataPosition2D.setAttribute(MOTION_ACCELERATION, motionAcceleration);

						cursors.add(dataPosition2D);
					}
				}

				publish(cursors);
			}
		});
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
						dataPosition2D.setAttribute(ORIGIN_ADDRESS,
								"/tuio/2Dobj");
						dataPosition2D.setAttribute(
								DataConstant.FRAME_SEQUENCE_ID, fseq);
						dataPosition2D.setAttribute(DataConstant.SESSION_ID,
								sessionId);
						dataPosition2D.setAttribute(FIDUCIAL_ID, markerId);
						dataPosition2D.setAttribute(MOVEMENT_VECTOR_X,
								movementVectorX);
						dataPosition2D.setAttribute(MOVEMENT_VECTOR_Y,
								movementVectorY);
						dataPosition2D.setAttribute(ROTATION_VECTOR_A,
								rotationVector);
						dataPosition2D.setAttribute(ANGLE_A, angle);
						dataPosition2D.setAttribute(ROTATION_ACCELERATION,
								rotationAcceleration);
						dataPosition2D.setAttribute(MOTION_ACCELERATION,
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

		oscPortIn.addListener("/tuio/_sxyXYmaPP", new OSCListener() {
			public void acceptMessages(Date time, OSCMessage[] messages) {

				List<DataPosition2D> cursors = new ArrayList<DataPosition2D>(1);

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
						float x = (Float) arguments[2];
						float y = (Float) arguments[3];
						float movementVectorX = (Float) arguments[4];
						float movementVectorY = (Float) arguments[5];
						float motionAcceleration = (Float) arguments[6];
						float angleA = (Float) arguments[6];
						float handWidth = (Float) arguments[6];
						float handHeight = (Float) arguments[6];

						DataPosition2D dataPosition2D = new DataPosition2D(TUIO.class, x, y);
						dataPosition2D.setAttribute(ORIGIN_ADDRESS, "/tuio/_sxyXYmaPP");
						dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID, fseq);
						dataPosition2D.setAttribute(DataConstant.SESSION_ID, sessionId);
						dataPosition2D.setAttribute(MOVEMENT_VECTOR_X, movementVectorX);
						dataPosition2D.setAttribute(MOVEMENT_VECTOR_Y, movementVectorY);
						dataPosition2D.setAttribute(MOTION_ACCELERATION, motionAcceleration);
						dataPosition2D.setAttribute(ANGLE_A, angleA);
						dataPosition2D.setAttribute(HAND_WIDTH, handWidth);
						dataPosition2D.setAttribute(HAND_HEIGHT, handHeight);

						cursors.add(dataPosition2D);
					}
				}

				publish(cursors);
			}
		});


		oscPortIn.addListener("/tuio/_sxyXYma", new OSCListener() {

			/* (non-Javadoc)
			 * @see com.illposed.osc.OSCListener#acceptMessages(java.util.Date, com.illposed.osc.OSCMessage[])
			 */
			public void acceptMessages(Date time, OSCMessage[] messages) {

				List<DataPosition2D> cursors = new ArrayList<DataPosition2D>(1);

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
						float x = (Float) arguments[2];
						float y = (Float) arguments[3];
						float movementVectorX = (Float) arguments[4];
						float movementVectorY = (Float) arguments[5];
						float motionAcceleration = (Float) arguments[6];
						float angleA = (Float) arguments[7];

						DataPosition2D dataPosition2D = new DataPosition2D(TUIO.class, x, y);
						dataPosition2D.setAttribute(ORIGIN_ADDRESS, "/tuio/_sxyXYma");
						dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID, fseq);
						dataPosition2D.setAttribute(DataConstant.SESSION_ID, sessionId);
						dataPosition2D.setAttribute(MOVEMENT_VECTOR_X, movementVectorX);
						dataPosition2D.setAttribute(MOVEMENT_VECTOR_Y, movementVectorY);
						dataPosition2D.setAttribute(MOTION_ACCELERATION, motionAcceleration);
						dataPosition2D.setAttribute(ANGLE_A, angleA);

						cursors.add(dataPosition2D);
					}
				}

				publish(cursors);
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
		if (oscPortOut != null) {
			//oscPortIn.stopListening();
			oscPortOut.close();
			oscPortIn = null;
		}
	}

	// ################################################################################
	// BEGIN OF Processing
	// ################################################################################

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.logic.ReflectionProcessable#beforeDataContainerProcessing(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer) {

		List<DataPosition2D> dataPositions2D = DataUtility.getDataOfType(DataPosition2D.class, dataContainer);
		if (dataPositions2D.size() <= 0) {
			return super.preProcess(dataContainer);
		}

		OSCBundle bundle = new OSCBundle(new Date(dataContainer.getTimestamp()));


		OSCMessage fseq2DCur = new OSCMessage("/tuio/2Dcur");
		fseq2DCur.addArgument("fseq");
		bundle.addPacket(fseq2DCur);

		OSCMessage alive2DCur = new OSCMessage("/tuio/2Dcur");
		alive2DCur.addArgument("alive");
		bundle.addPacket(alive2DCur);

		OSCMessage fseq_sxyXYmaPP = new OSCMessage("/tuio/_sxyXYmaPP");
		fseq_sxyXYmaPP.addArgument("fseq");
		bundle.addPacket(fseq_sxyXYmaPP);

		OSCMessage alive_sxyXYmaPP = new OSCMessage("/tuio/_sxyXYmaPP");
		alive_sxyXYmaPP.addArgument("alive");
		bundle.addPacket(alive_sxyXYmaPP);

		OSCMessage fseq_sxyXYma = new OSCMessage("/tuio/_sxyXYma");
		fseq_sxyXYma.addArgument("fseq");
		bundle.addPacket(fseq_sxyXYma);

		OSCMessage alive_sxyXYma = new OSCMessage("/tuio/_sxyXYma");
		alive_sxyXYma.addArgument("alive");
		bundle.addPacket(alive_sxyXYma);
		/*
		OSCMessage fseq = new OSCMessage("/tuio/2Dcur");
		fseq.addArgument("fseq");
		bundle.addPacket(fseq);

		OSCMessage alive = new OSCMessage("/tuio/2Dcur");
		alive.addArgument("alive");
		bundle.addPacket(alive);
		*/
		// Iterate 2D cursors.
		for (DataPosition2D dataPosition2D : dataPositions2D) {

			String originAddress = (String) dataPosition2D.getAttribute(ORIGIN_ADDRESS);


			// Set frame sequence id
			/*
			if (fseq.getArguments().length < 2) {
				fseq.addArgument(dataPosition2D.getAttribute(DataConstant.FRAME_SEQUENCE_ID));
			}
			alive.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));
			*/

			OSCMessage set;
			if ("/tuio/_sxyXYmaPP".equals(originAddress)) {

				if (fseq_sxyXYmaPP.getArguments().length < 2) {
					fseq_sxyXYmaPP.addArgument(dataPosition2D.getAttribute(DataConstant.FRAME_SEQUENCE_ID));
				}
				alive_sxyXYmaPP.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));


				set = prepare_sxyXYmaPP(dataPosition2D);
			}
			else if ("/tuio/_sxyXYma".equals(originAddress)) {
				if (fseq_sxyXYma.getArguments().length < 2) {
					fseq_sxyXYma.addArgument(dataPosition2D.getAttribute(DataConstant.FRAME_SEQUENCE_ID));
				}
				alive_sxyXYma.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));

				set = prepare_sxyXYma(dataPosition2D);
			}
			// /tuio/2DCur
			else {
				if (fseq2DCur.getArguments().length < 2) {
					fseq2DCur.addArgument(dataPosition2D.getAttribute(DataConstant.FRAME_SEQUENCE_ID));
				}
				alive2DCur.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));

				set = prepare2DCur(dataPosition2D);
			}

			bundle.addPacket(set);
		}

		try {
			oscPortOut.send(bundle);
		}
		catch (IOException e) {
			throw new ProcessException(e.getMessage(), e);
		}

		return super.preProcess(dataContainer);
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	private OSCMessage prepare2DCur(DataPosition2D dataPosition2D) {
		OSCMessage set = new OSCMessage("/tuio/2Dcur");
		set.addArgument("set");
		set.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));
		set.addArgument((float) dataPosition2D.getX());
		set.addArgument((float) dataPosition2D.getY());
		set.addArgument(dataPosition2D.getAttribute(MOVEMENT_VECTOR_X));
		set.addArgument(dataPosition2D.getAttribute(MOVEMENT_VECTOR_Y));
		set.addArgument(dataPosition2D.getAttribute(MOTION_ACCELERATION));
		return set;
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	private OSCMessage prepare_sxyXYmaPP(DataPosition2D dataPosition2D) {
		OSCMessage set = new OSCMessage("/tuio/_sxyXYmaPP");
		set.addArgument("set");
		set.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));
		set.addArgument((float) dataPosition2D.getX());
		set.addArgument((float) dataPosition2D.getY());
		set.addArgument(dataPosition2D.getAttribute(MOVEMENT_VECTOR_X));
		set.addArgument(dataPosition2D.getAttribute(MOVEMENT_VECTOR_Y));
		set.addArgument(dataPosition2D.getAttribute(MOTION_ACCELERATION));
		set.addArgument(dataPosition2D.getAttribute(ANGLE_A));
		set.addArgument(dataPosition2D.getAttribute(HAND_WIDTH));
		set.addArgument(dataPosition2D.getAttribute(HAND_HEIGHT));
		return set;
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	private OSCMessage prepare_sxyXYma(DataPosition2D dataPosition2D) {
		OSCMessage set = new OSCMessage("/tuio/_sxyXYma");
		set.addArgument("set");
		set.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));
		set.addArgument((float) dataPosition2D.getX());
		set.addArgument((float) dataPosition2D.getY());
		set.addArgument(dataPosition2D.getAttribute(MOVEMENT_VECTOR_X));
		set.addArgument(dataPosition2D.getAttribute(MOVEMENT_VECTOR_Y));
		set.addArgument(dataPosition2D.getAttribute(MOTION_ACCELERATION));
		set.addArgument(dataPosition2D.getAttribute(ANGLE_A));
		return set;
	}
	
	private void printMessage(OSCMessage message) {
		StringBuilder sb = new StringBuilder();
		sb.append(message.getAddress()).append(",");
		for (Object o : message.getArguments()) {
			sb.append(o).append(",");
		}
		System.out.println(sb);
	}
	// ################################################################################
	// END OF Processing
	// ################################################################################

}
