/**
 * 
 */
package org.squidy.nodes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.Slider;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>FourDirectionGestureRecognizer</code>.
 *
 * <pre>
 * Date: September 20, 2010
 * Time: 11:57:02 AM
 * </pre>
 *
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id$
 * @since 1.5
 */
@XmlType(name = "FourDirectionGestureRecognizer")
@Processor(
	types = { Processor.Type.OUTPUT },
	name = "Four Direction Gesture Recognizer",
	tags = { "gesture", "recognizer", "static" }
)
public class FourDirectionGestureRecognizer extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################
	
	@XmlAttribute(name = "release-threshold")
	@Property(
		name = "Release threshold",
		suffix = "\u0025"
	)
	@Slider(
		minimumValue = 0,
		maximumValue = 100,
		majorTicks = 50,
		minorTicks = 25,
		showTicks = true,
		showLabels = true,
		type = Integer.class
	)
	private int releaseThreshold = 50;
	
	public int getReleaseThreshold() {
		return releaseThreshold;
	}

	public void setReleaseThreshold(int releaseThreshold) {
		this.releaseThreshold = releaseThreshold;
	}
	
	// ################################################################################

	@XmlAttribute(name = "timeout-threshold")
	@Property(
		name = "Timeout threshold",
		suffix = "ms"
	)
	@Slider(
		minimumValue = 0,
		maximumValue = 5000,
		majorTicks = 1000,
		minorTicks = 500,
		showTicks = true,
		showLabels = true,
		type = Integer.class
	)
	private int timeoutThreshold = 1000;
	
	public int getTimeoutThreshold() {
		return timeoutThreshold;
	}

	public void setTimeoutThreshold(int timeoutThreshold) {
		this.timeoutThreshold = timeoutThreshold;
	}
	
	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	private int counterUp = 0;
	private int counterDown = 0;
	private int counterLeft = 0;
	private int counterRight = 0;
	
	private DataPosition2D anchorDataPosition2D;
	private DataPosition2D lastDataPosition2D;
	private long lastReleaseTime;
	
	@Override
	public void onStart() {
		super.onStart();
		
		// Reset release time.
		lastReleaseTime = 0;
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		
		resetCounters();
	}
	
	private int ignore;
	
	/**
	 * @param dataPosition2D
	 * @return
	 */
	public IData process(DataPosition2D dataPosition2D) {
//		ignore--;
		
		// Continue only if full hand is tracked.
		if (!dataPosition2D.hasAttribute(iPoint.TRACKER_STATE) ||
				!iPoint.TRACKER_STATE_FIST.equals(dataPosition2D.getAttribute(iPoint.TRACKER_STATE))) {
			resetCounters();
			return dataPosition2D;
		}
		
//		System.out.println("FULL HAND");
		
		if (System.currentTimeMillis() - lastReleaseTime < timeoutThreshold)
			return null;
		
//		int counterSum = counterLeft + counterUp + counterRight + counterDown;
		
		if (anchorDataPosition2D == null) {// || (counterSum > 0 && counterSum % 20 == 0)) {
//			ignore = 15;
			anchorDataPosition2D = dataPosition2D.getClone();
//			System.out.println("SET NEW ANCHOR");
			return null;
		}
		
		double movementX = anchorDataPosition2D.getX() - dataPosition2D.getX();
		double movementY = anchorDataPosition2D.getY() - dataPosition2D.getY();
		
//		System.out.println(movementX + " | " + movementY);
		
//		lastDataPosition2D = dataPosition2D.getClone();
//		
//		// Prefers vertical movement
//		if (Math.abs(movementY) > Math.abs(movementX)) {
//			if (movementY > 0) {
//				counterLeft = 0;
//				counterUp++;
//				counterRight = 0;
//				counterDown = 0;
//			}
//			else {
//				counterLeft = 0;
//				counterUp = 0;
//				counterRight = 0;
//				counterDown++;
//			}
//		}
//		// Prefers horizontal movement
//		else {
//			System.out.println("XMOV: " + movementX);
//			if (movementX > 0) {
//				counterLeft++;
//				counterUp = 0;
//				counterRight = 0;
//				counterDown = 0;
//			}
//			else {
//				counterLeft = 0;
//				counterUp = 0;
//				counterRight++;
//				counterDown = 0;
//			}
//		}
//		
//		if (counterLeft > counterThreshold) {
//			resetCounters();
//			System.out.println("Swipe Left");
//		}
//		else if (counterRight > counterThreshold) {
//			resetCounters();
//			System.out.println("Swipe Right");
//		}
//		else if (counterUp > counterThreshold) {
//			resetCounters();
//			System.out.println("Swipe Up");
//		}
//		else if (counterDown > counterThreshold) {
//			resetCounters();
//			System.out.println("Swipe Down");
//		}
		
		double threshold = (double) ((double) releaseThreshold) / 100.0;
		if (Math.abs(movementX) > threshold) {
			
			new Thread() {
				public void run() {
					JWindow frame = new JWindow();
					frame.setSize(600, 400);
					frame.setAlwaysOnTop(true);
					
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					System.out.println("SIZE: " + screenSize);
					
					frame.getContentPane().add(new JLabel(new ImageIcon(FourDirectionGestureRecognizer.class.getResource("/arrowleft.jpg"))), BorderLayout.CENTER);
					
					frame.setLocation(1448, -300);
					frame.setVisible(true);
					
					try {
						sleep(250);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					frame.setVisible(false);
					frame.dispose();
				};
			}.start();
			
			DataDigital keyDown = new DataDigital(StaticGestureRecognizer2D.class, true);
			DataDigital keyUp = new DataDigital(StaticGestureRecognizer2D.class, false);
			
			if (movementX > 0) {
				keyDown.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_RIGHT);
				keyUp.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_RIGHT);
			}
			else {
				keyDown.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_LEFT);
				keyUp.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_LEFT);
			}
			
			publish(keyDown);
			publish(keyUp);
			
			System.out.println("X MOVE: " + movementX);
			resetCounters();
		}
		else if (Math.abs(movementY) > threshold) {
			System.out.println("Y MOVE: " + movementY);
			resetCounters();
		}
		
		return null;
	}
	
	/**
	 * 
	 */
	private void resetCounters() {
		counterLeft = 0;
		counterUp = 0;
		counterRight = 0;
		counterDown = 0;
		lastReleaseTime = System.currentTimeMillis();
		anchorDataPosition2D = null;
		lastDataPosition2D = null;
	}
}
