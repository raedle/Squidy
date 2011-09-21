/**
 * Squidy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Squidy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Squidy. If not, see <http://www.gnu.org/licenses/>.
 *
 * 2006-2009 Human-Computer Interaction Group, University of Konstanz.
 * <http://hci.uni-konstanz.de>
 *
 * Please contact info@squidy-lib.de or visit our website http://squidy-lib.de for
 * further information.
 */
/**
 *
 */
package org.squidy.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>TuioStatus</code>.
 *
 * <pre>
 * Date: Okt 12, 2009
 * Time: 15:32:29 PM
 * </pre>
 *
 *
 * @author Nicolas Hirrle <a
 *         href="mailto:nihirrle@htwg-konstanz.de">nihirrle@htwg-konstanz.de</a>
 *         Human-Computer Interaction Group University of Konstanz
 *
 * @version $Id: TuioStatus.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "TuioStatus")
@Processor(
	name = "TuioStatus",
	description = "Sets a status attribute (add / refreshed / updated / removed) on each DataPosition2D for a correct TuioOutput.",
	types = { Processor.Type.FILTER },
	tags = {},
	status = Status.UNSTABLE
)
public class TuioStatus extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################


	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	// ################################################################################
	// BEGIN OF LAUNCH
	// ################################################################################


	private Hashtable<Integer, DataPosition2D> cursors;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() throws ProcessException
	{
		cursors = new Hashtable<Integer, DataPosition2D>();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() throws ProcessException
	{
		cursors.clear();
	}

	// ################################################################################
	// END OF LAUNCH
	// ################################################################################

	// ################################################################################
	// BEGIN OF PROCESS
	// ################################################################################

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.squidy.manager.model.AbstractNode#beforeDataContainerProcessing
	 * (org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer)
	{
//		System.out.println("start------");

		Vector<IData> dataCont = new Vector<IData>(Arrays.asList(dataContainer.getData()));

		Iterator<Integer> hashIterator = cursors.keySet().iterator();
		while (hashIterator.hasNext())
		{
			int id = hashIterator.next();
			boolean inContainer = false;
			for(IData data : dataContainer.getData())
			{
				int idTmp = (Integer) data.getAttribute(DataConstant.SESSION_ID);
				if (id == idTmp)
					inContainer = true;
			}
			if (inContainer == false)
			{
				DataPosition2D dataRemoved = cursors.get(id);
				dataRemoved.setAttribute(ReacTIVision.TUIO_CURSOR, "removed");
				dataCont.add(dataRemoved);
			}
		}

		IData[] arr = new IData[dataCont.size()];
		dataCont.toArray(arr);
		IDataContainer dataContP = dataContainer.getClone();
		dataContP.setData(arr);
		return super.preProcess(dataContP);
		
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	public IData process(DataPosition2D dataPosition2D)
	{
		DataPosition2D data2D = dataPosition2D;

		if (dataPosition2D.getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dcur"))
		{
			int id = (Integer) data2D.getAttribute(DataConstant.SESSION_ID);
			if (!cursors.containsKey(id))
				data2D.setAttribute(ReacTIVision.TUIO_CURSOR, "add");
			else
			{
				if(data2D.hasAttribute(ReacTIVision.TUIO_CURSOR))
				{
					if(data2D.getAttribute(ReacTIVision.TUIO_CURSOR) == "removed")
					{
						cursors.remove(id);
//						System.out.println(data2D.getAttribute(ReacTIVision.TUIO_CURSOR) + " on ID: " + data2D.getAttribute(DataConstant.SESSION_ID));
						return data2D;
					}
				}
				
				
				DataPosition2D data2DCompare = cursors.get(id);

				double x = data2D.getX();
				double y = data2D.getY();
				float speedX = (Float) data2D.getAttribute(TUIO.MOVEMENT_VECTOR_X);
				float speedY = (Float) data2D.getAttribute(TUIO.MOVEMENT_VECTOR_Y);
				float motionAcc =  (Float) data2D.getAttribute(TUIO.MOTION_ACCELERATION);


				double xComp = data2DCompare.getX();
				double yComp = data2DCompare.getY();
				float speedXComp = (Float) data2DCompare.getAttribute(TUIO.MOVEMENT_VECTOR_X);
				float speedYComp = (Float) data2DCompare.getAttribute(TUIO.MOVEMENT_VECTOR_Y);
				float motionAccComp =  (Float) data2DCompare.getAttribute(TUIO.MOTION_ACCELERATION);

				if ((x != xComp) || (y != yComp) || (speedX != speedXComp) || (speedY != speedYComp) || (motionAcc != motionAccComp))
				{
					data2D.setAttribute(ReacTIVision.TUIO_CURSOR, "updated");
				}
				else
				{
					data2D.setAttribute(ReacTIVision.TUIO_CURSOR, "refreshed");

					Iterator<Integer> hashIterator = cursors.keySet().iterator();
					while (hashIterator.hasNext())
					{
						int idTmp = hashIterator.next();
						long timestamp = cursors.get(idTmp).getTimestamp();
						Vector<Integer> aliveObjects = new Vector<Integer>();

						if (System.currentTimeMillis() - timestamp <= 100)
						{
							aliveObjects.add(idTmp);
						}
						if (!aliveObjects.isEmpty())
							data2D.setAttribute(ReacTIVision.TUIO_ALIVE, aliveObjects);
					}
				}
			}

			cursors.put(id, data2D);
		}
//		System.out.println(data2D.getAttribute(ReacTIVision.TUIO_CURSOR) + " on ID: " + data2D.getAttribute(DataConstant.SESSION_ID));
		return data2D;
	}

	@Override
	public IDataContainer postProcess(IDataContainer dataContainer)
	{
//		System.out.println("end-----");
		return super.postProcess(dataContainer);

	}

	// ################################################################################
	// END OF PROCESS
	// ################################################################################
}
