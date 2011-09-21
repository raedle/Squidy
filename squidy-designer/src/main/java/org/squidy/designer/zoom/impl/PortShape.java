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

package org.squidy.designer.zoom.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.component.TemporaryNotification;
import org.squidy.designer.constant.Constants;
import org.squidy.designer.model.PipeShape;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.zoom.ConnectionManager;
import org.squidy.designer.zoom.ConnectorShape;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ZoomPort</code>.
 * 
 * <pre>
 * Date: Feb 16, 2009
 * Time: 1:05:38 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: PortShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class PortShape extends VisualShape<VisualShape<?>> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 4624149154508794494L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(PortShape.class);

	private boolean isCreatingEdge;

	private double startX;
	private double startY;

	private double currentX;
	private double currentY;

	private Color innerColor = Color.LIGHT_GRAY;

	public PortShape() {
		setBounds(Constants.DEFAULT_PORT_BOUNDS);

		addInputEventListener(new PBasicInputEventHandler() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mousePressed
			 * (edu.umd.cs.piccolo .event.PInputEvent)
			 */
			@Override
			public void mousePressed(PInputEvent event) {
				super.mousePressed(event);

				if (!event.isHandled()) {
					isCreatingEdge = true;
					Rectangle2D bounds = localToGlobal(getBounds());
					startX = bounds.getX() + bounds.getWidth() / 2;// .getCenterX();
					startY = bounds.getY() + bounds.getWidth() / 2;// .getCenterY();
					currentX = startX;
					currentY = startY;

					// moveToFront();

					event.setHandled(true);
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mouseReleased
			 * (edu.umd.cs.piccolo.event.PInputEvent)
			 */
			@Override
			public void mouseReleased(PInputEvent event) {
				super.mouseReleased(event);

				isCreatingEdge = false;

				ConnectionManager connectionManager = ShapeUtils.getConnectionManager(PortShape.this);

				Point2D point = event.getPosition();

				if (connectionManager.hasConnectionAtDifferentNodeAtPoint(PortShape.this, point)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Connection port found at " + point);
					}

					ConnectorShape<?, ?> source = (ConnectorShape<?, ?>) getParent();
					ConnectorShape<?, ?> target = (ConnectorShape<?, ?>) connectionManager.getConnectionAtPoint(point)
							.getParent();
					
					if (!source.getParent().equals(target.getParent())) {
//						ConnectorShape<?, ?> tmp = source;
//						source = target;
//						target = tmp;
					}

					PipeShape pipeShape;
					try {
						pipeShape = PipeShape.create(source, target);
					} catch (Exception e) {
						publishNotification(new TemporaryNotification(e.getMessage()));
						return;
					}

					VisualShape<VisualShape<?>> parentShape;
					if (!source.getParent().equals(target.getParent())) {
						if (target.getParent().equals(source)) {
							parentShape = (VisualShape<VisualShape<?>>) source;
						}
						else {
							parentShape = (VisualShape<VisualShape<?>>) target;
						}
					}
					else {
						parentShape = (VisualShape<VisualShape<?>>) source.getParent();
					}
					
					parentShape.addVisualShape(pipeShape);
					pipeShape.invalidateFullBounds();
				}
				else {
					// TODO [RR]: Repaint from bounds (do not repaint full node bounds)
					getParent().getParent().invalidatePaint();
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mouseDragged
			 * (edu.umd.cs.piccolo.event.PInputEvent)
			 */
			@Override
			public void mouseDragged(PInputEvent event) {
				super.mouseDragged(event);

				Point2D point = event.getPosition();

				currentX = point.getX();
				currentY = point.getY();

				event.getCamera().invalidatePaint();

				event.setHandled(true);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mouseEntered
			 * (edu.umd.cs.piccolo.event.PInputEvent)
			 */
			@Override
			public void mouseEntered(PInputEvent event) {
				super.mouseEntered(event);

				innerColor = Color.GRAY;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mouseExited(
			 * edu.umd.cs.piccolo.event.PInputEvent)
			 */
			@Override
			public void mouseExited(PInputEvent event) {
				super.mouseExited(event);

				innerColor = Color.LIGHT_GRAY;
			}
		});
	}

	private static final Stroke STROKE_PORT = new BasicStroke(20f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);

	/**
	 * @param paintContext
	 */
	@Override
	protected void paintShape(PPaintContext paintContext) {
		super.paintShape(paintContext);

		Graphics2D g = paintContext.getGraphics();

		PBounds bounds = getBoundsReference();

		int x = (int) bounds.getX();
		int y = (int) bounds.getY();
		int width = (int) bounds.getWidth();
		int height = (int) bounds.getHeight();

		g.setColor(innerColor);
		g.fillOval(x, y, width, width);

		Stroke defaultStroke = g.getStroke();

		g.setStroke(STROKE_PORT);
		g.setColor(Color.BLACK);
		g.drawOval(x, y, width, width);

		g.drawLine(x + 20, height - 20, x + width - 20, height - 20);
		g.drawLine(x + width - 50, height - 40, x + width - 20, height - 20);
		g.drawLine(x + width - 50, height + 0, x + width - 20, height - 20);

		g.setStroke(defaultStroke);

		// //////////////////

		if (isCreatingEdge) {
			Graphics2D g2d = paintContext.getGraphics();

			g2d.setColor(Color.GRAY);

			// // Paint label.
			// if (showLabel) {
			// paintLabel(paintContext);

			// Paint edge if is creating edge.
			if (isCreatingEdge) {
				paintCreatingEdge(paintContext);
			}
		}
	}
	
	private static final Stroke STROKE_CREATING_EDGE = new BasicStroke(30f);
	private Point2D pointStart = new Point2D.Double();
	private Point2D pointCurrent = new Point2D.Double();
//	private CubicCurve2D curve = new CubicCurve2D.Double();
	
	/**
	 * @param paintContext
	 */
	private void paintCreatingEdge(PPaintContext paintContext) {

		// Get graphics to paint creating edge.
		Graphics2D g2d = paintContext.getGraphics();

		Stroke defaultStroke = g2d.getStroke();
		g2d.setColor(Color.GRAY);
		g2d.setStroke(STROKE_CREATING_EDGE);

		pointStart.setLocation(startX, startY);
		pointCurrent.setLocation(currentX, currentY);
		globalToLocal(pointStart);
		globalToLocal(pointCurrent);

		double x1 = pointStart.getX();
		double y1 = pointStart.getY();
		double x2 = pointCurrent.getX();
		double y2 = pointCurrent.getY();
		
/*		
		double width = Math.abs(x1 - x2);
		double height = Math.abs(y1 - y2);

		int division = 2;
		double middleLeftX = x1 + (width / division);
		double middleLeftY = y1 + (height / division);
		double middleRightX = x2 - (width / division);
		double middleRightY = y2 - (height / division);
		
		curve.setCurve(x1, y1, middleLeftX, middleLeftY, middleRightX, middleRightY, x2, y2);
*/

//		double width = Math.abs(x1 - x2);
//		curve.setCurve(x1, y1, x1 + (width / 2), y1, x2 - (width / 2), y2, x2, y2);
//		g2d.draw(curve);
		
		g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
		g2d.setStroke(defaultStroke);
	}
}
