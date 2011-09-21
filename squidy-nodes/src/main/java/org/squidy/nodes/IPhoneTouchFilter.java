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

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;

import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>IPhoneTouchFilter</code>.
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
 * @version $Id: IPhoneTouchFilter.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0.0
 */
//@XmlType(name = "IPhoneTouchFilter")
//@Processor(
//	name = "IPhoneTouchFilter",
//	icon = "/org/squidy/nodes/image/48x48/iphone.png",
//	description = "Filters out Fingertouchevents if an Iphone (marked with a specific Token) is layed down.",
//	types = { Processor.Type.INPUT, Processor.Type.FILTER, Processor.Type.OUTPUT },
//	tags = { },
//	status = Status.UNSTABLE
//)
public class IPhoneTouchFilter extends AbstractNode {

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

	@XmlAttribute(name = "Height")
	@Property(
		name = "Height",
		description = "Height of the Iphone (in pixel)"
	)
	@TextField
	private int height = 160;

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}


	@XmlAttribute(name = "Width")
	@Property(
		name = "Width",
		description = "Width of the Iphone (in pixel)"
	)
	@TextField
	private int width = 90;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@XmlAttribute(name = "IPhoneTokenID1")
	@Property(
		name = "IPhoneTokenID1",
		description = "Token ID of the first Iphone"
	)
	@TextField
	private int iphoneID1 = -10;

	public int getIphoneID1() {
		return iphoneID1;
	}
	public void setIphoneID1(int iphoneID1) {
		this.iphoneID1 = iphoneID1;
	}

	@XmlAttribute(name = "IPhoneTokenID2")
	@Property(
		name = "IPhoneTokenID2",
		description = "Token ID of the second Iphone"
	)
	@TextField
	private int iphoneID2 = -10;

	public int getIphoneID2() {
		return iphoneID2;
	}
	public void setIphoneID2(int iphoneID2) {
		this.iphoneID2 = iphoneID2;
	}


	@XmlAttribute(name = "IPhoneTokenID3")
	@Property(
		name = "IPhoneTokenID3",
		description = "Token ID of the third Iphone"
	)
	@TextField
	private int iphoneID3 = -10;

	public int getIphoneID3() {
		return iphoneID3;
	}
	public void setIphoneID3(int iphoneID3) {
		this.iphoneID3 = iphoneID3;
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




	private Hashtable<Integer, DataPosition2D> tokenArray = new Hashtable<Integer, DataPosition2D>();
	// id als key und DataPosition2D als value


	/*
	 * (non-Javadoc)
	 *
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() throws ProcessException
	{
		tokenArray.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() throws ProcessException
	{
		tokenArray.clear();
	}


	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#beforeDataContainerProcessing(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer)
	{

		IDataContainer dataCont = dataContainer.getClone();
		Vector<IData> data = new Vector<IData>(Arrays.asList(dataCont.getData()));

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

		//
		Iterator<Integer> hashIterator = tokenArray.keySet().iterator();
		while (hashIterator.hasNext())
		{
//			boolean inContainer = false;
			int id = hashIterator.next();

			for (int i=0; i<data.size(); i++)
			{
				if (data.get(i) instanceof DataPosition2D)
				{
					if (data.get(i).getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dobj"))
					{
						int idTmp = (Integer) data.get(i).getAttribute(TUIO.FIDUCIAL_ID);
						if (id == idTmp)
						{
							if (id == iphoneID1 || id == iphoneID2 || id == iphoneID3)
							{
//								inContainer = true;
								tokenArray.put(idTmp, (DataPosition2D) data.get(i));
								break;
							}
						}
					}
//					if (data.get(i).getAttribute(TUIO.TUIO_ORIGIN_ADDRESS).equals("/tuio/2Dcur"))
//					{
//						inContainer = true;
//					}
				}
			}
//			if (inContainer == false)
//				hashIterator.remove();
		}


		for (int i=0; i<data.size(); i++)
		{
			if (data.get(i) instanceof DataPosition2D)
			{
				if (data.get(i).getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dobj"))
				{
					int id = (Integer) data.get(i).getAttribute(TUIO.FIDUCIAL_ID);
					if (id == iphoneID1 || id == iphoneID2 || id == iphoneID3)
						tokenArray.put(id, (DataPosition2D) data.get(i));
				}
			}
		}

		for (int i=0; i<data.size(); i++)
		{
			if (data.get(i) instanceof DataPosition2D)
			{
				if (data.get(i).getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dcur"))
				{
					double Px = ((DataPosition2D) data.get(i)).getX();
					double Py = ((DataPosition2D) data.get(i)).getY();

					hashIterator = tokenArray.keySet().iterator();
					while (hashIterator.hasNext())
					{
						int id = hashIterator.next();
						DataPosition2D dataT = tokenArray.get(id);
						double x = dataT.getX();
						double y = dataT.getY();

						double Ax = x - width;
						double Ay = y + height;

						double Bx = x + width;
						double By = y + height;

						double Cx = x + width;
						double Cy = y - height;

						double Dx = x - width;
						double Dy = y - height;

						float angle = (Float) dataT.getAttribute(TUIO.ANGLE_A);

						Point2D A = new Point2D.Double(Ax, Ay);
						AffineTransform at =  new AffineTransform();
						at = AffineTransform.getRotateInstance(angle, x, y);
						A = at.transform(A, null);
						Ax = A.getX();
						Ay = A.getY();

						Point2D B = new Point2D.Double(Bx, By);
						at = AffineTransform.getRotateInstance(angle, x, y);
						B = at.transform(B, null);
						Bx = B.getX();
						By = B.getY();

						Point2D C = new Point2D.Double(Cx, Cy);
						at = AffineTransform.getRotateInstance(angle, x, y);
						C = at.transform(C, null);
						Cx = C.getX();
						Cy = C.getY();

						Point2D D = new Point2D.Double(Dx, Dy);
						at = AffineTransform.getRotateInstance(angle, x, y);
						D = at.transform(D, null);
						Dx = D.getX();
						Dy = D.getY();



						double det1 = Ax*By - Ay*Bx - Ax*Py + Ay*Px + Bx*Py - By*Px;
						double det2 = Bx*Cy - By*Cx - Bx*Py + By*Px + Cx*Py - Cy*Px;
						double det3 = Cx*Dy - Cy*Dx - Cx*Py + Cy*Px + Dx*Py - Dy*Px;
						double det4 = Dx*Ay - Dy*Ax - Dx*Py + Dy*Px + Ax*Py - Ay*Px;

						if (det1 >= 0 && det2 >= 0 && det3 >= 0 && det4 >= 0)
						{
							// im Rechteck
							data.remove(i);
						}
						else
						{
							if (det1 <= 0 && det2 <= 0 && det3 <= 0 && det4 <= 0)
							{
								// im Rechteck
								data.remove(i);
							}
						}
					}
				}
			}
		}



		for (int i=0; i<data.size(); i++)
		{
			if (data.get(i) instanceof DataPosition2D)
			{
				if (data.get(i).getAttribute(TUIO.ORIGIN_ADDRESS).equals("/tuio/2Dcur"))
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
		//	IDataContainer dataContP = dataContainer;
			//IDataContainer dataContP;
			//dataContP.setData(arr);
			publish(arr);
		}
//	System.out.println("Token Array size: "+tokenArray.size());
	return null;

	}

	// ################################################################################
	// END OF PROCESS
	// ################################################################################
}
