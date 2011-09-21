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

package org.squidy.designer.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.Designer;
import org.squidy.designer.Initializable;
import org.squidy.designer.constant.DebugConstants;
import org.squidy.designer.shape.LayoutConstraint;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.zoom.ConnectorShape;
import org.squidy.designer.zoom.impl.DataTypeShape;
import org.squidy.designer.zoom.impl.PortShape;
import org.squidy.designer.zoom.impl.VisualizationShape;
import org.squidy.manager.Manager;
import org.squidy.manager.data.IData;
import org.squidy.manager.model.Pipe;
import org.squidy.manager.model.Piping;
import org.squidy.manager.model.Processable;
import org.squidy.manager.model.Processable.Action;
import org.squidy.manager.util.DataUtility;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;

/**
 * <code>Edge</code>.
 * 
 * <pre>
 * Date: Feb 19, 2009
 * Time: 2:00:01 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&auml;dle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: PipeShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "PipeShape")
public class PipeShape extends VisualShape<VisualShape<?>> implements
		Initializable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 2506753802315463256L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(PipeShape.class);

	// #############################################################################
	// BEGIN CREATOR
	// #############################################################################

	public static PipeShape create(ConnectorShape<?, ?> source,
			ConnectorShape<?, ?> target, String... ids) {
		PipeShape shape = new PipeShape(source, target);
		
		if (ids != null && ids.length == 2)
			shape.setId(ids[0]);

		LayoutConstraint layoutConstraint = shape.getLayoutConstraint();
		layoutConstraint.setScale(0.1);

		Pipe pipe = new Pipe();
		
		if (ids != null && ids.length == 2)
		pipe.setId(ids[1]);
		
		pipe.setSource(source.getProcessable());
		pipe.setTarget(target.getProcessable());
		pipe.setInputTypes(new ArrayList<Class<? extends IData>>(
				DataUtility.ALL_DATA_TYPES));
		pipe.setOutputTypes(new ArrayList<Class<? extends IData>>(
				DataUtility.ALL_DATA_TYPES));

		shape.setPipe(pipe);

		// TODO: [RR] Remove if pipe will be added to parent of source.
		Processable sourceProcessable = source.getProcessable();
		if (sourceProcessable instanceof Piping) {
			((Piping) sourceProcessable).addOutgoingPipe(pipe);
		}

		Processable targetProcessable = target.getProcessable();
		if (targetProcessable instanceof Piping) {
			((Piping) targetProcessable).addIncomingPipe(pipe);
		}

		shape.initialize();
		
		// TODO [RR]: This is just a hack an prevents recursive call of PipeShape.create()
		if (ids.length == 0)
			Designer.getInstance().add(shape);

		return shape;
	}

	// #############################################################################
	// END CREATOR
	// #############################################################################

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################

	/**
	 * Default constructor at least required for JAXB.
	 */
	public PipeShape() {
		// empty
	}

	@XmlIDREF
	@XmlAttribute(name = "pipe-ref")
	private Pipe pipe;

	/**
	 * @return the pipe
	 */
	public final Pipe getPipe() {
		return pipe;
	}

	/**
	 * @param pipe
	 *            the pipe to set
	 */
	public final void setPipe(Pipe pipe) {
		this.pipe = pipe;
	}

	@XmlIDREF
	@XmlAttribute(name = "source")
	private ConnectorShape<?, ?> source;

	public ConnectorShape<?, ?> getSource() {
		return source;
	}

	public void setSource(ConnectorShape<?, ?> source) {
		this.source = source;
	}

	@XmlIDREF
	@XmlAttribute(name = "target")
	private ConnectorShape<?, ?> target;

	public ConnectorShape<?, ?> getTarget() {
		return target;
	}

	public void setTarget(ConnectorShape<?, ?> target) {
		this.target = target;
	}

	// #############################################################################
	// END JAXB
	// #############################################################################

	// #############################################################################
	// BEGIN Initializable
	// #############################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.Initializable#initialize()
	 */
	public void initialize() {

		if (pipe == null) {
			if (LOG.isErrorEnabled()) {
				LOG
						.error("Could not initialize PipeShape without a pipe instance. Removing from parent.");
			}
			return;
		}

		visualization = new VisualizationShape(this);
		visualization.setScale(0.5);
		addChild(visualization);
		// ShapeUtils.setApparent(visualization, false);

		pipe.addProcessingFeedback(visualization);

		flowIncoming = new DataTypeShape(pipe.getInputTypes());
		flowIncoming.setScale(0.1);
		addChild(flowIncoming);
		// ShapeUtils.setApparent(flowIncoming, false);

		flowOutgoing = new DataTypeShape(pipe.getOutputTypes());
		flowOutgoing.setScale(0.1);
		addChild(flowOutgoing);
		// ShapeUtils.setApparent(flowOutgoing, false);
		
		final PropertyChangeListener changeListener = new PropertyChangeListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * 
			 * @seejava.beans.PropertyChangeListener#propertyChange(java.
			 * beans. PropertyChangeEvent)
			 */
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() != null) {
					setBounds(computeBounds());
					positionVisualization();
				}
			}
		};

		addPropertyChangeListener(PNode.PROPERTY_PARENT, changeListener);

		source.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, changeListener);

		target.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, changeListener);

		pipe.addStatusChangeListener(Processable.STATUS_PROCESSABLE_DELETED,
				new PropertyChangeListener() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * java.beans.PropertyChangeListener#propertyChange(java
					 * .beans.PropertyChangeEvent)
					 */
					public void propertyChange(PropertyChangeEvent evt) {
						
						if (source != null) {
							source.removePropertyChangeListener(changeListener);
						}
						
						if (target != null) {
							target.removePropertyChangeListener(changeListener);
						}
						
						removeFromParent();
					}
				});

		addInputEventListener(new PBasicInputEventHandler() {

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
				// ShapeUtils.setApparent(visualization, true);
				// ShapeUtils.setApparent(flowIncoming, true);
				// ShapeUtils.setApparent(flowOutgoing, true);
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
				// ShapeUtils.setApparent(visualization, false);
				// ShapeUtils.setApparent(flowIncoming, false);
				// ShapeUtils.setApparent(flowOutgoing, false);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mousePressed
			 * (edu.umd.cs.piccolo. event.PInputEvent)
			 */
			@Override
			public void mousePressed(PInputEvent event) {
				super.mousePressed(event);

				if (!event.isHandled()) {
					Point2D p = event.getPosition();
					double x = p.getX();
					double y = p.getY();
					// Check intersection based on mouse position (5 pixel
					// around
					// position)

					// Rectangle2D rectangle = globalToLocal(new
					// Rectangle2D.Double(x, y, 100 * getGlobalScale(),
					// 100 * getGlobalScale()));
					// if (shape.intersects(rectangle)) {
					event.getInputManager().setKeyboardFocus(event.getPath());
					//
					// if (event.isRightMouseButton()) {
					// globalToLocal(p);
					// System.out.println("Do you wan't to create a bendpoint at x="
					// + p.getX() + " / y="
					// + p.getY() + "?");
					// }

					event.setHandled(true);
					// }
				}

				// PNode nextNode = event.getPath().nextPickedNode();
				// if (nextNode != null) {
				// EventListenerList listenerList = nextNode.getListenerList();
				//					
				// if (listenerList != null) {
				// PBasicInputEventHandler[] listeners =
				// listenerList.getListeners(PBasicInputEventHandler.class);
				// for (PBasicInputEventHandler listener : listeners) {
				// listener.mousePressed(event);
				// }
				// }
				// }
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#keyboardFocusGained
			 * (edu.umd.cs.piccolo .event.PInputEvent)
			 */
			@Override
			public void keyboardFocusGained(PInputEvent event) {
				super.keyboardFocusGained(event);
				selected = true;
				moveToFront();
				invalidatePaint();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#keyboardFocusLost
			 * (edu.umd.cs.piccolo .event.PInputEvent)
			 */
			@Override
			public void keyboardFocusLost(PInputEvent event) {
				super.keyboardFocusLost(event);
				selected = false;
				invalidatePaint();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#keyPressed(edu
			 * .umd.cs.piccolo.event .PInputEvent)
			 */
			@Override
			public void keyPressed(PInputEvent event) {
				super.keyPressed(event);

				if (KeyEvent.VK_DELETE == event.getKeyCode()) {
					// DrawingArea drawingArea = (DrawingArea)
					// event.getCamera().getComponent();
					// drawingArea.removeEdge(Edge.this);

					if (LOG.isDebugEnabled()) {
						LOG.debug("Backspace has been pressed. Trigger deletion of edge?");
					}

					pipe.delete();
					Manager.get().notify(getPipe(), Action.DELETE);
				}
			}
		});
	}

	// Rectangle2D rect;

	// #############################################################################
	// END Initializable
	// #############################################################################

	// #############################################################################
	// BEGIN INTERNAL
	// #############################################################################

	private boolean selected = false;

	public Line2D shape = new Line2D.Double();
//	public CubicCurve2D shape = new CubicCurve2D.Double();

	private VisualizationShape visualization;
	private DataTypeShape flowIncoming;
	private DataTypeShape flowOutgoing;

	/**
	 * @param source
	 * @param target
	 */
	public PipeShape(ConnectorShape<?, ?> source, ConnectorShape<?, ?> target) {
		this.source = source;
		this.target = target;
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see edu.umd.cs.piccolo.PNode#pick(edu.umd.cs.piccolo.util.PPickPath)
	// */
	// @Override
	// protected boolean pick(PPickPath pickPath) {
	// PBounds bounds = pickPath.getPickBounds();
	// return shape.intersects(bounds);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.diagram.node.AbstractNode#pickAfterChildren
	 * (edu.umd.cs.piccolo.util.PPickPath)
	 */
	@Override
	protected boolean pickAfterChildren(PPickPath pickPath) {
		PBounds bounds = pickPath.getPickBounds();

		bounds.add(bounds.getMinX() - 50, bounds.getMinY() - 50);
		bounds.add(bounds.getMaxX() + 50, bounds.getMaxY() + 50);
		return shape.intersects(bounds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.diagram.node.AbstractNode#computeFullBounds
	 * (edu.umd.cs.piccolo.util.PBounds)
	 */
	private PBounds computeBounds() {

		// TODO: [RR] Hack!!! Do not compute bounds if ports are invisible.
		// if (source.getOutputPort().getScale() < 1.0 ||
		// target.getOutputPort().getScale() < 1.0) {
		// return getBoundsReference();
		// }

		PortShape sourcePortShape;
		PortShape targetPortShape;
		if (!source.getParent().equals(target.getParent())) {
			if (source instanceof PipelineShape
					&& !(target instanceof PipelineShape)) {
				// ConnectorShape<?, ?> tmp = source;
				// source = target;
				// target = tmp;

				sourcePortShape = source.getInputPort();
				targetPortShape = target.getInputPort();
			} else if (!(source instanceof PipelineShape)
					&& target instanceof PipelineShape) {
				sourcePortShape = source.getOutputPort();
				targetPortShape = target.getOutputPort();
			}
			else {
				// TODO [RR]: This may cause unexpected painting of shapes -> The visual shape drawing does not match the processing hierarchy.
				sourcePortShape = source.getInputPort();
				targetPortShape = target.getInputPort();
			}
		} else {
			sourcePortShape = source.getOutputPort();
			targetPortShape = target.getInputPort();
		}

		// System.out.println("GFB: " + source.getGlobalFullBounds());
		// System.out.println("FBR: " + source.getFullBoundsReference());
		// System.out.println("BR : " + source.getBoundsReference());
		// System.out.println("Off: " + source.getOffset());

		Point2D p1 = globalToLocal(sourcePortShape.getParent().localToGlobal(
				sourcePortShape.getFullBoundsReference().getCenter2D()));
		Point2D p2 = globalToLocal(targetPortShape.getParent().localToGlobal(
				targetPortShape.getFullBoundsReference().getCenter2D()));

		double x1 = p1.getX();
		double x2 = p2.getX();
		double y1 = p1.getY();
		double y2 = p2.getY();

		// Rectangle2D bounds1 =
		// globalToLocal(sourcePortShape.getGlobalFullBounds());
		// Rectangle2D bounds2 =
		// globalToLocal(targetPortShape.getGlobalFullBounds());
		//		
		// double x1 = bounds1.getMinX();
		// double x2 = bounds2.getMinX();
		// double y1 = bounds1.getMinY();
		// double y2 = bounds2.getMinY();

		double x = Math.min(x1, x2);
		double y = Math.min(y1, y2);
		double width = Math.max(Math.abs(x1 - x2), 1);
		double height = Math.max(Math.abs(y1 - y2), 1);

		// Rectangle2D b = localToParent(new PBounds(x, y, width, height));
//		 shape.setCurve(x1, y1, x1 + (width / 2), y1, x2 - (width / 2), y2,
//		 x2, y2);

		shape.setLine(x1, y1 - LINE_WIDTH / 2.0 - 10, x2, y2 - LINE_WIDTH / 2.0
				- 10);

		return new PBounds(shape.getBounds());
	}

	/**
	 * Position the <code>ZoomVisualization</code> at the middle of the edge.
	 */
	private void positionVisualization() {
		PBounds bounds = getBoundsReference();

//		System.out.println(bounds.getCenterY());
		
		PBounds visualizationBounds = visualization.getBounds();
		visualization.localToParent(visualizationBounds);
		visualization.setOffset(bounds.getCenterX()
				- (visualizationBounds.getWidth() / 2), bounds.getCenterY()
				- (visualizationBounds.getHeight() / 2));

		double x1 = shape.getX1();
		double y1 = shape.getY1();
		double x2 = shape.getX2();
		double y2 = shape.getY2();
		PBounds flowIncomingBounds = flowIncoming.getBounds();
		flowIncoming.localToParent(flowIncomingBounds);
		PBounds flowOutgoingBounds = flowOutgoing.getBounds();
		flowOutgoing.localToParent(flowOutgoingBounds);
		
//		PBounds flowIncomingBounds = flowIncoming.getFullBoundsReference();
//		PBounds flowOutgoingBounds = flowOutgoing.getFullBoundsReference();
		
		if (x1 < x2) {
			x1 += (bounds.getWidth() / 4) - (flowIncomingBounds.getWidth() / 2);
			x2 -= (bounds.getWidth() / 4) + (flowOutgoingBounds.getWidth() / 2);
		} else {
			x1 -= (bounds.getWidth() / 4) + (flowIncomingBounds.getWidth() / 2);
			x2 += (bounds.getWidth() / 4) - (flowOutgoingBounds.getWidth() / 2);
		}

		if (y1 < y2) {
			y1 += (bounds.getHeight() / 4)
					- (flowIncomingBounds.getHeight() / 2);
			y2 -= (bounds.getHeight() / 4)
					+ (flowOutgoingBounds.getHeight() / 2);
		} else {
			y1 -= (bounds.getHeight() / 4)
					+ (flowIncomingBounds.getHeight() / 2);
			y2 += (bounds.getHeight() / 4)
					- (flowOutgoingBounds.getHeight() / 2);
		}

		flowIncoming.setOffset(x1, y1);
		flowOutgoing.setOffset(x2, y2);

		// double theta = bounds.getHeight() / bounds.getWidth();
		// theta = Math.atan(theta);
		//		
		// System.out.println("THETA: " + theta);
		//		
		// flowIncoming.setRotation(theta);

		// double theta = bounds.getHeight() / bounds.getWidth();
		// flowIncoming.transformBy(AffineTransform.getRotateInstance(theta,
		// flowIncomingBounds.getCenterX(), flowIncomingBounds.getCenterY()));
		//		
		// double rotationTheta = Math.sin(visualizationBounds.getHeight() /
		// visualizationBounds.getWidth()) * Math.PI;
		// visualization.setRotation(rotationTheta);
	}

	private static final double LINE_WIDTH = 70.0;
	private static final Color COLOR_NORMAL = new Color(120, 120, 120, 50);
	private static final Color COLOR_PROCESSING = new Color(0, 255, 0, 50);
	private static final Color COLOR_FAILURE = new Color(255, 0, 0, 50);
	private static final Color COLOR_NORMAL_SELECTED = new Color(120, 120, 120);
	private static final Color COLOR_PROCESSING_SELECTED = new Color(0, 255, 0);
	private static final Color COLOR_FAILURE_SELECTED = new Color(255, 0, 0);
	private static final Stroke STROKE_SHAPE[] = new Stroke[3];
	static {
		STROKE_SHAPE[0] = new BasicStroke((float) LINE_WIDTH,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
		STROKE_SHAPE[1] = new BasicStroke((float) (LINE_WIDTH - 10.0),
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
		STROKE_SHAPE[2] = new BasicStroke((float) (LINE_WIDTH - 20.0),
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.VisualShape#paintShape(edu.umd.cs.piccolo.
	 * util.PPaintContext)
	 */
	@Override
	protected void paintShape(PPaintContext paintContext) {
		super.paintShape(paintContext);

		Graphics2D g = paintContext.getGraphics();

		boolean processing = getPipe().getSource().isProcessing();
		boolean hasFailure = getSource().hasFailure();
		if (selected) {
			g.setColor(hasFailure ? COLOR_FAILURE_SELECTED
					: (processing ? COLOR_PROCESSING_SELECTED
							: COLOR_NORMAL_SELECTED));
		} else {
			g.setColor(hasFailure ? COLOR_FAILURE
					: (processing ? COLOR_PROCESSING : COLOR_NORMAL));
		}

		for (int i = 0; i < 3; i++) {
			g.setStroke(STROKE_SHAPE[i]);
			g.draw(shape);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.VisualShape#paintDebug(edu.umd.cs.piccolo.
	 * util.PPaintContext)
	 */
	@Override
	protected void paintDebug(PPaintContext paintContext) {
		super.paintDebug(paintContext);

		Graphics2D g = paintContext.getGraphics();

		if (DebugConstants.PAINTING_BOUNDS) {
			g.setColor(DebugConstants.COLOR_BOUNDS);
			g.draw(getBoundsReference());
		}

		if (DebugConstants.PAINTING_FULL_BOUNDS) {
			g.setColor(DebugConstants.COLOR_FULL_BOUNDS);
			g.draw(getFullBounds());
		}

		if (DebugConstants.PAINTING_CENTER) {
			g.setColor(DebugConstants.COLOR_CENTER);

			PBounds bounds = getBoundsReference();
			g.fillOval((int) bounds.getCenterX() - 5,
					(int) bounds.getCenterY() - 5, 10, 10);
		}
	}

	// #############################################################################
	// END INTERNAL
	// #############################################################################
}
