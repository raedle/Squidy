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

package org.squidy.nodes.tracking.configclient.service.comm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.nodes.tracking.config.xml.Configuration;
import org.squidy.nodes.tracking.configclient.service.Service;
import org.squidy.nodes.tracking.configclient.service.ServiceRegistry;
import org.squidy.nodes.tracking.configclient.service.javaprop.JavaPropService;


public class TcpIpCommService extends CommService {

	// Logger to log info, error, debug,... messages.
	private static Log LOG = LogFactory.getLog(TcpIpCommService.class);

	private static final String PROTOCOL_ELEM = "protocol";
	private static final String PROTOCOL_COMMENT = "config";

	// services
	private JavaPropService pService = (JavaPropService) ServiceRegistry
			.getInstance().getService(JavaPropService.class);

	// stuff for debugging
	private boolean debug = false;
	private static final String DEBUGFILE_IN = "debugfile_in.xml";
	private static final String DEBUGFILE_OUT = "debugfile_out.xml";
	private FileOutputStream debugFos_in;
	private FileOutputStream debugFos_out;

	// connection
	private InetSocketAddress address;
	private Socket server;

	// xml processor
	private XMLStreamWriter xsw;
	private XMLEventReader xer;

	// jaxb
	private JAXBContext context;
	private Unmarshaller unmarshaller;
	private Marshaller marshaller;

	public TcpIpCommService(InetSocketAddress s) {
		this.address = s;
	}

	@Override
	protected void startupImpl() throws CommException {
		Object property = pService.getProperties().getProperty(
				JavaPropService.DEBUG_COMM);
		if (property != null) {
			debug = Boolean.parseBoolean((String) property);
		}
		if (debug)
			initDebugOutput();

		// init connection
		LOG.info("Creating connection: " + address);
		try {
			server = new Socket(address.getAddress(), address.getPort());
			LOG.info("Connection accepted");
			newConnection(server.getInputStream(), server.getOutputStream());
		} catch (ConnectException e) {
			String msg = "Connection to " + address + " refused.";
			LOG.error(msg);
			throw new CommException(msg);
		} catch (UnknownHostException e) {
			String msg = "Unknown host: " + address;
			LOG.error(msg);
			throw new CommException(msg);
		} catch (IOException e) {
			String msg = "Cannot read from: " + address;
			LOG.error(msg);
			throw new CommException(msg);
		}

		// init jaxb
		try {
			context = JAXBContext
					.newInstance(Configuration.class.getPackage().getName());
			unmarshaller = context.createUnmarshaller();
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		} catch (JAXBException e) {
			LOG.error(e.getMessage(), e);
			throw new CommException(CommService.INTERNAL_ERROR_MSG);
		}
	}

	private void initDebugOutput() {
		LOG.info("Creating debug files: " + DEBUGFILE_IN + " " + DEBUGFILE_OUT);
		try {
			debugFos_in = new FileOutputStream(DEBUGFILE_IN);
			debugFos_out = new FileOutputStream(DEBUGFILE_OUT);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	private void newConnection(InputStream in, OutputStream out)
			throws CommException {
		try {

			xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
			xsw.writeStartDocument();
			xsw.writeStartElement(PROTOCOL_ELEM);
			xsw.writeComment(PROTOCOL_COMMENT); // arbitrary
			xsw.flush();

			xer = XMLInputFactory.newInstance().createXMLEventReader(in);
			xer.nextEvent(); // startDocument
			xer.nextTag(); // protocol element TODO: protocol check
			xer.nextEvent(); // arbitrary comment

		} catch (Exception e) {
			// XMLStreamException
			// FactoryConfigurationError
			String msg = "Unable to initialize protocol.";
			LOG.error(msg, e);
			throw new CommException(msg);
		}
	}

	@Override
	protected Configuration loadConfigImpl() throws CommException {
		assert (server != null);
		LOG.info("Receiving config");
		
		Configuration config;
		try {
			config = (Configuration) unmarshaller.unmarshal(xer);
			if (debug) {
				marshaller.marshal(config, System.out);
			}
		} catch (Exception e) {
			String msg = "Unable to load configuration (JAXB).";
			LOG.error(msg, e);
			throw new CommException(msg);
		}
		return config;
	}

	@Override
	protected Configuration saveConfigImpl(Configuration config)
			throws CommException {
		assert (server != null);
		LOG.info("Sending config");
		try {
			// send
			if (debug)
				marshaller.marshal(config, debugFos_out);
			marshaller.marshal(config, xsw);
			xsw.flush();

			// receive
			config = (Configuration) unmarshaller.unmarshal(xer);
			if (debug)
				marshaller.marshal(config, debugFos_in);

		} catch (JAXBException e) {
			String msg = "Unable to save configuration (JAXB).";
			LOG.error(msg, e);
			throw new CommException(msg);
		} catch (XMLStreamException e) {
			String msg = "Unable to save configuration (XML).";
			LOG.error(msg, e);
			throw new CommException(msg);
		}
		return config;
	}

	@Override
	protected void shutdownImpl() {
		LOG.info("Closing connection.");
		if (xsw != null) {
			try {
				xsw.writeEndDocument();
				xsw.close();
			} catch (XMLStreamException ignore) {
			}
		}
		if (xer != null) {
			try {
				xer.close();
			} catch (XMLStreamException ignore) {
			}
			;
		}
		if (server != null) {
			try {
				server.close();
			} catch (IOException ignore) {
			}
			;
		}
		if (debug) {
			if (debugFos_in != null) {
				try {
					debugFos_in.close();
				} catch (IOException ignore) {
				}
				;
			}
			if (debugFos_out != null) {
				try {
					debugFos_out.close();
				} catch (IOException ignore) {
				}
				;
			}
		}
	}

	@Override
	public Class<? extends Service> getServiceType() {
		return CommService.class;
	}

}
