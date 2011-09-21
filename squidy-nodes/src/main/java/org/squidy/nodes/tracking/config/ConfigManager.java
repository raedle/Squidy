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

package org.squidy.nodes.tracking.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.commander.ControlClient;
import org.squidy.manager.commander.command.impl.WhiteScreen;
import org.squidy.manager.commander.command.utility.Switch;
import org.squidy.manager.util.CommanderUtils;
import org.squidy.nodes.ir.ConfigManagable;
import org.squidy.nodes.plugin.PatternScreen;
import org.squidy.nodes.tracking.config.xml.Camera;
import org.squidy.nodes.tracking.config.xml.Configuration;
import org.squidy.nodes.tracking.config.xml.Display;
import org.squidy.nodes.tracking.config.xml.Property;


/**
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * 
 */
public class ConfigManager {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ConfigManager.class);

	private static final String CONFIG_XML = "laserConfig.xml";

	private Configuration configuration;

	private static JAXBContext context;
	
	/**
	 * Returns the JAXB context and if it is not initialized already it will be initialized
	 * once.
	 * 
	 * @return The JAXB context.
	 * @throws JAXBException
	 */
	public static JAXBContext getJAXBContext() throws JAXBException {
		if (context == null) {
			context = JAXBContext.newInstance(Configuration.class.getPackage().getName());
		}
		return context;
	}

	private ConfigServer conigServer;

	private List<ConfigConnection> cameraConnections = new ArrayList<ConfigConnection>();

	private List<ConfigConnection> configClientConnections = new ArrayList<ConfigConnection>();

	private Camera calibCam = null;

	private ConfigManagable configManagable;

	public ConfigManager(ConfigManagable configManagable, int port) {
		this.configManagable = configManagable;
		try {
			loadConfiguration();
		}
		catch (Exception e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			return;
		}
		conigServer = new ConfigServer(this, port);
	}

	/**
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	private synchronized void loadConfiguration() throws JAXBException, FileNotFoundException {
		
		// Set configuration as empty.
		configuration = null;
		
		File configurationFile = new File(CONFIG_XML);
		if (!configurationFile.exists()) {
			throw new FileNotFoundException("Couldn't find laserConfig.xml at " + configurationFile.getAbsolutePath());
		}
		
		Unmarshaller u = getJAXBContext().createUnmarshaller();
		configuration = (Configuration) u.unmarshal(configurationFile);
	}

	/**
	 * @param configuration
	 */
	private synchronized void saveConfiguration(Configuration configuration) {
		if (configuration != null) {
			this.configuration = configuration;
			configManagable.notifyUpdateConfig();

			if (configManagable.isConfigXmlReadOnly()) {
				return;
			}

			File configXmlFile = new File(CONFIG_XML);
			try {
				configXmlFile.renameTo(new File(CONFIG_XML + ".bak"));
			}
			catch (Exception e) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Couldn't create a backup of " + configXmlFile);
				}
			}

			saveConfiguration(configuration, configXmlFile);
		}
	}

	/**
	 * @param configuration
	 * @param configXmlFile
	 */
	public synchronized void saveConfiguration(Configuration configuration, File configXmlFile) {
		if (configuration != null) {
			try {
				Marshaller marshaller = getJAXBContext().createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(configuration, new FileOutputStream(configXmlFile));
				
				if (LOG.isDebugEnabled()) {
					LOG.debug("Saved configuration to " + configXmlFile);
				}
			}
			catch (Exception e) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Couldn't save configuration to " + configXmlFile);
				}
			}
		}
	}

	public void close() {
		if (conigServer != null) {
			conigServer.close();
		}
	}

	public Configuration getConfig() {
		return configuration;
	}

	public Camera getCalibrationCamera() {
		return calibCam;
	}

	public synchronized void attachConnection(ConfigConnection connection) {
		if (connection.isConnectionToConfigClient()) {
			for (ConfigConnection configClientConnection : configClientConnections) {
				if (configClientConnection != null && configClientConnection.getIdentifier().equals(connection.getIdentifier())) {
					configClientConnections.remove(configClientConnection);
					break;
				}
			}
			configClientConnections.add(connection);

			if (LOG.isInfoEnabled()) {
				LOG.info("Laserpointer client " + connection.getIdentifier() + " attached.");
			}
		}
		else {
			// if camera is already inside
			for (ConfigConnection cameraConnection : cameraConnections) {
				if (cameraConnection != null && cameraConnection.getIdentifier().equals(connection.getIdentifier())) {
					cameraConnections.remove(cameraConnection);
					break;
				}
			}
			cameraConnections.add(connection);
			for (Camera cam : configuration.getCameras()) {
				if (connection != null && connection.getIdentifier().equals(cam.getId())) {
					cam.setOnline(true);
				}
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("Laserpointer camera #" + connection.getIdentifier() + " attached (" + cameraConnections.size()
						+ " cameras connected)");
			}
		}
	}

	/**
	 * @param configConnection
	 */
	public synchronized void detach(ConfigConnection configConnection) {
		for (Camera camera : configuration.getCameras()) {
			if (configConnection != null && configConnection.getIdentifier().equals(camera.getId())) {
				camera.setOnline(false);
			}
		}
		cameraConnections.remove(configConnection);
		configClientConnections.remove(configConnection);

		if (LOG.isInfoEnabled()) {
			LOG.info("Laserpointer " + configConnection.getIdentifier() + " detached.");
		}
	}

	/**
	 * @param connection
	 * @param configuration
	 */
	public synchronized void updateConfig(ConfigConnection connection, Configuration configuration) {
		
		String host = configManagable.getRemoteAddress();
		int port = configManagable.getRemotePort();
		ControlClient client = CommanderUtils.getCommanderClient(host, port);
		
		if (connection.isConnectionToConfigClient()) {

			for (Display display : configuration.getDisplays()) {
				if (display.getMsg() != null && display.getMsg().length() > 0) {
					display.setMsg(null);
					Collection<Property> properties = display.getProperties();
					for (Property property : properties) {
						property.setMsg(null);
					}
					saveConfiguration(configuration);
					return;
				}
			}

			// For all cameras in config
			for (Camera cam : configuration.getCameras()) {

				// if there is a message for camera
				if (cam.getMsg() != null && cam.getMsg().length() > 0) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Update configuration action: " + cam.getMsg() + " (" + cameraConnections.size() + " cameras connected)");
					}
					
					boolean found = false;
					// find connection for camera
					for (ConfigConnection configConnection : cameraConnections) {
						if (configConnection.getIdentifier().equals(cam.getId())) {
							calibCam = cam;

							// just refresh
							if (cam.getMsg().equalsIgnoreCase("refresh")) {
								cam.setMsg("");
							}

							if (cam.getMsg().equalsIgnoreCase("start_calibration")) {
								try {
									int patternW = Integer.parseInt(cam.getPropertyHashtable().get("patternW").getContent());
									int patternH = Integer.parseInt(cam.getPropertyHashtable().get("patternH").getContent());
									int displayPosX = Integer.parseInt(cam.getPropertyHashtable().get("displayPosX").getContent());
									int displayPosY = Integer.parseInt(cam.getPropertyHashtable().get("displayPosY").getContent());
									int displayPosWidth = Integer.parseInt(cam.getPropertyHashtable().get("displayPosWidth").getContent());
									int displayPosHeight = Integer.parseInt(cam.getPropertyHashtable().get("displayPosHeight").getContent());
									boolean startPatternWhite = (Integer.parseInt(cam.getPropertyHashtable().get("startPatternWhite").getContent()) == 1) ? true : false;

									int displayWidth = 0;
									int displayHeight = 0;
									for (Display display : configuration.getDisplays()) {
										displayWidth = Integer.parseInt(display.getPropertyHashtable().get("size_x").getContent());
										displayHeight = Integer.parseInt(display.getPropertyHashtable().get("size_y").getContent());
										break;
									}

//									showWhiteScreen(client, 0, false);
//									showPatternScreen(client, true, "\\Display0", patternW, patternH, 6, 6);
									
//									calibFrame.openPattern(patternW, patternH, displayPosX, displayPosY,
//											displayPosWidth, displayPosHeight, startPatternWhite, displayWidth,
//											displayHeight);

									if (LOG.isDebugEnabled()) {
										LOG.debug("Open calibration pattern for camera #" + cam.getId());
									}
								}
								catch (NumberFormatException e) {
									if (LOG.isErrorEnabled()) {
										LOG.error("Couldn't parse String to integer: xml properties cam #"
												+ cam.getId());
									}
								}
							}

							if (cam.getMsg().equalsIgnoreCase("start_tracking")) {
								long backdiffTime = Long.parseLong(cam.getPropertyHashtable().get("backdiff_time").getContent());
								showWhiteScreen(client, backdiffTime, true);
							}

							cam.setOnline(true);
							
							// update connected camera
							try {
								configuration = configConnection.updateCamera(configuration);
								found = true;
							}
							catch (JAXBException e) {
								if (LOG.isErrorEnabled()) {
									LOG.error(e.getMessage(), e);
								}

								getConfig().getCameraHashtable().get(cam.getId()).setMsg("Error_Server: JAXBException: " + e.getMessage());
								detach(configConnection);
								return;
							}
							catch (XMLStreamException e) {
								if (LOG.isErrorEnabled()) {
									LOG.error(e.getMessage(), e);
								}

								getConfig().getCameraHashtable().get(cam.getId()).setMsg("Error_Server: XMLStreamException: " + e.getMessage());
								return;
							}
							break;
						}
					}
					if (found) {
						cam.setOnline(true);
						saveConfiguration(configuration);
					}
					else {
						Configuration cfg = getConfig();
						Camera camera = cfg.getCameraHashtable().get(cam.getId());
						if (camera != null) {
							camera.setOnline(false);
						}
						return;
					}
				}
			}
		}
		
		showWhiteScreen(client, 0, false);
		
		client.close();
	}
	
	/**
	 * @param show
	 */
	private void showWhiteScreen(final ControlClient client, long backdiffTime, boolean show) {
		if(!configManagable.isShowWhite()) return;
		
		final WhiteScreen whiteScreen = new WhiteScreen();
		whiteScreen.setState(show ? Switch.ON : Switch.OFF);
		
		new Thread() {
			
			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				client.send(whiteScreen);
			}
		}.start();
		
		try {
			Thread.sleep(5000 + backdiffTime);
		} catch (InterruptedException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * @param show
	 * @param params
	 */
	private void showPatternScreen(final ControlClient client, boolean show, String graphicsDevice, int... params) {
		final PatternScreen patternScreen;
		if (params.length == 4) {
			patternScreen = new PatternScreen(graphicsDevice, params[0], params[1], params[2], params[3]);
		}
		else {
			patternScreen = new PatternScreen();
		}
		
		System.out.println("SENDING PATTERN SCREEN");
		
		patternScreen.setState(show ? Switch.ON : Switch.OFF);
		
		new Thread() {
			
			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				client.send(patternScreen);
			}
		}.start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
}
