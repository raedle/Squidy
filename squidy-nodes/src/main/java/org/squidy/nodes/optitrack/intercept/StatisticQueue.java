package org.squidy.nodes.optitrack.intercept;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import javax.vecmath.Point2d;


public class StatisticQueue extends LinkedList<Double> {
	
	private int size;
	
	public StatisticQueue(int size)
	{
		super();
		this.size = size;
	}
	
	public LinkedList<Double> winsorize(double percentage)
	{
		LinkedList<Double> tmpList =  (LinkedList<Double>) this.clone();
		LinkedList<Double> outList =  (LinkedList<Double>) this.clone();
		Collections.sort(tmpList);
		int threshold = (int) Math.floor(percentage * outList.size());
		for (int i = 0; i < threshold; i++)
		{
			outList.remove(tmpList.getFirst());
			outList.remove(tmpList.getLast());
		}
		return outList;
	}
	
	public double getAverage(int reverseElementCount, double winsorizePercentage)
	{
		LinkedList<Double> outList;
		if (winsorizePercentage > 0)
			outList =  this.winsorize(winsorizePercentage);
		else
			outList = (LinkedList<Double>) this.clone();
		
		if (reverseElementCount == 0)
		{
			reverseElementCount = outList.size();
			Collections.reverse(outList);
		}
		
		java.util.Iterator<Double> itr = outList.iterator();
		int counter = 0;
		double avg = 0;
		while (itr.hasNext())
		{
			if (counter++ > reverseElementCount)
				break;
			avg += itr.next();
		}
		return (avg / reverseElementCount);
	}
	public double getSum(int reverseElementCount, double winsorizePercentage)
	{
		LinkedList<Double> outList;
		if (winsorizePercentage > 0)
			outList =  this.winsorize(winsorizePercentage);
		else
			outList = (LinkedList<Double>) this.clone();
		
		if (reverseElementCount == 0)
		{
			reverseElementCount = outList.size();
			Collections.reverse(outList);
		}

		Iterator<Double> itr = (Iterator<Double>) outList.iterator();
		int counter = 0;
		double sum = 0;
		while (itr.hasNext())
		{
			if (counter++ > reverseElementCount)
				break;
			sum += itr.next();
		}
		return sum;		
	}
	
	public boolean add(double e) {
		boolean retBool = super.add(e);
		if (retBool)
		{
			if (this.size() > this.size)
				this.poll();
		}
		return retBool;
	}
}
