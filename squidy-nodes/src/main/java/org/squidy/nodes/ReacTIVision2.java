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

import java.util.Timer;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.reactivision.TuioClient;
import org.squidy.nodes.reactivision.TuioCursor;
import org.squidy.nodes.reactivision.TuioListener;
import org.squidy.nodes.reactivision.TuioObject;


/**
 * <code>ReacTIVision2</code>.
 * 
 * <pre>
 * Date: Okt 30, 2009
 * Time: 15:08:41 PM
 * </pre>
 * 
 * @author Nicolas Hirrle, <a
 *         href="mailto:nihirrle@htwg-konstanz.de">nihirrle@htwg-konstanz.de</a>
 *         Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: ReacTIVision2.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 * @deprecated Use {@link ReacTIVision} instead
 */
@Deprecated
@XmlType(name = "reacTIVision2")
@Processor(
	name = "reacTIVision2",
	icon = "/org/squidy/nodes/image/48x48/reactivision.png",
	description = "/org/squidy/nodes/html/ReacTIVision.html",
	types = { Processor.Type.INPUT },
	tags = { "reactivision", "fiducial", "id", "token" },
	status = Status.UNSTABLE
)
public class ReacTIVision2 extends AbstractNode {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ReacTIVision2.class);

	public static final DataConstant FIDUCIAL_ID = DataConstant.get(
			Integer.class, "FIDUCIAL_ID");

	public static final DataConstant TUIO_TOKEN = DataConstant.get(
			String.class, "TUIO_TOKEN");
	public static final DataConstant TUIO_CURSOR = DataConstant.get(
			String.class, "TUIO_CURSOR");
	public static final DataConstant TUIO_ALIVE = DataConstant.get(
			Vector.class, "TUIO_ALIVE");

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "port")
	@Property(name = "Port", description = "The port on which the reacTIVision node receives TUIO messages.")
	@TextField
	private int port = 4444;

	/**
	 * @return the port
	 */
	public final int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public final void setPort(int port) {
		this.port = port;

		if (tuioClient != null) {
			stop();
			start();
		}
	}

	@XmlAttribute(name = "counter-clockwise-angle")
	@Property(name = "Counter clockwise angle", description = "Returns the object angle in a counter clockwise manner."

	)
	@CheckBox
	private boolean counterClockwiseAngle = false;

	public boolean isCounterClockwiseAngle() {
		return counterClockwiseAngle;

	}

	public void setCounterClockwiseAngle(boolean counterClockwiseAngle) {
		this.counterClockwiseAngle = counterClockwiseAngle;

	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private TuioClient tuioClient;
	private long lastTime;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {

		tuioClient = new TuioClient(port);

		tuioClient.addTuioListener(new TuioListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.manager.input.impl.reactivision.TuioListener
			 * #addTuioCursor
			 * (org.squidy.manager.input.impl.reactivision.TuioCursor)
			 */
			public void addTuioCursor(TuioCursor tuioCursor) {
				// DataPosition2D dataPosition2D = new
				// DataPosition2D(TUIO.class, tuioCursor.getX(),
				// tuioCursor.getY());
				// dataPosition2D.setAttribute(TUIO.TUIO_ORIGIN_ADDRESS,
				// "/tuio/2Dcur");
				// dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID,
				// -1);
				// dataPosition2D.setAttribute(DataConstant.SESSION_ID, ((Long)
				// tuioCursor.getSessionID()).intValue());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_X,
				// tuioCursor.getSpeedX());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_Y,
				// tuioCursor.getSpeedY());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOTION_ACCELERATION,
				// tuioCursor.getMotionAccel());
				// dataPosition2D.setAttribute(TUIO_CURSOR, "down");
				// publish(dataPosition2D);

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.manager.input.impl.reactivision.TuioListener
			 * #addTuioObject
			 * (org.squidy.manager.input.impl.reactivision.TuioObject)
			 */
			public void addTuioObject(TuioObject tuioObject) {
				publishTokenCapacity();
				// if (LOG.isDebugEnabled()) {
				// LOG.debug("Added TUIO object with id: " +
				// tuioObject.getFiducialID());
				// }
				//
				// DataDigital dd = new DataDigital(TUIO.class, true);
				// dd.setAttribute(TUIO_TOKEN, "down");
				// publish(dd);
				//
				// DataPosition2D dataPosition2D = new
				// DataPosition2D(TUIO.class, tuioObject.getX(),
				// tuioObject.getY());
				// dataPosition2D.setAttribute(TUIO.TUIO_ORIGIN_ADDRESS,"/tuio/2Dobj");
				// dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID,
				// -1);
				// dataPosition2D.setAttribute(DataConstant.SESSION_ID, ((Long)
				// tuioObject.getSessionID()).intValue());
				// dataPosition2D.setAttribute(FIDUCIAL_ID,
				// tuioObject.getFiducialID());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_X,
				// tuioObject.getSpeedX());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_Y,
				// tuioObject.getSpeedY());
				// dataPosition2D.setAttribute(TUIO.TUIO_ROTATION_VECTOR_A,
				// tuioObject.getRotationSpeed());
				// dataPosition2D.setAttribute(TUIO.TUIO_ANGLE_A,
				// tuioObject.getAngle());
				// dataPosition2D.setAttribute(TUIO.TUIO_ROTATION_ACCELERATION,
				// tuioObject.getRotationAccel());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOTION_ACCELERATION,
				// tuioObject.getMotionAccel());
				// dataPosition2D.setAttribute(TUIO_TOKEN, "down");
				// publish(dataPosition2D);

			}

			private long minTime = Long.MAX_VALUE;
			private long time = 0;

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.manager.input.impl.reactivision.TuioListener
			 * #refresh(long)
			 */
			public void refresh(long timestamp) {
				if (time == 0) {
					time = timestamp;
				} else {
					if (minTime > (timestamp - time)) {
						minTime = timestamp - time;
					}

					System.out.println(minTime);
					time = timestamp;
				}

				// if (LOG.isDebugEnabled()) {
				// LOG.debug("Refresh");
				// }

				// List<IData> dataToPublish = new ArrayList<IData>();

				Vector<Integer> aliveObjects = new Vector<Integer>();
				Vector<TuioObject> tuioObjects = tuioClient.getTuioObjects();

				for (TuioObject tuioObjectTmp : tuioObjects) {
					aliveObjects.add(((Long) tuioObjectTmp.getSessionID())
							.intValue());
				}

				if (System.currentTimeMillis() - lastTime >= 10) {
					DataPosition2D dataPosition2D = new DataPosition2D();
					dataPosition2D.setAttribute(TUIO_ALIVE, aliveObjects);
					dataPosition2D.setAttribute(TUIO.ORIGIN_ADDRESS,
							"/tuio/2Dobj");
					dataPosition2D.setAttribute(TUIO_CURSOR, "refreshed");
					publish(dataPosition2D);
				}

				// Vector<TuioObject> objects = tuioClient.getTuioObjects();
				// if (System.currentTimeMillis() - lastTime >= 10)
				// {
				// for (int i=0; i<objects.size(); i++)
				// {
				// TuioObject tuioObject = objects.get(i);
				// //System.out.println(System.currentTimeMillis());
				// //System.out.println(tuioObject.);
				// //int id = ((Long) tuioObject.getSessionID()).intValue();
				// //if (lastUpdateTime.containsKey(id) ||
				// lastUpdateTime.isEmpty())
				// //{
				// //long lastUpdated = -1;
				// //if (!lastUpdateTime.isEmpty())
				// // lastUpdated = lastUpdateTime.get(((Long)
				// tuioObject.getSessionID()).intValue());
				//	
				//								
				// DataPosition2D dataPosition2D = new
				// DataPosition2D(TUIO.class, tuioObject.getX(),
				// tuioObject.getY());
				// dataPosition2D.setAttribute(TUIO.TUIO_ORIGIN_ADDRESS,"/tuio/2Dobj");
				// dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID,
				// -1);
				// dataPosition2D.setAttribute(DataConstant.SESSION_ID, ((Long)
				// tuioObject.getSessionID()).intValue());
				// dataPosition2D.setAttribute(FIDUCIAL_ID,
				// tuioObject.getFiducialID());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_X,
				// tuioObject.getSpeedX());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_Y,
				// tuioObject.getSpeedY());
				// dataPosition2D.setAttribute(TUIO.TUIO_ROTATION_VECTOR_A,
				// tuioObject.getRotationSpeed());
				// dataPosition2D.setAttribute(TUIO.TUIO_ANGLE_A,
				// counterClockwiseAngle ? ((float)((2 * Math.PI) -
				// tuioObject.getAngle())) : tuioObject.getAngle());
				// dataPosition2D.setAttribute(TUIO.TUIO_ROTATION_ACCELERATION,
				// tuioObject.getRotationAccel());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOTION_ACCELERATION,
				// tuioObject.getMotionAccel());
				// dataPosition2D.setAttribute(TUIO_TOKEN, "refreshed");
				// dataToPublish.add(dataPosition2D);
				// }
				// publish(dataToPublish);
				// }
				// dataToPublish.clear();

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.manager.input.impl.reactivision.TuioListener
			 * #removeTuioCursor
			 * (org.squidy.manager.input.impl.reactivision.TuioCursor)
			 */
			public void removeTuioCursor(TuioCursor tuioCursor) {

				// DataPosition2D dataPosition2D = new
				// DataPosition2D(TUIO.class, tuioCursor.getX(),
				// tuioCursor.getY());
				// dataPosition2D.setAttribute(TUIO.TUIO_ORIGIN_ADDRESS,
				// "/tuio/2Dcur");
				// dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID,
				// -1);
				// dataPosition2D.setAttribute(DataConstant.SESSION_ID, ((Long)
				// tuioCursor.getSessionID()).intValue());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_X,
				// tuioCursor.getSpeedX());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_Y,
				// tuioCursor.getSpeedY());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOTION_ACCELERATION,
				// tuioCursor.getMotionAccel());
				// dataPosition2D.setAttribute(TUIO_CURSOR, "lifted");
				// publish(dataPosition2D);

			}

			private Timer removeTimer = new Timer();

			private void publishTokenCapacity() {
				DataDigital DDtimeout = new DataDigital();
				DDtimeout.setAttribute(DataConstant.TIMEOUT, true);
				DDtimeout.setAttribute(ReacTIVision2.TUIO_TOKEN, String
						.valueOf(tuioClient.getTuioObjects().capacity()));
				DDtimeout.setAttribute(DataConstant.IDENTIFIER, String
						.valueOf(Thread.currentThread().getId()));
				DDtimeout.setFlag(true);
				publish(DDtimeout);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.manager.input.impl.reactivision.TuioListener
			 * #removeTuioObject
			 * (org.squidy.manager.input.impl.reactivision.TuioObject)
			 */
			public void removeTuioObject(TuioObject tuioObject) {
				publishTokenCapacity();
				// if(tuioClient.getTuioObjects().capacity() == 0){
				// removeTimer.schedule(new TimerTask() {
				// @Override
				// public void run() {
				// DataDigital DDtimeout = new DataDigital();
				//
				// DDtimeout.setAttribute(DataConstant.TIMEOUT, true);
				// DDtimeout.setAttribute(ReacTIVision.TUIO_TOKEN,
				// String.valueOf(tuioClient.getTuioObjects().capacity()));
				// DDtimeout.setAttribute(DataConstant.IDENTIFIER,
				// String.valueOf(Thread.currentThread().getId()));
				// DDtimeout.setFlag(true);
				//
				// publish(DDtimeout);
				// }
				// }, timeout);
				// }

				// System.out.println("removed token:" +
				// tuioObject.getFiducialID());
				// System.out.println("token number:" +
				// tuioClient.getTuioObjects().capacity());
				//
				// //last remove will not update, since no refresh occures
				// if(tuioClient.getTuioObjects().capacity() == 0){
				// DataPosition2D dataPosition2D = new
				// DataPosition2D(TUIO.class, tuioObject.getX(),
				// tuioObject.getY());
				// dataPosition2D.setAttribute(TUIO.TUIO_ORIGIN_ADDRESS,"/tuio/2Dobj");
				// dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID,
				// -1);
				// dataPosition2D.setAttribute(DataConstant.SESSION_ID, ((Long)
				// tuioObject.getSessionID()).intValue());
				// dataPosition2D.setAttribute(FIDUCIAL_ID,
				// tuioObject.getFiducialID());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_X,
				// tuioObject.getSpeedX());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_Y,
				// tuioObject.getSpeedY());
				// dataPosition2D.setAttribute(TUIO.TUIO_ROTATION_VECTOR_A,
				// tuioObject.getRotationSpeed());
				// dataPosition2D.setAttribute(TUIO.TUIO_ANGLE_A,
				// tuioObject.getAngle());
				// dataPosition2D.setAttribute(TUIO.TUIO_ROTATION_ACCELERATION,
				// tuioObject.getRotationAccel());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOTION_ACCELERATION,
				// tuioObject.getMotionAccel());
				// dataPosition2D.setAttribute(TUIO_TOKEN, "lifted");
				// publish(dataPosition2D);
				// }

				// if (LOG.isDebugEnabled()) {
				// LOG.debug("Removed TUIO object with id: " +
				// tuioObject.getFiducialID());
				// }

				// if (LOG.isDebugEnabled()) {
				// LOG.debug("Remove TUIO object with id: " +
				// tuioObject.getFiducialID());
				// }
				// DataDigital dd = new DataDigital(TUIO.class, false);
				// dd.setAttribute(TUIO_TOKEN, "lifted");
				// publish(dd);
				//
				// // DataToken dataToken = new DataToken(ReacTIVision.class,
				// "DataToken" + tuioObject.getFiducialID(), tuioObject.getX(),
				// tuioObject.getY());
				// // dataToken.setAttribute(DataConstant.SESSION_ID, ((Long)
				// tuioObject.getSessionID()).intValue());
				// // dataToken.setAttribute(FIDUCIAL_ID,
				// tuioObject.getFiducialID());
				// // dataToken.setAttribute(DataConstant.get(Float.class,
				// "Rotation"), (float) tuioObject.getAngle());
				// DataPosition2D dataPosition2D = new
				// DataPosition2D(TUIO.class, tuioObject.getX(),
				// tuioObject.getY());
				// dataPosition2D.setAttribute(TUIO.TUIO_ORIGIN_ADDRESS,"/tuio/2Dobj");
				// dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID,
				// -1);
				// dataPosition2D.setAttribute(DataConstant.SESSION_ID, ((Long)
				// tuioObject.getSessionID()).intValue());
				// dataPosition2D.setAttribute(FIDUCIAL_ID,
				// tuioObject.getFiducialID());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_X,
				// tuioObject.getSpeedX());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_Y,
				// tuioObject.getSpeedY());
				// dataPosition2D.setAttribute(TUIO.TUIO_ROTATION_VECTOR_A,
				// tuioObject.getRotationSpeed());
				// dataPosition2D.setAttribute(TUIO.TUIO_ANGLE_A,
				// tuioObject.getAngle());
				// dataPosition2D.setAttribute(TUIO.TUIO_ROTATION_ACCELERATION,
				// tuioObject.getRotationAccel());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOTION_ACCELERATION,
				// tuioObject.getMotionAccel());
				// dataPosition2D.setAttribute(TUIO_TOKEN, "lifted");
				// publish(dataPosition2D);

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.manager.input.impl.reactivision.TuioListener
			 * #updateTuioCursor
			 * (org.squidy.manager.input.impl.reactivision.TuioCursor)
			 */
			public void updateTuioCursor(TuioCursor tuioCursor) {
				// DataPosition2D dataPosition2D = new
				// DataPosition2D(TUIO.class, tuioCursor.getX(),
				// tuioCursor.getY());
				// dataPosition2D.setAttribute(TUIO.TUIO_ORIGIN_ADDRESS,
				// "/tuio/2Dcur");
				// dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID,
				// -1);
				// dataPosition2D.setAttribute(DataConstant.SESSION_ID, ((Long)
				// tuioCursor.getSessionID()).intValue());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_X,
				// tuioCursor.getSpeedX());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOVEMENT_VECTOR_Y,
				// tuioCursor.getSpeedY());
				// dataPosition2D.setAttribute(TUIO.TUIO_MOTION_ACCELERATION,
				// tuioCursor.getMotionAccel());
				// dataPosition2D.setAttribute(TUIO_CURSOR, "updated");
				//
				// publish(dataPosition2D);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.manager.input.impl.reactivision.TuioListener
			 * #updateTuioObject
			 * (org.squidy.manager.input.impl.reactivision.TuioObject)
			 */
			public void updateTuioObject(TuioObject tuioObject) {
				// if (LOG.isDebugEnabled()) {
				// LOG.debug("Update of TUIO object with id: " +
				// tuioObject.getFiducialID());
				// }

				// DataToken dataToken = new DataToken(ReacTIVision.class,
				// "DataToken" + tuioObject.getFiducialID(), tuioObject.getX(),
				// tuioObject.getY());
				// dataToken.setAttribute(DataConstant.SESSION_ID, ((Long)
				// tuioObject.getSessionID()).intValue());
				// dataToken.setAttribute(FIDUCIAL_ID,
				// tuioObject.getFiducialID());
				// dataToken.setAttribute(DataConstant.get(Float.class,
				// "Rotation"), (float) tuioObject.getAngle());
				// if(tuioObject.getUpdateTime() != -1)
				// lastUpdateTime = tuioObject.getUpdateTime();
				// System.out.println(tuioObject.getState());

				Vector<TuioObject> tuioObjects = tuioClient.getTuioObjects();
				Vector<Integer> aliveObjects = new Vector<Integer>();

				for (TuioObject tuioObjectTmp : tuioObjects) {
					aliveObjects.add(((Long) tuioObjectTmp.getSessionID())
							.intValue());
				}
				// .getSessionID()).intValue()

				DataPosition2D dataPosition2D = new DataPosition2D(TUIO.class,
						tuioObject.getX(), tuioObject.getY());
				dataPosition2D.setAttribute(TUIO.ORIGIN_ADDRESS,
						"/tuio/2Dobj");
				dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID, -1);
				dataPosition2D.setAttribute(DataConstant.SESSION_ID,
						((Long) tuioObject.getSessionID()).intValue());
				dataPosition2D.setAttribute(FIDUCIAL_ID, tuioObject
						.getFiducialID());
				dataPosition2D.setAttribute(TUIO.MOVEMENT_VECTOR_X,
						tuioObject.getSpeedX());
				dataPosition2D.setAttribute(TUIO.MOVEMENT_VECTOR_Y,
						tuioObject.getSpeedY());
				dataPosition2D.setAttribute(TUIO.ROTATION_VECTOR_A,
						tuioObject.getRotationSpeed());
				dataPosition2D
						.setAttribute(
								TUIO.ANGLE_A,
								counterClockwiseAngle ? ((float) ((2 * Math.PI) - tuioObject
										.getAngle()))
										: tuioObject.getAngle());
				dataPosition2D.setAttribute(TUIO.ROTATION_ACCELERATION,
						tuioObject.getRotationAccel());
				dataPosition2D.setAttribute(TUIO.MOTION_ACCELERATION,
						tuioObject.getMotionAccel());
				dataPosition2D.setAttribute(TUIO_ALIVE, aliveObjects);
				dataPosition2D.setAttribute(TUIO_TOKEN, "updated");

				publish(dataPosition2D);
				lastTime = System.currentTimeMillis();
			}
		});
		tuioClient.connect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		lastTime = 0;
		if (tuioClient != null) {
			tuioClient.disconnect();
			tuioClient = null;
		}
	}
}
