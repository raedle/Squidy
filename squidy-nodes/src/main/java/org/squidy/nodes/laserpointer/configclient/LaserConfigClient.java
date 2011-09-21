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

package org.squidy.nodes.laserpointer.configclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.squidy.designer.piccolo.JComponentWrapper;
import org.squidy.manager.plugin.Pluggable;
import org.squidy.manager.plugin.Plugin;
import org.squidy.manager.plugin.Plugin.Event;
import org.squidy.nodes.laserpointer.configclient.service.ServiceRegistry;
import org.squidy.nodes.laserpointer.configclient.service.comm.CommException;
import org.squidy.nodes.laserpointer.configclient.service.javaprop.JavaPropService;

import edu.umd.cs.piccolo.PNode;

@Plugin(
	name = "Laser Config Client",
	icon = "/org/squidy/nodes/image/48x48/gear.png"
)
public class LaserConfigClient implements Pluggable {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(LaserConfigClient.class);
	
	private MainConfigFrameDirector mainConfigFrameDirector;

	/**
	 * 
	 */
	public LaserConfigClient() {
		// LOGGING
//		PropertyConfigurator.configure("logger.cfg");
		// BasicConfigurator.configure();

		// SERVICES
		JavaPropService gps = new JavaPropService("properties");
		ServiceRegistry.getInstance().addService(gps);
		try {
			gps.startup();
		}
		catch (CommException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		mainConfigFrameDirector = new MainConfigFrameDirector();
	}
	
	public void startAllTracking() {
		mainConfigFrameDirector.startAllCameras();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.Plugin#getInterface()
	 */
	@Plugin.Interface
	public PNode getInterface() {
		return new PNode();// new JComponentWrapper(mainConfigFrameDirector.getMainConfigFrame());
	}

	@Plugin.Logic(events = { Event.ZOOM_OUT })
	public void startTracking() {
		System.out.println("START TRACKING");
		
//		mainConfigFrameDirector.startTracking(0);
	}

	@Plugin.Logic(events = { Event.ZOOM_IN })
	public void stopTracking() {
		System.out.println("STOP TRACKING");
		
//		mainConfigFrameDirector.stopTracking(0);
	}

	@Plugin.Logic(events = { Event.ZOOM_IN })
	public void stopTracking1() {
		System.out.println("STOP TRACKING 1");
		
//		mainConfigFrameDirector.stopTracking(0);
	}
}
