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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public class ImagePanelControl extends AbstractBasicControl<Boolean, JPanel>
		implements MouseListener {
	/**
	 * @param value
	 */

	private ImageComponent imageComp;
	private float imageScaleFactor = 1.0f;
	private int imageWidthInPanel = 560;
	private Vector cornerPoints = new Vector();
	private Dimension cirleDim = new Dimension(4, 4);
	private CirclesPanel circlesPanel = new CirclesPanel();
	private Point circleOffset = new Point(0, 0);

	public ImagePanelControl() {
		super(new JPanel());

		// FlowLayout layout = new FlowLayout();

		((JPanel) getComponent()).setLayout(null);
		imageComp = new ImageComponent();
		// imageComp.setImage("C:\\projects\\Squidy\\squidy-2.0.0\\bayer.jpg");
		Dimension imageDim = imageComp.getImageDimension();
		circleOffset.x = 97;
		circleOffset.y = -3;
		if (imageDim != null) {
			int newW = 0;
			int newH = 0;
			if (imageDim.width > imageDim.height) {
				newW = imageWidthInPanel;
				imageScaleFactor = (float) imageWidthInPanel
						/ (float) imageDim.width;
				newH = (int) (imageScaleFactor * imageDim.height);

			} else {
				newH = imageWidthInPanel;
				imageScaleFactor = (float) imageWidthInPanel
						/ (float) imageDim.width;
				newH = (int) (imageScaleFactor * imageDim.width);
			}
			imageComp.setImageSize(new Dimension(newW, newH));
			imageComp.setBounds(110, 0, newW, newH);
			((JPanel) getComponent()).setBounds(0, 0, imageDim.width, newH);
			circlesPanel.setBounds(0, 0, imageDim.width, newH);
		}

		((JPanel) getComponent()).add(circlesPanel);

		((JPanel) getComponent()).add(imageComp);

		((JPanel) getComponent()).addMouseListener(this);

		/*
		 * ((JPanel) getComponent()).addMouseListener(new MouseListener() {
		 * 
		 * 
		 * });
		 */
	}

	@Override
	public void customPInputEvent(PInputEvent event) {
		PNode node = event.getPickedNode();

		// JComponentWrapper child = (JComponentWrapper)node.getChild(0);
		Point2D po = event.getPositionRelativeTo(node);

		Point p = new Point((int) po.getX(), (int) po.getY());
		p.x = p.x + circleOffset.x;
		p.y = p.y + circleOffset.y;
		Point dummyPoint = new Point(0, 0);
		if (event.isLeftMouseButton()) {
			if (!isPointInVec(p)) {
				cornerPoints.add(p);
				circlesPanel.paintCircles(cornerPoints);
				Paint paint = node.getPaint();
				node.setChildPaintInvalid(true);
				// node.repaint();

			}
		} else if (event.isRightMouseButton()) {
			if (isPointInVec(p)) {
				cornerPoints.remove(getPoint(p));
				circlesPanel.paintCircles(cornerPoints);

			}
		}
		// node.invalidatePaint();
	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		Point p = new Point(e.getX(), e.getY());
		p.setLocation(1, 2);
	}

	public void paintCircles(Graphics g) {
		g.setColor(Color.cyan);
		for (Enumeration e = cornerPoints.elements(); e.hasMoreElements();) {
			Point p = (Point) e.nextElement();
			g.drawOval(p.x - cirleDim.width / 2, p.y - cirleDim.height / 2,
					cirleDim.width, cirleDim.height);
		}
	}

	private boolean isPointInVec(Point p) {
		for (Enumeration e = cornerPoints.elements(); e.hasMoreElements();) {
			Point curPoint = (Point) e.nextElement();
			if ((p.x <= curPoint.x + cirleDim.width / 2 && p.x >= curPoint.x
					- cirleDim.width / 2)
					&& (p.y <= curPoint.y + cirleDim.height / 2 && p.y >= curPoint.y
							- cirleDim.height / 2)) {
				return true;
			}
		}
		return false;
	}

	public Point getPoint(Point p) {
		for (Enumeration e = cornerPoints.elements(); e.hasMoreElements();) {
			Point curPoint = (Point) e.nextElement();
			if ((p.x <= curPoint.x + cirleDim.width / 2 && p.x >= curPoint.x
					- cirleDim.width / 2)
					&& (p.y <= curPoint.y + cirleDim.height / 2 && p.y >= curPoint.y
							- cirleDim.height / 2)) {
				return curPoint;
			}
		}
		return null;
	}

	public void mouseClicked(PInputEvent event) {
		PNode node = event.getPickedNode();
		Point2D po = event.getPositionRelativeTo(node);

		Point p = new Point((int) po.getX(), (int) po.getY());
		p.x = p.x + circleOffset.x;
		p.y = p.y + circleOffset.y;
		Point dummyPoint = new Point(0, 0);
		if (event.isLeftMouseButton()) {
			if (!isPointInVec(p)) {
				cornerPoints.add(p);
				circlesPanel.paintCircles(cornerPoints);
				node.repaint();

			}
		} else if (event.isRightMouseButton()) {
			if (isPointInVec(p)) {
				cornerPoints.remove(getPoint(p));
				circlesPanel.paintCircles(cornerPoints);

			}
		}
		node.invalidatePaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.components.basiccontrols.IBasicControl#getValue
	 * ()
	 */

	public Boolean getValue() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.controls.AbstractBasicControl#
	 * setValueWithoutPropertyUpdate(java.lang.Object)
	 */

	public void setValue(Boolean value) {

	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.IBasicControl#valueFromString(java.lang.String)
	 */
//	@Override
	public Boolean valueFromString(String value) {
		return Boolean.valueOf(value);
	}

	public void paintCircle(int x, int y) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.controls.AbstractBasicControl#reconcileComponent
	 * ()
	 */
	@Override
	protected void reconcileComponent() {

	}
}
