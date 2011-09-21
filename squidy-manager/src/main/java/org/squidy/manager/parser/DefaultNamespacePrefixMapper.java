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

package org.squidy.manager.parser;

import org.squidy.Namespaces;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;


/**
 * <code>DefaultNamespacePrefixMapper</code>.
 * 
 * <pre>
 * Date: Jul 10, 2008
 * Time: 9:24:49 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: DefaultNamespacePrefixMapper.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class DefaultNamespacePrefixMapper extends NamespacePrefixMapper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sun.xml.bind.marshaller.NamespacePrefixMapper#getPreferredPrefix(
	 * java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {

		if (Namespaces.NAMESPACE_PREFIX_COMMON.equals(namespaceUri)) {
			return "common";
		}
		else if (Namespaces.NAMESPACE_PREFIX_MANAGER.equals(namespaceUri)) {
			return "manager";
		}
		else if (Namespaces.NAMESPACE_PREFIX_DESIGNER.equals(namespaceUri)) {
			return "designer";
		}
		else if (Namespaces.NAMESPACE_PREFIX_HEURISTICS.equals(namespaceUri)) {
			return "heuristics";
		}

		return suggestion;
	}
}
