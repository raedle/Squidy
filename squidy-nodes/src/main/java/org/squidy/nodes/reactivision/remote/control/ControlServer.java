package org.squidy.nodes.reactivision.remote.control;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ServerSocketChannel;

import org.squidy.nodes.ReacTIVision;
import org.squidy.nodes.reactivision.remote.Util;


public class ControlServer implements Runnable{
	private ServerSocketChannel serverSocketChannel; 
	private ControlSocket controlSocket;
	private int serverPort;
	private boolean connected;
	private boolean running = false;
	private ReacTIVision reacTIVisionInstance;
	
	public ControlServer(ReacTIVision callingNode, int port) {
		reacTIVisionInstance = callingNode;
		serverPort = port;
		connected = false;
	}
	
	public void run() {
		running = true;
		while (running) {
			try {
				controlSocket = new ControlSocket(serverSocketChannel.accept());
				connected = true;
				
				//set FiducialSet, if necessary
				/*
				final FiducialSet remoteSet = getFiducialSet();//TODO required restart of ReavTIVision causes problems
				if (remoteSet != null && !remoteSet.equals(reacTIVisionInstance.getFiducialSet())) {
					setFiducialSet(reacTIVisionInstance.getFiducialSet());
					continue;
				}
				*/
				//fix: change value of ReacTIVision node
				reacTIVisionInstance.refreshFiducialSet(getFiducialSet());
				
				setAllCameraValues();
			} catch (AsynchronousCloseException e) {
				//do nothing
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
		//wait for connection attempt
		(new Thread(this)).start();
		return true;
	}
	
	public synchronized float[] getGrid() {
		ControlMessage request = new ControlMessage(OpCode.GET_GRID);
		if (!sendMessage(request))
			return null;
		ControlMessage response = receiveMessage();
		if (response == null)
			return null;
		
		if (!(response.getOpCode().equals(OpCode.SEND_GRID) && response.getPayloadLength() == 7*9*4*2))
			return null;
		final float[] grid = Util.byteArrayToFloatArray(response.getPayload().array());
		return grid;
	}
	
	public synchronized boolean setGrid(float[] grid) {
		ByteBuffer buffer = ByteBuffer.allocate(grid.length * 4);
		for (float f : grid)
			buffer.putFloat(f);
		ControlMessage message = new ControlMessage(OpCode.SET_GRID, buffer.array());
		return sendMessage(message);
	}
	
	public synchronized boolean startCameraFeed() {
		ControlMessage message = new ControlMessage(OpCode.START_CAMERA_FEED);
		return sendMessage(message);
	}
	
	public synchronized boolean stopCameraFeed() {
		ControlMessage message = new ControlMessage(OpCode.STOP_CAMERA_FEED);
		return sendMessage(message);
	}
	
	public synchronized CameraSettingsContainer getCameraSettings() {
		ControlMessage message = new ControlMessage(OpCode.GET_CAMERA_SETTINGS);
		if (!sendMessage(message))
			return null;
		message = receiveMessage();
		if (message == null || message.getOpCode() != OpCode.SEND_CAMERA_SETTINGS)
			return null;
		else {
			final ByteBuffer buffer = message.getPayload();
			return CameraSettingsContainer.deserialize(buffer);
		}
	}
	
	public synchronized boolean setFramerate(float framerate) {
		final ControlMessage message = ControlMessage
			.createSettingsControlMessage(OpCode.SET_CAMERA_SETTINGS_FRAMERATE, framerate);
		return sendMessage(message);
	}
	
	public synchronized boolean setExposureTime(float exposureTime) {
		final ControlMessage message = ControlMessage
			.createSettingsControlMessage(OpCode.SET_CAMERA_SETTINGS_EXPOSURE_TIME, exposureTime);
		return sendMessage(message);
	}
	
	public synchronized boolean setPixelClock(int pixelClock) {
		final ControlMessage message = ControlMessage
			.createSettingsControlMessage(OpCode.SET_CAMERA_SETTINGS_PIXEL_CLOCK, pixelClock);
		return sendMessage(message);
	}
	
	public synchronized boolean setHardwareGain(int hardwareGain) {
		final ControlMessage message = ControlMessage
			.createSettingsControlMessage(OpCode.SET_CAMERA_SETTINGS_HARDWARE_GAIN, hardwareGain);
		return sendMessage(message);
	}
	
	public synchronized boolean setEdgeEnhancement(int edgeEnhancement) {
		final ControlMessage message = ControlMessage
			.createSettingsControlMessage(OpCode.SET_CAMERA_SETTINGS_EDGE_ENHANCEMENT, edgeEnhancement);
		return sendMessage(message);
	}
	
	public synchronized boolean setGamma(int gamma) {
		final ControlMessage message = ControlMessage
			.createSettingsControlMessage(OpCode.SET_CAMERA_SETTINGS_GAMMA, gamma);
		return sendMessage(message);
	}
	
	public synchronized FiducialSet getFiducialSet() {
		ControlMessage message = new ControlMessage(OpCode.GET_FIDUCIAL_SET);
		if (!sendMessage(message))
			return null;
		message = receiveMessage();
		if (message == null)
			return null;
		final int fiducialSetID = Util.readFourByteInt(message.getPayload(), 0);
		return FiducialSet.valueOf(fiducialSetID);
	}
	
	public synchronized boolean setFiducialSet(FiducialSet fiducialSet) {
		final ControlMessage message = ControlMessage.createSetFiducialSetControlMessage(fiducialSet);
		return sendMessage(message);
	}
	
	private void setAllCameraValues() {
		//transfer desired values to ReacTIVision program
		setPixelClock(reacTIVisionInstance.getPixelClock());
		setExposureTime((float)reacTIVisionInstance.getExposureTime());
		setFramerate((float)reacTIVisionInstance.getFramerate());
		
		setHardwareGain(reacTIVisionInstance.getHardwareGain());
		setEdgeEnhancement(reacTIVisionInstance.getEdgeEnhancement());
		setGamma(reacTIVisionInstance.getGamma());
		//receive and set actual values
		reacTIVisionInstance.refreshCameraSettings();
	}
	
	private boolean sendMessage(ControlMessage message) {
		if (!connected)
			return false;
		try {
			controlSocket.send(message);
			//System.out.println("Sending this ControlMessage:\n   " + message.toString());
		} catch (IOException e) {
			e.printStackTrace();
			restart();
			return false;
		}
		return true;
	}
	
	private ControlMessage receiveMessage() {
		try {
			ControlMessage message = controlSocket.receive();
			//System.out.println("Receiving this ControlMessage:\n   " + message.toString());
			return message;
		} catch (IOException e) {
			e.printStackTrace();
			restart();
		}
		return null;
	}
	
	private void restart() {
		connected = false;
		(new Thread(this)).start();
	}
	
	public void stop() {
		running = false;
		connected = false;
		if (controlSocket != null)
			controlSocket.close();
		if (serverSocketChannel != null)
			try {
				serverSocketChannel.close();
			} catch (IOException e) {}
	}
}
