/**
 * Squidy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Squidy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Squidy. If not, see <http://www.gnu.org/licenses/>.
 *
 * 2006-2009 Human-Computer Interaction Group, University of Konstanz.
 * <http://hci.uni-konstanz.de>
 *
 * Please contact info@squidy-lib.de or visit our website http://squidy-lib.de for
 * further information.
 */
/**
 *
 */
package org.squidy.nodes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>Timeout</code>.
 *
 * <pre>
 * Date: Okt 12, 2009
 * Time: 15:32:29 PM
 * </pre>
 *
 *
 * @author
 * Nicolas Hirrle
 * <a href="mailto:nihirrle@htwg-konstanz.de">nihirrle@htwg-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 *
 * @version $Id: Timeout.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0.0
 */
@XmlType(name = "Timeout")
@Processor(
	name = "Timeout",
	icon = "/org/squidy/nodes/image/48x48/funnel.png",
	description = "to be done",
	types = { Processor.Type.FILTER },
	tags = { },
	status = Status.UNSTABLE
)
public class Timeout extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################

	@XmlAttribute(name = "timeout")
	@Property(
		name = "Timeout",
		description = "Sets the timeout of no existing positions."
	)
	@TextField
	private long timeout = 200;

	/**
	 * @return the timeout
	 */
	public final long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public final void setTimeout(long timeout) {
		this.timeout = timeout;
		currentTimeout = timeout;
	}

	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	// ################################################################################
	// BEGIN OF LAUNCH
	// ################################################################################

	private long currentTimeout;
	private long timeSinceLastTimeout = 0;
	private boolean wait = true;
	private boolean wasLastSendEmpty = false;
	private boolean wasSend = false;
	 
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {
		new Thread(){

			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				 DataDigital DDtimeout = new DataDigital();
				DDtimeout.setAttribute(DataConstant.TIMEOUT, true);
				DDtimeout.setAttribute(ReacTIVision.TUIO_CURSOR, "removed");
				DDtimeout.setFlag(true);
				while (isProcessing()) 
				{
					long currentTime = System.currentTimeMillis();

					if ((currentTime - timeSinceLastTimeout) > timeout) 
					{
						timeSinceLastTimeout = currentTime;
						if(wasLastSendEmpty && !wasSend)
						{
							//sendEmulatedPosition();
							publish(DDtimeout);
							wait = true;
							wasSend = true;
							
						}
						wasLastSendEmpty = true;
					}
					

//					else
//						wasLastSendEmpty = false;
//						else if( sendEmulatedPos == true )// && !wasLastSendEmpty )
//						{
//							//System.out.println("Sending empty Cursor");
//							sendEmulatedPos = false;
//							//wasLastSendEmpty = true;
//						}
					//}


					try 
					{
						long sleep = Math.max(timeout, 10);
						wait = false;
						Thread.sleep(sleep);

					}
					catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

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
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer)
	{
		wasLastSendEmpty = false;
		wasSend = false;
		return super.preProcess(dataContainer);
	}

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
//	public IData process(DataPosition2D dataPosition2D)
//	{
//		return null;
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
//	public IDataContainer afterDataContainerProcessing(IDataContainer dataContainer)
//	{
//		return super.afterDataContainerProcessing(dataContainer);
//	}

	// ################################################################################
	// END OF PROCESS
	// ################################################################################
}
