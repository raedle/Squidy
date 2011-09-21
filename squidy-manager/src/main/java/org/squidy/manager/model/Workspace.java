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

package org.squidy.manager.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.IProcessable;
import org.squidy.manager.ProcessException;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;


/**
 * <code>Workspace</code>.
 * 
 * <pre>
 * Date: Feb 22, 2009
 * Time: 9:31:00 PM
 * </pre>
 * 
 * @author
 * Roman R&amp;aumldle<br />
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 * Human-Computer Interaction Group<br />
 * University of Konstanz
 * 
 * @version $Id: Workspace.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "Workspace")
public class Workspace extends Piping {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Workspace.class);
	
	// #############################################################################
	// BEGIN JAXB
	// #############################################################################
	
	/**
	 * Default constructor required for JAXB.
	 */
	public Workspace() {
		// empty
	}
	
	// #############################################################################
	// END JAXB
	// #############################################################################
	
	// #############################################################################
	// BEGIN IProcessable
	// #############################################################################

	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#delete()
	 */
	public void delete() throws ProcessException {
//		throw new UnsupportedOperationException("Workspace does not have a parental object.");
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.IProcessable#process(org.squidy.manager.data.IDataContainer)
	 */
	public IDataContainer process(IDataContainer dataContainer) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Processing container in workspace... " + dataContainer);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.IProcessable#publish(org.squidy.manager.data.IData[])
	 */
	public void publish(IData... data) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Publishing data from workspace... " + data);
		}
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.IProcessable#publish(org.squidy.manager.data.IDataContainer)
	 */
	public void publish(IDataContainer dataContainer) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Publishing container from workspace... " + dataContainer);
		}
	}
	
	// #############################################################################
	// END IProcessable
	// #############################################################################
}
