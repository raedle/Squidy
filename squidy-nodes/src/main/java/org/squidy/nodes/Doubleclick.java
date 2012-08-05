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

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.Vector;

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
 * <code>Doubleclick</code>.
 *
 * <pre>
 * Date: Okt 12, 2009
 * Time: 15:32:29 PM
 * </pre>
 *
 *
 * @author
 * Nicolas Hirrle
 * <a href="mailto:nihirrle@htwg-konstanz.de">nihirrle@htwg-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 *
 * @version $Id: Doubleclick.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0.0
 */
@Processor(
	name = "Doubleclick",
	icon = "/org/squidy/nodes/image/48x48/tactilefinger.png",
	description = "Stops unwanted doubleclicks",
	types = { Processor.Type.FILTER },
	tags = { },
	status = Status.UNSTABLE
)
@Deprecated
public class Doubleclick extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################


	@XmlAttribute(name = "Interpolate-Time")
	@Property(
		name = "Interpolate-Time",
		description = "Time (in ms) where lost cursors are interpolated (so that windows doesn't interpret it as a doubleclick)."
	)
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 2000, showLabels = true, showTicks = true, majorTicks = 1000, minorTicks = 50, snapToTicks = false)
	private int interpolateTime = 50;

	public int getInterpolateTime() {
			return interpolateTime;
	}

	public void setInterpolateTime(int interpolateTime) {
		this.interpolateTime = interpolateTime;
	}

	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################



	// ################################################################################
	// BEGIN OF LAUNCH
	// ################################################################################
	private class DataTime
	{
		private long time;
		private DataPosition2D dataPos2D = new DataPosition2D();
		public DataTime(long time, DataPosition2D dataPos) {
			this.time = time;
			this.dataPos2D = dataPos;
		}
	};
	private Hashtable<Integer, DataTime> interpolateCursors = new Hashtable<Integer, DataTime>();	// id als key und value: removetime und Dataposition2d
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() throws ProcessException
	{
		interpolateCursors.clear();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		interpolateCursors.clear();
	}

	// ################################################################################
	// END OF LAUNCH
	// ################################################################################

	// ################################################################################
	// BEGIN OF PROCESS
	// ################################################################################

	private long lastProcessedTime;
	private boolean printText = false;
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#beforeDataContainerProcessing(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer)
	{
		if (printText)
			System.out.println("start------");

		IDataContainer dataContP = interpolate(dataContainer);

		return super.preProcess(dataContP);
	}


	/**
	 * Uncomment method if processing of data position 2d is desired.
	 *
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
	public IData process(DataPosition2D dataPosition2D)
	{
		actualizeCursors (dataPosition2D);
		return dataPosition2D;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#afterDataContainerProcessing(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer postProcess(IDataContainer dataContainer)
	{
		if (printText)
			System.out.println("end-----");
		return super.postProcess(dataContainer);
	}



	private IDataContainer interpolate(IDataContainer dataContainer)
	{
		Vector<IData> dataCont = new Vector<IData>(Arrays.asList(dataContainer.getData()));

		Iterator<Integer> hashIterator = interpolateCursors.keySet().iterator();
		while (hashIterator.hasNext())
		{

			boolean inContainer = false;
			int id = hashIterator.next();
			for (IData data : dataContainer.getData())
			{
				if (data instanceof DataPosition2D)
				{
					int idTmp = (Integer) data.getAttribute(DataConstant.SESSION_ID);
					if (id == idTmp)
					{
						inContainer = true;
						break;
					}
				}
			}
			if (inContainer == false)
			{
				DataTime dt = interpolateCursors.get(id);
				long accTime = System.currentTimeMillis();

				if (accTime - dt.time <= interpolateTime)
				{
					dt.dataPos2D.setAttribute(ReacTIVision.TUIO_CURSOR, "interpolated");
					dataCont.add(dt.dataPos2D);
				}else{
					hashIterator.remove();
				}


			}
		}

		IData[] arr = new IData[dataCont.size()];
		dataCont.toArray(arr);
		IDataContainer dataContP = dataContainer.getClone();
		dataContP.setData(arr);
		return dataContP;
	}

	private void actualizeCursors (DataPosition2D dataPosition2D)
	{
		DataPosition2D dataPos = dataPosition2D;

		if (dataPos.hasAttribute(TUIO.ORIGIN_ADDRESS))
		{
			if (dataPos.getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dcur"))
			{
				int id = (Integer) dataPos.getAttribute(DataConstant.SESSION_ID);

				if (dataPos.getAttribute(ReacTIVision.TUIO_CURSOR) != "interpolated")
				{
					long lastTime = System.currentTimeMillis();
					DataTime dt = new DataTime(lastTime, dataPos);
					interpolateCursors.put(id, dt);
				}

				if (printText)
					System.out.println(dataPos.getAttribute(ReacTIVision.TUIO_CURSOR) + " on ID: " + dataPos.getAttribute(DataConstant.SESSION_ID));
			}
		}
	}
	// ################################################################################
	// END OF PROCESS
	// ################################################################################
}
