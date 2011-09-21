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

package org.squidy.manager.commander;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>ControlServer</code>.
 *
 * <pre>
 * Date: Sep 20, 2008
 * Time: 10:55:18 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ControlServer.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class ControlServer extends Thread {
	
	public static void main(String[] args) throws Exception {
		
		if (args.length > 1) {
			System.out.println("Usage: java -jar <JAR_FILE> <PORT>");
		}
		
		if (args.length == 1) {
			int port = Integer.parseInt(args[0]);
			
			new ControlServer(port);
		}
		else {
			new ControlServer();
		}
	}
	
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ControlServer.class);

	private ControlServerContext context;
	
	private ServerSocket server;
	
	private Collection<Socket> clients;
	
	private Collection<Incoming> incomings;
	
	private Collection<Outgoing> outgoings;
	
	private boolean running;
	
	public ControlServer() throws IOException {
		this(9999);
	}
	
	public ControlServer(int port) throws IOException {
		server = new ServerSocket(port);
		
		context = new ControlServerContext();
		clients = new ArrayList<Socket>();
		incomings = new ArrayList<Incoming>();
		outgoings = new ArrayList<Outgoing>();
		
		running = true;
		
		start();
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Started control server on port " + port);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			incomings = new ArrayList<Incoming>();
			outgoings = new ArrayList<Outgoing>();
			
			while (running) {
				Socket client = server.accept();
				client.setTcpNoDelay(true);
				
				if (LOG.isInfoEnabled()) {
					LOG.info("Client connected: " + client.getInetAddress() + " on port " + client.getPort());
				}
				
				clients.add(client);
				
				Incoming incoming = new Incoming(client.getInputStream(), context);
				Outgoing outgoing = new Outgoing(client.getOutputStream(), context);
				
				ConnectionPeer connectionPeer = new ConnectionPeer(incoming, outgoing);
				
				incomings.add(incoming);
				outgoings.add(outgoing);
			}
			
			for (Incoming incoming : incomings) {
				incoming.close();
			}
			
			for (Outgoing outgoing : outgoings) {
				outgoing.close();
			}
			
			for (Socket client : clients) {
				client.close();
			}
		}
		catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Error occured in control server: " + e.getMessage(), e);
			}
		}
	}
	
	public void close() throws IOException {
		running = false;
		server.close();
	}
}
