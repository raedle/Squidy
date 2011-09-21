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

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.SquidyException;
import org.squidy.common.util.ReflectionUtil;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.protocol.osc.OSCListener;
import org.squidy.manager.protocol.osc.OSCServer;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.RegisterListener;
import com.illposed.osc.Endian;
import com.illposed.osc.OSCMessage;


/**
 * <code>Android</code>.
 * 
 * <pre>
 * Date: December 14, 2009
 * Time: 1:34:12 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University of Konstanz
 *         
 * @version $Id: Android.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "Android")
@Processor(
	name = "Android",
	types = { Processor.Type.INPUT, Processor.Type.OUTPUT },
	icon = "/org/squidy/nodes/image/48x48/android.png",
	description = "/org/squidy/nodes/html/Android.html",
	tags = { "Motorola", "Droid", "Android", "Smartphone", "touch", "multi-touch", "multitouch" }
)
public class Android extends AbstractNode implements RegisterListener {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Android.class);

	// Data constants sent from Squidy reference implementation on the Android.
	public static final DataConstant TOUCHES_BEGAN = DataConstant.get(String.class, "TOUCHES_BEGAN");
	public static final DataConstant TOUCHES_MOVED = DataConstant.get(String.class, "TOUCHES_MOVED");
	public static final DataConstant TOUCHES_ENDED = DataConstant.get(String.class, "TOUCHES_ENDED");
	public static final DataConstant TOUCHES_CANCELLED = DataConstant.get(String.class, "TOUCHES_CANCELLED");
	public static final DataConstant TAP_COUNT = DataConstant.get(Integer.class, "TAP_COUNT");

	public static final String SERVICE_TYPE_UDP = "_squidy-client-app._udp";

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "service-name")
	@Property(
		name = "Service name",
		description = "Service name under which an Android can connect to the current Android node."
	)
	@TextField
	private String serviceName = "Squidy Android";

	/**
	 * @return the serviceName
	 */
	public final String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName
	 *            the serviceName to set
	 */
	public final void setServiceName(String serviceName) {
		this.serviceName = serviceName;

		if (isProcessing()) {
			stopDNSSDService();
			startDNSSDService();
		}
	}

	@XmlAttribute(name = "port")
	@Property(name = "Port", group = "Connection Settings")
	@TextField
	private int port = 2020;

	/**
	 * @return the port
	 */
	public final int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public final void setPort(int port) {
		this.port = port;

		if (isProcessing()) {
			stopOSCServer();
			startOSCServer();
		}
	}

	@XmlAttribute(name = "should-release-buttons")
	@Property(name = "Should release buttons", group = "Options", description = "Whether the Android node should release buttons or not.")
	@CheckBox
	private boolean releaseButtonOnSingleTouch = true;

	/**
	 * @return the releaseButtonOnSingleTouch
	 */
	public final boolean isReleaseButtonOnSingleTouch() {
		return releaseButtonOnSingleTouch;
	}

	/**
	 * @param releaseButtonOnSingleTouch
	 *            the releaseButtonOnSingleTouch to set
	 */
	public final void setReleaseButtonOnSingleTouch(
			boolean releaseButtonOnSingleTouch) {
		this.releaseButtonOnSingleTouch = releaseButtonOnSingleTouch;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	// ################################################################################
	// BEGIN OF DNS SD REGISTER LISTENER
	// ################################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.apple.dnssd.RegisterListener#serviceRegistered(com.apple.dnssd.
	 * DNSSDRegistration, int, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	public void serviceRegistered(DNSSDRegistration dnssdRegistration,
			int flags, String serviceName, String regType, String domain) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Registered dns sd service " + SERVICE_TYPE_UDP
					+ " [flags=" + flags + ",serviceName=" + serviceName
					+ ",regType=" + regType + ",domain=" + domain + "]");
		}

		this.dnssdService = dnssdRegistration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.apple.dnssd.BaseListener#operationFailed(com.apple.dnssd.DNSSDService
	 * , int)
	 */
	public void operationFailed(DNSSDService dnssdService, int errorCode) {
		publishFailure(new SquidyException("Registration of dns sd service "
				+ SERVICE_TYPE_UDP + " failed [error code=" + errorCode + "]"));

		this.dnssdService = dnssdService;

		// Stopping dns sd service.
		stopDNSSDService();
	}

	// ################################################################################
	// END OF DNS SD REGISTER LISTENER
	// ################################################################################

	private DNSSDService dnssdService;

	// The OSC server to receive Android data.
	private OSCServer oscServer;

	private ServerSocket tcpServer;
	private Map<Socket, DataOutputStream> outputStreams = new ConcurrentHashMap<Socket, DataOutputStream>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {
		// Registering dns sd service.
		startDNSSDService();

		startOSCServer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		stopOSCServer();

		// Stopping dns sd service.
		stopDNSSDService();
	}

	/**
	 * 
	 */
	private void startDNSSDService() {
		try {
			dnssdService = DNSSD.register(serviceName, SERVICE_TYPE_UDP, port,
					this);
		} catch (DNSSDException e) {
			// TODO [RR]: Uncomment if Bonjour has to be installed.
//			publishFailure(e);
			
			if (LOG.isErrorEnabled()) {
				LOG.error(e);
			}
		} catch (Error e) {
			if (LOG.isWarnEnabled()) {
				LOG.warn(e);
			}
		}
	}

	/**
	 * 
	 */
	private void stopDNSSDService() {
		if (dnssdService != null) {
			dnssdService.stop();
			dnssdService = null;

			if (LOG.isDebugEnabled()) {
				LOG.debug("Stopped dns sd service " + SERVICE_TYPE_UDP);
			}
		}
	}

	/**
	 * 
	 */
	private void startOSCServer() {
		oscServer = new OSCServer(port, Endian.LITTLE_ENDIAN);

		oscServer.addOSCListener("/squidy/bridge/osc", new OSCListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.manager.protocol.osc.OSCListener#handleMessages
			 * (com.illposed.osc.OSCMessage[])
			 */
			public void handleMessages(OSCMessage[] messages) {

				List<IData> datas = new ArrayList<IData>();
				for (OSCMessage message : messages) {

					Object[] arguments = message.getArguments();

					IData data = ReflectionUtil
							.createInstance((String) arguments[0]);

					Object[] rawData = new Object[arguments.length - 1];
					System.arraycopy(arguments, 1, rawData, 0, rawData.length);

					data.deserialize(rawData);

					if (data instanceof DataPosition2D) {
						long timestamp = System.currentTimeMillis();

						data.setTimestamp(timestamp);

						if (releaseButtonOnSingleTouch && data.hasAttribute(TAP_COUNT)) {
							if (messages.length == 1) {
								int tapCount = (Integer) data.getAttribute(TAP_COUNT);
								for (int i = 0; i < tapCount; i++) {

									if (data.hasAttribute(TOUCHES_ENDED)) {
										datas.add(new DataButton(Android.class,
												DataButton.BUTTON_1, true));
										datas.add(new DataButton(Android.class,
												DataButton.BUTTON_1, false));
									}
								}
							}
						}
					}

					datas.add(data);
				}

				// Publish received data.
				publish(datas);
			}
		});
		oscServer.startListening();
	}

	/**
	 * 
	 */
	private void stopOSCServer() {
		if (oscServer != null) {
			oscServer.stopListening();
			oscServer.close();
			oscServer = null;
		}
	}
}
