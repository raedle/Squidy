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

import java.beans.IntrospectionException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;

import de.unikonstanz.hci.ieval3.sensor.SensorException;
import de.unikonstanz.hci.ieval3.sensor.SensorSample;
import de.unikonstanz.hci.ieval3.sensor.impl.UDPSensorSampleSenderDiscrete;

/**
 * <code>IEvalScreen</code>.
 * 
 * <pre>
 * Date: Apr 30, 2008
 * Time: 1:49:27 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: IEvalScreen.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "iEval Screen")
@Processor(
	name = "iEval Screen",
	types = { Processor.Type.OUTPUT },
	description = "/org/squidy/nodes/html/iEvalScreen.html",
	tags = { "iEval", "hci", "konstanz" },
	status = Status.UNSTABLE
)
public class IEvalScreen extends AbstractNode {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(IEvalScreen.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "address")
	@Property(name = "Address")
	@TextField
	private String address = "192.168.0.57";

	/**
	 * @return the address
	 */
	public final String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public final void setAddress(String address) {
		this.address = address;
	}

	@XmlAttribute(name = "port")
	@Property(name = "Port")
	@TextField
	private int port = 5552;

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
	}

	@XmlAttribute(name = "send-easy-click")
	@Property(name = "Send EasyClick")
	@CheckBox
	private boolean sendEasyClick = true;

	/**
	 * @return the port
	 */
	public final boolean getSendEasyClick() {
		return sendEasyClick;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public final void setSendEasyClick(boolean sendEasyClick) {
		this.sendEasyClick = sendEasyClick;
	}

//	@XmlAttribute(name = "id-first-point")
//	@Property(name = "ID first point")
//	@TextField
//	private String idFirstPoint = "EasyClick";
//
//	/**
//	 * @return the port
//	 */
//	public final String getIdFirstPoint() {
//		return idFirstPoint;
//	}
//
//	/**
//	 * @param port
//	 *            the port to set
//	 */
//	public final void setIdFirstPoint(String IdFirstPoint) {
//		this.idFirstPoint = IdFirstPoint;
//	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private UDPSensorSampleSenderDiscrete<S4i1b> sender;

	private boolean lastDigital = false;

	private DataPosition2D firstPos = null;
	private DataPosition2D secondPos = null;
	private int sizeContainer = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() {
		try {
			sender = new UDPSensorSampleSenderDiscrete<S4i1b>(S4i1b.class, address, port);
			sender.start();
		}
		catch (IntrospectionException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		catch (SensorException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() {
		if (sender != null) {
			sender.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.data.logic.ReflectionProcessable#
	 * beforeDataContainerProcessing
	 * (org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer) {
		secondPos = null;
		sizeContainer = dataContainer.getData().length;
		return super.preProcess(dataContainer);
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	public IData process(DataPosition2D dataPosition2D) {

		if (!sendEasyClick || sizeContainer == 1) {

			firstPos = dataPosition2D.getClone();

			if (firstPos == null)
				return null;

			sender.updateSample(new S4i1b(firstPos.getTimestamp(), firstPos.getX(), firstPos.getY(), firstPos.getX(),
					firstPos.getY(), lastDigital));
			// System.out.println("Sent Normal Pos: "+firstPos.getX()+" "+
			// firstPos.getY()+" "+ firstPos.getX()+" "+ firstPos.getY()+" "+
			// lastDigital);

		}
		else {

			// if((Boolean)dataPosition2D.getAttribute(DataConstant.get(Boolean.class,"EasyClick"))==null){
			if (dataPosition2D.hasAttribute(EasyClick.EASY_CLICKED) && (Boolean) dataPosition2D.getAttribute(EasyClick.EASY_CLICKED)) {

				firstPos = dataPosition2D.getClone();

				if (firstPos == null || secondPos == null)
					return null;

				sender.updateSample(new S4i1b(firstPos.getTimestamp(), firstPos.getX(), firstPos.getY(), secondPos
						.getX(), secondPos.getY(), lastDigital));
				LOG.info("Sent EasyClick Pos: " + firstPos.getX() + " " + firstPos.getY() + " " + secondPos.getX()
						+ " " + secondPos.getY() + " " + lastDigital);
				// System.out.println("Sent EasyClick Pos: "+firstPos.getX()+" "+
				// firstPos.getY()+" "+ secondPos.getX()+" "+
				// secondPos.getY()+" "+ lastDigital);

			}
			else {

				secondPos = dataPosition2D.getClone();

			}

		}

		return null;
	}

	/**
	 * @param dataDigital
	 * @return
	 */
	public IData process(DataDigital dataDigital) {
		lastDigital = dataDigital.getFlag();
		return null;
	}

	/**
	 * A simple sensor state with 2D coordinates (x,y) and a boolean value, e.g.
	 * for a button.
	 * 
	 * @author Jo Bieg
	 * 
	 */
	public class S4i1b extends SensorSample {
		public long timestamp;
		public double x, y, x2, y2;
		public boolean b1; // button pressed

		public S4i1b() {
		}

		public S4i1b(long timestamp, double x, double y, double x2, double y2, boolean b1) {
			this.timestamp = timestamp;
			this.x = x;
			this.y = y;
			this.x2 = x2;
			this.y2 = y2;
			this.b1 = b1;
		}

		@Override
		public SensorSample clone() {
			return new S4i1b(timestamp, x, y, x2, y2, b1);
		}

		@Override
		public String toString() {
			return "timestamp=" + timestamp + ", x=" + x + ", y=" + y + ", x2=" + x2 + ", y2=" + y2 + ", b1=" + b1;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public boolean isB1() {
			return b1;
		}

		public void setB1(boolean b1) {
			this.b1 = b1;
		}

		public double getX2() {
			return x2;
		}

		public void setX2(double x2) {
			this.x2 = x2;
		}

		public double getY2() {
			return y2;
		}

		public void setY2(double y2) {
			this.y2 = y2;
		}
	}
}
