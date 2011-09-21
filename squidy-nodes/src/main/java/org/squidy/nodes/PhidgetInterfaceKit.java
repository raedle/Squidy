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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataAnalog;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.model.Processable;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.SensorChangeEvent;
import com.phidgets.event.SensorChangeListener;


/**
 * <code>PhidgetInterfaceKit</code>.
 * 
 * <pre>
 * Date: Nov 7, 2008
 * Time: 11:39:17 PM
 * </pre>
 * 
 * @author Roman RŠdle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @author Stefan Dierdorf, <a href="mailto:stefan.dierdorf@uni-konstanz.de">stefan
 *         .dierdorf@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: PhidgetInterfaceKit.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
@XmlType(name = "Phidget InterfaceKit")
@Processor(
	name = "Phidget InterfaceKit",
	types = { Processor.Type.INPUT, Processor.Type.OUTPUT },
	description = "/org/squidy/nodes/html/PhidgetInterfaceKit.html",
	tags = { "phidget", "interface", "interfacekit" },
	status = Status.UNSTABLE
)
public class PhidgetInterfaceKit extends AbstractNode {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(PhidgetInterfaceKit.class);
	
	// Data constants used to identify data objects coming from an interface kit.
	public static final DataConstant SENSOR_INDEX_INPUT = DataConstant.get(Integer.class, "SENSOR_INDEX");
	public static final DataConstant SENSOR_INDEX_OUTPUT = DataConstant.get(Integer.class, "SENSOR_OUTPUT_INDEX");

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "interface-kit-serial")
	@Property(
		name = "InterfaceKIT serial"	
	)
	@TextField
	private int interfaceKitSerial = 0;

	/**
	 * @return the interfaceKitSerial
	 */
	public final int getInterfaceKitSerial() {
		return interfaceKitSerial;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setInterfaceKitSerial(int interfaceKitSerial) {
		this.interfaceKitSerial = interfaceKitSerial;
	}
	
	// ################################################################################

	@XmlAttribute(name = "show-pipeline-status-via-LEDs")
	@Property(
		name = "show pipeline status via LEDs",
		description = "Displays if pipeline is running (green) or stopped (red)"
	)
	@CheckBox
	private Boolean pipelineStatus = false;

	/**
	 * @return the interfaceKitSerial
	 */
	public final Boolean getPipelineStatus() {
		return pipelineStatus;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setPipelineStatus(Boolean pipelineStatus) {
		this.pipelineStatus = pipelineStatus;

	}
	
	// ################################################################################

	@XmlAttribute(name = "interface-ratiometric-active")
	@Property(
		name = "Interface ratiometric active",
		description = "Activate or Deactivate Ratiometric"
	)
	@CheckBox
	private boolean ratiometric = true;

	/**
	 * @return the interfaceKitRatiometric
	 */
	public final boolean getRatiometric() {
		return ratiometric;
	}

	/**
	 * @param ratiometric
	 *            the ratiometric to set
	 */
	public final void setRatiometric(boolean ratiometric) {
		this.ratiometric = ratiometric;

		if (interfaceKit != null) {
			try {
				interfaceKit.setRatiometric(this.getRatiometric());
			}
			catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// ################################################################################

	@XmlAttribute(name = "interface-digital-output-active-0")
	@Property(
		name = "Interface digital output 0 active",
		description = "Activate or Deactivate digital Output 0"
	)
	@CheckBox
	private Boolean digitalOutput0 = false;

	/**
	 * @return the interfaceKitSerial
	 */
	public final Boolean getDigitalOutput0() {
		return digitalOutput0;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setDigitalOutput0(Boolean digitalOutput) {
		this.digitalOutput0 = digitalOutput;

		if (interfaceKit != null) {
			try {
				interfaceKit.setOutputState(0, digitalOutput);
			}
			catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// ################################################################################

	@XmlAttribute(name = "interface-digital-output-active-1")
	@Property(
		name = "Interface digital output 1 active",
		description = "Activate or Deactivate digital Output 1"
	)
	@CheckBox
	private Boolean digitalOutput1 = false;

	/**
	 * @return the interfaceKitSerial
	 */
	public final Boolean getDigitalOutput1() {
		return digitalOutput1;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setDigitalOutput1(Boolean digitalOutput) {
		this.digitalOutput1 = digitalOutput;

		if (interfaceKit != null) {
			try {
				interfaceKit.setOutputState(1, digitalOutput);
			}
			catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// ################################################################################

	@XmlAttribute(name = "interface-digital-output-active-2")
	@Property(
		name = "Interface digital output 2 active",
		description = "Activate or Deactivate digital Output 2"
	)
	@CheckBox
	private Boolean digitalOutput2 = false;

	/**
	 * @return the interfaceKitSerial
	 */
	public final Boolean getDigitalOutput2() {
		return digitalOutput2;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setDigitalOutput2(Boolean digitalOutput) {
		this.digitalOutput2 = digitalOutput;

		if (interfaceKit != null) {
			try {
				interfaceKit.setOutputState(2, digitalOutput);
			}
			catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// ################################################################################

	@XmlAttribute(name = "interface-digital-output-active-3")
	@Property(
		name = "Interface digital output 3 active",
		description = "Activate or Deactivate digital Output 3"
	)
	@CheckBox
	private Boolean digitalOutput3 = false;

	/**
	 * @return the interfaceKitSerial
	 */
	public final Boolean getDigitalOutput3() {
		return digitalOutput3;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setDigitalOutput3(Boolean digitalOutput) {
		this.digitalOutput3 = digitalOutput;

		if (interfaceKit != null) {
			try {
				interfaceKit.setOutputState(3, digitalOutput);
			}
			catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// ################################################################################

	@XmlAttribute(name = "interface-digital-output-active-4")
	@Property(
		name = "Interface digital output 4 active",
		description = "Activate or Deactivate digital Output 4"
	)
	@CheckBox
	private Boolean digitalOutput4 = false;

	/**
	 * @return the interfaceKitSerial
	 */
	public final Boolean getDigitalOutput4() {
		return digitalOutput4;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setDigitalOutput4(Boolean digitalOutput) {
		this.digitalOutput4 = digitalOutput;

		if (interfaceKit != null) {
			try {
				interfaceKit.setOutputState(4, digitalOutput);
			}
			catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// ################################################################################
	
	@XmlAttribute(name = "interface-digital-output-active-5")
	@Property(
		name = "Interface digital output 5 active",
		description = "Activate or Deactivate digital Output 5"
	)
	@CheckBox
	private Boolean digitalOutput5 = false;

	/**
	 * @return the interfaceKitSerial
	 */
	public final Boolean getDigitalOutput5() {
		return digitalOutput5;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setDigitalOutput5(Boolean digitalOutput) {
		this.digitalOutput5 = digitalOutput;

		if (interfaceKit != null) {
			try {
				interfaceKit.setOutputState(5, digitalOutput);
			}
			catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// ################################################################################

	@XmlAttribute(name = "interface-digital-output-active-6")
	@Property(
		name = "Interface digital output 6 active",
		description = "Activate or Deactivate digital Output 6"
	)
	@CheckBox
	private Boolean digitalOutput6 = false;

	/**
	 * @return the interfaceKitSerial
	 */
	public final Boolean getDigitalOutput6() {
		return digitalOutput6;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setDigitalOutput6(Boolean digitalOutput) {
		this.digitalOutput6 = digitalOutput;

		if (interfaceKit != null) {
			try {
				interfaceKit.setOutputState(6, digitalOutput);
			}
			catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// ################################################################################

	@XmlAttribute(name = "interface-digital-output-active-7")
	@Property(
		name = "Interface digital output 7 active",
		description = "Activate or Deactivate digital Output 7"
	)
	@CheckBox
	private Boolean digitalOutput7 = false;

	/**
	 * @return the interfaceKitSerial
	 */
	public final Boolean getDigitalOutput7() {
		return digitalOutput7;
	}

	/**
	 * @param interfaceKitSerial
	 *            the interfaceKitSerial to set
	 */
	public final void setDigitalOutput7(Boolean digitalOutput) {
		this.digitalOutput7 = digitalOutput;

		if (interfaceKit != null) {
			try {
				interfaceKit.setOutputState(7, digitalOutput);
			}
			catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private InterfaceKitPhidget interfaceKit;

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {

		try {
			interfaceKit = new InterfaceKitPhidget();
			if (interfaceKitSerial != 0) {
				interfaceKit.open(interfaceKitSerial);
			}
			else {
				interfaceKit.openAny();
			}
			interfaceKit.waitForAttachment(10000);

			interfaceKit.setOutputState(0, digitalOutput0);
			interfaceKit.setOutputState(1, digitalOutput1);
			interfaceKit.setOutputState(2, digitalOutput2);
			interfaceKit.setOutputState(3, digitalOutput3);
			interfaceKit.setOutputState(4, digitalOutput4);
			interfaceKit.setOutputState(5, digitalOutput5);
			interfaceKit.setOutputState(6, digitalOutput6);
			interfaceKit.setOutputState(7, digitalOutput7);

			interfaceKit.addSensorChangeListener(new SensorChangeListener() {

				/*
				 * (non-Javadoc)
				 * 
				 * @seecom.phidgets.event.SensorChangeListener#sensorChanged(com.phidgets.event.
				 * SensorChangeEvent)
				 */
				public void sensorChanged(SensorChangeEvent ae) {
					// TODO Auto-generated method stub
					int sensorIndex = ae.getIndex();
					double sensorValue = (double) ae.getValue() / 1000;

					DataAnalog dataAnalog = new DataAnalog(PhidgetInterfaceKit.class, sensorValue);
					dataAnalog.setAttribute(SENSOR_INDEX_INPUT, sensorIndex);
					publish(dataAnalog);
				}

			});
			
			Processable processable = getParent();
			if(pipelineStatus) {
				if(processable.isProcessing()) {
					interfaceKit.setOutputState(7, true);
					interfaceKit.setOutputState(0, false);
				}
				else {
					interfaceKit.setOutputState(7, false);
					interfaceKit.setOutputState(0, true);
				}
			}
		}
		catch (PhidgetException e) {
			throw new ProcessException(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {

		if (interfaceKit != null) {
			try {
				for (int i = 0; i < interfaceKit.getOutputCount(); i++) {
					interfaceKit.setOutputState(i, false);
				}
				Processable processable = getParent();
				if(pipelineStatus) {
					if(processable.isProcessing()) {
						interfaceKit.setOutputState(7, false);
						interfaceKit.setOutputState(0, true);
					}
					else {
						interfaceKit.setOutputState(7, true);
						interfaceKit.setOutputState(0, false);
					}
				}
				interfaceKit.close();
			}
			catch (PhidgetException e) {
				throw new ProcessException(e.getMessage(), e);
			}
		}
	}
	
//	public DataDigital process(DataButton dataButton) {
//		setDigitalOutput0(dataButton.getFlag());
//		return dataButton;
//	}

	public DataDigital process(DataDigital dataDigital) {

//		Integer callSensorIndex = (Integer) dataDigital.getAttribute(DataConstant.get(Integer.class,
//				"CALL_FOR_SENSOR_INDEX"));
		Integer callSensorIndex = (Integer) dataDigital.getAttribute(SENSOR_INDEX_INPUT);
		Integer outputSensorIndex = (Integer) dataDigital.getAttribute(SENSOR_INDEX_OUTPUT);
		if (dataDigital.getFlag() && callSensorIndex != null) {
			try {
				int value = interfaceKit.getSensorValue(callSensorIndex);
				value /= 1000;

				DataAnalog dataAnalog = new DataAnalog(PhidgetInterfaceKit.class, value);
				dataAnalog.setAttribute(SENSOR_INDEX_INPUT, callSensorIndex);

				publish(dataAnalog);
			}
			catch (PhidgetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Integer sensorOutputIndex = (Integer) dataDigital.getAttribute(SENSOR_INDEX_OUTPUT);

			if (sensorOutputIndex != null) {
				try {
					interfaceKit.setOutputState(sensorOutputIndex, dataDigital.getFlag());
				}
				catch (PhidgetException e) {
					// TODO Auto-generated catch block
					throw new ProcessException(e.getMessage(), e);
				}
			}
			else {
				Integer sensorInd = (Integer) dataDigital.getAttribute(SENSOR_INDEX_INPUT);
				try {
					interfaceKit.setOutputState(sensorInd, dataDigital.getFlag());
				}
				catch (PhidgetException e) {
					// TODO Auto-generated catch block
					throw new ProcessException(e.getMessage(), e);
				}
			}
		}
		else if (outputSensorIndex != null) {
			try {
				interfaceKit.setOutputState(outputSensorIndex, dataDigital.getFlag());
			}
			catch (PhidgetException e) {
				// TODO Auto-generated catch block
				throw new ProcessException(e.getMessage(), e);
			}
		}
		return null;
	}
}