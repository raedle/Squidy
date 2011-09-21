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

package org.squidy.manager.commander.command;

import org.squidy.manager.commander.ControlServerContext;
import org.squidy.manager.commander.command.utility.Switch;

/**
 * <code>SwitchableCommand</code>.
 * 
 * <pre>
 * Date: Sep 25, 2008
 * Time: 7:01:21 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: SwitchableCommand.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public abstract class SwitchableCommand implements ICommand, Switchable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -7788834426740375282L;
	
	private Switch state;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.control.command.ICommand#execute(org.squidy.control.ControlServerContext
	 * )
	 */
	public final ICommand execute(ControlServerContext context) {
		switch (state) {
		case ON:
			return on(context);
		case OFF:
			return off(context);
		default:
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.control.command.Switchable#setState(org.squidy.control.command.utility
	 * .Switch)
	 */
	public void setState(Switch state) {
		this.state = state;
	}

	public Switch getState() {
		return state;
	}

	/**
	 * @param context
	 * @return
	 */
	public abstract ICommand on(ControlServerContext context);

	/**
	 * @param context
	 * @return
	 */
	public abstract ICommand off(ControlServerContext context);
}
