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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ietf.jgss.Oid;

/**
 * <code>TCPOutgoing</code>.
 *
 * <pre>
 * Date: Nov 9, 2008
 * Time: 10:48:41 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: TCPOutgoing.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class TCPOutgoing extends Thread {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(TCPOutgoing.class);
	
	private BufferedOutputStream outgoing;
	
	private boolean running = true;
	
	protected TCPOutgoing(Socket client) throws IOException {
		outgoing = new BufferedOutputStream(client.getOutputStream());
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (running) {
			
		}
	}
	
	/**
	 * @throws IOException
	 */
	public void close() throws IOException {
		running = false;
		if (outgoing != null) {
			outgoing.close();
		}
	}
}
