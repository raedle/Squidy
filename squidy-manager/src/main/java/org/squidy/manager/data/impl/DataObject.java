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

import org.squidy.common.util.ReflectionUtil;
import org.squidy.manager.IProcessable;
import org.squidy.manager.data.AbstractData;
import org.squidy.manager.util.CloneUtility;


/**
 * <code>DataObject</code>.
 * 
 * <pre>
 * Date: Feb 13, 2008
 * Time: 6:52:36 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DataObject.java 772 2011-09-16 15:39:44Z raedle $$
 */
public class DataObject<T extends DataObject> extends AbstractData<T> {

	/**
	 * The default constructor is required to deserialize data
	 * types.
	 */
	public DataObject() {
		// empty
	}
	
	/**
	 * @param source
	 * @param identifier
	 */
	public DataObject(Class<? extends IProcessable<?>> source) {
		super(source);
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#getClone()
	 */
	public T getClone() {
		T clone = (T) ReflectionUtil.createInstance(getClass());
		clone.source = source;
		clone.timestamp = timestamp;
		clone.original = (T) (this.original == null ? this : this.original);
		
		// TODO [RR]: Add an interface to the IData class that enables cloning. This cast can cause crashes if the data
		// object is not within the DataObject class hierarchy.
		((DataObject) clone.original).addClone(clone);
		
		clone.attributes = CloneUtility.getDeepClone(attributes);
		
		return clone;
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.data.AbstractData#deserialize(java.lang.Object[])
	 */
	public void deserialize(Object[] serial) {
		super.deserialize(serial);
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#serialize()
	 */
	public Object[] serialize() {
		return super.serialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ukn.hci.interaction.manager.data.AbstractData#toString()
	 */
	@Override
	public String toString() {
		return super.toString();
	}
}
