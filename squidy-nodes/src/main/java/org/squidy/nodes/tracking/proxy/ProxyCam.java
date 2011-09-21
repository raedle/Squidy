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


package org.squidy.nodes.tracking.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.nodes.ir.ConfigManagable;
import org.squidy.nodes.tracking.config.ConfigNotifier;
import org.squidy.nodes.tracking.config.xml.Camera;


/**
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 *
 */
public class ProxyCam extends Thread implements ConfigNotifier {
	
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ProxyCam.class);

	private Socket socket = null;

	private boolean running = true;

	private ConfigManagable configManagable;

	private int numOfBytes = 50;

	private byte[] buffLarge = new byte[numOfBytes];
	byte[] bytes; // holds image data

	private OutputStream output = null;

	private BufferedInputStream input = null;

	private static Vector<ProxyCam> cams = new Vector<ProxyCam>();
	
	// stuff for debugging
	private boolean debug = false;
	private static final String DEBUGFILE_IN = "imagedata.txt";
	private BufferedWriter debugWriter;
	private NumberFormat nf = NumberFormat.getNumberInstance();
	
	int aoiW;
	int aoiH;
	
	int cam_id;
	
	private void initDebugOutput() {
		LOG.info("Creating debug file: " + DEBUGFILE_IN + ".");
		try {
			debugWriter = new BufferedWriter(new FileWriter(DEBUGFILE_IN,false));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public ProxyCam(ConfigManagable configManagable, Socket socket) {
		this.socket = socket;
		this.configManagable = configManagable;

		try {
			output = socket.getOutputStream();
			input = new BufferedInputStream(socket.getInputStream());
			configManagable.attachNotifier(this);
			start();
			cams.add(this);
		} catch (IOException e) {
			LOG.error("Couldn't initiate connection.");
		}
	}

	public static Vector<ProxyCam> getAttachedCams() {
		return cams;
	}

	public void close() {
		LOG.info("Close socket");
		cams.remove(this);
		running = false;
		configManagable.detachNotifier(this);
		try {
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			LOG.error("Couldn't close socket");
		}
	}

	public void updateConfig() {
		Camera cam = configManagable.getCalibrationCamera();
		if (cam == null)
			return;
		cam_id = Integer.parseInt(cam.getId());
		aoiW = Integer.parseInt(cam.getPropertyHashtable().get("aoiW")
				.getContent());
		aoiH = Integer.parseInt(cam.getPropertyHashtable().get("aoiH")
				.getContent());
		numOfBytes = aoiW * aoiH * 3;
		buffLarge = new byte[numOfBytes];
		LOG.debug("aoi: " + aoiW + " " + aoiH);

	}

	public void sendMessage(byte[] receive) throws IOException {
		if (output == null)
			return;

		if(cam_id != (int)(receive[0])){
			throw new IOException();
		}
			
		output.write(receive);
		output.flush();
		LOG.debug("Message sent.");
	}

	public void run() {
		
		updateConfig();

		while (running) {
			int bytesRead = 0;
			LOG.debug("wait for image");
			try {
				if(debug) {
					debugWriter.newLine();
					debugWriter.write("========= New image =========");
					debugWriter.newLine();
				}
				BufferedInputStream bis = new BufferedInputStream(input);
				
				// read 4-byte header with number of bytes and allocate byte[]
				DataInputStream dis = new DataInputStream(bis);
				int size = Integer.reverseBytes(dis.readInt());
				//System.out.println(Integer.toHexString(size));
				//System.out.println(size);
				
				byte[] sizebytes = new byte[4];
				sizebytes[0] = (byte)(size >> 24);
				sizebytes[1] = (byte)(size >> 16);
				sizebytes[2] = (byte)(size >> 8);
				sizebytes[3] = (byte) size;
				ProxyConfig.getConfigConnection().sendMessage(sizebytes);

				bytes = new byte[size];
				
				bytesRead = 0;
				do {
					bytes[bytesRead] = (byte)bis.read();
					bytesRead++;
				} while(bytesRead < size);
				
				LOG.debug("Total bytes read: " + bytesRead + ".");
				
				if(debug) {
					debugWriter.newLine();
					debugWriter.write("========= " + aoiW + "x" + aoiH + " =========");
					debugWriter.newLine();
					debugWriter.flush();
				}
				
			} catch (IOException e) {
				LOG.error("Connection error occured.");
				close();
				return;
			}
			
			try {
				ProxyConfig.getConfigConnection().sendMessage(bytes);
			} catch (IOException e) {
				LOG.error("Couldn't send image to config.");
			}
		}

		close();
	}
}
