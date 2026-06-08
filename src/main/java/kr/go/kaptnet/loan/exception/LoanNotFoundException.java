package kr.go.kaptnet.loan.exception;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(Long id) {
        super("대출기록을 찾을 수 없습니다: id=" + id);
    }

    public LoanNotFoundException(String message) {
        super(message);
    }
}
