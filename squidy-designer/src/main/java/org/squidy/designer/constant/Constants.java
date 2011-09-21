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

package org.squidy.designer.constant;

import java.awt.Color;
import java.awt.Dimension;

import edu.umd.cs.piccolo.util.PBounds;

/**
 * <code>Constants</code>.
 * 
 * <pre>
 * Date: Feb 16, 2009
 * Time: 1:08:02 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: Constants.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public interface Constants {
	
	// Colors
	public interface Color {
		public static final java.awt.Color COLOR_SHAPE_BACKGROUND = new java.awt.Color(120, 120, 120, 80);
		public static final java.awt.Color COLOR_SHAPE_BORDER = java.awt.Color.BLACK;
	}
	
	public static final Dimension SCREEN_RESOLUTION = new Dimension(1440, 900);
	public static final double SCALE_FACTOR_X = SCREEN_RESOLUTION.width / 1440;
	public static final double SCALE_FACTOR_Y = SCREEN_RESOLUTION.height / 900;
	
	public static final double SEMANTIC_ZOOM_SCALE = 0.4;
	
	public static final PBounds DEFAULT_NODE_BOUNDS = new PBounds(0, 0, 1300, 1000);
	public static final PBounds DEFAULT_CIRCLE_BOUNDS = new PBounds(0, 0, 1200, 800);
	public static final PBounds DEFAULT_PORT_BOUNDS = new PBounds(0, 0, 150, 250);
	public static final PBounds DEFAULT_KNOWLEDGE_BASE_BOUNDS = new PBounds(0, 0, 1300, 105);
}
