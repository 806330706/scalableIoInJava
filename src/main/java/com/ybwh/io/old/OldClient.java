package com.ybwh.io.old;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class OldClient {

	public static void main(String[] args) {
		int port = 6666;
		Socket socket = null;
		try {
			socket = new Socket("127.0.0.1", port);
			socket.getOutputStream().write("dsfsfsfs".getBytes("UTF-8"));
			byte[] input = new byte[1024];
			socket.getInputStream().read(input);
			System.out.println("$$$$$ "+new String(input,"UTF-8"));
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != socket) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
