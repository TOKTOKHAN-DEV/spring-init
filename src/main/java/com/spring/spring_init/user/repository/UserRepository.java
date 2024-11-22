package com.spring.spring_init.user.repository;

import com.spring.spring_init.user.entity.User;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(final String username);

    User save(final User user);

    Optional<User> findByEmail(String email);
}
