package com.cos.blogapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.comment.Comment;
import com.cos.blogapp.domain.comment.CommentRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.ex.MyAsyncNotFoundException;
import com.cos.blogapp.handler.ex.MyNotFoundException;
import com.cos.blogapp.web.dto.CommentSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;
	
	@Transactional(rollbackFor = MyAsyncNotFoundException.class)
	public void 댓글삭제(int id, User principal) {
		Comment commentEntity = commentRepository.findById(id)
				.orElseThrow(() -> new MyAsyncNotFoundException("없는 댓글 번호입니다."));
		
		if(principal.getId()!= commentEntity.getUser().getId()) {
			throw new MyAsyncNotFoundException("삭제할 권한이 없습니다.");
		}
		commentRepository.deleteById(id);
	}
	
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
}
