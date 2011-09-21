
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

public class TuioObject extends TuioContainer {
	
	protected int fiducialId;
	protected float angle;
	protected float rotationSpeed, rotationAcceleration;
	
	public TuioObject (long s_id, int f_id, float xpos, float ypos, float angle) {
		super(s_id,xpos,ypos);
		this.fiducialId = f_id;
		this.angle = angle;
	}

	public TuioObject (TuioObject tuioObject) {
		super(tuioObject);
		this.fiducialId = tuioObject.getFiducialID();
		this.angle = tuioObject.getAngle();
	}
	
	public void update (float xpos, float ypos, float angle, float xspeed, float yspeed, float rspeed, float maccel, float raccel) {
		super.update(xpos,ypos,xspeed,yspeed,maccel);
		this.angle = angle;
		this.rotationSpeed = rspeed;
		this.rotationAcceleration = raccel;
	}

	public void update (TuioObject tuioObject) {
		super.update(tuioObject);
		this.angle = tuioObject.getAngle();
		this.rotationSpeed = tuioObject.getRotationSpeed();
		this.rotationAcceleration = tuioObject.getRotationAccel();
	}
	
	public int getFiducialID() {
		return fiducialId;
	}
		
	public float getAngle() {
		return angle;
	}

	public float getAngleDegrees() {
		return angle/(float)Math.PI*180.0f;
	}
	
	public float getRotationSpeed() {
		return rotationSpeed;
	}
		
	public float getRotationAccel() {
		return rotationAcceleration;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		
		sb.append("[TuioObject{fiducialId=" + fiducialId + ", angle=" + angle + ", rotationSpeed=" + rotationSpeed + ", rotationAcceleration=" + rotationAcceleration + "}]");
		
		return sb.toString();
	}
}
