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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.reactivision.TuioClient;
import org.squidy.nodes.reactivision.TuioCursor;
import org.squidy.nodes.reactivision.TuioListener;
import org.squidy.nodes.reactivision.TuioObject;
import org.squidy.nodes.reactivision.remote.CalibrationWindow;
import org.squidy.nodes.reactivision.remote.control.CameraSettingsContainer;
import org.squidy.nodes.reactivision.remote.control.ControlServer;
import org.squidy.nodes.reactivision.remote.control.FiducialSet;
import org.squidy.nodes.reactivision.remote.image.ImageServer;


/**
 * <code>ReacTIVision</code>.
 * 
 * <pre>
 * Date: Okt 30, 2009
 * Time: 15:08:41 PM
 * </pre>
 * 
 * @author Andreas Ergenzinger, <a
 *         href="mailto:andreas.ergenzinger@uni-konstanz.de">andreas.ergenzinger@uni-konstanz.de</a>
 *         Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: ReacTIVision.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
@XmlType(name = "reacTIVision")
@Processor(
	name = "reacTIVision",
	icon = "/org/squidy/nodes/image/48x48/reactivision.png",
	description = "/org/squidy/nodes/html/ReacTIVision.html",
	types = { Processor.Type.INPUT },
	tags = { "reactivision", "fiducial", "id", "token" }
)
public class ReacTIVision extends AbstractNode implements TuioListener {
	
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ReacTIVision.class);

	public static final DataConstant TUIO_TOKEN = DataConstant.get(
			String.class, "TUIO_TOKEN");
	public static final DataConstant TUIO_CURSOR = DataConstant.get(
			String.class, "TUIO_CURSOR");
	public static final DataConstant TUIO_ALIVE = DataConstant.get(
			Vector.class, "TUIO_ALIVE");
	
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		if (enableRemoteConfiguration)
			startCommServers();
	}
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "reactivision-identifier")
	@Property(
		name = "ReacTIVision Identifier",
		description = "Information about which camera and ReacTIVision instance will connect to this node."
	)
	@TextField
	private String reactivisionIdentifier = "id";

	/**
	 * @return the port
	 */
	public final String getReactivisionIdentifier() {
		return reactivisionIdentifier;
	}
	
	public void setReactivisionIdentifier(String reactivisionIdentifier) {
		this.reactivisionIdentifier = reactivisionIdentifier;
	}
	
	
	
	@XmlAttribute(name = "tuio-port")
	@Property(
		name = "TUIO Port",
		description = "The port on which the reacTIVision node receives TUIO messages."
	)
	@TextField
	private int tuioPort = 4444;

	/**
	 * @return the port
	 */
	public final int getTuioPort() {
		return tuioPort;
	}

	/**
	 * Makes the following changes:
	 * <ul>
	 * <li>tuioPort = port</li>
	 * <li>controlServerPort = port + 1</li>
	 * <li>imageServerPort = port + 2</li>
	 * </ul>
	 * @param port the new value for the tuioPort variable
	 */
	public final void setTuioPort(int port) {
		//set all affected port numbers
		tuioPort = port;
		refreshControlServerPort(port + CONTROL_SERVER_OFFSET);
		refreshImageServerPort(port + IMAGE_SERVER_OFFSET);
		
		if (tuioClient != null) {
			stop();
			start();
		}
	}
	
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
	
	@XmlAttribute(name = "counter-clockwise-angle")
	@Property(name = "Counter clockwise angle", description = "Returns the object angle in a counter clockwise manner.")
	@CheckBox
	private boolean counterClockwiseAngle = false;

	public boolean isCounterClockwiseAngle() {
		return counterClockwiseAngle;

	}

	public void setCounterClockwiseAngle(boolean counterClockwiseAngle) {
		this.counterClockwiseAngle = counterClockwiseAngle;

	}
	
	//REMOTE CONFIGURATION
	
	private ControlServer controlServer;
	private ImageServer imageServer;
	private CalibrationWindow calibrationWindow;
	
	@XmlAttribute(name = "enable-remote-configuration")
	@Property(name = "Enable remote configuration", group = "Camera Configuration")
	@CheckBox
	private boolean enableRemoteConfiguration = false;

	public boolean isEnableRemoteConfiguration() {
		return enableRemoteConfiguration;
	}

	public void setEnableRemoteConfiguration(boolean enableRemoteConfiguration) {
		if (enableRemoteConfiguration == true) {
			int result = startCommServers();
			switch (result) {
				case 0:
					//everything is fine
					this.enableRemoteConfiguration = enableRemoteConfiguration;
					break; 
				case 1:
					showErrorPopUp("Could not create ControlServer, since port " + (tuioPort+1)
							+ " is already in use.");
					break;
				case 2:
					showErrorPopUp("Could not create ImageServer, since port " + (tuioPort+2)
							+ " is already in use.");
					break;
				case 3:
					showErrorPopUp("Could not create ControlServer and ImageServer, since "
							+ "ports " + (tuioPort+1) + " and " + (tuioPort+2) + " are already in use.");
			}
		} else {
			this.enableRemoteConfiguration = enableRemoteConfiguration;
			if (calibrationWindow != null)
				calibrationWindow.dispose();
			this.refreshGridCalibration(MODE_OFF);
			stopCommServers();
		}
	}
	
	private int startCommServers() {
		stopCommServers();
		controlServer = new ControlServer(this, tuioPort + CONTROL_SERVER_OFFSET);
		imageServer = new ImageServer(tuioPort + IMAGE_SERVER_OFFSET);
		int i = 0;
		if (!controlServer.start())
			i += 1;
		if (!imageServer.start())
			i += 2;
		return i;
	}
	
	private void stopCommServers() {
		if (controlServer != null)
			controlServer.stop();
		if (imageServer != null)
			imageServer.stop();
	}
	
	public static void showErrorPopUp(String errorMessage) {
		//get screen size
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		//create pop up
		final JFrame frame = new JFrame();
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		frame.setUndecorated(true);
		JLabel text = new JLabel(" " + errorMessage + " ");
		frame.getContentPane().setBackground(Color.RED);
		text.setForeground(Color.WHITE);
		frame.add(text);
		frame.pack();
		frame.setLocation((dim.width - frame.getWidth())/2, (dim.height - frame.getHeight())/2);
		frame.setVisible(true);
		frame.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				frame.dispose();
			}
		});
	}
	
	//############################################################################
	/**
	 * controlServerPort - tuioPort = ...
	 */
	private static final int CONTROL_SERVER_OFFSET = 1;
	
	@XmlAttribute(name = "control-server-port")
	@Property(
		name = "ControlServer Port",
		group = "Camera Configuration",
		description = "The port ReacTIVision will connect to in order to receive control messages."
	)
	@TextField
	private int controlServerPort = tuioPort + CONTROL_SERVER_OFFSET;

	public final int getControlServerPort() {
		return controlServerPort;
	}
	
	public final void setControlServerPort(int controlServerPort) {
		if (controlServerPort != this.controlServerPort) {
			//do not change port number
			//but show message ...
			publishNotification("ControlServer port number depends on TUIO port number.");
			//... and reset displayed value
			refreshControlServerPort(tuioPort + CONTROL_SERVER_OFFSET);
		}
	}
	
	public final void refreshControlServerPort(int controlServerPort) {
		fireStatusChange("controlServerPort", "", ((Integer)controlServerPort).toString());
		this.controlServerPort = controlServerPort;
	}
	
	//############################################################################
	
	private static final int IMAGE_SERVER_OFFSET = 2;
	
	@XmlAttribute(name = "image-server-port")
	@Property(
		name = "ImageServer Port",
		group = "Camera Configuration",
		description = "The port ReacTIVision will connect to in order to send image data to Squidy."
	)
	@TextField
	private int imageServerPort = tuioPort + IMAGE_SERVER_OFFSET;


	public final int getImageServerPort() {
		return imageServerPort;
	}
	
	public final void setImageServerPort(int imageServerPort) {
		if (imageServerPort != this.imageServerPort) {
			//do not change port number
			//but show message ...
			publishNotification("ImageServer port number depends on TUIO port number.");
			//... and reset displayed value
			refreshImageServerPort(tuioPort + IMAGE_SERVER_OFFSET);
		}
	}
	
	public final void refreshImageServerPort(int imageServerPort) {
		fireStatusChange("imageServerPort", "", ((Integer)imageServerPort).toString());
		this.imageServerPort = imageServerPort;
	}
	
	//############################################################################
	
	public final static int MODE_OFF = 0;
	public final static int MODE_ON = 1;
	
	@XmlAttribute(name = "grid-calibration")
	@Property(name = "Grid Calibration", group = "Camera Configuration")
	@ComboBox(domainProvider = OnOffModeDomainProvider.class)
	private int gridCalibration = MODE_OFF;
	
	public int getGridCalibration() {
		return gridCalibration;
	}
	
	public void setGridCalibration(int mode) {
		this.gridCalibration = mode;
		if (enableRemoteConfiguration) {
			if (mode == MODE_ON) {
				calibrationWindow = new CalibrationWindow(this, controlServer, imageServer);
				calibrationWindow.setVisible(true);
			} else {
				if (calibrationWindow != null)
					calibrationWindow.dispose();
			}
		} else if (mode == MODE_ON){
			//prevent setting mode to MODE_ON
			fireStatusChange("gridCalibration",
					MODE_ON, MODE_OFF);
		}
	}
	
	public void refreshGridCalibration(int mode) {
		fireStatusChange("gridCalibration",
				getGridCalibration(), mode);
	}
	
	
	@XmlAttribute(name = "framerate")
	@Property(name = "Framerate", group = "Camera Configuration", description = "Camera framerate")
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 80, showLabels = true, showTicks = true, majorTicks = 20, minorTicks = 10, snapToTicks = false)
	private int framerate = 20;

	public int getFramerate() {
		return framerate;
	}

	public void setFramerate(int framerate) {
		this.framerate = framerate;
		if (enableRemoteConfiguration) {
			controlServer.setFramerate((float)framerate);
			refreshCameraSettings();
		}
	}

	public void refreshFramerate(int framerate) {
		fireStatusChange("framerate", getFramerate(), framerate);
		this.framerate = framerate;
	}
	
	
	@XmlAttribute(name = "exposure-time")
	@Property(name = "Exposure Time", group = "Camera Configuration", description = "Camera exposure time")
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 50, showLabels = true, showTicks = true, majorTicks = 10, minorTicks = 5, snapToTicks = false)
	private int exposureTime = 20;

	public int getExposureTime() {
		return exposureTime;
	}

	public void setExposureTime(int exposureTime) {
		this.exposureTime = exposureTime;
		if (enableRemoteConfiguration) {
			controlServer.setExposureTime((float)exposureTime);
			refreshCameraSettings();
		}
	}

	public void refreshExposureTime(int exposureTime) {
		fireStatusChange("exposureTime", getExposureTime(), exposureTime);
		this.exposureTime = exposureTime;
	}
	
	
	@XmlAttribute(name = "pixel-clock")
	@Property(name = "Pixel Clock", group = "Camera Configuration", description = "Camera pixel clock value")
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 60, showLabels = true, showTicks = true, majorTicks = 10, minorTicks = 1, snapToTicks = false)
	private int pixelClock = 30;

	public int getPixelClock() {
		return pixelClock;
	}

	public void setPixelClock(int pixelClock) {
		this.pixelClock = pixelClock;
		if (enableRemoteConfiguration) {
			controlServer.setPixelClock(pixelClock);
			refreshCameraSettings();
		}
	}

	public void refreshPixelClock(int pixelClock) {
		fireStatusChange("pixelClock", getPixelClock(), pixelClock);
		this.pixelClock = pixelClock;
	}
	
	
	@XmlAttribute(name = "hardware-gain")
	@Property(name = "Hardware Gain", group = "Camera Configuration", description = "Hardware gain")
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 100, showLabels = true, showTicks = true, majorTicks = 20, minorTicks = 5, snapToTicks = false)
	private int hardwareGain = 30;

	public int getHardwareGain() {
		return hardwareGain;
	}

	public void setHardwareGain(int hardwareGain) {
		this.hardwareGain = hardwareGain;
		if (enableRemoteConfiguration) {
			controlServer.setHardwareGain(hardwareGain);
			refreshCameraSettings();
		}
	}

	public void refreshHardwareGain(int hardwareGain) {
		fireStatusChange("hardwareGain", getHardwareGain(), hardwareGain);
		this.hardwareGain = hardwareGain;
	}
	
	
	@XmlAttribute(name = "edge-enhancement")
	@Property(name = "Edge Enhancement", group = "Camera Configuration", description = "Edge enhancement")
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 2, showLabels = true, showTicks = true, majorTicks = 1, minorTicks = 1, snapToTicks = true)
	private int edgeEnhancement = 0;

	public int getEdgeEnhancement() {
		return edgeEnhancement;
	}

	public void setEdgeEnhancement(int edgeEnhancement) {
		this.edgeEnhancement = edgeEnhancement;
		if (enableRemoteConfiguration) {
			controlServer.setEdgeEnhancement(edgeEnhancement);
			refreshCameraSettings();
		}
	}

	public void refreshEdgeEnhancement(int edgeEnhancement) {
		fireStatusChange("edgeEnhancement", getEdgeEnhancement(), edgeEnhancement);
		this.edgeEnhancement = edgeEnhancement;
	}
	
	
	@XmlAttribute(name = "gamma")
	@Property(name = "Gamma", group = "Camera Configuration", description = "Gamma. Default value is 100.")
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 1000, showLabels = true, showTicks = true, majorTicks = 250, minorTicks = 50, snapToTicks = false)
	private int gamma = 100;

	public int getGamma() {
		return gamma;
	}

	public void setGamma(int gamma) {
		this.gamma = gamma;
		if (enableRemoteConfiguration) {
			controlServer.setGamma(gamma);
			refreshCameraSettings();
		}
	}

	public void refreshGamma(int gamma) {
		fireStatusChange("gamma", getGamma(), gamma);
		this.gamma = gamma;
	}
	
	
	public void refreshCameraSettings() {
		final CameraSettingsContainer container = controlServer.getCameraSettings();
		if (container == null)
			return;
		//else
		
		refreshFramerate(Math.round(container.framerate));
		refreshExposureTime(Math.round(container.exposureTime));
		refreshPixelClock(container.pixelClock);
		refreshHardwareGain(container.hardwareGain);
		refreshEdgeEnhancement(container.edgeEnhancement);
		refreshGamma(container.gamma);
		
	}
	
	@XmlAttribute(name = "fiducial-set")
	@Property(name = "Fiducial Set", group = "Camera Configuration")
	@ComboBox(domainProvider = FiducialSetDomainProvider.class)
	private FiducialSet fiducialSet = FiducialSet.AMOEBA_DEFAULT;
	
	public FiducialSet getFiducialSet() {
		return fiducialSet;
	}
	
	public void setFiducialSet(FiducialSet fiducialSet) {
		this.fiducialSet = fiducialSet;
		
		//make ReacTIVision write the desired set into its configuration file,
		//terminate and restart ReacTIVision 
		if (enableRemoteConfiguration)
			controlServer.setFiducialSet(fiducialSet);
	}
	
	public void refreshFiducialSet(FiducialSet fiducialSet) {
		fireStatusChange("fiducialSet", getFiducialSet(), fiducialSet);
		this.fiducialSet = fiducialSet;
	}
	
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private TuioClient tuioClient;
	
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
		
		tuioClient = new TuioClient(tuioPort);

		tuioClient.addTuioListener(this);
		tuioClient.connect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		if (tuioClient != null) {
			tuioClient.disconnect();
			tuioClient = null;
		}
		activePeriodicThread = null;
	}
	
	public static class OnOffModeDomainProvider implements DomainProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.squidy.manager.data.domainprovider.DomainProvider#getValues
		 * ()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[2];
			values[0] = new ComboBoxItemWrapper(MODE_OFF, "Off");
			values[1] = new ComboBoxItemWrapper(MODE_ON, "On");
			
			return values;
		}
	}
	
	
	public static class FiducialSetDomainProvider implements DomainProvider {
		
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[FiducialSet.size()];
			FiducialSet[] fSetValues = FiducialSet.values();
			for (int i = 0; i < values.length; ++i)
				values[i] = new ComboBoxItemWrapper(fSetValues[i], fSetValues[i].name().toLowerCase());
			
			return values;
		}
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
							publishObject(null, "periodic");
						}
					}
					activePeriodicThread = null;
				}
			});
			activePeriodicThread.start();
		}
	}
	
	// ################################################################################
	// BEGIN OF TuioListener
	// ################################################################################
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.input.impl.reactivision.TuioListener
	 * #addTuioObject
	 * (org.squidy.manager.input.impl.reactivision.TuioObject)
	 */
	public void addTuioObject(TuioObject tuioObject) {
		publishObject(tuioObject, "add");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.input.impl.reactivision.TuioListener
	 * #updateTuioObject
	 * (org.squidy.manager.input.impl.reactivision.TuioObject)
	 */
	public void updateTuioObject(TuioObject tuioObject) {
		publishObject(tuioObject, "update");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.input.impl.reactivision.TuioListener
	 * #removeTuioObject
	 * (org.squidy.manager.input.impl.reactivision.TuioObject)
	 */
	public void removeTuioObject(TuioObject tuioObject) {
		publishObject(tuioObject, "remove");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.input.impl.reactivision.TuioListener
	 * #addTuioCursor
	 * (org.squidy.manager.input.impl.reactivision.TuioCursor)
	 */
	public void addTuioCursor(TuioCursor tuioCursor) {
		// ignore
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.input.impl.reactivision.TuioListener
	 * #updateTuioCursor
	 * (org.squidy.manager.input.impl.reactivision.TuioCursor)
	 */
	public void updateTuioCursor(TuioCursor tuioCursor) {
		// ignore
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.input.impl.reactivision.TuioListener
	 * #removeTuioCursor
	 * (org.squidy.manager.input.impl.reactivision.TuioCursor)
	 */
	public void removeTuioCursor(TuioCursor tuioCursor) {
		// ignore
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.input.impl.reactivision.TuioListener
	 * #refresh(long)
	 */
	public void refresh(long timestamp) {
		// ignore
	}
	
	/**
	 * @param tuioObject
	 * @param objectState
	 */
	private void publishObject(TuioObject tuioObject, String objectState) {
		needPeriodicMessage = false;
		
		DataPosition2D dataPosition2D;
		if (tuioObject != null) {
			dataPosition2D = new DataPosition2D(ReacTIVision.class, tuioObject.getX(), tuioObject.getY());
			dataPosition2D.setAttribute(TUIO.ORIGIN_ADDRESS, "/tuio/2Dobj");
			dataPosition2D.setAttribute(DataConstant.SESSION_ID, ((Long) tuioObject.getSessionID()).intValue());
			dataPosition2D.setAttribute(TUIO.FIDUCIAL_ID, tuioObject.getFiducialID());
			dataPosition2D.setAttribute(TUIO.MOVEMENT_VECTOR_X, tuioObject.getSpeedX());
			dataPosition2D.setAttribute(TUIO.MOVEMENT_VECTOR_Y, tuioObject.getSpeedY());
			dataPosition2D.setAttribute(TUIO.ROTATION_VECTOR_A, tuioObject.getRotationSpeed());
			dataPosition2D.setAttribute(TUIO.ANGLE_A, counterClockwiseAngle ? ((float) ((2 * Math.PI) - tuioObject.getAngle())) : tuioObject.getAngle());
			dataPosition2D.setAttribute(TUIO.ROTATION_ACCELERATION, tuioObject.getRotationAccel());
			dataPosition2D.setAttribute(TUIO.MOTION_ACCELERATION, tuioObject.getMotionAccel());
			dataPosition2D.setAttribute(TUIO.OBJECT_STATE, objectState);
		}
		else {
			dataPosition2D = new DataPosition2D(ReacTIVision.class, 0, 0);
			dataPosition2D.setAttribute(TUIO.OBJECT_STATE, objectState);
		}
		
		publish(dataPosition2D);
	}
	
	// ################################################################################
	// END OF TuioListener
	// ################################################################################
}
