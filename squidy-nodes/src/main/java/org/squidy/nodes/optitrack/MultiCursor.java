package org.squidy.nodes.optitrack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.squidy.designer.util.ImageUtils;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.optitrack.multicursor.MCursor;
import org.squidy.nodes.optitrack.utils.TrackingConstant;



/*<code>MultiCursor</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AMd
* </pre>
* 
* @author Simon Faeh, <a href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de<a/>, University of Konstanz
* 
* @version 17.11.2010 / sf
*/
@XmlType(name = "MultiCursor")
@Processor(
	name = "MultiCursor",
	icon = "/org/squidy/nodes/image/48x48/multicursor.png",
	description = "Displays multiple cursors for 2D-Data",
	types = {Processor.Type.OUTPUT, Processor.Type.INPUT },
	tags = { "cursor", "multi", "Dataposition2D"},
	status = Status.UNSTABLE
)

public class MultiCursor extends AbstractNode {

	// ################################################################################
	// Begin OF ADJUSTABLES
	// ################################################################################	

	// ################################################################################

	@XmlAttribute(name = "manual-width")
	@Property(name = "Manual display width", group = "Display Settings", description = "The manual display width of the allowed mouse positioning in pixels. Only used if positioning is set manually.")
	@TextField
	private double manualWidth = 1024;

	/**
	 * @return the manualWidth
	 */
	public final double getManualWidth() {
		return manualWidth;
	}

	/**
	 * @param manualWidth
	 *            the manualWidth to set
	 */
	public final void setManualWidth(double manualWidth) {
		this.manualWidth = manualWidth;
	}

	// ################################################################################

	@XmlAttribute(name = "manual-height")
	@Property(name = "Manual display height", group = "Display Settings", description = "The manual display height of the allowed mouse positioning in pixels. Only used if positioning is set manually.")
	@TextField
	private double manualHeight = 768;

	/**
	 * @return the manualHeight
	 */
	public final double getManualHeight() {
		return manualHeight;
	}

	/**
	 * @param manualHeight
	 *            the manualHeight to set
	 */
	public final void setManualHeight(double manualHeight) {
		this.manualHeight = manualHeight;
	}

	// ################################################################################

	@XmlAttribute(name = "origin-offset-x")
	@Property(name = "Manual display origin offset X", group = "Display Settings", description = "The manual display origin X offset of mouse positions in pixels.")
	@TextField
	private double originOffsetX = 0;

	/**
	 * @return the originOffsetX
	 */
	public final double getOriginOffsetX() {
		return originOffsetX;
	}

	/**
	 * @param originOffsetX
	 *            the originOffsetX to set
	 */
	public final void setOriginOffsetX(double originOffsetX) {
		this.originOffsetX = originOffsetX;
	}

	// ################################################################################

	@XmlAttribute(name = "origin-offset-y")
	@Property(name = "Manual display origin offset Y", group = "Display Settings", description = "The manual display origin Y offset of mouse positions in pixels.")
	@TextField
	private double originOffsetY = 0;

	/**
	 * @return the originOffsetY
	 */
	public final double getOriginOffsetY() {
		return originOffsetY;
	}

	/**
	 * @param originOffsetY
	 *            the originOffsetY to set
	 */
	public final void setOriginOffsetY(double originOffsetY) {
		this.originOffsetY = originOffsetY;
	}

	// ################################################################################
	
	// ################################################################################

	@XmlAttribute(name = "timeout")
	@Property(name = "Cursor Timeout", group = "Display Settings", description = "Hides Cursor if no Data is received (ms)")
	@TextField
	private double cursorTimeout = 500;

	/**
	 * @return the timeout
	 */
	public final double getCursorTimeout() {
		return cursorTimeout;
	}

	/**
	 * @param originOffsetY
	 *            the originOffsetY to set
	 */
	public final void setCursorTimeout(double cursorTimeout) {
		this.cursorTimeout = cursorTimeout;
	}
	// ################################################################################
	
//	@XmlAttribute(name = "hidePosition")
//	@Property(name = "Cursor hide location", group = "Display Settings", description = "Hides Cursor if placed on this location")
//	@TextField
//	private String hideLocation = "0,0";
//
//	/**
//	 * @return the timeout
//	 */
//	public final String getHideLocation() {
//		return hideLocation;
//	}
//
//	/**
//	 * @param originOffsetY
//	 *            the originOffsetY to set
//	 */
//	public final void setHideLocation(String hideLocation) {
//		this.hideLocation = hideLocation;
//	}
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	
	
	private ArrayList<MCursor> cursorThreads;
	private long lastUpdate;
	private Point cursorHideLocation;
	
	@Override
	public void onStart() {
		cursorThreads = new ArrayList<MCursor>();
//    	cursorHideLocation = new Point(0,0);
//		if (hideLocation.length() > 0)
//		{
//			String[] chunks = this.hideLocation.split(",");
//			cursorHideLocation.x = Integer.parseInt(chunks[0]);
//			cursorHideLocation.y = Integer.parseInt(chunks[1]);
//		}		
	} 
	
	@Override
	public void onStop()
	{
		for (MCursor cThread : cursorThreads)
		{
			cThread.isReadyToDestroy(true);
		}
	}
	
	
	
	private boolean changed = false;
	private String imagePath = "";
	private int handSide = 0;
	private int gestureID = 0;
	private int currentHand = 0;
	private int currentdGesture = 0;
	public IData process(DataPosition2D d2d) {
		
		//lastTimeOfDataPosition2D = System.currentTimeMillis();
		int sessionID;
		if (d2d.hasAttribute(TrackingConstant.RIGIDBODYID))
		{
			sessionID = Integer.valueOf(d2d.getAttribute(TrackingConstant.RIGIDBODYID).toString());
		}else
		{
			sessionID = Integer.MAX_VALUE - 10;
		}	
		if (d2d.hasAttribute(TrackingConstant.GESTUREID))
		{
			handSide = Integer.valueOf(d2d.getAttribute(TrackingConstant.HANDSIDE).toString());
			gestureID = Integer.valueOf(d2d.getAttribute(TrackingConstant.GESTUREID).toString());
		}
		else
		{
			gestureID = 0;
			handSide = sessionID;
		}
			//int sessionID = 1;//
			boolean cursorFound = false;
			boolean noNewCursor = false;
			if (cursorThreads.size() > 0)
				for (MCursor cThread : cursorThreads)
				{
					if (cThread.getCursorID() == sessionID)
					{
						imagePath = "";
						if (d2d.getX() == 0 && d2d.getY() == 0)
						{
//							gestureID = -1;
//							imagePath = "/org/squidy/nodes/image/32x32/hand_null.png";
						}
						else
						{
//							if (d2d.hasAttribute(TrackingConstant.GESTUREID) && this.gestureID != cThread.getGestureID())
//							{
//								switch (Integer.valueOf(d2d.getAttribute(TrackingConstant.GESTUREID).toString()))
//								{
//									case 1 : 
//									{
//										if (Integer.valueOf(d2d.getAttribute(TrackingConstant.HANDSIDE).toString()) == 1)
//											imagePath = "/org/squidy/nodes/image/32x32/hand_click_right.png";
//										else
//											imagePath = "/org/squidy/nodes/image/32x32/hand_click_left.png";
//										break;
//									}
//									case 2 : 
//									{
//										if (Integer.valueOf(d2d.getAttribute(TrackingConstant.HANDSIDE).toString()) == 1)
//											imagePath ="/org/squidy/nodes/image/32x32/hand_grab_right.png";
//										else
//											imagePath ="/org/squidy/nodes/image/32x32/hand_grab_left.png";
//										break;
//									}
//									case 3 : 
//									{
//										if (Integer.valueOf(d2d.getAttribute(TrackingConstant.HANDSIDE).toString()) == 1)
//											imagePath ="/org/squidy/nodes/image/32x32/hand_click_right.png";
//										else
//											imagePath ="/org/squidy/nodes/image/32x32/hand_click_left.png";
//										break;
//									}
//									default : 
//									{
//										if (Integer.valueOf(d2d.getAttribute(TrackingConstant.HANDSIDE).toString()) == 1)
//											imagePath = "/org/squidy/nodes/image/32x32/hand_point_right.png";
//										else
//											imagePath = "/org/squidy/nodes/image/32x32/hand_point_left.png";
//										break;
//									}				
//								}
//							}							
						}
						if (imagePath != "")
						{
							changed = true;
							cThread.forceDestruction();
							cursorThreads.remove(cThread);
							MCursor mCursor = new MCursor(imagePath,32,getScreenCoordinates(d2d), sessionID, handSide, gestureID);
							//CursorThread cThread = new CursorThread(new CursorRunnable("/org/squidy/nodes/image/32x32/hand_point4.png",32,getScreenCoordinates(d2d), sessionID));
							cursorThreads.add(mCursor);
							lastUpdate = System.currentTimeMillis();	
							cursorFound = true;
							System.out.println("cursor added ");				
						}
						else
						{
							if (this.gestureID != cThread.getGestureID())
							{
//								changed = true;
//								cThread.forceDestruction();
//								cursorThreads.remove(cThread);
//								MCursor mCursor;
//								if (handSide == 2)
//								{
//									mCursor = new MCursor("/org/squidy/nodes/image/32x32/hand_point_right.png",32,getScreenCoordinates(d2d), sessionID, handSide, gestureID);
//								}
//								else
//								{
//									mCursor = new MCursor("/org/squidy/nodes/image/32x32/hand_point_left.png",32,getScreenCoordinates(d2d), sessionID, handSide, gestureID);
//								}
//								//CursorThread cThread = new CursorThread(new CursorRunnable("/org/squidy/nodes/image/32x32/hand_point4.png",32,getScreenCoordinates(d2d), sessionID));
//								cursorThreads.add(mCursor);
//								System.out.println("cursor added ");
//								lastUpdate = System.currentTimeMillis();	
//								cursorFound = true;
							}
							cThread.updateLocation(getScreenCoordinates(d2d));
							cursorFound = true;
						}
						break;
					}
						
					}
//					if (!cThread.isReadyToDestroy())
//					{
//						//if (d2d.getX() < 100)
//						//if (lastUpdate + 5000 < System.currentTimeMillis())
//						if (d2d.getX() == 0 && d2d.getY() == 0)
//						{
//							//cThread.interrupt();
//							//noNewCursor = true;
//							cThread.isReadyToDestroy(true);
//							cursorThreads.remove(cThread);
//							if (cursorThreads.size() > 0)
//								System.out.println("FUUUUUUUUUUUUUCK");
//						}else
//						{
//							cThread.updateLocation(getScreenCoordinates(d2d));
//							cursorFound = true;
//						}
//						
//						break;
//					}
//				}	
			if (!cursorFound)
			{
				System.out.println("new cursor");
				MCursor mCursor;
				if (handSide == 2)
				{
					mCursor = new MCursor("/org/squidy/nodes/image/32x32/hand_point_right.png",32,getScreenCoordinates(d2d), sessionID, handSide, gestureID);
				}
				else
				{
					mCursor = new MCursor("/org/squidy/nodes/image/32x32/hand_point_left.png",32,getScreenCoordinates(d2d), sessionID, handSide, gestureID);
				}
				
				//CursorThread cThread = new CursorThread(new CursorRunnable("/org/squidy/nodes/image/32x32/hand_point4.png",32,getScreenCoordinates(d2d), sessionID));
				cursorThreads.add(mCursor);
				lastUpdate = System.currentTimeMillis();
			}
		
		return d2d;
	}
	
	private Point getScreenCoordinates(DataPosition2D d2d)
	{
		return new Point((int)(d2d.getX() * manualWidth + originOffsetX),
		              (int)(d2d.getY() * manualHeight + originOffsetY));
	}
}

