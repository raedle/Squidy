package org.squidy.nodes;

import java.net.DatagramPacket;
import java.nio.charset.Charset;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.Slider;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.protocol.udp.UDPListener;
import org.squidy.manager.protocol.udp.UDPServer;
import org.squidy.nodes.ipoint.HHIHandData;
import org.squidy.nodes.ipoint.Position;

import sun.tools.tree.DoStatement;

import com.google.gson.Gson;


/**
 * <code>iPoint</code>.
 *
 * <pre>
 * Date: September 17, 2010
 * Time: 10:47:02 PM
 * </pre>
 *
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id$
 * @since 1.5
 */
@XmlType(name = "iPoint")
@Processor(
	types = { Processor.Type.OUTPUT },
	name = "iPoint",
	tags = { "HHI", "Heinrich Hertz Institute", "Fraunhofer", "gesture", "pointing", "freehand" }
)
public class iPoint extends AbstractNode {

//	public static final DataConstant TRACKER_STATE = DataConstant.get(TrackerState.class, "TRACKER_STATE");
//	
//	public enum TrackerState {
//		POINTING, FULL_HAND, FIST, FINGER_AND_THUMB, VICTORY
//	}
	
	public static final DataConstant TRACKER_STATE = DataConstant.get(String.class, "TRACKER_STATE");
	
	public static final String TRACKER_STATE_INVALID = "Invalid";
	public static final String TRACKER_STATE_POINTING = "pointing";
	public static final String TRACKER_STATE_FULL_HAND = "full hand";
	public static final String TRACKER_STATE_FIST = "fist";
	public static final String TRACKER_STATE_FINGER_AND_THUMB = "finger and thumb";
	public static final String TRACKER_STATE_VICTORY = "victory";
	
	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################
	
	@XmlAttribute(name = "boundary-x-left")
	@Property(
		name = "Boundary X Left",
		description = "Indicates the left boundary value of the x-axis"
	)
	@Slider(
		minimumValue = -250,
		maximumValue = 250,
		majorTicks = 100,
		minorTicks = 50,
		showTicks = true,
		showLabels = true,
		type = Integer.class
	)
	private int boundaryXLeft = -140;
	
	public int getBoundaryXLeft() {
		return boundaryXLeft;
	}

	public void setBoundaryXLeft(int boundaryXLeft) {
		this.boundaryXLeft = boundaryXLeft;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "boundary-x-right")
	@Property(
		name = "Boundary X Right",
		description = "Indicates the right boundary value of the x-axis"
	)
	@Slider(
		minimumValue = -250,
		maximumValue = 250,
		majorTicks = 100,
		minorTicks = 50,
		showTicks = true,
		showLabels = true,
		type = Integer.class
	)
	private int boundaryXRight = 140;
	
	public int getBoundaryXRight() {
		return boundaryXRight;
	}

	public void setBoundaryXRight(int boundaryXRight) {
		this.boundaryXRight = boundaryXRight;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "boundary-y-upper")
	@Property(
		name = "Boundary Y Upper",
		description = "Indicates the upper boundary value of the y-axis"
	)
	@Slider(
		minimumValue = 0,
		maximumValue = 1000,
		majorTicks = 250,
		minorTicks = 100,
		showTicks = true,
		showLabels = true,
		type = Integer.class
	)
	private int boundaryYUpper = 600;
	
	public int getBoundaryYUpper() {
		return boundaryYUpper;
	}

	public void setBoundaryYUpper(int boundaryYUpper) {
		this.boundaryYUpper = boundaryYUpper;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "boundary-y-lower")
	@Property(
		name = "Boundary Y Lower",
		description = "Indicates the lower boundary value of the y-axis"
	)
	@Slider(
		minimumValue = 0,
		maximumValue = 1000,
		majorTicks = 250,
		minorTicks = 100,
		showTicks = true,
		showLabels = true,
		type = Integer.class
	)
	private int boundaryYLower = 280;
	
	public int getBoundaryYLower() {
		return boundaryYLower;
	}

	public void setBoundaryYLower(int boundaryYLower) {
		this.boundaryYLower = boundaryYLower;
	}
	
	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	private UDPServer server;
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		
		final Gson gson = new Gson();
		
		server = new UDPServer(3000);
		server.addUDPListener(new UDPListener() {
			
			public void parseData(String s) {
//				System.out.printf("ECHO %s\n", s);
			}
			
			public void parseData(byte[] data) {
//				System.out.println(data.length);
////				HHIHandData handData = gson.fromJson(new String(data, Charset.forName("UTF-8")), HHIHandData.class);
//
//				System.out.println(new String(data, Charset.forName("UTF-8")));
			}

			public void receive(DatagramPacket packet) {
				String s = new String(packet.getData(), packet.getOffset(), packet.getLength(), Charset.forName("UTF-8"));
				
//				System.out.println(s);
				
				HHIHandData handData = gson.fromJson(s, HHIHandData.class);
				
				if (TRACKER_STATE_POINTING.equals(handData.getAction()) ||
						TRACKER_STATE_VICTORY.equals(handData.getAction()) ||
						TRACKER_STATE_FINGER_AND_THUMB.equals(handData.getAction())) {
					doPublishIfIsInBoundaries(handData.getFingerTip(), boundaryXLeft, boundaryXRight, boundaryYUpper, boundaryYLower, handData.getAction());
				}
				else if (TRACKER_STATE_FULL_HAND.equals(handData.getAction()) ||
						TRACKER_STATE_FIST.equals(handData.getAction())) {
//					System.out.println(handData.getHandCenter());
					doPublishIfIsInBoundaries(handData.getHandCenter(), -200, 200, 145, 45, handData.getAction());
				}
			}
		});
		
//		Gson gson = new Gson();
//		gson.
	}
	
	private void doPublishIfIsInBoundaries(Position position, int boundaryXLeft, int boundaryXRight, int boundaryYUpper, int boundaryYLower, String action) {
		
		double x = 0;
		double posX = position.getdPosX();
		if (posX >= boundaryXLeft && posX <= boundaryXRight) {
			x = (posX - boundaryXLeft) / Math.abs(boundaryXRight - boundaryXLeft);
			
			// minimize tracking error rate
			if (x < 0.01) {
				return;
			}
			else if (x > 0.99)
				return;
		}
		
		double y = 0;
		double posY = position.getdPosY();
		if (posY >= boundaryYLower && posY <= boundaryYUpper) {
			y = (posY - boundaryYLower) / Math.abs(boundaryYUpper - boundaryYLower);
			
			// minimize tracking error rate
			if (y < 0.01) {
				return;
			}
			else if (y > 0.99)
				return;
		}
		
		if (x == 0 || y == 0)
			return;
		
		DataPosition2D dataPosition2D = new DataPosition2D(iPoint.class, x, y);
		dataPosition2D.setAttribute(TRACKER_STATE, action);
		publish(dataPosition2D);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		
		if (server != null) {
			server.close();
			server = null;
		}
	}
}