package com.cos.blogapp.domain.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 테이블 모델, DB의 테이블 모습과 일치
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id; // PK (자동 증가 번호)
	private String username;
	private String password;
	private String email;
}
