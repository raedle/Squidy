package org.squidy.nodes.optitrack;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Track;
import javax.swing.KeyStroke;
import javax.vecmath.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.Slider;
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
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.data.impl.DataString;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.Keyboard;
import org.squidy.nodes.optitrack.RigidBody.RBIDDomainProvider;
import org.squidy.nodes.optitrack.utils.TrackingConstant;


/*<code>RoomObject</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AMd
* </pre>
* 
* @author Simon Faeh, <a href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de<a/>, University of Konstanz
* 
* @version 
*/
@XmlType(name = "RoomObject")
@Processor(
	name = "Room Object",
	icon = "/org/squidy/nodes/image/48x48/roomobject.png",
	description = "Physical object in the tracked area",
	types = {Processor.Type.OUTPUT, Processor.Type.INPUT },
	tags = { "room", "object", "optitrack", "handtracking", "interception" },
	status = Status.UNSTABLE
)

public class RoomObject extends AbstractNode {
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	
	@XmlAttribute(name = "displayPreset")
	@Property(
		name = "Display Preset",
		description = "Load Display definition"
	)
	@ComboBox(domainProvider = RBIDDomainProvider.class)
	private int displayDef = TrackingConstant.ROOMOBJECT_MANUAL;
	
	public final int getDisplayDef() {
		return displayDef;
	}

	public final void setDisplayDef(int rID) {
		this.displayDef = rID;
		createRoomObject();
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "screenOversize")
	@Property(
		name = "Screen Oversize",
		description = "Virutally enlarged Screen",
		suffix = " %"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 100,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 5,
		snapToTicks = true
	)
	private int overSize = 20;

	public int getOverSize() {
		return overSize;
	}

	public void setOverSize(int overSize) {
		this.overSize = overSize;
	}
	
	// ################################################################################		
	
	@XmlAttribute(name = "backFaceTracking")
	@Property(
			name = "Backside Pointing", 
			description = "Allows pointing through the backside of the display")
	@CheckBox
	private boolean backFaceTracking =  false;

	public final boolean getBackFaceTracking() {
		return backFaceTracking;
	}

	public final void setBackFaceTracking(boolean backFaceTracking) {
		this.backFaceTracking = backFaceTracking;
	}	
	
	// ################################################################################	
	
	@XmlAttribute(name = "objectName")
	@Property(
			name = "Object Name", 
			description = "Name of the Target-Object",
			group = "Manual")
	@TextField
	private String objectName = "Cubes";

	public final String getObjectName() {
		return objectName;
	}

	public final void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "displayTopLeftCornder3D")
	@Property(
			name = "Top Left Corner (x,y,z)", 
			description = "3D coordinates of the Top Left Corner \"x,y,z\" in mm",
			group = "Manual")
	@TextField
	private String displayTL = "1445,1885,0";

	public final String getDisplayTL() {
		return displayTL;
	}

	public final void setDisplayTL(String displayTL) {
		this.displayTL = displayTL;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "displayBottomLeftCornder3D")
	@Property(
			name = "Bottom Left Corner (x,y,z)", 
			description = "3D coordinates of the Bottom Left Corner \"x,y,z\" in mm",
			group = "Manual")
	@TextField
	private String displayBL = "1445,1135,0";

	public final String getDisplayBL() {
		return displayBL;
	}

	public final void setDisplayBL(String displayBL) {
		this.displayBL = displayBL;
	}		
	

	// ################################################################################	
	
	@XmlAttribute(name = "displayBottomRightCornder3D")
	@Property(
			name = "Bottom Right Corner (x,y,z)", 
			description = "3D coordinates of the Bottom Right Corner \"x,y,z\" in mm",
			group = "Manual")
	@TextField
	private String displayBR = "4353,1135,41";

	public final String getDisplayBR() {
		return displayBR;
	}

	public final void setDisplayBR(String displayBR) {
		this.displayBR = displayBR;
	}	
	// ################################################################################	
	
	
	@XmlAttribute(name = "objectHeight")
	@Property(
			name = "Object Height", 
			description = "Height of the object in mm",
			group = "Mobile")
	@TextField
	private double objectHeight = 0;

	public final double getObjectHeight() {
		return objectHeight;
	}

	public final void setObjectHeight(double displayHeight) {
		this.objectHeight = displayHeight;
	}
	// ################################################################################	
	
	@XmlAttribute(name = "objectWidth")
	@Property(
			name = "Object Width", 
			description = "Width of the object in mm",
			group = "Mobile")
	@TextField
	private double objectWidth = 0;

	public final double getObjectWidth() {
		return objectWidth;
	}

	public final void setObjectWidth(double displayDepth) {
		this.objectWidth = displayDepth;
	}	
	// ################################################################################	
	
	@XmlAttribute(name = "objectDepth")
	@Property(
			name = "Object Depth", 
			description = "Depth of the object in mm",
			group = "Mobile")
	@TextField
	private double objectDepth = 0;

	public final double getObjectDepth() {
		return objectDepth;
	}

	public final void setObjectDepth(double displayDepth) {
		this.objectDepth = displayDepth;
	}
	
	// ################################################################################	
	
	@XmlAttribute(name = "originOffsett")
	@Property(
			name = "Offset from Markers Origin", 
			description = "Offset \"x,y,z\" in mm",
			group = "Mobile")
	@TextField
	private String displayOffset = "0,0,0";

	public final String getDisplayOffset() {
		return displayOffset;
	}

	public final void setDisplayOffset(String displayOS) {
		this.displayOffset = displayOS;
	}	
	// ################################################################################		
	
	@XmlAttribute(name = "isMobileDisplay")
	@Property(
			name = "Mobile Display", 
			description = "Check if this Display gets its position by a Rigidbody",
			group ="Mobile")
	@CheckBox
	private boolean isDisplay =  false;

	public final boolean getIsDisplay() {
		return isDisplay;
	}

	public final void setIsDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}
	
	// ################################################################################	



	@XmlAttribute(name = "keyID")
	@Property(
		name = "Calibration Key",
		description = "Pressing the key sets the Object to static",
		group = "Mobile"
	)
	@TextField
	private String keyStroke = "-";
	
	public final String getKeyStroke() {
		return keyStroke;
	}

	public final void setKeyStroke(String key) {
		this.keyStroke = key;
	}
	
	@XmlAttribute(name = "host")
	@Property(
		name = "Host",
		description = "The host which should receive Squidy Remote data.",
		group = "Remote Connection"
	)
	@TextField
	private String host = "127.0.0.1";
	
	/**
	 * @return the host
	 */
	public final String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public final void setHost(String host) {
		this.host = host;
	}
	
	// ################################################################################

	@XmlAttribute(name = "port")
	@Property(
		name = "Port",
		description = "The port which should receive Squidy Remote data.",
		group = "Remote Connection"
	)
	@TextField
	private int port = 1919;

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
	}
	
	// ################################################################################
	
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
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[6];
			values[0] = new ComboBoxItemWrapper(TrackingConstant.ROOMOBJECT_CUBES, "Cubes");
			values[1] = new ComboBoxItemWrapper(TrackingConstant.ROOMOBJECT_CITRON, "Citron");
			values[2] = new ComboBoxItemWrapper(TrackingConstant.ROOMOBJECT_SURFACE, "Surface");
			values[3] = new ComboBoxItemWrapper(TrackingConstant.ROOMOBJECT_ICT, "ICT Touch-Table");
			values[4] = new ComboBoxItemWrapper(TrackingConstant.ROOMOBJECT_4k, "4K-Dispaly");
			values[5] = new ComboBoxItemWrapper(TrackingConstant.ROOMOBJECT_MANUAL, "Set Manually");
			return values;
		}
	}
	
	// ################################################################################
	// END OF DOMAIN PROVIDERS
	// ################################################################################	
		
	@Override
	public void onStarted() {
		if(!this.isDisplay)
		{
			publish(createRoomObject());
		}
	} 
	@Override
	public void onStart() {
		if(!this.isDisplay)
		{
			keyPressed = false;
			publish(createRoomObject());
		}
	} 
	private DataString createRoomObject()
	{
		switch(this.displayDef) 
		{
			case TrackingConstant.ROOMOBJECT_CUBES : 
			{
				this.displayTL = "4480,1900,0";
				this.displayBL = "4480,1140,0";
				this.displayBR = "0,1135,0";
				break;
			}
			case TrackingConstant.ROOMOBJECT_CITRON : 
			{
				this.displayTL = "4484,1780,4320";
				this.displayBL = "4484,1220,4320";
				this.displayBR = "5270,1228,3775";
				break;
			}
			case TrackingConstant.ROOMOBJECT_ICT : 
			{
//				this.displayTL = "5020,1400,3200";
//				this.displayBL = "5020,1000,3200";
//				this.displayBR = "5020,1000,2700";
				this.displayTL = "3450,1050,4210";
				this.displayBL = "3450,800,4100";
				this.displayBR = "3850,800,4100";				
				break;
			}
			case TrackingConstant.ROOMOBJECT_SURFACE : 
			{
				this.displayTL = "4417,558,1836";
				this.displayBL = "4659,579,2250";
				this.displayBR = "4020,557,2537";
				break;
			}
			case TrackingConstant.ROOMOBJECT_4k : 
			{
				this.displayTL = "1470,1700,3820";
				this.displayBL = "1470,1000,3820";
				this.displayBR = "2705,1010,4600";				
				break;
			}	
			case TrackingConstant.ROOMOBJECT_MANUAL : 
			{
				break;
			}
		}		
		DataString ds = new DataString(RoomObject.class, this.displayTL+";"+this.displayBL+";"+this.displayBR);
		ds.setAttribute(TrackingConstant.OBJECTHEIGHT, this.objectHeight);
		ds.setAttribute(TrackingConstant.OBJECTWIDHT, this.objectDepth);
		ds.setAttribute(TrackingConstant.OBJECTDEPTH, this.objectDepth);
		ds.setAttribute(DataConstant.IDENTIFIER, this.objectName);
		ds.setAttribute(TrackingConstant.SCREENOVERSIZE, (this.overSize/100.0));
		ds.setAttribute(TrackingConstant.REMOTEHOST, this.host);
		ds.setAttribute(TrackingConstant.REMOTEPORT, this.port);
		ds.setAttribute(TrackingConstant.BACKFACETRACKING, this.backFaceTracking);
		
		/*
		 * 
		 */
//		Vector3d TL, BL, BR, displayUp, displaySide, displayNorm, xVec,yVec,zVec,pv,ov, rv;
//		String chunks[] = displayTL.split(",");
//        TL = new Vector3d(Double.parseDouble(chunks[0]),
//			      Double.parseDouble(chunks[1]),
//			      Double.parseDouble(chunks[2]));
//        chunks = displayBL.split(",");
//		BL = new Vector3d(Double.parseDouble(chunks[0]),
//					      Double.parseDouble(chunks[1]),
//					      Double.parseDouble(chunks[2]));
//		chunks = displayBR.split(",");
//		BR = new Vector3d(Double.parseDouble(chunks[0]),
//					      Double.parseDouble(chunks[1]),
//					      Double.parseDouble(chunks[2]));
//		
//		displayUp = (Vector3d) TL.clone();
//		displayUp.sub(BL);
//		double height = displayUp.length();
//		displayUp.normalize();
//		displaySide = (Vector3d) BR.clone();
//		displaySide.sub(BL);
//		double with = displaySide.length();
//		displaySide.normalize();
//		displayNorm = new Vector3d();
//		displayNorm.cross(displayUp, displaySide);
//		displayNorm.normalize();
//		System.out.println("displayUP "   + displayUp.toString());
//		System.out.println("displaySide " + displaySide.toString());
//		xVec = new Vector3d(1,0,0);
//		yVec = new Vector3d(0,1,0);
//		zVec = new Vector3d(0,0,-1);
//		double angleX, angleY, angleZ;
//		angleX = yVec.angle(displayUp);
//		angleY = zVec.angle(displayNorm);
//		angleZ = xVec.angle(displaySide);
//		System.out.println("angles " + Math.toDegrees(angleX) + " " + Math.toDegrees(angleY) + " " + Math.toDegrees(angleZ));
//		MathUtility mu = new MathUtility();
//		//double[][] mRot = mu.rotateMatrix(null, -angleX, 0, 0);
//		double[][] mRot = mu.rotateMatrix(MathUtility.Y_AXIS, null, -angleY);
//		//mRot = mu.transpose(mRot);
//		pv = new Vector3d(0,0,1);
//		ov = new Vector3d(0,0,0);
//	    rv = mu.rotatePoint(pv, ov, mRot, false);
//		System.out.println("rotation vector Y" + rv.toString());
//		angleZ = rv.angle(displaySide);
//		mRot = mu.rotateMatrix(MathUtility.Z_AXIS, mRot, -angleZ);
//		pv = new Vector3d(0,0,1);
//		rv = mu.rotatePoint(pv, ov, mRot, false);
//		System.out.println("rotation vector Z" + rv.toString());
//		angleX = rv.angle(displayNorm);
//		mRot = mu.rotateMatrix(MathUtility.X_AXIS, mRot, -angleX);
//		pv = new Vector3d(1,0,0);
//		rv = mu.rotatePoint(pv, ov, mRot, false);
//		System.out.println("rotation vector Y" + rv.toString());		
		
		//BL.scaleAdd(height, pv);
		//BL.sub(rv);
//		System.out.println("BL " + BL.toString());
//		BL.add(rv);
//		System.out.println("NL " + BL.toString());
//		System.out.println("BR " + BR.toString());

		return ds;
	}
	
    /* (non-Javadoc)
     * @see org.squidy.manager.model.AbstractNode#preProcess(org.squidy.manager.data.IDataContainer)
     */
	
	private boolean keyPressed;
	public void process(DataDigital dataDigital) 
    {
    	if (dataDigital.hasAttribute(Keyboard.KEY_EVENT))
    	{
        	Integer key_event = (Integer) dataDigital.getAttribute(Keyboard.KEY_EVENT);
        	if (KeyStroke.getKeyStroke(this.keyStroke) == KeyStroke.getKeyStroke(key_event.intValue(),0))
    		{
        		if (dataDigital.getFlag())
        			keyPressed = !keyPressed;
        		
        		dataDigital.killAll();
    		}
    	}
    }
	private MathUtility mu = new MathUtility();
	public IData process(DataPosition6D d6d) 
    {
		if (d6d.hasAttribute(TrackingConstant.RIGIDBODYROLE) && !keyPressed) 
		{
			if (Integer.valueOf(d6d.getAttribute(TrackingConstant.RIGIDBODYROLE).toString()) == TrackingConstant.RBROLE_MOBILEDISPLAY)
			{
				d6d.setAttribute(TrackingConstant.OBJECTHEIGHT, this.objectHeight);
		        d6d.setAttribute(TrackingConstant.OBJECTWIDHT, this.objectWidth);
		        d6d.setAttribute(TrackingConstant.OBJECTDEPTH, this.objectDepth);
		        d6d.setAttribute(TrackingConstant.RIGIDBODYROLE, d6d.getAttribute(TrackingConstant.RIGIDBODYROLE));
		        d6d.setAttribute(DataConstant.IDENTIFIER, this.objectName);		
		        d6d.setAttribute(DataConstant.IDENTIFIER, this.objectName);
		        d6d.setAttribute(TrackingConstant.SCREENOVERSIZE, this.overSize/100.0);
		        d6d.setAttribute(TrackingConstant.REMOTEHOST, this.host);
		        d6d.setAttribute(TrackingConstant.REMOTEPORT, this.port);
		        d6d.setAttribute(TrackingConstant.BACKFACETRACKING, this.backFaceTracking);		        
		        String[] chunks = this.displayOffset.split(",");
		        /*d6d = TrackingUtility.Norm2RoomCoordinates(Optitrack.class, d6d);
		        //Vector3d offsetRot = new Vector3d(Double.parseDouble(chunks[0]),Double.parseDouble(chunks[1]),Double.parseDouble(chunks[2]));
		        DataPosition3D offsetRot = new DataPosition3D();
		        offsetRot.setX(Double.parseDouble(chunks[0]));
		        offsetRot.setY(Double.parseDouble(chunks[1]));
		        offsetRot.setZ(Double.parseDouble(chunks[2]));
		        offsetRot = mu.rotatePoint(offsetRot, d6d, d6d, true,false);
		        //System.out.println(offsetRot.getX() + "\t"+ offsetRot.getY() + "\t"+ offsetRot.getZ());
		        d6d.setX(d6d.getX()  + offsetRot.getX());
		        d6d.setY(d6d.getY()  + offsetRot.getY());
		        d6d.setZ(d6d.getZ()  + offsetRot.getZ());
//		        d6d.setX(d6d.getX()- Double.parseDouble(chunks[0]));
//		        d6d.setY(d6d.getY()- Double.parseDouble(chunks[1]));
//		        d6d.setZ(d6d.getZ()- Double.parseDouble(chunks[2]));
		        d6d = TrackingUtility.Room2NormCoordinates(Optitrack.class, d6d);*/
//		        System.out.println("d6dm0 " + d6d.getM00() + " \t " + d6d.getM01() + " \t " + d6d.getM02());
//		        System.out.println("d6dm1 " + d6d.getM10() + " \t " + d6d.getM11() + " \t " + d6d.getM12());
//		        System.out.println("d6dm2 " + d6d.getM20() + " \t " + d6d.getM21() + " \t " + d6d.getM22());
//		        System.out.println("");
		        //System.out.println(d6d.getM20()+"\t"+d6d.getM22()+"\t"+d6d.getM21()+"\t"+displayNorm.x+"\t"+displayNorm.y+"\t"+displayNorm.z);
		       // System.out.println(d6d.getAttribute(TrackingConstant.OBJECTHEIGHT));
		        //System.out.println(d6d.getYaw()+"\t"+d6d.getPitch()+"\t"+d6d.getRoll());
		    	return d6d;
			}
		}
		return null;
    }
}


// * 		m6d = mu.rotateMatrix(MathUtility.Y_AXIS, null, (yVec.angle(displayNorm)));
//		m6d = mu.rotateMatrix(MathUtility.Z_AXIS, m6d, (zVec.angle(displayNorm)));
//		m6d = mu.rotateMatrix(MathUtility.X_AXIS, m6d, (xVec.angle(displayNorm)));
//        m6d = mu.transpose(m6d);
//		m6d2 = mu.rotateMatrix(MathUtility.Y_AXIS, null, (yVec.angle(n1)));
//		m6d2 = mu.rotateMatrix(MathUtility.Z_AXIS, m6d2, (zVec.angle(n1)));
//		m6d2 = mu.rotateMatrix(MathUtility.X_AXIS, m6d2, (xVec.angle(n1)));
//		
//		/*System.out.println("m6 " + m6d[0][0] +"\t"+ m6d[0][1] + "\t"+ m6d[0][2]);
//		System.out.println("m6 " + m6d[1][0] +"\t"+ m6d[1][1] + "\t"+ m6d[1][2]);
//		System.out.println("m6 " + m6d[2][0] +"\t"+ m6d[2][1] + "\t"+ m6d[2][2]);
//        System.out.println();
//		System.out.println("m6 " + m6d2[0][0] +"\t"+ m6d2[0][1] + "\t"+ m6d2[0][2]);
//		System.out.println("m6 " + m6d2[1][0] +"\t"+ m6d2[1][1] + "\t"+ m6d2[1][2]);
//		System.out.println("m6 " + m6d2[2][0] +"\t"+ m6d2[2][1] + "\t"+ m6d2[2][2]);*/
//		
//		m6d3[0][0] = m6d[0][0]*m6d2[0][0]+ m6d[0][1]*m6d2[1][0]+ m6d[0][2]*m6d2[2][0];
//		m6d3[0][1] = m6d[0][0]*m6d2[0][1]+ m6d[0][1]*m6d2[1][1]+ m6d[0][2]*m6d2[2][1];
//		m6d3[0][2] = m6d[0][0]*m6d2[0][2]+ m6d[0][1]*m6d2[1][2]+ m6d[0][2]*m6d2[2][2];
//
//		m6d3[1][0] = m6d[1][0]*m6d2[0][0]+ m6d[1][1]*m6d2[1][0]+ m6d[1][2]*m6d2[2][0];
//		m6d3[1][1] = m6d[1][0]*m6d2[0][1]+ m6d[1][1]*m6d2[1][1]+ m6d[1][2]*m6d2[2][1];
//		m6d3[1][2] = m6d[1][0]*m6d2[0][2]+ m6d[1][1]*m6d2[1][2]+ m6d[1][2]*m6d2[2][2];
//
//		m6d3[2][0] = m6d[2][0]*m6d2[0][0]+ m6d[2][1]*m6d2[1][0]+ m6d[2][2]*m6d2[2][0];
//		m6d3[2][1] = m6d[2][0]*m6d2[0][1]+ m6d[2][1]*m6d2[1][1]+ m6d[2][2]*m6d2[2][1];
//		m6d3[2][2] = m6d[2][0]*m6d2[0][2]+ m6d[2][1]*m6d2[1][2]+ m6d[2][2]*m6d2[2][2];		
//        m6d3 = mu.transpose(m6d3);
//		System.out.println("m6 " + m6d3[0][0] +"\t"+ m6d3[0][1] + "\t"+ m6d3[0][2]);
//		System.out.println("m6 " + m6d3[1][0] +"\t"+ m6d3[1][1] + "\t"+ m6d3[1][2]);
//		System.out.println("m6 " + m6d3[2][0] +"\t"+ m6d3[2][1] + "\t"+ m6d3[2][2]);
//        System.out.println();		
//		//m6d = mu.rotateMatrix(null, xVec.angle(displayNorm), yVec.angle(displayNorm), zVec.angle(displayNorm));
//		//m6d2 = mu.rotateMatrix(null, xVec.angle(n2), yVec.angle(n2), zVec.angle(n2));
//		//m6d = mu.rotateMatrix(m6d, xVec.angle(n1), yVec.angle(n1), zVec.angle(n1));
//		
//		
//
//	    double[] p1 = {0,0,1};
//	    double[] p2 = {0,1,0};
//	    double[] o1 = {0,0,0};
//
////	    System.out.println();
////		System.out.println("m6 " + m6d[0][0] +"\t"+ m6d[0][1] + "\t"+ m6d[0][2]);
////		System.out.println("m6 " + m6d[1][0] +"\t"+ m6d[1][1] + "\t"+ m6d[1][2]);
////		System.out.println("m6 " + m6d[2][0] +"\t"+ m6d[2][1] + "\t"+ m6d[2][2]);
//		
//	    //m6d = mu.transpose(m6d);
//
//		Matrix3d m3d = new Matrix3d();
//		Matrix3d m3d2 = new Matrix3d();
//		m3d.m00 = 1;
//		m3d.m11 = 1;
//		m3d.m22 = 1;
//		m3d2 = m3d;
//		
//		m3d.rotY(yVec.angle(displayNorm));
//		m3d.rotZ(zVec.angle(displayNorm));
//		//System.out.println("m3d\n " + m3d);
//		//m3d.rotX(xVec.angle(displayNorm));
//		m3d2.rotZ(zVec.angle(displayNorm));
//		//System.out.println("m3d2\n " + m3d2);
//	    f1 = mu.rotatePoint(p1, o1, m6d3, false);
//	    //System.out.println("f1 " + (f1[0]) + "\t"+(f1[1])+"\t"+(f1[2]));
//	    Vector3d out3d = new Vector3d(-f1[2],f1[0],f1[2]);
//		System.out.println("displaynorm " + displayNorm.toString());
//		System.out.println();	    
//	    System.out.println("out3d " + out3d.toString());
//	        
//	    System.out.println("");

