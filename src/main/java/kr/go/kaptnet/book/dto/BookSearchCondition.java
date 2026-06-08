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
@Schema(name = "BookSearchCondition", title = "도서 검색 조건")
public class BookSearchCondition {
    @Schema(description = "도서 제목 (부분 검색)")
    private String title;

    @Schema(description = "저자 (부분 검색)")
    private String author;

    @Schema(description = "카테고리")
    private String category;
}
