/* Squidy Interaction Library is free software: you can redistribute it and/or
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>BlobToken</code>.
 *
 * <pre>
 * Date: Apr 1, 2010
 * Time: 10:38:23 PM
 * </pre>
 *
 * @author Nicolas Hirrle, <a
 *         href="mailto:nihirrle@htwg-konstanz.de">nihirrle@htwg-
 *         konstanz.de</a>, University of Konstanz
 * @version $Id: BlobToken.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
@XmlType(name = "BlobToken")
@Processor(
	name = "BlobToken",
	icon = "/org/squidy/nodes/image/48x48/blobtoken.png",
	types = { Processor.Type.FILTER },
	tags = {"BlobToken", "hci", "konstanz", "blob", "token" },
	status = Status.UNSTABLE
)
public class BlobToken extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################

	@XmlAttribute(name = "X-Resolution")
	@Property(name = "X-Resolution", description = "X-Resolution (in pixel) of the screen")
	@TextField
	private int resolutionX = 1920;

	public int getResolutionX() {
		return resolutionX;
	}

	public void setResolutionX(int resolutionX) {
		this.resolutionX = resolutionX;
	}

	@XmlAttribute(name = "Y-Resolution")
	@Property(name = "Y-Resolution", description = "Y-Resolution (in pixel) of the screen")
	@TextField
	private int resolutionY = 1200;

	public int getResolutionY() {
		return resolutionY;
	}

	public void setResolutionY(int resolutionY) {
		this.resolutionY = resolutionY;
	}

	@XmlAttribute(name = "Resolution_token_0")
	@Property(name = "Resolution_token_0", description = "Resolution (in pixel) where blob is recogniezd as token 0  (has to be greater than Resolution finger)")
	@TextField
	private int treshholdT0 = 50;

	public int getTreshholdT0() {
		return treshholdT0;
	}

	public void setTreshholdT0(int treshholdT0) {
		this.treshholdT0 = treshholdT0;
	}

	@XmlAttribute(name = "Resolution_token_1")
	@Property(name = "Resolution_token_1", description = "Resolution (in pixel) where blob is recogniezd as token 1")
	@TextField
	private int treshholdT1 = 65;

	public int getTreshholdT1() {
		return treshholdT1;
	}

	public void setTreshholdT1(int treshholdT1) {
		this.treshholdT1 = treshholdT1;
	}

	@XmlAttribute(name = "Resolution_token_2")
	@Property(name = "Resolution_token_2", description = "Resolution (in pixel) where blob is recogniezd as token 2")
	@TextField
	private int treshholdT2 = 75;

	public int getTreshholdT2() {
		return treshholdT2;
	}

	public void setTreshholdT2(int treshholdT2) {
		this.treshholdT2 = treshholdT2;
	}

	@XmlAttribute(name = "Resolution_token_3")
	@Property(name = "Resolution_token_3", description = "Resolution (in pixel) where blob is recogniezd as token 3")
	@TextField
	private int treshholdT3 = 85;

	public int getTreshholdT3() {
		return treshholdT3;
	}

	public void setTreshholdT3(int treshholdT3) {
		this.treshholdT3 = treshholdT3;
	}

	@XmlAttribute(name = "Resolution_token_4")
	@Property(name = "Resolution_token_4", description = "Resolution (in pixel) where blob is recogniezd as token 4")
	@TextField
	private int treshholdT4 = 95;

	public int getTreshholdT4() {
		return treshholdT4;
	}

	public void setTreshholdT4(int treshholdT4) {
		this.treshholdT4 = treshholdT4;
	}

	@XmlAttribute(name = "Resolution_token_5")
	@Property(name = "Resolution_token_5", description = "Resolution (in pixel) where blob is recogniezd as token 5")
	@TextField
	private int treshholdT5 = 105;

	public int getTreshholdT5() {
		return treshholdT5;
	}

	public void setTreshholdT5(int treshholdT5) {
		this.treshholdT5 = treshholdT5;
	}

	@XmlAttribute(name = "Resolution_finger")
	@Property(name = "Resolution_finger", description = "Resolution (in pixel) where blob is recogniezd as a finger. IMPORTANT: Has to be smaller than Token 0!")
	@TextField
	private int treshholdFinger = 35;

	public int getTreshholdFinger() {
		return treshholdFinger;
	}

	public void setTreshholdFinger(int treshholdFinger) {
		if (treshholdFinger > treshholdT0)
		{
			System.err.println("ERROR: TreshholdFinger has to be smaller than treshholdT0!");
			treshholdFinger = treshholdT0 -1;
			System.err.println("TreshholdFinger is set to" + treshholdFinger);
		}
		else
			this.treshholdFinger = treshholdFinger;
	}

	@XmlAttribute(name = "TokenID_0")
	@Property(name = "TokenID_0", description = "TokenID_for token 0")
	@TextField
	private int tokenID_0 = 0;

	public int getTokenID_0() {
		return tokenID_0;
	}

	public void setTokenID_0(int tokenID_0) {
		this.tokenID_0 = tokenID_0;
	}

	@XmlAttribute(name = "TokenID_1")
	@Property(name = "TokenID_1", description = "TokenID_for token 1")
	@TextField
	private int tokenID_1 = 1;

	public int getTokenID_1() {
		return tokenID_1;
	}

	public void setTokenID_1(int tokenID_1) {
		this.tokenID_1 = tokenID_1;
	}

	@XmlAttribute(name = "TokenID_2")
	@Property(name = "TokenID_2", description = "TokenID_for token 2")
	@TextField
	private int tokenID_2 = 2;

	public int getTokenID_2() {
		return tokenID_2;
	}

	public void setTokenID_2(int tokenID_2) {
		this.tokenID_2 = tokenID_2;
	}

	@XmlAttribute(name = "TokenID_3")
	@Property(name = "TokenID_3", description = "TokenID_for token 3")
	@TextField
	private int tokenID_3 = 3;

	public int getTokenID_3() {
		return tokenID_3;
	}

	public void setTokenID_3(int tokenID_3) {
		this.tokenID_3 = tokenID_3;
	}

	@XmlAttribute(name = "TokenID_4")
	@Property(name = "TokenID_4", description = "TokenID_for token 4")
	@TextField
	private int tokenID_4 = 4;

	public int getTokenID_4() {
		return tokenID_4;
	}

	public void setTokenID_4(int tokenID_4) {
		this.tokenID_4 = tokenID_4;
	}

	@XmlAttribute(name = "TokenID_5")
	@Property(name = "TokenID_5", description = "TokenID_for token 5")
	@TextField
	private int tokenID_5 = 5;

	public int getTokenID_5() {
		return tokenID_5;
	}

	public void setTokenID_5(int tokenID_5) {
		this.tokenID_5 = tokenID_5;
	}

	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	// ################################################################################
	// BEGIN OF PROCESSABLE
	// ################################################################################

	private HashSet<Integer> allTrackedTokens;

	public void onStart() {
		super.onStart();

		allTrackedTokens = new HashSet<Integer>();
	}

	@Override
	public void onStop() {
		super.onStop();

		allTrackedTokens.clear();
	}

	public IDataContainer preProcess(IDataContainer dataContainer) {
		IData data[] = dataContainer.getData();
		HashSet<Integer> currentAlives;
		
		for (int i=0; i<data.length; i++)
		{
			if (data[i] instanceof DataDigital){
				if (data[i].hasAttribute(DataConstant.SESSION_ID)){
					int id = (Integer) data[i].getAttribute(DataConstant.SESSION_ID);
					if (((DataDigital) data[i]).getFlag() == false){
						allTrackedTokens.remove(id);
					}
				}
			}
		}
		
		if (data[0] instanceof DataPosition2D) {
			DataPosition2D dataPosition2D = (DataPosition2D) data[0];
			if (dataPosition2D.hasAttribute(DreaMTouch.TUIO_ALIVE)) {
				currentAlives = setHashSetAlives(dataPosition2D);
				ArrayList<DataPosition2D> dataFinger = new ArrayList<DataPosition2D>();
				ArrayList<DataPosition2D> dataToken = new ArrayList<DataPosition2D>();

				DataPosition2D dataFingerAlive = new DataPosition2D(
						BlobToken.class, 0, 0);
				dataFingerAlive.setAttribute(TUIO.ORIGIN_ADDRESS,
						"/tuio/2Dblb");

				DataPosition2D dataTokenAlive = new DataPosition2D(
						BlobToken.class, 0, 0);
				dataTokenAlive.setAttribute(TUIO.ORIGIN_ADDRESS,
						"/tuio/2Dobj");

				String aliveFinger = new String();
				String aliveToken = new String();

				for (int i = 1; i < data.length; i++) {
					if (data[i] instanceof DataPosition2D) {
						DataPosition2D dataTmp = (DataPosition2D) data[i];
						if (dataTmp.hasAttribute(TUIO.ORIGIN_ADDRESS)) {
							if (dataTmp
									.getAttribute(TUIO.ORIGIN_ADDRESS)
									.equals("/tuio/2Dblb")) {
								DataPosition2D data2D = checkIfToken((DataPosition2D) data[i]);
								if (data2D.getAttribute(
										TUIO.ORIGIN_ADDRESS).equals(
										"/tuio/2Dblb")) {
									dataFinger.add(data2D);
								} else if (data2D.getAttribute(
										TUIO.ORIGIN_ADDRESS).equals(
										"/tuio/2Dobj")) {
									//System.out.println("published token id: " + data2D.getAttribute(DataConstant.SESSION_ID) + "width: " + data2D.getAttribute(DreaMTouch.TUIO_WIDTH));
									dataToken.add(data2D);
								}
							}
						}
					}
				}

				Iterator<Integer> itr = currentAlives.iterator();

				while (itr.hasNext()) {
					int id = (Integer) itr.next();
					if (allTrackedTokens.contains(id)) {
						aliveToken = aliveToken + id + " ";
					} else {
						aliveFinger = aliveFinger + id + " ";
					}
				}
				dataFingerAlive.setAttribute(DreaMTouch.TUIO_ALIVE, aliveFinger);
				dataTokenAlive.setAttribute(DreaMTouch.TUIO_ALIVE, aliveToken);

				dataFinger.add(0, dataFingerAlive);
				dataToken.add(0, dataTokenAlive);

				publish(dataFinger);
				publish(dataToken);
			}
		}

		return null;
	}

	// ################################################################################
	// END OF PROCESSABLE
	// ################################################################################

	// ################################################################################
	// BEGIN OF USERDEFINED FUNCTIONS
	// ################################################################################

	private DataPosition2D checkIfToken(DataPosition2D dataPosition2D) {
		dataPosition2D = setResolution(dataPosition2D);

		float width = (Float) dataPosition2D.getAttribute(TUIO.WIDTH);
		// float height = (Float)
		// dataPosition2D.getAttribute(DreaMTouch.TUIO_HEIGHT);
		// float area = (Float) dataPosition2D.getAttribute(DreaMTouch.TUIO_AREA);

		int id = (Integer) dataPosition2D.getAttribute(DataConstant.SESSION_ID);

		System.out.println("width: " + width);
		DataPosition2D dataToken = null;
		if (width < 35) {
//			System.out.println("tracked Finger");
			dataPosition2D = setResolutionBack(dataPosition2D);
			dataPosition2D.setAttribute(TUIO.ORIGIN_ADDRESS,
					"/tuio/2Dblb");
			return dataPosition2D;
		} else {
			dataToken = new DataPosition2D(BlobToken.class, dataPosition2D
					.getX(), dataPosition2D.getY());
			dataToken.setAttribute(TUIO.ORIGIN_ADDRESS, "/tuio/2Dobj");
			dataToken.setAttribute(DataConstant.SESSION_ID,
					(Integer) dataPosition2D
							.getAttribute(DataConstant.SESSION_ID));
			dataToken.setAttribute(TUIO.MOVEMENT_VECTOR_X, dataPosition2D
					.getAttribute(TUIO.MOVEMENT_VECTOR_X));
			dataToken.setAttribute(TUIO.MOVEMENT_VECTOR_Y, dataPosition2D
					.getAttribute(TUIO.MOVEMENT_VECTOR_Y));
			dataToken.setAttribute(TUIO.ROTATION_VECTOR_A, dataPosition2D
					.getAttribute(TUIO.ROTATION_VECTOR_A));
			dataToken.setAttribute(TUIO.ANGLE_A, 0f);
			dataToken.setAttribute(TUIO.ROTATION_ACCELERATION, dataPosition2D
					.getAttribute(TUIO.ROTATION_ACCELERATION));
			dataToken.setAttribute(TUIO.MOTION_ACCELERATION, dataPosition2D
					.getAttribute(TUIO.ROTATION_ACCELERATION));

			if (width < treshholdT0) {
				dataToken.setAttribute(TUIO.FIDUCIAL_ID, tokenID_0);
			} else if (width < treshholdT1) {
				dataToken.setAttribute(TUIO.FIDUCIAL_ID, tokenID_1);
			} else if (width < treshholdT2) {
				dataToken.setAttribute(TUIO.FIDUCIAL_ID, tokenID_2);
			} else if (width < treshholdT3) {
				dataToken.setAttribute(TUIO.FIDUCIAL_ID, tokenID_3);
			} else if (width < treshholdT4) {
				dataToken.setAttribute(TUIO.FIDUCIAL_ID, tokenID_4);
			} else if (width < treshholdT5) {
				dataToken.setAttribute(TUIO.FIDUCIAL_ID, tokenID_5);
			}
//			System.out.println("tracked token with id: " + dataToken.getAttribute(TUIO.FIDUCIAL_ID));
		}
System.out.println("width: " + width + "tokenid" + dataToken.getAttribute(TUIO.FIDUCIAL_ID));
		DataPosition2D Pos2DTmp = setResolutionBack(dataPosition2D);
		width = (Float) Pos2DTmp.getAttribute(TUIO.WIDTH);
		double x = Pos2DTmp.getX();
		double y = Pos2DTmp.getY();

		dataToken.setX(x);
		dataToken.setY(y);
		dataToken.setAttribute(TUIO.WIDTH, width);
		dataToken.setAttribute(DataConstant.SESSION_ID, id);
		allTrackedTokens.add(id);

		return dataToken;
	}

	private HashSet<Integer> setHashSetAlives(DataPosition2D dataPosition2D) {
		HashSet<Integer> alives = new HashSet<Integer>();
		String alivestr = (String) dataPosition2D
				.getAttribute(DreaMTouch.TUIO_ALIVE);

		if (alivestr != null) {
			StringTokenizer st = new StringTokenizer(alivestr, " ");
			String sid = new String();

			while (st.hasMoreElements()) {
				sid = st.nextToken();
				int id = Integer.parseInt(sid);
				alives.add(id);
			}
		}
		return alives;
	}

	private DataPosition2D setResolutionBack(DataPosition2D dataPosition2D) {
		double x = dataPosition2D.getX();
		if (x > 1) {
			x = x / resolutionX;
			dataPosition2D.setX(x);
		}
		double y = dataPosition2D.getY();
		if (y > 1) {
			y = y / resolutionY;
			dataPosition2D.setY(y);
		}

		float width = (Float) dataPosition2D.getAttribute(TUIO.WIDTH);
		width = width / resolutionY;

		dataPosition2D.setAttribute(TUIO.WIDTH, width);
		return dataPosition2D;
	}

	private DataPosition2D setResolution(DataPosition2D dataPosition2D) {
		double x = dataPosition2D.getX();
		x = Math.round(x * resolutionX);
		double y = dataPosition2D.getY();
		y = Math.round(y * resolutionY);
		dataPosition2D.setX(x);
		dataPosition2D.setY(y);

		float width = (Float) dataPosition2D.getAttribute(TUIO.WIDTH);
		width = Math.round(width * resolutionY);

		dataPosition2D.setAttribute(TUIO.WIDTH, width);
		return dataPosition2D;
	}

	// ################################################################################
	// END OF USERDEFINED FUNCTIONS
	// ################################################################################
}