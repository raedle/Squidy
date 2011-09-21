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

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataGesture;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;

import wiigee.logic.GestureType;

/**
 * <code>GestureRecognizer</code>.
 * 
 * <pre>
 * Date: May 31, 2009
 * Time: 10:58:31 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: GestureRecognizer.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "GestureRecognizer")
@Processor(
	name = "Gesture Recognizer",
	types = { Processor.Type.FILTER },
	icon = "/org/squidy/nodes/image/48x48/gesture-recognizer.png",
	description = "/org/squidy/nodes/html/GestureRecognizer.html",
	tags = { "gesture", "recognition" },
	status = Status.UNSTABLE
)
@Deprecated
public class GestureRecognizer extends AbstractNode {

	private Queue<DataPosition2D> DATA_QUEUE = new LinkedBlockingQueue<DataPosition2D>();
	
	/**
	 * @param dataPosition2D
	 * @return
	 */
	public IData process(DataPosition2D dataPosition2D) {
		
		DATA_QUEUE.add(dataPosition2D);
		
		if (DATA_QUEUE.size() > 10) {
			System.out.println("POLLING DATA");
			DATA_QUEUE.poll();
		}
		
		System.out.println(DATA_QUEUE.size());
		
		DataPosition2D prev = null;
		if (DATA_QUEUE.size() > 9) {
			boolean recog = true;
			
			for (DataPosition2D pos : DATA_QUEUE) {
				if (prev == null) {
					prev = pos;
					continue;
				}

				if (pos.getX() < prev.getX()) {
					recog = false;
					break;
				}
			}

			if (recog) {
				System.out.println("PUB");
				publish(new DataGesture(GestureRecognizer.class, "", 0, GestureType.VOID, true));
				DATA_QUEUE.clear();
			}
			else {
				DATA_QUEUE.clear();
			}
		}
		
		return dataPosition2D;
	}
}
