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


package org.squidy.nodes;

import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>FlipXY2D</code>.
 * 
 * <pre>
 * Date: Feb 12, 2008
 * Time: 1:42:21 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: FlipXY2D.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "FlipXY2D")
@Processor(
	name = "Flip XY 2D",
	types = { Processor.Type.FILTER },
	description = "/org/squidy/nodes/html/FlipXY2D.html",
	tags = { "flip", "vertical", "horizontal", "xy", "2D" }
)
public class FlipXY2D extends AbstractNode {

	/**
	 * @param dataPosition2D
	 * @return
	 */
	public IData process(DataPosition2D dataPosition2D) {
			double tmp = dataPosition2D.getX();
			dataPosition2D.setX(dataPosition2D.getY());
			dataPosition2D.setY(tmp);

			return dataPosition2D;
		/*
		DataPosition2D newFinger = finger.getClone();
		
		
		double tmpX = finger.getX();
		double tmpY = finger.getY();
		newFinger.setX(tmpY);
		newFinger.setY(tmpX);*/
		
		
	//	return null;
	}
	/*
	public IDataContainer preProcess(IDataContainer dataContainer) {
		
		List<DataPosition2D> dataPositions2D = DataUtility.getDataOfType(DataPosition2D.class, dataContainer);
		for(DataPosition2D dp : dataPositions2D){
			double tmp = dp.getX();
			dp.setX(dp.getY());
			dp.setY(tmp);			
		}
		publish(dataPositions2D);
		return null;
	}
	*/
	
}
