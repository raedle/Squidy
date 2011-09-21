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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.squidy.Namespaces;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.ZoomShape;
import org.squidy.manager.model.ModelData;
import org.squidy.manager.model.Workspace;

import edu.umd.cs.piccolo.util.PBounds;

/**
 * <code>Data</code>.
 * 
 * <pre>
 * Date: Feb 21, 2009
 * Time: 11:56:43 PM
 * </pre>
 * 
 * @author
 * Roman R&amp;aumldle<br />
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 * Human-Computer Interaction Group<br />
 * University of Konstanz
 * 
 * @version $Id: Data.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlRootElement(name = "Data", namespace = Namespaces.NAMESPACE_PREFIX_COMMON)
public class Data implements ViewData, ModelData {
	
	// #############################################################################
	// BEGIN JAXB
	// #############################################################################
	
	@XmlAttribute(name = "render-primitive-rect")
	private boolean renderPrimitiveRect = VisualShape.isRenderPrimitiveRect();

	/**
	 * @return the renderPrimitiveRect
	 */
	public boolean isRenderPrimitiveRect() {
		return renderPrimitiveRect;
	}

	/**
	 * @param renderPrimitiveRect the renderPrimitiveRect to set
	 */
	public void setRenderPrimitiveRect(boolean renderPrimitiveRect) {
		this.renderPrimitiveRect = renderPrimitiveRect;
	}
	
	@XmlIDREF
	@XmlAttribute(name = "zoomed-shape")
	private ZoomShape<?> zoomedShape;
	
	/**
	 * @return the zoomedShape
	 */
	public final ZoomShape<?> getZoomedShape() {
		return zoomedShape;
	}

	/**
	 * @param zoomedShape the zoomedShape to set
	 */
	public final void setZoomedShape(ZoomShape<?> zoomedShape) {
		this.zoomedShape = zoomedShape;
	}
	
	@XmlAttribute(name = "zoomed-bounds")
	@XmlJavaTypeAdapter(PBoundsAdapter.class)
	private PBounds zoomedBounds;

	public PBounds getZoomedBounds() {
		return zoomedBounds;
	}

	public void setZoomedBounds(PBounds zoomedBounds) {
		this.zoomedBounds = zoomedBounds;
	}

	@XmlElement(name = "workspace-shape")
	private WorkspaceShape workspaceShape;

	/**
	 * @return the zoomWorkspace
	 */
	public final WorkspaceShape getWorkspaceShape() {
		return workspaceShape;
	}

	/**
	 * @param workspaceShape the workspaceShape to set
	 */
	public final void setWorkspaceShape(WorkspaceShape workspaceShape) {
		this.workspaceShape = workspaceShape;
	}

	@XmlElement(name = "workspace", namespace = Namespaces.NAMESPACE_PREFIX_MANAGER)
	public Workspace workspace = new Workspace();

	/**
	 * @return the workspace
	 */
	public final Workspace getWorkspace() {
		return workspace;
	}

	/**
	 * @param workspace the workspace to set
	 */
	public final void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}
	
	// #############################################################################
	// END JAXB
	// #############################################################################
}
