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

package org.squidy.manager.controls;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import org.squidy.manager.IBasicControl;
import org.squidy.manager.PropertyUpdateListener;

import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * <code>AbstractBasicControl</code>.
 * 
 * <pre>
 * Date: Mar 22, 2009
 * Time: 2:54:16 PM
 * </pre>
 * 
 * @author Roman R&amp;aumldle<br />
 *         <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 *         Human-Computer Interaction Group<br />
 *         University of Konstanz
 * 
 * @version $Id: AbstractBasicControl.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public abstract class AbstractBasicControl<T, C extends JComponent> implements IBasicControl<T, C> {

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private final EventListenerList listenerList = new EventListenerList();

	private boolean allowFirePropertyUpdate = true;
	
	private C component;

	/**
	 * @param component
	 */
	public AbstractBasicControl(C component) {
		this.component = component;
		reconcileComponent();
	}

	
	public void customPInputEvent(PInputEvent event) {
		// do something
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.components.basiccontrols.IBasicControl#
	 * getComponent()
	 */
	public C getComponent() {
		return component;
	}
	
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.components.basiccontrols.IBasicControl#
	 * addPropertyUpdateListener
	 * (org.squidy.designer.components.PropertyUpdateListener)
	 */
	public void addPropertyUpdateListener(PropertyUpdateListener<T> listener) {
		listenerList.add(PropertyUpdateListener.class, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.components.basiccontrols.IBasicControl#
	 * removePropertyUpdateListener
	 * (org.squidy.designer.components.PropertyUpdateListener)
	 */
	public void removePropertyUpdateListener(PropertyUpdateListener<T> listener) {
		listenerList.remove(PropertyUpdateListener.class, listener);
	}

	// public abstract register

	/**
	 * 
	 */
	protected abstract void reconcileComponent();
	
	/**
	 * If this method will be used the value will be set on the component
	 * without releasing any property update events.
	 * 
	 * @param value The new value of the component.
	 */
	public void setValueWithoutPropertyUpdate(T value) {
		allowFirePropertyUpdate = false;
		setValue(value);
		allowFirePropertyUpdate = true;
	}

	/**
	 * @param value
	 */
	protected void firePropertyUpdateEvent(Object value) {
		// DO NOT FIRE EVENTS IF NOT ALLOWD.
		if (!allowFirePropertyUpdate) {
			return;
		}
		
		PropertyUpdateListener<Object>[] listeners = listenerList.getListeners(PropertyUpdateListener.class);
		for (PropertyUpdateListener<Object> listener : listeners) {
			listener.propertyUpdate(value);
		}
	}

}
