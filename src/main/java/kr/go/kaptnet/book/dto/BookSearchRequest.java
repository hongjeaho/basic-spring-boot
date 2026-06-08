package kr.go.kaptnet.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "BookSearchRequest", title = "도서 고급 검색 요청 (CDATA 예시)")
public class BookSearchRequest {
    @Schema(description = "도서 제목 (선택 사항, XML 특수 문자: C++, AT&T, <tag> 등)")
    private String title;

    @Schema(description = "저자 (선택 사항)")
    private String author;

    @Schema(description = "카테고리 (선택 사항)")
    private String category;

    @Schema(description = "최소 ID (이상, >= 연산자 사용)")
    private Long minId;

    @Schema(description = "최대 ID (이하, <= 연산자 사용)")
    private Long maxId;
}
