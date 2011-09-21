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

import org.squidy.designer.shape.visualization.PlotContext;
import org.squidy.designer.shape.visualization.Visualization;
import org.squidy.manager.data.IDataContainer;

import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ImagePlot</code>.
 * 
 * <pre>
 * Date: Jun 12, 2009
 * Time: 6:28:23 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: ImagePlot.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class ImagePlot implements Visualization {

	/* (non-Javadoc)
	 * @see org.squidy.designer.shape.visualization.Visualization#paintContextual(edu.umd.cs.piccolo.util.PPaintContext, org.squidy.designer.shape.visualization.PlotContext)
	 */
	public void paintContextual(PPaintContext paintContext, PlotContext plotContext) {

	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.shape.visualization.Visualization#plotData(org.squidy.designer.shape.visualization.PlotContext, org.squidy.manager.data.IDataContainer)
	 */
	public void plotData(PlotContext plotContext, IDataContainer dataContainer) {
		
//		for (IData data : dataContainer.getData()) {
//			
//			if (data instanceof DataBlob) {
//				DataBlob dataBlob = (DataBlob) data;
//				
//				MemoryImageSource mis = new MemoryImageSource(320, 240, dataBlob.getBlob(), 0, 320);
//				
//				plotContext.getGraphics().drawImage(Toolkit.getDefaultToolkit().createImage(mis), 0, 0, null);
//			}
//		}
	}
}
