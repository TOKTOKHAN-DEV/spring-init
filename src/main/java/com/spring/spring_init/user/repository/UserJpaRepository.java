package com.spring.spring_init.user.repository;

import com.spring.spring_init.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(final String username);

    Optional<User> findByEmail(final String email);
}