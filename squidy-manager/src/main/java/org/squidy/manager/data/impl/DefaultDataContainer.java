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

package org.squidy.manager.data.impl;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.util.DataUtility;


/**
 * <code>DefaultDataContainer</code>.
 *
 * <pre>
 * Date: Apr 15, 2008
 * Time: 10:47:20 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DefaultDataContainer.java 772 2011-09-16 15:39:44Z raedle $$
 *
 * $Id: DefaultDataContainer.java 772 2011-09-16 15:39:44Z raedle $
 */
public class DefaultDataContainer implements IDataContainer {

	// The attributes map allows to store any kind of attribute to this specific
	// data type.
	protected Map<DataConstant, Object> attributes;

	/**
	 * @param dataConstant
	 * @param value
	 */
	public void setAttribute(DataConstant dataConstant, Object value) {
		if (attributes == null) {
//			attributes = Collections.synchronizedMap(new HashMap<DataConstant, Object>());
			attributes = new ConcurrentHashMap<DataConstant, Object>();
		}

		// Security check.
		if (value == null) {
			return;
		}

		// Security check that prevents values of unsupported type.
		if (!dataConstant.getType().isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException("Required type " + dataConstant.getType().getName()
					+ " of DataConstant doesn't match value type " + value.getClass().getName());
		}

		attributes.put(dataConstant, value);
	}

	/**
	 * @param dataConstant
	 * @return
	 */
	public Object getAttribute(DataConstant dataConstant) {
		if (attributes == null) {
			return null;
		}

		return attributes.get(dataConstant);
	}
	
	public boolean hasAttribute(DataConstant dataConstant) {
		if (attributes == null) {
			return false;
		}
		return attributes.containsKey(dataConstant);
	}
	
	/**
	 * 
	 */
	public DefaultDataContainer() {
		timestamp = System.currentTimeMillis();
	}
	
	private long timestamp;
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IDataContainer#getTimestamp()
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IDataContainer#setTimestamp()
	 */
	public void setTimestamp(long date) {
		timestamp = date;
	}

	private IData[] data;
	
	public DefaultDataContainer(IData[] data) {
		this();
		this.data = data;
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.data.DataContainer#getData()
	 */
	public IData[] getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IDataContainer#setData(org.squidy.manager.data.IData[])
	 */
	public void setData(IData[] data) {
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IDataContainer#getClone()
	 */
	public IDataContainer getClone() {
		return new DefaultDataContainer(DataUtility.getClones(data));
	}
}
