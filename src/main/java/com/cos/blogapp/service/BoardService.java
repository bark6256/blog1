package com.cos.blogapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.comment.Comment;
import com.cos.blogapp.domain.comment.CommentRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotFoundException;
import com.cos.blogapp.web.dto.BoardSaveReqDto;
import com.cos.blogapp.web.dto.CommentSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardService {

	// 생성자 주입(DI)
	private final BoardRepository boardRepository;
	private final CommentRepository commentRepository;
	
	@Transactional
	public void 댓글작성(int boardId, CommentSaveReqDto dto, User principal) {
		Board boardEntity = boardRepository.findById(boardId)
				.orElseThrow(()-> new MyNotFoundException("해당 게시글을 찾을 수 없습니다."));

		Comment comment = new Comment();
		comment.setContent(dto.getContent());
		comment.setUser(principal);
		comment.setBoard(boardEntity);

		// 4. save 하기
		commentRepository.save(comment);
	}
	
	@Transactional
	public void 게시글수정(int id, User principal, BoardSaveReqDto dto) {
		
		// 권한
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(()-> new MyAsyncNotFoundException("해당 게시글을 찾을 수 없습니다."));
		if(principal.getId() != boardEntity.getUser().getId()) {
			throw new MyAsyncNotFoundException("해당 게시글의 주인이 아닙니다.");
		}
		
		// 영속화된 데이터를 변경하면!!
		boardEntity.setTitle(dto.getTitle());
		boardEntity.setContent(dto.getContent());
	} // 트랜잭션 종료 (더티체킹)
	
	public Board 게시글수정페이지이동(int id) {
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(()-> new MyNotFoundException(id + "번의 게시글을 찾을 수 없습니다."));
		return boardEntity;
	}
	
	@Transactional
	public void 게시글삭제(int id, User principal) {
		// 유효성
		Board boardEntity = boardRepository.findById(id)
			.orElseThrow(()-> new MyAsyncNotFoundException("해당글을 찾을 수 없습니다."));
		// 권한이 있는 사람만 함수 접근 가능(principal.id == {id})
		if(principal.getId() != boardEntity.getUser().getId()) {
			throw new MyAsyncNotFoundException("해당글을 삭제할 권한이 없습니다.");
		}
		
		// 핵심로직
		try {
			boardRepository.deleteById(id);
		} catch (Exception e) {
			throw new MyAsyncNotFoundException(id + "를 찾을 수 없어 삭제가 불가능합니다.");
		}
	}
	
	public Board 게시글상세보기(int id) {
		Board boardEntity = boardRepository.findById(id)
				.orElseThrow(() -> new MyNotFoundException(id + "번은 없는 게시글입니다") );
		return boardEntity;
	}
	
	@Transactional
	public void 게시글등록(BoardSaveReqDto dto, User principal) {
		boardRepository.save(dto.toEntity(principal));
	}
	
	public Page<Board> 게시글목록보기(int page) {
		PageRequest pageRequest = PageRequest.of(page, 3, Sort.by("id").descending());
		
		Page<Board> boardsEntity = boardRepository.findAll(pageRequest);
		return boardsEntity;
	}
}
