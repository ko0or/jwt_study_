package me.silvernine.tutorial.service;

import lombok.extern.slf4j.Slf4j;
import me.silvernine.tutorial.entity.User;
import me.silvernine.tutorial.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


/*

    #. 로그인을 처리하는 기능

    - 스프링 시큐리티 UserDetailsService 클래스의 loadUserByUsername 메소드를 오버라이딩
    -

 */

@Component("userDetailsService")
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {

        // ~ AuthController 파일에서 다음 코드에의해 넘어오는 곳
        // Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        log.info("@@## [3] ==>> 레파지토리에서 username 으로 검색 (입력받은 username : " + username + ")");

        return userRepository.findOneWithAuthoritiesByUsername(username)
                .map(user -> createUser(username, user))
                .orElseThrow(() ->
                        {
                            log.info("@@## [4, 로그인 실패시] ==>> userRepository.findOneWithAuthoritiesByUsername() 메소드에서 에러 발생해서 넘어옴");
                            log.info("@@## [4, 로그인 실패시] ==>> new UsernameNotFoundException 발생");
                            return new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다.");
                        }
                );
    }

    private org.springframework.security.core.userdetails.User createUser(String username, User user) {

        log.info("@@## [4, 로그인 성공시] ==>> user 객체의 Boolean 타입 속성값 activated이 false인지 확인(비활성화 상태인지)  ");
        if (!user.isActivated()) {
            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
        }

        log.info("@@## [5] ==>> 람다식 사용 -> user 객체가 갖고있는 권한정보를 List<GrantedAuthority> 로 이동 ");
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());

        log.info("$$ user ==>> " + user);
        log.info("$$ username ==>> " + username);

        log.info("@@## [6] ==>> 최종 유저 객체 생성 -> 스프링 시큐리티 User 객체에 username , password , 위 5번에서 설정한 권한 목록으로 생성된 유저 객체   ");
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                grantedAuthorities
        );
    }
}