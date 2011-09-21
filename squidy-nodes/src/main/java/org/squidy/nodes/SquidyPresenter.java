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

import java.awt.event.KeyEvent;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataKey;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * The <code>SquidyPresenter</code> node maps iPhone-gestures to mouse- or key-events
 * such that you can use your iPhone to control PowerPoint presentations.
 * Simply use the iPhone touch screen to move the mouse cursor or touch to click.
 * Swiping from left to right and right to left maps to PAGE UP and PAGE DOWN keys,
 * therefore accessing the next / previous slide.
 * Swiping from top to bottom or vice versa maps to a right click (context menu).
 * 
 * <pre>
 * Date: Dec 16, 2009
 * Time: 20:10:33 PM
 * </pre>
 * 
 * @author Markus Nitsche, markus.nitsche@uni-konstanz.de, University of Konstanz
 * @version $Id: SquidyPresenter.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "SquidyPresenter")
@Processor(
	name = "SquidyPresenter",
	icon = "/org/squidy/nodes/image/48x48/presenter.png", 
	types = { Processor.Type.FILTER }, 
	description = "/org/squidy/nodes/html/SquidyPresenter.html",
	tags = { "presenter", "iPhone", "Android", "PowerPoint" }
)		
public class SquidyPresenter extends AbstractNode {

	
	// Private global variables
	private DataPosition2D last = new DataPosition2D(); //saves the last sent position
	private DataPosition2D origin; //saves the position of any "TOUCHES_BEGAN" event
	private double mThreshold = 0.3;
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	@XmlAttribute(name = "movement-threshold")
	@Property(
			name = "Movement threshold",
			suffix = "\u0025",
			description = "This value describes how large the 'distance' of a swiping gesture must be such that a gesture is recognized. If the value is for example is 30, the gesture needs to cover 30% of the screen width/height to be recognized"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 100,
		showLabels = true,
		showTicks = true,
		majorTicks = 25,
		minorTicks = 5,
		snapToTicks = true
	)
	private int movementThreshold = 30;

	public int getMovementThreshold() {
		return movementThreshold;
	}

	public void setMovementThreshold(int movementThreshold) {
		this.movementThreshold = movementThreshold;
		mThreshold = (double) movementThreshold / 100.0;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "time-threshold")
	@Property(
			name = "Time threshold",
			suffix = "ms",
			description = "After the given time (in ms), touches are ignored (Timeout)"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 250,
		maximumValue = 1000,
		showLabels = true,
		showTicks = true,
		majorTicks = 250,
		minorTicks = 50,
		snapToTicks = true
	)
	private int timeThreshold = 500;

	public int getTimeThreshold() {
		return timeThreshold;
	}

	public void setTimeThreshold(int timeThreshold) {
		this.timeThreshold = timeThreshold;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "swipe-right")
	@Property(
			name = "Swipe right",
			description = "The key event that will be released on swipe right."
	)
	@ComboBox(domainProvider = KeyEventDomainProvider.class)
	private int swipeRight = KeyEvent.VK_PAGE_DOWN;
	
	public int getSwipeRight() {
		return swipeRight;
	}

	public void setSwipeRight(int swipeRight) {
		this.swipeRight = swipeRight;
	}

	@XmlAttribute(name = "swipe-left")
	@Property(
			name = "Swipe left",
			description = "The key event that will be released on swipe left."
	)
	@ComboBox(domainProvider = KeyEventDomainProvider.class)
	private int swipeLeft = KeyEvent.VK_PAGE_UP;

	public int getSwipeLeft() {
		return swipeLeft;
	}

	public void setSwipeLeft(int swipeLeft) {
		this.swipeLeft = swipeLeft;
	}
	
	public static class KeyEventDomainProvider implements DomainProvider {

		public Object[] getValues() {
			ComboBoxItemWrapper[] keyEvents = new ComboBoxItemWrapper[4];
			keyEvents[0] = new ComboBoxItemWrapper(KeyEvent.VK_RIGHT, "Arrow Right");
			keyEvents[1] = new ComboBoxItemWrapper(KeyEvent.VK_LEFT, "Arrow Left");
			keyEvents[2] = new ComboBoxItemWrapper(KeyEvent.VK_PAGE_DOWN, "Page Down");
			keyEvents[3] = new ComboBoxItemWrapper(KeyEvent.VK_PAGE_UP, "Page Up");
			return keyEvents;
		}
	}
	
	// ################################################################################
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################
	
	
	
	//Here's what happens when Data comes in
	public IData process(DataPosition2D data) {
		if(data.getAttribute(iPhone.TOUCHES_BEGAN) != null) {
			//save origin of touch
			origin = data;
		}
		
		if(data.getAttribute(iPhone.TOUCHES_ENDED) != null) {
			DataDigital key = null;
			
			if(data.getTimestamp()-origin.getTimestamp() > timeThreshold) {
				//ignore touches that have been active too lang
				return data;
			}
			
			if(origin.getX() - data.getX() > mThreshold) {
				//this is a swiping gesture from right to left --> previous slide / PAGE UP
//				key = new DataKey(this.getClass(), swipeLeft, true);
				
				key = new DataDigital(Powerpointer.class, true);
				key.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_LEFT);
//				publish(key);
			}
			else if (data.getX() - origin.getX() > mThreshold) {
				//swiping gesture from left to right --> next slide / PAGE DOWN
//				key = new DataKey(this.getClass(), swipeRight, true);
				
				key = new DataDigital(Powerpointer.class, true);
				key.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_RIGHT);
//				publish(key);
			}
			else if (origin.getY() - data.getY() > mThreshold || data.getY() - origin.getY() > mThreshold) {
				//top-down movement --> right-click
				DataButton but = new DataButton(this.getClass(), DataButton.BUTTON_3, true);
				publish(but);
				but.setFlag(false);
				publish(but);
			}
			if(key != null) {
				//if we set a key, publish it to the pipeline
				publish(key);
				
				DataDigital key2 = key.getClone();
				key2.setFlag(false);
				publish(key2);
			}
		}
		
		//always return data, otherwise it will get "lost"
		return data;
	}
}
