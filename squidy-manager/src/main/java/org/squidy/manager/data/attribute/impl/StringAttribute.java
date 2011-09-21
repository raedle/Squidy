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

package org.squidy.manager.data.attribute.impl;

import org.squidy.manager.data.attribute.IDataAttribute;

/**
 * <code>StringAttribute</code>.
 *
 * <pre>
 * Date: Nov 8, 2008
 * Time: 12:22:36 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: StringAttribute.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class StringAttribute implements IDataAttribute<StringAttribute, String> {

	private String value;
	
	public StringAttribute(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.attribute.IDataAttribute#getValue()
	 */
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.attribute.IDataAttribute#getClone()
	 */
	public StringAttribute getClone() {
		return new StringAttribute(new String(value));
	}
}