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

package org.squidy.designer.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.Storable;
import org.squidy.database.BaseXSessionProvider;
import org.squidy.designer.Designer;
import org.squidy.designer.DesignerPreferences;
import org.squidy.designer.Initializable;
import org.squidy.designer.constant.Constants;
import org.squidy.designer.dragndrop.Draggable;
import org.squidy.designer.event.MultiSelectionHandler;
import org.squidy.designer.knowledgebase.AdvancedKnowledgeBase;
import org.squidy.designer.model.IModelStore;
import org.squidy.designer.model.PipeShape;
import org.squidy.designer.model.WorkspaceShape;
import org.squidy.designer.shape.modularity.NodeBased;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.zoom.ZoomState;
import org.squidy.designer.zoom.Zoomable;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ZoomShape</code>.
 * 
 * <pre>
 * Date: Jan 31, 2009
 * Time: 6:55:05 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: ZoomShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public abstract class ZoomShape<T extends VisualShape<?>> extends
		VisualShape<T> implements Initializable, Zoomable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -2712173601846983666L;

	// Logger to info, error, debug,... messages.
	private static Log LOG = LogFactory.getLog(ZoomShape.class);

	public static final String ZOOM_BEGAN = "ZOOM_BEGAN";
	public static final String ZOOM_ENDED = "ZOOM_ENDED";

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################

	/**
	 * 
	 */
	public ZoomShape() {
		this(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.VisualShape#afterUnmarshal(javax.xml.bind.
	 * Unmarshaller, java.lang.Object)
	 */
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		super.afterUnmarshal(unmarshaller, parent);
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
		super.initialize();
		
		for (VisualShape<?> child : getChildren()) {
			if (child instanceof Initializable) {
				((Initializable) child).initialize();
			}
		}
	}

	// #############################################################################
	// END Initializable
	// #############################################################################

	// #############################################################################
	// BEGIN INTERNAL
	// #############################################################################

	/**
	 * Reflects the current zoom state of the zoom shape.
	 * 
	 * @see this{@link #layoutSemantics(ZoomState)}
	 * @see this{@link #layoutSemanticsZoomedIn()}
	 * @see this{@link #layoutSemanticsZoomedOut()}
	 */
	protected ZoomState currentZoomState;

	private boolean zoomInProgress = false;

	/**
	 * @return
	 * 
	 *         TODO [RR]: Please review because of performance aspects!!!!
	 */
	public boolean isZoomInProgress() {
		return zoomInProgress;
	}

	/**
	 * Returns whether a zoom activity is in progress. If no zoom activity is in
	 * progress for this node a hierarchical search will be performed for parent
	 * node and its activity.
	 * 
	 * @return Whether a zoom activity is in progress or not.
	 * 
	 *         TODO [RR]: Please review because of performance aspects!!!!
	 */
	public boolean isHierarchicalZoomInProgress() {
		if (!zoomInProgress) {

			boolean parentZoomInProgress = false;
			PNode parent = getParent();
			while (parent != null && !parentZoomInProgress) {
				if (parent instanceof ZoomShape<?>) {
					parentZoomInProgress = ((ZoomShape<?>) parent)
							.isZoomInProgress();

					if (parentZoomInProgress) {
						return parentZoomInProgress;
					}
				}

				parent = parent.getParent();
			}
		}

		boolean childZoomInProgress = false;
		for (Object o : getChildrenReference()) {
			if (o instanceof ZoomShape) {
				childZoomInProgress = ((ZoomShape<?>) o)
						.isHierarchicalZoomInProgress();

				if (childZoomInProgress) {
					return childZoomInProgress;
				}
			}
		}

		return zoomInProgress;
	}

	private Shape multiSelection;
	private boolean allowMultiSelection = true;

	/**
	 * @return the allowMultiSelection
	 */
	public final boolean isAllowMultiSelection() {
		return allowMultiSelection;
	}

	/**
	 * @param allowMultiSelection
	 *            the allowMultiSelection to set
	 */
	public final void setAllowMultiSelection(boolean allowMultiSelection) {
		this.allowMultiSelection = allowMultiSelection;
	}

	private PNode knowledgeBase;

	/**
	 * @param goalDirectedZoom
	 */
	public ZoomShape(boolean goalDirectedZoom) {
		setBounds(Constants.DEFAULT_NODE_BOUNDS);

		if (goalDirectedZoom) {
			addInputEventListener(new PBasicInputEventHandler() {

				@Override
				public void mouseClicked(PInputEvent event) {
					if (!event.isHandled() && event.isLeftMouseButton()
							&& event.getClickCount() == 2) {

						PNode store = event.getPickedNode();
						while (store != null && !(store instanceof IModelStore)) {
							store = store.getParent();
						}

						PNode node = event.getPickedNode();
						while (node != null && !(node instanceof ZoomShape<?>)) {
							node = node.getParent();
						}

						if (node instanceof ZoomShape<?>) {

//							if (LOG.isDebugEnabled()) {
//								 LOG.debug("Selected node to zoom is " + node + " and this is " + ZoomShape.this);
//							}

							node.moveToFront();

//							PBounds boundsView = event.getCamera()
//									.getViewBounds();
//							PBounds boundsNode = ((ZoomShape<?>) node)
//									.getGlobalBoundsZoomedIn();
//							boolean sameNode = (Math.abs(boundsView.x
//									- boundsNode.x) < 0.1 && Math
//									.abs(boundsView.width - boundsNode.width) < 0.1)
//									|| (Math.abs(boundsView.y - boundsNode.y) < 0.1 && Math
//											.abs(boundsView.height
//													- boundsNode.height) < 0.1);
//
//							if (!sameNode) {
								if (store != null) {
									((IModelStore) store)
											.getModel()
											.setZoomedShape((ZoomShape<?>) node);
									((IModelStore) store).getModel()
											.setZoomedBounds(
													getGlobalBoundsZoomedIn());
								}
								animateToCenterView(event.getCamera());
//							}

							event.setHandled(true);
						}
					}
				}
			});
		}

		if (this instanceof Draggable) {
			addInputEventListener(new PDragEventHandler() {

				private PNode draggable;

				/*
				 * (non-Javadoc)
				 * 
				 * @seeedu.umd.cs.piccolo.event.PDragEventHandler#
				 * shouldStartDragInteraction
				 * (edu.umd.cs.piccolo.event.PInputEvent)
				 */
				@Override
				protected boolean shouldStartDragInteraction(PInputEvent event) {
					if (!event.isHandled()) {
						PNode node = event.getPickedNode();
						while (node != null && !(node instanceof Draggable)) {
							node = node.getParent();
						}
						// Set dragged node to allow drag transformation.
						draggable = node;
						if (node instanceof Draggable) {
							if (((Draggable) node).isDraggable()
									&& super.shouldStartDragInteraction(event)) {
								return true;
							}
						}
						return false;
					}
					return false;
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * edu.umd.cs.piccolo.event.PDragEventHandler#drag(edu.umd.cs
				 * .piccolo .event.PInputEvent)
				 */
				@Override
				protected void drag(PInputEvent event) {
					if (!event.isHandled()) {
						if (!event.getPath().acceptsNode(draggable)) {
							if (LOG.isDebugEnabled()) {
								LOG.debug("Pick path doesn't accept node "
										+ draggable.getClass().getName() + ".");
							}
							return;
						}

						PNode parent = draggable.getParent();

						PDimension d = event.getDeltaRelativeTo(draggable);
						draggable.localToParent(d);

						PBounds parentBounds = parent.getBoundsReference();
						Rectangle2D draggableBounds = draggable
								.localToParent(draggable.getBounds().moveBy(
										d.getWidth(), d.getHeight()));

						if (parentBounds.contains(draggableBounds)) {
							draggable.offset(d.getWidth(), d.getHeight());
						}

						Point2D offset = draggable.getOffset();
						if (offset.getX() < 0) {
							draggable.setOffset(0, offset.getY());
						}
						if (offset.getY() < 0) {
							draggable.setOffset(offset.getX(), 0);
						}
						if (offset.getX() > parentBounds.getWidth()
								- draggableBounds.getWidth()) {
							draggable
									.setOffset(parentBounds.getWidth()
											- draggableBounds.getWidth(),
											offset.getY());
						}
						if (offset.getY() > parentBounds.getHeight()
								- draggableBounds.getHeight()) {
							draggable.setOffset(offset.getX(), parentBounds
									.getHeight()
									- draggableBounds.getHeight());
						}
						
						event.setHandled(true);
					}

					// if (!event.isHandled()) {
					// if (!event.getPath().acceptsNode(draggable)) {
					// if (LOG.isDebugEnabled()) {
					// LOG.debug("Pick path doesn't accept node " +
					// draggable.getClass().getName() + ".");
					// }
					// return;
					// }
					//
					// Point2D current =
					// event.getPositionRelativeTo(ZoomShape.this);
					// draggable.localToParent(current);
					//						
					// Point2D dest = new Point2D.Double();
					//						
					// dest.setLocation((current.getX()), (current.getY()));
					//				
					// dest.setLocation(dest.getX() - (dest.getX() % 20),
					// dest.getY() - (dest.getY() % 20));
					//						
					// // dest.setLocation(nodeStartPosition.getX() - (d.getX()
					// % 20), nodeStartPosition.getY() - (d.getY() % 20));
					//						
					// System.out.println("OFFSET: " + dest);
					//						
					// draggable.setOffset(dest.getX(), dest.getY());
					//
					// // }
					// event.setHandled(true);
					// }
				}
			});
		}

		MultiSelectionHandler multiSelectionHandler = new MultiSelectionHandler() {
			
			/* (non-Javadoc)
			 * @see org.squidy.designer.event.MultiSelectionHandler#selectionAllowed(edu.umd.cs.piccolo.event.PInputEvent)
			 */
			@Override
			protected boolean selectionAllowed(PInputEvent event) {
				PNode node = event.getPickedNode();
				return node instanceof Draggable && !((Draggable) node).isDraggable();
			}
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.designer.event.MultiSelectionHandler#startSelection
			 * (edu.umd.cs.piccolo.event.PInputEvent, java.awt.Shape)
			 */
			@Override
			public void startSelection(PInputEvent event, Shape selectionShape) {
				if (!event.isHandled()) {
					multiSelection = selectionShape;

					event.setHandled(true);
					invalidatePaint();
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.designer.event.MultiSelectionHandler#selection
			 * (edu.umd.cs.piccolo.event.PInputEvent, java.awt.Shape)
			 */
			@Override
			public void selection(PInputEvent event, Shape selectionShape) {
				if (!event.isHandled()) {
					multiSelection = selectionShape;

					event.setHandled(true);
					invalidatePaint();
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.designer.event.MultiSelectionHandler#endSelection
			 * (edu.umd.cs.piccolo.event.PInputEvent, java.awt.Shape)
			 */
			@Override
			public void endSelection(PInputEvent event, Shape selectionShape) {
				if (!event.isHandled()) {
					multiSelection = null;

					for (Object o : getChildrenReference()) {
						if (o instanceof VisualShape<?>) {
							VisualShape<?> shape = (VisualShape<?>) o;
							
							PBounds bounds = shape.getGlobalFullBounds();
							if (selectionShape.contains(bounds)) {
								System.out.println("containing: " + shape);
							}
						}
					}
					
					event.setHandled(true);
					invalidatePaint();
				}
			}
		};

//		 addInputEventListener(multiSelectionHandler);

		// Add knowledge base to zoom object if class is of type
		// <code>KnowledgeBased</code>.
		if (this instanceof NodeBased<?>) {
			boolean isWorkspace = this instanceof WorkspaceShape;

			knowledgeBase = new AdvancedKnowledgeBase<ZoomShape<VisualShape<?>>>(
					isWorkspace);

			addChild(knowledgeBase);
			knowledgeBase.setOffset(0, 895);
		}
	}

	/**
	 * @return
	 */
	private PBounds getGlobalBoundsZoomedIn() {
		double border = 50.0 * getGlobalScale();
		PBounds bounds = getGlobalBounds();
		bounds.add(bounds.getMinX() - border, bounds.getMinY() - border);
		bounds.add(bounds.getMaxX() + border, bounds.getMaxY() + border);
		return bounds;
	}

	private RoundRectangle2D shapeZoomedOut = new RoundRectangle2D.Double(0.,
			0., 0., 0., 150., 150.);

	/**
	 * @return
	 */
	protected Shape getZoomedOutShape() {
		shapeZoomedOut.setFrame(getBoundsReference().getFrame());
		return shapeZoomedOut;
	}

	private GradientPaint paintZoomedOutFill = new GradientPaint(0f, 0f,
			Color.WHITE, (float) Constants.DEFAULT_NODE_BOUNDS.getWidth(),
			(float) Constants.DEFAULT_NODE_BOUNDS.getHeight(), Color.LIGHT_GRAY);

	/**
	 * @return
	 */
	protected Paint getZoomedOutFillPaint() {
		return paintZoomedOutFill;
	}

	/**
	 * @return
	 */
	protected Paint getZoomedOutDrawPaint() {
		return DesignerPreferences.STATUS_STABLE;
	}

	private RoundRectangle2D shapeZoomedIn = new RoundRectangle2D.Double(0.,
			0., 0., 0., 25., 25.);

	/**
	 * @return
	 */
	protected Shape getZoomedInShape() {
		shapeZoomedIn.setFrame(getBoundsReference().getFrame());
		return shapeZoomedIn;
	}

	/**
	 * @return
	 */
	protected Paint getZoomedInFillPaint() {
		return Color.WHITE;
	}

	/**
	 * @return
	 */
	protected Paint getZoomedInDrawPaint() {
		return DesignerPreferences.STATUS_STABLE;
	}

	/**
	 * 
	 */
	protected void zoomBegan() {
		Storable storable = ShapeUtils.getObjectInHierarchy(Storable.class,
				this);
		if (storable != null) {
			storable.store();
		}
		firePropertyChange(0, ZOOM_BEGAN, false, true);
	}

	/**
	 * 
	 */
	protected void zoomEnded() {
		if (getParent() != null) {
			// Set pipe shapes invisible and not pickable.
			for (Object child : getParent().getChildrenReference()) {
				if (child instanceof PipeShape) {
					// ShapeUtils.setApparent((PipeShape) child, false);
					// ((PipeShape) child).invalidateLayout();
					// ((PipeShape) child).invalidateFullBounds();
					// ((PipeShape) child).invalidatePaint();
				}
			}
		}
		firePropertyChange(0, ZOOM_ENDED, false, true);
	}

	private PActivityDelegate delegateAnimateToCenterView = new PActivityDelegate() {

		/*
		 * (non-Javadoc)
		 * 
		 * @seeedu.umd.cs.piccolo.activities.PActivity.PActivityDelegate#
		 * activityStarted(edu.umd.cs.piccolo.activities.PActivity)
		 */
		public void activityStarted(PActivity activity) {
			zoomInProgress = true;
			zoomBegan();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeedu.umd.cs.piccolo.activities.PActivity.PActivityDelegate#
		 * activityFinished(edu.umd.cs.piccolo.activities.PActivity)
		 */
		public void activityFinished(PActivity activity) {
			zoomInProgress = false;
			zoomEnded();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeedu.umd.cs.piccolo.activities.PActivity.PActivityDelegate#
		 * activityStepped(edu.umd.cs.piccolo.activities.PActivity)
		 */
		public void activityStepped(PActivity activity) {
			zoomInProgress = true;
		}
	};

	/**
	 * Animates the current shape to the maximum bounds of the camera.
	 * 
	 * @param camera
	 *            The camera that gets the current shape as maximum bounds to be
	 *            viewed.
	 */
	public void animateToCenterView(PCamera camera) {

		// Set current node as NOT draggable.
		if (this instanceof Draggable) {
			((Draggable) this).setDraggable(false);
		}

		// Set all children of current node as draggable.
		for (Object child : getChildrenReference()) {
			if (child instanceof Draggable) {
				((Draggable) child).setDraggable(true);
			}
		}

		// Start zoom and mark current shape as zoom in progress.
		PTransformActivity activity = camera.animateViewToCenterBounds(
				getGlobalBoundsZoomedIn(), true, 500);
		activity.setDelegate(delegateAnimateToCenterView);
	}

	private static final Color COLOR_MULTI_SELECTION = new Color(0, 0, 255, 20);
	private static final Stroke STROKE_MULTI_SELECTION = new BasicStroke(0.1f);
	private static final Stroke STROKE_SEMANTIC_ZOOM = new BasicStroke(10f);
	private static final Stroke STROKE_ZOOM_OUT = new BasicStroke(25f);
	private static final Stroke STROKE_ZOOMED_OUT = new BasicStroke(5f); // SketchyStroke(40f,
																			// 25f,
																			// 5f)
	private static final Stroke STROKE_ZOOMED_IN = new BasicStroke(5f); // SketchyStroke(8f,
																		// 5f,
																		// 1f)

	@Override
	protected void paintAfterChildren(PPaintContext paintContext) {
		super.paintAfterChildren(paintContext);

		Graphics2D g = paintContext.getGraphics();

		if (multiSelection != null) {
			g.setColor(COLOR_MULTI_SELECTION);
			g
					.fill(globalToLocal((Rectangle2D) ((RectangularShape) multiSelection)
							.clone()));
			g.setColor(Color.BLUE);
			// System.out.println(getGlobalScale());
			g.setStroke(STROKE_MULTI_SELECTION);
			g
					.draw(globalToLocal((Rectangle2D) ((RectangularShape) multiSelection)
							.clone()));
		}
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
		double scale = paintContext.getScale();

		g.setColor(Color.BLACK);
		if (scale > Constants.SEMANTIC_ZOOM_SCALE) {
			g.setStroke(STROKE_SEMANTIC_ZOOM);
			layoutSemantics(ZoomState.ZOOM_IN);
			paintShapeZoomedIn(paintContext);

		} else {
			g.setStroke(STROKE_ZOOM_OUT);
			layoutSemantics(ZoomState.ZOOM_OUT);
			paintShapeZoomedOut(paintContext);
		}
	}

	/**
	 * @param paintContext
	 */
	protected void paintShapeZoomedOut(PPaintContext paintContext) {

		Shape shapeZoomedOut = getZoomedOutShape();
		if (shapeZoomedOut != null) {
			Graphics2D g = paintContext.getGraphics();
			Rectangle bounds = shapeZoomedOut.getBounds();

			g.setPaint(getZoomedOutFillPaint());
			if (isRenderPrimitiveRect())
				g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			else
				g.fill(shapeZoomedOut);

			g.setStroke(STROKE_ZOOMED_OUT);
			g.setPaint(getZoomedOutDrawPaint());
			if (isRenderPrimitiveRect())
				g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
			else
				g.draw(shapeZoomedOut);
		}
	}

	/**
	 * @param paintContext
	 */
	protected void paintShapeZoomedIn(PPaintContext paintContext) {

		Shape shapeZoomedIn = getZoomedInShape();
		if (shapeZoomedIn != null) {
			Graphics2D g = paintContext.getGraphics();
			Rectangle bounds = shapeZoomedIn.getBounds();

			g.setPaint(getZoomedInFillPaint());
			if (isRenderPrimitiveRect())
				g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			else
				g.fill(shapeZoomedIn);

			g.setStroke(STROKE_ZOOMED_IN);
			g.setPaint(getZoomedInDrawPaint());
			if (isRenderPrimitiveRect())
				g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
			else
				g.draw(shapeZoomedIn);
		}
	}

	/**
	 * Layout the shape for a current zoom state. This method delegates either
	 * to this{@link #layoutSemanticsZoomedIn()} or this
	 * {@link #layoutSemanticsZoomedOut()} method.
	 * 
	 * @param zoomState
	 *            The current progressed zoom state.
	 * 
	 * @see this{@link #currentZoomState}
	 * @see this{@link #layoutSemanticsZoomedIn()}
	 * @see this{@link #layoutSemanticsZoomedOut()}
	 */
	protected void layoutSemantics(ZoomState zoomState) {

		// Simple case. Returns if no zoom state change occured.
		if (currentZoomState == zoomState) {
			return;
		}

		currentZoomState = zoomState;

		switch (currentZoomState) {
		case ZOOM_IN:
			layoutSemanticsZoomedIn();
			break;
		case ZOOM_OUT:
			layoutSemanticsZoomedOut();
			break;
		}
	}

	/**
	 * Allows to layout semantics for current zoomed in shape.
	 */
	protected void layoutSemanticsZoomedIn() {

		// Knowledge base.
		if (knowledgeBase != null) {
			ShapeUtils.setApparent(knowledgeBase, true);
		}
	}

	/**
	 * Allows to layout semantics for current zoomed out shape.
	 */
	protected void layoutSemanticsZoomedOut() {

		// Knowledge base.
		if (knowledgeBase != null) {
			ShapeUtils.setApparent(knowledgeBase, false);
		}
	}

	// #############################################################################
	// END INTERNAL
	// #############################################################################
}
