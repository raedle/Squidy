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


/**
 * <code>DataPosition6D</code>.
 * 
 * <pre>
 * Date: Feb 14, 2008
 * Time: 9:34:09 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DataPosition6D.java 772 2011-09-16 15:39:44Z raedle $$
 */
@DataType(color = {0xff, 0x80, 0x80, 0xff})
public class DataPosition6D<T extends DataPosition6D> extends DataPosition3D<T> {

	// rotation matrix
	//public double rxx, ryx, rzx, rxy, ryy, rzy, rxz, ryz, rzz;
	private double m00,m01,m02,m10,m11,m12,m20,m21,m22;
	private double yaw, pitch, roll;

	public DataPosition6D() {
		// empty
	}
	
	public DataPosition6D(Class<? extends IProcessable<?>> source, double x, double y, double z,
			double m00, double m01, double m02, double m10, double m11,
			double m12, double m20, double m21, double m22, double yaw, double pitch, double roll, int groupID) {
		super(source, x, y, z, groupID);

		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		
	}
	
	public DataPosition6D(Class<? extends IProcessable <?>> source, DataPosition3D d3d, double m00, double m01, double m02, double m10, double m11,
			double m12, double m20, double m21, double m22, double yaw, double pitch, double roll)
	{
		super(source,d3d.getX(),d3d.getY(),d3d.getZ(),d3d.getGroupID());
		
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.yaw = yaw;
		this.pitch = pitch;

	}

	public T getClone() {
		T clone = super.getClone();
		clone.m00 = m00;
		clone.m01 = m01;
		clone.m02 = m02;
		clone.m10 = m10;
		clone.m11 = m11;
		clone.m12 = m12;
		clone.m20 = m20;
		clone.m21 = m21;
		clone.m22 = m22;
		clone.yaw = yaw;
		clone.pitch = pitch;
		
		return clone;
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataPosition2D#deserialize(java.lang.Object[])
	 */
	@Override
	public void deserialize(Object[] serial) {
		super.deserialize(serial);
		m00 = (Float) serial[7];
		m01 = (Float) serial[8];
		m02 = (Float) serial[9];
		m10 = (Float) serial[10];
		m11 = (Float) serial[11];
		m12 = (Float) serial[12];
		m20 = (Float) serial[13];
		m21 = (Float) serial[14];
		m22 = (Float) serial[15];
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataPosition3D#serialize()
	 */
	@Override
	public Object[] serialize() {
		List<Object> serial = new ArrayList<Object>(Arrays.asList(super.serialize()));
		
		serial.add(((Double) m00).floatValue());
		serial.add(((Double) m01).floatValue());
		serial.add(((Double) m02).floatValue());
		serial.add(((Double) m10).floatValue());
		serial.add(((Double) m11).floatValue());
		serial.add(((Double) m12).floatValue());
		serial.add(((Double) m20).floatValue());
		serial.add(((Double) m21).floatValue());
		serial.add(((Double) m22).floatValue());
		
		return serial.toArray();
	}

	public String toString() {
		return new String(this.getClass() + " " + source + " " + timestamp + " " + x + " " + y + " "
				+ z + " " + m00 + " " + m01 + " " + m02 + " " + m10 + " " + m11 + " " + m12 + " " + m20 + " " + m21
				+ " " + m22);
	}
	public String getPosition()
	{
		return super.getPosition();
	}
	
	// Return DCM Matrix M(row)(column)
	public double getM00()
	{
		return this.m00;		
	}
	public double getM01()
	{
		return this.m01;		
	}	
	public double getM02()
	{
		return this.m02;		
	}
	public double getM10()
	{
		return this.m10;		
	}
	public double getM11()
	{
		return this.m11;		
	}	
	public double getM12()
	{
		return this.m12;		
	}
	public double getM20()
	{
		return this.m20;		
	}
	public double getM21()
	{
		return this.m21;		
	}	
	public double getM22()
	{
		return this.m22;		
	}
	public double getYaw()
	{
		return this.yaw;
	}
	public double getPitch()
	{
		return this.pitch;
	}
	public double getRoll()
	{
		return this.roll;
	}
	
	public void setM01( double m )
	{
		this.m01 = m;
	}
	public void setM02( double m )
	{
		this.m02 = m;
	}
	public void setM10( double m )
	{
		this.m10 = m;
	}
	public void setM11( double m )
	{
		this.m11 = m;
	}
	public void setM12( double m )
	{
		this.m12 = m;
	}
	public void setM20( double m )
	{
		this.m20 = m;
	}
	public void setM21( double m )
	{
		this.m21 = m;
	}
	public void setM22( double m )
	{
		this.m22 = m;
	}
	public void setM00( double m )
	{
		this.m00 = m;
	}
	public void setYaw( double y)
	{
		this.yaw = y;
	}
	public void setPitch( double p)
	{
		this.pitch = p;
	}
	public void setRoll( double r )
	{
		this.roll = r;
	}
	
	
	// DCM Transposed Matrix
	/*public double getMT00()
	{
		return this.rxx;		
	}
	public double getMT01()
	{
		return this.ryx;		
	}	
	public double getMT02()
	{
		return this.ryz;		
	}
	public double getMT10()
	{
		return this.rxy;		
	}
	public double getMT11()
	{
		return this.ryy;		
	}	
	public double getMT12()
	{
		return this.rzy;		
	}
	public double getMT20()
	{
		return this.rxz;		
	}
	public double getMT21()
	{
		return this.ryz;		
	}	
	public double getMT22()
	{
		return this.rzz;		
	}	*/
	
	
}
