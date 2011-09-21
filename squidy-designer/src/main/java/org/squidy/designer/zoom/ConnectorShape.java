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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.constant.Constants;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.util.StrokeUtils;
import org.squidy.designer.zoom.impl.PortShape;
import org.squidy.manager.model.Processable;

import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import java.awt.Rectangle;

/**
 * <code>ConnectorShape</code>.
 * 
 * <pre>
 * Date: Feb 19, 2009
 * Time: 5:47:30 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: ConnectorShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public abstract class ConnectorShape<T extends VisualShape<?>, P extends Processable> extends ContainerShape<T, P>
		implements Connectable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 4999506198228284183L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ConnectorShape.class);

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################

	/**
	 * Default constructor required for JAXB.
	 */
	public ConnectorShape() {
		super();
		inputPort = new PortShape();
		addChild(inputPort);

		outputPort = new PortShape();
		addChild(outputPort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ZoomNavigationObject#afterUnmarshal(javax
	 * .xml.bind.Unmarshaller, java.lang.Object)
	 */
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		super.afterUnmarshal(unmarshaller, parent);
	}

	// #############################################################################
	// END JAXB
	// #############################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.VisualShape#addVisualShape(org.squidy
	 * .designer.VisualShape)
	 */
	public void addVisualShape(T child) {
		super.addVisualShape(child);

		// Prepare connectable child.
		prepareConnectableChild(child);
	}

	// #############################################################################
	// BEGIN Initializable
	// #############################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();

		// Prepare connectable child.
		for (T child : getChildren()) {
			prepareConnectableChild(child);
		}
	}

	// #############################################################################
	// END Initializable
	// #############################################################################

	// #############################################################################
	// BEGIN INTERNAL
	// #############################################################################

	private Shape shapePortLeft;
	private Shape shapePortRight;

	/**
	 * @param child
	 */
	private void prepareConnectableChild(T child) {
		if (child instanceof Connectable) {
			Connectable connectable = (Connectable) child;

			PortShape inputPort = connectable.getInputPort();
			PortShape outputPort = connectable.getOutputPort();

			ConnectionManager connectionManager = ShapeUtils.getConnectionManager(this);
			connectionManager.addConnection(inputPort);
			connectionManager.addConnection(outputPort);
		}
	}

	// The input port if the connector shape.
	private PortShape inputPort;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.Connectable#getInputPort()
	 */
	public PortShape getInputPort() {
		return inputPort;
	}

	// The output port of the connector shape.
	private PortShape outputPort;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.Connectable#getOutputPort()
	 */
	public PortShape getOutputPort() {
		return outputPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ZoomActionObject#layoutSemanticsZoomedIn
	 * ()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();

		PBounds bounds = getBoundsReference();
		double x = bounds.getX();
		double centerY = bounds.getCenterY();
		double width = bounds.getWidth();

//		ShapeUtils.setApparent(getInputPort(), false);
//		ShapeUtils.setApparent(getOutputPort(), false);
		inputPort.setScale(0.1);
		inputPort.setOffset(x + 10, centerY - (inputPort.getHeight() * inputPort.getScale()) / 20);

		outputPort.setScale(0.1);
		outputPort.setOffset(x + width - 10 - (outputPort.getWidth() * outputPort.getScale()), centerY
				- (outputPort.getHeight() * outputPort.getScale()) / 20);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ZoomActionObject#layoutSemanticsZoomedOut
	 * ()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		super.layoutSemanticsZoomedOut();

		PBounds bounds = getBoundsReference();
		double x = bounds.getX();
		double y = bounds.getY();
		double centerY = bounds.getCenterY();
		double width = bounds.getWidth();

//		ShapeUtils.setApparent(getInputPort(), true);
//		ShapeUtils.setApparent(getOutputPort(), true);
		inputPort.setScale(1.0);
		inputPort.setOffset(x + 50, centerY - inputPort.getHeight() / 2);
		outputPort.setScale(1.0);
		outputPort.setOffset(x + width - 50 - outputPort.getWidth(), centerY - outputPort.getHeight() / 2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ZoomNavigationObject#paintShapeZoomedIn
	 * (edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedIn(PPaintContext paintContext) {
		super.paintShapeZoomedIn(paintContext);

		if (getInputPort().getVisible() || getOutputPort().getVisible()) {
			Graphics2D g = paintContext.getGraphics();
			PBounds bounds = getBoundsReference();
			double x = bounds.getX();
			double y = bounds.getY();
			double centerY = bounds.getCenterY();
			double width = bounds.getWidth();

			// Paint a border for zoom ports.
			// g.setStroke(new BasicStroke(6f));

			// g.setColor(Color.RED);
			// g.draw(getBoundsReference());

			// Painting left port.
			if (getInputPort().getVisible()) {

				if (shapePortLeft == null) {
					double portScale = inputPort.getScale();
					double portWidth = inputPort.getWidth() * portScale;
					double portHeight = inputPort.getHeight() * portScale;

					shapePortLeft = new RoundRectangle2D.Double(x - portWidth + 15, centerY - portHeight / 2 - 5, 35,
							60, 10, 10);
				}

				Rectangle boundsPort = shapePortLeft.getBounds();
				g.setColor(Constants.Color.COLOR_SHAPE_BACKGROUND);
				if (isRenderPrimitiveRect())
					g.fillRect(boundsPort.x, boundsPort.y, boundsPort.width, boundsPort.height);
				else
					g.fill(shapePortLeft);

				g.setColor(Constants.Color.COLOR_SHAPE_BORDER);
				g.setStroke(StrokeUtils.getBasicStroke(3f));
				if (isRenderPrimitiveRect())
					g.drawRect(boundsPort.x, boundsPort.y, boundsPort.width, boundsPort.height);
				else
					g.draw(shapePortLeft);
			}

			// Painting right port.
			if (getOutputPort().getVisible()) {

				if (shapePortRight == null) {
					double portScale = outputPort.getScale();
					double portWidth = outputPort.getWidth() * portScale;
					double portHeight = outputPort.getHeight() * portScale;

					shapePortRight = new RoundRectangle2D.Double(x + width - portWidth - 21, centerY - portHeight / 2
							- 5, 35, 60, 10, 10);
				}

				Rectangle boundsPort = shapePortRight.getBounds();
				g.setColor(Constants.Color.COLOR_SHAPE_BACKGROUND);
				if (isRenderPrimitiveRect())
					g.fillRect(boundsPort.x, boundsPort.y, boundsPort.width, boundsPort.height);
				else
					g.fill(shapePortRight);

				g.setColor(Constants.Color.COLOR_SHAPE_BORDER);
				g.setStroke(StrokeUtils.getBasicStroke(3f));
				if (isRenderPrimitiveRect())
					g.drawRect(boundsPort.x, boundsPort.y, boundsPort.width, boundsPort.height);
				else
					g.draw(shapePortRight);
			}
		}
	}

	// #############################################################################
	// END INTERNAL
	// #############################################################################
}
