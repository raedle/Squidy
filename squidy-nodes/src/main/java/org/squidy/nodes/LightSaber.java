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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.model.AbstractNode;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.TextLCDPhidget;
import com.phidgets.event.SensorChangeEvent;
import com.phidgets.event.SensorChangeListener;


/**
 * <code>LightSaber</code>.
 * 
 * <pre>
 * Date: Feb 7, 2009
 * Time: 10:55:41 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: LightSaber.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0
 */
@XmlType(name = "Light Saber")
@Processor(
	name = "Light Saber",
	icon = "/org/squidy/nodes/image/48x48/lightsaber.png",
	description = "/org/squidy/nodes/html/LightSaber.html",
	types = { Processor.Type.INPUT },
	tags = { "light", "pen", "ir" },
	status = Status.UNSTABLE
)
public class LightSaber extends AbstractNode {

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	@XmlAttribute(name = "upper-bound")
	@Property(
		name = "Upper bound"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 1000,
		minorTicks = 50,
		majorTicks = 250,
		showTicks = true,
		snapToTicks = true
	)
	private int upperBound = 50;

	/**
	 * @return
	 */
	public int getUpperBound() {
		return upperBound;
	}

	/**
	 * @param upperBound
	 */
	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}
	
	// ################################################################################

	@XmlAttribute(name = "lower-bound")
	@Property(
		name = "Lower bound"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 1000,
		minorTicks = 50,
		majorTicks = 250,
		showTicks = true,
		snapToTicks = true
	)
	private int lowerBound = 20;

	/**
	 * @return
	 */
	public int getLowerBound() {
		return lowerBound;
	}

	/**
	 * @param lowerBound
	 */
	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private InterfaceKitPhidget interfaceKit;
	
	private TextLCDPhidget textLCD;

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {

		try {
			textLCD = new TextLCDPhidget();
			textLCD.openAny();
			textLCD.waitForAttachment(10000);
			
			interfaceKit = new InterfaceKitPhidget();
			interfaceKit.openAny();
			interfaceKit.waitForAttachment(10000);
			interfaceKit.addSensorChangeListener(new SensorChangeListener() {

				public void sensorChanged(SensorChangeEvent e) {
					double value = e.getValue();

					try {
						if (value < lowerBound) {
							System.out.println("PEN UP");
							
							interfaceKit.setOutputState(0, false);
							
							textLCD.setDisplayString(0, "Pen Up Event");
							textLCD.setDisplayString(1, "");
							
							publish(new DataButton(LightSaber.class, DataButton.BUTTON_1, false));
						} else if (value > upperBound) {
							System.out.println("PEN DOWN: PRESSURE: " + value);
							
							interfaceKit.setOutputState(0, true);
							
							textLCD.setDisplayString(0, "Pen Down Event");
							textLCD.setDisplayString(1, "Pressure: " + value);
							
							publish(new DataButton(LightSaber.class, DataButton.BUTTON_1, true));
						}
					} catch (PhidgetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		} catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		try {
			interfaceKit.close();
		} catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
