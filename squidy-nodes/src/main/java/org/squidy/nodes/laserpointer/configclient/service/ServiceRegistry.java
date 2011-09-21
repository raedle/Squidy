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

package org.squidy.nodes.laserpointer.configclient.service;

import java.util.Hashtable;


public class ServiceRegistry {
	protected static final String STARTEDMSG = "Service must be started.";
	
	private static ServiceRegistry instance;
	
	private Hashtable<Class<? extends Service>, Service> services = new Hashtable<Class<? extends Service>, Service>();
		
	public static ServiceRegistry getInstance() {
		if(instance == null) instance = new ServiceRegistry();
		return instance;
	}
	
	public void addService(Service service) {
		services.put(service.getServiceType(), service);
	}
	
	public Service getService(Class<? extends Service> type) {
		Service s = services.get(type);
		//assert(s != null) : "No such service in registry: " + type;
		return s;
	}

}
