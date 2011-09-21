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

package org.squidy.nodes.tracking.configclient.service.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.NumberFormat;

import org.apache.log4j.Logger;
import org.squidy.nodes.tracking.config.xml.Camera;
import org.squidy.nodes.tracking.configclient.service.ServiceRegistry;
import org.squidy.nodes.tracking.configclient.service.comm.CommException;
import org.squidy.nodes.tracking.configclient.service.javaprop.JavaPropService;


public class TcpIpByteImageService extends ImageService {
	private static Logger logger = Logger.getLogger(TcpIpByteImageService.class);
	
	private static final int SLEEPMS = 1000;
	private static final int MAXTIME = 5000;
	
	// services
	private JavaPropService pService = (JavaPropService) ServiceRegistry
	.getInstance().getService(JavaPropService.class);

	// stuff for debugging
	private boolean debug = false;
	private static final String DEBUGFILE_IN = "imagedata.txt";
	private FileOutputStream debugFos_in;
	private BufferedWriter debugWriter;
	private NumberFormat nf = NumberFormat.getNumberInstance();
	
	private BufferedImage currentImage;
	private BufferedImage aoiImage;
	private int numAoiBytes;
	
	private Thread imagePollingThread;
	private Exception imagePollingException;

	// connection
	private InetSocketAddress address;
	private Socket socket;
	private OutputStream out;
	private InputStream in;
	private BufferedInputStream bis;
	private byte cameraIdByte;
	
	private final static int IMAGETYPE = BufferedImage.TYPE_3BYTE_BGR;
	
	public TcpIpByteImageService(String adr, int port, int w, int h) {
		super(w, h, null);
		address = new InetSocketAddress(adr, port);
	}

	@Override
	public BufferedImage getCurrentImage() {
		if(currentImage == null) createUnavailableImage();
		return currentImage;
	}

	private void createUnavailableImage() {
		currentImage = new BufferedImage(w,h,IMAGETYPE);
		Graphics g = currentImage.getGraphics();
		g.drawString("No image data avaliable.", 10, 10);
	}

	@Override
	public void setAoi(Rectangle aoi) {
		this.aoi = aoi;
		numAoiBytes = aoi.height*aoi.width*3;
		aoiImage = new BufferedImage(aoi.width,aoi.height,IMAGETYPE);
	}

	@Override
	protected void startupImpl() throws CommException {
		Object property = pService.getProperties().getProperty(JavaPropService.DEBUG_IMAGE);
		if(property != null) {
			debug = Boolean.parseBoolean((String)property);
		}
		if(debug) initDebugOutput();
		
		logger.info("Connecting to " + address);
		try {
			socket = new Socket(address.getAddress(), address.getPort());
			out = socket.getOutputStream();
			in = socket.getInputStream();
			bis = new BufferedInputStream(in);
			logger.info("Connection established.");
		} catch (IOException e) {
			String msg = "Could not connect to image server " + address;
			logger.info(msg);
			throw new CommException(msg);
		}		
	}
	
	private void initDebugOutput() {
		logger.info("Creating debug file: " + DEBUGFILE_IN + ".");
		try {
			debugWriter = new BufferedWriter(new FileWriter(DEBUGFILE_IN,false));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void shutdownImpl() {
		logger.info("Stopping image service.");
		if(imagePollingThread != null) imagePollingThread.interrupt();
		if(in != null) {
			try { in.close(); } catch (IOException ignore) { }
		}
		if(out != null) {
			try { out.close(); } catch (IOException ignore) { }
		}
		if(socket != null) {
			try { socket.close(); } catch (IOException ignore) { }
		}
		if(debug) {
			if(debugWriter != null) {
				try { debugWriter.close(); } catch (IOException ignore) { };
			}
		}
		logger.info("Image service stopped.");
	}
	
	@Override
	public void fetchImage() throws CommException {
		assert(aoi != null) : "Aoi must not be null.";
		imagePollingThread = new ImagePollingThread();
		imagePollingThread.setUncaughtExceptionHandler(new ImagePollingExceptionHandler());
		imagePollingThread.start();
		int count = 0;
		while ( (imagePollingThread != null) && (imagePollingThread.isAlive()) ) {
			if(count*1000 >= MAXTIME) {
				currentImage = null;
				throw new CommException("Cannot load image: Timeout reached.");
			}
			count++;
			try {
				Thread.sleep(SLEEPMS);
			} catch (InterruptedException e) {
				exceptionShutdown();
			}
		}
		if(imagePollingException != null) exceptionShutdown();
		// construct current image
		if(currentImage == null) currentImage = new BufferedImage(w,h,IMAGETYPE);
		Graphics g = currentImage.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, currentImage.getWidth(), currentImage.getHeight());
		g.drawImage(aoiImage, aoi.x, aoi.y, null);
		fireImageUpdate();
	}

	private void exceptionShutdown() throws CommException {
		logger.info("Loading of image has been interrupted. Shutting down service.");
		currentImage = null;
		shutdown();
		throw new CommException("Unable to load image.");
	}

	private class ImagePollingThread extends Thread {
		@Override
		public void run() {
			assert (aoiImage != null) : "Aoi image must be created prior to polling image (run setAoi before).";
			if(debug) nf.setMinimumIntegerDigits(3);
			try {
				out.write(cameraIdByte);
				out.flush();
				
				byte[] bytes = new byte[numAoiBytes];
				
				logger.debug("Current aoi: " + aoi);
				logger.debug("Expecting: " + numAoiBytes + " bytes.");
				if(debug) {
					debugWriter.newLine();
					debugWriter.write("========= New image (" + aoi.height + "x" + aoi.width + ") =========");
					debugWriter.newLine();
				}
				
				int bytesRead = 0;
				do {
					bytes[bytesRead] = (byte)bis.read();
					if(debug) {
						if(bytesRead%(aoi.width*3)==0) debugWriter.newLine();
						debugWriter.write( nf.format( ((int)bytes[bytesRead]) & 0xFF) + " ");
					}
					bytesRead++;
				} while(bytesRead < numAoiBytes);
				
				if(debug) debugWriter.flush();
				
				logger.debug("Total bytes read: " + bytesRead + ".");
				
				DataBuffer dbAoi = new DataBufferByte(bytes,numAoiBytes);
				aoiImage.setData(Raster.createRaster(aoiImage.getSampleModel(), dbAoi, null));
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	private class ImagePollingExceptionHandler implements UncaughtExceptionHandler {
		public void uncaughtException(Thread t, Throwable e) {
			logger.error("ImagePolling thread terminated abnormally.",e);
			shutdownImpl();
		}
	}

	@Override
	public void setCamera(Camera camera) {
		try {
			cameraIdByte = ((byte) Integer.parseInt(camera.getId()));
		} catch(NumberFormatException e) { cameraIdByte = 0; };
		super.setCamera(camera);
	}

}
