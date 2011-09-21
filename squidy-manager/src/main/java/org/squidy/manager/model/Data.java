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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.squidy.Namespaces;


/**
 * <code>XMI</code>.
 * 
 * <pre>
 * Date: Feb 16, 2008
 * Time: 11:11:44 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>,, University of Konstanz
 * @version $Id: Data.java 772 2011-09-16 15:39:44Z raedle $$
 * 
 */
@XmlRootElement(name = "Data", namespace = Namespaces.NAMESPACE_PREFIX_COMMON)
public class Data implements ModelData {

	@XmlElement(name = "workspace", namespace = Namespaces.NAMESPACE_PREFIX_MANAGER)
	private Workspace workspace = new Workspace();

	/**
	 * @return the workspace
	 */
	public final Workspace getWorkspace() {
		return workspace;
	}

	/**
	 * @param workspace
	 *            the workspace to set
	 */
	public final void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Data=[Workspace=");
		sb.append(workspace);
		sb.append("]");

		return sb.toString();
	}
}
