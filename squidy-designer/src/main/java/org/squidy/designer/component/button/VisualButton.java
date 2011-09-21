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

package org.squidy.designer.component.button;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.util.StrokeUtils;

import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>VisualButton</code>.
 * 
 * <pre>
 * Date: Feb 19, 2009
 * Time: 7:39:56 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: VisualButton.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class VisualButton extends VisualShape<VisualShape<?>> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -4863130563716567549L;

	// Whether the visual button should have a border or not.
	private boolean border;

	/**
	 * @return the border
	 */
	public boolean isBorder() {
		return border;
	}

	/**
	 * @param border
	 *            the border to set
	 */
	public void setBorder(boolean border) {
		this.border = border;
	}

	/**
	 * @param border
	 */
	public VisualButton(boolean border) {
		super();
		this.border = border;
	}

	/**
	 * @param paintContext
	 */
	@Override
	protected void paintShape(PPaintContext paintContext) {
		super.paintShape(paintContext);

		if (border) {
			paintBorder(paintContext);
		}

		paintContent(paintContext);
	}
	
	private Paint buttonPaint;

	/**
	 * @param paintContext
	 */
	protected void paintBorder(PPaintContext paintContext) {
		
		Graphics2D g = paintContext.getGraphics();
		
		PBounds bounds = getBoundsReference();
		int x = (int) bounds.getX();
		int y = (int) bounds.getY();
		int width = (int) bounds.getWidth();
		int height = (int) bounds.getHeight();

		if (buttonPaint == null) {
			buttonPaint = new GradientPaint(x - 5, y - 5, Color.WHITE, x + width + 10, y + height + 10, Color.GRAY);
		}
		
		g.setPaint(buttonPaint);
		if (isRenderPrimitiveRect())
			g.fillRect(x - 5, y - 5, width + 10, height + 10);
		else
			g.fillOval(x - 5, y - 5, width + 10, height + 10);
		
		Stroke defaultStroke = g.getStroke();
		g.setStroke(StrokeUtils.getBasicStroke(0.5f));
		g.setColor(Color.BLACK);
		if (isRenderPrimitiveRect())
			g.drawRect(x - 5, y - 5, width + 10, height + 10);
		else
			g.drawOval(x - 5, y - 5, width + 10, height + 10);
		g.setStroke(defaultStroke);
	}

	/**
	 * @param paintContext
	 */
	protected void paintContent(PPaintContext paintContext) {
	}
}
