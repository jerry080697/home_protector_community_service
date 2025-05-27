package hp.home_protector.domain.community.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secretKeyBase64;

    private Key getSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 로그인/회원가입 엔드포인트, OPTIONS 프리플라이트 등 제외
        String path = request.getRequestURI();
        return HttpMethod.OPTIONS.matches(request.getMethod())
                || path.startsWith("/auth/")
                ;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // ① 헤더 확인
        String token = resolveToken(request);
        System.out.println("▶ JwtAuthFilter: token=" + token);

        if (token != null) {
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // ② 토큰 잘 파싱됐는지 확인
                String userId = claims.getSubject();
                System.out.println("▶ JwtAuthFilter: userId=" + userId);

                request.setAttribute("X-User-Id", userId);
                boolean isAdmin = "ADMIN".equalsIgnoreCase(claims.get("role", String.class));
                request.setAttribute("X-Is-Admin", isAdmin);
                System.out.println("▶ JwtAuthFilter: role(유저면 fasle, admin이면 true)=" + isAdmin);

            } catch (JwtException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter()
                        .write("{\"error\":\"Invalid or expired JWT token\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
