package com.cos.blogapp.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.BoardSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BoardController {
	
	private final BoardRepository boardRepository;
	private final HttpSession session;
	
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
		// 1. orElse 는 값을 찾으면 Board가 리턴, 못찾으면 (괄호안의 내용 리턴)
		//   default값이 필요하다면 orElse를 쓴다.
//		Board boardEntity = boardRepository.findById(id)
//				.orElse(new Board());
		
		// 2. orElseThrow 익셉션 발생시 이 함수를 호출한곳으로 오류 메시지를 던져준다.
		//     컨트롤러를 호출한 곳 - 디스페쳐 서블릿이 받는다.
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow();
		model.addAttribute("boardEntity", boardEntity);
		return "board/detail";
	}
}