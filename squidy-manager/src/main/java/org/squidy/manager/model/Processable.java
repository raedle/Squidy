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

package org.squidy.manager.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.IProcessable;
import org.squidy.manager.Manager;
import org.squidy.manager.ProcessException;


/**
 * <code>AbstractProcessable</code>.
 * 
 * <pre>
 * Date: Mar 14, 2009
 * Time: 1:43:46 PM
 * </pre>
 * 
 * @author
 * Roman R&amp;aumldle<br />
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 * Human-Computer Interaction Group<br />
 * University of Konstanz
 * 
 * @version $Id: Processable.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "Processable")
public abstract class Processable implements IProcessable<Processable> {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Processable.class);
	
	public enum Action {
		START, STOP, DELETE, DUPLICATE
	}
	
	// #############################################################################
	// BEGIN JAXB
	// #############################################################################
	
	/**
	 * Default constructor required for JAXB.
	 */
	public Processable() {
		this.id = UUID.randomUUID().toString();
	}
	
	@XmlID
	@XmlAttribute(name = "id")
	private String id;

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(String id) {
		String oldId = this.id;
		this.id = id;
		fireStatusChange("id", oldId, this.id);
	}
	
	@XmlIDREF
	@XmlAttribute(name = "parent")
	private Processable parent;
	
	/**
	 * @return the parent
	 */
	public Processable getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Processable parent) {
		Processable oldParent = this.parent;
		this.parent = parent;
		fireStatusChange("parent", oldParent, this.parent);
	}
	
	@XmlElement(name = "processable")
	@XmlElementWrapper(name = "sub-processables")
	private Collection<Processable> subProcessables = new ArrayList<Processable>();

	/**
	 * @return the subProcessables
	 */
	public Collection<Processable> getSubProcessables() {
		return subProcessables;
	}

	/**
	 * @param subProcessables the subProcessables to set
	 */
	public void setSubProcessables(Collection<Processable> subProcessables) {
		Collection<Processable> oldSubProcessables = this.subProcessables;
		this.subProcessables = subProcessables;
//		fireStatusChange("subProcessables", oldSubProcessables, this.subProcessables);
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.IProcessable#addSubProcessable(org.squidy.manager.IProcessable)
	 */
	public void addSubProcessable(Processable processable) {
		subProcessables.add(processable);
		processable.setParent(this);
//		fireStatusChange(STATUS_PROCESSABLE_ADDED, null, processable);
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.IProcessable#removeSubProcessable(org.squidy.manager.IProcessable)
	 */
	public void removeSubProcessable(Processable processable) {
		subProcessables.remove(processable);
//		fireStatusChange(STATUS_PROCESSABLE_DELETED, processable, null);
	}
	
	// #############################################################################
	// END JAXB
	// #############################################################################
	
	// #############################################################################
	// BEGIN ILaunchable
	// #############################################################################

	protected boolean processing = false;

	/* (non-Javadoc)
	 * @see org.squidy.manager.IProcessable#isProcessing()
	 */
	public boolean isProcessing() {
		return processing;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#start()
	 */
	public void start() throws ProcessException {
		processing = true;
		
		// Before sub-processables will be started.
		preStartSubProcessables();
		
//		if (LOG.isDebugEnabled()) {
//			LOG.debug("Starting sub-processables.");
//		}
		
		// Starting all sub-processables.
		for (Processable subProcessable : getSubProcessables()) {
			subProcessable.start();
		}
		
		// Notify the ui.
		fireStatusChange(PROPERTY_PROCESSING, false, true);
		
		for (Processable subProcessable : getSubProcessables()) {
			subProcessable.onStarted();
		}
	}
	
	/**
	 * 
	 */
	protected void onStarted() {
		// empty
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#stop()
	 */
	public void stop() throws ProcessException {
		// Stopping all sub-processables.
		for (Processable subProcessable : getSubProcessables()) {
			subProcessable.stop();
		}
		
//		if (LOG.isDebugEnabled()) {
//			LOG.debug("Stopping sub-processables.");
//		}
		
		// After sub-processables have been stopped.
		postStopSubProcessables();
		
		processing = false;

		// Notify the ui.
		fireStatusChange(PROPERTY_PROCESSING, true, false);
		
		for (Processable subProcessable : getSubProcessables()) {
			subProcessable.onStopped();
		}
	}
	
	/**
	 * 
	 */
	protected void onStopped() {
		// empty
	}
	
	/**
	 * Will be executed before sub-processables will be started.
	 */
	protected void preStartSubProcessables() {
	}
	
	/**
	 * Will be executed after sub-processables has been stopped. 
	 */
	protected void postStopSubProcessables() {
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#delete()
	 */
	public void delete() throws ProcessException {
		if (isProcessing())
			stop();
		
		Processable parent = getParent();
		
		if (parent != null) {
			parent.removeSubProcessable(this);
		}
		
		fireStatusChange(STATUS_PROCESSABLE_DELETED, this, null);
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#publishFailure()
	 */
	public void publishFailure(Throwable e) {
		fireStatusChange(PROPERTY_FAILURE_PUBLISH, null, e);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#resolveFailure()
	 */
	public void resolveFailure() {
		fireStatusChange(PROPERTY_FAILURE_RESOLVE, null, null);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#resolveFailure()
	 */
	public void publishNotification(String notification) {
		fireStatusChange(PROPERTY_NOTIFICATION, null, notification);
	}
	
	// #############################################################################
	// END ILaunchable
	// #############################################################################
	
	// #############################################################################
	// BEGIN INTERNAL
	// #############################################################################

	public static final String STATUS_PROCESSABLE_ADDED = "status.processable.added";
	public static final String STATUS_PROCESSABLE_DELETED = "status.processable.deleted";
	
	public static final String PROPERTY_FAILURE_PUBLISH = "PROPERTY_FAILURE_PUBLISH";
	public static final String PROPERTY_FAILURE_RESOLVE = "PROPERTY_FAILURE_RESOLVE";
	public static final String PROPERTY_PROCESSING = "PROPERTY_PROCESSING";
	public static final String PROPERTY_NOTIFICATION = "PROPERTY_NOTIFICATION";
	
	// Property change support for processable to register UI component and provide update of UI components
	// while data is processing.
	private final PropertyChangeSupport statusChangeSupport = new PropertyChangeSupport(this);
	
	/**
	 * @param listener
	 * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
	 */
	public final void addStatusChangeListener(PropertyChangeListener listener) {
		statusChangeSupport.addPropertyChangeListener(listener);
	}
	
	/**
	 * @param propertyName
	 * @param listener
	 * @see PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public final void addStatusChangeListener(String propertyName, PropertyChangeListener listener) {
		statusChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	
	/**
	 * @param listener
	 * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
	 */
	public final void removeStatusChangeListener(PropertyChangeListener listener) {
		statusChangeSupport.removePropertyChangeListener(listener);
	}
	
	/**
	 * @param propertyName
	 * @param listener
	 * @see PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
	 */
	public final void removeStatusChangeListener(String propertyName, PropertyChangeListener listener) {
		statusChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
	
	/**
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 * @see PropertyChangeSupport#firePropertyChange(String, Object, Object)
	 */
	public final void fireStatusChange(String propertyName, Object oldValue, Object newValue) {
		statusChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	// #############################################################################
	// END INTERNAL
	// #############################################################################

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Processable other = (Processable) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
