/**
 * 
 */
package org.squidy.nodes;

import java.awt.event.KeyEvent;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataKey;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>StaticGestureRecognizer2D</code>.
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
@XmlType(name = "StaticGestureRecognizer2D")
@Processor(
	types = { Processor.Type.OUTPUT },
	name = "Static Gesture Recognizer 2D",
	tags = { "gesture", "recognizer", "static" }
)
public class StaticGestureRecognizer2D extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################
	
	
	
	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################
	
	private ConcurrentLinkedQueue<DataPosition2D> dataPositions;
	
	private long releaseTimestamp = 0;
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		
		dataPositions = new ConcurrentLinkedQueue<DataPosition2D>();
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		
		dataPositions.clear();
		dataPositions = null;
	}
	
	private boolean slideMode = false;
	
	/**
	 * @param dataPosition2D
	 * @return
	 */
	public IData process(DataPosition2D dataPosition2D) {
		
//		if (!slideMode && iPoint.TRACKER_STATE_VICTORY.equals(dataPosition2D.getAttribute(iPoint.TRACKER_STATE))) {
//			System.out.println("SHOW SLIDES");
//			slideMode = true;
//			return null;
//		}
//		
//		slideMode = false;
		
		// Continue only if full hand is tracked.
		if (!dataPosition2D.hasAttribute(iPoint.TRACKER_STATE) ||
				!iPoint.TRACKER_STATE_FULL_HAND.equals(dataPosition2D.getAttribute(iPoint.TRACKER_STATE)))
			return dataPosition2D;
		
		if (dataPosition2D.getX() < 0.0001) {
			return dataPosition2D;
		}
		
		if (System.currentTimeMillis() - releaseTimestamp < 2000) {
			return dataPosition2D;
		}
		
		// Add current position.
		dataPositions.add(dataPosition2D);
		
		if (dataPositions.size() < 10) {
			return dataPosition2D;
		}
		
		// Remove data if queue size is larger than 10.
		while (dataPositions.size() > 10) {
			dataPositions.poll();
		}
		
//		System.out.println(dataPosition2D.getX());
		
		boolean leftToRight = false;
		boolean rightToLeft = false;
		double xMovement = dataPositions.peek().getX();
		int counter = 0;
		for (DataPosition2D position : dataPositions) {
			
			if (leftToRight && rightToLeft) {
				break;
			}
			
//			System.out.println(position.getX());
			
			if (xMovement > position.getX()) {
				leftToRight = true;
				counter++;
			}
			else if (xMovement < position.getX()) {
				rightToLeft = true;
				counter++;
			}
			
			xMovement = position.getX();
		}
		
//		System.out.println(counter + " | " + leftToRight + " | " + rightToLeft);
		
		// Reset queue if movement switched into another direction.
//		if (leftToRight && rightToLeft) {
//			System.out.println("clear");
////			releaseTimestamp = System.currentTimeMillis();
//			dataPositions.clear();
//			return dataPosition2D;
//		}
//		else
			if (leftToRight && !rightToLeft && counter > 8) {
			System.out.println("Release left to right");
			
			releaseTimestamp = System.currentTimeMillis();
			dataPositions.clear();
			
			DataDigital keyDown = new DataDigital(StaticGestureRecognizer2D.class, true);
			keyDown.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_RIGHT);
			publish(keyDown);
			
			DataDigital keyUp = new DataDigital(StaticGestureRecognizer2D.class, false);
			keyUp.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_RIGHT);
			publish(keyUp);
		}
		else if (!leftToRight && rightToLeft && counter > 8) {
			System.out.println("Release right to left");
			
			releaseTimestamp = System.currentTimeMillis();
			dataPositions.clear();
			
			DataDigital keyDown = new DataDigital(StaticGestureRecognizer2D.class, true);
			keyDown.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_LEFT);
			publish(keyDown);
			
			DataDigital keyUp = new DataDigital(StaticGestureRecognizer2D.class, false);
			keyUp.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_LEFT);
			publish(keyUp);
		}
		
		return dataPosition2D;
	}
}
