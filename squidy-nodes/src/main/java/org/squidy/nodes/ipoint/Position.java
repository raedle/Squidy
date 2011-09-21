package org.squidy.nodes.ipoint;

public class Position {
	private int iValid;
	private double dPosX;
	private double dPosY;
	private double dPosZ;
	
	public double getdPosX() {
		return dPosX;
	}

	public double getdPosY() {
		return dPosY;
	}

	public double getdPosZ() {
		return dPosZ;
	}

	@Override
	public String toString() {
		return "\"iValid\":" + iValid + ",\"dPosX:\"" + dPosX + ",\"dPosY:\"" + dPosY + ",\"dPosZ:\"" + dPosZ;
	}
}
