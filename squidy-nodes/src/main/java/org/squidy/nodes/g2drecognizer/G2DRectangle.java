package org.squidy.nodes.g2drecognizer;

public class G2DRectangle {
	
	public double X, Y, Width, Height;

	public G2DRectangle(double x, double y, double width, double height){
		this.X = x;
		this.Y = y;
		this.Width = width;
		this.Height = height;
	}
	
	public G2DPoint getTopLeft(){
		return new G2DPoint(X, Y);
	}

	public G2DPoint getBottomRight(){
		return new G2DPoint(X + Width, Y + Height);
	}

	public G2DPoint getCenter(){
		return new G2DPoint(X + Width / 2d, Y + Height / 2d);
	}

	public double getMaxSide(){
		return Math.max(Width, Height);
	}

	public double getMinSide(){
		return Math.min(Width, Height);
	}

	public double getDiagonal(){
		return G2DUtils.Distance(getTopLeft(), getBottomRight());
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof G2DRectangle) {
			G2DRectangle r = (G2DRectangle) obj;
			return (X == r.X && Y == r.Y && Width == r.Width && Height == r.Height);
		}
		return false;
	}
}
