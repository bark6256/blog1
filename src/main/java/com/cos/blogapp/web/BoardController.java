package com.cos.blogapp.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.comment.Comment;
import com.cos.blogapp.domain.comment.CommentRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotFoundException;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.BoardSaveReqDto;
import com.cos.blogapp.web.dto.CMRespDto;
import com.cos.blogapp.web.dto.CommentSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BoardController {
	
	private final BoardRepository boardRepository;
	private final CommentRepository commentRepository;
	private final HttpSession session;
	
	// ---- 댓글달기
	@PostMapping("/board/{boardId}/comment")
	public String commentSave(@PathVariable int boardId, CommentSaveReqDto dto) {

		// 1. DTO로 데이터 받기

		// 2. Comment 객체 만들기 (빈객체 생성)
		Comment comment = new Comment();

		// 3. Comment 객체에 값 추가하기 , id : X, content: DTO값, user: 세션값, board: boardId로 findById하세요
		User principal = (User) session.getAttribute("principal");
		Board boardEntity = boardRepository.findById(boardId)
				.orElseThrow(()-> new MyNotFoundException("해당 게시글을 찾을 수 없습니다."));

		comment.setContent(dto.getContent());
		comment.setUser(principal);
		comment.setBoard(boardEntity);

		// 4. save 하기
		commentRepository.save(comment);

		return "redirect:/board/"+boardId;
	}
	
	//@requestBody -> 있는 그대로 가져온다
	@PutMapping("/board/{id}")
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
		
		// 인증 (공통로직)
		if (principal == null) { // 로그인 안됨
			throw new MyAsyncNotFoundException("인증이 되지 않았습니다");
		}
		
		// 권한
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(()-> new MyAsyncNotFoundException("해당 게시글을 찾을 수 없습니다."));
		if(principal.getId() != boardEntity.getUser().getId()) {
			throw new MyAsyncNotFoundException("해당 게시글의 주인이 아닙니다.");
		}
		
		
		Board board = dto.toEntity(principal);
		board.setId(id);
		
		boardRepository.save(board);
		
		return new CMRespDto<>(1, "업데이트 성공", null);
	}
	
	@GetMapping("/board/{id}/updateForm")
	public String boardUpdateForm(@PathVariable int id, Model model) {
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(()-> new MyNotFoundException(id + "번의 게시글을 찾을 수 없습니다."));
		
		model.addAttribute(boardEntity);
		
		return "board/updateForm";
	}
	
	@GetMapping("/board")
	public String home(Model model, int page) {
		
		PageRequest pageRequest = PageRequest.of(page, 3, Sort.by("id").descending());
		
	//	List<Board> boardsEntity = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		Page<Board> boardsEntity = boardRepository.findAll(pageRequest);
		
		model.addAttribute("boardsEntity", boardsEntity);
		return "board/list";
	}
	
	@GetMapping("/board/saveForm")
	public String saveForm() {
		return "board/saveForm";
	}
	
	@PostMapping("/board")
	public @ResponseBody String save(@Valid BoardSaveReqDto dto, BindingResult bindingResult) {

		User principal = (User)session.getAttribute("principal");
		
		// 인증체크
		if(principal == null) { // 로그인 안됨
			return Script.href("/","잘못된 접근입니다");
		}
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
			}
			return Script.back(errorMap.toString());
		}
		
		
	//	User user = new User();
	//	user.setId(3);
	//	boardRepository.save(dto.toEntity(user));
		
		boardRepository.save(dto.toEntity(principal));
		return Script.href("/", "글쓰기 성공");
	}
	
	// 1. 컨트롤러 선정,
	// 2. http method 선정,
	// 3. 받을 데이터가 있는지(body, queryString, pathVariable)
	// 쿼리스트링 : board?----  ? 이후의 내용으로 전달(get)
	// queryString, pathVariable -> DB의 where에 걸리는 내용
	// POST는 3종류 다 받을수 있음.
	// 4. DB에 접근을 해야하면 Model에 접근. 아니면 접근할 필요가 없다.
	@GetMapping("/board/{id}")
	public String detail(@PathVariable int id, Model model) {
		/*
		 *  1. orElse 는 값을 찾으면 Board가 리턴, 못찾으면 (괄호안의 내용 리턴)
		 *  default값이 필요하다면 orElse를 쓴다.
		 *  Board boardEntity = boardRepository.findById(id).orElse(new Board());
		 */
		
		// 2. orElseThrow 익셉션 발생시 이 함수를 호출한곳으로 오류 메시지를 던져준다.
		//     컨트롤러를 호출한 곳 - 디스페쳐 서블릿이 받는다.
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(() -> 
					new MyNotFoundException(id + "번은 없는 게시글입니다")
				);
		
		model.addAttribute("boardEntity", boardEntity);
		return "board/detail";
	}
	
	@DeleteMapping("/board/{id}")
	public @ResponseBody CMRespDto<String> deleteById(@PathVariable int id) {
		// 인증이 된 사람만 함수 접근 가능!! (로그인 된 사람)
		User principal = (User) session.getAttribute("principal");
		if(principal == null) {
			throw new MyAsyncNotFoundException("인증이 되지 않았습니다.");
		}
		
		
		Board boardEntity = boardRepository.findById(id)
			.orElseThrow(()-> new MyAsyncNotFoundException("해당글을 찾을 수 없습니다."));
		
		// 권한이 있는 사람만 함수 접근 가능(principal.id == {id})
		if(principal.getId() != boardEntity.getUser().getId()) {
			throw new MyAsyncNotFoundException("해당글을 삭제할 권한이 없습니다.");
		}
		
		try {
			boardRepository.deleteById(id);
		} catch (Exception e) {
			throw new MyAsyncNotFoundException(id + "를 찾을 수 없어 삭제가 불가능합니다.");
		}
		
		return new CMRespDto<String>(1, "성공", null); // @ResponseBody 데이터 리턴!! String = text/plain
	}
}
