//package hp.home_protector.domain.community.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpMethod;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.security.Key;
//import java.util.Base64;
//
//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    @Value("${jwt.secret}")
//    private String secretKeyBase64;
//
//    private Key getSigningKey() {
//        byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
//        return Keys.hmacShaKeyFor(decodedKey);
//    }
//
//    private String resolveToken(HttpServletRequest request) {
//        String bearer = request.getHeader("Authorization");
//        if (bearer != null && bearer.startsWith("Bearer ")) {
//            return bearer.substring(7);
//        }
//        return null;
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        // 로그인/회원가입 엔드포인트, OPTIONS 프리플라이트 등 제외
//        String path = request.getRequestURI();
//        return HttpMethod.OPTIONS.matches(request.getMethod())
//                || path.startsWith("/auth/")
//                ;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        // ① 헤더 확인
//        String token = resolveToken(request);
//        System.out.println("▶ JwtAuthFilter: token=" + token);
//
//        if (token != null) {
//            try {
//                Claims claims = Jwts.parserBuilder()
//                        .setSigningKey(getSigningKey())
//                        .build()
//                        .parseClaimsJws(token)
//                        .getBody();
//
//                // ② 토큰 잘 파싱됐는지 확인
//                String userId = claims.getSubject();
//                System.out.println("▶ JwtAuthFilter: userId=" + userId);
//
//                request.setAttribute("X-User-Id", userId);
//                boolean isAdmin = "ADMIN".equalsIgnoreCase(claims.get("role", String.class));
//                request.setAttribute("X-Is-Admin", isAdmin);
//                System.out.println("▶ JwtAuthFilter: role(유저면 fasle, admin이면 true)=" + isAdmin);
//
//            } catch (JwtException e) {
//                e.printStackTrace();
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setContentType("application/json;charset=UTF-8");
//                response.getWriter()
//                        .write("{\"error\":\"Invalid or expired JWT token\"}");
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
package hp.home_protector.domain.community.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;

//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    @Value("${jwt.secret}")
//    private String secretKeyBase64;
//    private Key getSigningKey() {
//        byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
//        return new SecretKeySpec(decodedKey, "HmacSHA512");
//    }
//
//    private String resolveToken(HttpServletRequest request) {
//        String bearer = request.getHeader("Authorization");
//        if (bearer != null && bearer.startsWith("Bearer ")) {
//            return bearer.substring(7);
//        }
//        return null;
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        // 로그인/회원가입 엔드포인트, OPTIONS 프리플라이트 등 제외
//        String path = request.getRequestURI();
//        return HttpMethod.OPTIONS.matches(request.getMethod())
//                || path.startsWith("/auth/");
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//        // ① 헤더 확인
//        String token = resolveToken(request);
//        System.out.println("▶ JwtAuthFilter: token=" + token);
//
//        if (token != null) {
//            try {
//                Claims claims = Jwts.parserBuilder()
//                        .setSigningKey(getSigningKey())
//                        .build()
//                        .parseClaimsJws(token)
//                        .getBody();
//
//                // ② 토큰 잘 파싱됐는지 확인
//                String userId = claims.getSubject();
//                System.out.println("▶ JwtAuthFilter: userId=" + userId);
//
//                request.setAttribute("X-User-Id", userId);
//                boolean isAdmin = "ADMIN".equalsIgnoreCase(claims.get("role", String.class));
//                request.setAttribute("X-Is-Admin", isAdmin);
//                System.out.println("▶ JwtAuthFilter: role(유저면 false, admin이면 true)=" + isAdmin);
//
//            } catch (JwtException e) {
//                e.printStackTrace();
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setContentType("application/json;charset=UTF-8");
//                response.getWriter()
//                        .write("{\"error\":\"Invalid or expired JWT token\"}");
//                return;
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//}
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secretKeyBase64;

    // ───────────────────────────────────────────────────────────────
    // HS512 알고리즘으로 서명 검증용 키를 만들어 주는 부분
    private Key getSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
        // 발급 시에도 HS512(=HmacSHA512)로 signWith 했으므로, 복원할 때도 같은 알고리즘을 사용해야 합니다.
        return new SecretKeySpec(decodedKey, "HmacSHA512");
    }
    // ───────────────────────────────────────────────────────────────

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // 1) OPTIONS (브라우저 프리플라이트) 요청 제외
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        // 2) 로그인/회원가입 등 인증 없이 열어두고 싶은 경로
        if (path.startsWith("/auth/")) {
            return true;
        }

        // 3) 공개 게시판 조회 등, 토큰 없이도 접근할 엔드포인트
        //    필요에 따라 추가/수정
        if ( (HttpMethod.GET.matches(request.getMethod()) && path.equals("/communities/free"))
                || (HttpMethod.GET.matches(request.getMethod()) && path.equals("/communities/info"))
                || (HttpMethod.GET.matches(request.getMethod()) && path.startsWith("/communities/search"))
                || (HttpMethod.GET.matches(request.getMethod()) && path.startsWith("/communities/detail/")) ) {
            return true;
        }

        // 그 외 모든 /communities/* 요청은 doFilterInternal()에서 토큰을 검증합니다.
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // ① 헤더에서 토큰을 읽어 온다
        String token = resolveToken(request);

        if (token != null) {
            try {
                // ② 토큰 파싱 및 서명 검증 (HS512)
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // ③ 토큰이 정상이라면, 사용자 정보를 request attribute에 저장
                String userId = claims.getSubject();
                request.setAttribute("X-User-Id", userId);

                String role = claims.get("role", String.class);
                boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
                request.setAttribute("X-Is-Admin", isAdmin);

                // (디버깅용 로그)
                System.out.println("▶ JwtAuthFilter: userId=" + userId + ", role=" + role);

            } catch (JwtException e) {
                // 서명이 틀리거나 만료된 경우 401 응답 후 필터 체인 종료
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Invalid or expired JWT token\"}");
                return;
            }
        }

        // ④ 토큰이 없거나 정상적인 토큰이라면 다음 필터/컨트롤러로 진행
        filterChain.doFilter(request, response);
    }

    // ───────────────────────────────────────────────────────────────
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
    // ───────────────────────────────────────────────────────────────
}
