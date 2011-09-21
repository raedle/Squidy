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

import java.util.Iterator;
import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")

public class RingBuffer<T> implements Iterable<T> {

	private T[] buffer;
	private int count = 0; //number of elements on queue
	private int first = 0; //index of first element
	private int last = 0; //index of last element
	private boolean fileRead = false; //true if file has been read completely
	private String currentHeader;
	

	public RingBuffer(int capacity) {
		buffer = (T[]) new Object[capacity];
	}
	
	public int getSize() {
		return count;
	}
	
	public synchronized boolean isFull() {
		return (count == buffer.length);
	}
	
	public boolean isEmpty() {
		return count==0;
	}
	
	public boolean hasSpaceAvailable() {
		return (count < buffer.length);
	}
	
	public synchronized void enqueue(T item) throws InterruptedException {
		if (isFull()) {
			wait();
		}
		buffer[last] = item;
		last = (last + 1) % buffer.length;
		count++;
		notify();
	}
	
	public synchronized T dequeue() throws InterruptedException{
		if(isEmpty()){
			if(fileRead) {
				notify();
				return null;
			}
			else {
				wait();
			}
		}
		T item = buffer[first];
		buffer[first] = null;
		count--;
		first = (first + 1) % buffer.length;
		notify();
		return item;
	}
	
	public synchronized void setFileRead(boolean b) {
		fileRead = b;
		notify();
	}
	
	public Iterator<T> iterator() {
		return new RingBufferIterator();
	}
	
	private class RingBufferIterator implements Iterator<T> {
		private int i = 0;
		
		public boolean hasNext() {
			return (i < count);
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		public T next() throws NoSuchElementException {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			return buffer[i++];
		}
	}
	
	public String getCurrentHeader() {
		return currentHeader;
	}

	public void setCurrentHeader(String currentHeader) {
		this.currentHeader = currentHeader;
	}
	 
}
