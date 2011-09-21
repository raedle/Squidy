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
 * <code>DataKey</code>.
 * 
 * <pre>
 * Date: Feb 13, 2008
 * Time: 6:44:42 PM
 * </pre>
 *
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,, University of Konstanz
 * @version $Id: DataKey.java 772 2011-09-16 15:39:44Z raedle $$
 *
 */
//@DataType(color = {0xfe, 0xff, 0x80, 0xff})
@Deprecated
public class DataKey<T extends DataKey> extends DataDigital<T> {

	// Identifies the type of the key.
	@FineGrain
	protected int keyType;

	/**
	 * @return the keyType
	 */
	public int getKeyType() {
		return keyType;
	}

	/**
	 * @param keyType the keyType to set
	 */
	public void setKeyType(int keyType) {
		this.keyType = keyType;
	}
	
	@FineGrain
	protected int keyStroke;
	
	/**
	 * @return the keyStroke
	 */
	public final int getKeyStroke() {
		return keyStroke;
	}

	/**
	 * @param keyStroke the keyStroke to set
	 */
	public final void setKeyStroke(int keyStroke) {
		this.keyStroke = keyStroke;
	}
	
	public DataKey() {
		
	}
	
	/**
	 * @param source
	 * @param identifier
	 */
	public DataKey(Class<? extends IProcessable<?>> source, int keyType, boolean flag) {
		super(source, flag);
		
		this.keyType = keyType;
	}
	
	/**
	 * @param source
	 * @param identifier
	 */
	public DataKey(Class<? extends IProcessable<?>> source, int keyType, int keyStroke, boolean flag) {
		super(source, flag);
		
		this.keyType = keyType;
		this.keyStroke = keyStroke;
	}

	/* (non-Javadoc)
	 * @see de.ukn.hci.interaction.manager.data.IData#getClone()
	 */
	public T getClone() {
		T clone = super.getClone();
		clone.keyType = keyType;
		
		return clone;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataDigital#deserialize(java.lang.Object[])
	 */
	@Override
	public void deserialize(Object[] serial) {
		super.deserialize(serial);
		keyType = (Integer) serial[4];
		keyStroke = (Integer) serial[5];
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataDigital#serialize()
	 */
	@Override
	public Object[] serialize() {
		List<Object> serial = new ArrayList<Object>(Arrays.asList(super.serialize()));
		
		serial.add(keyType);
		serial.add(keyStroke);
		
		return serial.toArray();
	}

	/* (non-Javadoc)
	 * @see de.ukn.hci.interaction.manager.data.impl.DataDigital#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("[keyType=").append(keyType).append("]");
		return sb.toString();
	}
}
