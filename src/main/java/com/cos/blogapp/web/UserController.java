package com.cos.blogapp.web;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.domain.user.UserRepository;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {
	
	private final UserRepository userRepository;
	private final HttpSession session;
	
	
	@GetMapping({"/", "/home"})
	public String home() {
		return "home";
	}
	
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
	public String login(LoginReqDto dto) {
		// 1. username, password 받기
		if(dto.getUsername() == null ||
				dto.getPassword() == null ||
				!dto.getUsername().equals("") || 
				!dto.getPassword().equals("")
				)
			{
				return "error/error";
			}
		// 2. DB -> SELECT
		User userEntity = userRepository.mLogin(dto.getUsername(), dto.getPassword());
		
		if(userEntity == null) { // 로그인 실패
			return "redirect:/loginForm";
		} else {
			session.setAttribute("principal", userEntity); // principal : 인증된 사용자.
			return "redirect:/home";
		}
		
		// 3-1 : 있으면 session에 저장(user 정보 전체 다 가져온다. 그러면 정보가 필요할때 세션에서 가져올수있다)
		// 3-2 : main페이지 돌려주기
	}
	
	@PostMapping("/join")
	public String join(JoinReqDto dto) {	// username=love&password=1234&email=love@naver.com
		if(dto.getUsername() == null ||
			dto.getPassword() == null ||
			dto.getEmail() == null ||
			!dto.getUsername().equals("") || 
			!dto.getPassword().equals("") || 
			!dto.getEmail().equals("")
			)
		{
			return "error/error";
		}
		
		
		User user = new User();
		user.setUsername(dto.getUsername());
		user.setPassword(dto.getPassword());
		user.setEmail(dto.getEmail());
		
		userRepository.save(user);
		return "redirect:/loginForm"; // 리다이렉션 (http 상태코드 300)
	}
}
