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

package org.squidy.designer.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.squidy.designer.constant.Constants;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.util.AffineTransformUtils;
import org.squidy.designer.util.StrokeUtils;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;

/**
 * <code>CropScroll</code>.
 * 
 * <pre>
 * Date: Mar 27, 2009
 * Time: 8:30:17 PM
 * </pre>
 * 
 * @author <pre>
 * Daniel Weidele
 * &lt;a href=&quot;mailto:Daniel.Weidele@uni-konstanz.de&quot;&gt;Daniel.Weidele@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: CropScroll.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class CropScroll extends VisualShape<VisualShape<?>> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 3112648133701151505L;

	private static final int CROP_SCROLLER_GAP = 30;

	public static final String CROP_SCROLLER_UPDATE = "CROP_SCROLLER_UPDATE";

	private CropScroller cropScroller;

	private PNode cropNode;

	private Dimension viewPort;
	
	private Image scrollerImage;
	private Image previousScrollerImage;
	private double scaleY = 1.0;
	private PAffineTransform scaleScrollerYTransform;

//	private double scaleY = 1.0;

	private boolean fitWidth = true;
	
	public CropScroll(final PNode cropNode, final Dimension viewPort, final double scrollerScale, boolean fitWidth) {
		this (cropNode, viewPort, scrollerScale);
		this.fitWidth = fitWidth;
	}
	
	public CropScroll(final PNode cropNode, final Dimension viewPort, final double scrollerScale) {
		this.cropNode = cropNode;
		this.viewPort = viewPort;
		
		addChild(cropNode);

		// PBounds cropNodeBounds = cropNode.getFullBoundsReference();
		// if (cropNodeBounds.getHeight() > viewPort.getHeight() /
		// scrollerScale) {
		// scaleY = viewPort.getHeight() / cropNodeBounds.getHeight();
		// }

		// cropNode.setPickable(false);

		// Create the crop scroller.
		cropScroller = new CropScroller();
		cropScroller.setScale(scrollerScale);
		addChild(cropScroller);
		
		cropNode.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, new PropertyChangeListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
			 * PropertyChangeEvent)
			 */
			public void propertyChange(PropertyChangeEvent evt) {
				cropScroller.updateBounds();
			}
		});

		cropNode.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {

			private boolean scaled = false;
			
			/*
			 * (non-Javadoc)
			 * 
			 * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
			 * PropertyChangeEvent)
			 */
			public void propertyChange(PropertyChangeEvent evt) {
				// TODO [RR]: Improve performance -> will be called each
				// scrolling
				
				if (!fitWidth) {
					return;
				}
				
				PBounds cropNodeFullBounds = (PBounds) evt.getNewValue();
				double scaleY = viewPort.getWidth() / cropNodeFullBounds.getWidth();

				if (!scaled) {
					scaled = true;
					
					cropNode.setScale(scaleY);

//					PBounds cropNodeBounds = cropNode.getFullBoundsReference();
//					if (cropNodeBounds.getHeight() * scaleY > viewPort.getHeight() / scrollerScale) {
//						CropScroll.this.scaleY = viewPort.getHeight() / cropNodeBounds.getHeight();
//					}

					//System.out.println("SCALEY: " + scaleY);

					cropScroller.updateBounds();
				}
			}
		});

		// Add property change listener to allow updates of crop scroller from
		// other components in component hierarchy.
		cropNode.addPropertyChangeListener(CROP_SCROLLER_UPDATE, new PropertyChangeListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
			 * PropertyChangeEvent)
			 */
			public void propertyChange(PropertyChangeEvent evt) {
				previousScrollerImage = scrollerImage;
				scrollerImage = null;
				invalidatePaint();
			}
		});

		setBounds(0, 0, viewPort.getWidth() + (viewPort.getWidth() * scrollerScale) + CROP_SCROLLER_GAP, viewPort
				.getHeight());
		cropScroller.updateBounds();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.VisualShape#paintShape(edu.umd.cs.piccolo.
	 * util.PPaintContext)
	 */
	protected void paintShape(PPaintContext paintContext) {
		super.paintShape(paintContext);
		
//		cropNode.toImage(scrollerImage, Color.WHITE);
		
//		if (scrollerImage == null && cropNode.getWidth() > 0 && cropNode.getHeight() > 0
		PBounds fullBounds = cropNode.getFullBoundsReference();
		if (scrollerImage == null && fullBounds.getWidth() > 0 && fullBounds.getHeight() > 0) {
			scrollerImage = cropNode.toImage();
			scaleScrollerYTransform = null;
		}
		
		Graphics2D g = paintContext.getGraphics();
		g.clip(getBoundsReference());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeedu.umd.cs.piccolo.PNode#paintAfterChildren(edu.umd.cs.piccolo.util.
	 * PPaintContext)
	 */
	@Override
	protected void paintAfterChildren(PPaintContext paintContext) {
		super.paintAfterChildren(paintContext);

		Graphics2D g = paintContext.getGraphics();
		g.setClip(null);
		
		g.setColor(Color.LIGHT_GRAY);
		g.setStroke(new BasicStroke(0.3f));
		
		PBounds cropNodeBounds = cropNode.getFullBoundsReference();
		PBounds bounds = getBoundsReference();
		
		if (isRenderPrimitiveRect())
			g.drawRect((int) bounds.getX(), (int) bounds.getY(), (int) cropNodeBounds.getWidth(), (int) bounds.getHeight());
		else
			g.drawRoundRect((int) bounds.getX(), (int) bounds.getY(), (int) cropNodeBounds.getWidth(), (int) bounds.getHeight(), 2, 2);

		// g.setColor(Color.RED);
		// g.draw(getBoundsReference());
		//		
		// paintContext.pushTransform(cropNode.getTransform());
		// g.setColor(Color.GREEN);
		// g.draw(cropNode.getBoundsReference());
		// paintContext.popTransform(cropNode.getTransform());
		//		
		// paintContext.pushTransform(cropScroller.getTransform());
		// g.setColor(Color.PINK);
		// g.draw(cropScroller.getBoundsReference());
		// paintContext.popTransform(cropScroller.getTransform());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.PNode#fullPick(edu.umd.cs.piccolo.util.PPickPath)
	 */
	@Override
	public boolean fullPick(PPickPath pickPath) {
		Point2D offset = cropNode.getOffset();
		PBounds pickBounds = pickPath.getPickBounds();
		double x = pickBounds.getX() + offset.getX();
		double y = pickBounds.getY() + offset.getY();
		double width = pickBounds.getWidth();
		double height = pickBounds.getHeight();

		if ((getPickable() || getChildrenPickable()) && getFullBoundsReference().intersects(x, y, width, height)) {
			pickPath.pushNode(this);
			pickPath.pushTransform(getTransform());

			Point2D offset2 = cropScroller.getOffset();
			PBounds movedBounds = cropScroller.getBounds();
			movedBounds = cropScroller.getBounds().moveBy(offset2.getX(), offset2.getY());

			if (/* cropScroller.fullIntersects(pickBounds) */pickBounds.intersects(movedBounds)) {
				pickPath.pushNode(cropScroller);
				pickPath.pushTransform(cropScroller.getTransform());
			}
			else if (cropNode.fullPick(pickPath)) {
				return true;
			}
			return true;
		}

		return false;
	}

	// #############################################################################
	// BEGIN INNER CLASSES
	// #############################################################################

	/**
	 * <code>CropScroller</code>.
	 * 
	 * <pre>
	 * Date: Mar 27, 2009
	 * Time: 8:26:49 PM
	 * </pre>
	 * 
	 * @author <pre>
	 * Daniel Weidele
	 * &lt;a href=&quot;mailto:Daniel.Weidele@uni-konstanz.de&quot;&gt;Daniel.Weidele@uni-konstanz.de&lt;/a&gt;
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 * </pre>
	 * @author <pre>
	 * Roman R&amp;aumldle
	 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 * </pre>
	 * 
	 * @version $Id: CropScroll.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	private class CropScroller extends VisualShape<VisualShape<?>> {

		private static final long serialVersionUID = 7634902132303924117L;

		private Shape scrollerOverlay;
		private PAffineTransform scrollerOverlayTranslation = new PAffineTransform();
		
		/**
		 * @param cropNode
		 */
		public CropScroller() {
			
			PBasicInputEventHandler mouseWheelScrollHandler = new PBasicInputEventHandler() {
				@Override
				public void mouseWheelRotated(PInputEvent event) {
					super.mouseWheelRotated(event);

					scrollByDelta(event.getWheelRotation() * 10);
					
					event.setHandled(true);
				}
			};
			
			cropNode.addInputEventListener(mouseWheelScrollHandler);
			addInputEventListener(mouseWheelScrollHandler);
			
			addInputEventListener(new PDragEventHandler() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * edu.umd.cs.piccolo.event.PDragEventHandler#drag(edu.umd.cs
				 * .piccolo.event.PInputEvent)
				 */
				@Override
				protected void drag(PInputEvent event) {
					if (!event.isHandled() && provideScrolling()) {

						PDimension delta = event.getDelta();
						cropScroller.globalToLocal(delta);
						double deltaY = delta.getHeight() / scaleY;

						scrollByDelta(deltaY);
						
						event.setHandled(true);
					}
				}
			});
		}

		/**
		 * @param deltaY
		 */
		private void scrollByDelta(double deltaY) {

			PBounds cropNodeBounds = cropNode.getFullBoundsReference();
			Point2D cropNodeOffset = cropNode.getOffset();

			double offsetY = cropNodeOffset.getY() - deltaY;
			if (offsetY > 0) {
				offsetY = 0;
			}
			else if (cropNodeBounds.getHeight() + cropNodeOffset.getY() - CropScroll.this.getHeight() - deltaY < 0) {
				offsetY = -(cropNodeBounds.getHeight() - CropScroll.this.getHeight());
			}

			cropNode.setOffset(0, offsetY);
			invalidatePaint();
		}

		/**
		 * 
		 */
		public void updateBounds() {
			previousScrollerImage = scrollerImage;
			scrollerImage = null;
			setBounds(cropNode.getFullBounds());
			cropScroller.setOffset(viewPort.getWidth() + CROP_SCROLLER_GAP, 0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.squidy.designer.VisualShape#paintShape(edu.umd.cs.piccolo
		 * .util.PPaintContext)
		 */
		@Override
		protected void paintShape(PPaintContext paintContext) {
			super.paintShape(paintContext);

			if (scrollerImage == null) {
				scrollerImage = previousScrollerImage;
				previousScrollerImage = null;
			}
			
			// Scale crop scroller to fit in y dimension.
			if (scaleScrollerYTransform == null) {
				int imageHeight = scrollerImage.getHeight(null);
				scaleY = (CropScroll.this.getHeight() / CropScroller.this.getScale()) / imageHeight;
				scaleScrollerYTransform = AffineTransformUtils.getScaleInstance(1.0, scaleY < 1 ? scaleY : 1.0);
			}
			paintContext.pushTransform(scaleScrollerYTransform);
			
			Rectangle2D cropScrollBounds = CropScroll.this.getBoundsReference();
			int x = (int) cropScrollBounds.getX();
			int y = (int) cropScrollBounds.getY();
			int width = viewPort.width;
			int height = viewPort.height;

			Graphics2D g = paintContext.getGraphics();

			if (!CropScroll.this.isRenderPrimitive()) {
				
				
				
				//cropNode.fullPaint(paintContext);
				g.drawImage(scrollerImage, 0, 0, null);
			}

			Point2D offset = cropNode.getOffset();
			scrollerOverlayTranslation.setToTranslation(-offset.getX(), -offset.getY());
			paintContext.pushTransform(scrollerOverlayTranslation);

			if (provideScrolling()) {
				g.setStroke(StrokeUtils.getBasicStroke(3.5f));
				
				// g.setColor(Color.GRAY);
				// g.drawRoundRect(x - 3, y, width + 6, height, 10, 10);
				// g.setColor(new Color(200, 200, 200, 100));
				// g.fillRoundRect(x - 3, y, width + 6, height, 10, 10);
				
				if (scrollerOverlay == null) {
					scrollerOverlay = new RoundRectangle2D.Double(x, y, width, height, 20, 20);
				}
				
				Rectangle bounds = scrollerOverlay.getBounds();
				g.setColor(Constants.Color.COLOR_SHAPE_BACKGROUND);
				if (isRenderPrimitiveRect())
					g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
				else
					g.fill(scrollerOverlay);

				g.setColor(Color.GRAY);
				if (isRenderPrimitiveRect())
					g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
				else
					g.draw(scrollerOverlay);
			}
			paintContext.popTransform(scrollerOverlayTranslation);

			// Scale crop scroller to fit in y dimension.
			paintContext.popTransform(scaleScrollerYTransform);
			
//			Rectangle2D cropScrollBounds = CropScroll.this.getBoundsReference();
//			int x = (int) cropScrollBounds.getX();
//			int y = (int) cropScrollBounds.getY();
//			int width = viewPort.width;
//			int height = viewPort.height;
//
//			Graphics2D g = paintContext.getGraphics();
//
//			Point2D offset = cropNode.getOffset();
//			PAffineTransform translation = new PAffineTransform();
//			translation.translate(-offset.getX() * scaleY, -offset.getY() * scaleY);
//			paintContext.pushTransform(translation);
//
//			PAffineTransform transform2 = new PAffineTransform();
//			transform2.scale(1.0, scaleY);
//			paintContext.pushTransform(transform2);
//
////			paintContext.pushTransform(TRANSFORM);
//			if (!CropScroll.this.isRenderPrimitiv()) {
//				cropNode.fullPaint(paintContext);
//			}
////			paintContext.popTransform(TRANSFORM);
//
//			if (provideScrolling()) {
//				g.setStroke(new BasicStroke(0.5f));
//
//				// g.setColor(Color.GRAY);
//				// g.drawRoundRect(x - 3, y, width + 6, height, 10, 10);
//				// g.setColor(new Color(200, 200, 200, 100));
//				// g.fillRoundRect(x - 3, y, width + 6, height, 10, 10);
//
//				g.setColor(Color.BLACK);
//				g.drawRoundRect(1 + x, y, width - 2, height, 2, 2);
//				g.setColor(new Color(200, 200, 200, 100));
//				g.fillRoundRect(x, y, width, height, 2, 2);
//			}
//
//			paintContext.popTransform(transform2);
//
//			paintContext.popTransform(translation);
		}
	}

	/**
	 * Whether scrolling should be provided or not.
	 * 
	 * @return
	 */
	private boolean provideScrolling() {
		return viewPort.getHeight() < cropNode.getFullBoundsReference().getHeight();
	}

	// #############################################################################
	// END INNER CLASSES
	// #############################################################################
}
