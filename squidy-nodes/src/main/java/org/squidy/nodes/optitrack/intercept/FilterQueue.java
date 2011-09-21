package org.squidy.nodes.optitrack.intercept;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import com.sun.xml.bind.v2.runtime.reflect.ListIterator;

public class FilterQueue extends LinkedList<Point2d> {
	
//	private Queue<Point2d> p2dQueue;
	private int size;
	private double lastAccel;
	private LinkedList<Double> accelData;
	
	public FilterQueue(int size)
	{
		super();
		accelData = new LinkedList<Double>();
		this.size = size;
		//p2dQueue = new LinkedList<Point2d>();
	}

	public Vector2d historyDirection(int size)
	{
		Vector2d v = new Vector2d();
		Point2d pPrev = null;
		int counter = 0;
		Collections.reverse(this);
		Iterator<Point2d> itr = this.iterator();
		while(itr.hasNext())
		{
			Point2d p2 = (Point2d)itr.next().clone();
			
			if (counter++ > size)
				break;
			
			if (pPrev == null)
			{
				pPrev = p2;
			}
			else
			{
				pPrev.x -= p2.x;
				pPrev.y -= p2.y;
				Point2d vTemp = (Point2d)pPrev.clone();
				v.add(vTemp);
				pPrev = p2;
			}
		}
		Collections.reverse(this);
//		System.out.println("mov " + v.x);
		return v;
	}
	
	public double getAcceleration(int size)
	{
		double accel = 0;
		int counter = 0;
		Point2d pPrev = null;
		double delta = 0;
		LinkedList<Point2d> accelRaw = this.selfWinsorizer();
		
		Collections.reverse(this);
		Iterator<Point2d> litr = this.iterator();
		
		while(litr.hasNext())
		{
			Point2d p2 = null;
		
			p2 = (Point2d)litr.next().clone();
//			System.out.println("DIR " + p2.toString());
			if (counter ++ > size)
				break;
			if (pPrev == null)
			{
				pPrev = p2;
			} 
			else
			{
//				System.out.println(pPrev.distance(p2) + " " + delta);
				if (delta == 0)
				{
					delta = pPrev.distance(p2);
				}
				else if (Math.abs(delta/(counter-2)) < pPrev.distance(p2))
				{
					delta += pPrev.distance(p2);
					accel += delta;
				}else
				{
					delta -= pPrev.distance(p2);
					accel -= delta;					
				}
				
				//pPrev = p2;
			}
		}
		Collections.reverse(this);
	
		if (accel < lastAccel)
		{
			lastAccel = accel;
			accel *= -100;
		}
		else
		{
			lastAccel = accel;
			accel *= 100;
		}
		accelData.add(accel);
		if (accelData.size() >= 4)
			accelData.poll();
		int neg = 0;
		int pos = 0;
		for (double d : accelData)
		{
			if (d < 0)
				neg++;
			else
				pos++;
		}
		if (accel > 0 && neg > pos)
			accel *= -1;
		if (accel < 0 && pos > neg)
			accel *= -1;
		return accel;
	}
	
	public LinkedList<Point2d> selfWinsorizer()
	{
		LinkedList<Point2d> al = new LinkedList<Point2d>((this));
		Collections.sort(al, new SortDist());

		if (this.size() >= this.size -1)
		{
			//al.remove();
			Point2d tmpFirst = al.getFirst();
			Point2d tmpLast = al.getLast();
			Iterator<Point2d> itr = al.iterator();
			while(itr.hasNext())
			{
				Point2d p2 = itr.next();
				if (tmpFirst.distance(p2) == 0)
					this.remove(p2);
				if (tmpLast.distance(p2) == 0)
					this.remove(p2);
			}
			return al;
		}
		return this;
	}
	
	public Point2d winsorize()
	{
		ArrayList<Point2d> al = new ArrayList<Point2d>((this));
		Collections.sort(al, new SortDist());
		al.remove(0);
		al.remove(al.size());
		Point2d ret2d = new Point2d(0,0);
		for (Point2d p2d : al)
		{
			ret2d.add(p2d);
		}
		ret2d.x /= al.size();
		ret2d.y /= al.size();
		return ret2d;
	}
	public Point2d winsorize(double value)
	{
		double accel = this.getAcceleration(10);
		int maxVal = 100;
		accel = Math.min(Math.abs(accel),maxVal);
		int counter = 0;
		ArrayList<Point2d> al = new ArrayList<Point2d>();
		Collections.reverse(this);
		for (Point2d p : this)
		{
			if (counter++ >= (maxVal/accel)+ 10)
				break;
			al.add(p);
		}
//		System.out.println(al.size() + " " + accel + " " + this.size() + " " + this.size);
		Collections.reverse(this);
		Collections.sort(al, new SortDist());
		Point2d sum2d = new Point2d(0,0);
		for (Point2d p2d : al)
		{
			sum2d.add(p2d);
		}
		sum2d.x /= al.size();
		sum2d.y /= al.size();
		Point2d p2 = (Point2d) al.get(0).clone();
		if (p2.distance(sum2d) > value)
		{
			System.out.println(p2.distance(sum2d));
			al.remove(0);
		}
		p2 = (Point2d) al.get(al.size()-1).clone();
		if (p2.distance(sum2d) > value)
		{
			System.out.println("max " + p2.distance(sum2d));
			al.remove(al.size()-1);
		}
		Point2d ret2d = new Point2d(0,0);
		for (Point2d p2d : al)
		{
			ret2d.add(p2d);
		}
		ret2d.x /= al.size();
		ret2d.y /= al.size();
		System.out.println(al.size() + " " + this.peekLast().x + " " + sum2d.x);
		return ret2d;
	}	
	
	private class SortDist implements Comparator<Point2d>
	{
		public int compare(Point2d p2d1, Point2d p2d2)
		{
			Point2d null2d = new Point2d(0,0);
			if (p2d1.distance(null2d) < p2d2.distance(null2d))
				return -1;
			else if (p2d1.distance(null2d) == p2d2.distance(null2d))
				return 0;
			else
				return 1;
		}
	}


	public boolean add(Point2d e) {
//		boolean retBool =  p2dQueue.add((Point2d) e);
		boolean retBool = super.add(e);
		if (retBool)
		{
			if (this.size() > this.size)
				this.poll();
		}
		return retBool;
	}
}
