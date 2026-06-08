package kr.go.kaptnet.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "MemberDto", title = "회원 DTO")
public class MemberDto {
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
}
