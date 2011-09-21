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


package org.squidy.nodes.vision;

import java.util.Collection;
import java.util.HashSet;

/**
 * <code>VisionSource</code>.
 * 
 * <pre>
 * Date: Apr 27, 2009
 * Time: 4:31:32 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: VisionSource.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class VisionSource {
	
	private Collection<VisionSourceCallback> callbacks = new HashSet<VisionSourceCallback>();
	
	/**
	 * @param callback
	 */
	public void addVisionSourceCallback(VisionSourceCallback callback) {
		callbacks.add(callback);
	}
	
	/**
	 * @param type
	 * @param data
	 */
	public void dataUpdate(String type, byte[] data) {
		for (VisionSourceCallback callback : callbacks) {
			callback.dataUpdate(data);
		}
	}
	
	public native String getProperty(String name);
	public native boolean setProperty(String name, String value);
	
	public native void readyToReceiveDataUpdate(String type, boolean ready);
}
