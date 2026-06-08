package kr.go.kaptnet.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.go.kaptnet.loan.dto.LoanDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "MemberWithLoansDto", title = "회원+대출기록 DTO - collection 예시")
public class MemberWithLoansDto {
    @Schema(description = "회원 ID")
    private Long id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "대출기록 목록 (collection 매핑)")
    private List<LoanDto> loans;
}
