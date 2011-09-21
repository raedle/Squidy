package org.squidy.nodes.reactivision.remote.control;

import java.util.HashMap;
import java.util.Map;

public enum FiducialSet {
	AMOEBA_DEFAULT(0),
	AMOEBA_LEGACY(1),
	AMOEBA_SMALL(2),
	AMOEBA_MINISET(3),
	CLASSIC(100),
	D_TOUCH(200);
	
	private int value;
	private static Map<Integer, FiducialSet> mapping;
	
	static {
		mapping = new HashMap<Integer, FiducialSet>();
		for (FiducialSet f : values())
			mapping.put(f.value, f);
	}
	
	private FiducialSet(int value) {
		this.value = value;
	}
	
	public boolean equals(FiducialSet f) {
		if (this.value == f.value)
			return true;
		return false;
	}
	
	public String toString() {
		String output = "<FiducialSet: ";
		switch (valueOf(value)) {
			case AMOEBA_DEFAULT:
				output += "AMOEBA_DEFAULT";
				break;
			case AMOEBA_LEGACY:
				output += "AMOEBA_LEGACY";
				break;
			case AMOEBA_SMALL:
				output += "AMOEBA_SMALL";
				break;
			case AMOEBA_MINISET:
				output += "AMOEBA_MINISET";
				break;
			case CLASSIC:
				output += "CLASSIC";
				break;
			case D_TOUCH:
				output += "D_TOUCH";
				break;
			default:
				output += "unknown";
		}
		return output + ">";
	}
	
	public final int value() {
		return value;
	}
	
	public static final FiducialSet valueOf(int value) {
		return mapping.get(value);
	}
	
	public static int size() {
		return values().length;
	}
}