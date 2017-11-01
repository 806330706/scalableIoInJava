package com.ybwh.nio;

import java.nio.ByteBuffer;

import org.junit.Test;

public class TestByteBuffer {
	@Test
	public void testRead(){
		ByteBuffer input = ByteBuffer.allocate(1024);
		input.put("111111".getBytes());
		System.out.println(input.position());
		input.flip();
		
		
	}
}
