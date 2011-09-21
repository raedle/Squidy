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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.squidy.manager.IProcessable;
import org.squidy.manager.data.DataType;
import org.squidy.manager.data.FineGrain;
import org.squidy.manager.util.CloneUtility;

/**
 * <code>DataAnalog</code>.
 * 
 * <pre>
 * Date: Feb 13, 2008
 * Time: 7:55:47 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DataAnalog.java 772 2011-09-16 15:39:44Z raedle $*/
@DataType(color = {0x4d, 0xa7, 0x00, 0xFF})
public class DataAnalog<T extends DataAnalog> extends DataObject<T> {

	@FineGrain
	protected double value;
	
	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}
	
	/**
	 * 
	 */
	public DataAnalog() {
		// empty
	}
	
	public DataAnalog(Class<? extends IProcessable<?>> source, double value) {
		super(source);

		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ukn.hci.interaction.manager.data.impl.DataObject#getClone()
	 */
	@Override
	public T getClone() {
		T clone = super.getClone();
		clone.value = value;
		
		return clone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ukn.hci.interaction.manager.data.AbstractData#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("[value=").append(value).append("]");

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataObject#deserialize(java.lang.Object[])
	 */
	public void deserialize(Object[] serial) {
		super.deserialize(serial);
		
		value = (Float) serial[3];
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#serialize()
	 */
	public Object[] serialize() {
		List<Object> serial = new ArrayList<Object>(Arrays.asList(super.serialize()));
		
		serial.add(value);
		
		return serial.toArray();
	}
}
