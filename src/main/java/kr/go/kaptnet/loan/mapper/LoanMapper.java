package kr.go.kaptnet.loan.mapper;

import kr.go.kaptnet.loan.dto.LoanDetailDto;
import kr.go.kaptnet.loan.dto.LoanDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoanMapper {
    // resultMap + association 예시 (1:1 관계 두 개)
    LoanDetailDto findById(Long id);

    // 동적 SQL 예시 - <foreach> (IN절 일괄 조회)
    List<LoanDto> findByMemberIds(List<Long> ids);
}
