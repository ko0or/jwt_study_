package me.silvernine.tutorial.controller;

import lombok.extern.slf4j.Slf4j;
import me.silvernine.tutorial.dto.LoginDto;
import me.silvernine.tutorial.dto.TokenDto;
import me.silvernine.tutorial.jwt.JwtFilter;
import me.silvernine.tutorial.jwt.TokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import jakarta.validation.Valid;
import javax.validation.Valid;


@RestController
@RequestMapping("/api")
@Slf4j
public class AuthController {

    // 로그인 성공시 Authentication 객체를 만들어주는 용도 ( AuthenticationManager 를 이용 )
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // Authentication 객체가 생성되었다면(로그인 성공시) 토큰 생성하기위해 필요
    private final TokenProvider tokenProvider;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        log.info("@@## [1] ==>> 로그인 요청받은 ID / PW : " + loginDto );
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        log.info("@@## [2] ==>> CustomUserDetailsService 파일에서 오버라이딩한 내용 실행 ");
        log.info("@@## [2] ==>> 오버라이딩된 내용 => UserDetailsService 클래스 loadUserByUsername 메소드");
        // 콘솔 로그 찍어보기 -> log.info( authenticationManagerBuilder.getObject().authenticate(authenticationToken).getClass().getName() );
        // 찍어본 결과 -> org.springframework.security.authentication.UsernamePasswordAuthenticationToken 를 리턴함
        // (업 캐스팅) Authentication <-  UsernamePasswordAuthenticationToken ( authenticationManagerBuilder 리턴값 )
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info( authenticationManagerBuilder.getObject().authenticate(authenticationToken).getClass().getName() );



        // === 로그인 성공시에만 실행 ====================================================================== >>
        log.info("@@## [7] ==>> SecurityContextHolder.getContext().setAuthentication(authentication);");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("@@## [8] ==>> JWT 토큰 만들어서 리턴");
        String jwt = tokenProvider.createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }
}