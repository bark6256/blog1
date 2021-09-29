package com.cos.blogapp.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.domain.user.UserRepository;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.util.MyAlgorithm;
import com.cos.blogapp.util.SHA;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.CMRespDto;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;
import com.cos.blogapp.web.dto.UserUpdateDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {
	
	private final UserRepository userRepository;
	private final HttpSession session;
	
	@PutMapping("/user/{id}")
	public @ResponseBody CMRespDto<String> update(@PathVariable int id, @Valid @RequestBody UserUpdateDto dto,
			BindingResult bindingResult) {
		// 유효성
		if (bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
			}
			throw new MyAsyncNotFoundException(errorMap.toString());
		}

		// 인증
		User principal = (User) session.getAttribute("principal");
		if (principal == null) { // 로그인 안됨
			throw new MyAsyncNotFoundException("인증이 되지 않았습니다.");
		}

		// 권한
		if (principal.getId() != id) {
			throw new MyAsyncNotFoundException("회원정보를 수정할 권한이 없습니다.");
		}

		// 핵심로직
		principal.setEmail(dto.getEmail());
		session.setAttribute("principal", principal); // 세션 값 변경

		userRepository.save(principal);

		return new CMRespDto<>(1, "성공", null);
	}
	
	@GetMapping("/joinForm")         // --------------- 회원가입 --------------
	public String joinForm() {
		return "user/joinForm";
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
		return Script.href("/loginForm"); // 리다이렉션 (http 상태코드 300)
	}
	
	@GetMapping("/loginForm")		//------------- 로그인 ---------------
	public String loginForm() {
		return "user/loginForm";
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
	
	@GetMapping("/logout")
	public String logout() {
		session.invalidate();	// 세션에 있는 값 초기화
		return "redirect:/";
	}
	
	// {id} : 페스벨리어블, 주소에서 값을 가져온다.
	@GetMapping("/user/{id}")
	public String userInfo(@PathVariable int id) {
		// 기본은 userRepository.findById(id) 디비에서 가져와야 한다.
		// 편법으로 세션값을 가져오는 방법도 있다.
		
		return "user/updateForm";
	}
}
