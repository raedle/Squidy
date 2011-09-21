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

package org.squidy.nodes.laserpointer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.nodes.ir.ConfigManagable;


/**
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * 
 */
public class LaserServer extends Thread {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(LaserServer.class);

	private int port;

	private boolean running = true;

	private Collection<LaserConnection> laserConnections = new Vector<LaserConnection>();

	ServerSocket serverSocket;

	private ConfigManagable configManagable;

	/**
	 * @param configManagable
	 * @param port
	 */
	public LaserServer(ConfigManagable configManagable, int port) {
		this.configManagable = configManagable;
		this.port = port;
		running = true;
		start();
	}

	/**
	 * 
	 */
	public void close() {
		LOG.info("Close server.");
		running = false;
		if (serverSocket != null) {
			try {
				serverSocket.close();
			}
			catch (IOException e) {
				LOG.error("Couldn't close socket");
			}
		}
		for (LaserConnection t : laserConnections) {
			t.close();
		}
		laserConnections.clear();
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		try {
			serverSocket = new ServerSocket(port);
		}
		catch (Exception e) {
			LOG.error("Couldn't start server on port " + port, e);
			return;
		}
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Server started on port " + port);
		}
		
		while (running) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				LaserConnection tmp = new LaserConnection(configManagable, socket);
				laserConnections.add(tmp);
			}
			catch (SocketException e) {
				if (running && LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			}
			catch (IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
				continue;
			}
		}
	}
}