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

package org.squidy.nodes.laserpointer.config;

import java.io.IOException;
import java.net.Socket;

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
import org.squidy.nodes.laserpointer.config.xml.Configuration;


/**
 * <code>ConfigConn</code>.
 *
 * <pre>
 * Date: Jun 26, 2008
 * Time: 12:37:42 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ConfigConnection.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0
 */
public class ConfigConnection extends Thread {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ConfigConnection.class);

	private Socket socket;
	private ConfigManager configManager;

	private boolean running = true;

	private static final String PROTOCOL_ELEM = "protocol";

	// Identifier of the connection.
	private String identifier = "";

	/**
	 * @return the identifier
	 */
	public final String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public final void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	private boolean connectionToConfigClient = false;

	/**
	 * @return the configClient
	 */
	public final boolean isConnectionToConfigClient() {
		return connectionToConfigClient;
	}

	/**
	 * @param connectionToConfigClient the configClient to set
	 */
	public final void setConnectionToConfigClient(boolean connectionToConfigClient) {
		this.connectionToConfigClient = connectionToConfigClient;
	}

	private XMLEventReader xmlEventReader;
	private XMLStreamWriter xmlStreamWriter;

	private Unmarshaller unmarshaller;
	private Marshaller marshaller;

	public ConfigConnection(ConfigManager configManager, Socket socket) {
		this.configManager = configManager;
		this.socket = socket;

		try {
			unmarshaller = ConfigManager.getJAXBContext().createUnmarshaller();
			marshaller = ConfigManager.getJAXBContext().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			
			start();
		}
		catch (JAXBException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Couldn't initiate JAXB: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * @param configuration
	 * @return
	 * @throws JAXBException
	 * @throws XMLStreamException
	 */
	public synchronized Configuration updateCamera(Configuration configuration) throws JAXBException, XMLStreamException {
		if (xmlStreamWriter != null && xmlEventReader != null && running) {
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Send update to camera camera #" + identifier);
			}
			
			// Send update to camera.
			marshaller.marshal(configuration, xmlStreamWriter);
			xmlStreamWriter.flush();
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Get update from camera #" + identifier);
			}
			
			// Get camera's update back.
			configuration = (Configuration) unmarshaller.unmarshal(xmlEventReader);
		}

		return configuration;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(socket.getInputStream());
			xmlEventReader.nextEvent(); // startDocument
			xmlEventReader.nextTag(); // protocol element
			identifier = "" + xmlEventReader.nextEvent();
			identifier = identifier.substring(4, identifier.length() - 3);
			connectionToConfigClient = identifier.startsWith("config");
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Connection from camera #" + identifier);
			}
			configManager.attachConnection(this);

			xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(socket.getOutputStream());
			xmlStreamWriter.writeStartDocument();
			xmlStreamWriter.writeStartElement(PROTOCOL_ELEM);
			xmlStreamWriter.writeComment("skipme");
			xmlStreamWriter.flush();

			// write xml for the first time
			marshaller.marshal(configManager.getConfig(), xmlStreamWriter);
			xmlStreamWriter.flush();

			if (connectionToConfigClient) {
				while (running && xmlEventReader.peek().isStartElement()) {
					
					if (LOG.isDebugEnabled()) {
						LOG.debug("Receiving configuration from config client.");
					}
					
					// Reading configuration from config client.
					Configuration configuration = (Configuration) unmarshaller.unmarshal(xmlEventReader);
					
					// Send update to cameras.
					configManager.updateConfig(this, configuration);

					if (LOG.isDebugEnabled()) {
						LOG.debug("Writing result back to config client.");
					}
					
					// Write result back to config client.
					marshaller.marshal(configManager.getConfig(), xmlStreamWriter);
					xmlStreamWriter.flush();
				}
				close();
			}
		}
		catch (Exception e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			close();
		}
	}

	/**
	 * 
	 */
	public void close() {
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Close config connection.");
		}
		
		running = false;
		interrupt();
		
		try {
			if (xmlEventReader != null) {
				xmlEventReader.close();
			}
		}
		catch (XMLStreamException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		try {
			if (xmlStreamWriter != null) {
				xmlStreamWriter.close();
			}
		}
		catch (XMLStreamException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		try {
			configManager.detach(this);
			socket.close();
		}
		catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

}
