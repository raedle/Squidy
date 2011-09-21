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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.common.util.ReflectionUtil;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.domainprovider.impl.EndianDomainProvider;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.protocol.osc.OSCListener;
import org.squidy.manager.protocol.osc.OSCServer;
import org.squidy.nodes.optitrack.utils.TrackingConstant;
import org.squidy.nodes.optitrack.utils.TrackingUtility;

import com.illposed.osc.Endian;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;


/**
 * <code>SquidyRemote</code>.
 *
 * <pre>
 * Date: Nov 29, 2008
 * Time: 7:25:32 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: SquidyRemote.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
@XmlType(name = "Squidy Remote")
@Processor(
	name = "Squidy Remote",
	icon = "/org/squidy/nodes/image/48x48/squidy-remote.png",
	description = "/org/squidy/nodes/html/SquidyRemote.html",
	types = { Processor.Type.INPUT, Processor.Type.OUTPUT },
	tags = { "squidy", "remote", "distributed", "computing", "multi", "OSC", "network" }
)
public class SquidyRemote extends AbstractNode {

	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(SquidyRemote.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "host")
	@Property(
		name = "Host",
		description = "The host which should receive Squidy Remote data.",
		group = "Connection Settings"
	)
	@TextField
	private String host = "127.0.0.1";
	
	/**
	 * @return the host
	 */
	public final String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public final void setHost(String host) {
		this.host = host;
		
		if (serverMap != null && host != "") {
			stopOSCServer();
			startOSCServer(host, port);
		}
	}
	
	// ################################################################################

	@XmlAttribute(name = "port")
	@Property(
		name = "Port",
		description = "The port which should receive Squidy Remote data.",
		group = "Connection Settings"
	)
	@TextField
	private int port = 1919;

	/**
	 * @return the port
	 */
	public final int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public final void setPort(int port) {
		this.port = port;
		
		if (serverMap != null) {
			stopOSCServer();
			startOSCServer(host, port);
		}
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "receiver")
	@Property(
		name = "Receiver",
		description = "Whether the Squidy Remote acts as a receiver or not.",
		group = "Options"
	)
	@CheckBox
	public boolean receiver;
	
	/**
	 * @return the receiver
	 */
	public final boolean isReceiver() {
		return receiver;
	}

	/**
	 * @param receiver the receiver to set
	 */
	public final void setReceiver(boolean receiver) {
		this.receiver = receiver;
		
		if (serverMap != null && host != "") {
			OSCServer server = serverMap.get(host);
			if (server  != null)
			{
				if (receiver && !server.isListening()) {
					server.startListening();
				}
				else if (!receiver && server.isListening()) {
					server.stopListening();
				}
			}
		}
	}
	
	// ################################################################################

	@XmlAttribute(name = "sender")
	@Property(
		name = "Sender",
		description = "Whether the Squidy Remote acts as a sender or not.",
		group = "Options"
	)
	@CheckBox
	public boolean sender = true;

	/**
	 * @return the sender
	 */
	public final boolean isSender() {
		return sender;
	}

	/**
	 * @param sender the sender to set
	 */
	public final void setSender(boolean sender) {
		this.sender = sender;
	}
	
	@XmlAttribute(name = "endian")
	@Property(
		name = "Endian",
		description = "Indicates which endian strategy will be used to identify bytes or not.",
		group = "Options"
	)
	@ComboBox(
		domainProvider = EndianDomainProvider.class
	)
	private Endian endian = Endian.LITTLE_ENDIAN;

	/**
	 * @return the endian
	 */
	public final Endian getEndian() {
		return endian;
	}
	
	/**
	 * @param endian
	 *            the endian to set
	 */
	public final void setEndian(Endian endian) {
		this.endian = endian;

		if (isProcessing() && host != "") {
			// Restart the osc server with new endian strategy.
			stopOSCServer();
			startOSCServer(host, port);
		}
	}
	
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	//private OSCServer server;
	private HashMap<String, OSCServer> serverMap;
	private ArrayList<String> hostList;
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {
		serverMap = new HashMap<String, OSCServer>();
		hostList = new ArrayList<String>();
		startOSCServer(host, port);
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
			stopOSCServer();
	}
	
	/**
	 * 
	 */
	private OSCServer startOSCServer(String variableHost, int variablePort) {
		OSCServer server = serverMap.get(host);
		if (server == null && variableHost != "" && variablePort > 0)
		{
			server = new OSCServer(variableHost, variablePort, variablePort, endian);
			serverMap.put(variableHost, server);
			hostList.add(variableHost);
			server.addOSCListener("/squidy/remote", new OSCListener() {
	
				/* (non-Javadoc)
				 * @see org.squidy.manager.protocol.osc.OSCListener#handleMessages(com.illposed.osc.OSCMessage[])
				 */
				public void handleMessages(OSCMessage[] messages) {
	
					List<IData> datas = new ArrayList<IData>();
					for (OSCMessage message : messages) {
						
						Object[] arguments = message.getArguments();
						
						IData data = ReflectionUtil.createInstance((String) arguments[0]);
						
						Object[] rawData = new Object[arguments.length - 1];
						System.arraycopy(arguments, 1, rawData, 0, rawData.length);
						
						data.deserialize(rawData);
						
						datas.add(data);
					}
					
					// Publish received data.
					publish(datas);
				}
			});
			
			if (receiver) {
				server.startListening();
			}
		}
		return server;
	}
	
	/**
	 * 
	 */
	private void stopOSCServer() {
		for (String iHost : hostList)
		{
			OSCServer server = serverMap.get(iHost);
			if (server != null) {
				server.close();
				server = null;			
			}
			serverMap.remove(iHost);
		}
		hostList.clear();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.logic.ReflectionProcessable#beforeDataContainerProcessing(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer) {

		if (sender) {
			OSCBundle bundle = new OSCBundle(new Date(dataContainer.getTimestamp()));
			OSCServer server = null;
			int i = 0;
			for (IData data : dataContainer.getData()) {
				i++;
				
				String type = data.getClass().getName();
				Object[] serializedData = data.serialize();

				OSCMessage dataMessage = new OSCMessage("/squidy/remote");
				dataMessage.addArgument(type);

				for (Object o : serializedData) {
					dataMessage.addArgument(o);
				}

				bundle.addPacket(dataMessage);
				
				String remoteHost = TrackingUtility.getAttributesAlpha(data, TrackingConstant.REMOTEHOST);
				if(remoteHost != "")
				{
					server = serverMap.get(remoteHost);
					if (server == null)
					{
						server = startOSCServer(remoteHost, TrackingUtility.getAttributesInteger(data, TrackingConstant.REMOTEPORT));
					}
				} 
				else if (this.host != "")
				{
					server = serverMap.get(this.host);
				}
				if (server != null)
				{
					if (i == 2) {
						server.send(bundle);
						bundle = new OSCBundle(new Date(dataContainer.getTimestamp()));
						i = 0;
					}
				}
			}
			if (server != null)
				server.send(bundle);
		}
		return super.preProcess(dataContainer);
	}
}

