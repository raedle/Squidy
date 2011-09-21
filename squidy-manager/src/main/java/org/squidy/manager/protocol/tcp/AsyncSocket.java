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

package org.squidy.manager.protocol.tcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>AsyncSocket</code>.
 * 
 * <pre>
 * Date: Jan 3, 2009
 * Time: 4:44:07 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: AsyncSocket.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0
 */
public class AsyncSocket extends Socket {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(AsyncSocket.class);

	public static final byte[] CRLF = new byte[] { 0x0D, 0x0A };

	private final Queue<Enum<?>> tagQueue = new ConcurrentLinkedQueue<Enum<?>>();

	private ByteArrayOutputStream outputBuffer;

	private InputStream inputStream;
	private OutputStream outputStream;
	// private byte[] cachedBuffer;

	private static final int MAX_BUFFER_SIZE = 1024;

	public AsyncSocket(AsyncSocketCallback callback, InetAddress address, int port) throws IOException {
		super(address, port);

		setTcpNoDelay(true);

		inputStream = getInputStream();
		outputStream = getOutputStream();

		callback.ready(this);
	}

	/**
	 * @param sequence
	 * @throws IOException
	 */
	public void readToByteSequence(final AsyncSocketCallback callback, final byte[] sequence, final Enum<?> tag) {

		// Add current tag to tag queue to identify current read job.
		tagQueue.add(tag);

		new Thread() {

			@Override
			public void run() {
				// Length of the sequence.
				int sequenceSize = sequence.length;
				int currentSequencePosition = 0;
				outputBuffer = new ByteArrayOutputStream();

				// if (cachedBuffer != null) {
				// boolean processFinished = processReadToByteSequenceBuffer(
				// callback, sequence, cachedBuffer,
				// cachedBuffer.length, sequenceSize,
				// currentSequencePosition);
				//
				// if (processFinished) {
				// return;
				// } else {
				// cachedBuffer = null;
				// }
				// }

				// int bufferSize = MAX_BUFFER_SIZE;
				// byte[] buffer = new byte[bufferSize];
				int read;
				try {
					while ((read = inputStream.read()) != -1) {
						// boolean processFinished =
						// processReadToByteSequenceBuffer(callback, sequence,
						// buffer, readBytes, sequenceSize,
						// currentSequencePosition);
						//						
						// if (processFinished) {
						// return;
						// }

						if (sequence[currentSequencePosition] == read) {
							currentSequencePosition++;
						}
						else {
							currentSequencePosition = 0;
						}

						outputBuffer.write(read);

						// System.out.println("STREAM: " + new
						// String(outputBuffer.toByteArray()));

						if (sequenceSize == currentSequencePosition) {
							byte[] data = outputBuffer.toByteArray();
							outputBuffer = null;
							callback.didReadToByteSequence(AsyncSocket.this, data, tagQueue.poll());

							return;
						}
					}
				}
				catch (IOException e) {
					callback.disconnected(AsyncSocket.this);
				}
			}
		}.start();
	}

	// /**
	// * @param callback
	// * @param sequence
	// * @param buffer
	// * @param readBytes
	// * @param sequenceSize
	// * @param currentSequencePosition
	// * @return
	// */
	// private boolean processReadToByteSequenceBuffer(AsyncSocketCallback
	// callback, byte[] sequence, byte[] buffer, int readBytes, int
	// sequenceSize, int currentSequencePosition) {
	// for (int i = 0; i < readBytes; i++) {
	// if (sequence[currentSequencePosition] == buffer[i]) {
	// currentSequencePosition++;
	// } else {
	// currentSequencePosition = 0;
	// }
	//
	// outputBuffer.write(buffer[i]);
	//
	// if (sequenceSize == currentSequencePosition) {
	// int restBytes = readBytes - i - 1;
	// if (restBytes > 0) {
	// cachedBuffer = new byte[restBytes];
	// System.arraycopy(buffer, i, cachedBuffer, 0, restBytes);
	// }
	//				
	// byte[] data = outputBuffer.toByteArray();
	// outputBuffer = null;
	// callback.didReadToByteSequence(AsyncSocket.this, data);
	//
	// return true;
	// }
	// }
	// return false;
	// }

	public void readBytes(final AsyncSocketCallback callback, final int bytesToRead, final Enum<?> tag) {

		// Add current tag to tag queue to identify current read job.
		tagQueue.add(tag);

		new Thread() {

			@Override
			public void run() {
				// Length of the sequence.
				int hasToRead = bytesToRead;
				outputBuffer = new ByteArrayOutputStream();

				int bufferSize = MAX_BUFFER_SIZE;
				byte[] buffer = new byte[bufferSize];
				int readBytes;
				try {
					while ((readBytes = inputStream.read(buffer, 0, (hasToRead < MAX_BUFFER_SIZE) ? hasToRead
							: MAX_BUFFER_SIZE)) != -1) {

						outputBuffer.write(buffer, 0, readBytes);

						hasToRead -= readBytes;

						if (hasToRead == 0) {
							byte[] data = outputBuffer.toByteArray();
							outputBuffer = null;
							callback.didReadBytes(AsyncSocket.this, data, tagQueue.poll());

							return;
						}
					}
				}
				catch (IOException e) {
					callback.disconnected(AsyncSocket.this);
				}
				catch (ArrayIndexOutOfBoundsException e) {
					try {
						if (LOG.isWarnEnabled()) {
							LOG.warn("Clearing already streamed data caused by: ", e);
						}

						while (inputStream.read() != -1) {
							callback.ready(AsyncSocket.this);
						}
					}
					catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}.start();
	}

	/**
	 * @param b
	 * @throws IOException
	 */
	public void write(int b) throws IOException {
		outputStream.write(b);
	}

	/**
	 * @param b
	 * @throws IOException
	 */
	public void write(byte[] b) throws IOException {
		outputStream.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		outputStream.write(b, off, len);
	}

	/**
	 * @throws IOException
	 */
	public void flush() throws IOException {
		outputStream.flush();
	}
}
