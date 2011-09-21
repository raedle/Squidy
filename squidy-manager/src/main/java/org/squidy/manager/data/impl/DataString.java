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
 * <code>DataString</code>.
 * 
 * <pre>
 * Date: Feb 14, 2008
 * Time: 9:35:00 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DataString.java 772 2011-09-16 15:39:44Z raedle $$
 */
@DataType(color = {0x00, 0x71, 0xb8, 0xff})
public class DataString<T extends DataString> extends DataObject<T> {

	@FineGrain
	public String data;

	/**
	 * @return the data
	 */
	public final String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public final void setData(String data) {
		this.data = data;
	}
	
	public DataString() {
		// empty
	}

	/**
	 * @param source
	 * @param data
	 */
	public DataString(Class<? extends IProcessable<?>> source, String data) {
		super(source);

		this.data = data;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataObject#getClone()
	 */
	public T getClone() {
		T clone = super.getClone();
		clone.data = data;
		
		return clone;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataObject#deserialize(java.lang.Object[])
	 */
	@Override
	public void deserialize(Object[] serial) {
		super.deserialize(serial);
		data = (String) serial[3];
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataObject#serialize()
	 */
	@Override
	public Object[] serialize() {
		List<Object> serial = new ArrayList<Object>(Arrays.asList(super.serialize()));
		
		serial.add(data);
		
		return serial.toArray();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataObject#toString()
	 */
	@Override
	public String toString() {
		return new String(this.getClass() + " " + source + " " + timestamp + " " + data);
	}
}
