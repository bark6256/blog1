package com.cos.blogapp.handler.ex;

/**
 * 
 * @author 용세 2021.09.24
 * 1. 게시글을 못찾았을때 사용
 *
 */

public class MyAsyncNotFoundException extends RuntimeException{
	public MyAsyncNotFoundException(String msg) {
		super(msg);
	}
}
