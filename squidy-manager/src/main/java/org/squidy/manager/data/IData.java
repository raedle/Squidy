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

package org.squidy.manager.data;

import org.squidy.manager.IProcessable;

/**
 * <code>DataObject</code>.
 * 
 * <pre>
 * Date: Feb 2, 2008
 * Time: 2:54:42 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: IData.java 772 2011-09-16 15:39:44Z raedle $$
 */
@DataType(color = {0xc6, 0xc6, 0xc6, 0xff})
public interface IData<T extends IData> {

	public enum Type {
		ACCELERATION(13), ANALOG(4), BUTTON(6), DIGITAL(5), FINGER(7), GESTURE(10), GLOVE(9), HAND(8), INERTIAL(11), KEY(
				12), OBJECT(-1), POSITION2D(0), POSITION3D(1), POSITION6D(2), STRING(3), VOID(-2);

		public final int typeInt;

		Type(int typeInt) {
			this.typeInt = typeInt;
		}

		public int getTypeInt() {
			return typeInt;
		}
	}

	public Class<? extends IProcessable<?>> getSource();
	
	public long getTimestamp();
	
	public void setTimestamp(long timestamp);

	/**
	 * Returns the original data object or null if this is the original
	 * data object already.
	 * 
	 * @return The orignial data object or null.
	 */
	public T getOriginal();

	public T getClone();
	
	/**
	 * Returns the clones of the original data types.
	 * 
	 * @return The cloned data objects originating from the original data
	 * object.
	 */
	public T[] getClones();
	
	/**
	 * Removes this and all clones originating of this data object from 
	 * processing chain.
	 */
	public void killAll();

	public Object[] serialize();

	public void deserialize(Object[] serial);
	
	public void setAttribute(DataConstant dataConstant, Object value);
	
	public Object getAttribute(DataConstant dataConstant);
	
	public boolean hasAttribute(DataConstant dataConstant);

	public boolean acceptVisitor(IDataVisitor visitor);
		
	public boolean dismissVisitor(IDataVisitor visitor);

	public void notifyVisitors(IProcessable<?> processable);
}
