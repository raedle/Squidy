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

package org.squidy.designer.shape;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.UUID;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;

import org.squidy.database.RemoteUpdatable;
import org.squidy.database.RemoteUpdatablePool;
import org.squidy.database.RemoteUpdatableSessionProvider;
import org.squidy.database.RemoteUpdateUtil;
import org.squidy.database.Session;
import org.squidy.database.SessionFactory;
import org.squidy.database.SessionFactoryProvider;


/**
 * <code>LayoutConstraint</code>.
 * 
 * <pre>
 * Date: Feb 21, 2009
 * Time: 10:35:42 PM
 * </pre>
 * 
 * @author
 * Roman R&amp;aumldle<br />
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 * Human-Computer Interaction Group<br />
 * University of Konstanz
 * 
 * @version $Id: LayoutConstraint.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "LayoutConstraint")
public class LayoutConstraint implements RemoteUpdatable, Serializable {

	/** Generated serial version UID. */
	private static final long serialVersionUID = -4968501880235099543L;
	
	/** Property change support. */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	@XmlID
	@XmlAttribute(name = "id")
	private String id;

	public LayoutConstraint() {
		this.id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		String oldId = this.id;
		this.id = id;
		firePropertyChange("id", oldId, this.id);
		RemoteUpdatablePool.putRemoteUpdatable(this);
	}
	
	@XmlAttribute(name = "x")
	private double x = 0.0;
	
	/**
	 * @return the x
	 */
	public final double getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public final void setX(double x) {
		double oldX = this.x;
		this.x = x;
		firePropertyChange("x", oldX, this.x);
	}

	@XmlAttribute(name = "y")
	private double y = 0.0;

	/**
	 * @return the y
	 */
	public final double getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public final void setY(double y) {
		double oldY = this.y;
		this.y = y;
		firePropertyChange("y", oldY, this.y);
	}

	@XmlAttribute(name = "scale", required = false)
	private double scale = 1.0;

	/**
	 * @return the scale
	 */
	public final double getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public final void setScale(double scale) {
		double oldScale = this.scale;
		this.scale = scale;
		firePropertyChange("scale", oldScale, this.scale);
	}
	
	/**
	 * This method is called after all the properties (except IDREF) are
	 * unmarshalled for this object, but before this object is set to the parent
	 * object.
	 * 
	 * @param unmarshaller
	 * @param parent
	 */
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		if (parent instanceof PropertyChangeListener)
			addPropertyChangeListener((PropertyChangeListener) parent);
		
		prepareForRemoteUpdate();
	}
	
	private void prepareForRemoteUpdate() {
		RemoteUpdatablePool.putRemoteUpdatable(this);
		
		addPropertyChangeListener(new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				SessionFactory<? extends Session> provider = SessionFactoryProvider.getProvider();
				if (provider instanceof RemoteUpdatableSessionProvider<?>)
					((RemoteUpdatableSessionProvider<? extends Session>) provider).updateRemote(LayoutConstraint.this);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LayoutConstraint[x=" + x + ",y=" + y + ",scale=" + scale + "]";
	}
	
	/**
	 * Adds a property change listener.
	 * @param propertyName Property name.
	 * @param listener Property change listener.
	 */
	public final void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	
	/**
	 * Adds a property change listener.
	 * @param listener Property change listener.
	 */
	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	/**
	 * Removes a property change listener.
	 * @param listener Property change listener.
	 */
	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	/**
	 * Fire a property change.
	 * @param propertyName Property name.
	 * @param oldValue The property's old value.
	 * @param newValue The property's new value.
	 */
	private final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.database.RemoteUpdatable#serialize()
	 * @see RemoteUpdateUtil#createSerial(String[], Object[])
	 */
	public String serialize() {
		return RemoteUpdateUtil.createSerial(
				new String[]{ "x", "y", "scale" },
				new Object[]{ x, y, scale });
	}
	
	/**
	 * @param value
	 * @return
	 */
	public void deserialize(String serial) {
		RemoteUpdateUtil.applySerial(this, serial);
	}
}
