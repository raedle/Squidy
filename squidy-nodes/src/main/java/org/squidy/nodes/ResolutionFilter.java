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
package org.squidy.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>ResolutionFilter</code>.
 *
 * <pre>
 * Date: Okt 16, 2009
 * Time: 11:32:29 PM
 * </pre>
 *
 *
 * @author
 * Nicolas Hirrle
 * <a href="mailto:nihirrle@htwg-konstanz.de">nihirrle@htwg-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 *
 * @version $Id: ResolutionFilter.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0.0
 */
@XmlType(name = "ResolutionFilter")
@Processor(
	name = "ResolutionFilter",
	icon = "/org/squidy/nodes/image/48x48/resolution.png",
	description = "to be done",
	types = { Processor.Type.INPUT, Processor.Type.FILTER, Processor.Type.OUTPUT },
	tags = { }
)
public class ResolutionFilter extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################

	@XmlAttribute(name = "X-Resolution")
	@Property(
		name = "X-Resolution",
		description = "X-Resolution (in pixel) of the screen"
	)
	@TextField
	private int resolutionX = 1920;



	public int getResolutionX() {
		return resolutionX;
	}
	public void setResolutionX(int resolutionX) {
		this.resolutionX = resolutionX;
	}


	@XmlAttribute(name = "Y-Resolution")
	@Property(
		name = "Y-Resolution",
		description = "Y-Resolution (in pixel) of the screen"
	)
	@TextField
	private int resolutionY = 1200;


	public int getResolutionY() {
		return resolutionY;
	}
	public void setResolutionY(int resolutionY) {
		this.resolutionY = resolutionY;
	}

	@XmlAttribute(name = "treshold")
	@Property(
		name = "Treshold",
		description = "Treshold (in pixel) where fingers aren't displayed"
	)
	@TextField
	private int treshold = 5;

	public int getTreshold() {
		return treshold;
	}
	public void setTreshold(int treshold) {
		this.treshold = treshold;
	}




	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	// ################################################################################
	// BEGIN OF LAUNCH
	// ################################################################################

	// ################################################################################
	// END OF LAUNCH
	// ################################################################################

	// ################################################################################
	// BEGIN OF PROCESS
	// ################################################################################

	private Lock lock = new ReentrantLock();
	private List<DataPosition2D> positionArray = new ArrayList<DataPosition2D>();




	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#beforeDataContainerProcessing(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer)
	{
		List<DataPosition2D> publishArray = new ArrayList<DataPosition2D>();
		IDataContainer dataCont = dataContainer.getClone();
		IData[] data = dataCont.getData();

		double x, y;

		List<Integer> removeIndexes = new ArrayList<Integer>();

		for (int i=0; i<positionArray.size(); i++)	// PositionsArray durchgehn und schauen ob die IDs
													// im dataContainer alle vorhanden sind. Falls eine ID fehlt
													// Arrayindex im removeIndexes Array speichern und anschliessend
													// alle indexes im Array löschen
		{

			boolean remove = true;
			int id = (Integer)positionArray.get(i).getAttribute(DataConstant.SESSION_ID);

			for (int j=0; j<data.length; j++)
			{
				if (data[j] instanceof DataPosition2D)
				{
					if (data[j].getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dblb"))
					{
						int tmpId = (Integer)((DataPosition2D) data[j]).getAttribute(DataConstant.SESSION_ID);

						if (tmpId == id)
						{
							remove = false;
							break;
						}
					}
				}
				else
					return dataContainer;
			}

			if (remove == true)
				positionArray.remove(i);

		}


		removeIndexes.clear();

		for (int i=0; i<data.length; i++)
		{

			if (data[i] instanceof DataPosition2D)
			{
				if (data[i].getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dblb"))
				{
					int id = (Integer)((DataPosition2D) data[i]).getAttribute(DataConstant.SESSION_ID);

					x = ((DataPosition2D) data[i]).getX();
					x = Math.round(x * resolutionX);
					y = ((DataPosition2D) data[i]).getY();
					y = Math.round(y * resolutionY);
					((DataPosition2D) data[i]).setX(x);
					((DataPosition2D) data[i]).setY(y);

					if (positionArray.isEmpty())	// first finger
					{
						positionArray.add((DataPosition2D) data[i]);
					}
					else
					{
						boolean inArray = false;
						for (int j=0; j<positionArray.size(); j++)
						{
							int tmpId = (Integer) positionArray.get(j).getAttribute(DataConstant.SESSION_ID);
							if (id == tmpId) // updated or refreshed
							{
								inArray = true;
								break;
							}
						}

						if (inArray == false) // finger or token down
							positionArray.add((DataPosition2D) data[i]);

						else	// updated or refreshed
						{
							for (int j=0; j<positionArray.size(); j++)	// array durchgehn, nach gleicher ID suchen und dann checken ob
																		// position hinzugefuegt werden darf
							{
								int tmpId = (Integer) positionArray.get(j).getAttribute(DataConstant.SESSION_ID);
								if (id == tmpId) // updated or refreshed
								{
									double fingerX = Math.round(positionArray.get(j).getX() * resolutionX);
									double fingerY = Math.round(positionArray.get(j).getY()* resolutionY);


									double left = ((x - fingerX) * ( x- fingerX)) + (( y - fingerY) * (y- fingerY));
									double right = treshold * treshold;

									if (left > right)
									{
										positionArray.remove(j);
										positionArray.add((DataPosition2D) data[i]);
									}
								}
							}
						}
					}
				}
				else if (data[i].getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dobj"))
				{
					publishArray.add((DataPosition2D) data[i]);
				}
			}
		}
		for (int i=0; i<positionArray.size(); i++)
		{
			x = positionArray.get(i).getX();
			if (x > 1)
			{
				x = x / resolutionX;
				positionArray.get(i).setX(x);
			}
			y = positionArray.get(i).getY();
			if (y > 1)
			{
				y = y / resolutionY;
				positionArray.get(i).setY(y);
			}
		}

		publishArray.addAll(positionArray);
		publish(publishArray);

		return null;//super.preProcess(dataContainer);
	}


	// ################################################################################
	// END OF PROCESS
	// ################################################################################
}
