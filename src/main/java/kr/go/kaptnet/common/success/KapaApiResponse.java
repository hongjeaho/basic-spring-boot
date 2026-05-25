package kr.go.kaptnet.common.success;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "ApiResponse", title = "성공 응답")
public class ApiResponse<T> {
	private boolean success; // 항상 true
	private T data;
	private Object meta; // 필요 시 페이지 정보 등

	public static <T> ApiResponse<T> of(T data) {
		return ApiResponse.<T>builder().success(true).data(data).build();
	}

	public static <T> ApiResponse<T> of(T data, Object meta) {
		return ApiResponse.<T>builder().success(true).data(data).meta(meta).build();
	}
}
