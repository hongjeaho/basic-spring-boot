package kr.go.kaptnet.loan.exception;

import kr.go.kaptnet.common.error.BusinessException;
import kr.go.kaptnet.common.error.KapaErrorCode;

/**
 * 대출기록을 찾을 수 없을 때 발생하는 예외.
 */
public class LoanNotFoundException extends BusinessException {
    public LoanNotFoundException(Long id) {
        super(KapaErrorCode.BUSINESS_ERROR, "대출기록을 찾을 수 없습니다: id=" + id);
    }

    public LoanNotFoundException(String message) {
        super(KapaErrorCode.BUSINESS_ERROR, message);
    }
}
