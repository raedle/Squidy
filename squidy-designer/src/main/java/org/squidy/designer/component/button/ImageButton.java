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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.event.EventListenerList;

import org.squidy.designer.event.ZoomActionEvent;
import org.squidy.designer.event.ZoomActionListener;
import org.squidy.designer.util.FontUtils;
import org.squidy.designer.util.ImageUtils;
import org.squidy.designer.util.StrokeUtils;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ZoomButton</code>.
 * 
 * <pre>
 * Date: Feb 1, 2009
 * Time: 9:12:14 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: ImageButton.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0
 */
public class ImageButton extends VisualButton implements TitledButton {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 3818850371254886288L;

	// The event listener list for this zoom button.
	private EventListenerList listenerList = new EventListenerList();

	private BufferedImage imageEnabled;
	private BufferedImage imageDisabled;
	private PImage image;

	private String title;

	private boolean titleVisible = true;

	/**
	 * @return the titleVisible
	 */
	public boolean isTitleVisible() {
		return titleVisible;
	}

	/**
	 * @param titleVisible
	 *            the titleVisible to set
	 */
	public void setTitleVisible(boolean titleVisible) {
		this.titleVisible = titleVisible;
	}

	// Whether the button is enabled or not.
	private boolean enabled = true;

	/**
	 * @return the enabled
	 */
	public final boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		// Set enabled or disabled image.
		image.setImage(enabled ? imageEnabled : imageDisabled);
		invalidatePaint();
	}

	/**
	 * @param resource
	 * @param border
	 */
	public ImageButton(URL resource, String title) {
		super(false);

		this.title = title;

		try {
			imageEnabled = ImageIO.read(resource);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		imageDisabled = ImageUtils.convertToGrayscaleImage(imageEnabled);
		
		image = new PImage(imageEnabled);
		addChild(image);

		PBounds imageBounds = image.getBoundsReference();
		setWidth(imageBounds.getWidth());
		setHeight(imageBounds.getHeight() + 20);

		// image.setOffset(0, 0);

		setChildrenPickable(false);

		addInputEventListener(new PBasicInputEventHandler() {

			@Override
			public void mousePressed(PInputEvent event) {
				super.mousePressed(event);

				if (!event.isHandled()) {
					buttonPaint = buttonPaintPressed;
					invalidatePaint();
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

				Point2D point = event.getPositionRelativeTo(ImageButton.this);

				if (!event.isHandled() && getBoundsReference().contains(point)) {
					ZoomActionListener[] listeners = listenerList.getListeners(ZoomActionListener.class);
					for (ZoomActionListener listener : listeners) {
						listener.actionPerformed(new ZoomActionEvent(ImageButton.this, event.getCamera()));
					}

					buttonPaint = buttonPaintReleased;
					invalidatePaint();
				}
			}
		});
	}

	/**
	 * @param actionListener
	 * 
	 * @see JButton#addActionListener(ActionListener)
	 */
	public void addZoomActionListener(ZoomActionListener zoomActionListener) {
		listenerList.add(ZoomActionListener.class, zoomActionListener);
	}

	private Paint buttonPaint;
	private Paint buttonPaintReleased;
	private Paint buttonPaintPressed;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.component.button.VisualButton#paintBorder(
	 * edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintBorder(PPaintContext paintContext) {

		Graphics2D g = paintContext.getGraphics();

		PBounds bounds = image.getBoundsReference();
		int x = (int) bounds.getX();
		int y = (int) bounds.getY();
		int width = (int) bounds.getWidth();
		int height = (int) bounds.getHeight();

		if (buttonPaintReleased == null) {
			buttonPaintReleased = new GradientPaint(x - 10, y - 10, Color.WHITE, x + width + 20, y + height + 20,
					Color.GRAY);
		}

		if (buttonPaintPressed == null) {
			buttonPaintPressed = new GradientPaint(x - 10, y - 10, Color.GRAY, x + width + 20, y + height + 20,
					Color.WHITE);
		}

		if (buttonPaint == null) {
			buttonPaint = buttonPaintReleased;
		}

		g.setPaint(enabled ? buttonPaint : Color.LIGHT_GRAY);
		if (isRenderPrimitiveRect())
			g.fillRect(x - 4, y - 4, width + 8, height + 8);
		else
			g.fillRoundRect(x - 4, y - 4, width + 8, height + 8, 15, 15);

		Stroke defaultStroke = g.getStroke();
		g.setStroke(StrokeUtils.getBasicStroke(1.0f));
		g.setColor(Color.GRAY);
		if (isRenderPrimitiveRect())
			g.drawRect(x - 4, y - 4, width + 8, height + 8);
		else
			g.drawRoundRect(x - 4, y - 4, width + 8, height + 8, 15, 15);
		g.setStroke(defaultStroke);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.component.button.VisualButton#paintContent
	 * (edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintContent(PPaintContext paintContext) {
		super.paintContent(paintContext);

		if (titleVisible) {
			Graphics2D g = paintContext.getGraphics();

			g.setColor(enabled ? Color.BLACK : Color.GRAY);
			g.setFont(g.getFont().deriveFont(15f));

			int textWidth = FontUtils.getWidthOfText(g.getFontMetrics(), title);
			double width = getWidth();
			g.drawString(title, (int) (width / 2 - textWidth / 2), 40);
		}
	}
}
