package org.squidy.nodes.reactivision.remote.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.squidy.nodes.reactivision.remote.CalibrationArea;


public class ImageServer implements Runnable {
	/**
	 * The port number on which the server is waiting for connection attempts.
	 */
	private int serverPort;
	private ServerSocketChannel serverSocketChannel;
	private SocketChannel socketChannel;
	private boolean connected;
	private boolean running;
	private BufferedImage currentImage = new BufferedImage(1024, 768, BufferedImage.TYPE_BYTE_GRAY);
	private CalibrationArea calibrationArea;
	
	public ImageServer(int serverPort) {
		this.serverPort = serverPort;
		connected = false;
	}
	
	public BufferedImage getImage() {
		return currentImage;
	}
	
	public void run() {
		//wait for connection attempt
		try {
			socketChannel = serverSocketChannel.accept();
			connected = true;
			ImageMessage message;
			while (connected) {
				//get image
				message = ImageMessage.read(socketChannel);
				BufferedImage image = new BufferedImage(message.width, message.height, BufferedImage.TYPE_INT_ARGB);
				//Option A (better)
				int imageDataCounter = 0;
				for (int y = 0; y < message.height; ++y) {
					for (int x = 0; x < message.width; ++x) {
						int rgb = (int)(message.imageData[imageDataCounter++] & 255);
						rgb = (255 << 24) | (rgb << 16) | (rgb << 8) | rgb;
						image.setRGB(x, y, rgb);
					}
				}
				//Option B
				/*
				DataBuffer data = new DataBufferByte(message.imageData, message.imageData.length);
				int bitMasks[] = new int[]{(byte)0xf};
	            SampleModel sampleModel = new SinglePixelPackedSampleModel(
	                DataBuffer.TYPE_BYTE, message.width, message.height, bitMasks);
	            WritableRaster raster = Raster.createWritableRaster(sampleModel, data, null);
				image.setData(raster);
				*/
				
				//make image available
				currentImage = image;
				//cause repaint
				calibrationArea.repaint();
			}
		} catch (AsynchronousCloseException e) {
			//do nothing
		} catch (IOException e) {
			e.printStackTrace();
			connected = false;
			try {
				if (socketChannel != null)
					socketChannel.close();
			} catch (IOException f) {
				f.printStackTrace();
			}
			if (running)
				restart();
		} catch (IllegalArgumentException e) {
			connected = false;
			try {
				socketChannel.close();
			} catch (IOException f) {
				f.printStackTrace();
			}
			if (running)
				restart();
		}
	}
	
	public void setCalibrationArea(CalibrationArea calibrationArea) {
		this.calibrationArea = calibrationArea;
	}
	
	/**
	 * Returns <code>true</code> if the ControlServer could be initialized and is,
	 * now waiting for connections on the specified port.
	 * 
	 * @return
	 */
	public boolean start() {
		// Create a server socket in blocking mode and check for connections
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(true);
			serverSocketChannel.socket().bind(new InetSocketAddress(serverPort));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		running = true;
		(new Thread(this)).start();
		return true;
	}
	
	public void stop() {
		running = false;
		try {
			serverSocketChannel.close();
		} catch (IOException e) {}
		if (connected) {
			connected = false;
			try {
				socketChannel.close();
			} catch (IOException e) {}
		}
	}


	private void restart() {
		(new Thread(this)).start();
	}
}