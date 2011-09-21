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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * <code>Issue</code>.
 *
 * <pre>
 * Date: Sep 26, 2008
 * Time: 1:36:44 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: Issue.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class Issue {

	private String message;
	
	private Exception exception;
	
	public Issue(String message) {
		this.message = message;
	}
	
	/**
	 * @param exception
	 */
	public Issue(Exception exception) {
		this.exception = exception;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(baos);
		exception.printStackTrace(stream);
		
		message = baos.toString();
	}

	/**
	 * @return the message
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * @return the exception
	 */
	public final Exception getException() {
		return exception;
	}
}
