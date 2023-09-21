package me.silvernine.tutorial.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TokenProvider implements InitializingBean {

/*

    인터페이스를 상속받아 구현
        - implements InitializingBean

    JWT 관련
        - 기본 설정
        - 인증정보로 매개변수로 받고, JWT 토큰 리턴
        - (혹은, 위와 반대로) JWT 토큰을 매개변수로 받고,  인증 정보를 리턴
        - JWT 토큰에 대한 유효성 검증

*/


    // #. 기본 설정들
    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long tokenValidityInMilliseconds;
    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds

    ) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    @Override /* 빈 객체 생성시 실행되는 내용 ( InitializingBean 인터페이스 상속받고, afterPropertiesSet 를 오버 라이딩했기때문 -> https://blog.naver.com/taehwa10404/223108804354 )  */
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // #. 인증 정보를 받고,  JWT 토큰을 리턴

    public String createToken(Authentication authentication) {

        log.debug("$$ 토큰 프로바이저 $$");

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        String newJWT = Jwts.builder()
                         .setSubject(authentication.getName()) // 유저 ID
                         .claim(AUTHORITIES_KEY, authorities) // 유저 권한
                         .signWith(key, SignatureAlgorithm.HS512) // 알고리즘 방식
                         .setExpiration(validity) // 유효기간
                         .compact();

        log.debug("@@ 생성된 JWT 토큰 @@ ==>> Bearer " + newJWT);

        return newJWT;
    }


    // #. JWT 토큰을 받고, 인증 정보를 리턴 (Authentication)
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }


    // #. JWT 토큰의 유효성을 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.debug("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.debug("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.debug("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}