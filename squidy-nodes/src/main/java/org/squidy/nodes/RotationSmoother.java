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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.reactivision.TuioClient;


/**
 * <code>RotationSmoother</code>.
 *
 * <pre>
 * Date: Dec 4, 2009
 * Time: 15:32:29 PM
 * </pre>
 *
 *
 * @author Nicolas Hirrle <a
 *         href="mailto:nihirrle@htwg-konstanz.de">nihirrle@htwg-konstanz.de</a>
 *         Human-Computer Interaction Group University of Konstanz
 *
 * @version $Id: RotationSmoother.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0.0
 */
@XmlType(name = "RotationSmoother")
@Processor(
	name = "RotationSmoother",
	description = "Interpolates Tokenmessages to rotate smoother",
	types = { Processor.Type.FILTER },
	tags = {},
	status = Status.UNSTABLE
)
public class RotationSmoother extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################
	@XmlAttribute(name = "Number of Messages to analyze")
	@Property(name = "Number of Messages", description = "Number of messages (DataPosition2D) to analyze and then to interpolate Rotation if necessary", suffix = "messages")
	@Slider(type = Integer.class, minimumValue = 5, maximumValue = 20, showLabels = true, showTicks = true, majorTicks = 1, minorTicks = 1, snapToTicks = false)
	private int messageNumber = 10;

	public int getMessageNumber() {
		return messageNumber;
	}

	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}

	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	// ################################################################################
	// BEGIN OF LAUNCH
	// ################################################################################




	/*
	 * (non-Javadoc)
	 *
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() throws ProcessException
	{
		dataPackages = new Hashtable<Integer, ArrayBlockingQueue<DataPosition2D>>();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() throws ProcessException
	{
		dataPackages.clear();
	}

	// ################################################################################
	// END OF LAUNCH
	// ################################################################################

	// ################################################################################
	// BEGIN OF PROCESS
	// ################################################################################


	private Object lock = new Object();
	private Hashtable<Integer, ArrayBlockingQueue<DataPosition2D>> dataPackages;  // key = ID, Value = queue aus den 10 vorhergehenden DataPosition2D der zugehörigen ID


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.squidy.manager.model.AbstractNode#beforeDataContainerProcessing
	 * (org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataCont)
	{
		synchronized (lock)
		{
			IDataContainer dataContainer = dataCont.getClone();
			IData dataTmp[] = dataContainer.getData();
			ArrayList<IData> dataPublish = new ArrayList<IData>();

			for (int i=0; i<dataTmp.length; i++)
			{
				dataPublish.add(dataTmp[i]);
			}
			if(dataPackages.isEmpty())
			{
				IData[] data = dataContainer.getData();
				for (int i=0; i<data.length; i++)
				{
					if (data[i] instanceof DataPosition2D)
					{
						ArrayBlockingQueue<DataPosition2D> dataPosition2Ds = new ArrayBlockingQueue<DataPosition2D>(messageNumber);
						dataPosition2Ds.add((DataPosition2D) data[i]);
						int id = (Integer) data[i].getAttribute(TUIO.FIDUCIAL_ID);

						dataPackages.put(id, dataPosition2Ds);
					}
				}
			}
			else
			{
				IData[] data = dataContainer.getData();
				for (int i=0; i<data.length; i++)
				{
					if (data[i] instanceof DataPosition2D)
					{
						int id = (Integer) data[i].getAttribute(TUIO.FIDUCIAL_ID);
						if(dataPackages.containsKey(id))
						{
							String status = (String) data[i].getAttribute(ReacTIVision.TUIO_TOKEN);
							if (status == "removed")
							{
								dataPackages.remove(id);
							}
							else
							{
								ArrayBlockingQueue<DataPosition2D> dataPosition2Ds = dataPackages.get(id);
								if (dataPosition2Ds.remainingCapacity() > 0)
									dataPosition2Ds.add((DataPosition2D) data[i]);
								else
								{
									dataPosition2Ds.remove();
									dataPosition2Ds.add((DataPosition2D) data[i]);
								}
							}
						}
						else
						{
							ArrayBlockingQueue<DataPosition2D> dataPosition2Ds = new ArrayBlockingQueue<DataPosition2D>(messageNumber);
							dataPosition2Ds.add((DataPosition2D) data[i]);
							dataPackages.put(id, dataPosition2Ds);
						}
					}
				}
				// am ende
				Iterator<Integer> hashIterator = dataPackages.keySet().iterator();
				while (hashIterator.hasNext())
				{
					boolean idInContainer = false;
					int id = hashIterator.next();
					for (int i=0; i<data.length; i++)
					{
						if (data[i] instanceof DataPosition2D)
						{
							int idTmp = (Integer) data[i].getAttribute(TUIO.FIDUCIAL_ID);
							if (id == idTmp)
							{
								idInContainer = true;
							}
						}
					}
					if (idInContainer == false)
					{
						// interpolate
						DataPosition2D dataPosition2D = interpolateRotation(id);
						if (dataPosition2D != null)
							dataPublish.add(dataPosition2D);
					}

				}
			}
			publish(dataPublish);
		}
		return null;// super.beforeDataContainerProcessing(dataContainer);
	}

	private DataPosition2D interpolateRotation(int id)
	{
		ArrayBlockingQueue<DataPosition2D> dataPosition2Ds = dataPackages.get(id);

		Iterator<DataPosition2D> arrayIterator = dataPosition2Ds.iterator();
		while (arrayIterator.hasNext())
		{
			DataPosition2D dataPosition2D = arrayIterator.next();
			if ((Double)dataPosition2D.getAttribute(TUIO.ROTATION_ACCELERATION) == 0)
				return null; 	// nichts machen, da keine Rotation
		}

		double sumRotationAccleration = 0;
		double sumAngles = 0;
		arrayIterator = dataPosition2Ds.iterator();

		while (arrayIterator.hasNext())
		{
			DataPosition2D dataPosition2D = arrayIterator.next();
			double rotationAccleration = (Double)dataPosition2D.getAttribute(TUIO.ROTATION_ACCELERATION);
			sumRotationAccleration = sumRotationAccleration + rotationAccleration;

			Iterator<DataPosition2D> nextElement = arrayIterator;
			int next = 0;
			while (nextElement.hasNext())
			{
				if (next == 1)
					break;
				next++;
			}
			if (nextElement != arrayIterator)
			{
				DataPosition2D dataPosition2DNext = nextElement.next();
				double difAngles = (Double) dataPosition2DNext.getAttribute(TUIO.ANGLE_A) -(Double) dataPosition2D.getAttribute(TUIO.ANGLE_A);
				sumAngles = sumAngles + difAngles;
			}
		}

		double medianRotAcc = sumRotationAccleration / dataPosition2Ds.size();
		double medianAngle = sumAngles /  dataPosition2Ds.size();

		// get last element of array
		DataPosition2D dataPosition2D = null;
		arrayIterator = dataPosition2Ds.iterator();
		while (arrayIterator.hasNext())
		{
			dataPosition2D = arrayIterator.next().getClone();
		}


		double angle = medianAngle + (Double) dataPosition2D.getAttribute(TUIO.ANGLE_A);//last angle
		dataPosition2D.setAttribute(TUIO.ROTATION_ACCELERATION, medianRotAcc);
		dataPosition2D.setAttribute(TUIO.ANGLE_A, angle);
		dataPosition2D.setAttribute(ReacTIVision.TUIO_TOKEN, "updated");
		return dataPosition2D;

	}

	// ################################################################################
	// END OF PROCESS
	// ################################################################################
}
