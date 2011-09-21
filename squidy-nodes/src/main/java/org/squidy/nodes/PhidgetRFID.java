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
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.model.AbstractNode;

import com.phidgets.PhidgetException;
import com.phidgets.RFIDPhidget;
import com.phidgets.event.TagGainEvent;
import com.phidgets.event.TagGainListener;
import com.phidgets.event.TagLossEvent;
import com.phidgets.event.TagLossListener;


/**
 * <code>PhidgetRFID</code>.
 *
 * <pre>
 * Date: Dec 18, 2008
 * Time: 10:42:28 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: PhidgetRFID.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
@XmlType(name = "Phidget RFID")
@Processor(
	name = "Phidget RFID",
	types = { Processor.Type.INPUT, Processor.Type.OUTPUT },
	description = "/org/squidy/nodes/html/PhidgetRFID.html",
	tags = { "phidget", "rfid" },
	status = Status.UNSTABLE
)
public class PhidgetRFID extends AbstractNode {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(PhidgetRFID.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	@XmlAttribute(name = "rfid-serial")
	@Property(
		name = "RFID serial"
	)
	@TextField
	private int rfidSerial = 0;

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.logic.ReflectionProcessable#start()
	 */
	/**
	 * @return the rfidSerial
	 */
	public final int getRfidSerial() {
		return rfidSerial;
	}

	/**
	 * @param rfidSerial the rfidSerial to set
	 */
	public final void setRfidSerial(int rfidSerial) {
		this.rfidSerial = rfidSerial;
	}
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private RFIDPhidget rfid;
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {
		
		try {
			rfid = new RFIDPhidget();
			
			if (rfidSerial != 0) {
				rfid.open(rfidSerial);
			}
			else {
				rfid.openAny();
			}
			rfid.waitForAttachment(10000);
			
			rfid.addTagGainListener(new TagGainListener() {

				/* (non-Javadoc)
				 * @see com.phidgets.event.TagGainListener#tagGained(com.phidgets.event.TagGainEvent)
				 */
				public void tagGained(TagGainEvent e) {
					String value = e.getValue();
					
					DataDigital tag = new DataDigital(PhidgetRFID.class, true);
					tag.setAttribute(DataConstant.get(String.class, "TAG_VALUE"), value);
					
					publish(tag);
				}
			});
			rfid.addTagLossListener(new TagLossListener() {

				/* (non-Javadoc)
				 * @see com.phidgets.event.TagLossListener#tagLost(com.phidgets.event.TagLossEvent)
				 */
				public void tagLost(TagLossEvent e) {
					String value = e.getValue();
					
					DataDigital tag = new DataDigital(PhidgetRFID.class, false);
					tag.setAttribute(DataConstant.get(String.class, "TAG_VALUE"), value);
					
					publish(tag);
				}
			});
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

		if (rfid != null) {
			try {
				rfid.close();
			}
			catch (PhidgetException e) {
				throw new ProcessException(e.getMessage(), e);
			}
		}
	}
	
	// ################################################################################
	// END OF PROCESS
	// ################################################################################
	
	/**
	 * @param dataDigital
	 * @return
	 */
	public IData process(DataDigital dataDigital) {
		
		if (dataDigital.hasAttribute(DataConstant.TICK)) {
			try {
//				rfid.setLEDOn(dataDigital.getFlag());
				rfid.setOutputState(0, dataDigital.getFlag());
				rfid.setOutputState(1, dataDigital.getFlag());
			}
			catch (PhidgetException e) {
				throw new ProcessException("Tried to set led and output state of RFID Phidget.", e);
			}
		}
		
		return null;
	}
	
	// ################################################################################
	// BEGIN OF PROCESS
	// ################################################################################
}
