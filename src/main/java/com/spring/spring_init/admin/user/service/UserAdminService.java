package com.spring.spring_init.admin.user.service;

import com.spring.spring_init.admin.user.dto.response.UserAdminInfo;
import com.spring.spring_init.admin.user.repository.UserAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserAdminRepository userAdminRepository;

    public Page<UserAdminInfo> getAllUser(Pageable pageable) {

        return userAdminRepository.getUsers(pageable);
    }
}
