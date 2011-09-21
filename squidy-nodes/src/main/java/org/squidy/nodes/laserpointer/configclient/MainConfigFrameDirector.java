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
import java.net.InetSocketAddress;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.squidy.nodes.laserpointer.config.xml.Camera;
import org.squidy.nodes.laserpointer.config.xml.Configuration;
import org.squidy.nodes.laserpointer.configclient.service.ServiceRegistry;
import org.squidy.nodes.laserpointer.configclient.service.comm.CommException;
import org.squidy.nodes.laserpointer.configclient.service.comm.CommHelper;
import org.squidy.nodes.laserpointer.configclient.service.comm.CommService;
import org.squidy.nodes.laserpointer.configclient.service.comm.Protocol;
import org.squidy.nodes.laserpointer.configclient.service.comm.TcpIpCommService;
import org.squidy.nodes.laserpointer.configclient.service.javaprop.JavaPropService;



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
		SwingWorker<Void,Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				cService.refreshCamera(camera);
				return null;
			}
		};
		new WorkerDialog("Refreshing camera " + camera.getId(), null, worker);
		try {
			worker.get();
		} catch (InterruptedException e) {
		} catch (CancellationException e) {
			fullReset();
			return false;
		} catch (ExecutionException e) {
			fullReset();
			JOptionPane.showMessageDialog(null,
					e.getCause().getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
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
		SwingWorker<Void,Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				cService.startTracking(camera);
				return null;
			};
		};
		new WorkerDialog("Starting camera " + camera.getId(), null, worker);
		try {
			worker.get();
		} catch (InterruptedException e) {
		} catch (CancellationException e) {
			fullReset();
			return false;
		} catch (ExecutionException e) {
			fullReset();
			JOptionPane.showMessageDialog(null, e.getCause().getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(!camera.isOnline()) {
			JOptionPane.showMessageDialog(null, "Camera " + camera.getId()
					+ " is offline. Unable to start tracking.",
					"Communication Failure", JOptionPane.ERROR_MESSAGE);
			camera.setTracking(false);
			return false;
		}
		if(camera.getMsg() != null && !camera.getMsg().equals(Protocol.CAM_STARTTRACKING)) {
			JOptionPane.showMessageDialog(null,
					"Camera reported an error when starting tracking. Message: " + camera.getMsg(),
					"Property Message", JOptionPane.INFORMATION_MESSAGE);
			CommHelper.clearMessages(camera);
			return false;
		}
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
		SwingWorker<Void,Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				cService.stopTracking(camera);
				return null;
			};
		};
		new WorkerDialog("Stopping Tracking", null, worker);
		try {
			worker.get();
		} catch (InterruptedException e) {
		} catch (CancellationException e) {
			fullReset();
			return false;
		} catch (ExecutionException e) {
			fullReset();
			JOptionPane.showMessageDialog(null, e.getCause().getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(camera.getMsg() != null) {
			assert(false) : "Unexpected return message.";
			camera.setTracking(null);
			return false;
		}
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
