package com.ybwh.io.old;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class ServerHandler implements Runnable {
	private static final int MAX_INPUT = 1024;
	private Socket socket;

	public ServerHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {

		try {
			byte[] input = new byte[MAX_INPUT];
			socket.getInputStream().read(input);//接收
			byte[] output = process(input);//处理
			socket.getOutputStream().write(output);//回应
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private byte[] process(byte[] cmd) {
		
		try {
			System.out.println("%%%%%%"+new String(cmd,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return cmd;
	}

}
