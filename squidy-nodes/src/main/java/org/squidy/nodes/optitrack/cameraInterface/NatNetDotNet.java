package org.squidy.nodes.optitrack.cameraInterface;

import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.MouseIO;
import org.squidy.nodes.optitrack.Optitrack;
import org.squidy.nodes.optitrack.TTStreaming;
import org.squidy.nodes.optitrack.utils.TrackingConstant;
import org.squidy.nodes.optitrack.utils.TrackingUtility;


public class NatNetDotNet extends AbstractNode  {
	
	static{
		System.loadLibrary("/ext/speechrecognition/oojnidotnet");
		System.loadLibrary("/ext/optitrack/NatNetStreaming");
	}
	
	private static TTStreaming ttStreaming;
	private static double maxX, maxY, maxZ;
	
	native int create(int p1, int p2, int p3, int p4, int port, int s1, int s2, int s3, int s4);
	
	public NatNetDotNet(TTStreaming tts)
	{
		this.ttStreaming = tts;
		String host = "192.168.178.100";
		String parts[] = host.split(".");
		int port = 1234;
		
//		new Thread() {
//			public void run() {
				create(192,168,178,101,port,192,168,178,100);
//			};
//		}.start();
//		
//		System.out.println("im here");
	}
	
	public static void JPrintLine(String in)
	{
		if (in.equalsIgnoreCase("stop"))
		{
			System.err.println("STOPPPPPP");
		}
				
		System.out.println(in);
	}
	public static void JRigidBodyMarker(int id, int frameID, float x, float y, float z)
	{

		
	}	
	public static void JSingleMarker(int frameID, float x, float y, float z)
	{
		DataPosition3D d3d = new DataPosition3D(Optitrack.class, x/6000.0, y/3000.0, z/6000.0);
		d3d.setAttribute(DataConstant.GROUP_ID, frameID);
		d3d.setAttribute(TrackingConstant.NATNET, true);
		d3d.setAttribute(DataConstant.MAX_X, 6000.0);
		d3d.setAttribute(DataConstant.MAX_Y, 3000.0);
		d3d.setAttribute(DataConstant.MAX_Z, 6000.0);
		d3d.setAttribute(DataConstant.CenterOffset_X, 0.0);
		d3d.setAttribute(DataConstant.CenterOffset_Y, 0.0);
		d3d.setAttribute(DataConstant.CenterOffset_Z, 0.0);		
		ttStreaming.publish(d3d);
	}
	private static long lastFrame = 0;
	private static float currentFrame;
	public static void JRigidBody(int id, int frameID, float x, float y, float z, float qx, float qy, float qz, float qw, float yaw, float pitch, float roll)
	{
		double[][] m6d = new double[3][3];
		m6d[0][0] = 2*(qx*qx + qw*qw)-1;
		m6d[0][1] = 2*(qx*qy - qz*qw);
		m6d[0][2] = 2*(qx*qz + qy*qw);

		m6d[1][0] = 2*(qx*qy + qz*qw);
		m6d[1][1] = 2*(qy*qy + qw*qw)-1;
		m6d[1][2] = 2*(qy*qz - qx*qw);

		m6d[2][0] = 2*(qx*qz - qy*qw);
		m6d[2][1] = 2*(qy*qz + qx*qw);
		m6d[2][2] = 2*(qz*qz + qw*qw)-1;
		DataPosition6D d6d = new DataPosition6D(Optitrack.class,x/6000.0,y/3000.0,z/6000.0, 
												m6d[0][0], m6d[0][1], m6d[0][2], 
												m6d[1][0], m6d[1][1], m6d[1][2],
												m6d[2][0], m6d[2][1], m6d[2][2],
												yaw, pitch,roll,frameID);
		d6d.setAttribute(DataConstant.IDENTIFIER,"" + id);
		
		d6d.setAttribute(DataConstant.GROUP_ID, frameID);
		if (frameID != currentFrame)
		{
			System.out.println(System.currentTimeMillis() - lastFrame + " " + frameID);
			lastFrame = System.currentTimeMillis();
		}
		currentFrame = frameID;
		d6d.setAttribute(DataConstant.GROUP_DESCRIPTION, "RIGIDBODY");
		d6d.setAttribute(TrackingConstant.NATNET, true);
		d6d.setAttribute(DataConstant.MAX_X, 6000.0);
		d6d.setAttribute(DataConstant.MAX_Y, 3000.0);
		d6d.setAttribute(DataConstant.MAX_Z, 6000.0);
		d6d.setAttribute(DataConstant.CenterOffset_X, 0.0);
		d6d.setAttribute(DataConstant.CenterOffset_Y, 0.0);
		d6d.setAttribute(DataConstant.CenterOffset_Z, 0.0);
		ttStreaming.publish(d6d);
	}		
	

}
