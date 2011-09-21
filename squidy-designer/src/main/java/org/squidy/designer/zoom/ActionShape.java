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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.component.TemporaryNotification;
import org.squidy.designer.component.button.TitledButton;
import org.squidy.designer.component.button.VisualButton;
import org.squidy.designer.model.PipeShape;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.util.StrokeUtils;
import org.squidy.manager.ILaunchable;
import org.squidy.manager.model.Pipe;
import org.squidy.manager.model.Processable;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ActionShape</code>.
 * 
 * <pre>
 * Date: Feb 2, 2009
 * Time: 12:29:44 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: ActionShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0
 */
public abstract class ActionShape<T extends VisualShape<?>, P extends Processable>
		extends NavigationShape<T> implements ILaunchable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 374617687692996886L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ActionShape.class);

	private boolean polygonRendering = false;

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################

	/**
	 * Default constructor required for JAXB.
	 */
	public ActionShape() {
		super();
		initializeLayout();
	}

	@XmlIDREF
	@XmlAttribute(name = "processable-ref")
	private P processable;

	/**
	 * @return the processable
	 */
	public P getProcessable() {
		return processable;
	}

	/**
	 * @param processable
	 *            the processable to set
	 */
	public void setProcessable(P processable) {
		this.processable = processable;
	}

	// #############################################################################
	// END JAXB
	// #############################################################################

	// #############################################################################
	// BEGIN ILaunchable
	// #############################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.ILaunchable#publishFailure(java.lang.Throwable)
	 */
	public void publishFailure(Throwable e) {
		failure = true;
		invalidatePaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ILaunchable#resolveFailure()
	 */
	public void resolveFailure() {
		failure = false;
		invalidatePaint();
	}

	/**
	 * @return
	 */
	public boolean hasFailure() {
		return failure;
	}

	// #############################################################################
	// END ILaunchable
	// #############################################################################

	// #############################################################################
	// BEGIN Initializable
	// #############################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.shape.ZoomShape#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();

		if (getProcessable() != null) {
			// Add property change listener that allows to start valve
			// processing this way.
			getProcessable().addStatusChangeListener(
					Processable.PROPERTY_PROCESSING,
					new PropertyChangeListener() {

						public void propertyChange(PropertyChangeEvent evt) {
							firePropertyChange(0,
									Processable.PROPERTY_PROCESSING,
									evt.getOldValue(), evt.getNewValue());
						}
					});
			
			getProcessable().addStatusChangeListener(Processable.STATUS_PROCESSABLE_DELETED,
					new PropertyChangeListener() {
						
						public void propertyChange(PropertyChangeEvent evt) {
							if (Pipe.class.isAssignableFrom(evt.getOldValue().getClass()))
								return;
							removeFromParent();
						}
					});

			getProcessable().addStatusChangeListener(
					Processable.PROPERTY_FAILURE_PUBLISH,
					new PropertyChangeListener() {

						/*
						 * (non-Javadoc)
						 * 
						 * @see
						 * java.beans.PropertyChangeListener#propertyChange(
						 * java.beans.PropertyChangeEvent)
						 */
						public void propertyChange(PropertyChangeEvent evt) {
							Throwable e = (Throwable) evt.getNewValue();
							publishFailure(e);
						}
					});

			getProcessable().addStatusChangeListener(
					Processable.PROPERTY_FAILURE_RESOLVE,
					new PropertyChangeListener() {

						/*
						 * (non-Javadoc)
						 * 
						 * @see
						 * java.beans.PropertyChangeListener#propertyChange(
						 * java.beans.PropertyChangeEvent)
						 */
						public void propertyChange(PropertyChangeEvent evt) {
							resolveFailure();

							// for (Object o : getChildrenReference()) {
							// if (o instanceof ZoomActionShape) {
							// ((ZoomActionShape<?>) o).resolveFailure();
							// }
							// }
						}
					});

			getProcessable().addStatusChangeListener(
					Processable.PROPERTY_NOTIFICATION,
					new PropertyChangeListener() {

						/*
						 * (non-Javadoc)
						 * 
						 * @see
						 * java.beans.PropertyChangeListener#propertyChange(
						 * java.beans.PropertyChangeEvent)
						 */
						public void propertyChange(PropertyChangeEvent evt) {
							publishNotification(new TemporaryNotification(evt
									.getNewValue().toString()));
						}
					});
		}
	}

	// #############################################################################
	// END Initializable
	// #############################################################################

	private boolean started = false;
	private boolean failure = false;

	private List<VisualButton> buttons = new ArrayList<VisualButton>();

	/**
	 * @param visualButton
	 */
	public void addAction(VisualButton visualButton) {
		addAction(visualButton, buttons.size());
	}

	/**
	 * @param visualButton
	 * @param index
	 */
	public void addAction(VisualButton visualButton, int index) {
		buttons.add(index, visualButton);
		addChild(visualButton);
	}

	private boolean showActionsOnMouseOver = true;

	/**
	 * @return the showActionsOnMouseOver
	 */
	public final boolean isShowActionsOnMouseOver() {
		return showActionsOnMouseOver;
	}

	/**
	 * @param showActionsOnMouseOver
	 *            the showActionsOnMouseOver to set
	 */
	public final void setShowActionsOnMouseOver(boolean showActionsOnMouseOver) {
		this.showActionsOnMouseOver = showActionsOnMouseOver;
	}

	/**
	 * 
	 */
	public void initializeLayout() {
		addInputEventListener(new PBasicInputEventHandler() {

			// /*
			// * (non-Javadoc)
			// *
			// * @see
			// * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mouseClicked
			// * (edu.umd.cs.piccolo.event.PInputEvent)
			// */
			// @Override
			// public void mouseClicked(PInputEvent event) {
			// super.mouseClicked(event);
			//
			// // Set keyboard request to current path object.
			// // event.getInputManager().setKeyboardFocus(event.getPath());
			//
			// if (event.isLeftMouseButton() && event.getClickCount() == 2) {
			// for (Object child : getChildrenReference()) {
			// if (child instanceof ActionShape<?, ?>) {
			// ((ActionShape<?, ?>) child).setActionsInvisible();
			// }
			// }
			// }
			// }

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

				if (showActionsOnMouseOver && !event.isHandled()) {
					for (int i = 0; i < 4 && i < buttons.size(); i++) {
						VisualButton button = buttons.get(i);
						ShapeUtils.setApparent(button, true);
					}
					event.setHandled(true);
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mouseEntered
			 * (edu.umd.cs.piccolo.event.PInputEvent)
			 */
			@Override
			public void mouseMoved(PInputEvent event) {
				super.mouseMoved(event);

				if (showActionsOnMouseOver && !event.isHandled()) {
					for (int i = 0; i < 4 && i < buttons.size(); i++) {
						VisualButton button = buttons.get(i);
						ShapeUtils.setApparent(button, true);
					}
					event.setHandled(true);
				}
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

				if (!event.isHandled()) {
					if (currentZoomState == ZoomState.ZOOM_OUT) {
						for (VisualButton button : buttons) {
							ShapeUtils.setApparent(button, false);
						}
					}
					event.setHandled(true);
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#keyPressed(edu
			 * .umd.cs.piccolo.event.PInputEvent)
			 */
			@Override
			public void keyPressed(PInputEvent event) {
				super.keyPressed(event);

				// if (!event.isHandled()) {
				// delete();
				// event.setHandled(true);
				// }
			}
		});

		// final ToggleImageButton startProcessing = new
		// ToggleImageButton(ActionShape.class
		// .getResource("/images/24x24/media_play_green.png"), ActionShape.class
		// .getResource("/images/24x24/delete2.png"), "Start");
		// startProcessing.addZoomToggleActionListener(new
		// ZoomToggleActionListener() {
		//
		// /*
		// * (non-Javadoc)
		// *
		// * @see
		// * org.squidy.designer.event.ZoomActionListener#actionPerformed
		// * (org.squidy.designer.event.ZoomActionEvent)
		// */
		// public void toggleActionPerformed(ZoomToggleActionEvent e) {
		// switch (e.getZoomToggle()) {
		// case PRESSED:
		// if (LOG.isDebugEnabled()) {
		// LOG.debug("Start has been pressed for " + getBreadcrumb());
		// }
		//
		// start();
		// doStart();
		//
		// break;
		//
		// // case RELEASED:
		// // if (LOG.isDebugEnabled()) {
		// // LOG.debug("Stop has been pressed for " + getBreadcrumb());
		// // }
		// //
		// // stop();
		// // doStop();
		// //
		// // break;
		// }
		// }
		// });
		//
		// final ToggleImageButton stopProcessing = new
		// ToggleImageButton(ActionShape.class
		// .getResource("/images/24x24/media_stop_red.png"), ActionShape.class
		// .getResource("/images/24x24/delete2.png"), "Stop");
		// stopProcessing.addZoomToggleActionListener(new
		// ZoomToggleActionListener() {
		//
		// /*
		// * (non-Javadoc)
		// *
		// * @see
		// * org.squidy.designer.event.ZoomActionListener#actionPerformed
		// * (org.squidy.designer.event.ZoomActionEvent)
		// */
		// public void toggleActionPerformed(ZoomToggleActionEvent e) {
		// switch (e.getZoomToggle()) {
		// // case PRESSED:
		// // if (LOG.isDebugEnabled()) {
		// // LOG.debug("Start has been pressed for " + getBreadcrumb());
		// // }
		// //
		// // start();
		// // doStart();
		// //
		// // break;
		//
		// case RELEASED:
		// if (LOG.isDebugEnabled()) {
		// LOG.debug("Stop has been pressed for " + getBreadcrumb());
		// }
		//
		// stop();
		// doStop();
		//
		// break;
		// }
		// }
		// });
	}

	/**
	 * 
	 */
	protected void doStart() {
		started = true;
		invalidatePaint();

		for (Object node : getParent().getChildrenReference()) {
			if (node instanceof PipeShape && !this.equals(node)) {
				((PipeShape) node).repaint();
			}
		}
	}

	/**
	 * 
	 */
	protected void doStop() {
		started = false;
		invalidatePaint();

		for (Object node : getParent().getChildrenReference()) {
			if (node instanceof PipeShape && !this.equals(node)) {
				((PipeShape) node).repaint();
			}
		}
	}

	// /**
	// *
	// */
	// protected void setActionsInvisible() {
	// for (VisualButton button : buttons) {
	// ShapeUtils.setApparent(button, false);
	// }
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();

		PBounds bounds = getBounds();

		int i = 0;
		for (VisualButton button : buttons) {
			button.setScale(1.0);
			button.setBorder(false);

			if (button instanceof TitledButton) {
				((TitledButton) button).setTitleVisible(true);
			}

			button.setOffset(bounds.getX() + i * 50 + ((i + 1) * 20),
					bounds.getY() + 10);
			ShapeUtils.setApparent(button, true);
			i++;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedOut()
	 */
	protected void layoutSemanticsZoomedOut() {
		super.layoutSemanticsZoomedOut();

		PBounds bounds = getBoundsReference();

		for (int i = 0; i < buttons.size(); i++) {

			VisualButton button = buttons.get(i);
			button.setScale(8);
			button.setBorder(true);

			if (button instanceof TitledButton) {
				((TitledButton) button).setTitleVisible(false);
			}

			double buttonWidth = button.getWidth() * button.getScale();
			double buttonHeight = button.getHeight() * button.getScale();

			// Calculate overlap of each button which is 1/6.
			double offsetX = buttonWidth / (double) 2;

			// System.out.println("OFFSET: " + offsetX + " | " + offsetY);

			switch (i) {
			case 0:
				button.setOffset(bounds.getX() - offsetX, bounds.getY()
						- offsetX);
				break;
			case 1:
				button.setOffset(bounds.getX() + bounds.getWidth() - offsetX,
						bounds.getY() - offsetX);
				break;
			case 2:
				button.setOffset(bounds.getX() - offsetX, bounds.getY()
						+ bounds.getHeight() - offsetX);
				break;
			case 3:
				button.setOffset(bounds.getX() + bounds.getWidth() - offsetX,
						bounds.getY() + bounds.getHeight() - offsetX);
				break;
			default:
				// ShapeUtils.setApparent(button, false);
				break;
			}

			ShapeUtils.setApparent(button, false);
		}
	}

	private static final Color COLOR_STOPPED = new Color(0, 0, 0, 100);
	private static final Color COLOR_STARTED = new Color(0, 255, 0, 100);
	private static final Color COLOR_FAILURE = new Color(255, 0, 0, 100);
	// private static final Color COLOR_STOPPED = new Color(0, 0, 0);
	// private static final Color COLOR_STARTED = new Color(0, 255, 0);
	// private static final Color COLOR_FAILURE = new Color(255, 0, 0);
	// private static final Stroke STROKE_ZOOMED_IN = new BasicStroke(10f);
	// private static final Stroke STROKE_ZOOMED_OUT = new BasicStroke(30f);

	private RoundRectangle2D shapeZoomedIn = new RoundRectangle2D.Double(0.,
			0., 0., 0., 25., 25.);
	private RoundRectangle2D shapeZoomedOut = new RoundRectangle2D.Double(0.,
			0., 0., 0., 200., 200.);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.TitledZoomShape#paintShapeZoomedOut(edu
	 * .umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShape(PPaintContext paintContext) {
		super.paintShape(paintContext);

		// Graphics2D g = paintContext.getGraphics();
		//
		// RectangularShape r = (RectangularShape) getZoomedOutShape();
		// if (r instanceof RoundRectangle2D) {
		// g.setStroke(new BasicStroke(15f));
		// int step = 40;
		// for (double shift = 30; shift >= 1; shift -= 5) {
		// int test = (int) (shift * step);
		// // g.setColor(new Color(test, test, test));
		//
		// g.setColor(new Color(hasFailure ? 255 : 0, !hasFailure && started ?
		// 255 : 0, 0, (int) (shift * step + 30) - test));
		//
		// // g.fill(new RoundRectangle2D.Double(r.getMinX() - shift,
		// r.getMinY() - shift, r.getMaxX() + 2 * shift, r.getMaxY() + 2 *
		// shift, 20, 20));
		// g.fill(new RoundRectangle2D.Double(r.getMinX() + shift, r.getMinY() +
		// shift, r.getMaxX() + shift, r.getMaxY() + shift, 200, 200));
		// // g.draw(new RoundRectangle2D.Double(r.getMinX() + shift,
		// r.getMinY() + shift, r.getMaxX() + shift, r.getMaxY() + shift, 20,
		// 20));
		// }
		// }
		//
		// g.setColor(Color.WHITE);
		// g.fill(getBoundsReference());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.NavigationShape#paintShapeZoomedIn(edu
	 * .umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedIn(PPaintContext paintContext) {

		Graphics2D g = paintContext.getGraphics();
		RectangularShape r = (RectangularShape) getZoomedOutShape();
		if (r instanceof RoundRectangle2D) {

			// int width = (int)(r.getMaxX()-r.getMinX());
			// int height = (int)(r.getMaxY()-r.getMinY());
			// BufferedImage shadeStarted = new BufferedImage(width, height,
			// BufferedImage.TYPE_INT_RGB);
			// BufferedImage shadeStopped = new BufferedImage(width, height,
			// BufferedImage.TYPE_INT_RGB);
			// BufferedImage shadeFailure = new BufferedImage(width, height,
			// BufferedImage.TYPE_INT_RGB);
			// shadeStarted.getGraphics().setColor(COLOR_STARTED);
			// shadeStopped.getGraphics().setColor(COLOR_STOPPED);
			// shadeFailure.getGraphics().setColor(COLOR_FAILURE);
			// shadeStarted.getGraphics().fillRect(0, 0, width, height);
			// shadeStopped.getGraphics().fillRect(0, 0, width, height);
			// shadeFailure.getGraphics().fillRect(0, 0, width, height);

			// g.setStroke(STROKE_ZOOMED_IN);
			g.setStroke(StrokeUtils.getBasicStroke(7f));

			g.setColor(failure ? COLOR_FAILURE : started ? COLOR_STARTED
					: COLOR_STOPPED);

			if (polygonRendering) {

				if (isRenderPrimitiveRect()) {
					if (!isHierarchicalZoomInProgress()) {

						Polygon shadeHorizontal = new Polygon();
						Polygon shadeVertical = new Polygon();

						double shift = 1;
						shapeZoomedIn.setFrame(r.getMinX() + shift, r.getMinY()
								+ shift, r.getMaxX() + shift, r.getMaxY()
								+ shift);
						Rectangle bounds = shapeZoomedIn.getBounds();
						shadeHorizontal.addPoint(bounds.x, bounds.y
								+ bounds.height);
						shadeHorizontal.addPoint(bounds.x + bounds.width,
								bounds.y + bounds.height);
						shadeVertical.addPoint(bounds.x + bounds.width,
								bounds.y);
						shadeVertical.addPoint(bounds.x + bounds.width,
								bounds.y + bounds.height);
						shift = 5;
						shapeZoomedIn.setFrame(r.getMinX() + shift, r.getMinY()
								+ shift, r.getMaxX() + shift, r.getMaxY()
								+ shift);
						bounds = shapeZoomedIn.getBounds();
						shadeHorizontal.addPoint(bounds.x + bounds.width,
								bounds.y + bounds.height);
						shadeHorizontal.addPoint(bounds.x, bounds.y
								+ bounds.height);
						shadeVertical.addPoint(bounds.x + bounds.width,
								bounds.y + bounds.height);
						shadeVertical.addPoint(bounds.x + bounds.width,
								bounds.y);
						g.fillPolygon(shadeHorizontal);
						g.fillPolygon(shadeVertical);
					}

				} else {
					g.draw(shapeZoomedIn);
				}

			} else {

				for (double shift = 6.; shift >= 1.; shift -= 1.) {
					shapeZoomedIn.setFrame(r.getMinX() + shift, r.getMinY()
							+ shift, r.getMaxX() + shift, r.getMaxY() + shift);
					if (isRenderPrimitiveRect()) {
						if (!isHierarchicalZoomInProgress()) {
							Rectangle bounds = shapeZoomedIn.getBounds();
							// g.drawImage(shadeStarted,null,bounds.x,
							// bounds.y);
							// g.fillRect(bounds.x, bounds.y, bounds.width,
							// bounds.height);
							g.drawLine(bounds.x + 1, bounds.y + bounds.height,
									bounds.x + bounds.width - 3, bounds.y
											+ bounds.height);
							g.drawLine(bounds.x + bounds.width, bounds.y + 1,
									bounds.x + bounds.width, bounds.y
											+ bounds.height - 3);
						}
					} else {
						g.draw(shapeZoomedIn);
					}
				}

			}

		}
		super.paintShapeZoomedIn(paintContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.TitledZoomShape#paintShapeZoomedOut(edu
	 * .umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedOut(PPaintContext paintContext) {

		Graphics2D g = paintContext.getGraphics();
		RectangularShape r = (RectangularShape) getZoomedOutShape();
		if (r instanceof RoundRectangle2D) {
			// g.setStroke(STROKE_ZOOMED_OUT);
			g.setColor(failure ? COLOR_FAILURE : started ? COLOR_STARTED
					: COLOR_STOPPED);
			g.setStroke(StrokeUtils.getBasicStroke(20f));

			if (polygonRendering) {

				if (isRenderPrimitiveRect()) {
					if (!isHierarchicalZoomInProgress()) {

						Polygon shadeHorizontal = new Polygon();
						Polygon shadeVertical = new Polygon();

						double shift = 1;
						shapeZoomedOut.setFrame(r.getMinX() + shift,
								r.getMinY() + shift, r.getMaxX() + shift,
								r.getMaxY() + shift);
						Rectangle bounds = shapeZoomedOut.getBounds();
						shadeHorizontal.addPoint(bounds.x, bounds.y
								+ bounds.height);
						shadeHorizontal.addPoint(bounds.x + bounds.width,
								bounds.y + bounds.height);
						shadeVertical.addPoint(bounds.x + bounds.width,
								bounds.y);
						shadeVertical.addPoint(bounds.x + bounds.width,
								bounds.y + bounds.height);
						shift = 30;
						shapeZoomedOut.setFrame(r.getMinX() + shift,
								r.getMinY() + shift, r.getMaxX() + shift,
								r.getMaxY() + shift);
						bounds = shapeZoomedOut.getBounds();
						shadeHorizontal.addPoint(bounds.x + bounds.width,
								bounds.y + bounds.height);
						shadeHorizontal.addPoint(bounds.x, bounds.y
								+ bounds.height);
						shadeVertical.addPoint(bounds.x + bounds.width,
								bounds.y + bounds.height);
						shadeVertical.addPoint(bounds.x + bounds.width,
								bounds.y);
						g.fillPolygon(shadeHorizontal);
						g.fillPolygon(shadeVertical);
					}

				} else {
					g.draw(shapeZoomedOut);
				}

			} else {

				for (double shift = 30.; shift >= 1.; shift -= 5.) {
					shapeZoomedOut.setFrame(r.getMinX() + shift, r.getMinY()
							+ shift, r.getMaxX() + shift, r.getMaxY() + shift);
					if (isRenderPrimitiveRect()) {
						if (!isHierarchicalZoomInProgress()) {
							Rectangle bounds = shapeZoomedOut.getBounds();
							// g.fillRect(bounds.x, bounds.y, bounds.width,
							// bounds.height);
							g.drawLine(bounds.x + 1, bounds.y + bounds.height,
									bounds.x + bounds.width - 3, bounds.y
											+ bounds.height);
							g.drawLine(bounds.x + bounds.width, bounds.y + 1,
									bounds.x + bounds.width, bounds.y
											+ bounds.height - 3);
						}
					} else {
						g.draw(shapeZoomedOut);
					}
				}
			}
		}
		super.paintShapeZoomedOut(paintContext);
	}
}
