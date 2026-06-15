package kr.go.kaptnet.book.exception;

import kr.go.kaptnet.common.error.BusinessException;
import kr.go.kaptnet.common.error.KapaErrorCode;

/**
 * 도서를 찾을 수 없을 때 발생하는 예외.
 */
public class BookNotFoundException extends BusinessException {
    public BookNotFoundException(Long id) {
        super(KapaErrorCode.BUSINESS_ERROR, "도서를 찾을 수 없습니다: id=" + id);
    }

    public BookNotFoundException(String message) {
        super(KapaErrorCode.BUSINESS_ERROR, message);
    }
}
