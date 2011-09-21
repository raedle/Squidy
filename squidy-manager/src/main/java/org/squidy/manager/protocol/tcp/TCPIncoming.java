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
import java.net.Socket;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>TCPIncoming</code>.
 *
 * <pre>
 * Date: Nov 9, 2008
 * Time: 10:29:01 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: TCPIncoming.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class TCPIncoming extends Thread {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(TCPIncoming.class);
	
	private List<TCPListener> listeners;
	
	private BufferedInputStream incoming;
	
	private boolean running = true;
	
	/**
	 * @param listeners
	 * @param client
	 * @throws IOException
	 */
	protected TCPIncoming(List<TCPListener> listeners, Socket client) throws IOException {
		this.listeners = listeners;
		incoming = new BufferedInputStream(client.getInputStream());
		start();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			while (running) {
				ByteArrayOutputStream output = new ByteArrayOutputStream();

				byte[] bytes = new byte[1024];
				int bytesRead;
				while ((bytesRead = incoming.read(bytes)) > 0) {
					output.write(bytes, 0, bytesRead);
				}

				for (TCPListener listener : listeners) {
					listener.parseData(String.valueOf(output.toByteArray()));
				}
			}
		}
		catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * @throws IOException
	 */
	public void close() throws IOException {
		running = false;
		if (incoming != null) {
			incoming.close();
		}
	}
}
