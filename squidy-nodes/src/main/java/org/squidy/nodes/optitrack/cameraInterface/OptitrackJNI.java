package org.squidy.nodes.optitrack.cameraInterface;

public class OptitrackJNI {
	static {
		/**
		 * External dependencies for Optirack
		 * squidy-extension-basic/ext/optitrack
		 */
		System.loadLibrary("NatNetLib");
		System.loadLibrary("NPRigidBody");
		System.loadLibrary("RigidBodyJNI");
	}
	
	/* Rigid Body Library */
	public static native int RB_InitalizeRigidBody();
	public static native int RB_ShutdownRigidBody();
	
	/* Rigid Body Interface */
	public static native int RB_LoadProfile( String name );
	public static native int RB_LoadDefinition( String name );
	public static native int RB_StartCameras();
	public static native int RB_StopCameras();
	public static native int RB_GetLatestFrame();
	public static native int RB_GetNextFrame();	
	
	/* Rigid Body Frame */
	public static native int RB_FrameMarkerCount();
	public static native float RB_FrameMarkerX( int index );
	public static native float RB_FrameMarkerY( int index );
	public static native float RB_FrameMarkerZ( int index );
	
	public static native boolean RB_IsRigidBodyTracked( int index);
	public static native int RB_GetRigidBodyCount();
	public static native int RB_GetRigidBodyID( int index );
	public static native void RB_GetRigidBodyLocation( int index, float[] data);
	public static native int RB_GetRigidBodyMarkerCount( int index);
	public static native void RB_GetRigidBodyMarker( int RigidIndex, int MarkerIndex, float [] data);
		
}
