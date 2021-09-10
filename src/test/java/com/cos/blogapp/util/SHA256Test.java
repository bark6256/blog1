package com.cos.blogapp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

public class SHA256Test {
	
	@Test
	public static void encrypt(){
		System.out.println("시작");
		String rawPassword = "1234";
		// 1. SHA256 함수를 가진 클래스 객체 가져오기
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		// 2. 비밀번호를 SHA256에게 던지기
		md.update(rawPassword.getBytes());
		
		// 3. 암호화된 글자를 16진수로 변환(헥사코드)
		StringBuilder sb = new StringBuilder();
		for(Byte b : md.digest()) {
			sb.append(String.format("%02x", b));
		}
		System.out.println(sb.toString());
		System.out.println(sb.length());
	}
}
