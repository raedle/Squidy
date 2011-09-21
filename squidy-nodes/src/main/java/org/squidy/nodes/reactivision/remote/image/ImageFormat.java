package org.squidy.nodes.reactivision.remote.image;

import java.util.HashMap;
import java.util.Map;



public enum ImageFormat {
	RAW_GRAYSCALE((byte)0x00),
	JPG((byte)0x01);
	
	private final byte value;
	private static Map<Byte, ImageFormat> mapping; 
	
	static {
		mapping = new HashMap<Byte, ImageFormat>();
		for (ImageFormat i : values())
			mapping.put(i.value, i);
	}
	
	private ImageFormat(byte value) {
		this.value = value;
	}
	
	public final boolean equals(ImageFormat imageFormat) {
		if (this.value == imageFormat.value)
			return true;
		return false;
	}
	
	public final byte value() {
		return value;
	}
	
	public static final ImageFormat valueOf(byte value) {
		return mapping.get(value);
	}
	
	public String toString() {
		String output = "<ImageFormat: ";
		switch (valueOf(value)) {
			case RAW_GRAYSCALE:
				output += "RAW_GRAYSCALE";
				break;
			case JPG:
				output += "JPG";
				break;
			default:
				output += "unknown";
		}
		return output + ">";
	}
}
