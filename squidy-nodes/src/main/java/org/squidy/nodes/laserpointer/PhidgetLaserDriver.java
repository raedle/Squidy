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

package org.squidy.nodes.laserpointer;

//import listeners.IFKitInputChangeListener;

import org.apache.log4j.Logger;
import org.squidy.SquidyException;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.nodes.Laserpointer;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.InputChangeEvent;
import com.phidgets.event.InputChangeListener;
import com.phidgets.event.SensorChangeEvent;
import com.phidgets.event.SensorChangeListener;


public class PhidgetLaserDriver extends Thread implements LaserVibrate, InputChangeListener, SensorChangeListener {
	private static Logger logger = Logger.getLogger(PhidgetLaserDriver.class);

	private LaserVibration laserVibration = null;
	private Laserpointer laserPointer;
	private boolean isVibrating = false;
	private InterfaceKitPhidget ifk;
	private double raw_a = 0;
	private double raw_b = 0;
	private double raw_c = 0;
	private boolean running = true;
	// TODO define properties for this in Recognizer for GUI access
	private int framerateSampling = 100;
	
	private boolean loggedErrorAlready;

	public PhidgetLaserDriver(Laserpointer laserPointer) throws PhidgetException {
		this.laserPointer = laserPointer;
		initialize();
	}

	private void initialize() throws PhidgetException {
		try {
			System.loadLibrary("phidget21");
			ifk = new InterfaceKitPhidget();
			ifk.openAny();
			
			ifk.waitForAttachment(10000);
		} catch (PhidgetException ex) {
			logger.error("Phidget Error: " + ex.getDescription());
			throw ex;
		}
		ifk.addInputChangeListener(this);
		ifk.addSensorChangeListener(this);
		start();
	}

	public void setSensitivityThreshold(int sensitivity) {
		try {
			if (ifk != null && ifk.isAttached()) {
				for (int i = 0; i < ifk.getSensorCount(); i++) {
					ifk.setSensorChangeTrigger(i, sensitivity);
				}
			}
		} catch (PhidgetException ex) {
			logger.error("Could not change sensitivity " + ex.getDescription());
		}
	}

	public void vibrate(boolean vib, int duration) {
		if (laserVibration != null) {
			laserVibration.cancel();
		}
		// if(isVibrating!=vib){
		isVibrating = vib;
		if (vib) {
			toggleVibOn();
		} else {
			toggleVibOff();
		}
		// }
		if (duration > 0) {
			laserVibration = new LaserVibration(this, duration);
		}
	}

	public void inputChanged(InputChangeEvent inputChangeEvent) {
		int buttonIndex = inputChangeEvent.getIndex();
		boolean buttonState = inputChangeEvent.getState();
		// left button
		if (buttonIndex == 0)
			laserPointer.publish(new DataButton(Laserpointer.class, DataButton.BUTTON_1, buttonState));
		// right button
		if (buttonIndex == 1)
			laserPointer.publish(new DataButton(Laserpointer.class, DataButton.BUTTON_3, buttonState));
		// upper button
		if (buttonIndex == 2)
			laserPointer.publish(new DataButton(Laserpointer.class, DataButton.BUTTON_2, buttonState));
	}

	public void run() {
		try {
			sleep(2000);
		} catch (InterruptedException e) {
		}
		while (running && laserPointer.isInertialActive()) {
			try {
				raw_a = ifk.getSensorRawValue(0);
				raw_b = ifk.getSensorRawValue(1);
				raw_c = ifk.getSensorRawValue(2);
				// System.out.println(raw_a+" "+ raw_b +" "+raw_c);
				laserPointer.publish(new DataInertial(Laserpointer.class, raw_a, raw_b, raw_c, true));
				
				loggedErrorAlready = false;
			} catch (PhidgetException e) {
				if (!loggedErrorAlready) {
					logger.error("Could not read out sensor values.");
					loggedErrorAlready = true;
				}
			}
			try {
				sleep(1000 / framerateSampling);
			} catch (InterruptedException e) {
			}
		}
	}

	public void sensorChanged(SensorChangeEvent sensorChangeEvent) {
		int sensorIndex = sensorChangeEvent.getIndex();
		int sensorValue = sensorChangeEvent.getValue();
		boolean joystick = false;

		switch (sensorIndex) {
		// save x value
		case 0:
			// raw_a = sensorValue;
			// lp.pushSample(new DataInertial(LaserPointer.class,
			// "LP-InertialSensor", raw_a, raw_b, raw_c, true));
			break;
		// save y value
		case 1:
			// raw_b = sensorValue;
			// lp.pushSample(new DataInertial(LaserPointer.class,
			// "LP-InertialSensor", raw_a, raw_b, raw_c, true));
			break;
		// push new z and saved x & y values
		case 2:
			// raw_c = sensorValue;
			// lp.pushSample(new DataInertial(LaserPointer.class,
			// "LP-InertialSensor", raw_a, raw_b, raw_c, true));
			break;
		case 6:
			joystick = false;
			if (sensorValue < 100)
				joystick = true;
			laserPointer.publish(new DataButton(Laserpointer.class, DataButton.BUTTON_STICK_LEFT, joystick));
			joystick = false;
			if (sensorValue > 900)
				joystick = true;
			laserPointer.publish(new DataButton(Laserpointer.class,	DataButton.BUTTON_STICK_RIGHT, joystick));
			break;
		case 7:
			joystick = false;
			if (sensorValue < 100)
				joystick = true;
			laserPointer.publish(new DataButton(Laserpointer.class, DataButton.BUTTON_STICK_UP, joystick));
			joystick = false;
			if (sensorValue > 900)
				joystick = true;
			laserPointer.publish(new DataButton(Laserpointer.class, DataButton.BUTTON_STICK_DOWN, joystick));
		}
	}

	public void toggleVibOn() {
		try {
			ifk.setOutputState(0, true);
		} catch (PhidgetException e) {
			logger.error("Could not change vibrator modus");
		}
	}

	public void toggleVibOff() {
		try {
			ifk.setOutputState(0, false);
		} catch (PhidgetException e) {
			logger.error("Could not change vibrator modus");
		}
	}

	public void close() {
		running = false;
		ifk.removeSensorChangeListener(this);
		ifk.removeInputChangeListener(this);

		// close the phidget
		try {
			ifk.close();
		} catch (PhidgetException e) {
			logger.error("Could not close phidget");
		}

		ifk = null;
	}
}
