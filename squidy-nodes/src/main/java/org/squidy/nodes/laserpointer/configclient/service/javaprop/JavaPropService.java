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

package org.squidy.nodes.laserpointer.configclient.service.javaprop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.squidy.nodes.laserpointer.configclient.service.Service;
import org.squidy.nodes.laserpointer.configclient.service.comm.CommException;


public class JavaPropService extends Service {
	private static Logger logger = Logger.getLogger(JavaPropService.class);

	public static final String COMM_IP = "comm_ip";
	public static final String COMM_PORT = "comm_port";
	public static final String VIDEO_IP = "video_ip";
	public static final String VIDEO_PORT = "video_port";
	public static final String DEBUG_IMAGE = "debug_image";
	public static final String DEBUG_COMM = "debug_comm";
	
	private String filename;
	private Properties properties;
	
	public JavaPropService(String filename) {
		this.filename = filename;
	}
	
	@Override
	public Class<? extends Service> getServiceType() {
		return JavaPropService.class;
	}

	@Override
	protected void shutdownImpl() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void startupImpl() throws CommException {
		File propertiesFile = new File(filename);
		if(!propertiesFile.exists()) {
			try {
				propertiesFile.createNewFile();
			} catch (IOException e1) {
				logger.fatal("Cannot create properties file: " + propertiesFile);
				throw new CommException();
			}
		}
		properties = new Properties();
		try {
			properties.load(new FileInputStream(propertiesFile));
		} catch (FileNotFoundException e) {
			assert(false) : "Cannot find properties file.";
			e.printStackTrace();
			throw new CommException();
		} catch (IOException e) {
			logger.fatal("Cannot open properties file: " + propertiesFile);
			throw new CommException();
		}
	}

	public Properties getProperties() {
		return properties;
	}
	
	public String get(String key) {
		return properties.getProperty(key);
	}

}
