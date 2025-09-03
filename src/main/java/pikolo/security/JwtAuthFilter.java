package pikolo.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import pikolo.user.CustomUserDetails;
import pikolo.user.UserDTO;
import pikolo.user.UserService;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String accessToken = resolveAccessToken(request);

        // 1) AT가 아예 없는 경우에도 RT가 있으면 즉시 재발급 시도
        if (!StringUtils.hasText(accessToken)) {
            if (tryRefreshAndContinue(request, response, chain)) return;
            chain.doFilter(request, response);
            return;
        }

        try {
            // 2) AT 정상 → 인증 세팅 후 진행
            Jws<Claims> jws = jwtUtil.validateToken(accessToken);
            Claims claims = jws.getBody();

            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            if (role == null) role = "USER";

            setAuthentication(userId, role);
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // 3) AT 만료 → RT로 재발급 시도
            if (tryRefreshAndContinue(request, response, chain)) return;

            SecurityContextHolder.clearContext();
            unauthorized(response, "ACCESS_TOKEN_EXPIRED");
        } catch (JwtException e) {
        	
            SecurityContextHolder.clearContext();
            unauthorized(response, "INVALID_TOKEN");
        }
    }

    /** RT로 재발급 시도 → 성공 시 인증 세팅 + 체인 진행, 실패 시 401 반환 */
    private boolean tryRefreshAndContinue(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String refreshToken = getCookie(request, "REFRESH_TOKEN");
        if (!StringUtils.hasText(refreshToken)) {
            return false; // RT 없음 → 재발급 불가
        }

        try {
        	LocalDateTime now = LocalDateTime.now();

            // 보기 좋게 포맷팅
            String formatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            
        	
            Jws<Claims> rJws = jwtUtil.validateToken(refreshToken);
            String userID = rJws.getBody().getSubject();
            
            System.out.println("처음 발급된 refresh 검증 "+ formatted);
            
            UserDTO user = userService.searchInfoByUserId(userID);
            if (user == null) {
                unauthorized(response, "USER_NOT_FOUND");
                return true;
            }

            String role = StringUtils.hasText(user.getUserRole()) ? user.getUserRole() : "USER";

            // 새 토큰 발급 (RT 로테이션 권장)
            String newAT = jwtUtil.generateAccessToken(userID, role);
            String newRT = jwtUtil.generateRefreshToken(userID);
            
            System.out.println("15분 지났음 access 재발급" + formatted);
            // 쿠키 세팅: 운영에선 Secure/SameSite 조정 권장
            // AT 쿠키 Max-Age는 토큰 만료보다 60초 길게 주는 것도 옵션
            addHttpOnlyCookie(response, "ACCESS_TOKEN", newAT, 15 * 60 /* + 60 */);
            addHttpOnlyCookie(response, "REFRESH_TOKEN", newRT, 7 * 24 * 3600);

            setAuthentication(userID, role);
            chain.doFilter(request, response);
            return true;
        } catch (JwtException je) {
            SecurityContextHolder.clearContext();
            unauthorized(response, "REFRESH_TOKEN_INVALID_OR_EXPIRED");
            return true;
        }
    }

    private void setAuthentication(String userId, String role) {
        CustomUserDetails userDetail = new CustomUserDetails(userId, role);
        var auth = new UsernamePasswordAuthenticationToken(
                userDetail, null, userDetail.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(auth) && auth.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return auth.substring(7).trim();
        }
        return getCookie(request, "ACCESS_TOKEN");
    }

    private String getCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;
        for (var c : req.getCookies()) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private void addHttpOnlyCookie(HttpServletResponse res, String name, String value, int maxAgeSeconds) {
        Cookie c = new Cookie(name, value);
        c.setHttpOnly(true);
        c.setPath("/");
        c.setMaxAge(maxAgeSeconds);
        // 운영 HTTPS라면:
        // c.setSecure(true);
        // SameSite=None 등이 필요하면 ResponseCookie 사용 권장
        res.addCookie(c);
    }

    private void unauthorized(HttpServletResponse res, String code) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"status\":\"fail\",\"code\":\"" + code + "\"}");
    }
}
