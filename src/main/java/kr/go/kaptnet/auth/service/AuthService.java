package kr.go.kaptnet.auth.service;

import kr.go.kaptnet.auth.dto.AuthRequest;
import kr.go.kaptnet.auth.dto.AuthUser;
import kr.go.kaptnet.config.database.KapaTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@KapaTransactional
public class AuthService {

    private final AuthenticationManager authenticationManager;

    /**
     * 로그인 인증 여부를 검사하고 사용자 정보를 return 한다.
     *
     * @param authRequest 로그인 정보
     * @return 사용자 정보
     */
    public AuthUser login(AuthRequest authRequest) {
        final var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getId(),
                        authRequest.getPassword()));

        return (AuthUser) authentication.getPrincipal();
    }
}
