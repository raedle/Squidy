
package org.squidy.nodes.reactivision;
/*
	TUIO Java backend - part of the reacTIVision project
	http://reactivision.sourceforge.net/

	Copyright (c) 2005-2008 Martin Kaltenbrunner <mkalten@iua.upf.edu>

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

public class TuioPoint {
	
	public static final int TUIO_UNDEFINED = -1;
	protected long timestamp;
	
	protected float xpos, ypos;
	
	public TuioPoint(float xpos, float ypos) {
		this.xpos = xpos;
		this.ypos = ypos;
		timestamp = TUIO_UNDEFINED;
	}

	public TuioPoint(TuioPoint tuioPoint) {
		this.xpos = tuioPoint.getX();
		this.ypos = tuioPoint.getY();
		timestamp = TUIO_UNDEFINED;
	}
	
	public void update(float xpos, float ypos) {
		this.xpos = xpos;
		this.ypos = ypos;
		timestamp = TUIO_UNDEFINED;
	}

	public void update(TuioPoint tuioPoint) {
		this.xpos = tuioPoint.getX();
		this.ypos = tuioPoint.getY();
		timestamp = TUIO_UNDEFINED;
	}
	
	public float getX() {
		return xpos;
	}
	
	public float getY() {
		return ypos;
	}
	
	public float getDistance(float x, float y) {
		float dx = xpos-x;
		float dy = ypos-y;
		return (float)Math.sqrt(dx*dx+dy*dy);
	}

	public float getDistance(TuioPoint tuioPoint) {
		float dx = xpos-tuioPoint.getX();
		float dy = ypos-tuioPoint.getY();
		return (float)Math.sqrt(dx*dx+dy*dy);
	}

	public float getAngle(TuioPoint tuioPoint) {
		
		float side = tuioPoint.getX()-xpos;
		float height = tuioPoint.getY()-ypos;
		float distance = tuioPoint.getDistance(xpos,ypos);
		
		float angle = (float)(Math.asin(side/distance)+Math.PI/2);
		if (height<0) angle = 2.0f*(float)Math.PI-angle;
				
		return angle;
	}

	public float getAngleDegrees(TuioPoint tuioPoint) {
		
		return (getAngle(tuioPoint)/(float)Math.PI)*180.0f;
	}
	
	public int getScreenX(int width) {
		return (int)(xpos*width);
	}
	
	public int getScreenY(int height) {
		return (int)(ypos*height);
	}
	
	public long getUpdateTime() {
		return timestamp;
	}
	
	protected void setUpdateTime(long timestamp) {
		this.timestamp = timestamp;
	} 
}
