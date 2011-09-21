package org.squidy.nodes.optitrack.gestures;

import java.util.ArrayList;


public class StaticGesture {
	
    private String gestureName;	
    private int gestureId;
    private int eventId;
    private int frameID;
//    private double thumbDist;
//    private double indexDist;
//    private double middleDist;
//    private double smallDist;
    private int recoCounter;
    private int handID;
    private int tuioCounter;
    private ArrayList<Double> distances;
    
    
	public StaticGesture(String name, int id, int eventId, int handId)
	{
		this.gestureName = name;
		this.gestureId = id;
		this.eventId = eventId;
		this.handID = handId;
//		this.thumbDist =  Double.MAX_VALUE;
//		this.indexDist = Double.MAX_VALUE;
//		this.middleDist = Double.MAX_VALUE;
//		this.smallDist = Double.MAX_VALUE;
		this.recoCounter = 0;
		this.tuioCounter = 0;
		this.distances = new ArrayList<Double>();
		
	}
	
	public void setDistance(int fingerIndex, double dist)
	{
		if (distances.size() < fingerIndex)
		{
			distances.add(dist);
		}else
		{
			if (distances.get(fingerIndex-1) == null)
			{
				distances.add(fingerIndex-1,dist);
			}else
			{
				distances.set(fingerIndex-1, dist);
			}
		}
//		switch(fingerIndex)
//		{
//			case 1: 
//			{
//				if (this.thumbDist >= distance)
//					this.thumbDist = distance;
//				break;
//			}
//			case 2: 
//			{
//				if (this.indexDist >= distance)
//					this.indexDist = distance;
//				break;
//			}
//			case 3: 
//			{
//				if (this.middleDist >= distance)
//					this.middleDist = distance;
//				break;
//			}
//			case 4: 
//			{
//				if (this.smallDist >= distance)
//					this.smallDist = distance;
//				break;
//			}			
//		}
		//System.out.println("dist " + this.thumbDist + " " + this.middleDist);
	}
	public double getDistanceSum()
	{
//		return this.thumbDist + this.indexDist + this.middleDist + this.smallDist;
		double sum = 0;
		for (double dist : distances)
		{
			sum += dist;
		}
		return sum;
	}
	public String getName()
	{
		return this.gestureName;
	}
	public void setMax()
	{
		for (double max : distances)
		{
			max = Double.MAX_VALUE;
		}
//		this.thumbDist =  Double.MAX_VALUE;
//		this.indexDist = Double.MAX_VALUE;
//		this.middleDist = Double.MAX_VALUE;
//		this.smallDist = Double.MAX_VALUE; 
	}

	public int getGestureId() {
		return this.gestureId;
	}
	public int getEventId()
	{
		return this.eventId;
	}
	public void setFrameId(int id)
	{
		this.frameID = id;
	}
	public int getHandId()
	{
		return this.handID;
	}
	public int getFrameId()
	{
		return this.frameID;
	}
	public void decRecoCounter()
	{
		if (this.recoCounter > 0)
			this.recoCounter -= 1;
	}
	public void decRecoCounter(int i)
	{
		if (this.recoCounter >= i)
		   this.recoCounter -= i;
		else
			this.recoCounter = 0;
	}
	public void incRecoCounter()
	{
		if (this.recoCounter < 7)
		    this.recoCounter += 1;
	}
	public void incRecoCounter(int i)
	{
		if (this.recoCounter < 7)
			this.recoCounter += i;
	}
	public void incTuioCounter()
	{
		this.tuioCounter++;
	}
	public int getTuioCounter()
	{
		return this.tuioCounter;
	}
	public void setRecoCounter(int r)
	{
		this.recoCounter = r;
	}
	public int getRecoCounter()
	{
		return this.recoCounter;
	}
	public void incFrameID()
	{
		this.frameID += 1;
	}
	

}
