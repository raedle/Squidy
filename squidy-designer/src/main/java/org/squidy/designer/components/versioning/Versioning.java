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

package org.squidy.designer.components.versioning;

import java.awt.Color;
import java.awt.Graphics2D;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.util.StrokeUtils;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>Versioning</code>.
 * 
 * <pre>
 * Date: Apr 1, 2009
 * Time: 2:18:17 AM
 * </pre>
 * 
 * 
 * @author
 * Roman R&amp;aumldle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: Versioning.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class Versioning extends VisualShape<VisualShape<?>> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 6864520360418297852L;
	
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Versioning.class);

	private PNode versionNode;
	
	/**
	 * @param versionNode
	 */
	public Versioning(PNode versionNode) {
		this.versionNode = versionNode;
	}

	private static Color COLOR_FILL = new Color(200, 200, 200, 100);
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.VisualShape#paintShape(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShape(PPaintContext paintContext) {
		super.paintShape(paintContext);
		
		Graphics2D g = paintContext.getGraphics();
		
		PBounds bounds = getBoundsReference();
		double x = bounds.getX();
		double y = bounds.getY();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		
//		g.setColor(Color.RED);
//		g.draw(bounds);
		
		g.setClip(bounds);
		
		for (int i = 0; i < 7; i++) {
			PAffineTransform transform = new PAffineTransform();
			transform.scale(0.12, 0.12);
			transform.translate(i * 122, 0);
			
			paintContext.pushTransform(transform);
			
			if (!isRenderPrimitive()) {
				versionNode.fullPaint(paintContext);
			}
			
			if (i == 6) {
				g.setStroke(StrokeUtils.getBasicStroke(5f));
				g.setColor(Color.GRAY);
				if (isRenderPrimitiveRect())
					g.drawRect((int) x, (int) y, 100, (int) (height / 0.12));
				else
					g.drawRoundRect((int) x, (int) y, 100, (int) (height / 0.12), 15, 15);

				g.setColor(COLOR_FILL);
				if (isRenderPrimitiveRect())
					g.fillRect((int) x, (int) y, 100, (int) (height / 0.12));
				else
					g.fillRoundRect((int) x, (int) y, 100, (int) (height / 0.12), 15, 15);
			}
			
			paintContext.popTransform(transform);
		}
			
		g.setClip(null);
	}
}
