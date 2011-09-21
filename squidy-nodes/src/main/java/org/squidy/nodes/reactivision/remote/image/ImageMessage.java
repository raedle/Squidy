package org.squidy.nodes.reactivision.remote.image;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.squidy.nodes.reactivision.remote.Util;


/**
 * byte 0:     ImageFormat
 * bytes 1-4:  image size in bytes (unsigned integer)
 * bytes 5+6:  image width in pixels (unsigned short)
 * bytes 7+8:  image height in pixels (unsigned short)
 * bytes 9-... image data
 */
public class ImageMessage {
	ImageFormat imageFormat;
	int imageSize;
	short width;
	short height;
	byte imageData[];
	
	
	
	public static ImageMessage read(SocketChannel socketChannel)
	throws IOException {
		ImageMessage message = new ImageMessage();
		
		//header data
		ByteBuffer header = ByteBuffer.allocate(9);
		socketChannel.read(header);
		message.imageFormat = ImageFormat.valueOf(header.get(0));
		message.imageSize = Util.readFourByteInt(header, 1);
		message.width = (short) Util.readTwoByteInt(header, 5);
		message.height = (short) Util.readTwoByteInt(header, 7);
		
		//image data
		message.imageData = new byte[message.imageSize];
		
		ByteBuffer readBuffer = ByteBuffer.allocate(8192);
		int read = 0;
		while (read <= message.imageSize - 1) {
			readBuffer.clear();
			final int readThisPass = socketChannel.read(readBuffer);
			if (readThisPass < 0) {
				break;
			}
			readBuffer.clear();
			
			for (int i = read; i < read + readThisPass; ++i)
				message.imageData[i] = readBuffer.get();
			read += readThisPass;
		}
		
		return message;
	}
	
	private ImageMessage() {
		//do nothing
	}
}
