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

package org.squidy.designer.shape.visualization;

import java.awt.Graphics2D;

import org.squidy.manager.data.IDataContainer;

import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>PlotContext</code>.
 * 
 * <pre>
 * Date: May 31, 2009
 * Time: 5:42:21 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: PlotContext.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class PlotContext {

	private PPaintContext paintContext;
	
	private int width;
	
	private int height;
	
	private double scale;
	
	private long sampleTime;
	
	public PlotContext(PPaintContext paintContext, int width, int height, double scale, long sampleTime) {
		this.paintContext = paintContext;
		this.width = width;
		this.height = height;
		this.scale = scale;
		this.sampleTime = sampleTime;
	}

	/**
	 * @return the graphics
	 */
	public Graphics2D getGraphics() {
		return paintContext.getGraphics();
	}
	
	/**
	 * @param transform
	 */
	public void pushTransform(PAffineTransform transform) {
		paintContext.pushTransform(transform);
	}
	
	/**
	 * @param transform
	 */
	public void popTransform(PAffineTransform transform) {
		paintContext.popTransform(transform);
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the scale
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * @return the sampleTime
	 */
	public long getSampleTime() {
		return sampleTime;
	}

	/**
	 * @param sampleTime the sampleTime to set
	 */
	public void setSampleTime(long sampleTime) {
		this.sampleTime = sampleTime;
	}
}
