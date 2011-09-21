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

package org.squidy.designer.model;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.Storable;
import org.squidy.StorageHandler;
import org.squidy.designer.Initializable;
import org.squidy.designer.dragndrop.Draggable;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.modularity.NodeBased;
import org.squidy.designer.zoom.ConnectionManagable;
import org.squidy.designer.zoom.ConnectionManager;
import org.squidy.designer.zoom.ContainerShape;
import org.squidy.designer.zoom.DefaultConnectionManager;
import org.squidy.manager.ProcessException;
import org.squidy.manager.model.Processable;


/**
 * <code>ZoomWorkspace</code>.
 * 
 * <pre>
 * Date: Feb 18, 2009
 * Time: 5:40:29 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: WorkspaceShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "WorkspaceShape")
public class WorkspaceShape extends ContainerShape<PipelineShape, Processable> implements Initializable, Draggable, NodeBased, ConnectionManagable, IModelStore, Storable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -7244565181031140525L;
	
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(WorkspaceShape.class);
	
	// #############################################################################
	// BEGIN JAXB
	// #############################################################################
	
	/**
	 * Default constructor required for JAXB.
	 */
	public WorkspaceShape() {
		super();
		
	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ZoomNavigationObject#afterUnmarshal(javax.xml.bind.Unmarshaller, java.lang.Object)
	 */
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		super.afterUnmarshal(unmarshaller, parent);
	}
	
	// #############################################################################
	// END JAXB
	// #############################################################################
	
	// #############################################################################
	// BEGIN Initializable
	// #############################################################################
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.Initializable#initialize()
	 */
	public void initialize() {
		for (VisualShape<?> child : getChildren()) {
			if (child instanceof Initializable) {
				((Initializable) child).initialize();
			}
		}
	}
	
	// #############################################################################
	// END Initializable
	// #############################################################################
	
	// #############################################################################
	// BEGIN ILaunchable
	// #############################################################################

	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#start()
	 */
	public void start() throws ProcessException {
		getProcessable().start();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#stop()
	 */
	public void stop() throws ProcessException {
		getProcessable().stop();
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#delete()
	 */
	public void delete() throws ProcessException {
		getProcessable().delete();
		removeFromParent();
	}
	
	// #############################################################################
	// END ILaunchable
	// #############################################################################
	
	// #############################################################################
	// BEGIN IModelStore
	// #############################################################################

	private Data model;
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.model.IModelStore#getModel()
	 */
	public Data getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public final void setModel(Data model) {
		this.model = model;
	}
	
	// #############################################################################
	// END IModelStore
	// #############################################################################
	
	// #############################################################################
	// BEGIN Storable
	// #############################################################################
	
	/* (non-Javadoc)
	 * @see org.squidy.Storable#store()
	 */
	public void store() {
		if (storageHandler != null) {
			storageHandler.store(getModel());
		}
	}
	
	// #############################################################################
	// END Storable
	// #############################################################################

	private ConnectionManager connectionManager = new DefaultConnectionManager();

	/**
	 * @return the connectionManager
	 */
	public final ConnectionManager getConnectionManager() {
		return connectionManager;
	}
	
	private StorageHandler storageHandler;

	/**
	 * @param storageHandler the storageHandler to set
	 */
	public final void setStorageHandler(StorageHandler storageHandler) {
		this.storageHandler = storageHandler;
	}

	/**
	 * @param title
	 */
	public WorkspaceShape(String title) {
		super();
		setTitle(title);
	}
}
