/**
 * Squidy Interaction Library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Squidy Interaction Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Squidy Interaction Library. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * 2009 Human-Computer Interaction Group, University of Konstanz.
 * <http://hci.uni-konstanz.de>
 * 
 * Please contact info@squidy-lib.de or visit our website
 * <http://www.squidy-lib.de> for further information.
 */

package org.squidy.nodes.recorder;

import org.squidy.nodes.DataRecorder;
import org.squidy.nodes.DataRecorder.ReplayModeDomainProvider;
import org.squidy.nodes.recorder.LoggingObjectFactory.LoggingObject;

public class DataPlayer extends Thread {
	
	public static final long TIMESTAMP_SUBTRACT_CORRECTION = 3600000; //Need to subtract because otherwise clock will show 01:00:... instead of 00:00:...
	
	private DataRecorder recorder;
	private RingBuffer<LoggingObject> buffer;
	private boolean pause = false; //set to true when pause is pressed
	private boolean singleStep = false; //used for step-by-step replay
	private boolean keepAlive = true; //set to false if player was stopped or closed
	private boolean ignorePause = false;
	
	//Header data
	private long duration;
	private long totalPauseTime;
	
	//if pauses are ignored, the duration of the pauses that are already over need to be subtracted from the current time
	private long currentPauseTime = 0;
	private long pauseStarted = -1;
	
	//stuff for calculating the slider position
	private long timePlayed = 0;
	private long lastTimeCalc = 0;
	
	public DataPlayer(DataRecorder rec, RingBuffer<LoggingObject> b, boolean ignorePause) {
		this.recorder = rec;
		this.buffer = b;
		this.ignorePause = ignorePause;
	}
	
	public synchronized void run() {
		lastTimeCalc = System.currentTimeMillis();
		long startTime = lastTimeCalc;
		
		long timeDifference = -1; // time difference between current time (the time replay started) and the timestamp of the first package
		
		while(buffer.getCurrentHeader() == null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String header = buffer.getCurrentHeader();
		String[] headerData = header.split(",");
		duration = Long.parseLong(headerData[1]);
		
		if (headerData.length > 2) {
			totalPauseTime = Long.parseLong(headerData[2]);
		}
		
		if(recorder.getReplayMode() == DataRecorder.REPLAYMODE_DEFAULT){
			duration = duration - totalPauseTime;
		}
		
		while(keepAlive) {
			while (pause) { 
				try {
					calculateTimeOver(System.currentTimeMillis());
					wait();
					lastTimeCalc = System.currentTimeMillis();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			LoggingObject l = null;
			try {
				l = buffer.dequeue();
			} catch (InterruptedException e) { }
			if(l == null) { 
				recorder.playerHasfinished();
				break;
			}
			if(timeDifference == -1){
				timeDifference = startTime - l.getTimestamp();
			}
			
			//Depending on replay mode, ignore pause or not
			if(l.getType() == LoggingObject.TYPE_PAUSE && recorder.getReplayMode() == DataRecorder.REPLAYMODE_DEFAULT) {
				if(pauseStarted == -1) {
					pauseStarted = l.getTimestamp();
				}
				else {
					currentPauseTime += l.getTimestamp()-pauseStarted;
					pauseStarted = -1;
				}
				continue;
			}
			
			while((l.getTimestamp() + timeDifference - currentPauseTime) > (startTime + timePlayed)) {
				while (pause) {
					try {
						calculateTimeOver(System.currentTimeMillis());
						wait();
						lastTimeCalc = System.currentTimeMillis();
						timeDifference = System.currentTimeMillis() - l.getTimestamp(); //
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				calculateTimeOver(System.currentTimeMillis());
				
				float percentagePlayed = (float) timePlayed / duration;
				recorder.updateGUI(percentagePlayed, timePlayed - TIMESTAMP_SUBTRACT_CORRECTION);
			}
			calculateTimeOver(System.currentTimeMillis());
			float percentagePlayed = (float) timePlayed / duration;
			recorder.updateGUI(percentagePlayed, timePlayed);
			
			if(l.getType() == LoggingObject.TYPE_DATA)
				recorder.doPublish(l.getDataContainer());
			
			if(singleStep){
				pause = true;
			}
		}
		//when reaching this part, the player has either been stopped or the file has ended. Clear the buffer
		buffer = null;
	}
	
	private synchronized void calculateTimeOver(long timestamp) {
		timePlayed += timestamp - lastTimeCalc;
		lastTimeCalc = timestamp;
	}
	
	public void pausePlayer() {
		pause = true;
	}

	public synchronized void proceed() {
		pause = false;
		singleStep = false;
		notify();
	}
	
	public void stepForward() {
		singleStep = true;
		pause = false;
		synchronized(this) {
			if(this.getState() == State.WAITING)
				notify();
		}
	}
	
	public void stopPlayer() {
		keepAlive = false;
		pause = false;
		synchronized(this) {
			notify();
		}
	}

	/**
	 * Sets the ignore pause option of the data player which can ignore pauses when playing back interaction
	 * data. Pauses will be ignored when parameter is set to true and takes pauses into account when parameter
	 * is set to false.
	 * 
	 * @param ignorePause Whether pauses should be taken into account (false) or not (true).
	 */
	public void setIgnorePause(boolean ignorePause) {
		this.ignorePause = ignorePause;
	}

	public boolean isIgnorePause() {
		return ignorePause;
	}
	
	
}
