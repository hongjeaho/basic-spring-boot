package kr.go.kaptnet.board.exception;

import kr.go.kaptnet.common.error.BusinessException;
import kr.go.kaptnet.common.error.KapaErrorCode;

/**
 * 게시글을 찾을 수 없을 때 발생하는 예외.
 */
public class BoardNotFoundException extends BusinessException {

    public BoardNotFoundException(Long id) {
        super(KapaErrorCode.BUSINESS_ERROR, "게시글을 찾을 수 없습니다: id=" + id);
    }

    public BoardNotFoundException(String message) {
        super(KapaErrorCode.BUSINESS_ERROR, message);
    }
}
