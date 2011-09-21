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

import java.util.Date;

/**
 * <code>DataContainer</code>.
 *
 * <pre>
 * Date: Apr 15, 2008
 * Time: 10:45:34 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: IDataContainer.java 772 2011-09-16 15:39:44Z raedle $$
 *
 * $Id: IDataContainer.java 772 2011-09-16 15:39:44Z raedle $
 */
public interface IDataContainer {
	long getTimestamp();
	void setTimestamp(long date);
	IData[] getData();
	void setData(IData[] data);
	IDataContainer getClone();
	
	public void setAttribute(DataConstant dataConstant, Object value);
	
	public Object getAttribute(DataConstant dataConstant);
	
	public boolean hasAttribute(DataConstant dataConstant);
}
