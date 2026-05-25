package kr.go.kaptnet.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.go.kaptnet.board.dto.*;
import kr.go.kaptnet.board.exception.BoardNotFoundException;
import kr.go.kaptnet.board.service.BoardService;
import kr.go.kaptnet.common.error.KapaApiErrorResponse;
import kr.go.kaptnet.common.error.KapaErrorCode;
import kr.go.kaptnet.common.success.KapaApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "게시판 API", description = "게시판 REST API")
public class RestBoardController {

    private final BoardService boardService;

    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "모든 게시글을 최신순으로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public KapaApiResponse<List<BoardResponse>> getAllBoards() {
        List<BoardResponse> boards = boardService.getAllBoards();
        return KapaApiResponse.of(boards);
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    public KapaApiResponse<BoardResponse> getBoardById(@PathVariable Long id) {
        BoardResponse board = boardService.getBoardById(id);
        return KapaApiResponse.of(board);
    }

    @PostMapping
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @ApiResponse(responseCode = "200", description = "작성 성공")
    public KapaApiResponse<BoardResponse> createBoard(@Valid @RequestBody BoardCreateRequest request) {
        BoardResponse created = boardService.createBoard(request);
        return KapaApiResponse.of(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    public KapaApiResponse<BoardResponse> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody BoardUpdateRequest request) {
        BoardResponse updated = boardService.updateBoard(id, request);
        return KapaApiResponse.of(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    public KapaApiResponse<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return KapaApiResponse.of(null);
    }

    @ExceptionHandler(BoardNotFoundException.class)
    public KapaApiErrorResponse handleBoardNotFound(BoardNotFoundException ex) {
        return KapaApiErrorResponse.of(KapaErrorCode.BUSINESS_ERROR, ex.getMessage());
    }
}
