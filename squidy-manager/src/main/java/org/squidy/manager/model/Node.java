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
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.Issuable;
import org.squidy.manager.Issue;


/**
 * <code>Valve</code>.
 * 
 * <pre>
 * Date: Feb 1, 2008
 * Time: 11:13:49 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: Node.java 772 2011-09-16 15:39:44Z raedle $$
 */
public abstract class Node extends Piping {
	
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Node.class);
	
	// #############################################################################
	// BEGIN JAXB
	// #############################################################################
	
	/**
	 * Default constructor required for JAXB.
	 */
	public Node() {
		// empty
	}
	
	// #############################################################################
	// END JAXB
	// #############################################################################
	
	private Issuable issuable;
	
	/**
	 * @return the issuable
	 */
	public final Issuable getIssuable() {
		return issuable;
	}

	/**
	 * @param issuable the issuable to set
	 */
	public final void setIssuable(Issuable issuable) {
		this.issuable = issuable;
	}

	private List<Issue> issues;

	/**
	 * @return
	 */
	public List<Issue> getIssues() {
		return issues;
	}
	
	/**
	 * @param issue
	 */
	public void addIssue(Issue issue) {
		if (issues == null) {
			issues = new ArrayList<Issue>();
		}
		issues.add(issue);
		
		releaseIfIssue();
	}
	
	/**
	 * @param issue
	 */
	public void removeIssue(Issue issue) {
		issues.remove(issue);
		
		releaseIfIssue();
	}
	
	/**
	 * @return
	 */
	public boolean hasIssues() {
		return issues != null && issues.size() > 0;
	}
	
	/**
	 * 
	 */
	private void releaseIfIssue() {
		if (issuable != null) {
			if (hasIssues()) {
				issuable.problematic(issues.toArray(new Issue[0]));
			}
			else {
				issuable.smooth();
			}
		}
		else if (hasIssues()) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Valve has issues but no issuable has been set to receive issues.");
			}
		}
	}

//	/* (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append("Node=[name=");
//		sb.append(get)
//		sb.append("]");
//		
//		return sb.toString();
//	}
}
