package com.ssafy.coco.utility;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.ssafy.coco.api.dto.response.JwtTokenResponseDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenGenerator {

	// reference : https://gksdudrb922.tistory.com/217

	private Key uniqueKey;

	private static Integer validTime;

	public static final String PREFIX = "SSAFY8Project1A703";
	public static final String HEADER = "JWTTokenHeader";
	public static final String ISSUEDBY = "CoCo";

	@Autowired
	public JwtTokenGenerator(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") Integer validTime) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.uniqueKey = Keys.hmacShaKeyFor(keyBytes);
		this.validTime = validTime;
	}

	// 유저 정보를 토대로 AccessToken, RefreshToken을 생성하는 메서드
	public JwtTokenResponseDto createToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		long now = (new Date()).getTime();

		// AccessToken 생성
		Date accessTokenExpireTime = new Date(now + validTime);
		String accessToken = Jwts.builder()
			.setSubject(authentication.getName())
			.claim("auth", authorities)
			.setExpiration(accessTokenExpireTime)
			.signWith(uniqueKey, SignatureAlgorithm.HS256)
			.compact();

		// RefreshToken 생성
		String refreshToken = Jwts.builder()
			.setExpiration(new Date(now + validTime))
			.signWith(uniqueKey, SignatureAlgorithm.HS256)
			.compact();

		return JwtTokenResponseDto.builder()
			.grantType("Bearer")
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	// Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 추출하는 메서드
	public Authentication getAuthentication(String accessToken) {
		// 토큰 복호화
		Claims claims = parseClaims(accessToken);

		if (claims.get("auth") == null) {
			throw new RuntimeException("[권한 정보 없음] 유효하지 않은 토큰입니다.");
		}

		// 클레임에서 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get("auth").toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		// UserDetails 객체를 만들어 Authentication 리턴
		UserDetails principal = new User(claims.getSubject(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	// 토큰 정보를 검증하는 메서드
	public boolean verifyToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(uniqueKey).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.info("유효하지 않은 JWT 토큰 !! -> " + token);
		} catch (ExpiredJwtException e) {
			log.info("만료된 JWT 토큰 !! -> " + token);
		} catch (UnsupportedJwtException e) {
			log.info("지원하지 않는 형식의 JWT 토큰 !! -> " + token);
		} catch (IllegalArgumentException e) {
			log.info("JWT에서 빈 문자열을 반환하였습니다 !! -> " + token);
		}
		return false;
	}

	private Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(uniqueKey).build().parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

}
