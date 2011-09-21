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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 *
 */
public class ProxyTest extends Thread{
	private static Log logger = LogFactory.getLog(ProxyTest.class);

	private Socket sock = null;

	private OutputStream output = null;
	private InputStream input = null;

	private boolean connected = false;
	
	private boolean running = true;
	
	private Random r = new Random(512);

	public ProxyTest() {
		start();
	}

	public void run() {
		while(running){
			if(!connected){
				try {
					sock = new Socket("localhost", 6666);
					//sock.setTcpNoDelay(true);
					input = sock.getInputStream();
					output = sock.getOutputStream();
					connected = true;
				} catch (UnknownHostException e) {
					logger.error("Couldn't initiate connection.");
				} catch (IOException e) {}
			}
			if(running){
				try {
					while(connected){
						int inByte;
						while((inByte = input.read()) == 0xFF) {
							logger.debug("Received: " + Integer.toBinaryString(inByte) + "[" + inByte + "]");
							// send bytes
							byte[] bytes = new byte[640*512*3];
							r.nextBytes(bytes);
							for (int i = 0; i < bytes.length; i++) {
								if(i%3==0) {
									bytes[i+1] = 0; // G
									bytes[i+2] = 0; // R
								}
							}
							output.write(bytes);
						}
					}
				} catch (IOException e) {
					logger.error("Connection error occured.");
					connected = false;
				}
			}
		}
	}
	
	public void close(){
		logger.info("Close socket");
		running = false;
		interrupt();
		try {
			output.close();
			input.close();
			sock.close();
		} catch (Exception e) {
			logger.error("Couldn't close socket");
		}
	}
}
