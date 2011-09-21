/**
 * Squidy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Squidy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Squidy. If not, see <http://www.gnu.org/licenses/>.
 *
 * 2006-2009 Human-Computer Interaction Group, University of Konstanz.
 * <http://hci.uni-konstanz.de>
 *
 * Please contact info@squidy-lib.de or visit our website http://squidy-lib.de for
 * further information.
 */
/**
 *
 */
package org.squidy.nodes;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>Synchronize</code>.
 * 
 * <pre>
 * Date: Okt 12, 2009
 * Time: 15:32:29 PM
 * </pre>
 * 
 * @author Nicolas Hirrle <a
 *         href="mailto:nihirrle@htwg-konstanz.de">nihirrle@htwg-konstanz.de</a>
 *         Human-Computer Interaction Group University of Konstanz
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 *         
 * @version $Id: Synchronize.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "Synchronize")
@Processor(
	name = "Synchronize",
	icon = "/org/squidy/nodes/image/48x48/synchronize.png",
	types = { Processor.Type.FILTER },
	tags = { "synchronize", "TUIO", "object", "position", "touch", "hittest" }
)
public class Synchronize extends AbstractNode {

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
	
	@XmlAttribute(name = "touch-synchronization-active")
	@Property(
		name = "Touch synchronization active",
		description = "Indicates whether touch synchronization is active.",
		group = "Touch Synchronization"
	)
	@CheckBox
	private boolean touchSynchronizationActive = true;
	
	public boolean isTouchSynchronizationActive() {
		return touchSynchronizationActive;
	}

	public void setTouchSynchronizationActive(boolean touchSynchronizationActive) {
		this.touchSynchronizationActive = touchSynchronizationActive;
	}

	@XmlAttribute(name = "x-resolution")
	@Property(
		name = "X-Resolution",
		description = "X-Resolution (in pixel) of the screen",
		group = "Touch Synchronization",
		suffix = "pixels"
	)
	@TextField
	private int resolutionX = 1920;

	public int getResolutionX() {
		return resolutionX;
	}

	public void setResolutionX(int resolutionX) {
		this.resolutionX = resolutionX;
	}

	@XmlAttribute(name = "y-resolution")
	@Property(
		name = "Y-Resolution",
		description = "Y-Resolution (in pixel) of the screen",
		group = "Touch Synchronization",
		suffix = "pixels"
	)
	@TextField
	private int resolutionY = 1200;

	public int getResolutionY() {
		return resolutionY;
	}

	public void setResolutionY(int resolutionY) {
		this.resolutionY = resolutionY;
	}

	@XmlAttribute(name = "radius")
	@Property(
		name = "Token radius",
		description = "Radius (in Pixel) where Fingertouchevents aren't displayed",
		group = "Touch Synchronization",
		suffix = "pixels"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 200,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 50,
		snapToTicks = false
	)
	private int radius = 17;

	/**
	 * @return the myProperty
	 */
	public final int getRadius() {
		return radius;
	}

	/**
	 * @param myProperty
	 *            the myProperty to set
	 */
	public final void setRadius(int radius) {
		this.radius = radius;
	}
	
	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	private Thread activePeriodicThread = null;
	private boolean needPeriodicMessage = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {
		
		if (isPeriodicMessages()) {
			startPeriodicMessages();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		activePeriodicThread = null;
	}
	/**
	 * 
	 */
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
							if (objectsAlive.size() > 0) {
								publish(objectsAlive.values());
							}
							else {
								DataPosition2D dataPosition2D = new DataPosition2D(ReacTIVision.class, 0, 0);
								dataPosition2D.setAttribute(TUIO.OBJECT_STATE, "periodic");
								publish(dataPosition2D);
							}
						}
					}
					activePeriodicThread = null;
				}
			});
			activePeriodicThread.start();
		}
	}
	
	// ################################################################################
	// BEGIN OF PROCESS
	// ################################################################################

	private int frameId = 0;
	
	/**
	 * @return
	 */
	private synchronized int getSessionId() {
		if (frameId > Integer.MAX_VALUE) {
			frameId = 0;
		}
		return ++frameId;
	}
	
	private final Map<Integer, DataPosition2D> objectsAlive = new HashMap<Integer, DataPosition2D>();
	private final Map<Integer, DataPosition2D> fingersAlive = new HashMap<Integer, DataPosition2D>();
	
	public IData process(DataPosition2D dataPosition2D) {
		needPeriodicMessage = false;
		
		if (dataPosition2D.hasAttribute(TUIO.ORIGIN_ADDRESS) &&
				"/tuio/2Dobj".equals(dataPosition2D.getAttribute(TUIO.ORIGIN_ADDRESS))) {
			if (dataPosition2D.hasAttribute(TUIO.OBJECT_STATE)) {
			
				for (DataPosition2D objectAlive : objectsAlive.values()) {
					objectAlive.setAttribute(TUIO.OBJECT_STATE, "refresh");
				}
				
				String objectState = (String) dataPosition2D.getAttribute(TUIO.OBJECT_STATE);
				Integer fiducialId = (Integer) dataPosition2D.getAttribute(TUIO.FIDUCIAL_ID);
	
				if (objectState.equals("add")) {
					dataPosition2D.setAttribute(DataConstant.SESSION_ID, getSessionId());
				}
				else if (objectsAlive.containsKey(fiducialId)) {
					int sessionId = (Integer) objectsAlive.get(fiducialId).getAttribute(DataConstant.SESSION_ID);
					dataPosition2D.setAttribute(DataConstant.SESSION_ID, sessionId);
				}
				
				objectsAlive.put(fiducialId, dataPosition2D);
				publish(objectsAlive.values());
				
				if (objectState.equals("remove")) {
					if (objectsAlive.containsKey(fiducialId)) {
						objectsAlive.remove(fiducialId);
					}
				}
				
//				System.out.println("SIZE OF OBJECTS2: " + objectsAlive.size());
			}
			else {
				throw new IllegalStateException("Object state was not set on data object.");
			}

			return null;
		}
		if (dataPosition2D.hasAttribute(TUIO.ORIGIN_ADDRESS) &&
				"/tuio/2Dcur".equals(dataPosition2D.getAttribute(TUIO.ORIGIN_ADDRESS))){
			
		}
		
		return dataPosition2D;
	}

	/**
	 * Within the postProcess method it will be checked whether any data position 2D that is not an object hits current
	 * alive objects area. If a position hits an objects area the position will be removed otherwise it will be retained.
	 * 
	 * @see org.squidy.manager.model.AbstractNode#postProcess(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer postProcess(IDataContainer dataContainer) {

		// Do not process if touch synchronization is not activated.
		if (!touchSynchronizationActive || objectsAlive.isEmpty()) {
			return dataContainer;
		}
		
		List<IData> collectionToReturn = new ArrayList<IData>();
		
		boolean hasHit = false;
		for (IData data : dataContainer.getData()) {
			if (data instanceof DataPosition2D) {
				if (!"/tuio/2Dobj".equals(data.getAttribute(TUIO.ORIGIN_ADDRESS))) {
			
					for (DataPosition2D object : objectsAlive.values()) {
						if (positionHitsObjectArea((DataPosition2D) data, object)) {
							hasHit = true;
							break;
						}
					}
					
					if (!hasHit) {
						collectionToReturn.add(data);
					}
				}
				else {
					continue;
				}
			}
			else {
				collectionToReturn.add(data);
			}
		}
		
		// Add all objects that are alive otherwise objects get lost.
		collectionToReturn.addAll(objectsAlive.values());
		
		dataContainer.setData(collectionToReturn.toArray(new IData[collectionToReturn.size()]));
		
		return dataContainer;
	}
	
	/**
	 * Checks whether a position hits the objects area or not. If the position is within an objects are
	 * it will return true otherwise false.
	 * 
	 * @param position The position that gets tested against an objects area.
	 * @param object The object that indicates the object area and the forbidden positions.
	 * @return Returns true if the position hits the objects area otherwise false.
	 */
	private boolean positionHitsObjectArea(DataPosition2D position, DataPosition2D object) {
		
		double x = object.getX() * resolutionX;
		double y = object.getY() * resolutionY;
		
		Ellipse2D circle = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
		
		double positionX = position.getX() * resolutionX;
		double positionY = position.getY() * resolutionY;
		
		return circle.contains(positionX, positionY);
	}
	
	// ################################################################################
	// END OF PROCESS
	// ################################################################################
}
