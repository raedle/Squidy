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

package org.squidy.manager;

import org.squidy.SquidyException;

/**
 * <code>ManagerException</code>.
 *
 * <pre>
 * Date: Jun 26, 2008
 * Time: 12:51:42 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ManagerException.java 772 2011-09-16 15:39:44Z raedle $  * @since 1.0.0  * @since 1.0.0
 * @since 1.0
 */
public class ManagerException extends SquidyException {
	
	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 193406470984088711L;

	/**
	 * @param message
	 */
	public ManagerException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public ManagerException(Throwable cause) {
		super(cause);
	}
}
