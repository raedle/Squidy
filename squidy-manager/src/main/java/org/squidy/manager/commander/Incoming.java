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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.commander.command.ICommand;


/**
 * <code>Incoming</code>.
 * 
 * <pre>
 * Date: Sep 20, 2008
 * Time: 10:42:02 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: Incoming.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class Incoming extends Thread {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Incoming.class);
	
	private ControlServerContext context;
	
	private boolean running;

	private Queue<ICommand> commandQueue = new ConcurrentLinkedQueue<ICommand>();

	private ObjectInputStream inputStream;
	
	private ConnectionPeer connectionPeer;

	/**
	 * @param connectionPeer
	 */
	public void setConnectionPeer(ConnectionPeer connectionPeer) {
		this.connectionPeer = connectionPeer;
	}

	/**
	 * @param inputStream
	 * @param context
	 * @throws IOException
	 */
	public Incoming(InputStream inputStream, ControlServerContext context) throws IOException {
		this.inputStream = new ObjectInputStream(inputStream);
		this.context = context;
		running = true;

		start();
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
				Object o = inputStream.readObject();
				
				if (LOG.isDebugEnabled()) {
					LOG.debug("Received object " + o + " of class " + o.getClass());
				}
				
				if (o instanceof ICommand) {
//					ICommand cmd = ((ICommand) o).execute(context);
					((ICommand) o).execute(context);
					
//					if (connectionPeer != null) {
//						connectionPeer.send(cmd);
//					}
				}
			}
			catch (EOFException e) {
				try {
					close();
				}
				catch (IOException e1) {
					if (LOG.isErrorEnabled()) {
						LOG.error(e1.getMessage(), e1);
					}
				}
			}
			catch (SocketException e) {
				try {
					if (LOG.isInfoEnabled()) {
						LOG.info("Closing incoming thread caused by: " + e.getMessage(), e);
					}
					close();
				}
				catch (IOException e1) {
					if (LOG.isInfoEnabled()) {
						LOG.info("Closing incoming thread caused by: " + e.getMessage(), e);
					}
					if (LOG.isErrorEnabled()) {
						LOG.error(e1.getMessage(), e1);
					}
				}
			}
			catch (IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			}
			catch (ClassNotFoundException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * @throws IOException
	 */
	public void close() throws IOException {
		running = false;
		commandQueue.clear();
		inputStream.close();
	}
}
