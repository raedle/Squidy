/**
 * 
 */
package org.squidy.designer.component;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JWindow;

public class TransparentBackground extends JComponent {
//	private JWindow window;
	protected Image background;
	private long lastupdate = 0;
	public boolean refreshRequested = true;

	public TransparentBackground() {
//		this.window = window;
//		updateBackground();
//		window.addComponentListener(this);
//		window.addWindowFocusListener(this);
//		new Thread(this).start();
		
		addPropertyChangeListener("ancestor", new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent arg0) {
				updateBackground();
			}
		});
	}

	public void updateBackground() {
		try {
			Robot rbt = new Robot();
			Toolkit tk = Toolkit.getDefaultToolkit();
			
			PointerInfo pointerInfo = MouseInfo.getPointerInfo();
			Point p = pointerInfo.getLocation();
			
			Dimension dim = tk.getScreenSize();
			
			Container parent = getParent();
			while (!(parent instanceof JWindow) && parent != null) {
				parent = parent.getParent();
			}
			
			background = rbt.createScreenCapture(parent.getBounds());
		} catch (Exception ex) {
//			p(ex.toString());
			ex.printStackTrace();
		}
	}

	public void paintComponent(Graphics g) {
//		Point pos = this.getLocationOnScreen();
//		Point offset = new Point(-pos.x, -pos.y);
		g.drawImage(background, getX(), getY(), null);//offset.x, offset.y, null);
		g.setFont(g.getFont().deriveFont(30.0f));
//		g.drawString("TEST IMAGE", getX() + 40, getY() + 40);
		super.paintComponent(g);
	}

	public void componentShown(ComponentEvent evt) {
		repaint();
	}

//	public void componentResized(ComponentEvent evt) {
//		repaint();
//	}
//
//	public void componentMoved(ComponentEvent evt) {
//		repaint();
//	}
//
//	public void componentHidden(ComponentEvent evt) {
//	}
//
//	public void windowGainedFocus(WindowEvent evt) {
//		refresh();
//	}
//
//	public void windowLostFocus(WindowEvent evt) {
//		refresh();
//	}

//	public void refresh() {
//		if (this.isVisible() && window.isVisible()) {
//			repaint();
//			refreshRequested = true;
//			lastupdate = new Date().getTime();
//		}
//	}

	/*
	 * private boolean recurse = false; public void quickRefresh() {
	 * p("quick refresh"); long now = new Date().getTime(); if(recurse || ((now
	 * - lastupdate) < 1000)) { return; }
	 * 
	 * recurse = true; Point location = frame.getLocation(); frame.hide();
	 * updateBackground(); frame.show(); frame.setLocation(location); repaint();
	 * recurse = false; lastupdate = now; }
	 */

//	public void run() {
//		try {
//			while (true) {
//				Thread.sleep(250);
//				long now = new Date().getTime();
//				if (refreshRequested && ((now - lastupdate) > 1000)) {
//					if (window.isVisible()) {
//						Point location = window.getLocation();
//						window.hide();
//						updateBackground();
//						window.show();
//						window.setLocation(location);
//						refresh();
//					}
//					lastupdate = now;
//					refreshRequested = false;
//				}
//			}
//		} catch (Exception ex) {
//			p(ex.toString());
//			ex.printStackTrace();
//		}
//	}
//
//	public static void p(String str) {
//		System.out.println(str);
//	}

}
