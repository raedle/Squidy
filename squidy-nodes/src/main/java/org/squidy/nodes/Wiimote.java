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
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import motej.IrPoint;
import motej.Mote;
import motej.event.AccelerometerEvent;
import motej.event.AccelerometerListener;
import motej.event.CoreButtonEvent;
import motej.event.CoreButtonListener;
import motej.event.IrCameraEvent;
import motej.event.IrCameraListener;
import motej.event.MoteDisconnectedEvent;
import motej.event.MoteDisconnectedListener;
import motej.request.ReportModeRequest;
import motejx.adapters.IrDistanceAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.manager.data.impl.DataKey;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.wiimote.SimpleMoteFinder;


/**
 * <code>WiiMote</code>.
 * 
 * <pre>
 * Date: Feb 13, 2008
 * Time: 12:49:13 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: Wiimote.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "Wiimote")
@Processor(
	name = "Wiimote",
	icon = "/org/squidy/nodes/image/48x48/wiimote.png",
	types = { Processor.Type.INPUT, Processor.Type.OUTPUT },
	description = "/org/squidy/nodes/html/Wiimote.html",
	tags = { "wiimote", "nintendo", "wii", "tracking", "ir", "infrared", "camera" },
	status = Status.UNSTABLE
)
public class Wiimote extends AbstractNode {

	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Wiimote.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	// -Dbluecove.jsr82.psm_minimum_off=true
	
	// @XmlValue()
	// @XmlElementWrapper(name = "bluetooth-addresses")
	private Collection<String> bluethoothAddresses;

	@XmlAttribute(name = "bluetooth-address")
	@Property(
		name = "Bluetooth address",
		description = "Bluetooth address of Wiimote device."
	)
	@TextField
	private String bluetoothAddress = "00:00:00:00:00";

	/**
	 * @return the bluetoothAddress
	 */
	public String getBluetoothAddress() {
		return bluetoothAddress;
	}

	/**
	 * @param bluetoothAddress
	 *            the bluetoothAddress to set
	 */
	public void setBluetoothAddress(String bluetoothAddress) {
		this.bluetoothAddress = bluetoothAddress;

		if (mote != null) {
			mote.rumble(rumbleTime);
		}
	}
	
	// ################################################################################

	// @XmlAttribute(name = "ir-light-distance")
	// @Adjustable(@Description("Distance between IR light transceivers."))
	// private double irLightDistance = 150d;
	//	
	// /**
	// * @return the irLightDistance
	// */
	// public double getIrLightDistance() {
	// return irLightDistance;
	// }
	//	
	// /**
	// * @param irLightDistance
	// * the irLightDistance to set
	// */
	// public void setIrLightDistance(double irLightDistance) {
	// this.irLightDistance = irLightDistance;
	//	
	// if (irCamera3D != null) {
	// irCamera3D.setIrLightDistance(irLightDistance);
	// }
	//	
	// if (mote != null) {
	// mote.rumble(rumbleTime);
	// }
	// }

	@XmlAttribute(name = "rumble-time")
	@Property(
		name = "Rumble time",
		description = "Rumble time in milliseconds"
	)
	@TextField
	private long rumbleTime = 100;

	/**
	 * @return the rumbleTime
	 */
	public long getRumbleTime() {
		return rumbleTime;
	}

	/**
	 * @param rumbleTime
	 *            the rumbleTime to set
	 */
	public void setRumbleTime(long rumbleTime) {
		this.rumbleTime = rumbleTime;

		if (mote != null) {
			mote.rumble(rumbleTime);
		}
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	// The connected mote.
	protected Mote mote;

	// The core button listener.
	protected CoreButtonListener coreButtonListener;
	
	// The 2 dimensional IR adapter.
	protected IrCameraListener irCamera2DListener;

	// The 3 dimensional IR adapter.
	protected IrDistanceAdapter irCamera3DListener;

	// The accelerometer listener.
	protected AccelerometerListener<Mote> accelerometerListener;

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() {

		System.out.println("start");
		
//		BlueCoveImpl.setConfigProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true");
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Initializing Wiimote.");
		}

		coreButtonListener = createCoreButtonListener();
		irCamera2DListener = createIrCameraListener2D();
		irCamera3DListener = createIrCameraListener3D();
		accelerometerListener = createAccelerometerListener();

		Thread startup = new Thread(new Runnable() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {

				final JFrame frame = new JFrame("Waiting for WiiMote");
				frame.add(new JLabel("Waiting for WiiMote - Please press button 1 and 2!"));
				frame.pack();
				frame.setVisible(true);
				
				mote = null;

				if (mote == null) {
					// If bluetooth address has been set using this address
					// instead
					// of searching for WiiMote devices.
					// Declaration of bluetooth address will result in a faster
					// connection.
					if (bluetoothAddress == null || "00:00:00:00:00".equals(bluetoothAddress)|| "".equals(bluetoothAddress)) {
						SimpleMoteFinder moteFinder = new SimpleMoteFinder();
						mote = moteFinder.findMote();
					} else {
						mote = new Mote(bluetoothAddress);
					}
				}
				else {
					System.out.println("using mote: " + mote);
				}
				mote.remoteMoteDisconnectedListener(new MoteDisconnectedListener<Mote>() {

					/* (non-Javadoc)
					 * @see motej.event.MoteDisconnectedListener#moteDisconnected(motej.event.MoteDisconnectedEvent)
					 */
					public void moteDisconnected(MoteDisconnectedEvent<Mote> evt) {
						System.out.println("Mote has been disconnected: " + evt.getSource());
					}
				});
				
				internalInitialize(frame, mote);
				
				frame.setVisible(false);
			}
		}, "WiimoteStartup");
		startup.start();
	}

	/**
	 * @param frame
	 * @param mote
	 */
	private void internalInitialize(JFrame frame, Mote mote) {
		
		// Add button listener.
		mote.addCoreButtonListener(coreButtonListener);

		// Add accelerometer listener.
		mote.addAccelerometerListener(accelerometerListener);

		// Add IR camera listener for 2 dimensional position.
		mote.addIrCameraListener(irCamera2DListener);

		// Add IR camera listener for 3 dimensional position.
		// todo [RR]: do not sample both (DataPosition2D and DataPosition3D) at
		// the same time.
		// mote.addIrCameraListener(irCamera3D);

		mote.enableIrCamera();
		mote.setReportMode(ReportModeRequest.DATA_REPORT_0x37);

		mote.setPlayerLeds(new boolean[] { true, false, false, false });
		mote.rumble(rumbleTime);

		frame.setVisible(false);
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() {

		System.out.println("stop");

		if (LOG.isDebugEnabled()) {
			LOG.debug("Closing Wiimote.");
		}

		Thread shutdown = new Thread(new Runnable() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {

				System.out.println("Shuttinng down Wiimote");

				final JFrame frame = new JFrame("Shutting down Wiimote");
				frame.add(new JLabel("Shutting down Wiimote"));
				frame.pack();
				frame.setVisible(true);

				if (mote != null) {

					mote.setPlayerLeds(new boolean[] { false, false, false, true });
					mote.setReportMode(ReportModeRequest.DATA_REPORT_0x30);
					mote.removeCoreButtonListener(coreButtonListener);
					mote.removeIrCameraListener(irCamera2DListener);
					mote.removeAccelerometerListener(accelerometerListener);
					mote.disableIrCamera();
					mote.disconnect();
				}
				
				frame.setVisible(false);
				
//				BlueCoveImpl.shutdown();
			}

		}, "WiimoteShutdown");
		shutdown.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.input.AbstractDriver#process(org.squidy.manager.data.IData)
	 */
	public IData process(IData data) {

		// Tactile feedback.
		if (mote != null) {
			mote.rumble(rumbleTime);
		}
		return null;
	}

	/**
	 * Adds a <code>CoreButtonListener</code> to the parameter mote.
	 * 
	 * @param mote
	 *            The mote that gets the core button listener.
	 */
	private CoreButtonListener createCoreButtonListener() {
		return new CoreButtonListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see motej.event.CoreButtonListener#buttonPressed(motej.event.CoreButtonEvent)
			 */
			public void buttonPressed(CoreButtonEvent evt) {
				// Propagate buttons as button events.
				publish(new DataButton(Wiimote.class, DataButton.BUTTON_1, evt
						.isButtonAPressed()));
				// TODO Roman: Change button back to BUTTON_3.
				publish(new DataButton(Wiimote.class, DataButton.BUTTON_10, evt
						.isButtonBPressed()));

				publish(new DataButton(Wiimote.class, DataButton.BUTTON_A, evt
						.isButtonAPressed()));
				publish(new DataButton(Wiimote.class, DataButton.BUTTON_B, evt
						.isButtonBPressed()));

				// Home button pressed results in a VK_H key event.
				publish(new DataKey(Wiimote.class, KeyEvent.VK_H, evt.isButtonHomePressed()));

				// Propagate pad keys. -> UP, RIGHT, DOWN, LEFT result in VK_UP,
				// VK_RIGHT, VK_DOWN, VK_LEFT.
				publish(new DataKey(Wiimote.class, KeyEvent.VK_UP, evt.isDPadUpPressed()));
				publish(new DataKey(Wiimote.class, KeyEvent.VK_RIGHT, evt
						.isDPadRightPressed()));
				publish(new DataKey(Wiimote.class, KeyEvent.VK_DOWN, evt.isDPadDownPressed()));
				publish(new DataKey(Wiimote.class, KeyEvent.VK_LEFT, evt.isDPadLeftPressed()));

				// Button (-) and (+) pressed -> result in VK_MINUS and VK_PLUS
				// key event.
				publish(new DataKey(Wiimote.class, KeyEvent.VK_MINUS, evt
						.isButtonMinusPressed()));
				publish(new DataKey(Wiimote.class, KeyEvent.VK_PLUS, evt
						.isButtonPlusPressed()));
				publish(new DataButton(Wiimote.class, DataButton.BUTTON_PLUS, evt
						.isButtonPlusPressed()));

				// Button (1) and (2) pressed -> result in VK_1 and VK_2 key
				// event.
				publish(new DataKey(Wiimote.class, KeyEvent.VK_1, evt.isButtonOnePressed()));
				publish(new DataKey(Wiimote.class, KeyEvent.VK_2, evt.isButtonTwoPressed()));
			}
		};
	}

	/**
	 * Creates the IR camera listener for 2 dimensional positions.
	 * 
	 * @return The configured 2D IR camera listener.
	 */
	private IrCameraListener createIrCameraListener2D() {
		return new IrCameraListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see motej.event.IrCameraListener#irImageChanged(motej.event.IrCameraEvent)
			 */
			public void irImageChanged(IrCameraEvent evt) {
				IrPoint point = evt.getIrPoint(0);

				// Adjust coordinates to relative values from either minimum 0
				// or maximum 1.
				// !!! y value could be more than 1 caused by a 1023 value if IR
				// transceiver diode is out-of-reach.
				double x = point.getX() / 1023;
				double y = point.getY() / 767;
				
				// Send data position event.
				publish(new DataPosition2D(Wiimote.class, x, y));
			}
		};
	}

	/**
	 * Creates the IR camera listener for 3 dimensional positions.
	 * 
	 * @return The configured 3D IR camera listener.
	 */
	private IrDistanceAdapter createIrCameraListener3D() {
		// return new IrDistanceAdapter(irLightDistance) {
		return new IrDistanceAdapter(150d) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see motejx.adapters.IrDistanceAdapter#positionChanged(double,
			 *      double, double)
			 */
			@Override
			public void positionChanged(double x, double y, double z) {
				publish(new DataPosition3D(Wiimote.class, x, y, z, -1));
			}
		};
	}

	/**
	 * Creates an accelerometer listener.
	 * 
	 * @return The configured accelerometer listener.
	 */
	private AccelerometerListener<Mote> createAccelerometerListener() {
		return new AccelerometerListener<Mote>() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see motej.event.AccelerometerListener#accelerometerChanged(motej.event.AccelerometerEvent)
			 */
			public void accelerometerChanged(AccelerometerEvent<Mote> evt) {
				int x = evt.getX();
				int y = evt.getY();
				int z = evt.getZ();

				publish(new DataInertial(Wiimote.class, x, y, z, false));
			}
		};
	}
}
