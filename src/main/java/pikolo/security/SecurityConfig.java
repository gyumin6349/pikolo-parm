package pikolo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import pikolo.user.UserService;

/**
 * 환경설정 용 클래스 웹보안 활성화
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtUtil jwtUtil;
	private final UserService userService;
	
	   @Bean
	    public JwtAuthFilter jwtAuthFilter() {
	        // ★ JwtAuthFilter(JwtUtil, UserService) 생성자와 맞춰서 주입
	        return new JwtAuthFilter(jwtUtil, userService);
	    }

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// 1) CSRF 사용 안 함: API/JWT는 보통 CSRF 미사용
				.csrf(csrf -> csrf.disable())

				// 2) 세션 완전 끔: JWT 기반이므로 상태 유지하지 않음
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// 3) URL 접근 규칙: 홈/로그인/회원가입/커뮤니티/채팅 목록은 공개
				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/", "/login/**", "/user/**", "/community", "/chat/rooms", "/api/pay/kakao/**")
								.permitAll().requestMatchers(
								        "/favicon.ico",
								        "/css/**", "/js/**", "/images/**", "/webjars/**"
								    ).permitAll().anyRequest().authenticated() // 그 외는 인증 필요(= JWT 필요)
				)

				// 4) 인증 실패/권한 부족 전역 처리(401/403 JSON)
				.exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint())
						.accessDeniedHandler(accessDeniedHandler()))

				// 5) 스프링 로그인 폼(기본 UI) 끔: 우리는 직접 만든 로그인/토큰 발급을 사용
				.formLogin(form -> form.disable())

				// 6) 커스텀 JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 배치
				.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() { // 인증 필요 자원에 미인증 접근 시
		return (req, res, ex) -> {
			res.setStatus(401);
			res.setContentType("application/json;charset=UTF-8");
			res.getWriter().write("{\"status\":\"fail\",\"code\":\"UNAUTHORIZED\"}");
		};
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() { // 인증은 되었지만 권한(Role) 부족
		return (req, res, ex) -> {
			res.setStatus(403);
			res.setContentType("application/json;charset=UTF-8");
			res.getWriter().write("{\"status\":\"fail\",\"code\":\"FORBIDDEN\"}");
		};
	}
}

//	@Bean
//	public SecurityFilterChain filterCharin(HttpSecurity http)throws Exception{
//		//필터체인설정 : 로그인 창을 제공하지 않도록 설정'
//		//모든 요청에대한 인증없이 사용가능하도록 설정.
//		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//		//csrf : 접속자의 연결 정보(세션)를 가로채서 접속 
//		.csrf(csrf -> csrf.disable()).
//		//login box 사용하지 않을때
//		formLogin(login -> login.disable());
//		
//		
//		return http.build();
//	}
