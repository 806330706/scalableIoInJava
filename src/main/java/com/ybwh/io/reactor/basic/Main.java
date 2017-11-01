package com.ybwh.io.reactor.basic;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		try {
			Reactor reactor = new Reactor(5555);
			
			new Thread(reactor).start();
			System.out.println("reactor listen on port 5555!!");
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
