package org.squidy.nodes.reactivision.remote.control;

import java.util.HashMap;
import java.util.Map;

public enum OpCode {
	//server OpCodes (most significant bit = 0)
	GET_GRID((byte)0x00),
	SET_GRID((byte)0x01),
	GET_CAMERA_SETTINGS((byte)0x10),
	SET_CAMERA_SETTINGS_FRAMERATE((byte)0x11),
	SET_CAMERA_SETTINGS_EXPOSURE_TIME((byte)0x12),
	SET_CAMERA_SETTINGS_PIXEL_CLOCK((byte)0x13),
	SET_CAMERA_SETTINGS_HARDWARE_GAIN((byte)0x14),
	SET_CAMERA_SETTINGS_EDGE_ENHANCEMENT((byte)0x15),
	SET_CAMERA_SETTINGS_GAMMA((byte)0x16),
	//camera
	START_CAMERA_FEED((byte)0x20),
	STOP_CAMERA_FEED((byte)0x21),
	//fiducial engine
	GET_FIDUCIAL_SET((byte)0x30),
	SET_FIDUCIAL_SET((byte)0x31),
	
	//client OpCodes (most significant bit = 1)
	SEND_GRID((byte)0x80),
	SEND_CAMERA_SETTINGS((byte)0x81),
	SEND_FIDUCIAL_FINDER((byte)0xb0);
	
	private final byte value;
	private static Map<Byte, OpCode> mapping;
	
	static {
		mapping = new HashMap<Byte, OpCode>();
		for (OpCode o : values())
			mapping.put(o.value, o);
	}
	
	private OpCode(byte value) {
		this.value = value;
	}
	
	public boolean equals(OpCode o) {
		if (this.value == o.value)
			return true;
		return false;
	}
	
	public String toString() {
		String output = "<OpCode: ";
		switch (valueOf(value)) {
			case GET_GRID:
				output += "GET_GRID";
				break;
			case SET_GRID:
				output += "SET_GRID";
				break;
			case GET_CAMERA_SETTINGS:
				output += "GET_CAMERA_SETTINGS";
				break;
			case SET_CAMERA_SETTINGS_FRAMERATE:
				output += "SET_CAMERA_SETTINGS_FRAMERATE";
				break;
			case SET_CAMERA_SETTINGS_EXPOSURE_TIME:
				output += "SET_CAMERA_EXPOSURE_TIME";
				break;
			case SET_CAMERA_SETTINGS_PIXEL_CLOCK:
				output += "SET_CAMERA_SETTINGS_PIXEL_CLOCK";
				break;
			case SET_CAMERA_SETTINGS_HARDWARE_GAIN:
				output += "SET_CAMERA_SETTINGS_HARDWARE_GAIN";
				break;
			case SET_CAMERA_SETTINGS_EDGE_ENHANCEMENT:
				output += "SET_CAMERA_SETTINGS_EDGE_ENHANCEMENT";
				break;
			case SET_CAMERA_SETTINGS_GAMMA:
				output += "SET_CAMERA_SETTINGS_GAMMA";
				break;
			case START_CAMERA_FEED:
				output += "START_FEED";
				break;
			case STOP_CAMERA_FEED:
				output += "STOP_FEED";
				break;
			case SET_FIDUCIAL_SET:
				output += "SET_FIDUCIAL_SET";
				break;
			case GET_FIDUCIAL_SET:
				output += "GET_FIDUCIAL_SET";
				break;
			case SEND_GRID:
				output += "SEND_GRID";
				break;
			case SEND_CAMERA_SETTINGS:
				output += "SEND_CAMERA_SETTINGS";
				break;
			default:
				output += "unknown";
		}
		return output + ">";
	}
	
	public final byte value() {
		return value;
	}
	
	public static final OpCode valueOf(byte value) {
		return mapping.get(value);
	}
}