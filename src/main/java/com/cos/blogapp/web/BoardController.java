package com.cos.blogapp.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotFoundException;
import com.cos.blogapp.service.BoardService;
import com.cos.blogapp.service.CommentService;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.BoardSaveReqDto;
import com.cos.blogapp.web.dto.CMRespDto;
import com.cos.blogapp.web.dto.CommentSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BoardController {
	
	private final BoardService boardService;
	private final CommentService commentService;
	private final HttpSession session;

	// 1. 컨트롤러 선정,
	// 2. http method 선정,
	// 3. 받을 데이터가 있는지(body, queryString, pathVariable)
	// 쿼리스트링 : board?----  ? 이후의 내용으로 전달(get)
	// queryString, pathVariable -> DB의 where에 걸리는 내용
	// POST는 3종류 다 받을수 있음.
	// 4. DB에 접근을 해야하면 Model에 접근. 아니면 접근할 필요가 없다.
	
	// ---- 댓글달기
	@PostMapping("/api/board/{boardId}/comment")
	public String commentSave(@PathVariable int boardId, CommentSaveReqDto dto) {
		// 1. DTO로 데이터 받기
		// 2. Comment 객체 만들기 (빈객체 생성)
		// 3. Comment 객체에 값 추가하기 , id : X, content: DTO값, user: 세션값, board: boardId로 findById하세요

		User principal = (User) session.getAttribute("principal");
		
		commentService.댓글작성(boardId, dto, principal);
		return "redirect:/board/"+boardId;
	}
	
	// ---- 게시글 수정
	//@requestBody -> 있는 그대로 가져온다
	@PutMapping("/api/board/{id}")
	public @ResponseBody CMRespDto<String> update(@PathVariable int id,@Valid @RequestBody BoardSaveReqDto dto, BindingResult bindingResult){
		
		User principal = (User) session.getAttribute("principal");
		
		// 유효성 검사
		if (bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
			}
			throw new MyAsyncNotFoundException(errorMap.toString());
		}
			
		boardService.게시글수정(id, principal, dto);
		return new CMRespDto<>(1, "업데이트 성공", null);
	}
	
	// ---- 게시글 수정 페이지 이동
	@GetMapping("/board/{id}/updateForm")
	public String boardUpdateForm(@PathVariable int id, Model model) {
		
		model.addAttribute("boardEntity",boardService.게시글수정페이지이동(id));
		return "board/updateForm";
	}
	
	// 게시글 삭제
	@DeleteMapping("/api/board/{id}")
	public @ResponseBody CMRespDto<String> deleteById(@PathVariable int id) {
		// 인증이 된 사람만 함수 접근 가능!! (로그인 된 사람)
		User principal = (User) session.getAttribute("principal");
		boardService.게시글삭제(id, principal);
		
		return new CMRespDto<String>(1, "성공", null); // @ResponseBody 데이터 리턴!! String = text/plain
	}
	
	// ---- 게시글 상세보기
	@GetMapping("/board/{id}")
	public String detail(@PathVariable int id, Model model) {
		// 1. orElse 는 값을 찾으면 Board가 리턴, 못찾으면 (괄호안의 내용 리턴)
		// default값이 필요하다면 orElse를 쓴다.
		// Board boardEntity = boardRepository.findById(id).orElse(new Board());
		
		// 2. orElseThrow 익셉션 발생시 이 함수를 호출한곳으로 오류 메시지를 던져준다.
		//     컨트롤러를 호출한 곳 - 디스페쳐 서블릿이 받는다.

		// Board 객체에 존재하는 것 (Board(0), User(0), List<Comment>(x))
		model.addAttribute("boardEntity", boardService.게시글상세보기(id));
		return "board/detail"; // ViewResolver
	}
	
	// ---- 게시글 쓰기 페이지 이동
	@GetMapping("/board/saveForm")
	public String saveForm() {
		return "board/saveForm";
	}
	
	// ---- 게시글 등록
	@PostMapping("/api/board")
	public @ResponseBody String save(@Valid BoardSaveReqDto dto, BindingResult bindingResult) {

		User principal = (User)session.getAttribute("principal");
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
			}
			return Script.back(errorMap.toString());
		}
		
		boardService.게시글등록(dto, principal);
		
		return Script.href("/", "글쓰기 성공");
	}
	
	// ---- 게시글 목록
	@GetMapping("/board")
	public String home(Model model, int page) {
		
		model.addAttribute("boardsEntity", boardService.게시글목록보기(page));
		return "board/list";
	}
	
}
