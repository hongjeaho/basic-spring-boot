package kr.go.kaptnet.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.Map;
import kr.go.kaptnet.authority.AuthUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtTokenUtil {

	private static final String JWT_ISSUER = "platform.go.kr";

	private final Algorithm algorithm;
	private final ObjectMapper objectMapper;

	public JwtTokenUtil(
			@Value("${jwt.secret}") String secret,
			ObjectMapper objectMapper
	) {
		this.algorithm = Algorithm.HMAC256(secret);
		this.objectMapper = objectMapper;
	}

	// 토큰 생성
	public String makeAuthToken(final AuthUser user) {
		long jwtExpirationPeriod = 24 * 60 * 60 * 1000; // 하루
		return makeAuthToken(user, jwtExpirationPeriod);
	}

	// 토큰을 생성한다.
	public String makeAuthToken(final AuthUser user, long jwtExpirationPeriod) {
		var currentTimeMillis = System.currentTimeMillis();
		Map<String, Object> userMap = objectMapper.convertValue(
				user, new com.fasterxml.jackson.core.type.TypeReference<>() {
				}
		);

		return JWT.create()
				.withIssuer(JWT_ISSUER)
				.withSubject(user.getUsername())
				.withIssuedAt(new Date(currentTimeMillis))
				.withExpiresAt(new Date(currentTimeMillis + jwtExpirationPeriod))
				.withClaim("user", userMap)
				.sign(algorithm);
	}

	// 토큰 정보를 조회 한다.
	public AuthUser verify(final String token) {
		try {
			DecodedJWT verify = JWT.require(algorithm).build().verify(token);
			Map<String, Object> userMap = verify.getClaim("user").asMap();
			var user = objectMapper.convertValue(userMap, AuthUser.class);
			user.setSuccess(true);
			return user;
		} catch (JWTVerificationException ex) {
			return AuthUser.builder().success(false).build();
		}
	}

	// 만료 시간을 조회 한다.
	public Date getExpirationDate(String token) {
		return JWT.require(algorithm).build().verify(token).getExpiresAt();
	}
}
