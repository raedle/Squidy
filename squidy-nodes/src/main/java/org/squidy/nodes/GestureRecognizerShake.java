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
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

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
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.manager.data.impl.DataKey;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataString;
import org.squidy.manager.model.AbstractNode;
import org.wiigee.device.Device;
import org.wiigee.event.AccelerationListener;
import org.wiigee.event.ButtonListener;
import org.wiigee.event.GestureEvent;
import org.wiigee.event.GestureListener;


/**
 * <code>GestureRecognizerShake</code>.
 *
 * <pre>
 * Date: Nov 21, 2008
 * Time: 5:13:03 PM
 * </pre>
 *
 * @author Nicolas Hirrle, nihirrle@htwg-konstanz.de, University of Konstanz
 * @version 1.0
 */
@XmlType(name = "GestureRecognizerShake")
@Processor(name = "GestureRecognizerShake", types = { Processor.Type.FILTER }, tags = {
		"gesture", "recognizer", "phidget" })
public class GestureRecognizerShake extends AbstractNode implements
		GestureListener {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory
			.getLog(GestureRecognizerShake.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "threshold")
	@Property(name = "Threshold", description = "Value threshold for adding to recognizer")
	@TextField
	private double threshold = 0.02;

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	@XmlAttribute(name = "Loadgestures")
	@Property(name = "Loadgestures")
	@CheckBox
	private boolean loadGestures = true;

	public boolean isLoadGestures() {
		return loadGestures;
	}

	public void setLoadGestures(boolean loadGestures) {
		this.loadGestures = loadGestures;
	}

	@XmlAttribute(name = "InputIPhone")
	@Property(name = "InputIPhone")
	@CheckBox
	private boolean inputIPhone = true;

	public boolean isInputIPhone() {
		return inputIPhone;
	}

	public void setInputIPhone(boolean inputIPhone) {
		this.inputIPhone = inputIPhone;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private String gestureFolder = "ext/shake/gestures";
	private HashMap<Integer, Gestures> gesturesLoaded;
	private Device wiigee;
	private DataInertial lastInertial;
	private long lastIPhoneTouch;
	private boolean isRecognizing = false;
	private Object synch = new Object();

	/*
	 * (non-Javadoc)
	 *
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() {
		if (inputIPhone)
			gestureFolder = "ext/iphone/gestures";
		else
			gestureFolder = "ext/shake/gestures";

		lastInertial = new DataInertial(GestureRecognizerShake.class, 0.5f,
				0.5f, 0.5f);
		gesturesLoaded = new HashMap<Integer, Gestures>();

		wiigee = new Device(false);

		wiigee.setTrainButton(4);
		wiigee.setRecognitionButton(3);
		wiigee.setCloseGestureButton(8);
		wiigee.addGestureListener(this);
		if (loadGestures)
			loadGestures();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() {
		gesturesLoaded.clear();
	}

	public DataPosition2D process(DataPosition2D dataPosition2D) {
		if (inputIPhone) {
//			synchronized (synch) {
//				lastIPhoneTouch = System.currentTimeMillis();
//			}
//			// check first if old Recognition is running
//			if (!isRecognizing) {
//				startSampleRecognition();
//				isRecognizing = true;
//				checkWhenToStop();
//			}
			if (dataPosition2D.hasAttribute(iPhone.TOUCHES_BEGAN)){
				if (dataPosition2D.getAttribute(iPhone.TOUCHES_BEGAN).equals("TOUCHES_BEGAN")){
					startSampleRecognition();
				}
			}
			else if (dataPosition2D.hasAttribute(iPhone.TOUCHES_ENDED)){
				if (dataPosition2D.getAttribute(iPhone.TOUCHES_ENDED).equals("TOUCHES_ENDED")){
					stopSampleRecognition();
				}
			}
		}

		return null;
	}

//	private void checkWhenToStop() {
//		new Thread() {
//			public void run() {
//				synchronized (synch) {
//					long timeNow = System.currentTimeMillis();
//					while (timeNow - 50 > lastIPhoneTouch) {
//						try {
//							Thread.sleep(10);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						timeNow = System.currentTimeMillis();
//					}
//					stopSampleRecognition();
//					isRecognizing = false;
//				}
//			}
//		}.start();
//	}

	/**
	 * @param dataInertial
	 * @return
	 */
	public IData process(DataInertial dataInertial) {

		if (dataInertial.getAbsoluteValue() > (1 + threshold)
				|| dataInertial.getAbsoluteValue() < (1 - threshold)) {
			double[] w = { dataInertial.getX(), dataInertial.getY(),
					dataInertial.getZ() };
			if (inputIPhone) {
				double[] tmp = { dataInertial.getX() - lastInertial.getX(),
						dataInertial.getY() - lastInertial.getY(),
						dataInertial.getZ() - lastInertial.getZ() };
				w = tmp;
				lastInertial = dataInertial.getClone();
			}

			wiigee.fireAccelerationEvent(w);
			//DataInertial tmp = new DataInertial(GestureRecognizerShake.class, (Float) w[0], (Float)w[1], (Float)w[2]);
			//return tmp;
		}

		return null;
	}

	/**
	 * To Recognize with the ShakeSensor do following: 1. Click Shake-button and
	 * hold. The Shake Sensor vibrates 2. Do gesture 3. Release Shake-button.
	 * The Shake Sensor vibrates
	 *
	 * @param dataButton
	 * @return
	 */
	public IData process(DataDigital dataDigital) {
		if (dataDigital.hasAttribute(DataConstant.IDENTIFIER)) {
			if (dataDigital.getAttribute(DataConstant.IDENTIFIER).equals(
					"ShakeSK7")) {
				if (dataDigital.getFlag())
					startSampleRecognition();
				else
					stopSampleRecognition();
			}
		}
		return dataDigital;

	}

	/**
	 * To Recognize with ShakeAcceleromter and KeyboardInput do following: 1.
	 * Learn gesture multiple times while holding the 't'-key on Keyboard-Input
	 * Window. Releasing it marks the end of one recording process; 2. Finish
	 * training of unique gesture by pressing 'return'-key 3. Recognize gesture
	 * with 'space'-bar on Keyboard-Input Window 4. To save a gesture hit 's' on
	 * Keyboard-Input Window and follow the instructions on the console. To be
	 * done.
	 *
	 * @param dataButton
	 * @return
	 */
	public IData process(DataKey dataKey) {

		// keyType 84 for 't'; Hold 't' for learning a gesture. Releasing it
		// marks the end of the recording process
		if (dataKey.getKeyType() == 84) {
			if (dataKey.getFlag())
				startSampleLearning();
			else
				stopSampleLearning();
		}

		// keyType 10 for 'return-key'; finalizes learning process of a unique
		// gesture
		// NOTE: Gesture is not stored!
		else if (dataKey.getKeyType() == 10) {
			if (dataKey.getFlag()) {
				closeGesture();
			}
		}

		// keyType 32 for 'space-key'; hold to learn gesture
		// switch to learning mode and sample gesture
		else if (dataKey.getKeyType() == 32) {

			if (dataKey.getFlag()) {
				startSampleRecognition();
			} else {
				stopSampleRecognition();
			}
		}

		// keyType 83 for 's-key'; press to save gesture
		else if (dataKey.getKeyType() == 83 && dataKey.getFlag()) {
			saveGesture();
		}
		return null;
	}

	public void gestureReceived(GestureEvent event) {
		// DataGesture dataGesture = null;
		DataString dataString = null;

		DataKey dataKeyUp = null;
		DataKey dataKeyDown = null;
		DataButton dataButtonUp = null;
		DataButton dataButtonDown = null;
		if (event.isValid() && event.getProbability() > 0.8) {
			if (gesturesLoaded.containsKey(event.getId())) {
				System.out.println("New Gesture Event \""
						+ gesturesLoaded.get(event.getId()).getGesture()
						+ "\" with ID " + event.getId() + " and Probability "
						+ event.getProbability() + " from Source "
						+ event.getSource());
				String gesture = gesturesLoaded.get(event.getId()).getGesture();
				if (gesture.contains("right")) {
					dataKeyUp = new DataKey(GestureRecognizerShake.class,
							KeyEvent.VK_RIGHT, true);
					dataKeyDown = new DataKey(GestureRecognizerShake.class,
							KeyEvent.VK_RIGHT, false);
					dataButtonUp = new DataButton(GestureRecognizerShake.class,
							MouseEvent.BUTTON1, true);
					dataButtonUp = new DataButton(GestureRecognizerShake.class,
							MouseEvent.BUTTON1, false);
				} else if (gesture.contains("left")) {
					dataKeyUp = new DataKey(GestureRecognizerShake.class,
							KeyEvent.VK_LEFT, true);
					dataKeyDown = new DataKey(GestureRecognizerShake.class,
							KeyEvent.VK_LEFT, false);
				} else if (gesture.contains("circle")) {
					dataKeyUp = new DataKey(GestureRecognizerShake.class,
							KeyEvent.VK_F5, true);
					dataKeyDown = new DataKey(GestureRecognizerShake.class,
							KeyEvent.VK_F5, false);
				}
				dataString = new DataString(GestureRecognizerShake.class,
						gesturesLoaded.get(event.getId()).getGesture());
			} else {
				LOG.info("New Gesture Event (not loaded) with ID "
						+ event.getId() + " and Probability "
						+ event.getProbability() + " from Source "
						+ event.getSource());

				dataString = new DataString(GestureRecognizerShake.class,
						gesturesLoaded.get(event.getId()).getGesture());
			}
			dataString.setAttribute(DataConstant.SESSION_ID, event.getId());

			if (dataString != null)
				publish(dataString);
			if (dataKeyUp != null)
				publish(dataKeyUp);
			if (dataKeyDown != null)
				publish(dataKeyDown);
		}
	}

	public void startSampleLearning() {
		fireButtonPressedEvent(4);
	}

	public void startSampleRecognition() {
		fireButtonPressedEvent(3);
	}

	public void closeGesture() {
		fireButtonPressedEvent(8);
	}

	public void stopSampleLearning() {
		fireButtonReleasedEvent(4);
	}

	public void stopSampleRecognition() {
		fireButtonReleasedEvent(3);
	}

	/**
	 * Fires a button pressed event.
	 *
	 * @param button
	 *            Integer value of the pressed button.
	 */
	public void fireButtonPressedEvent(int button) {

		wiigee.fireButtonPressedEvent(button);
	}

	/**
	 * Fires a button released event.
	 */
	public void fireButtonReleasedEvent(int button) {
		wiigee.fireButtonReleasedEvent(button);

	}

	private void loadGestures() {
		new Thread() {
			public void run() {
				File f = new File(gestureFolder);
				String[] filenames = f.list();
				if (filenames == null) {
					System.out.println("Failed to load gestures");
					return;
				} else if (filenames.length == 0) {
					System.out.println("No gestures found");
					return;
				} else {
					for (int i = 0; i < filenames.length; i++) {
						int minus = filenames[i].lastIndexOf('-');
						int dot = filenames[i].lastIndexOf('.');
						String file = filenames[i].substring(0, dot);
						if (file.isEmpty())
							continue;
						String gesture = filenames[i].substring(minus + 1, dot);
						String idstr = filenames[i].substring(0, minus);

						int id = Integer.parseInt(idstr);
						Gestures g = new Gestures(id, gesture);
						gesturesLoaded.put(id, g);

						wiigee.loadGesture(gestureFolder + "/" + file);
						System.out.println("Gesture " + gesture + " loaded");

						// int minus = filenames[i].lastIndexOf('-');
						// int dot = filenames[i].lastIndexOf('.');
						// String file = filenames[i].substring(0, dot);
						// String gesture = filenames[i].substring(0, minus);
						// String idstr = filenames[i].substring(minus + 1,
						// dot);
						//
						// int id = Integer.parseInt(idstr);
						// Gestures g = new Gestures(id, gesture);
						// gesturesLoaded.put(id, g);
						//
						// wiigee.loadGesture(gestureFolder + "/" + file);
						// System.out.println("Gesture " + gesture + " loaded");
					}
				}
			}
		}.start();
	}

	private void saveGesture() {
		new Thread() {
			public void run() {
				System.out.println("Do you want to save a Gesture? (y/n)");
				BufferedReader console = new BufferedReader(
						new InputStreamReader(System.in));
				String str = new String();
				try {
					str = console.readLine();
					if (str.equals("y")) {
						System.out.println("Gesture ID: ");
						String idstr = console.readLine();
						int id = Integer.parseInt(idstr);

						if (id < 10)
							idstr = "0" + id;
						System.out
								.println("Gesture Name (don't use '-' and '.'): ");
						BufferedReader consoleF = new BufferedReader(
								new InputStreamReader(System.in));
						String filename = new String();
						filename = consoleF.readLine();

						Gestures g = new Gestures(id, filename);
						gesturesLoaded.put(g.id, g);
						wiigee.saveGesture(id, gestureFolder + "/" + idstr + "-"
								+ filename);
						// wiigee.saveGesture(id, gestureFolder + "/" + filename
						// + "-" + id);
						System.out.println("Gesture " + g.getGesture()
								+ " saved!");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	private static class Gestures {
		public Gestures(int id, String gesture) {
			this.id = id;
			this.gesture = gesture;
		}

		private int id;
		private String gesture;

		public String getGesture() {
			return gesture;
		}

	}
}
