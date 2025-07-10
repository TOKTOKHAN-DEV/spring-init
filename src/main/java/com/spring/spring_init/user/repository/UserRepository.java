package com.spring.spring_init.user.repository;

import com.spring.spring_init.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(final User user);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    Optional<User> findById(final Long userId);

    Optional<User> findByOauthId(String id);
}
