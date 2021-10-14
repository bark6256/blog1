package com.cos.blogapp.web.dto;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.service.CommentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class CommentController {
	private final CommentService commentService;
	private final HttpSession session;
	
	@DeleteMapping("/comment/{id}")
	public @ResponseBody CMRespDto<?> deleteById(@PathVariable int id){
		User principal = (User) session.getAttribute("principal");
		commentService.댓글삭제(id, principal);
		return new CMRespDto<>(1, "성공", null);
	}
}
