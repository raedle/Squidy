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

/**
 * <code>ControlException</code>.
 *
 * <pre>
 * Date: Sep 25, 2008
 * Time: 5:31:35 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ControlException.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class ControlException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6204951619584218979L;

	/**
	 * 
	 */
	public ControlException() {
	}

	/**
	 * @param message
	 */
	public ControlException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ControlException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ControlException(String message, Throwable cause) {
		super(message, cause);
	}

}
