package org.squidy.nodes.optitrack.multicursor;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Cursor;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.UIManager;

import org.squidy.designer.component.TransparentWindow;
import org.squidy.designer.util.ImageUtils;
import org.squidy.manager.data.impl.DataPosition2D;


import com.sun.jna.platform.WindowUtils;

public class MCursor{

	private CursorRunnable cursorThread;
	private Thread cThread;
	private long lastUpdate;
	private int timeOut;
	private int handSide;
	private int gestureID;
	
	public MCursor(String imagePath, int size, Point p, int cursorID, int handSide, int gestureID)
	{
		cursorThread = new CursorRunnable(imagePath, size, p,cursorID);
		cThread = new Thread(cursorThread);
		cThread.start();
		this.handSide = handSide;
		this.gestureID = gestureID;
	}
	public int getCursorID()
	{
		return cursorThread.getCursorID();
	}	
	public boolean isReadyToDestroy()
	{
		return cursorThread.isReadyToDestroy();
	}
	public void isReadyToDestroy(boolean ird)
	{
		cursorThread.isReadyToDestroy(ird);
		/*if (ird)
		   cThread.interrupt();*/
	}
	public void forceDestruction()
	{
		cursorThread.forceDestruction();
	}
	public void updateLocation(Point p)
	{
		cursorThread.updateLocation(p);
	}
	public int getHandSide()
	{
		return this.handSide;
	}
	public int getGestureID()
	{
		return gestureID;
	}
	public void setGestureID(int gid)
	{
		this.gestureID = gid;
	}
}
