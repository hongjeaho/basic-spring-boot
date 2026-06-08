package kr.go.kaptnet.member.mapper;

import kr.go.kaptnet.member.dto.MemberWithLoansDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    // resultMap + collection 예시 - N+1 문제 해결 (JOIN)
    MemberWithLoansDto findWithLoansById(Long id);
}
