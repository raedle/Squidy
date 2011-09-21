package org.squidy.nodes.reactivision.remote.control;

import java.io.IOException;
import java.nio.channels.SocketChannel;


public class ControlSocket {
	private SocketChannel socketChannel;
	
	ControlSocket(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}
	
	void send(ControlMessage message) throws IOException {
			message.send(socketChannel);
	}
	
	ControlMessage receive() throws IOException{
		return ControlMessage.read(socketChannel);
	}
	
	void close() {
		try {
			socketChannel.close();
		} catch (IOException e) {}
	}
}
