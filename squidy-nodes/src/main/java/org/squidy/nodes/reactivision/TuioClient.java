
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

import com.illposed.osc.*;

import java.util.*;

public class TuioClient implements OSCListener {

	private int port = 3333;
	private OSCPortIn oscPort;
	private Hashtable<Long, TuioObject> objectList = new Hashtable<Long, TuioObject>();
	private Vector<Long> aliveObjectList = new Vector<Long>();
	private Vector<Long> newObjectList = new Vector<Long>();
	private Hashtable<Long, TuioCursor> cursorList = new Hashtable<Long, TuioCursor>();
	private Vector<Long> aliveCursorList = new Vector<Long>();
	private Vector<Long> newCursorList = new Vector<Long>();

	private Vector<TuioCursor> freeCursorList = new Vector<TuioCursor>();
	private int maxFingerID = -1;

	private long currentFrame = 0;
	private long lastFrame = 0;
	private long startTime = 0;
	private long lastTime = 0;

	private final int UNDEFINED = -1;

	private Vector<TuioListener> listenerList = new Vector<TuioListener>();

	public TuioClient(int port) {
		this.port = port;
	}

	public TuioClient() {
	}

	public void connect() {
		try {
			oscPort = new OSCPortIn(port, Endian.LITTLE_ENDIAN);
			oscPort.addListener("/tuio/2Dobj", this);
			oscPort.addListener("/tuio/2Dcur", this);
			oscPort.startListening();
			startTime = System.currentTimeMillis();
		}
		catch (Exception e) {
			System.out.println("failed to connect to port " + port);
		}
	}

	public void disconnect() {
		oscPort.stopListening();
		try {
			Thread.sleep(100);
		}
		catch (Exception e) {
		}
		;
		oscPort.close();
	}

	public void addTuioListener(TuioListener listener) {
		listenerList.addElement(listener);
	}

	public void removeTuioListener(TuioListener listener) {
		listenerList.removeElement(listener);
	}

	public Vector<TuioObject> getTuioObjects() {
		return new Vector<TuioObject>(objectList.values());
	}

	public Vector<TuioCursor> getTuioCursors() {
		return new Vector<TuioCursor>(cursorList.values());
	}

	public TuioObject getTuioObject(long s_id) {
		return (TuioObject) objectList.get(new Long(s_id));
	}

	public TuioCursor getTuioCursor(long s_id) {
		return (TuioCursor) cursorList.get(new Long(s_id));
	}

	public void acceptMessages(Date date, OSCMessage[] messages) {

		for (OSCMessage message : messages) {
			Object[] args = message.getArguments();
			String command = (String) args[0];
			String address = message.getAddress();
			if (address.equals("/tuio/2Dobj")) {

				if (command.equals("set")) {
					if ((currentFrame < lastFrame) && (currentFrame > 0))
						return;
					long s_id = ((Integer) args[1]).longValue();
					int f_id = ((Integer) args[2]).intValue();
					float xpos = ((Float) args[3]).floatValue();
					float ypos = ((Float) args[4]).floatValue();
					float angle = ((Float) args[5]).floatValue();
					float xspeed = ((Float) args[6]).floatValue();
					float yspeed = ((Float) args[7]).floatValue();
					float rspeed = ((Float) args[8]).floatValue();
					float maccel = ((Float) args[9]).floatValue();
					float raccel = ((Float) args[10]).floatValue();

					if (objectList.get(s_id) == null) {

						TuioObject addObject = new TuioObject(s_id, f_id, xpos, ypos, angle);
						objectList.put(s_id, addObject);

						for (int i = 0; i < listenerList.size(); i++) {
							TuioListener listener = (TuioListener) listenerList.elementAt(i);
							if (listener != null)
								listener.addTuioObject(addObject);
						}
					}
					else {

						TuioObject updateObject = (TuioObject) objectList.get(s_id);
						if ((updateObject.xpos != xpos) || (updateObject.ypos != ypos) || (updateObject.angle != angle)
								|| (updateObject.x_speed != xspeed) || (updateObject.y_speed != yspeed)
								|| (updateObject.rotationSpeed != rspeed) || (updateObject.motion_accel != maccel)
								|| (updateObject.rotationAcceleration != raccel)) {
							updateObject.update(xpos, ypos, angle, xspeed, yspeed, rspeed, maccel, raccel);
							for (int i = 0; i < listenerList.size(); i++) {
								TuioListener listener = (TuioListener) listenerList.elementAt(i);
								if (listener != null)
									listener.updateTuioObject(updateObject);
							}
						}

					}

					// System.out.println("set obj "
					// +s_id+" "+f_id+" "+xpos+" "+ypos+" "+angle+" "+xspeed+" "+yspeed+" "+rspeed+" "+maccel+" "+raccel);

				}
				else if (command.equals("alive")) {
					if ((currentFrame < lastFrame) && (currentFrame > 0))
						return;

					for (int i = 1; i < args.length; i++) {
						// get the message content
						long s_id = ((Integer) args[i]).longValue();
						newObjectList.addElement(s_id);
						// reduce the object list to the lost objects
						if (aliveObjectList.contains(s_id))
							aliveObjectList.removeElement(s_id);
					}

					// remove the remaining objects
					for (int i = 0; i < aliveObjectList.size(); i++) {
						TuioObject removeObject = (TuioObject) objectList.remove(aliveObjectList.elementAt(i));
						if (removeObject == null)
							continue;
						removeObject.remove();
						// System.out.println("remove "+id);
						for (int j = 0; j < listenerList.size(); j++) {
							TuioListener listener = (TuioListener) listenerList.elementAt(j);
							if (listener != null)
								listener.removeTuioObject(removeObject);
						}
					}

					Vector<Long> buffer = aliveObjectList;
					aliveObjectList = newObjectList;

					// recycling of the vector
					newObjectList = buffer;
					newObjectList.clear();

				}
				else if (command.equals("fseq")) {
					if (currentFrame >= 0)
						lastFrame = currentFrame;
					currentFrame = ((Integer) args[1]).intValue();

					if ((currentFrame >= lastFrame) || (currentFrame < 0)) {

						long currentTime = lastTime;
						if (currentFrame > lastFrame) {
							currentTime = System.currentTimeMillis() - startTime;
							lastTime = currentTime;
						}

						Enumeration<TuioObject> refreshList = objectList.elements();
						while (refreshList.hasMoreElements()) {
							TuioObject refreshObject = refreshList.nextElement();
							if (refreshObject.getUpdateTime() == UNDEFINED)
								refreshObject.setUpdateTime(currentTime);
						}

						for (int i = 0; i < listenerList.size(); i++) {
							TuioListener listener = (TuioListener) listenerList.elementAt(i);
							if (listener != null)
								listener.refresh(currentTime);
						}
					}
				}

			}
			else if (address.equals("/tuio/2Dcur")) {

				if (command.equals("set")) {
					if ((currentFrame < lastFrame) && (currentFrame > 0))
						return;

					long s_id = ((Integer) args[1]).longValue();
					float xpos = ((Float) args[2]).floatValue();
					float ypos = ((Float) args[3]).floatValue();
					float xspeed = ((Float) args[4]).floatValue();
					float yspeed = ((Float) args[5]).floatValue();
					float maccel = ((Float) args[6]).floatValue();

					if (cursorList.get(s_id) == null) {

						int f_id = cursorList.size();
						if (cursorList.size() <= maxFingerID) {
							TuioCursor closestCursor = freeCursorList.firstElement();
							Enumeration<TuioCursor> testList = freeCursorList.elements();
							while (testList.hasMoreElements()) {
								TuioCursor testCursor = testList.nextElement();
								if (testCursor.getDistance(xpos, ypos) < closestCursor.getDistance(xpos, ypos))
									closestCursor = testCursor;
							}
							f_id = closestCursor.getFingerID();
							freeCursorList.removeElement(closestCursor);
						}
						else
							maxFingerID = f_id;

						TuioCursor addCursor = new TuioCursor(s_id, f_id, xpos, ypos);
						cursorList.put(s_id, addCursor);

						for (int i = 0; i < listenerList.size(); i++) {
							TuioListener listener = (TuioListener) listenerList.elementAt(i);
							if (listener != null)
								listener.addTuioCursor(addCursor);
						}
					}
					else {

						TuioCursor updateCursor = (TuioCursor) cursorList.get(s_id);
						if ((updateCursor.xpos != xpos) || (updateCursor.ypos != ypos)
								|| (updateCursor.x_speed != xspeed) || (updateCursor.y_speed != yspeed)
								|| (updateCursor.motion_accel != maccel)) {

							updateCursor.update(xpos, ypos, xspeed, yspeed, maccel);
							for (int i = 0; i < listenerList.size(); i++) {
								TuioListener listener = (TuioListener) listenerList.elementAt(i);
								if (listener != null)
									listener.updateTuioCursor(updateCursor);
							}
						}
					}

					// System.out.println("set cur " +
					// s_id+" "+xpos+" "+ypos+" "+xspeed+" "+yspeed+" "+maccel);

				}
				else if (command.equals("alive")) {
					if ((currentFrame < lastFrame) && (currentFrame > 0))
						return;

					for (int i = 1; i < args.length; i++) {
						// get the message content
						long s_id = ((Integer) args[i]).longValue();
						newCursorList.addElement(s_id);
						// reduce the cursor list to the lost cursors
						if (aliveCursorList.contains(s_id))
							aliveCursorList.removeElement(s_id);
					}

					// remove the remaining cursors
					for (int i = 0; i < aliveCursorList.size(); i++) {
						TuioCursor removeCursor = (TuioCursor) cursorList.remove(aliveCursorList.elementAt(i));
						if (removeCursor == null)
							continue;
						removeCursor.remove();

						if (removeCursor.finger_id == maxFingerID) {
							maxFingerID = -1;
							if (cursorList.size() > 0) {
								Enumeration<TuioCursor> clist = cursorList.elements();
								while (clist.hasMoreElements()) {
									int f_id = clist.nextElement().finger_id;
									if (f_id > maxFingerID)
										maxFingerID = f_id;
								}

								Enumeration<TuioCursor> flist = freeCursorList.elements();
								while (flist.hasMoreElements()) {
									int c_id = flist.nextElement().getFingerID();
									if (c_id >= maxFingerID)
										freeCursorList.removeElement(c_id);
								}
							}
						}
						else if (removeCursor.finger_id < maxFingerID)
							freeCursorList.addElement(removeCursor);

						// System.out.println("remove "+id);
						for (int j = 0; j < listenerList.size(); j++) {
							TuioListener listener = (TuioListener) listenerList.elementAt(j);
							if (listener != null)
								listener.removeTuioCursor(removeCursor);
						}
					}

					Vector<Long> buffer = aliveCursorList;
					aliveCursorList = newCursorList;

					// recycling of the vector
					newCursorList = buffer;
					newCursorList.clear();
				}
				else if (command.equals("fseq")) {
					if (currentFrame >= 0)
						lastFrame = currentFrame;
					currentFrame = ((Integer) args[1]).intValue();

					if ((currentFrame >= lastFrame) || (currentFrame < 0)) {
						long currentTime = lastTime;
						if (currentFrame > lastFrame) {
							currentTime = System.currentTimeMillis() - startTime;
							lastTime = currentTime;
						}

						Enumeration<TuioCursor> refreshList = cursorList.elements();
						while (refreshList.hasMoreElements()) {
							TuioCursor refreshCursor = refreshList.nextElement();
							if (refreshCursor.getUpdateTime() == UNDEFINED)
								refreshCursor.setUpdateTime(currentTime);
						}

						for (int i = 0; i < listenerList.size(); i++) {
							TuioListener listener = (TuioListener) listenerList.elementAt(i);
							if (listener != null)
								listener.refresh(currentTime);
						}
					}
				}

			}
		}
	}
}
