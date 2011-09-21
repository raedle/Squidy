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
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.impl.EndianDomainProvider;
import org.squidy.manager.data.impl.DataDigital;
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
 * @version $Id: TuioICTFinger.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
@XmlType(name = "TuioICTFinger")
@Processor(
	name = "TuioICTFinger",
	icon = "/org/squidy/nodes/image/48x48/tuio_logo.png",
	description = "/org/squidy/nodes/html/TuioICTToken.html",
	types = { Processor.Type.OUTPUT },
	tags = { "flexable", "hci", "konstanz", "design", "framework", "Flex" },
	status = Status.UNSTABLE
)
public class TuioICTFinger extends AbstractNode {

	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(TuioICTFinger.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "osc-server-address-outgoing")
	@Property(name = "OSC server address out", description = "The outgoing address for the open-sound-control server.")
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
	@Property(name = "OSC server port out", description = "The outgoing port for the open-sound-control server.")
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
	@Property(name = "Endian", description = "Indicates which endian strategy will be used to identify bytes or not.")
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

	@XmlAttribute(name = "timeout")
	@Property(name = "Timeout", description = "Sets the timeout of no existing positions.")
	@TextField
	private long timeout = 200;

	/**
	 * @return the timeout
	 */
	public final long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public final void setTimeout(long timeout) {
		this.timeout = timeout;
		currentTimeout = timeout;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	// ################################################################################
	// BEGIN OF DATA CONSTANTS
	// ################################################################################

	private int lastSessionID = 0;
	private int lastFrameID = 0;

	private long currentTimeout;

	private OSCServer oscServer;
	private boolean sendEmulatedPos = true;
	private boolean wasLastSendEmpty = true;
	private int frameId = 0;
	private long timeSinceLastSend = 0;
	private long timeSinceLastTimeout = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {
		startOSCServer();

		// currentTimeout = System.currentTimeMillis();

		new Thread() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {

				while (isProcessing()) {

					long currentTime = System.currentTimeMillis();

					if ((currentTime - timeSinceLastTimeout) > timeout) {
						timeSinceLastTimeout = currentTime;
						if (sendEmulatedPos && !wasLastSendEmpty) {
							System.out.println("Sending empty Cursor");
							sendPositions(new DataPosition2D[0], new Date(
									currentTime));
							sendEmulatedPos = false;
							wasLastSendEmpty = true;
						}
					}

					try {
						long sleep = Math.max(timeout, 10);
						sendEmulatedPos = true;
						Thread.sleep(sleep);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		stopOSCServer();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.ukn.hci.squidy.manager.data.logic.ReflectionProcessable#
	 * beforeDataContainerProcessing
	 * (org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer) {

		List<DataPosition2D> positions = new ArrayList<DataPosition2D>();
		// List<DataPosition2D> tokens = new ArrayList<DataPosition2D>();

		for (IData data : dataContainer.getData()) {
			if (data instanceof DataPosition2D) {
				if (data.hasAttribute(TUIO.ORIGIN_ADDRESS)
						&& data.getAttribute(TUIO.ORIGIN_ADDRESS).equals(
								"/tuio/2Dcur")) {
					positions.add((DataPosition2D) data);
				}
			}
		}

		if (positions.size() > 0) {
			currentTimeout = System.currentTimeMillis();
			sendPositions(positions.toArray(new DataPosition2D[0]),
					new Date(dataContainer.getTimestamp()));
		}

		positions.clear();

		return super.preProcess(dataContainer);
	}

	/**
	 * @param positions
	 * @param timestamp
	 */
	private void sendPositions(DataPosition2D[] positions, Date timestamp) {

		OSCBundle bundle = new OSCBundle(timestamp);

		OSCMessage alive2DCur = new OSCMessage("/tuio/2Dcur");
		alive2DCur.addArgument("alive");
		bundle.addPacket(alive2DCur);

		OSCMessage fseq2DCur = new OSCMessage("/tuio/2Dcur");
		fseq2DCur.addArgument("fseq");
		fseq2DCur.addArgument(getFrameId());
		bundle.addPacket(fseq2DCur);

		for (DataPosition2D dataPosition2D : positions) {

			lastSessionID = (Integer) dataPosition2D
					.getAttribute(DataConstant.SESSION_ID);
			lastFrameID = (Integer) dataPosition2D
					.getAttribute(DataConstant.FRAME_SEQUENCE_ID);

			alive2DCur.addArgument(dataPosition2D
					.getAttribute(DataConstant.SESSION_ID));

			bundle.addPacket(prepare2DCur(dataPosition2D));
		}

		long currentTime = System.currentTimeMillis();
		timeSinceLastSend = currentTime - timeSinceLastSend;
		oscServer.send(bundle);
		wasLastSendEmpty = false;
		sendEmulatedPos = false;
		timeSinceLastSend = currentTime;
	}

	private OSCMessage prepare2DCur(DataPosition2D dataPosition2D) {
		OSCMessage set = new OSCMessage("/tuio/2Dcur");
		set.addArgument("set");
		set.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));
		set.addArgument((float) dataPosition2D.getX());
		set.addArgument((float) dataPosition2D.getY());
		set.addArgument(dataPosition2D.getAttribute(TUIO.MOVEMENT_VECTOR_X));
		set.addArgument(dataPosition2D.getAttribute(TUIO.MOVEMENT_VECTOR_Y));
		set.addArgument(dataPosition2D.getAttribute(TUIO.MOTION_ACCELERATION));

		return set;
	}
}
