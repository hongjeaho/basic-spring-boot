package kr.go.kaptnet.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.go.kaptnet.config.filter.wrapper.AuthorizationHeaderRequestWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 로컬 환경에서 동작하는 요청 필터로, Swagger 요청에 대해 특정 인증 헤더를 추가합니다.
 * <br>
 * 이 클래스는 Spring Filter 체인의 일부로 동작하며, 요청이 필터를 통과하기 전에
 * Swagger 관련 요청인지 여부를 판단하고, 해당 요청일 경우 인증 헤더를 추가한
 * 새로운 HttpServletRequest 객체로 변환합니다.
 * <br>
 * 주요 동작:
 * 1. 요청 URI가 Swagger 관련 경로인지 판단합니다.
 * 2. 요청이 Swagger 경로이며 공용 URI가 아닌 경우 인증 헤더를 추가한
 *    AuthorizationHeaderRequestWrapper 객체를 생성하여 필터 체인에 전달합니다.
 * 3. 그 외의 경우, 원래의 HttpServletRequest 객체를 필터 체인에 전달합니다.
 * <br>
 * 사용 환경:
 * - 로컬 프로파일에서 활성화되며, "local" 프로파일이 설정된 경우에만 동작합니다.
 */
@Profile("local")
@Component("customHeaderFilter")
@RequiredArgsConstructor
public class LocalCustomHeaderFilter extends OncePerRequestFilter {
	private final String createSwaggerJwtToken;

	@Override
	protected void doFilterInternal(
			@NonNull final HttpServletRequest request,
			@NonNull final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException {
		filterChain.doFilter(getCustomAuthorizationRequest(request), response);
	}

	private HttpServletRequest getCustomAuthorizationRequest(HttpServletRequest request) {
		final boolean isPublicUri = new AntPathMatcher().match(
				"/api/public/**",
				request.getRequestURI()
		);

		return isSwagger(request) && !isPublicUri
				? new AuthorizationHeaderRequestWrapper(request, this.createSwaggerJwtToken)
				: request;
	}

	private boolean isSwagger(HttpServletRequest request) {
		final var referer = request.getHeader(HttpHeaders.REFERER);
		return referer != null && referer.contains("/public/swagger-ui/");
	}
}
