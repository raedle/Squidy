
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

import java.util.*;

abstract class TuioContainer extends TuioPoint {

	protected long session_id;
	protected float x_speed, y_speed;
	protected float motion_speed;	
	protected float motion_accel;	
	
	protected Vector<TuioPoint> path;

	public static final int TUIO_ADDED = 0;
	public static final int TUIO_UPDATED = 1;
	public static final int TUIO_REMOVED = 2;
	protected int state;
	
	TuioContainer(long s_id, float xpos, float ypos) {
		super(xpos,ypos);
		
		this.session_id = s_id;
		this.x_speed = 0.0f;
		this.y_speed = 0.0f;
		this.motion_speed = 0.0f;
		this.motion_accel = 0.0f;
		
		path = new Vector<TuioPoint>();
		path.addElement(new TuioPoint(xpos,ypos));
		state = TUIO_ADDED;
	}

	TuioContainer(TuioContainer tuioContainer) {
		super(tuioContainer);
		
		this.session_id = tuioContainer.getSessionID();
		this.x_speed = 0.0f;
		this.y_speed = 0.0f;
		this.motion_speed = 0.0f;
		this.motion_accel = 0.0f;
		
		path = new Vector<TuioPoint>();
		path.addElement(new TuioPoint(xpos,ypos));
		state = TUIO_ADDED;
	}
	
	public void update(float xpos, float ypos,float xspeed,float yspeed,float maccel) {
		super.update(xpos,ypos);
		this.x_speed = xspeed;
		this.y_speed = yspeed;
		this.motion_speed = (float)Math.sqrt(x_speed*x_speed+y_speed*y_speed);
		this.motion_accel = maccel;
		path.addElement(new TuioPoint(xpos,ypos));
		state = TUIO_UPDATED;
	}

	public void update (TuioContainer tuioContainer) {
		super.update(tuioContainer);
		x_speed = tuioContainer.getSpeedX();
		y_speed = tuioContainer.getSpeedY();
		motion_speed = tuioContainer.getMotionSpeed();
		this.motion_accel =  tuioContainer.getMotionAccel();
		path.addElement(new TuioPoint(xpos,ypos));
		state = TUIO_UPDATED;
	}
	
	public void remove() {
		state = TUIO_REMOVED;
		timestamp = TUIO_UNDEFINED;
	}
	
	public long getSessionID() {
		return session_id;
	}
		
	public float getSpeedX() {
		return x_speed;
	}
	
	public float getSpeedY() {
		return y_speed;
	}
	
	public TuioPoint getPosition() {
		return new TuioPoint(xpos,ypos);
	}
		
	public Vector<TuioPoint> getPath() {
		return path;
	}
	
	public float getMotionSpeed() {
		return motion_speed;
	}
	
	public float getMotionAccel() {
		return motion_accel;
	}
	
	public int getState() {
		return state;
	}
	
	protected void setUpdateTime(long timestamp) {
		this.timestamp = timestamp;
		TuioPoint lastPoint = path.lastElement();
		if (lastPoint!=null) lastPoint.setUpdateTime(timestamp);
	} 
	
	
}
