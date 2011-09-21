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

package org.squidy.nodes.optitrack;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.*;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.MouseIO;
import org.squidy.nodes.optitrack.RoomObject.RBIDDomainProvider;
import org.squidy.nodes.optitrack.cameraInterface.TrackingToolsJNI;
import org.squidy.nodes.optitrack.utils.TrackingConstant;
import org.squidy.nodes.optitrack.utils.TrackingUtility;


/*<code>Optitrack</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AM
* </pre>
* 
* @author Simon Faeh, <a href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de<a/>, University of Konstanz
* @version 27.10.2010 / sf 
*/
@XmlType(name = "TrackingTool")
@Processor(
	name = "Optitrack TT",
	icon = "/org/squidy/nodes/image/48x48/optitrack48.png",
	description = "Camera interaface for Optitrack Trackingsystem",
	types = {Processor.Type.OUTPUT},
	tags = { "optitrack", "camera control", "trackingtool" },
	status = Status.UNSTABLE
)

public class TrackingTool extends AbstractNode {
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "file-path")
	@Property(
		name = "Path to TTP Folder",
		description = "Path to folder with the Calibration Files"
	)
	@TextField
	private String filePath = "D:\\Development\\Squidy\\squidy-extension-basic\\ext\\optitrack\\";
	
	/**
	 * @return the filePath
	 */
	public final String getFilePath() {
		return filePath;
	}

	/**
	 * @param projectFile to set
	 */
	public final void setFilePath(String aProjectFile) {
		this.filePath = aProjectFile;
	}
	
	// ################################################################################
		
	@XmlAttribute(name = "project-file")
	@Property(
		name = "TrackingTool Project File",
		description = "The path to the project file. (*.ttp)"
	)
	@TextField
	private String projectFile = "TT23.ttp";
	
	/**
	 * @return the projectFile
	 */
	public final String getProjectFile() {
		return projectFile;
	}

	/**
	 * @param projectFile to set
	 */
	public final void setProjectFile(String aProjectFile) {
		this.projectFile = aProjectFile;
	}
	
	// ################################################################################
	
	
	@XmlAttribute(name = "room-dimensions")
	@Property(
		name = "Dimension of the tracked Area",
		description = "Set the Dimension of the tracked Area in mm (x,y,z)"
	)
	@TextField
	private String roomDimension = "6000,3000,6000";
	
	/**
	 * @return roomDimension
	 */
	public final String getRoomDimension() {
		return roomDimension;
	}

	/**
	 * @param roomDimesiom [x,y,z]
	 */
	public final void setRoomDimension(String aRoomDimension) {
		this.roomDimension = aRoomDimension;
	}

	// ################################################################################
	
	@XmlAttribute(name = "centerOffset")
	@Property(
		name = "Virtual Origin-Offset",
		description = "Set the virtual Origin-Offset as set by the Tracking-System (in mm)"
	)
	@TextField
	private String centerOffset = "0,0,0";
	
	/**
	 * @return centerOffset 
	 */
	public final String getCenterOffset() {
		return centerOffset;
	}

	/**
	 * @param centerOffset [x,y,z]
	 */
	public final void setCenterOffset(String aCenterOffset) {
		this.centerOffset = aCenterOffset;
	}

	// ################################################################################
	
	@XmlAttribute(name = "maximumTargetExtend")
	@Property(
		name = "Max Target Extend",
		description = "Set the maximal target size (in mm)"
	)
	@TextField
	private int maxTargetExtend = 200;
	
	public final int getMaxTargetExtend() {
		return maxTargetExtend;
	}

	public final void setMaxTargetExtend(int mte) {
		this.maxTargetExtend = mte;
	}
	// ################################################################################
	
	@XmlAttribute(name = "sendoption-allMarkers")
	@Property(name = "Send all Markers", description = "Sending each Framemarker as seperate DataPosition3D")
	@CheckBox
	private boolean sendoptionAllMarkers = false;

	/**
	 * @return sendoptionAllMarkers 
	 */
	public boolean isSendoptionAllMarkers() {
		return sendoptionAllMarkers;
	}
	
	/**
	 * @param sendoptionAllMarkers
	 */
	public void setSendoptionAllMarkers(boolean allMarkers) {
		this.sendoptionAllMarkers = allMarkers;
	}
	// ################################################################################
	
	@XmlAttribute(name = "sendoption-rigidBody")
	@Property(name = "Send Rigid Bodies", description = "Send Rigid Bodies as DataPosition6D")
	@CheckBox
	private boolean sendoptionRigidBodies = true;

	/**
	 * @return sendoptionRigidBodies 
	 */	
	public boolean isSendoptionRigidBodies() {
		return sendoptionRigidBodies;
	}

	/**
	 * @param sendoptionRigidBodies
	 */
	public void setSendoptionRigidBodies(boolean allMarkers) {
		this.sendoptionRigidBodies = allMarkers;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "sendoption-addBodyMarkers")
	@Property(name = "Send additional Markers", description = "Send all markers inside the radius set by maximum target extension with each Rigid Body")
	@CheckBox
	private boolean sendoptionAdditionalMarkers = true;

	/**
	 * @return sendoptionAdditionalMarkers 
	 */
	public boolean isSendoptionAdditionalMarkers() {
		return sendoptionAdditionalMarkers;
	}

	/**
	 * @param sendoptionAdditionalMarkers
	 */
	public void setSendoptionAdditionalMarkers(boolean allMarkers) {
		this.sendoptionAdditionalMarkers = allMarkers;
	}	
	
	// ################################################################################
	
	@XmlAttribute(name = "Trackables")
	@Property(
		name = "Trackables",
		description = "Load Specify Trackable definitions"
	)
	@ComboBox(domainProvider = TrackableDomainProvider.class)
	private int trackables = TrackingConstant.EMPTYTRACKABLES;
	public final int getTrackables() {
		return trackables;
	}

	public final void setTrackables(int rID) {
		this.trackables = rID;
		setupCameras();
	}
	
	@XmlAttribute(name = "fps")
	@Property(
		name = "Fps in ProjectSetting",
		description = "Specify the frame / seconds the cameras are running at"
	)
	@TextField
	private int framePS = 100;
	
	/**
	 * @return the filePath
	 */
	public final int getFramePS() {
		return framePS;
	}

	/**
	 * @param projectFile to set
	 */
	public final void setFramePS(int framePS) {
		this.framePS = framePS;
	}
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	
	
	
	// ################################################################################
	// BEGIN OF DOMAIN PROVIDERS
	// ################################################################################

	public static class TrackableDomainProvider implements DomainProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[5];
			values[0] = new ComboBoxItemWrapper(TrackingConstant.PRIME_IPHONE, "Prime iPhone");
			values[1] = new ComboBoxItemWrapper(TrackingConstant.PRIME_LASER, "Prime LaserPointer");
			values[2] = new ComboBoxItemWrapper(TrackingConstant.GLOVES, "Gloves");
			values[3] = new ComboBoxItemWrapper(TrackingConstant.EMPTYTRACKABLES, "unload Trackables");
			values[4] = new ComboBoxItemWrapper(TrackingConstant.ALLTRACKABLES, "All Tackables");
			return values;
		}
	}
	
	// ################################################################################
	// END OF DOMAIN PROVIDERS
	// ################################################################################	
			
	private boolean isLooping;
	private int frameCounter;
	private MathUtility mu = new MathUtility();
	private double[][] m6d = new double[3][3];
	private double[] f = new double[3];
	private double[] o = new double[3];
	private double[] t = new double[3];
	
	private String[] dimensionChunks;
	private double maxX, maxY, maxZ;
	private double centerOffX, centerOffY, centerOffZ;
	private ArrayList<String> trackableList;
	long sWatch;
	
	private void setupCameras()
	{
		boolean wasLooping = isLooping;
		if (isLooping)
			onStop();
		trackableList = new ArrayList<String>();
		switch(trackables)
		{
			case TrackingConstant.PRIME_IPHONE : 
			{
				trackableList.add("iPhone.tra");
				break;
			}
			case TrackingConstant.PRIME_LASER : 
			{
				trackableList.add("LaserPointer.tra");
				break;
			}
			case TrackingConstant.GLOVES : 
			{
				break;
			}
			case TrackingConstant.EMPTYTRACKABLES : 
			{
				break;
			}
			case TrackingConstant.ALLTRACKABLES : 
			{
				break;
			}
		}
		if (wasLooping)
			onStart();
	}
			
	
	@Override
	public void onStart() {
	    setupCameras();
		new Thread() {
			
			public void run() {
				dimensionChunks = roomDimension.split(",");
				maxX = Double.parseDouble(dimensionChunks[0]);
				maxY = Double.parseDouble(dimensionChunks[1]);
				maxZ = Double.parseDouble(dimensionChunks[2]);
				dimensionChunks = centerOffset.split(",");
				centerOffX = Double.parseDouble(dimensionChunks[0]);
				centerOffY = Double.parseDouble(dimensionChunks[1]);
				centerOffZ = Double.parseDouble(dimensionChunks[2]);				
				
				int ttInit = TrackingToolsJNI.TT_Initialize();
				if (ttInit != 11)
				{
					if (TrackingToolsJNI.TT_LoadProject(filePath +  projectFile) != 0)
						return;
					if (trackableList != null)
					{
						for (String t : trackableList)
						{
							TrackingToolsJNI.TT_LoadTrackables(filePath + t);	
							try {
								sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					isLooping = true;
					sWatch = System.currentTimeMillis();
					GetFrames();	
				}
			}
		}.start();
	}

	@Override
	public void onStop() {
		isLooping = false;
		TrackingToolsJNI.TT_ShutDown();
	}	
	/**
	 * GetFrames() : Retrieves framedata from the tracking cameras and publishes 3D markerData and 6D RigidbodyData
	 * @param void
	 * @return void
	 */
	private void GetFrames()
	{
	    int j, rID = 0;
	    double x,y,z,qx,qy,qz,qw;
	    float[] rbData = new float[10];
	    float[] rbMarker = new float[10];
	    boolean markerFound;
	    ArrayList<DataPosition3D> additionalMarker = new ArrayList<DataPosition3D>();
	    DataPosition6D d6d;
	    DataPosition3D d3d;
	    frameCounter = 0;
	    long currentTime;
	    currentTime = System.currentTimeMillis();
		while(isLooping)
		{
			// Retrieve Frames at 100fps
			sWatch = System.currentTimeMillis() - sWatch;
			
			double fpsWatch = (1.0 / framePS) * 1000.0;
//			System.out.print(fpsWatch + " " + sWatch);
			if (sWatch < fpsWatch)
			{
				try 
				{
					
					Thread.sleep((long)fpsWatch-sWatch);
//					System.out.print(" \tsleep " + (fpsWatch - sWatch));
				} catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
			sWatch = System.currentTimeMillis();
			
			TrackingToolsJNI.TT_Update();
//			System.out.println("time " + (System.currentTimeMillis()- currentTime));
			currentTime = System.currentTimeMillis();
			
			if (frameCounter++ == Integer.MAX_VALUE)
				frameCounter = 1;
			
			if (this.sendoptionAllMarkers)
			{
				j = TrackingToolsJNI.TT_FrameMarkerCount();
				for (int a = 0; a < j; a++)
				{
					x = TrackingToolsJNI.TT_FrameMarkerX( a ) * 1000;
					y = TrackingToolsJNI.TT_FrameMarkerY( a ) * 1000;
					z = TrackingToolsJNI.TT_FrameMarkerZ( a ) * 1000;
					d3d = new DataPosition3D(Optitrack.class,
							TrackingUtility.Room2NormPoint(x, maxX, centerOffX),
							TrackingUtility.Room2NormPoint(y, maxY, centerOffY),
							TrackingUtility.Room2NormPoint(z, maxZ, centerOffZ),
							frameCounter);
					d3d.setAttribute(DataConstant.GROUP_ID, frameCounter);
					d3d.setAttribute(DataConstant.IDENTIFIER,"" + a);
					d3d.setAttribute(DataConstant.GROUP_DESCRIPTION, "SINGLEMARKER");
					d3d.setAttribute(DataConstant.MAX_X, maxX);
					d3d.setAttribute(DataConstant.MAX_Y, maxY);
					d3d.setAttribute(DataConstant.MAX_Z, maxZ);
					d3d.setAttribute(DataConstant.CenterOffset_X, centerOffX);
					d3d.setAttribute(DataConstant.CenterOffset_Y, centerOffY);
					d3d.setAttribute(DataConstant.CenterOffset_Z, centerOffZ);
					publish(d3d);
				}
			}
			if (this.sendoptionRigidBodies)
			{
				j = TrackingToolsJNI.TT_TrackableCount();
				for (int trackable = 0; trackable < j; trackable++)
				{
					if (TrackingToolsJNI.TT_IsTrackableTracked(trackable))
					{
						TrackingToolsJNI.TT_TrackableLocation(trackable, rbData);
						rID = TrackingToolsJNI.TT_TrackableID(trackable);				

	  					//Read x,y,z and convert to mm
	  					x = rbData[0]*1000; 
	  					y = rbData[1]*1000;
	  					z = rbData[2]*1000;
	  					//Read quaternions
	  					qx = rbData[3];
	  					qy = rbData[4];
	  					qz = rbData[5];
	  					qw = rbData[6];
	  					Quat4d q4d = new Quat4d(rbData[3], rbData[4], rbData[5], rbData[6]);
	  					Matrix3d m3d = new Matrix3d();
	  					m3d.set(q4d);

	  					//System.out.println(x+"\t"+y+"\t"+z);
						// calculate rotation matrix (unusual version by S.Foehrenbach)
						m6d[0][0] = 2*(qx*qx + qw*qw)-1;
						m6d[0][1] = 2*(qx*qy - qz*qw);
						m6d[0][2] = 2*(qx*qz + qy*qw);

						m6d[1][0] = 2*(qx*qy + qz*qw);
						m6d[1][1] = 2*(qy*qy + qw*qw)-1;
						m6d[1][2] = 2*(qy*qz - qx*qw);

						m6d[2][0] = 2*(qx*qz - qy*qw);
						m6d[2][1] = 2*(qy*qz + qx*qw);
						m6d[2][2] = 2*(qz*qz + qw*qw)-1;
	  					//System.out.println(m6d[2][0] +"\t"+ m6d[2][1] +"\t"+ m6d[2][2]);
//						Rotationsmatrix transponieren (-> ergibt inverse, da zurückrotiert werden muss)
						m6d = mu.transpose(m6d);
						
		    			additionalMarker.removeAll(additionalMarker);
		    			if (this.sendoptionAdditionalMarkers)
		    			{
		    				for (int frameMarker = 0; frameMarker < TrackingToolsJNI.TT_FrameMarkerCount(); frameMarker++)
		    				{
				            	markerFound = false;
//					            for(int l = 0; l< TrackingToolsJNI.TT_TrackableMarkerCount(trackable); l++)
//					            {
//			    					t[0] = TrackingToolsJNI.TT_FrameMarkerX( frameMarker );// * 1000;
//			    				    t[1] = TrackingToolsJNI.TT_FrameMarkerY( frameMarker );// * 1000;
//			    					t[2] = TrackingToolsJNI.TT_FrameMarkerZ( frameMarker );// * 1000;
//			    					//System.out.println(t[0]*1000 + "\t " + frameMarker);
//					            	TrackingToolsJNI.TT_TrackableMarker(trackable, l, rbMarker);
//					            	o[0] = x/1000;
//					            	o[1] = y/1000;
//					            	o[2] = z/1000;		            	
//					            	
//					            	
//					            	f = mu.rotatePoint(t, o, m6d, false);
//					            	if (mu.euclidDist(f[0], f[1], f[2], rbMarker[0], rbMarker[1], rbMarker[2]) < 0.02)
//					            		markerFound = true;
//
//					            }
					            if(!markerFound)
					            {
			    					/*
			    					 * Uncomment the following 3 lines to send handcoordinates instead of room coordinates
			    					 */
			    					t[0] = TrackingToolsJNI.TT_FrameMarkerX( frameMarker) * 1000;
			    				    t[1] = TrackingToolsJNI.TT_FrameMarkerY( frameMarker) * 1000;
			    					t[2] = TrackingToolsJNI.TT_FrameMarkerZ( frameMarker) * 1000;	
			    					if (mu.euclidDist(t[0],t[1], t[2], x,y,z) < this.maxTargetExtend)
			    					{
						            	//System.out.println("t02 "+ t[2]*1000);
						            	d3d = new DataPosition3D(Optitrack.class,
												TrackingUtility.Room2NormPoint(t[0], maxX, centerOffX),
												TrackingUtility.Room2NormPoint(t[1], maxY, centerOffY),
												TrackingUtility.Room2NormPoint(t[2], maxZ, centerOffZ),
												frameCounter);
				    					d3d.setAttribute(DataConstant.IDENTIFIER, "" + frameMarker);
				    					d3d.setAttribute(DataConstant.GROUP_ID, frameCounter);
				    					d3d.setAttribute(DataConstant.GROUP_DESCRIPTION, "ADDITIONALMARKER");
				    					d3d.setAttribute(DataConstant.MAX_X, maxX);
				    					d3d.setAttribute(DataConstant.MAX_Y, maxY);
				    					d3d.setAttribute(DataConstant.MAX_Z, maxZ);	
				    					d3d.setAttribute(DataConstant.CenterOffset_X, centerOffX);
				    					d3d.setAttribute(DataConstant.CenterOffset_Y, centerOffY);
				    					d3d.setAttribute(DataConstant.CenterOffset_Z, centerOffZ);
				    					additionalMarker.add(d3d);
			    					}				    					
			    					//System.out.println("d3d "+d3d.getZ());
			    					//System.out.println("r2d "+TrackingUtility.Norm2RoomCoordinates(Optitrack.class, d3d).getZ());
		    					}
					        	
		    				}
			            }
                        //System.out.println(additionalMarker.size()+"\t"+TrackingToolsJNI.TT_FrameMarkerCount() +"\t"+(TrackingToolsJNI.TT_FrameMarkerCount()-additionalMarker.size()));				
		    			m6d = mu.transpose(m6d);
						d6d = new DataPosition6D(MouseIO.class,
								TrackingUtility.Room2NormPoint(x, maxX, centerOffX),
								TrackingUtility.Room2NormPoint(y, maxY, centerOffY),
								TrackingUtility.Room2NormPoint(z, maxZ, centerOffZ),
								m6d[0][0], m6d[0][1], m6d[0][2], m6d[1][0], m6d[1][1], m6d[1][2],
								m6d[2][0], m6d[2][1], m6d[2][2] ,rbData[7],rbData[8],rbData[9], frameCounter);
						d6d.setAttribute(DataConstant.IDENTIFIER,"" + rID);
						d6d.setAttribute(DataConstant.GROUP_ID, frameCounter);
						d6d.setAttribute(DataConstant.GROUP_DESCRIPTION, "RIGIDBODY");
						d6d.setAttribute(DataConstant.MAX_X, maxX);
						d6d.setAttribute(DataConstant.MAX_Y, maxY);
						d6d.setAttribute(DataConstant.MAX_Z, maxZ);
						d6d.setAttribute(DataConstant.CenterOffset_X, centerOffX);
						d6d.setAttribute(DataConstant.CenterOffset_Y, centerOffY);
						d6d.setAttribute(DataConstant.CenterOffset_Z, centerOffZ);
						
						//System.out.println(x + " " + y + " " + z);
						//System.out.println(d6d.getX()+"\t"+d6d.getY()+"\t"+d6d.getZ());
						if (!this.sendoptionAdditionalMarkers)
						{
							publish(d6d);
						}
						else
						{
							List<IData> publishRigidBodies;
							publishRigidBodies = new ArrayList<IData>();
							publishRigidBodies.add(d6d);
							for (DataPosition3D  dataPosition3D : additionalMarker)
							{
								publishRigidBodies.add(dataPosition3D);
							}
							publish(publishRigidBodies);
						}		  	
					}		  
				}	
			}			    
		}
	}
}
