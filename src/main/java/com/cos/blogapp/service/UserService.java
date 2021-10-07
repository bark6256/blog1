package com.cos.blogapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.domain.user.UserRepository;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotFoundException;
import com.cos.blogapp.util.MyAlgorithm;
import com.cos.blogapp.util.SHA;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;
import com.cos.blogapp.web.dto.UserUpdateDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	
	// 이건 하나의 서비스인가? (핵심로직 > principal 값 변경 + update (+ 세션값 변경(x)) )
	
	@Transactional(rollbackFor = MyAsyncNotFoundException.class)
	public void 유저정보수정(UserUpdateDto dto, User principal) {
		User userEntity = userRepository.findById(principal.getId())
				.orElseThrow(()-> new MyAsyncNotFoundException("회원정보를 찾을 수 없습니다."));
		userEntity.setEmail(dto.getEmail());
	}
	
	@Transactional(rollbackFor = MyNotFoundException.class)
	public void 회원가입(JoinReqDto dto) {
		String encPassowrd = SHA.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		dto.setPassword(encPassowrd);
		
		userRepository.save(dto.toEntity());
	}
	
	public User 로그인(LoginReqDto dto) {
		String encPassowrd = SHA.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		dto.setPassword(encPassowrd);
		User userEntity = userRepository.mLogin(dto.getUsername(), dto.getPassword());
		return userEntity;
	}
}
