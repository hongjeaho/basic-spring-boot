package kr.go.kaptnet.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AuthRequest", title = "로그인 요청 정보")
public class AuthRequest {
    @Schema(title = "id", example = "admin")
    private String id;
    @Schema(title = "password", example = "12345")
    private String password;
}