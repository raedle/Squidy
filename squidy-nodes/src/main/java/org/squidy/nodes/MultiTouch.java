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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.Designer;
import org.squidy.manager.ProcessException;
import org.squidy.manager.commander.ControlClient;
import org.squidy.manager.commander.command.SwitchableCommand;
import org.squidy.manager.commander.command.utility.Switch;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.domainprovider.impl.EndianDomainProvider;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;
import org.squidy.nodes.plugin.PatternScreen;
import org.squidy.nodes.tracking.CameraCallback;
import org.squidy.nodes.tracking.CameraConfigComm;
import org.squidy.nodes.tracking.LaserServer;
import org.squidy.nodes.tracking.config.ConfigNotifier;
import org.squidy.nodes.tracking.proxy.ProxyServer;

import com.illposed.osc.Endian;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;


/**
 * <code>MultiTouch</code>.
 * 
 * <pre>
 * Date: Feb 28, 2008
 * Time: 3:34:49 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @author Toni Schmidt, <a href="mailto:Toni.Schmidt@uni-konstanz.de">Toni.
 *         Schmidt@uni-konstanz.de</a>, University of Konstanz
 * @version $Id
 * @since 1.0
 * 
 */
@XmlType(name = "MultiTouch")
@Processor(name = "Multi-Touch", icon = "/org/squidy/nodes/image/48x48/multitouch-icon.png", description = "/org/squidy/nodes/html/MultiTouch.html", types = { Processor.Type.INPUT }, tags = {
		"multi", "touch", "multi-touch", "multitouch", "tracking" }, status = Status.STABLE)
public class MultiTouch extends AbstractNode implements CameraCallback {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(MultiTouch.class);

	// ################################################################################
	// BEGIN OF DATA CONSTANTS
	// ################################################################################

	// ################################################################################
	// BEGIN OF TUIO DEFINED PARAMETERS
	// ################################################################################

	public static final DataConstant TUIO_ORIGIN_ADDRESS = DataConstant.get(
			String.class, "TUIO_ORIGIN_ADDRESS");

	public static final DataConstant TUIO_MOVEMENT_VECTOR_X = DataConstant.get(
			Float.class, "TUIO_MOVEMENT_VECTOR_X");
	public static final DataConstant TUIO_MOVEMENT_VECTOR_Y = DataConstant.get(
			Float.class, "TUIO_MOVEMENT_VECTOR_Y");
	public static final DataConstant TUIO_MOTION_ACCELERATION = DataConstant
			.get(Float.class, "TUIO_MOTION_ACCELERATION");
	public static final DataConstant TUIO_ANGLE_A = DataConstant.get(
			Float.class, "TUIO_ANGLE_A");
	public static final DataConstant TUIO_ANGLE_B = DataConstant.get(
			Float.class, "TUIO_ANGLE_B");
	public static final DataConstant TUIO_ANGLE_C = DataConstant.get(
			Float.class, "TUIO_ANGLE_C");
	public static final DataConstant SOURCE = DataConstant.get(Integer.class,
			"SOURCE");
	// ################################################################################
	// END OF TUIO DEFINED PARAMETERS
	// ################################################################################

	// ################################################################################
	// BEGIN OF FREE DEFINED PARAMETERS
	// ################################################################################

	public static final DataConstant TUIO_HAND_WIDTH = DataConstant.get(
			Float.class, "TUIO_HAND_WIDTH");
	public static final DataConstant TUIO_HAND_HEIGHT = DataConstant.get(
			Float.class, "TUIO_HAND_HEIGHT");

	// ################################################################################
	// END OF FREE DEFINED PARAMETERS
	// ################################################################################

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	/*
	 * @XmlAttribute(name = "tracking-sensitivity")
	 * 
	 * @Property(name = "Tracking Sensitivity", group = "Camera Configuration",
	 * description = "Tracking Sensitivity")
	 * 
	 * @Slider(type = Integer.class, minimumValue = 0, maximumValue = 200,
	 * showLabels = true, showTicks = true, majorTicks = 100, minorTicks = 10,
	 * snapToTicks = false) private int trackingSensitivity = 200;
	 * 
	 * 
	 * public int getTrackingSensitivity() { return trackingSensitivity; }
	 * 
	 * public void setTrackingSensitivity(int trackingSensitivity) { float
	 * fSensitivity = (float)trackingSensitivity / 10.0f;
	 * this.trackingSensitivity = trackingSensitivity;
	 * cameraConfigComm.sendParameter("tracking-sensitivity", "float",
	 * Float.toString(fSensitivity)); }
	 */
	@XmlAttribute(name = "tracking-sensitivity")
	@Property(name = "Tracking Sensitivity", group = "Camera Configuration", description = "Tracking Sensitivity")
	@TextField
	private int trackingSensitivity = 200;

	public int getTrackingSensitivity() {
		return trackingSensitivity;
	}

	public void setTrackingSensitivity(int trackingSensitivity) {

		float fSensitivity = (float) trackingSensitivity / 10.0f;
		this.trackingSensitivity = trackingSensitivity;
		cameraConfigComm.sendParameter("tracking-sensitivity", "float", Float
				.toString(fSensitivity));
	}

	@XmlAttribute(name = "pixelclock")
	@Property(name = "Pixelclock", group = "Camera Configuration", description = "Pixelclock")
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 60, showLabels = true, showTicks = true, majorTicks = 10, minorTicks = 1, snapToTicks = false)
	private int pixelclock = 60;

	public int getPixelclock() {
		return pixelclock;
	}

	public void setPixelclock(int pixelclock) {
		this.pixelclock = pixelclock;
		cameraConfigComm.sendParameter("pixelclock", "int", Integer
				.toString(pixelclock));
		cameraConfigComm
				.sendParameter("exposure", "int", Integer.toString(200));
		cameraConfigComm.sendParameter("framerate", "int", Integer
				.toString(120));

	}

	public void refreshPixelclock(int pixelclock) {
		fireStatusChange("pixelclock", getPixelclock(), pixelclock);
		this.pixelclock = pixelclock;
	}

	@XmlAttribute(name = "framerate")
	@Property(name = "Framerate", group = "Camera Configuration", description = "Framerate")
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 200, showLabels = true, showTicks = true, majorTicks = 50, minorTicks = 10, snapToTicks = false)
	private int framerate = 120;

	public int getFramerate() {
		return framerate;
	}

	public void setFramerate(int framerate) {
		this.framerate = framerate;
		// cameraConfigComm.sendParameter("pixelclock", "int",
		// Integer.toString(pixelclock));
		cameraConfigComm.sendParameter("framerate", "int", Integer
				.toString(framerate));
		// cameraConfigComm.sendParameter("exposure", "int",
		// Integer.toString(exposure));

	}

	public void refreshFramerate(int framerate) {
		fireStatusChange("framerate", getFramerate(), framerate);
		this.framerate = framerate;
	}

	@XmlAttribute(name = "exposure")
	@Property(name = "Exposure", group = "Camera Configuration", description = "Exposure")
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 200, showLabels = true, showTicks = true, majorTicks = 50, minorTicks = 10, snapToTicks = false)
	private int exposure = 9;

	public int getExposure() {
		return exposure;
	}

	public void setExposure(int exposure) {
		this.exposure = exposure;
		// cameraConfigComm.sendParameter("pixelclock", "int",
		// Integer.toString(pixelclock));
		// cameraConfigComm.sendParameter("framerate", "int",
		// Integer.toString(framerate));
		cameraConfigComm.sendParameter("exposure", "int", Integer
				.toString(exposure));

	}

	public void refreshExposure(int exposure) {
		fireStatusChange("exposure", getExposure(), exposure);
		this.exposure = exposure;
	}

	@XmlAttribute(name = "dynamic-backdiff")
	@Property(name = "Dynamic Background Subtraction", group = "Camera Configuration", description = "Turn dynamic background subtraction on/off")
	@CheckBox
	private boolean dynamicBackdiff = false;

	public boolean isDynamicBackdiff() {
		return dynamicBackdiff;
	}

	public void setDynamicBackdiff(boolean dynamicBackdiff) {
		this.dynamicBackdiff = dynamicBackdiff;
		cameraConfigComm.sendParameter("dynamic-backdiff", "bool", Boolean
				.toString(dynamicBackdiff));
	}

	@XmlAttribute(name = "backdiff-tresh")
	@Property(name = "Background Subtraction Treshold", group = "Camera Configuration", description = "All pixels brighter (i.e. larger) than the background subtraction treshold will be set to 255 (i.e. pure white)")
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 255, showLabels = true, showTicks = true, majorTicks = 50, minorTicks = 10, snapToTicks = false)
	private int backdiffTresh = 100;

	public int getBackdiffTresh() {
		return backdiffTresh;
	}

	public void setBackdiffTresh(int backdiffTresh) {
		this.backdiffTresh = backdiffTresh;
		cameraConfigComm.sendParameter("backdiff-tresh", "int", Integer
				.toString(backdiffTresh));
	}

	@XmlAttribute(name = "dynamic-backdiff-speed")
	@Property(name = "Dynamic Background Subtraction Frequency", group = "Camera Configuration", description = "Camera ID", suffix = "ms")
	@TextField
	private int dynamicBackdiffSpeed = 1000;

	public int getDynamicBackdiffSpeed() {
		return dynamicBackdiffSpeed;
	}

	public void setDynamicBackdiffSpeed(int dynamicBackdiffSpeed) {
		this.dynamicBackdiffSpeed = dynamicBackdiffSpeed;
		cameraConfigComm.sendParameter("dynamic-backdiff-speed", "int", Integer
				.toString(dynamicBackdiffSpeed));
	}

	@XmlAttribute(name = "track-fingers")
	@Property(name = "Track Fingers", group = "Camera Configuration", description = "Turn finger tracking on/off")
	@CheckBox
	private boolean trackFingers = false;

	public boolean isTrackFingers() {
		return trackFingers;
	}

	public void setTrackFingers(boolean trackFingers) {
		this.trackFingers = trackFingers;
		if (trackFingers == true) {

			sendAllTrackingParams();
			setPixelclock(getPixelclock());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cameraConfigComm.sendParameter("track-fingers", "bool", Boolean
					.toString(trackFingers));
			setPixelclock(getPixelclock());
		} else
			cameraConfigComm.sendParameter("track-fingers", "bool", Boolean
					.toString(trackFingers));
	}

	@XmlAttribute(name = "track-contours")
	@Property(name = "Track Contours", group = "Camera Configuration", description = "Turn contour tracking on/off")
	@CheckBox
	private boolean trackContours = false;

	public boolean isTrackContours() {
		return trackContours;
	}

	public void setTrackContours(boolean trackContours) {
		this.trackContours = trackContours;
		cameraConfigComm.sendParameter("track-contours", "bool", Boolean
				.toString(trackContours));
	}

	/*
	 * @XmlAttribute(name = "remote-squidy-port")
	 * 
	 * @Property(name = "Remote Port", group = "Camera Calibration", description
	 * = "Remote port of Squidy Coontroller")
	 * 
	 * @TextField private int remoteSquidyPort = 9999; public int
	 * getRemoteSquidyPort() { return remoteSquidyPort; }
	 * 
	 * public void setRemoteSquidyPort(int remoteSquidyPort) {
	 * this.remoteSquidyPort = remoteSquidyPort; }
	 */

	@XmlAttribute(name = "pattern-width")
	@Property(name = "Pattern Width", group = "Camera Calibration", description = "Calibration Pattern Width")
	@TextField
	private int patternW = 5;

	public int getPatternW() {
		return patternW;
	}

	public void setPatternW(int patternW) {
		this.patternW = patternW;

		if (imageDisplay != null) {
			imageDisplay.setRequiredCornerPoints(patternW * patternH);
		}

		cameraConfigComm.sendParameter("pattern-width", "int", Integer
				.toString(patternW));
	}

	@XmlAttribute(name = "pattern-height")
	@Property(name = "Pattern Height", group = "Camera Calibration", description = "Calibration Pattern Height")
	@TextField
	private int patternH = 5;

	public int getPatternH() {
		return patternH;
	}

	public void setPatternH(int patternH) {
		this.patternH = patternH;

		if (imageDisplay != null) {
			imageDisplay.setRequiredCornerPoints(patternW * patternH);
		}

		cameraConfigComm.sendParameter("pattern-height", "int", Integer
				.toString(patternH));
	}

	@XmlAttribute(name = "screen-res-w")
	@Property(name = "Screen Resolution Width", group = "Camera Calibration", description = "Screen pixel width")
	@TextField
	private int screenResW = 1920;

	public int getScreenResW() {
		return screenResW;
	}

	public void setScreenResW(int screenResW) {
		this.screenResW = screenResW;
	}

	@XmlAttribute(name = "screen-res-h")
	@Property(name = "Screen Resolution Height", group = "Camera Calibration", description = "Screen pixel height")
	@TextField
	private int screenResH = 1080;

	public int getScreenResH() {
		return screenResH;
	}

	public void setScreenResH(int screenResH) {
		this.screenResH = screenResH;
	}

	@XmlAttribute(name = "image-stream-mode")
	@Property(name = "Image Stream Mode", group = "Camera Configuration")
	@ComboBox(domainProvider = ModeDomainProvider.class)
	private int mode = MODE_OFF;

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;

		if (mode == MODE_OFF) {
			cameraConfigComm.sendParameter("stream_image", "bool", "false");
			frame.setVisible(false);
		} else {
			frame.setVisible(true);
			cameraConfigComm.sendParameter("stream_image", "bool", "true");
			cameraConfigComm.sendParameter("image-stream-mode", "int", Integer
					.toString(mode));
		}
	}

	@XmlAttribute(name = "pattern-device")
	@Property(name = "Screen", group = "Camera Calibration")
	@ComboBox(domainProvider = PatternDomainProvider.class)
	private int patternDevice = 0;

	public int getPatternDevice() {
		return patternDevice;
	}

	public void setPatternDevice(int patternDevice) {
		this.patternDevice = patternDevice;
	}

	@XmlAttribute(name = "display-pattern")
	@Property(name = "Display Pattern", group = "Camera Calibration", description = "Turn calibration pattern on/off")
	@CheckBox
	private boolean displayPattern = false;

	public boolean isDisplayPattern() {
		return displayPattern;
	}

	public void setDisplayPattern(boolean displayPattern) {

		InetAddress address = null;
		try {
			address = InetAddress.getByName(getRemoteAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		try {
			/*
			 * if (client != null) { client.close(); client = null; }
			 */

			if (client == null) {
				System.out.println("Create new Control Client");
				client = new ControlClient(address, getRemotePort());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		String id = Integer.toString(getPatternDevice());
		String deviceId = "\\Display" + id;

		SwitchableCommand command = new PatternScreen(deviceId,
				getScreenResW(), getScreenResH(), getPatternW() - 1,
				getPatternH() - 1);
		command.setState(displayPattern ? Switch.ON : Switch.OFF);
		client.send(command);
		Designer.getInstance().requestFocus();

		this.displayPattern = displayPattern;
	}

	@XmlAttribute(name = "remote-address")
	@Property(name = "Remote address", group = "Remote Connection")
	@TextField
	private String remoteAddress = "127.0.0.1";

	/**
	 * @return the remoteAddress
	 */
	public final String getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * @param remoteAddress
	 *            the remoteAddress to set
	 */
	public final void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
		cameraConfigComm.sendParameter("remote-address", "String",
				remoteAddress);
	}

	// ################################################################################
	
	private static final int REMOTE_PORT_OFFSET = 4200;	//offsets relative to cameraID
	private static final int OSC_PORT_OUT_OFFSET = 4000;
	private static final int OSC_PORT_IN_OFFSET = 4100;
	
	@XmlAttribute(name = "remote-port")
	@Property(name = "Remote port", group = "Remote Connection")
	@TextField
	private int remotePort = REMOTE_PORT_OFFSET;

	/**
	 * @return the remotePort
	 */
	public final int getRemotePort() {
		return remotePort;
	}

	/**
	 * @param remotePort
	 *            the remotePort to set
	 */
	public final void setRemotePort(int remotePort) {
		if (remotePort != this.remotePort) {
			//do not change port number
			//but show message ...
			publishNotification("The Remote Port number depends on the Camera ID field.");
			//... and reset displayed value
			refreshRemotePort(cameraID + REMOTE_PORT_OFFSET);
		}
	}
	
	public final void refreshRemotePort(int remotePort) {
		fireStatusChange("remotePort", "", ((Integer)remotePort).toString());
		this.remotePort = remotePort;
	}
	
	@XmlAttribute(name = "osc-port-out")
	@Property(name = "OSC Port Out", group = "Remote Connection")
	@TextField
	private int oscPortConfigOut = OSC_PORT_OUT_OFFSET;

	public int getOscPortConfigOut() {
		return oscPortConfigOut;
	}

	public void setOscPortConfigOut(int oscPortConfigOut) {
		if (oscPortConfigOut != this.oscPortConfigOut) {
			//do not change port number
			//but show message ...
			publishNotification("OSC Port Out depends on the Camera ID field.");
			//... and reset displayed value
			refreshOscPortConfigOut(cameraID + OSC_PORT_OUT_OFFSET);
		}
	}
	
	public void refreshOscPortConfigOut(int oscPortConfigOut) {
		fireStatusChange("oscPortConfigOut", "", ((Integer)oscPortConfigOut).toString());
		this.oscPortConfigOut = oscPortConfigOut;
		if (cameraConfigComm != null) {
			cameraConfigComm.setPortOutgoing(oscPortConfigOut);
			cameraConfigComm.sendParameter("osc-port-out", "int", Integer
					.toString(oscPortConfigOut));
		}
	}
	
	
	@XmlAttribute(name = "osc-port-in")
	@Property(name = "OSC Port In", group = "Remote Connection")
	@TextField
	private int oscPortConfigIn = OSC_PORT_IN_OFFSET;

	public int getOscPortConfigIn() {
		return oscPortConfigIn;
	}
	
	public void setOscPortConfigIn(int oscPortConfigIn) {
		if (oscPortConfigIn != this.oscPortConfigIn) {
			//do not change port number
			//but show message ...
			publishNotification("OSC Port In depends on the Camera ID field.");
			//... and reset displayed value
			refreshOscPortConfigIn(cameraID + OSC_PORT_IN_OFFSET);
		}
	}
	
	public void refreshOscPortConfigIn(int oscPortConfigIn) {
		fireStatusChange("oscPortConfigIn", "", ((Integer)oscPortConfigIn).toString());
		this.oscPortConfigIn = oscPortConfigIn;
		if (cameraConfigComm != null) {
			cameraConfigComm.setPortIncoming(oscPortConfigIn);
			cameraConfigComm.sendParameter("osc-port-in", "int", Integer
					.toString(oscPortConfigIn));
		}
	}
	
	
	@XmlAttribute(name = "image-server-port")
	@Property(name = "Image Streaming Port", group = "Remote Connection")
	@TextField
	private int imageServerPort = 7777;

	public int getImageServerPort() {
		return imageServerPort;
	}

	public void setImageServerPort(int imageServerPort) {
		this.imageServerPort = imageServerPort;
		cameraConfigComm.setImageServerPort(imageServerPort);
		cameraConfigComm.sendParameter("image-server-port", "int", Integer
				.toString(imageServerPort));
	}

	@XmlAttribute(name = "show-pattern")
	@Property(name = "Show pattern", description = "Whether a pattern should be shown for calibration frame or not.")
	@CheckBox
	private boolean showPattern = true;

	/**
	 * @return the showPattern
	 */
	public final boolean isShowPattern() {
		return showPattern;
	}

	/**
	 * @param showPattern
	 *            the showPattern to set
	 */
	public final void setShowPattern(boolean showPattern) {
		this.showPattern = showPattern;
	}

	// ################################################################################

	@XmlAttribute(name = "show-white")
	@Property(name = "Show white", description = "Whether a white fullscreen image should be shown for background subtraction when cameras get started.")
	@CheckBox
	private boolean showWhite = true;

	/**
	 * @return the showWhite
	 */
	public final boolean isShowWhite() {
		return showWhite;
	}

	/**
	 * @param showWhite
	 *            the showWhite to set
	 */
	public final void setShowWhite(boolean showWhite) {
		this.showWhite = showWhite;
	}

	@XmlAttribute(name = "corner-points")
	public String cornerPointsStr = "";

	public String getCornerPointsStr() {
		return cornerPointsStr;
	}

	public void setCornerPointsStr(String cornerPointsStr) {
		this.cornerPointsStr = cornerPointsStr;
	}

	// ################################################################################

	public final void setPortIncoming(int portIncoming) {
		this.portIncoming = portIncoming;
		cameraConfigComm.sendParameter("port-tuio", "int", Integer
				.toString(portIncoming));
	}

	@XmlAttribute(name = "repaint-img")
	@Property(name = "Show live image in camera window", group = "Camera Configuration", description = "Show live image in camera window on/off")
	@CheckBox
	private boolean repaintImg = false;

	public boolean isRepaintImg() {
		return repaintImg;
	}

	public void setRepaintImg(boolean repaintImg) {
		this.repaintImg = repaintImg;
		cameraConfigComm.sendParameter("repaint-img", "bool", Boolean
				.toString(repaintImg));
	}

	@XmlAttribute(name = "print-fps")
	@Property(name = "Print Camera FPS", group = "Camera Configuration", description = "Print camera fps to console on/off")
	@CheckBox
	private boolean printFPS = false;

	public boolean isPrintFPS() {
		return printFPS;
	}

	public void setPrintFPS(boolean printFPS) {
		this.printFPS = printFPS;
		cameraConfigComm.sendParameter("print-fps", "bool", Boolean
				.toString(printFPS));
	}

	@XmlAttribute(name = "start-tracking-automatically")
	@Property(name = "Start camera automatically", group = "Camera Configuration", description = "Start Tracking software automatically on/off")
	@CheckBox
	private boolean startTrackingAutomatically = false;

	public boolean isStartTrackingAutomatically() {
		return startTrackingAutomatically;
	}

	public void setStartTrackingAutomatically(boolean startTrackingAutomatically) {
		this.startTrackingAutomatically = startTrackingAutomatically;
	}

	

	@XmlAttribute(name = "camera-id")
	@Property(name = "Camera ID", group = "Advanced Configuration", description = "Camera ID")
	@TextField
	private int cameraID = 37;

	public int getCameraID() {
		return cameraID;
	}

	public void setCameraID(int cameraID) {
		this.cameraID = cameraID;
		refreshRemotePort(cameraID + REMOTE_PORT_OFFSET);
		this.refreshOscPortConfigOut(cameraID + OSC_PORT_OUT_OFFSET);
		this.refreshOscPortConfigIn(cameraID + OSC_PORT_IN_OFFSET);
	}

	@XmlAttribute(name = "homogenization")
	@Property(name = "Homogenization", group = "Advanced Configuration", description = "Turn homogenization on/off")
	@CheckBox
	private boolean homogenization = false;

	public boolean isHomogenization() {
		return homogenization;
	}

	public void setHomogenization(boolean homogenization) {
		this.homogenization = homogenization;
		cameraConfigComm.sendParameter("homogenization", "bool", Boolean
				.toString(homogenization));
	}

	@XmlAttribute(name = "backdiff")
	@Property(name = "Background Subtraction", group = "Advanced Configuration", description = "Turn initial background subtraction on/off")
	@CheckBox
	private boolean backdiff = false;

	public boolean isBackdiff() {
		return backdiff;
	}

	public void setBackdiff(boolean backdiff) {
		this.backdiff = backdiff;
		cameraConfigComm.sendParameter("backdiff", "bool", Boolean
				.toString(backdiff));
	}

	@XmlAttribute(name = "subsampling")
	@Property(name = "Subsampling", group = "Advanced Configuration", description = "Camera Subsampling property")
	@TextField
	private int subsampling = 3;

	public int getSubsampling() {
		return subsampling;
	}

	public void setSubsampling(int subsampling) {
		this.subsampling = subsampling;
		cameraConfigComm.sendParameter("subsampling", "int", Integer
				.toString(subsampling));
	}

	@XmlAttribute(name = "backdiff-scale-tresh")
	@Property(name = "Backdiff Scale Treshold", group = "Advanced Configuration", description = "All image points above this treshold will be scaled up according to the parameter Upscale Factor")
	@TextField
	private int backdiffScaleTresh = 2;

	public int getBackdiffScaleTresh() {
		return backdiffScaleTresh;
	}

	public void setBackdiffScaleTresh(int backdiffScaleTresh) {
		this.backdiffScaleTresh = backdiffScaleTresh;
		cameraConfigComm.sendParameter("backdiff-scale-tresh", "int", Integer
				.toString(backdiffScaleTresh));
	}

	@XmlAttribute(name = "upscale-factor")
	@Property(name = "Upscale Factor", group = "Advanced Configuration", description = "Upscale Factor for the Background Image")
	@TextField
	private float upscaleFactor = 12.0f;

	public float getUpscaleFactor() {
		return upscaleFactor;
	}

	public void setUpscaleFactor(float upscaleFactor) {
		this.upscaleFactor = upscaleFactor;
		cameraConfigComm.sendParameter("upscale-factor", "float", Float
				.toString(upscaleFactor));
	}

	@XmlAttribute(name = "downscale-factor")
	@Property(name = "Downscale Factor", group = "Advanced Configuration", description = "Downscale Factor for the Background Image")
	@TextField
	private float downscaleFactor = 4.0f;

	public float getDownscaleFactor() {
		return downscaleFactor;
	}

	public void setDownscaleFactor(float downscaleFactor) {
		this.downscaleFactor = downscaleFactor;
		cameraConfigComm.sendParameter("downscale-factor", "float", Float
				.toString(downscaleFactor));
	}

	@XmlAttribute(name = "erode-dist")
	@Property(name = "Erode Distance", group = "Advanced Configuration", description = "Distance of the Erode Filter")
	@TextField
	private int erodeDist = 2;

	public int getErodeDist() {
		return erodeDist;
	}

	public void setErodeDist(int erodeDist) {
		this.erodeDist = erodeDist;
		cameraConfigComm.sendParameter("erode-dist", "int", Integer
				.toString(erodeDist));
	}

	@XmlAttribute(name = "dilate-dist")
	@Property(name = "Dilate Distance", group = "Advanced Configuration", description = "Distance of the Dilate Filter")
	@TextField
	private int dilateDist = 2;

	public int getDilateDist() {
		return dilateDist;
	}

	public void setDilateDist(int dilateDist) {
		this.dilateDist = dilateDist;
		cameraConfigComm.sendParameter("dilate-dist", "int", Integer
				.toString(dilateDist));
	}

	@XmlAttribute(name = "erode-tresh")
	@Property(name = "Erode Treshold", group = "Advanced Configuration", description = "Treshold of the Erode Filter")
	@TextField
	private int erodeTresh = 12;

	public int getErodeTresh() {
		return erodeTresh;
	}

	public void setErodeTresh(int erodeTresh) {
		this.erodeTresh = erodeTresh;
		cameraConfigComm.sendParameter("erode-tresh", "int", Integer
				.toString(erodeTresh));
	}

	@XmlAttribute(name = "dilate-tresh")
	@Property(name = "Dilate Treshold", group = "Advanced Configuration", description = "Treshold of the Dilate Filter")
	@TextField
	private int dilateTresh = 12;

	public int getDilateTresh() {
		return dilateTresh;
	}

	public void setDilateTresh(int dilateTresh) {
		this.dilateTresh = dilateTresh;
		cameraConfigComm.sendParameter("dilate-tresh", "int", Integer
				.toString(dilateTresh));
	}

	@XmlAttribute(name = "upscale-factor-2")
	@Property(name = "Upscale Factor 2", group = "Advanced Configuration", description = "Final Upscale Factor")
	@TextField
	private float upscaleFactor2 = 12.0f;

	public float getUpscaleFactor2() {
		return upscaleFactor2;
	}

	public void setUpscaleFactor2(float upscaleFactor2) {
		this.upscaleFactor2 = upscaleFactor2;
		cameraConfigComm.sendParameter("upscale-factor-2", "float", Float
				.toString(upscaleFactor2));
	}

	@XmlAttribute(name = "simple-tracking")
	@Property(name = "Simple Tracking", group = "Advanced Configuration", description = "Turn on for Laserpointer-Tracking")
	@CheckBox
	private boolean simpleTracking = false;

	public boolean isSimpleTracking() {
		return simpleTracking;
	}

	public void setSimpleTracking(boolean simpleTracking) {
		this.simpleTracking = simpleTracking;
		cameraConfigComm.sendParameter("simple-tracking", "bool", Boolean
				.toString(simpleTracking));

	}

	@XmlAttribute(name = "min-blob-size")
	@Property(name = "Minimum Blob Size", group = "Advanced Configuration", description = "")
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 30, showLabels = true, showTicks = true, majorTicks = 10, minorTicks = 1, snapToTicks = false)
	private int minBlobSize = 4;

	public int getMinBlobSize() {
		return minBlobSize;
	}

	public void setMinBlobSize(int minBlobSize) {
		this.minBlobSize = minBlobSize;
		cameraConfigComm.sendParameter("min-blob-size", "int", Integer
				.toString(minBlobSize));
	}

	@XmlAttribute(name = "x-from")
	@Property(name = "X From", group = "Coordinates Range", description = "")
	@TextField
	public float xFrom = 0.0f;

	public float getxFrom() {
		return xFrom;
	}

	public void setxFrom(float xFrom) {
		this.xFrom = xFrom;
	}

	@XmlAttribute(name = "x-end")
	@Property(name = "X End", group = "Coordinates Range", description = "")
	@TextField
	public float xEnd = 1.0f;

	public float getxEnd() {
		return xEnd;
	}

	public void setxEnd(float xEnd) {
		this.xEnd = xEnd;
	}

	@XmlAttribute(name = "y-from")
	@Property(name = "Y From", group = "Coordinates Range", description = "")
	@TextField
	public float yFrom = 0.0f;

	public float getyFrom() {
		return yFrom;
	}

	public void setyFrom(float yFrom) {
		this.yFrom = yFrom;
	}

	@XmlAttribute(name = "y-end")
	@Property(name = "Y End", group = "Coordinates Range", description = "")
	@TextField
	public float yEnd = 1.0f;

	public float getyEnd() {
		return yEnd;
	}

	public void setyEnd(float yEnd) {
		this.yEnd = yEnd;
	}

	@XmlAttribute(name = "address-outgoing")
	@Property(name = "Address outgoing", group = "Connection Settings", description = "The outgoing address for the TUIO server.")
	@TextField
	private String addressOutgoing = "127.0.0.1";

	/**
	 * @return the addressOutgoing
	 */
	public final String getAddressOutgoing() {
		return addressOutgoing;
	}

	/**
	 * @param addressOutgoing
	 *            the addressOutgoing to set
	 */
	public final void setAddressOutgoing(String addressOutgoing) {
		this.addressOutgoing = addressOutgoing;
	}

	@XmlAttribute(name = "port-outgoing")
	@Property(name = "Port outgoing", group = "Connection Settings", description = "The outgoing port for the TUIO server.")
	@TextField
	private int portOutgoing = 3333;

	/**
	 * @return the portOutgoing
	 */
	public final int getPortOutgoing() {
		return portOutgoing;
	}

	/**
	 * @param portOutgoing
	 *            the portOutgoing to set
	 */
	public final void setPortOutgoing(int portOutgoing) {
		this.portOutgoing = portOutgoing;
	}

	@XmlAttribute(name = "port-incoming")
	@Property(name = "Port incoming", group = "Connection Settings", description = "The incoming port for the TUIO server.")
	@TextField
	protected int portIncoming = 3333;

	/**
	 * @return the portIncoming
	 */
	public final int getPortIncoming() {
		return portIncoming;
	}

	@XmlAttribute(name = "endian")
	@Property(name = "Endian", description = "Indicates which endian strategy will be used to identify bytes or not.")
	@ComboBox(domainProvider = EndianDomainProvider.class)
	private Endian endian = Endian.LITTLE_ENDIAN;

	/**
	 * @return the endian
	 */
	public final Endian getEndian() {
		return endian;
	}

	/**
	 * @param endian
	 *            the endian to set
	 */
	public final void setEndian(Endian endian) {
		this.endian = endian;

		if (isProcessing()) {
			// Restart the osc server with new endian strategy.
			stopOSCServer();
			startOSCServer();
		}
	}

	/*
	 * 
	 * @XmlAttribute(name = "camera-image")
	 * 
	 * @Property(name = "Camera Image")
	 * 
	 * @ImagePanel private boolean blub = true; public boolean isBlub() { return
	 * blub; } public void setBlub(boolean blub) { this.blub = blub; }
	 */

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private LaserServer laserServer;
	private ControlClient client;
	// private ConfigManager configManager;
	private ProxyServer proxy;
	private ProxyServer imageServer;
	private Socket trackerSocket;
	public static int MODE_OFF = 0;
	public static int MODE_INPUT_IMAGE = 1;
	public static int MODE_PROCESSED_IMAGE = 2;
	public static int MODE_BACKDIFF = 3;

	// public static final int MODE_POINT_VELOCITY = 2;

	private CameraConfigComm cameraConfigComm;// = new
	// CameraConfigComm(remoteAddress,
	// 4444, 4445,
	// Endian.LITTLE_ENDIAN, this);

	private Vector<ConfigNotifier> configUpdate = new Vector<ConfigNotifier>();

	private int aoix = 0;
	private int aoiy = 0;
	private int aoiw = 0;
	private int aoih = 0;

	private Collection<Process> cameraProcesses = new ArrayList<Process>();
	private int camerasReady = 0;
	private boolean isTracking = false;

	// private LaserConfigClient laserConfigClient;

	private ImageDisplay imageDisplay;
	private JFrame frame;
	private boolean connectedToTracker = false;

	private OSCPortOut oscPortOut;
	private OSCPortIn oscPortIn;

	/**
	 * 
	 */
	protected void startOSCServer() {

		try {
			oscPortOut = new OSCPortOut(InetAddress.getByName(addressOutgoing),
					portOutgoing);
		} catch (SocketException e) {
			throw new ProcessException(e.getMessage(), e);
		} catch (UnknownHostException e) {
			throw new ProcessException(e.getMessage(), e);
		}

		try {
			oscPortIn = new OSCPortIn(portIncoming, endian);
		} catch (SocketException e) {
			throw new ProcessException(e.getMessage(), e);
		}

		oscPortIn.addListener("/tuio/2Dcur", new OSCListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.illposed.osc.OSCListener#acceptMessages(java.util.Date,
			 * com.illposed.osc.OSCMessage[])
			 */
			public void acceptMessages(Date time, OSCMessage[] messages) {

				List<DataPosition2D> cursors = new ArrayList<DataPosition2D>(1);

				int fseq = -1;
				for (OSCMessage message : messages) {
					Object[] arguments = message.getArguments();
					if ("fseq".equals(arguments[0])) {

						fseq = (Integer) arguments[1];
						// System.out.println(fseq);
					}
				}

				for (OSCMessage message : messages) {
					Object[] arguments = message.getArguments();

					if ("set".equals(arguments[0])) {

						int sessionId = (Integer) arguments[1];
						float x = (Float) arguments[2];
						float y = (Float) arguments[3];
						float movementVectorX = (Float) arguments[4];
						float movementVectorY = (Float) arguments[5];
						float motionAcceleration = (Float) arguments[6];

						float xFactor = getxEnd() - getxFrom();
						x = x * xFactor + getxFrom();

						float yFactor = getyEnd() - getyFrom();
						y = y * yFactor + getyFrom();

						DataPosition2D dataPosition2D = new DataPosition2D(
								TUIO.class, x, y);
						dataPosition2D.setAttribute(TUIO_ORIGIN_ADDRESS,
								"/tuio/2Dcur");
						dataPosition2D.setAttribute(
								DataConstant.FRAME_SEQUENCE_ID, fseq);
						dataPosition2D.setAttribute(DataConstant.SESSION_ID,
								sessionId);
						dataPosition2D.setAttribute(TUIO_MOVEMENT_VECTOR_X,
								movementVectorX);
						dataPosition2D.setAttribute(TUIO_MOVEMENT_VECTOR_Y,
								movementVectorY);
						dataPosition2D.setAttribute(TUIO_MOTION_ACCELERATION,
								motionAcceleration);
						dataPosition2D.setAttribute(SOURCE, getCameraID());
						cursors.add(dataPosition2D);
					}
				}

				publish(cursors);
			}
		});

		oscPortIn.addListener("/tuio/_sxyXYmaPP", new OSCListener() {
			public void acceptMessages(Date time, OSCMessage[] messages) {

				List<DataPosition2D> cursors = new ArrayList<DataPosition2D>(1);

				int fseq = -1;
				for (OSCMessage message : messages) {
					Object[] arguments = message.getArguments();
					if ("fseq".equals(arguments[0])) {
						fseq = (Integer) arguments[1];
					}
				}

				for (OSCMessage message : messages) {
					Object[] arguments = message.getArguments();

					if ("set".equals(arguments[0])) {

						int sessionId = (Integer) arguments[1];
						float x = (Float) arguments[2];
						float y = (Float) arguments[3];
						float movementVectorX = (Float) arguments[4];
						float movementVectorY = (Float) arguments[5];
						float motionAcceleration = (Float) arguments[6];
						float angleA = (Float) arguments[6];
						float handWidth = (Float) arguments[6];
						float handHeight = (Float) arguments[6];

						DataPosition2D dataPosition2D = new DataPosition2D(
								TUIO.class, x, y);
						dataPosition2D.setAttribute(TUIO_ORIGIN_ADDRESS,
								"/tuio/_sxyXYmaPP");
						dataPosition2D.setAttribute(
								DataConstant.FRAME_SEQUENCE_ID, fseq);
						dataPosition2D.setAttribute(DataConstant.SESSION_ID,
								sessionId);
						dataPosition2D.setAttribute(TUIO_MOVEMENT_VECTOR_X,
								movementVectorX);
						dataPosition2D.setAttribute(TUIO_MOVEMENT_VECTOR_Y,
								movementVectorY);
						dataPosition2D.setAttribute(TUIO_MOTION_ACCELERATION,
								motionAcceleration);
						dataPosition2D.setAttribute(TUIO_ANGLE_A, angleA);
						dataPosition2D.setAttribute(TUIO_HAND_WIDTH, handWidth);
						dataPosition2D.setAttribute(TUIO_HAND_HEIGHT,
								handHeight);

						cursors.add(dataPosition2D);
					}
				}

				publish(cursors);
			}
		});

		oscPortIn.addListener("/tuio/_sxyXYma", new OSCListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.illposed.osc.OSCListener#acceptMessages(java.util.Date,
			 * com.illposed.osc.OSCMessage[])
			 */
			public void acceptMessages(Date time, OSCMessage[] messages) {

				List<DataPosition2D> cursors = new ArrayList<DataPosition2D>(1);

				int fseq = -1;
				for (OSCMessage message : messages) {
					Object[] arguments = message.getArguments();
					if ("fseq".equals(arguments[0])) {
						fseq = (Integer) arguments[1];
					}
				}

				for (OSCMessage message : messages) {
					Object[] arguments = message.getArguments();

					if ("set".equals(arguments[0])) {

						int sessionId = (Integer) arguments[1];
						float x = (Float) arguments[2];
						float y = (Float) arguments[3];
						float movementVectorX = (Float) arguments[4];
						float movementVectorY = (Float) arguments[5];
						float motionAcceleration = (Float) arguments[6];
						float angleA = (Float) arguments[7];

						DataPosition2D dataPosition2D = new DataPosition2D(
								TUIO.class, x, y);
						dataPosition2D.setAttribute(TUIO_ORIGIN_ADDRESS,
								"/tuio/_sxyXYma");
						dataPosition2D.setAttribute(
								DataConstant.FRAME_SEQUENCE_ID, fseq);
						dataPosition2D.setAttribute(DataConstant.SESSION_ID,
								sessionId);
						dataPosition2D.setAttribute(TUIO_MOVEMENT_VECTOR_X,
								movementVectorX);
						dataPosition2D.setAttribute(TUIO_MOVEMENT_VECTOR_Y,
								movementVectorY);
						dataPosition2D.setAttribute(TUIO_MOTION_ACCELERATION,
								motionAcceleration);
						dataPosition2D.setAttribute(TUIO_ANGLE_A, angleA);

						cursors.add(dataPosition2D);
					}
				}

				publish(cursors);
			}
		});

		oscPortIn.startListening();
	}

	/**
	 * 
	 */
	protected void stopOSCServer() {
		if (oscPortIn != null) {
			oscPortIn.stopListening();
			oscPortIn.close();
			oscPortIn = null;
		}
	}

	@XmlAttribute(name = "display-blob-detector")
	@Property(name = "Display BlobDetector", group = "Filter Display Settings", description = "Activates/Deactivates window showing the identified (finger) blobs.")
	@CheckBox
	private boolean displayBlobDetector = false;

	public boolean isDisplayBlobDetector() {
		return displayBlobDetector;
	}

	public void setDisplayBlobDetector(boolean displayBlobDetector) {
		this.displayBlobDetector = displayBlobDetector;

		cameraConfigComm.sendParameter("display-blob-detector", "bool", Boolean
				.toString(displayBlobDetector));
	}

	@XmlAttribute(name = "update-background-image")
	@Property(name = "Update Background Image", group = "Testing", description = "Changing this property refreshes the background image.")
	@CheckBox
	private boolean updateBackgroundImage = false;

	public boolean isUpdateBackgroundImage() {
		return updateBackgroundImage;
	}

	public void setUpdateBackgroundImage(boolean updateBackgroundImage) {
		this.updateBackgroundImage = updateBackgroundImage;

		cameraConfigComm.sendParameter("update-background-image", "bool",
				"true");// always true
	}

	// ################################################################################
	// BEGIN OF Processing
	// ################################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.data.logic.ReflectionProcessable#
	 * beforeDataContainerProcessing
	 * (org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer) {

		List<DataPosition2D> dataPositions2D = DataUtility.getDataOfType(
				DataPosition2D.class, dataContainer);
		if (dataPositions2D.size() <= 0) {
			return super.preProcess(dataContainer);
		}

		OSCBundle bundle = new OSCBundle(new Date(dataContainer.getTimestamp()));

		OSCMessage fseq2DCur = new OSCMessage("/tuio/2Dcur");
		fseq2DCur.addArgument("fseq");
		bundle.addPacket(fseq2DCur);

		OSCMessage alive2DCur = new OSCMessage("/tuio/2Dcur");
		alive2DCur.addArgument("alive");
		bundle.addPacket(alive2DCur);

		OSCMessage fseq_sxyXYmaPP = new OSCMessage("/tuio/_sxyXYmaPP");
		fseq_sxyXYmaPP.addArgument("fseq");
		bundle.addPacket(fseq_sxyXYmaPP);

		OSCMessage alive_sxyXYmaPP = new OSCMessage("/tuio/_sxyXYmaPP");
		alive_sxyXYmaPP.addArgument("alive");
		bundle.addPacket(alive_sxyXYmaPP);

		OSCMessage fseq_sxyXYma = new OSCMessage("/tuio/_sxyXYma");
		fseq_sxyXYma.addArgument("fseq");
		bundle.addPacket(fseq_sxyXYma);

		OSCMessage alive_sxyXYma = new OSCMessage("/tuio/_sxyXYma");
		alive_sxyXYma.addArgument("alive");
		bundle.addPacket(alive_sxyXYma);
		/*
		 * OSCMessage fseq = new OSCMessage("/tuio/2Dcur");
		 * fseq.addArgument("fseq"); bundle.addPacket(fseq);
		 * 
		 * OSCMessage alive = new OSCMessage("/tuio/2Dcur");
		 * alive.addArgument("alive"); bundle.addPacket(alive);
		 */
		// Iterate 2D cursors.
		for (DataPosition2D dataPosition2D : dataPositions2D) {

			String originAddress = (String) dataPosition2D
					.getAttribute(TUIO_ORIGIN_ADDRESS);

			// Set frame sequence id
			/*
			 * if (fseq.getArguments().length < 2) {
			 * fseq.addArgument(dataPosition2D
			 * .getAttribute(DataConstant.FRAME_SEQUENCE_ID)); }
			 * alive.addArgument
			 * (dataPosition2D.getAttribute(DataConstant.SESSION_ID));
			 */

			OSCMessage set;
			if ("/tuio/_sxyXYmaPP".equals(originAddress)) {

				if (fseq_sxyXYmaPP.getArguments().length < 2) {
					fseq_sxyXYmaPP.addArgument(dataPosition2D
							.getAttribute(DataConstant.FRAME_SEQUENCE_ID));
				}
				alive_sxyXYmaPP.addArgument(dataPosition2D
						.getAttribute(DataConstant.SESSION_ID));

				set = prepare_sxyXYmaPP(dataPosition2D);
			} else if ("/tuio/_sxyXYma".equals(originAddress)) {
				if (fseq_sxyXYma.getArguments().length < 2) {
					fseq_sxyXYma.addArgument(dataPosition2D
							.getAttribute(DataConstant.FRAME_SEQUENCE_ID));
				}
				alive_sxyXYma.addArgument(dataPosition2D
						.getAttribute(DataConstant.SESSION_ID));

				set = prepare_sxyXYma(dataPosition2D);
			}
			// /tuio/2DCur
			else {
				if (fseq2DCur.getArguments().length < 2) {
					fseq2DCur.addArgument(dataPosition2D
							.getAttribute(DataConstant.FRAME_SEQUENCE_ID));
				}
				alive2DCur.addArgument(dataPosition2D
						.getAttribute(DataConstant.SESSION_ID));

				set = prepare2DCur(dataPosition2D);
			}

			bundle.addPacket(set);
		}

		try {
			oscPortOut.send(bundle);
		} catch (IOException e) {
			throw new ProcessException(e.getMessage(), e);
		}

		return super.preProcess(dataContainer);
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	private OSCMessage prepare2DCur(DataPosition2D dataPosition2D) {
		OSCMessage set = new OSCMessage("/tuio/2Dcur");
		set.addArgument("set");
		set.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));
		set.addArgument((float) dataPosition2D.getX());
		set.addArgument((float) dataPosition2D.getY());
		set.addArgument(dataPosition2D.getAttribute(TUIO_MOVEMENT_VECTOR_X));
		set.addArgument(dataPosition2D.getAttribute(TUIO_MOVEMENT_VECTOR_Y));
		set.addArgument(dataPosition2D.getAttribute(TUIO_MOTION_ACCELERATION));
		return set;
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	private OSCMessage prepare_sxyXYmaPP(DataPosition2D dataPosition2D) {
		OSCMessage set = new OSCMessage("/tuio/_sxyXYmaPP");
		set.addArgument("set");
		set.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));
		set.addArgument((float) dataPosition2D.getX());
		set.addArgument((float) dataPosition2D.getY());
		set.addArgument(dataPosition2D.getAttribute(TUIO_MOVEMENT_VECTOR_X));
		set.addArgument(dataPosition2D.getAttribute(TUIO_MOVEMENT_VECTOR_Y));
		set.addArgument(dataPosition2D.getAttribute(TUIO_MOTION_ACCELERATION));
		set.addArgument(dataPosition2D.getAttribute(TUIO_ANGLE_A));
		set.addArgument(dataPosition2D.getAttribute(TUIO_HAND_WIDTH));
		set.addArgument(dataPosition2D.getAttribute(TUIO_HAND_HEIGHT));
		return set;
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	private OSCMessage prepare_sxyXYma(DataPosition2D dataPosition2D) {
		OSCMessage set = new OSCMessage("/tuio/_sxyXYma");
		set.addArgument("set");
		set.addArgument(dataPosition2D.getAttribute(DataConstant.SESSION_ID));
		set.addArgument((float) dataPosition2D.getX());
		set.addArgument((float) dataPosition2D.getY());
		set.addArgument(dataPosition2D.getAttribute(TUIO_MOVEMENT_VECTOR_X));
		set.addArgument(dataPosition2D.getAttribute(TUIO_MOVEMENT_VECTOR_Y));
		set.addArgument(dataPosition2D.getAttribute(TUIO_MOTION_ACCELERATION));
		set.addArgument(dataPosition2D.getAttribute(TUIO_ANGLE_A));
		return set;
	}

	public static class PatternDomainProvider implements DomainProvider {
		public Object[] getValues() {
			GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[graphicsEnvironment
					.getScreenDevices().length];
			int cntr = 0;
			for (final GraphicsDevice device : graphicsEnvironment
					.getScreenDevices()) {
				String idStr = device.getIDstring();
				values[cntr] = new ComboBoxItemWrapper(cntr, idStr);
				cntr++;
			}
			return values;
		}

	}

	public static class ModeDomainProvider implements DomainProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.squidy.manager.data.domainprovider.DomainProvider#getValues
		 * ()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[5];
			values[0] = new ComboBoxItemWrapper(MODE_OFF, "Off");
			values[1] = new ComboBoxItemWrapper(MODE_INPUT_IMAGE,
					"Camera Input Image");
			values[2] = new ComboBoxItemWrapper(MODE_PROCESSED_IMAGE,
					"Processed Image");
			values[3] = new ComboBoxItemWrapper(MODE_BACKDIFF,
					"Background Image");
			// values[2] = new ComboBoxItemWrapper(MODE_POINT_VELOCITY,
			// "Dynamic Model");
			// values[3] = new ComboBoxItemWrapper(MODE_MULTI_WEIGHTED,
			// "Weighted Combination");
			// values[4] = new ComboBoxItemWrapper(MODE_MULIT_CHOICE,
			// "Best Choice (XOR)");

			return values;
		}
	}

	@SuppressWarnings("serial")
	class ImageDisplay extends JComponent implements MouseListener,
			MouseMotionListener {

		private ArrayList<Point> cornerPoints;
		private Dimension circleDim = new Dimension(6, 6);
		private BufferedImage img = null;

		private final Stroke CIRCLE_STROKE = new BasicStroke(1.5f);

		private int requiredCornerPoints;

		public int getRequiredCornerPoints() {
			return requiredCornerPoints;
		}

		public void setRequiredCornerPoints(int requiredCornerPoints) {
			this.requiredCornerPoints = requiredCornerPoints;

			setTitle("Image Display (" + cornerPoints.size() + " of "
					+ requiredCornerPoints + " corner points)");
		}

		public int getNumberOfCornerPoints() {
			if (cornerPoints == null)
				return 0;
			return cornerPoints.size();
		}

		public ImageDisplay() {
			cornerPoints = new ArrayList<Point>();
			if (cornerPointsStr == null)
				return;
			StringTokenizer tokens = new StringTokenizer(cornerPointsStr, ",");
			while (tokens.hasMoreTokens()) {
				int x = Integer.parseInt((String) tokens.nextElement());
				int y = Integer.parseInt((String) tokens.nextElement());
				Point p = new Point(x, y);
				cornerPoints.add(p);
			}

			addMouseListener(this);
			addMouseMotionListener(this);
			repaint();
		}

		public void mouseMoved(MouseEvent e) {

		}

		public void mouseDragged(MouseEvent e) {
			// if (e.getButton() == MouseEvent.BUTTON0) {
			Point p = e.getPoint();
			p.x += aoix;
			p.y += aoiy;
			if (isPointInArr(p)) {
				Point existingPoint = getPoint(p);
				existingPoint.x = p.x;
				existingPoint.y = p.y;
				repaint();
			}
			// }
		}

		public void mousePressed(MouseEvent e) {

		}

		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Point p = e.getPoint();
				p.x += aoix;
				p.y += aoiy;
				if (isPointInArr(p)) {
					Point existingPoint = getPoint(p);
					existingPoint.x = p.x;
					existingPoint.y = p.y;
					setCornerPointsStr(arrToStr());
					sendCornerPoints();
					repaint();
				}
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Point p = e.getPoint();
				p.x += aoix;
				p.y += aoiy;
				if (!isPointInArr(p)) {

					cornerPoints.add(p);

					setTitle("Image Display (" + cornerPoints.size() + " of "
							+ requiredCornerPoints + " corner points)");

					setCornerPointsStr(arrToStr());
					sendCornerPoints();

					repaint();
				}
			}
			if (e.getButton() == MouseEvent.BUTTON3) {
				Point p = e.getPoint();
				p.x += aoix;
				p.y += aoiy;
				if (isPointInArr(p)) {
					Point existingPoint = getPoint(p);
					cornerPoints.remove(existingPoint);

					setTitle("Image Display (" + cornerPoints.size() + " of "
							+ requiredCornerPoints + " corner points)");

					setCornerPointsStr(arrToStr());
					sendCornerPoints();
					// cornerPointsStr = arrToStr();
					repaint();
				}
			}

		}

		private void setTitle(String title) {
			Container c = getParent();
			while (c != null && !(c instanceof JFrame)) {
				c = c.getParent();
			}

			if (c != null) {
				((JFrame) c).setTitle(title);
			}
		}

		public void sendCornerPoints() {
			int strSize = cornerPoints.size() * 2;
			String[] cornerArr = new String[strSize];
			int cntr = 0;
			for (int i = 0; i < strSize; i += 2) {
				Point p = cornerPoints.get(cntr);
				cornerArr[i] = Integer.toString(p.x);
				cornerArr[i + 1] = Integer.toString(p.y);
				cntr++;
			}
			if (cameraConfigComm != null) {
				cameraConfigComm.sendMultipleParameters("corner_point", "int",
						cornerArr, strSize);
			}
		}

		public String arrToStr() {
			String str = "";
			for (int i = 0; i < cornerPoints.size(); i++) {
				Point curPoint = cornerPoints.get(i);
				str = str + curPoint.x + "," + curPoint.y + ",";
			}
			return str;
		}

		public void setImage(BufferedImage img) {
			this.img = img;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			// super.paintComponent(g);
			if (connectedToTracker == false) {
				// this.setSize(600,600);
				// this.setPreferredSize(new Dimension(600, 600));
				g.drawString("Not Connected to Touch Tracker", 2, 100);
			} else {

				if (this.img != null) {
					g.drawImage(this.img, 0, 0, this);
				}

				g2d.setStroke(CIRCLE_STROKE);

				if (cornerPoints.size() != requiredCornerPoints) {
					g.setColor(Color.RED);
				} else {
					g.setColor(Color.GREEN);
				}

				for (int i = 0; i < cornerPoints.size(); i++) {
					Point p = (Point) cornerPoints.get(i);
					g.drawOval(p.x - circleDim.width / 2 - aoix, p.y
							- circleDim.height / 2 - aoiy, circleDim.width,
							circleDim.height);
				}
			}

		}

		private boolean isPointInArr(Point p) {
			for (int i = 0; i < cornerPoints.size(); i++) {
				Point curPoint = (Point) cornerPoints.get(i);

				if ((p.x <= curPoint.x + circleDim.width + 2 / 2 && p.x >= curPoint.x
						- circleDim.width + 2 / 2)
						&& (p.y <= curPoint.y + circleDim.height + 2 / 2 && p.y >= curPoint.y
								- circleDim.height + 2 / 2)) {
					return true;
				}
			}
			return false;
		}

		public Point getPoint(Point p) {
			for (int i = 0; i < cornerPoints.size(); i++) {
				Point curPoint = (Point) cornerPoints.get(i);
				if ((p.x <= curPoint.x + circleDim.width + 2 / 2 && p.x >= curPoint.x
						- circleDim.width + 2 / 2)
						&& (p.y <= curPoint.y + circleDim.height + 2 / 2 && p.y >= curPoint.y
								- circleDim.height + 2 / 2)) {
					return curPoint;
				}
			}
			return null;
		}

		public void deleteAllCornerPoints() {
			cornerPoints.clear();
			setTitle("Image Display (" + cornerPoints.size() + " of "
					+ requiredCornerPoints + " corner points)");
			setCornerPointsStr(arrToStr());
			sendCornerPoints();
			repaint();
		}
	}

	public void sendAllTrackingParams() {
		setImageServerPort(getImageServerPort());
		setSubsampling(getSubsampling());
		setPrintFPS(isPrintFPS());
		setPatternH(getPatternH());
		setPatternW(getPatternW());
		setPortIncoming(getPortIncoming());
		imageDisplay.sendCornerPoints();
		setPixelclock(getPixelclock());
		setMode(getMode());

		setTrackContours(isTrackContours());
		setBackdiff(isBackdiff());
		setHomogenization(isHomogenization());
		setDynamicBackdiff(isDynamicBackdiff());
		setDynamicBackdiffSpeed(getDynamicBackdiffSpeed());
		setBackdiffTresh(getBackdiffTresh());
		setRepaintImg(isRepaintImg());

		setBackdiffScaleTresh(getBackdiffScaleTresh());
		setDownscaleFactor(getDownscaleFactor());
		setUpscaleFactor(getUpscaleFactor());
		setUpscaleFactor2(getUpscaleFactor2());
		setErodeDist(getErodeDist());
		setDilateDist(getDilateDist());
		setErodeTresh(getErodeTresh());
		setDilateTresh(getDilateTresh());
		setTrackingSensitivity(getTrackingSensitivity());
		setMinBlobSize(getMinBlobSize());
		setSimpleTracking(isSimpleTracking());
	}

	private static final String trackerProgramName = "SquidyVision.exe";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.nodes.TUIO#onStart()
	 */
	@Override
	public final void onStart() {

		super.onStart();

		startOSCServer();
		fireStatusChange("pixelclock", getPixelclock(), pixelclock);

		int camID = getCameraID();
		int portOut = 4000 + camID;
		int portIn = 4100 + camID;
		setOscPortConfigIn(portIn);
		setOscPortConfigOut(portOut);
		fireStatusChange("osc-port-in", getOscPortConfigIn(), portIn);
		fireStatusChange("osc-port-out", getOscPortConfigOut(), portOut);

		if (cameraConfigComm != null)
			cameraConfigComm.closeConnections();
		cameraConfigComm = new CameraConfigComm(remoteAddress,
				getOscPortConfigOut(), getOscPortConfigIn(),
				getImageServerPort(), Endian.LITTLE_ENDIAN, this);

		// laserServer = new LaserServer(this, laserPositionPort);
		// configManager = new ConfigManager(this, laserConfigurationPort);
		// proxy = new ProxyServer(this, proxyConfigurationPort, true);
		// imageServer = new ProxyServer(this, proxyCameraPort, false);

		frame = new JFrame("Image Display");

		frame.setLayout(new BorderLayout());
		// frame.setSize(new Dimension(800, 800));
		// frame.setPreferredSize(new Dimension(800, 800));
		// //frame.setResizable(false);

		imageDisplay = new ImageDisplay();
		imageDisplay.setBackground(Color.BLACK);
		imageDisplay.setSize(200, 200);
		imageDisplay.setPreferredSize(new Dimension(200, 200));
		imageDisplay.setRequiredCornerPoints(patternW * patternH);

		frame.setBackground(Color.BLACK);

		frame.add(imageDisplay, BorderLayout.CENTER);

		JButton clearCorners = new JButton("Delete All Corner Points");
		clearCorners.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int option = JOptionPane.showConfirmDialog(frame,
						"Would you like to remove all corner points?",
						"Remove corner points", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (option == JOptionPane.YES_OPTION) {
					imageDisplay.deleteAllCornerPoints();
				}
			}
		});
		frame.add(clearCorners, BorderLayout.SOUTH);
		// frame.add(imageDisplay);
		// Insets insets = frame.getInsets();

		frame.pack();
		frame.validate();
		if (getMode() != MODE_OFF)
			frame.setVisible(true);

		new Thread() {

			public void run() {
				super.run();
				while (isProcessing()) {
					try {
						trackerSocket = new Socket(remoteAddress, remotePort);
						System.out.println("Connected to Touch Tracker");
						connectedToTracker = true;
						cameraConfigComm.setStopped(false);
						if (isTrackFingers() == true)
							setTrackFingers(isTrackFingers());
						else
							sendAllTrackingParams();
						
						//close SquidyVision windows
						if (!displayBlobDetector)
							cameraConfigComm.sendParameter("display-blob-detector", "bool", "false");
						
						
						BufferedReader in = new BufferedReader(
								new InputStreamReader(trackerSocket
										.getInputStream()));
						in.readLine();
					} catch (UnknownHostException e1) {
						System.out
								.println("Connection to Touch Tracker closed. Retrying...");
						imageDisplay.repaint();
						connectedToTracker = false;
						cameraConfigComm.setStopped(true);
					} catch (IOException e1) {
						System.out
								.println("Connection to Touch Tracker closed. Retrying...");
						imageDisplay.repaint();
						connectedToTracker = false;
						cameraConfigComm.setStopped(true);
					}
					try {
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

		if (isStartTrackingAutomatically() && !connectedToTracker) {
			new Thread() {

				/**
				 * 
				 */
				@Override
				public void run() {
					super.run();

					String curDir = System.getProperty("user.dir");
					System.out.println("Java Working Directory: " + curDir);
					// if the tracker isn't running
					if (!connectedToTracker && !trackerRunning()) {
						
						ProcessBuilder pb = new ProcessBuilder(
								new String[] { "ext/" + trackerProgramName,
										Integer.toString(getCameraID()) });
						pb.directory(new File(".", "ext"));
						Process process = null;
						try {
							process = pb.start();

							// camerasReady++;
							// if (camerasReady == cameras && !isTracking) {
							// new Thread() {
							// public void run() {
							// try {
							// sleep(6000);
							// } catch (InterruptedException e) {
							// e.printStackTrace();
							// }
							//									
							// if (laserConfigClient == null) {
							// laserConfigClient = new LaserConfigClient();
							// }
							// // laserConfigClient.startAllTracking();
							// };
							// }.start();
							// }

							processInputStreamReading(process);
						} catch (IOException e) {
							if (LOG.isErrorEnabled()) {
								LOG.error(e.getMessage(), e);
							}
							publishFailure(e);
						}

						if (process != null) {
							cameraProcesses.add(process);
						}
					}
				}
			}.start();
		}

		// check number of set corner points
		final int difference = imageDisplay.getRequiredCornerPoints()
				- imageDisplay.getNumberOfCornerPoints();
		if (difference != 0) {
			if (difference > 0)
				ReacTIVision.showErrorPopUp("Too few corner points set.");
			else
				ReacTIVision.showErrorPopUp("Too many corner points set.");
		}
	}

	/**
	 * Returns <code>true</code> if an instance of
	 * <code>NLaserTracker.exe</code> is already running.
	 * <p>
	 * This requires <code>tasklist.exe</code>, which doesn't come with the HOME
	 * editions of Windows, but can be found on the web for free.
	 * 
	 * @return <code>true</code> if an instance of
	 *         <code>NLaserTracker.exe</code> is already running,
	 *         <code>false</code> otherwise
	 */
	private final boolean trackerRunning() {

		// TODO
		/*
		 * try { String line; Process p =
		 * Runtime.getRuntime().exec("tasklist.exe /fo csv /nh"); BufferedReader
		 * input = new BufferedReader (new
		 * InputStreamReader(p.getInputStream()));
		 * 
		 * while ((line = input.readLine()) != null) { if
		 * (!line.trim().equals("")) { // keep only the process name line =
		 * line.substring(1); if (line.substring(0,
		 * line.indexOf("\"")).equals(trackerProgramName)) return true; } }
		 * input.close(); } catch (Exception err) { err.printStackTrace(); }
		 */
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.nodes.TUIO#onStop()
	 */
	@Override
	public final void onStop() {
		stopOSCServer();
		if (connectedToTracker) {
			cameraConfigComm.sendParameter("kill", "bool", "true");
		}

		cameraConfigComm.sendParameter("stream_image", "bool", "false");

		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
		}
		cameraConfigComm.setStopped(true);
		cameraConfigComm.closeConnections();

		// if (laserConfigClient != null) {
		// laserConfigClient.stopAllTracking();
		// laserConfigClient = null;
		// }
		isTracking = false;

		camerasReady = 0;
		// for (Process p : cameraProcesses) {
		// p.destroy();
		// }

		if (laserServer != null) {
			laserServer.close();
		}
		/*
		 * if (configManager != null) { configManager.close(); }
		 */
		if (proxy != null) {
			proxy.close();
		}

		if (imageServer != null) {
			imageServer.close();
		}

		super.onStop();
	}

	/**
	 * @param process
	 */
	private void processInputStreamReading(final Process process) {
		new Thread() {
			/**
			 * 
			 */
			@Override
			public void run() {
				super.run();

				try {
					String cameraId = "";

					InputStream is = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
					while ((line = br.readLine()) != null) {

						if (line.startsWith("Camera ID: ")) {
							cameraId = line.substring(11, line.length());
						}

						// if ("Initialization completed".equals(line)) {
						// camerasReady++;
						//
						// if (cameras == camerasReady && !isTracking) {
						// if (false) {
						// // if (laserConfigClient == null) {
						// // laserConfigClient = new LaserConfigClient();
						// // }
						// // laserConfigClient.startAllTracking();
						// isTracking = true;
						// }
						// }
						// }

						// if ("Number of corners not matching".equals(line)) {
						// JOptionPane
						// .showMessageDialog(
						// Designer.getInstance(),
						// line
						// +
						// "\n\nUse laserConfig.xml backup or recalibrate camera.",
						// "Tracking error",
						// JOptionPane.ERROR_MESSAGE);
						// publishFailure(new ProcessException(line));
						// }

						System.out.println("Camera " + cameraId + ": " + line);
					}
				} catch (IOException e) {
					if (LOG.isErrorEnabled()) {
						LOG.error(e.getMessage(), e);
					}
					publishFailure(e);
				}
			}
		}.start();

	}

	public void imageUpdate(BufferedImage img) {
		imageDisplay.setImage(img);

		if (img.getWidth() != imageDisplay.getWidth()
				|| img.getHeight() != imageDisplay.getHeight()) {
			Dimension newDim = new Dimension(img.getWidth(), img.getHeight());
			imageDisplay.setSize(newDim);
			imageDisplay.setPreferredSize(newDim);
			frame.pack();
		}

		// imageDisplay.setSize(newDim);

	}

	public void configUpdate(String name, String type, String value) {

		String f = "framerate";

		if (name.equals(f)) {
			refreshFramerate(Integer.parseInt(value));

		}

		if (name.equals("exposure")) {
			refreshExposure(Integer.parseInt(value));
		}
		if (name.equals("pixelclock")) {
			refreshPixelclock(Integer.parseInt(value));

		}

		if (name.equals("aoix")) {
			aoix = Integer.parseInt(value);
		}
		if (name.equals("aoiy")) {
			aoiy = Integer.parseInt(value);
		}
		if (name.equals("aoiw")) {
			aoiw = Integer.parseInt(value);
		}
		if (name.equals("aoih")) {
			aoih = Integer.parseInt(value);
		}
		// notifyUpdateConfig();
	}

	/*
	 * 
	 * public void attachNotifier(ConfigNotifier configNotifier) {
	 * configUpdate.add(configNotifier); }
	 * 
	 * 
	 * public void detachNotifier(ConfigNotifier configNotifier) {
	 * configUpdate.remove(configNotifier); }
	 * 
	 * 
	 * public void notifyUpdateConfig() { for (ConfigNotifier configNotifier :
	 * configUpdate) { configNotifier.updateConfig(); } }
	 * 
	 * 
	 * public Camera getCalibrationCamera() { return
	 * configManager.getCalibrationCamera(); }
	 * 
	 * 
	 * public Configuration getConfig() { return configManager.getConfig(); }
	 */
}
