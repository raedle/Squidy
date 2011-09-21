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

import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataGesture;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.manager.data.impl.DataKey;
import org.squidy.manager.model.AbstractNode;

import wiigee.device.Wiimote;
import wiigee.event.GestureEvent;
import wiigee.event.GestureListener;
import wiigee.event.StateEvent;
import wiigee.event.WiimoteAccelerationEvent;
import wiigee.event.WiimoteButtonPressedEvent;
import wiigee.event.WiimoteButtonReleasedEvent;
import wiigee.event.WiimoteListener;
import wiigee.logic.AccelerationStreamAnalyzer;

/**
 * <code>GestureRecognizerPhidget</code>.
 * 
 * <pre>
 * Date: Nov 21, 2008
 * Time: 5:13:03 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Stefan Dierdorf, <a
 *         href="mailto:Stefan.Dierdorf@uni-konstanz.de">Stefan.Dierdorf@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version 1.0
 */
@XmlType(name = "GestureRecognizerPhidget")
@Processor(
	name = "Gesture Recognizer Phidget",
	types = { Processor.Type.FILTER },
	tags = { "gesture", "recognizer", "phidget" },
	status = Status.UNSTABLE
)
public class GestureRecognizerPhidget extends AbstractNode implements GestureListener {
	
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(GestureRecognizerPhidget.class);
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	@XmlAttribute(name = "threshold")
	@Property(
		name = "Threshold",
		description = "Value threshold for adding to recognizer"
	)
	@TextField
	private double threshold = 0.02;

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	@XmlAttribute(name = "learning-mode")
	@Property(
		name = "Learning mode",
		description = "True if system is in learning mode, false for recognition mode"
	)
	@CheckBox
	private boolean learningMode = false;

	public boolean getLearningMode() {
		return learningMode;
	}

	public void setLearningMode(boolean learningMode) {
		this.learningMode = learningMode;
	}
	
	@XmlAttribute(name = "output-sensor-index")
	@Property(
		name = "Output sensor index"
	)
	@TextField
	private int outputSensorIndex = 0;

	/**
	 * @return the outputSensorIndex
	 */
	public final int getOutputSensorIndex() {
		return outputSensorIndex;
	}

	/**
	 * @param outputSensorIndex
	 *            the outputSensorIndex to set
	 */
	public final void setOutputSensorIndex(int outputSensorIndex) {
		this.outputSensorIndex = outputSensorIndex;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private AccelerationStreamAnalyzer analyzer;

	private Wiimote wiimote;

	// Listeners, receive generated events
	Vector<WiimoteListener> listen = new Vector<WiimoteListener>();

	private DataGesture gestureReceived = null;

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() {
		wiimote = new Wiimote("");
		
		analyzer = new AccelerationStreamAnalyzer();
		addWiimoteListener(analyzer);
		analyzer.addGestureListener(this);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() {
		if (wiimote != null) {
			wiimote.disconnect();
		}
		
		if (listen != null) {
			listen.remove(analyzer);
		}
		
		if (analyzer != null) {
			analyzer = null;
		}
	}

	/**
	 * @param dataInertial
	 * @return
	 */
	public IData process(DataInertial dataInertial) {

		if (dataInertial.getAbsoluteValue() > (1 + threshold) || dataInertial.getAbsoluteValue() < (1 - threshold)) {
			WiimoteAccelerationEvent w = new WiimoteAccelerationEvent(wiimote, dataInertial.getX(),
					dataInertial.getY(), dataInertial.getZ());
			
			for (int i = 0; i < this.listen.size(); i++) {
				this.listen.get(i).accelerationReceived(w);
			}
		}

		return null;
	}

	/**
	 * To Recognize with PhidgetAcceleromter and KeyboardInput do following: 
	 * 1. Activate 'learningMode' by pressing 't'-Key;
	 * 2. Do gesture multiple times while holding 'space'-bar on Keyboard-Input Window;
	 * 3. Train model with 'return'-Key on Keyboard-Input Window after each unique gesture;
	 * 4. After all gestures have been trained press 't' to leave learningMode;
	 * 5. Recognize gesture with 'space'-bar on MouseWindow ('learningMode' has to be deactivated);
	 * @param dataButton
	 * @return
	 */
	public IData process(DataKey dataKey) {
		gestureReceived = null;
		
		// keyType 84 for 't'; enables/disables learning-mode
		if(!dataKey.getFlag() && dataKey.getKeyType() == 84 ) {
			learningMode = !learningMode;
			LOG.info("Learning Mode = " + getLearningMode());
		}
		
		// keyType 32 for 'space-key'; hold to learn gesture
		// switch to learning mode and sample gesture
		if(dataKey.getKeyType() == 32 && learningMode) {
			learningMode = true;
			
			if(dataKey.getFlag() && dataKey.getFlag()) {
				startSampleLearning();
			} else {
				stopSample();
			}
		}
		
		// keyType 10 for 'return-key'; finalizes gesture
		// train recognition model after multiple execution of the same gesture
		else if (dataKey.getKeyType() == 10) {
			if (dataKey.getFlag()) {
				trainModel();
			}
		} 
		
		// keyType 32 for 'space-key'; hold to recognize gesture (after gesture has been learned)
		// switch to recognition mode and sample gesture
		else if(dataKey.getKeyType() == 32 && !learningMode) {
			learningMode = false;
			if (dataKey.getFlag()) {
				startSampleRecognition();
			} else {
				stopSample();
			}
		}
		
		if (gestureReceived != null) {
			return gestureReceived;
		}
		
		return null;
	}
	
	/**
	 * To Recognize with PhidgetAcceleromter, Keyboard and (Touch-)Button do following: 
	 * 1. Activate 'learningMode' by pressing 't'-Key;
	 * 2. Do gesture multiple times while holding (Touch-)Button;
	 * 3. Train model with 'RighMouse'-Button on MouseWindow and 'lerningMode' deactivated after each unique gesture;
	 * 4. Recognize gesture with 'LeftMouse'-Button on MouseWindow and 'lerningMode' deactivated;
	 * @param dataButton
	 * @return
	 */
	public IData process(DataDigital dataDigital) {
		gestureReceived = null;
		
		if(learningMode) {
			learningMode = true;
			if (dataDigital.getFlag()) {
				startSampleLearning();
			} else {
				stopSample();
			}
		}
		// switch to recognition mode and sample gesture
		else if(!learningMode) {
			learningMode = false;
			if (dataDigital.getFlag()) {
				startSampleRecognition();
			} else {
				stopSample();
			}
		}
		
		if (gestureReceived != null) {
			DataDigital dataDigi = new DataDigital(GestureRecognizerPhidget.class, true);
			dataDigi.setAttribute(DataConstant.TACTILE, true);
			publish(dataDigi);
			return gestureReceived;
		}

		return null;
	}
	
	/**
	 * To Recognize with PhidgetAcceleromter and Mouse do following: 
	 * 1. Activate 'learningMode' in Designer;
	 * 2. Do gesture multiple times while holding 'LeftMouse'-Button on MouseWindow;
	 * 3. Train model with 'RighMouse'-Button on MouseWindow and 'lerningMode' deactivated after each unique gesture;
	 * 4. Recognize gesture with 'LeftMouse'-Button on MouseWindow and 'lerningMode' deactivated;
	 * @param dataButton
	 * @return
	 */
	public IData process(DataButton dataButton) {
		gestureReceived = null;
		
		if(dataButton.getButtonType() == 1 && learningMode) {
			learningMode = true;
			if (dataButton.getFlag()) {
				startSampleLearning();
			} else {
				stopSample();
			}
		}
		// switch to recognition mode and sample gesture
		else if(dataButton.getButtonType() == 1 && !learningMode) {
			learningMode = false;
			if (dataButton.getFlag()) {
				startSampleRecognition();
			} else {
				stopSample();
			}
		}
		
		// train recognition model after multiple execution of the same gesture
		else if (dataButton.getButtonType() == 3 && !learningMode) {
			if (dataButton.getFlag()) {
				trainModel();
			}
		} 
		
		if (gestureReceived != null) {
			return gestureReceived;
		}

		return null;
	}
	
	public void gestureReceived(GestureEvent event) {
		// logger.info("New Gesture Event: "+event.getName()+"
		// "+event.getProbability()+" "+event.getSource());

		gestureReceived = new DataGesture(GestureRecognizerPhidget.class, "" + event.getGesture().getGestureType(),
				event.getGesture().getGestureNumber(), event.getGesture().getGestureType(), true);
	}

	public void stateReceived(StateEvent event) {
		// System.out.println("New State Event: "+event.getState()+"
		// "+event.getSource());
	}

	/**
	 * Adds an WiimoteListener to the wiimote. Everytime an action on the
	 * wiimote is performed the WiimoteListener would receive an event of this
	 * action.
	 */
	public void addWiimoteListener(WiimoteListener listener) {
		this.listen.add(listener);
	}

	public void startSampleLearning() {
		fireButtonPressedEvent(4);
	}

	public void startSampleRecognition() {
		fireButtonPressedEvent(3);
	}

	public void trainModel() {
		fireButtonPressedEvent(8);
	}

	public void stopSample() {
		fireButtonReleasedEvent();
	}

	/**
	 * Fires a button pressed event.
	 * 
	 * @param button
	 *            Integer value of the pressed button.
	 */
	public void fireButtonPressedEvent(int button) {
		WiimoteButtonPressedEvent w = new WiimoteButtonPressedEvent(wiimote, button);
		for (int i = 0; i < this.listen.size(); i++) {
			this.listen.get(i).buttonPressReceived(w);
		}
	}

	/**
	 * Fires a button released event.
	 */
	public void fireButtonReleasedEvent() {
		WiimoteButtonReleasedEvent w = new WiimoteButtonReleasedEvent(wiimote);
		for (int i = 0; i < this.listen.size(); i++) {
			this.listen.get(i).buttonReleaseReceived(w);
		}
	}

	public void noGestureRecognized() {
		// TODO Auto-generated method stub
		
	}
}
