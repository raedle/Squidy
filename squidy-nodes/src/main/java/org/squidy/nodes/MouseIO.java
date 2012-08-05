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
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.SquidyException;
import org.squidy.designer.util.InputWindow;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.directinputmouse.CDirectInputMouse;


/**
 * <code>Mouse2D</code>.
 * 
 * <pre>
 * Date: Feb 12, 2008
 * Time: 2:13:34 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: MouseIO.java 772 2011-09-16 15:39:44Z raedle $
 */
@XmlType(name = "MouseIO")
@Processor(
	name = "Mouse I/O",
	icon = "/org/squidy/nodes/image/48x48/mouse.png",
	description = "/org/squidy/nodes/html/MouseIO.html",
	types = { Processor.Type.INPUT, Processor.Type.OUTPUT },
	tags = { "mouse", "2D" }
)
public class MouseIO extends AbstractNode {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(MouseIO.class);
	private static final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
	private InputWindow inputWindow;
	private CDirectInputMouse cdim;
	
	public static final DataConstant CLICK_COUNT = DataConstant.get(Integer.class, "CLICK_COUNT");

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "middle-double-click")
	@Property(name = "Emulate double-click with middle button", description = "True if a click with the middle button should emulate a left-button double-click")
	@CheckBox
	private boolean middleDoubleClick = false;

	/**
	 * @return true if middle button should emulate a double click with left
	 *         button
	 */
	public boolean isMiddleDoubleClick() {
		return middleDoubleClick;
	}

	/**
	 * @param true if middle button should emulate a double click with left
	 *        button
	 */
	public void setMiddleDoubleClick(boolean middleDoubleClick) {
		this.middleDoubleClick = middleDoubleClick;
	}

	// ################################################################################

	@XmlAttribute(name = "open-input-window")
	@Property(name = "Open window for mouse input", description = "True if a dedicated window for mouse tracking should be opened.")
	@CheckBox
	private boolean openInputWindow = !isWindows;

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
				inputWindow.registerMouseListener(this);
			}
		}
		else {
			if (inputWindow != null) {
				inputWindow.removeMouseListener(this);
			}
		}
	}

	// ################################################################################

	@XmlAttribute(name = "manual")
	@Property(name = "Activate manual display setup", group = "Display Settings", description = "Indicates whether mouse position is set with automatically detected display settings or manually defined settings (following parameters).")
	@CheckBox
	private boolean manual = false;

	/**
	 * @return the manual
	 */
	public final boolean isManual() {
		return manual;
	}

	/**
	 * @param manual
	 *            the manual to set
	 */
	public final void setManual(boolean manual) {
		this.manual = manual;
	}

	// ################################################################################

	@XmlAttribute(name = "manual-width")
	@Property(name = "Manual display width", group = "Display Settings", description = "The manual display width of the allowed mouse positioning in pixels. Only used if positioning is set manually.")
	@TextField
	private double manualWidth = 1024;

	/**
	 * @return the manualWidth
	 */
	public final double getManualWidth() {
		return manualWidth;
	}

	/**
	 * @param manualWidth
	 *            the manualWidth to set
	 */
	public final void setManualWidth(double manualWidth) {
		this.manualWidth = manualWidth;
	}

	// ################################################################################

	@XmlAttribute(name = "manual-height")
	@Property(name = "Manual display height", group = "Display Settings", description = "The manual display height of the allowed mouse positioning in pixels. Only used if positioning is set manually.")
	@TextField
	private double manualHeight = 768;

	/**
	 * @return the manualHeight
	 */
	public final double getManualHeight() {
		return manualHeight;
	}

	/**
	 * @param manualHeight
	 *            the manualHeight to set
	 */
	public final void setManualHeight(double manualHeight) {
		this.manualHeight = manualHeight;
	}

	// ################################################################################

	@XmlAttribute(name = "origin-offset-x")
	@Property(name = "Manual display origin offset X", group = "Display Settings", description = "The manual display origin X offset of mouse positions in pixels.")
	@TextField
	private double originOffsetX = 0;

	/**
	 * @return the originOffsetX
	 */
	public final double getOriginOffsetX() {
		return originOffsetX;
	}

	/**
	 * @param originOffsetX
	 *            the originOffsetX to set
	 */
	public final void setOriginOffsetX(double originOffsetX) {
		this.originOffsetX = originOffsetX;
	}

	// ################################################################################

	@XmlAttribute(name = "origin-offset-y")
	@Property(name = "Manual display origin offset Y", group = "Display Settings", description = "The manual display origin Y offset of mouse positions in pixels.")
	@TextField
	private double originOffsetY = 0;

	/**
	 * @return the originOffsetY
	 */
	public final double getOriginOffsetY() {
		return originOffsetY;
	}

	/**
	 * @param originOffsetY
	 *            the originOffsetY to set
	 */
	public final void setOriginOffsetY(double originOffsetY) {
		this.originOffsetY = originOffsetY;
	}

	// ################################################################################

	@XmlAttribute(name = "directinput")
	@Property(name = "Use DirectInput (Windows only)", description = "Indicates whether DirectInput is used with Microsoft Windows to capture mouse position directly from screen.")
	@CheckBox
	private boolean directInput = isWindows;

	/**
	 * @return true if DirectInput is used with Microsoft Windows
	 */
	public boolean isDirectInput() {
		return directInput;
	}

	/**
	 * @param true if DirectInput should be used with Microsoft Windows
	 */
	public void setDirectInput(boolean directInput) {
		this.directInput = directInput;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private Robot robot;

	double minX = 0;
	double minY = 0;
	double maxX = 0;
	double maxY = 0;

	double width = 0;
	double height = 0;

	Rectangle[] screenBounds;
	Rectangle lastBounds;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() {

		// Search minimum/maximum of x/y.
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		screenBounds = new Rectangle[devices.length];
		for (int i = 0; i < devices.length; i++) {
			GraphicsDevice device = devices[i];
			GraphicsConfiguration gc = device.getDefaultConfiguration();
			Rectangle bounds = gc.getBounds();
			screenBounds[i] = bounds;

			double x, y;
			x = bounds.getX();
			minX = Math.min(minX, x);
			x += bounds.getWidth();
			maxX = Math.max(maxX, x);
			y = bounds.getY();
			minY = Math.min(minY, y);
			y += bounds.getHeight();
			maxY = Math.max(maxY, y);
		}

		width = Math.abs(minX) + Math.abs(maxX);
		height = Math.abs(minY) + Math.abs(maxY);

		try {
			robot = new Robot();
			robot.setAutoDelay(0);
		}
		catch (AWTException e) {
			if (LOG.isErrorEnabled())
				LOG.error("Could not initialize Robot.");
			publishFailure(e);
		}

		if (openInputWindow) {
			inputWindow = InputWindow.getInstance();
			inputWindow.registerMouseListener(this);
		}

		if (directInput && isWindows) {
			if (cdim == null) {
				createDirectInputMouse();
				if (cdim != null) {
					if (cdim.Init(0) != 0) {
						cdim = null;
						publishFailure(new SquidyException("Could not initialize DirectInput mouse."));
					}
				}
			}
			if (cdim != null) {
				if (cdim.StartCapture() != 0)
					publishFailure(new SquidyException("Could not start DirectInput mouse."));
				else
					new Thread(new Runnable() {
						public void run() {
							while (processing) {
								cdim.WaitForBufferedData();
								if (processing)
									processDirectInputMouseBufferedData();
							}
						}
					}, getId() + "_DIM").start();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() {
		if (robot != null)
			robot = null;
		if (inputWindow != null)
			inputWindow.removeMouseListener(this);
		if (directInput && isWindows && cdim != null)
			if (cdim.StopCapture() != 0)
				publishFailure(new SquidyException("Could not stop DirectInput mouse."));
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	public IData process(DataPosition2D dataPosition2D) {
		setMouse(dataPosition2D);
		return dataPosition2D;
	}

	/**
	 * @param dataButton
	 * @return
	 */
	public IData process(DataButton dataButton) {
		if (dataButton.getButtonType() == DataButton.BUTTON_1) {
			setMouseStatus(MouseEvent.BUTTON1, dataButton.getFlag());
		}
		if (dataButton.getButtonType() == DataButton.BUTTON_2) {
			if (middleDoubleClick) {
				setMouseStatus(MouseEvent.BUTTON1, true);
				setMouseStatus(MouseEvent.BUTTON1, false);
				setMouseStatus(MouseEvent.BUTTON1, true);
				setMouseStatus(MouseEvent.BUTTON1, false);
			}
			else {
				setMouseStatus(MouseEvent.BUTTON2, dataButton.getFlag());
			}
		}
		if (dataButton.getButtonType() == DataButton.BUTTON_3) {
			setMouseStatus(MouseEvent.BUTTON3, dataButton.getFlag());
			// if (dataButton.getFlag()) {
			// setSingleMousePress(DataButton.BUTTON_3);
			// }
		}

		return dataButton;
	}

	protected void createDirectInputMouse() {
		try {
			cdim = new CDirectInputMouse();
		} catch (Exception e) {
			publishFailure(e);
		}
	}

	protected void processDirectInputMouseBufferedData() {
		int i, count;
		
		cdim.ReadBufferedData();
		count = cdim.GetDataCount();
		for (i = 0; i < count; i++)
			processDirectInputMouseEvent(cdim.GetDataEvent(i), cdim.GetDataValue(i));
	}

	protected void processDirectInputMouseEvent(CDirectInputMouse.EventID iEventID, int iData) {
		IData data = null;

		// move mouse (relative coordinates in iData)
		if (iEventID.equals(CDirectInputMouse.EventID.DIMEID_X) ||
		    iEventID.equals(CDirectInputMouse.EventID.DIMEID_Y) ||
		    iEventID.equals(CDirectInputMouse.EventID.DIMEID_XY)) {
			Point  ptMouse = MouseInfo.getPointerInfo().getLocation();
			double x = (double)ptMouse.x / (maxX - minX); 
			double y = (double)ptMouse.y / (maxY - minY); 
			data = new DataPosition2D(MouseIO.class, x, y);
		}

		/*
		// for debugging only
		if (iEventID.equals(CDirectInputMouse.EventID.DIMEID_XY)) {
			short x = (short)(iData & 0xffff);
			short y = (short)(iData >> 16);
			LOG.debug(iEventID.toString() + " " + x + " " + y);
		}
		*/

		// scroll wheel (offset in iData)
		//if (iEventID.equals(DirectInputMouseEventID.DIMEID_Z)
		//	data = new DataMouseWheel(MouseIO, iData);

		// click button (flag 0x80 is set in iData when pressed)  
		if (iEventID.equals(CDirectInputMouse.EventID.DIMEID_BUTTON0))
			data = new DataButton(MouseIO.class, DataButton.BUTTON_0, iData == 0x80);
		if (iEventID.equals(CDirectInputMouse.EventID.DIMEID_BUTTON1))
			data = new DataButton(MouseIO.class, DataButton.BUTTON_1, iData == 0x80);
		if (iEventID.equals(CDirectInputMouse.EventID.DIMEID_BUTTON2))
			data = new DataButton(MouseIO.class, DataButton.BUTTON_2, iData == 0x80);
		
		if (data != null)
			publish(data);
	}

	protected void setMouse(DataPosition2D dataPosition2D) {

		if (!manual) {
			double xPos = (width * dataPosition2D.getX()) + minX;
			double yPos = (height * dataPosition2D.getY()) + minY;
			boolean allowPoint = false;
			// Fasts up the contain procedure -> most cases it won't require an
			// iteration for all bounds.
			if (lastBounds != null && lastBounds.contains((int) xPos, (int) yPos)) {
				allowPoint = true;
			}
			if (!allowPoint) {
				for (Rectangle bounds : screenBounds) {
					if (bounds.contains((int) xPos, (int) yPos)) {
						lastBounds = bounds;
						allowPoint = true;
						break;
					}
				}
			}
			// Only move mouse if point is within a bound of a graphics device.
			if (allowPoint) {
				robot.mouseMove((int) xPos, (int) yPos);
			}
		}
		else {
			double xPos = (manualWidth * dataPosition2D.getX()) + originOffsetX;
			double yPos = (manualHeight * dataPosition2D.getY()) + originOffsetY;

			robot.mouseMove((int) xPos, (int) yPos);
		}
	}

	protected void setSingleMousePress(int button) {
		if (button == MouseEvent.BUTTON1) {
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		else if (button == MouseEvent.BUTTON2) {
			robot.mousePress(InputEvent.BUTTON2_MASK);
			robot.mouseRelease(InputEvent.BUTTON2_MASK);

		}
		else if (button == MouseEvent.BUTTON3) {
			robot.mousePress(InputEvent.BUTTON3_MASK);
			robot.mouseRelease(InputEvent.BUTTON3_MASK);
		}
	}

	protected void setMouseStatus(int button, boolean status) {
		if (button == MouseEvent.BUTTON1) {
			if (status) {
				robot.mousePress(InputEvent.BUTTON1_MASK);
			}
			else {
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			}
		}
		else if (button == MouseEvent.BUTTON2) {
			if (status) {
				robot.mousePress(InputEvent.BUTTON2_MASK);
			}
			else {
				robot.mouseRelease(InputEvent.BUTTON2_MASK);
			}
		}
		else if (button == MouseEvent.BUTTON3) {
			if (status) {
				robot.mousePress(InputEvent.BUTTON3_MASK);
			}
			else {
				robot.mouseRelease(InputEvent.BUTTON3_MASK);
			}
		}
	}
}
