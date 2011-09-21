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

package org.squidy.nodes.laserpointer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.nodes.Laserpointer;


/**
 * <code>MicroControllerLaserDriver</code>.
 * 
 * <pre>
 * Date: Apr 30, 2009
 * Time: 7:59:54 PM
 * </pre>
 * 
 * @author
 * Werner A. Kšnig
 * <a href="mailto:Werner.Koenig@uni-konstanz.de">Werner.Koenig@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: MicroControllerLaserDriver.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 * @see Use {@link PhidgetLaserDriver} instead. !!! Native dll doesn't correspond to this
 * JNI interface anymore. Please re-compile interface and JNI header.
 */
@Deprecated
public class MicroControllerLaserDriver extends Thread implements LaserVibrate {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(MicroControllerLaserDriver.class);

	// ################################################################################
	// BEGIN OF NATIVES
	// ################################################################################

	static {
		System.loadLibrary("laserConnection");
	}

	public native void init();

	public native void close();

	public native int readBL();

	public native int readBR();

	public native int readBU();

	public native void toggleVibOn();

	public native void toggleVibOff();

	// ################################################################################
	// END OF NATIVES
	// ################################################################################

	private boolean running = true;
	private Laserpointer laserPointer;
	private LaserVibration laserVibration;

	// private boolean isVibrating = false;

	public MicroControllerLaserDriver(Laserpointer laserPointer) {
		this.laserPointer = laserPointer;
		init();
		start();
	}

	public void run() {
		while (running) {
			try {
				laserPointer.publish(new DataButton(Laserpointer.class, DataButton.BUTTON_1, (readBL() == 1) ? true	: false));
				laserPointer.publish(new DataButton(Laserpointer.class, DataButton.BUTTON_2, (readBU() == 1) ? true	: false));
				laserPointer.publish(new DataButton(Laserpointer.class, DataButton.BUTTON_3, (readBR() == 1) ? true	: false));

				sleep(8);
			}
			catch (InterruptedException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	/*
	 * TODO: [RR] Make this method simple. (non-Javadoc)
	 * 
	 * @see org.squidy.manager.input.impl.laserPointer.LaserVibrate#vibrate(boolean,
	 *      int)
	 */
	public void vibrate(boolean vibrate, int duration) {

		if (laserVibration != null) {
			laserVibration.cancel();
		}

		if (vibrate) {
			toggleVibOn();
		}
		else {
			toggleVibOff();
		}

		if (duration > 0) {
			laserVibration = new LaserVibration(this, duration);
		}
	}
}
