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
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.squidy.manager.commander.command.ICommand;


/**
 * <code>ControlClient</code>.
 * 
 * <pre>
 * Date: Sep 22, 2008
 * Time: 12:21:20 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: ControlClient.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class ControlClient extends Thread {

	private Socket socket;
	private ObjectOutputStream outputStream;

	private final Object lock = new Object();
	private Queue<ICommand> commands = new ConcurrentLinkedQueue<ICommand>();

	private boolean running;
	
	public ControlClient(InetAddress address, int port) throws IOException {
		socket = new Socket(address, port);
		socket.setTcpNoDelay(true);
		outputStream = new ObjectOutputStream(socket.getOutputStream());

		running = true;
		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (running) {
			synchronized (lock) {
				try {
					lock.wait();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}

				while (!commands.isEmpty()) {
					ICommand command = commands.poll();

					try {
						outputStream.writeObject(command);
						outputStream.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		try {
			outputStream.flush();
			outputStream.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(ICommand command) {
		commands.add(command);

		synchronized (lock) {
			lock.notifyAll();
		}
	}

	public void close() {
		running = false;

		synchronized (lock) {
			lock.notifyAll();
		}
		
		try {
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
