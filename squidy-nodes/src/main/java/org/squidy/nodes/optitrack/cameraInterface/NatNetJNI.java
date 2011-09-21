package org.squidy.nodes.optitrack.cameraInterface;

public class NatNetJNI {
	static {
		/**
		 * External dependencies for Optirack
		 * squidy-extension-basic/ext/optitrack
		 */
//		System.loadLibrary("ext/optitrack/NPTrackingTools");
//		System.loadLibrary("ext/optitrack/TTJNI");
	}
	  // Declare native methods.
	  public native void doSomething ();

	  // Declare static variables.
	  private static float a_static_float;

	  // Declare instance variables.
	  private int some_int;
	  private int[] array;
	  private int[][] array2d;
	  private String some_string;
	  //public MyCustomObject my_custom;
	  
	  // Load the native library.
	  static { System.loadLibrary ("ext/optitrack/NatNetClient"); }

	  /** Constructs the JNIDemo object and initializes variables. **/
	  public NatNetJNI () {
	    a_static_float = 4.0f;
	    some_int = 2;
	    array = new int[5];
	    array2d = new int[5][2];
	    some_string = "hello JNIDemo";
	   // my_custom = new MyCustomObject ();
	  } //ctor


	  /** This is the callback function called from the native code. It throws
	   * an exception that is caught in the native code. **/
	  private int callback (float x, float y, float z) throws java.io.IOException {
//	    if (x<0)
//	      throw new java.io.IOException ("fake io exception from Java");
//	    else
	    	System.out.println("yeah " + x+ " " +y + " " + z);
	    return (int)x/2;
	  } // callback
		
}
