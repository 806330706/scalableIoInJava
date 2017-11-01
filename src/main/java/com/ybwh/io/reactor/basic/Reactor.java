package com.ybwh.io.reactor.basic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

class Reactor implements Runnable {
	final Selector selector;
	final ServerSocketChannel serverSocket;

	Reactor(int port) throws IOException { // Reactor初始化
		selector = Selector.open();
		serverSocket = ServerSocketChannel.open();
		serverSocket.socket().bind(new InetSocketAddress(port));
		serverSocket.configureBlocking(false); // 非阻塞
		SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT); // 分步处理,第一步,接收accept事件
		sk.attach(new Acceptor()); // attach callback object, Acceptor
	}

	public void run() {
		try {
			while (!Thread.interrupted()) {
				selector.select();
				Set<SelectionKey> selected = selector.selectedKeys();
				Iterator<SelectionKey> it = selected.iterator();
				while (it.hasNext())
					dispatch((SelectionKey) it.next()); // Reactor负责dispatch收到的事件
				selected.clear();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	void dispatch(SelectionKey k) {// 单线程分发
		Runnable r = (Runnable) k.attachment(); // 调用之前注册的callback对象
		if (r != null)
			r.run();
	}

	class Acceptor implements Runnable { // inner
		public void run() {
			try {
				SocketChannel c = serverSocket.accept();
				System.out.println("%%%%%%%%%%%%%accept");
				if (c != null)
					new Handler(selector, c);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
