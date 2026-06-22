package kr.go.kaptnet.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.go.kaptnet.auth.dto.AuthRequest;
import kr.go.kaptnet.auth.dto.AuthUser;
import kr.go.kaptnet.auth.service.AuthService;
import kr.go.kaptnet.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Public Authority API", description = "인증에 처리 API")
@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
public class RestPublicAuthController {
    @Value("${jwt.expiration.period:86400000}") // 기본 하루 (1 * 24 * 60 * 60 * 1000)
    private long jwtExpirationPeriod;

    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "로그인 처리", description = "로그인 처리 후 인증된 정보를 내려준다.")
    @PostMapping("/login")
    ResponseEntity<AuthUser> login(@RequestBody AuthRequest authRequest) {
        final var user = authService.login(authRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.makeAuthToken(user, jwtExpirationPeriod))
                .body(user);
    }
}