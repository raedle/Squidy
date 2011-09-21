/**
 * Squidy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Squidy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Squidy. If not, see <http://www.gnu.org/licenses/>.
 *
 * 2006-2009 Human-Computer Interaction Group, University of Konstanz.
 * <http://hci.uni-konstanz.de>
 *
 * Please contact info@squidy-lib.de or visit our website http://squidy-lib.de for
 * further information.
 */
/**
 *
 */
package org.squidy.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.manager.data.impl.DataKey;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.keyboard.KeyStrokeMap;

import jserial.JSerial;

/**
 * <code>Shake</code>.
 *
 * <pre>
 * Date: Okt 12, 2009
 * Time: 15:32:29 PM
 * </pre>
 *
 * @author Nicolas Hirrle <a
 *         href="mailto:nihirrle@htwg-konstanz.de">nihirrle@htwg-konstanz.de</a>
 *         Human-Computer Interaction Group University of Konstanz
 * @version $Id: Shake.java 68 2009-11-10 20:22:53Z hirrle $
 * @since 2.0.0
 *
 */
@SuppressWarnings("restriction")
@XmlType(name = "Shake")
@Processor(name = "Shake", icon = "/org/squidy/nodes/image/48x48/shake.png", types = {
		Processor.Type.INPUT, Processor.Type.FILTER, Processor.Type.OUTPUT }, tags = { "shake" })
public class Shake extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################

	@XmlAttribute(name = "ComPort")
	@Property(name = "ComPort", description = "Com Port Number")
	@TextField
	private int port = 6;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

//	@XmlAttribute(name = "ShowCube")
//	@Property(name = "ShowCube", description = "Show Cube for visualization of the Position of the Shake Sensor")
//	@CheckBox
//	private boolean cube = true;
//
//	public boolean isCube() {
//		return cube;
//	}
//
//	public void setCube(boolean cube) {
//		this.cube = cube;
//	}

	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################

	// ################################################################################
	// BEGIN OF LAUNCH
	// ################################################################################

	private long lastMessageReceived = 0;
//	private RotatingCube rotatingCube;
	private JSerial js;
	private boolean stopped = true;
	private ShakeMessage last;
	private Timer timer = new Timer();

	@Override
	public void onStart() throws ProcessException {
		stopped = false;
		new Thread() {
			public void run() {
				if (js == null)
					js = new JSerial();

				boolean open = js.open(port);
				System.out.println("connect: " + open);

				timer.schedule(new CheckConnection(), 10000, 2000);

				if (open)
					read();

			}
		}.start();

		new Thread() {
			public void run() {
//				if (cube)
//					startCube();
			}
		}.start();

	}

	private void startCube() {
		stopped = true;
		new Thread() {
			public void run() {
//				if (rotatingCube == null) {
//					rotatingCube = new RotatingCube();
//					rotatingCube.main(null);
//				}
//					else
//					rotatingCube.setVisible(true);
			}
		}.start();
	}

	@Override
	public void onStop() throws ProcessException {
//		if (rotatingCube != null)
//			rotatingCube.stop();//.setVisible(false);// .destroy();
		if (js != null)
			js.close();

	}

	public void read() {
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		byte[] buf = new byte[256];
		int read = js.readBytes(buf, 256, 0);

		List<String> messages = new LinkedList<String>();
		while (!Thread.interrupted() && Shake.this.isProcessing()) {
			lastMessageReceived = System.currentTimeMillis();
			// System.out.println("read bytes: " + read);
			String message = new String();
			parseMessages(buf, read, messages, message);

			for (String m : messages) {
				StringTokenizer st = new StringTokenizer(m, ",");
				List<String> tokens = new LinkedList<String>();
				while (st.hasMoreElements()) {
					String token = st.nextToken();
					// System.out.println(token);
					tokens.add(token);
				}

				String id = tokens.get(0);
				if (id.equals("$NVC\r\n")) // send DataDigital to signal
											// GestureRecognizerShake that
											// recognition shall start
				{
					vibrate();

					DataDigital dataDigital = new DataDigital(Shake.class, true);
					dataDigital.setAttribute(DataConstant.IDENTIFIER,
							"ShakeSK7");
					publish(dataDigital);
				} else if (id.equals("$NVN\r\n")) // send DataDigital to signal
													// GestureRecognizerShake
													// that recognition shall
													// stop
				{
					vibrate();

					DataDigital dataDigital = new DataDigital(Shake.class,
							false);
					dataDigital.setAttribute(DataConstant.IDENTIFIER,
							"ShakeSK7");
					publish(dataDigital);
				} else if (tokens.size() >= 5
						&& (id.equals("$ACC") || id.equals("$RPH"))) {
					double x = 0;
					double y = 0;
					double z = 0;
					try {
						x = Math.round( Double.parseDouble(tokens.get(1)) * 1000. ) / 1000.;
						y = Math.round( Double.parseDouble(tokens.get(2)) * 1000. ) / 1000.;
						z = Math.round( Double.parseDouble(tokens.get(3)) * 1000. ) / 1000.;
//						x = Double.parseDouble(tokens.get(1));
//						y = Double.parseDouble(tokens.get(2));
//						z = Double.parseDouble(tokens.get(3));
//						if(id.equals("$RPH"))
//							System.out.println("x: " + x + " y: " + y + " z: " + z);
					} catch (NumberFormatException e) {
						continue;
					}

					double messageNumber = 1;
					ShakeMessage sm = new ShakeMessage(id, x, y, z,
							messageNumber);

					if (last == null) {
						last = sm;
					} else {
						if (sm.id
								.equals(ShakeMessageID.ROTATE_PITCH_HITCHHIKING)) {
							DataPosition6D dataPosition6D = new DataPosition6D(
									Shake.class, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
									1000 * sm.x, 1000 * sm.y, 1000 * sm.z, 0.0,
									0.0, 0.0, 0.0, 0.0, 0.0, 0);
							publish(dataPosition6D);

//							if (cube) {
//								rotateCube(sm);
//							}

							last = sm;
							// System.out.println(sm.toString());
						}

						else if (sm.id.equals(ShakeMessageID.ACCELERATION)) {
							publish(new DataInertial(Shake.class, sm.x, sm.y,
									sm.z, false));
						}
					}
				}
			}

			messages.clear();

			buf = new byte[256];
			read = js.readBytes(buf, 256, 0);
		}
	}

	private void rotateCube(final ShakeMessage sm) {
		double phi = (last.x - sm.x) * 10;
		double theta = (sm.y - last.y) * 10;
		double psi = (last.z - sm.z) * 10;

		if (last.x > 0 && sm.x < 0)
			phi = (last.x - Math.abs(sm.x)) * 10;
		else if (last.x < 0 && sm.x > 0)
			phi = (Math.abs(last.x) - sm.x) * 10;

		// if (last.y < 0 && sm.y >0 )
		// phi = (last.y - Math.abs(sm.y)) * 10;
		// else if (last.y > 0 && sm.y < 0 )
		// phi = (Math.abs(last.y) - sm.y) * 10;

		if (last.z > 0.35 && sm.z < 0.05)
			psi = ((sm.z + 0.36) - last.z) * 10;
		else if (last.z < 0.05 && sm.z > 0.35)
			psi = ((last.z + 0.36) - sm.z) * 10;

//		World3D.itsBody.rotateInPlace(phi, theta, psi);
//		World3D.instance.repaint();
	}

	private void parseMessages(byte[] buf, int read, List<String> messages,
			String message) {
		for (int i = 0; i < read; i++) {
			char c = (char) buf[i];
			if (c == '$') {
				if (message.length() > 0) {
					if (!message.contains("ACK") && !message.contains("NAK")) {
						// System.out.println(message);
						messages.add(new String(message));
					}

					message = new String();
				}
				message += c;
				continue;
			}
			if (message.length() > 0) {
				message += c;
				if (c == '+' || c == '-')
					message += "0.";
			}
		}
	}

	private static class ShakeMessage {

		public ShakeMessageID id;

		public double x;
		public double y;
		public double z;

		public double messageNumber;

		public ShakeMessage(String id, double x, double y, double z,
				double messageNumber) {
			this(ShakeMessageID.fromValue(id), x, y, z, messageNumber);
		}

		public ShakeMessage(ShakeMessageID id, double x, double y, double z,
				double messageNumber) {
			super();
			this.id = id;
			this.x = x;
			this.y = y;
			this.z = z;
			this.messageNumber = messageNumber;
		}

		@Override
		public String toString() {
			return "id=" + id + ", x=" + x + ", y=" + y + ", z=" + z
					+ ", messageNumber=" + messageNumber;

		}

	}

	private enum ShakeMessageID {
		ACCELERATION, ROTATE_PITCH_HITCHHIKING, CAPACITIVE_SENDING;

		public static ShakeMessageID fromValue(String id) {
			if (id.equals("$ACC"))
				return ShakeMessageID.ACCELERATION;
			if (id.equals("$RPH"))
				return ShakeMessageID.ROTATE_PITCH_HITCHHIKING;
			if (id.equals("$CSA"))
				return ShakeMessageID.CAPACITIVE_SENDING;
			return null;
		}
	}

	private void vibrate() {
		String str = "vcAB";
		byte[] bufwrite = str.getBytes();
		js.writeBytes(bufwrite, bufwrite.length, 0);
	}



	private class CheckConnection extends TimerTask
	{
		@Override public void run()
		{
			long currentTime = System.currentTimeMillis();
			if (!Shake.this.isProcessing())
			{
				return;
			}
			if (currentTime - Shake.this.lastMessageReceived > 1000)
			{
//				Shake.this.onStop();
//				Shake.this.onStart();
				if (js != null)
				{
					js.close();
					boolean open = js.open(port);
					System.out.println("connect: " + open);
				}
//				if (open)
//					read();
			}
		}
	}

	// ################################################################################
	// END OF LAUNCH
	// ################################################################################

}
