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

package org.squidy.nodes.laserpointer.proxy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 *
 */
public class ProxyConfig extends Thread{
	private static Log logger = LogFactory.getLog(ProxyConfig.class);
	private Socket socket = null;
	private boolean running = true;

	private OutputStream output = null;
	private BufferedInputStream input = null;
	
	private static ProxyConfig instance = null;

	public ProxyConfig(Socket socket) {
		this.socket = socket;
		try {
			output = socket.getOutputStream();
			input = new BufferedInputStream(socket.getInputStream());
			start();
			instance = this;
		} catch (IOException e) {
			logger.error("Couldn't initiate connection.");
		}
	}
	
	public static ProxyConfig getConfigConnection(){
		return instance;
	}
	
	public void close(){
		logger.info("Close socket");
		running = false;
		try {
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			logger.error("Couldn't close socket");
		}
	}
	
	public void sendMessage(byte[] buf) throws IOException{
		if(output==null) return;
		output.write(buf);
		output.flush();
		logger.debug("Message send.");
	}

	public void run() {
		try {

			while (running) {
				
				byte[] buffSmall = new byte[1];
				
				input.read(buffSmall);
				logger.debug("Small Packet received:"+buffSmall[0]);
				if(buffSmall[0]==0){
					throw new IOException();
				}
				
				boolean send = false;
				for (ProxyCam cam : ProxyCam.getAttachedCams()) {
					try {
						cam.sendMessage(buffSmall);
						send = true;
					} catch (IOException e) {
						logger.error("Couldn't send message to camera");
						running = false;
						break;
					}
				}
				if(!send){
					throw new IOException();
				}
			}
			
			input.close();
			output.close();
			socket.close();
			
		} catch (IOException e) {
			logger.error("Connection error occured.");
			close();
		}
	}
}
