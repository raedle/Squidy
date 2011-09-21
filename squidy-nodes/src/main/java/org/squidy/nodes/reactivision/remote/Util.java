package org.squidy.nodes.reactivision.remote;

import java.nio.ByteBuffer;

public final class Util {
	public static float[] byteArrayToFloatArray(byte[] bytes) {
		float[] floats = new float[bytes.length / 4];
		for (int i = 0; i < floats.length; ++i)
			floats[i] = readFloatFromBytes(bytes, i * 4);
		return floats;
	}
	
	public static float readFloatFromBytes(byte[] bytes, int start) {
		Integer i = bytes[start] << 24;
		i |= (bytes[start + 1] & 255) << 16;
		i |= (bytes[start + 2] & 255) << 8;
		i |= (bytes[start + 3] & 255);
		return Float.intBitsToFloat(i);
	}
	
	public static int readTwoByteInt(ByteBuffer buffer, int start) {
		final byte b1 = buffer.get(start);
		final byte b2 = buffer.get(start + 1);
		return ((b1 & 255) << 8) + (b2 & 255);
	}
	
	
	public static int readTwoByteInt(byte[] array, int start) {
		return ((array[start] & 255) << 8) + (array[start + 1] & 255);
	}
	
	public static int readThreeByteInt(ByteBuffer buffer, int start) {
		final byte b1 = buffer.get(start);
		final byte b2 = buffer.get(start + 1);
		final byte b3 = buffer.get(start + 2);
		return ((b1 & 255) << 16) + ((b2 & 255) << 8) + (b3 & 255);
	}
	
	public static int readThreeByteInt(byte[] array, int start) {
		return ((array[start] & 255) << 16) | ((array[start + 1] & 255) << 8) | (array[start + 2] & 255);
	}
	
	public static int readFourByteInt(ByteBuffer buffer, int start) {
		final byte b1 = buffer.get(start);
		final byte b2 = buffer.get(start + 1);
		final byte b3 = buffer.get(start + 2);
		final byte b4 = buffer.get(start + 3);
		return ((b1 & 255)  << 24) | ((b2 & 255) << 16) | ((b3 & 255) << 8) | (b4 & 255);
	}
	
	public static int readFourByteInt(byte[] array, int start) {
		return ((array[start] & 255) << 24) | ((array[start + 1] & 255) << 16) | ((array[start + 2] & 255) << 8) | (array[start + 3] & 255);
	}
	
	public static void writeIntToByteArray(int number, byte[] bytes, int start) {
		bytes[start] = (byte)(number >> 24);
		bytes[start + 1] = (byte)(number >> 16);
		bytes[start + 2] = (byte)(number >> 8);
		bytes[start + 3] = (byte) number;
	}
	
	public static void writeFloatToByteArray(float number, byte[] bytes, int start) {
		int i = Float.floatToIntBits(number);
		writeIntToByteArray(i, bytes, start);
	}
}
