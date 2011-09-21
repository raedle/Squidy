package org.squidy.nodes.g2drecognizer;

import java.util.ArrayList;
import java.util.Random;

public class G2DUtils {

	private static final Random rand = new Random();

	public static G2DRectangle FindBox(G2DPoint[] points) {
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;

		for (G2DPoint p : points) {
			if (p.X < minX)
				minX = p.X;
			if (p.X > maxX)
				maxX = p.X;

			if (p.Y < minY)
				minY = p.Y;
			if (p.Y > maxY)
				maxY = p.Y;
		}

		return new G2DRectangle(minX, minY, maxX - minX, maxY - minY);
	}

	public static double Distance(G2DPoint p1, G2DPoint p2) {
		double dx = p2.X - p1.X;
		double dy = p2.Y - p1.Y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	// compute the centroid of the points given
	public static G2DPoint Centroid(G2DPoint[] points) {
		double xsum = 0.0;
		double ysum = 0.0;

		for (G2DPoint p : points) {
			xsum += p.X;
			ysum += p.Y;
		}
		return new G2DPoint(xsum / (double)points.length, ysum / (double)points.length);
	}

	public static double PathLength(G2DPoint[] points) {
		double length = 0;
		for (int i = 1; i < points.length; i++) {
			length += Distance(points[i - 1], points[i]);
		}
		return length;
	}

	// determines the angle, in degrees, between two points. the angle is defined 
	// by the circle centered on the start point with a radius to the end point, 
	// where 0 degrees is straight right from start (+x-axis) and 90 degrees is
	// straight down (+y-axis).
	public static double AngleInDegrees(G2DPoint start, G2DPoint end,
			boolean positiveOnly) {
		double radians = AngleInRadians(start, end, positiveOnly);
		return Rad2Deg(radians);
	}

	// determines the angle, in radians, between two points. the angle is defined 
	// by the circle centered on the start point with a radius to the end point, 
	// where 0 radians is straight right from start (+x-axis) and PI/2 radians is
	// straight down (+y-axis).
	public static double AngleInRadians(G2DPoint start, G2DPoint end,
			boolean positiveOnly) {
		double radians = 0.0;
		if (start.X != end.X) {
			radians = Math.atan2(end.Y - start.Y, end.X - start.X);
		} else // pure vertical movement
		{
			if (end.Y < start.Y)
				radians = -Math.PI / 2.0; // -90 degrees is straight up
			else if (end.Y > start.Y)
				radians = Math.PI / 2.0; // 90 degrees is straight down
		}
		if (positiveOnly && radians < 0.0) {
			radians += Math.PI * 2.0;
		}
		return radians;
	}

	public static double Rad2Deg(double rad) {
		return (rad * 180d / Math.PI);
	}

	public static double Deg2Rad(double deg) {
		return (deg * Math.PI / 180d);
	}

	// rotate the points by the given degrees about their centroid
	public static G2DPoint[] RotateByDegrees(G2DPoint[] points, double degrees) {
		double radians = Deg2Rad(degrees);
		return RotateByRadians(points, radians);
	}

	// rotate the points by the given radians about their centroid
	public static G2DPoint[] RotateByRadians(G2DPoint[] points, double radians) {
		G2DPoint[] newPoints = new G2DPoint[points.length];
		G2DPoint c = Centroid(points);

		double cos = Math.cos(radians);
		double sin = Math.sin(radians);

		double cx = c.X;
		double cy = c.Y;

		for (int i = 0; i < points.length; i++) {
			G2DPoint p = (G2DPoint) points[i];

			double dx = p.X - cx;
			double dy = p.Y - cy;

			G2DPoint q = new G2DPoint(0.0, 0.0);
			q.X = dx * cos - dy * sin + cx;
			q.Y = dx * sin + dy * cos + cy;

			newPoints[i] = q;
		}
		return newPoints;
	}

	// Rotate a point 'p' around a point 'c' by the given radians.
	// Rotation (around the origin) amounts to a 2x2 matrix of the form:
	//
	//		[ cos A		-sin A	] [ p.x ]
	//		[ sin A		cos A	] [ p.y ]
	//
	// Note that the C# Math coordinate system has +x-axis stright right and
	// +y-axis straight down. Rotation is clockwise such that from +x-axis to
	// +y-axis is +90 degrees, from +x-axis to -x-axis is +180 degrees, and 
	// from +x-axis to -y-axis is -90 degrees.
	public static G2DPoint RotatePoint(G2DPoint p, G2DPoint c, double radians) {
		G2DPoint q = new G2DPoint(0d, 0d);
		q.X = (p.X - c.X) * Math.cos(radians) - (p.Y - c.Y) * Math.sin(radians)
				+ c.X;
		q.Y = (p.X - c.X) * Math.sin(radians) + (p.Y - c.Y) * Math.cos(radians)
				+ c.Y;
		return q;
	}

	// translates the points so that the upper-left corner of their bounding box lies at 'toPt'
	public static G2DPoint[] TranslateBBoxTo(G2DPoint[] points, G2DPoint toPt) {
		G2DPoint[] newPoints = new G2DPoint[points.length];
		G2DRectangle r = G2DUtils.FindBox(points);
		for (int i = 0; i < points.length; i++) {
			G2DPoint p = (G2DPoint) points[i];
			p.X += (toPt.X - r.X);
			p.Y += (toPt.Y - r.Y);
			newPoints[i] = p;
		}
		return newPoints;
	}

	// translates the points so that their centroid lies at 'toPt'
	public static G2DPoint[] TranslateCentroidTo(G2DPoint[] points,
			G2DPoint toPt) {
		G2DPoint[] newPoints = new G2DPoint[points.length];
		G2DPoint centroid = Centroid(points);
		for (int i = 0; i < points.length; i++) {
			G2DPoint p = (G2DPoint) points[i];
			p.X += (toPt.X - centroid.X);
			p.Y += (toPt.Y - centroid.Y);
			newPoints[i] = p;
		}
		return newPoints;
	}

	// translates the points by the given delta amounts
	public static G2DPoint[] TranslateBy(G2DPoint[] points, G2DSize sz) {
		G2DPoint[] newPoints = new G2DPoint[points.length];
		for (int i = 0; i < points.length; i++) {
			G2DPoint p = (G2DPoint) points[i];
			p.X += sz.Width;
			p.Y += sz.Height;
			newPoints[i] = p;
		}
		return newPoints;
	}

	// scales the points so that they form the size given. does not restore the 
	// origin of the box.
	public static G2DPoint[] ScaleTo(G2DPoint[] points, G2DSize sz) {
		G2DPoint[] newPoints = new G2DPoint[points.length];
		G2DRectangle r = FindBox(points);
		for (int i = 0; i < points.length; i++) {
			G2DPoint p = (G2DPoint) points[i];
			if (r.Width != 0d)
				p.X *= (sz.Width / r.Width);
			if (r.Height != 0d)
				p.Y *= (sz.Height / r.Height);
			newPoints[i] = p;
		}
		return newPoints;
	}

	// scales by the percentages contained in the 'sz' parameter. values of 1.0 would result in the
	// identity scale (that is, no change).
	public static G2DPoint[] ScaleBy(G2DPoint[] points, G2DSize sz) {
		G2DPoint[] newPoints = new G2DPoint[points.length];
		G2DRectangle r = FindBox(points);
		for (int i = 0; i < points.length; i++) {
			G2DPoint p = (G2DPoint) points[i];
			p.X *= sz.Width;
			p.Y *= sz.Height;
			newPoints[i] = p;
		}
		return newPoints;
	}

	// scales the points so that the length of their longer side
	// matches the length of the longer side of the given box.
	// thus, both dimensions are warped proportionally, rather than
	// independently, like in the function ScaleTo.
	public static G2DPoint[] ScaleToMax(G2DPoint[] points, G2DRectangle box) {
		G2DPoint[] newPoints = new G2DPoint[points.length];
		G2DRectangle r = FindBox(points);
		for (int i = 0; i < points.length; i++) {
			G2DPoint p = (G2DPoint) points[i];
			p.X *= (box.getMaxSide() / r.getMaxSide());
			p.Y *= (box.getMaxSide() / r.getMaxSide());
			newPoints[i] = p;
		}
		return newPoints;
	}

	// scales the points so that the length of their shorter side
	// matches the length of the shorter side of the given box.
	// thus, both dimensions are warped proportionally, rather than
	// independently, like in the function ScaleTo.
	public static G2DPoint[] ScaleToMin(G2DPoint[] points, G2DRectangle box) {
		G2DPoint[] newPoints = new G2DPoint[points.length];
		G2DRectangle r = FindBox(points);
		for (int i = 0; i < points.length; i++) {
			G2DPoint p = (G2DPoint) points[i];
			p.X *= (box.getMinSide() / r.getMinSide());
			p.Y *= (box.getMinSide() / r.getMinSide());
			newPoints[i] = p;
		}
		return newPoints;
	}

	public static G2DPoint[] Resample(G2DPoint[] points, int n) {
		double I = PathLength(points) / (double)(n - 1); // interval length
		double D = 0.0;
		ArrayList<G2DPoint> srcPts = new ArrayList<G2DPoint>();
		for(int j=0;j<points.length;j++){
			srcPts.add(points[j]);
		}
		ArrayList<G2DPoint> dstPts = new ArrayList<G2DPoint>();
		dstPts.add(srcPts.get(0));
		for (int i = 1; i < srcPts.size(); i++) {
			G2DPoint pt1 = srcPts.get(i - 1);
			G2DPoint pt2 = srcPts.get(i);

			double d = Distance(pt1, pt2);
			if ((D + d) >= I) {
				double qx = pt1.X + ((I - D) / d) * (pt2.X - pt1.X);
				double qy = pt1.Y + ((I - D) / d) * (pt2.Y - pt1.Y);
				G2DPoint q = new G2DPoint(qx, qy);
				dstPts.add(q); // append new point 'q'
				srcPts.add(i, q); // insert 'q' at position i in points s.t. 'q' will be the next i
				D = 0.0;
			} else {
				D += d;
			}
		}
		// somtimes we fall a rounding-error short of adding the last point, so add it if so
		if (dstPts.size() == n - 1) {
			dstPts.add(srcPts.get(srcPts.size()- 1));
		}

		G2DPoint[] array = new G2DPoint[dstPts.size()];
		dstPts.toArray(array);
		return array;
	}

	// computes the 'distance' between two point paths by summing their corresponding point distances.
	// assumes that each path has been resampled to the same number of points at the same distance apart.
	public static double PathDistance(G2DPoint[] path1, G2DPoint[] path2) {
		double distance = 0;
		for (int i = 0; i < path1.length; i++) {
			distance += Distance((G2DPoint) path1[i], (G2DPoint) path2[i]);
		}
		return distance / (double)path1.length;
	}

	/// <summary>
	/// Gets a random number between low and high, inclusive.
	/// </summary>
	/// <param name="low"></param>
	/// <param name="high"></param>
	/// <returns></returns>
	public static int Random(int low, int high) {
		int tmp = rand.nextInt(high + 1 - low);
		return tmp + low;
	}

	/// <summary>
	/// Gets multiple random numbers between low and high, inclusive. The
	/// numbers are guaranteed to be distinct.
	/// </summary>
	/// <param name="low"></param>
	/// <param name="high"></param>
	/// <param name="num"></param>
	/// <returns></returns>
	public static int[] Random(int low, int high, int num) {
		int[] array = new int[num];
		for (int i = 0; i < num; i++) {
			array[i] = Random(low, high);
			for (int j = 0; j < i; j++) {
				if (array[i] == array[j]) {
					i--; // redo i
					break;
				}
			}
		}
		return array;
	}

}
