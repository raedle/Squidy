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


package org.squidy.nodes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.protocol.tcp.AsyncSocket;
import org.squidy.manager.protocol.tcp.AsyncSocketCallback;


/**
 * <code>InertiaCube</code>.
 * 
 * <pre>
 * Date: Jan 10, 2009
 * Time: 4:45:45 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: InertiaCube.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "Inertia Cube")
@Processor(
	name = "Inertia Cube",
	types = { Processor.Type.INPUT },
	description = "/org/squidy/nodes/html/InertiaCube.html",
	tags = { "interia", "InterSense", "cube" },
	status = Status.UNSTABLE
)
public class InertiaCube extends AbstractNode implements AsyncSocketCallback {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(InertiaCube.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "host")
	@Property(
		name = "Host"
	)
	@TextField
	private String host = "127.0.0.1";

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		if (this.host.equals(host)) {
			return;
		}

		this.host = host;

		if (isProcessing()) {
			disconnectFromServer();
			connectToServer();
		}
	}

	@XmlAttribute(name = "port")
	@Property(
		name = "Port"
	)
	@TextField
	private int port = 17283;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		if (this.port == port) {
			return;
		}

		this.port = port;

		if (isProcessing()) {
			disconnectFromServer();
			connectToServer();
		}
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private AsyncSocket client;
	
	enum TagMode {
		RAW_VALUE
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {
		connectToServer();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		disconnectFromServer();
	}

	/**
	 * 
	 */
	private void connectToServer() throws ProcessException {
		try {
			client = new AsyncSocket(this, InetAddress.getByName(host), port);
			
			client.readToByteSequence(this, AsyncSocket.CRLF, TagMode.RAW_VALUE);
		}
		catch (UnknownHostException e) {
			throw new ProcessException(e);
		}
		catch (IOException e) {
			throw new ProcessException(e);
		}
	}

	/**
	 * 
	 */
	private void disconnectFromServer() throws ProcessException {
		try {
			if (client != null) {
				client.close();
			}
		}
		catch (IOException e) {
			throw new ProcessException(e.getMessage(), e);
		}
	}
	
	// ################################################################################
	// BEGIN OF AsyncSocketCallback
	// ################################################################################
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.protocol.tcp.AsyncSocketCallback#didReadBytes(org.squidy.manager.protocol.tcp.AsyncSocket, byte[])
	 */
	public void didReadBytes(AsyncSocket asyncSocket, byte[] data, Enum<?> tag) {
		client.readToByteSequence(this, AsyncSocket.CRLF, TagMode.RAW_VALUE);
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.protocol.tcp.AsyncSocketCallback#didReadToByteSequence(org.squidy.manager.protocol.tcp.AsyncSocket, byte[])
	 */
	public void didReadToByteSequence(AsyncSocket asyncSocket, byte[] data, Enum<?> tag) {
		String values = new String(data);
		
		String[] value = values.split(",");
		
		double x = Double.valueOf(value[0]);
		double y = Double.valueOf(value[1]);
		double z = Double.valueOf(value[2]);
		
		// TODO publish inertia and 3D orientation...
		publish(new DataPosition2D(InertiaCube.class, x, y));
		publish(new DataInertial(InertiaCube.class, x, y, z, false));
		
		client.readToByteSequence(this, AsyncSocket.CRLF, TagMode.RAW_VALUE);
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.protocol.tcp.AsyncSocketCallback#exceptionOccured(java.lang.Exception)
	 */
	public void exceptionOccured(Exception e) {
		if (LOG.isErrorEnabled()) {
			LOG.error(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.protocol.tcp.AsyncSocketCallback#ready(org.squidy.manager.protocol.tcp.AsyncSocket)
	 */
	public void ready(AsyncSocket asyncSocket) {
//		System.out.println("READY TO READ");
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.protocol.tcp.AsyncSocketCallback#disconnected(org.squidy.manager.protocol.tcp.AsyncSocket)
	 */
	public void disconnected(AsyncSocket asyncSocket) {
		// TODO Auto-generated method stub
		
	}
	
	// ################################################################################
	// END OF AsyncSocketCallback
	// ################################################################################
}
