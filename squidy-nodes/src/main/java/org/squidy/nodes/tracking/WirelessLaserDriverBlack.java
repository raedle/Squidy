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


package org.squidy.nodes.tracking;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.nodes.Laserpointer;


/**
 * 
 * @author Markus Nitsche, markus.nitsche@uni-konstanz.de, University of Konstanz
 * 
 */
public class WirelessLaserDriverBlack extends Thread implements
		SerialPortEventListener, LaserVibrate {
	
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(WirelessLaserDriverBlack.class);

	private String port;
	private Laserpointer laserPointer;

	private static CommPortIdentifier portId;
	private static Enumeration portList;
	private InputStream inputStream;
	private SerialPort serialPort;
	private OutputStream outputStream;
	private LaserVibration laserVibration;
	private static boolean outputBufferEmptyFlag = false;
	private boolean isVibrating = false;
	private boolean connected = false;
	private boolean running = true;
	private int aliveCounter = 0;
	private boolean pushMode = true;
	private boolean laserOn = true;
	private boolean inertiaOn = false;
	private double batmin = 999999;
	private double batmax = -1;
	
	//Battery status variables
	private long batteryStatusUpdateInterval = 10000;
	private long lastUpdateTime = 0;
	private Vector<Double> batteryData = new Vector<Double>();
	
	public boolean isInertiaOn() {
		return inertiaOn;
	}

	public void setInertiaOn(boolean inertiaOn) {
//		this.inertiaOn = false;
		this.inertiaOn = inertiaOn;
		updateStatus();
	}

	private boolean shutDown = false;

	public WirelessLaserDriverBlack(Laserpointer laserPointer, String port) {
		this.laserPointer = laserPointer;
		this.port = port;
		init();
		
		updateStatus();
	}

	public void init() {
		boolean portFound = false;
		
		// parse ports and if the specified port is found, initialized the
		// reader
		portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(port)) {
					portFound = true;
					start();
					break;
				}
			}
		}
		if (!portFound) {
			if (LOG.isErrorEnabled()) {
				LOG.error("serial port " + port + " not found.");
			}
			laserPointer.publishFailure(new Exception("serial port " + port + " not found."));
		}
	}

	public void run() {

		// initalize serial port
		try {
			serialPort = (SerialPort) portId.open("WirelessLaserDriverBlack",
					2000);
		} catch (PortInUseException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			laserPointer.publishFailure(e);
			return;
		}

		try {
			inputStream = serialPort.getInputStream();
		} catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			laserPointer.publishFailure(e);
			return;
		}

		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			laserPointer.publishFailure(e);
			return;
		}

		// activate the DATA_AVAILABLE notifier
		serialPort.notifyOnDataAvailable(true);

		try {
			// set port parameters
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			laserPointer.publishFailure(e);
			return;
		}

		 initwritetoport();
		 initLaserPointer();
//		 initAlive();
		 
		// try {
		// while (true) {
		// // write string to port, the serialEvent will read it
		// writetoport();
		// Thread.sleep(1000);
		// }
		// } catch (InterruptedException e) {}

	}

	public void initwritetoport() {
		// initwritetoport() assumes that the port has already been opened and
		// initialized
		try {
			// get the outputstream
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			laserPointer.publishFailure(e);
			return;
		}

		try {
			// activate the OUTPUT_BUFFER_EMPTY notifier
			serialPort.notifyOnOutputEmpty(true);
		} catch (Exception e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			laserPointer.publishFailure(e);
			return;
		}

		connected = true;
	}
	
	public void initLaserPointer() {
		isVibrating = false;
		pushMode = true;
		laserOn = true;
		inertiaOn = laserPointer.isInertialActive();
		shutDown = false;
		updateStatus();
	}
	
	private synchronized void writeSerial(String str) throws IOException {
		// logger.debug("write serial "+str);
		if (outputStream != null && connected) {
			outputStream.write(str.getBytes());
			outputStream.flush();
		}
	}

	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			// we get here if data has been received
			byte[] readBuffer = new byte[20];
				
			if(!connected){
				connected = true;
				updateStatus();
				return;
			}
			aliveCounter++;
			
			// read data
			int numBytes = 0;
			try {
				while (inputStream.available() > 0) {
					numBytes = inputStream.read(readBuffer);
				}
			} catch (IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
				laserPointer.publishFailure(e);
				return;
			}
			
			String[] messageHex = new String[numBytes];
			for(int i=0; i<numBytes;i++){
				messageHex[i] = asciiToHex(new String(readBuffer,i,1));
			}
			
			// read button status
			int butBin = Integer.parseInt(messageHex[0], 16);
			
			// read inertia status
			float raw_a = 0.0f;
			float raw_b = 0.0f;
			float raw_c = 0.0f;
			double batteryStatus = 0.0;
			
			if (messageHex.length >= 4) {
				raw_a = (float)Integer.parseInt(messageHex[1], 16)/200.0f; // Sensor_Z, Wii_-Y
				raw_b = (float)Integer.parseInt(messageHex[2], 16)/200.0f; // Sensor_X, Wii_-Z
				raw_c = (float)Integer.parseInt(messageHex[3], 16)/200.0f; // Sensor_Y, Wii_X
			}
//			System.out.println("ECHO: " + raw_b);
			
			if (messageHex.length >= 5) {
				//TODO: float? why /255?
				batteryStatus = (double)Integer.parseInt(messageHex[4], 16); ///255.0;
			}
			
//			System.out.println(messageHex[0] + " | " + messageHex[1] + " | " + messageHex[2]);
			
			List<IData> dataContainer = new ArrayList<IData>(4); 
			
			// publish data
			if(inertiaOn) {
				dataContainer.add(new DataInertial(laserPointer.getClass(), raw_a, raw_b, raw_c));
			}
			dataContainer.add(new DataButton(Laserpointer.class, DataButton.BUTTON_3, ((butBin & 0x1) > 0)));
			dataContainer.add(new DataButton(Laserpointer.class, DataButton.BUTTON_1, ((butBin & 0x2) > 0)));
			dataContainer.add(new DataButton(Laserpointer.class, DataButton.BUTTON_2, ((butBin & 0x4) > 0)));
			laserPointer.publish(dataContainer);
			
			//if inertia sensor is off, no battery status is sent. Therefore, we'll keep the original value
			if(batteryStatus > 0.0) {
				//calculate battery status value from 0 to 1 based on hardware specs
				//max: 103.0; min: 31.0; critical (vibration): 35.0 corresponds to
				//max: 1.0; min: 1.0; crit: 0.05556
				
				batteryStatus = (batteryStatus - 31.0) / 72.0;
				if(batteryStatus < 0.0)
					batteryStatus = 0.0;
				else if(batteryStatus > 1.0)
					batteryStatus = 1.0;
				batteryData.add(batteryStatus);
				
				if((System.currentTimeMillis() - lastUpdateTime) > batteryStatusUpdateInterval) {
					lastUpdateTime = System.currentTimeMillis();
					
					//calculate average of batterydata for last 10 seconds
					double avg = 0.0;
					for(double d : batteryData) {
						avg += d;
					}
					avg = avg / (double) batteryData.size();
					
					laserPointer.setBatterystatus((float) avg);
					batteryData.removeAllElements();
				}
			}
			
			//Change status LED color on button press
			if(laserPointer.getLaserpointerMode() == Laserpointer.LP_MODE_DEFAULT) {
//				if(((butBin & 0x1) > 0) || ((butBin & 0x2) > 0) || ((butBin & 0x4) > 0)) {
//					if(laserPointer.getLED2color() != Laserpointer.LED_COLOR_BLUE)
//						laserPointer.setLED2color(Laserpointer.LED_COLOR_BLUE);
//				}
//				else if(laserPointer.getLED2color() != Laserpointer.LED_COLOR_GREEN) {
//					laserPointer.setLED2color(Laserpointer.LED_COLOR_GREEN);
//				}
			}
			
			if(laserPointer.getLaserpointerMode() == Laserpointer.LP_MODE_DEMO) {
				if((butBin & 0x1) > 0 && laserPointer.getLED5color() != laserPointer.getLED2color()) {
					laserPointer.setLED2color(laserPointer.getLED5color());
					updateStatus();
					vibrate(true, 3000);
				}
				else if((butBin & 0x2) > 0 && laserPointer.getLED4color() != laserPointer.getLED2color()) {
					laserPointer.setLED2color(laserPointer.getLED4color());
					updateStatus();
				}
				else if((butBin & 0x4) > 0 && laserPointer.getLED3color() != laserPointer.getLED2color()) {
					laserPointer.setLED2color(laserPointer.getLED3color());
					updateStatus();
				}
			}

			break;
		}
	}
	
	public synchronized void updateLEDStatus(int led) {
		if (!connected)
			return;
		if (led < 1 || led > 5)
			return;
		
		byte ledmsg = 0;
		if(led == 1) {
			ledmsg = (byte) (200 | laserPointer.getLED1color());
		}
		else if (led == 2) {
			ledmsg = (byte) (208 | laserPointer.getLED2color());
		}
		else if (led == 3) {
			ledmsg = (byte) (216 | laserPointer.getLED3color());
		}
		else if (led == 4) {
			ledmsg = (byte) (224 | laserPointer.getLED4color());
		}
		else if (led == 5) {
			ledmsg = (byte) (232 | laserPointer.getLED5color());
		}
		try {
			if (outputStream != null) {
				outputStream.write(ledmsg);
				outputStream.flush();
			}
		}
		catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			laserPointer.publishFailure(e);
		}
			
	}
	
	public synchronized void updateStatus() {
		if (!connected)
			return;
		try {			
			inertiaOn = laserPointer.isInertialActive();
			
			byte led1 = (byte) (200 | laserPointer.getLED1color());
			byte led2 = (byte) (208 | laserPointer.getLED2color());
			byte led3 = (byte) (216 | laserPointer.getLED3color());
			byte led4 = (byte) (224 | laserPointer.getLED4color());
			byte led5 = (byte) (232 | laserPointer.getLED5color());
			
			//Push, Intertia, Shutdown
			byte push = (byte) (pushMode?1:0);
			byte inertia = (byte) (inertiaOn?1:0);
			byte shutdown = (byte) (shutDown?1:0);
			byte mode1 = (byte)((shutdown * 1) | (inertia * 2) | (push * 4));
			
			//Laser, Vibration
			byte laser = (byte)(laserOn?1:0);
			byte vibrate = (byte)(isVibrating?1:0);
			byte mode2 = (byte) ((vibrate*1) | (laser * 2) | 128);
			
			if (outputStream != null) {
				outputStream.write(mode1);		
				outputStream.write(mode2);				
				outputStream.write(led1);
				outputStream.write(led2);
				outputStream.write(led3);
				outputStream.write(led4);
				outputStream.write(led5);
				outputStream.flush();
			}

		}
		catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			laserPointer.publishFailure(e);
		}
	}

	
    private String asciiToHex(String ascii){
        StringBuilder hex = new StringBuilder();
        
        for (int i=0; i < ascii.length(); i++) {
            hex.append(Integer.toHexString(ascii.charAt(i)));
        }
        
        return hex.toString();
    }

	public void vibrate(boolean vibrate, int duration) {
		if (laserVibration != null) {
			laserVibration.cancel();
		}
		isVibrating = vibrate;
		updateStatus();
		if (duration > 0) {
			laserVibration = new LaserVibration(this, duration);
		}
	}

	public void close() {
		if(laserPointer.getShutdownOnStop()) {
			shutDown = true;
			updateStatus();
		}
		running = false;
		try {
			inputStream.close();
			outputStream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (laserVibration != null) {
			laserVibration.cancel();
		}
		serialPort.close();
	}

}
