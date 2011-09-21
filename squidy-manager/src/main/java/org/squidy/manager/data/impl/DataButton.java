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
 * <code>DataButton</code>.
 * 
 * <pre>
 * Date: Feb 14, 2008
 * Time: 9:12:42 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DataButton.java 772 2011-09-16 15:39:44Z raedle $*/
@DataType(color = {0xfe, 0xff, 0x80, 0xff})
public class DataButton<T extends DataButton> extends DataDigital<T> {

	public static final int BUTTON_0 = 0;
	public static final int BUTTON_1 = 1;
	public static final int BUTTON_2 = 2;
	public static final int BUTTON_3 = 3;
	public static final int BUTTON_4 = 4;
	public static final int BUTTON_5 = 5;
	public static final int BUTTON_6 = 6;
	public static final int BUTTON_7 = 7;
	public static final int BUTTON_8 = 8;
	public static final int BUTTON_9 = 9;
	public static final int BUTTON_10 = 10;
	public static final int BUTTON_11 = 11;
	public static final int BUTTON_A = 12;
	public static final int BUTTON_B = 13;
	public static final int BUTTON_PLUS = 14;
	public static final int BUTTON_STICK_UP = 15;
	public static final int BUTTON_STICK_DOWN = 16;
	public static final int BUTTON_STICK_LEFT = 17;
	public static final int BUTTON_STICK_RIGHT = 18;

	// Indicates the pressed button.
	@FineGrain
	private int buttonType;

	/**
	 * @return the buttonType
	 */
	public final int getButtonType() {
		return buttonType;
	}

	/**
	 * @param buttonType the buttonType to set
	 */
	public final void setButtonType(int buttonType) {
		this.buttonType = buttonType;
	}

	/**
	 * The default constructor is required to deserialize data
	 * types.
	 */
	public DataButton() {
		// empty
	}
	
	public DataButton(Class<? extends IProcessable<?>> source, int buttonType, boolean flag) {
		super(source, flag);

		this.buttonType = buttonType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.data.impl.DataDigital#getClone()
	 */
	public T getClone() {
		T clone = super.getClone();
		clone.buttonType = buttonType;
		
		return clone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.data.impl.DataDigital#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("[buttonType=").append(buttonType).append("]");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#deserialize(java.lang.String[])
	 */
	public void deserialize(Object[] serial) {
		super.deserialize(serial);
		
		buttonType = (Integer) serial[4];
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#serialize()
	 */
	public Object[] serialize() {
		List<Object> serial = new ArrayList<Object>(Arrays.asList(super.serialize()));
		
		serial.add(buttonType);
		
		return serial.toArray();
	}
}
