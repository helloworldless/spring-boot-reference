package com.davidagood.springbootreference;

import lombok.SneakyThrows;

import java.net.ServerSocket;

public class TestUtil {

	@SneakyThrows
	public static int getFreePort() {
		ServerSocket socket = new ServerSocket(0);
		int port = socket.getLocalPort();
		socket.close();
		return port;
	}

}
