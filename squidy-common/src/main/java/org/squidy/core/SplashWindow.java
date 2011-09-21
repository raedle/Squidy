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

package org.squidy.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import org.squidy.util.ImageUtilities;


/**
 * <code>SplashWindow</code>.
 * 
 * <pre>
 * Date: Nov 19, 2008
 * Time: 1:24:15 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: SplashWindow.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class SplashWindow extends Window {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 8170196223649522659L;

	public static final double SCREEN_RATIO = 2. / 3.;
	
	/**
	 * The current instance of the splash window. (Singleton design pattern).
	 */
	private static SplashWindow instance;

	/**
	 * The splash image which is displayed on the splash window.
	 */
	private volatile Image image;

	/**
	 * This attribute indicates whether the method paint(Graphics) has been called at least once
	 * since the construction of this window.<br>
	 * This attribute is used to notify method splash(Image) that the window has been drawn at least
	 * once by the AWT event dispatcher thread.<br>
	 * This attribute acts like a latch. Once set to true, it will never be changed back to false
	 * again.
	 * 
	 * @see #paint
	 * @see #splash
	 */
	private boolean paintCalled = false;

	/**
	 * Creates a new instance.
	 * 
	 * @param parent
	 *            the parent of the window.
	 * @param image
	 *            the splash image.
	 */
	private SplashWindow(Frame parent, Image image) {
		super(parent);

		// Load the image
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(image, 0);
		try {
			mt.waitForID(0);
		}
		catch (InterruptedException ie) {
		}

		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

		Dimension splashDimension = calculateSplashDimension(image);
		
		this.image = ImageUtilities.scaleImageTo(image, (int) splashDimension.getWidth(), (int) splashDimension.getHeight());
		setSize(splashDimension);

		// Center the window on the screen
		setLocation((screenDim.width - (int) splashDimension.getWidth()) / 2, (screenDim.height - (int) splashDimension.getHeight()) / 2);

		// Users shall be able to close the splash window by
		// clicking on its display area. This mouse listener
		// listens for mouse clicks and disposes the splash window.
		MouseAdapter disposeOnClick = new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				// Note: To avoid that method splash hangs, we
				// must set paintCalled to true and call notifyAll.
				// This is necessary because the mouse click may
				// occur before the contents of the window
				// has been painted.
				synchronized (SplashWindow.this) {
					SplashWindow.this.paintCalled = true;
					SplashWindow.this.notifyAll();
				}
				dispose();
			}
		};
		addMouseListener(disposeOnClick);
		
//		// Sets the splash window on top of everything
//		setAlwaysOnTop(true);
	}

	/**
	 * Updates the display area of the window.
	 */
	public void update(Graphics g) {
		// Note: Since the paint method is going to draw an
		// image that covers the complete area of the component we
		// do not fill the component with its background color
		// here. This avoids flickering.
		paint(g);
	}

	/**
	 * Paints the image on the window.
	 */
	public void paint(Graphics g) {
		
		g.drawImage(image, 0, 0, this);
		
		Graphics2D g2d = (Graphics2D) g;
		
		Stroke defaultStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(1f));
		Color defaultColor = g.getColor();
		g.setColor(Color.LIGHT_GRAY);
		
		g.drawRect(0, 0, getWidth(), getHeight());
		
		g2d.setStroke(defaultStroke);
		g.setColor(defaultColor);

		// Notify method splash that the window
		// has been painted.
		// Note: To improve performance we do not enter
		// the synchronized block unless we have to.
//		if (!paintCalled) {
//			paintCalled = true;
//			synchronized (this) {
//				notifyAll();
//			}
//		}
	}

	/**
	 * Open's a splash window using the specified image.
	 * 
	 * @param image
	 *            The splash image.
	 */
	public static void splash(Image image) {
		if (instance == null && image != null) {
			Frame f = new Frame();

			// Create the splash image
			instance = new SplashWindow(f, image);

			// Show the window.
			instance.setVisible(true);

			// Note: To make sure the user gets a chance to see the
			// splash window we wait until its paint method has been
			// called at least once by the AWT event dispatcher thread.
			// If more than one processor is available, we don't wait,
			// and maximize CPU throughput instead.
			if (!EventQueue.isDispatchThread() && Runtime.getRuntime().availableProcessors() == 1) {
				synchronized (instance) {
					while (!instance.paintCalled) {
						try {
							instance.wait();
						}
						catch (InterruptedException e) {
						}
					}
				}
			}
		}
	}

	/**
	 * Open's a splash window using the specified image.
	 * 
	 * @param imageURL
	 *            The url of the splash image.
	 */
	public static void splash(URL imageURL) {
		if (imageURL != null) {
			splash(Toolkit.getDefaultToolkit().createImage(imageURL));
		}
	}

	/**
	 * Calculates an adequate splash window size.
	 * 
	 * @param image
	 *            Image used to calculate splash window size.
	 * @return The calculated splash window size.
	 */
	private Dimension calculateSplashDimension(Image image) {
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

		double screenWidth = screenDimension.getWidth();
		double imageWidth = image.getWidth(this);

		double splashWidth = (screenWidth * SCREEN_RATIO);
		if (splashWidth < imageWidth) {
			double ratio = imageWidth / splashWidth;

			return new Dimension((int) splashWidth, (int) (image.getHeight(this) / ratio));
		}
		return new Dimension((int) imageWidth, (int) (image.getHeight(this)));
	}

	/**
	 * Closes the splash window.
	 */
	public static void disposeSplash() {
		if (instance != null) {
			instance.getOwner().dispose();
			instance = null;
		}
	}

	/**
	 * Invokes the main method of the provided class name.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void invokeMain(String className, String[] args) {
		try {
			Class.forName(className).getMethod("main", new Class[] { String[].class }).invoke(null,
					new Object[] { args });
		}
		catch (Exception e) {
			InternalError error = new InternalError("Failed to invoke main method");
			error.initCause(e);
			throw error;
		}
	}
}
