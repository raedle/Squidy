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
import java.util.List;

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


/**
 * <code>Synchronize</code>.
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
 * @version $Id: Synchronize2.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0.0
 */
@XmlType(name = "Synchronize2")
@Processor(
	name = "Synchronize2",
	description = "Synchronizes multiple devices...",
	types = { Processor.Type.FILTER },
	tags = {},
	status = Status.UNSTABLE
)
public class Synchronize2 extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################

	@XmlAttribute(name = "delay-time")
	@Property(name = "Delay time", description = "Time to wait for all incomming data for synchronzing them.", suffix = "ms")
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 2000, showLabels = true, showTicks = true, majorTicks = 500, minorTicks = 500, snapToTicks = false)
	private int delayTime = 50;

	/**
	 * @return the myProperty
	 */
	public final int getDelayTime() {
		return delayTime;
	}

	/**
	 * @param myProperty
	 *            the myProperty to set
	 */
	public final void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	// ################################################################################
	// BEGIN OF LAUNCH
	// ################################################################################

	private List<IData> dataContainerSync;

	private Object lock = new Object();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {
		dataContainerSync = new ArrayList<IData>();

		new Thread() {
			@Override
			public void run() {
				while (isProcessing()) {
					try {
						Thread.sleep(delayTime);

						synchronized (lock) // publish data every
											// delaytime-sequence
						{

							List<IData> dataContainerPublish = new ArrayList<IData>(
									dataContainerSync);
							publish(dataContainerPublish);
							dataContainerSync.clear();
							dataContainerPublish.clear();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		dataContainerSync.clear();
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
	public IDataContainer preProcess(IDataContainer dataContainer) {
		synchronized (lock) {
			IData[] data = dataContainer.getData();
			boolean inContainer = false;
			for (int i = 0; i < data.length; i++) {

				if (data[i] instanceof DataPosition2D) {
					Integer fiducialId = (Integer) (data[i]
							.hasAttribute(TUIO.FIDUCIAL_ID) ? data[i]
							.getAttribute(TUIO.FIDUCIAL_ID) : 1);
					if (fiducialId.equals(1)) {
						fiducialId = (Integer) (data[i]
								.hasAttribute(TUIO.FIDUCIAL_ID) ? data[i]
								.getAttribute(TUIO.FIDUCIAL_ID) : 1);
					}

					if (dataContainerSync.isEmpty())
						dataContainerSync.add(data[i]);
					else {
						// in container ?
						for (int j = 0; j < dataContainerSync.size(); j++) {
							IData dataTmp = dataContainerSync.get(j);
							Integer fiId = (Integer) (dataTmp
									.hasAttribute(TUIO.FIDUCIAL_ID) ? dataTmp
									.getAttribute(TUIO.FIDUCIAL_ID)
									: 1);
							if (fiId.equals(1)) {
								fiId = (Integer) (dataTmp
										.hasAttribute(TUIO.FIDUCIAL_ID) ? dataTmp
										.getAttribute(TUIO.FIDUCIAL_ID)
										: 1);
							}

							if (fiducialId == fiId) {
								inContainer = true;
								// if
								// (data[i].hasAttribute(ReacTIVision.TUIO_TOKEN))
								// {
								// // if
								// (data[i].getAttribute(ReacTIVision.TUIO_TOKEN)
								// == "down")
								// // {
								// // dataContainerSynch.add(data[i]);
								// // }
								// // else if
								// (data[i].getAttribute(ReacTIVision.TUIO_TOKEN)
								// == "lifted")
								// // {
								// // dataContainerSynch.add(data[i]);
								// // }
								// // else
								// dataContainerSynch.add(data[i]);
								// dataContainerSynch.remove(j);
								// inContainer = false;
								// }
								// if
								// ((Boolean)data[i].getAttribute(TUIO.TUIO_TOKEN)
								// == true)
								// {
								// dataContainerSynch.get(j).setAttribute(TUIO.TUIO_TOKEN,
								// true);
								// }

								break;

							}

						}
						if (inContainer == false)
							dataContainerSync.add(data[i]);
						inContainer = false;
					}
				} else
					dataContainerSync.add(data[i]);

			}
		}
		return null;// super.beforeDataContainerProcessing(dataContainer);
	}

	// ################################################################################
	// END OF PROCESS
	// ################################################################################
}
