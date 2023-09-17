package me.silvernine.tutorial.service;

import java.util.Collections;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import me.silvernine.tutorial.dto.UserDto;
import me.silvernine.tutorial.entity.Authority;
import me.silvernine.tutorial.entity.User;
import me.silvernine.tutorial.repository.UserRepository;
import me.silvernine.tutorial.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }



    // #. 회원가입 메소드
    @Transactional
    public User signup(UserDto userDto) {

        log.info("@@@## userDto => " + userDto);
        // 기 존재 회원일경우 -> RuntimeException
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        // 신규 회원일경우 -> 일반 유저 권한 부여
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();


        // dto -> entity 이동
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        // 이동된 entity 저장
        return userRepository.save(user);
    }



    // 유저 조회 -> username 으로 검색
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }


    // 유저 조회 -> 현재 로그인된 정보로 검색
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }
}