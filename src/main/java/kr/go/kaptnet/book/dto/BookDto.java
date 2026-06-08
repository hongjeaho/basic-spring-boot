package kr.go.kaptnet.book.dto;

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
@Schema(name = "BookDto", title = "도서 DTO")
public class BookDto {
    @Schema(description = "도서 ID")
    private Long id;

    @Schema(description = "도서 제목")
    private String title;

    @Schema(description = "저자")
    private String author;

    @Schema(description = "카테고리")
    private String category;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;
}
