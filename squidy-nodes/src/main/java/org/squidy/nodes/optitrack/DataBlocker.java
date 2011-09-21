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

import org.hibernate.mapping.Array;
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
@XmlType(name = "Datablocker")
@Processor(
	name = "Datablocker",
	icon = "/org/squidy/nodes/image/48x48/datablocker.png",
	description = "",
	types = { Processor.Type.OUTPUT },
	tags = { "datanlocker", "block", "debugging" }
)

public class DataBlocker extends AbstractNode {
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "keyID")
	@Property(
		name = "Key",
		description = "Key to be pressed"
	)
	@TextField
	private String keyStroke = "-";
	
	public final String getKeyStroke() {
		return keyStroke;
	}

	public final void setKeyStroke(String key) {
		this.keyStroke = key;
	}
	
	// ################################################################################

	@XmlAttribute(name = "identKeyWord")
	@Property(
		name = "Permitted keywords Keyword",
		description = "Datatypes with this identifier will be blocked (multiple keywords seperaty with ;)"
	)
	@ComboBox(domainProvider = KWDomainProvider.class)
	private String keyWord = "";
	
	public final String getKeyWord() {
		return keyWord;
	}

	public final void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
		
	// ################################################################################

	@XmlAttribute(name = "fps")
	@Property(
		name = "Allowed frame count",
		description = "Number of frames to be blocked"
	)
	@TextField
	private int fps = 0;
	
	public final int getFps() {
		return fps;
	}

	public final void setFps(int fps) {
		
		this.fps = fps;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "multiScreenMode")
	@Property(name = "Multi-Screen Mode", description = "If enabled, only sends (0,0) if all Pipes are (0,0)")
	@CheckBox
	private boolean multiScreenMode = false;

	public boolean getMultiScreenMode() {
		return multiScreenMode;
	}

	public void setMultiScreenMode(boolean multiScreenMode) {
		this.multiScreenMode = multiScreenMode;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "block2D")
	@Property(name = "Block 2D Data", description = "Block 2D Data")
	@CheckBox
	private boolean blockData2D = false;

	public boolean getBlockData2D() {
		return blockData2D;
	}

	public void setBlockData2D(boolean blockData2D) {
		this.blockData2D = blockData2D;
	}
	// ################################################################################
	
	@XmlAttribute(name = "block3D")
	@Property(name = "Block 3D Data", description = "Block 3D Data")
	@CheckBox
	private boolean blockData3D = false;

	public boolean getBlockData3D() {
		return blockData3D;
	}

	public void setBlockData3D(boolean blockData3D) {
		this.blockData3D = blockData3D;
	}	
	// ################################################################################
	
	@XmlAttribute(name = "block6D")
	@Property(name = "Block 6D Data", description = "Block 6D Data")
	@CheckBox
	private boolean blockData6D = false;

	public boolean getBlockData6D() {
		return blockData6D;
	}

	public void setBlockData6D(boolean blockData6D) {
		this.blockData6D = blockData6D;
	}	
	// ################################################################################
	
	@XmlAttribute(name = "blockDigital")
	@Property(name = "Block DataDigital", description = "Block DigitalData")
	@CheckBox
	private boolean blockDataDigital = false;

	public boolean getBlockDataDigital() {
		return blockDataDigital;
	}

	public void setBlockDataDigital(boolean blockDataDigital) {
		this.blockDataDigital = blockDataDigital;
	}	
	
	// ################################################################################
	
	
	@XmlAttribute(name = "rigidBodyID")
	@Property(
		name = "Blocked RigidBodies",
		description = "ID of RigidBodies to block"
	)
	@ComboBox(domainProvider = RBIDDomainProvider.class)
	private int rigidBodyID = TrackingConstant.RB_HANDRIGHT;
	
	public final int getRigidBodyID() {
		return rigidBodyID;
	}

	public final void setRigidBodyID(int rID) {
		this.rigidBodyID = rID;
	}
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	
	
	// ################################################################################
	// BEGIN OF DOMAIN PROVIDERS
	// ################################################################################
	
	public static class RBIDDomainProvider implements DomainProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[8];
			values[0] = new ComboBoxItemWrapper(0, "Unspecified");
			values[1] = new ComboBoxItemWrapper(TrackingConstant.RB_HANDRIGHT, "Glove (right)");
			values[2] = new ComboBoxItemWrapper(TrackingConstant.RB_HANDLEFT, "Glove (left)");
			values[3] = new ComboBoxItemWrapper(TrackingConstant.RB_PEN, "Pen");
			values[4] = new ComboBoxItemWrapper(TrackingConstant.RB_LASER, "Laser-Pointer");
			values[5] = new ComboBoxItemWrapper(TrackingConstant.RB_IPHONE, "iPhone1");
			values[6] = new ComboBoxItemWrapper(TrackingConstant.RB_IPHONE2, "iPhone2");
			values[7] = new ComboBoxItemWrapper(TrackingConstant.RB_MOBILEDISPLAY, "Mobile-Display");
			return values;
		}
	}
	
	// ################################################################################
	
	
	public static class KWDomainProvider implements DomainProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[5];
			values[0] = new ComboBoxItemWrapper(TrackingConstant.KEY_NOKEY, "Unspecified");
			values[1] = new ComboBoxItemWrapper(TrackingConstant.KEY_ONSCREEN, "Screen Coordinates");
			values[2] = new ComboBoxItemWrapper(TrackingConstant.KEY_OFFSCREEN, "Offscreen Coordinates");
			values[3] = new ComboBoxItemWrapper(TrackingConstant.KEY_OVERSIZESCREEN, "Oversize Coordinates");
			values[4] = new ComboBoxItemWrapper(TrackingConstant.KEY_ENTERSCREEN, "Enter Screen Coordinates");

			return values;
		}
	}	
	
	//################################################################################
	// END OF DOMAIN PROVIDERS
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
		//d2dHistory = new AbstractQueue(); 
	}
	
	@Override 
	public IDataContainer preProcess(IDataContainer dataContainer)
	{
		List<DataPosition2D> l2d = DataUtility.getDataOfType(DataPosition2D.class, dataContainer);
		if (l2d.size() > 0)
		{
			if (this.keyWord.length() > 0)
			{
				if (l2d.get(0).hasAttribute((TrackingConstant.KEYWORD)))
				{
					String ident = String.valueOf(l2d.get(0).getAttribute(TrackingConstant.KEYWORD));
					if (!ident.contains(keyWord))
					{
						return null;
					}
				}else
				{
					return null;
				}

			}
			if (getBlockData2D() && getFps() > 0)
			{
				if (last2d == 0 || (last2d + (1.0/ (double)fps * 1000.0) < System.currentTimeMillis()))
				{
					last2d = System.currentTimeMillis();
					return dataContainer;
				}else
				{
					
					return null;
				}
			}			
		}
		if (getBlockData3D() && fps > 0)
		{
			List<DataPosition3D> l3d = DataUtility.getDataOfType(DataPosition3D.class, dataContainer);
			if (l3d.size() > 0)
			{
				if (last3d == 0 || (last3d + (1.0/ (double)fps * 1000.0) < System.currentTimeMillis()))
				{
					last3d = System.currentTimeMillis();
					return dataContainer;
				}else
				{
					return null;
				}				
			}
		}
		if (getBlockData6D() && fps > 0)
		{
			List<DataPosition6D> l6d = DataUtility.getDataOfType(DataPosition6D.class, dataContainer);
			if (l6d.size() > 0)
			{
				if (last6d == 0 || (last6d + (1.0/ (double)fps * 1000.0) < System.currentTimeMillis()))
				{
					last6d = System.currentTimeMillis();
					return dataContainer;
				}else
				{
					return null;
				}				
			}
		}		
		return dataContainer;
	}
	
	public IData process(DataPosition2D d2d)
	{
		if (multiScreenMode)
		{
			if (d2d.getX()== 0 && d2d.getY() == 0)
			{
				if (prevD2D.getX() > 0  && prevD2D.getY() > 0)
				{
					d2d = prevD2D.getClone();
					prevD2D.setX(0);
					prevD2D.setY(0);
				}
			}
		}
//		d2dHistory.add(new Point2d(d2d.getX(),d2d.getY()))
		if (d2d.hasAttribute(TrackingConstant.RIGIDBODYID) && this.rigidBodyID > 0)
		{
			if (Integer.valueOf(d2d.getAttribute(TrackingConstant.RIGIDBODYID).toString()) != this.rigidBodyID)
				return null;
		}
		if (keyPressed && getBlockData2D())
		{
			return null;
		}else
		{
			return d2d;
		}
	}
	public IData process(DataPosition3D d3d)
	{
		if (d3d.hasAttribute(TrackingConstant.RIGIDBODYID))
		{
			if (Integer.valueOf(d3d.getAttribute(TrackingConstant.RIGIDBODYID).toString()) == this.rigidBodyID)
				return null;
		}
		if (keyPressed && getBlockData3D())
		{
			return null;
		}else
		{
			return d3d;
		}
	}
	
	public IData process(DataPosition6D d6d)
	{
		if (d6d.hasAttribute(TrackingConstant.RIGIDBODYID))
		{
			if (Integer.valueOf(d6d.getAttribute(TrackingConstant.RIGIDBODYID).toString()) == this.rigidBodyID)
				return null;
		}
		if (keyPressed && getBlockData6D())
		{
			return null;
		}else
		{
			return d6d;
		}
	}
	
    public DataDigital process(DataDigital dataDigital) 
    {
    	if (dataDigital.hasAttribute(Keyboard.KEY_EVENT))
    	{
        	Integer key_event = (Integer) dataDigital.getAttribute(Keyboard.KEY_EVENT);
        	System.out.println(key_event);
        	System.out.println(KeyStroke.getKeyStroke(this.keyStroke) + " == " + KeyStroke.getKeyStroke(key_event.intValue(),0));
        	if (KeyStroke.getKeyStroke(this.keyStroke) == KeyStroke.getKeyStroke(key_event.intValue(),0))
    		{
        		if (dataDigital.getFlag())
        			keyPressed = !keyPressed;
    		}
    	}
    	if (keyPressed && getBlockDataDigital())
    		return null;
    	else
    		return dataDigital;
    }
}
