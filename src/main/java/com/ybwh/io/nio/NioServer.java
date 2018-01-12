  package com.ybwh.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioServer {
	private static int BUF_SIZE = 1024;
	private int port;
	private Selector selector;
	private ExecutorService threadPool = Executors.newFixedThreadPool(10);

	public NioServer(int port) {
		this.port = port;

	}

	public void start() {
		ServerSocketChannel ssc = null;
		try {
			selector = Selector.open();
			ssc = ServerSocketChannel.open();
			ssc.socket().bind(new InetSocketAddress(port));
			ssc.configureBlocking(false);
			//对于ServerSocketChannel来说，accept是惟一的有效操作
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("start success,listen on port " + port);

			while (true) {
				/**
				 * select操作执行时其他线程register,将会阻塞.可以在任意时刻关闭通道或者取消键.
				 * 因为select操作并未对Key.cancell()同步,因此有可能再selectedKey中出现的key是已经被取消的.
				 * 这一点需要注意.需要校验:key.isValid() && key.isReadable()
				 */
				if (selector.select(10000) == 0) {
					System.out.println("==");
					continue;
				}
				Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
				while (iter.hasNext()) {
					SelectionKey key = iter.next();
					if (key.isAcceptable()) {
						System.out.println("%%%%%%%%%%%%accept");
						handleAccept(key);
					}
					if (key.isValid() && key.isReadable()) {
						System.out.println("%%%%%%%%%%%%read");
						handleRead(key);
					}
					if (key.isWritable() && key.isValid()) {
						System.out.println("%%%%%%%%%%%%write");
						handleWrite(key);
					}
					if (key.isConnectable()) {
						System.out.println("%%%%%%%%%%%%isConnectable = true");
					}
					iter.remove();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() throws IOException {
		selector.wakeup();
		selector.close();
		threadPool.shutdown();
	}

	public static void handleAccept(SelectionKey key) throws IOException {
		ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
		SocketChannel sc = ssChannel.accept();
		sc.configureBlocking(false);
		
		
		/**
		 * 调用信道的register()方法可以将一个选择器注册到该信道。
		 * 在注册过程中，通过存储在int型数据中的位图来指定该信道上的初始兴趣操作集（见上文的"SelectionKey：兴趣操作集"）。
		 * register()方法将返回一个代表了信道和给定选择器之间的关联的SelectionKey实例。
		 * validOps()方法用于返回一个指示了该信道上的有效I/O操作集的位图。
		 * 对于ServerSocketChannel来说，accept是惟一的有效操作，
		 * 而对于SocketChannel来说，有效操作包括读、写和连接。
		 * 对于DatagramChannel，只有读写操作是有效的。
		 * 一个信道可能只与一个选择器注册一次，因此后续对register()方法的调用只是简单地更新该key所关联的兴趣操作集。
		 * 使用isRegistered()方法可以检查信道是否已经注册了选择器。
		 * keyFor()方法与第一次调用register()方法返回的是同一个SelectionKey实例，除非该信道没有注册给定的选择器。
		 */
		//任何对key（信道）所关联的兴趣操作集的改变，都只在下次调用了select()方法后才会生效。
		sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(BUF_SIZE));
		
	}

	public static void handleRead(SelectionKey key) throws IOException {
		SocketChannel sc = (SocketChannel) key.channel();
		ByteBuffer buf = (ByteBuffer) key.attachment();
		long bytesRead = sc.read(buf);
		while (bytesRead > 0) {
			buf.flip();
			while (buf.hasRemaining()) {
				System.out.print((char) buf.get());
			}
			System.out.println();
			buf.clear();
			bytesRead = sc.read(buf);
		}
		if (bytesRead == -1) {
			sc.close();
		}
	}

	public static void handleWrite(SelectionKey key) throws IOException {
		ByteBuffer buf = (ByteBuffer) key.attachment();
		buf.flip();
		SocketChannel sc = (SocketChannel) key.channel();
		while (buf.hasRemaining()) {
			sc.write(buf);
		}
		buf.compact();
	}

	public static void main(String[] args) {
		new NioServer(5555).start();

	}

}
