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

package org.squidy.manager.protocol.tcp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>TCPServer</code>.
 * 
 * <pre>
 * Date: May 13, 2008
 * Time: 5:14:32 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: TCPServer.java 772 2011-09-16 15:39:44Z raedle $ * @since 1.0.0 * @since 1.0.0
 * @since 1.0
 */
public class TCPServer extends Thread {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(TCPServer.class);

	private ServerSocket serverSocket;

	private int port;

	// All incoming threads.
	protected List<TCPIncoming> incomings = Collections.synchronizedList(new ArrayList<TCPIncoming>());

	// All outgoing threads.
	protected List<TCPOutgoing> outgoings = Collections.synchronizedList(new ArrayList<TCPOutgoing>());

	// All listeners attached to this server.
	protected List<TCPListener> listeners = Collections.synchronizedList(new ArrayList<TCPListener>());

	private boolean running = true;

	public TCPServer(int port) {
		this.port = port;

		try {
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	public void addTCPListener(TCPListener listener) {
		listeners.add(listener);
	}

	public void removeTCPListener(TCPListener listener) {
		listeners.remove(listener);
	}

	public void close() {
		running = false;

		try {

			// Close all incomings.
			for (TCPIncoming incoming : incomings) {
				incoming.close();
			}

//			// Close all outgoings.
//			for (TCPOutgoing outgoing : outgoings) {
//				outgoing.close();
//			}

			// Closing server socket.
			serverSocket.close();

			if (LOG.isDebugEnabled()) {
				LOG.debug("Server socket on port " + port + " has been closed properly.");
			}
		}
		catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
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
				Socket socket = serverSocket.accept();

				TCPIncoming incoming = new TCPIncoming(listeners, socket);
				incomings.add(incoming);

				TCPOutgoing outgoing = new TCPOutgoing(socket);
				outgoings.add(outgoing);
			}
			catch (IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
				running = false;
			}
		}
	}
}
