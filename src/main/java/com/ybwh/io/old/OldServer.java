package com.ybwh.io.old;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OldServer {

	public static void main(String[] args) {
		int port = 6666;
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(port);
			System.out.println("OldServer start listen on port " + port + "!!!!");

			while (true) {
				Socket socket = ss.accept();// 不断接收请求
				new Thread(new ServerHandler(socket)).start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != ss) {
					ss.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
