/**
 * 
 */
package org.squidy.manager.bridge;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DefaultDataContainer;
import org.squidy.manager.model.Processable;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import com.illposed.osc.utility.OSCJavaToByteArrayConverter;


/**
 * <code>CSharpBridge</code>.
 *
 * <pre>
 * Date: 10.05.2010
 * Time: 17:00:18
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id$
 * @since 1.5.0
 */
public class CSharpBridge implements Bridge {

	public static void main(String[] args) throws Exception {
		CSharpBridge bridge = new CSharpBridge();
		bridge.open();
		
		DataPosition2D dataPosition2D = new DataPosition2D(Processable.class, 0.5f, 0.5f);
		dataPosition2D.setAttribute(DataConstant.IDENTIFIER, "It is a two-dimensional position");
		dataPosition2D.setAttribute(DataConstant.TACTILE, Boolean.TRUE);
		bridge.publish(dataPosition2D, new DataButton(Processable.class, 2, true), new DataButton(Processable.class, 2, false));
		
//		bridge.close();
	}
	
	private boolean open = false;
	
	protected Process process;
	
//	protected OSCJavaToByteArrayConverter converter;
	
	protected boolean windowHasFocus = true;
	protected Rectangle window;
	protected Rectangle graphicsBounds;
	
	private OSCPortOut oscConnection;

	private BridgeCallback callback;

	public void setCallback(BridgeCallback callback) {
		this.callback = callback;
	}
	
	private String executablePath = null;
	
	/**
	 * @param executablePath the executablePath to set
	 */
	public final void setExecutablePath(String executablePath) {
		this.executablePath = executablePath;
	}

	private String host = null;

	/**
	 * @param host the host to set
	 */
	public final void setHost(String host) {
		this.host = host;
	}

	private int port = -1;

	/**
	 * @param port the port to set
	 */
	public final void setPort(int port) {
		this.port = port;
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.bridge.Bridge#openBridge()
	 */
	public void open() throws IOException {
		window = new Rectangle();
		
//		oscConnection = prepareOSCConnection();
		
		ProcessBuilder processBuilder = new ProcessBuilder(executablePath);
		processBuilder.directory(new File(executablePath).getParentFile());
		process = processBuilder.start();
		
//		outputStream = process.getOutputStream();
		processInputStreamReading(process);
		
//		converter = new OSCJavaToByteArrayConverter();
		
		graphicsBounds = new Rectangle();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (GraphicsDevice gd : env.getScreenDevices()) {
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			graphicsBounds.add(gc.getBounds());
		}
		
		open = true;
		
		if (callback != null) {
			callback.opened();
		}
	}
	
	/**
	 * @return
	 */
	private OSCPortOut prepareOSCConnection() {
		OSCPortOut portOut = null;
		try {
			portOut = new OSCPortOut(InetAddress.getByName(host), port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return portOut;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.bridge.Bridge#closeBridge()
	 */
	public void close() throws IOException {
		
		open = false;
		
		if (window != null) {
			window = null;
		}
		
//		if (converter != null) {
//			converter = null;
//		}
		
		if (oscConnection != null) {
			oscConnection.close();
			oscConnection =  null;
		}
		
		if (process != null) {
			try {
				process.destroy();
				process.waitFor();
				process = null;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (callback != null) {
			callback.closed();
		}
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.bridge.Bridge#publish(org.squidy.manager.data.IData[])
	 */
	public void publish(IData... data) throws IOException {
		publish(new DefaultDataContainer(data));
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.bridge.Bridge#publish(org.squidy.manager.data.IDataContainer)
	 */
	public void publish(IDataContainer dataContainer) throws IOException {
//		System.out.println("publish packet");
		
		if (!open) {
			return;
		}
		
//		List<IData> allData = new ArrayList<IData>();
//		for (IData data : dataContainer.getData()) {
//	
////			if (data.getSource().getSimpleName().equals("MouseIO") && data instanceof DataPosition2D) {
////				DataPosition2D dataPosition2D = (DataPosition2D) data;
////				
////				double x = dataPosition2D.getX();
////				double y = dataPosition2D.getY();
////				
////				double xPos = graphicsBounds.getWidth() * x;
////				double yPos = graphicsBounds.getHeight() * y;
////				
//////				System.out.println(window.contains(xPos, yPos) + "; " + xPos + ":" + yPos + "; " + window + "; " + graphicsBounds);
////				
////				if (windowHasFocus && window.contains(xPos, yPos)) {
////					dataPosition2D.setX((xPos - window.getX()) / window.getWidth());
////					dataPosition2D.setY((yPos - window.getY()) / window.getHeight());
////					
//////					sendData(data);
////					allData.add(data);
////				}
//////				dataPosition2D.setX(xPos);
//////				dataPosition2D.setY(yPos);
//////				
//////				sendData(data);
////			}
////			else {
////				sendData(data);
//				allData.add(data);
////			}
//		}
//		
////		System.out.println("BEFORE BRIDGE: " + allData.size());
//		
//		// Ignore empty data containers.
//		if (allData.size() < 1)
//			return;
//		
//		dataContainer.setData(allData.toArray(new IData[0]));
		
		sendDataContainer(dataContainer);
	}
	
	private void sendDataContainer(IDataContainer dataContainer) throws IOException {
		
		OSCBundle bundle = new OSCBundle(new Date(dataContainer.getTimestamp()));
		
		for (IData data : dataContainer.getData()) {
			bundle.addPacket(createMessage(data));
		}
		
		if (oscConnection != null) {
			oscConnection.send(bundle);
		}
	}
	
	private OSCMessage createMessage(IData data) throws IOException {
		
//		OSCBundle bundle = new OS
		
		OSCMessage message = new OSCMessage("/squidy/bridge/osc");
		message.addArgument(data.getClass().getName());
		for (Object o : data.serialize()) {
			message.addArgument(o);
		}

//		oscConnection.send(message);
		return message;
	}
	
	/**
	 * @param process
	 */
	private void processInputStreamReading(final Process process) {
		new Thread() {
			/**
			 * 
			 */
			@Override
			public void run() {
				super.run();

				try {
					InputStream is = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
					while ((line = br.readLine()) != null) {
						System.out.println(line);
						
						if (line.startsWith("CloseBridge")) {
							close();
						}
						else if (line.startsWith("WindowLocation")) {

							// do window location changed
							
							String raw = line.split(":")[1];
							String[] rawLocations = raw.split(",");
							
							int x = Integer.parseInt(rawLocations[0].split("=")[1]);
							int y = Integer.parseInt(rawLocations[1].split("=")[1]);
							
							window.setLocation(x, y);
							
//							System.out.println("WINDOW: " + window);
						}
						else if (line.startsWith("WindowSize")) {
							// do window size changed
							
							String raw = line.split(":")[1];
							String[] rawLocations = raw.split(",");
							
							int width = Integer.parseInt(rawLocations[0].split("=")[1]);
							int height = Integer.parseInt(rawLocations[1].split("=")[1]);
							
							window.setSize(width, height);
							
//							System.out.println("WINDOW: " + window);
						}
						else if (line.startsWith("WindowFocus")) {
							// do window size changed
							
							String raw = line.split(":")[1];
							
							windowHasFocus = Boolean.parseBoolean(raw);
						}
						else if (line.startsWith("Port")) {
							// do window size changed
							
							String raw = line.split(":")[1];
							
							port = Integer.parseInt(raw);

							oscConnection = prepareOSCConnection();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

	}
}
