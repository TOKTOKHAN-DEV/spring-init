package com.spring.spring_init.admin.user.service;

import com.spring.spring_init.admin.user.dto.response.UserAdminInfo;
import com.spring.spring_init.admin.user.repository.UserAdminRepository;
import com.spring.spring_init.common.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserAdminRepository userAdminRepository;

    public PageResponseDTO<UserAdminInfo> getAllUser() {
        return null;
    }
}
