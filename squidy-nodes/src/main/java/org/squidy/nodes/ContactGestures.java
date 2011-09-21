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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Throughput;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataObject;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>ContactGestures</code>.
 * 
 * <pre>
 * Date: June 13, 2008
 * Time: 1:34:36 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: ContactGestures.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "Contact Gestures")
@Processor(
	name = "Contact Gestures",
	icon = "/org/squidy/nodes/image/48x48/tactilefinger.png",
	description = "/org/squidy/nodes/html/ContactGestures.html",
	types = { Processor.Type.FILTER },
	tags = {"touch", "gesture", "contact", "button", "click", "multitouch"}
)
public class ContactGestures extends AbstractNode {

	// Logger to log info, error, debug,... messages.
	private static Log LOG = LogFactory.getLog(ContactGestures.class);
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	@XmlAttribute(name = "timeout")
	@Property(
		name = "Timeout",
		suffix = "ms"
	)
	@TextField
	private int timeout = 80;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@XmlAttribute(name = "maximum-distance")
	@Property(
		name = "Maximum distance"
	)
	@TextField
	private double maximumDistance = 0.05;

	public double getMaximumDistance() {
		return maximumDistance;
	}

	public void setMaximumDistance(double maximumDistance) {
		this.maximumDistance = maximumDistance;
	}
	
	@XmlAttribute(name = "first-hit")
	@Property(
		name = "First hit"
	)
	@CheckBox
	private boolean firstHit = false;

	public boolean getFirstHit() {
		return firstHit;
	}

	public void setFirstHit(boolean firstHit) {
		this.firstHit = firstHit;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################
	
	private Map<Object, DataPosition2D[]> contacts;

	/**
	 * {@inheritDoc}
	 */
	public DataPosition2D process(DataPosition2D data2d) {
		Object sessionID = data2d.getAttribute(DataConstant.SESSION_ID);
		if(sessionID==null) return data2d;
		
//		System.out.println(contacts.size());
		
		DataPosition2D[] data = contacts.get(sessionID);
		if (data == null) {
			
//			System.out.println("new session id: "+sessionID);
			DataPosition2D data2dClone = data2d.getClone();
			contacts.put(sessionID,
					new DataPosition2D[] { data2dClone, data2dClone });
			
			if(firstHit){	
				DataButton dataButtonTrue = new DataButton(ContactGestures.class, DataButton.BUTTON_1, true);
				//TODO DeepClone!!!
				dataButtonTrue.setAttribute(DataConstant.SESSION_ID,sessionID);
				dataButtonTrue.setAttribute(DataConstant.DEVICE_ID, data2d.getAttribute(DataConstant.DEVICE_ID));
				DataButton dataButtonFalse = new DataButton(ContactGestures.class, DataButton.BUTTON_1, false);
				//TODO DeepClone!!!
				dataButtonFalse.setAttribute(DataConstant.SESSION_ID,sessionID);
				dataButtonFalse.setAttribute(DataConstant.DEVICE_ID, data2d.getAttribute(DataConstant.DEVICE_ID));
				publish(data2d, dataButtonTrue);
				publish(data2d, dataButtonFalse);
			}
			
			
			
//			for (Map.Entry<Object, DataPosition2D[]> contact : contacts.entrySet()) {
//				DataPosition2D[] pos = contact.getValue();
//				System.out.println(pos[0].getAttribute(DataConstant.SESSION_ID));
//			}
		}
		else {
//			System.out.println("old session id: "+sessionID);
			data[1] = data2d.getClone();
			
//			System.out.println(data2d.getAttribute(DataConstant.SESSION_ID));
			
			contacts.put(sessionID, data);
		}
		return data2d;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() {
		contacts = new ConcurrentHashMap<Object, DataPosition2D[]>();

		Thread t = new Thread(new Runnable() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				while (isProcessing()) {

					long timestamp = System.currentTimeMillis();
//					System.out.println(contacts.size());
					for (Map.Entry<Object, DataPosition2D[]> contact : contacts.entrySet()) {
						DataPosition2D[] data = contact.getValue();
						if (timestamp - data[1].getTimestamp() > timeout) {
							
							double x1 = data[0].getX();
							double x2 = data[1].getX();
							double y1 = data[0].getY();
							double y2 = data[1].getY();
							double distX = x1-x2;
							double distY = y1-y2;
							
							
							double distAbs = Math.sqrt(distX * distX + distY * distY);
							if (distAbs <= maximumDistance) {
								if(!firstHit){
									DataButton dataButtonTrue = new DataButton(ContactGestures.class, DataButton.BUTTON_1, true);
									//TODO DeepClone!!!
									dataButtonTrue.setAttribute(DataConstant.SESSION_ID, data[1].getAttribute(DataConstant.SESSION_ID));
									dataButtonTrue.setAttribute(DataConstant.DEVICE_ID, data[1].getAttribute(DataConstant.DEVICE_ID));
									DataButton dataButtonFalse = new DataButton(ContactGestures.class, DataButton.BUTTON_1, false);
									//TODO DeepClone!!!
									dataButtonFalse.setAttribute(DataConstant.SESSION_ID, data[1].getAttribute(DataConstant.SESSION_ID));
									dataButtonFalse.setAttribute(DataConstant.DEVICE_ID, data[1].getAttribute(DataConstant.DEVICE_ID));
									publish(data[1], dataButtonTrue);
									publish(data[1], dataButtonFalse);
								}
//								System.out.println(contact.getKey()+" removed");
								contacts.remove(contact.getKey());
							}
						}
					}
					
					try {
						Thread.sleep(10);
					}
					catch (InterruptedException e) {
						if (LOG.isErrorEnabled()) {
							LOG.error(e.getMessage(), e);
						}
					}
				}
			}

		});
//		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}
}