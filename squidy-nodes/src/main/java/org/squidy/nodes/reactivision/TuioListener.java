
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

public interface TuioListener {

	public void addTuioObject(TuioObject tuioObject);
	public void updateTuioObject(TuioObject tuioObject);
	public void removeTuioObject(TuioObject tuioObject);

	public void addTuioCursor(TuioCursor tuioCursor);
	public void updateTuioCursor(TuioCursor tuioCursor);
	public void removeTuioCursor(TuioCursor tuioCursor);

	public void refresh(long timestamp);
}