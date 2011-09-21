package org.squidy.nodes.ipoint;


public class HHIHandData {
	
	private String Action;
	private Position FingerTip;
	private Position HandCenter;
	
	public HHIHandData() {
		// no-args
	}
	
	public HHIHandData(Position FingerTip, Position HandCenter, String Action) {
		this.FingerTip = FingerTip;
		this.HandCenter = HandCenter;
		this.Action = Action;
	}
	
	public String getAction() {
		return Action;
	}

	public Position getFingerTip() {
		return FingerTip;
	}
	
	public Position getHandCenter() {
		return HandCenter;
	}

	@Override
	public String toString() {
		return "\"FingerTip\":{" + FingerTip + "}, \"HandCenter\":{" + HandCenter + "}, \"Action\":" + Action;
	}
}
