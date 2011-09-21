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

package org.squidy.manager;

import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

/**
 * <code>IBasicControl</code>.
 * 
 * <pre>
 * Date: Mar 22, 2009
 * Time: 2:51:27 PM
 * </pre>
 * 
 * @author
 * Daniel Weidele<br />
 * <a href="mailto:Daniel.Weidele@uni-konstanz.de">Daniel.Weidele@uni-konstanz.de</a><br />
 * Human-Computer Interaction Group<br />
 * University of Konstanz
 * @author
 * Roman R&amp;aumldle<br />
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 * Human-Computer Interaction Group<br />
 * University of Konstanz
 * 
 * @version $Id: IBasicControl.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public interface IBasicControl<T, C extends JComponent> {
	public C getComponent();
	public T getValue();
	public void setValue(T value);
	public T valueFromString(String value);
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
	public void addPropertyUpdateListener(PropertyUpdateListener<T> listener);
	public void removePropertyUpdateListener(PropertyUpdateListener<T> listener);
}
