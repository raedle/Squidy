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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.squidy.manager.data.IData;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;

import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>VisualDebugUtils</code>.
 * 
 * <pre>
 * Date: Mar 17, 2009
 * Time: 5:20:33 PM
 * </pre>
 * 
 * @author
 * Roman R&amp;aumldle<br />
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 * Human-Computer Interaction Group<br />
 * University of Konstanz
 * 
 * @version $Id: VisualDebugUtils.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class VisualDebugUtils {

	// Affine transform scales sx to 0.5 and sy to 0.5.
	public static final PAffineTransform SCALE_TRANSFORM = new PAffineTransform();
	static {
		SCALE_TRANSFORM.setToScale(0.5, 0.5);
	}
	
	// Affine transform rotates graphics -45 degrees.
	public static final PAffineTransform ROTATION_TRANSFORM = new PAffineTransform();
	public static final double ROTATION_RADIANS = Math.toRadians(-45);
	static {
		ROTATION_TRANSFORM.setToRotation(ROTATION_RADIANS);
	}
	
	/**
	 * TODO: DOCUMENT ME!!!
	 * 
	 * @param data
	 * @param paintContext
	 * @param xPosition
	 * @param maxHeight
	 */
	public static final void drawData(IData data, PPaintContext paintContext, double xPosition, double maxHeight) {
		if (data instanceof DataPosition2D) {
			drawDataPosition2D((DataPosition2D) data, paintContext, xPosition, maxHeight);
		}
		else if (data instanceof DataDigital) {
			drawDataDigital((DataDigital) data, paintContext, xPosition, maxHeight);
		}
	}
	
	/**
	 * TODO: DOCUMENT ME!!!
	 * 
	 * @param dataPosition2D
	 * @param paintContext
	 * @param xPosition
	 * @param maxHeight
	 */
	public static final void drawDataPosition2D(DataPosition2D dataPosition2D, PPaintContext paintContext, double xPosition, double maxHeight) {
		
		Graphics2D g = paintContext.getGraphics();
		g.setStroke(new BasicStroke(0.1f));

		// Push scaling transform.
		paintContext.pushTransform(SCALE_TRANSFORM);
		
		// Draw x-position of DataPosition2D.
		g.setColor(Color.RED);
		dataPosition2D.getX();
		int dataPosition2DXPosY = (int) (Math.abs(dataPosition2D.getX() - 1.0) * maxHeight * 2);
		g.fillOval((int) (xPosition * 2), dataPosition2DXPosY, 1, 1);

		// Draw x-position of DataPosition2D.
		g.setColor(Color.BLUE);
		dataPosition2D.getY();
		int dataPosition2DYPosY = (int) (Math.abs(dataPosition2D.getY() - 1.0) * maxHeight * 2);
		g.fillOval((int) (xPosition * 2), dataPosition2DYPosY, 1, 1);
		
		// Pop scaling transform.
		paintContext.popTransform(SCALE_TRANSFORM);
	}
	
	/**
	 * TODO: DOCUMENT ME!!!
	 * 
	 * @param dataDigital
	 * @param paintContext
	 * @param xPosition
	 * @param maxHeight
	 */
	public static final void drawDataDigital(DataDigital dataDigital, PPaintContext paintContext, double xPosition, double maxHeight) {

		Graphics2D g = paintContext.getGraphics();
		g.setStroke(new BasicStroke(0.5f));

		g.setColor(Color.YELLOW);
		g.drawLine((int) xPosition, (int) maxHeight, (int) xPosition, 0);

		String label = "" + dataDigital.getFlag();
		if (dataDigital instanceof DataButton) {
			label += ", buttonType=" + ((DataButton) dataDigital).getButtonType();
		}
		
		double oppositeLeg = Math.abs(Math.sin(ROTATION_RADIANS) * xPosition);
		double adjustment = Math.cos(ROTATION_RADIANS) * xPosition - xPosition;
		
		// Push rotation transform.
		paintContext.pushTransform(ROTATION_TRANSFORM);
		
		// Push backwards translation transform.
		PAffineTransform translateBackwards = new PAffineTransform();
		translateBackwards.setToTranslation(adjustment + 0.5, oppositeLeg - 0.5);
		paintContext.pushTransform(translateBackwards);
		
		g.setColor(Color.BLACK);
		g.drawString(label, (int) xPosition, 0);

		// Pop backwards translation transform.
		paintContext.popTransform(translateBackwards);
		
		// Pop rotation transform.
		paintContext.popTransform(ROTATION_TRANSFORM);
	}
}
