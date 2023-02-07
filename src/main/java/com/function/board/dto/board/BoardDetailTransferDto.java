package com.function.board.dto.board;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.function.board.domain.board.Board;
import com.function.board.domain.comment.Comment;
import com.function.board.dto.comment.CommentResponseDto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardDetailTransferDto {

	private long id;
	private String title;
	private List<ContentComponentDto> content;
	private String writer;
	private int hit;
	private List<ContentComponentDto> code;
	private Page<CommentResponseDto> comments;
	private LocalDateTime createdAt;

	@Builder
	public BoardDetailTransferDto(Board entity, BoardDetailTransferDto dto, Page<Comment> comments) {
		this.id = entity.getId();
		this.title = entity.getTitle();
		this.content = dto.getContent();
		this.writer = entity.getWriter();
		this.hit = entity.getHit();
		this.code = dto.getCode();
		this.createdAt = entity.getCreatedAt();
		this.comments = comments.map(CommentResponseDto::new);
	}

}
