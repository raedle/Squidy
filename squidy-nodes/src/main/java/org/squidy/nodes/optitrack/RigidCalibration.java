package org.squidy.nodes.optitrack;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Track;
import javax.swing.KeyStroke;
import javax.vecmath.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.designer.model.Data;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.Keyboard;
import org.squidy.nodes.optitrack.utils.TrackingUtility;

import Jama.Matrix;


/*<code>RigidCalibration</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AMd
* </pre>
* 
* @author Simon Faeh, <a href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de<a/>, University of Konstanz
* 
* @version 27.10.2010 / sf
*/
@XmlType(name = "RigidCalibration")
@Processor(
	name = "RigidBody Calibration",
	icon = "/org/squidy/nodes/image/48x48/calibration6D.png",
	description = "Specify rotation correction",
	types = {Processor.Type.OUTPUT, Processor.Type.INPUT },
	tags = { "rotation", "object", "optitrack", "handtracking", "direction adjuster" },
	status = Status.UNSTABLE
)

public class RigidCalibration extends AbstractNode {
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################


	@XmlAttribute(name = "keyID")
	@Property(
		name = "Key",
		description = "Pressing the key set the rigidbody to the specified position"
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
	
	@XmlAttribute(name = "Yaw")
	@Property(name = "Yaw", description = "Angle to adjust in degrees [-180,180]")
	@TextField
	private Double xDirAngle = 0.0;

	public final Double getXDirAngle() {
		return xDirAngle;
	}

	public final void setXDirAngle(Double dirAngle) {
		this.xDirAngle = dirAngle;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "pitch")
	@Property(name = "Pitch", description = "Angle to adjust in degrees [-180,180]")
	@TextField
	private Double yDirAngle = 0.0;

	public final Double getYDirAngle() {
		return yDirAngle;
	}

	public final void setYDirAngle(Double dirAngle) {
		this.yDirAngle = dirAngle;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "roll")
	@Property(name = "Roll", description = "Angle to adjust in degrees [-180,180]")
	@TextField
	private Double zDirAngle = 0.0;

	public final Double getZDirAngle() {
		return zDirAngle;
	}

	public final void setZDirAngle(Double dirAngle) {
		this.zDirAngle = dirAngle;
	}	

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	
	
    
	private MathUtility mu = new MathUtility();
	private double[][] m6d = new double[3][3];
	private double[][] m6drot = new double[3][3];
	private DataPosition6D current6D;
	private double yaw, pitch, roll = 0.0;
	
   @Override
	public IDataContainer preProcess(IDataContainer dataContainer) 
    {
    	DataPosition6D d6d;
	    List<DataPosition6D> rigidBodies = DataUtility.getDataOfType(DataPosition6D.class, dataContainer);
    	if (rigidBodies.size() > 0)
    	{
    		d6d = rigidBodies.get(0);
    		current6D = d6d;
			if (roll != 0.0 || yaw != 0.0 || pitch != 0.0)
			{
				m6d[0][0] = d6d.getM00();
				m6d[0][1] = d6d.getM01();
				m6d[0][2] = d6d.getM02();
				
				m6d[1][0] = d6d.getM10();
				m6d[1][1] = d6d.getM11();
				m6d[1][2] = d6d.getM12();
				
				m6d[2][0] = d6d.getM20();
				m6d[2][1] = d6d.getM21();
				m6d[2][2] = d6d.getM22();
	
				m6d = mu.rotateMatrix(m6d, -Math.toRadians((roll-zDirAngle)),  -Math.toRadians((yaw - xDirAngle)), -Math.toRadians((pitch - yDirAngle)));
				
				d6d.setM00(m6d[0][0]);
				d6d.setM01(m6d[0][1]);
				d6d.setM02(m6d[0][2]);
				
				d6d.setM10(m6d[1][0]);
				d6d.setM11(m6d[1][1]);
				d6d.setM12(m6d[1][2]);
				
				d6d.setM20(m6d[2][0]);
				d6d.setM21(m6d[2][1]);
				d6d.setM22(m6d[2][2]);
				d6d.setYaw(0);
				d6d.setPitch(0);
				d6d.setRoll(0);
				
				m6d = mu.rotateMatrix(null, -Math.toRadians((roll-zDirAngle)),  -Math.toRadians((yaw - xDirAngle)), -Math.toRadians((pitch - yDirAngle)));
	    		d6d = TrackingUtility.Norm2RoomCoordinates(Optitrack.class, d6d);
	    		List<DataPosition3D> additionalMarker = DataUtility.getDataOfType(DataPosition3D.class, dataContainer);
	    		additionalMarker.remove(0);
	    		DataPosition3D additionalMarker3D;
	    		if(additionalMarker.size() > 0)
	    		{
	    			for(int i = 0; i < additionalMarker.size(); i++)
	    			{
	    				additionalMarker3D = TrackingUtility.Norm2RoomCoordinates(Optitrack.class, additionalMarker.get(i));
	    				additionalMarker3D = mu.rotatePoint(additionalMarker3D, d6d, m6drot, true, false);
	    				additionalMarker3D = TrackingUtility.Room2NormCoordinates(Optitrack.class, additionalMarker3D);				
	    			}
	    		}  
	    		d6d = TrackingUtility.Room2NormCoordinates(Optitrack.class, d6d);			
				
			}
    	}
		return dataContainer;	
    }
  
   private boolean keyPressed = false;
   public void process(DataDigital dataDigital) 
   {
	   	if (dataDigital.hasAttribute(Keyboard.KEY_EVENT))
	   	{
	       	Integer key_event = (Integer) dataDigital.getAttribute(Keyboard.KEY_EVENT);
	       	if (KeyStroke.getKeyStroke(this.keyStroke) == KeyStroke.getKeyStroke(key_event.intValue(),0))
	   		{
	       		if (dataDigital.getFlag())
	       		{
					if (keyPressed)
					{
		       			yaw = 0.0;
						pitch = 0.0;
						roll = 0.0;
						
					}else
					{
		       			yaw = current6D.getYaw();
						pitch = current6D.getPitch();
						roll = current6D.getRoll();
					}
					keyPressed = !keyPressed;
	       		}
	   		}
	   	}
   }
	public IData process(DataButton dataButton) {

		if (dataButton.getButtonType() == DataButton.BUTTON_3) {
			if (dataButton.getFlag()) {
				yaw = current6D.getYaw();
				pitch = current6D.getPitch();
				roll = current6D.getRoll();
			}
		}
		return null;
	}
}

