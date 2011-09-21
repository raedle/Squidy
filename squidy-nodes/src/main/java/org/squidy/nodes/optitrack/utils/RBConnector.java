package org.squidy.nodes.optitrack.utils;

import java.util.ArrayList;

import org.squidy.manager.data.IData;
import org.squidy.manager.data.impl.DataPosition3D;


public class RBConnector {

	private int index;
	private ArrayList<IData> d3dList;
	public RBConnector(int index, ArrayList<IData> d3d)
	{
		this.index = index;
		this.d3dList = d3d;
	}
	
	public ArrayList<IData> getIndex(int index)
	{
		if (index == this.index)
			return d3dList;
		else 
			return null;
	}
	public ArrayList<IData> getIndex()
	{
		return d3dList;
	}	
}
