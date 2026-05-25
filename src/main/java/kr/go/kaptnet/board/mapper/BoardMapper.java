package kr.go.kaptnet.board.mapper;

import kr.go.kaptnet.board.dto.BoardDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<BoardDto> findAll();
    BoardDto findById(Long id);
    int insert(BoardDto board);
    int update(BoardDto board);
    int deleteById(Long id);
}
