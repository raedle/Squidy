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

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>TokenTouchFilter</code>.
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
 * @version $Id: TokenTouchFilter.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "TokenTouchFilter")
@Processor(
	name = "TokenTouchFilter",
	icon = "/org/squidy/nodes/image/48x48/blobtoken.png",
	description = "Filters out Fingertouchevents if a Token is layed down.",
	types = { Processor.Type.FILTER },
	tags = { },
	status = Status.UNSTABLE
)
@Deprecated
public class TokenTouchFilter extends AbstractNode {

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


	@XmlAttribute(name = "Tokenradius")
	@Property(
		name = "Tokenradius",
		description = "Radius (in Pixel) where Fingertouchevents aren't displayed"
	)
	@Slider(type = Integer.class, minimumValue = 0, maximumValue = 200, showLabels = true, showTicks = true, majorTicks = 50, minorTicks = 50, snapToTicks = false)
	private int radius = 17;



	/**
	 * @return the myProperty
	 */
	public final int getRadius() {
		return radius;
	}
	/**
	 * @param myProperty the myProperty to set
	 */
	public final void setRadius(int radius) {
		this.radius = radius;
	}

	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	// ################################################################################
	// BEGIN OF LAUNCH
	// ################################################################################


	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() throws ProcessException
	{
		tokenArray.clear();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		tokenArray.clear();
	}

	// ################################################################################
	// END OF LAUNCH
	// ################################################################################

	// ################################################################################
	// BEGIN OF PROCESS
	// ################################################################################

	private Hashtable<Integer, DataPosition2D> tokenArray = new Hashtable<Integer, DataPosition2D>();


	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#beforeDataContainerProcessing(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer)
	{

		IDataContainer dataCont = dataContainer.getClone();
		Vector<IData> data = new Vector<IData>(Arrays.asList(dataCont.getData()));


		Iterator<Integer> hashIterator = tokenArray.keySet().iterator();
		while (hashIterator.hasNext())
		{
			int id = hashIterator.next();

			for (int i=0; i<data.size(); i++)
			{
				if (data.get(i) instanceof DataPosition2D)
				{
					if (data.get(i).hasAttribute(TUIO.ORIGIN_ADDRESS) && data.get(i).getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dobj"))
					{
						int idTmp = (Integer) data.get(i).getAttribute(TUIO.FIDUCIAL_ID);
						if (id == idTmp)
						{
							tokenArray.put(idTmp, (DataPosition2D) data.get(i));
							break;

						}
					}
				}
			}
		}

		for (int i=0; i<data.size(); i++)
		{
			if (data.get(i) instanceof DataPosition2D)
			{
				double x, y;

				x = ((DataPosition2D) data.get(i)).getX();
				x = Math.round(x * resolutionX);
				y = ((DataPosition2D) data.get(i)).getY();
				y = Math.round(y * resolutionY);
				((DataPosition2D) data.get(i)).setX(x);
				((DataPosition2D) data.get(i)).setY(y);
			}
		}

		for (int i=0; i<data.size(); i++)
		{
			if (data.get(i) instanceof DataPosition2D)
			{
				if (data.get(i).hasAttribute(TUIO.ORIGIN_ADDRESS) && data.get(i).getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dobj"))
				{
					int id = (Integer) data.get(i).getAttribute(TUIO.FIDUCIAL_ID);
					tokenArray.put(id, (DataPosition2D) data.get(i));
				}
			}
		}

		for (int i=0; i<data.size(); i++)
		{
			if (data.get(i) instanceof DataPosition2D)
			{
				if (data.get(i).hasAttribute(TUIO.ORIGIN_ADDRESS) && data.get(i).getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dcur"))
				{
					double Px = ((DataPosition2D) data.get(i)).getX();
					double Py = ((DataPosition2D) data.get(i)).getY();

					hashIterator = tokenArray.keySet().iterator();
					while (hashIterator.hasNext())
					{
						int id = hashIterator.next();
						DataPosition2D dataT = tokenArray.get(id);

						DataPosition2D dataPosition2D = (DataPosition2D) data.get(i);
						double fingerX = dataPosition2D.getX();
						double fingerY = dataPosition2D.getY();

						double x = dataT.getX();
						double y = dataT.getY();

						double left = ((fingerX - x) * (fingerX - x)) + ((fingerY - y) * (fingerY - y));
						double right = radius * radius;

						if (left < right)
						{
							data.remove(i);
							break;
						}
					}
				}
			}
		}



		for (int i=0; i<data.size(); i++)
		{
			if (data.get(i) instanceof DataPosition2D)
			{
				if (data.get(i).hasAttribute(TUIO.ORIGIN_ADDRESS) && data.get(i).getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dcur"))
				{
					double x, y;

					x = ((DataPosition2D) data.get(i)).getX();
					x = x / resolutionX;
					y = ((DataPosition2D) data.get(i)).getY();
					y = y / resolutionY;
					((DataPosition2D) data.get(i)).setX(x);
					((DataPosition2D) data.get(i)).setY(y);
				}
				else
				{
					data.remove(i);
					if (i>=0)
						i = i-1;
				}
			}
			else
			{
				data.remove(i);
				if (i>=0)
					i = i-1;
			}
		}
		if (!data.isEmpty())
		{
			IData[] arr = new IData[data.size()];
			data.toArray(arr);
			publish(arr);
		}
	return null;

	}



	// ################################################################################
	// END OF PROCESS
	// ################################################################################
}
