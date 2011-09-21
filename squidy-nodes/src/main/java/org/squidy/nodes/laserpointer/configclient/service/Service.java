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

import org.squidy.nodes.laserpointer.configclient.service.comm.CommException;

/**
 * Generic singleton service.
 * @author Jo Bieg
 *
 */
public abstract class Service {
	protected static final String STARTEDMSG = "Service must be started.";
	
	// whether or not service is started
	protected boolean started = false;
	
	public void startup() throws CommException {
		if(!started) startupImpl();
		started = true;
	}
	
	public void shutdown() {
		if(!started) return;
		shutdownImpl();
		started = false;
	}
	
	public abstract Class<? extends Service> getServiceType();
	
	protected abstract void startupImpl() throws CommException;
	protected abstract void shutdownImpl();

	public boolean isStarted() {
		return started;
	}

}
