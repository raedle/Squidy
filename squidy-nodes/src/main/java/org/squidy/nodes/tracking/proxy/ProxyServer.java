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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.nodes.ir.ConfigManagable;


/**
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * 
 */
public class ProxyServer extends Thread {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ProxyServer.class);

	private int port;

	private boolean running = true;

	private ConfigManagable configManagable;

	private Vector<ProxyConfig> confs = new Vector<ProxyConfig>();
	private Vector<ProxyCam> cams = new Vector<ProxyCam>();

	ServerSocket s = null;

	boolean isConfigServer = false;

	public ProxyServer(ConfigManagable configManagable, int port, boolean isConfigServer) {
		this.configManagable = configManagable;
		this.port = port;
		this.isConfigServer = isConfigServer;
		running = true;
		start();
	}

	public void close() {
		LOG.info("Close server.");
		running = false;
		if (s != null) {
			try {
				s.close();
			}
			catch (IOException e) {
				LOG.error("Couldn't close socket");
			}
		}
		for (ProxyConfig t : confs) {
			t.close();
		}
		confs.clear();
		for (ProxyCam t : cams) {
			t.close();
		}
		cams.clear();
	}

	public void run() {

		try {
			s = new ServerSocket(port);
		}
		catch (Exception e) {
			LOG.error("Couldn't start server on port " + port);
			return;
		}
		LOG.info("Server started on port " + port);

		while (running) {
			Socket in = null;
			try {
				in = s.accept();
				if (isConfigServer) {
					confs.add(new ProxyConfig(in));
					LOG.info("Config-client attached");
				}
				else {
					cams.add(new ProxyCam(configManagable, in));
					LOG.info("Cam attached");
				}
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
