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

package org.squidy.designer.shape.visualization.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.visualization.PlotContext;
import org.squidy.designer.shape.visualization.Visualization;
import org.squidy.designer.util.StrokeUtils;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;

import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>NorthernLightsMap</code>.
 * 
 * <pre>
 * Date: May 30, 2009
 * Time: 3:30:26 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 *         @uni-konstanz.de</a> Human-Computer Interaction Group University of
 *         Konstanz
 * 
 * @version $Id: ThermoPlot.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class ThermoPlot implements Visualization {
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.shape.visualization.Visualization#plotData(org.squidy.designer.shape.visualization.PlotContext, org.squidy.manager.data.IDataContainer)
	 */
	public void plotData(PlotContext plotContext, IDataContainer dataContainer) {

		Graphics2D g = plotContext.getGraphics();
		int width = plotContext.getWidth();
		int height = plotContext.getHeight();
		
		long sampleTime = plotContext.getSampleTime();
		
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - dataContainer.getTimestamp();
		
		double alphaSteps = (double) 255 / sampleTime;
		long alphaBudget = Math.abs(sampleTime - elapsedTime);
		int alpha = (int) (alphaSteps * alphaBudget);
		
		for (IData data : dataContainer.getData()) {
			if (data instanceof DataPosition2D) {
				DataPosition2D dataPosition2D = (DataPosition2D) data;
				
				g.setColor(new Color(255, 0, 0, alpha));
				g.fillOval((int) (dataPosition2D.getX() * width), (int) (dataPosition2D.getY() * height), 100, 100);
			}
			else if (data instanceof DataDigital) {
				IData[] datas = dataContainer.getData();
				
				boolean plotted = false;
				for (IData d : datas) {
					if (d instanceof DataPosition2D) {
						plotted = true;
						
						DataPosition2D pos2D = (DataPosition2D) d;
					
						int x = (int) (pos2D.getX() * width);
						int y = (int) (pos2D.getY() * height);
						
						g.setStroke(new BasicStroke(50f));
						g.setColor(new Color(255, 255, 0, alpha));
						g.drawOval(x - 200, y - 200, 400, 400);
					}
				}
				
				if (!plotted) {
					g.setColor(new Color(255, 255, 0, alpha));
					g.fillRect(0, 0, width, height);
				}
			}
		}
	}
	
	private static final String VISUALIZATION_NAME = "Thermo Plot";
	private static final Font FONT = VisualShape.internalFont.deriveFont(180f);
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.shape.visualization.Visualization#paintContextual(edu.umd.cs.piccolo.util.PPaintContext, org.squidy.designer.shape.visualization.PlotContext)
	 */
	public void paintContextual(PPaintContext paintContext, PlotContext plotContext) {
		
		Graphics2D g = paintContext.getGraphics();
		
		int width = plotContext.getWidth();
		int height = plotContext.getHeight();
		
		g.setColor(Color.BLACK);
		g.setStroke(StrokeUtils.getBasicStroke(25f));
		g.drawRect(0, 0, width, height);
		
		g.setFont(FONT);
		g.drawString(VISUALIZATION_NAME, 0, 0 - 100);
	}
}
