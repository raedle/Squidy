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

package org.squidy.designer.util;

import org.squidy.SquidyException;
import org.squidy.designer.model.NodeShape;
import org.squidy.designer.model.PipelineShape;
import org.squidy.designer.shape.LayoutConstraint;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.zoom.ActionShape;
import org.squidy.designer.zoom.ConnectionManagable;
import org.squidy.designer.zoom.ConnectionManager;
import org.squidy.manager.VisualRepresentation;
import org.squidy.manager.data.Processor;
import org.squidy.manager.model.Piping;
import org.squidy.manager.model.Processable;

import edu.umd.cs.piccolo.PNode;

/**
 * <code>ShapeUtils</code>.
 * 
 * <pre>
 * Date: Feb 19, 2009
 * Time: 7:06:44 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: ShapeUtils.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class ShapeUtils {

	/**
	 * @param shape
	 * @param apparent
	 */
	public static final void setApparent(final PNode shape, final boolean apparent) {
		if (shape == null) {
			return;
		}
		
		shape.setVisible(apparent);
		shape.setPickable(apparent);
		shape.setChildrenPickable(apparent);
	}
	
	/**
	 * @param shape
	 * @return
	 */
	public static final boolean isApparent(final PNode shape) {
		if (shape == null) {
			return false;
		}
		
		return shape.getVisible() && shape.getPickable() && shape.getChildrenPickable();
	}
	
	/**
	 * @param shape
	 * @param x
	 * @param y
	 */
	public static final void setOffset(final PNode shape, final double x, final double y) {
		if (shape == null) {
			return;
		}
		
		shape.setOffset(x, y);
	}
	
	/**
	 * @param shape
	 * @param scale
	 */
	public static final void setScale(final PNode shape, final double scale) {
		if (shape == null) {
			return;
		}
		
		shape.setScale(scale);
	}
	
	/**
	 * @param shape
	 * @param renderPrimitive
	 */
	public static final void setRenderPrimitive(final VisualShape<?> shape, final boolean renderPrimitive) {
		if (shape == null) {
			return;
		}
		
		shape.setRenderPrimitive(renderPrimitive);
	}
	
	/**
	 * Searches for a <code>ConnectionManagable</code> within the hierarchy of the
	 * parameter node. It returns the found <code>ConnectionManager</code> instance.
	 * 
	 * @param node The beginner node of the hierarchical search.
	 * @return The connection manager instance.
	 * @throws SquidyException If no connection manager has been found.
	 */
	public static final ConnectionManager getConnectionManager(PNode node) throws SquidyException {
		ConnectionManagable connectionManagable = getObjectInHierarchy(ConnectionManagable.class, node);
		
		if (connectionManagable == null) {
			throw new SquidyException("No connection manager found in node hierarchy.");
		}
		
		return connectionManagable.getConnectionManager();
	}
	
	/**
	 * @param <O>
	 * @param objectType
	 * @param hierarchy
	 * @return
	 */
	public static final <O> O getObjectInHierarchy(Class<? extends O> objectType, PNode hierarchy) {
		PNode parent = hierarchy.getParent();
		while (parent != null) {
			if (objectType.isAssignableFrom(parent.getClass())) {
				return (O) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	/**
	 * Returns the visual shape that has the given id if it exists somewhere in the object
	 * hierarchy. A null reference will be returned otherwise.
	 * @param parent The parent/container shape.
	 * @param id The id of the searched shape.
	 * @return The shape that has the given id.
	 */
	public static final VisualShape<?> getShapeWithId(VisualShape<?> parent, String id) {
		if (id.equals(parent.getId())) {
			return parent;
		}

		for (VisualShape<?> subShape : parent.getChildren()) {
			VisualShape<?> res = getShapeWithId(subShape, id);
			if (res != null) {
				return res;
			}
		}
		return null;
	}
	
	/**
	 * @param processable
	 * @return
	 */
	public static ActionShape<?, ?> getActionShape(Processable processable) {

		Class<? extends Processable> processableClass = processable.getClass();
		
		boolean stable = false;
		if (processableClass.isAnnotationPresent(Processor.class)) {
			Processor.Status status = processableClass.getAnnotation(Processor.class).status();
			stable = Processor.Status.STABLE.equals(status) ? true : false;
		}
		
		if (processableClass.isAnnotationPresent(VisualRepresentation.class)) {
			VisualRepresentation visualRepresentation = processableClass.getAnnotation(VisualRepresentation.class);

			ActionShape<?, Piping> shape = null;
			switch (visualRepresentation.type()) {
			case NODE:
				shape = new NodeShape();
				((NodeShape) shape).setStable(stable);
				break;
			case PIPELINE:
				shape = new PipelineShape();

				// String name =
				// JOptionPane.showInputDialog(Designer.getInstance(),
				// "Pipeline Name", "Pipeline Name");
				// shape.setTitle(name);

				break;
			}

			shape.setProcessable((Piping) processable);
			shape.initialize();

			LayoutConstraint lc = shape.getLayoutConstraint();
			lc.setScale(0.1);
			lc.setX(0);
			lc.setY(0);

			return shape;
		}
		else {
			NodeShape nodeShape = new NodeShape();
			nodeShape.setProcessable((Piping) processable);
			nodeShape.initialize();
			nodeShape.setStable(stable);

			LayoutConstraint lc = nodeShape.getLayoutConstraint();
			lc.setScale(0.1);
			lc.setX(0);
			lc.setY(0);

			return nodeShape;
		}
	}
}
