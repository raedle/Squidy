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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;

public class CirclesPanel extends JPanel {
	private Vector cornerPoints = new Vector();
	private Dimension cirleDim = new Dimension(4, 4);
	private boolean circlesUpdated = false;		
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.yellow);
		for (Enumeration e = cornerPoints.elements(); e.hasMoreElements();) {
			Point p = (Point) e.nextElement();
			g.drawOval(p.x - cirleDim.width / 2, p.y - cirleDim.height / 2,
					cirleDim.width, cirleDim.height);
		}				
	}
	
	public void paintCircles(Vector circles)
	{
		cornerPoints = circles;
		repaint();
	}
}
