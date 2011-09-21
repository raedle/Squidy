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

package org.squidy.manager.commander;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>ControlServerContext</code>.
 *
 * <pre>
 * Date: Sep 21, 2008
 * Time: 11:15:51 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ControlServerContext.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class ControlServerContext {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ControlServerContext.class);
	
	private Map<String, Object> context;
	
	public ControlServerContext() {
		context = new HashMap<String, Object>();
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void putObject(String key, Object value) {
		context.put(key, value);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param type
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> type, String key) {
		
		Object value = context.get(key);
		
		if (value == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No value available in commander server context for key " + key);
			}
			
			return null;
		}
		
		if (!type.isAssignableFrom(value.getClass())) {
			throw new ControlException("Requested object with key " + key + " isn't assignable from " + type.getName());
		}
		
		return (T) value;
	}
}
