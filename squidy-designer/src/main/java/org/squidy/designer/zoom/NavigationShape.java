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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.Designer;
import org.squidy.designer.constant.Constants;
import org.squidy.designer.piccolo.JComponentWrapper;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.util.AffineTransformUtils;
import org.squidy.designer.util.FontUtils;
import org.squidy.designer.util.ShapeUtils;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ZoomNavigationObject</code>.
 * 
 * <pre>
 * Date: Feb 19, 2009
 * Time: 4:40:32 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: NavigationShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public abstract class NavigationShape<T extends VisualShape<?>> extends TitledShape<T> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -5787025974725394517L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(NavigationShape.class);

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################

	private JComponentWrapper titleInputWrapper;

	/**
	 * Default constructor required for JAXB.
	 */
	public NavigationShape() {
		super();

		addInputEventListener(new PBasicInputEventHandler() {

			private JTextField titleInput = new JTextField(getTitle());

			{
				titleInputWrapper = new JComponentWrapper(titleInput);
				titleInput.setFont(fontBreadcrumb);
				addChild(titleInputWrapper);
				// titleInputWrapper.setScale(2);
				ShapeUtils.setApparent(titleInputWrapper, false);

				titleInput.addKeyListener(new KeyAdapter() {

					/**
					 * @param e
					 */
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							if (ShapeUtils.isApparent(titleInputWrapper)) {
								prepareTitleIfNecessary();
							}
						}
					}
				});
			}

			/**
			 * Prepare title if user did new input and title is different than
			 * current title.
			 */
			void prepareTitleIfNecessary() {
				String oldTitle = getTitle();
				String newTitle = titleInput.getText();

				if (!oldTitle.equals(newTitle)) {
					if (changeTitle(oldTitle, newTitle)) {
						setTitle(titleInput.getText());

						ShapeUtils.setApparent(titleInputWrapper, false);

						invalidateTitle();
						invalidateBreadcrumb();
						invalidatePaint();
					}
				}
				else {
					ShapeUtils.setApparent(titleInputWrapper, false);
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mouseClicked
			 * (edu.umd.cs.piccolo.event.PInputEvent)
			 */
			@Override
			public void mouseClicked(PInputEvent event) {

				if (!event.isHandled()) {
					switch (event.getClickCount()) {
					case 1:
						if (!event.getPickedNode().equals(titleInputWrapper)
								&& ShapeUtils.isApparent(titleInputWrapper)) {
							prepareTitleIfNecessary();

							event.setHandled(true);
						}
						break;
					case 2:
						Point2D p = event.getPositionRelativeTo(NavigationShape.this);
						if (titleBounds != null && titleBounds.contains(p)) {
							if (!ShapeUtils.isApparent(titleInputWrapper)) {
								titleInputWrapper.setOffset(titleBounds.getX(), titleBounds.getY());
								titleInput.setText(getTitle());
								// titleInput.requestFocus();

								ShapeUtils.setApparent(titleInputWrapper, true);
							}
							event.setHandled(true);
						}
						break;
					}
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.TitledZoomShape#afterUnmarshal(javax.
	 * xml.bind.Unmarshaller, java.lang.Object)
	 */
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		super.afterUnmarshal(unmarshaller, parent);
	}

	// #############################################################################
	// END JAXB
	// #############################################################################

	private double titleGap = 0;

	/**
	 * @return the titleGap
	 */
	public final double getTitleGap() {
		return titleGap;
	}

	/**
	 * @param titleGap
	 *            the titleGap to set
	 */
	public final void setTitleGap(double titleGap) {
		this.titleGap = titleGap;
	}

	private boolean showNavigation = true;

	/**
	 * @return the showNavigation
	 */
	public final boolean isShowNavigation() {
		return showNavigation;
	}

	/**
	 * @param showNavigation
	 *            the showNavigation to set
	 */
	public final void setShowNavigation(boolean showNavigation) {
		this.showNavigation = showNavigation;
	}

	/**
	 * @param title
	 * @return
	 */
	protected boolean changeTitle(String oldTitle, String newTitle) {
		
		if ("".equals(newTitle.trim())) {
			JOptionPane.showMessageDialog(Designer.getInstance(), "Blank title is not allowed.", "Blank title", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}

	private String breadcrumb;

	/**
	 * @return
	 */
	protected String getBreadcrumb() {
		if (breadcrumb == null) {

			PNode parent = getParent();
			while (parent != null && !(parent instanceof NavigationShape)) {
				parent = parent.getParent();
			}

			if (parent == null) {
				return getTitle();
			}

			breadcrumb = ((NavigationShape<?>) parent).getBreadcrumb();
			if (!"".equals(breadcrumb)) {
				breadcrumb += " / ";
			}

			breadcrumb += getTitle();
		}

		return breadcrumb;
	}

	/**
	 * 
	 */
	protected void invalidateBreadcrumb() {
		breadcrumb = null;
		croppedBreadcrumb = null;

		for (Object o : getChildrenReference()) {
			if (o instanceof NavigationShape<?>) {
				((NavigationShape<?>) o).invalidateBreadcrumb();
			}
		}
	}

	private Rectangle2D titleBounds;

	private PAffineTransform SCALE_TRANSFORM = AffineTransformUtils.getScaleInstance(0.1, 0.1);

	private static Font fontBreadcrumb = internalFont.deriveFont(18f);

	private String croppedBreadcrumb;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ZoomShape#paintShapeZoomedIn(edu.umd.
	 * cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedIn(PPaintContext paintContext) {
		super.paintShapeZoomedIn(paintContext);

		Graphics2D g = (Graphics2D) paintContext.getGraphics();

		if (showNavigation) {// && !isHierarchicalZoomInProgress()) {

			PBounds bounds = getBoundsReference();

			int x = (int) bounds.getX();
			int y = (int) bounds.getY();
			int width = (int) bounds.getWidth();
			int height = (int) bounds.getHeight();

			g.setColor(Constants.Color.COLOR_SHAPE_BACKGROUND);
			if (isRenderPrimitiveRect())
				g.fillRect(x, y, width, 60);
			else {
				g.clearRect(x, y, width, 60);
				g.fillRoundRect(x, y, width, 60, 25, 25);
			}

			g.setColor(Constants.Color.COLOR_SHAPE_BORDER);
			if (isRenderPrimitiveRect())
				g.drawRect(x, y, width, 60);
			else
				g.drawRoundRect(x, y, width, 60, 25, 25);

			g.setFont(fontBreadcrumb);

			if (titleBounds == null) {
				FontMetrics fm = g.getFontMetrics();
				titleBounds = new Rectangle2D.Double(bounds.getX() + 455 + titleGap, bounds.getY() + 25
						- fm.getHeight(), FontUtils.getWidthOfText(fm, getTitle()) + 10, fm.getHeight() + 5);
			}

			// Font font = internalFont.deriveFont(3.2f);
			for (int i = 0; i < 3; i++) {
				if (isRenderPrimitiveRect())
					g.fillRect((int) (bounds.getX() + 430), (int) bounds.getY() + i * 15 + 10, 5, 10);
				else
					g.fillOval((int) (bounds.getX() + 430), (int) bounds.getY() + i * 15 + 10, 5, 10);
			}

			if (!ShapeUtils.isApparent(titleInputWrapper)) {
				g.drawString(getTitle(), (int) (bounds.getX() + 460 + titleGap), (int) (bounds.getY() + 25));
			}

			if (croppedBreadcrumb == null) {
				croppedBreadcrumb = FontUtils.createCroppedLabelIfNecessary(g.getFontMetrics(), getBreadcrumb(),
						(int) bounds.getWidth() * 10 - 450);
			}
			g.drawString(croppedBreadcrumb, (int) (bounds.getX() + 460), (int) (bounds.getY() + 50));
		}
	}
}
