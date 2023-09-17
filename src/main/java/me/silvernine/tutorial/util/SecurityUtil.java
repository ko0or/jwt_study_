package me.silvernine.tutorial.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtil {
/*

    #. 시큐리티 테스트용 -> 간단한 유틸리티 메소드 사용해보기위한 용도

*/

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private SecurityUtil() {}

    public static Optional<String> getCurrentUsername() {
        // 1. JwtFilter 클래스에서 작성한 doFilter 메소드의 if 조건문 통과시 setAuthentication() 메소드로 저장했었음
        // 2. 위 1`번 내용으로 저장된걸 꺼내옴 ( getAuthentication )
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        // NOE (NullPointException) 예방용
        if (authentication == null) {
            logger.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }


        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }
}