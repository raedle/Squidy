package org.squidy.nodes.optitrack;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.text.JTextComponent.KeyBinding;
import javax.vecmath.Point2d;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;
import org.squidy.nodes.Keyboard;
import org.squidy.nodes.optitrack.RigidBody.RBIDDomainProvider;
import org.squidy.nodes.optitrack.utils.TrackingConstant;
import org.squidy.nodes.optitrack.utils.TrackingUtility;


/*<code>DataBlocker</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AM
* </pre>
* 
* @author Simo Faeh, < href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de</>, University f Konstanz
* 
* @version $Id: OptitrackPrint.java 373 2010-08-08 12:06:21Z raedle $
*/
@XmlType(name = "Mouse2Hand")
@Processor(
	name = "Mouse2Hand",
	description = "",
	types = { Processor.Type.OUTPUT },
	tags = { "Mouse2Hand", "mouse", "simulator" }
)

public class Mouse2Hand extends AbstractNode {
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################


	
	private boolean keyPressed = false;
	private int frameCounter2D, frameCounter3D, frameCounter6D = 0;
	private int d2dCounter = 0;
	private DataPosition2D prevD2D;
	private long last2d = 0;
	private long last3d = 0;
	private long last6d = 0;
	private Timer publishTimer;
	private AbstractQueue<Point2d> d2dHistory;
	
	@Override
	public void onStart()
	{
		handSide = 2;
		frameID = 0;
		click = false;
		//d2dHistory = new AbstractQueue(); 
	}
	
	
	
	public DataPosition2D process(DataPosition2D d2d)
	{
		if (frameID == Integer.MAX_VALUE)
		{
			frameID = 0;
		}
		frameID++;
		d2d.setAttribute(TrackingConstant.REMOTEHOST, "127.0.0.1");
		d2d.setAttribute(TrackingConstant.REMOTEPORT, 15202);
		if (d2d.getY() > 0.9)
		{
			d2d.setAttribute(TrackingConstant.KEYWORD, "SCREENOVERSIZE");
		}else
		{
			d2d.setAttribute(TrackingConstant.KEYWORD, "ONSCREEN");
		}
		d2d.setAttribute(TrackingConstant.SCREENOVERSIZE, 0.4);
		d2d.setAttribute(DataConstant.GROUP_ID, frameID);
		if (click)
		{
			d2d.setAttribute(TrackingConstant.GESTUREID, 5501);
			d2d.setAttribute(TrackingConstant.SENDTUIO, "TRUE");
		}
		else
			d2d.setAttribute(TrackingConstant.GESTUREID, 5500);
		d2d.setAttribute(TrackingConstant.RIGIDBODYID, handSide);
		d2d.setAttribute(TrackingConstant.HANDSIDE, handSide);
		d2d.setX(d2d.getX() + 0.1);
		return d2d;
	}
	private int frameID;
	private int handSide = 0;
	private boolean click = false;
	public void process(DataButton dataButton)
	{
		click = dataButton.getFlag();
	}
	
    public DataDigital process(DataDigital dataDigital) 
    {
    	if (dataDigital.hasAttribute(Keyboard.KEY_EVENT))
    	{
        	Integer key_event = (Integer) dataDigital.getAttribute(Keyboard.KEY_EVENT);
        	if (KeyStroke.getKeyStroke("L") == KeyStroke.getKeyStroke(key_event.intValue(),0))
    		{
        		if (dataDigital.getFlag())
        			handSide = TrackingConstant.RB_HANDLEFT;
    		} else if (KeyStroke.getKeyStroke("R") == KeyStroke.getKeyStroke(key_event.intValue(),0))
    		{
        		if (dataDigital.getFlag())
        			handSide = TrackingConstant.RB_HANDRIGHT;    			
    		}
    		
    	}
  		return dataDigital;
    }
}
