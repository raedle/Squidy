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


package org.squidy.nodes.wiimote;

import motej.Mote;
import motej.MoteFinder;
import motej.MoteFinderListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>SimpleMoteFinder</code>.
 * 
 * <pre>
 * Date: Apr 21, 2008
 * Time: 1:16:56 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: SimpleMoteFinder.java 772 2011-09-16 15:39:44Z raedle $
 * 
 * $Id: SimpleMoteFinder.java 772 2011-09-16 15:39:44Z raedle $
 */
public class SimpleMoteFinder implements MoteFinderListener {

	// Log to log message like INFO, DEBUG, ERROR messages.
	private static final Log LOG = LogFactory.getLog(SimpleMoteFinder.class);

	private MoteFinder finder;
	private Object lock = new Object();
	private Mote mote;

	public void moteFound(Mote mote) {
		LOG.info("SimpleMoteFinder received notification of a found mote.");
		this.mote = mote;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	public Mote findMote() {
		if (finder == null) {
			finder = MoteFinder.getMoteFinder();
			finder.addMoteFinderListener(this);
		}

//		for (int i = 0; i < 5 || mote == null; i++) {
//			try {
				finder.startDiscovery();
//			}
//			catch (RuntimeException e) {
//				finder.stopDiscovery();
//				
//				if (LOG.isErrorEnabled()) {
//					LOG.error("Restart device discovery: " + e.getMessage(), e);
//				}
//			}
//		}

		try {
			synchronized (lock) {
				lock.wait();
			}
		}
		catch (InterruptedException ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return mote;
	}

}
