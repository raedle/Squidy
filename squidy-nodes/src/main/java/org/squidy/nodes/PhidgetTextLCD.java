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
import org.squidy.SquidyException;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Throughput;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataObject;
import org.squidy.manager.data.impl.DataString;
import org.squidy.manager.model.AbstractNode;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.TextLCDPhidget;


/**
 * <code><ClassName></code>.
 * 
 * <pre>
 * Date: Nov 7, 2008
 * Time: 11:39:17 PM
 * </pre>
 * 
 * @author Stefan Dierdorf, <a
 *         href="mailto:stefan.dierdorf@uni-konstanz.de">stefan
 *         .dierdorf@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: PhidgetTextLCD.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
@XmlType(name = "Phidget LCD")
@Processor(
	name = "Phidget LCD", icon = "/org/squidy/nodes/image/48x48/text_lcd.png",
	types = { Processor.Type.INPUT, Processor.Type.OUTPUT }, 
	description = "/org/squidy/nodes/html/PhidgetLCD.html",
	tags = { "phidget", "interface", "interfacekit", "text", "lcd", "display" },
	status = Status.UNSTABLE
)
public class PhidgetTextLCD extends AbstractNode {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(PhidgetTextLCD.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "interface-kit-serial")
	@Property(name = "InterfaceKit serial")
	@TextField
	private int interfaceKitSerial = 0;

	/**
	 * @return the interfaceKitSerial
	 */
	public final int getInterfaceKitSerial() {
		return interfaceKitSerial;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setInterfaceKitSerial(int interfaceKitSerial) {
		this.interfaceKitSerial = interfaceKitSerial;
	}

	// ################################################################################

	@XmlAttribute(name = "lcd-backlight")
	@Property(name = "LCD backlight", description = "Turn backlight ON/OFF")
	@CheckBox
	private Boolean lcdBacklight = true;

	/**
	 * @return the interfaceKitSerial
	 */
	public final Boolean getLcdBacklight() {
		return lcdBacklight;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setLcdBacklight(Boolean lcdBacklight) {
		this.lcdBacklight = lcdBacklight;

		if (lcdPhidget != null) {
			try {
				lcdPhidget.setBacklight(this.getLcdBacklight());
			} catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// ################################################################################

	@XmlAttribute(name = "lcd-text-1st-row")
	@Property(name = "LCD text 1st row", description = "Set text on LCD")
	@TextField
	private String lcdText1stRow = "LCD initiated";

	/**
	 * @return the interfaceKitSerial
	 */
	public final String getLcdText1stRow() {
		return lcdText1stRow;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setLcdText1stRow(String lcdText1stRow) {
		this.lcdText1stRow = lcdText1stRow;

		if (lcdPhidget != null) {
			try {
				lcdPhidget.setDisplayString(0, this.getLcdText1stRow());
			} catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// ################################################################################

	@XmlAttribute(name = "lcd-text-2nd-row")
	@Property(name = "LCD text 2nd row", description = "Set text on LCD")
	@TextField
	private String lcdText2ndRow = "LCD initiated";

	/**
	 * @return the interfaceKitSerial
	 */
	public final String getLcdText2ndRow() {
		return lcdText2ndRow;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setLcdText2ndRow(String lcdText2ndRow) {
		this.lcdText2ndRow = lcdText2ndRow;

		if (lcdPhidget != null) {
			try {
				lcdPhidget.setDisplayString(1, this.getLcdText2ndRow());
			} catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// ################################################################################

	@XmlAttribute(name = "show-cursor")
	@Property(name = "Show cursor", description = "Show a Cursor on LCD")
	@CheckBox
	private boolean showCursor = false;

	/**
	 * @return the interfaceKitSerial
	 */
	public final boolean getShowCursor() {
		return showCursor;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setShowCursor(boolean showCursor) {
		this.showCursor = showCursor;

		if (lcdPhidget != null) {
			try {
				lcdPhidget.setCursor(this.getShowCursor());
			} catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// ################################################################################

	@XmlAttribute(name = "cursor-blink")
	@Property(name = "Cursor blink", description = "Cursor will blink on LCD")
	@CheckBox
	private boolean cursorBlink = false;

	/**
	 * @return the interfaceKitSerial
	 */
	public final boolean getCursorBlink() {
		return cursorBlink;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setCursorBlink(boolean cursorBlink) {
		this.cursorBlink = cursorBlink;

		if (lcdPhidget != null) {
			try {
				lcdPhidget.setCursorBlink(this.getCursorBlink());
			} catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// ################################################################################

	@XmlAttribute(name = "contrast")
	@Property(name = "LCD contrast", description = "Set Contrast between 0 and 255")
	@Slider(minimumValue = 0, maximumValue = 255)
	private int lcdContrast = 126;

	/**
	 * @return the interfaceKitSerial
	 */
	public final int getLcdContrast() {
		return lcdContrast;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setLcdContrast(int lcdContrast) {
		this.lcdContrast = lcdContrast;

		if (lcdPhidget != null) {
			try {
				lcdPhidget.setContrast(this.getLcdContrast());
			} catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private TextLCDPhidget lcdPhidget;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws SquidyException {

		try {
			lcdPhidget = new TextLCDPhidget();
			if (interfaceKitSerial != 0) {
				lcdPhidget.open(interfaceKitSerial);
			} else {
				lcdPhidget.openAny();
			}
			lcdPhidget.waitForAttachment(10000);
			this.setLcdText1stRow(lcdPhidget.getDeviceName());
			this.setLcdText2ndRow("...initiated");
		} catch (PhidgetException e) {
			throw new SquidyException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {

		if (lcdPhidget != null) {
			try {
				lcdPhidget.setDisplayString(1, "...closed");
				lcdPhidget.close();
			} catch (PhidgetException e) {
				throw new ProcessException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * @param dataString
	 * @return
	 */
	public IData process(DataString dataString) {
		String data = dataString.getData();
		if (data.length() > 20) {
			data = data.substring(0, 20);
		}
		
		try {
			lcdPhidget.setDisplayString(1, data);
		}
		catch (PhidgetException e) {
			throw new ProcessException(e.getMessage(), e, dataString);
		}
		
		return dataString;
	}
}