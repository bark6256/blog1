package com.cos.blogapp.util;

import lombok.Getter;

@Getter
public enum MyAlgorithm {
	SHA256("SHA-256"), SHA512("SHA-512");

	// 키값을 변경하고싶을때 사용
	private String type;
	private MyAlgorithm(String type) {
		this.type = type;
	}
}
