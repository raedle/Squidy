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

package org.squidy.designer.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.SquidyException;
import org.squidy.manager.data.Processor;
import org.squidy.manager.plugin.Plugin;


/**
 * <code>ImageUtils</code>.
 * 
 * <pre>
 * Date: Feb 21, 2009
 * Time: 12:10:09 AM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: ImageUtils.java 776 2011-09-18 21:34:48Z raedle $
 * @since 1.0.0
 */
public class ImageUtils {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ImageUtils.class);

	public static BufferedImage loadImageFromClasspath(String pathInClasspath) throws IOException {
		return ImageIO.read(ImageUtils.class.getResource(pathInClasspath));
	}

	public static Shape getShapeOfImage(BufferedImage image) {
		// Get the data
		Raster data = image.getData();
		//
//		System.out.println("num of bands = " + data.getNumBands());
		// The colour of the pixel looking at
		// Shoulld have length of 4 (RGBA)
		int[] lookAt = null;
		// The map of all the points
		Point2D[][] pointMap = new Point2D[data.getWidth()][data.getHeight()];
		// The from point
		Point2D from = null;
		// The general path
		GeneralPath path = new GeneralPath();

		// Go round height
		for (int y = 0; y < data.getHeight(); y++) {
			// Go round width
			for (int x = 0; x < data.getWidth(); x++) {
				// Get the colour
				lookAt = data.getPixel(x, y, lookAt);
				// The alpha
				int a = lookAt[3];
				// If > then 0
				if (a > 0) {
					// Output 1
					//System.out.print(1);
					// Save point
					pointMap[x][y] = new Point2D.Double(x, y);

					if (from == null) {
						from = pointMap[x][y];
					}
				} // 0
				else {
					// Output 0
					//System.out.print(0);
					// Nothing her
					pointMap[x][y] = null;
				}
			}
			// New line
			//System.out.println();
		}

		// Move it to the from
		if (from != null)
		{
			path.moveTo(from.getX(), from.getY());
			/*
			 * Make the shape
			 */
			// Go round height
			for (int y = 0; y < data.getHeight(); y++) {
				// Go round width
				for (int x = 0; x < data.getWidth(); x++) {
					// If the point is not null
					if (pointMap[x][y] != null) {
						// Draw a line to
						path.append(new Rectangle2D.Double(pointMap[x][y].getX(), pointMap[x][y].getY(), 1, 1), true);
	//					path.lineTo(pointMap[x][y].getX(), pointMap[x][y].getY());
					}
				}
				
			}
			path.closePath();
			// TODO: Put in the middle
			return path;
		}
		return null;
	}

	/**
	 * @param processor
	 * @return
	 * @throws SquidyException
	 */
	public static URL getProcessorIconURL(Processor processor)
			throws SquidyException {
		if ("".equals(processor.icon())) {
			return ImageUtils.class
					.getResource("/org/squidy/nodes/image/48x48/funnel.png");
		}
		URL url = ImageUtils.class.getResource(processor.icon());

		if (url == null) {
			throw new SquidyException(
					"Couldn't load icon for processor. [path="
							+ processor.icon() + "]");
		}
		return url;
	}

	public static URL getPluginIconURL(Plugin plugin) {
		if ("".equals(plugin.icon())) {
			return ImageUtils.class
					.getResource("/org/squidy/nodes/image/48x48/gear.png");
		}
		URL url = ImageUtils.class.getResource(plugin.icon());

		if (url == null) {
			throw new SquidyException("Couldn't load icon for plugin. [path="
					+ plugin.icon() + "]");
		}
		return url;
	}

	public static URL getPluginIconSmallURL(Plugin plugin) {
		if ("".equals(plugin.smallIcon())) {
			return ImageUtils.class
					.getResource("/org/squidy/nodes/image/16x16/gear.png");
		}
		URL url = ImageUtils.class.getResource(plugin.smallIcon());

		if (url == null) {
			throw new SquidyException(
					"Couldn't load small icon for plugin. [path="
							+ plugin.smallIcon() + "]");
		}
		return url;
	}

	/**
	 * @param source
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage scale(BufferedImage source, int width,
			int height) throws IOException {

		BufferedImage target = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = target.createGraphics();
		AffineTransform at = AffineTransform.getScaleInstance((double) width
				/ source.getWidth(), (double) height / source.getHeight());
		g.drawRenderedImage(source, at);

		return target;
	}

	/**
	 * Converts a given image to a grayscale image.
	 * 
	 * @param source
	 * @return
	 */
	public static BufferedImage convertToGrayscaleImage(BufferedImage source) {
		BufferedImage grayImage = new BufferedImage(source.getWidth(), source
				.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < source.getWidth(); x++)
			for (int y = 0; y < source.getHeight(); y++) {
				int argb = source.getRGB(x, y);

				int a = (argb >> 24) & 0xff;
				int r = (argb >> 16) & 0xff;
				int g = (argb >> 8) & 0xff;
				int b = (argb) & 0xff;

				int l = (int) (.299 * r + .587 * g + .114 * b); // luminance

				grayImage.setRGB(x, y, (a << 24) + (l << 16) + (l << 8) + l);
			}
		return grayImage;
	}
}
