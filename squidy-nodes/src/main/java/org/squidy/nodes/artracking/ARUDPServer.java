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

package org.squidy.nodes.artracking;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.nodes.ARTracking;


/**
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * 
 */
public class ARUDPServer extends Thread {
	private static Log LOG = LogFactory.getLog(ARUDPServer.class);

	private DatagramSocket socket = null;

	private String data = "";

	private ARTracking arTracking;

	private int buffer = 2048;

	private double display_width = 0;

	private double display_height = 0;

	private double display_depth = 0;

	private int lastFrameCounter = -1;

	double x;

	double y;

	double z;

	double ax;

	double ay;

	double az;

	double rxx;

	double ryx;

	double rzx;

	double rxy;

	double ryy;

	double rzy;

	double rxz;

	double ryz;

	double rzz;

	double quality;

	int trackedBodies;

	double rad;

	double len_o;

	double ang_om;

	double len_m;

	double ang_mi;

	double len_i;

	StringTokenizer toker;
	StringTokenizer lineToker;

	private boolean running = true;

	public ARUDPServer(ARTracking art, int port) {
		this.arTracking = art;

		try {
			socket = new DatagramSocket(port);
			start();
			LOG.info("UDP Server started on port " + port);
		}
		catch (IOException e) {
			LOG.error("Couldn't start udp server on port " + port);
		}

	}

	/*	public void run() {

		while (running) {
			try {
				byte[] buf = new byte[buffer];
				DatagramPacket packet = new DatagramPacket(buf, buffer);
				socket.receive(packet);
				data = new String(packet.getData());
				int last = 0;
				while (last < buffer) {
					if (buf[last] == 0)
						break;
					last++;
				}
				data = data.substring(0, last);
				if (last == buffer) {
					LOG.error("Input buffer overflow");
				}
				else {
					parseData(data);
				}

			}
			catch (SocketException e) {
				if (running && LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			}
			catch (IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			}

		}
	}
*/
	public void refreshDriver() {
		display_width = arTracking.getWidth();
		display_height = arTracking.getHeight();
		display_depth = arTracking.getDepth();
	}
/*
	private void parseData(String val) {
		lineToker = new StringTokenizer(val, "\r\n");
		while (lineToker.hasMoreTokens()) {
			String lineToken = lineToker.nextToken();
			if (lineToken.startsWith("fr")) {
				parse_fr(lineToken);
				continue;
			}
			if (lineToken.startsWith("ts")) {
				parse_ts(lineToken);
				continue;
			}
			if (lineToken.startsWith("3d")) {
				parse_3d(lineToken);
				continue;
			}
			if (lineToken.startsWith("6df")) {
				parse_6df(lineToken);
				continue;
			}
			if (lineToken.startsWith("6dcal")) {
				parse_6dcal(lineToken);
				continue;
			}
			if (lineToken.startsWith("6dmt")) {
				parse_6dmt(lineToken);
				continue;
			}
			if (lineToken.startsWith("6d")) {
				parse_6d(lineToken);
				continue;
			}
			if (lineToken.startsWith("glcal")) {
				parse_glcal(lineToken);
				continue;
			}
			if (lineToken.startsWith("gl")) {
				parse_gl(lineToken);
				continue;
			}
		}
	}

	private void parse_fr(String val) {
		// remove "fr"
		int index = val.indexOf(" ");
		val = val.substring(index + 1, val.length());
		lastFrameCounter = Integer.parseInt(val);
	}

	private void parse_ts(String val) {
		// unused
	}

	private void parse_3d(String val) {
		// remove "3d"
		int index = val.indexOf(" ");
		val = val.substring(index + 1, val.length());
		index = val.indexOf(" ");
		// number of found bodies
		int trackedBodies = Integer.parseInt(val.substring(0, index));
		if (trackedBodies == 0)
			return;

		val = val.substring(index + 2, val.length());
		toker = new StringTokenizer(val, "] [");
		// parse each 3d body
		while (toker.hasMoreTokens()) {

			String bodyID = toker.nextToken();
			quality = Float.parseFloat(toker.nextToken());
			x = Float.parseFloat(toker.nextToken());
			y = Float.parseFloat(toker.nextToken());
			z = Float.parseFloat(toker.nextToken());

			arTracking.publish(new DataPosition3D(arTracking.getClass(), x, y, z, display_width,
					display_height, display_depth, lastFrameCounter, trackedBodies));
		}
	}

	private void parse_6df(String val) {
		// remove "6df"
		int index = val.indexOf(" ");
		val = val.substring(index + 1, val.length());
		index = val.indexOf(" ");
		// number of found bodies
		trackedBodies = Integer.parseInt(val.substring(0, index));
		if (trackedBodies == 0)
			return;

		val = val.substring(index + 2, val.length());
		toker = new StringTokenizer(val, "] [");
		// parse each 6d body
		while (toker.hasMoreTokens()) {

			String bodyID = toker.nextToken();
			quality = Float.parseFloat(toker.nextToken());
			int butBin = Integer.parseInt(toker.nextToken());
			x = Float.parseFloat(toker.nextToken());
			y = Float.parseFloat(toker.nextToken());
			z = Float.parseFloat(toker.nextToken());
			ax = Float.parseFloat(toker.nextToken());
			ay = Float.parseFloat(toker.nextToken());
			az = Float.parseFloat(toker.nextToken());
			rxx = Float.parseFloat(toker.nextToken());
			ryx = Float.parseFloat(toker.nextToken());
			rzx = Float.parseFloat(toker.nextToken());
			rxy = Float.parseFloat(toker.nextToken());
			ryy = Float.parseFloat(toker.nextToken());
			rzy = Float.parseFloat(toker.nextToken());
			rxz = Float.parseFloat(toker.nextToken());
			ryz = Float.parseFloat(toker.nextToken());
			rzz = Float.parseFloat(toker.nextToken());

			boolean[] buttons = new boolean[8];
			// 00000001 -> fire
			buttons[0] = (butBin & 0x01) > 0;

			// 00000010 -> right
			buttons[1] = (butBin & 0x02) > 0;

			// 00000100 -> middle
			buttons[2] = (butBin & 0x04) > 0;

			// 00001000 -> left
			buttons[3] = (butBin & 0x08) > 0;

			// 00010000 -> stick down
			buttons[4] = (butBin & 0x10) > 0;

			// 00100000 -> stick left
			buttons[5] = (butBin & 0x20) > 0;

			// 01000000 -> stick up
			buttons[6] = (butBin & 0x40) > 0;

			// 10000000 -> stick right
			buttons[7] = (butBin & 0x80) > 0;

			// TODO [RR]: Change this to a single data container with buttons and position as bundle.
			int i = 0;
			for (boolean b : buttons) {
				arTracking.publish(new DataButton(arTracking.getClass(), i++, b));
			}

			if (quality != -1.0f) {
				arTracking.publish(new DataPosition6D(arTracking.getClass(), x, y, z, display_width,
						display_height, display_depth, rxx, ryx, rzx, rxy, ryy, rzy, rxz, ryz, rzz, lastFrameCounter,
						trackedBodies));
			}
		}
	}

	private void parse_6dcal(String val) {
		// unused
	}

	private void parse_6dmt(String val) {
		// remove "6dmt"
		int index = val.indexOf(" ");
		val = val.substring(index + 1, val.length());
		index = val.indexOf(" ");
		// number of found bodies
		int trackedBodies = Integer.parseInt(val.substring(0, index));
		if (trackedBodies == 0)
			return;

		val = val.substring(index + 2, val.length());
		toker = new StringTokenizer(val, "] [");
		// parse each 6d body
		while (toker.hasMoreTokens()) {

			String bodyID = toker.nextToken();
			quality = Float.parseFloat(toker.nextToken());
			int butBin = Integer.parseInt(toker.nextToken());
			x = Float.parseFloat(toker.nextToken());
			y = Float.parseFloat(toker.nextToken());
			z = Float.parseFloat(toker.nextToken());
			rxx = Float.parseFloat(toker.nextToken());
			ryx = Float.parseFloat(toker.nextToken());
			rzx = Float.parseFloat(toker.nextToken());
			rxy = Float.parseFloat(toker.nextToken());
			ryy = Float.parseFloat(toker.nextToken());
			rzy = Float.parseFloat(toker.nextToken());
			rxz = Float.parseFloat(toker.nextToken());
			ryz = Float.parseFloat(toker.nextToken());
			rzz = Float.parseFloat(toker.nextToken());
			arTracking.publish(new DataPosition6D(arTracking.getClass(), x, y, z, display_width,
					display_height, display_depth, rxx, ryx, rzx, rxy, ryy, rzy, rxz, ryz, rzz, lastFrameCounter,
					trackedBodies));
		}
	}

	private void parse_6d(String val) {
		// remove "6d"
		int index = val.indexOf(" ");
		val = val.substring(index + 1, val.length());
		index = val.indexOf(" ");
		// number of found bodies
		int trackedBodies = Integer.parseInt(val.substring(0, index));
		if (trackedBodies == 0)
			return;

		val = val.substring(index + 2, val.length());
		toker = new StringTokenizer(val, "] [");
		// parse each 6d body
		while (toker.hasMoreTokens()) {

			String bodyID = toker.nextToken();
			quality = Float.parseFloat(toker.nextToken());
			x = Float.parseFloat(toker.nextToken());
			y = Float.parseFloat(toker.nextToken());
			z = Float.parseFloat(toker.nextToken());
			ax = Float.parseFloat(toker.nextToken());
			ay = Float.parseFloat(toker.nextToken());
			az = Float.parseFloat(toker.nextToken());
			rxx = Float.parseFloat(toker.nextToken());
			ryx = Float.parseFloat(toker.nextToken());
			rzx = Float.parseFloat(toker.nextToken());
			rxy = Float.parseFloat(toker.nextToken());
			ryy = Float.parseFloat(toker.nextToken());
			rzy = Float.parseFloat(toker.nextToken());
			rxz = Float.parseFloat(toker.nextToken());
			ryz = Float.parseFloat(toker.nextToken());
			rzz = Float.parseFloat(toker.nextToken());
			arTracking.publish(new DataPosition6D(arTracking.getClass(), x, y, z, display_width,
					display_height, display_depth, rxx, ryx, rzx, rxy, ryy, rzy, rxz, ryz, rzz, lastFrameCounter,
					trackedBodies));
		}
	}

	private void parse_glcal(String val) {
		// unused
	}

	private void parse_gl(String val) {
		// remove "gl"
		int index = val.indexOf(" ");
		val = val.substring(index + 1, val.length());
		index = val.indexOf(" ");
		if (index == -1)
			return;
		// number of found bodies
		int trackedBodies = Integer.parseInt(val.substring(0, index));
		if (trackedBodies == 0)
			return;

		val = val.substring(index + 2, val.length());
		toker = new StringTokenizer(val, "] [");
		// parse each 6d body
		while (toker.hasMoreTokens()) {

			String bodyID = toker.nextToken();
			quality = Float.parseFloat(toker.nextToken());
			int leftRight = Integer.parseInt(toker.nextToken());
			int numFingers = Integer.parseInt(toker.nextToken());

			// 6d hand
			x = Float.parseFloat(toker.nextToken());
			y = Float.parseFloat(toker.nextToken());
			z = Float.parseFloat(toker.nextToken());

			rxx = Float.parseFloat(toker.nextToken());
			ryx = Float.parseFloat(toker.nextToken());
			rzx = Float.parseFloat(toker.nextToken());
			rxy = Float.parseFloat(toker.nextToken());
			ryy = Float.parseFloat(toker.nextToken());
			rzy = Float.parseFloat(toker.nextToken());
			rxz = Float.parseFloat(toker.nextToken());
			ryz = Float.parseFloat(toker.nextToken());
			rzz = Float.parseFloat(toker.nextToken());

			DataGlove glove = new DataGlove(arTracking.getClass(), leftRight, x, y, z, display_width,
					display_height, display_depth, rxx, ryx, rzx, rxy, ryy, rzy, rxz, ryz, rzz, lastFrameCounter,
					trackedBodies);

			// 6d thumb
			x = Float.parseFloat(toker.nextToken());
			y = Float.parseFloat(toker.nextToken());
			z = Float.parseFloat(toker.nextToken());

			rxx = Float.parseFloat(toker.nextToken());
			ryx = Float.parseFloat(toker.nextToken());
			rzx = Float.parseFloat(toker.nextToken());
			rxy = Float.parseFloat(toker.nextToken());
			ryy = Float.parseFloat(toker.nextToken());
			rzy = Float.parseFloat(toker.nextToken());
			rxz = Float.parseFloat(toker.nextToken());
			ryz = Float.parseFloat(toker.nextToken());
			rzz = Float.parseFloat(toker.nextToken());

			rad = Float.parseFloat(toker.nextToken());
			len_o = Float.parseFloat(toker.nextToken());
			ang_om = Float.parseFloat(toker.nextToken());
			len_m = Float.parseFloat(toker.nextToken());
			ang_mi = Float.parseFloat(toker.nextToken());
			len_i = Float.parseFloat(toker.nextToken());

			DataFinger thumb = new DataFinger(arTracking.getClass(), DataFinger.THUMB, leftRight, x,
					y, z, display_width, display_height, display_depth, rxx, ryx, rzx, rxy, ryy, rzy, rxz, ryz, rzz,
					rad, len_o, ang_om, len_m, ang_mi, len_i, lastFrameCounter, trackedBodies);
			glove.thumb = thumb;

			// 6d index finger
			x = Float.parseFloat(toker.nextToken());
			y = Float.parseFloat(toker.nextToken());
			z = Float.parseFloat(toker.nextToken());

			rxx = Float.parseFloat(toker.nextToken());
			ryx = Float.parseFloat(toker.nextToken());
			rzx = Float.parseFloat(toker.nextToken());
			rxy = Float.parseFloat(toker.nextToken());
			ryy = Float.parseFloat(toker.nextToken());
			rzy = Float.parseFloat(toker.nextToken());
			rxz = Float.parseFloat(toker.nextToken());
			ryz = Float.parseFloat(toker.nextToken());
			rzz = Float.parseFloat(toker.nextToken());

			rad = Float.parseFloat(toker.nextToken());
			len_o = Float.parseFloat(toker.nextToken());
			ang_om = Float.parseFloat(toker.nextToken());
			len_m = Float.parseFloat(toker.nextToken());
			ang_mi = Float.parseFloat(toker.nextToken());
			len_i = Float.parseFloat(toker.nextToken());

			DataFinger indexFinger = new DataFinger(arTracking.getClass(),
					DataFinger.INDEXFINGER, leftRight, x, y, z, display_width, display_height, display_depth, rxx, ryx,
					rzx, rxy, ryy, rzy, rxz, ryz, rzz, rad, len_o, ang_om, len_m, ang_mi, len_i, lastFrameCounter,
					trackedBodies);
			glove.indexFinger = indexFinger;

			// 6d middle finger
			x = Float.parseFloat(toker.nextToken());
			y = Float.parseFloat(toker.nextToken());
			z = Float.parseFloat(toker.nextToken());

			rxx = Float.parseFloat(toker.nextToken());
			ryx = Float.parseFloat(toker.nextToken());
			rzx = Float.parseFloat(toker.nextToken());
			rxy = Float.parseFloat(toker.nextToken());
			ryy = Float.parseFloat(toker.nextToken());
			rzy = Float.parseFloat(toker.nextToken());
			rxz = Float.parseFloat(toker.nextToken());
			ryz = Float.parseFloat(toker.nextToken());
			rzz = Float.parseFloat(toker.nextToken());

			rad = Float.parseFloat(toker.nextToken());
			len_o = Float.parseFloat(toker.nextToken());
			ang_om = Float.parseFloat(toker.nextToken());
			len_m = Float.parseFloat(toker.nextToken());
			ang_mi = Float.parseFloat(toker.nextToken());
			len_i = Float.parseFloat(toker.nextToken());

			DataFinger middleFinger = new DataFinger(arTracking.getClass(),
					DataFinger.MIDDLEFINGER, leftRight, x, y, z, display_width, display_height, display_depth, rxx,
					ryx, rzx, rxy, ryy, rzy, rxz, ryz, rzz, rad, len_o, ang_om, len_m, ang_mi, len_i, lastFrameCounter,
					trackedBodies);
			glove.middleFinger = middleFinger;

			arTracking.publish(glove);

		}
	}
*/
	public void close() {
		if (LOG.isInfoEnabled()) {
			LOG.info("Close server.");
		}
		running = false;
		socket.close();
	}
}
