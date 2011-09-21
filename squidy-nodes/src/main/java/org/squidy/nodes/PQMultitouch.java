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

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;

import PQSDKMultiTouch.*;

/**
 * <code>PQMultitouch</code>.
 *
 * <pre>
 * Date: May, 2010
 * Time:
 * </pre>
 *
 * @author Nicolas Hirrle, nihirrle@htwg-konstanz.de, University of Konstanz
 * @version 1.0
 */
@XmlType(name = "PQMultitouch")
@Processor(name = "PQMultitouch", types = { Processor.Type.INPUT }, tags = {
		"gesture", "PQMultitouch", "multitouch" })
public class PQMultitouch extends AbstractNode{

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################

	@XmlAttribute(name = "periodic-messages")
	@Property(
		name = "Periodic messages",
		description = "Sends a periodic update every second just to indicate that the tracker is still available and to correct eventually lost packets in between"
	)
	@CheckBox
	private boolean periodicMessages = false;

	public synchronized boolean isPeriodicMessages() {
		return periodicMessages;
	}

	public synchronized void setPeriodicMessages(boolean periodicMessages) {
		this.periodicMessages = periodicMessages;

		if (isProcessing() && periodicMessages) {
			startPeriodicMessages();
		}
	}
	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	public static final DataConstant TUIO_ORIGIN_ADDRESS = DataConstant.get(
			String.class, "TUIO_ORIGIN_ADDRESS");
	public static final DataConstant TUIO_ANGLE_A = DataConstant.get(
			Float.class, "TUIO_ANGLE_A");
	public static final DataConstant TUIO_WIDTH = DataConstant.get(Float.class,
			"TUIO_WIDTH");
	public static final DataConstant TUIO_HEIGHT = DataConstant.get(
			Float.class, "TUIO_HEIGHT");
	public static final DataConstant TUIO_AREA = DataConstant.get(Float.class,
			"TUIO_AREA");
	public static final DataConstant TUIO_MOVEMENT_VECTOR_X = DataConstant.get(
			Float.class, "TUIO_MOVEMENT_VECTOR_X");
	public static final DataConstant TUIO_MOVEMENT_VECTOR_Y = DataConstant.get(
			Float.class, "TUIO_MOVEMENT_VECTOR_Y");
	public static final DataConstant TUIO_ROTATION_VECTOR_A = DataConstant.get(
			Float.class, "TUIO_ROTATION_VECTOR_A");
	public static final DataConstant TUIO_MOTION_ACCELERATION = DataConstant
			.get(Float.class, "TUIO_MOTION_ACCELERATION");
	public static final DataConstant TUIO_ROTATION_ACCELERATION = DataConstant
			.get(Float.class, "TUIO_ROTATION_ACCELERATION");
	public static final DataConstant TUIO_ALIVE = DataConstant.get(
			String.class, "TUIO_ALIVE");
	public static final DataConstant TUIO_TIMESTAMP = DataConstant.get(
			Integer.class, "TUIO_TIMESTAMP");
	public static final DataConstant TUIO_SPEED = DataConstant.get(
			Float.class, "TUIO_SPEED");



	private Hashtable<Integer, DataPosition2D> aliveObjects = null;
	private float resolutionX;
	private float resolutionY;
	private Thread activePeriodicThread = null;
	private boolean needPeriodicMessage = false;
	private PQMethods pqMethods;

	@Override
	public void onStart(){
		aliveObjects = new Hashtable<Integer, DataPosition2D>();
		pqMethods = new PQMethods();
		try {
			pqMethods.Init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			stop();
		}
		if (isPeriodicMessages()) {
			startPeriodicMessages();
		}
	}

	@Override
	public void onStop(){
		aliveObjects.clear();
		aliveObjects = null;
		activePeriodicThread = null;
		try {
			pqMethods.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private void startPeriodicMessages() {

		if (activePeriodicThread == null) {
			activePeriodicThread = new Thread(new Runnable() {

				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					while (isPeriodicMessages()) {
						needPeriodicMessage = true;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
							stop();
						}

						if (isPeriodicMessages() && needPeriodicMessage) {
							DataPosition2D dataPosition2D = pqMethods.setAliveMessage();
							publish(dataPosition2D);
						}
					}
					activePeriodicThread = null;
				}
			});
			activePeriodicThread.start();
		}
	}

	private class PQMethods extends PQMTClient
	{
		public int disconnect() throws Exception{
			int err_code=PQMTClientConstant.PQ_MT_SUCESS;
			try{
				if((err_code=DisconnectServer())!=PQMTClientConstant.PQ_MT_SUCESS)
				{
					System.out.println("connect server fail, socket error code:"+err_code);
					return err_code;
				}
			}catch(ConnectException ex){
				System.out.println("Please Run the PQLabs MultiTouch Platform(Server) first.");
				return err_code;
			}
			return err_code;
		}
		
		
		/**
		 * connect to server and setup client request type
		 * */
		public int Init()throws Exception{
			int err_code=PQMTClientConstant.PQ_MT_SUCESS;
			try{
				if((err_code=ConnectServer())!=PQMTClientConstant.PQ_MT_SUCESS)
				{
					System.out.println("connect server fail, socket error code:"+err_code);
					return err_code;
				}
			}catch(ConnectException ex){
				System.out.println("Please Run the PQLabs MultiTouch Platform(Server) first.");
				return err_code;
			}

			TouchClientRequest clientRequest=new TouchClientRequest();
			clientRequest.app_id=GetTrialAppID();

			try{
			clientRequest.type=PQMTClientConstant.RQST_RAWDATA_ALL|PQMTClientConstant.RQST_GESTURE_ALL;
			if((err_code=SendRequest(clientRequest))!=PQMTClientConstant.PQ_MT_SUCESS)
			{
				System.out.println("send request  fail,  error code:"+err_code);
				return err_code;
			}
			if((err_code=GetServerResolution())!=PQMTClientConstant.PQ_MT_SUCESS)
			{
				System.out.println("get server resolution fail,  error code:"+err_code);
				return err_code;
			}
			System.out.println("connected, start receive:"+err_code);

			}catch(Exception ex){
				System.out.println(ex.getMessage());
			}
			return err_code;
		}

		@Override
		public int OnServerBreak(){
			stop();
			return 0;
		}

		@Override
		public int OnGetResolution(int max_x, int max_y){
			resolutionX = max_x;
			resolutionY = max_y;
			System.out.println(resolutionX);
			return PQMTClientConstant.PQ_MT_SUCESS;
		}


		@Override
		public int OnTouchFrame(int frame_id, int time_stamp, Vector<TouchPoint> point_list){

			List<DataPosition2D> cursors = new ArrayList<DataPosition2D>();

		    for(TouchPoint point:point_list )
			{
//				String message="Touch at "+point.m_x+" "+point.m_y+"with size"+point.m_dx+"*"+point.m_dy;
//				System.out.println(message);

		    	int id = point.m_id;
				DataPosition2D dataPosition2D = preparePos2D(point, time_stamp);
				aliveObjects.put(id, dataPosition2D);

				if (point.m_point_event == PQMTClientConstant.TP_DOWN)
				{
					cursors.add(dataPosition2D);
					System.out.println("New Touch at "+point.m_x+" "+point.m_y+"with size"+point.m_dx+"*"+point.m_dy);
				}
				else if (point.m_point_event == PQMTClientConstant.TP_UP)
				{
					//System.out.println("Touch removed at "+point.m_x+" "+point.m_y+"with size"+point.m_dx+"*"+point.m_dy);
					aliveObjects.remove(id);				
				}
				else // MOVE
				{
					cursors.add(dataPosition2D);
				}
			}

			if(point_list.size() >= 0)
			{
				DataPosition2D dataPosition2D = setAliveMessage();
				cursors.add(0, dataPosition2D);
			}
			publish(cursors);
			return PQMTClientConstant.PQ_MT_SUCESS;
		}

		private DataPosition2D preparePos2D(TouchPoint point, int time_stamp) {
			float posXY[] = setResolution(point.m_x, point.m_y);
			float posXYSize[] = setResolution(point.m_dx, point.m_dy);

			int id = point.m_id;

			DataPosition2D dataPosition2D = new DataPosition2D(PQMultitouch.class, posXY[0], posXY[1]);
			dataPosition2D.setAttribute(TUIO_ORIGIN_ADDRESS, "/tuio/2Dblb");
			dataPosition2D.setAttribute(DataConstant.SESSION_ID, id);
			dataPosition2D.setAttribute(TUIO_WIDTH, posXYSize[0]);
			dataPosition2D.setAttribute(TUIO_HEIGHT, posXYSize[1]);
			dataPosition2D.setAttribute(TUIO_TIMESTAMP, time_stamp);
			dataPosition2D.setAttribute(TUIO_ROTATION_VECTOR_A, 0f);
			dataPosition2D.setAttribute(TUIO_MOTION_ACCELERATION, 0f);
			dataPosition2D.setAttribute(TUIO_ROTATION_ACCELERATION, 0f);

			if (aliveObjects.containsKey(id))
			{
				DataPosition2D oldPos = aliveObjects.get(id);

				int old_timestamp = (Integer) oldPos.getAttribute(TUIO_TIMESTAMP);
				int dt = (time_stamp - old_timestamp) * 1000;

				double dx = dataPosition2D.getX() - oldPos.getX();
				double dy = dataPosition2D.getY() - oldPos.getY();
				float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
				float movementX = (float) (dx / dt);
				float movementY = (float) (dy / dt);
				float speed = distance / dt;
				float m_acceleration = (speed - (Float) oldPos.getAttribute(TUIO_SPEED));


				dataPosition2D.setAttribute(TUIO_MOVEMENT_VECTOR_X, movementX);
				dataPosition2D.setAttribute(TUIO_MOVEMENT_VECTOR_Y, movementY);
				dataPosition2D.setAttribute(TUIO_SPEED, speed);
				dataPosition2D.setAttribute(TUIO_MOTION_ACCELERATION, m_acceleration);
			}
			else
			{
				dataPosition2D.setAttribute(TUIO_MOVEMENT_VECTOR_X, 0f);
				dataPosition2D.setAttribute(TUIO_MOVEMENT_VECTOR_Y, 0f);
				dataPosition2D.setAttribute(TUIO_SPEED, 0f);
				dataPosition2D.setAttribute(TUIO_MOTION_ACCELERATION, 0f);
			}

			return dataPosition2D;
		}

		private float[] setResolution(int x, int y) {
			float posXY[] = new float[2];
			posXY[0] = x / resolutionX;
			posXY[1] = y / resolutionY;
			return posXY;
		}

		private DataPosition2D setAliveMessage() {
			DataPosition2D dataPosition2D = new DataPosition2D(PQMultitouch.class, 0, 0);
			String alive = new String();

			dataPosition2D.setAttribute(TUIO_ORIGIN_ADDRESS, "/tuio/2Dblb");
			dataPosition2D.setAttribute(DataConstant.SESSION_ID, -1);

			Iterator<Integer> it = aliveObjects.keySet().iterator();

			while (it.hasNext())
			{
				int sid = it.next();
				alive = alive + sid + " ";
			}
			dataPosition2D.setAttribute(TUIO_ALIVE, alive);
			return dataPosition2D;
		}
	}
}
