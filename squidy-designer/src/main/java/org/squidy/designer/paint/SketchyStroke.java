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

package org.squidy.designer.paint;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

/**
 * <code>SketchyStroke</code>.
 * 
 * <pre>
 * Date: Jul 15, 2009
 * Time: 9:37:37 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 *         @uni-konstanz.de</a> Human-Computer Interaction Group University of
 *         Konstanz
 * 
 * @version $Id: SketchyStroke.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class SketchyStroke implements Stroke {
	private float detail = 2;
	private float amplitude = 2;
	private static final float FLATNESS = 1;

	private float thickness;
	
	public SketchyStroke(float thickness, float detail, float amplitude) {
		this.thickness = thickness;
		this.detail = detail;
		this.amplitude = amplitude;
	}

	/**
	 * @param shape
	 * @return
	 */
	public Shape createStrokedShape(Shape shape) {
		GeneralPath result = new GeneralPath();
		shape = new BasicStroke(thickness).createStrokedShape(shape);
		PathIterator it = new FlatteningPathIterator(shape.getPathIterator(null), FLATNESS);
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;
		boolean first = false;
		float next = 0;

		while (!it.isDone()) {
			type = it.currentSegment(points);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				moveX = lastX = randomize(points[0]);
				moveY = lastY = randomize(points[1]);
				result.moveTo(moveX, moveY);
				first = true;
				next = 0;
				break;

			case PathIterator.SEG_CLOSE:
				points[0] = moveX;
				points[1] = moveY;
				// Fall into....

			case PathIterator.SEG_LINETO:
				thisX = randomize(points[0]);
				thisY = randomize(points[1]);
				float dx = thisX - lastX;
				float dy = thisY - lastY;
				float distance = (float) Math.sqrt(dx * dx + dy * dy);
				if (distance >= next) {
					float r = 1.0f / distance;
					while (distance >= next) {
						float x = lastX + next * dx * r;
						float y = lastY + next * dy * r;
						result.lineTo(randomize(x), randomize(y));
						next += detail;
					}
				}
				next -= distance;
				first = false;
				lastX = thisX;
				lastY = thisY;
				break;
			}
			it.next();
		}

		return result;
	}
	
	private float runner = 0.0f;
	private boolean up = true;

	/**
	 * @param x
	 * @return
	 */
	private float randomize(float x) {
//		return x + (float) Math.random() * amplitude * 2 - 1;
		
		if (up) {
			runner += 0.1f;
		}
		else {
			runner -= 0.1f;
		}
			
		if (runner > 1.0f) {
			runner = 1.0f;
			up = false;
		}
		else if (runner < 0.0f) {
			runner = 0.0f;
			up = true;
		}
		
		return x + (float) runner * amplitude * 2 - 1;
	}
}