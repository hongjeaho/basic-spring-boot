package kr.go.kaptnet.common.error;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ApiErrorResponse", title = "에러 응답")
public class KapaApiErrorResponse {

	private boolean success = false;
	private ErrorBody error;
	private LocalDateTime timestamp;

	public static KapaApiErrorResponse of(KapaErrorCode code, String message) {
		return of(code, message, null);
	}

	public static KapaApiErrorResponse of(KapaErrorCode code, String message, Map<String, String> errors) {
		return KapaApiErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.error(ErrorBody.builder()
						.code(code.name())
						.message(message)
						.details(Optional.ofNullable(errors).orElse(Collections.emptyMap()))
						.build())
				.build();
	}

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ErrorBody {

		private String code;
		private String message;
		private  Map<String, String> details;
	}
}
