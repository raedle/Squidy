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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.protocol.multicast.MulticastAdapter;
import org.squidy.manager.protocol.multicast.MulticastServer;


/**
 * <code>Tracking</code>.
 *
 * <pre>
 * Date: Oct 15, 2008
 * Time: 2:42:56 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 *         
 * @version $Id: Tracking.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "Tracking")
@Processor(
	name = "Tracking",
	types = { Processor.Type.INPUT },
	description = "/org/squidy/nodes/html/Tracking.html",
	tags = { "tracking" },
	status = Status.UNSTABLE
)
public class Tracking extends AbstractNode {

	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Tracking.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	@XmlAttribute(name = "multicast-group-address")
	@Property(
		name = "Multicast group address",
		description = "The address of the multicast group that receives multicasting data."
	)
	@TextField
	private String multicastGroupAddress = "224.0.0.1";
	
	/**
	 * @return the multicastGroupAddress
	 */
	public final String getMulticastGroupAddress() {
		return multicastGroupAddress;
	}

	/**
	 * @param multicastGroupAddress the multicastGroupAddress to set
	 */
	public final void setMulticastGroupAddress(String multicastGroupAddress) {
		this.multicastGroupAddress = multicastGroupAddress;
		
		if (server != null) {
			stopMulticastServer();
			startMulticastServer();
		}
	}
	
	// ################################################################################

	@XmlAttribute(name = "port")
	@Property(
		name = "Port",
		description = "The port of the multicast server that receives the tracking data."
	)
	@TextField
	private int port = 1001;
	
	/**
	 * @return the port
	 */
	public final int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public final void setPort(int port) {
		this.port = port;
		
		if (server != null) {
			stopMulticastServer();
			startMulticastServer();
		}
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "width")
	@Property(
		name = "Width",
		description = "Cube Display width in mm.",
		suffix = "mm"
	)
	@TextField
	private float width = 2000;

	/**
	 * @return the width
	 */
	public final float getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public final void setWidth(float width) {
		this.width = width;
	}
	
	// ################################################################################

	@XmlAttribute(name = "height")
	@Property(
		name = "Height",
		description = "Cube Display height in mm.",
		suffix = "mm"
	)
	@TextField
	private float height = 1000;

	/**
	 * @return the height
	 */
	public final float getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public final void setHeight(float height) {
		this.height = height;
	}
	
	// ################################################################################

	@XmlAttribute(name = "depth")
	@Property(
		name = "Depth",
		description = "Cube Display depth."
	)
	@TextField
	private float depth = 5000;

	/**
	 * @return the depth
	 */
	public final float getDepth() {
		return depth;
	}

	/**
	 * @param depth the depth to set
	 */
	public final void setDepth(float depth) {
		this.depth = depth;
	}
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	protected int lastFrameCounter = -1;
	protected int trackedBodies;
	double x;
	double y;
	double z;
	double qx;
	double qy;
	double qz;
	double qw;
	double rxx;
	double ryx;
	double rzx;
	double rxy;
	double ryy;
	double rzy;
	double rxz;
	double ryz;
	double rzz;
	
	
	protected MulticastServer server;

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() throws ProcessException {
		startMulticastServer();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	@Override
	public void onStop() throws ProcessException {
		stopMulticastServer();
	}
	
	/**
	 * 
	 */
	private void startMulticastServer() throws ProcessException {
		InetAddress multicastGroup;
		try {
			multicastGroup = InetAddress.getByName(multicastGroupAddress);
		}
		catch (UnknownHostException e) {
			throw new ProcessException(e.getMessage(), e);
		}
		
		server = new MulticastServer(multicastGroup, port);
		
		server.addMulticastListener(new MulticastAdapter() {

			/* (non-Javadoc)
			 * @see org.squidy.manager.protocol.udp.UDPListener#parseData(byte[])
			 */
			public void parseData(byte[] data) {
				// TODO [SF]: Do your parsing stuff here!!!
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				DataInputStream instream = new DataInputStream(bais);
				
				
				try {
					System.out.println("short: " + instream.readUnsignedShort() + " | " + instream.readUnsignedShort() + " | " + (instream.readInt() & 0x7F));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
//					
//				String s = new String(data);
//				System.out.println(s);
				
/*	
 *              Data is coded with a started MessageID = 7
 				lastFrameCounter = <FrameId>;			
 				
  				if (<singleMarker == true>){
  				
  					trackedBodies = <numTrackedSingleMarker>;
  					bodyId = 0;
  				
  					//Read x,y,z and convert to mm
  					x = <x>*100; 
  					y = <y>*100;
  					z = <z>*100;
  				
  					publish(new DataPosition3D(Tracking.class, bodyID, x, y, z, width,
						height, depth, lastFrameCounter, trackedBodies));
						
					// only publish single marker positions, not the ones belonging to a rigit body	
  				}
  				if (<rigitBody == true>){
  				
  					trackedBodies = <numTrackedRigitBodies>;
  				  	bodyId = <rigitBodyId>; 				
  					//Read x,y,z and convert to mm
  					x = <x>*100; 
  					y = <y>*100;
  					z = <z>*100;
  					
  					//Read quatrions
  					qx = <qx>;
  					qy = <qy>;
  					qz = <qz>;
  					qw = <qw>;
  					
  					//Transform to rotation matrix
  					//Spalte 1:
					rxx = 2*(qx*qx + qw*qw)-1;
					ryx = 2*(qx*qy + qz*qw);
					rzx = 2*(qx*qz - qy*qw);
					Spalte 2:
					rxy = 2*(qx*qy - qz*qw);
					ryy = 2*(qy*qy + qq*qw)-1;
					rzy = 2*(qy*qz + qx*qw);
					Spalte 3:
					rxz = 2*(qx*qz + qy*qw);
					ryz = 2*(qy*qz - qx*qw);
					rzz = 2*(qz*qz + qw*qw)-1;
				
					//quadToMatrix: ?
					//m[0] = 1-2*q[1]*q[1]-2*q[2]*q[2]; m[1] = 2*q[0]*q[1]-2*q[3]*q[2];   m[2] = 2*q[0]*q[2]+2*q[3]*q[1];
  					//m[3] = 2*q[0]*q[1]+2*q[3]*q[2];   m[4] = 1-2*q[0]*q[0]-2*q[2]*q[2]; m[5] = 2*q[1]*q[2]-2*q[3]*q[0];
  					//m[6] = 2*q[0]*q[2]-2*q[3]*q[1];   m[7] = 2*q[1]*q[2]+2*q[3]*q[0];   m[8] = 1-2*q[0]*q[0]-2*q[1]*q[1];
  				
  					publish(new DataPosition6D(arTracking.getClass(), bodyID, x, y, z, width,
					height, depth, rxx, ryx, rzx, rxy, ryy, rzy, rxz, ryz, rzz, lastFrameCounter,
					trackedBodies));

				
				}
				
				
*/	
				
//			
//			    ...
			}
		});
	}
	
	/**
	 * 
	 */
	private void stopMulticastServer() {
		if (server != null) {
			server.close();
			server = null;
		}
	}
}
