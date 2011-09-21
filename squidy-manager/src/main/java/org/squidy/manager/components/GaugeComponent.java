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

package org.squidy.manager.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class GaugeComponent extends JComponent {
	
	private static final long serialVersionUID = -3059629062621206109L;
	private static int GAUGE_WIDTH = 200;
	private static int VALUE_WIDTH = 50;
	private static int STATUS_BAR_WIDTH = 2;

	public GaugeComponent () {
		setPreferredSize(new Dimension(GAUGE_WIDTH + VALUE_WIDTH, 20));
	}
	
	private float currentValue = 0.0f;
	
	public float getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(float currentValue) {
		this.currentValue = currentValue;
		repaint();
	}

	protected void paintComponent(Graphics g) {
		
		int width = getWidth();
		int height = getHeight();
		
		GradientPaint gp = new GradientPaint(0, 0.0f, Color.RED, GAUGE_WIDTH, height, Color.GREEN);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(gp);
		g2d.fillRoundRect(0, 0, GAUGE_WIDTH, height, 10, 10);
		
		g2d.setPaint(null);
		g2d.setColor(Color.BLACK);
		float pos = GAUGE_WIDTH * currentValue;
		g2d.fillRect((int) pos - STATUS_BAR_WIDTH / 2, 0, STATUS_BAR_WIDTH, height);
		
		g.drawString(currentValue * 100 + "%", GAUGE_WIDTH + 10, height / 2 + 5);
	}

}
