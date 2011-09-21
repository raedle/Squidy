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


package org.squidy.nodes.tracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.nodes.Laserpointer;
import org.squidy.nodes.ir.ConfigManagable;
import org.squidy.nodes.tracking.config.ConfigNotifier;
import org.squidy.nodes.tracking.config.xml.Camera;
import org.squidy.nodes.tracking.config.xml.Display;
import org.squidy.nodes.tracking.config.xml.Property;


/**
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * 
 */
public class LaserConnection extends Thread implements ConfigNotifier {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(LaserConnection.class);

	private Socket socket = null;

	private String line;

	private String identifier = "";

	private ConfigManagable configManagable;

	private boolean running = true;

	private double displaySizeX, displaySizeY, displayPosX, displayPosY, displayPosWidth, displayPosHeight = 0;
	private boolean isCaptureBackside, isCaptureHorizontal = false;

	public LaserConnection(ConfigManagable configManagable, Socket socket) {
		this.socket = socket;
		this.configManagable = configManagable;
		configManagable.attachNotifier(this);
		start();
	}

	public void close() {
		LOG.info("Close socket");
		running = false;
		configManagable.detachNotifier(this);
		try {
			socket.close();
		}
		catch (IOException e) {
			LOG.error("Couldn't close socket");
		}
	}

	public void updateConfig() {

		for (Display display : configManagable.getConfig().getDisplays()) {
			// TODO for zkm use the first one - later multi-display-support
			displaySizeX = Integer.parseInt(display.getPropertyHashtable().get("size_x").getContent());
			displaySizeY = Integer.parseInt(display.getPropertyHashtable().get("size_y").getContent());
			break;
		}

		for (Camera camera : configManagable.getConfig().getCameras()) {
			if (identifier.equals(camera.getId())) {
				try {
					Hashtable<String, Property> properties = camera.getPropertyHashtable();

					displayPosX = Integer.parseInt(properties.get("displayPosX").getContent());
					displayPosY = Integer.parseInt(properties.get("displayPosY").getContent());
					displayPosWidth = Integer.parseInt(properties.get("displayPosWidth").getContent());
					displayPosHeight = Integer.parseInt(properties.get("displayPosHeight").getContent());
					// TODO change to boolean
					isCaptureBackside = (Integer.parseInt(properties.get("isCaptureBackside").getContent()) == 1) ? true
							: false;
					// TODO change to boolean
					isCaptureHorizontal = (Integer.parseInt(properties.get("isCaptureHorizontal").getContent()) == 1) ? true
							: false;
				}
				catch (NumberFormatException e) {
					if (LOG.isErrorEnabled()) {
						LOG.error("Couldn't parse string property to integer value for camera #" + camera.getId());
					}
				}
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {

			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socket.setTcpNoDelay(true);

			while (running && (line = input.readLine()) != null) {

				if (line.indexOf("|") != -1) {
					// laser-pointer-koordinaten
					StringTokenizer str = new StringTokenizer(line, "|");
					if (configManagable != null && (str.countTokens() == 2 || str.countTokens() == 3)) {

						double cx = 0;
						double cy = 0;
						try {
							cx = Double.parseDouble(str.nextToken());
							cy = Double.parseDouble(str.nextToken());
						}
						catch (NumberFormatException e) {
							LOG.error("Parse error occured");
							continue;
						}

						if (str.countTokens() == 1) {
							boolean button = Boolean.parseBoolean(str.nextToken());
							// TODO [RR]: FIXME
							configManagable
									.publish(new DataButton(Laserpointer.class, 1, button));
						}

						if (isCaptureHorizontal) {
							double tmp = cx;
							cx = cy;
							cy = tmp;
						}
						if (isCaptureBackside) {
							cx = 1.0f - cx;
						}

						double rx = (displayPosWidth / displaySizeX * cx) + (displayPosX / displaySizeX);
						double ry = (displayPosHeight / displaySizeY * cy) + (displayPosY / displaySizeY);

						// TODO [RR]: FIXME
						configManagable.publish(new DataPosition2D(Laserpointer.class, rx, ry));
					}
					else {
						LOG.info("unknown packet: " + line);
					}

				}
				else {
					if (line.indexOf("#") != -1) {
						// kamera name, width, height
						StringTokenizer str = new StringTokenizer(line, "#");
						identifier = str.nextToken();

						LOG.info("Camera \"" + identifier + "\" connected!");

						updateConfig();

					}
					else {
						LOG.info("unknown packet: " + line);
						close();
					}
				}
			}
			input.close();

		}
		catch (IOException ex) {
			LOG.error("Connection error occured.");
			close();
		}
	}
}
