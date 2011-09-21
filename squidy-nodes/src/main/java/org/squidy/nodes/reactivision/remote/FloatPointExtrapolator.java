package org.squidy.nodes.reactivision.remote;


/**
 * This class offers static methods for extrapolating coordinates of a
 * {@link FloatPoint} continuing a sequence of two or more FloatPoints.
 */
public class FloatPointExtrapolator {
	
	public static FloatPoint extrapolate(FloatPoint a, FloatPoint b) {
		return new FloatPoint( 2 * b.x - a.x, 2 * b.y - a.y );
	}
}
