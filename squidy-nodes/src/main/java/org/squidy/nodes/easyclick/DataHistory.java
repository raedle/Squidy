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

package org.squidy.nodes.easyclick;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.impl.DataObject;


/**
 * <code>DataHistory</code>.
 *
 * <pre>
 * Date: Nov 9, 2008
 * Time: 7:02:46 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DataHistory.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class DataHistory {
	
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(DataHistory.class);
	
	private IData[] objects = null;
	private long[] times = null;
	private int size;
	private int pos = 0;
	private boolean locked = false;

	public DataHistory() {

	}

	public DataHistory(int size) {
		this.size = size;
		objects = new IData[size];
		times = new long[size];
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized IData process(IData data) {
		if (!locked) {
			objects[pos % size] = data.getClone();
			times[pos % size] = data.getTimestamp();
			pos++;
			if (pos == 2 * size)
				pos = size;
		} else {
			LOG.debug("History logged. Data array could not be changed.");
		}
		return null;
	}

	/**
	 * Returns an logged object at a specific or closed time
	 * 
	 * @param time,
	 *            target time
	 * @param thres,
	 *            (if no object could be found with a time frame of +/-thres,
	 *            method returns null)
	 * @return
	 */
	public DataObject getObjectAt(long time, int thres) {
		locked = true;
		DataObject o = findObject(time, thres);
		locked = false;
		if (o != null) {
			o.setTimestamp(System.currentTimeMillis());
		}
		return o;
	}

	public DataObject getLastObject() {
		if (pos == 0)
			return null;
		locked = true;
		DataObject o = (DataObject) objects[(pos - 1) % size];
		locked = false;
		return o;
	}

	private DataObject findObject(long time, int thres) {
		for (int i = pos - 1; i >= 0 && i >= pos - size; i--) {
			if (times[i % size] <= time) {
				if (i == pos - 1) {
					IData lower = objects[i % size];
					if (lower == null)
						return null;
					return (DataObject) lower.getClone();
				} else {
					IData lower = objects[i % size];
					IData upper = objects[(i + 1) % size].getClone();
					if (lower == null)
						return null;
					lower = lower.getClone();
					long distLow = Math.abs(time - lower.getTimestamp());
					long distUp = Math.abs(time - upper.getTimestamp());
					if (distLow > thres && distUp > thres) {
						return null;
					}
					if (distLow < distUp) {
						return (DataObject) lower;
					} else {
						return (DataObject) upper;
					}
				}
			}
		}
		return null;
	}
}
