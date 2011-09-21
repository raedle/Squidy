package org.squidy.nodes.g2drecognizer;

public class G2DSize {
	public double Width;
	public double Height;

	public G2DSize(double width, double height)
	{
		this.Width = width;
		this.Height = height;
	}

	// copy constructor
	public G2DSize(G2DSize sz)
	{
		this.Width = sz.Width;
		this.Height = sz.Height;
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof G2DSize)
		{
			G2DSize sz = (G2DSize) obj;
			return (Width == sz.Width && Height == sz.Height);
		}
		return false;
	}
}
