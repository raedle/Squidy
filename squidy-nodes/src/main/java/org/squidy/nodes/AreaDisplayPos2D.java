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

import org.squidy.designer.util.AreaOutputWindow;
import org.squidy.manager.ProcessException;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>AreaDisplayPos2D</code>.
 * 
 * <pre>
 * Date: Aug 20, 2009
 * Time: 2:13:34 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: AreaDisplayPos2D.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "AreaDisplayPos2D")
@Processor(
	name = "Area Display Pos2D",
	icon = "/org/squidy/nodes/image/48x48/funnel.png", 
	types = { Processor.Type.FILTER }, 
	description = "/org/squidy/nodes/html/AreaDisplayPos2D.html",
	tags = {"display", "area", "position", "2d" },
	status = Status.UNSTABLE
)
public class AreaDisplayPos2D extends AbstractNode {

	private AreaOutputWindow areaOutputWindow = null;

	@Override
	public void onStart() throws ProcessException {
		areaOutputWindow = AreaOutputWindow.getInstance();
	}
	
	
	@Override
	public IDataContainer preProcess(
			IDataContainer dataContainer) {
		areaOutputWindow.drawDataContainer(dataContainer);
		return super.preProcess(dataContainer);
	}


	@Override
	public void onStop() throws ProcessException {
		if(areaOutputWindow!=null)
		areaOutputWindow.closeWindow();
	}

}
