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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>ScreenDispatcher</code>.
 *
 * <pre>
 * Date: Sep 1, 2008
 * Time: 7:03:00 PM
 * </pre>
 *
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: ScreenDispatcher.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
@XmlType(name = "Screen Dispatcher")
@Processor(
	name = "Screen Dispatcher",
	icon = "/org/squidy/nodes/image/48x48/screen-dispatcher.png",
	description = "/org/squidy/nodes/html/ScreenDispatcher.html",
	types = { Processor.Type.OUTPUT },
	tags = { "screen", "dispatcher", "capture", "casting", "video" },
	status = Status.UNSTABLE
)
public class ScreenDispatcher extends AbstractNode {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ScreenDispatcher.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "remote-address")
	@Property(
		name = "Remote address",
		description = "The address to the remote device."
	)
	@TextField
	private String remoteAddress = "141.14.248.74";

	/**
	 * @return the remoteAddress
	 */
	public final String getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * @param remoteAddress
	 *            the remoteAddress to set
	 */
	public final void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;

		if (isProcessing()) {
			closeDispatcherSocket();
			openDispatcherSocket();
		}
	}

	@XmlAttribute(name = "remote-port")
	@Property(
		name = "Remote port",
		description = "The port to the remote device."
	)
	@TextField
	private int remotePort = 7777;

	/**
	 * @return the remotePort
	 */
	public final int getRemotePort() {
		return remotePort;
	}

	/**
	 * @param remotePort
	 *            the remotePort to set
	 */
	public final void setRemotePort(int remotePort) {
		this.remotePort = remotePort;

		if (isProcessing()) {
			closeDispatcherSocket();
			openDispatcherSocket();
		}
	}

	@XmlAttribute(name = "dispatch-position-x")
	@Property(
		name = "Dispatch position x",
		description = "The X position of the dispatched screen."
	)
	@TextField
	private int dispatchPositionX = 0;

	/**
	 * @return the dispatchPositionX
	 */
	public final int getDispatchPositionX() {
		return dispatchPositionX;
	}

	/**
	 * @param dispatchPositionX the dispatchPositionX to set
	 */
	public final void setDispatchPositionX(int dispatchPositionX) {
		this.dispatchPositionX = dispatchPositionX;
	}

	@XmlAttribute(name = "dispatch-position-y")
	@Property(
		name = "Dispatch position y",
		description = "The Y position of the dispatched screen."
	)
	@TextField
	private int dispatchPositionY = 0;

	/**
	 * @return the dispatchPositionY
	 */
	public final int getDispatchPositionY() {
		return dispatchPositionY;
	}

	/**
	 * @param dispatchPositionY the dispatchPositionY to set
	 */
	public final void setDispatchPositionY(int dispatchPositionY) {
		this.dispatchPositionY = dispatchPositionY;
	}

	@XmlAttribute(name = "dispatch-position-width")
	@Property(
		name = "Dispatch position width",
		description = "The width of the dispatched screen."
	)
	@TextField
	private int dispatchPositionWidth = 1280;

	/**
	 * @return the dispatchPositionWidth
	 */
	public final int getDispatchPositionWidth() {
		return dispatchPositionWidth;
	}

	/**
	 * @param dispatchPositionWidth the dispatchPositionWidth to set
	 */
	public final void setDispatchPositionWidth(int dispatchPositionWidth) {
		this.dispatchPositionWidth = dispatchPositionWidth;
	}

	@XmlAttribute(name = "dispatch-position-height")
	@Property(
		name = "Dispatch position height",
		description = "The height of the dispatched screen."
	)
	@TextField
	private int dispatchPositionHeight = 800;

	/**
	 * @return the dispatchPositionHeight
	 */
	public final int getDispatchPositionHeight() {
		return dispatchPositionHeight;
	}

	/**
	 * @param dispatchPositionHeight the dispatchPositionHeight to set
	 */
	public final void setDispatchPositionHeight(int dispatchPositionHeight) {
		this.dispatchPositionHeight = dispatchPositionHeight;
	}

	@XmlAttribute(name = "screen-width")
	@Property(
		name = "Screen width",
		description = "The screen width of the screen."
	)
	@TextField
	private int screenWidth = 1280;

	/**
	 * @return the screenWidth
	 */
	public final int getScreenWidth() {
		return screenWidth;
	}

	/**
	 * @param screenWidth the screenWidth to set
	 */
	public final void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	@XmlAttribute(name = "screen-height")
	@Property(
		name = "Screen height",
		description = "The screen height of the screen."
	)
	@TextField
	private int screenHeight = 800;

	/**
	 * @return the screenHeight
	 */
	public final int getScreenHeight() {
		return screenHeight;
	}

	/**
	 * @param screenHeight the screenHeight to set
	 */
	public final void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	@XmlAttribute(name = "remote-screen-width")
	@Property(
		name = "Remote screen width",
		description = "The screen width of the remote screen."
	)
	@TextField
	private int remoteScreenWidth = 320;

	/**
	 * @return the remoteScreenWidth
	 */
	public final int getRemoteScreenWidth() {
		return remoteScreenWidth;
	}

	/**
	 * @param remoteScreenWidth
	 *            the remoteScreenWidth to set
	 */
	public final void setRemoteScreenWidth(int remoteScreenWidth) {
		this.remoteScreenWidth = remoteScreenWidth;
	}

	@XmlAttribute(name = "remote-screen-height")
	@Property(
		name = "Remote screen height",
		description = "The screen height of the remote screen."
	)
	@TextField
	private int remoteScreenHeight = 480;

	/**
	 * @return the remoteScreenHeight
	 */
	public final int getRemoteScreenHeight() {
		return remoteScreenHeight;
	}

	/**
	 * @param remoteScreenHeight
	 *            the remoteScreenHeight to set
	 */
	public final void setRemoteScreenHeight(int remoteScreenHeight) {
		this.remoteScreenHeight = remoteScreenHeight;
	}

	@XmlAttribute(name = "scale-screen")
	@Property(
		name = "Scale screen",
		description = "Whether the screen should be scaled before transmitting the screen to the remote device.."
	)
	@CheckBox
	private boolean scaleScreen = true;

	/**
	 * @return the scaleScreen
	 */
	public final boolean isScaleScreen() {
		return scaleScreen;
	}

	/**
	 * @param scaleScreen the scaleScreen to set
	 */
	public final void setScaleScreen(boolean scaleScreen) {
		this.scaleScreen = scaleScreen;
	}

	@XmlAttribute(name = "flip-screen")
	@Property(
		name = "Flip screen",
		description = "Whether the screen ."
	)
	@CheckBox
	private boolean flipScreen = false;

	/**
	 * @return the flipScreen
	 */
	public final boolean isFlipScreen() {
		return flipScreen;
	}

	/**
	 * @param flipScreen
	 *            the flipScreen to set
	 */
	public final void setFlipScreen(boolean flipScreen) {
		this.flipScreen = flipScreen;
	}

	@XmlAttribute(name = "refresh-rate")
	@Property(
		name = "Refresh rate",
		description = "Indicates the refresh rate of the dispatcher.",
		suffix = "ms"
	)
	@Slider(
		minimumValue = 0,
		maximumValue = 5000,
		minorTicks = 500,
		majorTicks = 2500,
		showTicks = true,
		showLabels = true
	)
	private int refreshRate = 500;

	/**
	 * @return the refreshRate
	 */
	public final int getRefreshRate() {
		return refreshRate;
	}

	/**
	 * @param refreshRate the refreshRate to set
	 */
	public final void setRefreshRate(int refreshRate) {
		this.refreshRate = refreshRate;
	}

	@XmlAttribute(name = "screen-capture-quality")
	@Property(
		name = "Screen capture quality",
		description = "Defines the quality a screen capture will be sent."
	)
	@Slider(
		minimumValue = 0,
		maximumValue = 100,
		minorTicks = 10,
		majorTicks = 25,
		showTicks = true,
		showLabels = true
	)
	private int screenCaptureQuality = 10;

	/**
	 * @return the screenCaptureQuality
	 */
	public final int getScreenCaptureQuality() {
		return screenCaptureQuality;
	}

	/**
	 * @param screenCaptureQuality the screenCaptureQuality to set
	 */
	public final void setScreenCaptureQuality(int screenCaptureQuality) {
		this.screenCaptureQuality = screenCaptureQuality;
	}

	@XmlAttribute(name = "show-dispatching-debug-window")
	@Property(
		name = "Show dispatching debug window",
		description = "Whether the dispatching debug window should be visible or not."
	)
	@CheckBox
	private boolean showDispatchingDebugWindow = false;

	/**
	 * @return the showDispatchingDebugWindow
	 */
	public final boolean isShowDispatchingDebugWindow() {
		return showDispatchingDebugWindow;
	}

	/**
	 * @param showDispatchingDebugWindow
	 *            the showDispatchingDebugWindow to set
	 */
	public final void setShowDispatchingDebugWindow(boolean showDispatchingDebugWindow) {
		this.showDispatchingDebugWindow = showDispatchingDebugWindow;

		if (isProcessing()) {
			if (showDispatchingDebugWindow) {
				showDispatchingDebugWindow();
			}
			else {
				hideDispatchingDebugWindow();
			}
		}
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private JFrame dispatchingDebugWindow;

	private ScreenCaptureDebugging screenCaptureDebugging;

	private Socket socket;

	private CaptureScreen captureScreen;

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {

		if (showDispatchingDebugWindow) {
			showDispatchingDebugWindow();
		}

		createCaptureScreen(new Rectangle(dispatchPositionX, dispatchPositionY, dispatchPositionWidth, dispatchPositionHeight));
		openDispatcherSocket();

		new Thread() {

			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				while (isProcessing()) {
					try {
						process2(null);

						Thread.sleep(refreshRate);
					}
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		closeDispatcherSocket();
		hideDispatchingDebugWindow();
		disposeCaptureScreen();
	}

	/**
	 * @param bounds
	 */
	private void createCaptureScreen(Rectangle bounds) throws ProcessException {
		try {
			captureScreen = new CaptureScreen(bounds);
		}
		catch (AWTException e) {
			throw new ProcessException(e.getMessage(), e);
		}
	}

	/**
	 *
	 */
	private void disposeCaptureScreen() {
		if (captureScreen != null) {
			captureScreen.dispose();
		}
	}

	/**
	 *
	 */
	private void openDispatcherSocket() {
		try {
			socket = new Socket(remoteAddress, remotePort);
		}
		catch (UnknownHostException e) {
//			throw new ProcessException("Could not initialize connection to screen capture receiver.", e);
		}
		catch (IOException e) {
//			throw new ProcessException("Could not initialize connection to screen capture receiver.", e);
		}
	}

	/**
	 *
	 */
	private void closeDispatcherSocket() {
		if (socket != null) {
			try {
				socket.close();
			}
			catch (IOException e) {
				throw new ProcessException("Could not close socket.", e);
			}
			socket = null;
		}
	}

	/**
	 *
	 */
	private void reopenDispatcherSocket() {
		closeDispatcherSocket();
		openDispatcherSocket();
	}

	/**
	 *
	 */
	private void showDispatchingDebugWindow() {
		if (dispatchingDebugWindow == null) {
			dispatchingDebugWindow = new JFrame("Dispatching Debug Window");
			dispatchingDebugWindow.setLayout(new BorderLayout());
			dispatchingDebugWindow.setSize(new Dimension(320, 480));
			dispatchingDebugWindow.setPreferredSize(new Dimension(320, 480));
			screenCaptureDebugging = new ScreenCaptureDebugging();
			dispatchingDebugWindow.add(screenCaptureDebugging, BorderLayout.CENTER);
			dispatchingDebugWindow.setVisible(true);
		}
	}

	/**
	 *
	 */
	private void hideDispatchingDebugWindow() {
		if (dispatchingDebugWindow != null) {
			dispatchingDebugWindow.setVisible(false);
			dispatchingDebugWindow.dispose();
			dispatchingDebugWindow = null;
		}
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	public IData process(DataPosition2D dataPosition2D) {

		double x = dataPosition2D.getX();
		double y = dataPosition2D.getY();
		double scaleX = 1.0 / screenWidth;
		double scaleY = 1.0 / screenHeight;

		x = (scaleX * dispatchPositionX) + (x * scaleX * dispatchPositionWidth);
		y = (scaleY * dispatchPositionY) + (y * scaleY * dispatchPositionHeight);

		dataPosition2D.setX(x);
		dataPosition2D.setY(y);

		return dataPosition2D;
	}

	/**
	 * @param data
	 * @return
	 */
	public IData process(IData data) {
		return data;
	}

	/**
	 * @param data
	 * @return
	 * @throws ProcessException
	 */
	public IData process2(IData data) throws ProcessException {

		// TODO [RR]: This may causes an exception if squidy will be stopped but dispatching thread is still alive.
		if (captureScreen == null) {
			throw new ProcessException("Capture screen to capture a screenshot is null", data);
		}

		BufferedImage image = captureScreen.capture();

		// Scale screen if set.
		if (scaleScreen) {
			image = scaleImageTo(image, remoteScreenWidth, remoteScreenHeight);
		}

		if (dispatchingDebugWindow != null) {
			screenCaptureDebugging.setImage(image);
			dispatchingDebugWindow.repaint();
		}

		// Reopen dispatcher socket if connection has been closed.
		if (socket == null || socket.isClosed() || !socket.isConnected()) {

			if (LOG.isDebugEnabled()) {
				LOG.debug("Connection is closed. Trying to reconnect in 5000 ms.");
			}

			try {
				Thread.sleep(5000);
			}
			catch (InterruptedException e1) {
				throw new ProcessException(e1.getMessage(), e1);
			}

			reopenDispatcherSocket();
		}
		else {
			try {
				float tmpScreenCaptureQuality = screenCaptureQuality / (float) 100;
//				System.out.println("QUAL: " + tmpScreenCaptureQuality);
				byte[] picture = ImageKit.getBytes(image, tmpScreenCaptureQuality);

				DataOutputStream stream = new DataOutputStream(socket.getOutputStream());

//				byte[] picture = pictureStream.toByteArray();

//				int v = picture.length;
//				System.out.println("LENGTH: " + v);
//				System.out.println((v >>> 24) & 0xFF);
//				System.out.println((v >>> 16) & 0xFF);
//				System.out.println((v >>>  8) & 0xFF);
//				System.out.println((v >>>  0) & 0xFF);

//				System.out.println("SIZE: " + picture.length);

				stream.writeInt(picture.length);
				stream.write(picture);
			}
			catch (IOException e) {
				closeDispatcherSocket();
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
//		else {
//			if (LOG.isErrorEnabled()) {
//				LOG.error("Dispatching socket is not open or have been closed already.");
//			}
////			throw new ProcessException("Dispatching socket is not open or have been closed already.", data);
//		}

		return data;
	}

	/**
	 * Scales an image with pixel ratio awareness.
	 *
	 * @param image
	 *            The source image that will be used to scale the image.
	 * @param width
	 *            The maximum width of the scaled image.
	 * @param height
	 *            The maximum height of the scaled image.
	 * @return Either the scaled image if its bigger than the source's width AND height or the
	 *         source image.
	 */
	/**
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	private BufferedImage scaleImageTo(BufferedImage image, int width, int height) {

		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);

		double scaleX = (double) width / imageWidth;
		double scaleY = (double) height / imageHeight;

		if (flipScreen) {
			scaleX = (double) width / imageHeight;
			scaleY = (double) height / imageWidth;
		}

		AffineTransform at = AffineTransform.getScaleInstance(scaleX, scaleY);
		if (flipScreen) {
			at.rotate(Math.PI / 2);
			at.translate(0, -imageHeight);
		}

		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = scaledImage.createGraphics();
		g2d.drawImage(image, at, null);
		g2d.dispose();

		return scaledImage;
	}

	@SuppressWarnings("serial")
	private class ScreenCaptureDebugging extends JComponent {

		private BufferedImage image;

		public void setImage(BufferedImage image) {
			if (this.image != null) {

			}
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

			if (image != null) {
				g.drawImage(image, 0, 0, null);
			}
		}
	}

	/**
	 * <code>CaptureScreen</code>.
	 *
	 * <pre>
	 * Date: Oct 29, 2008
	 * Time: 4:24:14 PM
	 * </pre>
	 *
	 * @author Roman R&auml;dle, <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
	 *         University of Konstanz
	 * @version $Id: ScreenDispatcher.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.1.0
	 */
	class CaptureScreen {

		private CaptureData[] captureData;

		private Rectangle bounds;

		public CaptureScreen(Rectangle bounds) throws AWTException {
			this.bounds = bounds;
			initializeCaptureData(bounds);
		}

		/**
		 * @param bounds
		 * @throws AWTException
		 */
		private void initializeCaptureData(Rectangle bounds) throws AWTException {
			List<CaptureData> data = new ArrayList<CaptureData>();

			GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
			for (GraphicsDevice graphicsDevice : graphicsDevices) {
				GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();

				Rectangle screenBounds = graphicsConfiguration.getBounds();


				if (screenBounds.intersects(bounds)) {
					Rectangle intersectionBounds = screenBounds.intersection(bounds);
					data.add(new CaptureData(new Robot(graphicsDevice), intersectionBounds));
				}
			}

			captureData = data.toArray(new CaptureData[data.size()]);
		}

		/**
		 * @return
		 */
		public BufferedImage capture() {

			if (captureData != null) {

				BufferedImage image = new BufferedImage((int) bounds.getWidth(), (int) bounds.getHeight(), BufferedImage.TYPE_INT_RGB);
				for (CaptureData data : captureData) {
					data.captureToImage(image);
				}
				return image;
			}
			return null;
		}

		/**
		 *
		 */
		public void dispose() {
			if (captureData != null) {
				for (CaptureData data : captureData) {
					data.dispose();
				}
			}
		}
	}

	/**
	 * <code>CaptureScreen</code>.
	 *
	 * <pre>
	 * Date: Oct 29, 2008
	 * Time: 4:11:21 PM
	 * </pre>
	 *
	 * @author Roman R&auml;dle, <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
	 *         University of Konstanz
	 * @version $Id: ScreenDispatcher.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.1.0
	 */
	class CaptureData {

		private Robot robot;

		private Rectangle bounds;

		/**
		 * @param robot
		 * @param bounds
		 */
		public CaptureData(Robot robot, Rectangle bounds) {
			this.robot = robot;
			this.bounds = bounds;
		}

		/**
		 * @param image
		 */
		public void captureToImage(BufferedImage image) {

			if (robot == null) {
				return;
			}
			
//			Rectangle adjustBounds = new Rectangle(0, 0, (int) bounds.getWidth(), (int) bounds.getHeight());
//			BufferedImage screenCapture = robot.createScreenCapture(adjustBounds);
			BufferedImage screenCapture = robot.createScreenCapture(bounds);

			Graphics2D g2d = image.createGraphics();
			g2d.drawImage(screenCapture, (int) bounds.getX() - dispatchPositionX, (int) bounds.getY() - dispatchPositionY, null);
			g2d.dispose();
		}

		/**
		 *
		 */
		public void dispose() {
			if (robot != null) {
				robot = null;
			}
		}
	}

	/**
	 * <code>ImageKit</code>.
	 *
	 * <pre>
	 * Date: Oct 29, 2008
	 * Time: 4:11:25 PM
	 * </pre>
	 *
	 * @author Roman R&auml;dle, <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
	 *         University of Konstanz
	 * @version $Id: ScreenDispatcher.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.1.0
	 */
	static public class ImageKit {
	    //quality means jpeg output, if quality is < 0 ==> use default quality
	    public static void write(BufferedImage image, float quality, OutputStream out) throws IOException {
	        Iterator writers = ImageIO.getImageWritersBySuffix("jpeg");
	        if (!writers.hasNext())
	            throw new IllegalStateException("No writers found");
	        ImageWriter writer = (ImageWriter) writers.next();
	        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
	        writer.setOutput(ios);
	        ImageWriteParam param = writer.getDefaultWriteParam();
	        if (quality >= 0) {
	            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	            param.setCompressionQuality(quality);
	        }
	        writer.write(null, new IIOImage(image, null, null), param);
	    }

	    public static BufferedImage read(byte[] bytes) {
	        try {
	            return ImageIO.read(new ByteArrayInputStream(bytes));
	        } catch(IOException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    public static byte[] getBytes(BufferedImage image, float quality) {
	        try {
	            ByteArrayOutputStream out = new ByteArrayOutputStream(50000);
	            write(image, quality, out);
	            return out.toByteArray();
	        } catch(IOException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    public static BufferedImage compress(BufferedImage image, float quality) {
	        try {
	            ByteArrayOutputStream out = new ByteArrayOutputStream(50000);
	            write(image, quality, out);
	            return ImageIO.read(new ByteArrayInputStream(out.toByteArray()));
	        } catch(IOException e) {
	            throw new RuntimeException(e);
	        }
	    }
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ScreenDispatcher sd = new ScreenDispatcher();
		sd.setRemoteAddress("192.168.1.101");
		sd.setScreenCaptureQuality(100);
		sd.setDispatchPositionX(0);
		sd.setDispatchPositionWidth(1440);
		sd.setDispatchPositionHeight(900);
//		sd.setFlipScreen(true);
		sd.setRefreshRate(350);
		sd.setShowDispatchingDebugWindow(true);

		sd.start();
	}
}
