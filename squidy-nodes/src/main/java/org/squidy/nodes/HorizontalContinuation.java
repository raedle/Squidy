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
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>HorizontalContinuation</code>.
 * 
 * <pre>
 * Date: Feb 21, 2008
 * Time: 1:32:12 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id
 * @since 1.0
 * 
 */
@XmlType(name = "HorizontalContinuation")
@Processor(
	name = "Horizontal Continuation",
	types = { Processor.Type.FILTER },
	description = "/org/squidy/nodes/html/HorizontalContinuation.html",
	tags = { "continuation", "horizontal" },
	status = Status.UNSTABLE
)
public class HorizontalContinuation extends AbstractNode {

	private static Log LOG = LogFactory.getLog(HorizontalContinuation.class);

	// Defined data constants
	public static final DataConstant HORIZONTAL_CONTINUED = DataConstant.get(Boolean.class, "HORIZONTAL_CONTINUED");
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "lower")
	@Property(
		name = "Lower bound",
		description = "The lower bound of the horizontal continuation."
	)
	@TextField
	private float lower = 0.13f;

	/**
	 * @return the lower
	 */
	public float getLower() {
		return lower;
	}

	/**
	 * @param lower
	 *            the lower to set
	 */
	public void setLower(float lower) {
		this.lower = lower;
	}

	@XmlAttribute(name = "upper")
	@Property(
		name = "Upper bound",
		description = "The upper bound of the horizontal continuation."
	)
	@TextField
	private float upper = 0.87f;

	/**
	 * @return the upper
	 */
	public float getUpper() {
		return upper;
	}

	/**
	 * @param upper
	 *            the upper to set
	 */
	public void setUpper(float upper) {
		this.upper = upper;
	}

	@XmlAttribute(name = "activation-delay")
	@Property(
		name = "Activation delay",
		description = "The delay between horizontal continuation activation."
	)
	@TextField
	private int activationDelay = 100;

	/**
	 * @return the activationDelay
	 */
	public final int getActivationDelay() {
		return activationDelay;
	}

	/**
	 * @param activationDelay
	 *            the activationDelay to set
	 */
	public final void setActivationDelay(int activationDelay) {
		this.activationDelay = activationDelay;

		counter = activationDelay;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private boolean started = false;
	private DataPosition2D last = null;
	private DataPosition2D lastLast = null;
	private int counter = activationDelay;
	private boolean onBoarder = false;

	/**
	 * 
	 */
	public HorizontalContinuation() {
		// empty
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ukn.hci.interaction.manager.IProcessable#process(de.ukn.hci.interaction.manager.data.IData)
	 */
	public synchronized IData process(DataPosition2D dataPosition2D) {

		if (!started) {
			started = true;

			new Thread(new Runnable() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					while (isProcessing()) {
						if (--counter < 0 && !onBoarder) {
							compPoint();
							onBoarder = true;
							lastLast = last = null;
						}
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							if (LOG.isErrorEnabled()) {
								LOG.error(e.getMessage(), e);
							}
						}
					}
				}
			}).start();
		}
		if (!dataPosition2D.hasAttribute(HORIZONTAL_CONTINUED) || !((Boolean) dataPosition2D.getAttribute(HORIZONTAL_CONTINUED))) {
			lastLast = last;
			last = dataPosition2D.getClone();
			counter = activationDelay;
			onBoarder = false;
		}
		return dataPosition2D;
	}

	private void compPoint() {
		if (lastLast == null || last == null)
			return;

		if (last.getX() > upper || last.getX() < lower) {
			double x = -1;
			double y = -1;

			double m = (lastLast.getY() - last.getY()) / (lastLast.getX() - last.getX());

			if (last.getX() > lastLast.getX()) {
				x = 0.999;
				if (last.getX() < 0.5)
					return;
			} else {
				x = 0.0;
				if (last.getX() > 0.5)
					return;
			}

			if ((m < 1000) && (m > -1000) && (m != 0)) {
				double b = lastLast.getY() - lastLast.getX() * m;
				y = m * x + b;
			} else {
				y = last.getY();
			}
			if (y < 0 || y > 1)
				return;

			DataPosition2D dataPosition2D = new DataPosition2D(this.getClass(), x, y);
			dataPosition2D.setAttribute(HORIZONTAL_CONTINUED, true);
			publish(dataPosition2D);
			
			if (LOG.isDebugEnabled()) {
				LOG.debug(x + " " + y);
			}
		}

	}
}
