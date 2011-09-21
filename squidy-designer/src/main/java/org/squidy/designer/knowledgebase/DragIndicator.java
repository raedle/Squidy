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

package org.squidy.designer.knowledgebase;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>DragIndicator</code>.
 * 
 * <pre>
 * Date: Jul 9, 2009
 * Time: 1:51:50 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: DragIndicator.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class DragIndicator extends PNode {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -7163673170048285057L;

	public DragIndicator() {
		setBounds(0, 0, 50, 50);
	}
	
	/* (non-Javadoc)
	 * @see edu.umd.cs.piccolo.PNode#setOffset(double, double)
	 */
	@Override
	public void setOffset(double x, double y) {
		super.setOffset(x - getWidth() / 2, y - getHeight() / 2);
	}
	
	/* (non-Javadoc)
	 * @see edu.umd.cs.piccolo.PNode#setOffset(java.awt.geom.Point2D)
	 */
	@Override
	public void setOffset(Point2D point) {
		setOffset(point.getX(), point.getY());
	}
	
	/* (non-Javadoc)
	 * @see edu.umd.cs.piccolo.PNode#paint(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paint(PPaintContext paintContext) {
		super.paint(paintContext);
		
		Graphics2D g = paintContext.getGraphics();
		
		g.setColor(Color.BLACK);
		g.drawOval((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
	}
}
