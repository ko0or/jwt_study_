package me.silvernine.tutorial.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

/*

    #. 스프링 시큐리티 필터에 다음 내용을 추가하는 역할.

    



    생성자로 의존성 주입 -> TokenProvider 클래스 파일
        - JWT 관련 기본 설정
        - 인증정보로 매개변수로 받고, JWT 토큰 리턴
        - (혹은, 위와 반대로) JWT 토큰을 매개변수로 받고,  인증 정보를 리턴
        - JWT 토큰에 대한 유효성 검증

 */
    private TokenProvider tokenProvider;
    public JwtSecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }


/*

    스프링 시큐리티 필터 목록에
    내가 직접 정한 조건일때만 로그인되도록 설정한 파일(TokenProvider 클래스)을 추가

 */

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(
                new JwtFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}