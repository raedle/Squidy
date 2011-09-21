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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
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
 * <code>iPhone</code>.
 *
 * <pre>
 * Date: May 25, 2008
 * Time: 4:54:02 PM
 * </pre>
 *
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: iPhone.java 772 2011-09-16 15:39:44Z raedle $ * @since 1.0.0 * @since
 *          1.0.0
 * @since 1.0
 */
@XmlType(name = "Apple iPhone / iPod Touch")
@Processor(
	types = { Processor.Type.INPUT, Processor.Type.OUTPUT },
	name = "iPhone",
	icon = "/org/squidy/nodes/image/48x48/iphone.png",
	description = "/org/squidy/nodes/html/iPhone.html",
	tags = { "Apple", "iPhone", "Smartphone", "touch", "multi-touch", "multitouch" }
)
public class iPhone extends AbstractNode implements RegisterListener {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(iPhone.class);

	// Data constants sent from Squidy reference implementation on the Apple
	// iPhone.
	public static final DataConstant TOUCHES_BEGAN = DataConstant.get(
			Boolean.class, "TOUCHES_BEGAN");
	public static final DataConstant TOUCHES_MOVED = DataConstant.get(
			Boolean.class, "TOUCHES_MOVED");
	public static final DataConstant TOUCHES_ENDED = DataConstant.get(
			Boolean.class, "TOUCHES_ENDED");
	public static final DataConstant TOUCHES_CANCELLED = DataConstant.get(
			Boolean.class, "TOUCHES_CANCELLED");
	public static final DataConstant TAP_COUNT = DataConstant.get(
			Integer.class, "TAP_COUNT");

	public static final DataConstant HEADING_X = DataConstant.get(String.class, "HEADING_X");
	public static final DataConstant HEADING_Y = DataConstant.get(String.class, "HEADING_Y");
	public static final DataConstant HEADING_Z = DataConstant.get(String.class, "HEADING_Z");
	public static final DataConstant HEADING_MAGNETIC = DataConstant.get(String.class, "MAGNETIC_HEADING");

	public static final String SERVICE_TYPE_UDP = "_squidy-client-app._udp.";
	// public static final String SERVICE_TYPE_TCP = "_squidy-client-app._tcp";

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################

	@XmlAttribute(name = "service-name")
	@Property(name = "Service name", description = "Service name under which an iPhone can connect to the current iPhone node.")
	@TextField
	private String serviceName = "Squidy iPhone";

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
	private int port = 1919;

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
	@Property(name = "Should release buttons", group = "Options", description = "Whether the iPhone node should release buttons or not.")
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
	// END OF PROPERTIES
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
//	private JmDNS jmdns;
//	private ServiceInfo info;

	// The OSC server to receive iPhone data.
	private OSCServer oscServer;

	private ServerSocket tcpServer;
	private Map<Socket, DataOutputStream> outputStreams = new ConcurrentHashMap<Socket, DataOutputStream>();

//	public iPhone() {
//		try {
//			jmdns = JmDNS.create();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {
		// Registering dns sd service.
		startDNSSDService();

		startOSCServer();
		startTCPServer();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		stopTCPServer();
		stopOSCServer();

		// Stopping dns sd service.
		stopDNSSDService();
	}

	/**
	 *
	 */
	private void startDNSSDService() {

//		try {
//			jmdns = JmDNS.create();
//			info = ServiceInfo.create(SERVICE_TYPE_UDP, serviceName, port, "");
//			jmdns.registerService(info);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

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

//		jmdns.unregisterService(info);

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

		oscServer.addOSCListener(OSCServer.STANDARD_SQUIDY_OSC_ADDRESS, new DataObjectReceiver());
		oscServer.addOSCListener("/squidy/remote", new DataObjectReceiverLegacy());

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

	/**
	 *
	 */
	private void startTCPServer() {
		new Thread() {
			@Override
			public void run() {
				try {
					tcpServer = new ServerSocket(port + 1);

					while (isProcessing()) {
						Socket client = tcpServer.accept();
						client.setTcpNoDelay(true);

						outputStreams.put(client, new DataOutputStream(client
								.getOutputStream()));
					}
				} catch (IOException e) {
					// publishFailure(e);
				}
			}
		}.start();
	}

	/**
	 *
	 */
	private void stopTCPServer() {
		if (tcpServer != null) {
			try {
				for (DataOutputStream outputStream : outputStreams.values()) {
					outputStream.close();
				}

				for (Socket client : outputStreams.keySet()) {
					client.close();
				}
				outputStreams.clear();

				tcpServer.close();
			} catch (IOException e) {
				publishFailure(e);
			}
		}
	}

	private boolean paletteVisible = false;

	/**
	 * @param dataButton
	 * @return
	 */
	public IData process(DataButton dataButton) {

		if (dataButton.getFlag()) {
			try {
				// if (!paletteVisible) {
				showButton(20, 20, 0, ImageIO.read(iPhone.class
						.getResource("/mouse.png")));
				showButton(80, 20, 1, ImageIO.read(iPhone.class
						.getResource("/pen_red.png")));
				showButton(140, 20, 2, ImageIO.read(iPhone.class
						.getResource("/pen_blue.png")));
				// paletteVisible = true;
				// }
				// else {
				// paletteVisible = false;
				// }
			} catch (IOException e) {
				publishFailure(e);
			}
		}
		return dataButton;
	}

	/**
	 * @param image
	 */
	public void showButton(int x, int y, int actionType, BufferedImage image) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);

			byte[] bytes = baos.toByteArray();

			System.out.println("BYTES TO READ: " + bytes.length);

			for (Socket client : outputStreams.keySet()) {

				try {
					DataOutputStream outputStream = outputStreams.get(client);
					outputStream.writeInt(x);
					outputStream.writeInt(y);
					outputStream.writeInt(actionType);
					outputStream.writeInt(bytes.length);
					outputStream.write(bytes);
					outputStream.flush();
				} catch (SocketException e) {
					outputStreams.remove(client);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			publishFailure(e);
		}
	}

	/**
	 * <code>DataObjectReceiver</code>.
	 *
	 * <pre>
	 * Date: Dec 14, 2009
	 * Time: 7:58:30 PM
	 * </pre>
	 *
	 *
	 * @author
	 * Roman RŠdle
	 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 *
	 * @version $Id: iPhone.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	class DataObjectReceiver implements OSCListener {

		/*
		 * (non-Javadoc)
		 *
		 * @see org.squidy.manager.protocol.osc.OSCListener#handleMessages
		 * (com.illposed.osc.OSCMessage[])
		 */
		public void handleMessages(OSCMessage[] messages) {

			List<IData> datas = new ArrayList<IData>();
			for (OSCMessage message : messages) {

				Object[] arguments = message.getArguments();

				IData data = ReflectionUtil.createInstance((String) arguments[0]);
				Object[] rawData = new Object[arguments.length - 1];
				System.arraycopy(arguments, 1, rawData, 0, rawData.length);

				try {
					data.deserialize(rawData);
				} catch (Exception e) {
					if (LOG.isErrorEnabled()) {
						LOG
								.error("Could not deserialize data object of type "
										+ data.getClass().getName()
										+ " [cause="
										+ e.getMessage() + "]");
					}
				}

				if (data instanceof DataPosition2D) {
					long timestamp = System.currentTimeMillis();

					data.setTimestamp(timestamp);

//					if (releaseButtonOnSingleTouch) {
//						if (messages.length == 1) {
//							int tapCount = (Integer) data.getAttribute(TAP_COUNT);
//							for (int i = 0; i < tapCount; i++) {
//
//								if (data.hasAttribute(TOUCHES_ENDED)) {
//									datas.add(new DataButton(iPhone.class,
//											DataButton.BUTTON_1, true));
//									datas.add(new DataButton(iPhone.class,
//											DataButton.BUTTON_1, false));
//								}
//							}
//						}
//					}
				}

				datas.add(data);
			}

			// Publish received data.
			publish(datas);
		}
	}

	@Deprecated
	class DataObjectReceiverLegacy implements OSCListener {

		/*
		 * (non-Javadoc)
		 *
		 * @see org.squidy.manager.protocol.osc.OSCListener#handleMessages
		 * (com.illposed.osc.OSCMessage[])
		 */
		public void handleMessages(OSCMessage[] messages) {

			List<IData> datas = new ArrayList<IData>(); 
			for (OSCMessage message : messages) {

				Object[] arguments = message.getArguments();

				IData data = ReflectionUtil.createInstance((String) arguments[0]);
				Object[] rawData = new Object[arguments.length - 2];
				System.arraycopy(arguments, 1, rawData, 0, 1);
				System.arraycopy(arguments, 3, rawData, 1, rawData.length - 1);

				try {
					data.deserialize(rawData);
				} catch (Exception e) {
					if (LOG.isErrorEnabled()) {
						LOG
								.error("Could not deserialize data object of type "
										+ data.getClass().getName()
										+ " [cause="
										+ e.getMessage() + "]");
					}
				}

				if (data instanceof DataPosition2D) {
					long timestamp = System.currentTimeMillis();

					data.setTimestamp(timestamp);

					if (releaseButtonOnSingleTouch) {
						if (messages.length == 1) {
							if (data.hasAttribute(TAP_COUNT))
							{
							int tapCount = (Integer) data.getAttribute(TAP_COUNT);
							for (int i = 0; i < tapCount; i++) {

								if (data.hasAttribute(TOUCHES_ENDED)) {
									datas.add(new DataButton(iPhone.class,
											DataButton.BUTTON_1, true));
									datas.add(new DataButton(iPhone.class,
											DataButton.BUTTON_1, false));
								}
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
	}
}
