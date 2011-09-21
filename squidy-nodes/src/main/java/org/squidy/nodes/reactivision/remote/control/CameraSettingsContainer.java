package org.squidy.nodes.reactivision.remote.control;

import java.nio.ByteBuffer;

import org.squidy.nodes.reactivision.remote.Util;


public class CameraSettingsContainer {
	public float framerate;
	public float exposureTime;
	public int pixelClock;
	public int hardwareGain;
	public int edgeEnhancement;
	public int gamma;
	
	private CameraSettingsContainer() {
		//do nothing
	}
	
	public static CameraSettingsContainer deserialize(ByteBuffer buffer) {
		final byte[] bytes = buffer.array();
		if (bytes.length != 24) {
			System.err.println("payload length is " + bytes.length + ", but should be 24.");
			return null;
		}
		final CameraSettingsContainer c = new CameraSettingsContainer();
		c.framerate = Util.readFloatFromBytes(bytes, 0);
		c.exposureTime = Util.readFloatFromBytes(bytes, 4);
		c.pixelClock = Util.readFourByteInt(bytes, 8);
		c.hardwareGain = Util.readFourByteInt(bytes, 12);
		c.edgeEnhancement = Util.readFourByteInt(bytes, 16);
		c.gamma = Util.readFourByteInt(bytes, 20);
		
		//deal with framerate == 0
		if (c.framerate == 0)
			c.framerate = 1000 / c.exposureTime;
		
		return c;
	}
	
	public String toString() {
		final StringBuilder s = new StringBuilder();
		s.append("   [framerate:        ");
		s.append(framerate);
		s.append("]\n");
		s.append("   [exposure time:    ");
		s.append(exposureTime);
		s.append("]\n");
		s.append("   [pixel clock:      ");
		s.append(pixelClock);
		s.append("]\n");
		s.append("   [hardware gain:    ");
		s.append(hardwareGain);
		s.append("]\n");
		s.append("   [edge enhancement: ");
		s.append(edgeEnhancement);
		s.append("]\n");
		s.append("   [gamma:            ");
		s.append(gamma);
		s.append("]\n");
		return s.toString();
	}
}
