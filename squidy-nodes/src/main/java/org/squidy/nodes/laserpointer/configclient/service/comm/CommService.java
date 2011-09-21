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

package org.squidy.nodes.laserpointer.configclient.service.comm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.nodes.laserpointer.config.xml.Camera;
import org.squidy.nodes.laserpointer.config.xml.Configuration;
import org.squidy.nodes.laserpointer.config.xml.Display;
import org.squidy.nodes.laserpointer.config.xml.Property;
import org.squidy.nodes.laserpointer.configclient.service.Service;


/**
 * Generic camera and display communication service.
 * 
 * Allows the use of multiple data sources, such as TCP/IP or local files. 
 * Therefore users must setInstance() to indicate what service to use.
 * @author Jo Bieg
 */
public abstract class CommService extends Service {
	
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Service.class);
		
	protected static final String INTERNAL_ERROR_MSG = "Internal error. See logfile for details.";
	
	// current configuration which users will hold a reference of
	private Configuration activeConfig = new Configuration(); 
	
	public Configuration loadConfig() throws CommException {
		assert(started) : STARTEDMSG;
		activeConfig = loadConfigImpl();
		return activeConfig;
	}

	private void save(Configuration config) throws CommException {
		assert(started) : STARTEDMSG;
		assert(activeConfig != null) : "Config must not be null.";
		Configuration newConfig = saveConfigImpl(config);
		activeConfig.update(newConfig);
	}
	
	private void save(Camera camera) throws CommException {
		String id = camera.getId();
		
		for (Camera tmpCamera : activeConfig.getCameras()) {
			if (tmpCamera.getId().equals(id)) {
				tmpCamera.setMsg(camera.getMsg());
			}
		}
		
		save(activeConfig);
		
		Camera newCamera = activeConfig.getCameraHashtable().get(id);
		
		if(newCamera == null) {
			LOG.error("Couldn't find saved camera " +
					camera.getId() + " in active configuration." +
					"Possibly server didn't return correct config?");
			throw new CommException("Unable to save camera. See logfile for more information.");
		}
	}

	private void save(Display display) throws CommException {
		String id = display.getId();
		
		save(activeConfig);
		
		Display newDisplay = activeConfig.getDisplayHashtable().get(id);
		
		if(newDisplay == null) {
			LOG.error("Couldn't find saved display " +
					display.getId() + " in active configuration." +
					"Possibly server didn't return correct config?");
			throw new CommException("Unable to save display. See logfile for more information.");
		}
	}
	
	public void startCalibration(Camera camera) throws CommException {
		assert(started) : STARTEDMSG;
		assert(activeConfig.getCamera().contains(camera)) : "Camera must be in active config";
		LOG.info("startCalibration");
		
		Property pFramerate = CommHelper.readPropertyFromConfig(Property.CAM_FRAMERATE_CALIB, camera);
		if(pFramerate == null) throw new CommException("Require calibration framerate in order to start tracking.");
		Property pPixelclock = CommHelper.readPropertyFromConfig(Property.CAM_PIXELCLOCK_CALIB, camera);
		if(pPixelclock == null) throw new CommException("Require calibration pixelclock in order to start tracking.");
		Property pExposure = CommHelper.readPropertyFromConfig(Property.CAM_EXPOSURE_CALIB, camera);
		if(pExposure == null) throw new CommException("Require calibration exposure in order to start tracking.");

		// overwrite cam props with calibration settings
		CommHelper.writePropertyToConfig(new Property(pFramerate.getContent(),
				Property.CAM_FRAMERATE, Property.TYPE_FLOAT, Protocol.UPDATE), camera);
		CommHelper.writePropertyToConfig(new Property(pPixelclock.getContent(),
				Property.CAM_PIXELCLOCK, Property.TYPE_FLOAT, Protocol.UPDATE),	camera);
		CommHelper.writePropertyToConfig(new Property(pExposure.getContent(),
				Property.CAM_EXPOSURE, Property.TYPE_FLOAT, Protocol.UPDATE), camera);
		
		camera.setMsg(Protocol.UPDATE);
		save(camera);
		
		camera.setMsg(Protocol.CAM_STARTCALIBRATION);
		save(camera);
	}
	
	public void startTracking(Camera camera) throws CommException {
		assert(started) : STARTEDMSG;
		assert(activeConfig.getCamera().contains(camera)) : "Camera must be in active config";
		LOG.info("startTracking");
		
		Property pFramerate = CommHelper.readPropertyFromConfig(Property.CAM_FRAMERATE_TRACK, camera);
		if(pFramerate == null) throw new CommException("Require tracking framerate in order to start tracking.");
		Property pPixelclock = CommHelper.readPropertyFromConfig(Property.CAM_PIXELCLOCK_TRACK, camera);
		if(pPixelclock == null) throw new CommException("Require tracking pixelclock in order to start tracking.");
		Property pExposure = CommHelper.readPropertyFromConfig(Property.CAM_EXPOSURE_TRACK, camera);
		if(pExposure == null) throw new CommException("Require tracking exposure in order to start tracking.");
		
		// overwrite cam props with tracking settings
		CommHelper.writePropertyToConfig(new Property(pFramerate.getContent(),
				Property.CAM_FRAMERATE, Property.TYPE_FLOAT, Protocol.UPDATE), camera);
		CommHelper.writePropertyToConfig(new Property(pPixelclock.getContent(),
				Property.CAM_PIXELCLOCK, Property.TYPE_FLOAT, Protocol.UPDATE),	camera);
		CommHelper.writePropertyToConfig(new Property(pExposure.getContent(),
				Property.CAM_EXPOSURE, Property.TYPE_FLOAT, Protocol.UPDATE), camera);
		
		camera.setMsg(Protocol.UPDATE);
		save(camera);
		
		// Hier kein update der _tracking properties mit den vom server zurückgesendeten parametern
		// da sich sonst Fehler aufschaukelt. Beschlossen 25.7. im ZKM.
		
		camera.setTracking(true);
		camera.setMsg(Protocol.CAM_STARTTRACKING);
		save(camera);
	}
	
	public void stopTracking(Camera camera) throws CommException {
		assert(started) : STARTEDMSG;
		assert(activeConfig.getCamera().contains(camera)) : "Camera must be in active config.";
		LOG.info("stopTracking");
		
		camera.setTracking(false);
		camera.setMsg(Protocol.CAM_STOPTRACKING);
		save(camera);
	}
	
	public void update(Camera camera) throws CommException {
		assert(started) : STARTEDMSG;
		assert(activeConfig.getCamera().contains(camera)) : "Camera must be in active config.";
		LOG.info("update(camera)");
		
		camera.setMsg(Protocol.UPDATE);
		save(camera);
	}
	
	public void update(Display display) throws CommException {
		assert(started) : STARTEDMSG;
		assert(activeConfig.getDisplay().contains(display)) : "Display must be in active config.";
		LOG.info("update(display)");
		
		display.setMsg(Protocol.UPDATE);
		save(display);
	}
	
	public void refreshCamera(Camera camera) throws CommException {
		assert(started) : STARTEDMSG;
		LOG.info("refreshCamera");
		
		camera.setMsg(Protocol.CAM_REFRESH);
		save(camera);
	}
	
	public Configuration getActiveConfig() {
		return activeConfig;
	}
	
	protected abstract Configuration loadConfigImpl() throws CommException;
	protected abstract Configuration saveConfigImpl(Configuration config) throws CommException;
	
	@Override
	public Class<? extends Service> getServiceType() {
		return CommService.class;
	}
}
