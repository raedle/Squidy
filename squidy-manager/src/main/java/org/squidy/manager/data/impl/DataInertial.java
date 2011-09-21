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
 * <code>DataInertial</code>.
 * 
 * <pre>
 * Date: Feb 14, 2008
 * Time: 9:36:46 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DataInertial.java 772 2011-09-16 15:39:44Z raedle $$
 */
@DataType(color = {0x70, 0xa0, 0x55, 0xff})
public class DataInertial<T extends DataInertial> extends DataAnalog<T> {
	protected double raw_a = 0;
	protected double raw_b = 0;
	protected double raw_c = 0;
	protected float a = 0;
	protected float b = 0;
	protected float c = 0;
	protected double absoluteValue = 0;
	protected boolean normalize = true;

//	// laserpointer values
//	double a0 = 534;
//	double a2 = 344;
//	double b0 = 527;
//	double b3 = 352;
//	double c0 = 552;
//	double c1 = 359;
	
	
//	x0 = (x1+x2)/2
//	y0 = (y1+y3)/2
//	z0 = (z2+z3)/2
	
//	//liegend
//	x1=1249
//	y1=1226
//	z1=764
//	//IR up
//	x2=1249
//	y2=760 (1712 ir down)
//	z2=1242
//	//left side up
//	x3=1708
//	y3=1235
//	z3=1252
	
	
/////// iPhone/////////
// liegend
//	x1=0.496
//	y1=0.5167
//	z1=0.27
//	//IR up
//	x2=0.49
//	y2=0.724433
//	z2=0.5
//	//left side up
//	x3=0.27
//	y3=0.519
//	z3=0.496
	
	
	
//	// laserpointer values
//	double a0 = 1249; .5
//	double a3 = 1708; .7
//	double b0 = 1230; .5
//	double b2 = 760;  .25
//	double c0 = 1247; .5
//	double c1 = 764;  .25

	// iphone values
	double a0 = 0.5; 
	double a3 = 0.75;
	double b0 = 0.5;
	double b2 = 0.25;
	double c0 = 0.5;
	double c1 = 0.25;
	
	private boolean laserpointerHistory = false;
	
	/**
	 * 
	 */
	public DataInertial() {
		// empty
	}
	
	public DataInertial(Class<? extends IProcessable<?>> source, float x, float y, float z) {
		this.a = x;
		this.b = y;
		this.c = z;
		
		this.absoluteValue = Math.sqrt((x * x) + (y * y) + (z * z));
	}

	@Deprecated
	public DataInertial(Class<? extends IProcessable<?>> source, double raw_a, double raw_b,
			double raw_c, boolean normalize) {
		super(source, 0);
		
		this.raw_a = raw_a;
		this.raw_b = raw_b;
		this.raw_c = raw_c;
		this.normalize = normalize;

		// LAserpointer
		// double raw_a = Integer.parseInt(toker.nextToken(),16); //Sensor_Z,
		// Wii_-Y
		// double raw_b = Integer.parseInt(toker.nextToken(),16); //Sensor_X,
		// Wii_-Z
		// double raw_c = Integer.parseInt(toker.nextToken(),16); //Sensor_Y,
		// Wii_X

		// sort coordinate system
		if(laserpointerHistory){
			a = (float) raw_b;
			b = (float) raw_c;
			c = (float) raw_a;
		} else {
			a = (float) raw_a;
			b = (float) raw_b;
			c = (float) raw_c;
		}

		// System.out.println(a+" "+b+" "+c+" "+absoluteValue);

		// normalize
		if(normalize){
			a = (float) ((a - a0) / (a3 - a0));
			b = (float) ((b - b0) / (b2 - b0));
			c = (float) ((c - c0) / (c1 - c0));
		}

		this.absoluteValue = Math.sqrt((a * a) + (b * b) + (c * c));

		// System.out.println(raw_a+" "+raw_b+" "+raw_c+" "+absoluteValue);
		// System.out.println(a+" "+b+" "+c+" "+absoluteValue);
		// System.out.println(getX()+"; "+getY()+"; "+getZ());
	}

	/**
	 * @return
	 */
	public double getX() {
		if (laserpointerHistory)
			return b;
		return a;
	}

	/**
	 * @return
	 */
	public double getY() {
		if (laserpointerHistory)
			return -1 * c;
		return b;
	}

	/**
	 * @return
	 */
	public double getZ() {
		if (laserpointerHistory)
			return -1 * a;
		return c;
	}

	/**
	 * @return
	 */
	public double getAbsoluteValue() {
		return absoluteValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.data.impl.DataObject#getClone()
	 */
	public T getClone() {
		T clone = super.getClone();
		clone.a = a;
		clone.b = b;
		clone.c = c;
		
		return clone;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataAnalog#deserialize(java.lang.Object[])
	 */
	@Override
	public void deserialize(Object[] serial) {
		super.deserialize(serial);
		
		this.a = (Float) serial[4];
		this.b = (Float) serial[5];
		this.c = (Float) serial[6];
		this.absoluteValue = (Double) serial[7];
		normalize = (Boolean) serial[8];
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.impl.DataObject#serialize()
	 */
	@Override
	public Object[] serialize() {
		List<Object> serial = new ArrayList<Object>(Arrays.asList(super.serialize()));
		
		serial.add(a);
		serial.add(b);
		serial.add(c);
		serial.add(absoluteValue);
		serial.add(normalize);
		
		return serial.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.data.impl.DataObject#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("[a=").append(a).append(", b=").append(b).append(", c=").append(c).append(", absolute=").append(absoluteValue).append("]");
		return sb.toString();
	}
}
