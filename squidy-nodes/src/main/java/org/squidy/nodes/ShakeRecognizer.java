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


package org.squidy.nodes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.Slider;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.manager.data.impl.DataString;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>ShakeRecognizer</code>.
 * 
 * <pre>
 * Date: Aug 20, 2009
 * Time: 6:23:29 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: ShakeRecognizer.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "ShakeRecognizer")
@Processor(
	name = "ShakeRecognizer",
	icon = "/org/squidy/nodes/image/48x48/funnel.png",
	types = { Processor.Type.FILTER },
	description = "/org/squidy/nodes/html/ShakeRecognizer.html",
	tags = { "shake", "acceleration", "accelerometer", "iphone", "laserpointer", "inertia", "digital", "motion" }
)
public class ShakeRecognizer extends AbstractNode {
	
	public static final DataConstant SHAKE_EVENT = DataConstant.get(String.class, "SHAKE_EVENT");

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################

//	@XmlAttribute(name = "my-property")
//	@Property(
//		name = "My Property",
//		description = "Description of my property."
//	)
//	@TextField
//	private String myProperty = "default value";
//
//	/**
//	 * @return the myProperty
//	 */
//	public final String getMyProperty() {
//		return myProperty;
//	}
//
//	/**
//	 * @param myProperty the myProperty to set
//	 */
//	public final void setMyProperty(String myProperty) {
//		this.myProperty = myProperty;
//	}
	
	
	// ################################################################################
	
	@XmlAttribute(name = "shake-threshold")
	@Property(
		name = "Shake Threshold",
		description = "Intensity threshold for the recognizing a shake event."
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 20,
		showLabels = true,
		showTicks = true,
		majorTicks = 5,
		minorTicks = 2,
		snapToTicks = false
	)
	private int shakeThreshold = 7;

	/**
	 * @return the myProperty
	 */
	public final int getShakeThreshold() {
		return shakeThreshold;
	}

	/**
	 * @param myProperty the myProperty to set
	 */
	public final void setShakeThreshold(int shakeThreshold) {
		this.shakeThreshold = shakeThreshold;
	}
	
// ################################################################################
	
	@XmlAttribute(name = "shake-interval")
	@Property(
		name = "Shake Interval",
		description = "Time interval in millisconds between subsequent shake events."
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 2000,
		showLabels = true,
		showTicks = true,
		majorTicks = 500,
		minorTicks = 100,
		snapToTicks = false
	)
	private int shakeInterval = 500;

	/**
	 * @return the myProperty
	 */
	public final int getShakeInterval() {
		return shakeInterval;
	}

	/**
	 * @param myProperty the myProperty to set
	 */
	public final void setShakeInterval(int shakeInterval) {
		this.shakeInterval = shakeInterval;
	}
	
	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################
	
	private long lastTime = System.currentTimeMillis();
	
	// ################################################################################
	// BEGIN OF LAUNCH
	// ################################################################################
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
//	@Override
//	public void onStart() throws ProcessException {
//		super.onStart();
//	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
//	@Override
//	public void onStop() throws ProcessException {
//		super.onStop();
//	}
	
	// ################################################################################
	// END OF LAUNCH
	// ################################################################################
	
	// ################################################################################
	// BEGIN OF PROCESS
	// ################################################################################
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#beforeDataContainerProcessing(org.squidy.manager.data.IDataContainer)
	 */
//	@Override
//	public IDataContainer beforeDataContainerProcessing(IDataContainer dataContainer) {
//		return super.beforeDataContainerProcessing(dataContainer);
//	}
	
	/**
	 * Uncomment method if processing of data analog is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
//	public IData process(DataAnalog dataAnalog) {
//		return dataAnalog;
//	}
	
	/**
	 * Uncomment method if processing of data intertial is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
	public IData process(DataInertial dataInertial) {
	
	    if((dataInertial.getAbsoluteValue() >= shakeThreshold/10f) && (System.currentTimeMillis() > lastTime + shakeInterval)) {
	        DataString event = new DataString(ShakeRecognizer.class, "Shake");
	        event.setAttribute(SHAKE_EVENT, "VERY IMPRESSIVE SHAKE");
	        lastTime = System.currentTimeMillis();
	        publish(event);
//   			publish(dataInertial);
	    } 
	    
//	    System.out.println(dataInertial.getX()+" "+dataInertial.getY()+" "+dataInertial.getZ());
		
		return null;
	}

	/**
	 * Uncomment method if processing of data position 2d is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
//	public IData process(DataPosition2D dataPosition2D) {
//		return dataPosition2D; 
//	}
	
	/**
	 * Uncomment method if processing of data position 3d is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
//	public IData process(DataPosition3D dataPosition3D) {
//		return dataPosition3D;
//	}
	
	/**
	 * Uncomment method if processing of data position 6d is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
//	public IData process(DataPosition6D dataPosition6D) {
//		return dataPosition6D;
//	}
	
	/**
	 * Uncomment method if processing of data finger is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
//	public IData process(DataFinger dataFinger) {
//		return dataFinger;
//	}
	
	/**
	 * Uncomment method if processing of data hand is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
//	public IData process(DataHand dataHand) {
//		return dataHand;
//	}
	
	/**
	 * Uncomment method if processing of data digital is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
//	public IData process(DataDigital dataDigital) {
//		return dataDigital;
//	}
	
	/**
	 * Uncomment method if processing of data tokens is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
//	public IData process(DataButton dataButton) {
//		return dataButton;
//	}
	
	/**
	 * Uncomment method if processing of data string is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
//	public IData process(DataString dataString) {
//		return dataString;
//	}
	
	/**
	 * Uncomment method if processing of data tokens is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
//	public IData process(DataToken dataToken) {
//		return dataToken;
//	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#afterDataContainerProcessing(org.squidy.manager.data.IDataContainer)
	 */
//	@Override
//	public IDataContainer afterDataContainerProcessing(IDataContainer dataContainer) {
//		return super.afterDataContainerProcessing(dataContainer);
//	}
	
	// ################################################################################
	// END OF PROCESS
	// ################################################################################
}
