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


package org.squidy.nodes.tracking;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Throughput;
import org.squidy.manager.data.domainprovider.impl.EndianDomainProvider;
import org.squidy.manager.data.impl.DataObject;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;
import org.squidy.nodes.ir.ConfigManagable;
import org.squidy.nodes.tracking.config.ConfigNotifier;

import java.net.Socket;
import com.illposed.osc.Endian;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;


public class CameraConfigComm /*extends Thread*/ implements ImageCallback {

	private static final Log LOG = LogFactory.getLog(CameraConfigComm.class);

	private String addressOutgoing = "127.0.0.1";
	private int portOutgoing = 4444;
	public int getPortOutgoing() {
		return portOutgoing;
	}

	public void setPortOutgoing(int portOutgoing) {
		
		if( portOutgoing != this.portOutgoing){
			this.portOutgoing = portOutgoing;
			if( oscPortOut != null){
				oscPortOut.close();
				try {
					oscPortOut = new OSCPortOut(InetAddress.getByName(addressOutgoing),
							portOutgoing);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}



	private int portIncoming = 4445;
	public int getPortIncoming() {
		return portIncoming;
	}

	public void setPortIncoming(int portIncoming) {
		
		if( portIncoming != this.portIncoming){
			this.portIncoming = portIncoming;
			if( oscPortIn != null){
				oscPortIn.stopListening();
				oscPortIn.close();
				try {
					oscPortIn = new OSCPortIn(portIncoming, endian);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startConfigListener();
			}
		}
	}



	private int id = 0;
	private boolean isFirstImageRequest = true;

	private OSCPortOut oscPortOut;
	private OSCPortIn oscPortIn;
	private ServerSocket imageServer;
	private Socket imageClient;
	private DatagramSocket imageClientUDP;
	private boolean initDone = false;
	private Endian endian;
	private CameraCallback configUpdate;
	private DataInputStream is;
	private String line;
	private int MAX_LEN = 100000;
	private byte[] imgBuffer;
	private int imgLoadDelay = 250;
	private ImageListener il;
	private boolean connectedToImageClient = false;
	private boolean streamImage = false;
	private ImageListener imageListener = null;
	private boolean isStopped = true;
	private int imageServerPort = 7777;
	
	
	public int getImageServerPort() {
		return imageServerPort;
	}

	public void setImageServerPort(int imageServerPort) {
		
		if(imageServerPort !=  this.imageServerPort){
			
		this.imageServerPort = imageServerPort;
		
		try {
			if(imageServer != null)
			{				
				imageServer.close();
				imageServer = new ServerSocket(imageServerPort);
				if( imageListener != null){
					imageListener = new ImageListener(imageServer, this);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
		
	}

	public boolean isStopped() {
		return isStopped;
	}

	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	public CameraConfigComm(String addressOut, int portOut, int portIn, int imageServerPort,
			Endian endian, CameraCallback cb) {
		this.imageServerPort = imageServerPort;
		portOutgoing = portOut;
		portIncoming = portIn;
		addressOutgoing = addressOut;
		this.endian = endian;
		configUpdate = cb;
	}

	public void updateImage(BufferedImage img) {
		configUpdate.imageUpdate(img);
	}

	/*
	 * public void streamImage(){ new Thread() {
	 * 
	 * @Override public void run() { super.run();
	 * 
	 * 
	 * 
	 * 
	 * } }.start(); }
	 */
	
	/*
	@Override
	public void run() {
		imageListener.setStopped(false);
		while (!isInterrupted() && !isStopped) {

			try {
				
				Thread.sleep(imgLoadDelay);
				OSCBundle bundle = new OSCBundle();
				OSCMessage param = new OSCMessage("/config/param");
				param.addArgument(id);
				param.addArgument(1);
				param.addArgument("set");
				param.addArgument("stream_image");
				param.addArgument("bool");
				param.addArgument("true");
				bundle.addPacket(param);
				try {
					oscPortOut.send(bundle);
				} catch (IOException e) {
					interrupt();
					imageListener.setStopped(true);
					//throw new ProcessException(e.getMessage(), e);
				}
				if (!imageListener.isAlive())
					imageListener.start();

				
			} catch (InterruptedException e) {
				interrupt();
				// System.out.println( "Unterbrechung in sleep()" );
			}
		}
		imageListener.setStopped(true);
	}
	*/

	public void closeConnections()
	{
		if(oscPortOut != null)
			oscPortOut.close();
		if(oscPortIn != null)
		{
			oscPortIn.stopListening();
			oscPortIn.close();
		}
		//if(imageClientUDP != null)
		{
			
			//imageClientUDP.close();
		}
		try {
			if(imageServer != null)
			{				
				imageServer.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initDone = false;
	}
	
	public void initConnection() throws UnknownHostException {
		try {
			//if(oscPortOut == null )
			{
			oscPortOut = new OSCPortOut(InetAddress.getByName(addressOutgoing),
					portOutgoing);
			}
			//if(oscPortIn == null )
			{			
				oscPortIn = new OSCPortIn(portIncoming, endian);
				startConfigListener();
			}
			//imageClientUDP = new DatagramSocket(7778);

			try {
				//if( imageServer == null && !imageServer.isBound()){
					imageServer = new ServerSocket(imageServerPort);
				//}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			initDone = true;
		} catch (SocketException e) {
			throw new ProcessException(e.getMessage(), e);
		}
	}

	public void waitForImage() {
		try {

			/*
			 * byte[] imgArrUDP = new byte[MAX_LEN]; DatagramPacket p = new
			 * DatagramPacket(imgArrUDP, MAX_LEN); imageClientUDP.receive(p);
			 * byte[] imgArr = new byte[p.getLength()];
			 * System.arraycopy(imgArrUDP, 0, imgArr, 0, p.getLength());
			 */

			if (!connectedToImageClient) {
				imageClient = imageServer.accept();
				connectedToImageClient = true;
			}

			is = new DataInputStream(imageClient.getInputStream());

			int i1 = is.read();
			int i2 = is.read();
			int i3 = is.read();
			int i4 = is.read();

			String s1 = Integer.toHexString(i1);
			String s2 = Integer.toHexString(i2);
			String s3 = Integer.toHexString(i3);
			String s4 = Integer.toHexString(i4);

			String sHex = s4 + s3 + s2 + s1;
			int imgLen = Integer.parseInt(sHex, 16);
			byte[] imgArr = new byte[imgLen];
			byte[] imgTemp = new byte[imgLen];

			int numBytesRead = is.read(imgTemp, 0, imgLen);
			System.arraycopy(imgTemp, 0, imgArr, 0, numBytesRead);
			while (numBytesRead < imgLen) {
				int bytesRead = is.read(imgTemp, 0, imgLen);
				System.arraycopy(imgTemp, 0, imgArr, numBytesRead - 1,
						bytesRead);
				numBytesRead += bytesRead;
			}

			// byte [] imgArr = new byte[v.size()];
			// byte[] imgArr = (byte[])al.toArray();
			BufferedImage image = ImageIO
					.read(new ByteArrayInputStream(imgArr));
			File outputFile = new File("image.jpg");
			ImageIO.write(image, "JPG", outputFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void startConfigListener() {

		oscPortIn.addListener("/config/param", new OSCListener() {
			public void acceptMessages(Date time, OSCMessage[] messages) {
				for (OSCMessage message : messages) {
					Object[] arguments = message.getArguments();
					int ackID = -1;
					int numArgs = arguments.length;
					ackID = Integer.parseInt(arguments[0].toString());
					int numParams = Integer.parseInt(arguments[1].toString());
					 for( int i = 2; i < numArgs; i+=4)
					 {					
					String command = arguments[i].toString();
					String name = arguments[i + 1].toString();
					String type = arguments[i + 2].toString();
					 String value = arguments[i+3].toString();
					 configUpdate.configUpdate(name, type, value);
					 }
				}
			}
		});

		oscPortIn.startListening();
	}

	public void sendMultipleParameters(String name, String type, String [] values, int numParams) {
		if (!initDone) {
			try {
				initConnection();
			} catch (UnknownHostException e1) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e1.getMessage(), e1);
				}
				System.out.println("Could not connect to Camera on "
						+ addressOutgoing + " : " + portOutgoing);
				return;
			}
		}
		OSCBundle bundle = new OSCBundle();
		OSCMessage param = new OSCMessage("/config/param");
		param.addArgument(id);
		param.addArgument(numParams);
		param.addArgument("set");
		for( int i = 0; i < numParams; i++ ){
			param.addArgument(name);
			param.addArgument(type);
			param.addArgument(values[i]);
		}
		bundle.addPacket(param);
	//	int len = bundle.getByteArray().length;
		
		try {
			oscPortOut.send(bundle);
		} catch (IOException e) {
			throw new ProcessException(e.getMessage(), e);
		}		
		
	}

	
	
	public void sendParameter(String name, String type, String value) {
		if (!initDone) {
			try {
				initConnection();
			} catch (UnknownHostException e1) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e1.getMessage(), e1);
				}
				System.out.println("Could not connect to Camera on "
						+ addressOutgoing + " : " + portOutgoing);
				return;
			}
		}
		OSCBundle bundle = new OSCBundle();
		OSCMessage param = new OSCMessage("/config/param");
		
		
		
		
		param.addArgument(id);
		param.addArgument(1);
		param.addArgument("set");
		param.addArgument(name);
		param.addArgument(type);
		param.addArgument(value);
		bundle.addPacket(param);
		int len = bundle.getByteArray().length;
		
		if (name.equals("stream_image") && value.equals("true")) {
			
			//init imageServer
			if (imageServer == null) {
				try {		
					imageServer = new ServerSocket(imageServerPort);
				} catch (IOException e) {
					e.printStackTrace();
						imageServer = null;
				}
			}
			if (imageListener == null) {
				imageListener = new ImageListener(imageServer, this);
				imageListener.start();
			}
		}
		
		//send
		try {
			oscPortOut.send(bundle);
		} catch (IOException e) {
			throw new ProcessException(e.getMessage(), e);
	}
		
		
		/*
		if (name.equals("stream_image") && value.equals("true")) {
			try {
				imageServer.close();			
				imageServer = new ServerSocket(imageServerPort);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imageListener = new ImageListener(imageServer, this);
			if (!this.isAlive())
				this.start();
			else
				this.resume();

		} else if (name.equals("stream_image") && value.equals("false")) {
			try {
				imageServer.close();
				imageServer = new ServerSocket(imageServerPort);
				if (this.isAlive())
					this.suspend();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				oscPortOut.send(bundle);
			} catch (IOException e) {
				throw new ProcessException(e.getMessage(), e);
			}
		}
		*/
		id++;

	}
	/*
	 * public void requestImage() { if( !initDone ) { try { initConnection(); }
	 * catch (UnknownHostException e1) { if (LOG.isErrorEnabled()) {
	 * LOG.error(e1.getMessage(), e1); }
	 * System.out.println("Could not connect to Camera on " + addressOutgoing +
	 * " : " + portOutgoing); return; } } OSCBundle bundle = new OSCBundle();
	 * OSCMessage param = new OSCMessage("/config/param");
	 * param.addArgument(id); param.addArgument("get");
	 * param.addArgument("image"); bundle.addPacket(param); int len =
	 * bundle.getByteArray().length; try { oscPortOut.send(bundle); } catch
	 * (IOException e) { throw new ProcessException(e.getMessage(), e); } id++;
	 * }
	 */
}
