package org.squidy.nodes.reactivision.remote;

public class FloatPoint {
	public float x;
	public float y;
	
	public FloatPoint() {
		this(0,0);
	}
	
	public FloatPoint(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public FloatPoint(FloatPoint p) {
		this(p.x, p.y);
	}
	
	public void set(FloatPoint p) {
		this.x = p.x;
		this.y = p.y;
	}
}
