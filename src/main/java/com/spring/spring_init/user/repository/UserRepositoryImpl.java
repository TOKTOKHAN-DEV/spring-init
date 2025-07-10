package com.spring.spring_init.user.repository;

import com.spring.spring_init.common.persistence.config.JpaConfig;
import com.spring.spring_init.user.entity.User;
import com.spring.spring_init.verify.repository.EmailVerifyJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final EmailVerifyJpaRepository emailVerifyJpaRepository;
    private final JpaConfig jpaConfig;

    @Override
    public User save(final User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }

    @Override
    public Optional<User> findById(final Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public Optional<User> findByOauthId(final String id) {
        return userJpaRepository.findByProviderId(id);
    }
}
