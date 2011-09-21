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

package org.squidy.manager.protocol.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.protocol.udp.UDPListener;
import org.squidy.manager.protocol.udp.UDPServer;


/**
 * <code>MuliticastServer</code>.
 *
 * <pre>
 * Date: Oct 17, 2008
 * Time: 7:56:58 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: MulticastServer.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
// TODO [RR]: Use UDPServer as superclass.
public class MulticastServer extends Thread {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(UDPServer.class);

	private MulticastSocket socket;
	
	private InetAddress group;

	private static final int BUFFER_SIZE = 1024;
	
	private String data = "";

	protected List<MulticastListener> listeners = Collections.synchronizedList(new ArrayList<MulticastListener>());

	private boolean running = true;

	public MulticastServer(InetAddress group, int port) {
		try {
			this.group = group;
			
			socket = new MulticastSocket(port);
			socket.joinGroup(group);
			start();
			LOG.info("Multicast server started on port " + port);
		}
		catch (IOException e) {
			LOG.error("Couldn't start multicast server on port " + port);
		}

	}

	public void addMulticastListener(MulticastListener listener) {
		listeners.add(listener);
	}

	public void removeMulticastListener(MulticastListener listener) {
		listeners.remove(listener);
	}

	public void close() {
		running = false;
		listeners.clear();
		
		try {
			socket.leaveGroup(group);
		}
		catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		
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
					for (MulticastListener listener : listeners) {
						listener.parseData(data);
					}
				}
				
				for (MulticastListener listener : listeners) {
					listener.parseData(packet.getData());
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
