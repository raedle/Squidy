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

package org.squidy.nodes.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
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
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: PatternScreen.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class PatternScreen extends SwitchableCommand implements Serializable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -6586295635391265718L;

	// Logger to log info, error, debug,... messages.
	// private static final Log LOG = LogFactory.getLog(WhiteScreen.class);

	public static final String KEY_PATTERNS = "patterns";

	private String graphicsDevice;
	private int width;
	private int height;
	private int horizontal;
	private int vertical;

	/**
	 * 
	 * @param state
	 */
	public PatternScreen() {
		// empty
	}

	/**
	 * 
	 * @param state
	 * @param horizontal
	 * @param vertical
	 */
	public PatternScreen(String graphicsDevice, int width, int height, int horizontal, int vertical) {
		this.graphicsDevice = graphicsDevice;
		this.width = width;
		this.height = height;
		this.horizontal = horizontal;
		this.vertical = vertical;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public final void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public final void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @param horizontal
	 *            the horizontal to set
	 */
	public final void setHorizontal(int horizontal) {
		this.horizontal = horizontal;
	}

	/**
	 * @param vertical
	 *            the vertical to set
	 */
	public final void setVertical(int vertical) {
		this.vertical = vertical;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.control.command.SwitchableCommand#on(org.squidy
	 * .control.ControlServerContext)
	 */
	public ICommand on(ControlServerContext context) {
		showPattern(context);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.control.command.SwitchableCommand#off(org.squidy
	 * .control.ControlServerContext)
	 */
	public ICommand off(ControlServerContext context) {
		hidePattern(context);
		return null;
	}

	/**
	 * 
	 */
	private void showPattern(ControlServerContext context) {

		Image image = createPattern();

		// Contains all created whites.
		List<Window> patterns = new ArrayList<Window>();

		// Exclusive fullscreen mode.
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (GraphicsDevice device : devices) {
			if (graphicsDevice.equals(device.getIDstring())) {
				patterns.add(setDeviceInFullScreenMode(device, image));
			}
		}

		context.putObject(KEY_PATTERNS, patterns);
	}

	@SuppressWarnings("unchecked")
	private void hidePattern(ControlServerContext context) {

		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (GraphicsDevice device : devices) {
			device.setFullScreenWindow(null);
		}

		List<JFrame> patterns = context.getObject(List.class, KEY_PATTERNS);

		// Nothing to do.
		if (patterns == null) {
			return;
		}

		for (Window pattern : patterns) {
			pattern.setVisible(false);
			pattern.dispose();
		}
	}

	/**
	 * @param device
	 * @return
	 */
	private Window setDeviceInFullScreenMode(GraphicsDevice device, Image image) {

		JFrame pattern = new JFrame("PatternScreen");
		Container contentPane = pattern.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(new ImageComponent(image));
		pattern.setMinimumSize(new Dimension(1920, 1200));
		
		GraphicsConfiguration gc = device.getDefaultConfiguration();
		
		pattern.setBounds(gc.getBounds());
		pattern.setAlwaysOnTop(true);
		pattern.setUndecorated(true);
//		device.setFullScreenWindow(pattern);
		pattern.setVisible(true);
		pattern.setResizable(false);

		if (!device.isFullScreenSupported()) {
			// if (LOG.isErrorEnabled()) {
			// LOG.error("Full screen isn't supported on this computer.");
			// }
		}

		return pattern;
	}

	/**
	 * 
	 * @return
	 */
	private Image createPattern() {

		double rectWidth = (double) width / (double) horizontal;
		double rectHeight = (double) height / (double) vertical;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = image.createGraphics();

		Color recentColor = Color.BLACK;
		for (int i = 0; i < horizontal; i++) {
			recentColor = alternateColor(recentColor);
			for (int j = 0; j < vertical; j++) {
				recentColor = alternateColor(recentColor);
				g2d.setColor(recentColor);
				g2d.fillRect((int) (rectWidth * i), (int) (rectHeight * j), (int) rectWidth, (int) rectHeight);
			}
		}
		g2d.dispose();

		return image;
	}

	/**
	 * @param recentColor
	 * @return
	 */
	private Color alternateColor(Color recentColor) {
		if (Color.BLACK.equals(recentColor)) {
			return Color.WHITE;
		}
		else {
			return Color.BLACK;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PatternScreen[width=" + width + ", height=" + height + ", horizontal=" + horizontal + ", vertical=" + vertical + "]";
	}

	/**
	 * 
	 * <code>ImageComponent</code>.
	 * 
	 * <pre>
	 * Date: Sep 25, 2008
	 * Time: 5:52:00 PM
	 * </pre>
	 * 
	 * @author Roman R&auml;dle, <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman
	 *         .Raedle@uni-konstanz.de</a>, University of Konstanz
	 * @version $Id: PatternScreen.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.1.0
	 */
	private class ImageComponent extends JComponent {

		/**
		 * Generated serial version UID.
		 */
		private static final long serialVersionUID = -7001147749802128380L;

		private Image image;

		private ImageComponent(Image image) {
			this.image = image;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			g.drawImage(image, 0, 0, null);
		}
	}
}
