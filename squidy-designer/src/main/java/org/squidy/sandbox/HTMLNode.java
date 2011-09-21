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

package org.squidy.sandbox;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import org.squidy.designer.dragndrop.Draggable;
import org.squidy.designer.shape.ZoomShape;

import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * HTMLNode is a Piccolo node for rendering HTML text.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 * @version $Revision$
 */
public class HTMLNode extends ZoomShape implements Draggable {

	// ----------------------------------------------------------------------------
	// Class data
	// ----------------------------------------------------------------------------

	private static final Font DEFAULT_FONT = new JLabel().getFont();
	private static final Color DEFAULT_HTML_COLOR = Color.BLACK;

	// ----------------------------------------------------------------------------
	// Instance data
	// ----------------------------------------------------------------------------

	private String html;
	private Font font;
	private Color htmlColor;
	private JLabel htmlLabel;
	private View htmlView;
	private final Rectangle htmlBounds; // BasicHTML$Renderer.paint requires a
										// Rectangle

	// ----------------------------------------------------------------------------
	// Constructors
	// ----------------------------------------------------------------------------

	public HTMLNode() {
		this(null, DEFAULT_FONT, DEFAULT_HTML_COLOR);
	}

	public HTMLNode(String html) {
		this(html, DEFAULT_FONT, DEFAULT_HTML_COLOR);
	}

	public HTMLNode(String html, Color htmlColor) {
		this(html, DEFAULT_FONT, htmlColor);
	}

	public HTMLNode(String html, Font font, Color htmlColor) {
		this.html = html;
		this.font = font;
		this.htmlColor = htmlColor;
		htmlLabel = new JLabel();
		htmlBounds = new Rectangle();
		update();
	}

	// ----------------------------------------------------------------------------
	// Accessors
	// ----------------------------------------------------------------------------

	/**
	 * Gets the HTML string.
	 * 
	 * @return HTML string
	 */
	public String getHTML() {
		return html;
	}

	/**
	 * Sets the HMTL string.
	 * 
	 * @param html
	 */
	public void setHTML(String html) {
		if ((this.html != null && html == null) || (this.html == null && html != null) || (!this.html.equals(html))) {
			this.html = html;
			update();
		}
	}

	/**
	 * Gets the font.
	 * 
	 * @return the font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets the font.
	 * 
	 * @param font
	 */
	public void setFont(Font font) {
		this.font = font;
		update();
	}

	/**
	 * Gets the color used to render the HTML. If you want to get the paint used
	 * for the node, use getPaint.
	 * 
	 * @return the color used to render the HTML.
	 */
	public Color getHTMLColor() {
		return htmlColor;
	}

	/**
	 * Sets the color used to render the HTML. If you want to set the paint used
	 * for the node, use setPaint.
	 * 
	 * @param color
	 */
	public void setHTMLColor(Color color) {
		htmlColor = color;
		update();
	}

	// ----------------------------------------------------------------------------
	// Update handler
	// ----------------------------------------------------------------------------

	/*
	 * Updates everything that is involved in rendering the HTML string. This
	 * method is called when one the HTML-related properties is modified.
	 */

	private void update() {
		htmlLabel.setText(html);
		htmlLabel.setFont(font);
		htmlLabel.setForeground(htmlColor);
		htmlLabel.setSize(htmlLabel.getPreferredSize());
		htmlView = BasicHTML.createHTMLView(htmlLabel, html == null ? "" : html);
		htmlBounds.setRect(0, 0, htmlView.getPreferredSpan(View.X_AXIS), htmlView.getPreferredSpan(View.Y_AXIS));
		setBounds(htmlBounds);
		repaint();
	}

	// ----------------------------------------------------------------------------
	// PNode overrides
	// ----------------------------------------------------------------------------

	/**
	 * Paints the node. The HTML string is painted last, so it appears on top of
	 * any child nodes.
	 * 
	 * @param paintContext
	 * @see org.squidy.designer.shape.ZoomShape#paintShape(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShape(PPaintContext paintContext) {
		super.paintShape(paintContext);
		
		if (htmlLabel.getWidth() == 0 || htmlLabel.getHeight() == 0) {
			return;
		}
		Graphics2D g2 = paintContext.getGraphics();
		htmlView.paint(g2, htmlBounds);
	}
}