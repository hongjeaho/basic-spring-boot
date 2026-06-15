package kr.go.kaptnet.member.exception;

import kr.go.kaptnet.common.error.BusinessException;
import kr.go.kaptnet.common.error.KapaErrorCode;

/**
 * 회원을 찾을 수 없을 때 발생하는 예외.
 */
public class MemberNotFoundException extends BusinessException {
    public MemberNotFoundException(Long id) {
        super(KapaErrorCode.BUSINESS_ERROR, "회원을 찾을 수 없습니다: id=" + id);
    }

    public MemberNotFoundException(String message) {
        super(KapaErrorCode.BUSINESS_ERROR, message);
    }
}
