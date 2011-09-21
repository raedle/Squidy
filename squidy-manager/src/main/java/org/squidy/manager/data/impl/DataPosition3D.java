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
 * <code>DataPosition3D</code>.
 * 
 * <pre>
 * Date: Feb 14, 2008
 * Time: 9:34:33 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DataPosition3D.java 772 2011-09-16 15:39:44Z raedle $$
 */
@DataType(color = {0xff, 0x40, 0x40, 0xff})
public class DataPosition3D<T extends DataPosition3D> extends DataPosition2D<T> {

	// z-coordinate
	@FineGrain
	protected double z;

	/**
	 * @return the z
	 */
	public double getZ() {

		
		return z;
	}

	/**
	 * @param z the z to set
	 */
	public void setZ(double z) {
		this.z = z;
	}
	
	@FineGrain
	protected int groupID = -1;
	public int getGroupID()
	{
		return groupID;
	}
	public void setGroupID( int aGroupID)
	{
		this.groupID = aGroupID;
	}

	public DataPosition3D() {
		// empty
	}
	
	public DataPosition3D(Class<? extends IProcessable<?>> source, double x, double y, double z) {
		super(source, x, y);

		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Deprecated
	public DataPosition3D(Class<? extends IProcessable<?>> source, double x, double y, double z, int groupID) {
		this(source, x, y, z);
		this.groupID = groupID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ukn.hci.interaction.manager.data.impl.DataObject#getClone()
	 */
	public T getClone() {
		T clone = super.getClone();
		clone.z = z;
		clone.groupID = groupID;
		
		return clone;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataPosition2D#deserialize(java.lang.Object[])
	 */
	@Override
	public void deserialize(Object[] serial) {
		super.deserialize(serial);
		
		z = (Float) serial[5];
		groupID = (Integer) serial[6];
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataObject#serialize()
	 */
	@Override
	public Object[] serialize() {
		List<Object> serial = new ArrayList<Object>(Arrays.asList(super.serialize()));
		
		serial.add(((Double) z).floatValue());
		serial.add(groupID);
		return serial.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ukn.hci.interaction.manager.data.impl.DataObject#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("[x=").append(x).append(" | y=").append(y).append(" | z=").append(z).append("]");
		sb.append("[groupID=").append(groupID).append("]");

		return sb.toString();
	}
	public String getPosition()
	{
		return x+"\t"+y+"\t"+z;
	}
}
