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

import java.io.BufferedReader;
import java.io.IOException;

import org.squidy.nodes.recorder.LoggingObjectFactory;
import org.squidy.nodes.recorder.LoggingObjectFactory.LoggingObject;


public class BufferLoader extends Thread {
	
	private RingBuffer<LoggingObject> buffer;
	private BufferedReader in;
	private boolean keepAlive = true;
	
	public BufferLoader(RingBuffer<LoggingObject> b, BufferedReader r) {
		buffer = b;
		in = r;
	}
	
	public void run() {
		if(in == null) {
			return;
		}
		boolean headerRead = false;
		while(keepAlive) {
			//if(buffer.hasSpaceAvailable()) {
			String line = "";
			try {
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(line == null) {	
				keepAlive = false;
				buffer.setFileRead(true);
				break;
			}
			if(!headerRead) {
				buffer.setCurrentHeader(line);
				headerRead = true;
				continue;
			}
			LoggingObject l = LoggingObjectFactory.getInstance().getLoggingObject();
			l.deserialize(line);
			try {
				buffer.enqueue(l);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void terminate(){
		keepAlive = false;
	}
	
}
