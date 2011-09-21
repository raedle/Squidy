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


package org.squidy.manager.model;

import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.data.Processor;


/**
 * <code>EmptyNode</code>.
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
 * @version $Id: EmptyNode.java 776 2011-09-18 21:34:48Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "EmptyNode")
@Processor(
	name = "EmptyNode",
	icon = "/org/squidy/nodes/image/48x48/funnel.png",
	description = "Empty Node to be filled...",
	types = { Processor.Type.INPUT, Processor.Type.FILTER, Processor.Type.OUTPUT },
	tags = { }
)
public class EmptyNode extends AbstractNode {

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
	// END OF PROPERTIES
	// ################################################################################
	
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
//	public IData process(DataInertial dataInertial) {
//		return dataInertial;
//	}

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
