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

package org.squidy.designer.event;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import org.squidy.designer.zoom.ActionShape;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * <code>MultiSelectionHandler</code>.
 * 
 * <pre>
 * Date: May 28, 2009
 * Time: 6:20:14 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 *         @uni-konstanz.de</a> Human-Computer Interaction Group University of
 *         Konstanz
 * 
 * @version $Id: MultiSelectionHandler.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public abstract class MultiSelectionHandler extends PBasicInputEventHandler {

	private Shape selectionShape;
	private boolean selectionAllowed = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mousePressed(edu.umd
	 * .cs.piccolo.event.PInputEvent)
	 */
	@Override
	public void mousePressed(PInputEvent event) {
		selectionAllowed = selectionAllowed(event);
		if (!selectionAllowed)
			return;
		
		Point2D p = event.getPosition();
		selectionShape = new Rectangle2D.Double(p.getX(), p.getY(), 0, 0);

		startSelection(event, selectionShape);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mouseDragged(edu.umd
	 * .cs.piccolo.event.PInputEvent)
	 */
	@Override
	public void mouseDragged(PInputEvent event) {
		if (!selectionAllowed)
			return;
		
		if (selectionShape != null) {
			Point2D p = event.getPosition();

			if (selectionShape instanceof RectangularShape) {
				RectangularShape rectangle = (RectangularShape) selectionShape;
				
				double shapeX = rectangle.getX();
				double shapeY = rectangle.getY();
				double shapeWidth = rectangle.getWidth();
				double shapeHeight = rectangle.getHeight();
				
				double pointX = p.getX();
				double pointY = p.getY();

				double x = Math.min(shapeX, pointX);
				double y = Math.min(shapeY, pointY);
				double width = Math.abs(Math.max(shapeX, pointX) - x);
				double height = Math.abs(Math.max(shapeY, pointY) - y);
				
				if (x != shapeX) {
					System.out.println("ECHO: " + x + " | " + shapeX);
					width += shapeWidth;
				}
				
				if (y != shapeY) {
					height += shapeHeight;
				}

				rectangle.setFrame(x, y, width, height);
				
				System.out.println(selectionShape);
			}

			selection(event, selectionShape);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mouseReleased(edu.umd
	 * .cs.piccolo.event.PInputEvent)
	 */
	@Override
	public void mouseReleased(PInputEvent event) {
		if (!selectionAllowed)
			return;
		
		endSelection(event, selectionShape);
		selectionShape = null;
	}
	
	protected abstract boolean selectionAllowed(PInputEvent event);

	protected abstract void startSelection(PInputEvent event, Shape selectionShape);

	protected abstract void selection(PInputEvent event, Shape selectionShape);

	protected abstract void endSelection(PInputEvent event, Shape selectionShape);
}
