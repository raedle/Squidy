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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;
import org.squidy.manager.ProcessException;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.g2drecognizer.G2DNBestList;
import org.squidy.nodes.g2drecognizer.G2DPoint;
import org.squidy.nodes.g2drecognizer.G2DRecognizer;


/**
 * <code>GestureRecognizer2D</code>.
 * 
 * <pre>
 * Date: Feb 19, 2008
 * Time: 5:00:56 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version 1.0
 */
@XmlType(name = "GestureRecognizer2D")
@Processor(
	name = "Gesture Recognizer 2D",
	types = { Processor.Type.FILTER },
	description = "/org/squidy/nodes/html/GestureRecognizer.html",
	tags = { "gesture", "recognition" },
	status = Status.UNSTABLE
)
public class GestureRecognizer2D extends AbstractNode {

	private static Logger logger = Logger.getLogger(GestureRecognizer2D.class);
	private G2DRecognizer recognizer;
	private boolean isLearning = false;

	ArrayList<G2DPoint> sample = new ArrayList<G2DPoint>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {
		recognizer = new G2DRecognizer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		// Release instance to get garbage collected.
		recognizer = null;
	}

	public void startSampleLearning() {
		isLearning = true;
		logger.info("Start sample (Learning)");
		sample = new ArrayList<G2DPoint>();
	}

	public void startSampleRecognition() {
		isLearning = false;
		logger.info("Start sample (Recognition)");
		sample = new ArrayList<G2DPoint>();
	}

	public IData stopSample() {
		G2DPoint[] array = new G2DPoint[sample.size()];
		sample.toArray(array);

		if (isLearning) {
			recognizer.addGesture("test", array);
		} else {
			G2DNBestList list = recognizer.Recognize(array);
			if (list == null) {
				logger.debug("Gesture set is empty.");
			} else {
				// TODO Werner: Return DataGesture instead of printout.
				System.out.println(list.getNamesString());
			}
		}
		return null;
	}

	public IData process(DataInertial dataInertial) {
		sample.add(new G2DPoint(dataInertial.getX(), dataInertial.getY()));

		return dataInertial;
	}

	public IData process(DataPosition2D dataPosition2D) {
		sample.add(new G2DPoint(dataPosition2D.getX(), dataPosition2D.getY()));
		// TODO Werner: Replace button handling with pipeline specific context
		// filtering.
		return dataPosition2D;
	}

	public IData process(DataButton dataButton) {

		if (dataButton.getButtonType() == DataButton.BUTTON_1) {
			if (dataButton.getFlag()) {
				startSampleLearning();
			} else {
				return stopSample();
			}
		} else if (dataButton.getButtonType() == DataButton.BUTTON_3) {
			if (dataButton.getFlag()) {
				startSampleRecognition();
			} else {
				return stopSample();
			}
		}
		return dataButton;
	}
}
