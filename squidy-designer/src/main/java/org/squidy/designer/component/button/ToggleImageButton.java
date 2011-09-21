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

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.net.URL;

import javax.swing.event.EventListenerList;

import org.squidy.designer.event.ZoomToggle;
import org.squidy.designer.event.ZoomToggleActionEvent;
import org.squidy.designer.event.ZoomToggleActionListener;
import org.squidy.designer.util.FontUtils;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ZoomToggleButton</code>.
 * 
 * <pre>
 * Date: Feb 14, 2009
 * Time: 4:25:40 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: ToggleImageButton.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class ToggleImageButton extends VisualButton implements TitledButton {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -7528424887819095042L;

	// The event listener list for this zoom button.
	private EventListenerList listenerList = new EventListenerList();

	/**
	 * Identifies the toggle state of the button.
	 */
	private ZoomToggle toggleState = ZoomToggle.RELEASED;

	/**
	 * @return the toggleState
	 */
	public final ZoomToggle getToggleState() {
		return toggleState;
	}

	/**
	 * @param toggleState
	 *            the toggleState to set
	 */
	public final void setToggleState(ZoomToggle toggleState) {
		this.toggleState = toggleState;
		updateState();
	}

	private PImage image;
	private URL resourceReleased;
	private URL resourcePressed;
	private String title;
	
	private boolean titleVisible = true;

	/**
	 * @return the titleVisible
	 */
	public boolean isTitleVisible() {
		return titleVisible;
	}

	/**
	 * @param titleVisible the titleVisible to set
	 */
	public void setTitleVisible(boolean titleVisible) {
		this.titleVisible = titleVisible;
	}

	/**
	 * @param resourceReleased
	 * @param resourcePressed
	 * @param border
	 */
	public ToggleImageButton(final URL resourceReleased, final URL resourcePressed, boolean border, String title) {
		super(border);

		this.title = title;
		this.resourceReleased = resourceReleased;
		this.resourcePressed = resourcePressed;

		image = new PImage(resourceReleased);
		addChild(image);

		PBounds imageBounds = image.getBoundsReference();
		setWidth(imageBounds.getWidth());
		setHeight(imageBounds.getHeight());

		// image.setOffset(20, 20);

		setChildrenPickable(false);

		addInputEventListener(new PBasicInputEventHandler() {

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

				Point2D point = event.getPositionRelativeTo(ToggleImageButton.this);

				if (!event.isHandled() && getBoundsReference().contains(point)) {
					switch (toggleState) {
					case RELEASED:
						toggleState = ZoomToggle.PRESSED;
						updateState();
						break;
					case PRESSED:
						toggleState = ZoomToggle.RELEASED;
						updateState();
						break;
					}

					ZoomToggleActionListener[] listeners = listenerList.getListeners(ZoomToggleActionListener.class);
					for (ZoomToggleActionListener listener : listeners) {
						listener.toggleActionPerformed(new ZoomToggleActionEvent(ToggleImageButton.this, event
								.getCamera(), toggleState));
					}
				}
			}
		});
	}

	/**
	 * @param resourceReleased
	 * @param resourcePressed
	 */
	public ToggleImageButton(URL resourceReleased, URL resourcePressed, String title) {
		this(resourceReleased, resourcePressed, false, title);
	}

	/**
	 * Update the button's image representation.
	 */
	private final void updateState() {
		switch (toggleState) {
		case RELEASED:
			image.setImage(Toolkit.getDefaultToolkit().getImage(resourceReleased));
			break;
		case PRESSED:
			image.setImage(Toolkit.getDefaultToolkit().getImage(resourcePressed));
			break;
		}
	}

	/**
	 * @param zoomToogleActionListener
	 */
	public void addZoomToggleActionListener(ZoomToggleActionListener zoomToogleActionListener) {
		listenerList.add(ZoomToggleActionListener.class, zoomToogleActionListener);
	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.component.button.VisualButton#paintContent(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintContent(PPaintContext paintContext) {
		super.paintContent(paintContext);

		if (titleVisible) {
			Graphics2D g = paintContext.getGraphics();
			g.setFont(g.getFont().deriveFont(15f));
			
			int textWidth = FontUtils.getWidthOfText(g.getFontMetrics(), title);
			double width = getWidth();
			g.drawString(title, (int) (width / 2 - textWidth / 2), 40);
		}
	}
}
