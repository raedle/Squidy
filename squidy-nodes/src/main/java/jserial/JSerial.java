package jserial;

import javax.swing.JPanel;

public class JSerial{

	static {
		try {
			System.loadLibrary("/ext/jserial");
		} catch (UnsatisfiedLinkError ule) {
		      System.err.println("Native code library 'jserial.dll' failed to load.\n" + ule);
		}
	}

	private int com_id = -1;

	public boolean open(int number) {
		if((com_id = openSerialPort(number)) == -1)
		{
			return false;
		}
		return true;
	}

	public boolean close() {
		if(com_id == -1)
			return false;

		if(closeSerialPort(com_id) == -1)
			return false;

		return true;
	}

	public int readBytes(byte[] buf, int bytes_to_read, int buf_offset) {
		if(com_id == -1)
			return -1;

		return readSerialBytes(com_id, buf, bytes_to_read, buf_offset);
	}

	public int writeBytes(byte[] buf, int bytes_to_write, int buf_offset) {
		if(com_id == -1)
			return -1;

		return writeSerialBytes(com_id, buf, bytes_to_write, buf_offset);
	}

	public static void main(String[] args) {
		JSerial js = new JSerial();
		System.out.println("connect: " + js.open(6));

		byte[] buf = new byte[1024];
		int read = js.readBytes(buf, 1024, 0);
		while(read > 0){
			System.out.println("read bytes: " + read);
			for(int i=0;i<read;i++) {
				System.out.print((char)buf[i]);
			}
			buf = new byte[1024];
			read = js.readBytes(buf, 1024, 0);
			Thread.yield();
		}
		js.close();
	}

	private static native int openSerialPort(int number);
	private static native int closeSerialPort(int number);

	private static native int readSerialBytes(int number, byte[] buf, int bytes_to_read, int buf_offset);
	private static native int writeSerialBytes(int number, byte[] buf, int bytes_to_write, int buf_offset);

}
