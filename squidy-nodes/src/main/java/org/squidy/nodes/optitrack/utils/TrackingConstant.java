package org.squidy.nodes.optitrack.utils;

import org.squidy.manager.data.DataConstant;

public class TrackingConstant {
	public static final DataConstant RIGIDBODY = DataConstant.get(String.class, "RIGIDBODY");
	public static final DataConstant RIGIDBODYID = DataConstant.get(Integer.class, "RIGIDBODYID");
	public static final DataConstant ADDITIONALMARKER = DataConstant.get(String.class, "ADDITIONALMARKER");
	public static final DataConstant SINGLEMARKER = DataConstant.get(String.class, "SINGLEMARKER");
	public static final DataConstant Y_DEFLECTION = DataConstant.get(Double.class, "Y_DEFLECTION");
	public static final DataConstant GESTURE = DataConstant.get(String.class, "GESTURE");
	public static final DataConstant RIGIDBODYROLE = DataConstant.get(Integer.class, "RIGIDBODYROLE");
	public static final DataConstant POINTINGMODE = DataConstant.get(Integer.class, "POINTINGMODE");
	public static final DataConstant OBJECTWIDHT   = DataConstant.get(Double.class, "OBJECTWIDTH");
	public static final DataConstant OBJECTHEIGHT = DataConstant.get(Double.class, "OBJEGTHEITH");
	public static final DataConstant OBJECTDEPTH = DataConstant.get(Double.class, "OBJECTDEPTH");
	public static final DataConstant REMOTEOBJECT = DataConstant.get(String.class, "REMOTEOBJECT");
	public static final DataConstant FINGERINDEX = DataConstant.get(Integer.class, "FINGERINDEX");
	public static final DataConstant KEYWORD = DataConstant.get(String.class, "KEYWORD");
	public static final DataConstant SENDTUIO = DataConstant.get(String.class, "SENDTUIO");
	public static final DataConstant TUIOID = DataConstant.get(Integer.class, "TUIOID");
	public static final DataConstant GESTUREID = DataConstant.get(Integer.class, "GESTUREID");
	public static final DataConstant HANDSIDE = DataConstant.get(Integer.class, "HANDSIDE");
	public static final DataConstant REMOTEIDENTIFIER = DataConstant.get(String.class, "REMOTEIDENTIFIER");
	public static final DataConstant MAXRANGE = DataConstant.get(Integer.class, "MAXRANGE");
	public static final DataConstant SCREENOVERSIZE = DataConstant.get(Double.class, "SCREENOVERSIZE");
	public static final DataConstant REMOTEHOST = DataConstant.get(String.class, "REMOTEHOST");
	public static final DataConstant REMOTEPORT = DataConstant.get(Integer.class, "REMOTEPORT");
	public static final DataConstant BACKFACETRACKING = DataConstant.get(Boolean.class, "BACKFACETRACKING");
	public static final DataConstant NATNET = DataConstant.get(Boolean.class, "NATNET");
	public static final DataConstant HASBUTTONS = DataConstant.get(Boolean.class, "HASBUTTONS");
	public static final DataConstant MERGEDIRECTLY = DataConstant.get(Boolean.class, "MERGEDIRECTLY");
	public static final DataConstant DPRATIO = DataConstant.get(Double.class, "DPRATIO");


	/* Trackables extern ID defined by TrakingTOols*/
	public static final int RB_HANDRIGHT = 2;
	public static final int RB_HANDLEFT = 3;
	public static final int RB_PEN = 4;
	public static final int RB_LASER = 1;
	public static final int RB_IPHONE = 5;
	public static final int RB_IPHONE2 = 6;
	public static final int RB_BASE1 = 10;
	public static final int RB_BASE2 = 11;
	public static final int RB_MOBILEDISPLAY = 101;
	public static final int RB_CITRON = 103;
	public static final int RB_PERSON1 = 50;
	public static final int RB_LEFT_CLICK = 31;
	public static final int RB_LEFT_GRAB = 32;
	public static final int RB_LEFT_POINT = 33;
	public static final int RB_RIGHT_BASE = 20;
	public static final int RB_RIGHT_CLICK = 21;
	public static final int RB_RIGHT_GRAB = 22;
	public static final int RB_RIGHT_POINT = 23;
	public static final int RB_RIGHT_DEFAULT = 200;
	public static final int RB_RIGHT_CLICK2 = 201;
	public static final int RB_RIGHT_POINT2 = 202;
	public static final int RB_LEFT_DEFAULT = 300;
	public static final int RB_LEFT_CLICK2 = 301;
	public static final int RB_LEFT_POINT2 = 302;	
	
	/* Rigid Bodies project load */
	public static final int PRIME_LASER = 1001;
	public static final int PRIME_IPHONE = 1002;
	public static final int GLOVES = 1003;
	public static final int EMPTYTRACKABLES = 1004;
	public static final int ALLTRACKABLES = 1005;
	
	/* Room Objects */
	public static final int ROOMOBJECT_MANUAL = 0;
	public static final int ROOMOBJECT_CUBES = 1;
	public static final int ROOMOBJECT_CITRON = 2;
	public static final int ROOMOBJECT_SURFACE = 3;
	public static final int ROOMOBJECT_ICT = 4;
	public static final int ROOMOBJECT_4k = 5;	
	
	/* Rigid Body Roles */
	public static final int RBROLE_POINTINGDEVICE = 1201;
	public static final int RBROLE_PERSON = 1202;
	public static final int RBROLE_MOBILEDISPLAY = 1203;
	public static final int RBROLE_ROOMOBJECT = 1204;
	public static final int RBROLE_GESTURE = 1205;
	
	/* Rigid Body Modes */
	public static final int RBMODE_NONE = 2200;
	public static final int RBMODE_DIRECTPOINTING = 2201;
	public static final int RBMODE_RELATIVEPIONTING = 2202;
	public static final int RBMODE_HYBRIDPOINTING = 2203;
	
	/* Gestures */
	public static final int GESTURE_DEFAULT = 5500;
	public static final int GESTURE_CLICK = 5501;
	public static final int GESTURE_GRAB = 5502;
	public static final int GESTURE_RIGHTCLICK = 5503;
	public static final int GESTURE_SINGLEPOINT = 5504;
	public static final int GESTURE_SINGLEPOINTCLICK = 5505;
	public static final int GESTURE_CLUTCH = 5506;
	public static final int GESTURE_FLATHAND = 5507;
	
	public static final String KEY_NOKEY = "";
	public static final String KEY_ONSCREEN = "ONSCREEN";
	public static final String KEY_OFFSCREEN = "OFFSCREEN";
	public static final String KEY_OVERSIZESCREEN = "SCREENOVERSIZE";
	public static final String KEY_ENTERSCREEN = "ENTERSCREEN";
}
