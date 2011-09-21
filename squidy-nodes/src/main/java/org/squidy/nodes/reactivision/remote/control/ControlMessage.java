package org.squidy.nodes.reactivision.remote.control;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.squidy.nodes.reactivision.remote.Util;


/**
 * byte 0:		OpCode
 * bytes 1-4:   payload length in bytes (unsigned integer)
 * bytes 5-...	payload (optional) 
 * 
 * @author Andreas Ergenzinger
 *
 */
public final class ControlMessage {
	
	private byte[] header;
	private ByteBuffer payload;
	
	
	private ControlMessage() {
		
	}
	
	public ControlMessage(OpCode opCode) {
		header = new byte[5];
		header[0] = opCode.value();
	}
	
	public ControlMessage(OpCode opCode, byte[] payload) {
		this(opCode);
		setPayloadLength(payload.length);
		this.payload = ByteBuffer.wrap(payload);
	}
	
	public static ControlMessage createSetFiducialSetControlMessage(FiducialSet fiducialSet) {
		final int fiducialSetID = fiducialSet.ordinal();
		final byte[] bytes = new byte[4];
		Util.writeIntToByteArray(fiducialSetID, bytes, 0);
		return new ControlMessage(OpCode.SET_FIDUCIAL_SET, bytes);
	}
	
	public static ControlMessage createSettingsControlMessage(OpCode opCode, int value) {
		byte[] bytes = new byte[4];
		Util.writeIntToByteArray(value, bytes, 0);
		return new ControlMessage(opCode, bytes);
	}
	
	public static ControlMessage createSettingsControlMessage(OpCode opCode, float value) {
		byte[] bytes = new byte[4];
		Util.writeFloatToByteArray(value, bytes, 0);
		return new ControlMessage(opCode, bytes);
	}
	
	public OpCode getOpCode() {
		return OpCode.valueOf(header[0]);
	}
	
	public ByteBuffer getPayload() {
		return payload;
	}
	
	public int getPayloadLength() {
		return ((header[1] & 255) << 24) | ((header[2] & 255) << 16) | ((header[3] & 255) << 8) | (header[4] & 255);
	}
	
	public void send(SocketChannel socketChannel) throws IOException{
		socketChannel.write(serialize());
	}
	
	/**
	 * Returns a ByteBuffer filled with the byte representation of this message, 
	 * which has been 
	 * @return
	 */
	private ByteBuffer serialize() {
		int payloadLength = getPayloadLength();
		final ByteBuffer buffer = ByteBuffer.allocate(5 + payloadLength);
		buffer.put(header);
		if (payloadLength > 0)
			buffer.put(payload);
		buffer.flip();
		return buffer;
	}
	
	private void setPayloadLength(int length) {
		header[1] = (byte)(length >> 24);
		header[2] = (byte)(length >> 16);
		header[3] = (byte)(length >> 8);
		header[4] = (byte) length;
	}
	
	public static ControlMessage read(SocketChannel socketChannel)
	throws IOException {
		ControlMessage message = new ControlMessage();
		
		//read header
		final ByteBuffer headerBB = ByteBuffer.allocate(5);
		socketChannel.read(headerBB);
		message.header = headerBB.array();
		final int length = message.getPayloadLength();
		if (length > 0) {
			//read payload
			message.payload = ByteBuffer.allocate(length);
			socketChannel.read(message.payload);
		}
		return message;
	}
	
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final OpCode operationCode = OpCode.valueOf(header[0]);
		s.append(operationCode.toString());
		switch (operationCode) {
			case SEND_CAMERA_SETTINGS:
				s.append("\n");
				s.append(CameraSettingsContainer.deserialize(payload).toString());
				break;
			case SET_CAMERA_SETTINGS_FRAMERATE:
				s.append("   framerate: " + Util.readFloatFromBytes(payload.array(), 0));
				break;
			case SET_CAMERA_SETTINGS_EXPOSURE_TIME:
				s.append("   exposure time: " + Util.readFloatFromBytes(payload.array(), 0));
				break;
			case SET_CAMERA_SETTINGS_PIXEL_CLOCK:
				s.append("   pixel clock: " + Util.readFourByteInt(payload, 0));
				break;
			case SET_CAMERA_SETTINGS_HARDWARE_GAIN:
				s.append("   hardware gain: " + Util.readFourByteInt(payload, 0));
				break;
			case SET_CAMERA_SETTINGS_EDGE_ENHANCEMENT:
				final int edgeEnhancement = Util.readFourByteInt(payload, 0);
				String inWords = " (none)";
				if (edgeEnhancement == 1)
					inWords = " (weak)";
				else if (edgeEnhancement == 2)
					inWords = " (strong)";
				s.append("   edge enhancement: " + edgeEnhancement + inWords);
				break;
			case SET_CAMERA_SETTINGS_GAMMA:
				s.append("   gamma: " + Util.readFourByteInt(payload, 0));
				break;
			default:
				s.append(" <payload length: ");
				s.append(Util.readFourByteInt(header, 1));
				s.append("> ");
				if (payload != null)
					s.append(java.util.Arrays.toString(payload.array()));
		}
		return s.toString();
	}
}
