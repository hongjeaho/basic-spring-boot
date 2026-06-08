package kr.go.kaptnet.loan.service;

import kr.go.kaptnet.loan.dto.LoanDetailDto;
import kr.go.kaptnet.loan.dto.LoanDto;
import kr.go.kaptnet.loan.exception.LoanNotFoundException;
import kr.go.kaptnet.loan.mapper.LoanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanMapper loanMapper;

    public LoanDetailDto getLoanDetail(Long id) {
        LoanDetailDto loan = loanMapper.findById(id);
        if (loan == null || loan.getId() == null) {
            throw new LoanNotFoundException(id);
        }
        return loan;
    }

    public List<LoanDto> getLoansByMemberIds(List<Long> memberIds) {
        return loanMapper.findByMemberIds(memberIds);
    }
}
