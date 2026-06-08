package kr.go.kaptnet.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.go.kaptnet.member.dto.MemberWithLoansDto;
import kr.go.kaptnet.member.mapper.MemberMapper;
import kr.go.kaptnet.common.success.KapaApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 REST API - collection 예시")
public class RestMemberController {

    private final MemberMapper memberMapper;

    @GetMapping("/{id}")
    @Operation(summary = "회원+대출기록 조회", description = "resultMap + collection 사용 - N+1 문제 해결 예시")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public KapaApiResponse<MemberWithLoansDto> getMemberWithLoans(
            @Parameter(description = "회원 ID") @PathVariable Long id) {
        MemberWithLoansDto member = memberMapper.findWithLoansById(id);
        return KapaApiResponse.of(member);
    }
}
