package com.spring.spring_init.user.repository;

import com.spring.spring_init.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByUsername(final String username) {
        return userJpaRepository.findByUsername(username);
    }

    @Override
    public User save(final User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return userJpaRepository.findByEmail(email);
    }
}
