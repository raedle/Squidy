package org.squidy.nodes.reactivision.remote;

import java.awt.Graphics2D;
import java.util.Vector;


public class CatmullRomSpline {
	private Vector<GridPoint> controlPoints;
	
	public CatmullRomSpline() {
		controlPoints = new Vector<GridPoint>();
	}
	
	public void addControlPoint(GridPoint p) {
		controlPoints.addElement(p);
	}
	
	public void draw(Graphics2D g) {
		for (int i = 0; i < controlPoints.size() - 1; ++i)
			if (controlPoints.get(i).isSet && controlPoints.get(i+1).isSet)
				drawSplineSegment(getPrevious(i), controlPoints.get(i), controlPoints.get(i+1), getNext(i+1), g);
			else
				break;
	}
	
	private GridPoint getPrevious(int position) {
		if (position > 0)
			return controlPoints.get(position - 1);
		return controlPoints.get(0);
	}
	
	private GridPoint getNext(int position) {
		if (position < controlPoints.size() - 1 && controlPoints.get(position+1).isSet)
			return controlPoints.get(position + 1);
		return controlPoints.get(position);
	}
	
	private static void drawSplineSegment(GridPoint p1, GridPoint p2, GridPoint p3, GridPoint p4, Graphics2D g) {
		final int steps = 10;
		final int x[] = new int[steps];
		final int y[] = new int[steps];
		
		final float m_0 = (p3.y - p1.y)/2;
		final float m_1 = (p4.y - p2.y)/2;
		
		for (int i = 0; i < steps; ++i) {
			final float t = (float)i / (steps - 1);
			x[i] = (int)(p2.x + t * (p3.x - p2.x));
			y[i] = (int)(h_00(t)*p2.y + h_10(t)*m_0 + h_01(t)*p3.y + h_11(t)*m_1);
		}
		g.drawPolyline(x, y, x.length);
	}
	
	/**
	 * 
	 * @param segmentLocation = segment number + t
	 * @return a GridPoint with absolute coordinates
	 */
	public GridPoint getInterpolated(float segmentLocation) {
		final GridPoint point = new GridPoint();
		float t = segmentLocation - (int)segmentLocation;
		final GridPoint p1 = getPrevious((int)segmentLocation);
		final GridPoint p2 = controlPoints.get((int)segmentLocation);
		int i = (int)segmentLocation + 1;
		if (i + 1 >= controlPoints.size())
			i = controlPoints.size() - 1;
		final GridPoint p3 = controlPoints.get(i);
		final GridPoint p4 = getNext(i);
		
		final float m_0 = (p3.y - p1.y)/2;
		final float m_1 = (p4.y - p2.y)/2;
		
		point.x = p2.x + t * (p3.x - p2.x);
		point.y = h_00(t)*p2.y + h_10(t)*m_0 + h_01(t)*p3.y + h_11(t)*m_1;
		
		return point;
	}
	
	private static float h_00(float t) {
		return (1 + 2*t)*(1 - t)*(1 - t);
	}
	
	private static float h_10(float t) {
		return t*(1 - t)*(1 - t);
	}
	
	private static float h_01(float t) {
		return t*t*(3 - 2*t);
	}
	
	private static float h_11(float t) {
		return t*t*(t - 1);
	}
	
	public int size() {
		return controlPoints.size();
	}
}
