package org.squidy.nodes.optitrack.multicursor;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JWindow;

public class BackgroundPanel extends JPanel 
{
	BufferedImage image = null;
	Rectangle rect = null;
	public BackgroundPanel(JWindow window) 
	{
		rect = window.getBounds();
		try {
			image = new Robot().createScreenCapture(rect);
		}
		catch (AWTException e) {
		throw new RuntimeException(e.getMessage());
		}
	}
	public void updateLocation(Point p, int dimension)
	{
		rect.x = p.x;
		rect.y = p.y;
		rect.height = dimension;
		rect.width = dimension;
		try {
			image = new Robot().createScreenCapture(rect);
		}
		catch (AWTException e) {
		throw new RuntimeException(e.getMessage());
		}	
	}
	protected void paintComponent(Graphics g)
	{
		g.drawImage(image, 0, 0, this);
	}	
}
