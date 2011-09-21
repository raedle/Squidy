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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.Gauge;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.ir.ConfigManagable;
import org.squidy.nodes.tracking.LaserServer;
import org.squidy.nodes.tracking.MicroControllerLaserDriver;
import org.squidy.nodes.tracking.PhidgetLaserDriver;
import org.squidy.nodes.tracking.WirelessLaserDriver;
import org.squidy.nodes.tracking.WirelessLaserDriverBlack;
import org.squidy.nodes.tracking.config.ConfigManager;
import org.squidy.nodes.tracking.config.ConfigNotifier;
import org.squidy.nodes.tracking.config.xml.Camera;
import org.squidy.nodes.tracking.config.xml.Configuration;
import org.squidy.nodes.tracking.configclient.LaserConfigClient;
import org.squidy.nodes.tracking.proxy.ProxyServer;

import com.phidgets.PhidgetException;


/**
 * <code>Laserpointer</code>.
 * 
 * <pre>
 * Date: Feb 12, 2008
 * Time: 1:43:12 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: Laserpointer.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "Laserpointer")
@Processor(
	name = "Laserpointer",
	icon = "/org/squidy/nodes/image/48x48/laserpointer.png",
	description = "/org/squidy/nodes/html/Laserpointer.html",
	types = { Processor.Type.INPUT },
	tags = {"laserpointer", "laser", "pointer", "absolute"}
)
public class Laserpointer extends AbstractNode implements ConfigManagable {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Laserpointer.class);

	private LaserServer laserServer;
	private ConfigManager configManager;
	private WirelessLaserDriver laserDriver;
	private WirelessLaserDriverBlack laserDriverBlack;
	private ProxyServer proxy;
	private ProxyServer proxyClient;
	private MicroControllerLaserDriver laserDriverCable;
	private PhidgetLaserDriver laserDriverPhidget;
	
	//Colorcoding for Laserpointer LEDs
	public static final byte LED_COLOR_OFF = (byte)0;
	public static final byte LED_COLOR_BLUE = (byte)1;
	public static final byte LED_COLOR_GREEN = (byte)2;
	public static final byte LED_COLOR_CYAN = (byte)3;
	public static final byte LED_COLOR_RED = (byte)4;
	public static final byte LED_COLOR_MAGENTA = (byte)5;
	public static final byte LED_COLOR_YELLOW = (byte)6;
	public static final byte LED_COLOR_WHITE = (byte)7;
	
	//Laserpointermodes
	public static final int LP_MODE_DEFAULT = 1;
	public static final int LP_MODE_STATIC = 2;
	public static final int LP_MODE_DEMO = 3;

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

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
	}

	// ################################################################################

	@XmlAttribute(name = "remote-port")
	@Property(name = "Remote port", group = "Remote Connection")
	@TextField
	private int remotePort = 9999;

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
		this.remotePort = remotePort;
	}

	// ################################################################################

//	@XmlAttribute(name = "cameras")
//	@Property(name = "Cameras", group = "Camera Configuration", description = "Cameras to be attached.")
//	@TextField
//	private int cameras = 1;
//
//	/**
//	 * @return the cameras
//	 */
//	public final int getCameras() {
//		return cameras;
//	}
//
//	/**
//	 * @param cameras
//	 *            the cameras to set
//	 */
//	public final void setCameras(int cameras) {
//		this.cameras = cameras;
//	}

	// ################################################################################

//	@XmlAttribute(name = "path-to-n-laser-tracker")
//	@Property(name = "NLaserTracker.exe Path", group = "Camera Configuration", description = "Path to NLaserTracker.exe.")
//	@TextField
//	private String pathToNLaserTracker = "C:\\Projects\\NLaserTracker_MT\\trunk\\Release";
//
//	/**
//	 * @return the pathToNLaserTracker
//	 */
//	public final String getPathToNLaserTracker() {
//		return pathToNLaserTracker;
//	}
//
//	/**
//	 * @param pathToNLaserTracker
//	 *            the pathToNLaserTracker to set
//	 */
//	public final void setPathToNLaserTracker(String pathToNLaserTracker) {
//		this.pathToNLaserTracker = pathToNLaserTracker;
//	}

	// ################################################################################

//	@XmlAttribute(name = "proxy-configuration-port")
//	@Property(name = "Proxy configuration port", group = "Proxy", description = "Proxy configuration port")
//	// @TextField
//	@Spinner(type = Integer.class, minimumValue = 1024, maximumValue = 65536, step = 1)
//	private int proxyConfigurationPort = 6667;
//
//	/**
//	 * @return the proxyConfigurationPort
//	 */
//	public int getProxyConfigurationPort() {
//		return proxyConfigurationPort;
//	}
//
//	/**
//	 * @param proxyConfigurationPort
//	 *            the proxyConfigurationPort to set
//	 */
//	public void setProxyConfigurationPort(int proxyConfigurationPort) {
//		this.proxyConfigurationPort = proxyConfigurationPort;
//	}

	// ################################################################################

//	@XmlAttribute(name = "proxy-camera-port")
//	@Property(name = "Proxy camera port", group = "Proxy", description = "Proxy camera port")
//	@TextField
//	private int proxyCameraPort = 6666;
//
//	/**
//	 * @return the proxyCameraPort
//	 */
//	public int getProxyCameraPort() {
//		return proxyCameraPort;
//	}
//
//	/**
//	 * @param proxyCameraPort
//	 *            the proxyCameraPort to set
//	 */
//	public void setProxyCameraPort(int proxyCameraPort) {
//		this.proxyCameraPort = proxyCameraPort;
//	}

	// ################################################################################

//	@XmlAttribute(name = "laser-configuration-port")
//	@Property(name = "Laser configuration port", group = "Laser", description = "Laserpointer configuration port")
//	@TextField
//	private int laserConfigurationPort = 5552;
//
//	/**
//	 * @return the laserConfigurationPort
//	 */
//	public int getLaserConfigurationPort() {
//		return laserConfigurationPort;
//	}
//
//	/**
//	 * @param laserConfigurationPort
//	 *            the laserConfigurationPort to set
//	 */
//	public void setLaserConfigurationPort(int laserConfigurationPort) {
//		this.laserConfigurationPort = laserConfigurationPort;
//	}

	// ################################################################################

//	@XmlAttribute(name = "laser-position-port")
//	@Property(name = "Laser position port", group = "Laser", description = "Laserpointer position port")
//	@TextField
//	private int laserPositionPort = 5000;
//
//	/**
//	 * @return the laserPositionPort
//	 */
//	public int getLaserPositionPort() {
//		return laserPositionPort;
//	}
//
//	/**
//	 * @param laserPositionPort
//	 *            the laserPositionPort to set
//	 */
//	public void setLaserPositionPort(int laserPositionPort) {
//		this.laserPositionPort = laserPositionPort;
//	}

	// ################################################################################

	@XmlAttribute(name = "vibration-duration")
	@Property(name = "Vibration duration", description = "Duration of vibration", suffix = "ms")
	@TextField
	private int vibrationDuration = 300;

	/**
	 * @return the vibrationDuration
	 */
	public int getVibrationDuration() {
		return vibrationDuration;
	}

	/**
	 * @param vibrationDuration
	 *            the vibrationDuration to set
	 */
	public void setVibrationDuration(int vibrationDuration) {
		this.vibrationDuration = vibrationDuration;
	}

	// ################################################################################

	@XmlAttribute(name = "connection-phidget")
	@Property(name = "Phidgets", group = "Controller Type", description = "True if laserpointer with phidget connection is used")
	@CheckBox
	private boolean connectionPhidget = false;

	/**
	 * @return True if laserpointer with phidget connection is used
	 */
	public boolean getConnectionPhidget() {
		return connectionPhidget;
	}

	/**
	 * @param flag
	 *            True if laserpointer with phidget connection is used
	 */
	public void setConnectionPhidget(boolean connectionPhidget) {
		this.connectionPhidget = connectionPhidget;
	}

	// ################################################################################

	@XmlAttribute(name = "phidget-sensitivity")
	@Property(name = "Phidget sinsitivity", description = "Sensitivity Threshold 0-250")
	@Slider(minimumValue = 0, maximumValue = 250, majorTicks = 25, minorTicks = 5, snapToTicks = true, showTicks = true)
	private int phidgetSensitivity = 5;

	/**
	 * @return Sensitivity Threshold 0-250
	 */
	public int getPhidgetSensitivity() {
		return phidgetSensitivity;
	}

	/**
	 * @param int Sensitivity Threshold 0-250
	 */
	public void setPhidgetSensitivity(int phidgetSensitivity) {
		this.phidgetSensitivity = phidgetSensitivity;
		if (laserDriverPhidget != null) {
			laserDriverPhidget.setSensitivityThreshold(phidgetSensitivity);
		}
	}

	// ################################################################################

	@XmlAttribute(name = "connection-io-chip")
	@Property(name = "IO-Chip", group = "Controller Type", description = "True if laserpointer with IOChip connection is used")
	@CheckBox
	private boolean connectionIOChip = false;

	/**
	 * @return True if laserpointer with IOChip connection is used
	 */
	public boolean getConnectionIOChip() {
		return connectionIOChip;
	}

	/**
	 * @param flag
	 *            True if laserpointer with IOChip connection is used
	 */
	public void setConnectionIOChip(boolean connectionIOChip) {
		this.connectionIOChip = connectionIOChip;
	}

	// ################################################################################

	@XmlAttribute(name = "connection-wireless")
	@Property(name = "Wireless", group = "Controller Type", description = "True if laserpointer with wireless transmission is used")
	@CheckBox
	private boolean connectionWireless = false;

	/**
	 * @return True if laserpointer with wireless transmission is used
	 */
	public boolean getConnectionWireless() {
		return connectionWireless;
	}

	/**
	 * @param flag
	 *            True if laserpointer with wireless transmission is used
	 */
	public void setConnectionWireless(boolean connectionWireless) {
		this.connectionWireless = connectionWireless;
	}
	
	// ################################################################################

	@XmlAttribute(name = "connection-wireless-black")
	@Property(name = "Wireless black", group = "Controller Type", description = "True if the black laserpointer with wireless transmission is used")
	@CheckBox
	private boolean connectionWirelessBlack = true;

	/**
	 * @return True if the black laserpointer with wireless transmission is used
	 */
	public boolean getConnectionWirelessBlack() {
		return connectionWirelessBlack;
	}

	/**
	 * @param flag
	 *            True if the black laserpointer with wireless transmission is used
	 */
	public void setConnectionWirelessBlack(boolean connectionWirelessBlack) {
		this.connectionWirelessBlack = connectionWirelessBlack;
	}

	// ################################################################################

	@XmlAttribute(name = "laser-serial-port")
	@Property(name = "Laser serial port", description = "Laserpointer serial port")
	@TextField
	private String laserSerialPort = "COM7";

	/**
	 * @return the laserSerialPort
	 */
	public String getLaserSerialPort() {
		return laserSerialPort;
	}

	/**
	 * @param laserSerialPort
	 *            the laserSerialPort to set
	 */
	public void setLaserSerialPort(String laserSerialPort) {
		this.laserSerialPort = laserSerialPort;
	}

	// ################################################################################

	@XmlAttribute(name = "config-xml-read-only")
	@Property(name = "Config XML read only", description = "Indicates whether the config xml is in readonly mode or not.")
	@CheckBox
	private boolean configXmlReadOnly = false;

	/**
	 * @return the configXmlReadOnly
	 */
	public final boolean isConfigXmlReadOnly() {
		return configXmlReadOnly;
	}

	/**
	 * @param configXmlReadOnly
	 *            the configXmlReadOnly to set
	 */
	public final void setConfigXmlReadOnly(boolean configXmlReadOnly) {
		this.configXmlReadOnly = configXmlReadOnly;
	}

	// ################################################################################

	@XmlAttribute(name = "inertial-active")
	@Property(name = "Inertial active", description = "Indicates whether the inertial is activated or not.")
	@CheckBox
	private boolean inertialActive = true;

	/**
	 * @return the inertialActive
	 */
	public final boolean isInertialActive() {
		return inertialActive;
	}

	/**
	 * @param inertialActive
	 *            the inertialActive to set
	 */
	public final void setInertialActive(boolean inertialActive) {
		this.inertialActive = inertialActive;
		if(laserDriverBlack != null){
			laserDriverBlack.setInertiaOn(inertialActive);
		}
	}

	// ################################################################################

	 @XmlAttribute(name = "show-pattern")
	 @Property(
	 name = "Show pattern",
	 description =
	 "Whether a pattern should be shown for calibration frame or not."
	 )
	 @CheckBox
	 private boolean showPattern = false;
	
	 /**
	 * @return the showPattern
	 */
	 public final boolean isShowPattern() {
	 return showPattern;
	 }
	
	 /**
	 * @param showPattern the showPattern to set
	 */
	 public final void setShowPattern(boolean showPattern) {
	 this.showPattern = showPattern;
	 }

	// ################################################################################

	@XmlAttribute(name = "show-white")
	@Property(name = "Show white", description = "Whether a white fullscreen image should be shown for background subtraction when cameras get started.")
	@CheckBox
	private boolean showWhite = false;

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
	public final void setShowWhite(boolean showPattern) {
		this.showWhite = showPattern;
	}
	
	// ################################################################################

	@XmlAttribute(name = "serial-number")
	@Property(name = "Serial number", description = "Laser pointer serial number.")
	@TextField
	private int serialNumber;

	/**
	 * @return the serialNumber
	 */
	public final int getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber
	 *            the serialNumber to set
	 */
	public final void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	// ################################################################################

//	@XmlAttribute(name = "restart-laser-pointer-exe")
//	@Property(name = "Restart laser pointer exe", description = "Whether laser pointer exe should restart laser pointer or not.")
//	@CheckBox
//	private boolean restartLaserPointerExe = false;
//
//	/**
//	 * @return the restartLaserPointerExe
//	 */
//	public final boolean isRestartLaserPointerExe() {
//		return restartLaserPointerExe;
//	}
//
//	/**
//	 * @param restartLaserPointerExe
//	 *            the restartLaserPointerExe to set
//	 */
//	public final void setRestartLaserPointerExe(boolean restartLaserPointerExe) {
//		this.restartLaserPointerExe = restartLaserPointerExe;
//	}

	// ################################################################################

	@XmlAttribute(name = "wait-for-alarm")
	@Property(name = "Wait for alarm", description = "Time to wait for alarm.", suffix = "ms")
	@TextField
	private int waitForAlarm = 20;

	/**
	 * @return the waitForAlarm
	 */
	public final int getWaitForAlarm() {
		return waitForAlarm;
	}

	/**
	 * @param waitForAlarm
	 *            the waitForAlarm to set
	 */
	public final void setWaitForAlarm(int waitForAlarm) {
		this.waitForAlarm = waitForAlarm;
	}
	
	// ################################################################################

	@XmlAttribute(name = "shutdown-on-stop")
	@Property(name = "Shutdown Laserpointer on stop", description = "Laserpointer (black) will be turned off when node is stopped.")
	@CheckBox
	private boolean shutdownOnStop = true;

	/**
	 * @return the shutdownOnStop
	 */
	public final boolean getShutdownOnStop() {
		return shutdownOnStop;
	}

	/**
	 * @param shutdownOnStop
	 *            the shutdownOnStop to set
	 */
	public final void setShutdownOnStop(boolean shutdownOnStop) {
		this.shutdownOnStop = shutdownOnStop;
	}
	
	// ################################################################################
	
	private int example = 0;
	
	public int getExample() {
		return example;
	}

	public void setExample(int example) {
		this.example = example;
	}

	@XmlAttribute(name = "batterystatus")
	@Property(name = "Battery status", description = "Status of the laserpointer's internal battery")
	@Gauge
	private float batterystatus = 0.5f;

	/**
	 * @return the batteryStatus
	 */
	public final float getBatterystatus() {
		return batterystatus;
	}

	/**
	 * @param batteryStatus
	 *            batteryStatus
	 */
	public final void setBatterystatus(float batteryStatus) {
		fireStatusChange("batterystatus", new Float(this.batterystatus), new Float(batteryStatus));
		this.batterystatus = batteryStatus;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "laserpointerMode")
	@Property(
		name = "Laserpointer Mode", description = "Defines how LEDs and virbration behave"
	)
	@ComboBox(domainProvider = LaserpointerModeProvider.class)
	private int laserpointerMode = LP_MODE_DEFAULT;

	public int getLaserpointerMode() {
		return laserpointerMode;
	}

	public void setLaserpointerMode(int laserpointerMode) {
		this.laserpointerMode = laserpointerMode;
		if(laserDriverBlack != null) {
			//laserDriverBlack.setLaserpointerMode();
		}
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "led-1-color")
	@Property(
		name = "LED 1 color", description = "Color of LED 1"
	)
	@ComboBox(domainProvider = LEDColorProvider.class)
	private byte LED1color = LED_COLOR_BLUE;

	public byte getLED1color() {
		return LED1color;
	}

	public void setLED1color(byte lED1color) {
		LED1color = lED1color;
		if(laserDriverBlack != null) {
			laserDriverBlack.updateLEDStatus(1);
		}
	}
	
	@XmlAttribute(name = "led-2-color")
	@Property(
		name = "LED 2 color", description = "Color of LED 2"
	)
	@ComboBox(domainProvider = LEDColorProvider.class)
	private byte LED2color = LED_COLOR_GREEN;

	public byte getLED2color() {
		return LED2color;
	}

	public void setLED2color(byte lED2color) {
		LED2color = lED2color;
		if(laserDriverBlack != null) {
			laserDriverBlack.updateLEDStatus(2);
		}
	}
	
	@XmlAttribute(name = "led-3-color")
	@Property(
		name = "LED 3 color", description = "Color of LED 3"
	)
	@ComboBox(domainProvider = LEDColorProvider.class)
	private byte LED3color = LED_COLOR_RED;

	public byte getLED3color() {
		return LED3color;
	}

	public void setLED3color(byte lED3color) {
		LED3color = lED3color;
		if(laserDriverBlack != null) {
			laserDriverBlack.updateLEDStatus(3);
		}
	}
	
	@XmlAttribute(name = "led-4-color")
	@Property(
		name = "LED 4 color", description = "Color of LED 4"
	)
	@ComboBox(domainProvider = LEDColorProvider.class)
	private byte LED4color = LED_COLOR_BLUE;

	public byte getLED4color() {
		return LED4color;
	}

	public void setLED4color(byte lED4color) {
		LED4color = lED4color;
		if(laserDriverBlack != null) {
			laserDriverBlack.updateLEDStatus(4);
		}
	}
	
	@XmlAttribute(name = "led-5-color")
	@Property(
		name = "LED 5 color", description = "Color of LED 5"
	)
	@ComboBox(domainProvider = LEDColorProvider.class)
	private byte LED5color = LED_COLOR_GREEN;

	public byte getLED5color() {
		return LED5color;
	}

	public void setLED5color(byte lED5color) {
		LED5color = lED5color;
		if(laserDriverBlack != null) {
			laserDriverBlack.updateLEDStatus(5);
		}
	}
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private Collection<ConfigNotifier> configUpdate = new Vector<ConfigNotifier>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.nodes.TUIO#onStart()
	 */
	public final void onStart() throws ProcessException {
		super.onStart();

		//configManager = new ConfigManager(this, laserConfigurationPort);
		//laserServer = new LaserServer(this, laserPositionPort);
		//proxy = new ProxyServer(this, proxyConfigurationPort, true);
		//proxyClient = new ProxyServer(this, proxyCameraPort, false);

		if (connectionWireless) {
			laserDriver = new WirelessLaserDriver(this, laserSerialPort);
		}
		
		if (connectionWirelessBlack) {
			laserDriverBlack = new WirelessLaserDriverBlack(this, laserSerialPort);
		}

		if (connectionPhidget) {
			try {
				laserDriverPhidget = new PhidgetLaserDriver(this);
			}
			catch (PhidgetException e) {
				throw new ProcessException("Couldn't initialize laser driver phidget.", e);
			}
			setPhidgetSensitivity(phidgetSensitivity);
		}

		if (connectionIOChip) {
			laserDriverCable = new MicroControllerLaserDriver(this);
		}
		
		new Thread() {
			
			/**
			 * 
			 */
			@Override
			public void run() {
				super.run();
				
				//for (int i = 0; i < cameras; i++) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					//ProcessBuilder pb = new ProcessBuilder(pathToNLaserTracker + "\\NLaserTracker.exe");
					//pb.directory(new File(pathToNLaserTracker));
					//Process process = null;
					//try {
					//	process = pb.start();

					//	processInputStreamReading(process);
					//}
					//catch (IOException e) {
						// TODO Auto-generated catch block
					//	e.printStackTrace();
					//}
					//if (process != null) {
					//	cameraProcesses.add(process);
					//}
				}
			//}
		}.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.nodes.TUIO#onStop()
	 */
	public final void onStop() {

		//for (Process p : cameraProcesses) {
		//	p.destroy();
		//}

		if (laserServer != null) {
			laserServer.close();
		}
		if (configManager != null) {
			configManager.close();
		}
		if (proxy != null) {
			proxy.close();
		}
		if (proxyClient != null) {
			proxyClient.close();
		}
		if (laserDriverCable != null) {
			laserDriverCable.close();
		}
		if (laserDriver != null) {
			laserDriver.close();
		}
		if (laserDriverBlack != null) {
			laserDriverBlack.close();
		}
		if (laserDriverPhidget != null) {
			laserDriverPhidget.close();
		}

		super.onStop();
	}

	private void processInputStreamReading(final Process process) {
		/*
		new Thread() {
			
			@Override
			public void run() {
				super.run();

				try {
					InputStream is = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
					while ((line = br.readLine()) != null) {
						System.out.println(line);

						if ("Update completed".equals(line)) {
							camerasReady++;
							
							System.out.println("CAMS READY: " + camerasReady);

							if (cameras == camerasReady && !isTracking) {
								if (laserConfigClient == null) {
									laserConfigClient = new LaserConfigClient();
								}
								laserConfigClient.startAllTracking();
								isTracking = true;
							}
						}
					}
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		*/
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.nodes.ir.ConfigManagable#attachNotifier(de
	 * .ukn.hci.squidy.extension.basic.laserpointer.config.ConfigNotifier)
	 */
	public void attachNotifier(ConfigNotifier configNotifier) {
		configUpdate.add(configNotifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.nodes.ir.ConfigManagable#detachNotifier(de
	 * .ukn.hci.squidy.extension.basic.laserpointer.config.ConfigNotifier)
	 */
	public void detachNotifier(ConfigNotifier configNotifier) {
		configUpdate.remove(configNotifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.nodes.ir.ConfigManagable#notifyUpdateConfig()
	 */
	public void notifyUpdateConfig() {
		for (ConfigNotifier configNotifier : configUpdate) {
			configNotifier.updateConfig();
		}
	}

	public void vibrate(boolean vib, final int duration) {
		if (connectionIOChip) {
			laserDriverCable.vibrate(vib, duration);
		}
		if (connectionWireless) {
			laserDriver.vibrate(vib, duration);
		}
		if (connectionPhidget) {
			laserDriverPhidget.vibrate(vib, duration);
		}
		if (connectionWirelessBlack) {
			laserDriverBlack.vibrate(vib, duration);		}
	}

	public void setLEDColor(boolean red, boolean green) {
		laserDriver.setLEDColor(red, green);
	}

	private void setGreenLED(boolean green) {
		laserDriver.setGreenLED(green);
	}

	public void setRedLED(boolean red) {
		laserDriver.setRedLED(red);
	}

	public void updateMAC() {
		laserDriver.updateMAC();
	}

	public Camera getCalibrationCamera() {
		return configManager.getCalibrationCamera();
	}

	public Configuration getConfig() {
		return configManager.getConfig();
	}

	public IData process(DataDigital dataDigital) {

		if (dataDigital.getAttribute(DataConstant.TACTILE) != null) {
			vibrate(dataDigital.getFlag(), vibrationDuration);
		}
		if (dataDigital.getAttribute(DataConstant.RED_LED) != null) {
			setRedLED(dataDigital.getFlag());
		}
		if (dataDigital.getAttribute(DataConstant.GREEN_LED) != null) {
			// setGreenLED(dataDigital.getFlag());
			laserDriver.setGreenLED(dataDigital.getFlag());
		}
		if(dataDigital.getAttribute(DataConstant.LED_ID) != null && dataDigital.getAttribute(DataConstant.LED_COLOR) != null && laserDriverBlack != null) {
//			if(laserDriverBlack == null) {
//				return null;
//			}
			int ledID = (Integer)dataDigital.getAttribute(DataConstant.LED_ID);
			byte col = (Byte)dataDigital.getAttribute(DataConstant.LED_COLOR);
			boolean on = dataDigital.getFlag();
			if(col < 0 || col > 7 || ledID < 1 || ledID > 5) {
				return null;
			}
			if(ledID == 1) {
				setLED1color(on?col:(byte)0);
			}
			else if(ledID == 2) {
				setLED2color(on?col:(byte)0);
			}
			else if(ledID == 3) {
				setLED3color(on?col:(byte)0);
			}
			else if(ledID == 4) {
				setLED4color(on?col:(byte)0);
			}
			else if(ledID == 5) {
				setLED5color(on?col:(byte)0);
			}
			laserDriverBlack.updateLEDStatus(ledID);
		}

		return null;
	}
	
	public static class LEDColorProvider implements DomainProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[8];
			values[0] = new ComboBoxItemWrapper(LED_COLOR_OFF, "Off");
			values[1] = new ComboBoxItemWrapper(LED_COLOR_BLUE, "Blue");
			values[2] = new ComboBoxItemWrapper(LED_COLOR_CYAN, "Cyan");
			values[3] = new ComboBoxItemWrapper(LED_COLOR_MAGENTA, "Magenta");
			values[4] = new ComboBoxItemWrapper(LED_COLOR_RED, "Red");
			values[5] = new ComboBoxItemWrapper(LED_COLOR_GREEN, "Green");
			values[6] = new ComboBoxItemWrapper(LED_COLOR_YELLOW, "Yellow");
			values[7] = new ComboBoxItemWrapper(LED_COLOR_WHITE, "White");

			return values;
		}
	}
	
	public static class LaserpointerModeProvider implements DomainProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[3];
			values[0] = new ComboBoxItemWrapper(LP_MODE_DEFAULT, "Default");
			values[1] = new ComboBoxItemWrapper(LP_MODE_STATIC, "Static");
			values[2] = new ComboBoxItemWrapper(LP_MODE_DEMO, "Demo");

			return values;
		}
	}

}