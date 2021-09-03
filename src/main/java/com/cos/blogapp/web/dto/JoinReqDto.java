package com.cos.blogapp.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class JoinReqDto {
	private String username;
	private String password;
	private String email;

}
