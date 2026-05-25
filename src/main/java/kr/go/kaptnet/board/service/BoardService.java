package kr.go.kaptnet.board.service;

import kr.go.kaptnet.board.dto.*;
import kr.go.kaptnet.board.exception.BoardNotFoundException;
import kr.go.kaptnet.board.mapper.BoardMapper;
import kr.go.kaptnet.config.database.KapaTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;

    // REST API methods - return BoardResponse
    public List<BoardResponse> getAllBoards() {
        return boardMapper.findAll().stream()
                .map(BoardResponse::from)
                .collect(Collectors.toList());
    }

    public BoardResponse getBoardById(Long id) {
        BoardDto board = boardMapper.findById(id);
        if (board == null) {
            throw new BoardNotFoundException(id);
        }
        return BoardResponse.from(board);
    }

    @KapaTransactional
    public BoardResponse createBoard(BoardCreateRequest request) {
        BoardDto dto = BoardDto.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(request.getAuthor())
                .build();

        boardMapper.insert(dto);
        return BoardResponse.from(dto);
    }

    @KapaTransactional
    public BoardResponse updateBoard(Long id, BoardUpdateRequest request) {
        BoardDto existing = boardMapper.findById(id);
        if (existing == null) {
            throw new BoardNotFoundException(id);
        }

        BoardDto dto = BoardDto.builder()
                .id(id)
                .title(request.getTitle())
                .content(request.getContent())
                .author(existing.getAuthor())
                .build();

        boardMapper.update(dto);
        return BoardResponse.from(boardMapper.findById(id));
    }

    @KapaTransactional
    public void deleteBoard(Long id) {
        BoardDto existing = boardMapper.findById(id);
        if (existing == null) {
            throw new BoardNotFoundException(id);
        }
        boardMapper.deleteById(id);
    }

    // Thymeleaf controller methods - return BoardDto directly
    public List<BoardDto> getAllBoardDtos() {
        return boardMapper.findAll();
    }

    public BoardDto getBoardDtoById(Long id) {
        BoardDto board = boardMapper.findById(id);
        if (board == null) {
            throw new BoardNotFoundException(id);
        }
        return board;
    }

    @KapaTransactional
    public BoardDto createBoardDto(BoardDto board) {
        boardMapper.insert(board);
        return board;
    }

    @KapaTransactional
    public BoardDto updateBoardDto(BoardDto board) {
        if (boardMapper.findById(board.getId()) == null) {
            throw new BoardNotFoundException(board.getId());
        }
        boardMapper.update(board);
        return board;
    }
}
