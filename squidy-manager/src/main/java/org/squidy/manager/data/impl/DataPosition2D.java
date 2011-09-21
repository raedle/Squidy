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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.squidy.manager.IProcessable;
import org.squidy.manager.data.DataType;
import org.squidy.manager.data.FineGrain;
import org.squidy.manager.util.CloneUtility;

/**
 * <code>DataPosition2D</code>.
 * 
 * <pre>
 * Date: Feb 13, 2008
 * Time: 7:55:47 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DataPosition2D.java 772 2011-09-16 15:39:44Z raedle $$
 */
@DataType(color = {0xff, 0x00, 0x00, 0xff})
public class DataPosition2D<T extends DataPosition2D> extends DataObject<T> {

	@FineGrain
	protected double x;
	
	/**
	 * @return the x
	 */
	public double getX() {

		
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	@FineGrain
	protected double y;

	/**
	 * @return the y
	 */
	public double getY() {
		
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * The default constructor is required to deserialize data
	 * types.
	 */
	public DataPosition2D() {
		// empty
	}

	public DataPosition2D(Class<? extends IProcessable<?>> source, double x, double y) {
		super(source);

		this.x = x;
		this.y = y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ukn.hci.interaction.manager.data.impl.DataObject#getClone()
	 */
	@Override
	public T getClone() {
	    T clone = super.getClone();
	    clone.x = x;
	    clone.y = y;
		
		return clone;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#deserialize(java.lang.Object[])
	 */
	public void deserialize(Object[] serial) {
		super.deserialize(serial);
		
		x = (Float) serial[3];
		y = (Float) serial[4];
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#serialize()
	 */
	public Object[] serialize() {
		List<Object> serial = new ArrayList<Object>(Arrays.asList(super.serialize()));
		
		serial.add(((Double) x).floatValue());
		serial.add(((Double) y).floatValue());
		
		return serial.toArray();
	}
	
	/**
	 * @param dataPosition2D
	 * @return
	 */
	public double distance(DataPosition2D dataPosition2D) {
		Point2D thisPoint = new Point2D.Double(x, y);
		return thisPoint.distance(new Point2D.Double(dataPosition2D.getX(), dataPosition2D.getY()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ukn.hci.interaction.manager.data.AbstractData#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("[x=").append(x).append("][y=").append(y).append("]");

		return sb.toString();
	}
}
