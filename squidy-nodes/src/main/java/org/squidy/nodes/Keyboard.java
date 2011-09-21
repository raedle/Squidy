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

import java.awt.AWTException;
import java.awt.Robot;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.util.InputWindow;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataKey;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>Keyboard</code>.
 * 
 * <pre>
 * Date: Aug 20, 2009
 * Time: 2:13:34 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: Keyboard.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "Keyboard")
@Processor(
	name = "Keyboard",
	icon = "/org/squidy/nodes/image/48x48/keyboard.png", 
	types = {Processor.Type.INPUT, Processor.Type.OUTPUT }, 
	description = "/org/squidy/nodes/html/Keyboard.html",
	tags = { "keyboard", "write", "keyevent" }
)
public class Keyboard extends AbstractNode {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Keyboard.class);

	public static final DataConstant KEY_EVENT = DataConstant.get(Integer.class, "KEY_EVENT");

	InputWindow inputWindow;

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "open-input-window")
	@Property(name = "Open window for keyboard input", description = "True if a dedicated window for keyboard tracking should be opened.")
	@CheckBox
	private boolean openInputWindow = true;

	/**
	 * @return the openInputWindow
	 */
	public final boolean isOpenInputWindow() {
		return openInputWindow;
	}

	/**
	 * @param openInputWindow
	 *            the openInputWindow to set
	 */
	public final void setOpenInputWindow(boolean openInputWindow) {
		this.openInputWindow = openInputWindow;
		if (openInputWindow && isProcessing()) {
			if (openInputWindow) {
				inputWindow = InputWindow.getInstance();
				inputWindow.registerKeyListener(this);
			}
		}
		else {
			if (inputWindow != null) {
				inputWindow.removeKeyListener(this);
			}
		}
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private Robot robot;

	@Override
	public void onStart() throws ProcessException {
		try {
			robot = new Robot();
			robot.setAutoDelay(0);
		}
		catch (AWTException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Couldn't initiate Robot.");
			}
			publishFailure(e);
		}

		if (openInputWindow) {
			inputWindow = InputWindow.getInstance();
			inputWindow.registerKeyListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() {
		if (robot != null) {
			robot = null;
		}
		if (inputWindow != null) {
			inputWindow.removeKeyListener(this);
		}
	}

	/**
	 * @param dataDigital
	 * @return
	 */
	public IData process(DataDigital dataDigital) {
		Integer key_event = (Integer) dataDigital.getAttribute(Keyboard.KEY_EVENT);
		if (key_event != null) {
			if (dataDigital.getFlag()) {
				robot.keyPress(key_event);
			}
			else {
				robot.keyRelease(key_event);
			}
		}
		return dataDigital;
	}
	
	/**
	 * @param dataKey
	 * @return
	 */
	public IData process(DataKey dataKey) {
		Integer key_event = dataKey.getKeyType();
		if (key_event != null) {
			if (dataKey.getFlag()) {
				robot.keyPress(key_event);
			}
			else {
				robot.keyRelease(key_event);
			}
		}
		return dataKey;
	}

}
