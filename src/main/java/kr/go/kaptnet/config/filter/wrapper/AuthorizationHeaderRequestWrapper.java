package kr.go.kaptnet.config.filter.wrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * AuthorizationHeaderRequestWrapper 클래스는 HttpServletRequestWrapper를 상속받아
 * 인증 헤더 값을 동적으로 재정의할 수 있도록 확장된 기능을 제공합니다.
 * <br>
 * 주로 요청 객체의 Authorization 헤더가 비어 있거나 유효하지 않을 경우,
 * 지정된 대체 인증 값을 반환하도록 동작을 변경하는 데 사용됩니다.
 * <br>
 * 주요 기능:
 * 1. HTTP 요청 객체를 래핑하여 Authorization 헤더 값을 조건적으로 대체합니다.
 * 2. 헤더 이름이 Authorization일 경우, 기존 헤더 값이 비어 있다면
 *    생성자에서 전달받은 authorizationValue를 반환합니다.
 * 3. 그 외의 경우에는 원래 HttpServletRequest의 헤더 값을 반환합니다.
 * <br>
 * 사용 사례:
 * - API 요청 필터 체인에서 특정 시나리오에 따라 수정된 인증 헤더를 제공해야 할 때 활용됩니다.
 * - Swagger와 연관된 요청에 대해 인증 헤더를 삽입하거나 대체하는 로직에 사용될 수 있습니다.
 * <br>
 * 참고:
 * - HttpServletRequestWrapper를 기반으로 확장되었으므로 다른 기본 요청 기능에도 영향을 주지 않습니다.
 */
public class AuthorizationHeaderRequestWrapper extends HttpServletRequestWrapper {

	private final String authorizationValue;

	public AuthorizationHeaderRequestWrapper(
			final HttpServletRequest request,
			final String authorizationValue) {
		super(request);
		this.authorizationValue = authorizationValue;
	}

	@Override
	public String getHeader(final String name) {
		var superValue = super.getHeader(name);
		if (HttpHeaders.AUTHORIZATION.equals(name) && !StringUtils.hasText(superValue)) {
			return authorizationValue;
		}

		return superValue;
	}

}
