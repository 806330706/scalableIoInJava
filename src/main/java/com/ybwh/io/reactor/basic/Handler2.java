package com.ybwh.io.reactor.basic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

class Handler2 { // ...
	private static final int MAXIN = 1024;
	private static final int MAXOUT = 0;
	final SocketChannel socket;
	final SelectionKey sk;
	ByteBuffer input = ByteBuffer.allocate(MAXIN);
	ByteBuffer output = ByteBuffer.allocate(MAXOUT);
	static final int READING = 0, SENDING = 1;
	int state = READING;

	Handler2(Selector sel, SocketChannel c) throws IOException {
		socket = c;
		c.configureBlocking(false);
		// Optionally try first read now
		sk = socket.register(sel, 0);
		sk.attach(this); // 将Handler作为callback对象
		sk.interestOps(SelectionKey.OP_READ); // 第二步,接收Read事件
		sel.wakeup();
	}

	boolean inputIsComplete() {
		return false;
	}

	boolean outputIsComplete() {
		return false;
	}

	void process() {
		/* ... */ }

	public void run() { // initial state is reader
		try {
			socket.read(input);
			if (inputIsComplete()) {
				process();
				sk.attach(new Sender()); // 状态迁移, Read后变成write,
											// 用Sender作为新的callback对象
				sk.interestOps(SelectionKey.OP_WRITE);
				sk.selector().wakeup();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class Sender implements Runnable {
		public void run() { // ...
			try {
				socket.write(output);
				if (outputIsComplete())
					sk.cancel();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}