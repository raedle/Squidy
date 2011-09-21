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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import javax.vecmath.*;

import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.optitrack.cameraInterface.OptitrackJNI;


/*<code>Optitrack</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AM
* </pre>
* 
* @author Simon Faeh, <a href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de<a/>, University of Konstanz
* 
* @version 
*/
@XmlType(name = "Optitrack RB")
@Processor(
	name = "Optitrack System",
	icon = "/org/squidy/nodes/image/48x48/optitrack.png",
	description = "",
	types = {Processor.Type.OUTPUT},
	tags = { "optitrack", "handtracking", "base" }
)

public class Optitrack extends AbstractNode {

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	@XmlAttribute(name = "calibration-file")
	@Property(
		name = "Calibration File",
		description = "The path to the calibration file."
	)
	@TextField
	private String calibrationFile = "D:\\Development\\Optitrack\\TrackingToolProjects\\Cal8CamSetting.cal";
	
	/**
	 * @return the multicastGroupAddress
	 */
	public final String getCalibrationFile() {
		return calibrationFile;
	}

	/**
	 * @param multicastGroupAddress the multicastGroupAddress to set
	 */
	public final void setCalibrationFile(String aCalibration) {
		this.calibrationFile = aCalibration;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "definition-file")
	@Property(
		name = "Definition File",
		description = "The path to the rigidbody definition file."
	)
	@TextField
	private String definitionFile = "D:\\Development\\Optitrack\\TrackingToolProjects\\Stift4MNF6Cam.rdef";
	
	/**
	 * @return the multicastGroupAddress
	 */
	public final String getDefinitionFile() {
		return calibrationFile;
	}

	/**
	 * @param multicastGroupAddress the multicastGroupAddress to set
	 */
	public final void setDefinitionFile(String aCalibration) {
		this.definitionFile = aCalibration;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "sendoption-allMarkers")
	@Property(name = "Send all Markers", description = "Sending each Framemarker as seperate 3D-Data")
	@CheckBox
	private boolean sendoptionAllMarkers = false;

	public boolean isSendoptionAllMarkers() {
		return sendoptionAllMarkers;
	}

	public void setSendoptionAllMarkers(boolean allMarkers) {
		this.sendoptionAllMarkers = allMarkers;
	}
	// ################################################################################
	
	@XmlAttribute(name = "sendoption-rigidBody")
	@Property(name = "Send Rigid Bodies", description = "Send Rigid Bodies")
	@CheckBox
	private boolean sendoptionRigidBodies = false;

	public boolean isSendoptionRigidBodies() {
		return sendoptionRigidBodies;
	}

	public void setSendoptionRigidBodies(boolean allMarkers) {
		this.sendoptionRigidBodies = allMarkers;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "sendoption-addBodyMarkers")
	@Property(name = "Send additional Markers", description = "Send all remaining Markers with each Rigid Body")
	@CheckBox
	private boolean sendoptionAdditionalMarkers = false;

	public boolean isSendoptionAdditionalMarkers() {
		return sendoptionAdditionalMarkers;
	}

	public void setSendoptionAdditionalMarkers(boolean allMarkers) {
		this.sendoptionAdditionalMarkers = allMarkers;
	}	
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	
	
	private boolean isLooping;
	private boolean isInitialized;
	private int frameCounter;
	

	@Override
	public void onStart() {
	    
		new Thread() {
			
			public void run() {
				int s = OptitrackJNI.RB_InitalizeRigidBody();
				System.out.println("Init " + s);
				int kj = OptitrackJNI.RB_LoadProfile(calibrationFile);
				System.out.println("Profil " + kj);
				int j = OptitrackJNI.RB_LoadDefinition(definitionFile);
				System.out.println("Definition " + j);
				isLooping = true;
				j = OptitrackJNI.RB_StartCameras();
				System.out.println("Start " + j);
				GetFrames();
			}
		}.start();
	}

	@Override
	public void onStop() {
		isLooping = false;
		int j = OptitrackJNI.RB_StopCameras();
	    System.out.println("Stopp Cameras"+j);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    OptitrackJNI.RB_ShutdownRigidBody();
	    System.out.println("Shutdown Tracking"+j);
		
		
	}	
	
	private void GetFrames()
	{
	    int j, rID, mCount = 0;
	    double x,y,z,qx,qy,qz,qw;
	    double rxx,rxy,rxz,ryx,ryy,ryz,rzx,rzy,rzz;
	    float[] rbData = new float[10];
	    float[] rbMData = new float[5];
	    ArrayList<DataPosition3D> additionalMarker = new ArrayList<DataPosition3D>();
	    ArrayList<DataPosition6D> rigidBodyList = new ArrayList<DataPosition6D>();
	    DataPosition6D d6d;
		while(isLooping)
		{
			
			{
				OptitrackJNI.RB_GetNextFrame();
				frameCounter++;
				/*j = OptitrackJNI.RB_FrameMarkerCount();
				for (int a = 0; a < j; a++)
				{
					x = OptitrackJNI.RB_FrameMarkerX( a );
					y = OptitrackJNI.RB_FrameMarkerY( a );
					z = OptitrackJNI.RB_FrameMarkerZ( a );
					//publish(new DataPosition3D(Optitrack.class,x,y,z,4000,4000,4000,frameCountr,j););
					//additionalMarker.add(d3d);
				}*/
				
				
				j = OptitrackJNI.RB_GetRigidBodyCount();
				for (int a = 0; a < j; a++)
				{
					if (OptitrackJNI.RB_IsRigidBodyTracked(a))
					{
						OptitrackJNI.RB_GetRigidBodyLocation(a, rbData);
						rID = OptitrackJNI.RB_GetRigidBodyID(a);				
			
	  					//Read x,y,z and convert to mm
	  					x = rbData[0]*1000; 
	  					y = rbData[1]*1000;
	  					z = rbData[2]*1000;
	  					
	  					//Read quatrions
	  					qx = rbData[3];
	  					qy = rbData[4];
	  					qz = rbData[5];
	  					qw = rbData[6];

	  					
	  				   double wh = qw;
	  				   double xh = qx;
	  				   double yh = qy;
	  				   double zh = qz;
	  				   
	  				   double p1x,p1y,p1z;
	  				   p1x = 0;
	  				   p1y = 0;
	  				   p1z = 1;

	  				   //double resultx = wh*wh*p1x + 2*yh*wh*p1z - 2*zh*wh*p1y + xh*xh*p1x + 2*yh*xh*p1y    + 2*zh*xh*p1z - zh*zh*p1x - yh*yh*p1x;
	  				   //double resulty = 2*xh*yh*p1x + yh*yh*p1y + 2*zh*yh*p1z + 2*wh*zh*p1x - zh*zh*p1y    + wh*wh*p1y - 2*xh*wh*p1z - xh*xh*p1y;
	  				   //double resultz = 2*xh*zh*p1x + 2*yh*zh*p1y + zh*zh*p1z - 2*wh*yh*p1x - yh*yh*p1z    + 2*wh*xh*p1y - xh*xh*p1z + wh*wh*p1z;

	  				   
	  					
	  					//Transform to rotation matrix
	  					//Spalte 1:
						/*rxx = 1- 2*(qx*qx + qw*qw);
						ryx = 2*(qx*qy + qz*qw);
						rzx = 2*(qx*qz - qy*qw);
						//Spalte 2:
						rxy = 2*(qx*qy - qz*qw);
						ryy = 1- 2 * (qx*qx -qz*qz);
						rzy = 2*(qy*qz + qx*qw);
						//Spalte 3:
						rxz = 2*(qx*qz + qy*qw);
						ryz = 2*(qy*qz - qx*qw);
						rzz = 1- 2*(qx*qx - qy*qy);
						
						
						// von steffi
						rxx = 2*(qx*qx + qw*qw)-1;
						ryx = 2*(qx*qy + qz*qw);
						rzx = 2*(qx*qz - qy*qw);
						//Spalte 2:
						rxy = 2*(qx*qy - qz*qw);
						ryy = 2*(qy*qy + qw*qw)-1;
						rzy = 2*(qy*qz + qx*qw);
						//Spalte 3:
						rxz = 2*(qx*qz + qy*qw);
						ryz = 2*(qy*qz - qx*qw);
						rzz = 2*(qz*qz + qw*qw)-1;*/
	  				   
	  				   // DCM
	  				   double m00,m01,m02;
	  				   double m10,m11,m12;
	  				   double m20,m21,m22;
	  				   
	  				   m00 = 1 - 2 * (qy*qy - qz * qz);
	  				   m01 = 2 * (qx * qy - qz * qw);
	  				   m02 = 2 * (qx * qz + qy * qw);
	  				   
	  				   m10 = 2 * (qx * qy + qz * qw);
	  				   m11 = 1 - 2 * (qx * qx - qz * qz);
	  				   m12 = 2 * (qy * qz - qx * qw);
	  				   
	  				   m20 = 2 * (qx * qz - qy * qw);
	  				   m21 = 2 * (qy * qz + qx * qw);
	  				   m22 = 1 - 2 * (qx * qx - qy * qy);
						
					 //System.out.println(x+" "+y+" "+z+" "+rID);
	  				   
	  				 // x = 0
	  				 // y = 1
	  				 // z = 2
	  				 System.out.println((1 - (2 * qx * qx - 2 * qy * qy)));
	  				 publish(new DataPosition6D(Optitrack.class, x, y, z,
	  						                    m00, m10, m20, m01, m11, m21, m02, m12, m22,rbData[7],rbData[8],rbData[9], frameCounter));
						/*publish(new DataPosition6D(Optitrack.class, x, y, z, 6000.0,
	  							6000.0, 6000.0,rxx, ryx, rzx, rxy, ryy, rzy, rxz, ryz, rzz,resultx,resulty,resultz, rID,0));
	  					*/		
	  							  	
					}
	
	  						  
				}
				
				
				
				
				
				
				
				
				
				
				

				
				
				
				// get single markers
				/*j = OptitrackJNI.RB_FrameMarkerCount();
				/*for (int a = 0; a < j; a++)
				{
					x = OptitrackJNI.RB_FrameMarkerX( a );
					y = OptitrackJNI.RB_FrameMarkerY( a );
					z = OptitrackJNI.RB_FrameMarkerZ( a );
					DataPosition3D d3d = new DataPosition3D(Optitrack.class,x,y,z,1000,1000,1000,0,0);
					//publish(d3d);
					additionalMarker.add(d3d);
				}*/
				
				/*j = OptitrackJNI.RB_GetRigidBodyCount();
				for (int a = 0; a < j; a++)
				{
					OptitrackJNI.RB_GetRigidBodyLocation(a, rbData);
					//System.out.println(rbData);
					rID = OptitrackJNI.RB_GetRigidBodyID(a);
					mCount = OptitrackJNI.RB_FrameMarkerCount();
					//System.out.println(mCount);
					if (OptitrackJNI.RB_IsRigidBodyTracked(a));
					   //if(rbData[0] > 0.0)
					   {
						//System.out.println(rID+ " "+ rbData[0] + " "+ rbData[1] + " "+ rbData[2]  +" "+ rbData[7] + " "+ rbData[8] + " "+ rbData[9]);
						   //System.out.println(rbData[7] + " "+ rbData[8] + " "+ rbData[9]);
						   
						/*for (int m = 0; m < mCount; m++)
						{
							OptitrackJNI.RB_GetRigidBodyMarker(a, m, rbMData);
							for (int n = 0; n < additionalMarker.size(); n++)
							{
								if (rbMData[0] == additionalMarker.get(n).getX() &&
								    rbMData[1] == additionalMarker.get(n).getY() &&
								    rbMData[2] == additionalMarker.get(n).getZ())
								{
									additionalMarker.remove(n);
									break;
								}
							}
						}*/
					/*	d6d = new DataPosition6D(Optitrack.class, rID, rbData[0], rbData[1], rbData[2],
												                1000,1000,1000, 
												                rbData[3], rbData[4], rbData[5], rbData[6], 
												                rbData[7], rbData[8], rbData[9], 
												                0,0, mCount);
						//System.out.println(d6d.toString());
						publish(d6d);
						//rigidBodyList.add(d6d);	
				
					   }

				}
				/*
				List<IData> publishRigidBodies;
				for (DataPosition6D dataPosition6D : rigidBodyList)	{
					publishRigidBodies = new ArrayList<IData>();
					publishRigidBodies.add(dataPosition6D);
					publishRigidBodies.addAll(additionalMarker);
					
					publish(publishRigidBodies);
				}*/
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }			    
	}
}











/*
  					//Read x,y,z and convert to mm
  					x = rbData[0]*1000; 
  					y = -rbData[1]*1000;
  					z = rbData[2]*1000;
  					
  					//Read quatrions
  					qx = rbData[3];
  					qy = rbData[4];
  					qz = rbData[5];
  					qw = rbData[6];
  					
  					
				    double c1 = Math.cos(rbData[9]/2 * (Math.PI/180));
				    double s1 = Math.sin(rbData[9]/2 * (Math.PI/180));
				    double c2 = Math.cos(rbData[7]/2 * (Math.PI/180));
				    double s2 = Math.sin(rbData[7]/2 * (Math.PI/180));
				    double c3 = Math.cos(rbData[8]/2 * (Math.PI/180));
				    double s3 = Math.sin(rbData[8]/2 * (Math.PI/180));
				    double c1c2 = c1*c2;
				    double s1s2 = s1*s2;
				    double angle =c1c2*c3 + s1s2*s3;
				   
				   
				    double wh = angle;
				    double xh = c1c2*s3 - s1s2*c3;
				    double yh = c1*s2*c3 + s1*c2*s3;
				    double zh = s1*c2*c3 - c1*s2*s3;
				    double aax,aay,aaz;
  					
  					
				    /*qx = xh;
				    qy = yh;
				    qz = zh;
				    qw = wh;*/
				    
  					//Transform to rotation matrix
  					//Spalte 1:
/*					rxx = 1- 2*(qx*qx + qw*qw);
					ryx = 2*(qx*qy + qz*qw);
					rzx = 2*(qx*qz - qy*qw);
					//Spalte 2:
					rxy = 2*(qx*qy - qz*qw);
					ryy = 1- 2 * (qx*qx -qz*qz);
					rzy = 2*(qy*qz + qx*qw);
					//Spalte 3:
					rxz = 2*(qx*qz + qy*qw);
					ryz = 2*(qy*qz - qx*qw);
					rzz = 1- 2*(qx*qx - qy*qy);
					
					
					/*rxx = 2*(qx*qx + qw*qw)-1;
					ryx = 2*(qx*qy + qz*qw);
					rzx = 2*(qx*qz - qy*qw);
					//Spalte 2:
					rxy = 2*(qx*qy - qz*qw);
					ryy = 2*(qy*qy + qw*qw)-1;
					rzy = 2*(qy*qz + qx*qw);
					//Spalte 3:
					rxz = 2*(qx*qz + qy*qw);
					ryz = 2*(qy*qz - qx*qw);
					rzz = 2*(qz*qz + qw*qw)-1;*/
	    
				    
				    /*
				    double p1x = 0;
				    double p1y = 0;
				    double p1z = 1;
				   

				    aax = wh*wh*p1x + 2*yh*wh*p1z - 2*zh*wh*p1y + xh*xh*p1x + 2*yh*xh*p1y    + 2*zh*xh*p1z - zh*zh*p1x - yh*yh*p1x;
				    aay = 2*xh*yh*p1x + yh*yh*p1y + 2*zh*yh*p1z + 2*wh*zh*p1x - zh*zh*p1y    + wh*wh*p1y - 2*xh*wh*p1z - xh*xh*p1y;
				    aaz = 2*xh*zh*p1x + 2*yh*zh*p1y + zh*zh*p1z - 2*wh*yh*p1x - yh*yh*p1z    + 2*wh*xh*p1y - xh*xh*p1z + wh*wh*p1z;
					
				    publish(new DataPosition6D(Optitrack.class, x, y, z, 6000.0,
  							6000.0, 6000.0,rxx, ryx, rzx, rxy, ryy, rzy, rxz, ryz, rzz, rID,0));  	

*/