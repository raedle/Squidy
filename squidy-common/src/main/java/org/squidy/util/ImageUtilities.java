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

package org.squidy.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * <code>ImageUtilities</code>.
 *
 * <pre>
 * Date: Nov 19, 2008
 * Time: 7:40:21 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ImageUtilities.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class ImageUtilities {

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
	public static BufferedImage scaleImageTo(Image image, int width, int height) {

		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);

		double scaleX = (double) width / imageWidth;
		double scaleY = (double) height / imageHeight;
		
		AffineTransform at = AffineTransform.getScaleInstance(scaleX, scaleY);
		
		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = scaledImage.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(image, at, null);
		g2d.dispose();
		
		return scaledImage;
	}
}
