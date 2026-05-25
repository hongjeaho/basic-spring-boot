package kr.go.kaptnet.auth.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@Schema(name = "AuthUser", description = "인증 정보")
public class AuthUser implements UserDetails {

	@Schema(description = "사용자 일련번호")
	private Long seq;
	@Schema(description = "email")
	private String userEmail;
	@Schema(description = "id")
	private String userId;
	@Schema(description = "사용자 이름")
	private String name;
	@JsonIgnore
	private String password;

	@JsonIgnore
	@Schema(description = "로그인 성공여부")
	private boolean success;

	@Schema(description = "권한정보")
	@Builder.Default
	private Set<BasicAuthority> roles = Collections.emptySet();

	public AuthUser(Long seq, String userEmail, String userId, String userPassword, String userName) {
		this.seq = seq;
		this.userEmail = userEmail;
		this.userId = userId;
		this.name = userName;
		this.password = userPassword;
	}

	public AuthUser(AuthUser authUser, Set<BasicAuthority> roles) {
		this.seq = authUser.getSeq();
		this.userEmail = authUser.getUserEmail();
		this.name = authUser.getName();
		this.userId = authUser.getUserId();
		this.password = authUser.getPassword();
		this.roles = roles;
	}

	@JsonIgnore
	@Override
	public String getUsername() {
		return userId;
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (roles == null || roles.isEmpty()) {
			return List.of();
		}

		return roles.stream()
				.map(userRole -> new SimpleGrantedAuthority(userRole.getAuthority()))
				.collect(Collectors.toList());
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}
}
