package com.ssafy.coco.api.members.controller;

import java.net.http.HttpResponse;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.coco.api.members.dto.request.MemberLoginRequestDto;
import com.ssafy.coco.api.members.dto.request.MemberRatingUpdateRequestDto;
import com.ssafy.coco.api.members.dto.request.MemberRegisterRequestDto;
import com.ssafy.coco.api.members.dto.request.MemberUpdateRequestDto;
import com.ssafy.coco.api.members.dto.request.PasswordChangeRequestDto;
import com.ssafy.coco.api.members.dto.request.SendPasswordRequestDto;
import com.ssafy.coco.api.members.dto.response.MemberResponseDto;
import com.ssafy.coco.api.members.service.MemberService;
import com.ssafy.coco.api.tokens.dto.JwtTokenDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@Api(tags = "회원 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
@CrossOrigin("*")
public class MemberController {

	private final MemberService memberService;

	// CI/CD 정상 동작 테스트를 위한 메서드. 및 URI
	@ApiOperation(value = "Hello", notes = "CI/CD 정상 동작 테스트를 위한 API")
	@GetMapping("/hello")
	public String hello(){
		return "member";
	}

	@PostMapping("/register")
	@ApiOperation(value = "회원 가입", notes = "넘겨받은 회원정보를 바탕으로 회원을 DB에 등록한다.")
	public Long RegisterMember(
		@RequestBody @ApiParam(value = "회원가입 정보", required = true) MemberRegisterRequestDto requestDto) {
		return memberService.RegisterMember(requestDto);
	}

	@PutMapping("/info/{id}")
	@ApiOperation(value = "정보 변경", notes = "갱신된 사용자 정보를 {id}를 PK로 가지는 레코드에 적용한다.")
	public ResponseEntity UpdateMember(@PathVariable @ApiParam(value = "회원정보를 수정할 사용자의 {id}", required = true) String id,
		@RequestBody @ApiParam(value = "수정할 내용이 담긴 데이터 객체", required = true) MemberUpdateRequestDto requestDto, HttpServletRequest request) {
		System.out.println("[UpdateMember@MemberController] id: "+id+", requestDto: "+requestDto);
		String updatedUserId=memberService.UpdateInfo(id, requestDto, request.getHeader("Authorization"));
		if(updatedUserId==null)
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(id+" 사용자의 수정 권한이 없는 사용자입니다.");
		return ResponseEntity.ok(updatedUserId);
	}

	@GetMapping("/info/{id}")
	@ApiOperation(value = "정보 조회", notes = "{id}에 해당하는 사용자 정보를 DB에서 가져온다.")
	public ResponseEntity findById(
		@PathVariable @ApiParam(value = "회원정보를 조회할 사용자의 {id}", required = true) String id) {
		MemberResponseDto member=memberService.findByUserId(id);
		if(member.getDelFlag()!=null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("탈퇴한 사용자입니다.");
		else
			return ResponseEntity.ok(member);
	}

	@PostMapping("/delete/{id}")
	@ApiOperation(value = "회원 탈퇴", notes = "{id}의 사용자 정보에 탈퇴일(del_flag)을 기록한다.")
	public String DeleteMember(@PathVariable @ApiParam(value = "탈퇴할 회원 ID", required = true) String id, HttpServletRequest request) {
		return memberService.DeleteMember(id, request.getHeader("Authorization"));
	}

	@PutMapping("/rating")
	@ApiOperation(value = "평판 점수 변경", notes = "사용자의 평판점수를 변경한다.")
	public String RatingUpdate(
		@RequestBody @ApiParam(value = "평판점수 변경 요청 정보", required = true) MemberRatingUpdateRequestDto requestDto) {
		return memberService.RatingUpdate(requestDto);
	}

	@PostMapping("/extract")
	@ApiOperation(value = "Jwt 토큰 정보 추출", notes = "제공된 AccessToken으로부터 사용자 ID를 추출해 반환한다.")
	public ResponseEntity getUserIdFromJwtToken(HttpServletRequest request) {
		String userId = memberService.getUserIdFromAccessToken(request.getHeader("Authorization"), request.getHeader("refreshToken"));
		if(userId!=null)
			return ResponseEntity.ok(userId);
		else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("세션이 만료되었습니다. 다시 로그인하세요.");
	}

	@PostMapping("/tempPassword")
	@ApiOperation(value = "임시 비밀번호 발급 API", notes = "비밀번호를 재설정하려는 ID와 이메일을 받아 회원 본인인지 확인하고, 맞다면 8자 구성의 임시 비밀번호를 반환한다.")
	public String getTempPassword(
		@RequestBody @ApiParam(value = "임시 비밀번호 발급 요청 정보", required = true) SendPasswordRequestDto requestDto) {
		String userId = requestDto.getUserId();
		String userEmail = requestDto.getEmail();

		boolean isValidInformation = memberService.ExistUserByIdAndEmail(userId, userEmail);

		if (isValidInformation) {
			String tempPassword = memberService.getTmpPassword(userId);
			return userId + " 님의 임시 비밀번호는 [ " + tempPassword + " ] 입니다.";
		}
		else return "입력하신 정보에 일치하는 회원이 없습니다.";
	}

	@PostMapping("/login")
	@ApiOperation(value = "로그인", notes = "ID와 암호화된 PW가 DB에 있는 정보와 일치하는 경우 로그인을 승인한다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "정상 로그인"),
		@ApiResponse(code = 403, message = "아이디 또는 비밀번호 오류"),
		@ApiResponse(code = 500, message = "내부 서버 오류")
	})
	public ResponseEntity login(
		@RequestBody @ApiParam(value = "로그인 요청 정보", required = true) MemberLoginRequestDto requestDto,
		HttpServletResponse response) {
		String userId = requestDto.getUserId();
		String password = requestDto.getPassword();

		JwtTokenDto jwtToken = memberService.login(userId, password);

		System.out.println("로그인 - " + jwtToken);

		if (jwtToken != null) {

			response.setHeader("Authorization", "bearer " + jwtToken.getAccessToken());
			response.setHeader("refreshToken", "bearer " + jwtToken.getRefreshToken());

			return ResponseEntity.ok().body("로그인 성공");
		}

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ID 비밀번호를 다시 확인하세요.");
	}

	@PostMapping("/logout")
	@ApiOperation(value = "로그아웃", notes = "Http 헤더로부터 refreshToken을 추출하여 DB에서 삭제 한다.")
	public ResponseEntity logout(HttpServletRequest request) {
		String refreshToken = request.getHeader("refreshToken");
		if (refreshToken != null) {
			boolean isLogoutSuccessful = memberService.logout(refreshToken);
			if(isLogoutSuccessful)
				return ResponseEntity.ok().body("정상적으로 로그아웃되었습니다.");
			else return ResponseEntity.internalServerError().body("로그아웃 중 문제가 발생하였습니다. 유효하지 않은 토큰입니다.");
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("토큰 값이 유효하지 않습니다.");
	}

	@PostMapping("/changePassword")
	@ApiOperation(value="비밀번호 변경", notes = "Request Header의 AccessToken으로부터 사용자 ID를 추출하여 해당 사용자의 비밀번호를 변경한다.")
	public ResponseEntity changePassword(@RequestBody@ApiParam(value = "새로운 비밀번호", required = true) PasswordChangeRequestDto requestDto, HttpServletRequest request){
		String accessToken=request.getHeader("Authorization");
		String result=memberService.changePassword(accessToken, requestDto.getNewPassword());
		if(result.startsWith("[error] "))
			return ResponseEntity.badRequest().body(result.split(" ")[1]);
		else return ResponseEntity.ok(result);
	}

}
