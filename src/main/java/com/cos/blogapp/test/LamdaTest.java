package com.cos.blogapp.test;

// 1.8 람다식
// 함수를 넘기는게 목적
// 인터페이스의 함수가 하나일때만 가능하다
// 쓰면 코드 간결해지고, 함수를 몰라도 됨
// {}를 안쓰면 한줄만 쓸수있고 해당 값을 자동으로 리턴한다.
interface MySupplier {
	void get();
}
public class LamdaTest {
	
//	static void start(MySupplier s) {
//		s.get();
//	}
//	
//	public static void main(String[] args) {
//		
//		start(  () -> {
//				System.out.println("get 함수 호출됨");
//		});
//	}
}
