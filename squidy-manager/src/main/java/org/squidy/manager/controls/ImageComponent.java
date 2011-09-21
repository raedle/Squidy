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

package org.squidy.manager.controls;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

public class ImageComponent extends JComponent {
	private BufferedImage image;


	public ImageComponent() {
		this.setOpaque(false);
        

	}

	public void setImage(String path) {
		try {
			if ((image = ImageIO.read(new File(path))) != null) {
				setPreferredSize(new Dimension(image.getWidth(), image
						.getHeight()));
				repaint();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Dimension getImageDimension() {
		if (image != null) {
			return new Dimension(image.getWidth(), image.getHeight());
		} else {
			return null;
		}
	}

	public void setImageSize(Dimension d) {
		if (image == null)
			return;

		ImageFilter replicate = new ReplicateScaleFilter(d.width, d.height);

		ImageProducer prod = new FilteredImageSource(image.getSource(),
				replicate);
		Image img = createImage(prod);
		image.getGraphics().drawImage(img, 0, 0, null);

	}

	@Override
	protected void paintComponent(Graphics g) {
		//Graphics2D g2 = (Graphics2D) g.create();
        //g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); 
		if (image != null) {
			g.drawImage(image, 0, 0, this);
		}

	}

	public void mouseClicked(PInputEvent event) {

	}

}
