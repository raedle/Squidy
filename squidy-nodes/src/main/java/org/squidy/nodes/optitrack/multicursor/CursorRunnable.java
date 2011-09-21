package org.squidy.nodes.optitrack.multicursor;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.jhlabs.image.ImageUtils;
import com.sun.jna.platform.WindowUtils;

public class CursorRunnable implements Runnable {
	
	private Object lock = new Object();
	
	public JFrame frame;
	public TransparentCursor tCursor ;
	private int size;
	private String imagePath;
	private Point p, location;
	private long currentTime;
	private boolean readyToDestroy;
	private int cursorID;
	private int cursorTimeout;
	private boolean destroyNow;
	
	public CursorRunnable(String imagePath, int size, Point p, int cursorID)
	{
		super();
		this.imagePath = imagePath;
		this.size = size;
		this.p = p;
		this.cursorID = cursorID;
		this.readyToDestroy = false;
		this.destroyNow = false;
	}
	public void updateLocation(Point p)
	{
		this.location = p;
		this.currentTime = System.currentTimeMillis();
		
		synchronized (lock) {
			lock.notify();
		}
		
		//this.frame.setLocation(p);
		//System.out.println("RUNNABLE " + p.x + " "  + p.y);
	}
	public int getCursorID()
	{
		return this.cursorID;
	}
	public boolean isReadyToDestroy()
	{
		return readyToDestroy;
	}
	public void isReadyToDestroy(boolean ird)
	{
		readyToDestroy = ird;
	}
	public void forceDestruction()
	{
		readyToDestroy = true;
		synchronized (lock) {
			lock.notify();
		}
	}
	public void run()
	{
	    
	    {
			try {
		        System.setProperty("sun.java2d.noddraw", "true");
		        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    }
		    catch(Exception e) {
		    	e.printStackTrace();
		    }
		    frame = new JFrame();
		    //final JFrame frame = new JFrame("Shaped Window Demo");
		    frame.setAlwaysOnTop(true);
		    Dimension dimension = new Dimension(size,size);
		    BufferedImage bImage;
		    try{
		    	bImage = org.squidy.designer.util.ImageUtils.loadImageFromClasspath(imagePath);
		    }catch (IOException e1)
		    {
		    	e1.printStackTrace();
		    	return;
		    }
		    tCursor = new TransparentCursor(new Dimension(size,size),bImage);
		    frame.setSize(dimension);
		    frame.getContentPane().add(tCursor);
		    frame.setUndecorated(true);
		    try {
		        //Shape mask = new Area(new Ellipse2D.Float(-5, -5, size+5, size+5));
		    	Shape mask = org.squidy.designer.util.ImageUtils.getShapeOfImage(bImage);
		    	if (mask != null)
		    	{
			    	int xPoints[] = {0,8,11,15,31,32,21,1};
			    	int yPoints[] = {0,0,2,1,15,23,30,14};
			    	//Shape mask = new Area(new Polygon(xPoints, yPoints, xPoints.length));
			        WindowUtils.setWindowMask(frame, mask);
			        if (WindowUtils.isWindowAlphaSupported()) {
			            WindowUtils.setWindowAlpha(frame, 0.8f);
			        }
		        }
		       // frame.setIconImage(face.getIconImage());
		        frame.setResizable(false);
		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        frame.setFocusable(false);
		        frame.setFocusableWindowState(true);
		        frame.setAlwaysOnTop(true);
		        frame.pack();
		        frame.setLocation(p.x, p.y);
		        frame.setVisible(true);
		    }
		    catch(UnsatisfiedLinkError e) {
		        e.printStackTrace();
		    }
		    while(!readyToDestroy)
		    {
		    	//System.out.println("location " + location);^
		    	synchronized(lock)
		    	{
			    	if (location != null) 
			    	{
				    	frame.setLocation(location);
				    	try {
							lock.wait(1000);
						} catch (InterruptedException e) {
							frame.dispose();
						}
					}
		    	}
		    }
		    frame.dispose();
		    //System.exit(1);
	    }
	}
}
