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

package org.squidy.manager.commander;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.squidy.manager.commander.command.ICommand;


/**
 * <code>Outgoing</code>.
 *
 * <pre>
 * Date: Sep 20, 2008
 * Time: 10:52:57 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: Outgoing.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class Outgoing extends Thread {

	private ControlServerContext context;
	
	private final Object LOCK = new Object();
	
	private boolean running;
	
	private Queue<ICommand> commandQueue = new ConcurrentLinkedQueue<ICommand>();
	
	private ObjectOutputStream outputStream;
	
	public Outgoing(OutputStream outputStream, ControlServerContext context) throws IOException {
		this.outputStream = new ObjectOutputStream(outputStream);
		this.context = context;
		running = true;
		
		start();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		
		while (running) {
			synchronized (LOCK) {
				try {
					LOCK.wait();
					
					while (!commandQueue.isEmpty()) {
						ICommand command = commandQueue.poll();
						
						try {
							outputStream.writeObject(command);
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void close() throws IOException {
		commandQueue.clear();
		outputStream.close();
		running = false;
		LOCK.notifyAll();
	}
	
	/**
	 * @param command
	 * @return
	 */
	public ICommand send(ICommand command) {
		commandQueue.add(command);
		
		synchronized (LOCK) {
			LOCK.notifyAll();
		}
		return null;
	}
}
