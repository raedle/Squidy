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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>TestInput</code>.
 * 
 * <pre>
 * Date: Jul 15, 2009
 * Time: 1:19:44 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: TestInput.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "TestInput")
@Processor(
	name = "Test Input",
	types = { Processor.Type.INPUT },
	description = "/org/squidy/nodes/html/TestInput.html",
	tags = { "test", "filter" },
	status = Status.UNSTABLE
)
public class TestInput extends AbstractNode {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(TestInput.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "frame-rate")
	@Property(name = "Frame rate", description = "Data objects will be released at the specified frame rate", suffix = "fps")
	@Slider(minimumValue = 0, maximumValue = 200, minorTicks = 10, majorTicks = 50, showTicks = true, showLabels = true, snapToTicks = true)
	private int frameRate = 100;

	/**
	 * @return the frameRate
	 */
	public int getFrameRate() {
		return frameRate;
	}

	/**
	 * @param frameRate
	 *            the frameRate to set
	 */
	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}
	
	@XmlAttribute(name = "data-quantity")
	@Property(
		name = "Data quantity",
		description = "Amount of data object that will be published."
	)
	@Slider(minimumValue = 0, maximumValue = 100, minorTicks = 5, majorTicks = 25, showTicks = true, showLabels = true, snapToTicks = false)
	private int dataQuantity = 1;

	/**
	 * @return the dataQuantity
	 */
	public int getDataQuantity() {
		return dataQuantity;
	}

	/**
	 * @param dataQuantity the dataQuantity to set
	 */
	public void setDataQuantity(int dataQuantity) {
		this.dataQuantity = dataQuantity;
	}

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {

		new Thread() {
			@Override
			public void run() {

				while (isProcessing()) {
					if (frameRate > 0) {
						IData[] data = new IData[dataQuantity];
						for (int i = 0; i < dataQuantity; i++) {
							data[i] = new DataPosition2D(TestInput.class, 1f / (float) (i + 1), 1f / (float) (i + 1));
						}
						publish(data);
					}
					
					try {
						sleep(frameRate == 0 ? 1000 : 1000 / frameRate);
					}
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
}
