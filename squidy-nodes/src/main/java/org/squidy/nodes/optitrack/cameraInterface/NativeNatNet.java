package org.squidy.nodes.optitrack.cameraInterface;

import java.awt.Canvas;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.vecmath.Point3d;

import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.nodes.*;
import org.squidy.nodes.optitrack.Optitrack;
import org.squidy.nodes.optitrack.TTStreaming;
import org.squidy.nodes.optitrack.utils.TrackingConstant;


public class NativeNatNet extends Canvas {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 2586762717367712276L;
	
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public void addNotify() {
		super.addNotify();
		ref = create();
		System.out.println("r1 "+ref);
	}

	public void removeNotify() {
		dispose(ref);
		super.removeNotify();
	}
	
	public int ref = 0;
	private static TTStreaming ttStreaming;
	private static NatNetWindow natNetWindow;
	private static Point3d dimensions;
		
	native int create();
	native void dispose(int ref);

	
	public void setParents(TTStreaming tts,NatNetWindow wnd, Point3d dim)
	{
		ttStreaming = tts;
		natNetWindow = wnd;
		dimensions = dim;
	}
	
	public void stop()
	{
		dispose(ref);
	}
	
	public static void JPrintLine(String in)
	{
		System.out.println("NATNET " + in);
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
//			System.out.println();
//			if (System.currentTimeMillis() - lastFrame > 20)
//				System.out.println("LATENCY " + (System.currentTimeMillis() - lastFrame) + " " + frameID);
			lastFrame = System.currentTimeMillis();
		}else
		{
//			System.out.print(frameID+"\t");
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