package org.squidy.nodes.g2drecognizer;

import java.io.Serializable;

public class G2DPoint implements Serializable{

	private static final long serialVersionUID = -3508731775712710807L;
	
	public double X, Y;
	public int T = 0;
	
	public G2DPoint(double x, double y){
		this.X = x;
		this.Y = y;
	}
	
	public G2DPoint(double x, double y, int t){
		this.X = x;
		this.Y = y;
		this.T = t;
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof G2DPoint)
		{
			G2DPoint p = (G2DPoint) obj;
			return (X == p.X && Y == p.Y);
		}
		return false;
	}
	
}
