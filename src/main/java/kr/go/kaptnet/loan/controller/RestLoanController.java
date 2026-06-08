package kr.go.kaptnet.loan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.go.kaptnet.loan.dto.LoanDetailDto;
import kr.go.kaptnet.loan.dto.LoanDto;
import kr.go.kaptnet.loan.exception.LoanNotFoundException;
import kr.go.kaptnet.loan.service.LoanService;
import kr.go.kaptnet.common.success.KapaApiResponse;
import kr.go.kaptnet.common.error.KapaApiErrorResponse;
import kr.go.kaptnet.common.error.KapaErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "대출기록 API", description = "대출기록 REST API - association, foreach 예시")
public class RestLoanController {

    private final LoanService loanService;

    @GetMapping("/{id}")
    @Operation(summary = "대출상세 조회", description = "resultMap + association 사용 (1:1 관계 두 개)")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanDetailDto.class)))
    public KapaApiResponse<LoanDetailDto> getLoanDetail(
            @Parameter(description = "대출 ID") @PathVariable Long id) {
        LoanDetailDto loan = loanService.getLoanDetail(id);
        return KapaApiResponse.of(loan);
    }

    @GetMapping("/by-member-ids")
    @Operation(summary = "회원들 대출기록 일괄 조회", description = "<foreach> 사용 - IN절 일괄 조회 예시")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LoanDto.class))))
    public KapaApiResponse<List<LoanDto>> getLoansByMemberIds(
            @Parameter(description = "회원 ID 리스트") @RequestParam List<Long> ids) {
        List<LoanDto> loans = loanService.getLoansByMemberIds(ids);
        return KapaApiResponse.of(loans);
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public KapaApiErrorResponse handleLoanNotFound(LoanNotFoundException ex) {
        return KapaApiErrorResponse.of(KapaErrorCode.BUSINESS_ERROR, ex.getMessage());
    }
}
