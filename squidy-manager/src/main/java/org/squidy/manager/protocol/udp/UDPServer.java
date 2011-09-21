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

package org.squidy.manager.protocol.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>UPDServer</code>.
 * 
 * <pre>
 * Date: Feb 28, 2008
 * Time: 3:50:55 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: UDPServer.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0
 * 
 */
public class UDPServer extends Thread {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(UDPServer.class);

	private DatagramSocket socket;

	private static final int BUFFER_SIZE = 1024;
	
	private String data = "";

	protected List<UDPListener> listeners = Collections.synchronizedList(new ArrayList<UDPListener>());

	private boolean running = true;

	public UDPServer(int port) {
		try {
			socket = new DatagramSocket(port);
			start();
			LOG.info("UDP Server started on port " + port);
		}
		catch (IOException e) {
			LOG.error("Couldn't start udp server on port " + port);
		}

	}

	public void addUDPListener(UDPListener listener) {
		listeners.add(listener);
	}

	public void removeUDPListener(UDPListener listener) {
		listeners.remove(listener);
	}

	public void close() {
		running = false;
		listeners.clear();
		socket.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (running) {
			try {
				byte[] buffer = new byte[BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
				socket.receive(packet);
				data = new String(packet.getData());
				int last = 0;
				while (last < BUFFER_SIZE) {
					if (buffer[last] == 0)
						break;
					last++;
				}
				data = data.substring(0, last);
				if (last == BUFFER_SIZE) {
					LOG.error("Input buffer overflow");
				} else {
					for (UDPListener listener : listeners) {
						listener.parseData(data);
					}
				}
				
				for (UDPListener listener : listeners) {
					listener.parseData(packet.getData());
				}
				
				for (UDPListener listener : listeners) {
					listener.receive(packet);
				}
			}
			catch (IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Connection error occured.");
				}
			}
		}
		socket.close();
	}
}
