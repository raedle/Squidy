package org.squidy.nodes.optitrack.intercept;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

import org.squidy.nodes.optitrack.utils.TrackingUtility;

import com.sun.pdfview.colorspace.CalGrayColor;


public class Intersection {
	
	private Point3d iPoint3d;
	private Point2d iPoint2d;
	private double centerDistance;
	private InterceptObject iObject;
	private boolean isOffscreen;
	private StatisticQueue weights;
	private FilterQueue calcPoints;
	private Queue<Point2d> relPoints;
	private Queue<Point2d> dirPoints;
	private FilterQueue dirQueue;
	
	public Intersection()
	{
		isOffscreen = false;
		weights = new StatisticQueue(10);
		calcPoints = new FilterQueue(5);
		relPoints = new LinkedList<Point2d>();
		dirPoints = new LinkedList<Point2d>();
		dirQueue = new FilterQueue(10);
	}
	
	public void setIntersection(Point3d p)
	{
		this.iPoint3d = p;
	}
	public void setIntersection(Point2d p)
	{
		this.iPoint2d = p;
	}
	public void setCenterDistance(double d)
	{
		this.centerDistance = d;
	}
	public void setIntercepObject(InterceptObject o)
	{
		this.iObject = o;
	}
	public void setIsOffscreen(boolean b)
	{
		this.isOffscreen = b;
	}
	public boolean getIsOffscreen()
	{
		return this.isOffscreen;
	}
	public boolean getIsOutOfArea()
	{
		if ((iPoint2d.x == 0.0 || iPoint2d.x == 1.0)  && (iPoint2d.y == 0.0 || iPoint2d.y == 1.0))
			return true;
		else
			return false;
	}
	public InterceptObject getInterceptObject()
	{
		return iObject;
	}
	public double getCenterDistance()
	{
		return centerDistance;
	}
	public Point2d getIntersectionPoint2d()
	{
		return this.iPoint2d;
	}
	
	
	
	public Point2d getAdaptiveIntersection2d(Intersection refPoint, double weight, double increaseSpeedThreshold)
	{
		weights.add(weight);
		
//		if (avg < weight)
		{
			weight = weights.getAverage(0, 0.2);
		}	
		Point2d p2d = new Point2d();
//		System.out.println(refPoint.getIntersectionPoint2d());
		dirQueue.add(refPoint.getIntersectionPoint2d());
		Point2d refP = refPoint.getIntersectionPoint2d();
		if (weight > increaseSpeedThreshold)
		{
			p2d.x = refP.x;
			p2d.y = refP.y; 
			if (dirQueue.size() > 1)
			{
				Point2d prevP = (Point2d)dirQueue.toArray()[dirQueue.size()-2];
				double d = p2d.distance(prevP);
//				System.out.println(p2d.x + " " + prevP.x);
				prevP.sub(p2d);
				p2d.x += prevP.x * (weight-increaseSpeedThreshold)*-30;
				p2d.y += prevP.y * (weight-increaseSpeedThreshold)*-30;
				refP = p2d;
//				System.out.println(refPoint.getIntersectionPoint2d().x + " " + p2d.x);
			}
			
		} 
//		else
		{
			double accel = dirQueue.getAcceleration(4);
//			System.out.println("Accel \t" + Math.exp(weight) + "\t" + weight);
			weight = Math.exp(weight);
			weight = TrackingUtility.minmax(weight, 2.7, 1, 1, 0);
//			System.out.println("Weight \t" + weight);
			if (accel > 0)
			{
				// ACCELERATE
				Vector2d movement = dirQueue.historyDirection(4);
//				System.out.println(refP.x + " " + iPoint2d.x);
				
				if (refP.x < this.iPoint2d.x && movement.x > 0)
				{
//					System.out.println("--X");
					p2d.x = this.iPoint2d.x;	
				}
				else if (refP.x > this.iPoint2d.x && movement.x < 0)
				{
//					System.out.println("X++");
					p2d.x = this.iPoint2d.x;					
				}
				else
				{
					p2d.x = (this.iPoint2d.x * (1-weight)  + refP.x * weight);
				}
				if (refP.y < this.iPoint2d.y && movement.y > 0)
				{
					p2d.y = this.iPoint2d.y;
				}
				else if (refP.y > this.iPoint2d.y && movement.y < 0)
				{
					p2d.y = this.iPoint2d.y;			
				}
				else
				{
					p2d.y = (this.iPoint2d.y * (1-weight) + refP.y * weight);
				}	
//				p2d.x = (this.iPoint2d.x * (1-weight)  + refP.x * weight);
//				p2d.y = (this.iPoint2d.y * (1-weight) + refP.y * weight);	
			} 
			else if (accel <= 0)
			{
				// DECCELERATE
//				System.out.println("\t\tDECC");
				p2d.x = (this.iPoint2d.x * (1-weight)  + refP.x * weight);
				p2d.y = (this.iPoint2d.y * (1-weight) + refP.y * weight);	
			}
		}
//		calcPoints.add(p2d);
		
//		Point2d tmp2d = new Point2d(0,0);
//		for (Point2d p : calcPoints)
//		{
//			tmp2d.x += p.x;
//			tmp2d.y += p.y;
//		}
//		tmp2d.x /= calcPoints.size();
//		tmp2d.y /= calcPoints.size();
//		p2d = tmp2d;
//		return calcPoints.winsorize(0.3);
		return p2d;
	}
	
	
	
	public Point2d getWeightedIntersetionPoint2d(Intersection refPoint, double weight)
	{
		weights.add(weight);
		if (weights.size() >= 20)
			weights.poll();
		double sum = 0;
		LinkedList<Double> tmpWeights = (LinkedList)weights.clone();
		Collections.sort(tmpWeights);
		if (tmpWeights.size() > 6)
		{
			tmpWeights.remove(tmpWeights.getLast());
			tmpWeights.remove(tmpWeights.getFirst());
		}
		if (tmpWeights.size() >= 12)
		{
			tmpWeights.remove(tmpWeights.getLast());
			tmpWeights.remove(tmpWeights.getFirst());
		}
		
		for (double w : tmpWeights)
		{
			sum += w;
		}
		double avg = sum / tmpWeights.size();
		
		if (avg < weight)
		{
			weight = avg;
		}

		Point2d p2d = new Point2d();
		if (weight > 1)
		{
			dirPoints.add(refPoint.getIntersectionPoint2d());
			if (dirPoints.size() > 10)
				dirPoints.poll();
			p2d.x = refPoint.getIntersectionPoint2d().x;
			p2d.y = refPoint.getIntersectionPoint2d().y; 
			if (dirPoints.size() > 1)
			{
				Point2d prevP = (Point2d)dirPoints.toArray()[dirPoints.size()-2];
				double d = p2d.distance(prevP);
//				System.out.println(p2d.x + " " + prevP.x);
				prevP.sub(p2d);
				p2d.x += prevP.x * (weight-1)*-30;
				p2d.y += prevP.y * (weight-1)*-30;
//				System.out.println(refPoint.getIntersectionPoint2d().x + " " + p2d.x);
			}
		}
		else
		{
			double deltaP = this.iPoint2d.distance(refPoint.getIntersectionPoint2d())*100;
			double maxThreshold = 40;
			double minThreshold = 20;
			double minMaxSpeed = Math.max(minThreshold, deltaP);
			minMaxSpeed = Math.min(maxThreshold, minMaxSpeed);
			minMaxSpeed = TrackingUtility.minmax(minMaxSpeed, maxThreshold, minThreshold, 1, 0);
//			System.out.println(deltaP + "\t " + minMaxSpeed);

			if (minMaxSpeed > 0.5)
			{
				minMaxSpeed -= 0.5;
				minMaxSpeed /=4;
				p2d.x = (this.iPoint2d.x * (1-weight-minMaxSpeed)  + refPoint.getIntersectionPoint2d().x * (weight+minMaxSpeed));
				p2d.y = (this.iPoint2d.y * (1-weight-minMaxSpeed) + refPoint.getIntersectionPoint2d().y * (weight+minMaxSpeed));
			}
			else
			{
				p2d.x = (this.iPoint2d.x * (1-weight)  + refPoint.getIntersectionPoint2d().x * weight);
				p2d.y = (this.iPoint2d.y * (1-weight) + refPoint.getIntersectionPoint2d().y * weight);		
			}
//			p2d.x = (p2d.x * (1-minMaxSpeed)) + (this.iPoint2d.x * (1-minMaxSpeed)  + refPoint.getIntersectionPoint2d().x * minMaxSpeed) * (minMaxSpeed);
//			p2d.y = (p2d.y * (1-minMaxSpeed))+ (this.iPoint2d.y * (1-minMaxSpeed) + refPoint.getIntersectionPoint2d().y * minMaxSpeed) * (minMaxSpeed);			
		}
		
		
		

		Point2d zz = new Point2d();
		zz.x = refPoint.getIntersectionPoint2d().x;
		zz.y = refPoint.getIntersectionPoint2d().y;
		Point2d tmp2d = new Point2d(0,0);
//		if (calcPoints.size()>0)
//		{
////			System.out.print(zz.x);
//			tmp2d = calcPoints.poll();
////			System.out.print("\t"+tmp2d.x);
//			zz.sub(tmp2d);
////			System.out.println("\t"+zz.x);
//		}
//		zz.x *= avg;
//		zz.y *= avg;
//		p2d.add(zz);
//		System.out.println(zz);
		calcPoints.add(p2d);
		if (calcPoints.size() >= 4)
			calcPoints.poll();
		
		
		for (Point2d p : calcPoints)
		{
			tmp2d.x += p.x;
			tmp2d.y += p.y;
		}
		tmp2d.x /= calcPoints.size();
		tmp2d.y /= calcPoints.size();
		p2d = tmp2d;
//		return tmp2d;
//		Point2d p2d = (Point2d)this.iPoint2d.clone();
//		p2d.sub(refPoint.getIntersectionPoint2d());
//		this.iPoint2d.scaleAdd(weight, p2d);
		return p2d;
	}
}
