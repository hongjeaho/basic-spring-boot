package kr.go.kaptnet.book.mapper;

import kr.go.kaptnet.book.dto.BookDto;
import kr.go.kaptnet.book.dto.BookSearchCondition;
import kr.go.kaptnet.book.dto.BookSearchRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookMapper {
    // resultType 예시 - 단순 조회
    List<BookDto> findAll();

    // resultType 예시 - 단건 조회
    BookDto findById(Long id);

    // 동적 SQL 예시 - <if>, <where> 사용
    List<BookDto> search(BookSearchCondition condition);

    // CDATA 예시 - XML 특수 문자 처리
    List<BookDto> advancedSearch(BookSearchRequest request);
}
