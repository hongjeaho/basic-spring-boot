package kr.go.kaptnet.loan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.go.kaptnet.book.dto.BookDto;
import kr.go.kaptnet.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoanDetailDto", title = "대출상세 DTO - association 예시")
public class LoanDetailDto {
    @Schema(description = "대출 ID")
    private Long id;

    @Schema(description = "회원 ID")
    private Long memberId;

    @Schema(description = "도서 ID")
    private Long bookId;

    @Schema(description = "대출일")
    private LocalDate loanDate;

    @Schema(description = "반납일")
    private LocalDate returnDate;

    @Schema(description = "회원 정보 (association 1:1)")
    private MemberDto member;

    @Schema(description = "도서 정보 (association 1:1)")
    private BookDto book;
}
