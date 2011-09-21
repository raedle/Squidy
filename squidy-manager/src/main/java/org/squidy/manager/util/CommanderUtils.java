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

package org.squidy.manager.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.commander.ControlClient;


/**
 * <code>CommanderUtils</code>.
 * 
 * <pre>
 * Date: Apr 28, 2009
 * Time: 11:00:53 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: CommanderUtils.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class CommanderUtils {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(CommanderUtils.class);
	
	/**
	 * @param host
	 * @param port
	 * @return
	 */
	public static ControlClient getCommanderClient(String host, int port) {
		
		InetAddress address = null;
		try {
			address = InetAddress.getByName(host);
		}
		catch (UnknownHostException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		try {
			return new ControlClient(address, port);
		}
		catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			return null;
		}
	}
}
