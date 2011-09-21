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

import org.squidy.manager.data.IData;

/**
 * <code>ProcessException</code>.
 *
 * <pre>
 * Date: Jul 30, 2008
 * Time: 12:09:18 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ProcessException.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class ProcessException extends ManagerException {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -8502576525864961143L;
	
	private IData data;

	public ProcessException(String message) {
		super(message);
	}

	public ProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProcessException(Throwable cause) {
		super(cause);
	}
	
	public ProcessException(String message, IData data) {
		this(message);
		this.data = data;
	}
	
	public ProcessException(String message, Throwable cause, IData data) {
		this(message, cause);
		this.data = data;
	}
	
	/**
	 * Returns the data object if the process exception has been caused within a
	 * data process.
	 * 
	 * @return The data object or null if no data has been set.
	 */
	public IData getData() {
		return data;
	}
}
