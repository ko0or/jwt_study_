package me.silvernine.tutorial.jwt;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
//public class JwtFilter extends GenericFilterBean {
public class JwtFilter extends OncePerRequestFilter {


/*

    클래스를 상속받아 구현
        - extends GenericFilterBean

    토큰의 인증정보를 필터링 + 필터링된 정보를 Security Context에 저장
        - 다음 doFilter 메소드를 오버라이드하여 작성 !


 */

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private TokenProvider tokenProvider;
    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

//        @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        // (다운 캐스팅) ServletRequest -> HttpServletRequest
//        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//        // 맨밑에 따로 작성해둔 메소드(헤더에 존재하는 토큰 정보를 리턴하는 기능)를 이용
//        String jwt = resolveToken(httpServletRequest);
//        String requestURI = httpServletRequest.getRequestURI();
//
//        // 같은 패키지에 존재하는 TokenProvider 클래스에 작성해두었던 메소드를 활용 -> 유효성 검증 결과로 분기처리 (if - else)
//        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
//            Authentication authentication = tokenProvider.getAuthentication(jwt);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            log.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
//        } else {
//            log.info("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
//        }
//
//        filterChain.doFilter(servletRequest, servletResponse);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        // (다운 캐스팅) ServletRequest -> HttpServletRequest
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        // 맨밑에 따로 작성해둔 메소드(헤더에 존재하는 토큰 정보를 리턴하는 기능)를 이용
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        // 같은 패키지에 존재하는 TokenProvider 클래스에 작성해두었던 메소드를 활용 -> 유효성 검증 결과로 분기처리 (if - else)
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        } else {
            log.info("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }





    // #. RequestHeader에서 토큰 정보를 가져오는 역할 ( 위에 있는 doFilter 메소드에서 사용하려고 작성 )
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }


}