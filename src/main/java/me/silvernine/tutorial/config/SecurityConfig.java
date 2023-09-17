package me.silvernine.tutorial.config;

import me.silvernine.tutorial.jwt.JwtAccessDeniedHandler;
import me.silvernine.tutorial.jwt.JwtAuthenticationEntryPoint;
import me.silvernine.tutorial.jwt.JwtSecurityConfig;
import me.silvernine.tutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity // 웹 보안 활성화 ( implements WebSecurityConfigurer 혹은 extends WebSecurityAdapter )
@EnableGlobalMethodSecurity(prePostEnabled = true) // 기본값은 false, @PreAuthorize 어노테이션을 동작하게 해주는 기능 (컨트롤러단에서 사용)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // ==== JWT 관련 설정들 ==================================================
    // JWT 정규식 검증 및 토큰 <> 인증정보를 리턴하는 메소드 포함
    private final TokenProvider tokenProvider;
    // JWT 관련 예외처리들 (401, 403 Error)
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 의존성 주입 -> 여기서 쓰기위해 만들었던 파일들
    public SecurityConfig(
            TokenProvider tokenProvider
            , JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
            , JwtAccessDeniedHandler jwtAccessDeniedHandler
    )
    {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }


    // ==== SpringSecurity 관련 설정들 ===================================================

    @Bean /* 의존성 주입 -> 비밀번호 암호화에 사용되는 BCryptPasswordEncoder  */
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override /* 다음 주소에 대해선 인증 권한 무시 -> h2 콘솔 및 정적 리소스 */
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**"
                        , "/favicon.ico"
                );
    }

    @Override /* 사이트 접근 관리 */
    protected void configure(HttpSecurity http) throws Exception {
        http
                // CSRF -> 사이트 간 요청 위조 (Cross-site request forgery)
                .csrf().disable()

                // 오류발생시 -> 내가 만들었던 내용이 실행되도록 해주기
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 401
                .accessDeniedHandler(jwtAccessDeniedHandler) // 403

                // h2 console 사용을 위해 필요한 설정들
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션 설정 -> 세션 방식을 사용하지않음으로 STATELESS 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 주소별 접근 권한 관리 -> hello, authenticate, signup 외엔 모두 인증 권한을 요구하도록 설정
                .and()
                .authorizeRequests()  // 요청에 대한 접근제한 ( 권한별 )
                .antMatchers("/api/hello").permitAll()
                .antMatchers("/api/signup").permitAll()
                .antMatchers("/api/authenticate").permitAll()
                .anyRequest().authenticated() // 위에 적어두지않은 요청들은 모두 인증 권한이 필요함

                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }


}
