package com.cos.blogapp.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserControllerTest {
	
	@GetMapping("/test/join")
	public @ResponseBody String testJoin() {
		return "test/join";
	}
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin() {
		return "<script>alert('hello');</script>";
	}
	
	@GetMapping("/test/data/{num}")
	public @ResponseBody String testData(@PathVariable int num) {
		
		
		return "" + num;
	}
}
