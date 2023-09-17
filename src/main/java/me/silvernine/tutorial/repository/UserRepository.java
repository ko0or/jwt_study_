package me.silvernine.tutorial.repository;

import me.silvernine.tutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "authorities") // LAZY -> EAGER 방식으로 authorities 가져오기
    Optional<User> findOneWithAuthoritiesByUsername(String username); // User 조회시 authorities도 함께 조회
}