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

package org.squidy.manager.protocol.osc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.SquidyException;

import com.illposed.osc.Endian;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;


/**
 * <code>OSCServer</code>.
 *
 * <pre>
 * Date: May 16, 2008
 * Time: 4:33:17 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: OSCServer.java 772 2011-09-16 15:39:44Z raedle $  * @since 1.0.0  * @since 1.0.0
 * @since 1.0
 */
public class OSCServer extends Thread {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(OSCServer.class);
	
	public static final String STANDARD_SQUIDY_OSC_ADDRESS = "/squidy/bridge/osc";
	
	protected Map<String, List<OSCListener>> listenerMap = Collections.synchronizedMap(new HashMap<String, List<OSCListener>>());
	
	private String oscAddressOut;
	
	private OSCPortOut oscPortOut;
	
	private OSCPortIn oscPortIn;
	
	public OSCServer(String addressOut, int portOut, int portIn, Endian endian) {
		this(addressOut, portOut, endian);
		
		try {
			oscPortIn = new OSCPortIn(portIn, endian);
		} catch (SocketException e) {
			throw new SquidyException(e.getMessage(), e);
		}
	}
	
	/**
	 * @param portIn
	 * @param endian
	 */
	public OSCServer(int portIn, Endian endian) {
		try {
			oscPortIn = new OSCPortIn(portIn, endian);
		} catch (SocketException e) {
			throw new SquidyException("Could not open socket on port " + portIn, e);
		}
	}
	
	/**
	 * 
	 * @param addressOut
	 * @param portOut
	 * @param endian
	 * 
	 * @deprecated use {@link #OSCServer(String, int)}
	 */
	@Deprecated
	public OSCServer(String addressOut, int portOut, Endian endian) {
		this(addressOut, portOut);
	}
	
	public OSCServer(String addressOut, int portOut) {
		try {
			oscPortOut = new OSCPortOut(InetAddress.getByName(addressOut), portOut);
		}
		catch (SocketException e) {
			throw new SquidyException(e.getMessage(), e);
		}
		catch (UnknownHostException e) {
			throw new SquidyException(e.getMessage(), e);
		}
	}
	
	/**
	 * Starts OSC server if it is not already listening.
	 */
	public final void startListening() {
		if (!oscPortIn.isListening()) {
			oscPortIn.startListening();
		}
	}
	
	/**
	 * Stops OSC server if it is already listening.
	 */
	public final void stopListening() {
		if (oscPortIn != null && oscPortIn.isListening()) {
			oscPortIn.stopListening();
		}
	}
	
	/**
	 * Returns whether the OSC server is listening or not.
	 */
	public final boolean isListening() {
		return oscPortIn.isListening();
	}
	
	/**
	 * Closes open OSC port.
	 */
	public final void close() {
		if (oscPortIn != null) {
			oscPortIn.close();
		}
	}
	
	/**
	 * @param address
	 * @param listener
	 */
	public void addOSCListener(final String address, OSCListener listener) {
		List<OSCListener> listeners = listenerMap.get(address);
		if (listeners == null) {
			listeners = new ArrayList<OSCListener>();
			listenerMap.put(address, listeners);
		}
		listeners.add(listener);
		
		com.illposed.osc.OSCListener oscListener = new com.illposed.osc.OSCListener() {

			/* (non-Javadoc)
			 * @see com.illposed.osc.OSCListener#acceptMessage(java.util.Date, com.illposed.osc.OSCMessage)
			 */
			public void acceptMessages(Date date, OSCMessage[] messages) {
				for (OSCListener listener : listenerMap.get(address)) {
					listener.handleMessages(messages);
				}
			}
		};
		
		oscPortIn.addListener(address, oscListener);
	}
	
	/**
	 * @param packet
	 */
	public void send(OSCPacket packet) {
		try {
			oscPortOut.send(packet);
		}
		catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	public static void main(String[] args) {
		OSCServer server = new OSCServer("localhost", 57109, 57110, Endian.LITTLE_ENDIAN);
		
		server.addOSCListener("/finger", new OSCListener() {

			/* (non-Javadoc)
			 * @see org.squidy.manager.protocol.osc.OSCListener#handleMessage(com.illposed.osc.OSCMessage)
			 */
			public void handleMessages(OSCMessage[] messages) {
				System.out.println(messages);
				for (OSCMessage message : messages) {
					for (Object o : message.getArguments()) {
						System.out.println(o.getClass().getName() + ": " + o);
					}
				}
			}
		});
		
//		try {
//			OSCPortOut out = new OSCPortOut(InetAddress.getByName("localhost"), 57110);
//			
//			System.out.println("Send message");
//			
//			OSCPacket packet = new OSCMessage("/finger", new Object[]{"Affe", "Banane", "Brot"});
//			
//			try {
//				out.send(packet);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} catch (SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
