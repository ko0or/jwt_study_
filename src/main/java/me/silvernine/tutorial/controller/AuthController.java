package me.silvernine.tutorial.controller;

import lombok.RequiredArgsConstructor;
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


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder; // 로그인 성공시 Authentication 객체를 만들어주는 용도 ( AuthenticationManager 를 이용 )
    private final TokenProvider tokenProvider; // Authentication 객체가 생성되었다면(로그인 성공시) 토큰 생성하기위해 필요

    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        // === 로그인 요청받았을때  ====================================================================== >>
        log.debug("@@## [1] ==>> 로그인 요청받은 ID / PW : " + loginDto );
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        log.debug("@@## [2] ==>> CustomUserDetailsService 파일에서 오버라이딩한 내용 실행 ");
        log.debug("@@## [2] ==>> UserDetailsService 클래스 loadUserByUsername 메소드");
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.debug( authenticationManagerBuilder.getObject().authenticate(authenticationToken).getClass().getName() );

        // === 로그인 성공시에만 실행 ====================================================================== >>
        log.debug("@@## [7] ==>> 인증된 정보를 시큐리티 및 토큰 정보로 저장");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        log.debug("@@## [8] ==>> 저장된 JWT 토큰을 리턴 :)");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);

    }
}