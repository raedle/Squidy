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

import javax.xml.bind.annotation.XmlType;

import motej.Mote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>Nunchuk</code>.
 * 
 * <pre>
 * Date: Feb 15, 2008
 * Time: 3:26:37 AM
 * </pre>
 *
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,, University of Konstanz
 * @version $Id: WiiNunchuk.java 772 2011-09-16 15:39:44Z raedle $
 *
 */
@XmlType(name = "WiiNunchuk")
@Processor(
	name = "WiiNunchuk",
	types = { Processor.Type.INPUT },
	description = "/org/squidy/nodes/html/WiiNunchuk.html",
	tags = { "wiimote", "nintendo", "wii", "tracking", "ir", "infrared", "camera" },
	status = Status.UNSTABLE
)
public class WiiNunchuk extends AbstractNode {

	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Wiimote.class);
	
	// The connected mote.
	protected Mote mote;
	
	/* (non-Javadoc)
	 * @see org.squidy.nodes.Wiimote#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();

		while (mote == null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		mote.addExtensionListener(new ExtensionListener() {
//
//			/* (non-Javadoc)
//			 * @see motej.event.ExtensionListener#extensionConnected(motej.event.ExtensionEvent)
//			 */
//			public void extensionConnected(ExtensionEvent evt) {
//				
//				Extension extension = evt.getExtension();
//				
//				if (extension instanceof Nunchuk) {
//					if (LOG.isDebugEnabled()) {
//						LOG.debug("Nunchuk has been connected to WiiMote [" + evt.getSource().getBluetoothAddress() + "].");
//					}
//					
//					Nunchuk nunchuk = (Nunchuk) extension;
//					
//					nunchuk.addAnalogStickListener(new AnalogStickListener() {
//
//						/* (non-Javadoc)
//						 * @see motejx.extensions.nunchuk.AnalogStickListener#analogStickChanged(motejx.extensions.nunchuk.AnalogStickEvent)
//						 */
//						public void analogStickChanged(AnalogStickEvent evt) {
//							Point p = evt.getPoint();
//							
//							double x = p.getX();
//							double y = p.getY();
//							
//							x = (x - 30) / (230 - 30);
//							y = (y - 37) / (224 - 37);
//							
//							System.out.println("X:Y " + x + " : " + y);
//							
//							publishSingle(new DataPosition2D(WiiNunchuk.class, "DataAnalogPosition2D", x, y));
//						}
//					});
//					mote.setReportMode(ReportModeRequest.DATA_REPORT_0x32);
//				}
//			}
//
//			/* (non-Javadoc)
//			 * @see motej.event.ExtensionListener#extensionDisconnected(motej.event.ExtensionEvent)
//			 */
//			public void extensionDisconnected(ExtensionEvent evt) {
//				Extension extension = evt.getExtension();
//				
//				if (extension instanceof Nunchuk) {
//					if (LOG.isDebugEnabled()) {
//						LOG.debug("Nunchuk has been removed from WiiMote [" + evt.getSource().getBluetoothAddress() + "].");
//					}
//				}
//				
//				mote.setReportMode(ReportModeRequest.DATA_REPORT_0x36);
//			}
//		});
	}
}
