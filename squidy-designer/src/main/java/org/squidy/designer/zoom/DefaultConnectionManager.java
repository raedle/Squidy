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

package org.squidy.designer.zoom;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

import org.squidy.designer.zoom.impl.PortShape;

import edu.umd.cs.piccolo.PNode;

/**
 * <code>DefaultConnectionManager</code>.
 * 
 * <pre>
 * Date: Mar 15, 2009
 * Time: 6:41:12 PM
 * </pre>
 * 
 * @author
 * Roman R&amp;aumldle<br />
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 * Human-Computer Interaction Group<br />
 * University of Konstanz
 * 
 * @version $Id: DefaultConnectionManager.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class DefaultConnectionManager implements ConnectionManager {

	/**
	 * The collections contains <code>ZoomPort</code>s and allows to identify connections
	 * at a specific point {@link this#hasConnectionAtPoint(Point2D)} or {@link this#hasConnectionAtPoint(double, double)}
	 */
	private Collection<PortShape> ports = new ArrayList<PortShape>();

	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ConnectionManager#addConnection(org.squidy.designer.zoom.impl.ZoomPort)
	 */
	public void addConnection(PortShape port) {
		ports.add(port);
	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ConnectionManager#removeConnection(org.squidy.designer.zoom.impl.ZoomPort)
	 */
	public void removeConnection(PortShape port) {
		ports.remove(port);
	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ConnectionManager#getConnectionAtPoint(java.awt.geom.Point2D)
	 */
	public PortShape getConnectionAtPoint(Point2D point) {
		for (PortShape port : ports) {
			Rectangle2D bounds = port.localToGlobal(port.getBounds());
			if (bounds.contains(point)) {
				return port;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ConnectionManager#hasConnectionAtPoint(java.awt.geom.Point2D)
	 */
	public boolean hasConnectionAtPoint(Point2D point) {
		return hasConnectionAtPoint(point.getX(), point.getY());
	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ConnectionManager#hasConnectionAtPoint(double, double)
	 */
	private boolean hasConnectionAtPoint(double x, double y) {
		for (PortShape port : ports) {
			Rectangle2D bounds = port.localToGlobal(port.getBounds());
			if (bounds.contains(x, y)) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ConnectionManager#hasConnectionAtDifferentNodeAtPoint(org.squidy.designer.zoom.impl.PortShape, java.awt.geom.Point2D)
	 */
	public boolean hasConnectionAtDifferentNodeAtPoint(PortShape portShape, Point2D point) {
		PNode parent = portShape.getParent();
		for (PortShape port : ports) {
			Rectangle2D bounds = port.localToGlobal(port.getBounds());
			if (bounds.contains(point) && !parent.equals(port.getParent())) {
				return true;
			}
		}
		return false;
	}
}
