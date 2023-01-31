package com.ssafy.coco.api.members.dto.request;

import java.util.Collections;

import com.ssafy.coco.api.members.data.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberRegisterRequestDto {
	private String userId;
	private String password;
	private String name;
	private String email;

	@Builder
	public MemberRegisterRequestDto(String userId, String password, String name, String email) {
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.email = email;
	}

	public Member toEntity() {
		return Member.builder()
			.userId(userId)
			.password(password)
			.name(name)
			.email(email)
			.roles(Collections.singletonList("ROLE_USER"))
			.build();
	}
}