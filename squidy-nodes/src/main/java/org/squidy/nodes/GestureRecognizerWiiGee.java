/**
 * Copyright (C) 2006-2008 Human-Computer Interaction Group, University of Konstanz. All Rights Reserved.
 *
 * This software is the proprietary information of University of Konstanz.
 * Use is subject to license terms.
 *
 * Please contact info@squidy-lib.de or visit our website http://hci.uni-konstanz.de
 * for further information.
 */
package org.squidy.nodes;

import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
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
 * <code>GestureRecognizerWiiGee</code>.
 * 
 * <pre>
 * Date: Feb 19, 2008
 * Time: 5:13:03 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version 1.0
 */
@XmlType(name = "GestureRecognizerWiiGee")
@Processor(
	name = "Gesture Recognizer WiiGee",
	types = { Processor.Type.FILTER },
	tags = { "gesture", "recognizer", "wii" },
	status = Status.UNSTABLE
)
public class GestureRecognizerWiiGee extends AbstractNode implements GestureListener {
	
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(GestureRecognizerWiiGee.class);
	
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

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private AccelerationStreamAnalyzer analyzer;

	private Wiimote wiimote;

	// Listeners, receive generated events
	Vector<WiimoteListener> listen = new Vector<WiimoteListener>();

	private DataGesture gestureReceived = null;
	private boolean doVibrate = false;

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
		// TODO Werner: Replace button handling with pipeline specific
		// context filtering.

		if (doVibrate) {
			doVibrate = false;
			return new DataDigital(this.getClass(), true);
		}

		return null;
	}

	/**
	 * To Recognize with Wii do follwing: 1. Do gesture while holding '+'-Button multiple times; 2. Train model with 'A'-Button after each unique gesture; 3. Recognize gesture with 'B'-Button
	 * @param dataButton
	 * @return
	 */
	public IData process(DataButton dataButton) {
		gestureReceived = null;
		
		// switches between learning and recognition mode for phidget joystick (laserpointer-interaction)
		if (dataButton.getButtonType() == DataButton.BUTTON_STICK_LEFT) {
			if (dataButton.getFlag()) {
				learningMode = !learningMode;
				LOG.info("Learning Mode = " + getLearningMode());
			}
		}
		
		
		// switch to learning mode and sample gesture
		if ((dataButton.getButtonType() == DataButton.BUTTON_2 && learningMode)
				|| dataButton.getButtonType() == DataButton.BUTTON_PLUS) {
			learningMode = true;
			if (dataButton.getFlag()) {
				startSampleLearning();
			} else {
				stopSample();
			}
		} 
		
		// switch to recognition mode and sample gesture
		else if ((dataButton.getButtonType() == DataButton.BUTTON_2 && !learningMode)
				|| dataButton.getButtonType() == DataButton.BUTTON_B) {
			learningMode = false;
			if (dataButton.getFlag()) {
				startSampleRecognition();
			} else {
				stopSample();
			}
		}
		
		// train recognition model after multiple execution of the same gesture
		else if (dataButton.getButtonType() == DataButton.BUTTON_STICK_DOWN
				|| dataButton.getButtonType() == DataButton.BUTTON_A) {
			if (dataButton.getFlag()) {
				trainModel();
			}
		} 
		
		if (gestureReceived != null) {
			return gestureReceived;
		}

		if (doVibrate) {
			doVibrate = false;
			return new DataDigital(this.getClass(), true);
		}

		return null;
	}
	
	/**
	 * To Recognize with Wii do follwing: 1. Do gesture while holding '+'-Button multiple times; 2. Train model with 'A'-Button after each unique gesture; 3. Recognize gesture with 'B'-Button
	 * @param dataButton
	 * @return
	 */
	public IData process(DataKey dataKey) {
		gestureReceived = null;
		
		System.out.println(dataKey.getKeyType());
//		// switches between learning and recognition mode for phidget joystick (laserpointer-interaction)
//		if (dataKey.getKeyType() == DataKey.class) {
//			if (dataButton.getFlag()) {
//				learningMode = !learningMode;
//				LOG.info("Learning Mode = " + getLearningMode());
//			}
//		}
//		
//		
//		// switch to learning mode and sample gesture
//		if ((dataButton.getButtonType() == DataButton.BUTTON_2 && learningMode)
//				|| dataButton.getButtonType() == DataButton.BUTTON_PLUS) {
//			learningMode = true;
//			if (dataButton.getFlag()) {
//				startSampleLearning();
//			} else {
//				stopSample();
//			}
//		} 
//		
//		// switch to recognition mode and sample gesture
//		else if ((dataButton.getButtonType() == DataButton.BUTTON_2 && !learningMode)
//				|| dataButton.getButtonType() == DataButton.BUTTON_B) {
//			learningMode = false;
//			if (dataButton.getFlag()) {
//				startSampleRecognition();
//			} else {
//				stopSample();
//			}
//		}
//		
//		// train recognition model after multiple execution of the same gesture
//		else if (dataButton.getButtonType() == DataButton.BUTTON_STICK_DOWN
//				|| dataButton.getButtonType() == DataButton.BUTTON_A) {
//			if (dataButton.getFlag()) {
//				trainModel();
//			}
//		} 
//		
//		if (gestureReceived != null) {
//			return gestureReceived;
//		}
//
//		if (doVibrate) {
//			doVibrate = false;
//			return new DataDigital(this.getClass(), "Vibrate", true);
//		}

		return null;
	}

	public void gestureReceived(GestureEvent event) {
		// logger.info("New Gesture Event: "+event.getName()+"
		// "+event.getProbability()+" "+event.getSource());

		gestureReceived = new DataGesture(GestureRecognizerWiiGee.class, "" + event.getGesture().getGestureType(),
				event.getGesture().getGestureNumber(), event.getGesture().getGestureType(), true);
	}

	public void noGestureRecognized() {
		doVibrate = true;
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
}
