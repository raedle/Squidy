package org.squidy.nodes.optitrack.cameraInterface;

public class TrackingToolsJNI {
	static {
		/**
		 * External dependencies for Optirack
		 * squidy-extension-basic/ext/optitrack
		 */
		System.loadLibrary("ext/optitrack/NPTrackingTools");
		System.loadLibrary("ext/optitrack/TTJNI");
	}
	
	/* Tracking Tool Library */ 
	public static native int TT_Initialize();
	public static native int TT_ShutDown();
	
	/* Trackking Tool Interface */
	public static native int TT_LoadCalibration( String name );
	public static native int TT_LoadTrackables( String name );
	public static native int TT_LoadProject( String name );
	public static native int TT_Update();
	public static native int TT_UpdateSingleFrame();	
	
	/* Tracking Tools Frame */
	public static native int TT_FrameMarkerCount();
	public static native float TT_FrameMarkerX( int index );
	public static native float TT_FrameMarkerY( int index );
	public static native float TT_FrameMarkerZ( int index );
	public static native double TT_FrameTimeStamp();   
	
	public static native boolean TT_IsTrackableTracked( int index);
	public static native int TT_TrackableCount();
	public static native int TT_TrackableID( int index );
	public static native char TT_TrackableName( int index );
	public static native void TT_TrackableLocation( int index, float[] data);
	public static native int TT_TrackableMarkerCount( int index);
	public static native void TT_TrackableMarker( int RigidIndex, int MarkerIndex, float [] data);
	public static native void TT_SetTrackableEnabled(int RigidIndex, boolean enabled);
	public static native boolean TT_TrackableEnabled(int RigidIndex);
		
}
