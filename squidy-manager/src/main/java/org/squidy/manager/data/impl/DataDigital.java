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
 * <code>DataDigital</code>.
 * 
 * <pre>
 * Date: Feb 14, 2008
 * Time: 9:00:55 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DataDigital.java 772 2011-09-16 15:39:44Z raedle $
 */
@DataType(color = {0xfd, 0xff, 0x00, 0xff})
public class DataDigital<T extends DataDigital> extends DataObject<T> {

	// Indicates '0' digital (false) or '1' digital (true).
	@FineGrain
	protected boolean flag;

	/**
	 * @return the flag
	 */
	public boolean getFlag() {
		return flag;
	}
	
	/**
	 * @param flag the flag to set
	 */
	public final void setFlag(boolean flag) {
		this.flag = flag;
	}

	/**
	 * The default constructor is required to deserialize data
	 * types.
	 */
	public DataDigital() {
		// empty
	}

	public DataDigital(Class<? extends IProcessable<?>> source, boolean flag) {
		super(source);

		this.flag = flag;
	}
	
	@Override
    public T getClone() {
	    T clone = super.getClone();
	    clone.flag = flag;
	        
	    return clone;
    }

    /* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#deserialize(java.lang.String[])
	 */
	public void deserialize(Object[] serial) {
		super.deserialize(serial);
		
		//flag = ((Integer) serial[3]) == 1 ? true : false;
		flag = (Boolean)serial[3];
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#serialize()
	 */
	public Object[] serialize() {
		List<Object> serial = new ArrayList<Object>(Arrays.asList(super.serialize()));
		
		//serial.add(flag ? 1 : 0);
		serial.add(flag);
		
		return serial.toArray();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("[flag=").append(flag).append("]");
		return sb.toString();
	}
}
