package org.squidy.nodes.optitrack.multicursor;

import java.awt.Point;

public class CursorThread extends Thread {

	
	private CursorRunnable cRunnable;
	
	public CursorThread() {
		// TODO Auto-generated constructor stub
	}

	public CursorThread(Runnable target) {
		super(target);
		this.cRunnable = (CursorRunnable) target;
		// TODO Auto-generated constructor stub
	}
	
	public int getCursorID()
	{
		return cRunnable.getCursorID();
	}
	public boolean isReadyToDestroy()
	{
		return cRunnable.isReadyToDestroy();
	}
	public void isReadyToDestroy(boolean ird)
	{
		cRunnable.isReadyToDestroy(ird);
	}
	public void updateLocation(Point p)
	{
		synchronized(this)
		{
			cRunnable.updateLocation(p);
			this.notify();
		}
	}
}
