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

package org.squidy.designer.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.zoom.ActionShape;



/**
 * <code>DrawableString</code>.
 * 
 * <pre>
 * Date: 21.08.2009
 * Time: 14:06:42
 * </pre>
 * 
 * 
 * @author
 * Toni Zeitler
 * <a href="mailto:anton.zeitler@campus.lmu.de">anton.zeitler@campus.lmu.de</a>
 * Media Informatics Group
 * University of Munich (LMU)
 * 
 * @version $Id: DrawableString.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class DrawableString {

	public enum AlignmentH {
		LEFT, CENTER, RIGHT
	}
	public enum AlignmentV {
		TOP, CENTER, BOTTOM
	}
	
	// input
	private String     value;
	private Color      color;
	private Font       font;
	private Rectangle  bounds;
	private AlignmentH alignmentH = AlignmentH.CENTER; 
	private AlignmentV alignmentV = AlignmentV.CENTER;
	private int        offsetX;
	private int        offsetY;
	
	// calculated
	private int        positionX;
	private int        positionY;
	private String     drawValue;
	private boolean    dirty = true;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(DrawableString.class);


	private class Cache {

		private class Item {
			public String drawValue;
			public int    positionX;
		
			public Item(String drawValue, int positionX) {
				super();
				this.drawValue = drawValue;
				this.positionX = positionX;
			}
		}
		
		private Item items[][][];
		
		public Cache() {
			super();
			items = new Item[100][][];
		}

		public void put(String drawValue, int positionX, double scale) {
			if (scale >= 100.0)
				return;

			int i2 = (int)(scale * 100.0);
			int i1 = i2 / 10;
			int i0 = i1 / 10;
			i2 -= i1 * 10;
			i1 -= i0 * 10;
			
			if (items[i0] == null)
				items[i0] = new Item[10][];
			if (items[i0][i1] == null)
				items[i0][i1] = new Item[10];
			if (items[i0][i1][i2] == null)
				items[i0][i1][i2] = new Item(drawValue, positionX);
			else {
				items[i0][i1][i2].drawValue = drawValue;
				items[i0][i1][i2].positionX = positionX;
			}
		}

		public Item get(double scale) {
			if (scale >= 100.0)
				return null;

			int i2 = (int)(scale * 100.0);
			int i1 = i2 / 10;
			int i0 = i1 / 10;
			i2 -= i1 * 10;
			i1 -= i0 * 10;

			if (items[i0] == null)
				return null;
			if (items[i0][i1] == null)
				return null;
			if (items[i0][i1][i2] == null)
				return null;
			return items[i0][i1][i2];
		}
	}

	private Cache cache;

	
	/**
	 * 
	 */
	public DrawableString() {
		super();
	}

	/**
	 * @param fullString
	 */
	public DrawableString(String fullString) {
		this.value = fullString;
	}

	/**
	 * @param fullString
	 * @param color
	 * @param font
	 * @param bounds
	 */
	public DrawableString(String fullString, Color color, Font font, Rectangle bounds) {
		this.value  = fullString;
		this.color  = color;
		this.font   = font;
		this.bounds = bounds;
	}
	
	/**
	 * @param g
	 */
	private void update(Graphics2D g, double viewScale) {
		Cache.Item item;

		// use screen if no bounds set
		if (bounds == null) {
			bounds = g.getDeviceConfiguration().getBounds();
		}

		// get item extents from cache or calculate
		if (cache == null || dirty) {
			cache = new Cache();
			item = null;
		} else {
			item = cache.get(viewScale);
		}

		if (item != null) {
			drawValue = item.drawValue;
			positionX = item.positionX;
		} else {
			FontMetrics fm;
			int width, height;

			// font width does not scale linear to view scale
			fm = g.getFontMetrics();
			drawValue = FontUtils.createCroppedLabelIfNecessary(fm, value, bounds.width - 50);
	
			positionX = bounds.x;
			if (alignmentH == AlignmentH.CENTER) {
				width = FontUtils.getWidthOfText(fm, drawValue);
				positionX += (bounds.width - width) / 2;
			} else if (alignmentH == AlignmentH.RIGHT) {
				width = FontUtils.getWidthOfText(fm, drawValue);
				positionX += (bounds.width - width);
			}

			// font height keeps the same all the time
			if (dirty) {
				positionY = bounds.y;
				if (alignmentV == AlignmentV.CENTER) {
					height     = (int)fm.getLineMetrics(drawValue, g).getHeight();
					positionY += (bounds.height - height) / 2;
				} else if (alignmentV == AlignmentV.BOTTOM) {
					height     = (int)fm.getLineMetrics(drawValue, g).getHeight();
					positionY += (bounds.height - height);
				}
			}
			
			// store extents in cache
			cache.put(drawValue, positionX, viewScale);
		}

		dirty = false;
	}
	
	/**
	 * @param g
	 */
	public void draw(Graphics2D g, double viewScale) {
		if (value == null)
			return;
		if (color != null)
			g.setColor(color);
		if (font != null)
			g.setFont(font);
		update(g, viewScale);
		if (drawValue != null)
			g.drawString(drawValue, positionX + offsetX, positionY + offsetY);
	}

	/**
	 * 
	 */
	public void invalidate() {
		dirty = true;
	}

	/**
	 * @return
	 */
	public String get() {
		return value;
	}
	
	/**
	 * @param value
	 */
	public void set(String value) {
		if (this.value != value) {
			this.value = value;
			dirty = true;
		}
	}
	
	/**
	 * @return
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * @return
	 */
	public Font getFont() {
		return font;
	}
	
	/**
	 * @param font
	 */
	public void setFont(Font font) {
		this.font = font;
		dirty = true;
	}
	
	/**
	 * @return
	 */
	public Rectangle getBounds() {
		return bounds;
	}
	
	/**
	 * @param bounds
	 */
	public void setBounds(Rectangle bounds) {
		if (bounds != null && !bounds.equals(this.bounds)) {
			this.bounds = bounds;
			dirty = true;
		}
	}
	
	/**
	 * @return
	 */
	public AlignmentH getAlignmentH() {
		return alignmentH;
	}
	
	/**
	 * @param alignmentH
	 */
	public void setAlignmentH(AlignmentH alignmentH) {
		this.alignmentH = alignmentH;
	}
	
	/**
	 * @return
	 */
	public AlignmentV getAlignmentV() {
		return alignmentV;
	}

	/**
	 * @param alignmentV
	 */
	public void setAlignmentV(AlignmentV alignmentV) {
		this.alignmentV = alignmentV;
	}
	
	/**
	 * @return
	 */
	public int getOffsetX() {
		return offsetX;
	}
	
	/**
	 * @param offsetX
	 */
	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}
	
	/**
	 * @return
	 */
	public int getOffsetY() {
		return offsetY;
	}
	
	/**
	 * @param offsetY
	 */
	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}
}
