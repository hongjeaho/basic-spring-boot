package kr.go.kaptnet.loan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoanDto", title = "대출기록 DTO")
public class LoanDto {
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
}
