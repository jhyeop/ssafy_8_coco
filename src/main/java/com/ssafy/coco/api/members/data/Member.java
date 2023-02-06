package com.ssafy.coco.api.members.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
public class Member implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(length = 32, nullable = false)
	private String userId;
	@Column(length = 255, nullable = false)
	private String password;
	@Column(length = 16, nullable = false)
	private String name;
	@Column(length = 64, nullable = false)
	private String email;
	@ElementCollection(fetch = FetchType.EAGER)
	@Builder.Default
	private List<String> roles = new ArrayList<>();
	@ColumnDefault("0")
	private Integer rating;
	@CreationTimestamp
	private LocalDateTime regTime;

	private LocalDateTime delFlag;

	@Builder
	public Member(String userId, String password, String name, String email) {
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.email = email;
	}

	public void UpdateInfo(String password, String name, String email) {
		this.password = password;
		this.name = name;
		this.email = email;
	}

	public void updateRating(Integer amount) {
		this.rating += amount;
		if (this.rating > 10000) {
			this.rating = 10000;
		} else if (this.rating < 0) {
			this.rating = 0;
		}
	}

	public void deleteMember(LocalDateTime time) {
		this.delFlag = time;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getUsername() {
		return userId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}