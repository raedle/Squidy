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

package org.squidy.nodes.tracking.config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>ConfigServer</code>.
 * 
 * <pre>
 * Date: Jun 26, 2008
 * Time: 12:42:16 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: ConfigServer.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0
 */
public class ConfigServer extends Thread {

	// Logger to log info, error, debug,... messages.
	private static Log LOG = LogFactory.getLog(ConfigServer.class);

	private ServerSocket serverSocket;
	private int port;
	private ConfigManager configManager;
	private Collection<ConfigConnection> configConnections = new ArrayList<ConfigConnection>();

	private boolean running = true;

	public ConfigServer(ConfigManager configManager, int port) {
		this.configManager = configManager;
		this.port = port;
		running = true;
		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Couldn't start config server on port " + port + ": "
						+ e.getMessage(), e);
			}
			return;
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("Server started on port " + port);
		}

		while (running) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
				ConfigConnection configConnection = new ConfigConnection(
						configManager, clientSocket);
				configConnections.add(configConnection);
			} catch (IOException e) {
				if (running) {
					if (LOG.isErrorEnabled()) {
						LOG.error(e.getMessage(), e);
					}
					continue;
				}
			}
		}
	}

	/**
	 * 
	 */
	public void close() {
		if (LOG.isInfoEnabled()) {
			LOG.info("Close server.");
		}

		// Stop connection thread.
		running = false;

		// Close all connected client config connections.
		for (ConfigConnection configConnection : configConnections) {
			configConnection.close();
		}
		configConnections.clear();

		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}
}
