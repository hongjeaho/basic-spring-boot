package kr.go.kaptnet.common.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataAccessException;
import org.mybatis.spring.MyBatisSystemException;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리기.
 * 모든 컨트롤러에서 발생하는 예외를 일관되게 처리합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 모든 비즈니스 예외를 처리합니다.
     * BookNotFoundException, BoardNotFoundException 등 모든 하위 예외를 포함합니다.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<KapaApiErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(KapaApiErrorResponse.of(e.getErrorCode(), e.getMessage()));
    }

    /**
     * Validation 실패(@Valid 검증 오류)를 처리합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<KapaApiErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> details = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage())
                                .orElse("invalid")
                ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(KapaApiErrorResponse.of(
                        KapaErrorCode.VALIDATION_FAILED,
                        "입력값이 올바르지 않습니다.",
                        details
                ));
    }

    /**
     * DB 연결 실패, 쿼리 타임아웃 등 JDBC 레벨 오류를 처리합니다.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<KapaApiErrorResponse> handleDataAccessException(
            DataAccessException e,
            HttpServletRequest request) {
        log.error("DataAccessException at [{}]", request.getRequestURI(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(KapaApiErrorResponse.of(
                        KapaErrorCode.DATABASE_ERROR,
                        "서버 오류가 발생했습니다."
                ));
    }

    /**
     * MyBatis Mapper XML 문법 오류, resultType 매핑 실패 등을 처리합니다.
     */
    @ExceptionHandler(MyBatisSystemException.class)
    public ResponseEntity<KapaApiErrorResponse> handleMyBatisSystemException(
            MyBatisSystemException e,
            HttpServletRequest request) {
        log.error("MyBatisSystemException at [{}]", request.getRequestURI(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(KapaApiErrorResponse.of(
                        KapaErrorCode.DATABASE_ERROR,
                        "서버 오류가 발생했습니다."
                ));
    }

    /**
     * 그 외 처리하지 못한 예외를 처리합니다. 최후의 방어선입니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<KapaApiErrorResponse> handleException(
            Exception e,
            HttpServletRequest request) {
        log.error("Unhandled exception at [{}]", request.getRequestURI(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(KapaApiErrorResponse.of(
                        KapaErrorCode.INTERNAL_SERVER_ERROR,
                        "서버 오류가 발생했습니다."
                ));
    }
}
