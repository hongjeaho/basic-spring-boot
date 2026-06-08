package kr.go.kaptnet.member.service;

import kr.go.kaptnet.member.dto.MemberWithLoansDto;
import kr.go.kaptnet.member.exception.MemberNotFoundException;
import kr.go.kaptnet.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;

    public MemberWithLoansDto getMemberWithLoans(Long id) {
        MemberWithLoansDto member = memberMapper.findWithLoansById(id);
        if (member == null || member.getId() == null) {
            throw new MemberNotFoundException(id);
        }
        return member;
    }
}
