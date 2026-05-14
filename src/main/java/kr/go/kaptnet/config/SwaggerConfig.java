package kr.go.kaptnet.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.util.Set;
import kr.go.kaptnet.authority.AuthUser;
import kr.go.kaptnet.authority.BasicAuthority;
import kr.go.kaptnet.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local")
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

	private final JwtTokenUtil jwtTokenUtil;

	@Bean
	OpenAPI openAPI() {
		return new OpenAPI()
				.components(new Components())
				.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
				.title("service-kapanet-web")
				.description("kapanet API")
				.version("1.0");
	}

	@Bean
	public String createSwaggerJwtToken() {
		AuthUser authUser = new AuthUser();
		authUser.setSeq(1L);
		authUser.setUserId("admin");
		authUser.setRoles(Set.of(
				BasicAuthority.builder().role("ADMIN").build(),
				BasicAuthority.builder().role("USER").build()
		));
		return "Bearer " + jwtTokenUtil.makeAuthToken(authUser);
	}
}
