package kr.go.kaptnet.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Profile("!local")
@Component("customHeaderFilter")
@RequiredArgsConstructor
public class CustomHeaderFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
			@NonNull final HttpServletRequest request,
			@NonNull final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException {
		filterChain.doFilter(request, response);
	}
}