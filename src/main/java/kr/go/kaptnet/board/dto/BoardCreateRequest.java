package kr.go.kaptnet.board.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "BoardCreateRequest", description = "게시글 생성 요청")
public class BoardCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이내로 작성해주세요.")
    @Schema(description = "제목", example = "Spring Boot 교육 시작!")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Schema(description = "내용", example = "오늘부터 Spring Boot 교육을 시작합니다.")
    private String content;

    @NotBlank(message = "작성자는 필수입니다.")
    @Size(max = 50, message = "작성자명은 50자 이내로 작성해주세요.")
    @Schema(description = "작성자", example = "홍재호")
    private String author;
}
