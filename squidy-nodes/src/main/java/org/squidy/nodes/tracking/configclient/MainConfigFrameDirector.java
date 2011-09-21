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


package org.squidy.nodes.tracking.configclient;
import java.net.InetSocketAddress;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.squidy.designer.Designer;
import org.squidy.nodes.tracking.config.xml.Camera;
import org.squidy.nodes.tracking.config.xml.Configuration;
import org.squidy.nodes.tracking.configclient.service.ServiceRegistry;
import org.squidy.nodes.tracking.configclient.service.comm.CommException;
import org.squidy.nodes.tracking.configclient.service.comm.CommHelper;
import org.squidy.nodes.tracking.configclient.service.comm.CommService;
import org.squidy.nodes.tracking.configclient.service.comm.Protocol;
import org.squidy.nodes.tracking.configclient.service.comm.TcpIpCommService;
import org.squidy.nodes.tracking.configclient.service.javaprop.JavaPropService;



public class MainConfigFrameDirector {
	
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(MainConfigFrameDirector.class);
	
	// service
	private JavaPropService pService = (JavaPropService) ServiceRegistry
			.getInstance().getService(JavaPropService.class);
	private CommService cService = (CommService) ServiceRegistry.getInstance()
			.getService(CommService.class);
	
	// data
	private String serverIp; // TODO: user InetSocketAddress type here
	private int serverPort = -1;

	private Configuration config;
		
	public MainConfigFrameDirector() {
		
		// load configuration file and check information
		serverIp = pService.get(JavaPropService.COMM_IP);
		serverPort = Integer.parseInt(pService.get(JavaPropService.COMM_PORT));
		
		if(serverPort == -1) {
			LOG.fatal("Comm server port must be specified in properties file.");
			System.exit(-1);
		}
		
		serverIp = "127.0.0.1";
		startServices();
		loadConfig();
	}
	
	public boolean startAllCameras() {
		for (Camera camera : config.getCamera()) {
			if(!startTracking(camera)) return false;
		}
		return true;
	}
	
	public boolean stopAllCameras() {
		for (Camera camera : config.getCamera()) {
			if(!stopTracking(camera)) return false;
		}
		return true;
	}
	
	public boolean refreshAllCameras() {
		for (Camera camera : config.getCamera()) {
			if(!cService.isStarted()) break;
			refreshCamera(camera);
		}
		return true;
	}
	
	public boolean reloadCamera(int row) {
		final Camera camera = config.getCamera().get(row);
		assert(camera != null);
		return refreshCamera(camera);
	}
	
	private boolean refreshCamera(final Camera camera) {
		try {
			cService.refreshCamera(camera);
		} catch (CommException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Refreshing camera " + camera.getId());
		return true;	
	}
	
	/**
	 * Access method for frame.
	 * @param row
	 * @return
	 */
	public boolean startTracking(int row) {
		Camera camera = config.getCamera().get(row);
		assert(camera != null);
		return startTracking(camera);
	}
	
	/**
	 * Starts a camera.
	 * @param camera
	 * @return
	 */
	private boolean startTracking(final Camera camera) {
		try {
			cService.startTracking(camera);
		} catch (CommException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Starting camera " + camera.getId());
		camera.setTracking(true);
		return true;
	}
	
	/**
	 * Access method for frame.
	 * @param row
	 * @return
	 */
	public boolean stopTracking(int row) {
		final Camera camera = config.getCamera().get(row);
		assert(camera != null);
		return(stopTracking(camera));
	}
	
	/**
	 * Stop a camera.
	 * @param camera
	 * @return
	 */
	private boolean stopTracking(final Camera camera) {
		try {
			cService.stopTracking(camera);
		} catch (CommException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Stopping Tracking");
		camera.setTracking(false);
		return true;
	}

	/**
	 * Load config file from current comm service.
	 * @return
	 */
	private boolean loadConfig() {
		assert(cService != null) : "Communication service must be set.";
		try {
			config = cService.loadConfig();
		} catch (CommException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Initialize services and start communication service.
	 * @return
	 */
	private boolean startServices() {
		cService = new TcpIpCommService(new InetSocketAddress(serverIp, serverPort));
		ServiceRegistry.getInstance().addService(cService);
		try {
			cService.startup();
		} catch (CommException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public void close() {
		cService.shutdown();
		System.exit(0);
	}
		
	public String getServerIp() {
		if(serverIp == null) serverIp = "";
		return serverIp;
	}
	
	/**
	 * Terminate connections and reset table contents.
	 */
	public void fullReset() {
		if(cService != null) cService.shutdown();
		config = null;
	}
}
