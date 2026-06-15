package kr.go.kaptnet.common.error;

import lombok.Getter;

/**
 * 최상위 비즈니스 예외 클래스.
 * 모든 도메인별 비즈니스 예외의 기반이 됩니다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final KapaErrorCode errorCode;

    /**
     * 비즈니스 예외를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param message   에러 메시지
     */
    public BusinessException(KapaErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
