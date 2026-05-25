package kr.go.kaptnet.common.error;

/**
 * 애플리케이션 전반에서 발생할 수 있는 다양한 에러 상태를 정의한 열거형 클래스.
 * <br>
 * 각 에러 코드는 특정 예외 상황을 나타내며, 이를 통해 명확하고 일관된 에러 응답을 제공할 수 있다.
 * <br>
 * - VALIDATION_FAILED: 입력 데이터 검증 실패를 나타냄.
 * - INTERNAL_SERVER_ERROR: 서버 내부 처리 중 발생한 알 수 없는 오류를 나타냄.
 * - DATABASE_ERROR: 데이터베이스와 관련된 예외 상황을 나타냄.
 * - BUSINESS_ERROR: 비즈니스 로직 처리 중 발생한 예외를 나타냄.
 * - AUTH_REQUIRED: 인증이 필요하지만 인증되지 않은 요청을 나타냄.
 * - FORBIDDEN: 권한이 없는 사용자의 접근 시도를 나타냄.
 */
public enum ErrorCode {
	VALIDATION_FAILED,
	INTERNAL_SERVER_ERROR,
	DATABASE_ERROR,
	BUSINESS_ERROR,
	AUTH_REQUIRED,
	FORBIDDEN
}

