package com.cos.blogapp.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotFoundException;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.CMRespDto;

//controller - 1. 익셉션 핸들링, 2. @Controller의 역활을 한다.
@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(value = MyNotFoundException.class)
	public @ResponseBody String error1(MyNotFoundException e) {
		System.out.println("오류 발생 : " + e.getMessage());
		
		return Script.href("/", e.getMessage());
	}
	
	// fetch 요청
	@ExceptionHandler(value = MyAsyncNotFoundException.class)
	public @ResponseBody CMRespDto<String> error2(MyAsyncNotFoundException e) {
		System.out.println("오류 발생");
		System.out.println(e.getMessage());
		
		return new CMRespDto<String>(-1, e.getMessage(), null);
	}
}
