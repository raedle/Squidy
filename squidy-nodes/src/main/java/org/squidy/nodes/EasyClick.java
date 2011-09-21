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
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.easyclick.DataHistory;


/**
 * TODO: needs to be refreshed (check if wait/notify is possible instead of sleep)
 *
 * <code>EasyClick</code>.
 *
 * <pre>
 * Date: Feb 12, 2008
 * Time: 1:39:46 AM
 * </pre>
 *
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: EasyClick.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "EasyClick")
@Processor(
	name = "Easy Click",
	icon = "/org/squidy/nodes/image/48x48/tactilefinger.png",
	description = "/org/squidy/nodes/html/EasyClick.html",
	types = { Processor.Type.FILTER },
	tags = {"click", "drift", "shift" },
	status = Status.UNSTABLE
)
public class EasyClick extends AbstractNode {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(EasyClick.class);

	// Defined data constants.
	public static final DataConstant EASY_CLICKED = DataConstant.get(Boolean.class, "EASY_CLICKED");
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "history-size")
	@Property(
		name = "History size",
		description = "The size of the position history."
	)
	@TextField
	private int historySize = 1000;

	/**
	 * @return the historySize
	 */
	public final int getHistorySize() {
		return historySize;
	}

	/**
	 * @param historySize
	 *            the historySize to set
	 */
	public final void setHistorySize(int historySize) {
		this.historySize = historySize;
	}

	@XmlAttribute(name = "history-time-difference-x")
	@Property(
		name = "History time difference X",
		description = "The history time difference in x-coordinate direction."
	)
	@TextField
	private long historyTimeDifferenceX = 20;

	/**
	 * @return the historyTimeDifferenceX
	 */
	public final long getHistoryTimeDifferenceX() {
		return historyTimeDifferenceX;
	}

	/**
	 * @param historyTimeDifferenceX
	 *            the historyTimeDifferenceX to set
	 */
	public final void setHistoryTimeDifferenceX(long historyTimeDifferenceX) {
		this.historyTimeDifferenceX = historyTimeDifferenceX;
	}

	@XmlAttribute(name = "history-time-difference-y")
	@Property(
		name = "History time difference Y",
		description = "The history time difference in y-coordinate direction."
	)
	@TextField
	private long historyTimeDifferenceY = 150;

	/**
	 * @return the historyTimeDifferenceY
	 */
	public final long getHistoryTimeDifferenceY() {
		return historyTimeDifferenceY;
	}

	/**
	 * @param historyTimeDifferenceY
	 *            the historyTimeDifferenceY to set
	 */
	public final void setHistoryTimeDifferenceY(long historyTimeDifferenceY) {
		this.historyTimeDifferenceY = historyTimeDifferenceY;
	}

	@XmlAttribute(name = "history-time-threshold-x")
	@Property(
		name = "History time threshold X",
		description = "The history time threshold in x-coordinate direction."
	)
	@TextField
	private int histTimeThresholdX = 20;

	/**
	 * @return the histTimeThresholdX
	 */
	public final int getHistTimeThresholdX() {
		return histTimeThresholdX;
	}

	/**
	 * @param histTimeThresholdX
	 *            the histTimeThresholdX to set
	 */
	public final void setHistTimeThresholdX(int histTimeThresholdX) {
		this.histTimeThresholdX = histTimeThresholdX;
	}

	@XmlAttribute(name = "history-time-threshold-y")
	@Property(
		name = "History time threshold Y",
		description = "The history time threshold in y-coordinate direction."
	)
	@TextField
	private int histTimeThresholdY = 20;

	/**
	 * @return the histTimeThresholdY
	 */
	public final int getHistTimeThresholdY() {
		return histTimeThresholdY;
	}

	/**
	 * @param histTimeThresholdY
	 *            the histTimeThresholdY to set
	 */
	public final void setHistTimeThresholdY(int histTimeThresholdY) {
		this.histTimeThresholdY = histTimeThresholdY;
	}

	@XmlAttribute(name = "max-distance-x")
	@Property(
		name = "Maximum distance X",
		description = "The maximum distance in x-direction."
	)
	@TextField
	private double maxDistanceX = 0.01;

	/**
	 * @return the maxDistanceX
	 */
	public final double getMaxDistanceX() {
		return maxDistanceX;
	}

	/**
	 * @param maxDistanceX
	 *            the maxDistanceX to set
	 */
	public final void setMaxDistanceX(double maxDistanceX) {
		this.maxDistanceX = maxDistanceX;
	}

	@XmlAttribute(name = "max-distance-y")
	@Property(
		name = "Maximum distance Y",
		description = "The maximum distance in y-direction."
	)
	@TextField
	private double maxDistanceY = 0.01;

	/**
	 * @return the maxDistanceY
	 */
	public final double getMaxDistanceY() {
		return maxDistanceY;
	}

	/**
	 * @param maxDistanceY
	 *            the maxDistanceY to set
	 */
	public final void setMaxDistanceY(double maxDistanceY) {
		this.maxDistanceY = maxDistanceY;
	}

	@XmlAttribute(name = "timer")
	@Property(
		name = "Timer",
		description = "The time the position sending will be locked."
	)
	@TextField
	private int timer = 150;

	/**
	 * @return the timer
	 */
	public int getTimer() {
		return timer;
	}

	/**
	 * @param timer
	 *            the timer to set
	 */
	public void setTimer(int timer) {
		this.timer = timer;
	}

	@XmlAttribute(name = "if-instant-drag")
	@Property(
		name = "If instant drag",
		description = "True if mode changes immidiately to dragging (vs. keeping position)"
	)
	@CheckBox
	private boolean ifInstantDrag = true;

	public final boolean getIfInstantDrag() {
		return ifInstantDrag;
	}

	public final void setIfInstantDrag(boolean ifInstantDrag) {
		this.ifInstantDrag = ifInstantDrag;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private DataHistory history;

	private HistoryTracking historyTracking;

	private boolean keeping;
	private boolean waiting;

	private DataPosition2D clickPos = null;

	private DataPosition2D lastPos;

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() {
		history = new DataHistory(historySize);
		historyTracking = new HistoryTracking();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() {
		if (historyTracking != null) {
			historyTracking.finish();
		}
	}

	/**
	 * @param dataButton
	 * @return
	 */
	public synchronized IData process(DataButton dataButton) {

		if (dataButton.getFlag()) {
			DataPosition2D tmp = lockPos();
			if(tmp!=null && lastPos != null){
				publish(lastPos, dataButton, tmp);
				return null;
			}
		}
		else {
			if (ifInstantDrag && keeping) {
				DataPosition2D tmp = clickPos.getClone();
				tmp.setTimestamp(System.currentTimeMillis());
				publish(lastPos, dataButton, tmp);
				unlockPos();
				return null;
			}
			unlockPos();
			return dataButton;
		}
		return dataButton;
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	public synchronized IData process(DataPosition2D dataPosition2D) {
		lastPos = dataPosition2D.getClone();
		history.process(dataPosition2D);
		if (keeping && !ifInstantDrag) {
			return null;
		}
		return dataPosition2D;
	}

	private synchronized DataPosition2D lockPos() {
		keeping = true;
		waiting = false;

		DataPosition2D last = (DataPosition2D) history.getLastObject();
		if (last == null) {
			unlockPos();
			LOG.info("No last object found in history.");
			return null;
		}

		long now = System.currentTimeMillis();
		DataPosition2D dataX = getTimeClosedData(last, now - historyTimeDifferenceX, histTimeThresholdX);
		DataPosition2D dataY = getTimeClosedData(last, now - historyTimeDifferenceY, histTimeThresholdY);

		if (dataX == null) {
			dataX = last;
			LOG.info("No closed object (time) for X found in history.");
		}
		if (dataY == null) {
			dataY = last;
			LOG.info("No closed object (time) for Y found in history.");
		}

		// System.out.println("x:");
		double x = getPosClosed(dataX.getX(), last.getX(), maxDistanceX, "x");
		// System.out.println("y:");
		double y = getPosClosed(dataY.getY(), last.getY(), maxDistanceY, "y");

		unlockPosTimer();

		clickPos = new DataPosition2D(last.getSource(), x, y);
		clickPos.setAttribute(EASY_CLICKED, true);

		return clickPos;
	}

	private DataPosition2D getTimeClosedData(DataPosition2D ref, long time, int thres) {
		DataPosition2D data = (DataPosition2D) history.getObjectAt(time, thres);
		if (data == null && time < ref.getTimestamp()) {
			data = getTimeClosedData(ref, time + thres, thres);
			LOG.info("time diff had to be reduced: time:" + time + " ref:" + ref);
		}
		return data;
	}

	private double getPosClosed(double pos, double ref, double dist, String direction) {
		double tmp = pos;
		double delta = ref - pos;
		if (delta < 0)
			delta = delta * (-1.0);
		if (delta > dist) {
			LOG.info("estimated pos too far away in "+direction+"-direction");
			// tmp = getPosClosed((pos + ref) / 2.0, ref, dist);
			// double res = (pos + ref) / 2.0;
			// LOG.debug("pos had to be interpolated: pos:" + pos + " ref:" + ref + " delta:" +
			// delta + " res:" + res);
			return ref;
		}
		return tmp;
	}

	private synchronized void unlockPos() {
		if (!isProcessing())
			return;
		keeping = waiting = false;
	}

	private synchronized void unlockPosTimer() {
		waiting = true;

		historyTracking.unlock();
	}

	private class HistoryTracking extends Thread {

		// Allows locking and unlocking history tracking.
		private Object lock = new Object();

		private boolean running = true;

		private HistoryTracking() {
			start();
		}

		public void unlock() {
			synchronized (lock) {
				lock.notify();
			}
		}

		public void finish() {
			running = false;
			synchronized (lock) {
				lock.notify();
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			while (running) {

				synchronized (lock) {
					try {
						lock.wait();

						for (int i = 0; i < timer && waiting; i++) {
							Thread.sleep(1);
						}

						if (waiting) {
							keeping = waiting = false;
						}
					}
					catch (InterruptedException e) {
						if (LOG.isErrorEnabled()) {
							LOG.error(e.getMessage(), e);
						}
					}
				}
			}
		}
	}
}
