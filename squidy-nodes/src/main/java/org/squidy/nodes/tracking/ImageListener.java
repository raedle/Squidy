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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.imageio.ImageIO;


public class ImageListener extends Thread {
	private ServerSocket imageServer;
	public ServerSocket getImageServer() {
		return imageServer;
	}

	public void setImageServer(ServerSocket imageServer) {
		this.imageServer = imageServer;
	}

	private Socket imageClient;
	private boolean connectedToImageClient = false;
	private DataInputStream is;
	private ImageCallback imageCallback;
	private BufferedImage image;
	private byte[] imgArr;
	private byte[] imgArrTemp;
	private byte[] imgTemp;
	private boolean isStopped = true;
	public boolean isStopped() {
		return isStopped;
	}

	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	public ImageListener(ServerSocket imageServer, ImageCallback cb) {

		this.imageServer = imageServer;
		imageCallback = cb;
	}

	@Override
	public void run ()
	{
		
		while(!isInterrupted()/* && !isStopped*/){
			
			//try {
			imgArr = null;
				try {
					imgArr = loadImage();
				} catch (IOException e) {					
					interrupt(); 
					e.printStackTrace();
				}
				if( imgArr != null )
				{
					if( imgArr[0] > 0)
					{
						
					
					try {
						image = ImageIO.read(new ByteArrayInputStream(imgArr));						
						imageCallback.updateImage(image);
					} catch (IOException e) {
						try {
							sleep(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// TODO Auto-generated catch block
						
						//e.printStackTrace();
					}

					}
				}
				else
					interrupt(); 
			//} catch (SocketException e) {
			//	interrupt(); 
			//	e.printStackTrace();
			//}
			
		}
	}
	
	public byte[] loadImage() throws IOException {

		try {

			/*
			 * byte[] imgArrUDP = new byte[MAX_LEN]; DatagramPacket p = new
			 * DatagramPacket(imgArrUDP, MAX_LEN); imageClientUDP.receive(p);
			 * byte[] imgArr = new byte[p.getLength()];
			 * System.arraycopy(imgArrUDP, 0, imgArr, 0, p.getLength());
			 */

			// if (!connectedToImageClient) {
			if (imageServer == null)
				return null;
			if (imageClient == null) {
				try {
					imageClient = imageServer.accept();
					connectedToImageClient = true;
					is = new DataInputStream(imageClient.getInputStream());
				} catch (SocketException ex) {
					//throw new IOException(ex.getMessage());
					return null;
				}
			}

			int i1 = is.read();
			int i2 = is.read();
			int i3 = is.read();
			int i4 = is.read();

			String s1 = Integer.toHexString(i1);
			String s2 = Integer.toHexString(i2);
			String s3 = Integer.toHexString(i3);
			String s4 = Integer.toHexString(i4);

			if( s1.length() < 2 )
			{
				s1 = "0" + s1;
				
			}
			if( s2.length() < 2 )
			{
				s2 = "0" + s2;
				
			}
			if( s3.length() < 2 )
			{
				s3 = "0" + s3;
				
			}
			if( s4.length() < 2 )
			{
				s4 = "0" + s4;
				
			}
						
			
			String sHex = s4 + s3 + s2 + s1;
			int imgLen = Integer.parseInt(sHex, 16);
			int clusterLen = 5000;
	
			
			if( imgArrTemp == null || imgTemp == null){
				imgArrTemp = new byte[imgLen];
				imgTemp = new byte[imgLen];				
			}
			
			if( imgArr==null || imgLen != imgArr.length || imgLen != imgArrTemp.length){
				imgArrTemp = new byte[imgLen];
				imgTemp = new byte[imgLen];
			}

			int numBytesRead = is.read(imgTemp, 0, imgLen);
			System.arraycopy(imgTemp, 0, imgArrTemp, 0, numBytesRead);
			while (numBytesRead < imgLen) {
				imgTemp = new byte[imgLen];
				int bytesRead = is.read(imgTemp, 0, imgLen-numBytesRead );
				System.arraycopy(imgTemp, 0, imgArrTemp, numBytesRead,
						bytesRead);
				numBytesRead += bytesRead;
			}

			// byte [] imgArr = new byte[v.size()];
			// byte[] imgArr = (byte[])al.toArray();
			
			//BufferedImage image = ImageIO.read(new ByteArrayInputStream(imgArr));
			return imgArrTemp;

		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}
	
	@Deprecated
	public byte[] oldLoadImage() throws IOException {

		try {

			/*
			 * byte[] imgArrUDP = new byte[MAX_LEN]; DatagramPacket p = new
			 * DatagramPacket(imgArrUDP, MAX_LEN); imageClientUDP.receive(p);
			 * byte[] imgArr = new byte[p.getLength()];
			 * System.arraycopy(imgArrUDP, 0, imgArr, 0, p.getLength());
			 */

			// if (!connectedToImageClient) {
			if (imageServer == null)
				return null;

			try {
				imageClient = imageServer.accept();
			} 
			catch (SocketException ex) 
			{
				//throw new IOException(ex.getMessage());
				return null;
			}
			connectedToImageClient = true;
			// }

			is = new DataInputStream(imageClient.getInputStream());

			int i1 = is.read();
			int i2 = is.read();
			int i3 = is.read();
			int i4 = is.read();

			String s1 = Integer.toHexString(i1);
			String s2 = Integer.toHexString(i2);
			String s3 = Integer.toHexString(i3);
			String s4 = Integer.toHexString(i4);

			if( s1.length() < 2 )
			{
				s1 = "0" + s1;
				
			}
			if( s2.length() < 2 )
			{
				s2 = "0" + s2;
				
			}
			if( s3.length() < 2 )
			{
				s3 = "0" + s3;
				
			}
			if( s4.length() < 2 )
			{
				s4 = "0" + s4;
				
			}
						
			
			String sHex = s4 + s3 + s2 + s1;
			int imgLen = Integer.parseInt(sHex, 16);
			int clusterLen = 5000;
	
			
			if( imgArrTemp == null || imgTemp == null){
				imgArrTemp = new byte[imgLen];
				imgTemp = new byte[imgLen];				
			}
			
			if( imgArr==null || imgLen != imgArr.length || imgLen != imgArrTemp.length){
				imgArrTemp = new byte[imgLen];
				imgTemp = new byte[imgLen];
			}

			int numBytesRead = is.read(imgTemp, 0, imgLen);
			System.arraycopy(imgTemp, 0, imgArrTemp, 0, numBytesRead);
			while (numBytesRead < imgLen) {
				imgTemp = new byte[imgLen];
				int bytesRead = is.read(imgTemp, 0, imgLen-numBytesRead );
				System.arraycopy(imgTemp, 0, imgArrTemp, numBytesRead,
						bytesRead);
				numBytesRead += bytesRead;
			}

			// byte [] imgArr = new byte[v.size()];
			// byte[] imgArr = (byte[])al.toArray();
			
			//BufferedImage image = ImageIO.read(new ByteArrayInputStream(imgArr));
			return imgArrTemp;

		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}

	}
}
