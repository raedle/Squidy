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

import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.impl.EndianDomainProvider;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;

import com.illposed.osc.Endian;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;


/**
 * <code>DreaMTouch</code>.
 *
 * <pre>
 * Date: Mar 17, 2010
 * Time: 3:44:49 PM
 * </pre>
 *
 * @author Nicolas Hirrle, nihirrle@htwg-konstanz.de, University of Konstanz
 * @version $Id
 * @since 1.0
 *
 */
@XmlType(name = "DreaMTouch")
@Processor(
	name = "DreaMTouch",
	icon = "/org/squidy/nodes/image/48x48/dreaMTouch.png",
	types = { Processor.Type.INPUT },
	tags = { "multi", "touch", "DreaMTouch" },
	status = Status.UNSTABLE
)
public class DreaMTouch extends AbstractNode {

	// ################################################################################
	// BEGIN OF DATA CONSTANTS
	// ################################################################################

//	public static final DataConstant TUIO_ORIGIN_ADDRESS = DataConstant.get(
//			String.class, "TUIO_ORIGIN_ADDRESS");
//	public static final DataConstant TUIO_ANGLE_A = DataConstant.get(
//			Float.class, "TUIO_ANGLE_A");
//	public static final DataConstant TUIO_WIDTH = DataConstant.get(Float.class,
//			"TUIO_WIDTH");
//	public static final DataConstant TUIO_HEIGHT = DataConstant.get(
//			Float.class, "TUIO_HEIGHT");
//	public static final DataConstant TUIO_AREA = DataConstant.get(Float.class,
//			"TUIO_AREA");
//	public static final DataConstant TUIO_MOVEMENT_VECTOR_X = DataConstant.get(
//			Float.class, "TUIO_MOVEMENT_VECTOR_X");
//	public static final DataConstant TUIO_MOVEMENT_VECTOR_Y = DataConstant.get(
//			Float.class, "TUIO_MOVEMENT_VECTOR_Y");
//	public static final DataConstant TUIO_ROTATION_VECTOR_A = DataConstant.get(
//			Float.class, "TUIO_ROTATION_VECTOR_A");
//	public static final DataConstant TUIO_MOTION_ACCELERATION = DataConstant
//			.get(Float.class, "TUIO_MOTION_ACCELERATION");
//	public static final DataConstant TUIO_ROTATION_ACCELERATION = DataConstant
//			.get(Float.class, "TUIO_ROTATION_ACCELERATION");
	
	@Deprecated
	public static final DataConstant TUIO_ALIVE = DataConstant.get(String.class, "TUIO_ALIVE");

	// ################################################################################
	// END OF DATA CONSTANTS
	// ################################################################################

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "tuio-port")
	@Property(name = "TUIO Port", description = "The port on which the DreaMTouch node receives TUIO messages.")
	@TextField
	private int tuioPort = 4444;

	public int getTuioPort() {
		return tuioPort;
	}

	public void setTuioPort(int tuioPort) {
		this.tuioPort = tuioPort;
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

	// ################################################################################
	// BEGIN OF PROCESSABLE
	// ################################################################################

	private OSCPortIn oscPortIn;

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public final void onStart() {
		super.onStart();
		startOSCServer();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public final void onStop() {
		stopOSCServer();
		super.onStop();
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

	/**
	 * 
	 */
	protected void startOSCServer() {

		try {
			oscPortIn = new OSCPortIn(tuioPort, endian);
		} catch (SocketException e) {
			throw new ProcessException(e.getMessage(), e);
		}

		oscPortIn.addListener(TUIO.PROFILE_2D_BLOB, new OSCListener() {

			public void acceptMessages(Date time, OSCMessage[] messages) {

				List<IData> data = new ArrayList<IData>(messages.length);
				for (OSCMessage message : messages) {
					Object[] arguments = message.getArguments();
					
					if (TUIO.MESSAGE_TYPE_SET.equals(arguments[0])) {
						int sessionId = (Integer) arguments[1];
						float x = (Float) arguments[2];
						float y = (Float) arguments[3];
						float angleA = (Float) arguments[4];
						float width = (Float) arguments[5];
						float height = (Float) arguments[6];
						float area = (Float) arguments[7];
						float motionspeedX = (Float) arguments[8];
						float motionspeedY = (Float) arguments[9];
						float rotionspeedA = (Float) arguments[10];
						float motionAcceleration = (Float) arguments[11];
						float rotionAcceleration = (Float) arguments[12];

						DataPosition2D dataPosition2D = new DataPosition2D(DreaMTouch.class, x, y);
						dataPosition2D.setAttribute(TUIO.ORIGIN_ADDRESS, TUIO.PROFILE_2D_BLOB);
						dataPosition2D.setAttribute(TUIO.SESSION_ID, sessionId);
						dataPosition2D.setAttribute(TUIO.ANGLE_A, angleA);
						dataPosition2D.setAttribute(TUIO.WIDTH, width);
						dataPosition2D.setAttribute(TUIO.HEIGHT, height);
						dataPosition2D.setAttribute(TUIO.AREA, area);
						dataPosition2D.setAttribute(TUIO.MOVEMENT_VECTOR_X, motionspeedX);
						dataPosition2D.setAttribute(TUIO.MOVEMENT_VECTOR_Y, motionspeedY);
						dataPosition2D.setAttribute(TUIO.ROTATION_VECTOR_A, rotionspeedA);
						dataPosition2D.setAttribute(TUIO.MOTION_ACCELERATION, motionAcceleration);
						dataPosition2D.setAttribute(TUIO.ROTATION_ACCELERATION, rotionAcceleration);
						
						data.add(dataPosition2D);
					}

					publish(data);
				}
			}
		});
		oscPortIn.startListening();
	}
}

// ################################################################################
// END OF PROCESSABLE
// ################################################################################
