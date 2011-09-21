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

import java.awt.BasicStroke;
import java.util.HashMap;
import java.util.Map;

/**
 * <code>StrokeUtils</code>.
 * 
 * <pre>
 * Date: Aug 20, 2009
 * Time: 1:33:03 AM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: StrokeUtils.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class StrokeUtils {

	private static final Map<Float, BasicStroke> STROKE_CACHE = new HashMap<Float, BasicStroke>();
	
	/**
	 * @param width
	 * @return
	 */
	public static BasicStroke getBasicStroke(float width) {
		if (!STROKE_CACHE.containsKey(width)) {
			STROKE_CACHE.put(width, new BasicStroke(width));
		}
		return STROKE_CACHE.get(width);
	}
}
