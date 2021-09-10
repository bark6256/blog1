package com.cos.blogapp.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.domain.user.UserRepository;
import com.cos.blogapp.util.MyAlgorithm;
import com.cos.blogapp.util.SHA;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {
	
	private final UserRepository userRepository;
	private final HttpSession session;
		
	//http://localhost:8080/login -> login.jsp
	// views/user/login.jsp
	@GetMapping("/loginForm")
	public String loginForm() {
		return "user/loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "user/joinForm";
	}
	
	@PostMapping("/login")
	public @ResponseBody String login(@Valid LoginReqDto dto, BindingResult bindingResult) {
		// 최소 입력 조건 확인
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for( FieldError error : bindingResult.getFieldErrors() ) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드 : " + error.getField());
				System.out.println("메시지 : " + error.getDefaultMessage());
			}
			return Script.back(errorMap.toString());
		}
		// 1. username, password 받기 -> LoginReqDto dto
		String encPassowrd = SHA.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		dto.setPassword(encPassowrd);
		// 2. DB -> SELECT
		User userEntity = userRepository.mLogin(dto.getUsername(), dto.getPassword());
		
		if(userEntity == null) { // 로그인 실패
			return Script.back("아이디와 암호가 틀렸습니다.");
		} else {								// 로그인 성공
			// 세션 날라가는 조건 : 1.swssion.invalidate() 2. 브라우저 닫기
			session.setAttribute("principal", userEntity); // principal : 인증된 사용자.
			return Script.href("/","로그인 성공");
		}
		
		// 3-1 : 있으면 session에 저장(user 정보 전체 다 가져온다. 그러면 정보가 필요할때 세션에서 가져올수있다)
		// 3-2 : main페이지 돌려주기
	}
	
	@PostMapping("/join")
	public @ResponseBody String join(@Valid JoinReqDto dto, BindingResult bindingResult) {	// username=love&password=1234&email=love@naver.com
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for( FieldError error : bindingResult.getFieldErrors() ) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드 : " + error.getField());
				System.out.println("메시지 : " + error.getDefaultMessage());
			}
			return Script.back(errorMap.toString());
		}
		
		String encPassowrd = SHA.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		dto.setPassword(encPassowrd);
		
		userRepository.save(dto.toEntity());
		return Script.href("/joinForm"); // 리다이렉션 (http 상태코드 300)
	}
}
