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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

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
 * @version $Id:
 * @since 1.1.0
 */
@XmlType(name = "TuioDreaMTouchToken")
@Processor(
	name = "TuioDreaMTouchToken",
	icon = "/org/squidy/nodes/image/48x48/tuio_logo.png",
	types = { Processor.Type.OUTPUT },
	tags = { "TuioDreaMTouchToken", "hci", "konstanz", "design", "framework", "TuioDreaMTouch" },
	status = Status.UNSTABLE
)
public class TuioDreaMTouchToken extends AbstractNode {

	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(TuioDreaMTouchToken.class);

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

	public static final DataConstant TUIO_ORIGIN_ADDRESS = DataConstant.get(
			String.class, "TUIO_ORIGIN_ADDRESS");


	private Hashtable<Integer, DataPosition2D> objectsAlive = null;

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


	@Override
	public IDataContainer preProcess(IDataContainer dataContainer) {
		List<DataPosition2D> positions = new ArrayList<DataPosition2D>();

		IData[] data = dataContainer.getData();

		for (int i=0; i<data.length; i++) {
			if (data[i] instanceof DataPosition2D) {
				if (data[i].getAttribute(TUIO_ORIGIN_ADDRESS)
						.equals("/tuio/2Dobj")) {
					positions.add((DataPosition2D) data[i]);
				}
			}
		}

		if (positions.size() > 0) {
			sendObjects(positions.toArray(new DataPosition2D[0]),
					new Date(dataContainer.getTimestamp()));
		}

		positions.clear();

		return super.preProcess(dataContainer);
	}



	private void sendObjects(DataPosition2D[] positions, Date timestamp) {

		OSCBundle bundle = new OSCBundle(timestamp);
		String alivestr = null;

		if (positions.length > 0) {
			DataPosition2D dataPosition2D = positions[0];
			String originAddress = (String) dataPosition2D
					.getAttribute(TUIO_ORIGIN_ADDRESS);
			if ("/tuio/2Dobj".equals(originAddress)) {
				alivestr = new String();
				alivestr = (String) dataPosition2D.getAttribute(DreaMTouch.TUIO_ALIVE);
			}
		}

		OSCMessage alive2Ddobj = new OSCMessage("/tuio/2Dobj");
		alive2Ddobj.addArgument("alive");

		if (alivestr != null) {
			StringTokenizer st = new StringTokenizer(alivestr, " ");
			String sid = new String();

			while (st.hasMoreElements()) {
				sid = st.nextToken();
				int id = Integer.parseInt(sid);
				alive2Ddobj.addArgument(id);
			}
		}

		bundle.addPacket(alive2Ddobj);

		for (int i=1; i<positions.length; i++) {
			DataPosition2D dataPosition2D = positions[i];

			OSCMessage setMessage = new OSCMessage();
			setMessage = new OSCMessage("/tuio/2Dobj");
			setMessage.addArgument("set");
			setMessage.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));
			setMessage.addArgument(dataPosition2D.getAttribute(TUIO.FIDUCIAL_ID)); // Token ID
			setMessage.addArgument((float) dataPosition2D.getX());
			setMessage.addArgument((float) dataPosition2D.getY());
			setMessage.addArgument(dataPosition2D.getAttribute(TUIO.ANGLE_A));
			setMessage.addArgument(dataPosition2D.getAttribute(TUIO.MOVEMENT_VECTOR_X));
			setMessage.addArgument(dataPosition2D.getAttribute(TUIO.MOVEMENT_VECTOR_Y));
			setMessage.addArgument(dataPosition2D.getAttribute(TUIO.ROTATION_VECTOR_A));
			setMessage.addArgument(dataPosition2D.getAttribute(TUIO.MOTION_ACCELERATION));
			setMessage.addArgument(dataPosition2D.getAttribute(TUIO.ROTATION_ACCELERATION));

			bundle.addPacket(setMessage);
		}

		OSCMessage frameMessage = new OSCMessage("/tuio/2Dobj");
		frameMessage.addArgument("fseq");
		frameMessage.addArgument(getFrameId());
		bundle.addPacket(frameMessage);

		
		oscServer.send(bundle);

	}
}
