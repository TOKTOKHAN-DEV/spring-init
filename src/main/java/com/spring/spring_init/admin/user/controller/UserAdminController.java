package com.spring.spring_init.admin.user.controller;

import com.spring.spring_init.admin.user.dto.response.UserAdminInfo;
import com.spring.spring_init.admin.user.service.UserAdminService;
import com.spring.spring_init.common.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    //로그인 페이지 이동
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    //회원 관리 > 회원 정보 페이지 이동
    @GetMapping("/users")
    public String userPage(
        @RequestParam(value = "page", defaultValue = "0") int page,
        Model model
    ) {
        Pageable pageable = PageRequest.of(
            page,
            30
        );

        Page<UserAdminInfo> userInfoList =
            userAdminService.getAllUser(pageable);

        model.addAttribute("pageData", new PageResponseDTO<UserAdminInfo>(userInfoList));
        return "admin/users";
    }
}
