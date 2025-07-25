package com.spring.spring_init.admin.user.repository;

import com.spring.spring_init.admin.user.dto.response.UserAdminInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserAdminRepository {

    private final UserAdminQueryRepository userAdminQueryRepository;

    public Page<UserAdminInfo> getUsers(Pageable pageable) {

        return userAdminQueryRepository.getUsers(pageable);
    }
}
