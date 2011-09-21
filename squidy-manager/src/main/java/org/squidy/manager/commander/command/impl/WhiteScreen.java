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

package org.squidy.manager.commander.command.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.squidy.manager.commander.ControlServerContext;
import org.squidy.manager.commander.command.ICommand;
import org.squidy.manager.commander.command.SwitchableCommand;


/**
 * <code>WhiteScreen</code>.
 * 
 * <pre>
 * Date: Sep 23, 2008
 * Time: 8:42:58 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: WhiteScreen.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class WhiteScreen extends SwitchableCommand implements Serializable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -5745047762109723496L;

	// Logger to log info, error, debug,... messages.
//	private static final Log LOG = LogFactory.getLog(WhiteScreen.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.control.command.ICommand#execute(org.squidy.control.ControlServerContext
	 * )
	 */
	public WhiteScreen() {
		// empty
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.ukn.hci.squidy.control.command.SwitchableCommand#on(org.squidy.control.
	 * ControlServerContext)
	 */
	public ICommand on(ControlServerContext context) {
		showWhite(context);
		return new Acknowledge();
	}

	/* (non-Javadoc)
	 * @see org.squidy.control.command.SwitchableCommand#off(org.squidy.control.ControlServerContext)
	 */
	public ICommand off(ControlServerContext context) {
		hideWhite(context);
		return new Acknowledge();
	}

	/**
	 * 
	 */
	private void showWhite(ControlServerContext context) {

		// Contains all created whites.
		List<Window> whites = new ArrayList<Window>();

		// Exclusive fullscreen mode.
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (GraphicsDevice device : devices) {
			whites.add(setDeviceInFullScreenMode(device));
		}

		context.putObject("WHITES", whites);
	}

	@SuppressWarnings("unchecked")
	private void hideWhite(ControlServerContext context) {

		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (GraphicsDevice device : devices) {
			device.setFullScreenWindow(null);
		}

		List<JFrame> whites = context.getObject(List.class, "WHITES");
		if (whites != null) {
			for (Window white : whites) {
				white.setVisible(false);
				white.dispose();
			}
		}
	}

	/**
	 * @param device
	 * @return
	 */
	private Window setDeviceInFullScreenMode(GraphicsDevice device) {

		JFrame white = new JFrame();
		white.getContentPane().setBackground(Color.WHITE);
		white.setAlwaysOnTop(true);
		white.setUndecorated(true);
		white.setMinimumSize(new Dimension(1920, 1200));
		
		GraphicsConfiguration gc = device.getDefaultConfiguration();
		
		white.setBounds(gc.getBounds());
		white.setVisible(true);
        white.setResizable(false);


		if (!device.isFullScreenSupported()) {
//			if (LOG.isErrorEnabled()) {
//				LOG.error("Full screen isn't supported on this computer.");
//			}
		}
		return white;
	}
}
