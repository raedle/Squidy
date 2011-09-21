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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;

import org.squidy.SquidyException;


/**
 * <code>ValidationException</code>.
 *
 * <pre>
 * Date: Oct 31, 2008
 * Time: 7:49:09 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ExceptionCollection.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class ExceptionCollection extends SquidyException {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -75670718979243588L;
	
	private Collection<Exception> exceptions;
	
	/**
	 * @param exceptions
	 */
	public ExceptionCollection(Collection<Exception> exceptions) {
		this.exceptions = exceptions;
	}

	/**
	 * @return the exceptions
	 */
	public final Collection<Exception> getExceptions() {
		return exceptions;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#printStackTrace()
	 */
	@Override
	public void printStackTrace() {
		super.printStackTrace();
		
		for (Exception e : exceptions) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 */
	@Override
	public void printStackTrace(PrintStream s) {
		super.printStackTrace(s);
		
		for (Exception e : exceptions) {
			e.printStackTrace(s);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
	 */
	@Override
	public void printStackTrace(PrintWriter s) {
		super.printStackTrace(s);
		
		for (Exception e : exceptions) {
			e.printStackTrace(s);
		}
	}
}
