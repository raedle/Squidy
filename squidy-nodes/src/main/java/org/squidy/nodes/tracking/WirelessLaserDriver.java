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
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.nodes.Laserpointer;


/**
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * 
 */
public class WirelessLaserDriver extends Thread implements SerialPortEventListener, LaserVibrate {
	private static Logger logger = Logger.getLogger(WirelessLaserDriver.class);

	private String delimiter = ";";

	private Laserpointer laserPointer = null;

	private static CommPortIdentifier portId;

	private static Enumeration portList;

	private InputStream inputStream;

	private SerialPort serialPort;

	private OutputStream outputStream;

	private String bufferInLine = "";

	private int discardPackets = 25;

	private int firstToken = 2;

	private int firstTokenCounter = firstToken;

	private byte[] readBuffer = new byte[200];

	private StringTokenizer line, toker;

	private boolean greenFront = true;

	private boolean greenBack = true;

	private boolean redFront = true;

	private boolean redBack = true;

	private boolean isVibrating = false;

	private boolean toggle = true;

	private LaserVibration laserVibration;

	private int alarmReset = 5;// property-file
	private int alarmCounter = alarmReset;
	private int reconnectLoop = 100;
	private int stopReconnectLoop = -1200;
	private boolean alarmed = false;
	private boolean connected = true;

	private boolean running = true;

	private boolean isConfig = false;
	private String[] mac_srv = { "01", "03", "05" };
	private String[] mac_lp = { "02", "04", "06" };
	private int currSerNo = 0;

	private double acc_a_zero, acc_b_zero, acc_c_zero;
	private double acc_a_1, acc_b_1, acc_c_1;
	private double acc_a_2, acc_b_2, acc_c_2;
	private double acc_a_3, acc_b_3, acc_c_3;

	// private FileOutputStream fos = null;

	public WirelessLaserDriver(Laserpointer laserPointer, String port) {
		// try {
		// fos = new FileOutputStream("serial-bytes");
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//		
		boolean portFound = false;
		laserPointer = laserPointer;

		// parse ports and if the default port is found, initialized the reader
		portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(port)) {
					logger.info("Found port: " + port);
					portFound = true;
					if (initStream()) {
						initAlarm();
						currSerNo = laserPointer.getSerialNumber();
						start();
						// LaserDriverTestZKM t = new LaserDriverTestZKM();
						break;
					}
				}
			}

		}
		if (!portFound) {
			logger.error("port " + port + " not found.");
		}
	}

	public boolean initStream() {
		// initalize serial port
		try {
			serialPort = (SerialPort) portId.open("LaserDriver", 2000);

			inputStream = serialPort.getInputStream();

			// serialPort.addEventListener(this);

			// activate the DATA_AVAILABLE notifier
			// serialPort.notifyOnDataAvailable(true);

			// set port parameters
			serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// get the outputstream
			outputStream = serialPort.getOutputStream();

			// activate the OUTPUT_BUFFER_EMPTY notifier
			// serialPort.notifyOnOutputEmpty(true);

//			if (laserPointer.isRestartLaserPointerExe()) {
//				try {
//					Runtime.getRuntime().exec("cmd.exe /c start /MIN resetLP.exe 10");
//				}
//				catch (IOException e) {
//					logger.error("Couldn't start the batch file.");
//				}
//			}

			return true;

		}
		catch (Exception e) {
			logger.error("Error setting serial connection");
			return false;
		}

	}

	private void initAlarm() {
		alarmReset = laserPointer.getWaitForAlarm();
		new Thread() {
			public void run() {
				while (running) {
					try {
						sleep(100);
					}
					catch (InterruptedException e) {
					}
					if (--alarmCounter < 0 && connected) {
						// alarm
						laserPointer.publish(new DataDigital(laserPointer.getClass(), true));
						connected = false;
						alarmed = true;
						bufferInLine = "";
						firstTokenCounter = firstToken;
						logger.error("Laserpointer not reachable");
						isVibrating = false;
						DataDigital data = new DataDigital(laserPointer.getClass(), true);
						laserPointer.publish(data);
					}
					// System.out.println(alarmCounter);
					if (!connected && alarmed && alarmCounter > stopReconnectLoop && alarmCounter < 0
							&& (alarmCounter % reconnectLoop == 0)) {
						// System.out.println("RESET");
						DataDigital data = new DataDigital(laserPointer.getClass(), true);
						laserPointer.publish(data);
					}

					if (connected && alarmed) {
						laserPointer.publish(new DataDigital(laserPointer.getClass(), false));
						alarmed = false;
						logger.info("Laserpointer reconnected");
						vibrate(false, 0);
					}
					if (alarmCounter < -100000) {
						alarmCounter = -100000;
					}
				}
			}
		}.start();
	}

	public synchronized void serialEvent(SerialPortEvent event) {
		// switch (event.getEventType()) {
		// case SerialPortEvent.BI:
		// case SerialPortEvent.OE:
		// case SerialPortEvent.FE:
		// case SerialPortEvent.PE:
		// case SerialPortEvent.CD:
		// case SerialPortEvent.CTS:
		// case SerialPortEvent.DSR:
		// case SerialPortEvent.RI:
		// case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
		// break;
		// case SerialPortEvent.DATA_AVAILABLE:
		// try {
		// readInput();
		// // jbReadInput();
		// } catch (IOException e) {
		// logger.error("Connection error occured (Receiving).");
		// DataDigital data = new DataDigital(lp.getClass(),"RESET-LP", true);
		// lp.pushSample(data);
		// }
		// break;
		// }
	}

	// public void jbReadInput() throws IOException {
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// byte[] bytes = new byte[300];
	// while(inputStream.available() > 0) {
	// int bytesRead = inputStream.read(bytes);
	// baos.write(bytes, 0, bytesRead);
	// }
	// fos.write(baos.toByteArray());
	// fos.flush();
	// }

	// private String readSerial() throws IOException {
	//
	// String result = "";
	//
	// while (inputStream.available() > 0) {
	//			
	// //int bytesRead = inputStream.read(readBuffer);
	//			
	// // ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// // baos.write(readBuffer, 0, bytesRead-1);
	// // fos.write(baos.toByteArray());
	//			
	// for (int i = 0; i < readBuffer.length; i++) {
	// readBuffer[i] = '_';
	// }
	// inputStream.read(readBuffer);
	// for (int i = 0; i < readBuffer.length; i++) {
	// if (readBuffer[i] == 0 || (readBuffer[i] & 0x80) > 0) {
	// readBuffer[i] = '_';
	// }
	// }
	// result += new String(readBuffer);
	// //System.out.println(".");
	// }
	// result = removeEmptySpaces(result);
	// // fos.flush();
	//		
	// //System.out.println("..."+result+" "+result.getBytes().length);
	//
	// return result;
	// }

	public void run() {

		int len = -1;
		String result = "";
		for (int i = 0; i < readBuffer.length; i++) {
			readBuffer[i] = '_';
		}
		while (running) {

			try {
				while ((len = this.inputStream.read(readBuffer)) > 0 && running) {
					if (len > 0) {

						// Calendar c = Calendar.getInstance();

						// System.out.println("buffer:"+new String(readBuffer));

						alarmCounter = alarmReset;
						if (!connected) {
							bufferInLine = "";
							connected = true;
							updateStatus();
							continue;
						}

						for (int i = 0; i < readBuffer.length; i++) {
							if (readBuffer[i] == 0 || (readBuffer[i] & 0x80) > 0) {
								readBuffer[i] = '_';
							}
						}

						result = removeEmptySpaces(new String(readBuffer));

						for (int i = 0; i < readBuffer.length; i++) {
							readBuffer[i] = '_';
						}

						if (isConfig) {
							System.out.println("Received: " + result);
							continue;
						}

						// System.out.println(bufferInLine+"----"+result);

						if (discardPackets < 0) {
							bufferInLine = bufferInLine + result;
							parseStatus();
						}
						else {
							discardPackets--;
							bufferInLine = "";
						}

						// System.out.println(c.getTimeInMillis());

					}
					try {
						sleep(4);
					}
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			catch (IOException e) {
				logger.error("Connection error occured.");
			}
		}
	}

	// private synchronized void readInput() throws IOException {
	// alarmCounter = alarmReset;
	// if(!connected){
	// bufferInLine = "";
	// connected = true;
	// updateStatus();
	// return;
	// }
	//		
	// String result = readSerial();
	//		
	// if(result.length()==0) return;
	//		
	// if(isConfig){
	// System.out.println("Received: "+result);
	// return;
	// }
	//		
	// if (discardPackets < 0) {
	// bufferInLine = bufferInLine + result;
	// parseStatus();
	// } else {
	// discardPackets--;
	// }
	// }

	private String removeEmptySpaces(String str) {
		for (int i = str.length() - 1; i >= 0; i--) {
			if (str.charAt(i) == '_') {
				str = str.substring(0, i) + str.substring(i + 1, str.length());
			}
		}
		return str;
	}

	private void parseStatus() {
		if (bufferInLine.indexOf("\r") == -1 || bufferInLine.length() < 38) {
			return;
		}

		// System.out.println(bufferInLine);

		line = new StringTokenizer(bufferInLine, "\r");

		String token, butHex;
		String leftOver = "";

		while (line.hasMoreTokens()) {
			token = line.nextToken();
			// System.out.println(token);
			toker = new StringTokenizer(token, delimiter);

			if (toker.countTokens() == 10 && token.length() == 38) {

				butHex = toker.nextToken();
				int butBin = Integer.parseInt(butHex, 16);

				toker.nextToken();
				toker.nextToken();
				toker.nextToken();
				toker.nextToken();
				toker.nextToken();
				toker.nextToken();
				double raw_a = Integer.parseInt(toker.nextToken(), 16); // Sensor_Z,
																		// Wii_-Y
				double raw_b = Integer.parseInt(toker.nextToken(), 16); // Sensor_X,
																		// Wii_-Z
				double raw_c = Integer.parseInt(toker.nextToken(), 16); // Sensor_Y,
																		// Wii_X

				// System.out.println(a+"("+raw_a+") "+b+"("+raw_b+")
				// "+c+"("+raw_c+") ");

				if (firstTokenCounter < 0) {
					laserPointer.publish(new DataInertial(laserPointer.getClass(), raw_a, raw_b, raw_c, true));

					laserPointer.publish(new DataButton(Laserpointer.class, DataButton.BUTTON_1, !((butBin & 0x2) > 0)));
					laserPointer.publish(new DataButton(Laserpointer.class, DataButton.BUTTON_2, !((butBin & 0x10) > 0)));
					laserPointer.publish(new DataButton(Laserpointer.class, DataButton.BUTTON_3, !((butBin & 0x4) > 0)));
				}
				else {
					firstTokenCounter--;
					// System.out.println("ignore");
				}

			}
			else {
				if (line.countTokens() == 0 && token.length() < 38) {
					leftOver = token;
				}
				else {
					logger.error("Wrong token size: " + toker.countTokens() + " " + token);
				}
			}
		}
		bufferInLine = leftOver;

	}

	public void vibrate(boolean vibrate, final int duration) {
		if (laserVibration != null) {
			laserVibration.cancel();
		}
		// if(isVibrating!=vib){
		isVibrating = vibrate;
		updateStatus();
		// }
		if (duration > 0) {
			laserVibration = new LaserVibration(this, duration);
		}
	}

	public void setGreenLED(boolean green) {
		greenBack = green;
		greenFront = green;
		updateStatus();
	}

	public void setRedLED(boolean red) {
		redFront = red;
		redBack = red;
		updateStatus();
		DataDigital data = new DataDigital(laserPointer.getClass(), true);
		laserPointer.publish(data);
	}

	public void setLEDColor(boolean red, boolean green) {
		redFront = red;
		redBack = red;
		greenBack = green;
		greenFront = green;
		updateStatus();
	}

	private synchronized void updateStatus() {
		if (!connected)
			return;
		try {
			toggle = !toggle;
			int t = (toggle) ? 1 : 0;
			int v = (isVibrating) ? 1 : 0;
			int rf = (redFront) ? 0 : 1;
			int rb = (redBack) ? 0 : 1;
			int gf = (greenFront) ? 0 : 1;
			int gb = (greenBack) ? 0 : 1;

			int res = 0;
			if (currSerNo == 1)
				res = (gf * 1) | (rf * 2) | (gb * 4) | (0 * 8) | (rb * 16) | (t * 32) | (v * 64) | (1 * 128);
			if (currSerNo == 0)
				res = (rf * 1) | (gf * 2) | (0 * 4) | (rb * 8) | (t * 16) | (gb * 32) | (v * 64) | (1 * 128);

			if (outputStream != null) {
				outputStream.write(res & 0xff);
				outputStream.flush();
			}

		}
		catch (IOException e) {
			logger.error("Connection error occured (Update).");
		}
	}

	public void updateMAC() {
		isConfig = true;
		currSerNo = laserPointer.getSerialNumber();
		logger.info("ChangeMAC");
		try {
			writeSerial("+++");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("ats4=");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("00-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("15-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("20-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("00-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("00-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("00-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("00-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial(mac_srv[currSerNo]);

			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("\r\n");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("ats5=");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("00-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("15-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("20-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("00-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("00-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("00-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("00-");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial(mac_lp[currSerNo]);

			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("\r\n");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

			writeSerial("at0\r\n");
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}

		}
		catch (IOException e1) {
			logger.error("Connection error occured (MAC).");
		}
		finally {
			isConfig = false;
		}
	}

	private synchronized void writeSerial(String str) throws IOException {
		// logger.debug("write serial "+str);
		if (outputStream != null && connected) {
			outputStream.write(str.getBytes());
			outputStream.flush();
		}
	}

	public void close() {
		// try {
		// fos.close(); // TODO: DEBUG jb
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		logger.info("Close serial connection");
		running = false;
		try {
			inputStream.close();
			outputStream.close();
		}
		catch (Exception e) {
			logger.error("Couldn't close socket");
		}
		if (laserVibration != null) {
			laserVibration.cancel();
		}
		serialPort.close();
	}

}
