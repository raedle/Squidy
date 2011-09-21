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
import org.squidy.manager.util.CloneUtility;

import wiigee.logic.GestureType;

/**
 * <code>DataGesture</code>.
 * 
 * <pre>
 * Date: Feb 14, 2008
 * Time: 9:32:35 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DataGesture.java 772 2011-09-16 15:39:44Z raedle $$
 */
@DataType(color = {0x5b, 0x92, 0xb5, 0xff})
public class DataGesture<T extends DataGesture> extends DataString<T> {

	public int typeId;
	
	protected String gestureName;

	public boolean classified;

	public static int numGestures;
	
	public GestureType recognizedType = GestureType.VOID;
	
	public DataGesture(Class<? extends IProcessable<?>> source, String gestureName, int typeId, GestureType recognizedType, boolean classified) {
		super(source, gestureName);

		this.typeId = typeId;
		this.classified = classified;
		this.recognizedType = recognizedType;
	}

	public T getClone() {
		T clone = super.getClone();
		clone.gestureName = gestureName;
		clone.typeId = typeId;
		clone.recognizedType = recognizedType;
		clone.classified = classified;
		
		return clone;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#deserialize(java.lang.String[])
	 */
	public void deserialize(String[] serial) {
		super.deserialize(serial);
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#serialize()
	 */
	public Object[] serialize() {
		List<Object> serial = new ArrayList<Object>(Arrays.asList(super.serialize()));
		
		serial.add(typeId);
		serial.add(classified);
		serial.add(recognizedType);
		
		return serial.toArray();
	}

	public String toString() {
		return new String(this.getClass() + " " + source + " " + timestamp + " " + typeId + " " + gestureName);
	}
	public void setName( String aName)
	{
		this.gestureName = aName;
	}
	public String getName()	{
		return this.gestureName;
	}

}
