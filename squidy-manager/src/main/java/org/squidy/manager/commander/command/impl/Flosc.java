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

package org.squidy.manager.commander.command.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.commander.ControlServerContext;
import org.squidy.manager.commander.command.ICommand;
import org.squidy.manager.commander.command.SwitchableCommand;


/**
 * <code>Flosc</code>.
 * 
 * <pre>
 * Date: Sep 25, 2008
 * Time: 5:22:50 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: Flosc.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class Flosc extends SwitchableCommand {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -5305709396393648072L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Flosc.class);

	public static final String KEY_GATEWAY = "flosc.gateway";

	private int portJava;

	private int portFlash;

	/**
	 * 
	 */
	public Flosc() {
		this.portJava = 3333;
		this.portFlash = 3000;
	}
	
	/**
	 * @param portJava
	 * @param portFlash
	 */
	public Flosc(int portJava, int portFlash) {
		this.portJava = portJava;
		this.portFlash = portFlash;
	}
	
	/**
	 * @param portJava the portJava to set
	 */
	public final void setPortJava(int portJava) {
		this.portJava = portJava;
	}

	/**
	 * @param portFlash the portFlash to set
	 */
	public final void setPortFlash(int portFlash) {
		this.portFlash = portFlash;
	}

	/* (non-Javadoc)
	 * @see org.squidy.control.command.SwitchableCommand#on(org.squidy.control.ControlServerContext)
	 */
	public ICommand on(ControlServerContext context) {
//		context.putObject(KEY_GATEWAY, new Gateway(portJava, portFlash));
//
//		if (LOG.isInfoEnabled()) {
//			LOG.info("Flosc gateway started. Java Port: " + portJava + " and Flash Port: " + portFlash);
//		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.squidy.control.command.SwitchableCommand#off(org.squidy.control.ControlServerContext)
	 */
	public ICommand off(ControlServerContext context) {
//		Gateway gateway = context.getObject(Gateway.class, KEY_GATEWAY);
//		gateway.kill();
//
//		if (LOG.isInfoEnabled()) {
//			LOG.info("Flosc gateway stopped");
//		}
		return null;
	}
}
