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

import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

/**
 * <code>FontUtils</code>.
 * 
 * <pre>
 * Date: Feb 1, 2009
 * Time: 3:41:31 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: FontUtils.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0
 */
public class FontUtils {
	
	/**
	 * @param fontMetrics
	 * @param label
	 * @param availableWidth
	 * @return
	 */
	public static String createCroppedLabelIfNecessary(FontMetrics fontMetrics, String label, int availableWidth) {
		
		// Prerequisites to calculate cropped label.
		int labelWidth = getWidthOfText(fontMetrics, label);
		
		// Simple case... label's width is less than available width.
		if (availableWidth > labelWidth) {
			return label;
		}
		
		int centerLetter = -1;
		
		// Remove required space for dots.
		availableWidth -= FontUtils.getWidthOfDots(fontMetrics);
		
		// Remove letters while label's width is bigger than available width.
		while (labelWidth > availableWidth) {
			centerLetter = label.length() / 2;
			label = label.substring(0, centerLetter) + label.substring(centerLetter + 1, label.length());

			labelWidth = FontUtils.getWidthOfText(fontMetrics, label);
		}

		// Determine odd or even amount of letters.
		if (centerLetter != -1) {
			switch (centerLetter % 2) {
			case 0:
				label = label.substring(0, centerLetter) + "..."
						+ label.substring(centerLetter, label.length());
				break;
			case 1:
				label = label.substring(0, centerLetter) + "..."
						+ label.substring(centerLetter + 1, label.length());
			}
		}
		return label;
	}

	/**
	 * @param fontMetrics
	 * @return
	 */
	public static int getWidthOfDots(FontMetrics fontMetrics) {
		return fontMetrics.stringWidth("...");
	}
	
	/**
	 * @param fontMetrics
	 * @param text
	 * @return
	 */
	public static int getWidthOfText(FontMetrics fontMetrics, String text) {
		return SwingUtilities.computeStringWidth(fontMetrics, text);
//		return fontMetrics.stringWidth(text);
	}
	
	/**
	 * Returns a string that fit into the maximum width based on the parameter
	 * string. It returns the orignal string if it fit into the maximum width
	 * otherwise it returns a cut string extended with "..." string.
	 * 
	 * @param fm
	 *            The metrics of the font to be calculated with.
	 * @param s
	 *            The string to calculate its width.
	 * @param maxWidth
	 *            The maximum width the string is allowed to take.
	 * @return The orignal string if it fit into the maximum width otherwise it
	 *         returns a cut string extended with "..." string.
	 */
	public static String getStringForMaxWidth(FontMetrics fm, String s, int maxWidth) {

		boolean changed = false;
		for (int nameWidth = fm.stringWidth(s); nameWidth > maxWidth; nameWidth = fm.stringWidth(s.concat("..."))) {
			s = s.substring(0, s.length() - 1);
			changed = true;
		}

		if (changed) {
			return s.concat("...");
		}
		return s;
	}
	
	/**
	 * @param text
	 * @return
	 */
	public static int getLineCount(String text, float width) {
		
		StringTokenizer tokenizer = new StringTokenizer(text, System.getProperty("line.separator"));
		int count = tokenizer.countTokens();
		
		AttributedString attributedString = new AttributedString(text);
		
		AttributedCharacterIterator paragraph = attributedString.getIterator();
		int paragraphnd = paragraph.getEndIndex();
		
		LineBreakMeasurer lbm = new LineBreakMeasurer(paragraph, new FontRenderContext(null, false, false));
		for ( ; lbm.getPosition() < paragraphnd; count++) {
			lbm.nextLayout(width);
		}
		return count;
	}
}
