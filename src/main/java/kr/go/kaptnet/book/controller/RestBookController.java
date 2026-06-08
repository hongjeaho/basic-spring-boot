package kr.go.kaptnet.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.go.kaptnet.book.dto.BookDto;
import kr.go.kaptnet.book.dto.BookSearchCondition;
import kr.go.kaptnet.book.exception.BookNotFoundException;
import kr.go.kaptnet.book.service.BookService;
import kr.go.kaptnet.common.success.KapaApiResponse;
import kr.go.kaptnet.common.error.KapaApiErrorResponse;
import kr.go.kaptnet.common.error.KapaErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "도서 API", description = "도서 REST API - MyBatis 핵심 기능 예시")
public class RestBookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "전체 도서 목록 조회", description = "resultType 사용 - 단순 1:1 매핑 예시")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookDto.class))))
    public KapaApiResponse<List<BookDto>> getAllBooks() {
        List<BookDto> books = bookService.getAllBooks();
        return KapaApiResponse.of(books);
    }

    @GetMapping("/{id}")
    @Operation(summary = "도서 상세 조회", description = "resultType 사용 - 단건 조회 예시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class)))
    })
    public KapaApiResponse<BookDto> getBookById(
            @Parameter(description = "도서 ID") @PathVariable Long id) {
        BookDto book = bookService.getBookById(id);
        return KapaApiResponse.of(book);
    }

    @GetMapping("/search")
    @Operation(summary = "도서 검색", description = "동적 SQL 사용 - <if>, <where> 예시")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookDto.class))))
    public KapaApiResponse<List<BookDto>> searchBooks(
            @Parameter(description = "검색 조건") BookSearchCondition condition) {
        List<BookDto> books = bookService.searchBooks(condition);
        return KapaApiResponse.of(books);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public KapaApiErrorResponse handleBookNotFound(BookNotFoundException ex) {
        return KapaApiErrorResponse.of(KapaErrorCode.BUSINESS_ERROR, ex.getMessage());
    }
}
